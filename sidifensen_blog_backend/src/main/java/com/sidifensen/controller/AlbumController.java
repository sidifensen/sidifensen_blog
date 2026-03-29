package com.sidifensen.controller;


import com.sidifensen.aspect.OperationLog;
import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.AlbumDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.domain.enums.OperationTypeEnum;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.service.AlbumService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-30
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/album")
public class AlbumController {

    @Resource
    private AlbumService albumService;

    /**
     * 查看相册详情
     *
     * @param albumId
     * @return
     */
    @RateLimit
    @GetMapping("/get/{albumId}")
    public Result<Object> getAlbum(@PathVariable("albumId") @NotNull(message = "相册id不能为空") Integer albumId) {
        AlbumVo album = albumService.getAlbum(albumId);
        return Result.success(album);
    }

    /**
     * 新建相册
     *
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
     *
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
     *
     * @param albumId
     * @return
     */
    @DeleteMapping("/delete/{albumId}")
    public Result<String> deleteAlbum(@PathVariable("albumId") @NotNull(message = "相册ID不能为空") Integer albumId) {
        albumService.deleteAlbum(albumId);
        return Result.success();
    }

    /**
     * 查看当前用户所有的相册
     *
     * @return
     */
    @RateLimit
    @GetMapping("/list")
    public Result<Object> listAlbum() {
        List<AlbumVo> albumVos = albumService.listAlbum();
        return Result.success(albumVos);
    }

    /**
     * 查看所有用户的相册(公开)
     */
    @RateLimit
    @GetMapping("/listAll")
    public Result<Object> listAllAlbum() {
        List<AlbumVo> albumVos = albumService.listAllAlbum();
        return Result.success(albumVos);
    }

    /**
     * 搜索相册（公开）
     */
    @RateLimit
    @GetMapping("/search")
    public Result<List<AlbumVo>> searchAlbum(@RequestParam @NotNull(message = "搜索关键词不能为空") String keyword) {
        List<AlbumVo> albumVos = albumService.searchAlbum(keyword);
        return Result.success(albumVos);
    }

    /**
     * 修改相册展示状态
     *
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
     *
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
    @OperationLog(module = "相册管理", type = OperationTypeEnum.SELECT, description = "管理员获取相册列表")
    @PreAuthorize("hasAuthority('album:list')")
    @GetMapping("/admin/list")
    public Result<PageVo<List<AlbumVo>>> adminList(
            @RequestParam(defaultValue = "1") @NotNull(message = "页码不能为空") Integer pageNum,
            @RequestParam(defaultValue = "10") @NotNull(message = "每页大小不能为空") Integer pageSize) {
        PageVo<List<AlbumVo>> albumVos = albumService.adminList(pageNum, pageSize);
        return Result.success(albumVos);
    }

    /**
     * 管理端查看相册详情
     *
     * @param albumId
     * @return
     */
    @OperationLog(module = "相册管理", type = OperationTypeEnum.GET, description = "管理员获取相册详情")
    @PreAuthorize("hasAuthority('album:detail')")
    @GetMapping("/admin/get/{albumId}")
    public Result<Object> adminGetAlbum(@PathVariable("albumId") @NotNull(message = "相册ID不能为空") Integer albumId) {
        AlbumVo album = albumService.adminGetAlbum(albumId);
        return Result.success(album);
    }

    /**
     * 管理端更新相册
     *
     * @param albumDto
     * @return
     */
    @OperationLog(module = "相册管理", type = OperationTypeEnum.UPDATE, description = "管理员更新相册")
    @PreAuthorize("hasAuthority('album:update')")
    @PutMapping("/admin/update")
    public Result<String> adminUpdateAlbum(@RequestBody @Valid AlbumDto albumDto) {
        albumService.adminUpdateAlbum(albumDto);
        return Result.success();
    }

    /**
     * 管理端搜索相册
     */
    @OperationLog(module = "相册管理", type = OperationTypeEnum.SEARCH, description = "管理员搜索相册")
    @PreAuthorize("hasAuthority('album:search')")
    @PostMapping("/admin/search")
    public Result<PageVo<List<AlbumVo>>> searchAlbum(@RequestBody @Valid AlbumDto albumDto) {
        PageVo<List<AlbumVo>> albumVos = albumService.searchAlbum(albumDto);
        return Result.success(albumVos);
    }

    /**
     * 管理端删除相册
     *
     * @param albumId
     * @return
     */
    @OperationLog(module = "相册管理", type = OperationTypeEnum.DELETE, description = "管理员删除相册")
    @PreAuthorize("hasAuthority('album:delete')")
    @DeleteMapping("/admin/delete/{albumId}")
    public Result<String> adminDeleteAlbum(@PathVariable("albumId") @NotNull(message = "相册ID不能为空") Integer albumId) {
        albumService.adminDeleteAlbum(albumId);
        return Result.success();
    }

}
