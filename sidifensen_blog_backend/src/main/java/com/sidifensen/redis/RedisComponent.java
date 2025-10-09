package com.sidifensen.redis;


import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.domain.constants.RedisConstants;
import com.sidifensen.domain.vo.AlbumVo;
import com.sidifensen.domain.vo.PhotoVo;
import com.sidifensen.utils.MyThreadFactory;
import com.sidifensen.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
        redisUtils.set(RedisConstants.CheckCode + checkCodeKey, checkCode, RedisConstants.CHECK_CODE_EXPIRE_TIME, TimeUnit.SECONDS);
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
        redisUtils.set(RedisConstants.EmailCheckCode + type + ":" + email, checkCode, RedisConstants.EMAIL_CHECK_CODE_EXPIRE_TIME, TimeUnit.SECONDS);
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
            redisUtils.hset(RedisConstants.Albums, albumId, albumVo, RedisConstants.ALBUM_EXPIRE_TIME, TimeUnit.SECONDS);
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
                redisUtils.hset(RedisConstants.AlbumPhotos, albumId, albumVo.getPhotos(), RedisConstants.ALBUM_EXPIRE_TIME, TimeUnit.SECONDS);
            }
        });
    }

    // 保存相册照片关联
    public void saveAlbumPhotos(Integer albumId, List<PhotoVo> photos) {
        redisUtils.hset(RedisConstants.AlbumPhotos, albumId, photos, RedisConstants.ALBUM_EXPIRE_TIME, TimeUnit.SECONDS);
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
            redisUtils.hset(RedisConstants.UserAlbums, userId, albumIds, RedisConstants.ALBUM_EXPIRE_TIME, TimeUnit.SECONDS);
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
        redisUtils.hmset(RedisConstants.Albums, map, RedisConstants.ALBUM_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    //保存所有用户相册
    public void saveAllUserAlbum(Map map) {
        redisUtils.hmset(RedisConstants.UserAlbums, map, RedisConstants.ALBUM_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    // 保存所有相册详情
    public void saveAllAlbumPhotos(Map map) {
        redisUtils.hmset(RedisConstants.AlbumPhotos, map, RedisConstants.ALBUM_EXPIRE_TIME, TimeUnit.SECONDS);
    }

    // ==================== 浏览历史相关方法 ====================
    
    /**
     * 检查用户是否已浏览过文章
     * @param articleId 文章ID
     * @param identifier 用户标识符
     * @return true-已浏览过，false-未浏览过
     */
    public boolean hasReadArticle(Integer articleId, String identifier) {
        String redisKey = RedisConstants.History + articleId;
        return redisUtils.sHasKey(redisKey, identifier);
    }
    
    /**
     * 记录用户浏览文章
     * @param articleId 文章ID
     * @param identifier 用户标识符
     */
    public void recordArticleRead(Integer articleId, String identifier) {
        String redisKey = RedisConstants.History + articleId;
        redisUtils.sSetAndTime(redisKey, RedisConstants.HISTORY_EXPIRE_TIME, identifier);
    }
    
    /**
     * 移除用户浏览记录
     * @param articleId 文章ID
     * @param identifier 用户标识符
     */
    public void removeArticleRead(Integer articleId, String identifier) {
        String redisKey = RedisConstants.History + articleId;
        redisUtils.setRemove(redisKey, identifier);
    }
    
    /**
     * 清除文章的所有浏览记录
     * @param articleId 文章ID
     */
    public void clearArticleReads(Integer articleId) {
        String redisKey = RedisConstants.History + articleId;
        redisUtils.del(redisKey);
    }
    
    /**
     * 批量清除多篇文章的所有浏览记录
     * @param articleIds 文章ID列表
     */
    public void batchClearArticleReads(List<Integer> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return;
        }
        
        // 构建所有需要删除的Redis key
        String[] redisKeys = articleIds.stream()
                .map(articleId -> RedisConstants.History + articleId)
                .toArray(String[]::new);
        
        // 批量删除
        redisUtils.del(redisKeys);
    }

    // ==================== 黑名单日志限流相关方法 ====================
    
    /**
     * 黑名单日志限流：检查是否应该记录黑名单警告日志
     * 用于防止黑名单用户频繁访问导致日志刷屏
     *
     * @param identifier 用户标识（如：user:2 或 ip:192.168.1.1）
     * @param duration   限流时长
     * @param timeUnit   时间单位
     * @return true-应该记录日志（首次或已过期），false-不应该记录日志（限流中）
     */
    public boolean shouldLogBlacklist(String identifier, long duration, TimeUnit timeUnit) {
        String logKey = RedisConstants.BlacklistLog + identifier;
        if (!redisUtils.hasKey(logKey)) {
            // 首次记录或已过期，设置限流标记
            redisUtils.set(logKey, "1", duration, timeUnit);
            return true;
        }
        // 限流中，不应记录日志
        return false;
    }

    // ==================== 热门文章统计相关方法 ====================

    /**
     * 增加文章热度（访问量+1）
     * 使用Redis ZSet存储，score为访问量，member为文章ID
     *
     * @param articleId 文章ID
     */
    public void incrementArticleHotScore(Integer articleId) {
        executorService.execute(() -> {
            redisUtils.zIncrementScore(RedisConstants.HotArticles7Days, articleId.toString(), 1.0);
            // 设置过期时间为7天
            redisUtils.expire(RedisConstants.HotArticles7Days, RedisConstants.HOT_ARTICLES_EXPIRE_TIME, TimeUnit.SECONDS);
        });
    }

    /**
     * 批量设置文章热度（原子操作，避免并发读取问题）
     * 用于定时任务从数据库同步7天数据
     * 
     * 优化策略：
     * 1. 使用临时key进行数据同步，避免清空主key导致的并发读取问题
     * 2. 同步完成后，使用RENAME原子操作切换key
     * 3. 这样用户始终能读取到完整数据
     *
     * @param articleScores Map<文章ID, 访问量>
     */
    public void batchSetArticleHotScore(Map<Integer, Double> articleScores) {
        if (articleScores == null || articleScores.isEmpty()) {
            return;
        }
        
        executorService.execute(() -> {
            try {
                // 使用临时key进行数据同步
                String tempKey = RedisConstants.HotArticles7Days + ":temp";
                
                // 先删除可能存在的临时key
                redisUtils.del(tempKey);
                
                // 批量添加新数据到临时key
                for (Map.Entry<Integer, Double> entry : articleScores.entrySet()) {
                    redisUtils.zAdd(tempKey, entry.getKey().toString(), entry.getValue());
                }
                
                // 使用RENAME原子操作切换key（旧key会被自动删除）
                // 这样用户在整个过程中都能读取到完整数据
                redisUtils.rename(tempKey, RedisConstants.HotArticles7Days);
                
                // 设置过期时间为7天
                redisUtils.expire(RedisConstants.HotArticles7Days, RedisConstants.HOT_ARTICLES_EXPIRE_TIME, TimeUnit.SECONDS);
                
            } catch (Exception e) {
                // 如果出现异常，确保临时key被清理
                String tempKey = RedisConstants.HotArticles7Days + ":temp";
                redisUtils.del(tempKey);
                throw e;
            }
        });
    }

    /**
     * 获取热门文章列表（按访问量倒序）
     *
     * @param topN 获取前N条数据
     * @return 文章ID列表（按热度从高到低排序）
     */
    public List<Integer> getHotArticles(int topN) {
        // 获取分数最高的topN个文章ID（倒序）
        Set<Object> result = redisUtils.zReverseRange(RedisConstants.HotArticles7Days, 0, topN - 1);
        if (result == null || result.isEmpty()) {
            return new ArrayList<>();
        }
        return result.stream()
                .map(obj -> Integer.valueOf(obj.toString()))
                .collect(Collectors.toList());
    }

    /**
     * 获取文章热度分数
     *
     * @param articleId 文章ID
     * @return 热度分数（访问量）
     */
    public Double getArticleHotScore(Integer articleId) {
        return redisUtils.zScore(RedisConstants.HotArticles7Days, articleId.toString());
    }

    /**
     * 批量获取文章热度分数
     * 使用Redis Pipeline优化性能：一次网络请求获取所有分数
     *
     * @param articleIds 文章ID列表
     * @return Map<文章ID, 热度分数>
     */
    public Map<Integer, Double> batchGetArticleHotScore(List<Integer> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return new HashMap<>();
        }

        // 将Integer ID转换为String
        List<String> articleIdStrings = articleIds.stream()
                .map(String::valueOf)
                .collect(Collectors.toList());

        // 使用Pipeline批量获取分数（一次网络往返）
        List<Object> scores = redisUtils.zScoreBatch(RedisConstants.HotArticles7Days, articleIdStrings);

        // 组装结果Map（需要防止空指针和索引越界）
        Map<Integer, Double> resultMap = new HashMap<>();
        if (scores == null || scores.isEmpty()) {
            // Redis中没有数据，返回空Map
            return resultMap;
        }

        // 遍历结果，注意长度可能不一致
        int size = Math.min(articleIds.size(), scores.size());
        for (int i = 0; i < size; i++) {
            Object score = scores.get(i);
            if (score instanceof Double) {
                resultMap.put(articleIds.get(i), (Double) score);
            }
        }
        return resultMap;
    }


}