/**
 * 格式化数字 - 使用 K(千) 和 M(百万) 表示大数字，不显示小数
 * @param {number} value - 要格式化的数字
 * @returns {string} 格式化后的字符串，如：500, 2K, 21K, 2M
 */
export const formatCompactNumber = (value) => {
  const numericValue = Number(value) || 0

  if (numericValue >= 1000000) {
    // 大于 100 万，使用 M 表示 (Million)
    return Math.round(numericValue / 1000000) + 'M'
  } else if (numericValue >= 1000) {
    // 大于 1000，使用 K 表示 (Thousand)
    return Math.round(numericValue / 1000) + 'K'
  }
  return numericValue.toString()
}
