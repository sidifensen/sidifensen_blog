package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.service.IAlbumService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    @Resource
    private AlbumMapper albumMapper;

    // 查询相册
    @Override
    public AlbumDto getAlbum(Long albumId) {
        Album album = this.getById(albumId);
        if (album == null) {
            throw new BlogException("相册不存在");
        }
        AlbumDto albumDto = new AlbumDto();
        BeanUtil.copyProperties(album, albumDto);
        return albumDto;
    }

    // 新增相册
    @Override
    public void createAlbum(AlbumDto albumDto) {
        Album album = new Album();
        albumDto.setUserId(SecurityUtils.getUserId());
        album.setName(albumDto.getName());
        if (albumDto.getDescription()!= null) {
            album.setDescription(albumDto.getDescription());
        }
        album.setShowStatus(ShowStatusEnum.PUBLIC.getCode());
        this.save(album);
    }

    // 更新相册
    @Override
    public void updateAlbum(AlbumDto albumDto) {
        albumDto.setUserId(SecurityUtils.getUserId());
        Album album = this.getById(albumDto.getId());
        album.setName(albumDto.getName());
        if (albumDto.getDescription()!= null) {
            album.setDescription(albumDto.getDescription());
        }
        if (albumDto.getCoverUrl()!= null) {
            album.setCoverUrl(albumDto.getCoverUrl());
        }
        album.setShowStatus(albumDto.getShowStatus());
        this.updateById(album);
    }

    // 删除相册
    @Override
    public void deleteAlbum(Long albumId) {
        int i = albumMapper.deleteById(albumId);
        if (i == 0) {
            throw new BlogException("相册不存在");
        }
    }

    // 查询用户所有的相册
    @Override
    public List<AlbumDto> listAlbum() {
        LambdaQueryWrapper<Album> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Album::getUserId, SecurityUtils.getUserId());
        List<Album> albums = this.list(queryWrapper);
        // 把List<Album>转为List<AlbumDto>
        List<AlbumDto> albumDtos = albums.stream().map(album -> {
            AlbumDto albumDto = new AlbumDto();
            albumDto.setId(album.getId());
            albumDto.setName(album.getName());
            albumDto.setDescription(album.getDescription());
            albumDto.setCoverUrl(album.getCoverUrl());
            albumDto.setShowStatus(album.getShowStatus());
            albumDto.setCreateTime(album.getCreateTime());
            return albumDto;
        }).toList();
        return albumDtos;
    }

    // 修改相册展示状态
    @Override
    public void showStatus(AlbumDto albumDto) {
        Album album = this.getById(albumDto.getId()).setShowStatus(albumDto.getShowStatus());
        this.updateById(album);
    }

    // 修改相册封面
    @Override
    public void changeCover(AlbumDto albumDto) {
        Album album = this.getById(albumDto.getId()).setCoverUrl(albumDto.getCoverUrl());
        this.updateById(album);
    }
}
