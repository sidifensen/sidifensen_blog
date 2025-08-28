package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.TagDto;
import com.sidifensen.domain.entity.Tag;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.TagMapper;
import com.sidifensen.service.TagService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
    public Map<String, List<String>> listTag() {
        List<Tag> tags = tagMapper.selectList(null);
        Map<String, List<String>> tagVos = tags.stream().collect(
                Collectors.groupingBy(
                        Tag::getCategory, Collectors.mapping(
                                Tag::getName, Collectors.toList()))
        );
        return tagVos;
    }
}
