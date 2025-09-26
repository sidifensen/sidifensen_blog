package com.sidifensen.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sidifensen.domain.entity.Follow;
import com.sidifensen.domain.vo.PageVo;
import com.sidifensen.domain.vo.SysUserVo;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sidifensen
 * @since 2025-09-25
 */
public interface FollowService extends IService<Follow> {

    /**
     * 切换关注状态
     * 如果已关注则取消关注，如果未关注则进行关注
     * 
     * @param followedId 被关注用户ID
     * @return 操作后的关注状态，true表示已关注，false表示已取消关注
     */
    Boolean toggleFollow(Integer followedId);

    /**
     * 检查是否已关注某用户
     * 
     * @param followerId 关注者ID
     * @param followedId 被关注者ID
     * @return true表示已关注，false表示未关注
     */
    Boolean isFollowing(Integer followerId, Integer followedId);

    /**
     * 分页获取用户的关注列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 关注用户列表
     */
    PageVo<List<SysUserVo>> getFollowList(Integer userId, Integer pageNum, Integer pageSize);

    /**
     * 分页获取用户的粉丝列表
     * 
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 粉丝用户列表
     */
    PageVo<List<SysUserVo>> getFansList(Integer userId, Integer pageNum, Integer pageSize);

}
