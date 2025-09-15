import request from "@/utils/Request";

/**
 * 点赞或取消点赞
 * @param {number} type 点赞类型 0-文章 1-评论
 * @param {number} typeId 点赞类型id
 * @returns {Promise}
 */
export function toggleLike(type, typeId) {
  return request({
    url: "/like/toggle",
    method: "post",
    params: {
      type,
      typeId,
    },
  });
}

/**
 * 判断当前用户是否已点赞
 * @param {number} type 点赞类型 0-文章 1-评论
 * @param {number} typeId 点赞类型id
 * @returns {Promise<boolean>}
 */
export function isLiked(type, typeId) {
  return request({
    url: "/like/isLiked",
    method: "get",
    params: {
      type,
      typeId,
    },
  });
}
