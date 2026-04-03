import { ref } from 'vue'
import { queryUser } from '@/api/user'

/**
 * 用户远程搜索 Hook
 *
 * 功能说明：
 * - 用于管理员在筛选下拉框中选择用户时的远程搜索
 * - 输入关键词后延迟 300ms 发送请求，避免频繁请求
 * - 支持搜索用户的 nickname（昵称）和 username（用户名）
 *
 * 使用方式：
 * ```js
 * import { useUserSearch } from '@/utils/userSearch'
 *
 * const { filteredUserList, userLoading, searchUsers } = useUserSearch()
 * ```
 *
 * @returns {Object} 包含以下属性和方法：
 * @returns {Ref<Array>} filteredUserList - 搜索结果列表
 * @returns {Ref<boolean>} userLoading - 加载状态
 * @returns {Function} searchUsers - 搜索方法，接收 keyword 参数
 */
export function useUserSearch() {
  const filteredUserList = ref([])
  const userLoading = ref(false)
  let userSearchTimer = null

  /**
   * 远程搜索用户
   * @param {string} keyword - 搜索关键词
   */
  const searchUsers = (keyword) => {
    // 清除之前的定时器，避免重复请求
    if (userSearchTimer) {
      clearTimeout(userSearchTimer)
    }

    // 如果关键词为空，清空结果列表
    if (!keyword) {
      filteredUserList.value = []
      return
    }

    // 显示加载状态
    userLoading.value = true

    // 延迟 300ms 后发送请求（防抖）
    userSearchTimer = setTimeout(async () => {
      try {
        const res = await queryUser({ keyword })
        filteredUserList.value = res.data || []
      } catch {
        ElMessage.error('搜索用户失败')
        filteredUserList.value = []
      } finally {
        userLoading.value = false
      }
    }, 300)
  }

  return { filteredUserList, userLoading, searchUsers }
}
