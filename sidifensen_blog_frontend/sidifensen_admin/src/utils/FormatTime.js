// 格式化时间
export const formatTime = (time) => {
  const date = new Date(time);
  const now = new Date();
  const diff = now - date;
  const minute = 60 * 1000;
  const hour = 60 * minute;
  const day = 24 * hour;

  if (diff < minute) {
    return "刚刚";
  } else if (diff < hour) {
    return Math.floor(diff / minute) + "分钟前";
  } else if (diff < day) {
    return Math.floor(diff / hour) + "小时前";
  } else if (diff < 30 * day) {
    return Math.floor(diff / day) + "天前";
  } else if (diff < 365 * day) {
    return Math.floor(diff / (30 * day)) + "个月前";
  } else {
    return Math.floor(diff / (365 * day)) + "年前";
  }
};
