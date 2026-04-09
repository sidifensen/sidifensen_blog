<template>
  <div class="management-container">
    <!-- 用户列表视图 -->
    <div v-if="!showColumns" class="card">
      <div class="card-header">
        <h2 class="card-title">用户专栏管理</h2>
        <div class="card-actions">
          <el-input v-model="searchUserKeyword" placeholder="搜索用户名" class="search-input" size="small" clearable @input="handleUserSearch">
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </div>
      </div>

      <!-- 用户列表卡片 -->
      <div class="user-list-container" v-loading="userLoading">
        <div v-if="filteredUserList.length === 0" class="empty-container">
          <el-empty description="暂无用户数据"></el-empty>
        </div>
        <div v-else class="user-cards">
          <el-card v-for="user in filteredUserList" :key="user.id" class="user-card" shadow="hover">
            <div class="user-card-content">
              <div class="user-avatar">
                <el-avatar :src="user.avatar" :size="60">
                  <template #default>
                    <el-icon><User /></el-icon>
                  </template>
                </el-avatar>
              </div>
              <div class="user-right-content">
                <div class="user-info">
                  <div class="user-id">用户ID: {{ user.id }}</div>
                  <div class="user-name">
                    <span class="username">{{ user.username }}</span>
                    <span v-if="user.nickname" class="nickname">({{ user.nickname }})</span>
                  </div>
                  <div class="column-count">
                    <span class="column-count-label">专栏数量:</span>
                    <span class="column-count-value">{{ user.columnCount || 0 }}</span>
                  </div>
                </div>
                <div class="user-actions">
                  <el-button type="primary" size="default" @click="handleViewUserColumns(user)" :icon="Collection" class="view-columns-btn"> 查看专栏 </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 用户专栏列表视图 -->
    <ManagementCard
      v-else
      :title="(currentUser?.nickname || currentUser?.username) + '的专栏'"
      :showTimeFilter="true"
      :showPagination="true"
      :modelCurrentPage="currentPage"
      :modelPageSize="pageSize"
      :total="total"
      @search="fetchUserColumnsData"
      @timeChange="handleTimeChange"
    >
      <!-- 筛选器 -->
      <template #filters>
        <el-button @click="handleBackToUsers" :icon="ArrowLeft" size="small" plain>返回用户列表</el-button>
        <ExamineStatusSelect v-model="searchExamineStatus" @change="handleSearch" />
        <KeywordSearch v-model="searchKeyword" placeholder="搜索专栏名称" label="" width="160px" :debounce="0" :prefixIcon="Search" @search="handleSearch" />
        <SearchButtons @search="handleSearch" @reset="handleReset" />
      </template>

      <!-- 批量操作按钮 -->
      <template #batch-actions>
        <BatchActions :selectedCount="selectedColumns.length" :showBatchDelete="true" @batchDelete="handleBatchDelete" />
      </template>

      <!-- 桌面端表格视图 -->
      <template #table-view>
        <DataTable
          v-loading="loading"
          :data="paginatedColumnList"
          :show-selection="true"
          :show-cover="true"
          :show-id="true"
          :show-actions="true"
          :has-detail-action="true"
          :has-edit-action="true"
          :has-delete-action="true"
          :actions-width="220"
          @selection-change="handleSelectionChange"
          @detail="handleViewColumn"
          @edit="handleEditColumn"
          @delete="handleDeleteColumn"
        >
          <!-- 专栏名称列 -->
          <el-table-column prop="name" label="专栏名称" min-width="180">
            <template #default="{ row }">
              <el-tooltip :content="row.name" placement="top-start">
                <div class="column-name">{{ row.name }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <!-- 专栏描述列 -->
          <el-table-column prop="description" label="专栏描述" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="row.description" placement="top-start">
                <div class="column-description">{{ row.description || '暂无描述' }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <!-- 展示状态列 -->
          <el-table-column prop="showStatus" label="展示状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.showStatus === 0 ? 'success' : 'warning'" size="small">
                {{ row.showStatus === 0 ? '公开' : '私密' }}
              </el-tag>
            </template>
          </el-table-column>
          <!-- 审核状态列 -->
          <el-table-column prop="examineStatus" label="审核状态" width="80">
            <template #default="{ row }">
              <StatusBadge :status="row.examineStatus" :statusMap="examineStatusMap" />
            </template>
          </el-table-column>
          <!-- 关注数列 -->
          <el-table-column prop="focusCount" label="关注数" width="70" />
          <!-- 文章数列 -->
          <el-table-column prop="articleCount" label="文章数" width="70" />
          <!-- 创建时间列 -->
          <el-table-column prop="createTime" label="创建时间" sortable width="110" />
        </DataTable>
      </template>

      <!-- 移动端卡片视图 -->
      <template #card-view>
        <MobileCardList
          :data="paginatedColumnList"
          :selectedItems="selectedColumns"
          showSelection
          showMeta
          :hasDetailAction="true"
          :hasEditAction="true"
          :hasDeleteAction="true"
          @select="handleMobileSelect"
          @detail="handleViewColumn"
          @edit="handleEditColumn"
          @delete="handleDeleteColumn"
        >
          <!-- 自定义卡片内容 -->
          <template #custom="{ item }">
            <div class="mobile-meta">
              <el-tag :type="item.showStatus === 0 ? 'success' : 'warning'" size="small">{{ item.showStatus === 0 ? '公开' : '私密' }}</el-tag>
              <StatusBadge :status="item.examineStatus" :statusMap="examineStatusMap" />
            </div>
            <div class="mobile-stats">
              <span
                ><el-icon><Star /></el-icon> {{ item.focusCount || 0 }}</span
              >
              <span
                ><el-icon><Document /></el-icon> {{ item.articleCount || 0 }}</span
              >
            </div>
            <div class="mobile-time">创建: {{ item.createTime }}</div>
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
                <el-image :src="currentColumn.coverUrl" class="column-cover" fit="cover">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
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
                  <el-tag :type="(currentColumn?.showStatus || 0) === 0 ? 'success' : 'warning'" size="small">
                    {{ (currentColumn?.showStatus || 0) === 0 ? '公开' : '私密' }}
                  </el-tag>
                </div>
                <div class="badge-group">
                  <span class="badge-label">审核状态:</span>
                  <StatusBadge :status="currentColumn?.examineStatus || 0" :statusMap="examineStatusMap" />
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
                <el-image :src="article.coverUrl" class="cover-img" fit="cover">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div v-else class="article-no-cover">
                <el-icon><Document /></el-icon>
              </div>
              <div class="article-info">
                <div class="article-title-detail">{{ article.title }}</div>
                <div class="article-description-detail" v-if="article.description">
                  {{ article.description }}
                </div>
                <div class="article-meta-detail">
                  <div class="meta-item">
                    <el-icon class="meta-icon"><View /></el-icon>
                    <span>{{ article.readCount || 0 }} 阅读</span>
                  </div>
                  <div class="meta-item">
                    <svg-icon name="like" width="14px" height="14px" color="#909399" />
                    <span>{{ article.likeCount || 0 }} 点赞</span>
                  </div>
                  <div class="meta-item">
                    <el-icon class="meta-icon"><ChatDotRound /></el-icon>
                    <span>{{ article.commentCount || 0 }} 评论</span>
                  </div>
                  <div class="meta-item">
                    <el-icon class="meta-icon"><Star /></el-icon>
                    <span>{{ article.collectCount || 0 }} 收藏</span>
                  </div>
                  <div class="meta-item">
                    <el-icon class="meta-icon"><Clock /></el-icon>
                    <span>{{ article.createTime }}</span>
                  </div>
                  <div class="meta-item">
                    <StatusBadge :status="article.examineStatus" :statusMap="examineStatusMap" />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 无文章提示 -->
        <div v-else-if="currentColumn && Array.isArray(currentColumn.articles) && currentColumn.articles.length === 0" class="no-articles">
          <el-empty description="该专栏暂无文章" />
        </div>

        <div v-else-if="currentColumn && !currentColumn.articles" class="no-articles">
          <el-empty description="该专栏暂无文章数据" />
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-else class="loading-container">
        <el-empty description="正在加载专栏详情..." />
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" :icon="Close">关闭</el-button>
          <el-button type="primary" @click="handleAuditColumn(currentColumn?.id)" :icon="Check" :disabled="!currentColumn || (currentColumn?.examineStatus || 0) === 1"> 审核通过 </el-button>
          <el-button type="warning" @click="handleRejectColumn(currentColumn?.id)" :icon="Close" :disabled="!currentColumn || (currentColumn?.examineStatus || 0) === 2"> 审核拒绝 </el-button>
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
          <el-button @click="handleCancelEdit" :disabled="editLoading">取消</el-button>
          <el-button type="primary" @click="handleSaveEdit" :loading="editLoading">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Delete, Close, Check, View, Search, ArrowLeft, User, Collection, Star, Clock, Document, Picture, Edit, ChatDotRound } from '@element-plus/icons-vue'
import { getUserListWithColumnCount } from '@/api/column'
import {
  adminGetColumnsByUserId,
  adminSearchColumn,
  adminExamineColumn,
  adminBatchExamineColumn,
  adminDeleteColumn,
  adminBatchDeleteColumn,
  adminUpdateColumn,
  adminGetColumnDetail,
} from '@/api/column'
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'

// 视图状态
const showColumns = ref(false)
const currentUser = ref(null)

// 用户列表数据
const userList = ref([])
const userLoading = ref(false)
const searchUserKeyword = ref('')

// 过滤后的用户列表
const filteredUserList = computed(() => {
  if (!searchUserKeyword.value) return userList.value
  const keyword = searchUserKeyword.value.toLowerCase()
  return userList.value.filter((user) => user.username.toLowerCase().includes(keyword) || user.nickname?.toLowerCase().includes(keyword))
})

// 审核状态映射
const examineStatusMap = {
  0: { text: '待审核', type: 'danger' },
  1: { text: '已审核', type: 'success' },
  2: { text: '未通过', type: 'warning' },
}

// 专栏列表数据
const columnList = ref([])
const paginatedColumnList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('专栏详情')
const currentColumn = ref(null)
const detailLoading = ref(false)

// 修改专栏相关变量
const editDialogVisible = ref(false)
const editForm = ref({
  id: null,
  name: '',
  description: '',
  showStatus: 0,
})
const editLoading = ref(false)

// 搜索条件
const searchExamineStatus = ref('')
const searchKeyword = ref('')
const searchStartTime = ref('')
const searchEndTime = ref('')

// 选中的专栏
const selectedColumns = ref([])

// 批量操作加载状态
const batchAuditLoading = ref(false)
const batchRejectLoading = ref(false)
const batchDeleteLoading = ref(false)

// 获取用户列表
const getUsers = async () => {
  userLoading.value = true
  try {
    const res = await getUserListWithColumnCount()
    userList.value = res.data
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    userLoading.value = false
  }
}

// 处理用户搜索
const handleUserSearch = () => {
  // 搜索逻辑已在computed中处理
}

// 查看用户专栏
const handleViewUserColumns = async (user) => {
  currentUser.value = user
  showColumns.value = true
  currentPage.value = 1
  await fetchUserColumnsData()
}

// 返回用户列表
const handleBackToUsers = () => {
  showColumns.value = false
  currentUser.value = null
  columnList.value = []
  paginatedColumnList.value = []
  currentPage.value = 1
  total.value = 0
  // 重置搜索条件
  searchExamineStatus.value = ''
  searchKeyword.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  selectedColumns.value = []
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchStartTime.value = startTime
  searchEndTime.value = endTime
}

const hasSearchConditions = () => !!(searchExamineStatus.value || searchKeyword.value || searchStartTime.value || searchEndTime.value)

const buildSearchPayload = () => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  userId: currentUser.value?.id,
  examineStatus: searchExamineStatus.value ? parseInt(searchExamineStatus.value, 10) : undefined,
  keyword: searchKeyword.value || undefined,
  createTimeStart: searchStartTime.value || undefined,
  createTimeEnd: searchEndTime.value || undefined,
})

const applyPageData = (pageData) => {
  columnList.value = pageData?.data || []
  paginatedColumnList.value = columnList.value
  total.value = Number(pageData?.total || 0)
  selectedColumns.value = []
}

const fetchUserColumnsData = async () => {
  if (!currentUser.value) return
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await adminSearchColumn(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await adminGetColumnsByUserId(currentUser.value.id, {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索专栏失败' : '获取用户专栏列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserColumnsData()
}

// 重置处理
const handleReset = () => {
  searchExamineStatus.value = ''
  searchKeyword.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  handleSearch()
}

// 智能刷新列表
const refreshColumnList = async (deletedCount = 0) => {
  if (!currentUser.value) return
  if (deletedCount > 0 && currentPage.value > 1 && columnList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchUserColumnsData()
}

// 表格多选
const handleSelectionChange = (columns) => {
  selectedColumns.value = columns
}

// 检查专栏是否被选中
const isColumnSelected = (columnId) => {
  return selectedColumns.value.some((column) => column.id === columnId)
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
  } catch (error) {
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
    await refreshColumnList()
  } catch (error) {
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
  try {
    await adminExamineColumn(columnId, 1)
    ElMessage.success('审核成功')
    await refreshColumnList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedColumns.value.length} 个专栏吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      batchAuditLoading.value = true
      try {
        const columnIds = selectedColumns.value.map((column) => column.id)
        await adminBatchExamineColumn(columnIds, 1)
        ElMessage.success('批量审核成功')
        await refreshColumnList()
      } catch (error) {
        ElMessage.error('批量审核失败')
      } finally {
        batchAuditLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('审核已取消')
    })
}

// 处理单个专栏拒绝
const handleRejectColumn = async (columnId) => {
  try {
    await adminExamineColumn(columnId, 2)
    ElMessage.success('拒绝成功')
    await refreshColumnList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('拒绝失败')
  }
}

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedColumns.value.length} 个专栏吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchRejectLoading.value = true
      try {
        const columnIds = selectedColumns.value.map((column) => column.id)
        await adminBatchExamineColumn(columnIds, 2)
        ElMessage.success('批量拒绝成功')
        await refreshColumnList()
      } catch (error) {
        ElMessage.error('批量拒绝失败')
      } finally {
        batchRejectLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('拒绝已取消')
    })
}

// 处理删除单个专栏
const handleDeleteColumn = (columnId) => {
  ElMessageBox.confirm('确定要删除该专栏吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeleteColumn(columnId)
        ElMessage.success('删除成功')
        await refreshColumnList()
        if (dialogVisible.value) {
          dialogVisible.value = false
        }
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 处理批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedColumns.value.length} 个专栏吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const columnIds = selectedColumns.value.map((column) => column.id)
        await adminBatchDeleteColumn(columnIds)
        ElMessage.success('批量删除成功')
        await refreshColumnList()
      } catch (error) {
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
  getUsers()
})
</script>

<style lang="scss" scoped>
// 用户列表视图 - 保留原有样式
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  .card {
    height: 100%;
    padding: 20px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 10px 0 10px;

      .card-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;

        &::before {
          content: '';
          display: inline-block;
          width: 4px;
          height: 20px;
          background-color: #e6a23c;
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;
        gap: 10px;

        .search-input {
          width: 200px;
          border-radius: 8px;
        }
      }
    }
  }

  // 用户列表
  .user-list-container {
    flex: 1;
    margin-top: 16px;
    overflow-y: auto;

    .empty-container {
      padding: 60px 20px;
      text-align: center;
    }

    .user-cards {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;
      padding: 16px;

      :deep(.el-card__body) {
        padding: 0;
      }

      .user-card {
        transition: all 0.3s ease;
        border-radius: 12px;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
        }

        .user-card-content {
          display: flex;
          gap: 16px;
          padding: 16px;
          min-height: 80px;

          .user-avatar {
            flex-shrink: 0;
            align-self: center;
          }

          .user-right-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            min-height: 60px;

            .user-info {
              .user-id {
                font-size: 12px;
                color: #909399;
                margin-bottom: 4px;
              }

              .user-name {
                font-size: 16px;
                font-weight: 500;
                color: #303133;
                word-break: break-all;
                line-height: 1.4;
                margin-bottom: 6px;

                .username {
                  color: #409eff;
                  font-weight: 600;
                  display: block;
                  margin-bottom: 2px;
                }

                .nickname {
                  color: #909399;
                  font-weight: 400;
                  font-size: 14px;
                  display: block;
                }
              }

              .column-count {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 13px;

                .column-count-label {
                  color: #606266;
                  font-weight: 500;
                }

                .column-count-value {
                  background: linear-gradient(45deg, #e6a23c, #d19d00);
                  color: white;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-weight: 600;
                  font-size: 12px;
                  min-width: 20px;
                  text-align: center;
                }
              }
            }

            .user-actions {
              align-self: center;
              margin-top: 8px;
              width: 100%;
              display: flex;
              justify-content: flex-start;

              .view-columns-btn {
                border-radius: 8px;
                font-weight: 500;
                transition: all 0.3s ease;
                white-space: nowrap;

                &:hover {
                  transform: scale(1.05);
                  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
                }
              }
            }
          }
        }
      }
    }
  }
}

// 专栏名称样式
.column-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    color: var(--el-color-primary);
  }
}

.column-description {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    color: var(--el-color-primary);
  }
}

// 移动端元信息
.mobile-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  margin: 4px 0;
}

.mobile-stats {
  display: flex;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);

  span {
    display: flex;
    align-items: center;
    gap: 2px;
  }
}

.mobile-time {
  font-size: 12px;
  color: var(--text-muted);
}

// 专栏详情对话框
:deep(.column-detail-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #e6a23c 0%, #d19d00 100%);
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

    .column-detail-header {
      display: flex;
      gap: 24px;
      align-items: flex-start;

      .column-detail-info {
        flex: 1;

        .column-title-section {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;

          .column-title-detail {
            margin: 0;
            font-size: 20px;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1.3;
            flex: 1;
          }

          .column-id-detail {
            background: linear-gradient(45deg, #e6a23c, #d19d00);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            white-space: nowrap;
          }
        }

        .column-cover-section {
          margin-bottom: 16px;

          .column-cover {
            width: 200px;
            height: 120px;
            border-radius: 8px;
            border: 1px solid var(--el-border-color);

            .loading-text {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              font-size: 12px;
              color: var(--el-text-color-regular);
              background-color: var(--el-bg-color-page);
            }

            .error {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background-color: var(--el-bg-color-page);

              .el-icon {
                font-size: 24px;
                color: var(--el-text-color-placeholder);
              }
            }
          }
        }

        .column-name-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }

          .name-text {
            padding: 12px;
            background-color: var(--el-bg-color-page);
            border-radius: 8px;
            line-height: 1.6;
            color: var(--el-text-color-primary);
            border-left: 4px solid #e6a23c;
            font-weight: 600;
            font-size: 16px;
          }
        }

        .column-description-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }

          .description-text {
            padding: 12px;
            background-color: var(--el-bg-color-page);
            border-radius: 8px;
            line-height: 1.6;
            color: var(--el-text-color-primary);
            border-left: 4px solid #e6a23c;
          }
        }

        .column-badges-detail {
          display: flex;
          flex-direction: column;
          gap: 12px;
          margin-bottom: 20px;

          .badge-group {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;

            .badge-label {
              font-size: 13px;
              font-weight: 600;
              color: #495057;
              min-width: 80px;
            }
          }
        }
      }
    }

    .column-stats-detail {
      border-top: 1px solid #e9ecef;
      padding-top: 20px;
      margin-top: 20px;

      .stats-group {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        justify-content: space-evenly;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 10px 16px;
          background: linear-gradient(135deg, #f8f9fa, #e9ecef);
          border-radius: 12px;
          border: 1px solid #dee2e6;
          transition: all 0.3s ease;
          flex: 1;
          min-width: 180px;
          max-width: 200px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .stat-icon {
            font-size: 16px;
            color: #6c757d;
          }

          .stat-label {
            font-size: 12px;
            color: #6c757d;
            font-weight: 500;
          }

          .stat-value {
            flex: 1;
            font-size: 14px;
            font-weight: 700;
            color: #495057;
            margin-left: auto;
          }

          &.time-stat-item {
            background: linear-gradient(135deg, #fff3cd, #ffeaa7);
            border-color: #f39c12;

            .stat-icon {
              color: #e67e22;
            }

            .stat-label {
              color: #e67e22;
              font-weight: 600;
            }

            .stat-value {
              color: #d68910;
              font-size: 12px;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}

// 专栏文章列表区域
.column-articles-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;

  .articles-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
    margin: 0 0 16px 0;
    padding-bottom: 8px;
    border-bottom: 2px solid #e6a23c;

    .title-icon {
      color: #e6a23c;
      font-size: 18px;
    }
  }

  .articles-list {
    display: flex;
    flex-direction: column;
    gap: 0;
    max-height: 450px;
    overflow-y: auto;
    padding: 0;
    background: var(--el-bg-color);
    border-radius: 8px;
    border: 1px solid #e9ecef;

    .article-item {
      display: flex;
      gap: 16px;
      padding: 16px 20px;
      background-color: var(--el-bg-color);
      border-bottom: 1px solid #f0f0f0;
      transition: all 0.3s ease;
      position: relative;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background-color: var(--el-fill-color-light);
      }

      .article-index {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 28px;
        height: 28px;
        background: linear-gradient(135deg, #e6a23c, #d19d00);
        color: white;
        border-radius: 50%;
        font-size: 13px;
        font-weight: 700;
        flex-shrink: 0;
        align-self: flex-start;
        margin-top: 2px;
      }

      .article-cover-mini {
        flex-shrink: 0;

        .cover-img {
          width: 48px;
          height: 36px;
          border-radius: 6px;
          border: 2px solid #f0f0f0;
          object-fit: cover;
        }
      }

      .article-no-cover {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 48px;
        height: 36px;
        background: linear-gradient(135deg, #f1f3f4, #e8eaed);
        border: 2px dashed #d0d7de;
        border-radius: 6px;
        flex-shrink: 0;

        .el-icon {
          font-size: 16px;
          color: #8c959f;
        }
      }

      .article-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 8px;
        min-width: 0;

        .article-title-detail {
          font-size: 15px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          line-height: 1.4;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .article-description-detail {
          font-size: 12px;
          color: #718096;
          line-height: 1.4;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .article-meta-detail {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          align-items: center;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 11px;
            color: #a0aec0;

            .meta-icon {
              font-size: 12px;
              color: #cbd5e0;
            }
          }
        }
      }
    }
  }
}

.no-articles {
  margin-top: 24px;
  padding: 40px 0;
  text-align: center;
  border-top: 1px solid #e9ecef;
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
  gap: 120px;
  padding: 16px 0;

  @media screen and (max-width: 768px) {
    gap: 4px;
  }

  .el-button {
    margin-left: 0px;
    padding: 10px 20px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
}

// 修改专栏对话框
:deep(.edit-column-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
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
  }
}

.edit-form {
  .el-form-item__label {
    font-weight: 600;
    color: #374151;
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .management-container .card {
    padding: 2px;

    .card-header {
      padding: 6px;

      .card-title {
        font-size: 16px;
      }

      .card-actions .search-input {
        width: 140px;
      }
    }
  }

  .user-list-container .user-cards {
    grid-template-columns: 1fr;
    gap: 10px;
    padding: 10px;
    max-width: 400px;
    margin: 0 auto;
  }

  .column-detail {
    .column-info-section {
      .column-detail-header {
        flex-direction: column;
        gap: 16px;
      }

      .column-stats-detail .stats-group {
        .stat-item {
          min-width: auto;
          max-width: none;
        }
      }
    }
  }
}
</style>
