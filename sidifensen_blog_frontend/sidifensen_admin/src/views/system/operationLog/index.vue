<template>
  <ManagementCard
    title="操作日志管理"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchOperationLogs"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <el-select v-model="searchForm.operatorRole" placeholder="操作角色" filterable clearable size="small" style="width: 140px" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="管理员" value="admin" />
        <el-option label="查看者" value="viewer" />
      </el-select>
      <el-select v-model="searchForm.status" placeholder="操作状态" filterable clearable size="small" style="width: 120px" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="成功" :value="0" />
        <el-option label="失败" :value="1" />
        <el-option label="异常" :value="2" />
      </el-select>
      <el-select v-model="searchForm.operation" placeholder="操作类型" filterable clearable size="small" style="width: 120px" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="获取" value="获取" />
        <el-option label="新增" value="新增" />
        <el-option label="修改" value="修改" />
        <el-option label="删除" value="删除" />
        <el-option label="查询" value="查询" />
        <el-option label="搜索" value="搜索" />
        <el-option label="审核" value="审核" />
        <el-option label="分配" value="分配" />
        <el-option label="其他" value="其他" />
      </el-select>
      <el-select v-model="searchForm.requestMethod" placeholder="请求方式" filterable clearable size="small" style="width: 120px" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="GET" value="GET" />
        <el-option label="POST" value="POST" />
        <el-option label="PUT" value="PUT" />
        <el-option label="DELETE" value="DELETE" />
      </el-select>
      <el-input v-model.number="searchForm.operatorId" placeholder="操作人员 ID" :prefix-icon="Search" size="small" style="width: 140px" clearable @input="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions :selectedCount="selectedLogs.length" :showBatchDelete="true" @batchDelete="handleBatchDelete" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <el-table v-loading="loading" :data="logList" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
        <el-table-column type="selection" width="30" />
        <el-table-column prop="id" label="ID" width="55" />
        <el-table-column prop="operatorName" label="操作人员" width="120">
          <template #default="{ row }">
            <span>{{ row.operatorName || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="operatorRole" label="角色" width="80">
          <template #default="{ row }">
            <div class="role-type" :class="getRoleTypeClass(row.operatorRole)">
              {{ row.operatorRole === 'admin' ? '管理员' : '查看者' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="功能模块" min-width="100">
          <template #default="{ row }">
            <el-tooltip v-if="row.module" :content="row.module" placement="top-start">
              <div class="log-module">{{ row.module }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="操作描述" min-width="150">
          <template #default="{ row }">
            <el-tooltip v-if="row.description" :content="row.description" placement="top-start">
              <div class="log-description">{{ row.description }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="operation" label="操作类型" width="80">
          <template #default="{ row }">
            <div class="operation-type" :class="getOperationTypeClass(row.operation)">
              {{ row.operation }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="requestMethod" label="请求方式" width="80">
          <template #default="{ row }">
            <span class="request-method">{{ row.requestMethod || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="requestUrl" label="请求 URL" min-width="150">
          <template #default="{ row }">
            <el-tooltip v-if="row.requestUrl" :content="row.requestUrl" placement="top-start">
              <div class="request-url">{{ row.requestUrl }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="ip" label="操作 IP" width="110">
          <template #default="{ row }">
            <span>{{ row.ip || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="address" label="操作地址" min-width="100">
          <template #default="{ row }">
            <el-tooltip v-if="row.address" :content="row.address" placement="top-start">
              <span>{{ row.address }}</span>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="75">
          <template #default="{ row }">
            <div class="log-status" :class="getStatusClass(row.status)">
              {{ getStatusText(row.status) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="time" label="耗时(ms)" width="107" sortable>
          <template #default="{ row }">
            <span :class="getTimeClass(row.time)">{{ row.time }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="操作时间" sortable width="110" />
        <el-table-column label="操作" width="170">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="info" size="small" @click="handleViewDetail(row.id)" :icon="InfoFilled" class="detail-button">详情</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row.id)" :icon="Delete" class="delete-button">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList :data="logList" :selectedItems="selectedLogs" showSelection :hasDeleteAction="true" @select="handleMobileSelect" @delete="handleDelete">
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-meta">
            <span class="meta-item">
              <span class="label">操作人员:</span>
              <span class="value">{{ item.operatorName || '未知' }}</span>
            </span>
            <div class="role-type" :class="getRoleTypeClass(item.operatorRole)">
              {{ item.operatorRole === 'admin' ? '管理员' : '查看者' }}
            </div>
            <div class="log-status" :class="getStatusClass(item.status)">
              {{ getStatusText(item.status) }}
            </div>
          </div>
          <div class="mobile-info-row" v-if="item.module">模块: {{ item.module }}</div>
          <div class="mobile-info-row">操作: {{ item.operation }}</div>
          <div class="mobile-info-row">
            <span class="request-method-badge" :class="'method-' + item.requestMethod?.toLowerCase()">{{ item.requestMethod }}</span>
            {{ item.requestUrl }}
          </div>
          <div class="mobile-time">耗时: {{ item.time }}ms | {{ item.createTime }}</div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 详情弹窗 -->
  <el-dialog v-model="detailDialogVisible" title="操作日志详情" width="600px" class="detail-dialog" destroy-on-close>
    <div v-if="detailData" class="detail-content">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="操作 ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="操作人员">{{ detailData.operatorName || '-' }} (ID: {{ detailData.operatorId }})</el-descriptions-item>
        <el-descriptions-item label="操作角色">
          <el-tag :type="detailData.operatorRole === 'admin' ? 'danger' : 'info'" size="small">
            {{ detailData.operatorRole === 'admin' ? '管理员' : '查看者' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="功能模块">{{ detailData.module || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ detailData.description || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(detailData.operation)" size="small">{{ detailData.operation }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求方式">
          <span class="request-method">{{ detailData.requestMethod || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="请求 URL">{{ detailData.requestUrl || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作 IP">{{ detailData.ip || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作地址">{{ detailData.address || '-' }}</el-descriptions-item>
        <el-descriptions-item label="操作状态">
          <el-tag :type="getStatusTag(detailData.status)" size="small">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="耗时 (ms)">{{ detailData.time }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item v-if="detailData.requestParam" label="请求参数">
          <div class="json-content">
            <pre>{{ formatJson(detailData.requestParam) }}</pre>
          </div>
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.responseResult" label="返回结果">
          <div class="json-content">
            <pre>{{ formatJson(detailData.responseResult) }}</pre>
          </div>
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.exception" label="异常信息">
          <div class="exception-content">{{ detailData.exception }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </div>
    <template #footer>
      <el-button @click="detailDialogVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Delete, InfoFilled } from '@element-plus/icons-vue'
import { getOperationLogList, searchOperationLog, deleteOperationLogs, getOperationLogDetail } from '@/api/operationLog'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 操作日志列表数据
const logList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索表单
const searchForm = reactive({
  operatorId: null,
  operatorRole: '',
  operation: '',
  requestMethod: '',
  status: '',
  createTimeStart: null,
  createTimeEnd: null,
})

// 选中的日志
const selectedLogs = ref([])

// 批量操作加载状态
const batchDeleteLoading = ref(false)

// 详情弹窗
const detailDialogVisible = ref(false)
const detailData = ref(null)

// 获取角色类型样式类
const getRoleTypeClass = (role) => {
  if (role === 'admin') return 'role-admin'
  if (role === 'viewer') return 'role-viewer'
  return ''
}

// 获取操作类型样式类
const getOperationTypeClass = (operation) => {
  if (operation === '新增') return 'operation-add'
  if (operation === '修改') return 'operation-edit'
  if (operation === '删除') return 'operation-delete'
  if (operation === '查询') return 'operation-search'
  return ''
}

// 获取操作类型标签颜色
const getOperationTypeTag = (operation) => {
  if (operation === '新增') return 'success'
  if (operation === '修改') return 'warning'
  if (operation === '删除') return 'danger'
  if (operation === '查询') return 'info'
  return ''
}

// 获取状态样式类
const getStatusClass = (status) => {
  if (status === 0) return 'status-success'
  if (status === 1) return 'status-failed'
  if (status === 2) return 'status-error'
  return ''
}

// 获取状态标签颜色
const getStatusTag = (status) => {
  if (status === 0) return 'success'
  if (status === 1) return 'danger'
  if (status === 2) return 'warning'
  return ''
}

// 获取状态文本
const getStatusText = (status) => {
  if (status === 0) return '成功'
  if (status === 1) return '失败'
  if (status === 2) return '异常'
  return '未知'
}

// 获取耗时样式类
const getTimeClass = (time) => {
  if (time < 100) return 'time-fast'
  if (time < 500) return 'time-normal'
  return 'time-slow'
}

// 格式化 JSON
const formatJson = (json) => {
  try {
    if (typeof json === 'string') {
      return JSON.stringify(JSON.parse(json), null, 2)
    }
    return JSON.stringify(json, null, 2)
  } catch {
    return json
  }
}

// 是否有搜索条件
const hasSearchConditions = () => {
  return !!(searchForm.operatorId || searchForm.operatorRole || searchForm.operation || searchForm.requestMethod || searchForm.status !== '' || searchForm.createTimeStart || searchForm.createTimeEnd)
}

// 构建搜索参数
const buildSearchPayload = () => {
  const searchData = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  }

  if (searchForm.operatorId) {
    searchData.operatorId = Number(searchForm.operatorId)
  }
  if (searchForm.operatorRole) {
    searchData.operatorRole = searchForm.operatorRole
  }
  if (searchForm.operation) {
    searchData.operation = searchForm.operation
  }
  if (searchForm.requestMethod) {
    searchData.requestMethod = searchForm.requestMethod
  }
  if (searchForm.status !== '' && searchForm.status !== null && searchForm.status !== undefined) {
    searchData.status = searchForm.status
  }
  if (searchForm.createTimeStart) {
    searchData.createTimeStart = searchForm.createTimeStart
  }
  if (searchForm.createTimeEnd) {
    searchData.createTimeEnd = searchForm.createTimeEnd
  }

  return searchData
}

// 应用分页响应
const applyPageData = (pageData) => {
  logList.value = pageData?.data || []
  total.value = Number(pageData?.total || 0)
  selectedLogs.value = []
}

// 获取操作日志列表
const fetchOperationLogs = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await searchOperationLog(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await getOperationLogList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索失败' : '获取操作日志列表失败')
  } finally {
    loading.value = false
  }
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchForm.createTimeStart = startTime
  searchForm.createTimeEnd = endTime
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchOperationLogs()
}

// 重置处理
const handleReset = () => {
  searchForm.operatorId = null
  searchForm.operatorRole = ''
  searchForm.operation = ''
  searchForm.requestMethod = ''
  searchForm.status = ''
  searchForm.createTimeStart = null
  searchForm.createTimeEnd = null
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
  await fetchOperationLogs()
}

// 查看详情
const handleViewDetail = async (id) => {
  try {
    const res = await getOperationLogDetail(id)
    detailData.value = res.data
    detailDialogVisible.value = true
  } catch {
    ElMessage.error('获取详情失败')
  }
}

// 删除单个日志
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该操作日志吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteOperationLogs([id])
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedLogs.value.length} 条操作日志吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const logIds = selectedLogs.value.map((log) => log.id)
        await deleteOperationLogs(logIds)
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
  fetchOperationLogs()
})
</script>

<style lang="scss" scoped>
// 功能模块样式
.log-module {
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

// 操作描述样式
.log-description {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  font-size: 12px;
  color: var(--text-regular);
  max-width: 170px;

  &:hover {
    color: var(--el-color-primary);
  }
}

// 角色类型样式
.role-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.role-admin {
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }

  &.role-viewer {
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }
}

// 操作类型样式
.operation-type {
  font-size: 12px;
  color: var(--text-regular);
}

// 请求方式样式
.request-method {
  font-family: 'Courier New', monospace;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-regular);
}

// 请求 URL 样式
.request-url {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
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

  &.status-error {
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
  }
}

// 耗时样式
.time-fast {
  color: var(--el-color-success);
  font-weight: 500;
}

.time-normal {
  color: var(--el-color-warning);
  font-weight: 500;
}

.time-slow {
  color: var(--el-color-danger);
  font-weight: 600;
}

// 表格操作按钮组
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;

  .detail-button {
    border-radius: 6px;
    background-color: var(--el-fill-color);
    color: var(--text-regular);
    border-color: var(--el-border-color);

    &:hover {
      background-color: var(--el-fill-color-dark);
      border-color: var(--el-border-color-dark);
    }
  }

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
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  margin-bottom: 4px;
}

.mobile-info-row {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 2px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mobile-time {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 4px;
}

// 请求方式徽章
.request-method-badge {
  display: inline-block;
  padding: 1px 4px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  font-family: 'Courier New', monospace;
  margin-right: 4px;

  &.method-get {
    background-color: var(--el-color-success-light-9);
    color: var(--el-color-success);
  }

  &.method-post {
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }

  &.method-put {
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
  }

  &.method-delete {
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }
}

// 详情弹窗样式
.detail-dialog {
  .detail-content {
    max-height: 70vh;
    overflow-y: auto;

    .json-content {
      background-color: var(--el-fill-color);
      padding: 8px;
      border-radius: 4px;
      max-height: 200px;
      overflow-y: auto;

      pre {
        margin: 0;
        font-family: 'Courier New', monospace;
        font-size: 12px;
        white-space: pre-wrap;
        word-wrap: break-word;
      }
    }

    .exception-content {
      background-color: var(--el-color-danger-light-9);
      color: var(--el-color-danger);
      padding: 8px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 12px;
      max-height: 200px;
      overflow-y: auto;
    }

    :deep(.el-descriptions__label) {
      font-weight: 600;
      width: 100px;
    }
  }
}
</style>
