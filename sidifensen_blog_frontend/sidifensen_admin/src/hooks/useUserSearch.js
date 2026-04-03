import { ref } from 'vue'
import { queryUser } from '@/api/user'

/**
 * 用户远程搜索 Hook
 * 用于管理员下拉框选择用户时的远程搜索
 */
export function useUserSearch() {
  const filteredUserList = ref([])
  const userLoading = ref(false)
  let userSearchTimer = null

  const searchUsers = (keyword) => {
    if (userSearchTimer) {
      clearTimeout(userSearchTimer)
    }
    if (!keyword) {
      filteredUserList.value = []
      return
    }
    userLoading.value = true
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
