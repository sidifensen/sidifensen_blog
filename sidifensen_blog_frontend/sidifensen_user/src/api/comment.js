import request from "@/utils/Request";

/**
 * 发表评论
 * @param {Object} data 评论数据
 * @param {number} data.articleId 文章ID
 * @param {number} data.parentId 父评论ID（0表示顶级评论）
 * @param {number} data.replyUserId 回复用户ID
 * @param {string} data.content 评论内容
 * @returns {Promise}
 */
export const addComment = (data) => {
  return request({
    url: "/comment/add",
    method: "post",
    data,
  });
};

/**
 * 删除评论
 * @param {number} commentId 评论ID
 * @returns {Promise}
 */
export const deleteComment = (commentId) => {
  return request({
    url: `/comment/${commentId}`,
    method: "delete",
  });
};

/**
 * 获取文章评论列表
 * @param {number} articleId 文章ID
 * @param {number} pageNum 页码
 * @param {number} pageSize 页大小
 * @returns {Promise}
 */
export const getCommentList = (articleId, pageNum, pageSize) => {
  return request({
    url: "/comment/list",
    method: "get",
    params: {
      articleId,
      pageNum,
      pageSize,
    },
  });
};

/**
 * 获取评论的回复列表
 * @param {number} commentId 评论ID
 * @param {number} pageNum 页码
 * @param {number} pageSize 页大小
 * @returns {Promise}
 */
export const getReplyList = (commentId, pageNum, pageSize) => {
  return request({
    url: "/comment/reply/list",
    method: "get",
    params: {
      commentId,
      pageNum,
      pageSize,
    },
  });
};

/**
 * 管理员审核评论
 * @param {number} commentId 评论ID
 * @param {number} examineStatus 审核状态 0-待审核 1-审核通过 2-审核未通过
 * @returns {Promise}
 */
export const auditComment = (commentId, examineStatus) => {
  return request({
    url: `/comment/admin/audit/${commentId}`,
    method: "put",
    params: {
      examineStatus,
    },
  });
};

/**
 * 管理员删除评论
 * @param {number} commentId 评论ID
 * @returns {Promise}
 */
export const adminDeleteComment = (commentId) => {
  return request({
    url: `/comment/admin/${commentId}`,
    method: "delete",
  });
};

/**
 * 获取用户评论管理列表
 * @param {number} pageNum 页码
 * @param {number} pageSize 页大小
 * @param {Object} commentFilterDto 评论筛选条件
 * @param {string} commentFilterDto.keyword 搜索关键词
 * @param {number} commentFilterDto.year 年份筛选
 * @param {number} commentFilterDto.month 月份筛选
 * @returns {Promise}
 */
export const getUserCommentManageList = (pageNum, pageSize, commentFilterDto) => {
  return request({
    url: "/comment/manage/list",
    method: "post",
    params: {
      pageNum,
      pageSize,
    },
    data: commentFilterDto,
  });
};
