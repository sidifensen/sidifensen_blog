package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.AlbumPhoto;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.AlbumPhotoMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.IAlbumService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Resource
    private AlbumPhotoMapper albumPhotoMapper;

    // 查询相册
    @Override
    public AlbumVo getAlbum(Long albumId) {
        Album album = this.getById(albumId);
        if (ObjectUtil.isEmpty(album)) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }

        AlbumVo albumVo = new AlbumVo();
        BeanUtil.copyProperties(album, albumVo);
        // 把album_id为albumId的全部照片查询出来
        List<AlbumPhoto> albumPhotos = albumPhotoMapper.selectList(new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getAlbumId, albumId));
        List<Photo> photos = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(albumPhotos)) {
            LambdaQueryWrapper<Photo> queryWrapper = new LambdaQueryWrapper<Photo>()
                    .in(Photo::getId, albumPhotos.stream().map(AlbumPhoto::getPhotoId).toList())
                    .eq(Photo::getUserId, album.getUserId());
            photos = photoMapper.selectList(queryWrapper);
        }

        // 把List<Photo>转为List<PhotoDto>
        List<PhotoDto> photoDtos = BeanUtil.copyToList(photos, PhotoDto.class);
        albumVo.setPhotos(photoDtos);
        SysUser sysUser = sysUserMapper.selectById(album.getUserId());
        albumVo.setUserName(sysUser.getNickname());
        return albumVo;
    }

    // 新增相册
    @Override
    public void createAlbum(AlbumDto albumDto) {
        Album album = new Album();
        album.setUserId(SecurityUtils.getUserId());
        album.setName(albumDto.getName());
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
            throw new BlogException(BlogConstants.CannotHandleOtherUserAlbum);
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
    public void deleteAlbum(Integer albumId) {
        Integer userId = SecurityUtils.getUserId();
        Album album = albumMapper.selectById(albumId);
        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOtherUserAlbum);
        }
        int i = albumMapper.deleteById(albumId);
        if (i == 0) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
    }

    // 查询用户所有的相册
    @Override
    public List<AlbumVo> listAlbum() {
        LambdaQueryWrapper<Album> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Album::getUserId, SecurityUtils.getUserId());
        List<Album> albums = this.list(queryWrapper);
        // 把List<Album>转为List<AlbumDto>
        List<AlbumVo> albumVos = albums.stream().map(album -> {
            AlbumVo albumVo = new AlbumVo();
            albumVo.setId(album.getId());
            albumVo.setName(album.getName());
            albumVo.setCoverUrl(album.getCoverUrl());
            albumVo.setShowStatus(album.getShowStatus());
            albumVo.setCreateTime(album.getCreateTime());
            return albumVo;
        }).toList();
        return albumVos;
    }

    // 查询所有用户的相册
    @Override
    public List<AlbumVo> listAllAlbum() {
        LambdaQueryWrapper<Album> eq = new LambdaQueryWrapper<Album>().eq(Album::getShowStatus, ShowStatusEnum.PUBLIC.getCode());
        List<Album> albums = this.list(eq);
        List<AlbumVo> albumVos = albums.stream().map(album -> {
            AlbumVo albumVo = new AlbumVo();
            BeanUtil.copyProperties(album, albumVo);
            SysUser sysUser = sysUserMapper.selectById(album.getUserId());
            albumVo.setUserName(sysUser.getNickname());
            return albumVo;
        }).toList();
        return albumVos;
    }

    // 修改相册展示状态
    @Override
    public void changeShowStatus(AlbumDto albumDto) {
        Integer userId = SecurityUtils.getUserId();
        Album album = this.getById(albumDto.getId());
        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOtherUserAlbum);
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
            throw new BlogException(BlogConstants.CannotHandleOtherUserAlbum);
        }
        album.setCoverUrl(albumDto.getCoverUrl());
        this.updateById(album);
    }

    /*管理端*/

    // 查询所有相册
    @Override
    public List<AlbumVo> adminList() {
        List<Album> albums = this.list();
        List<AlbumVo> albumVos = albums.stream().map(album -> {
            AlbumVo albumVo = new AlbumVo();
            BeanUtil.copyProperties(album, albumVo);
            SysUser sysUser = sysUserMapper.selectById(album.getUserId());
            albumVo.setUserName(sysUser.getNickname());
            return albumVo;
        }).toList();
        return albumVos;
    }

    // 更新相册
    @Override
    public void adminUpdateAlbum(AlbumDto albumDto) {
        Album album = this.getById(albumDto.getId());
        if (album == null) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
        album.setName(albumDto.getName());
        album.setCoverUrl(albumDto.getCoverUrl());
        album.setShowStatus(albumDto.getShowStatus());
        this.updateById(album);
    }

    @Override
    public void adminDeleteAlbum(Integer albumId) {
        int i = albumMapper.deleteById(albumId);
        if (i == 0) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
    }
}
