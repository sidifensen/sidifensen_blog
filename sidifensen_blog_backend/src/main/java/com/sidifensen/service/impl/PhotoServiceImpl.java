package com.sidifensen.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.AlbumPhoto;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.ImageAuditStatusEnum;
import com.sidifensen.domain.enums.UploadEnum;
import com.sidifensen.domain.result.ImageAuditResult;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.AlbumPhotoMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.service.IPhotoService;
import com.sidifensen.utils.FileUploadUtils;
import com.sidifensen.utils.ImageAuditUtils;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-07-30
 */
@Service
@Slf4j
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements IPhotoService {

    @Resource
    private FileUploadUtils fileUploadUtils;

    @Resource
    private PhotoMapper photoMapper;

    @Resource
    private AlbumMapper albumMapper;

    @Resource
    private ImageAuditUtils imageAuditUtils;

    @Resource
    private AlbumPhotoMapper albumPhotoMapper;

    // 创建线程池用于异步处理
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);

    // 上传图片
    @Override
    public void uploadAlbumPhoto(MultipartFile file, Integer albumId) throws Exception {
        // 拼接userId/albumId作为目录名
        Integer userId = SecurityUtils.getUserId();
        String dirName = userId + "/" + albumId + "/";
        String url = fileUploadUtils.upload(UploadEnum.ALBUM, file, dirName);
        auditAndUpdate(userId, url, albumId);
    }

    private void auditAndUpdate(Integer userId, String url, Integer albumId) {
        // 异步处理图片审核
        executorService.execute(() -> {
            try {
                // 保存图片信息到数据库
                Photo photo = new Photo();
                photo.setUserId(userId);
                photo.setUrl(url);
                // 初始设置为待审核状态
                photo.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                photoMapper.insert(photo);

                ImageAuditResult imageAuditResult = imageAuditUtils.auditImageWithDetails(url);
                Integer status = imageAuditResult.getStatus();

                // 根据审核结果设置审核状态
                Photo updatePhoto = new Photo();
                updatePhoto.setId(photo.getId());
                if (status.equals(ImageAuditStatusEnum.PASS.getCode())) {
                    // 审核通过，设置审核状态为审核通过
                    updatePhoto.setExamineStatus(ExamineStatusEnum.PASS.getCode());
                } else if (status.equals(ImageAuditStatusEnum.REJECT.getCode())) {
                    // 审核未通过，设置审核状态为审核未通过
                    updatePhoto.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
                } else if (status.equals(ImageAuditStatusEnum.MANUAL_REVIEW.getCode())) {
                    // 需要人工审核, 设置审核状态为待审核
                    updatePhoto.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                } else {
                    // 状态错误
                    throw new BlogException(BlogConstants.ExamineStatusError);
                }

                photoMapper.updateById(updatePhoto);

                AlbumPhoto albumPhoto = new AlbumPhoto();
                albumPhoto.setAlbumId(albumId);
                albumPhoto.setPhotoId(photo.getId());
                albumPhotoMapper.insert(albumPhoto);

                // 更新相册的封面为最新上传的图片（仅当审核通过时）
                if (status == 0) {
                    LambdaUpdateWrapper<Album> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.eq(Album::getId, albumId);
                    updateWrapper.set(Album::getCoverUrl, url);
                    albumMapper.update(null, updateWrapper);
                }

            } catch (Exception e) {
                log.error("图片异步处理失败", e);
            }
        });
    }

    private void auditAndUpdate(Integer userId, String url) {
        // 异步处理图片审核
        executorService.execute(() -> {
            try {
                // 保存图片信息到数据库
                Photo photo = new Photo();
                photo.setUserId(userId);
                photo.setUrl(url);
                // 初始设置为待审核状态
                photo.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                photoMapper.insert(photo);

                ImageAuditResult imageAuditResult = imageAuditUtils.auditImageWithDetails(url);
                Integer status = imageAuditResult.getStatus();

                // 根据审核结果设置审核状态
                Photo updatePhoto = new Photo();
                updatePhoto.setId(photo.getId());
                if (status.equals(ImageAuditStatusEnum.PASS.getCode())) {
                    // 审核通过，设置审核状态为审核通过
                    updatePhoto.setExamineStatus(ExamineStatusEnum.PASS.getCode());
                } else if (status.equals(ImageAuditStatusEnum.REJECT.getCode())) {
                    // 审核未通过，设置审核状态为审核未通过
                    updatePhoto.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
                } else if (status.equals(ImageAuditStatusEnum.MANUAL_REVIEW.getCode())) {
                    // 需要人工审核, 设置审核状态为待审核
                    updatePhoto.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
                } else {
                    // 状态错误
                    throw new BlogException(BlogConstants.ExamineStatusError);
                }

                photoMapper.updateById(updatePhoto);
            } catch (Exception e) {
                log.error("图片异步处理失败", e);
            }
        });
    }

    // 删除照片
    @Override
    public void delete(Integer photoId) throws Exception {
        Photo photo = photoMapper.selectById(photoId);
        if (ObjectUtil.isEmpty(photo)) {
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }
        if (photo.getUserId() != SecurityUtils.getUserId()){
            throw new BlogException(BlogConstants.CannotHandleOtherUserPhoto);
        }
        AlbumPhoto albumPhoto = albumPhotoMapper.selectOne(new LambdaUpdateWrapper<AlbumPhoto>().eq(AlbumPhoto::getPhotoId, photoId));
        if (ObjectUtil.isEmpty(albumPhoto)) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }

        // 删除照片信息
        photoMapper.deleteById(photoId);
        // 删除相册照片关联记录
        albumPhotoMapper.delete(new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getPhotoId, photoId));

        String fileName = fileUploadUtils.getFileName(photo.getUrl());
        String dirName = UploadEnum.ALBUM.getDir() + SecurityUtils.getUserId() + "/" + albumPhoto.getAlbumId() + "/";
        // 删除照片对应的文件
        fileUploadUtils.deleteFile(dirName, fileName);
    }

    // 批量删除照片
    @Override
    public void batchDelete(List<Integer> photoIds) throws Exception {
        Integer userId = SecurityUtils.getUserId();
        // 查询要删除的照片信息
        List<Photo> photos = photoMapper.selectBatchIds(photoIds);
        if (ObjectUtil.isEmpty(photos)) {
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }

        // 在photos中对比每一张图片的userId是否与当前登录用户一致，如果不一致则抛出异常
        for (Photo photo : photos) {
            if (!photo.getUserId().equals(userId)) {
                throw new BlogException(BlogConstants.CannotHandleOtherUserPhoto);
            }
        }

        // 收集要删除的文件路径信息
        List<String> filePaths = new ArrayList<>();
        String baseDir = UploadEnum.ALBUM.getDir() + userId + "/";

        // 获取所有要删除的照片对应的相册id
        List<AlbumPhoto> albumPhotos = albumPhotoMapper.selectList(new LambdaQueryWrapper<AlbumPhoto>().in(AlbumPhoto::getPhotoId, photoIds));

        // 构建照片ID到相册ID的映射
        Map<Integer, Integer> photoToAlbumMap = new HashMap<>();
        for (AlbumPhoto albumPhoto : albumPhotos) {
            photoToAlbumMap.put(albumPhoto.getPhotoId(), albumPhoto.getAlbumId());
        }

        // 构建所有要删除的文件完整路径
        for (Photo photo : photos) {
            // 通过映射获取相册ID，而不是从photo对象中获取（photo对象中没有albumId字段）
            Integer albumId = photoToAlbumMap.get(photo.getId());
            if (albumId == null) {
                log.warn("照片ID {} 没有找到对应的相册ID", photo.getId());
                continue;
            }
            
            String dirName = baseDir + albumId + "/";
            String fileName = fileUploadUtils.getFileName(photo.getUrl());
            String fullPath = dirName + fileName;
            filePaths.add(fullPath);
        }

        // 批量删除照片信息
        photoMapper.deleteBatchIds(photoIds);
        
        // 删除相册照片关联记录
        albumPhotoMapper.delete(new LambdaQueryWrapper<AlbumPhoto>().in(AlbumPhoto::getPhotoId, photoIds));

        // 批量删除文件
        fileUploadUtils.deleteFiles(filePaths);
    }

    @Override
    public void adminDelete(Integer photoId) throws Exception {
        Photo photo = photoMapper.selectById(photoId);
        if (ObjectUtil.isEmpty(photo)) {
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }
        AlbumPhoto albumPhoto = albumPhotoMapper.selectOne(new LambdaUpdateWrapper<AlbumPhoto>().eq(AlbumPhoto::getPhotoId, photoId));
        if (ObjectUtil.isEmpty(albumPhoto)) {
            throw new BlogException(BlogConstants.NotFoundAlbum);
        }
        // 删除照片信息
        photoMapper.deleteById(photoId);
        // 删除相册照片关联记录
        albumPhotoMapper.delete(new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getPhotoId, photoId));

        String fileName = fileUploadUtils.getFileName(photo.getUrl());
        String dirName = UploadEnum.ALBUM.getDir() + SecurityUtils.getUserId() + "/" + albumPhoto.getAlbumId() + "/";
        // 删除照片对应的文件
        fileUploadUtils.deleteFile(dirName, fileName);
    }

    @Override
    public void adminBatchDelete(List<Integer> photoIds) throws Exception {
        Integer userId = SecurityUtils.getUserId();
        // 查询要删除的照片信息
        List<Photo> photos = photoMapper.selectBatchIds(photoIds);
        if (ObjectUtil.isEmpty(photos)) {
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }

        // 收集要删除的文件路径信息
        List<String> filePaths = new ArrayList<>();
        String baseDir = UploadEnum.ALBUM.getDir() + userId + "/";

        // 获取所有要删除的照片对应的相册id
        List<AlbumPhoto> albumPhotos = albumPhotoMapper.selectList(new LambdaQueryWrapper<AlbumPhoto>().in(AlbumPhoto::getPhotoId, photoIds));

        // 构建照片ID到相册ID的映射
        Map<Integer, Integer> photoToAlbumMap = new HashMap<>();
        for (AlbumPhoto albumPhoto : albumPhotos) {
            photoToAlbumMap.put(albumPhoto.getPhotoId(), albumPhoto.getAlbumId());
        }

        // 构建所有要删除的文件完整路径
        for (Photo photo : photos) {
            // 通过映射获取相册ID，而不是从photo对象中获取（photo对象中没有albumId字段）
            Integer albumId = photoToAlbumMap.get(photo.getId());
            if (albumId == null) {
                log.warn("照片ID {} 没有找到对应的相册ID", photo.getId());
                continue;
            }

            String dirName = baseDir + albumId + "/";
            String fileName = fileUploadUtils.getFileName(photo.getUrl());
            String fullPath = dirName + fileName;
            filePaths.add(fullPath);
        }

        // 批量删除照片信息
        photoMapper.deleteBatchIds(photoIds);

        // 删除相册照片关联记录
        albumPhotoMapper.delete(new LambdaQueryWrapper<AlbumPhoto>().in(AlbumPhoto::getPhotoId, photoIds));

        // 批量删除文件
        fileUploadUtils.deleteFiles(filePaths);
    }


}