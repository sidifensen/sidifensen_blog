package com.sidifensen.redis;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sidifensen.domain.entity.AlbumPhoto;
import com.sidifensen.domain.entity.Photo;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.domain.vo.PhotoVo;
import com.sidifensen.mapper.AlbumPhotoMapper;
import com.sidifensen.mapper.PhotoMapper;
import com.sidifensen.service.AlbumService;
import com.sidifensen.utils.MyThreadFactory;
import jakarta.annotation.Resource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author sidifensen
 * @since 2025-08-23
 */
@Service
public class RedisService implements ApplicationRunner {

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private AlbumService albumService;

    @Resource
    private AlbumPhotoMapper albumPhotoMapper;

    @Resource
    private PhotoMapper photoMapper;

    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("RedisService")
    );

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initAlbums();
        initUserAlbums();
        initAlbumPhotos();
    }

    // 初始化全部相册数据
    public void initAlbums() {
        List<AlbumVo> albumVos = albumService.listAllAlbum();
        HashMap<Integer, Object> albumMap = new HashMap<>();
        for (AlbumVo albumVo : albumVos) {
            albumMap.put(albumVo.getId(), albumVo);
        }
        redisComponent.saveAllAlbum(albumMap);
    }

    // 初始化所有用户相册数据
    public void initUserAlbums() {
        List<AlbumVo> albumVos = albumService.listAllAlbum();
        // 将列表转换为Map，key为userId，value为该用户拥有的相册ID列表
        Map<Integer, List<Integer>> map = albumVos.stream()
                .collect(Collectors.groupingBy(
                        AlbumVo::getUserId, Collectors.mapping(AlbumVo::getId, Collectors.toList())
                ));
        redisComponent.saveAllUserAlbum(map);
    }

    // 初始化所有相册详情
    public void initAlbumPhotos() {
        List<AlbumPhoto> albumPhotos = albumPhotoMapper.selectList(null);

        // 按albumId分组，创建Map<Integer, List<Integer>>，key为albumId，value为photoId列表
        Map<Integer, List<Integer>> albumPhotoIdsMap = albumPhotos.stream()
                .collect(Collectors.groupingBy(
                        AlbumPhoto::getAlbumId,
                        Collectors.mapping(AlbumPhoto::getPhotoId, Collectors.toList())
                ));

        // 创建Map<Integer, List<Photo>>，key为albumId，value为Photo列表
        Map<Integer, List<Photo>> albumPhotoMap = new HashMap<>();

        // 如果有相册与照片的关联关系
        if (!albumPhotoIdsMap.isEmpty()) {
            // 获取所有的photoId
            List<Integer> photoIds = albumPhotoIdsMap.values().stream()
                    .flatMap(List::stream)
                    .distinct()
                    .collect(Collectors.toList());

            // 通过photoIds批量查询所有相关照片
            List<Photo> photos = photoMapper.selectBatchIds(photoIds);

            // 创建photoId到Photo对象的映射
            Map<Integer, Photo> photoMap = photos.stream()
                    .collect(Collectors.toMap(Photo::getId, photo -> photo));

            // 填充albumPhotoMap
            for (Map.Entry<Integer, List<Integer>> entry : albumPhotoIdsMap.entrySet()) {
                Integer albumId = entry.getKey();
                List<Integer> photoIdList = entry.getValue();

                // 根据photoId列表获取对应的Photo对象列表
                List<Photo> photoList = photoIdList.stream()
                        .map(photoMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                // 将albumId和对应的Photo列表放入结果Map
                albumPhotoMap.put(albumId, photoList);
            }
        }
        redisComponent.saveAllAlbumPhotos(albumPhotoMap);
    }

    // 更新相册照片关联
    public void updateAlbumPhotos(Integer albumId) {
        executorService.execute(() -> {
            List<AlbumPhoto> albumPhotos = albumPhotoMapper.selectList(new LambdaQueryWrapper<AlbumPhoto>().eq(AlbumPhoto::getAlbumId, albumId));
            List<Integer> photoIds = albumPhotos.stream().map(AlbumPhoto::getPhotoId).collect(Collectors.toList());
            List<Photo> photos = photoMapper.selectBatchIds(photoIds);
            redisComponent.saveAlbumPhotos(albumId, BeanUtil.copyToList(photos, PhotoVo.class));
        });
    }

    // 批量更新相册照片关联
    public void multiUpdateAlbumPhotos(List<Integer> albumIds) {
        for (Integer albumId : albumIds) {
            updateAlbumPhotos(albumId);
        }
    }

}
