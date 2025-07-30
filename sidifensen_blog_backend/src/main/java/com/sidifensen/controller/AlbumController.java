package com.sidifensen.controller;


import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.IAlbumService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
    @GetMapping("/{albumId}")
    public Result<Object> getAlbum(@PathVariable("albumId") Long albumId) {
        AlbumDto album = albumService.getAlbum(albumId);
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
        return Result.successMsg("相册创建成功");
    }

    /**
     * 更新相册
     * @param albumDto
     * @return
     */
    @PutMapping("/update")
    public Result<String> updateAlbum(@RequestBody @Valid AlbumDto albumDto) {
        albumService.updateAlbum(albumDto);
        return Result.successMsg("相册更新成功");
    }

    /**
     * 删除相册
     * @param albumId
     * @return
     */
    @DeleteMapping("/delete/{albumId}")
    public Result<String> deleteAlbum(@PathVariable("albumId") Long albumId) {
        albumService.deleteAlbum(albumId);
        return Result.successMsg("相册删除成功");
    }

    /**
     * 查看用户所有的相册
     * @return
     */
    @GetMapping("/list")
    public Result<Object> listAlbum() {
        List<AlbumDto> albumDtos = albumService.listAlbum();
        return Result.success(albumDtos);
    }

    /**
     * 修改相册展示状态
     * @param albumDto
     * @return
     */
    @PutMapping("/showStatus")
    public Result<String> showStatus(@RequestBody @Valid AlbumDto albumDto) {
        albumService.showStatus(albumDto);
        return Result.successMsg("相册展示状态修改成功");
    }

    /**
     * 相册更换封面
     * @param albumDto
     * @return
     */
    @PutMapping("/cover")
    public Result<String> changeCover(@RequestBody @Valid AlbumDto albumDto) {
        albumService.changeCover(albumDto);
        return Result.successMsg("相册封面更换成功");
    }

}
