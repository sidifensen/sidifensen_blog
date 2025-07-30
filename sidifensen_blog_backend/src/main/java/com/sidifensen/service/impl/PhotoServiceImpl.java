package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.domain.enums.UploadEnum;
import com.sidifensen.domain.result.Result;
import com.sidifensen.exception.FileUploadException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.service.IPhotoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.utils.FileUploadUtils;
import com.sidifensen.utils.SecurityUtils;
import io.minio.errors.MinioException;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Service
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements IPhotoService {

    @Resource
    private FileUploadUtils fileUploadUtils;

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private AlbumMapper albumMapper;

    // 上传图片
    @Override
    public void upload(MultipartFile file, Long albumId) throws Exception {
        // 拼接userId/albumId作为目录名
        String dirName = SecurityUtils.getUserId() + "/" + albumId;
        String url = fileUploadUtils.upload(UploadEnum.ALBUM, file, dirName);
        // 保存图片信息到数据库
        Photo photo = new Photo();
        photo.setUserId(SecurityUtils.getUserId());
        photo.setUrl(url);
        photo.setAlbumId(albumId);
        photo.setShowStatus(ShowStatusEnum.PUBLIC.getCode());
        photo.setExamineStatus(ExamineStatusEnum.UNEXAMINED.getCode());
        photoMapper.insert(photo);

        // 更新相册的封面为最新上传的图片
        LambdaUpdateWrapper<Album> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Album::getId, albumId);
        updateWrapper.set(Album::getCoverUrl, url);
        albumMapper.update(null, updateWrapper);
    }
}
