<template>
  <ManagementCard
    title="文章审核"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchArticles"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <ExamineStatusSelect v-model="searchExamineStatus" width="140px" />
      <UserSearchSelect v-model="searchUserId" width="180px" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions
        :selectedCount="selectedArticles.length"
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
      <el-table v-loading="loading" :data="paginatedArticleList" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
        <el-table-column type="selection" width="30" />
        <el-table-column prop="coverUrl" label="封面" width="120">
          <template #default="{ row }">
            <div class="article-cover-container">
              <el-image v-if="row.coverUrl" :src="row.coverUrl" class="article-cover" :preview-src-list="[row.coverUrl]" fit="cover" preview-teleported />
              <div v-else class="no-cover">暂无封面</div>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="文章标题" min-width="180">
          <template #default="{ row }">
            <el-tooltip :content="row.title" placement="top-start">
              <div class="article-title">{{ row.title }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="作者昵称" width="120" />
        <el-table-column prop="tag" label="标签" width="150">
          <template #default="{ row }">
            <el-tag v-if="row.tag" size="small" type="info" class="tag-wrap">
              {{ row.tag }}
            </el-tag>
            <span v-else class="no-tag">无标签</span>
          </template>
        </el-table-column>
        <el-table-column prop="reprintType" label="类型" width="60">
          <template #default="{ row }">
            <el-tag :type="row.reprintType === 0 ? 'success' : 'warning'" size="small">
              {{ row.reprintType === 0 ? '原创' : '转载' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visibleRange" label="可见范围" width="80">
          <template #default="{ row }">
            <el-tag :type="getVisibleRangeType(row.visibleRange)" size="small">
              {{ getVisibleRangeText(row.visibleRange) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="editStatus" label="编辑状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getEditStatusType(row.editStatus)" size="small">
              {{ getEditStatusText(row.editStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="examineStatus" label="审核状态" width="80">
          <template #default="{ row }">
            <div class="article-status" :class="row.examineStatus === 0 ? 'status-unaudited' : row.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
              {{ row.examineStatus === 0 ? '待审核' : row.examineStatus === 1 ? '已审核' : '未通过' }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="readCount" label="阅读量" width="80" />
        <el-table-column prop="likeCount" label="点赞量" width="80" />
        <el-table-column prop="commentCount" label="评论数" width="80" />
        <el-table-column prop="collectCount" label="收藏量" width="80" />
        <el-table-column prop="createTime" label="创建时间" sortable width="110" />
        <el-table-column prop="updateTime" label="更新时间" sortable width="110" />
        <el-table-column label="操作" width="320">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="info" @click="handleViewArticle(row.id)" :icon="View" class="view-button" size="small">查看</el-button>
              <el-button type="primary" @click="handleAuditArticle(row.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
              <el-button type="warning" @click="handleRejectArticle(row.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
              <el-button type="danger" @click="handleDeleteArticle(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedArticleList"
        :selectedItems="selectedArticles"
        showSelection
        :hasAuditAction="true"
        :hasRejectAction="true"
        :hasDeleteAction="true"
        @select="handleMobileSelect"
        @audit="handleAuditArticle"
        @reject="handleRejectArticle"
        @delete="handleDeleteArticle"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-cover" v-if="item.coverUrl">
            <el-image :src="item.coverUrl" style="width: 100%; height: 120px" :preview-src-list="[item.coverUrl]" fit="cover" preview-teleported />
          </div>
          <div class="mobile-cover-placeholder" v-else>暂无封面</div>
          <div class="mobile-info">
            <span class="article-status" :class="item.examineStatus === 0 ? 'status-unaudited' : item.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
              {{ item.examineStatus === 0 ? '待审核' : item.examineStatus === 1 ? '已审核' : '未通过' }}
            </span>
            <span class="mobile-time">{{ item.createTime }}</span>
          </div>
          <div class="mobile-title">{{ item.title }}</div>
          <div class="mobile-meta">作者: {{ item.nickname }}</div>
          <div class="mobile-stats">阅读:{{ item.readCount || 0 }} 点赞:{{ item.likeCount || 0 }} 评论:{{ item.commentCount || 0 }}</div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 文章详情对话框 -->
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="90%"
    class="article-detail-dialog"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    draggable
    align-center
    @close="handleDialogClose"
  >
    <div v-if="currentArticle" class="article-detail" v-loading="detailLoading">
      <!-- 文章基本信息 -->
      <div class="article-info-section">
        <div class="article-detail-header">
          <!-- 左侧：文章信息 -->
          <div class="article-detail-info">
            <div class="article-title-section">
              <h2 class="article-title-detail">{{ currentArticle?.title || '无标题' }}</h2>
              <div class="article-id-detail">#{{ currentArticle?.id || 'N/A' }}</div>
            </div>

            <div class="article-author-section">
              <el-icon class="author-icon"><User /></el-icon>
              <span class="author-name-detail">{{ currentArticle?.nickname || '未知作者' }}</span>
            </div>

            <div class="article-description-detail" v-if="currentArticle && currentArticle.description && currentArticle.description.trim()">
              <el-icon class="desc-icon"><Document /></el-icon>
              <span>{{ currentArticle.description }}</span>
            </div>
            <div v-else-if="currentArticle" class="article-description-detail no-description">
              <el-icon class="desc-icon"><Document /></el-icon>
              <span>暂无描述</span>
            </div>

            <!-- 状态标签 -->
            <div class="article-badges-detail">
              <div class="badge-group">
                <span class="badge-label">文章标签:</span>
                <el-tag v-if="currentArticle && currentArticle.tag" type="info" size="small">{{ currentArticle.tag }}</el-tag>
                <span v-else class="no-data">无标签</span>
              </div>

              <div class="badge-group">
                <span class="badge-label">所属专栏:</span>
                <template v-if="currentArticle && currentArticle.columns && currentArticle.columns.length > 0">
                  <el-tag v-for="column in currentArticle.columns" :key="column.id" type="primary" size="small" class="column-tag">
                    {{ column.name }}
                  </el-tag>
                </template>
                <span v-else class="no-data">无专栏</span>
              </div>

              <div class="badge-group">
                <span class="badge-label">文章状态:</span>
                <el-tag :type="(currentArticle?.reprintType || 0) === 0 ? 'success' : 'warning'" size="small">
                  {{ (currentArticle?.reprintType || 0) === 0 ? '原创' : '转载' }}
                </el-tag>
                <el-tag :type="getVisibleRangeType(currentArticle?.visibleRange || 0)" size="small">
                  {{ getVisibleRangeText(currentArticle?.visibleRange || 0) }}
                </el-tag>
                <el-tag :type="getEditStatusType(currentArticle?.editStatus || 0)" size="small">
                  {{ getEditStatusText(currentArticle?.editStatus || 0) }}
                </el-tag>
              </div>

              <!-- 转载链接 -->
              <div v-if="(currentArticle?.reprintType || 0) === 1 && currentArticle?.reprintUrl" class="badge-group reprint-url-group">
                <span class="badge-label">转载链接:</span>
                <a :href="currentArticle.reprintUrl" target="_blank" rel="noopener noreferrer" class="reprint-url-link">
                  {{ currentArticle.reprintUrl }}
                  <el-icon class="external-link-icon"><Top /></el-icon>
                </a>
              </div>

              <div class="badge-group">
                <span class="badge-label">审核状态:</span>
                <div
                  class="article-status"
                  :class="(currentArticle?.examineStatus || 0) === 0 ? 'status-unaudited' : (currentArticle?.examineStatus || 0) === 1 ? 'status-audited' : 'status-rejected'"
                >
                  {{ (currentArticle?.examineStatus || 0) === 0 ? '待审核' : (currentArticle?.examineStatus || 0) === 1 ? '已审核' : '未通过' }}
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：文章封面 -->
          <div class="article-cover-detail">
            <el-image
              v-if="currentArticle && currentArticle.coverUrl"
              :src="currentArticle.coverUrl"
              class="detail-cover-img"
              :preview-src-list="[currentArticle.coverUrl]"
              fit="cover"
              preview-teleported
            />
            <div v-else class="no-cover-detail">
              <el-icon class="cover-icon"><Picture /></el-icon>
              <span>暂无封面</span>
            </div>
          </div>
        </div>

        <!-- 底部：统计数据和时间信息 -->
        <div class="article-stats-detail">
          <div class="stats-group">
            <div class="stat-item">
              <el-icon class="stat-icon"><View /></el-icon>
              <span class="stat-label">阅读</span>
              <span class="stat-value">{{ currentArticle?.readCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <svg-icon name="like" width="16px" height="16px" color="#909399" />
              <span class="stat-label">点赞</span>
              <span class="stat-value">{{ currentArticle?.likeCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><ChatDotRound /></el-icon>
              <span class="stat-label">评论</span>
              <span class="stat-value">{{ currentArticle?.commentCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><Star /></el-icon>
              <span class="stat-label">收藏</span>
              <span class="stat-value">{{ currentArticle?.collectCount || 0 }}</span>
            </div>
            <div class="stat-item time-stat-item">
              <el-icon class="stat-icon"><Clock /></el-icon>
              <span class="stat-label">创建:</span>
              <span class="stat-value">{{ currentArticle?.createTime || '未知' }}</span>
            </div>
            <div class="stat-item time-stat-item">
              <el-icon class="stat-icon"><Refresh /></el-icon>
              <span class="stat-label">更新:</span>
              <span class="stat-value">{{ currentArticle?.updateTime || '未知' }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 文章内容 -->
      <div class="article-content-section">
        <div class="content-header">
          <h3>文章内容</h3>
          <el-divider />
        </div>
        <div class="article-content" v-html="currentArticle?.content || '暂无内容'"></div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-container">
      <el-empty description="正在加载文章详情..." />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false" :icon="Close">关闭</el-button>
        <el-button type="primary" @click="handleAuditArticle(currentArticle?.id)" :icon="Check" :disabled="!currentArticle || (currentArticle?.examineStatus || 0) === 1"> 审核通过 </el-button>
        <el-button type="warning" @click="handleRejectArticle(currentArticle?.id)" :icon="Close" :disabled="!currentArticle || (currentArticle?.examineStatus || 0) === 2"> 审核拒绝 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Delete, Close, Check, View, Picture, User, Document, Star, ChatDotRound, Collection, Clock, Refresh } from '@element-plus/icons-vue'
import { adminGetArticleList, adminDeleteArticle, adminDeleteBatchArticle, adminExamineArticle, adminExamineBatchArticle, adminSearchArticle, adminGetArticle } from '@/api/article'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import UserSearchSelect from '@/components/search/UserSearchSelect.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 文章列表数据
const articleList = ref([])
const paginatedArticleList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('文章详情')
const currentArticle = ref(null)
const detailLoading = ref(false)

// 搜索条件
const searchUserId = ref('')
const searchExamineStatus = ref('')
const searchCreateTimeStart = ref(null)
const searchCreateTimeEnd = ref(null)

// 选中的文章
const selectedArticles = ref([])

// 批量操作加载状态
const batchAuditLoading = ref(false)
const batchRejectLoading = ref(false)
const batchDeleteLoading = ref(false)

// 对话框关闭处理
const handleDialogClose = () => {
  currentArticle.value = null
  detailLoading.value = false
}

// 是否有搜索条件
const hasSearchConditions = () => !!(searchUserId.value || searchExamineStatus.value || searchCreateTimeStart.value || searchCreateTimeEnd.value)

// 构建搜索参数
const buildSearchPayload = () => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  userId: searchUserId.value || undefined,
  examineStatus: searchExamineStatus.value || undefined,
  createTimeStart: searchCreateTimeStart.value || undefined,
  createTimeEnd: searchCreateTimeEnd.value || undefined,
})

// 应用分页数据
const applyPageData = (pageData) => {
  articleList.value = pageData?.data || []
  paginatedArticleList.value = articleList.value
  total.value = Number(pageData?.total || 0)
  selectedArticles.value = []
}

// 获取文章列表
const fetchArticles = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await adminSearchArticle(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await adminGetArticleList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索文章失败' : '获取文章列表失败')
  } finally {
    loading.value = false
  }
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchCreateTimeStart.value = startTime
  searchCreateTimeEnd.value = endTime
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchArticles()
}

// 重置处理
const handleReset = () => {
  searchUserId.value = ''
  searchExamineStatus.value = ''
  searchCreateTimeStart.value = null
  searchCreateTimeEnd.value = null
  handleSearch()
}

// 表格多选
const handleSelectionChange = (articles) => {
  selectedArticles.value = articles
}

// 移动端选择处理
const handleMobileSelect = (article) => {
  const index = selectedArticles.value.findIndex((item) => item.id === article.id)
  if (index > -1) {
    selectedArticles.value.splice(index, 1)
  } else {
    selectedArticles.value.push(article)
  }
}

// 智能刷新列表
const refreshArticleList = async (deletedCount = 0) => {
  if (deletedCount > 0 && currentPage.value > 1 && articleList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchArticles()
}

// 查看文章详情
const handleViewArticle = async (articleId) => {
  try {
    detailLoading.value = true
    currentArticle.value = null
    dialogTitle.value = '文章详情'
    const res = await adminGetArticle(articleId)
    if (res && res.data) {
      currentArticle.value = res.data
      dialogVisible.value = true
    } else {
      throw new Error('文章数据为空或格式错误')
    }
  } catch (error) {
    ElMessage.error('获取文章详情失败: ' + (error.message || '未知错误'))
    dialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

// 处理单个文章审核
const handleAuditArticle = async (articleId) => {
  try {
    await adminExamineArticle({ articleId: articleId, examineStatus: 1 })
    ElMessage.success('审核成功')
    await refreshArticleList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedArticles.value.length} 篇文章吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      batchAuditLoading.value = true
      try {
        const data = selectedArticles.value.map((article) => ({
          articleId: article.id,
          examineStatus: 1,
        }))
        await adminExamineBatchArticle(data)
        ElMessage.success('批量审核成功')
        await refreshArticleList()
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

// 处理单个文章拒绝
const handleRejectArticle = async (articleId) => {
  try {
    await adminExamineArticle({ articleId: articleId, examineStatus: 2 })
    ElMessage.success('拒绝成功')
    await refreshArticleList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('拒绝失败')
  }
}

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedArticles.value.length} 篇文章吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchRejectLoading.value = true
      try {
        const data = selectedArticles.value.map((article) => ({
          articleId: article.id,
          examineStatus: 2,
        }))
        await adminExamineBatchArticle(data)
        ElMessage.success('批量拒绝成功')
        await refreshArticleList()
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

// 处理删除单个文章
const handleDeleteArticle = (articleId) => {
  ElMessageBox.confirm('确定要删除该文章吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeleteArticle(articleId)
        ElMessage.success('删除成功')
        await refreshArticleList()
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedArticles.value.length} 篇文章吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const articleIds = selectedArticles.value.map((article) => article.id)
        await adminDeleteBatchArticle(articleIds)
        ElMessage.success('批量删除成功')
        await refreshArticleList()
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

// 获取可见范围文本
const getVisibleRangeText = (visibleRange) => {
  const texts = {
    0: '全部可见',
    1: '仅我可见',
    2: '粉丝可见',
    3: 'VIP可见',
  }
  return texts[visibleRange] || '未知'
}

// 获取可见范围标签类型
const getVisibleRangeType = (visibleRange) => {
  const types = {
    0: 'success',
    1: 'info',
    2: 'warning',
    3: 'danger',
  }
  return types[visibleRange] || 'info'
}

// 获取编辑状态文本
const getEditStatusText = (editStatus) => {
  const texts = {
    0: '已发布',
    1: '草稿箱',
    2: '回收站',
  }
  return texts[editStatus] || '未知'
}

// 获取编辑状态标签类型
const getEditStatusType = (editStatus) => {
  const types = {
    0: 'success',
    1: 'warning',
    2: 'danger',
  }
  return types[editStatus] || 'info'
}

// 初始化
onMounted(() => {
  fetchArticles()
})
</script>

<style lang="scss" scoped>
// 文章封面容器样式
.article-cover-container {
  display: flex;
  justify-content: center;
  align-items: center;

  .article-cover {
    width: 100px;
    height: 60px;
    border-radius: 6px;
    cursor: pointer;
  }

  .no-cover {
    width: 100px;
    height: 60px;
    background-color: var(--el-fill-color);
    border: 1px dashed var(--el-border-color);
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 12px;
    color: var(--el-text-color-placeholder);
  }
}

// 文章标题样式
.article-title {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 无标签样式
.no-tag {
  color: var(--el-text-color-placeholder);
  font-size: 12px;
}

// 标签样式
.tag-wrap {
  white-space: normal;
  word-break: break-all;
  line-height: 1.4;
  max-width: 180px;
  display: inline-block;
  height: auto;
}

// 审核状态样式
.article-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.status-unaudited {
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }

  &.status-audited {
    background-color: var(--el-color-success-light-9);
    color: var(--el-color-success);
  }

  &.status-rejected {
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
  }
}

// 表格操作按钮组
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;

  .view-button {
    border-radius: 6px;
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    border-color: var(--el-color-primary-light-9);

    &:hover {
      background-color: var(--el-color-primary-light-8);
      border-color: var(--el-color-primary-light-8);
    }
  }

  .examine-button {
    border-radius: 6px;
    background-color: var(--el-color-success-light-9);
    color: var(--el-color-success);
    border-color: var(--el-color-success-light-9);

    &:hover {
      background-color: var(--el-color-success-light-8);
      border-color: var(--el-color-success-light-8);
    }
  }

  .reject-button {
    border-radius: 6px;
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
    border-color: var(--el-color-warning-light-9);

    &:hover {
      background-color: var(--el-color-warning-light-8);
      border-color: var(--el-color-warning-light-8);
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

// 移动端封面
.mobile-cover {
  width: 100%;
  height: 120px;
  border-radius: 6px;
  overflow: hidden;
  margin-bottom: 8px;
}

.mobile-cover-placeholder {
  width: 100%;
  height: 120px;
  background-color: var(--el-fill-color);
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  color: var(--el-text-color-placeholder);
  margin-bottom: 8px;
}

.mobile-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.mobile-time {
  font-size: 11px;
  color: var(--text-muted);
}

.mobile-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mobile-meta {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.mobile-stats {
  font-size: 11px;
  color: var(--text-muted);
}

// ===== 文章详情对话框样式 ===== //
:deep(.article-detail-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    position: relative;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      position: absolute;
      top: 50%;
      right: 20px;
      transform: translateY(-50%);
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;

      .el-dialog__close {
        color: white;
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
    max-height: 80vh;
    overflow-y: auto;
  }
}

// 文章详情内容
.article-detail {
  .article-info-section {
    margin-bottom: 24px;

    .article-detail-header {
      display: flex;
      gap: 24px;
      align-items: flex-start;

      .article-detail-info {
        flex: 1;

        .article-title-section {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;

          .article-title-detail {
            margin: 0;
            font-size: 24px;
            font-weight: 700;
            color: var(--text-primary);
            line-height: 1.3;
            flex: 1;
          }

          .article-id-detail {
            background: linear-gradient(45deg, #667eea, #764ba2);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
          }
        }

        .article-author-section {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;
          padding: 8px 12px;
          background-color: var(--el-fill-color);
          border-radius: 8px;

          .author-icon {
            color: var(--el-text-color-secondary);
          }

          .author-name-detail {
            font-weight: 600;
            color: var(--text-regular);
          }
        }

        .article-description-detail {
          display: flex;
          align-items: flex-start;
          gap: 8px;
          font-size: 14px;
          color: var(--el-text-color-secondary);
          line-height: 1.6;
          margin-bottom: 16px;
          padding: 12px;
          background-color: var(--el-color-primary-light-9);
          border-radius: 8px;
          border-left: 4px solid var(--el-color-primary);

          .desc-icon {
            color: var(--el-color-primary);
          }

          &.no-description {
            background-color: var(--el-fill-color);
            border-left-color: var(--el-border-color);

            .desc-icon {
              color: var(--el-text-color-placeholder);
            }
          }
        }

        .article-badges-detail {
          display: flex;
          flex-direction: column;
          gap: 12px;

          .badge-group {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;

            .badge-label {
              font-size: 13px;
              font-weight: 600;
              color: var(--text-regular);
              min-width: 80px;
            }

            .no-data {
              font-size: 12px;
              color: var(--el-text-color-placeholder);
            }

            .column-tag {
              margin-right: 4px;
              margin-bottom: 4px;
            }

            &.reprint-url-group {
              .reprint-url-link {
                color: var(--el-color-primary);
                text-decoration: none;
                display: flex;
                align-items: center;
                gap: 4px;
                max-width: 400px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;

                &:hover {
                  text-decoration: underline;
                }
              }
            }
          }
        }
      }

      .article-cover-detail {
        flex-shrink: 0;
        width: 640px;
        height: 360px;

        .detail-cover-img {
          width: 100%;
          height: 100%;
          border-radius: 16px;
          cursor: pointer;
        }

        .no-cover-detail {
          width: 100%;
          height: 100%;
          background: var(--el-fill-color);
          border: 2px dashed var(--el-border-color);
          border-radius: 16px;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          color: var(--el-text-color-placeholder);
          gap: 12px;
        }
      }
    }

    .article-stats-detail {
      border-top: 1px solid var(--el-border-color);
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
          background: var(--el-fill-color);
          border-radius: 12px;
          flex: 1;
          min-width: 160px;
          max-width: 200px;

          .stat-icon {
            color: var(--el-text-color-secondary);
          }

          .stat-label {
            font-size: 12px;
            color: var(--el-text-color-secondary);
          }

          .stat-value {
            font-size: 14px;
            font-weight: 700;
            color: var(--text-primary);
            margin-left: auto;
          }

          &.time-stat-item {
            background: var(--el-color-primary-light-9);

            .stat-icon {
              color: var(--el-color-primary);
            }

            .stat-label {
              color: var(--el-color-primary);
              font-weight: 600;
            }

            .stat-value {
              font-size: 12px;
              font-weight: 600;
              color: var(--el-color-primary);
            }
          }
        }
      }
    }
  }

  .article-content-section {
    .content-header {
      margin-bottom: 16px;

      h3 {
        margin: 0;
        font-size: 18px;
        font-weight: 600;
        color: var(--text-primary);
        display: flex;
        align-items: center;
        gap: 8px;

        &::before {
          content: '';
          width: 4px;
          height: 20px;
          background: linear-gradient(135deg, #667eea, #764ba2);
          border-radius: 2px;
        }
      }
    }

    .article-content {
      max-height: 69vh;
      overflow-y: auto;
      padding: 20px;
      background-color: var(--el-bg-color);
      border: 1px solid var(--el-border-color);
      border-radius: 12px;
      line-height: 1.8;
      font-size: 15px;
      color: var(--text-regular);

      :deep(img) {
        width: 100%;
        height: auto;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        margin: 8px 0;
      }

      :deep(pre) {
        background-color: var(--el-fill-color);
        padding: 16px;
        border-radius: 8px;
        overflow-x: auto;
        border-left: 4px solid var(--el-color-primary);
        margin: 12px 0;
      }

      :deep(code) {
        background-color: var(--el-fill-color);
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 14px;
        color: var(--el-color-danger);
      }

      :deep(blockquote) {
        border-left: 4px solid var(--el-color-primary);
        padding-left: 16px;
        margin: 16px 0;
        color: var(--el-text-color-secondary);
        font-style: italic;
      }

      :deep(h1, h2, h3, h4, h5, h6) {
        margin: 20px 0 12px 0;
        color: var(--text-primary);
      }

      :deep(p) {
        margin: 12px 0;
      }

      :deep(ul, ol) {
        margin: 12px 0;
        padding-left: 24px;
      }
    }
  }
}

// 加载状态容器
.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

// 对话框底部按钮区域
.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 120px;
  padding: 16px 0;

  .el-button {
    margin-left: 0px;
    padding: 10px 20px;
    font-weight: 500;
    border-radius: 8px;
  }
}

// 响应式设计
@media screen and (max-width: 768px) {
  .article-detail {
    .article-info-section {
      .article-detail-header {
        flex-direction: column;
        gap: 16px;

        .article-cover-detail {
          align-self: center;
          width: 100%;
          height: 150px;
        }
      }

      .article-stats-detail {
        .stats-group {
          flex-direction: column;

          .stat-item {
            min-width: auto;
            max-width: none;
          }
        }
      }
    }
  }

  .dialog-footer {
    gap: 4px;
  }
}
</style>
