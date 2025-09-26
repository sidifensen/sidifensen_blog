package com.sidifensen.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.domain.entity.Follow;
import com.sidifensen.domain.entity.SysUser;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysUserVo;
import com.sidifensen.exception.BlogException;
import com.sidifensen.mapper.FollowMapper;
import com.sidifensen.service.FollowService;
import com.sidifensen.service.SysUserService;
import com.sidifensen.utils.SecurityUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
}
