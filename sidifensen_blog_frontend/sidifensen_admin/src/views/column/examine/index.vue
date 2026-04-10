<template>
  <ManagementCard
    title="专栏审核"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchColumns"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <ExamineStatusSelect v-model="searchExamineStatus" width="140px" />
      <UserSearchSelect v-model="searchUserId" width="180px" />
      <KeywordSearch v-model="searchKeyword" showLabel label="关键词" placeholder="搜索专栏名称或描述" auto-width />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions
        :selectedCount="selectedColumns.length"
        :showBatchAudit="true"
        :showBatchReject="true"
        :showBatchDelete="true"
        @batchAudit="handleBatchAudit"
        @batchReject="handleBatchReject"
        @batchDelete="handleBatchDelete"
      />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        :data="paginatedColumnList"
        :loading="loading"
        showSelection
        showCover
        showId
        showName
        showUser
        showExamineStatus
        showCreateTime
        coverWidth="120"
        nameMinWidth="200"
        userWidth="120"
        examineStatusWidth="80"
        createTimeWidth="110"
        :hasEditAction="true"
        :hasDeleteAction="true"
        :hasAuditAction="true"
        :hasRejectAction="true"
        actionsWidth="320"
        @selectionChange="handleSelectionChange"
        @edit="handleEditColumn"
        @audit="handleAuditColumn"
        @reject="handleRejectColumn"
        @delete="handleDeleteColumn"
        @view="handleViewColumn"
      >
        <!-- 描述列 -->
        <el-table-column prop="description" label="专栏描述" min-width="250">
          <template #default="{ row }">
            <el-tooltip :content="row.description" placement="top-start">
              <div class="column-description">{{ row.description || '暂无描述' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 展示状态列 -->
        <el-table-column prop="showStatus" label="展示状态" width="80">
          <template #default="{ row }">
            <div class="column-status" :class="row.showStatus === 0 ? 'status-public' : 'status-private'">
              {{ row.showStatus === 0 ? '公开' : '私密' }}
            </div>
          </template>
        </el-table-column>

        <!-- 关注数列 -->
        <el-table-column prop="focusCount" label="关注数" width="80" />

        <!-- 文章数列 -->
        <el-table-column prop="articleCount" label="文章数" width="80" />
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedColumnList"
        :selectedItems="selectedColumns"
        showSelection
        showCover
        showId
        showName
        showMeta
        :hasEditAction="true"
        :hasDeleteAction="true"
        :hasAuditAction="true"
        :hasRejectAction="true"
        @select="handleMobileSelect"
        @edit="handleEditColumn"
        @audit="handleAuditColumn"
        @reject="handleRejectColumn"
        @delete="handleDeleteColumn"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-description">{{ item.description || '暂无描述' }}</div>
          <el-tag :type="item.showStatus === 0 ? 'success' : 'warning'" size="small">
            {{ item.showStatus === 0 ? '公开' : '私密' }}
          </el-tag>
          <div class="mobile-stats">
            <span>关注: {{ item.focusCount || 0 }}</span>
            <span>文章: {{ item.articleCount || 0 }}</span>
          </div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 专栏详情对话框 -->
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="70%"
    class="column-detail-dialog"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    draggable
    align-center
    @close="handleDialogClose"
  >
    <div v-if="currentColumn" class="column-detail" v-loading="detailLoading">
      <!-- 专栏基本信息 -->
      <div class="column-info-section">
        <div class="column-detail-header">
          <!-- 左侧：专栏信息 -->
          <div class="column-detail-info">
            <div class="column-title-section">
              <h2 class="column-title-detail">专栏详情</h2>
              <div class="column-id-detail">#{{ currentColumn?.id || 'N/A' }}</div>
            </div>

            <!-- 专栏封面 -->
            <div class="column-cover-section" v-if="currentColumn?.coverUrl">
              <el-image :src="currentColumn.coverUrl" class="column-cover" fit="cover" />
            </div>

            <div class="column-name-detail">
              <h4>专栏名称</h4>
              <div class="name-text">{{ currentColumn?.name || '暂无名称' }}</div>
            </div>

            <div class="column-description-detail" v-if="currentColumn?.description">
              <h4>专栏描述</h4>
              <div class="description-text">{{ currentColumn.description }}</div>
            </div>

            <!-- 状态信息 -->
            <div class="column-badges-detail">
              <div class="badge-group">
                <span class="badge-label">展示状态:</span>
                <div class="column-status" :class="(currentColumn?.showStatus || 0) === 0 ? 'status-public' : 'status-private'">
                  {{ (currentColumn?.showStatus || 0) === 0 ? '公开' : '私密' }}
                </div>
              </div>
              <div class="badge-group">
                <span class="badge-label">审核状态:</span>
                <div class="column-status" :class="(currentColumn?.examineStatus || 0) === 0 ? 'status-unaudited' : (currentColumn?.examineStatus || 0) === 1 ? 'status-audited' : 'status-rejected'">
                  {{ (currentColumn?.examineStatus || 0) === 0 ? '待审核' : (currentColumn?.examineStatus || 0) === 1 ? '已审核' : '未通过' }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 底部：统计数据 -->
        <div class="column-stats-detail">
          <div class="stats-group">
            <div class="stat-item">
              <el-icon class="stat-icon"><Star /></el-icon>
              <span class="stat-label">关注</span>
              <span class="stat-value">{{ currentColumn?.focusCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><Document /></el-icon>
              <span class="stat-label">文章</span>
              <span class="stat-value">{{ currentColumn?.articleCount || 0 }}</span>
            </div>
            <div class="stat-item time-stat-item">
              <el-icon class="stat-icon"><Clock /></el-icon>
              <span class="stat-label">创建时间:</span>
              <span class="stat-value">{{ currentColumn?.createTime || '未知' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 专栏文章列表 -->
      <div class="column-articles-section" v-if="currentColumn?.articles && currentColumn.articles.length > 0">
        <h3 class="articles-title">
          <el-icon class="title-icon"><Document /></el-icon>
          专栏文章列表 ({{ currentColumn.articles.length }} 篇)
        </h3>
        <div class="articles-list">
          <div v-for="(article, index) in currentColumn.articles" :key="article.id" class="article-item">
            <div class="article-index">{{ index + 1 }}</div>
            <div class="article-cover-mini" v-if="article.coverUrl">
              <el-image :src="article.coverUrl" class="cover-img" fit="cover" />
            </div>
            <div v-else class="article-no-cover">
              <el-icon><Document /></el-icon>
            </div>
            <div class="article-info">
              <div class="article-title-detail">{{ article.title }}</div>
              <div class="article-meta-detail">
                <div class="meta-item">
                  <span>{{ article.readCount || 0 }} 阅读</span>
                </div>
                <div class="meta-item">
                  <span>{{ article.likeCount || 0 }} 点赞</span>
                </div>
                <div class="meta-item">
                  <span>{{ article.createTime }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-else-if="currentColumn && Array.isArray(currentColumn.articles) && currentColumn.articles.length === 0" class="no-articles">
        <el-empty description="该专栏暂无文章" />
      </div>
    </div>

    <div v-else class="loading-container">
      <el-empty description="正在加载专栏详情..." />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button type="primary" @click="handleAuditColumn(currentColumn?.id)" :disabled="!currentColumn || (currentColumn?.examineStatus || 0) === 1"> 审核通过 </el-button>
        <el-button type="warning" @click="handleRejectColumn(currentColumn?.id)" :disabled="!currentColumn || (currentColumn?.examineStatus || 0) === 2"> 审核拒绝 </el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 修改专栏对话框 -->
  <el-dialog
    v-model="editDialogVisible"
    title="修改专栏"
    width="50%"
    class="edit-column-dialog"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    draggable
    align-center
    @close="handleCancelEdit"
  >
    <el-form :model="editForm" label-width="80px" class="edit-form">
      <el-form-item label="专栏名称" required>
        <el-input v-model="editForm.name" placeholder="请输入专栏名称" maxlength="30" show-word-limit clearable />
      </el-form-item>
      <el-form-item label="专栏描述">
        <el-input v-model="editForm.description" type="textarea" placeholder="请输入专栏描述" :rows="4" maxlength="200" show-word-limit />
      </el-form-item>
      <el-form-item label="展示状态">
        <el-radio-group v-model="editForm.showStatus">
          <el-radio :value="0">公开</el-radio>
          <el-radio :value="1">私密</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleCancelEdit">取消</el-button>
        <el-button type="primary" @click="handleSaveEdit" :loading="editLoading">保存</el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Star, Document, Clock } from '@element-plus/icons-vue'
import { adminGetColumnList, adminSearchColumn, adminExamineColumn, adminBatchExamineColumn, adminDeleteColumn, adminBatchDeleteColumn, adminUpdateColumn, adminGetColumnDetail } from '@/api/column'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import UserSearchSelect from '@/components/search/UserSearchSelect.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 专栏列表数据
const columnList = ref([])
const paginatedColumnList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('专栏详情')
const currentColumn = ref(null)
const detailLoading = ref(false)

// 搜索条件
const searchExamineStatus = ref('')
const searchKeyword = ref('')
const searchStartTime = ref('')
const searchEndTime = ref('')
const searchUserId = ref('')

// 选中的专栏
const selectedColumns = ref([])

// 修改对话框
const editDialogVisible = ref(false)
const editForm = ref({
  id: null,
  name: '',
  description: '',
  showStatus: 0,
})
const editLoading = ref(false)

// 搜索条件是否为空
const hasSearchConditions = computed(() => !!(searchExamineStatus.value || searchKeyword.value || searchStartTime.value || searchEndTime.value || searchUserId.value))

// 构建搜索参数
const buildSearchPayload = computed(() => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  userId: searchUserId.value || undefined,
  examineStatus: searchExamineStatus.value ? parseInt(searchExamineStatus.value, 10) : undefined,
  keyword: searchKeyword.value || undefined,
  createTimeStart: searchStartTime.value || undefined,
  createTimeEnd: searchEndTime.value || undefined,
}))

// 获取专栏列表
const fetchColumns = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions.value) {
      const res = await adminSearchColumn(buildSearchPayload.value)
      pageData = res.data
    } else {
      const res = await adminGetColumnList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    columnList.value = pageData?.data || []
    paginatedColumnList.value = columnList.value
    total.value = Number(pageData?.total || 0)
    selectedColumns.value = []
  } catch {
    ElMessage.error(hasSearchConditions.value ? '搜索专栏失败' : '获取专栏列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = async () => {
  currentPage.value = 1
  await fetchColumns()
}

// 重置处理
const handleReset = () => {
  searchExamineStatus.value = ''
  searchKeyword.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  searchUserId.value = ''
  handleSearch()
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchStartTime.value = startTime
  searchEndTime.value = endTime
  handleSearch()
}

// 表格多选
const handleSelectionChange = (columns) => {
  selectedColumns.value = columns
}

// 移动端选择处理
const handleMobileSelect = (column) => {
  const index = selectedColumns.value.findIndex((item) => item.id === column.id)
  if (index > -1) {
    selectedColumns.value.splice(index, 1)
  } else {
    selectedColumns.value.push(column)
  }
}

// 对话框关闭处理
const handleDialogClose = () => {
  currentColumn.value = null
  detailLoading.value = false
}

// 查看专栏详情
const handleViewColumn = async (column) => {
  currentColumn.value = column
  dialogTitle.value = '专栏详情'
  dialogVisible.value = true
  detailLoading.value = true

  try {
    const res = await adminGetColumnDetail(column.id)
    currentColumn.value = res.data
  } catch {
    ElMessage.error('获取专栏详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 修改专栏
const handleEditColumn = (column) => {
  editForm.value = {
    id: column.id,
    name: column.name,
    description: column.description || '',
    showStatus: column.showStatus,
  }
  editDialogVisible.value = true
}

// 保存专栏修改
const handleSaveEdit = async () => {
  if (!editForm.value.name.trim()) {
    ElMessage.warning('专栏名称不能为空')
    return
  }

  editLoading.value = true
  try {
    await adminUpdateColumn(editForm.value)
    ElMessage.success('修改成功')
    editDialogVisible.value = false
    await fetchColumns()
  } catch {
    ElMessage.error('修改失败')
  } finally {
    editLoading.value = false
  }
}

// 取消修改
const handleCancelEdit = () => {
  editDialogVisible.value = false
  editForm.value = {
    id: null,
    name: '',
    description: '',
    showStatus: 0,
  }
}

// 处理单个专栏审核
const handleAuditColumn = async (columnId) => {
  if (!columnId) return
  try {
    await adminExamineColumn(columnId, 1)
    ElMessage.success('审核成功')
    await fetchColumns()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch {
    ElMessage.error('审核失败')
  }
}

// 处理单个专栏拒绝
const handleRejectColumn = async (columnId) => {
  if (!columnId) return
  try {
    await adminExamineColumn(columnId, 2)
    ElMessage.success('拒绝成功')
    await fetchColumns()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch {
    ElMessage.error('拒绝失败')
  }
}

// 处理删除单个专栏
const handleDeleteColumn = (column) => {
  ElMessageBox.confirm(`确定要删除专栏【${column.name}】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeleteColumn(column.id)
        ElMessage.success('删除成功')
        await fetchColumns()
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 处理批量审核
const handleBatchAudit = () => {
  if (selectedColumns.value.length === 0) return
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedColumns.value.length} 个专栏吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      try {
        const columnIds = selectedColumns.value.map((column) => column.id)
        await adminBatchExamineColumn(columnIds, 1)
        ElMessage.success('批量审核成功')
        await fetchColumns()
      } catch {
        ElMessage.error('批量审核失败')
      }
    })
    .catch(() => {
      ElMessage.info('审核已取消')
    })
}

// 处理批量拒绝
const handleBatchReject = () => {
  if (selectedColumns.value.length === 0) return
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedColumns.value.length} 个专栏吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const columnIds = selectedColumns.value.map((column) => column.id)
        await adminBatchExamineColumn(columnIds, 2)
        ElMessage.success('批量拒绝成功')
        await fetchColumns()
      } catch {
        ElMessage.error('批量拒绝失败')
      }
    })
    .catch(() => {
      ElMessage.info('拒绝已取消')
    })
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedColumns.value.length === 0) return
  ElMessageBox.confirm(`确定要删除选中的 ${selectedColumns.value.length} 个专栏吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const columnIds = selectedColumns.value.map((column) => column.id)
        await adminBatchDeleteColumn(columnIds)
        ElMessage.success('批量删除成功')
        await fetchColumns()
      } catch {
        ElMessage.error('批量删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 初始化
onMounted(() => {
  fetchColumns()
})
</script>

<style lang="scss" scoped>
// 专栏描述样式
.column-description {
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

// 专栏状态样式
.column-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.status-unaudited {
    background-color: var(--badge-unaudited-bg);
    color: var(--badge-unaudited-color);
  }

  &.status-audited {
    background-color: var(--badge-success-bg);
    color: var(--badge-success-color);
  }

  &.status-rejected {
    background-color: var(--badge-warning-bg);
    color: var(--badge-warning-color);
  }

  &.status-public {
    background-color: var(--badge-success-bg);
    color: var(--badge-success-color);
  }

  &.status-private {
    background-color: var(--badge-warning-bg);
    color: var(--badge-warning-color);
  }
}

// 移动端描述样式
.mobile-description {
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

// 移动端统计样式
.mobile-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

// 专栏详情对话框样式
:deep(.column-detail-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: var(--admin-warning);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }
  }

  .el-dialog__body {
    padding: 24px;
    max-height: 80vh;
    overflow-y: auto;
  }
}

.column-detail {
  .column-info-section {
    margin-bottom: 24px;

    .column-title-section {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 16px;

      .column-title-detail {
        margin: 0;
        font-size: 20px;
        font-weight: 700;
        color: var(--text-primary);
      }

      .column-id-detail {
        background: var(--admin-warning);
        color: white;
        padding: 4px 12px;
        border-radius: 20px;
        font-size: 12px;
        font-weight: 600;
      }
    }

    .column-cover-section {
      margin-bottom: 16px;

      .column-cover {
        width: 200px;
        height: 120px;
        border-radius: 8px;
        border: 1px solid var(--el-border-color);
      }
    }

    .column-name-detail,
    .column-description-detail {
      margin-bottom: 16px;

      h4 {
        margin: 0 0 8px 0;
        font-size: 14px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .name-text,
      .description-text {
        padding: 12px;
        background-color: var(--el-fill-color-light);
        border-radius: 8px;
        line-height: 1.6;
        color: var(--text-regular);
      }

      .name-text {
        border-left: 4px solid var(--admin-warning);
        font-weight: 600;
        font-size: 16px;
      }
    }

    .column-badges-detail {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .badge-group {
        display: flex;
        align-items: center;
        gap: 8px;

        .badge-label {
          font-size: 13px;
          font-weight: 600;
          color: var(--text-secondary);
          min-width: 80px;
        }
      }
    }
  }

  .column-stats-detail {
    border-top: 1px solid var(--el-border-color);
    padding-top: 20px;
    margin-top: 20px;

    .stats-group {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;

      .stat-item {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 10px 16px;
        background: var(--el-fill-color-light);
        border-radius: 12px;
        border: 1px solid var(--el-border-color);
        flex: 1;
        min-width: 150px;

        .stat-icon {
          font-size: 16px;
          color: var(--text-muted);
        }

        .stat-label {
          font-size: 12px;
          color: var(--text-muted);
        }

        .stat-value {
          font-size: 14px;
          font-weight: 700;
          color: var(--text-primary);
          margin-left: auto;
        }

        &.time-stat-item {
          background: var(--time-stat-bg);
          border-color: var(--time-stat-border);

          .stat-icon {
            color: var(--time-stat-icon);
          }

          .stat-label {
            color: var(--time-stat-label);
          }

          .stat-value {
            color: var(--time-stat-value);
          }
        }
      }
    }
  }
}

// 专栏文章列表
.column-articles-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid var(--el-border-color);

  .articles-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin: 0 0 16px 0;

    .title-icon {
      color: var(--admin-warning);
    }
  }

  .articles-list {
    max-height: 400px;
    overflow-y: auto;
    display: flex;
    flex-direction: column;
    gap: 12px;

    .article-item {
      display: flex;
      gap: 12px;
      padding: 12px;
      background: var(--el-fill-color-light);
      border-radius: 8px;

      .article-index {
        width: 24px;
        height: 24px;
        background: var(--admin-warning);
        color: white;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        font-weight: 600;
        flex-shrink: 0;
      }

      .article-cover-mini {
        width: 48px;
        height: 36px;
        border-radius: 4px;
        overflow: hidden;
        flex-shrink: 0;

        .cover-img {
          width: 100%;
          height: 100%;
        }
      }

      .article-no-cover {
        width: 48px;
        height: 36px;
        background: var(--el-fill-color);
        border-radius: 4px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;
      }

      .article-info {
        flex: 1;
        min-width: 0;

        .article-title-detail {
          font-size: 14px;
          font-weight: 600;
          color: var(--text-primary);
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .article-meta-detail {
          display: flex;
          gap: 12px;
          font-size: 11px;
          color: var(--text-muted);
        }
      }
    }
  }
}

.no-articles {
  padding: 40px 0;
  text-align: center;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 60px;
}

// 修改专栏对话框
:deep(.edit-column-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: var(--admin-success);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }
  }
}

.edit-form {
  .el-form-item__label {
    font-weight: 600;
    color: var(--text-primary);
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .column-description {
    width: 100%;
  }

  .dialog-footer {
    gap: 8px;
  }
}
</style>
