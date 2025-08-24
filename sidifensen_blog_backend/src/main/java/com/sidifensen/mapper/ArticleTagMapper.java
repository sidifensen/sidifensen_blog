package com.sidifensen.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sidifensen.domain.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 文章标签关联Mapper接口
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-24
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

}