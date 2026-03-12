package com.sidifensen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sidifensen.domain.entity.VipMember;
import org.apache.ibatis.annotations.Mapper;

/**
 * VIP 会员数据访问层。
 */
@Mapper
public interface VipMemberMapper extends BaseMapper<VipMember> {
}
