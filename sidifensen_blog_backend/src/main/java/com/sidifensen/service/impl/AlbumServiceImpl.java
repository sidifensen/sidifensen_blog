package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.IAlbumService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements IAlbumService {

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    // 查询相册
    @Override
    public AlbumDto getAlbum(Long albumId) {
        Album album = this.getById(albumId);
        if (album == null) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
        AlbumDto albumDto = new AlbumDto();
        BeanUtil.copyProperties(album, albumDto);
        // 把album_id为albumId的全部照片查询出来
        List<Photo> photos = photoMapper.selectList(new LambdaQueryWrapper<Photo>().eq(Photo::getAlbumId, albumId));
        // 把List<Photo>转为List<PhotoDto>
        List<PhotoDto> photoDtos = BeanUtil.copyToList(photos, PhotoDto.class);
        albumDto.setPhotos(photoDtos);
        return albumDto;
    }

    // 新增相册
    @Override
    public void createAlbum(AlbumDto albumDto) {
        Album album = new Album();
        album.setUserId(SecurityUtils.getUserId());
        album.setName(albumDto.getName());
        if (albumDto.getDescription() != null) {
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

        Integer userId = SecurityUtils.getUserId();
        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotDeleteOtherUserAlbum);
        }
        if (albumDto.getDescription() != null) {
            album.setDescription(albumDto.getDescription());
        }
        if (albumDto.getCoverUrl() != null) {
            album.setCoverUrl(albumDto.getCoverUrl());
        }
        if (albumDto.getShowStatus() != null) {
            album.setShowStatus(albumDto.getShowStatus());
        }
        this.updateById(album);
    }

    // 删除相册
    @Override
    public void deleteAlbum(Long albumId) {
        Integer userId = SecurityUtils.getUserId();
        Album album = albumMapper.selectById(albumId);
        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotDeleteOtherUserAlbum);
        }
        int i = albumMapper.deleteById(albumId);
        if (i == 0) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
    }

    // 查询用户所有的相册
    @Override
    public List<AlbumDto> listAlbum() {
        LambdaQueryWrapper<Album> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Album::getUserId, SecurityUtils.getUserId());
        queryWrapper.eq(Album::getShowStatus, ShowStatusEnum.PUBLIC.getCode());
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

    // 查询所有用户的相册
    @Override
    public List<AlbumDto> listAllAlbum() {
        LambdaQueryWrapper<Album> eq = new LambdaQueryWrapper<Album>().eq(Album::getShowStatus, ShowStatusEnum.PUBLIC.getCode());
        List<Album> albums = this.list(eq);
        // 把List<Album>转为List<AlbumDto>
        List<AlbumDto> albumDtos = albums.stream().map(album -> {
            AlbumDto albumDto = new AlbumDto();
            BeanUtil.copyProperties(album, albumDto);
            SysUser sysUser = sysUserMapper.selectById(album.getUserId());
            albumDto.setUserName(sysUser.getUsername());
            return albumDto;
        }).toList();
        return albumDtos;
    }

    // 修改相册展示状态
    @Override
    public void changeShowStatus(AlbumDto albumDto) {
        Integer userId = SecurityUtils.getUserId();
        Album album = this.getById(albumDto.getId());
        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotDeleteOtherUserAlbum);
        }
        if (album == null) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
        album.setShowStatus(albumDto.getShowStatus());
        this.updateById(album);
    }

    // 修改相册封面
    @Override
    public void changeCover(AlbumDto albumDto) {
        Album album = this.getById(albumDto.getId());
        Integer userId = SecurityUtils.getUserId();
        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotDeleteOtherUserAlbum);
        }
        album.setCoverUrl(albumDto.getCoverUrl());
        this.updateById(album);
    }
}
