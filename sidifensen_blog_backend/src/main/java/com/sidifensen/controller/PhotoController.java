package com.sidifensen.controller;


import com.sidifensen.aspect.TimeConsuming;
import com.sidifensen.domain.dto.PhotoAuditDto;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.domain.vo.PhotoVo;
import com.sidifensen.service.PhotoService;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-30
 */
@RestController
@RequestMapping("/photo")
@TimeConsuming
public class PhotoController {

    @Resource
    private PhotoService photoService;

    /**
     * 上传照片到相册
     */
    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file,
                         @RequestParam("albumId") Integer albumId) throws Exception {
        photoService.uploadAlbumPhoto(file, albumId);
        return Result.success();
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/delete/{photoId}")
    public Result delete(@PathVariable("photoId") Integer photoId) throws Exception {
        photoService.delete(photoId);
        return Result.success();
    }

    /**
     * 批量删除照片
     */
    @DeleteMapping("/delete/batch")
    public Result<String> batchDelete(@RequestBody List<Integer> photoIds) throws Exception {
        photoService.batchDelete(photoIds);
        return Result.success();
    }

    // 管理端

    /**
     * 管理端删除照片
     */
    @PreAuthorize("hasAuthority('photo:delete')")
    @DeleteMapping("/admin/delete/{photoId}")
    public Result adminDelete(@PathVariable("photoId") Integer photoId) throws Exception {
        photoService.adminDelete(photoId);
        return Result.success();
    }

    /**
     * 管理端批量删除照片
     */
    @PreAuthorize("hasAuthority('photo:deleteBatch')")
    @DeleteMapping("/admin/delete/batch")
    public Result<String> adminBatchDelete(@RequestBody List<Integer> photoIds) throws Exception {
        photoService.adminBatchDelete(photoIds);
        return Result.success();
    }

    /**
     * 管理端审核图片
     */
    @PreAuthorize("hasAuthority('photo:audit')")
    @PutMapping("/admin/audit")
    public Result<String> adminAudit(@RequestBody PhotoAuditDto photoAuditDto) {
        photoService.adminAudit(photoAuditDto);
        return Result.success();
    }


    /**
     * 管理端批量审核图片
     */
    @PreAuthorize("hasAuthority('photo:auditBatch')")
    @PutMapping("/admin/auditBatch")
    public Result<String> adminAuditBatch(@RequestBody List<PhotoAuditDto> photoAuditDto) {
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
    public Result adminSearch(@RequestBody PhotoDto photoDto) {
        List<PhotoVo> photoVoList = photoService.adminSearch(photoDto);
        return Result.success(photoVoList);
    }


}