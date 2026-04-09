<template>
  <ManagementCard
    title="登录日志管理"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchLoginLogs"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <CommonSelect v-model="searchForm.loginType" :options="loginTypeOptions" label="登录方式" placeholder="登录方式" width="140px" size="small" @change="handleSearch" />
      <CommonSelect v-model="searchForm.status" :options="loginStatusOptions" label="登录状态" placeholder="登录状态" width="140px" size="small" @change="handleSearch" />
      <KeywordSearch v-model.number="searchForm.userId" placeholder="用户ID" :debounce="0" @search="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions :selectedCount="selectedLogs.length" :showBatchDelete="true" @batchDelete="handleBatchDelete" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        v-loading="loading"
        :data="paginatedLogList"
        :show-selection="true"
        :show-id="true"
        :show-user="true"
        :show-create-time="false"
        :show-actions="true"
        :has-view-action="false"
        :has-edit-action="false"
        :has-delete-action="true"
        :actions-width="80"
        @selection-change="handleSelectionChange"
        @delete="handleDelete"
      >
        <!-- 用户ID列 -->
        <el-table-column prop="userId" label="用户ID" width="80">
          <template #default="{ row }">
            <span>{{ row.userId || '-' }}</span>
          </template>
        </el-table-column>
        <!-- 登录方式列 -->
        <el-table-column prop="loginType" label="登录方式" width="120">
          <template #default="{ row }">
            <div class="login-type" :class="getLoginTypeClass(row.loginType)">
              {{ row.loginTypeDesc }}
            </div>
          </template>
        </el-table-column>
        <!-- 登录IP列 -->
        <el-table-column prop="loginIp" label="登录IP" width="150">
          <template #default="{ row }">
            <el-tooltip v-if="row.loginIp" :content="row.loginIp" placement="top-start">
              <div class="log-ip">{{ row.loginIp }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <!-- 登录地址列 -->
        <el-table-column prop="loginAddress" label="登录地址" min-width="150">
          <template #default="{ row }">
            <el-tooltip v-if="row.loginAddress" :content="row.loginAddress" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
              <div class="log-address">{{ row.loginAddress }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <!-- 状态列 -->
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <div class="log-status" :class="row.status === 0 ? 'status-success' : 'status-failed'">
              {{ row.statusDesc }}
            </div>
          </template>
        </el-table-column>
        <!-- 登录时间列 -->
        <el-table-column prop="loginTime" label="登录时间" sortable width="180" />
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList :data="paginatedLogList" :selectedItems="selectedLogs" showSelection showMeta :hasDeleteAction="true" @select="handleMobileSelect" @delete="handleDelete">
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-meta">
            <span class="meta-item">
              <span class="label">用户:</span>
              <span class="value">{{ item.username || '未知' }} (ID: {{ item.userId || '-' }})</span>
            </span>
          </div>
          <div class="mobile-ip" v-if="item.loginIp">IP: {{ item.loginIp }}</div>
          <div class="login-type" :class="getLoginTypeClass(item.loginType)">{{ item.loginTypeDesc }}</div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Delete } from '@element-plus/icons-vue'
import { getLoginLogList, searchLoginLog, deleteLoginLogs } from '@/api/loginLog'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import CommonSelect from '@/components/search/CommonSelect.vue'

// 登录日志列表数据
const logList = ref([])
const paginatedLogList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  userId: null,
  loginType: '',
  status: '',
  loginTimeStart: null,
  loginTimeEnd: null,
})

// 选中的日志
const selectedLogs = ref([])

// 批量操作加载状态
const batchDeleteLoading = ref(false)

// 获取登录方式样式类
const getLoginTypeClass = (loginType) => {
  if (loginType === 0) return 'type-email'
  if (loginType === 1) return 'type-gitee'
  if (loginType === 2) return 'type-github'
  if (loginType === 3) return 'type-qq'
  return ''
}

// 是否有搜索条件
const hasSearchConditions = () => {
  return !!(searchForm.userId || searchForm.loginType !== '' || searchForm.status !== '' || searchForm.loginTimeStart || searchForm.loginTimeEnd)
}

// 构建搜索参数
const buildSearchPayload = () => {
  const searchData = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  }

  if (searchForm.userId) {
    searchData.userId = Number(searchForm.userId)
  }
  if (searchForm.loginType !== '' && searchForm.loginType !== null && searchForm.loginType !== undefined) {
    searchData.loginType = searchForm.loginType
  }
  if (searchForm.status !== '' && searchForm.status !== null && searchForm.status !== undefined) {
    searchData.status = searchForm.status
  }
  if (searchForm.loginTimeStart) {
    searchData.loginTimeStart = searchForm.loginTimeStart
  }
  if (searchForm.loginTimeEnd) {
    searchData.loginTimeEnd = searchForm.loginTimeEnd
  }

  return searchData
}

// 应用分页响应
const applyPageData = (pageData) => {
  logList.value = pageData?.data || []
  paginatedLogList.value = logList.value
  total.value = Number(pageData?.total || 0)
  selectedLogs.value = []
}

// 获取登录日志列表
const fetchLoginLogs = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await searchLoginLog(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await getLoginLogList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索失败' : '获取登录日志列表失败')
  } finally {
    loading.value = false
  }
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchForm.loginTimeStart = startTime
  searchForm.loginTimeEnd = endTime
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchLoginLogs()
}

// 重置处理
const handleReset = () => {
  searchForm.userId = null
  searchForm.loginType = ''
  searchForm.status = ''
  searchForm.loginTimeStart = null
  searchForm.loginTimeEnd = null
  handleSearch()
}

// 登录方式选项
const loginTypeOptions = [
  { label: '用户名/邮箱', value: 0 },
  { label: 'Gitee', value: 1 },
  { label: 'GitHub', value: 2 },
  { label: 'QQ', value: 3 },
]

// 登录状态选项
const loginStatusOptions = [
  { label: '成功', value: 0 },
  { label: '失败', value: 1 },
]

// 表格多选
const handleSelectionChange = (logs) => {
  selectedLogs.value = logs
}

// 移动端选择处理
const handleMobileSelect = (log) => {
  const index = selectedLogs.value.findIndex((item) => item.id === log.id)
  if (index > -1) {
    selectedLogs.value.splice(index, 1)
  } else {
    selectedLogs.value.push(log)
  }
}

// 智能刷新列表
const refreshLogList = async (deletedCount = 0) => {
  if (deletedCount > 0 && currentPage.value > 1 && logList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchLoginLogs()
}

// 删除单个日志
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该登录日志吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteLoginLogs([id])
        ElMessage.success('删除成功')
        await refreshLogList(1)
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedLogs.value.length} 条登录日志吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const logIds = selectedLogs.value.map((log) => log.id)
        await deleteLoginLogs(logIds)
        ElMessage.success('批量删除成功')
        await refreshLogList(logIds.length)
      } catch {
        ElMessage.error('批量删除失败')
      } finally {
        batchDeleteLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 初始化
onMounted(() => {
  fetchLoginLogs()
})
</script>

<style lang="scss" scoped>
// 用户名样式
.log-username {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  font-size: 12px;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 登录方式样式
.login-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.type-email {
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }

  &.type-gitee {
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }

  &.type-github {
    background-color: var(--el-fill-color);
    color: var(--text-regular);
  }

  &.type-qq {
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }
}

// IP地址样式
.log-ip {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  font-size: 12px;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 登录地址样式
.log-address {
  overflow: hidden;
  cursor: pointer;
  display: -webkit-box;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 状态样式
.log-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.status-success {
    background-color: var(--el-color-success-light-9);
    color: var(--el-color-success);
  }

  &.status-failed {
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }
}

// 表格操作按钮组
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;

  .delete-button {
    border-radius: 6px;
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
    border-color: var(--el-color-danger-light-9);

    &:hover {
      background-color: var(--el-color-danger-light-8);
      border-color: var(--el-color-danger-light-8);
    }
  }
}

// 移动端元信息
.mobile-meta {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);
  margin: 4px 0;

  .meta-item {
    .label {
      font-weight: 500;
      color: var(--text-secondary);
    }
  }
}

.mobile-ip {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 4px;
}
</style>
