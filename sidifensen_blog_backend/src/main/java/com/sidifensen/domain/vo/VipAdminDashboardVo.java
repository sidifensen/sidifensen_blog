package com.sidifensen.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理端会员经营总览数据。
 */
@Data
public class VipAdminDashboardVo implements Serializable {

    private Long activeMemberCount;

    private Long expiredMemberCount;

    private Long expiringSoonCount;

    private Long paidOrderCount30d;

    private Integer paidAmountFen30d;

    private String paidAmountYuan30d;
}
