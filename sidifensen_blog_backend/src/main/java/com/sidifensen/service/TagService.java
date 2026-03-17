package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.CategorySortDto;
import com.sidifensen.domain.dto.TagDto;
import com.sidifensen.domain.entity.Tag;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-24
 */
public interface TagService extends IService<Tag> {

    void addTag(TagDto tagDto);

    Map<String, List<Tag>> listTag();
    
    void updateCategorySort(CategorySortDto categorySortDto);

    void deleteTags(List<Integer> ids);

    /**
     * 获取热门标签列表（按文章数量排序）
     * @param limit 返回数量限制
     * @return 热门标签列表
     */
    List<Tag> getHotTags(Integer limit);

}
