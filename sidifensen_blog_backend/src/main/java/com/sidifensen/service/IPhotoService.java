package com.sidifensen.service;

import com.sidifensen.domain.entity.Photo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
public interface IPhotoService extends IService<Photo> {

    // 上传图片到相册
    void upload(MultipartFile file, Long albumId) throws Exception;
}
