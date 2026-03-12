package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 管理端会员详情展示对象。
 */
@Data
public class VipAdminMemberDetailVo implements Serializable {

    private Integer userId;

    private String username;

    private String nickname;

    private String email;

    private String avatar;

    private Integer userStatus;

    private Integer fansCount;

    private Integer followCount;

    private String introduction;

    private Date loginTime;

    private Date createTime;

    private Boolean isVip;

    private String vipStatus;

    private Date vipStartTime;

    private Date vipExpireTime;

    private Long remainingDays;

    private String lastOrderNo;

    private String latestPlanName;

    private List<VipAdminRecentOrderVo> recentOrders = new ArrayList<>();
}
