package com.sidifensen.domain.enums;

/**
 * 点赞类型枚举
 *
 * @author sidifensen
 * @since 2025-09-15
 */
public enum LikeTypeEnum {

    /**
     * 文章点赞
     */
    ARTICLE(0, "文章"),

    /**
     * 评论点赞
     */
    COMMENT(1, "评论");

    private final Integer code;
    private final String desc;

    LikeTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据代码获取枚举
     *
     * @param code 代码
     * @return 枚举
     */
    public static LikeTypeEnum getByCode(Integer code) {
        for (LikeTypeEnum typeEnum : values()) {
            if (typeEnum.getCode().equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }
}
