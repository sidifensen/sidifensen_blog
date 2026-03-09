package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysOperationlogQueryDto;
import com.sidifensen.domain.entity.SysOperationlog;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.SysOperationlogListVo;
import com.sidifensen.domain.vo.SysOperationlogVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysOperationlogMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.SysOperationlogService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 操作日志 Service 实现类
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Service
@Slf4j
public class SysOperationlogServiceImpl extends ServiceImpl<SysOperationlogMapper, SysOperationlog>
        implements SysOperationlogService {

    @Resource
    private SysOperationlogMapper sysOperationlogMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public void insertOperationlogRecord(SysOperationlog operationlog) {
        sysOperationlogMapper.insert(operationlog);
    }

    @Override
    public List<SysOperationlogListVo> getOperationlogList() {
        // 查询所有操作日志，按创建时间倒序
        LambdaQueryWrapper<SysOperationlog> qw = new LambdaQueryWrapper<SysOperationlog>()
                .orderByDesc(SysOperationlog::getCreateTime);

        List<SysOperationlog> logs = sysOperationlogMapper.selectList(qw);

        // 转换为 VO 并填充用户名
        return logs.stream().map(log -> {
            SysOperationlogListVo vo = BeanUtil.copyProperties(log, SysOperationlogListVo.class);

            // 如果有用户 ID，查询用户名
            if (log.getOperatorId() != null) {
                SysUser user = sysUserMapper.selectById(log.getOperatorId());
                if (user != null) {
                    vo.setOperatorName(user.getUsername());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SysOperationlogListVo> searchOperationlog(SysOperationlogQueryDto queryDto) {
        // 构建查询条件
        LambdaQueryWrapper<SysOperationlog> qw = new LambdaQueryWrapper<SysOperationlog>()
                .eq(queryDto.getOperatorId() != null, SysOperationlog::getOperatorId, queryDto.getOperatorId())
                .eq(StrUtil.isNotBlank(queryDto.getModule()), SysOperationlog::getModule, queryDto.getModule())
                .eq(StrUtil.isNotBlank(queryDto.getOperation()), SysOperationlog::getOperation, queryDto.getOperation())
                .eq(queryDto.getStatus() != null, SysOperationlog::getStatus, queryDto.getStatus())
                .ge(queryDto.getCreateTimeStart() != null, SysOperationlog::getCreateTime, queryDto.getCreateTimeStart())
                .le(queryDto.getCreateTimeEnd() != null, SysOperationlog::getCreateTime, queryDto.getCreateTimeEnd())
                .orderByDesc(SysOperationlog::getCreateTime);

        List<SysOperationlog> logs = sysOperationlogMapper.selectList(qw);

        // 转换为 VO 并填充用户名
        return logs.stream().map(log -> {
            SysOperationlogListVo vo = BeanUtil.copyProperties(log, SysOperationlogListVo.class);

            // 如果有用户 ID，查询用户名
            if (log.getOperatorId() != null) {
                SysUser user = sysUserMapper.selectById(log.getOperatorId());
                if (user != null) {
                    vo.setOperatorName(user.getUsername());
                }
            }

            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public SysOperationlogVo getOperationlogDetail(Integer id) {
        if (id == null) {
            throw new BlogException(BlogConstants.NotFoundOperationlog);
        }

        SysOperationlog log = sysOperationlogMapper.selectById(id);
        if (log == null) {
            throw new BlogException(BlogConstants.NotFoundOperationlog);
        }

        SysOperationlogVo vo = BeanUtil.copyProperties(log, SysOperationlogVo.class);

        // 如果有用户 ID，查询用户名
        if (log.getOperatorId() != null) {
            SysUser user = sysUserMapper.selectById(log.getOperatorId());
            if (user != null) {
                vo.setOperatorName(user.getUsername());
            }
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOperationlogs(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BlogException(BlogConstants.OperationlogIdsRequired);
        }

        // 使用 MyBatis-Plus 的 removeByIds 方法批量删除
        boolean success = this.removeByIds(ids);
        if (!success) {
            log.warn("批量删除操作日志失败");
        }
    }
}
