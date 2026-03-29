/**
 * 格式化工具函数
 */

/**
 * 格式化日期
 * @param {string|Date} date 日期
 * @param {string} format 格式 默认 'YYYY-MM-DD HH:mm'
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm') {
  if (!date) return ''

  const d = new Date(date)

  const year = d.getFullYear()
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  const hours = String(d.getHours()).padStart(2, '0')
  const minutes = String(d.getMinutes()).padStart(2, '0')
  const seconds = String(d.getSeconds()).padStart(2, '0')

  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hours)
    .replace('mm', minutes)
    .replace('ss', seconds)
}

/**
 * 相对时间（多久之前）
 * @param {string|Date} date 日期
 */
export function timeAgo(date) {
  if (!date) return ''

  const now = Date.now()
  const d = new Date(date).getTime()
  const diff = now - d

  const minute = 60 * 1000
  const hour = 60 * minute
  const day = 24 * hour
  const week = 7 * day
  const month = 30 * day
  const year = 365 * day

  if (diff < minute) {
    return '刚刚'
  } else if (diff < hour) {
    return Math.floor(diff / minute) + '分钟前'
  } else if (diff < day) {
    return Math.floor(diff / hour) + '小时前'
  } else if (diff < week) {
    return Math.floor(diff / day) + '天前'
  } else if (diff < month) {
    return Math.floor(diff / week) + '周前'
  } else if (diff < year) {
    return Math.floor(diff / month) + '月前'
  } else {
    return formatDate(date, 'YYYY-MM-DD')
  }
}

/**
 * 格式化数字（超过千显示K，超过百万显示M）
 * @param {number} num 数字
 * @param {number} threshold 阈值，默认1000
 */
export function formatCount(num, threshold = 1000) {
  if (!num) return '0'

  if (num >= threshold * 1000 * 1000) {
    return (num / (threshold * 1000 * 1000)).toFixed(1).replace(/\.0$/, '') + 'M'
  } else if (num >= threshold * 1000) {
    return (num / (threshold * 1000)).toFixed(1).replace(/\.0$/, '') + 'K'
  }

  return String(num)
}

/**
 * 格式化数字（超过万显示万）
 * @param {number} num 数字
 */
export function formatNumber(num) {
  if (!num) return '0'

  if (num >= 10000) {
    return (num / 10000).toFixed(1) + '万'
  }

  return String(num)
}

/**
 * 格式化文件大小
 * @param {number} bytes 字节数
 */
export function formatFileSize(bytes) {
  if (!bytes) return '0 B'

  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))

  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

/**
 * 手机号脱敏
 * @param {string} phone 手机号
 */
export function maskPhone(phone) {
  if (!phone || phone.length !== 11) return phone
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

/**
 * 邮箱脱敏
 * @param {string} email 邮箱
 */
export function maskEmail(email) {
  if (!email) return ''
  const [name, domain] = email.split('@')
  if (!domain) return email
  const maskedName = name.length > 3
    ? name.slice(0, 2) + '***'
    : name.slice(0, 1) + '***'
  return maskedName + '@' + domain
}

export default {
  formatDate,
  timeAgo,
  formatCount,
  formatNumber,
  formatFileSize,
  maskPhone,
  maskEmail
}
