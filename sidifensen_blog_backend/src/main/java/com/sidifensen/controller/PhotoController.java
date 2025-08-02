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
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam("albumId") Integer albumId) throws Exception {
        photoService.upload(file, albumId);
        return Result.successMsg("上传成功");
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/{photoId}")
    public Result<String> delete(@PathVariable("photoId") Integer photoId) throws Exception {
        photoService.delete(photoId);
        return Result.successMsg("删除成功");
    }

    /**
     * 批量删除照片
     */
    @DeleteMapping("/batch")
    public Result<String> batchDelete(@RequestBody List<Integer> photoIds) throws Exception {
        photoService.batchDelete(photoIds);
        return Result.successMsg("批量删除成功");
    }

}
