package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.SysOperationlogQueryDto;
import com.sidifensen.domain.entity.SysOperationlog;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysOperationlogListVo;
import com.sidifensen.domain.vo.SysOperationlogVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysOperationlogMapper;
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

    @Override
    public void insertOperationlogRecord(SysOperationlog operationlog) {
        sysOperationlogMapper.insert(operationlog);
    }

    @Override
    public PageVo<List<SysOperationlogListVo>> getOperationlogList(Integer pageNum, Integer pageSize) {
        Page<SysOperationlog> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysOperationlog> queryWrapper = new LambdaQueryWrapper<SysOperationlog>()
                .orderByDesc(SysOperationlog::getCreateTime);
        Page<SysOperationlog> logPage = sysOperationlogMapper.selectPage(page, queryWrapper);
        return new PageVo<>(convertToListVo(logPage.getRecords()), logPage.getTotal());
    }

    @Override
    public PageVo<List<SysOperationlogListVo>> searchOperationlog(SysOperationlogQueryDto queryDto) {
        Page<SysOperationlog> page = new Page<>(queryDto.getPageNum(), queryDto.getPageSize());
        LambdaQueryWrapper<SysOperationlog> queryWrapper = buildQueryWrapper(queryDto);
        Page<SysOperationlog> logPage = sysOperationlogMapper.selectPage(page, queryWrapper);
        return new PageVo<>(convertToListVo(logPage.getRecords()), logPage.getTotal());
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

    /**
     * 构建操作日志查询条件
     */
    private LambdaQueryWrapper<SysOperationlog> buildQueryWrapper(SysOperationlogQueryDto queryDto) {
        return new LambdaQueryWrapper<SysOperationlog>()
                .eq(queryDto.getOperatorId() != null, SysOperationlog::getOperatorId, queryDto.getOperatorId())
                .eq(StrUtil.isNotBlank(queryDto.getOperatorRole()), SysOperationlog::getOperatorRole, queryDto.getOperatorRole())
                .eq(StrUtil.isNotBlank(queryDto.getModule()), SysOperationlog::getModule, queryDto.getModule())
                .eq(StrUtil.isNotBlank(queryDto.getOperation()), SysOperationlog::getOperation, queryDto.getOperation())
                .eq(queryDto.getStatus() != null, SysOperationlog::getStatus, queryDto.getStatus())
                .ge(queryDto.getCreateTimeStart() != null, SysOperationlog::getCreateTime, queryDto.getCreateTimeStart())
                .le(queryDto.getCreateTimeEnd() != null, SysOperationlog::getCreateTime, queryDto.getCreateTimeEnd())
                .orderByDesc(SysOperationlog::getCreateTime);
    }

    /**
     * 转换操作日志列表 VO，保留落库时的审计快照字段
     */
    private List<SysOperationlogListVo> convertToListVo(List<SysOperationlog> logs) {
        return logs.stream()
                .map(log -> BeanUtil.copyProperties(log, SysOperationlogListVo.class))
                .collect(Collectors.toList());
    }
}
