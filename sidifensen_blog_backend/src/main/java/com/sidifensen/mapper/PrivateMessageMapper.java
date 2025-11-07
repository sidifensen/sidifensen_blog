package com.sidifensen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sidifensen.domain.entity.PrivateMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 私信 Mapper
 */
@Mapper
public interface PrivateMessageMapper extends BaseMapper<PrivateMessage> {
}
