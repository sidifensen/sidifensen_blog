package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Follow;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.FollowMapper;
import com.sidifensen.mapper.SysUserMapper;
import com.sidifensen.service.FollowService;
import com.sidifensen.service.MessageService;
import com.sidifensen.service.SysUserService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-25
 */
@Service
@Slf4j
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private MessageService messageService;

    // 通知发送专用线程池
    private final ExecutorService notificationExecutor = new ThreadPoolExecutor(
            3, // 核心线程数
            5, // 最大线程数
            60L, // 空闲线程存活时间
            TimeUnit.SECONDS, // 时间单位
            new LinkedBlockingQueue<>(500), // 任务队列
            r -> {
                Thread thread = new Thread(r);
                thread.setName("follow-notification-thread-" + thread.getId());
                thread.setDaemon(true);
                return thread;
            },
            new ThreadPoolExecutor.CallerRunsPolicy());

    // 异步线程池，用于更新用户计数
    private final ExecutorService asyncExecutor = Executors.newVirtualThreadPerTaskExecutor();

    @Override
    @Transactional
    public Boolean toggleFollow(Integer followedId) {
        // 参数校验
        if (followedId == null) {
            throw new BlogException(BlogConstants.FollowedUserIdRequired);
        }

        // 获取当前登录用户ID
        Integer followerId = SecurityUtils.getUserId();
        if (followerId == null || followerId == 0) {
            throw new BlogException(BlogConstants.LoginRequired);
        }

        // 不能关注自己
        if (followerId.equals(followedId)) {
            throw new BlogException(BlogConstants.CannotFollowSelf);
        }

        // 检查被关注用户是否存在
        if (!sysUserService.lambdaQuery().eq(com.sidifensen.domain.entity.SysUser::getId, followedId).exists()) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 检查是否已关注
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowedId, followedId);

        Follow existingFollow = this.getOne(queryWrapper);

        if (existingFollow != null) {
            // 已关注，执行取消关注
            boolean removed = this.remove(queryWrapper);
            if (!removed) {
                throw new BlogException(BlogConstants.UnfollowError);
            }

            // 异步更新用户计数（取消关注）
            asyncUpdateUserCounts(followerId, followedId, false);

            return false; // 返回false表示已取消关注
        } else {
            // 未关注，执行关注
            Follow follow = new Follow();
            follow.setFollowerId(followerId);
            follow.setFollowedId(followedId);
            follow.setCreateTime(LocalDateTime.now());

            boolean saved = this.save(follow);
            if (!saved) {
                throw new BlogException(BlogConstants.FollowError);
            }

            // 异步更新用户计数（关注）
            asyncUpdateUserCounts(followerId, followedId, true);

            // 发送关注通知
            sendFollowNotification(followerId, followedId);

            return true; // 返回true表示已关注
        }
    }

    /**
     * 发送关注通知（异步）
     * 
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     */
    private void sendFollowNotification(Integer followerId, Integer followedId) {
        // 使用线程池异步执行
        notificationExecutor.execute(() -> {
            try {
                // 查询关注者信息
                SysUser follower = sysUserMapper.selectById(followerId);
                if (follower == null) {
                    log.warn("发送关注通知失败：关注者不存在，followerId={}", followerId);
                    return;
                }

                // 发送通知
                messageService.sendFollowNotification(
                        followerId,
                        followedId,
                        follower.getNickname());
            } catch (Exception e) {
                log.error("发送关注通知失败：followerId={}, followedId={}", followerId, followedId, e);
            }
        });
    }

    @Override
    public Boolean isFollowing(Integer followerId, Integer followedId) {
        if (followerId == null || followedId == null) {
            return false;
        }

        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, followerId)
                .eq(Follow::getFollowedId, followedId);

        return this.exists(queryWrapper);
    }

    @Override
    public PageVo<List<SysUserVo>> getFollowList(Integer userId, Integer pageNum, Integer pageSize) {
        // 参数校验
        if (userId == null) {
            throw new BlogException(BlogConstants.UserIdRequired);
        }

        // 检查用户是否存在
        if (!sysUserService.lambdaQuery().eq(SysUser::getId, userId).exists()) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 分页查询关注关系
        Page<Follow> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowerId, userId)
                .orderByDesc(Follow::getCreateTime);

        Page<Follow> followPage = this.page(page, queryWrapper);
        List<Follow> followList = followPage.getRecords();

        // 获取被关注用户ID列表
        List<Integer> followedUserIds = followList.stream()
                .map(Follow::getFollowedId)
                .collect(Collectors.toList());

        List<SysUserVo> userVoList;
        if (followedUserIds.isEmpty()) {
            userVoList = List.of();
        } else {
            // 查询被关注用户信息
            List<SysUser> users = sysUserService.lambdaQuery()
                    .in(SysUser::getId, followedUserIds)
                    .eq(SysUser::getStatus, 0) // 只查询正常状态的用户
                    .list();

            // 转换为VO对象
            userVoList = users.stream()
                    .map(user -> {
                        SysUserVo userVo = new SysUserVo();
                        BeanUtils.copyProperties(user, userVo);
                        return userVo;
                    })
                    .collect(Collectors.toList());
        }

        return new PageVo<>(userVoList, followPage.getTotal());
    }

    @Override
    public PageVo<List<SysUserVo>> getFansList(Integer userId, Integer pageNum, Integer pageSize) {
        // 参数校验
        if (userId == null) {
            throw new BlogException(BlogConstants.UserIdRequired);
        }

        // 检查用户是否存在
        if (!sysUserService.lambdaQuery().eq(SysUser::getId, userId).exists()) {
            throw new BlogException(BlogConstants.NotFoundUser);
        }

        // 分页查询粉丝关系
        Page<Follow> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Follow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Follow::getFollowedId, userId)
                .orderByDesc(Follow::getCreateTime);

        Page<Follow> followPage = this.page(page, queryWrapper);
        List<Follow> followList = followPage.getRecords();

        // 获取关注者用户ID列表
        List<Integer> followerUserIds = followList.stream()
                .map(Follow::getFollowerId)
                .collect(Collectors.toList());

        List<SysUserVo> userVoList;
        if (followerUserIds.isEmpty()) {
            userVoList = List.of();
        } else {
            // 查询粉丝用户信息
            List<SysUser> users = sysUserService.lambdaQuery()
                    .in(SysUser::getId, followerUserIds)
                    .eq(SysUser::getStatus, 0) // 只查询正常状态的用户
                    .list();

            // 转换为VO对象
            userVoList = users.stream()
                    .map(user -> {
                        SysUserVo userVo = new SysUserVo();
                        BeanUtils.copyProperties(user, userVo);
                        return userVo;
                    })
                    .collect(Collectors.toList());
        }

        return new PageVo<>(userVoList, followPage.getTotal());
    }

    /**
     * 异步更新用户关注数和粉丝数
     * 
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     * @param isFollow   true-关注操作，false-取消关注操作
     */
    private void asyncUpdateUserCounts(Integer followerId, Integer followedId, boolean isFollow) {
        CompletableFuture.runAsync(() -> {
            try {
                if (isFollow) {
                    // 关注操作：关注者的关注数+1，被关注者的粉丝数+1
                    updateFollowCount(followerId, 1);
                    updateFansCount(followedId, 1);
                } else {
                    // 取消关注操作：关注者的关注数-1，被关注者的粉丝数-1
                    updateFollowCount(followerId, -1);
                    updateFansCount(followedId, -1);
                }
            } catch (Exception e) {
                log.error("异步更新用户计数失败：关注者ID={}, 被关注者ID={}, 操作类型={}",
                        followerId, followedId, isFollow ? "关注" : "取消关注", e);
            }
        }, asyncExecutor);
    }

    /**
     * 更新用户关注数
     * 
     * @param userId    用户ID
     * @param increment 增量（正数为增加，负数为减少）
     */
    private void updateFollowCount(Integer userId, int increment) {
        try {
            LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUser::getId, userId);

            if (increment > 0) {
                // 增加关注数，使用 MySQL 的 COALESCE 函数处理 NULL 值
                updateWrapper.setSql("follow_count = COALESCE(follow_count, 0) + " + increment);
            } else {
                // 减少关注数，确保不会小于0
                updateWrapper.setSql("follow_count = GREATEST(COALESCE(follow_count, 0) + " + increment + ", 0)");
            }

            int updateResult = sysUserMapper.update(null, updateWrapper);
            if (updateResult == 0) {
                log.warn("更新用户关注数失败，可能用户不存在：userId={}", userId);
            }
        } catch (Exception e) {
            log.error("更新用户关注数异常：userId={}, increment={}", userId, increment, e);
            throw e;
        }
    }

    /**
     * 更新用户粉丝数
     * 
     * @param userId    用户ID
     * @param increment 增量（正数为增加，负数为减少）
     */
    private void updateFansCount(Integer userId, int increment) {
        try {
            LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(SysUser::getId, userId);

            if (increment > 0) {
                // 增加粉丝数，使用 MySQL 的 COALESCE 函数处理 NULL 值
                updateWrapper.setSql("fans_count = COALESCE(fans_count, 0) + " + increment);
            } else {
                // 减少粉丝数，确保不会小于0
                updateWrapper.setSql("fans_count = GREATEST(COALESCE(fans_count, 0) + " + increment + ", 0)");
            }

            int updateResult = sysUserMapper.update(null, updateWrapper);
            if (updateResult == 0) {
                log.warn("更新用户粉丝数失败，可能用户不存在：userId={}", userId);
            }
        } catch (Exception e) {
            log.error("更新用户粉丝数异常：userId={}, increment={}", userId, increment, e);
            throw e;
        }
    }
}
