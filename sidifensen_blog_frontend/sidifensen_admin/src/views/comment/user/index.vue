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
    <div v-else class="card">
      <div class="card-header">
        <h2 class="card-title">{{ currentUser?.nickname || currentUser?.username }}的评论</h2>
        <div class="card-actions">
          <el-button @click="handleBackToUsers" :icon="ArrowLeft" size="small">返回用户列表</el-button>
          <el-select v-model="searchExamineStatus" placeholder="审核状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="审核不通过" value="2" />
            <el-option label="全部" value="" />
          </el-select>
        </div>
      </div>

      <!-- 时间筛选区域 -->
      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker
            v-model="searchStartTime"
            type="datetime"
            placeholder="开始时间"
            size="small"
            class="time-input"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            clearable
            @change="handleSearch"
          />
          <el-date-picker
            v-model="searchEndTime"
            type="datetime"
            placeholder="结束时间"
            size="small"
            class="time-input"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            clearable
            @change="handleSearch"
          />
        </div>
      </div>

      <div class="card-second">
        <el-input v-model="searchKeyword" placeholder="搜索评论内容" class="search-input" size="small" clearable @input="handleSearch">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="card-third">
        <el-button type="primary" plain round @click="handleBatchAudit" :disabled="selectedComments.length === 0" :loading="batchAuditLoading"> 批量审核 </el-button>
        <el-button type="warning" plain round @click="handleBatchReject" :disabled="selectedComments.length === 0" :loading="batchRejectLoading"> 批量拒绝 </el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedComments.length === 0" :loading="batchDeleteLoading"> 批量删除 </el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedCommentList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="content" label="评论内容" min-width="300">
            <template #default="{ row }">
              <el-tooltip :content="row.content" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }"">
                <div class="comment-content">{{ row.content }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="articleTitle" label="所属文章" min-width="170">
            <template #default="{ row }">
              <el-tooltip :content="row.articleTitle" placement="top-start" :popper-style="{ maxWidth: '300px', wordWrap: 'break-word', whiteSpace: 'normal' }"">
                <div class="article-title">{{ row.articleTitle }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="replyUserNickname" label="回复对象" width="120">
            <template #default="{ row }">
              <span v-if="row.replyUserNickname">{{ row.replyUserNickname }}</span>
              <span v-else class="no-reply">无</span>
            </template>
          </el-table-column>
          <el-table-column prop="examineStatus" label="审核状态" width="80">
            <template #default="{ row }">
              <div class="comment-status" :class="row.examineStatus === 0 ? 'status-unaudited' : row.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                {{ row.examineStatus === 0 ? "待审核" : row.examineStatus === 1 ? "已审核" : "未通过" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="likeCount" label="点赞量" width="80" />
          <el-table-column prop="replyCount" label="回复数" width="80" />
          <el-table-column prop="createTime" label="创建时间" sortable width="110" />
          <el-table-column label="操作" width="320">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="info" @click="handleViewComment(row)" :icon="View" class="view-button" size="small">查看</el-button>
                <el-button type="primary" @click="handleAuditComment(row.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectComment(row.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteComment(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="comment-cards">
          <el-card v-for="comment in paginatedCommentList" :key="comment.id" class="comment-card" :class="{ 'is-selected': isCommentSelected(comment.id) }">
            <div class="comment-card-content">
              <div class="comment-header-section">
                <div class="comment-info">
                  <div class="comment-header">
                    <el-checkbox :model-value="isCommentSelected(comment.id)" @change="handleMobileSelect(comment)" class="mobile-checkbox" />
                    <div class="comment-id">#{{ comment.id }}</div>
                    <div class="comment-status" :class="comment.examineStatus === 0 ? 'status-unaudited' : comment.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                      {{ comment.examineStatus === 0 ? "待审核" : comment.examineStatus === 1 ? "已审核" : "未通过" }}
                    </div>
                  </div>
                  <div class="comment-content-mobile">{{ comment.content }}</div>
                  
                  <!-- 评论用户信息 -->
                  <div class="comment-author-mobile" v-if="comment.nickname">
                    <span class="author-label">评论用户:</span>
                    <span class="author-name">{{ comment.nickname }}</span>
                  </div>

                  <!-- 文章信息 -->
                  <div class="comment-article-mobile" v-if="comment.articleTitle">
                    <span class="article-label">文章:</span>
                    <span class="article-name">{{ comment.articleTitle }}</span>
                  </div>

                  <!-- 其他元信息 -->
                  <div class="comment-meta-mobile">
                    <div class="meta-item" v-if="comment.articleId">
                      <span class="label">文章ID:</span>
                      <span>{{ comment.articleId }}</span>
                    </div>
                    <div class="meta-item" v-if="comment.parentId">
                      <span class="label">父评论:</span>
                      <span>#{{ comment.parentId }}</span>
                    </div>
                    <div class="meta-item" v-if="comment.replyUserNickname">
                      <span class="label">回复用户:</span>
                      <span>{{ comment.replyUserNickname }}</span>
                    </div>
                  </div>
                  <div class="comment-meta">
                    <div class="stats-row">
                      <div class="meta-item stat-item">
                        <span class="label">点赞:</span>
                        <span>{{ comment.likeCount || 0 }}</span>
                      </div>
                      <div class="meta-item stat-item">
                        <span class="label">回复:</span>
                        <span>{{ comment.replyCount || 0 }}</span>
                      </div>
                    </div>
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">创建:</span>
                        <span>{{ comment.createTime }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="comment-actions">
                <el-button type="info" @click="handleViewComment(comment)" :icon="View" class="view-button" size="small">查看</el-button>
                <el-button type="primary" @click="handleAuditComment(comment.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectComment(comment.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteComment(comment.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout="prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 评论详情对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="70%" class="comment-detail-dialog" :close-on-click-modal="false" :close-on-press-escape="true" draggable align-center @close="handleDialogClose">
      <div v-if="currentComment" class="comment-detail" v-loading="detailLoading">
        <!-- 评论基本信息 -->
        <div class="comment-info-section">
          <div class="comment-detail-header">
            <!-- 左侧：评论信息 -->
            <div class="comment-detail-info">
              <div class="comment-title-section">
                <h2 class="comment-title-detail">评论详情</h2>
                <div class="comment-id-detail">#{{ currentComment?.id || "N/A" }}</div>
              </div>

              <div class="comment-user-section">
                <el-avatar :src="currentComment?.avatar" :size="40">
                  <template #default>
                    <el-icon><User /></el-icon>
                  </template>
                </el-avatar>
                <div class="user-info-detail">
                  <span class="user-name-detail">{{ currentComment?.nickname || "匿名用户" }}</span>
                  <span class="comment-time-detail">{{ currentComment?.createTime || "未知时间" }}</span>
                </div>
              </div>

              <div class="comment-content-detail">
                <h4>评论内容</h4>
                <div class="content-text">{{ currentComment?.content || "暂无内容" }}</div>
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
                  <div class="comment-status" :class="(currentComment?.examineStatus || 0) === 0 ? 'status-unaudited' : (currentComment?.examineStatus || 0) === 1 ? 'status-audited' : 'status-rejected'">
                    {{ (currentComment?.examineStatus || 0) === 0 ? "待审核" : (currentComment?.examineStatus || 0) === 1 ? "已审核" : "未通过" }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 底部：统计数据 -->
          <div class="comment-stats-detail">
            <div class="stats-group">
              <div class="stat-item">
                <el-icon class="stat-icon"><Star /></el-icon>
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
                <span class="stat-value">{{ currentComment?.createTime || "未知" }}</span>
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
import { ref, onMounted, onUnmounted, computed } from "vue";
import { Delete, Close, Check, View, Search, ArrowLeft, User, ChatDotRound, Star, Clock } from "@element-plus/icons-vue";
import { getUserListWithCommentCount } from "@/api/comment";
import { adminGetCommentsByUserId, adminSearchComment, adminExamineComment, adminExamineBatchComment, adminDeleteComment, adminDeleteBatchComment } from "@/api/comment";

// 视图状态
const showComments = ref(false);
const currentUser = ref(null);

// 用户列表数据
const userList = ref([]);
const userLoading = ref(false);
const searchUserKeyword = ref("");

// 过滤后的用户列表
const filteredUserList = computed(() => {
  if (!searchUserKeyword.value) return userList.value;
  const keyword = searchUserKeyword.value.toLowerCase();
  return userList.value.filter((user) => user.username.toLowerCase().includes(keyword) || user.nickname?.toLowerCase().includes(keyword));
});

// 评论列表数据
const commentList = ref([]);
const paginatedCommentList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("评论详情");
const currentComment = ref(null);
const detailLoading = ref(false);

// 搜索条件
const searchExamineStatus = ref("");
const searchKeyword = ref("");
const searchStartTime = ref("");
const searchEndTime = ref("");

// 选中的评论
const selectedComments = ref([]);

// 批量操作加载状态
const batchAuditLoading = ref(false);
const batchRejectLoading = ref(false);
const batchDeleteLoading = ref(false);

// 移动端检测
const isMobileView = ref(false);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 获取用户列表
const getUsers = async () => {
  userLoading.value = true;
  try {
    const res = await getUserListWithCommentCount();
    userList.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取用户列表失败");
  } finally {
    userLoading.value = false;
  }
};

// 处理用户搜索
const handleUserSearch = () => {
  // 搜索逻辑已在computed中处理
};

// 查看用户评论
const handleViewUserComments = async (user) => {
  currentUser.value = user;
  showComments.value = true;
  await getUserComments(user.id);
};

// 返回用户列表
const handleBackToUsers = () => {
  showComments.value = false;
  currentUser.value = null;
  commentList.value = [];
  paginatedCommentList.value = [];
  // 重置搜索条件
  searchExamineStatus.value = "";
  searchKeyword.value = "";
  searchStartTime.value = "";
  searchEndTime.value = "";
  selectedComments.value = [];
};

// 获取用户评论列表
const getUserComments = async (userId) => {
  loading.value = true;
  try {
    const res = await adminGetCommentsByUserId(userId);
    commentList.value = res.data.data.sort((a, b) => b.id - a.id);
    total.value = commentList.value.length;
    currentPage.value = 1;
    updatePaginatedCommentList();
  } catch (error) {
    ElMessage.error("获取用户评论列表失败");
  } finally {
    loading.value = false;
  }
};

// 更新分页数据
const updatePaginatedCommentList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedCommentList.value = commentList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  updatePaginatedCommentList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedCommentList();
};

// 处理搜索
const handleSearch = async () => {
  if (!currentUser.value) return;

  loading.value = true;
  try {
    const res = await adminSearchComment({
      userId: currentUser.value.id,
      examineStatus: searchExamineStatus.value,
      keyword: searchKeyword.value,
      createTimeStart: searchStartTime.value,
      createTimeEnd: searchEndTime.value,
    });
    commentList.value = res.data.data;
    total.value = commentList.value.length;
    currentPage.value = 1;
    updatePaginatedCommentList();
  } catch (error) {
    ElMessage.error("搜索评论失败");
  } finally {
    loading.value = false;
  }
};

// 智能刷新列表
const refreshCommentList = async () => {
  if (!currentUser.value) return;

  // 检查是否有任何搜索条件
  const hasSearchConditions = searchExamineStatus.value || searchKeyword.value || searchStartTime.value || searchEndTime.value;

  if (hasSearchConditions) {
    await handleSearch();
  } else {
    await getUserComments(currentUser.value.id);
  }
};

// 表格多选
const handleSelectionChange = (comments) => {
  selectedComments.value = comments;
};

// 检查评论是否被选中
const isCommentSelected = (commentId) => {
  return selectedComments.value.some((comment) => comment.id === commentId);
};

// 移动端选择处理
const handleMobileSelect = (comment) => {
  const index = selectedComments.value.findIndex((item) => item.id === comment.id);
  if (index > -1) {
    // 已选中，取消选中
    selectedComments.value.splice(index, 1);
  } else {
    // 未选中，添加到选中列表
    selectedComments.value.push(comment);
  }
};

// 对话框关闭处理
const handleDialogClose = () => {
  currentComment.value = null;
  detailLoading.value = false;
};

// 查看评论详情
const handleViewComment = (comment) => {
  currentComment.value = comment;
  dialogTitle.value = "评论详情";
  dialogVisible.value = true;
};

// 处理单个评论审核
const handleAuditComment = async (commentId) => {
  try {
    await adminExamineComment({ commentId: commentId, examineStatus: 1 });
    ElMessage.success("审核成功");
    await refreshCommentList();
    if (dialogVisible.value) {
      dialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error("审核失败");
  }
};

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedComments.value.length} 条评论吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      batchAuditLoading.value = true;
      try {
        const data = selectedComments.value.map((comment) => ({
          commentId: comment.id,
          examineStatus: 1,
        }));
        await adminExamineBatchComment(data);
        ElMessage.success("批量审核成功");
        await refreshCommentList();
      } catch (error) {
        ElMessage.error("批量审核失败");
      } finally {
        batchAuditLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("审核已取消");
    });
};

// 处理单个评论拒绝
const handleRejectComment = async (commentId) => {
  try {
    await adminExamineComment({ commentId: commentId, examineStatus: 2 });
    ElMessage.success("拒绝成功");
    await refreshCommentList();
    if (dialogVisible.value) {
      dialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error("拒绝失败");
  }
};

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedComments.value.length} 条评论吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchRejectLoading.value = true;
      try {
        const data = selectedComments.value.map((comment) => ({
          commentId: comment.id,
          examineStatus: 2,
        }));
        await adminExamineBatchComment(data);
        ElMessage.success("批量拒绝成功");
        await refreshCommentList();
      } catch (error) {
        ElMessage.error("批量拒绝失败");
      } finally {
        batchRejectLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("拒绝已取消");
    });
};

// 处理删除单个评论
const handleDeleteComment = (commentId) => {
  ElMessageBox.confirm("确定要删除该评论吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await adminDeleteComment(commentId);
        ElMessage.success("删除成功");
        await refreshCommentList();
        if (dialogVisible.value) {
          dialogVisible.value = false;
        }
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 处理批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedComments.value.length} 条评论吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const commentIds = selectedComments.value.map((comment) => comment.id);
        await adminDeleteBatchComment(commentIds);
        ElMessage.success("批量删除成功");
        await refreshCommentList();
      } catch (error) {
        ElMessage.error("批量删除失败");
      } finally {
        batchDeleteLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 初始化
onMounted(() => {
  getUsers();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 用户评论管理主容器
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  // 主卡片容器 - 包含所有内容
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

    // 卡片头部 - 标题和搜索区域
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
          content: "";
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

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
              border-color: #e6a23c;
            }
          }

          :deep(.el-select__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
              border-color: #e6a23c;
            }
          }
        }
      }
    }

    // 时间筛选区域
    .card-time-filters {
      display: flex;
      justify-content: flex-end;
      padding: 10px 10px 0 10px;

      .time-filter-group {
        display: flex;
        align-items: center;
        gap: 12px;

        .time-input {
          width: 160px;
          border-radius: 8px;

          :deep(.el-date-editor.el-input) {
            .el-input__wrapper {
              border-radius: 8px;
              transition: all 0.3s ease;

              &:focus-within {
                box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
                border-color: #e6a23c;
              }
            }
          }
        }
      }
    }

    // 卡片第二行 - 关键词搜索区域
    .card-second {
      display: flex;
      justify-content: flex-end;
      padding: 5px 5px 0 5px;
      gap: 10px;

      .search-input {
        width: 240px; // 设置固定宽度
      }

      :deep(.el-input__wrapper) {
        border-radius: 8px;
        &:focus-within {
          box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
          border-color: #e6a23c;
        }
      }
    }

    // 卡片第三行 - 批量操作按钮区域
    .card-third {
      display: flex;
      justify-content: flex-end;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
    }
  }

  // 用户列表视图 - 当 showComments = false 时显示
  .user-list-container {
    flex: 1;
    margin-top: 16px;
    overflow-y: auto;

    // 空状态容器
    .empty-container {
      padding: 60px 20px;
      text-align: center;
    }

    // 用户卡片网格容器
    .user-cards {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 16px;
      padding: 16px;

      :deep(.el-card__body) {
        padding: 0;
      }

      // 单个用户卡片
      .user-card {
        transition: all 0.3s ease;
        border-radius: 12px;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
        }

        // 用户卡片内容容器
        .user-card-content {
          display: flex;
          gap: 16px;
          padding: 16px;
          min-height: 80px;

          // 用户头像区域
          .user-avatar {
            flex-shrink: 0;
            align-self: center;
          }

          // 用户信息和操作区域
          .user-right-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            min-height: 60px;

            // 用户基本信息
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

              .comment-count {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 13px;

                .comment-count-label {
                  color: #606266;
                  font-weight: 500;
                }

                .comment-count-value {
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

            // 用户操作按钮
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

  // 桌面端表格视图 - 当 isMobileView = false 时显示
  .desktop-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding-bottom: 60px; // 为分页容器预留空间

    // 评论表格
    .table {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      max-height: calc(100vh - 280px);

      // 表格头部样式
      :deep(.el-table__header-wrapper) {
        background-color: var(--el-bg-color);
        th {
          font-weight: 600;
          color: #475569;
        }
      }

      // 表格主体样式
      :deep(.el-table__body-wrapper) {
        tr td {
          color: #64748b;
          padding: 12px 0;
          vertical-align: middle;

          .cell {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            min-height: 40px;
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
          color: #e6a23c;
        }
      }

      // 文章标题样式
      .article-title {
        overflow: hidden;
        text-overflow: ellipsis;
        cursor: pointer;

        &:hover {
          color: #e6a23c;
        }
      }

      // 无回复提示
      .no-reply {
        color: #999;
        font-size: 12px;
      }

      // 表格操作按钮组
      .table-actions {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        height: 100%;
        min-height: 60px;

        // 通用按钮样式
        .view-button,
        .examine-button,
        .reject-button,
        .delete-button {
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
          }
        }

        // 查看按钮
        .view-button {
          background-color: #f0f9ff;
          color: #0369a1;
          border-color: #f0f9ff;

          &:hover {
            background-color: #dbeafe;
            border-color: #dbeafe;
            box-shadow: 0 2px 8px rgba(3, 105, 161, 0.3);
          }
        }

        // 审核按钮
        .examine-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;

          &:hover {
            background-color: #bae6fd;
            border-color: #bae6fd;
            box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
          }
        }

        // 拒绝按钮
        .reject-button {
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;

          &:hover {
            background-color: #fde68a;
            border-color: #fde68a;
            box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
          }
        }

        // 删除按钮
        .delete-button {
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;

          &:hover {
            background-color: #fecaca;
            border-color: #fecaca;
            box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
          }
        }
      }
    }
  }

  // 移动端卡片视图 - 当 isMobileView = true 时显示
  .mobile-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    padding-bottom: 60px;
    overflow-y: auto;

    // 评论卡片列表容器
    .comment-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      // 单个评论卡片
      .comment-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        // 选中状态样式
        &.is-selected {
          border: 2px solid #e6a23c;
          box-shadow: 0 0 12px rgba(230, 162, 60, 0.3);
        }

        // 评论卡片内容容器
        .comment-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 评论头部区域
          .comment-header-section {
            display: flex;
            flex-direction: column;

            // 评论信息区域
            .comment-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              // 评论头部 - ID和状态
              .comment-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-bottom: 5px;

                // 移动端复选框样式
                .mobile-checkbox {
                  flex-shrink: 0;

                  :deep(.el-checkbox__inner) {
                    width: 18px;
                    height: 18px;
                  }
                }

                .comment-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }
              }

              // 移动端评论内容
              .comment-content-mobile {
                font-size: 14px;
                font-weight: 500;
                color: #333;
                line-height: 1.4;
                margin-bottom: 8px;
                display: -webkit-box;
                -webkit-line-clamp: 3;
                line-clamp: 3;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              // 移动端评论用户信息
              .comment-author-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .author-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .author-name {
                  color: #555;
                  font-weight: 500;
                  flex: 1;
                }
              }

              // 移动端文章信息
              .comment-article-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 6px;
                display: flex;
                align-items: center;

                .article-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .article-name {
                  color: #555;
                  font-weight: 500;
                  flex: 1;
                  word-break: break-all; // 强制断词
                }
              }

              // 移动端评论元信息
              .comment-meta-mobile {
                display: flex;
                flex-direction: column;
                gap: 4px;
                margin-bottom: 8px;

                .meta-item {
                  font-size: 12px;
                  color: #666;
                  display: flex;
                  align-items: center;

                  .label {
                    font-weight: 500;
                    margin-right: 4px;
                    color: #888;
                  }
                }
              }


              // 评论元信息
              .comment-meta {
                display: flex;
                flex-direction: column;
                gap: 8px;

                .meta-item {
                  font-size: 12px;
                  color: #666;
                  display: flex;
                  align-items: center;

                  .label {
                    font-weight: 500;
                    margin-right: 4px;
                  }
                }

                // 统计数据行
                .stats-row {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 6px 8px;

                  .stat-item {
                    background-color: #f8f9fa;
                    padding: 3px 8px;
                    border-radius: 12px;
                    font-size: 11px;
                    color: #555;
                    border: 1px solid #e9ecef;
                    white-space: nowrap;

                    .label {
                      color: #666;
                      margin-right: 2px;
                    }
                  }
                }

                // 时间信息行
                .time-row {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 8px;
                  margin-top: 4px;

                  .time-item {
                    font-size: 11px;
                    color: #888;

                    .label {
                      color: #999;
                    }
                  }
                }
              }
            }
          }

          // 评论操作按钮
          .comment-actions {
            display: flex;
            gap: 4px;
            justify-content: center;
            padding-top: 8px;
            border-top: 1px solid #f0f0f0;

            .el-button {
              font-size: 12px;
              padding: 6px 10px;
              height: auto;
              border-radius: 4px;
              flex: 1;
            }
          }
        }
      }
    }
  }

  // 通用的评论状态样式
  .comment-status {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;

    &.status-unaudited {
      background-color: #fff1f0;
      color: #f56c6c;
    }

    &.status-audited {
      background-color: #f0f9eb;
      color: #67c23a;
    }

    &.status-rejected {
      background-color: #fdf6ec;
      color: #e6a23c;
    }
  }

  // 分页容器
  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding: 10px;
    background-color: var(--el-bg-color);
    border-radius: 0 0 12px 12px;
    z-index: 10;
    width: 100%;
    box-sizing: border-box;
    position: absolute;
    bottom: 0;
    left: 0;
  }
}

// 评论详情对话框样式
:deep(.comment-detail-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #e6a23c 0%, #d19d00 100%);
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
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
        transform: translateY(-50%) scale(1.1);
      }

      .el-dialog__close {
        color: white;
        font-size: 18px;
        font-weight: bold;

        &:hover {
          color: #ff6b6b;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
    max-height: 80vh;
    overflow-y: auto;
  }

  @media screen and (max-width: 767px) {
    width: 95% !important;

    .el-dialog__body {
      padding: 16px;
    }
  }
}

// 评论详情容器
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
            color: #2c3e50;
            line-height: 1.3;
            flex: 1;
          }

          .comment-id-detail {
            background: linear-gradient(45deg, #e6a23c, #d19d00);
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
            color: #2c3e50;
          }

          .content-text {
            padding: 12px;
            background-color: #f8f9fa;
            border-radius: 8px;
            line-height: 1.6;
            color: #333;
            border-left: 4px solid #e6a23c;
          }
        }

        .comment-article-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }

          .article-info {
            padding: 8px 12px;
            background-color: #e3f2fd;
            border-radius: 6px;
            color: #1976d2;
            font-weight: 500;
          }
        }

        .comment-reply-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }

          .reply-info {
            padding: 8px 12px;
            background-color: #f3e5f5;
            border-radius: 6px;
            color: #7b1fa2;
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
              color: #495057;
              min-width: 80px;
            }
          }
        }
      }
    }

    .comment-stats-detail {
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
          min-width: 160px;
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

// Tooltip 样式优化
:deep(.comment-tooltip) {
  max-width: 400px !important;
  word-wrap: break-word !important;
  white-space: normal !important;
  line-height: 1.4 !important;

  @media screen and (max-width: 768px) {
    max-width: 80vw !important;
  }

  @media screen and (max-width: 480px) {
    max-width: 90vw !important;
  }
}

:deep(.article-tooltip) {
  max-width: 300px !important;
  word-wrap: break-word !important;
  white-space: normal !important;
  line-height: 1.4 !important;

  @media screen and (max-width: 768px) {
    max-width: 70vw !important;
  }

  @media screen and (max-width: 480px) {
    max-width: 80vw !important;
  }
}

// 响应式设计
@media screen and (max-width: 1400px) {
  .management-container .card .card-header .card-actions .search-input {
    width: 140px;
  }
}

@media screen and (max-width: 1220px) {
  .management-container .card .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;

    .card-actions {
      width: 100%;
      justify-content: space-between;

      .search-input {
        width: 100%;
      }
    }
  }

  // 时间筛选区域响应式
  .management-container .card .card-time-filters {
    .time-filter-group {
      .time-input {
        width: 140px;
      }
    }
  }

  // 在中屏幕上也保持搜索框的合理宽度
  .management-container .card .card-second {
    .search-input {
      width: 280px; // 中屏幕稍微增加宽度
    }
  }

  .user-list-container .user-cards {
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 14px;
    padding: 14px;
  }
}

@media screen and (max-width: 768px) {
  .management-container .card {
    padding: 2px;

    .card-header {
      padding: 6px;

      .card-title {
        font-size: 16px;
      }
    }

    // 移动端时间筛选器
    .card-time-filters {
      padding: 8px;

      .time-filter-group {
        display: flex;
        flex-direction: row;
        gap: 8px;
        width: 100%;

        .time-input {
          flex: 1;
          width: auto !important;
        }
      }
    }

    // 移动端搜索框撑满宽度
    .card-second {
      .search-input {
        width: 100% !important;
      }
    }

    .table {
      margin-top: 0;
      max-height: calc(100vh - 240px);
    }

    .pagination-container {
      padding: 4px;

      :deep(.el-pagination .el-pager) {
        display: none;
      }
    }
  }

  .user-list-container .user-cards {
    grid-template-columns: 1fr;
    gap: 10px;
    padding: 10px;
    max-width: 400px;
    margin: 0 auto;

    .user-card {
      :deep(.el-card__body) {
        padding: 0;
      }

      .user-card-content {
        flex-direction: column;
        align-items: center;
        text-align: center;
        gap: 12px;
        padding: 16px;

        .user-avatar :deep(.el-avatar) {
          width: 64px !important;
          height: 64px !important;
          font-size: 32px;
        }

        .user-right-content {
          width: 100%;
          align-items: center;

          .user-info {
            .user-name {
              .username {
                display: block;
                margin-bottom: 4px;
                font-size: 16px;
              }

              .nickname {
                display: block;
                font-size: 14px;
              }
            }

            .comment-count {
              justify-content: center;
              margin-top: 8px;
            }
          }

          .user-actions {
            width: 100%;
            margin-top: 12px;

            .view-comments-btn {
              width: 100%;
              font-size: 14px;
            }
          }
        }
      }
    }
  }

  .comment-detail .comment-info-section {
    .comment-detail-header {
      flex-direction: column;
      gap: 16px;
    }

    .comment-stats-detail .stats-group {
      flex-direction: column;

      .stat-item {
        min-width: auto;
        max-width: none;
      }
    }
  }
}
</style>
