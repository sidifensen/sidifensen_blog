<template>
  <ManagementCard
    title="评论审核"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchComments"
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
        :selectedCount="selectedComments.length"
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
        v-loading="loading"
        :data="paginatedCommentList"
        :show-selection="true"
        :show-id="true"
        :show-examine-status="true"
        :show-create-time="true"
        :show-actions="true"
        :has-view-action="true"
        :has-edit-action="false"
        :has-delete-action="true"
        :has-audit-action="true"
        :has-reject-action="true"
        :actions-width="280"
        @selection-change="handleSelectionChange"
        @view="handleViewComment"
        @audit="handleAuditComment"
        @reject="handleRejectComment"
        @delete="handleDeleteComment"
      >
        <!-- 评论内容列 -->
        <el-table-column prop="content" label="评论内容" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.content" placement="top-start" :popper-style="{ maxWidth: '300px', wordWrap: 'break-word', whiteSpace: 'normal' }">
              <div class="comment-content">{{ row.content }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <!-- 评论用户列 -->
        <el-table-column prop="nickname" label="评论用户" width="100" />
        <!-- 所属文章列 -->
        <el-table-column prop="articleTitle" label="所属文章" min-width="170">
          <template #default="{ row }">
            <el-tooltip :content="row.articleTitle" placement="top-start">
              <div class="article-title">{{ row.articleTitle }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <!-- 文章ID列 -->
        <el-table-column prop="articleId" label="文章ID" width="80" />
        <!-- 父评论ID列 -->
        <el-table-column prop="parentId" label="父评论ID" width="100">
          <template #default="{ row }">
            <span v-if="row.parentId">{{ row.parentId }}</span>
            <span v-else class="no-parent">主评论</span>
          </template>
        </el-table-column>
        <!-- 回复用户列 -->
        <el-table-column prop="replyUserNickname" label="回复用户" width="100">
          <template #default="{ row }">
            <span v-if="row.replyUserNickname">{{ row.replyUserNickname }}</span>
            <span v-else class="no-reply">无回复</span>
          </template>
        </el-table-column>
        <!-- 点赞量列 -->
        <el-table-column prop="likeCount" label="点赞量" width="80" />
        <!-- 回复数列 -->
        <el-table-column prop="replyCount" label="回复数" width="80" />
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedCommentList"
        :selectedItems="selectedComments"
        showSelection
        :hasAuditAction="true"
        :hasRejectAction="true"
        :hasDeleteAction="true"
        @select="handleMobileSelect"
        @audit="handleAuditComment"
        @reject="handleRejectComment"
        @delete="handleDeleteComment"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-header">
            <span class="comment-id">#{{ item.id }}</span>
            <span class="comment-status" :class="item.examineStatus === 0 ? 'status-unaudited' : item.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
              {{ item.examineStatus === 0 ? '待审核' : item.examineStatus === 1 ? '已审核' : '未通过' }}
            </span>
          </div>
          <div class="mobile-content">{{ item.content }}</div>
          <div class="mobile-meta">
            <span>用户: {{ item.nickname }}</span>
            <span v-if="item.articleTitle">文章: {{ item.articleTitle }}</span>
          </div>
          <div class="mobile-stats">点赞:{{ item.likeCount || 0 }} 回复:{{ item.replyCount || 0 }} | {{ item.createTime }}</div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 评论详情对话框 -->
  <el-dialog
    v-model="dialogVisible"
    :title="dialogTitle"
    width="90%"
    class="comment-detail-dialog"
    :close-on-click-modal="false"
    :close-on-press-escape="true"
    draggable
    align-center
    @close="handleDialogClose"
  >
    <div v-if="currentComment" class="comment-detail" v-loading="detailLoading">
      <!-- 评论基本信息 -->
      <div class="comment-info-section">
        <div class="comment-detail-header">
          <!-- 左侧：评论信息 -->
          <div class="comment-detail-info">
            <div class="comment-title-section">
              <h2 class="comment-title-detail">评论详情</h2>
              <div class="comment-id-detail">#{{ currentComment?.id || 'N/A' }}</div>
            </div>

            <div class="comment-author-section">
              <el-icon class="author-icon"><User /></el-icon>
              <span class="author-name-detail">{{ currentComment?.nickname || '未知用户' }}</span>
            </div>

            <div class="comment-content-detail">
              <el-icon class="content-icon"><ChatDotRound /></el-icon>
              <span>{{ currentComment?.content || '无内容' }}</span>
            </div>

            <!-- 状态标签 -->
            <div class="comment-badges-detail">
              <div class="badge-group">
                <span class="badge-label">评论类型:</span>
                <el-tag v-if="currentComment?.parentId" type="warning" size="small"> 回复评论 (父评论ID: {{ currentComment.parentId }}) </el-tag>
                <el-tag v-else type="success" size="small">主评论</el-tag>
              </div>

              <div class="badge-group" v-if="currentComment?.replyUserNickname">
                <span class="badge-label">回复用户:</span>
                <el-tag type="info" size="small">{{ currentComment.replyUserNickname }}</el-tag>
              </div>

              <div class="badge-group">
                <span class="badge-label">所属文章:</span>
                <el-tag type="primary" size="small">文章ID: {{ currentComment?.articleId || '未知' }}</el-tag>
              </div>

              <div class="badge-group">
                <span class="badge-label">审核状态:</span>
                <div
                  class="comment-status"
                  :class="(currentComment?.examineStatus || 0) === 0 ? 'status-unaudited' : (currentComment?.examineStatus || 0) === 1 ? 'status-audited' : 'status-rejected'"
                >
                  {{ (currentComment?.examineStatus || 0) === 0 ? '待审核' : (currentComment?.examineStatus || 0) === 1 ? '已审核' : '未通过' }}
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧：用户头像 -->
          <div class="comment-avatar-detail">
            <el-avatar v-if="currentComment && currentComment.avatar" :size="120" :src="currentComment.avatar" class="detail-avatar-img" />
            <div v-else class="no-avatar-detail">
              <el-icon class="avatar-icon"><User /></el-icon>
              <span>暂无头像</span>
            </div>
          </div>
        </div>

        <!-- 底部：统计数据和时间信息 -->
        <div class="comment-stats-detail">
          <div class="stats-group">
            <div class="stat-item">
              <svg-icon name="like" width="16px" height="16px" color="#909399" />
              <span class="stat-label">点赞</span>
              <span class="stat-value">{{ currentComment?.likeCount || 0 }}</span>
            </div>
            <div class="stat-item">
              <el-icon class="stat-icon"><ChatDotRound /></el-icon>
              <span class="stat-label">回复</span>
              <span class="stat-value">{{ currentComment?.replyCount || 0 }}</span>
            </div>
            <div class="stat-item time-stat-item">
              <el-icon class="stat-icon"><Clock /></el-icon>
              <span class="stat-label">创建:</span>
              <span class="stat-value">{{ currentComment?.createTime || '未知' }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-else class="loading-container">
      <el-empty description="正在加载评论详情..." />
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false" :icon="Close">关闭</el-button>
        <el-button type="primary" @click="handleAuditComment(currentComment?.id)" :icon="Check" :disabled="!currentComment || (currentComment?.examineStatus || 0) === 1"> 审核通过 </el-button>
        <el-button type="warning" @click="handleRejectComment(currentComment?.id)" :icon="Close" :disabled="!currentComment || (currentComment?.examineStatus || 0) === 2"> 审核拒绝 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Delete, Close, Check, View, User, ChatDotRound, Clock } from '@element-plus/icons-vue'
import { adminGetCommentList, adminDeleteComment, adminDeleteBatchComment, adminExamineComment, adminExamineBatchComment, adminSearchComment } from '@/api/comment'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import UserSearchSelect from '@/components/search/UserSearchSelect.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 评论列表数据
const commentList = ref([])
const paginatedCommentList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('评论详情')
const currentComment = ref(null)
const detailLoading = ref(false)

// 搜索条件
const searchUserId = ref('')
const searchExamineStatus = ref('')
const searchCreateTimeStart = ref(null)
const searchCreateTimeEnd = ref(null)

// 选中的评论
const selectedComments = ref([])

// 批量操作加载状态
const batchAuditLoading = ref(false)
const batchRejectLoading = ref(false)
const batchDeleteLoading = ref(false)

// 对话框关闭处理
const handleDialogClose = () => {
  currentComment.value = null
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
  commentList.value = pageData?.data || []
  paginatedCommentList.value = commentList.value
  total.value = Number(pageData?.total || 0)
  selectedComments.value = []
}

// 获取评论列表
const fetchComments = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await adminSearchComment(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await adminGetCommentList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索评论失败' : '获取评论列表失败')
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
  await fetchComments()
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
const handleSelectionChange = (comments) => {
  selectedComments.value = comments
}

// 移动端选择处理
const handleMobileSelect = (comment) => {
  const index = selectedComments.value.findIndex((item) => item.id === comment.id)
  if (index > -1) {
    selectedComments.value.splice(index, 1)
  } else {
    selectedComments.value.push(comment)
  }
}

// 智能刷新列表
const refreshCommentList = async (deletedCount = 0) => {
  if (deletedCount > 0 && currentPage.value > 1 && commentList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchComments()
}

// 查看评论详情
const handleViewComment = async (comment) => {
  try {
    detailLoading.value = true
    currentComment.value = null
    dialogTitle.value = '评论详情'
    currentComment.value = comment
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取评论详情失败')
    dialogVisible.value = false
  } finally {
    detailLoading.value = false
  }
}

// 处理单个评论审核
const handleAuditComment = async (commentId) => {
  try {
    await adminExamineComment({ commentId: commentId, examineStatus: 1 })
    ElMessage.success('审核成功')
    await refreshCommentList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('审核失败')
  }
}

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedComments.value.length} 条评论吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      batchAuditLoading.value = true
      try {
        const data = selectedComments.value.map((comment) => ({
          commentId: comment.id,
          examineStatus: 1,
        }))
        await adminExamineBatchComment(data)
        ElMessage.success('批量审核成功')
        await refreshCommentList()
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

// 处理单个评论拒绝
const handleRejectComment = async (commentId) => {
  try {
    await adminExamineComment({ commentId: commentId, examineStatus: 2 })
    ElMessage.success('拒绝成功')
    await refreshCommentList()
    if (dialogVisible.value) {
      dialogVisible.value = false
    }
  } catch (error) {
    ElMessage.error('拒绝失败')
  }
}

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedComments.value.length} 条评论吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchRejectLoading.value = true
      try {
        const data = selectedComments.value.map((comment) => ({
          commentId: comment.id,
          examineStatus: 2,
        }))
        await adminExamineBatchComment(data)
        ElMessage.success('批量拒绝成功')
        await refreshCommentList()
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

// 处理删除单个评论
const handleDeleteComment = (commentId) => {
  ElMessageBox.confirm('确定要删除该评论吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeleteComment(commentId)
        ElMessage.success('删除成功')
        await refreshCommentList()
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedComments.value.length} 条评论吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const commentIds = selectedComments.value.map((comment) => comment.id)
        await adminDeleteBatchComment(commentIds)
        ElMessage.success('批量删除成功')
        await refreshCommentList()
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
  fetchComments()
})
</script>

<style lang="scss" scoped>
// 评论内容样式
.comment-content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 文章标题样式
.article-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 无父评论和无回复状态样式
.no-parent,
.no-reply {
  color: var(--el-text-color-placeholder);
  font-size: 12px;
}

// 审核状态样式
.comment-status {
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

// 移动端卡片样式
.mobile-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 4px;
}

.mobile-content {
  font-size: 14px;
  font-weight: 500;
  color: var(--text-primary);
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.mobile-meta {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.mobile-stats {
  font-size: 11px;
  color: var(--text-muted);
}

// ===== 评论详情对话框样式 ===== //
:deep(.comment-detail-dialog) {
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

// 评论详情内容
.comment-detail {
  .comment-info-section {
    margin-bottom: 24px;

    .comment-detail-header {
      display: flex;
      gap: 24px;
      align-items: flex-start;

      .comment-detail-info {
        flex: 1;

        .comment-title-section {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;

          .comment-title-detail {
            margin: 0;
            font-size: 24px;
            font-weight: 700;
            color: var(--text-primary);
            line-height: 1.3;
            flex: 1;
          }

          .comment-id-detail {
            background: linear-gradient(45deg, #667eea, #764ba2);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
          }
        }

        .comment-author-section {
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

        .comment-content-detail {
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

          .content-icon {
            color: var(--el-color-primary);
          }
        }

        .comment-badges-detail {
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
          }
        }
      }

      .comment-avatar-detail {
        flex-shrink: 0;

        .detail-avatar-img {
          border: 4px solid rgba(255, 255, 255, 0.3);
        }

        .no-avatar-detail {
          width: 120px;
          height: 120px;
          background: var(--el-fill-color);
          border: 2px dashed var(--el-border-color);
          border-radius: 50%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 14px;
          color: var(--el-text-color-placeholder);
          gap: 8px;
        }
      }
    }

    .comment-stats-detail {
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
  .comment-detail {
    .comment-info-section {
      .comment-detail-header {
        flex-direction: column;
        gap: 16px;

        .comment-avatar-detail {
          align-self: center;

          .no-avatar-detail {
            width: 100px;
            height: 100px;
          }
        }
      }

      .comment-stats-detail {
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
