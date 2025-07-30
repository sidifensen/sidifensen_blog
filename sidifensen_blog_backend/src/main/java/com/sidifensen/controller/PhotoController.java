package com.sidifensen.controller;


import com.sidifensen.domain.result.Result;
import com.sidifensen.service.IPhotoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

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
    public Result<Object> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam("albumId") Long albumId) throws Exception {
        photoService.upload(file, albumId);
        return Result.success();
    }


}
