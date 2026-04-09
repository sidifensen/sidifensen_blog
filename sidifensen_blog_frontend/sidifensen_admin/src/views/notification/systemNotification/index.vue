<template>
  <ManagementCard
    title="系统通知管理"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchMessages"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <ExamineStatusSelect v-model="searchIsRead" :options="isReadOptions" width="140px" labelText="已读状态" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions :selectedCount="selectedMessages.length" :showBatchAudit="true" :showBatchDelete="true" @batchAudit="handleBatchRead" @batchDelete="handleBatchDelete" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        :data="paginatedMessageList"
        :loading="loading"
        showSelection
        showId
        :hasEditAction="false"
        :hasDeleteAction="true"
        :hasAuditAction="true"
        actionsWidth="180"
        @selectionChange="handleSelectionChange"
        @audit="handleRead"
        @delete="handleDelete"
      >
        <!-- 发送者ID列 -->
        <el-table-column prop="senderId" label="发送者ID" width="100" />

        <!-- 接收者ID列 -->
        <el-table-column prop="receiverId" label="接收者ID" width="100" />

        <!-- 消息内容列 -->
        <el-table-column prop="content" label="消息内容" min-width="300">
          <template #default="{ row }">
            <el-tooltip :content="getMessageContent(row)" placement="top-start">
              <div class="message-content">{{ getMessageContent(row) }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 状态列 -->
        <el-table-column prop="isRead" label="状态" width="80">
          <template #default="{ row }">
            <div class="message-status" :class="row.isRead === 0 ? 'status-unread' : 'status-read'">
              {{ row.isRead === 0 ? '未读' : '已读' }}
            </div>
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createTime" label="创建时间" sortable width="160" />
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedMessageList"
        :selectedItems="selectedMessages"
        showSelection
        showId
        showMeta
        :hasAuditAction="true"
        :hasDeleteAction="true"
        @select="handleMobileSelect"
        @audit="handleRead"
        @delete="handleDelete"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-meta">
            <span class="meta-item">
              <span class="label">发送者:</span>
              <span class="value">{{ item.senderId }}</span>
            </span>
            <span class="meta-item">
              <span class="label">接收者:</span>
              <span class="value">{{ item.receiverId }}</span>
            </span>
          </div>
          <div class="mobile-content">{{ getMessageContent(item) }}</div>
          <el-tag :type="item.isRead === 0 ? 'warning' : 'info'" size="small">{{ item.isRead === 0 ? '未读' : '已读' }}</el-tag>
          <el-button v-if="item.isRead === 0" type="primary" link size="small" @click.stop="handleRead(item)"> 标记已读 </el-button>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getMessagePage, readAdminMessages, deleteAdminMessages } from '@/api/message'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 系统通知列表数据
const messageList = ref([])
const paginatedMessageList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件
const searchIsRead = ref('')
const searchStartTime = ref('')
const searchEndTime = ref('')

// 状态选项
const isReadOptions = [
  { label: '未读', value: 0 },
  { label: '已读', value: 1 },
]

// 选中的消息
const selectedMessages = ref([])

// 批量操作加载状态
const batchReadLoading = ref(false)
const batchDeleteLoading = ref(false)

// 解析消息内容（兼容 JSON 和纯文本）
const getMessageContent = (row) => {
  if (!row.content) return '-'
  try {
    const parsed = JSON.parse(row.content)
    return parsed.text || row.content
  } catch {
    return row.content
  }
}

// 是否有搜索条件
const hasSearchConditions = computed(() => searchIsRead.value !== '' || searchStartTime.value || searchEndTime.value)

// 构建搜索参数
const buildSearchPayload = computed(() => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  isRead: searchIsRead.value !== '' ? searchIsRead.value : undefined,
  startTime: searchStartTime.value || undefined,
  endTime: searchEndTime.value || undefined,
}))

// 获取消息列表
const fetchMessages = async () => {
  loading.value = true
  try {
    const res = await getMessagePage(buildSearchPayload.value)
    messageList.value = res.data?.data || []
    paginatedMessageList.value = messageList.value
    total.value = Number(res.data?.total || 0)
    selectedMessages.value = []
  } catch {
    ElMessage.error(hasSearchConditions.value ? '搜索系统通知失败' : '获取系统通知列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = async () => {
  currentPage.value = 1
  await fetchMessages()
}

// 重置处理
const handleReset = () => {
  searchIsRead.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  handleSearch()
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchStartTime.value = startTime
  searchEndTime.value = endTime
  handleSearch()
}

// 表格多选
const handleSelectionChange = (messages) => {
  selectedMessages.value = messages
}

// 移动端选择处理
const handleMobileSelect = (message) => {
  const index = selectedMessages.value.findIndex((item) => item.id === message.id)
  if (index > -1) {
    selectedMessages.value.splice(index, 1)
  } else {
    selectedMessages.value.push(message)
  }
}

// 标记单条消息已读
const handleRead = async (row) => {
  if (row.isRead === 1) return
  try {
    await readAdminMessages([row.id])
    ElMessage.success('标记已读成功')
    await fetchMessages()
  } catch {
    ElMessage.error('标记已读失败')
  }
}

// 批量标记已读
const handleBatchRead = async () => {
  if (selectedMessages.value.length === 0) return
  ElMessageBox.confirm(`确定要将选中的 ${selectedMessages.value.length} 条消息标记为已读吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      batchReadLoading.value = true
      try {
        const messageIds = selectedMessages.value.filter((m) => m.isRead === 0).map((m) => m.id)
        if (messageIds.length === 0) {
          ElMessage.info('所选消息均已读')
          return
        }
        await readAdminMessages(messageIds)
        ElMessage.success('批量标记已读成功')
        await fetchMessages()
      } catch {
        ElMessage.error('批量标记已读失败')
      } finally {
        batchReadLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('操作已取消')
    })
}

// 删除单条消息
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该消息吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteAdminMessages([row.id])
        ElMessage.success('删除成功')
        await fetchMessages()
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedMessages.value.length} 条消息吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const messageIds = selectedMessages.value.map((m) => m.id)
        await deleteAdminMessages(messageIds)
        ElMessage.success('批量删除成功')
        await fetchMessages()
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
  fetchMessages()
})
</script>

<style lang="scss" scoped>
// 消息内容样式
.message-content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 消息状态样式
.message-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.status-unread {
    background-color: #fef3c7;
    color: #d97706;
  }

  &.status-read {
    background-color: #f0f0f0;
    color: #999;
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

// 移动端内容样式
.mobile-content {
  font-size: 13px;
  color: var(--text-muted);
  margin: 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

// 响应式
@media screen and (max-width: 768px) {
  .message-content {
    width: 100%;
  }
}

html.dark {
  .message-status {
    &.status-read {
      background-color: var(--el-fill-color);
      color: var(--el-text-color-secondary);
    }
  }
}
</style>
