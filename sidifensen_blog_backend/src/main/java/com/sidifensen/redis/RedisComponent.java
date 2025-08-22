package com.sidifensen.redis;


import cn.hutool.json.JSONArray;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.utils.MyThreadFactory;
import com.sidifensen.utils.RedisUtils;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
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
    public String getEmailCheckCode(String email,String type) {
        return (String) redisUtils.get(RedisConstants.EmailCheckCode + type + ":" + email);
    }

    // 清除邮箱验证码
    public void cleanEmailCheckCode(String email,String type) {
        redisUtils.del(RedisConstants.EmailCheckCode + type + ":" + email);
    }

    // 保存相册详情
    public void saveAlbumDetail(Integer albumId, AlbumVo albumVo) {
        executorService.execute(() -> {
            redisUtils.set(RedisConstants.AlbumDetail + albumId, albumVo, 7, TimeUnit.DAYS);
        });
    }

    // 获取相册详情
    public AlbumVo getAlbumDetail(Integer albumId) {
        return (AlbumVo) redisUtils.get(RedisConstants.AlbumDetail + albumId);
    }

    // 删除相册详情
    public void delAlbumDetail(Integer albumId) {
        redisUtils.del(RedisConstants.AlbumDetail + albumId);
    }

    // 保存用户相册
    public void saveUserAlbum(Integer userId, List<AlbumVo> albumVos) {
        executorService.execute(() -> {
            redisUtils.set(RedisConstants.UserAlbum + userId, albumVos, 7, TimeUnit.DAYS);
        });
    }

    // 获取用户相册
    public List<AlbumVo> getUserAlbum(Integer userId) {
        Object obj = redisUtils.get(RedisConstants.UserAlbum + userId);
        if (obj == null) {
            return null;
        }
        // 如果是JSONArray类型，使用JSONUtil转换为List<AlbumVo>
        if (obj instanceof JSONArray) {
            String json = obj.toString();
            return JSONUtil.toList(JSONUtil.parseArray(json), AlbumVo.class);
        }
        // 如果已经是List<AlbumVo>类型，直接转换
        return (List<AlbumVo>) obj;
    }

    // 删除用户相册
    public void delUserAlbum(Integer userId) {
        redisUtils.del(RedisConstants.UserAlbum + userId);
    }

}