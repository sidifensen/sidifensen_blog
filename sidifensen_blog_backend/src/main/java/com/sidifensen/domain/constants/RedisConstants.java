package com.sidifensen.domain.constants;

/**
 * @author sidifensen
 * @since 2025-06-30
 */
public class RedisConstants {

    public static final String RedisKeyPrefix = "sidifensen_blog:";

    public static final String CheckCode = RedisKeyPrefix + "CheckCode:";

    public static final String EmailCheckCode = RedisKeyPrefix + "EmailCheckCode:";

    public static final String AlbumDetail = RedisKeyPrefix + "AlbumDetail:";

    public static final String UserAlbum = RedisKeyPrefix + "UserAlbum:";
}
