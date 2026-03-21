package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.dto.VipAdminMemberAdjustDto;
import com.sidifensen.domain.dto.VipAdminMemberPageDto;
import com.sidifensen.domain.dto.VipAdminOrderPageDto;
import com.sidifensen.domain.dto.VipAdminPlanUpdateDto;
import com.sidifensen.domain.entity.PayOrder;
import com.sidifensen.domain.entity.SysRole;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.entity.VipMember;
import com.sidifensen.domain.entity.VipPlan;
import com.sidifensen.domain.enums.PayOrderStatusEnum;
import com.sidifensen.domain.enums.VipAdminAdjustActionEnum;
import com.sidifensen.domain.enums.VipMemberStatusEnum;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.VipAdminDashboardVo;
import com.sidifensen.domain.vo.VipAdminMemberDetailVo;
import com.sidifensen.domain.vo.VipAdminMemberPageVo;
import com.sidifensen.domain.vo.VipAdminOrderPageVo;
import com.sidifensen.domain.vo.VipAdminPlanVo;
import com.sidifensen.domain.vo.VipAdminRecentOrderVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.PayOrderMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.mapper.VipMemberMapper;
import com.sidifensen.mapper.VipPlanMapper;
import com.sidifensen.service.VipAdminService;
import com.sidifensen.service.VipMemberService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 管理端 VIP 聚合服务，负责会员、订单、套餐三类后台读写场景。
 */
@Service
@Slf4j
public class VipAdminServiceImpl implements VipAdminService {

    @Resource
    private VipMemberMapper vipMemberMapper;

    @Resource
    private VipPlanMapper vipPlanMapper;

    @Resource
    private PayOrderMapper payOrderMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private VipMemberService vipMemberService;

    /**
     * 统计口径全部以当前时间为准，避免定时任务未跑时出现展示偏差。
     */
    @Override
    public VipAdminDashboardVo getDashboard() {
        Date now = new Date();
        // 仪表盘里的临期和近30天口径全部在当前请求时动态计算。
        Date sevenDaysLater = new Date(now.toInstant().plus(7, ChronoUnit.DAYS).toEpochMilli());
        Date thirtyDaysAgo = new Date(now.toInstant().minus(30, ChronoUnit.DAYS).toEpochMilli());

        // 会员数统计统一直接读会员数据表，并按当前时间重新归一化状态。
        Long activeMemberCount = vipMemberMapper.selectCount(new LambdaQueryWrapper<VipMember>()
                .eq(VipMember::getStatus, VipMemberStatusEnum.ACTIVE.getCode())
                .gt(VipMember::getExpireTime, now));

        Long expiredMemberCount = vipMemberMapper.selectCount(new LambdaQueryWrapper<VipMember>()
                .and(wrapper -> wrapper.eq(VipMember::getStatus, VipMemberStatusEnum.EXPIRED.getCode())
                        .or()
                        .le(VipMember::getExpireTime, now)));

        Long expiringSoonCount = vipMemberMapper.selectCount(new LambdaQueryWrapper<VipMember>()
                .eq(VipMember::getStatus, VipMemberStatusEnum.ACTIVE.getCode())
                .gt(VipMember::getExpireTime, now)
                .le(VipMember::getExpireTime, sevenDaysLater));

        List<PayOrder> recentPaidOrders = payOrderMapper.selectList(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getStatus, PayOrderStatusEnum.PAID.getCode())
                .ge(PayOrder::getPaidTime, thirtyDaysAgo)
                .orderByDesc(PayOrder::getPaidTime));

        // 收入口径以真实支付订单累计金额为准，不把人工调整会员混进来。
        int paidAmountFen30d = recentPaidOrders.stream()
                .map(PayOrder::getAmountFen)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .sum();

        VipAdminDashboardVo vipAdminDashboardVo = new VipAdminDashboardVo();
        vipAdminDashboardVo.setActiveMemberCount(activeMemberCount);
        vipAdminDashboardVo.setExpiredMemberCount(expiredMemberCount);
        vipAdminDashboardVo.setExpiringSoonCount(expiringSoonCount);
        vipAdminDashboardVo.setPaidOrderCount30d((long) recentPaidOrders.size());
        vipAdminDashboardVo.setPaidAmountFen30d(paidAmountFen30d);
        vipAdminDashboardVo.setPaidAmountYuan30d(fenToYuan(paidAmountFen30d));
        return vipAdminDashboardVo;
    }

    /**
     * 会员页以 sys_user 为主表分页，再批量补齐会员数据和订单快照信息。
     */
    @Override
    public PageVo<List<VipAdminMemberPageVo>> pageMembers(VipAdminMemberPageDto vipAdminMemberPageDto) {
        // 先把会员状态、到期时间等条件翻译成用户范围，保证后续分页仍以用户表为主。
        UserScope userScope = resolveMemberUserScope(vipAdminMemberPageDto);
        if (userScope.type == UserScopeType.EMPTY) {
            return new PageVo<>(Collections.emptyList(), 0L);
        }

        Page<SysUser> page = new Page<>(vipAdminMemberPageDto.getPageNum(), vipAdminMemberPageDto.getPageSize());
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        appendUserKeywordCondition(queryWrapper, vipAdminMemberPageDto.getKeyword());
        applyUserScope(queryWrapper, userScope);
        queryWrapper.orderByAsc(SysUser::getId);

        Page<SysUser> userPage = sysUserMapper.selectPage(page, queryWrapper);
        List<SysUser> sysUsers = userPage.getRecords();
        if (sysUsers.isEmpty()) {
            return new PageVo<>(Collections.emptyList(), userPage.getTotal());
        }

        // 当前页用户再批量补齐会员记录和最后订单快照，避免 N+1 查询。
        Map<Integer, VipMember> vipMemberMap = queryVipMemberMap(sysUsers.stream().map(SysUser::getId).toList());
        Map<String, PayOrder> payOrderMap = queryLastOrderMap(vipMemberMap.values().stream()
                .map(VipMember::getLastOrderNo)
                .filter(orderNo -> orderNo != null && !orderNo.isBlank())
                .toList());

        List<VipAdminMemberPageVo> vipAdminMemberPageVos = sysUsers.stream()
                .map(sysUser -> toVipAdminMemberPageVo(sysUser, vipMemberMap.get(sysUser.getId()), payOrderMap))
                .toList();
        maskMemberEmailsForViewer(vipAdminMemberPageVos);
        return new PageVo<>(vipAdminMemberPageVos, userPage.getTotal());
    }

    /**
     * 详情页需要同时展示基础信息、会员状态和最近订单摘要。
     */
    @Override
    public VipAdminMemberDetailVo getMemberDetail(Integer userId) {
        // 详情入口先确认用户真实存在，避免后续多个数据表都做空判。
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 会员详情需要同时加载会员数据和最近支付订单摘要。
        VipMember vipMember = vipMemberService.getVipMemberByUserId(userId);
        List<PayOrder> recentOrders = payOrderMapper.selectList(new LambdaQueryWrapper<PayOrder>()
                .eq(PayOrder::getUserId, userId)
                .orderByDesc(PayOrder::getCreateTime)
                .last("limit 5"));

        PayOrder latestOrder = resolveLatestOrder(vipMember, recentOrders);
        VipAdminMemberDetailVo vipAdminMemberDetailVo = new VipAdminMemberDetailVo();
        vipAdminMemberDetailVo.setUserId(sysUser.getId());
        vipAdminMemberDetailVo.setUsername(sysUser.getUsername());
        vipAdminMemberDetailVo.setNickname(sysUser.getNickname());
        vipAdminMemberDetailVo.setEmail(sysUser.getEmail());
        vipAdminMemberDetailVo.setAvatar(sysUser.getAvatar());
        vipAdminMemberDetailVo.setUserStatus(sysUser.getStatus());
        vipAdminMemberDetailVo.setFansCount(sysUser.getFansCount());
        vipAdminMemberDetailVo.setFollowCount(sysUser.getFollowCount());
        vipAdminMemberDetailVo.setIntroduction(sysUser.getIntroduction());
        vipAdminMemberDetailVo.setLoginTime(sysUser.getLoginTime());
        vipAdminMemberDetailVo.setCreateTime(sysUser.getCreateTime());
        // 会员公共字段和最近订单列表在这里一起装配，保证详情页字段口径统一。
        fillVipFields(vipAdminMemberDetailVo, vipMember, latestOrder);
        vipAdminMemberDetailVo.setRecentOrders(recentOrders.stream().map(this::toVipAdminRecentOrderVo).toList());
        maskDetailEmailForViewer(vipAdminMemberDetailVo);
        return vipAdminMemberDetailVo;
    }

    /**
     * 手动调整只改会员数据，不生成新的支付订单。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adjustMember(VipAdminMemberAdjustDto vipAdminMemberAdjustDto) {
        // 先确认操作目标存在，避免对不存在用户写入会员数据。
        SysUser sysUser = sysUserMapper.selectById(vipAdminMemberAdjustDto.getUserId());
        if (sysUser == null) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 管理动作必须落在既定枚举内，避免 controller 透传任意字符串触发未知分支。
        VipAdminAdjustActionEnum actionEnum = VipAdminAdjustActionEnum.fromCode(vipAdminMemberAdjustDto.getActionType());
        if (actionEnum == null) {
            throw new BlogException(BlogConstants.VipAdjustActionInvalid);
        }

        // 失效动作不需要天数，其余动作必须传入正数天数。
        if (actionEnum != VipAdminAdjustActionEnum.EXPIRE_NOW
                && (vipAdminMemberAdjustDto.getDays() == null || vipAdminMemberAdjustDto.getDays() <= 0)) {
            throw new BlogException(BlogConstants.VipAdjustDaysInvalid);
        }

        VipMember currentVipMember = vipMemberService.getVipMemberByUserId(vipAdminMemberAdjustDto.getUserId());
        String lastOrderNo = currentVipMember == null ? null : currentVipMember.getLastOrderNo();
        // 手动调整只改会员数据，不伪造支付订单；续期时沿用最后订单号便于后续追溯。
        switch (actionEnum) {
            case ACTIVATE -> vipMemberService.overwriteVip(vipAdminMemberAdjustDto.getUserId(), vipAdminMemberAdjustDto.getDays());
            case EXTEND -> vipMemberService.activateVip(vipAdminMemberAdjustDto.getUserId(), lastOrderNo, vipAdminMemberAdjustDto.getDays());
            case EXPIRE_NOW -> vipMemberService.expireVipImmediately(vipAdminMemberAdjustDto.getUserId());
            default -> throw new BlogException(BlogConstants.VipAdjustActionInvalid);
        }
    }

    /**
     * 订单列表按支付订单数据表分页，再补齐用户展示信息。
     */
    @Override
    public PageVo<List<VipAdminOrderPageVo>> pageOrders(VipAdminOrderPageDto vipAdminOrderPageDto) {
        Page<PayOrder> page = new Page<>(vipAdminOrderPageDto.getPageNum(), vipAdminOrderPageDto.getPageSize());
        LambdaQueryWrapper<PayOrder> queryWrapper = new LambdaQueryWrapper<>();
        // 订单筛选全部直接落在支付订单数据表，保持列表结果与真实支付记录一致。
        if (vipAdminOrderPageDto.getOrderNo() != null && !vipAdminOrderPageDto.getOrderNo().isBlank()) {
            queryWrapper.like(PayOrder::getOrderNo, vipAdminOrderPageDto.getOrderNo().trim());
        }
        if (vipAdminOrderPageDto.getPlanCode() != null && !vipAdminOrderPageDto.getPlanCode().isBlank()) {
            queryWrapper.eq(PayOrder::getPlanCode, vipAdminOrderPageDto.getPlanCode().trim());
        }
        if (vipAdminOrderPageDto.getStatus() != null && !vipAdminOrderPageDto.getStatus().isBlank()) {
            queryWrapper.eq(PayOrder::getStatus, vipAdminOrderPageDto.getStatus().trim());
        }
        if (vipAdminOrderPageDto.getChannel() != null && !vipAdminOrderPageDto.getChannel().isBlank()) {
            queryWrapper.eq(PayOrder::getChannel, vipAdminOrderPageDto.getChannel().trim());
        }
        if (vipAdminOrderPageDto.getClientType() != null && !vipAdminOrderPageDto.getClientType().isBlank()) {
            queryWrapper.eq(PayOrder::getClientType, vipAdminOrderPageDto.getClientType().trim());
        }
        if (vipAdminOrderPageDto.getCreateTimeStart() != null) {
            queryWrapper.ge(PayOrder::getCreateTime, vipAdminOrderPageDto.getCreateTimeStart());
        }
        if (vipAdminOrderPageDto.getCreateTimeEnd() != null) {
            queryWrapper.le(PayOrder::getCreateTime, vipAdminOrderPageDto.getCreateTimeEnd());
        }

        List<Integer> matchedUserIds = resolveUserIdsByKeyword(vipAdminOrderPageDto.getUserKeyword());
        if (matchedUserIds != null && matchedUserIds.isEmpty()) {
            return new PageVo<>(Collections.emptyList(), 0L);
        }
        if (matchedUserIds != null) {
            // 用户关键词先反查用户ID集合，再回到订单表过滤，避免在订单表存冗余搜索字段。
            queryWrapper.in(PayOrder::getUserId, matchedUserIds);
        }

        queryWrapper.orderByDesc(PayOrder::getCreateTime);
        Page<PayOrder> payOrderPage = payOrderMapper.selectPage(page, queryWrapper);
        List<PayOrder> payOrders = payOrderPage.getRecords();
        if (payOrders.isEmpty()) {
            return new PageVo<>(Collections.emptyList(), payOrderPage.getTotal());
        }

        // 当前页订单再批量补齐用户展示信息，减少表格渲染时的重复查询。
        Map<Integer, SysUser> sysUserMap = queryUserMap(payOrders.stream().map(PayOrder::getUserId).toList());
        List<VipAdminOrderPageVo> vipAdminOrderPageVos = payOrders.stream()
                .map(payOrder -> toVipAdminOrderPageVo(payOrder, sysUserMap.get(payOrder.getUserId())))
                .toList();
        maskOrderEmailsForViewer(vipAdminOrderPageVos);
        return new PageVo<>(vipAdminOrderPageVos, payOrderPage.getTotal());
    }

    /**
     * 套餐后台管理需要看到启停状态，因此读取全部套餐而不是仅启用项。
     */
    @Override
    public List<VipAdminPlanVo> listPlans() {
        // 管理端需要看见全部套餐，包括已停用项，所以不加 enabled 过滤。
        return vipPlanMapper.selectList(new LambdaQueryWrapper<VipPlan>()
                        .orderByAsc(VipPlan::getDays)
                        .orderByAsc(VipPlan::getId))
                .stream()
                .map(this::toVipAdminPlanVo)
                .toList();
    }

    /**
     * 套餐编辑只允许修改展示和销售配置，不变更套餐编码。
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePlan(VipAdminPlanUpdateDto vipAdminPlanUpdateDto) {
        // 先校验套餐是否存在，再覆盖允许修改的销售配置字段。
        VipPlan vipPlan = vipPlanMapper.selectById(vipAdminPlanUpdateDto.getId());
        if (vipPlan == null) {
            throw new BlogException(BlogConstants.VipPlanNotFound);
        }
        vipPlan.setName(vipAdminPlanUpdateDto.getName());
        vipPlan.setDays(vipAdminPlanUpdateDto.getDays());
        vipPlan.setPriceFen(vipAdminPlanUpdateDto.getPriceFen());
        vipPlan.setEnabled(vipAdminPlanUpdateDto.getEnabled());
        vipPlan.setDescription(vipAdminPlanUpdateDto.getDescription());
        vipPlanMapper.updateById(vipPlan);
    }

    /**
     * 根据会员筛选条件先收敛用户范围，再交给用户表做真实分页。
     */
    private UserScope resolveMemberUserScope(VipAdminMemberPageDto vipAdminMemberPageDto) {
        Date now = new Date();
        String status = vipAdminMemberPageDto.getStatus();
        Date expireTimeStart = vipAdminMemberPageDto.getExpireTimeStart();
        Date expireTimeEnd = vipAdminMemberPageDto.getExpireTimeEnd();
        boolean hasExpireFilter = expireTimeStart != null || expireTimeEnd != null;

        if (status == null || status.isBlank()) {
            if (!hasExpireFilter) {
                return UserScope.all();
            }
            // 未指定状态但指定了到期时间时，只需圈出有会员记录且命中时间范围的用户。
            List<Integer> userIds = vipMemberMapper.selectList(buildVipMemberQuery(null, expireTimeStart, expireTimeEnd, now))
                    .stream()
                    .map(VipMember::getUserId)
                    .toList();
            return userIds.isEmpty() ? UserScope.empty() : UserScope.in(userIds);
        }

        if (VipMemberStatusEnum.NONE.getCode().equalsIgnoreCase(status)) {
            if (hasExpireFilter) {
                return UserScope.empty();
            }
            // “未开通”本质上是没有任何会员记录，因此要从用户全集里排除已有会员记录的用户。
            List<Integer> vipUserIds = vipMemberMapper.selectList(new LambdaQueryWrapper<VipMember>().select(VipMember::getUserId))
                    .stream()
                    .map(VipMember::getUserId)
                    .toList();
            return vipUserIds.isEmpty() ? UserScope.all() : UserScope.notIn(vipUserIds);
        }

        if (!VipMemberStatusEnum.ACTIVE.getCode().equalsIgnoreCase(status)
                && !VipMemberStatusEnum.EXPIRED.getCode().equalsIgnoreCase(status)) {
            throw new BlogException(BlogConstants.ParamError);
        }

        List<Integer> userIds = vipMemberMapper.selectList(buildVipMemberQuery(status, expireTimeStart, expireTimeEnd, now))
                .stream()
                .map(VipMember::getUserId)
                .toList();
        return userIds.isEmpty() ? UserScope.empty() : UserScope.in(userIds);
    }

    /**
     * ACTIVE/EXPIRED 的筛选要按当前时间归一化，避免依赖定时任务先执行。
     */
    private LambdaQueryWrapper<VipMember> buildVipMemberQuery(String status, Date expireTimeStart, Date expireTimeEnd, Date now) {
        LambdaQueryWrapper<VipMember> queryWrapper = new LambdaQueryWrapper<>();
        if (VipMemberStatusEnum.ACTIVE.getCode().equalsIgnoreCase(status)) {
            queryWrapper.eq(VipMember::getStatus, VipMemberStatusEnum.ACTIVE.getCode())
                    .gt(VipMember::getExpireTime, now);
        } else if (VipMemberStatusEnum.EXPIRED.getCode().equalsIgnoreCase(status)) {
            queryWrapper.and(wrapper -> wrapper.eq(VipMember::getStatus, VipMemberStatusEnum.EXPIRED.getCode())
                    .or()
                    .le(VipMember::getExpireTime, now));
        }
        if (expireTimeStart != null) {
            queryWrapper.ge(VipMember::getExpireTime, expireTimeStart);
        }
        if (expireTimeEnd != null) {
            queryWrapper.le(VipMember::getExpireTime, expireTimeEnd);
        }
        queryWrapper.orderByDesc(VipMember::getExpireTime);
        return queryWrapper;
    }

    /**
     * 关键词检索统一复用，避免会员页和订单页出现不同搜索口径。
     */
    private void appendUserKeywordCondition(LambdaQueryWrapper<SysUser> queryWrapper, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return;
        }
        String normalizedKeyword = keyword.trim();
        Integer exactUserId = parseUserId(normalizedKeyword);
        queryWrapper.and(wrapper -> {
            if (exactUserId != null) {
                wrapper.eq(SysUser::getId, exactUserId).or();
            }
            wrapper.like(SysUser::getUsername, normalizedKeyword)
                    .or()
                    .like(SysUser::getNickname, normalizedKeyword)
                    .or()
                    .like(SysUser::getEmail, normalizedKeyword);
        });
    }

    /**
     * 订单页按用户搜索时，先查用户ID集合再反向过滤订单。
     */
    private List<Integer> resolveUserIdsByKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        appendUserKeywordCondition(queryWrapper, keyword);
        return sysUserMapper.selectList(queryWrapper).stream().map(SysUser::getId).toList();
    }

    /**
     * 只给分页用户批量补齐会员信息，避免列表页产生逐条查询。
     */
    private Map<Integer, VipMember> queryVipMemberMap(List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return vipMemberMapper.selectList(new LambdaQueryWrapper<VipMember>().in(VipMember::getUserId, userIds))
                .stream()
                .collect(Collectors.toMap(VipMember::getUserId, vipMember -> vipMember, (left, right) -> right, HashMap::new));
    }

    /**
     * 根据最后订单号批量回查套餐快照，减少页面上的“最近套餐”缺失。
     */
    private Map<String, PayOrder> queryLastOrderMap(List<String> orderNos) {
        if (orderNos.isEmpty()) {
            return Collections.emptyMap();
        }
        return payOrderMapper.selectList(new LambdaQueryWrapper<PayOrder>().in(PayOrder::getOrderNo, orderNos))
                .stream()
                .collect(Collectors.toMap(PayOrder::getOrderNo, payOrder -> payOrder, (left, right) -> left, LinkedHashMap::new));
    }

    /**
     * 订单页和详情页都需要用户基础信息，这里统一按用户ID批量回查。
     */
    private Map<Integer, SysUser> queryUserMap(List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyMap();
        }
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>().in(SysUser::getId, userIds))
                .stream()
                .collect(Collectors.toMap(SysUser::getId, sysUser -> sysUser, (left, right) -> left, HashMap::new));
    }

    /**
     * 会员详情优先读取会员记录关联的最后订单，没有则回退到最近一笔支付订单。
     */
    private PayOrder resolveLatestOrder(VipMember vipMember, List<PayOrder> recentOrders) {
        if (vipMember != null && vipMember.getLastOrderNo() != null && !vipMember.getLastOrderNo().isBlank()) {
            PayOrder payOrder = payOrderMapper.selectOne(new LambdaQueryWrapper<PayOrder>()
                    .eq(PayOrder::getOrderNo, vipMember.getLastOrderNo())
                    .last("limit 1"));
            if (payOrder != null) {
                return payOrder;
            }
        }
        return recentOrders.isEmpty() ? null : recentOrders.get(0);
    }

    /**
     * 列表行仅保留运营视角需要的信息，减少前端重复组装。
     */
    private VipAdminMemberPageVo toVipAdminMemberPageVo(SysUser sysUser, VipMember vipMember, Map<String, PayOrder> payOrderMap) {
        VipAdminMemberPageVo vipAdminMemberPageVo = new VipAdminMemberPageVo();
        vipAdminMemberPageVo.setUserId(sysUser.getId());
        vipAdminMemberPageVo.setUsername(sysUser.getUsername());
        vipAdminMemberPageVo.setNickname(sysUser.getNickname());
        vipAdminMemberPageVo.setEmail(sysUser.getEmail());
        vipAdminMemberPageVo.setAvatar(sysUser.getAvatar());
        vipAdminMemberPageVo.setUserStatus(sysUser.getStatus());
        vipAdminMemberPageVo.setLoginTime(sysUser.getLoginTime());
        vipAdminMemberPageVo.setCreateTime(sysUser.getCreateTime());
        PayOrder latestOrder = vipMember == null ? null : payOrderMap.get(vipMember.getLastOrderNo());
        fillVipFields(vipAdminMemberPageVo, vipMember, latestOrder);
        return vipAdminMemberPageVo;
    }

    /**
     * 会员相关公共字段统一在这里补齐，保证列表和详情展示一致。
     */
    private void fillVipFields(Object target, VipMember vipMember, PayOrder latestOrder) {
        boolean hasVip = hasVipAccess(vipMember);
        String vipStatus = resolveVipStatus(vipMember);
        Date vipStartTime = vipMember == null ? null : vipMember.getStartTime();
        Date vipExpireTime = vipMember == null ? null : vipMember.getExpireTime();
        Long remainingDays = calculateRemainingDays(vipExpireTime);
        String lastOrderNo = vipMember == null ? null : vipMember.getLastOrderNo();
        String latestPlanName = resolveLatestPlanName(vipMember, latestOrder);

        if (target instanceof VipAdminMemberPageVo pageVo) {
            // 列表页保留轻量字段，避免表格一次返回过多详情信息。
            pageVo.setIsVip(hasVip);
            pageVo.setVipStatus(vipStatus);
            pageVo.setVipStartTime(vipStartTime);
            pageVo.setVipExpireTime(vipExpireTime);
            pageVo.setRemainingDays(remainingDays);
            pageVo.setLastOrderNo(lastOrderNo);
            pageVo.setLatestPlanName(latestPlanName);
            return;
        }

        if (target instanceof VipAdminMemberDetailVo detailVo) {
            // 详情页补齐同一套会员字段，确保与列表页状态口径完全一致。
            detailVo.setIsVip(hasVip);
            detailVo.setVipStatus(vipStatus);
            detailVo.setVipStartTime(vipStartTime);
            detailVo.setVipExpireTime(vipExpireTime);
            detailVo.setRemainingDays(remainingDays);
            detailVo.setLastOrderNo(lastOrderNo);
            detailVo.setLatestPlanName(latestPlanName);
        }
    }

    /**
     * 最近套餐优先读订单快照；没有订单快照时，说明当前会员可能来自人工调整。
     */
    private String resolveLatestPlanName(VipMember vipMember, PayOrder latestOrder) {
        if (latestOrder != null && latestOrder.getPlanName() != null && !latestOrder.getPlanName().isBlank()) {
            return latestOrder.getPlanName();
        }
        if (vipMember != null && vipMember.getStartTime() != null) {
            return "手动调整";
        }
        return "-";
    }

    /**
     * 订单页按用户信息补齐昵称和邮箱。
     */
    private VipAdminOrderPageVo toVipAdminOrderPageVo(PayOrder payOrder, SysUser sysUser) {
        VipAdminOrderPageVo vipAdminOrderPageVo = new VipAdminOrderPageVo();
        if (sysUser != null) {
            vipAdminOrderPageVo.setUserId(sysUser.getId());
            vipAdminOrderPageVo.setUsername(sysUser.getUsername());
            vipAdminOrderPageVo.setNickname(sysUser.getNickname());
            vipAdminOrderPageVo.setEmail(sysUser.getEmail());
        } else {
            vipAdminOrderPageVo.setUserId(payOrder.getUserId());
        }
        vipAdminOrderPageVo.setOrderNo(payOrder.getOrderNo());
        vipAdminOrderPageVo.setPlanCode(payOrder.getPlanCode());
        vipAdminOrderPageVo.setPlanName(payOrder.getPlanName());
        vipAdminOrderPageVo.setPlanDays(payOrder.getPlanDays());
        vipAdminOrderPageVo.setAmountFen(payOrder.getAmountFen());
        vipAdminOrderPageVo.setPriceYuan(fenToYuan(payOrder.getAmountFen()));
        vipAdminOrderPageVo.setStatus(payOrder.getStatus());
        vipAdminOrderPageVo.setChannel(payOrder.getChannel());
        vipAdminOrderPageVo.setClientType(payOrder.getClientType());
        vipAdminOrderPageVo.setPaidTime(payOrder.getPaidTime());
        vipAdminOrderPageVo.setCreateTime(payOrder.getCreateTime());
        return vipAdminOrderPageVo;
    }

    /**
     * 最近订单摘要保留最小必要字段，供抽屉内快速查看。
     */
    private VipAdminRecentOrderVo toVipAdminRecentOrderVo(PayOrder payOrder) {
        VipAdminRecentOrderVo vipAdminRecentOrderVo = new VipAdminRecentOrderVo();
        vipAdminRecentOrderVo.setOrderNo(payOrder.getOrderNo());
        vipAdminRecentOrderVo.setPlanCode(payOrder.getPlanCode());
        vipAdminRecentOrderVo.setPlanName(payOrder.getPlanName());
        vipAdminRecentOrderVo.setAmountFen(payOrder.getAmountFen());
        vipAdminRecentOrderVo.setPriceYuan(fenToYuan(payOrder.getAmountFen()));
        vipAdminRecentOrderVo.setStatus(payOrder.getStatus());
        vipAdminRecentOrderVo.setChannel(payOrder.getChannel());
        vipAdminRecentOrderVo.setClientType(payOrder.getClientType());
        vipAdminRecentOrderVo.setPaidTime(payOrder.getPaidTime());
        vipAdminRecentOrderVo.setCreateTime(payOrder.getCreateTime());
        return vipAdminRecentOrderVo;
    }

    /**
     * 套餐管理直接返回数据库字段和换算后的元金额。
     */
    private VipAdminPlanVo toVipAdminPlanVo(VipPlan vipPlan) {
        VipAdminPlanVo vipAdminPlanVo = new VipAdminPlanVo();
        vipAdminPlanVo.setId(vipPlan.getId());
        vipAdminPlanVo.setCode(vipPlan.getCode());
        vipAdminPlanVo.setName(vipPlan.getName());
        vipAdminPlanVo.setDays(vipPlan.getDays());
        vipAdminPlanVo.setPriceFen(vipPlan.getPriceFen());
        vipAdminPlanVo.setPriceYuan(fenToYuan(vipPlan.getPriceFen()));
        vipAdminPlanVo.setEnabled(vipPlan.getEnabled());
        vipAdminPlanVo.setDescription(vipPlan.getDescription());
        vipAdminPlanVo.setCreateTime(vipPlan.getCreateTime());
        vipAdminPlanVo.setUpdateTime(vipPlan.getUpdateTime());
        return vipAdminPlanVo;
    }

    /**
     * viewer 账号沿用现有后台规则，不直接暴露用户邮箱。
     */
    private void maskMemberEmailsForViewer(List<VipAdminMemberPageVo> vipAdminMemberPageVos) {
        if (isCurrentAdmin()) {
            return;
        }
        vipAdminMemberPageVos.forEach(vipAdminMemberPageVo -> vipAdminMemberPageVo.setEmail(null));
    }

    /**
     * 订单页同样遵循邮箱脱敏规则。
     */
    private void maskOrderEmailsForViewer(List<VipAdminOrderPageVo> vipAdminOrderPageVos) {
        if (isCurrentAdmin()) {
            return;
        }
        vipAdminOrderPageVos.forEach(vipAdminOrderPageVo -> vipAdminOrderPageVo.setEmail(null));
    }

    /**
     * 详情页也需要与列表页保持同一套脱敏策略。
     */
    private void maskDetailEmailForViewer(VipAdminMemberDetailVo vipAdminMemberDetailVo) {
        if (!isCurrentAdmin()) {
            vipAdminMemberDetailVo.setEmail(null);
        }
    }

    /**
     * 是否为有效 VIP 以状态和到期时间共同判定。
     */
    private boolean hasVipAccess(VipMember vipMember) {
        return vipMember != null
                && VipMemberStatusEnum.ACTIVE.getCode().equals(vipMember.getStatus())
                && vipMember.getExpireTime() != null
                && vipMember.getExpireTime().after(new Date());
    }

    /**
     * 即便定时任务尚未同步，后台展示也要按当前时间归一化会员状态。
     */
    private String resolveVipStatus(VipMember vipMember) {
        if (vipMember == null) {
            return VipMemberStatusEnum.NONE.getCode();
        }
        if (vipMember.getExpireTime() != null && vipMember.getExpireTime().after(new Date())
                && VipMemberStatusEnum.ACTIVE.getCode().equals(vipMember.getStatus())) {
            return VipMemberStatusEnum.ACTIVE.getCode();
        }
        return VipMemberStatusEnum.EXPIRED.getCode();
    }

    /**
     * 剩余天数向上取整，避免当天过期的会员被展示成负数。
     */
    private Long calculateRemainingDays(Date expireTime) {
        if (expireTime == null) {
            return 0L;
        }
        long diffMillis = expireTime.getTime() - System.currentTimeMillis();
        if (diffMillis <= 0) {
            return 0L;
        }
        long oneDayMillis = 24L * 60L * 60L * 1000L;
        return Math.max(1L, (diffMillis + oneDayMillis - 1) / oneDayMillis);
    }

    /**
     * 金额统一保留两位小数，减少前端重复换算。
     */
    private String fenToYuan(Integer amountFen) {
        if (amountFen == null) {
            return "0.00";
        }
        return BigDecimal.valueOf(amountFen)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .toPlainString();
    }

    /**
     * 根据当前登录角色决定是否展示敏感字段。
     */
    private boolean isCurrentAdmin() {
        SysUser currentUser = SecurityUtils.getUser();
        if (currentUser == null || currentUser.getSysRoles() == null) {
            return false;
        }
        return currentUser.getSysRoles().stream()
                .map(SysRole::getRole)
                .anyMatch("admin"::equals);
    }

    /**
     * 纯数字关键词按用户ID精确匹配，其余情况按普通字符串模糊查询。
     */
    private Integer parseUserId(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return null;
        }
        for (int i = 0; i < keyword.length(); i++) {
            if (!Character.isDigit(keyword.charAt(i))) {
                return null;
            }
        }
        return Integer.valueOf(keyword);
    }

    /**
     * 将 VIP 侧过滤结果转换为 sys_user 分页时可复用的用户范围。
     */
    private void applyUserScope(LambdaQueryWrapper<SysUser> queryWrapper, UserScope userScope) {
        if (userScope.type == UserScopeType.IN) {
            queryWrapper.in(SysUser::getId, userScope.userIds);
            return;
        }
        if (userScope.type == UserScopeType.NOT_IN && !userScope.userIds.isEmpty()) {
            queryWrapper.notIn(SysUser::getId, userScope.userIds);
        }
    }

    /**
     * 用户范围对象用于隔离“全部 / 指定集合 / 排除集合 / 空结果”四种情况。
     */
    private static class UserScope {

        private final UserScopeType type;

        private final List<Integer> userIds;

        private UserScope(UserScopeType type, List<Integer> userIds) {
            this.type = type;
            this.userIds = userIds;
        }

        private static UserScope all() {
            return new UserScope(UserScopeType.ALL, Collections.emptyList());
        }

        private static UserScope in(List<Integer> userIds) {
            return new UserScope(UserScopeType.IN, userIds);
        }

        private static UserScope notIn(List<Integer> userIds) {
            return new UserScope(UserScopeType.NOT_IN, userIds);
        }

        private static UserScope empty() {
            return new UserScope(UserScopeType.EMPTY, Collections.emptyList());
        }
    }

    /**
     * 用户范围类型。
     */
    private enum UserScopeType {
        ALL,
        IN,
        NOT_IN,
        EMPTY
    }
}
