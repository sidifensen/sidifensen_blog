package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.AlbumPhoto;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.domain.vo.PhotoVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.AlbumPhotoMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.AlbumService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private AlbumPhotoMapper albumPhotoMapper;


    // 查询相册详情
    @Override
    public AlbumVo getAlbum(Integer albumId) {
        Album album = albumMapper.selectById(albumId);
        if (ObjectUtil.isEmpty(album)) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }

        AlbumVo albumVo = BeanUtil.copyProperties(album, AlbumVo.class);
        // 把album_id为albumId的全部照片查询出来
        List<AlbumPhoto> albumPhotos = albumPhotoMapper.selectList(new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getAlbumId, albumId));
        List<Photo> photos = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(albumPhotos)) {
            LambdaQueryWrapper<Photo> queryWrapper = new LambdaQueryWrapper<Photo>()
                    .in(Photo::getId, albumPhotos.stream().map(AlbumPhoto::getPhotoId).toList())
                    .eq(album.getUserId() != SecurityUtils.getUserId(), Photo::getExamineStatus, ExamineStatusEnum.PASS.getCode());// 如果相册不是当前用户的，只显示审核通过的照片
            photos = photoMapper.selectList(queryWrapper);
        }

        List<PhotoVo> photoVos = BeanUtil.copyToList(photos, PhotoVo.class);
        albumVo.setPhotos(photoVos);
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
        SysUser user = SecurityUtils.getUser();
        Integer userId = user.getId();

        albumDto.setUserId(userId);
        Album album = this.getById(albumDto.getId());
        album.setName(albumDto.getName());

        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOthersAlbum);
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
            throw new BlogException(BlogConstants.CannotHandleOthersAlbum);
        }
        int i = albumMapper.deleteById(albumId);
        if (i == 0) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
    }

    // 查询用户所有的相册
    @Override
    public List<AlbumVo> listAlbum() {
        Integer userId = SecurityUtils.getUserId();
        List<Album> albums = this.list(new LambdaQueryWrapper<Album>().eq(Album::getUserId, userId));

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
        List<AlbumVo> albumVos = BeanUtil.copyToList(albums, AlbumVo.class);
        // 查询所有用户信息
        List<SysUser> users = sysUserMapper.selectList(null);
        Map<Integer, String> userMap = users.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));
        // 将用户名设置到AlbumVo中
        albumVos.forEach(albumVo -> albumVo.setUserName(userMap.get(albumVo.getUserId())));

        return albumVos;
    }

    // 修改相册展示状态
    @Override
    public void changeShowStatus(AlbumDto albumDto) {
        Integer userId = SecurityUtils.getUserId();
        Album album = this.getById(albumDto.getId());
        if (album == null) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
        if (userId != album.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOthersAlbum);
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
            throw new BlogException(BlogConstants.CannotHandleOthersAlbum);
        }
        album.setCoverUrl(albumDto.getCoverUrl());
        this.updateById(album);
    }

    /*管理端*/

    // 查询所有相册
    @Override
    public List<AlbumVo> adminList() {
        List<Album> albums = this.list();
        List<AlbumVo> albumVos = BeanUtil.copyToList(albums, AlbumVo.class);
        // 用userId把username查询出来
        HashMap<Integer, String> userMap = new HashMap<>();
        // 查询所有用户信息
        List<SysUser> users = sysUserMapper.selectList(null);
        for (SysUser user : users) {
            userMap.put(user.getId(), user.getUsername());
        }
        // 将用户名设置到AlbumVo中
        for (AlbumVo albumVo : albumVos) {
            albumVo.setUserName(userMap.get(albumVo.getUserId()));
        }
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

    // 删除相册
    @Override
    public void adminDeleteAlbum(Integer albumId) {
        Album album = albumMapper.selectById(albumId);
        if (album == null) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
        int i = albumMapper.deleteById(albumId);
        if (i == 0) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
        // 删除照片
        LambdaQueryWrapper<AlbumPhoto> eq = new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getAlbumId, albumId);
        List<Integer> photoIds = albumPhotoMapper.selectList(eq).stream().map(AlbumPhoto::getPhotoId).toList();
        photoMapper.delete(new LambdaQueryWrapper<Photo>().in(Photo::getId, photoIds));

        // 删除相册照片关联表
        albumPhotoMapper.delete(new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getAlbumId, albumId));


    }

    // 搜索相册
    @Override
    public List<AlbumVo> searchAlbum(AlbumDto albumDto) {
        LambdaQueryWrapper<Album> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(albumDto.getUserId()), Album::getUserId, albumDto.getUserId())
                .like(ObjectUtil.isNotEmpty(albumDto.getName()), Album::getName, albumDto.getName())
                .eq(ObjectUtil.isNotEmpty(albumDto.getShowStatus()), Album::getShowStatus, albumDto.getShowStatus())
                .ge(ObjectUtil.isNotEmpty(albumDto.getCreateTimeStart()), Album::getCreateTime, albumDto.getCreateTimeStart())
                .le(ObjectUtil.isNotEmpty(albumDto.getCreateTimeEnd()), Album::getCreateTime, albumDto.getCreateTimeEnd());

        List<Album> albums = this.list(queryWrapper);
        List<AlbumVo> albumVos = BeanUtil.copyToList(albums, AlbumVo.class);
        // 用userId把username查询出来
        HashMap<Integer, String> userMap = new HashMap<>();
        // 查询所有用户信息
        List<SysUser> users = sysUserMapper.selectList(null);
        for (SysUser user : users) {
            userMap.put(user.getId(), user.getUsername());
        }
        // 将用户名设置到AlbumVo中
        for (AlbumVo albumVo : albumVos) {
            albumVo.setUserName(userMap.get(albumVo.getUserId()));
        }
        return albumVos;
    }

    // 管理员查询相册详情
    @Override
    public AlbumVo adminGetAlbum(Integer albumId) {
        Album album = this.getById(albumId);
        if (ObjectUtil.isEmpty(album)) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }

        AlbumVo albumVo = BeanUtil.copyProperties(album, AlbumVo.class);
        // 把album_id为albumId的全部照片查询出来
        List<AlbumPhoto> albumPhotos = albumPhotoMapper.selectList(new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getAlbumId, albumId));
        List<Photo> photos = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(albumPhotos)) {
            photos = photoMapper.selectList(new LambdaQueryWrapper<Photo>().in(Photo::getId, albumPhotos.stream().map(AlbumPhoto::getPhotoId).toList()));
        }

        List<PhotoVo> photoVos = BeanUtil.copyToList(photos, PhotoVo.class);
        albumVo.setPhotos(photoVos);
        SysUser sysUser = sysUserMapper.selectById(album.getUserId());
        albumVo.setUserName(sysUser.getUsername());
        return albumVo;
    }
}
