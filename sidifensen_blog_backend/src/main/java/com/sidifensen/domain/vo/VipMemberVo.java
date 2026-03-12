package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 当前登录用户的 VIP 状态展示对象。
 */
@Data
public class VipMemberVo implements Serializable {

    private Boolean isVip;

    private String vipStatus;

    private Date vipExpireTime;

    private Integer aiDailyQuota;

    private Integer aiRemainingQuota;
}
