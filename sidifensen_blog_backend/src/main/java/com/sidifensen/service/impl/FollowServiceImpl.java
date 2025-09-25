package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Follow;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.FollowMapper;
import com.sidifensen.service.FollowService;
import com.sidifensen.service.SysUserService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
            log.info("用户 {} 关注用户 {}", followerId, followedId);
            return true; // 返回true表示已关注
        }
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
}
