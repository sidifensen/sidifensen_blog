package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.PhotoDto;
import com.sidifensen.domain.entity.Album;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.enums.ExamineStatusEnum;
import com.sidifensen.domain.enums.ShowStatusEnum;
import com.sidifensen.domain.enums.UploadEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.AlbumMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.service.IPhotoService;
import com.sidifensen.utils.FileUploadUtils;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

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
    public void upload(MultipartFile file, Integer albumId) throws Exception {
        // 拼接userId/albumId作为目录名
        String dirName = SecurityUtils.getUserId() + "/" + albumId + "/";
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

        // TODO 异步发送信息给管理员审核

    }

    // 修改照片的展示状态
    @Override
    public void changeShowStatus(PhotoDto photoDto) {
        Photo photo = new Photo();
        photo.setId(photoDto.getId());
        photo.setShowStatus(photoDto.getShowStatus());
        photoMapper.updateById(photo);
    }

    // 删除照片
    @Override
    public void delete(Integer photoId) throws Exception {
        Photo photo = photoMapper.selectById(photoId);
        if (photo == null){
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }
        String fileName = fileUploadUtils.getFileName(photo.getUrl());
        String dirName = UploadEnum.ALBUM.getDir() + SecurityUtils.getUserId() + "/" + photo.getAlbumId() + "/";
        // 删除照片信息
        photoMapper.deleteById(photoId);
        // 删除照片对应的文件
        fileUploadUtils.deleteFile(dirName, fileName);
    }

    // 批量删除照片
    @Override
    public void batchDelete(List<Integer> photoIds) throws Exception {
        // 查询要删除的照片信息
        List<Photo> photos = photoMapper.selectBatchIds(photoIds);
        if (photos == null || photos.isEmpty()) {
            throw new BlogException(BlogConstants.NotFoundPhoto);
        }
        
        // 收集要删除的文件路径信息
        List<String> filePaths = new ArrayList<>();
        String baseDir = UploadEnum.ALBUM.getDir() + SecurityUtils.getUserId() + "/";
        
        // 构建所有要删除的文件完整路径
        for (Photo photo : photos) {
            String dirName = baseDir + photo.getAlbumId() + "/";
            String fileName = fileUploadUtils.getFileName(photo.getUrl());
            String fullPath = dirName + fileName;
            filePaths.add(fullPath);
        }
        
        // 批量删除照片信息
        photoMapper.deleteBatchIds(photoIds);
        
        // 批量删除文件
        fileUploadUtils.deleteFiles(filePaths);
    }

}
