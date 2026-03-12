package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 管理端会员列表行数据。
 */
@Data
public class VipAdminMemberPageVo implements Serializable {

    private Integer userId;

    private String username;

    private String nickname;

    private String email;

    private String avatar;

    private Integer userStatus;

    private Boolean isVip;

    private String vipStatus;

    private Date vipStartTime;

    private Date vipExpireTime;

    private Long remainingDays;

    private String lastOrderNo;

    private String latestPlanName;

    private Date loginTime;

    private Date createTime;
}
