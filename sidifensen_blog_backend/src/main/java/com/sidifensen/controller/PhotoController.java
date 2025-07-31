package com.sidifensen.controller;


import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.result.Result;
import com.sidifensen.service.IPhotoService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
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
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam("albumId") Integer albumId) throws Exception {
        photoService.upload(file, albumId);
        return Result.successMsg("上传成功");
    }

    /**
     * 修改照片的展示状态
     */
    @PutMapping("/changeShowStatus")
    public Result<Object> changeShowStatus(@RequestBody @Valid PhotoDto photoDto) {
         photoService.changeShowStatus(photoDto);
         return Result.success();
    }

    /**
     * 删除照片
     */
    @DeleteMapping("/{photoId}")
    public Result<String> delete(@PathVariable("photoId") Long photoId) throws Exception {
        photoService.delete(photoId);
        return Result.successMsg("删除成功");
    }


}
