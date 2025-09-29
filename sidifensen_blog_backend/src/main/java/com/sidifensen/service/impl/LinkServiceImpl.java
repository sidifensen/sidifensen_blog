package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.MessageConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.LinkAuditDto;
import com.sidifensen.domain.dto.LinkRequestDto;
import com.sidifensen.domain.dto.LinkSearchDto;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.entity.Link;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.vo.AdminLinkVo;
import com.sidifensen.domain.vo.LinkVo;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.LinkMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.LinkService;
import com.sidifensen.service.MessageService;
import com.sidifensen.utils.MyThreadFactory;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-28
 */
@Service
@Slf4j
public class LinkServiceImpl extends ServiceImpl<LinkMapper, Link> implements LinkService {

    @Resource
    private LinkMapper linkMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private MessageService messageService;

    @Resource
    private RabbitTemplate rabbitTemplate;

    //创建线程池
    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("LinkServiceImpl"));

    @Override
    public void applyLink(LinkRequestDto linkRequestDto) {
        try {
            // 获取当前登录用户ID
            Integer currentUserId = SecurityUtils.getUserId();
            if (ObjectUtil.isEmpty(currentUserId) || currentUserId == 0) {
                throw new BlogException(BlogConstants.LoginRequired);
            }

            // 创建友链实体
            Link link = new Link();
            BeanUtil.copyProperties(linkRequestDto, link);
            link.setUserId(currentUserId);

            // 保存友链申请
            int result = linkMapper.insert(link);
            if (result == 0) {
                throw new BlogException(BlogConstants.AddLinkError);
            }

            auditLink(link);

        } catch (Exception e) {
            log.error("申请友链失败：{}", e.getMessage(), e);
            throw new BlogException(BlogConstants.AddLinkError);
        }
    }

    private void auditLink(Link link) {
        executorService.execute(() -> {
            MessageDto messageDto = new MessageDto();
            messageDto.setContent(MessageConstants.LinkNeedReview(link.getId(), link.getName()));
            messageService.sendToAdmin(messageDto);

            HashMap<String, Object> sendEmail = new HashMap<>();
            sendEmail.put("text", String.format(MessageConstants.LINK_NEED_REVIEW, link.getId(), link.getName()));
            rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange, RabbitMQConstants.Examine_Routing_Key, sendEmail);
        });
    }

    @Override
    public void deleteLink(Integer linkId) {
        try {
            // 获取当前登录用户ID
            Integer currentUserId = SecurityUtils.getUserId();
            if (ObjectUtil.isEmpty(currentUserId) || currentUserId == 0) {
                throw new BlogException(BlogConstants.LoginRequired);
            }

            // 查询友链是否存在
            Link link = this.getById(linkId);
            if (ObjectUtil.isEmpty(link)) {
                throw new BlogException(BlogConstants.NotFoundLink);
            }

            // 权限校验：只能删除自己的友链
            if (!link.getUserId().equals(currentUserId)) {
                throw new BlogException(BlogConstants.CannotHandleOthersLink);
            }

            boolean deleted = this.removeById(linkId);
            if (!deleted) {
                throw new BlogException(BlogConstants.DeleteLinkError);
            }

        } catch (Exception e) {
            log.error("删除友链失败：{}", e.getMessage(), e);
            throw new BlogException(BlogConstants.DeleteLinkError);
        }
    }

    @Override
    public PageVo<List<LinkVo>> getLinkList(Integer pageNum, Integer pageSize) {
        try {
            // 创建分页对象
            Page<Link> page = new Page<>(pageNum, pageSize);
            
            // 查询审核通过的友链，按创建时间倒序排列
            LambdaQueryWrapper<Link> queryWrapper = new LambdaQueryWrapper<Link>()
                    .eq(Link::getExamineStatus, ExamineStatusEnum.PASS.getCode())
                    .orderByDesc(Link::getCreateTime);
            
            Page<Link> linkPage = this.page(page, queryWrapper);
            List<Link> linkList = linkPage.getRecords();
            
            // 转换为VO对象
            List<LinkVo> linkVos = BeanUtil.copyToList(linkList, LinkVo.class);
            
            return new PageVo<>(linkVos, linkPage.getTotal());
            
        } catch (Exception e) {
            log.error("获取友链列表失败：{}", e.getMessage(), e);
            throw new BlogException(BlogConstants.GetLinkListError);
        }
    }

    @Override
    public List<AdminLinkVo> adminGetLinkList() {
        try {
            // 查询所有友链
            List<Link> linkList = this.list(new LambdaQueryWrapper<Link>()
                    .orderByDesc(Link::getCreateTime));

            // 转换为AdminLinkVo并填充用户信息
            return convertToAdminLinkVo(linkList);
        } catch (Exception e) {
            log.error("管理员获取友链列表失败：{}", e.getMessage(), e);
            throw new BlogException("获取友链列表失败");
        }
    }

    @Override
    public List<AdminLinkVo> adminSearchLink(LinkSearchDto linkSearchDto) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<Link> qw = new LambdaQueryWrapper<Link>()
                    .eq(ObjectUtil.isNotEmpty(linkSearchDto.getExamineStatus()), Link::getExamineStatus, linkSearchDto.getExamineStatus())
                    .eq(ObjectUtil.isNotEmpty(linkSearchDto.getUserId()), Link::getUserId, linkSearchDto.getUserId())
                    .like(StrUtil.isNotBlank(linkSearchDto.getKeyword()), Link::getName, linkSearchDto.getKeyword())
                    .ge(ObjectUtil.isNotEmpty(linkSearchDto.getCreateTimeStart()), Link::getCreateTime, linkSearchDto.getCreateTimeStart())
                    .le(ObjectUtil.isNotEmpty(linkSearchDto.getCreateTimeEnd()), Link::getCreateTime, linkSearchDto.getCreateTimeEnd())
                    .orderByDesc(Link::getCreateTime);

            // 查询友链列表
            List<Link> linkList = this.list(qw);

            // 转换为AdminLinkVo并填充用户信息
            return convertToAdminLinkVo(linkList);
        } catch (Exception e) {
            log.error("管理员搜索友链失败：{}", e.getMessage(), e);
            throw new BlogException("搜索友链失败");
        }
    }

    @Override
    public void adminExamineLink(LinkAuditDto linkAuditDto) {
        try {
            // 查询友链是否存在
            Link link = this.getById(linkAuditDto.getLinkId());
            if (ObjectUtil.isEmpty(link)) {
                throw new BlogException(BlogConstants.NotFoundLink);
            }
            link.setExamineStatus(linkAuditDto.getExamineStatus());
            boolean updated = this.updateById(link);
            if (!updated) {
                throw new BlogException("友链审核失败");
            }

            // 审核通过时发送邮件通知
            if (ExamineStatusEnum.PASS.getCode().equals(linkAuditDto.getExamineStatus()) && StrUtil.isNotEmpty(link.getEmail())) {
                sendLinkApprovalEmail(link);
            }

        } catch (Exception e) {
            log.error("管理员审核友链失败：{}", e.getMessage(), e);
            throw new BlogException("友链审核失败");
        }
    }

    @Override
    public void adminExamineBatchLink(List<LinkAuditDto> linkAuditDtos) {
        try {
            // 参数校验
            if (ObjectUtil.isEmpty(linkAuditDtos) || linkAuditDtos.isEmpty()) {
                throw new BlogException("友链审核信息列表不能为空");
            }

            // 提取所有友链ID，验证友链是否存在
            List<Integer> linkIds = linkAuditDtos.stream()
                    .map(LinkAuditDto::getLinkId)
                    .collect(Collectors.toList());
            
            List<Link> existingLinks = this.listByIds(linkIds);
            if (existingLinks.size() != linkIds.size()) {
                throw new BlogException(BlogConstants.NotFoundLink);
            }

            // 构建批量更新的Link列表
            List<Link> linksToUpdate = linkAuditDtos.stream().map(dto -> {
                Link link = new Link();
                link.setId(dto.getLinkId());
                link.setExamineStatus(dto.getExamineStatus());
                return link;
            }).collect(Collectors.toList());

            // 执行批量更新
            boolean updated = this.updateBatchById(linksToUpdate);
            if (!updated) {
                throw new BlogException("批量审核友链失败");
            }

            // 为审核通过的友链发送邮件通知
            sendBatchLinkApprovalEmails(linkAuditDtos, existingLinks);

        } catch (Exception e) {
            log.error("管理员批量审核友链失败：{}", e.getMessage(), e);
            throw new BlogException("批量审核友链失败");
        }
    }

    @Override
    public void adminDeleteLink(Integer linkId) {
        try {
            // 参数校验
            if (ObjectUtil.isEmpty(linkId)) {
                throw new BlogException(BlogConstants.LinkIdRequired);
            }

            // 查询友链是否存在
            Link link = this.getById(linkId);
            if (ObjectUtil.isEmpty(link)) {
                throw new BlogException(BlogConstants.NotFoundLink);
            }

            // 删除友链
            boolean deleted = this.removeById(linkId);
            if (!deleted) {
                throw new BlogException(BlogConstants.DeleteLinkError);
            }

        } catch (Exception e) {
            log.error("管理员删除友链失败：{}", e.getMessage(), e);
            throw new BlogException(BlogConstants.DeleteLinkError);
        }
    }

    @Override
    public void adminDeleteBatchLink(List<Integer> linkIds) {
        try {
            // 参数校验
            if (ObjectUtil.isEmpty(linkIds) || linkIds.isEmpty()) {
                throw new BlogException("友链ID列表不能为空");
            }

            // 批量删除
            boolean deleted = this.removeByIds(linkIds);
            if (!deleted) {
                throw new BlogException("批量删除友链失败");
            }

        } catch (Exception e) {
            log.error("管理员批量删除友链失败：{}", e.getMessage(), e);
            throw new BlogException("批量删除友链失败");
        }
    }


    /**
     * 将Link列表转换为AdminLinkVo列表，并填充用户信息
     *
     * @param linkList 友链列表
     * @return AdminLinkVo列表
     */
    private List<AdminLinkVo> convertToAdminLinkVo(List<Link> linkList) {
        if (ObjectUtil.isEmpty(linkList)) {
            return List.of();
        }

        // 提取所有用户ID
        List<Integer> userIds = linkList.stream()
                .map(Link::getUserId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询用户信息
        Map<Integer, String> userNicknameMap = Map.of();
        if (!userIds.isEmpty()) {
            List<SysUser> userList = sysUserMapper.selectList(
                    new LambdaQueryWrapper<SysUser>()
                            .in(SysUser::getId, userIds)
                            .select(SysUser::getId, SysUser::getNickname)
            );
            userNicknameMap = userList.stream()
                    .collect(Collectors.toMap(SysUser::getId, SysUser::getNickname));
        }

        // 转换为AdminLinkVo
        final Map<Integer, String> finalUserNicknameMap = userNicknameMap;
        return linkList.stream().map(link -> {
            AdminLinkVo adminLinkVo = new AdminLinkVo();
            BeanUtil.copyProperties(link, adminLinkVo);
            // 设置用户昵称
            adminLinkVo.setUserNickname(finalUserNicknameMap.get(link.getUserId()));
            return adminLinkVo;
        }).collect(Collectors.toList());
    }

    /**
     * 发送友链审核通过邮件通知
     * @param link 友链信息
     */
    private void sendLinkApprovalEmail(Link link) {
        try {
            // 构建邮件消息
            Map<String, Object> emailMessage = new HashMap<>();
            emailMessage.put("email", link.getEmail());
            emailMessage.put("text", MessageConstants.LinkAuditPass(link.getUrl()));

            // 发送邮件消息到友链审核通过专用的RabbitMQ交换机
            rabbitTemplate.convertAndSend(
                RabbitMQConstants.Link_Approval_Exchange,
                RabbitMQConstants.Link_Approval_Routing_Key,
                emailMessage
            );
            
        } catch (Exception e) {
            log.error("发送友链审核通过邮件失败，友链ID：{}，邮箱：{}，错误：{}", 
                link.getId(), link.getEmail(), e.getMessage(), e);
        }
    }

    /**
     * 批量发送友链审核通过邮件通知
     * @param linkAuditDtos 审核信息列表
     * @param existingLinks 现有友链列表
     */
    private void sendBatchLinkApprovalEmails(List<LinkAuditDto> linkAuditDtos, List<Link> existingLinks) {
        try {
            // 创建友链ID到友链对象的映射
            Map<Integer, Link> linkMap = existingLinks.stream()
                    .collect(Collectors.toMap(Link::getId, link -> link));
            
            // 筛选出审核通过的友链并发送邮件
            linkAuditDtos.stream()
                    .filter(dto -> ExamineStatusEnum.PASS.getCode().equals(dto.getExamineStatus()))
                    .forEach(dto -> {
                        Link link = linkMap.get(dto.getLinkId());
                        if (link != null && StrUtil.isNotEmpty(link.getEmail())) {
                            sendLinkApprovalEmail(link);
                        }
                    });
                    
        } catch (Exception e) {
            log.error("批量发送友链审核通过邮件失败，错误：{}", e.getMessage(), e);
        }
    }
}
