package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysUserRole;
import com.sidifensen.domain.entity.VipMember;
import com.sidifensen.domain.enums.VipMemberStatusEnum;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.SysRoleMapper;
import com.sidifensen.mapper.SysUserRoleMapper;
import com.sidifensen.mapper.VipMemberMapper;
import com.sidifensen.service.VipMemberService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * VIP 会员实现，以 vip_member 作为会员数据来源，并同步 vip 角色投影。
 */
@Service
@Slf4j
public class VipMemberServiceImpl extends ServiceImpl<VipMemberMapper, VipMember> implements VipMemberService {

    @Resource
    private VipMemberMapper vipMemberMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    /**
     * 有效期和状态同时满足时，才视为当前仍然是有效会员。
     */
    @Override
    public boolean hasVipAccess(Integer userId) {
        if (userId == null || userId <= 0) {
            return false;
        }
        // 权益判断统一以会员数据为准，不依赖角色投影反推。
        VipMember vipMember = getVipMemberByUserId(userId);
        if (vipMember == null || vipMember.getExpireTime() == null) {
            return false;
        }
        return VipMemberStatusEnum.ACTIVE.getCode().equals(vipMember.getStatus())
                && vipMember.getExpireTime().after(new Date());
    }

    /**
     * 每个用户只保留一条会员记录，这里按 userId 直接读取。
     */
    @Override
    public VipMember getVipMemberByUserId(Integer userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        // 当前模型约定每个用户只保留一条会员记录，因此这里直接按 userId 读取。
        return vipMemberMapper.selectOne(new LambdaQueryWrapper<VipMember>()
                .eq(VipMember::getUserId, userId)
                .last("limit 1"));
    }

    /**
     * 首购从当前时间起算，续费则在现有有效期后顺延。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activateVip(Integer userId, String orderNo, Integer days) {
        Date now = new Date();
        VipMember vipMember = getVipMemberByUserId(userId);
        Date startTime = now;
        Date expireTime = Date.from(now.toInstant().plus(days, ChronoUnit.DAYS));

        if (vipMember == null) {
            // 首次开通直接写入新的会员记录，并保留触发本次开通的订单号。
            vipMember = new VipMember();
            vipMember.setUserId(userId);
            vipMember.setStartTime(startTime);
            vipMember.setExpireTime(expireTime);
            vipMember.setStatus(VipMemberStatusEnum.ACTIVE.getCode());
            vipMember.setLastOrderNo(orderNo);
            vipMemberMapper.insert(vipMember);
        } else {
            Date currentExpireTime = vipMember.getExpireTime();
            // 当前仍在有效期内时，从旧到期时间继续顺延；过期后则从现在重新开始。
            Instant baseInstant = currentExpireTime != null && currentExpireTime.after(now)
                    ? currentExpireTime.toInstant()
                    : now.toInstant();
            Date nextExpireTime = Date.from(baseInstant.plus(days, ChronoUnit.DAYS));
            // 已有记录时只覆盖会员数据的关键字段，保证续期和回溯都可追踪。
            vipMember.setStartTime(startTime);
            vipMember.setExpireTime(nextExpireTime);
            vipMember.setStatus(VipMemberStatusEnum.ACTIVE.getCode());
            vipMember.setLastOrderNo(orderNo);
            vipMemberMapper.updateById(vipMember);
        }

        // 会员生效后同步补齐 vip 角色投影，供权限系统直接使用。
        ensureVipRoleAttached(userId);
    }

    /**
     * 管理端手动开通时不沿用旧有效期，直接以当前时间重新起算。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void overwriteVip(Integer userId, Integer days) {
        Date now = new Date();
        Date expireTime = Date.from(now.toInstant().plus(days, ChronoUnit.DAYS));
        VipMember vipMember = getVipMemberByUserId(userId);
        if (vipMember == null) {
            // 管理端首次手动开通时，直接创建会员数据记录。
            vipMember = new VipMember();
            vipMember.setUserId(userId);
            vipMember.setStatus(VipMemberStatusEnum.ACTIVE.getCode());
            vipMember.setStartTime(now);
            vipMember.setExpireTime(expireTime);
            vipMemberMapper.insert(vipMember);
        } else {
            // 已有会员记录时忽略旧有效期，从当前时间重新覆盖起止时间。
            vipMember.setStatus(VipMemberStatusEnum.ACTIVE.getCode());
            vipMember.setStartTime(now);
            vipMember.setExpireTime(expireTime);
            vipMemberMapper.updateById(vipMember);
        }
        // 手动开通同样需要把权限投影同步为 vip。
        ensureVipRoleAttached(userId);
    }

    /**
     * 立即失效只修改会员数据，并同步清理 vip 角色投影。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void expireVipImmediately(Integer userId) {
        VipMember vipMember = getVipMemberByUserId(userId);
        Date now = new Date();
        if (vipMember != null) {
            // 立即失效只把会员数据状态和到期时间拉到当前时刻，不删除历史记录。
            vipMember.setStatus(VipMemberStatusEnum.EXPIRED.getCode());
            vipMember.setExpireTime(now);
            if (vipMember.getStartTime() == null) {
                vipMember.setStartTime(now);
            }
            vipMemberMapper.updateById(vipMember);
        }
        // 会员数据失效后同步回收 vip 角色，避免权限滞留。
        removeVipRole(userId);
    }

    /**
     * 每日定时将已过期会员状态改为失效，并清理角色映射。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncExpiredVipRoles() {
        Date now = new Date();
        // 仅挑出仍标记为 ACTIVE、但实际上已经过期的会员，减少无效更新。
        List<VipMember> expiredMembers = vipMemberMapper.selectList(new LambdaQueryWrapper<VipMember>()
                .eq(VipMember::getStatus, VipMemberStatusEnum.ACTIVE.getCode())
                .lt(VipMember::getExpireTime, now));
        if (expiredMembers.isEmpty()) {
            return;
        }

        List<Integer> userIds = expiredMembers.stream().map(VipMember::getUserId).toList();
        // 先批量修正会员数据状态，再批量回收 vip 角色投影。
        this.update(new LambdaUpdateWrapper<VipMember>()
                .in(VipMember::getUserId, userIds)
                .set(VipMember::getStatus, VipMemberStatusEnum.EXPIRED.getCode()));

        SysRole vipRole = getVipRole();
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getRoleId, vipRole.getId())
                .in(SysUserRole::getUserId, userIds));
        
    }

    /**
     * vip 角色只是权限投影，支付成功后需要确保角色关系存在。
     */
    private void ensureVipRoleAttached(Integer userId) {
        SysRole vipRole = getVipRole();
        SysUserRole existRole = sysUserRoleMapper.selectOne(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, vipRole.getId())
                .last("limit 1"));
        if (existRole != null) {
            return;
        }
        // 只有缺少投影关系时才插入，避免重复角色映射。
        sysUserRoleMapper.insert(new SysUserRole()
                .setUserId(userId)
                .setRoleId(vipRole.getId()));
    }

    /**
     * 会员失效后移除投影角色，避免用户继续保留 VIP 权限。
     */
    private void removeVipRole(Integer userId) {
        SysRole vipRole = getVipRole();
        sysUserRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, userId)
                .eq(SysUserRole::getRoleId, vipRole.getId()));
    }

    /**
     * 统一读取系统内的 vip 角色定义。
     */
    private SysRole getVipRole() {
        SysRole vipRole = sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                .eq(SysRole::getRole, "vip")
                .last("limit 1"));
        if (vipRole == null) {
            // 会员能力依赖 vip 角色投影，没有角色定义时直接中断流程并暴露配置问题。
            throw new BlogException(BlogConstants.NotFoundRole);
        }
        return vipRole;
    }
}
