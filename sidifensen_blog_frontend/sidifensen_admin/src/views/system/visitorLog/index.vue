<template>
  <ManagementCard
    title="访客日志管理"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchVisitorLogs"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <el-select v-model="searchForm.device" placeholder="设备类型" filterable clearable size="small" style="width: 140px" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="PC" value="PC" />
        <el-option label="Mobile" value="Mobile" />
      </el-select>
      <el-input v-model.number="searchForm.userId" placeholder="用户ID" :prefix-icon="Search" size="small" style="width: 160px" clearable @input="handleSearch" />
      <el-input v-model="searchForm.ip" placeholder="访客IP地址" :prefix-icon="Search" size="small" style="width: 160px" clearable @input="handleSearch" />
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
        <!-- 访客IP列 -->
        <el-table-column prop="ip" label="访客IP" width="150">
          <template #default="{ row }">
            <el-tooltip v-if="row.ip" :content="row.ip" placement="top-start">
              <div class="log-ip">{{ row.ip }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <!-- 地理位置列 -->
        <el-table-column prop="address" label="地理位置" min-width="150">
          <template #default="{ row }">
            <el-tooltip v-if="row.address" :content="row.address" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
              <div class="log-address">{{ row.address }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <!-- 设备类型列 -->
        <el-table-column prop="device" label="设备类型" width="100">
          <template #default="{ row }">
            <div class="device-type" :class="getDeviceTypeClass(row.device)">
              {{ row.device }}
            </div>
          </template>
        </el-table-column>
        <!-- 访问时间列 -->
        <el-table-column prop="visitTime" label="访问时间" sortable width="180" />
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
              <span class="value">{{ item.username || '游客' }} {{ item.userId ? `(ID: ${item.userId})` : '' }}</span>
            </span>
          </div>
          <div class="mobile-ip" v-if="item.ip">IP: {{ item.ip }}</div>
          <div class="device-type" :class="getDeviceTypeClass(item.device)">{{ item.device }}</div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Delete } from '@element-plus/icons-vue'
import { getVisitorLogList, searchVisitorLog, deleteVisitorLogs } from '@/api/visitorLog'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 访客日志列表数据
const logList = ref([])
const paginatedLogList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  userId: null,
  ip: '',
  device: '',
  visitTimeStart: null,
  visitTimeEnd: null,
})

// 选中的日志
const selectedLogs = ref([])

// 批量操作加载状态
const batchDeleteLoading = ref(false)

// 获取设备类型样式类
const getDeviceTypeClass = (device) => {
  if (device === 'PC') return 'device-pc'
  if (device === 'Mobile') return 'device-mobile'
  return ''
}

// 是否有搜索条件
const hasSearchConditions = () => {
  return !!(searchForm.userId || searchForm.ip || searchForm.device || searchForm.visitTimeStart || searchForm.visitTimeEnd)
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
  if (searchForm.ip) {
    searchData.ip = searchForm.ip.trim()
  }
  if (searchForm.device) {
    searchData.device = searchForm.device
  }
  if (searchForm.visitTimeStart) {
    searchData.visitTimeStart = searchForm.visitTimeStart
  }
  if (searchForm.visitTimeEnd) {
    searchData.visitTimeEnd = searchForm.visitTimeEnd
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

// 获取访客日志列表
const fetchVisitorLogs = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await searchVisitorLog(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await getVisitorLogList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索失败' : '获取访客日志列表失败')
  } finally {
    loading.value = false
  }
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchForm.visitTimeStart = startTime
  searchForm.visitTimeEnd = endTime
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchVisitorLogs()
}

// 重置处理
const handleReset = () => {
  searchForm.userId = null
  searchForm.ip = ''
  searchForm.device = ''
  searchForm.visitTimeStart = null
  searchForm.visitTimeEnd = null
  handleSearch()
}

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
  await fetchVisitorLogs()
}

// 删除单个日志
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该访客日志吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteVisitorLogs([id])
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedLogs.value.length} 条访客日志吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const logIds = selectedLogs.value.map((log) => log.id)
        await deleteVisitorLogs(logIds)
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
  fetchVisitorLogs()
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

// 游客标识
.visitor-guest {
  font-size: 12px;
  color: var(--text-muted);
  font-style: italic;
}

// 设备类型样式
.device-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.device-pc {
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }

  &.device-mobile {
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
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

// 地理位置样式
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
