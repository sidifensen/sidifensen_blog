package com.sidifensen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.config.SidifensenConfig;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.constants.MessageConstants;
import com.sidifensen.domain.constants.RabbitMQConstants;
import com.sidifensen.domain.dto.MessageDto;
import com.sidifensen.domain.dto.PhotoAuditDto;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.AlbumPhoto;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.MessageTypeEnum;
import com.sidifensen.domain.enums.UploadEnum;
import com.sidifensen.domain.result.AuditResult;
import com.sidifensen.domain.vo.PhotoVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.AlbumPhotoMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.redis.RedisService;
import com.sidifensen.service.MessageService;
import com.sidifensen.service.PhotoService;
import com.sidifensen.utils.FileUploadUtils;
import com.sidifensen.utils.ImageAuditUtils;
import com.sidifensen.utils.MyThreadFactory;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
public class PhotoServiceImpl extends ServiceImpl<PhotoMapper, Photo> implements PhotoService {

    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("PhotoServiceImpl")
    );
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
    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private MessageService messageService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private SidifensenConfig sidifensenConfig;
    @Resource
    private RedisService redisService;

    private static @NotNull Photo setPhotoExamineStatus(Photo photo, Integer status) {
        Photo updatePhoto = new Photo();
        updatePhoto.setId(photo.getId());
        if (status.equals(ExamineStatusEnum.PASS.getCode())) {
            // 审核通过，设置审核状态为审核通过
            updatePhoto.setExamineStatus(ExamineStatusEnum.PASS.getCode());
        } else if (status.equals(ExamineStatusEnum.NO_PASS.getCode())) {
            // 审核未通过，设置审核状态为审核未通过
            updatePhoto.setExamineStatus(ExamineStatusEnum.NO_PASS.getCode());
        } else if (status.equals(ExamineStatusEnum.WAIT.getCode())) {
            // 需要人工审核, 设置审核状态为待审核
            updatePhoto.setExamineStatus(ExamineStatusEnum.WAIT.getCode());
        } else {
            // 状态错误
            throw new BlogException(BlogConstants.ExamineStatusError);
        }
        return updatePhoto;
    }

    // 上传图片到相册
    @Override
    public void uploadAlbum(MultipartFile file, Integer albumId) {
        // 拼接userId/albumId作为目录名
        Integer userId = SecurityUtils.getUserId();
        String dirName = userId + "/" + albumId + "/";
        String url = fileUploadUtils.upload(UploadEnum.ALBUM, file, dirName);
        auditAndUpdate(userId, url, albumId);
    }

    @Override
    public String uploadArticle(MultipartFile file) {
        Integer userId = SecurityUtils.getUserId();
        String dirName = userId + "/";
        String url = fileUploadUtils.upload(UploadEnum.ARTICLE, file, dirName);
        auditAndUpdate(userId, url);
        return url;
    }

    @Override
    public String uploadColumn(MultipartFile file) {
        Integer userId = SecurityUtils.getUserId();
        String dirName = userId + "/";
        String url = fileUploadUtils.upload(UploadEnum.COLUMN, file, dirName);
        auditAndUpdate(userId, url);
        return url;
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        Integer userId = SecurityUtils.getUserId();
        String dirName = userId + "/";
        String url = fileUploadUtils.upload(UploadEnum.USER_AVATAR, file, dirName);
        auditAndUpdate(userId, url);
        return url;
    }

    // 相册图片审核
    private void auditAndUpdate(Integer userId, String url, Integer albumId) {
        executorService.execute(() -> {
            // 保存图片信息到数据库
            Photo photo = new Photo();
            photo.setUserId(userId);
            photo.setUrl(url);
            photo.setExamineStatus(ExamineStatusEnum.WAIT.getCode());// 初始设置为待审核状态
            photoMapper.insert(photo);

            Integer status = 0;
            // 图片自动审核
            if (sidifensenConfig.isPhotoAutoAudit()) {
                AuditResult imageAuditResult = imageAuditUtils.auditImageWithDetails(url);
                status = imageAuditResult.getStatus();
                // 根据审核结果设置审核状态
                Photo updatePhoto = setPhotoExamineStatus(photo, status);
                // 更新图片审核状态
                photoMapper.updateById(updatePhoto);
            }

            // 保存相册照片关联记录
            albumPhotoMapper.insert(new AlbumPhoto(null, albumId, photo.getId()));

            // 更新相册的封面为最新上传的图片（仅当审核通过时）
            if (sidifensenConfig.isPhotoAutoAudit() && status.equals(ExamineStatusEnum.PASS.getCode())) {
                LambdaUpdateWrapper<Album> updateWrapper = new LambdaUpdateWrapper<>();
                updateWrapper.eq(Album::getId, albumId).set(Album::getCoverUrl, url);
                albumMapper.update(null, updateWrapper);
            }

            if (!sidifensenConfig.isPhotoAutoAudit() || status.equals(ExamineStatusEnum.WAIT.getCode())) {
                // 需要人工审核，发送消息给管理员
                String text = MessageConstants.ImageNeedReview(photo.getId());
                MessageDto messageDto = new MessageDto();
                messageDto.setType(MessageTypeEnum.SYSTEM.getCode());
                messageDto.setContent(text);
                messageService.sendToAdmin(messageDto);
                // 发送邮件给管理员
                HashMap<String, Object> sendEmail = new HashMap<>();
                sendEmail.put("text", String.format(MessageConstants.IMAGE_NEED_REVIEW, photo.getId()));
                rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange, RabbitMQConstants.Examine_Routing_Key, sendEmail);
            }


            redisService.updateAlbumPhotos(albumId);
        });
    }

    // 图片审核(文章和专栏)
    private void auditAndUpdate(Integer userId, String url) {
        executorService.execute(() -> {
            try {
                // 保存图片信息到数据库
                Photo photo = new Photo();
                photo.setUserId(userId);
                photo.setUrl(url);
                photo.setExamineStatus(ExamineStatusEnum.WAIT.getCode());// 初始设置为待审核状态
                photoMapper.insert(photo);

                Integer status = 0;
                // 图片自动审核
                if (sidifensenConfig.isPhotoAutoAudit()) {
                    AuditResult imageAuditResult = imageAuditUtils.auditImageWithDetails(url);
                    status = imageAuditResult.getStatus();
                    // 根据审核结果设置审核状态
                    Photo updatePhoto = setPhotoExamineStatus(photo, status);
                    // 更新图片审核状态
                    photoMapper.updateById(updatePhoto);
                }

                if (!sidifensenConfig.isPhotoAutoAudit() || status.equals(ExamineStatusEnum.WAIT.getCode())) {
                    // 需要人工审核，发送消息给管理员
                    String text = MessageConstants.ImageNeedReview(photo.getId());
                    MessageDto messageDto = new MessageDto();
                    messageDto.setType(MessageTypeEnum.SYSTEM.getCode());
                    messageDto.setContent(text);
                    messageService.sendToAdmin(messageDto);
                    // 发送邮件给管理员
                    HashMap<String, Object> sendEmail = new HashMap<>();
                    sendEmail.put("text", String.format(MessageConstants.IMAGE_NEED_REVIEW, photo.getId()));
                    rabbitTemplate.convertAndSend(RabbitMQConstants.Examine_Exchange, RabbitMQConstants.Examine_Routing_Key, sendEmail);
                }

            } catch (Exception e) {
                log.error("图片异步处理失败", e);
            }
        });
    }

    // 删除照片
    @Override
    public void delete(Integer photoId) {
        Photo photo = photoMapper.selectById(photoId);
        if (ObjectUtil.isEmpty(photo)) {
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }
        if (photo.getUserId() != SecurityUtils.getUserId()) {
            throw new BlogException(BlogConstants.CannotHandleOthersPhoto);
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

        // 更新相册照片关联
        redisService.updateAlbumPhotos(albumPhoto.getAlbumId());
    }

    // 批量删除照片
    @Override
    public void batchDelete(List<Integer> photoIds) {
        Integer userId = SecurityUtils.getUserId();
        // 查询要删除的照片信息
        List<Photo> photos = photoMapper.selectBatchIds(photoIds);
        if (ObjectUtil.isEmpty(photos)) {
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }

        // 在photos中对比每一张图片的userId是否与当前登录用户一致，如果不一致则抛出异常
        for (Photo photo : photos) {
            if (!photo.getUserId().equals(userId)) {
                throw new BlogException(BlogConstants.CannotHandleOthersPhoto);
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

        // 批量更新相册照片关联
        redisService.multiUpdateAlbumPhotos(albumPhotos.stream().map(AlbumPhoto::getAlbumId).collect(Collectors.toList()));
    }

    @Override
    public void adminDelete(Integer photoId) {
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

        // 更新相册照片关联
        redisService.updateAlbumPhotos(albumPhoto.getAlbumId());
    }

    @Override
    public void adminBatchDelete(List<Integer> photoIds) {
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
                log.error("照片ID {} 没有找到对应的相册ID", photo.getId());
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

        // 批量更新相册照片关联
        redisService.multiUpdateAlbumPhotos(albumPhotos.stream().map(AlbumPhoto::getAlbumId).collect(Collectors.toList()));
    }

    @Override
    public void adminAudit(PhotoAuditDto photoAuditDto) {
        // 更新照片审核状态
        LambdaUpdateWrapper<Photo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Photo::getId, photoAuditDto.getPhotoId());
        updateWrapper.set(Photo::getExamineStatus, photoAuditDto.getExamineStatus());
        photoMapper.update(null, updateWrapper);

        // 如果是审核通过
        if (photoAuditDto.getExamineStatus() == 1) {
            AlbumPhoto albumPhoto = albumPhotoMapper.selectById(photoAuditDto.getPhotoId());
            if (ObjectUtil.isNotEmpty(albumPhoto)) {
                // 更新相册封面
                Album album = albumMapper.selectById(albumPhoto.getAlbumId());
                if (ObjectUtil.isNotEmpty(album)) {
                    Photo photo = photoMapper.selectById(photoAuditDto.getPhotoId());
                    album.setCoverUrl(photo.getUrl());
                    albumMapper.updateById(album);
                    // 更新相册照片关联
                    redisService.updateAlbumPhotos(albumPhoto.getAlbumId());
                }
            }
        }
    }

    @Override
    public void adminAuditBatch(List<PhotoAuditDto> photoAuditDto) {
        List<Integer> photoIds = photoAuditDto.stream().map(PhotoAuditDto::getPhotoId).collect(Collectors.toList());
        // 更新照片审核状态
        LambdaUpdateWrapper<Photo> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Photo::getId, photoIds);
        updateWrapper.set(Photo::getExamineStatus, photoAuditDto.get(0).getExamineStatus());
        photoMapper.update(null, updateWrapper);

        List<Integer> albumIds = new ArrayList<>();
        // 更新相册封面
        for (PhotoAuditDto dto : photoAuditDto) {
            if (dto.getExamineStatus() == 1) {
                AlbumPhoto albumPhoto = albumPhotoMapper.selectById(dto.getPhotoId());
                if (ObjectUtil.isNotEmpty(albumPhoto)) {
                    Album album = albumMapper.selectById(albumPhoto.getAlbumId());
                    if (ObjectUtil.isNotEmpty(album)) {
                        Photo photo = photoMapper.selectById(dto.getPhotoId());
                        album.setCoverUrl(photo.getUrl());
                        albumMapper.updateById(album);
                    }
                    albumIds.add(albumPhoto.getAlbumId());
                }
            }
        }

        // 批量更新相册照片关联
        redisService.multiUpdateAlbumPhotos(albumIds);
    }

    @Override
    public List<PhotoVo> listPhotos() {
        List<Photo> photos = photoMapper.selectList(null);
        if (ObjectUtil.isEmpty(photos)) {
            return List.of();
        }
        List<Integer> userIds = photos.stream().map(Photo::getUserId).collect(Collectors.toList());
        List<SysUser> sysUsers = sysUserMapper.selectBatchIds(userIds);
        Map<Integer, String> userMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));

        List<PhotoVo> photoVos = BeanUtil.copyToList(photos, PhotoVo.class);
        // 填充用户名
        photoVos.forEach(photoVo -> photoVo.setUsername(userMap.get(photoVo.getUserId())));
        return photoVos;
    }

    @Override
    public List<PhotoVo> adminSearch(PhotoDto photoDto) {
        // 管理员搜索照片 (
        LambdaQueryWrapper<Photo> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(ObjectUtil.isNotEmpty(photoDto.getUserId()), Photo::getUserId, photoDto.getUserId())
                .eq(ObjectUtil.isNotEmpty(photoDto.getExamineStatus()), Photo::getExamineStatus, photoDto.getExamineStatus())
                .ge(ObjectUtil.isNotEmpty(photoDto.getCreateTimeStart()), Photo::getCreateTime, photoDto.getCreateTimeStart())
                .le(ObjectUtil.isNotEmpty(photoDto.getCreateTimeEnd()), Photo::getCreateTime, photoDto.getCreateTimeEnd());
        List<Photo> photos = photoMapper.selectList(queryWrapper);
        if (ObjectUtil.isEmpty(photos)) {
            return List.of();
        }
        List<Integer> userIds = photos.stream().map(Photo::getUserId).collect(Collectors.toList());
        List<SysUser> sysUsers = sysUserMapper.selectBatchIds(userIds);
        Map<Integer, String> userMap = sysUsers.stream().collect(Collectors.toMap(SysUser::getId, SysUser::getUsername));

        List<PhotoVo> photoVos = BeanUtil.copyToList(photos, PhotoVo.class);
        // 填充用户名
        photoVos.forEach(photoVo -> photoVo.setUsername(userMap.get(photoVo.getUserId())));
        return photoVos;
    }

}