import request from "@/utils/Request";

/**
 * 分页获取当前用户的文章浏览记录
 * @param {number} pageNum 页码
 * @param {number} pageSize 每页大小
 * @returns {Promise} 分页浏览历史数据
 */
export const getUserHistoryList = (pageNum, pageSize) => {
  return request({
    url: "/history/list",
    method: "get",
    params: {
      pageNum,
      pageSize,
    },
  });
};

/**
 * 清除当前用户的所有浏览记录
 * @returns {Promise} 清除的记录数量
 */
export const clearUserHistory = () => {
  return request({
    url: "/history/clear",
    method: "delete",
  });
};
