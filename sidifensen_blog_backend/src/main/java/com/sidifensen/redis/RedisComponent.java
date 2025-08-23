package com.sidifensen.redis;


import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.domain.vo.PhotoVo;
import com.sidifensen.utils.MyThreadFactory;
import com.sidifensen.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class RedisComponent {

    @Resource
    private RedisUtils redisUtils;

    ExecutorService executorService = new ThreadPoolExecutor(
            2, 4, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new MyThreadFactory("RedisComponent")
    );

    // 保存登录验证码
    public String saveCheckCode(String checkCode) {
        String checkCodeKey = UUID.randomUUID().toString().replace("-", "");
        redisUtils.set(RedisConstants.CheckCode + checkCodeKey, checkCode, 5, TimeUnit.MINUTES);
        return checkCodeKey;
    }

    // 获取登录验证码
    public String getCheckCode(String checkCodeKey) {
        return (String) redisUtils.get(RedisConstants.CheckCode + checkCodeKey);
    }

    // 清除登录验证码
    public void cleanCheckCode(String checkCodeKey) {
        redisUtils.del(RedisConstants.CheckCode + checkCodeKey);
    }

    // 保存邮箱验证码
    public void saveEmailCheckCode(String email, String type, String checkCode) {
        redisUtils.set(RedisConstants.EmailCheckCode + type + ":" + email, checkCode, 5, TimeUnit.MINUTES);
    }

    // 获取邮箱验证码
    public String getEmailCheckCode(String email, String type) {
        return (String) redisUtils.get(RedisConstants.EmailCheckCode + type + ":" + email);
    }

    // 清除邮箱验证码
    public void cleanEmailCheckCode(String email, String type) {
        redisUtils.del(RedisConstants.EmailCheckCode + type + ":" + email);
    }

    // 保存相册
    public void saveAlbum(Integer albumId, AlbumVo albumVo) {
        executorService.execute(() -> {
            redisUtils.hset(RedisConstants.Albums, albumId, albumVo, 7, TimeUnit.DAYS);
        });
    }

    // 获取相册
    public AlbumVo getAlbum(List<Integer> albumIds) {
        return (AlbumVo) redisUtils.hmget(RedisConstants.Albums, albumIds);
    }

    // 删除相册
    public void delAlbum(List<Integer> albumIds) {
        redisUtils.hdel(RedisConstants.Albums, albumIds);
    }


    // 保存相册详情
    public void saveAlbumDetail(Integer albumId, AlbumVo albumVo) {
        executorService.execute(() -> {
            // 分离存储相册基本信息和照片列表
            // 存储相册基本信息（不包含照片列表）
            AlbumVo albumInfo = new AlbumVo();
            albumInfo.setId(albumVo.getId());
            albumInfo.setUserId(albumVo.getUserId());
            albumInfo.setName(albumVo.getName());
            albumInfo.setCoverUrl(albumVo.getCoverUrl());
            albumInfo.setShowStatus(albumVo.getShowStatus());
            albumInfo.setCreateTime(albumVo.getCreateTime());
            albumInfo.setUpdateTime(albumVo.getUpdateTime());
            albumInfo.setUserName(albumVo.getUserName());

            this.saveAlbum(albumId, albumInfo);

            // 单独存储照片列表
            if (albumVo.getPhotos() != null && !albumVo.getPhotos().isEmpty()) {
                redisUtils.hset(RedisConstants.AlbumPhotos, albumId, albumVo.getPhotos(), 7, TimeUnit.DAYS);
            }
        });
    }

    // 获取相册详情
    public AlbumVo getAlbumDetail(List<Integer> albumIds) {
        // 获取相册基本信息
        AlbumVo albumInfo = this.getAlbum(albumIds);
        if (albumInfo == null) {
            return null;
        }
        // 获取照片列表
        List<PhotoVo> photos = (List<PhotoVo>) redisUtils.hmultiGet(RedisConstants.AlbumPhotos, albumIds);
        //拼接信息
        albumInfo.setPhotos(photos != null ? photos : new ArrayList<>());
        return albumInfo;
    }

    // 删除相册详情
    public void delAlbumDetail(Integer albumId) {
        redisUtils.hdel(RedisConstants.AlbumPhotos, albumId);
    }


    // 保存用户相册
    public void saveUserAlbum(Integer userId, List<Integer> albumIds) {
        executorService.execute(() -> {
            redisUtils.hset(RedisConstants.UserAlbums, userId, albumIds, 7, TimeUnit.DAYS);
        });
    }

    // 获取用户相册
    public List<AlbumVo> getUserAlbum(List<Integer> userIds) {
        List<Integer> userAlbumIds = (List<Integer>) redisUtils.hmultiGet(RedisConstants.UserAlbums, userIds);
        if (ObjectUtil.isEmpty(userAlbumIds)) {
            return new ArrayList<>();
        }
        return (List<AlbumVo>) this.getAlbum(userAlbumIds);
    }

    // 删除用户相册
    public void delUserAlbum(List<Integer> userIds) {
        redisUtils.hdel(RedisConstants.UserAlbums, userIds);
    }

    // 获取所有相册
    public List<AlbumVo> getAllAlbum() {
        return (List<AlbumVo>) redisUtils.hmgetValues(RedisConstants.Albums);
    }

    // 保存所有相册
    public void saveAllAlbum(Map map) {
        redisUtils.hmset(RedisConstants.Albums, map, 7, TimeUnit.DAYS);
    }

    //保存所有用户相册
    public void saveAllUserAlbum(Map map) {
        redisUtils.hmset(RedisConstants.UserAlbums, map, 7, TimeUnit.DAYS);
    }

    // 保存所有相册详情
    public void saveAllAlbumPhotos(Map map) {
        redisUtils.hmset(RedisConstants.AlbumPhotos, map, 7, TimeUnit.DAYS);
    }


}