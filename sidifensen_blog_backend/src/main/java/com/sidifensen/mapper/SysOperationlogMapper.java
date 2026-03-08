package com.sidifensen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sidifensen.domain.entity.SysOperationlog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper 接口
 *
 * @author sidifensen
 * @since 2025-07-08
 */
@Mapper
public interface SysOperationlogMapper extends BaseMapper<SysOperationlog> {
}
