/**
 * 时间格式化工具函数
 */

/**
 * 格式化时间为相对时间或绝对时间
 * @param {Date|string} date 日期
 * @returns {string} 格式化后的时间
 */
export const formatTime = (date) => {
  if (!date) return "";

  const now = new Date();
  const target = new Date(date);
  const diff = now.getTime() - target.getTime();

  // 时间差（毫秒）
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;
  const week = 7 * day;
  const month = 30 * day;
  const year = 365 * day;

  if (diff < minute) {
    return "刚刚";
  } else if (diff < hour) {
    return `${Math.floor(diff / minute)}分钟前`;
  } else if (diff < day) {
    return `${Math.floor(diff / hour)}小时前`;
  } else if (diff < week) {
    return `${Math.floor(diff / day)}天前`;
  } else if (diff < month) {
    return `${Math.floor(diff / week)}周前`;
  } else if (diff < year) {
    return `${Math.floor(diff / month)}个月前`;
  } else {
    // 超过一年显示具体日期
    return formatDate(target, "YYYY-MM-DD");
  }
};

/**
 * 格式化日期
 * @param {Date} date 日期对象
 * @param {string} format 格式字符串
 * @returns {string} 格式化后的日期
 */
export const formatDate = (date, format = "YYYY-MM-DD HH:mm:ss") => {
  if (!date) return "";

  const d = new Date(date);

  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  const hours = String(d.getHours()).padStart(2, "0");
  const minutes = String(d.getMinutes()).padStart(2, "0");
  const seconds = String(d.getSeconds()).padStart(2, "0");

  return format.replace("YYYY", year).replace("MM", month).replace("DD", day).replace("HH", hours).replace("mm", minutes).replace("ss", seconds);
};

/**
 * 格式化日期为中文本地化格式
 * @param {Date|string} dateString 日期字符串或日期对象
 * @returns {string} 格式化后的日期 (YYYY/MM/DD)
 */
export const formatDateCN = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

/**
 * 获取友好的时间显示
 * @param {Date|string} date 日期
 * @returns {string} 友好的时间显示
 */
export const getFriendlyTime = (date) => {
  if (!date) return "";

  const now = new Date();
  const target = new Date(date);

  // 如果是今天，显示时间
  if (now.toDateString() === target.toDateString()) {
    return formatDate(target, "HH:mm");
  }

  // 如果是昨天
  const yesterday = new Date(now);
  yesterday.setDate(now.getDate() - 1);
  if (yesterday.toDateString() === target.toDateString()) {
    return `昨天 ${formatDate(target, "HH:mm")}`;
  }

  // 如果是今年
  if (now.getFullYear() === target.getFullYear()) {
    return formatDate(target, "MM-DD HH:mm");
  }

  // 其他情况显示完整日期
  return formatDate(target, "YYYY-MM-DD HH:mm");
};
