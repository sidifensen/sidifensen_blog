package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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
    void uploadAlbumPhoto(MultipartFile file, Integer albumId) throws Exception;

    // 删除图片
    void delete(Integer photoId) throws Exception;

    // 批量删除图片
    void batchDelete(List<Integer> photoIds) throws Exception;

    // 管理员删除图片
    void adminDelete(Integer photoId) throws Exception;

    // 管理员批量删除图片
    void adminBatchDelete(List<Integer> photoIds) throws Exception;
}