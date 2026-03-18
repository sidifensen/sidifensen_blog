package com.sidifensen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sidifensen.domain.entity.UserSettings;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户个人设置 Mapper 接口
 * </p>
 *
 * @author sidifensen
 * @since 2026-03-18
 */
@Mapper
public interface UserSettingsMapper extends BaseMapper<UserSettings> {

}
