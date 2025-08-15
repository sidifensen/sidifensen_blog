package com.sidifensen.domain.constants;

/**
 * 图片审核常量类
 */
public class ImageAuditConstants {

    /**
     * 审核场景
     */
    public static class Scene {
        /** 色情检测 */
        public static final String PORN = "porn";
        /** 暴恐检测 */
        public static final String TERRORISM = "terrorism";
        /** 广告检测 */
        public static final String AD = "ad";
        /** 不良场景检测 */
        public static final String LIVE = "live";
        /** Logo识别 */
        public static final String LOGO = "logo";
    }

    /**
     * 审核标签
     */
    public static class Label {
        /** 正常图片 */
        public static final String NORMAL = "normal";
        /** 性感图片 */
        public static final String SEXY = "sexy";
        /** 色情图片 */
        public static final String PORN = "porn";
        /** 血腥 */
        public static final String BLOODY = "bloody";
        /** 爆炸烟光 */
        public static final String EXPLOSION = "explosion";
        /** 特殊装束 */
        public static final String OUTFIT = "outfit";
        /** 特殊标识 */
        public static final String LOGO = "logo";
        /** 武器 */
        public static final String WEAPON = "weapon";
        /** 敏感内容 */
        public static final String POLITICS = "politics";
        /** 打斗 */
        public static final String VIOLENCE = "violence";
        /** 聚众 */
        public static final String CROWD = "crowd";
        /** 游行 */
        public static final String PARADE = "parade";
        /** 车祸现场 */
        public static final String CARCRASH = "carcrash";
        /** 旗帜 */
        public static final String FLAG = "flag";
        /** 地标 */
        public static final String LOCATION = "location";
        /** 涉毒 */
        public static final String DRUG = "drug";
        /** 赌博 */
        public static final String GAMBLE = "gamble";
        /** 其他 */
        public static final String OTHERS = "others";
        /** 无意义图片 */
        public static final String MEANINGLESS = "meaningless";
        /** 画中画 */
        public static final String PIP = "PIP";
        /** 吸烟 */
        public static final String SMOKING = "smoking";
        /** 车内直播 */
        public static final String DRIVELIVE = "drivelive";
        /** 牛皮癣广告 */
        public static final String NPX = "npx";
        /** 二维码 */
        public static final String QRCODE = "qrcode";
        /** 小程序码 */
        public static final String PROGRAM_CODE = "programCode";
        /** 其他广告 */
        public static final String AD = "ad";
        /** 文字含敏感内容 */
        public static final String POLITICS_TEXT = "politics";
        /** 文字含涉黄内容 */
        public static final String PORN_TEXT = "porn";
        /** 文字含辱骂内容 */
        public static final String ABUSE = "abuse";
        /** 文字含涉恐内容 */
        public static final String TERRORISM_TEXT = "terrorism";
        /** 文字含违禁内容 */
        public static final String CONTRABAND = "contraband";
        /** 文字含其他垃圾内容 */
        public static final String SPAM = "spam";
        /** 带有管控logo的图片 */
        public static final String TV = "TV";
        /** 商标 */
        public static final String TRADEMARK = "trademark";
    }

    /**
     * 审核建议
     */
    public static class Suggestion {
        /** 通过 */
        public static final String PASS = "pass";
        /** 建议人工复审 */
        public static final String REVIEW = "review";
        /** 不通过 */
        public static final String BLOCK = "block";
    }
    
    /**
     * 审核场景描述
     */
    public static class SceneDescription {
        /** 色情检测 */
        public static final String PORN = "色情检测";
        /** 暴恐检测 */
        public static final String TERRORISM = "暴恐检测";
        /** 广告检测 */
        public static final String AD = "广告检测";
        /** 不良场景检测 */
        public static final String LIVE = "不良场景检测";
        /** Logo识别 */
        public static final String LOGO = "Logo识别";
        /** 未知检测 */
        public static final String UNKNOWN = "未知检测";
    }
    
    /**
     * 审核标签描述信息
     */
    public static class LabelDescription {
        /** 正常图片 */
        public static final String NORMAL = "正常图片";
        /** 性感图片 */
        public static final String SEXY = "性感图片";
        /** 色情图片 */
        public static final String PORN = "色情图片";
        /** 色情检测异常 */
        public static final String PORN_EXCEPTION = "色情检测异常";
        /** 血腥内容 */
        public static final String BLOODY = "血腥内容";
        /** 爆炸烟光 */
        public static final String EXPLOSION = "爆炸烟光";
        /** 特殊装束 */
        public static final String OUTFIT = "特殊装束";
        /** 特殊标识 */
        public static final String LOGO = "特殊标识";
        /** 武器 */
        public static final String WEAPON = "武器";
        /** 敏感内容 */
        public static final String POLITICS = "敏感内容";
        /** 打斗 */
        public static final String VIOLENCE = "打斗";
        /** 聚众 */
        public static final String CROWD = "聚众";
        /** 游行 */
        public static final String PARADE = "游行";
        /** 车祸现场 */
        public static final String CARCRASH = "车祸现场";
        /** 旗帜 */
        public static final String FLAG = "旗帜";
        /** 地标 */
        public static final String LOCATION = "地标";
        /** 涉毒内容 */
        public static final String DRUG = "涉毒内容";
        /** 赌博内容 */
        public static final String GAMBLE = "赌博内容";
        /** 其他敏感内容 */
        public static final String OTHERS = "其他敏感内容";
        /** 暴恐检测异常 */
        public static final String TERRORISM_EXCEPTION = "暴恐检测异常";
        /** 文字含敏感内容 */
        public static final String POLITICS_TEXT = "文字含敏感内容";
        /** 文字含涉黄内容 */
        public static final String PORN_TEXT = "文字含涉黄内容";
        /** 文字含辱骂内容 */
        public static final String ABUSE = "文字含辱骂内容";
        /** 文字含涉恐内容 */
        public static final String TERRORISM_TEXT = "文字含涉恐内容";
        /** 文字含违禁内容 */
        public static final String CONTRABAND = "文字含违禁内容";
        /** 文字含其他垃圾内容 */
        public static final String SPAM = "文字含其他垃圾内容";
        /** 牛皮癣广告 */
        public static final String NPX = "牛皮癣广告";
        /** 包含二维码 */
        public static final String QRCODE = "包含二维码";
        /** 包含小程序码 */
        public static final String PROGRAM_CODE = "包含小程序码";
        /** 其他广告 */
        public static final String AD = "其他广告";
        /** 广告检测异常 */
        public static final String AD_EXCEPTION = "广告检测异常";
        /** 无意义图片 */
        public static final String MEANINGLESS = "无意义图片";
        /** 画中画 */
        public static final String PIP = "画中画";
        /** 吸烟 */
        public static final String SMOKING = "吸烟";
        /** 车内直播 */
        public static final String DRIVELIVE = "车内直播";
        /** 不良场景检测异常 */
        public static final String LIVE_EXCEPTION = "不良场景检测异常";
        /** 带有管控logo的图片 */
        public static final String TV = "带有管控logo的图片";
        /** 商标 */
        public static final String TRADEMARK = "商标";
        /** 未知检测类型 */
        public static final String UNKNOWN = "未知检测类型";
    }
}