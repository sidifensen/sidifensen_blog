import request from "@/utils/Request";

// 切换关注状态（关注/取消关注）
export function toggleFollow(followedId) {
  return request({
    url: `/follow/toggle/${followedId}`,
    method: "post",
  });
}

// 检查是否关注某个用户
export function isFollowing(followerId, followedId) {
  return request({
    url: `/follow/isFollowing`,
    method: "get",
    params: {
      followerId,
      followedId,
    },
  });
}

// 获取用户的关注列表
export function getFollowList(userId, pageNum = 1, pageSize = 10) {
  return request({
    url: `/follow/followList/${userId}`,
    method: "get",
    params: {
      pageNum,
      pageSize,
    },
  });
}

// 获取用户的粉丝列表
export function getFansList(userId, pageNum = 1, pageSize = 10) {
  return request({
    url: `/follow/fansList/${userId}`,
    method: "get",
    params: {
      pageNum,
      pageSize,
    },
  });
}
