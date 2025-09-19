package com.sidifensen.domain.constants;

/**
 * @author sidifensen
 * @since 2025-06-30
 */
public class RedisConstants {

    public static final String RedisKeyPrefix = "sidifensen_blog:";

    public static final String CheckCode = RedisKeyPrefix + "CheckCode:";

    public static final String EmailCheckCode = RedisKeyPrefix + "EmailCheckCode:";

    public static final String Albums = RedisKeyPrefix + "Albums:";

    public static final String UserAlbums = RedisKeyPrefix + "UserAlbums:";

    public static final String AlbumPhotos = RedisKeyPrefix + "AlbumPhotos:";

    /**
     * 浏览历史缓存键
     * 格式：sidifensen_blog:History:文章ID
     * 存储结构：Set，存储已浏览过该文章的用户ID或IP地址
     */
    public static final String History = RedisKeyPrefix + "History:";
    
    /**
     * 浏览记录Redis缓存过期时间（24小时，单位：秒）
     */
    public static final long HISTORY_EXPIRE_TIME = 24 * 60 * 60;
    
    /**
     * 登录验证码过期时间（5分钟，单位：秒）
     */
    public static final long CHECK_CODE_EXPIRE_TIME = 5 * 60;
    
    /**
     * 邮箱验证码过期时间（5分钟，单位：秒）
     */
    public static final long EMAIL_CHECK_CODE_EXPIRE_TIME = 5 * 60;
    
    /**
     * 相册相关数据过期时间（7天，单位：秒）
     */
    public static final long ALBUM_EXPIRE_TIME = 7 * 24 * 60 * 60;
}
