package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.entity.Tag;
import com.sidifensen.mapper.TagMapper;
import com.sidifensen.service.TagService;
import org.springframework.stereotype.Service;

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

}
