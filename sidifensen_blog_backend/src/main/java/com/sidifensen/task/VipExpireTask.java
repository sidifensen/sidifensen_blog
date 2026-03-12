package com.sidifensen.task;

import com.sidifensen.service.VipMemberService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 每日同步过期会员状态，清理对应的 VIP 角色投影。
 */
@Component
@Slf4j
public class VipExpireTask {

    @Resource
    private VipMemberService vipMemberService;

    /**
     * 会员过期后，角色投影需要及时回收，避免权限残留。
     */
    @Scheduled(cron = "0 10 3 * * ?")
    public void syncExpiredVipRoles() {
        vipMemberService.syncExpiredVipRoles();
    }
}
