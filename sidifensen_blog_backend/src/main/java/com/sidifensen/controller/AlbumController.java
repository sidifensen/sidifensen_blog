package com.sidifensen.controller;


import com.sidifensen.aspect.TimeConsuming;
import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.service.IAlbumService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-30
 */
@RestController
@RequestMapping("/album")
public class AlbumController {

    @Resource
    private IAlbumService albumService;

    /**
     * 查看相册详情
     * @param albumId
     * @return
     */
    @TimeConsuming
    @GetMapping("/{albumId}")
    public Result<Object> getAlbum(@PathVariable("albumId") Long albumId) {
        AlbumVo album = albumService.getAlbum(albumId);
        return Result.success(album);
    }

    /**
     * 新建相册
     * @param albumDto
     * @return
     */
    @PostMapping("/create")
    public Result<String> createAlbum(@RequestBody @Valid AlbumDto albumDto) {
        albumService.createAlbum(albumDto);
        return Result.success();
    }

    /**
     * 更新相册
     * @param albumDto
     * @return
     */
    @PutMapping("/update")
    public Result<String> updateAlbum(@RequestBody @Valid AlbumDto albumDto) {
        albumService.updateAlbum(albumDto);
        return Result.success();
    }

    /**
     * 删除相册
     * @param albumId
     * @return
     */
    @DeleteMapping("/{albumId}")
    public Result<String> deleteAlbum(@PathVariable("albumId") Integer albumId) {
        albumService.deleteAlbum(albumId);
        return Result.success();
    }

    /**
     * 查看当前用户所有的相册
     * @return
     */
    @GetMapping("/list")
    public Result<Object> listAlbum() {
        List<AlbumVo> albumVos = albumService.listAlbum();
        return Result.success(albumVos);
    }

    /**
     * 查看所有用户的相册(公开)
     */
    @GetMapping("/listAll")
    public Result<Object> listAllAlbum() {
        List<AlbumVo> albumVos = albumService.listAllAlbum();
        return Result.success(albumVos);
    }

    /**
     * 修改相册展示状态
     * @param albumDto
     * @return
     */
    @PutMapping("/changeShowStatus")
    public Result<String> changeShowStatus(@RequestBody @Valid AlbumDto albumDto) {
        albumService.changeShowStatus(albumDto);
        return Result.success();
    }

    /**
     * 相册更换封面
     * @param albumDto
     * @return
     */
    @PutMapping("/changeCover")
    public Result<String> changeCover(@RequestBody @Valid AlbumDto albumDto) {
        albumService.changeCover(albumDto);
        return Result.success();
    }

    // 管理端

    /**
     * 管理端查看所有用户的相册
     */
    @PreAuthorize("hasAuthority('album:list')")
    @GetMapping("/admin/list")
    public Result<Object> adminList() {
        List<AlbumVo> albumVos = albumService.adminList();
        return Result.success(albumVos);
    }

    /**
     * 更新相册
     * @param albumDto
     * @return
     */
    @PreAuthorize("hasAuthority('album:update')")
    @PutMapping("/admin/update")
    public Result<String> adminUpdateAlbum(@RequestBody @Valid AlbumDto albumDto) {
        albumService.adminUpdateAlbum(albumDto);
        return Result.success();
    }

    /**
     * 删除相册
     * @param albumId
     * @return
     */
    @PreAuthorize("hasAuthority('album:delete')")
    @DeleteMapping("/admin/{albumId}")
    public Result<String> adminDeleteAlbum(@PathVariable("albumId") Integer albumId) {
        albumService.adminDeleteAlbum(albumId);
        return Result.success();
    }


}
