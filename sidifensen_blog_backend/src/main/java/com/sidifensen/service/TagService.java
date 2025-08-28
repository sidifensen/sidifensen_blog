package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
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

    Map<String, List<String>> listTag();
}
