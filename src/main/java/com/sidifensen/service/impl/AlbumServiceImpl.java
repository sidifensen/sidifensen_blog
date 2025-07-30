package com.sidifensen.service.impl;

import com.sidifensen.domain.entity.Album;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.service.IAlbumService;
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
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements IAlbumService {

}
