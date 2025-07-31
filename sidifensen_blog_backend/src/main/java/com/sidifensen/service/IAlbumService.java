package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.entity.Album;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
public interface IAlbumService extends IService<Album> {

    // 查询相册详情
    AlbumDto getAlbum(Long albumId);

    // 新增相册
    void createAlbum(AlbumDto albumDto);

    // 更新相册
    void updateAlbum(AlbumDto albumDto);

    // 删除相册
    void deleteAlbum(Long albumId);

    // 查询用户所有的相册
    List<AlbumDto> listAlbum();

    // 查询所有用户的相册
    List<AlbumDto> listAllAlbum();

    // 修改相册展示状态
    void changeShowStatus(AlbumDto albumDto);

    // 修改相册封面
    void changeCover(AlbumDto albumDto);
}
