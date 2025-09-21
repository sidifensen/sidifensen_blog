package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.dto.PhotoAuditDto;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.vo.PhotoVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
public interface PhotoService extends IService<Photo> {

    // 上传图片到相册
    void uploadAlbum(MultipartFile file, Integer albumId);
    
    // 上传文章图片
    String uploadArticle(MultipartFile file);

    // 上传图片到专栏
    String uploadColumn(MultipartFile file, Integer columnId);

    // 删除图片
    void delete(Integer photoId);

    // 批量删除图片
    void batchDelete(List<Integer> photoIds);

    // 管理员删除图片
    void adminDelete(Integer photoId);

    // 管理员批量删除图片
    void adminBatchDelete(List<Integer> photoIds);

    // 审核图片
    void adminAudit(PhotoAuditDto photoAuditDto);

    // 批量审核图片
    void adminAuditBatch(List<PhotoAuditDto> photoAuditDto);

    // 获取图片列表
    List<PhotoVo> listPhotos();

    // 搜索图片
    List<PhotoVo> adminSearch(PhotoDto photoDto);

}