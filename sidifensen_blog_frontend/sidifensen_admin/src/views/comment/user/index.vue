<template>
  <div class="management-container">
    <!-- 用户列表视图 -->
    <div v-if="!showComments" class="card">
      <div class="card-header">
        <h2 class="card-title">用户评论管理</h2>
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
                  <div class="comment-count">
                    <span class="comment-count-label">评论数量:</span>
                    <span class="comment-count-value">{{ user.commentCount || 0 }}</span>
                  </div>
                </div>
                <div class="user-actions">
                  <el-button type="primary" size="default" @click="handleViewUserComments(user)" :icon="ChatDotRound" class="view-comments-btn"> 查看评论 </el-button>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>

    <!-- 用户评论列表视图 -->
    <ManagementCard
      v-else
      :title="(currentUser?.nickname || currentUser?.username) + '的评论'"
      :showTimeFilter="true"
      :showPagination="true"
      :modelCurrentPage="currentPage"
      :modelPageSize="pageSize"
      :total="total"
      @search="fetchUserCommentsData"
      @timeChange="handleTimeChange"
    >
      <!-- 筛选器 -->
      <template #filters>
        <el-button @click="handleBackToUsers" :icon="ArrowLeft" size="small" plain>返回用户列表</el-button>
        <ExamineStatusSelect v-model="searchExamineStatus" @change="handleSearch" />
        <KeywordSearch v-model="searchKeyword" placeholder="搜索评论内容" label="" width="160px" :debounce="0" :prefixIcon="Search" @search="handleSearch" />
        <SearchButtons @search="handleSearch" @reset="handleReset" />
      </template>

      <!-- 批量操作按钮 -->
      <template #batch-actions>
        <BatchActions :selectedCount="selectedComments.length" :showBatchDelete="true" @batchDelete="handleBatchDelete" />
      </template>

      <!-- 桌面端表格视图 -->
      <template #table-view>
        <DataTable
          v-loading="loading"
          :data="paginatedCommentList"
          :show-selection="true"
          :show-id="true"
          :show-actions="true"
          :has-detail-action="true"
          :has-delete-action="true"
          :actions-width="200"
          @selection-change="handleSelectionChange"
          @detail="handleViewComment"
          @delete="handleDeleteComment"
        >
          <!-- 评论内容列 -->
          <el-table-column prop="content" label="评论内容" min-width="250">
            <template #default="{ row }">
              <el-tooltip :content="row.content" placement="top-start">
                <div class="comment-content">{{ row.content }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <!-- 所属文章列 -->
          <el-table-column prop="articleTitle" label="所属文章" min-width="150">
            <template #default="{ row }">
              <el-tooltip :content="row.articleTitle" placement="top-start">
                <div class="article-title">{{ row.articleTitle || '未知文章' }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <!-- 回复对象列 -->
          <el-table-column prop="replyUserNickname" label="回复对象" width="100">
            <template #default="{ row }">
              <span v-if="row.replyUserNickname">{{ row.replyUserNickname }}</span>
              <span v-else class="no-reply">无</span>
            </template>
          </el-table-column>
          <!-- 审核状态列 -->
          <el-table-column prop="examineStatus" label="审核状态" width="80">
            <template #default="{ row }">
              <StatusBadge :status="row.examineStatus" :statusMap="examineStatusMap" />
            </template>
          </el-table-column>
          <!-- 点赞量列 -->
          <el-table-column prop="likeCount" label="点赞量" width="70" />
          <!-- 回复数列 -->
          <el-table-column prop="replyCount" label="回复数" width="70" />
          <!-- 创建时间列 -->
          <el-table-column prop="createTime" label="创建时间" sortable width="110" />
        </DataTable>
      </template>

      <!-- 移动端卡片视图 -->
      <template #card-view>
        <MobileCardList
          :data="paginatedCommentList"
          :selectedItems="selectedComments"
          showSelection
          showMeta
          :hasDetailAction="true"
          :hasDeleteAction="true"
          @select="handleMobileSelect"
          @detail="handleViewComment"
          @delete="handleDeleteComment"
        >
          <!-- 自定义卡片内容 -->
          <template #custom="{ item }">
            <div class="mobile-meta">
              <span class="article-label">文章:</span>
              <span class="article-name">{{ item.articleTitle || '未知文章' }}</span>
            </div>
            <div class="mobile-meta">
              <StatusBadge :status="item.examineStatus" :statusMap="examineStatusMap" />
              <span v-if="item.replyUserNickname" class="reply-to">回复: {{ item.replyUserNickname }}</span>
            </div>
            <div class="mobile-stats">
              <span><svg-icon name="like" width="12px" height="12px" /> {{ item.likeCount || 0 }}</span>
              <span
                ><el-icon><ChatDotRound /></el-icon> {{ item.replyCount || 0 }}</span
              >
            </div>
            <div class="mobile-time">创建: {{ item.createTime }}</div>
          </template>
        </MobileCardList>
      </template>
    </ManagementCard>

    <!-- 评论详情对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="70%"
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

              <div class="comment-user-section">
                <el-avatar :src="currentComment?.avatar" :size="40">
                  <template #default>
                    <el-icon><User /></el-icon>
                  </template>
                </el-avatar>
                <div class="user-info-detail">
                  <span class="user-name-detail">{{ currentComment?.nickname || '匿名用户' }}</span>
                  <span class="comment-time-detail">{{ currentComment?.createTime || '未知时间' }}</span>
                </div>
              </div>

              <div class="comment-content-detail">
                <h4>评论内容</h4>
                <div class="content-text">{{ currentComment?.content || '暂无内容' }}</div>
              </div>

              <div class="comment-article-detail" v-if="currentComment?.articleTitle">
                <h4>所属文章</h4>
                <div class="article-info">{{ currentComment.articleTitle }}</div>
              </div>

              <div class="comment-reply-detail" v-if="currentComment?.replyUserNickname">
                <h4>回复对象</h4>
                <div class="reply-info">{{ currentComment.replyUserNickname }}</div>
              </div>

              <!-- 状态信息 -->
              <div class="comment-badges-detail">
                <div class="badge-group">
                  <span class="badge-label">审核状态:</span>
                  <StatusBadge :status="currentComment?.examineStatus || 0" :statusMap="examineStatusMap" />
                </div>
              </div>
            </div>
          </div>

          <!-- 底部：统计数据 -->
          <div class="comment-stats-detail">
            <div class="stats-group">
              <div class="stat-item">
                <svg-icon name="like" width="16px" height="16px" color="var(--text-muted)" />
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
                <span class="stat-label">创建时间:</span>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Delete, Close, Check, View, Search, ArrowLeft, User, ChatDotRound, Star, Clock } from '@element-plus/icons-vue'
import { getUserListWithCommentCount } from '@/api/comment'
import { adminGetCommentsByUserId, adminSearchComment, adminExamineComment, adminExamineBatchComment, adminDeleteComment, adminDeleteBatchComment } from '@/api/comment'
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import StatusBadge from '@/components/common/StatusBadge.vue'

// 视图状态
const showComments = ref(false)
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

// 评论列表数据
const commentList = ref([])
const paginatedCommentList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('评论详情')
const currentComment = ref(null)
const detailLoading = ref(false)

// 搜索条件
const searchExamineStatus = ref('')
const searchKeyword = ref('')
const searchStartTime = ref('')
const searchEndTime = ref('')

// 选中的评论
const selectedComments = ref([])

// 批量操作加载状态
const batchAuditLoading = ref(false)
const batchRejectLoading = ref(false)
const batchDeleteLoading = ref(false)

// 获取用户列表
const getUsers = async () => {
  userLoading.value = true
  try {
    const res = await getUserListWithCommentCount()
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

// 查看用户评论
const handleViewUserComments = async (user) => {
  currentUser.value = user
  showComments.value = true
  currentPage.value = 1
  await fetchUserCommentsData()
}

// 返回用户列表
const handleBackToUsers = () => {
  showComments.value = false
  currentUser.value = null
  commentList.value = []
  paginatedCommentList.value = []
  currentPage.value = 1
  total.value = 0
  // 重置搜索条件
  searchExamineStatus.value = ''
  searchKeyword.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  selectedComments.value = []
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
  commentList.value = pageData?.data || []
  paginatedCommentList.value = commentList.value
  total.value = Number(pageData?.total || 0)
  selectedComments.value = []
}

const fetchUserCommentsData = async () => {
  if (!currentUser.value) return
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await adminSearchComment(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await adminGetCommentsByUserId(currentUser.value.id, {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索评论失败' : '获取用户评论列表失败')
  } finally {
    loading.value = false
  }
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchUserCommentsData()
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
const refreshCommentList = async (deletedCount = 0) => {
  if (!currentUser.value) return
  if (deletedCount > 0 && currentPage.value > 1 && commentList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchUserCommentsData()
}

// 表格多选
const handleSelectionChange = (comments) => {
  selectedComments.value = comments
}

// 检查评论是否被选中
const isCommentSelected = (commentId) => {
  return selectedComments.value.some((comment) => comment.id === commentId)
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

// 对话框关闭处理
const handleDialogClose = () => {
  currentComment.value = null
  detailLoading.value = false
}

// 查看评论详情
const handleViewComment = (comment) => {
  currentComment.value = comment
  dialogTitle.value = '评论详情'
  dialogVisible.value = true
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
    box-shadow: 0 4px 20px var(--shadow-card);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px var(--shadow-hover);
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
          background-color: var(--admin-warning);
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
          box-shadow: 0 8px 25px var(--shadow-hover);
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
                color: var(--text-secondary);
                margin-bottom: 4px;
              }

              .user-name {
                font-size: 16px;
                font-weight: 500;
                color: var(--text-dark);
                word-break: break-all;
                line-height: 1.4;
                margin-bottom: 6px;

                .username {
                  color: var(--text-link);
                  font-weight: 600;
                  display: block;
                  margin-bottom: 2px;
                }

                .nickname {
                  color: var(--text-secondary);
                  font-weight: 400;
                  font-size: 14px;
                  display: block;
                }
              }

              .comment-count {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 13px;

                .comment-count-label {
                  color: var(--text-light);
                  font-weight: 500;
                }

                .comment-count-value {
                  background: var(--admin-warning);
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

              .view-comments-btn {
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

// 评论内容样式
.comment-content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    color: var(--el-color-primary);
  }
}

.article-title {
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;

  &:hover {
    color: var(--el-color-primary);
  }
}

.no-reply {
  color: var(--text-muted);
  font-size: 12px;
}

// 移动端元信息
.mobile-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: var(--text-muted);
  margin: 4px 0;

  .article-label {
    font-weight: 500;
    color: var(--text-regular);
  }

  .article-name {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    flex: 1;
  }

  .reply-to {
    color: var(--text-muted);
  }
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

// 评论详情对话框
:deep(.comment-detail-dialog) {
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
            font-size: 20px;
            font-weight: 700;
            color: var(--text-primary);
            line-height: 1.3;
            flex: 1;
          }

          .comment-id-detail {
            background: var(--admin-warning);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            white-space: nowrap;
          }
        }

        .comment-user-section {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;
          padding: 12px;
          background-color: var(--el-border-color-light);
          border-radius: 8px;

          .user-info-detail {
            display: flex;
            flex-direction: column;

            .user-name-detail {
              font-weight: 600;
              color: var(--el-text-color-regular);
              font-size: 16px;
            }

            .comment-time-detail {
              font-size: 12px;
              color: var(--el-text-color-secondary);
              margin-top: 2px;
            }
          }
        }

        .comment-content-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary);
          }

          .content-text {
            padding: 12px;
            background-color: var(--bg-content);
            border-radius: 8px;
            line-height: 1.6;
            color: var(--text-regular);
            border-left: 4px solid var(--admin-warning);
          }
        }

        .comment-article-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary);
          }

          .article-info {
            padding: 8px 12px;
            background-color: var(--bg-blue-light);
            border-radius: 6px;
            color: var(--text-article-link);
            font-weight: 500;
          }
        }

        .comment-reply-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: var(--text-primary);
          }

          .reply-info {
            padding: 8px 12px;
            background-color: var(--bg-purple-light);
            border-radius: 6px;
            color: var(--text-reply);
            font-weight: 500;
          }
        }

        .comment-badges-detail {
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
              color: var(--text-badge);
              min-width: 80px;
            }
          }
        }
      }
    }

    .comment-stats-detail {
      border-top: 1px solid var(--border-lighter);
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
          background: var(--bg-card);
          border-radius: 12px;
          border: 1px solid var(--border-light);
          transition: all 0.3s ease;
          flex: 1;
          min-width: 160px;
          max-width: 200px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px var(--shadow-card);
          }

          .stat-icon {
            font-size: 16px;
            color: var(--text-muted);
          }

          .stat-label {
            font-size: 12px;
            color: var(--text-muted);
            font-weight: 500;
          }

          .stat-value {
            font-size: 14px;
            font-weight: 700;
            color: var(--text-primary);
            margin-left: auto;
          }

          &.time-stat-item {
            background: var(--badge-warning-bg);
            border-color: var(--admin-warning);

            .stat-icon {
              color: var(--admin-warning);
            }

            .stat-label {
              color: var(--admin-warning);
              font-weight: 600;
            }

            .stat-value {
              color: var(--admin-warning);
              font-size: 12px;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
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
      box-shadow: 0 4px 12px var(--shadow-hover);
    }
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
}
</style>
