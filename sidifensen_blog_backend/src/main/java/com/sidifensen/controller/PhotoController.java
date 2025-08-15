package com.sidifensen.controller;


import com.sidifensen.domain.result.Result;
import com.sidifensen.service.IPhotoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author sidifensen
 * @since 2025-07-30
 */
@RestController
@RequestMapping("/photo")
public class PhotoController {

    @Resource
    private IPhotoService photoService;

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
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Integer> photoIds) throws Exception {
        photoService.batchDelete(photoIds);
        return Result.success();
    }

    // 管理端

    /**
     * 管理端删除照片
     */
    @DeleteMapping("/admin/delete/{photoId}")
    public Result adminDelete(@PathVariable("photoId") Integer photoId) throws Exception {
        photoService.adminDelete(photoId);
        return Result.success();
    }

    /**
     * 管理端批量删除照片
     */
    @DeleteMapping("/admin/batch")
    public Result<String> adminBatchDelete(@RequestBody List<Integer> photoIds) throws Exception {
        photoService.adminBatchDelete(photoIds);
        return Result.success();
    }



}