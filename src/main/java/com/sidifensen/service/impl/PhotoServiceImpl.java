package com.sidifensen.service.impl;

import com.sidifensen.domain.entity.Photo;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.service.IPhotoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements IPhotoService {

}
