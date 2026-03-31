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
     * 每天凌晨3点10分执行，会员过期后及时回收角色权限。
     */
    @Scheduled(cron = "0 10 3 * * ?")
    public void syncExpiredVipRoles() {
        long startTime = System.currentTimeMillis();
        try {
            vipMemberService.syncExpiredVipRoles();
        } catch (Exception e) {
            log.error("VIP角色同步任务执行失败，耗时={}ms", System.currentTimeMillis() - startTime, e);
        }
    }
}
