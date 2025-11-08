/**
 * 格式化时间为相对时间
 * @param {string|Date} time - 时间
 * @returns {string} 格式化后的时间
 */
export function formatTimeAgo(time) {
  if (!time) return "";

  const date = new Date(time);
  const now = new Date();
  const diff = now - date;

  // 小于1分钟
  if (diff < 60 * 1000) {
    return "刚刚";
  }

  // 小于1小时
  if (diff < 60 * 60 * 1000) {
    const minutes = Math.floor(diff / (60 * 1000));
    return `${minutes}分钟前`;
  }

  // 小于1天
  if (diff < 24 * 60 * 60 * 1000) {
    const hours = Math.floor(diff / (60 * 60 * 1000));
    return `${hours}小时前`;
  }

  // 小于7天
  if (diff < 7 * 24 * 60 * 60 * 1000) {
    const days = Math.floor(diff / (24 * 60 * 60 * 1000));
    return `${days}天前`;
  }

  // 超过7天，显示具体日期
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");

  // 如果是今年，不显示年份
  if (year === now.getFullYear()) {
    return `${month}-${day}`;
  }

  return `${year}-${month}-${day}`;
}

/**
 * 格式化时间为标准格式
 * @param {string|Date} time - 时间
 * @param {string} format - 格式，默认 YYYY-MM-DD HH:mm:ss
 * @returns {string} 格式化后的时间
 */
export function formatTime(time, format = "YYYY-MM-DD HH:mm:ss") {
  if (!time) return "";

  const date = new Date(time);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  const seconds = String(date.getSeconds()).padStart(2, "0");

  return format.replace("YYYY", year).replace("MM", month).replace("DD", day).replace("HH", hours).replace("mm", minutes).replace("ss", seconds);
}
