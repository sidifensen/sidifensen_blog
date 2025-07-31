package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.entity.Photo;
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
    void upload(MultipartFile file, Integer albumId) throws Exception;

    // 修改图片的展示状态
    void changeShowStatus(PhotoDto photoDto);

    // 删除图片
    void delete(Long photoId) throws Exception;

}
