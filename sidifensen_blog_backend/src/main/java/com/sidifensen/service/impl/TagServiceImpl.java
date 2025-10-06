package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.CategorySortDto;
import com.sidifensen.domain.dto.TagDto;
import com.sidifensen.domain.entity.Tag;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.TagMapper;
import com.sidifensen.service.TagService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-08-24
 */
@Service
@Slf4j
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Resource
    private TagMapper tagMapper;

    @Override
    public void addTag(TagDto tagDto) {
        Tag tag = BeanUtil.copyProperties(tagDto, Tag.class);
        int insert = tagMapper.insert(tag);
        if (insert <= 0) {
            throw new BlogException(BlogConstants.AddTagError);
        }
    }

    @Override
    public Map<String, List<Tag>> listTag() {
        // 使用 LambdaQueryWrapper 添加排序：先按 sort 升序，再按 category 升序
        List<Tag> tags = this.lambdaQuery()
                .orderByAsc(Tag::getSort)
                .orderByAsc(Tag::getCategory)
                .list();

        // 按分类分组，使用 LinkedHashMap 保持顺序
        Map<String, List<Tag>> tagVos = tags.stream().collect(
                Collectors.groupingBy(Tag::getCategory,
                        java.util.LinkedHashMap::new,
                        Collectors.toList())
        );
        return tagVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategorySort(CategorySortDto categorySortDto) {
        // 1. 参数校验
        if (ObjectUtil.isEmpty(categorySortDto.getCategory())) {
            throw new BlogException(BlogConstants.CategoryNameRequired);
        }
        if (ObjectUtil.isEmpty(categorySortDto.getNewSort())) {
            throw new BlogException(BlogConstants.SortValueRequired);
        }

        String category = categorySortDto.getCategory();
        Integer newSort = categorySortDto.getNewSort();

        // 2. 查询当前分类的所有标签
        List<Tag> currentCategoryTags = this.lambdaQuery()
                .eq(Tag::getCategory, category)
                .list();

        if (currentCategoryTags.isEmpty()) {
            throw new BlogException(BlogConstants.CategoryNotFound);
        }

        // 3. 获取当前分类的旧排序值（取第一个标签的 sort 值，同一分类的 sort 值应该相同）
        Integer oldSort = currentCategoryTags.get(0).getSort();

        // 4. 如果新排序值和旧排序值相同，无需调整
        if (oldSort.equals(newSort)) {
            return;
        }

        // 5. 处理排序冲突
        // 如果是向前移动（newSort < oldSort），需要把 [newSort, oldSort) 区间的分类 sort +1
        if (newSort < oldSort) {
            // 查询受影响的分类（排序值在 [newSort, oldSort) 区间的所有标签）
            List<Tag> affectedTags = this.lambdaQuery()
                    .ge(Tag::getSort, newSort)
                    .lt(Tag::getSort, oldSort)
                    .list();

            // 批量更新：将这些标签的 sort +1
            for (Tag tag : affectedTags) {
                tag.setSort(tag.getSort() + 1);
            }
            if (!affectedTags.isEmpty()) {
                this.updateBatchById(affectedTags);
            }
        }
        // 如果是向后移动（newSort > oldSort），需要把 (oldSort, newSort] 区间的分类 sort -1
        else {
            // 查询受影响的分类（排序值在 (oldSort, newSort] 区间的所有标签）
            List<Tag> affectedTags = this.lambdaQuery()
                    .gt(Tag::getSort, oldSort)
                    .le(Tag::getSort, newSort)
                    .list();

            // 批量更新：将这些标签的 sort -1
            for (Tag tag : affectedTags) {
                tag.setSort(tag.getSort() - 1);
            }
            if (!affectedTags.isEmpty()) {
                this.updateBatchById(affectedTags);
            }
        }

        // 6. 更新当前分类的所有标签为新的排序值
        for (Tag tag : currentCategoryTags) {
            tag.setSort(newSort);
        }
        boolean result = this.updateBatchById(currentCategoryTags);
        if (!result) {
            throw new BlogException(BlogConstants.UpdateCategorySortError);
        }

    }

    @Override
    public void deleteTags(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BlogException(BlogConstants.DeleteTagError);
        }
        boolean result = this.removeByIds(ids);
        if (!result) {
            throw new BlogException(BlogConstants.DeleteTagError);
        }
    }

}
