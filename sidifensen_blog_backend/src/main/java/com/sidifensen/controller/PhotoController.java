package com.sidifensen.controller;


import com.sidifensen.aspect.RateLimit;
import com.sidifensen.domain.dto.PhotoAuditDto;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.PhotoVo;
import com.sidifensen.service.PhotoService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-30
 */
@RateLimit(30)
@Validated
@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Resource
    private PhotoService photoService;

    /**
     * 上传文章图片
     */
    @RateLimit(10)
    @PostMapping("/uploadArticle")
    public Result uploadArticle(@RequestParam("file") MultipartFile file) {
        String url = photoService.uploadArticle(file);
        return Result.success(url);
    }

    /**
     * 上传照片到相册
     */
    @RateLimit(10)
    @PostMapping("/uploadAlbum")
    public Result uploadAlbum(@RequestParam("file") MultipartFile file,
                              @RequestParam("albumId") Integer albumId) {
        photoService.uploadAlbum(file, albumId);
        return Result.success();
    }

    /**
     * 上传专栏图片
     */
    @RateLimit(10)
    @PostMapping("/uploadColumn")
    public Result uploadColumn(@RequestParam("file") MultipartFile file) {
        String url = photoService.uploadColumn(file);
        return Result.success(url);
    }

    /**
     * 上传用户头像
     */
    @RateLimit(10)
    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("file") MultipartFile file) {
        photoService.uploadAvatar(file);
        return Result.success();
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/delete/{photoId}")
    public Result delete(@PathVariable("photoId") @NotNull(message = "照片ID不能为空") Integer photoId) {
        photoService.delete(photoId);
        return Result.success();
    }

    /**
     * 批量删除照片
     */
    @DeleteMapping("/delete/batch")
    public Result<String> batchDelete(@RequestBody @NotNull(message = "照片ID列表不能为空") List<Integer> photoIds) {
        photoService.batchDelete(photoIds);
        return Result.success();
    }

    // 管理端

    /**
     * 管理端删除照片
     */
    @PreAuthorize("hasAuthority('photo:delete')")
    @DeleteMapping("/admin/delete/{photoId}")
    public Result adminDelete(@PathVariable("photoId") @NotNull(message = "照片ID不能为空") Integer photoId) {
        photoService.adminDelete(photoId);
        return Result.success();
    }

    /**
     * 管理端批量删除照片
     */
    @PreAuthorize("hasAuthority('photo:deleteBatch')")
    @DeleteMapping("/admin/delete/batch")
    public Result<String> adminBatchDelete(@RequestBody @NotNull(message = "照片ID列表不能为空") List<Integer> photoIds) {
        photoService.adminBatchDelete(photoIds);
        return Result.success();
    }

    /**
     * 管理端审核图片
     */
    @PreAuthorize("hasAuthority('photo:audit')")
    @PutMapping("/admin/audit")
    public Result<String> adminAudit(@RequestBody @Valid PhotoAuditDto photoAuditDto) {
        photoService.adminAudit(photoAuditDto);
        return Result.success();
    }


    /**
     * 管理端批量审核图片
     */
    @PreAuthorize("hasAuthority('photo:auditBatch')")
    @PutMapping("/admin/auditBatch")
    public Result<String> adminAuditBatch(@RequestBody @Valid List<PhotoAuditDto> photoAuditDto) {
        photoService.adminAuditBatch(photoAuditDto);
        return Result.success();
    }

    /**
     * 获取图片列表
     */
    @PreAuthorize("hasAuthority('photo:list')")
    @GetMapping("/admin/list")
    public Result list() {
        List<PhotoVo> photoVoList = photoService.listPhotos();
        return Result.success(photoVoList);
    }

    /**
     * 搜索图片列表
     */
    @PreAuthorize("hasAuthority('photo:search')")
    @PostMapping("/admin/search")
    public Result adminSearch(@RequestBody @Valid PhotoDto photoDto) {
        List<PhotoVo> photoVoList = photoService.adminSearch(photoDto);
        return Result.success(photoVoList);
    }


}