<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">评论审核</h2>
        <div class="card-actions">
          <el-select v-model="searchExamineStatus" placeholder="审核状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="审核不通过" value="2" />
            <el-option label="全部" value="" />
          </el-select>
          <el-select v-model="searchUserId" placeholder="用户名称" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option v-for="user in userList" :key="user.id" :label="user.nickname || user.username" :value="user.id" />
          </el-select>
        </div>
      </div>
      <div class="card-second">
        <el-date-picker v-model="searchCreateTimeStart" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="创建时间开始" :prefix-icon="Calendar" size="small" class="search-input" clearable @change="handleSearch" />
        <el-date-picker v-model="searchCreateTimeEnd" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="创建时间结束" :prefix-icon="Calendar" size="small" class="search-input" clearable @change="handleSearch" />
      </div>
      <div class="card-third">
        <el-button type="primary" plain round @click="handleBatchAudit" :disabled="selectedComments.length === 0" :loading="batchAuditLoading"> 批量审核 </el-button>
        <el-button type="warning" plain round @click="handleBatchReject" :disabled="selectedComments.length === 0" :loading="batchRejectLoading"> 批量拒绝 </el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedComments.length === 0" :loading="batchDeleteLoading"> 批量删除 </el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedCommentList" class="table" style="height: 100%" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="content" label="评论内容" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="row.content" placement="top-start" :popper-style="{ maxWidth: '300px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="comment-content">{{ row.content }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="nickname" label="评论用户" width="100" />
          <el-table-column prop="articleTitle" label="所属文章" min-width="170">
            <template #default="{ row }">
              <el-tooltip :content="row.articleTitle" placement="top-start" :popper-style="{ maxWidth: '300px', wordWrap: 'break-word', whiteSpace: 'normal' }"">
                <div class="article-title">{{ row.articleTitle }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="articleId" label="文章ID" width="80" />
          <el-table-column prop="parentId" label="父评论ID" width="100">
            <template #default="{ row }">
              <span v-if="row.parentId">{{ row.parentId }}</span>
              <span v-else class="no-parent">主评论</span>
            </template>
          </el-table-column>
          <el-table-column prop="replyUserNickname" label="回复用户" width="100">
            <template #default="{ row }">
              <span v-if="row.replyUserNickname">{{ row.replyUserNickname }}</span>
              <span v-else class="no-reply">无回复</span>
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

                  <!-- 评论内容 -->
                  <div class="comment-content-mobile">{{ comment.content }}</div>

                  <!-- 用户信息 -->
                  <div class="comment-author-mobile">
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
                    <div class="meta-item">
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
                    <!-- 统计数据一行显示 -->
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

                    <!-- 时间信息 -->
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="90%" class="comment-detail-dialog" :close-on-click-modal="false" :close-on-press-escape="true" draggable align-center @close="handleDialogClose">
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

              <div class="comment-author-section">
                <el-icon class="author-icon"><User /></el-icon>
                <span class="author-name-detail">{{ currentComment?.nickname || "未知用户" }}</span>
              </div>

              <div class="comment-content-detail">
                <el-icon class="content-icon"><ChatDotRound /></el-icon>
                <span>{{ currentComment?.content || "无内容" }}</span>
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
                  <el-tag type="primary" size="small">文章ID: {{ currentComment?.articleId || "未知" }}</el-tag>
                </div>

                <div class="badge-group">
                  <span class="badge-label">审核状态:</span>
                  <div class="comment-status" :class="(currentComment?.examineStatus || 0) === 0 ? 'status-unaudited' : (currentComment?.examineStatus || 0) === 1 ? 'status-audited' : 'status-rejected'">
                    {{ (currentComment?.examineStatus || 0) === 0 ? "待审核" : (currentComment?.examineStatus || 0) === 1 ? "已审核" : "未通过" }}
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
import { Delete, Close, Check, View, Calendar, User, ChatDotRound, Clock } from "@element-plus/icons-vue";
import { getUserListWithCommentCount } from "@/api/user";
import { adminGetCommentList, adminDeleteComment, adminDeleteBatchComment, adminExamineComment, adminExamineBatchComment, adminSearchComment } from "@/api/comment";

// 评论列表数据
const commentList = ref([]);
// 分页后的评论列表
const paginatedCommentList = ref([]);
// 加载状态
const loading = ref(false);
// 当前页码
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);
// 总条数
const total = ref(0);
// 对话框可见性
const dialogVisible = ref(false);
// 对话框标题
const dialogTitle = ref("评论详情");
// 当前查看的评论
const currentComment = ref(null);
// 详情加载状态
const detailLoading = ref(false);

// 对话框关闭处理
const handleDialogClose = () => {
  currentComment.value = null;
  detailLoading.value = false;
};

// 用户列表
const userList = ref([]);

// 获取用户列表
const getUsers = async () => {
  try {
    const res = await getUserListWithCommentCount();
    userList.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取用户列表失败");
  }
};

// 获取评论列表
const getComments = async () => {
  loading.value = true;
  try {
    const res = await adminGetCommentList();
    commentList.value = res.data.data.sort((a, b) => new Date(b.createTime) - new Date(a.createTime)); // 按照创建时间倒序展示
    total.value = commentList.value.length;
    updatePaginatedCommentList();
  } catch (error) {
    ElMessage.error("获取评论列表失败");
  } finally {
    loading.value = false;
  }
};

// 移动端检测
const isMobileView = ref(false);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 初始化
onMounted(() => {
  getComments();
  getUsers();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});

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

// 搜索条件
const searchUserId = ref("");
const searchExamineStatus = ref("");
const searchCreateTimeStart = ref(null);
const searchCreateTimeEnd = ref(null);

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    const res = await adminSearchComment({
      userId: searchUserId.value,
      examineStatus: searchExamineStatus.value,
      createTimeStart: searchCreateTimeStart.value,
      createTimeEnd: searchCreateTimeEnd.value,
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

// 智能刷新列表（根据是否有搜索条件决定调用搜索还是获取全部）
const refreshCommentList = async () => {
  // 检查是否有任何搜索条件
  const hasSearchConditions = searchUserId.value || searchExamineStatus.value || searchCreateTimeStart.value || searchCreateTimeEnd.value;

  if (hasSearchConditions) {
    // 有搜索条件时重新执行搜索
    await handleSearch();
  } else {
    // 没有搜索条件时获取全部评论
    await getComments();
  }
};

// 选中的评论
const selectedComments = ref([]);

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

// 批量操作加载状态
const batchAuditLoading = ref(false);
const batchRejectLoading = ref(false);
const batchDeleteLoading = ref(false);

// 查看评论详情
const handleViewComment = async (comment) => {
  try {
    detailLoading.value = true;
    currentComment.value = null; // 清空之前的数据
    dialogTitle.value = "评论详情";

    // 直接使用传入的评论数据
    currentComment.value = comment;
    dialogVisible.value = true; // 数据加载成功后再显示对话框
    ElMessage.success("评论详情加载成功");
  } catch (error) {
    ElMessage.error("获取评论详情失败: " + (error.message || "未知错误"));
    dialogVisible.value = false; // 加载失败时关闭对话框
  } finally {
    detailLoading.value = false;
  }
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
</script>

<style lang="scss" scoped>
// ===== 主要页面容器样式 ===== // 评论审核页面的根容器，包含所有内容
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  // ===== 主卡片容器 =====
  // 包含标题、搜索条件、数据内容的主要卡片
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

    // ===== 卡片头部区域 =====
    // 包含页面标题和筛选条件的顶部区域
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 10px 0 10px;

      // 页面标题样式
      .card-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;

        // 标题前的装饰线
        &::before {
          content: "";
          display: inline-block;
          width: 4px;
          height: 20px;
          background-color: #42b983;
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      // 搜索筛选操作区域
      .card-actions {
        display: flex;
        align-items: center;

        // 搜索输入框和下拉选择器通用样式
        .search-input {
          width: 240px;
          border-radius: 8px;
          margin-left: 10px;

          // Element Plus 输入框样式覆盖
          :deep(.el-input__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
              border-color: #42b983;
            }
          }

          // Element Plus 选择器样式覆盖
          :deep(.el-select__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
              border-color: #42b983;
            }
          }
        }
      }
    }

    // ===== 第二行筛选区域 =====
    // 包含时间筛选的第二行
    .card-second {
      display: flex;
      justify-content: flex-end;
      padding: 5px 5px 0 5px;
      gap: 10px;

      // 时间选择器样式
      :deep(.el-input__wrapper) {
        border-radius: 8px;
        &:focus-within {
          box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
          border-color: #42b983;
        }
      }
    }

    // ===== 批量操作按钮区域 =====
    // 包含批量审核、拒绝、删除按钮的第三行
    .card-third {
      display: flex;
      justify-content: flex-end;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
    }

    // ===== 桌面端表格视图 =====
    // PC端显示的表格布局
    .desktop-view {
      flex: 1;
      display: flex;
      flex-direction: column;
      padding-bottom: 60px; // 为分页容器预留空间

      // 评论列表表格
      .table {
        flex: 1;
        display: flex;
        flex-direction: column;
        margin-top: 16px;
        max-height: calc(100vh - 280px); // 增加高度预留，为分页容器留空间

        // Element Plus 表格样式覆盖
        :deep(.tag-wrap) {
          white-space: normal;
          word-break: break-all;
          line-height: 1.4;
          max-width: 180px;
          display: inline-block;
          height: auto;
        }

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
          tr {
            td {
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
        }

        // 评论内容样式
        .comment-content {
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
          cursor: pointer;

          &:hover {
            color: #42b983;
          }
        }

        // 无父评论和无回复状态样式
        .no-parent,
        .no-reply {
          color: #999;
          font-size: 12px;
        }

        // 表格操作按钮区域样式
        .table-actions {
          display: flex;
          align-items: center;
          justify-content: center;
          flex-wrap: wrap;
          height: 100%;
          min-height: 60px;

          // 查看按钮样式
          .view-button {
            background-color: #f0f9ff;
            color: #0369a1;
            border-color: #f0f9ff;
            border-radius: 6px;
            transition: all 0.3s ease;

            &:hover {
              background-color: #dbeafe;
              border-color: #dbeafe;
              transform: translateY(-2px);
              box-shadow: 0 2px 8px rgba(3, 105, 161, 0.3);
            }
          }

          // 审核按钮样式
          .examine-button {
            background-color: #e0f2fe;
            color: #0284c7;
            border-color: #e0f2fe;
            border-radius: 6px;
            transition: all 0.3s ease;

            &:hover {
              background-color: #bae6fd;
              border-color: #bae6fd;
              transform: translateY(-2px);
              box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
            }
          }

          // 拒绝按钮样式
          .reject-button {
            background-color: #fef3c7;
            color: #d97706;
            border-color: #fef3c7;
            border-radius: 6px;
            transition: all 0.3s ease;

            &:hover {
              background-color: #fde68a;
              border-color: #fde68a;
              transform: translateY(-2px);
              box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
            }
          }

          // 删除按钮样式
          .delete-button {
            background-color: #fee2e2;
            color: #ef4444;
            border-color: #fee2e2;
            border-radius: 6px;
            transition: all 0.3s ease;

            &:hover {
              background-color: #fecaca;
              border-color: #fecaca;
              transform: translateY(-2px);
              box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
            }
          }
        }
      }
    }

    // ===== 移动端卡片视图 =====
    // 手机端显示的卡片布局
    .mobile-view {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      padding-bottom: 60px; // 为分页容器预留空间
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
            border: 2px solid #42b983;
            box-shadow: 0 0 12px rgba(66, 185, 131, 0.3);
          }

          // 卡片内容容器
          .comment-card-content {
            display: flex;
            flex-direction: column;
            gap: 12px;

            // 评论头部信息区域
            .comment-header-section {
              display: flex;
              flex-direction: column;

              // 评论信息区域
              .comment-info {
                width: 100%;
                display: flex;
                flex-direction: column;

                // 评论头部信息（ID和状态）
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

                  // 评论ID样式
                  .comment-id {
                    font-size: 12px;
                    color: #666;
                    background-color: #f5f5f5;
                    padding: 2px 6px;
                    border-radius: 4px;
                  }
                }

                // 移动端评论内容样式
                .comment-content-mobile {
                  font-size: 16px;
                  font-weight: 500;
                  color: #333;
                  line-height: 1.4;
                  margin-bottom: 8px;
                  word-wrap: break-word;
                }

                // 移动端作者信息样式
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
                  }
                }

                // 移动端文章信息样式
                .comment-article-mobile {
                  font-size: 13px;
                  color: #666;
                  margin-bottom: 8px;
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
                  }
                }

                // 移动端评论元信息样式
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

                // 评论元数据区域
                .comment-meta {
                  display: flex;
                  flex-direction: column;
                  gap: 8px;

                  // 元数据项通用样式
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

            // 评论操作按钮区域
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

    // ===== 分页容器 =====
    // 固定在底部的分页组件
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

  // ===== 通用评论状态样式 =====
  // 桌面端和移动端共用的审核状态标签样式
  .comment-status {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;

    // 待审核状态样式
    &.status-unaudited {
      background-color: #fff1f0;
      color: #f56c6c;
    }

    // 已审核状态样式
    &.status-audited {
      background-color: #f0f9eb;
      color: #67c23a;
    }

    // 审核拒绝状态样式
    &.status-rejected {
      background-color: #fdf6ec;
      color: #e6a23c;
    }
  }
}

// ===== 评论详情对话框样式 =====
// Element Plus 对话框样式覆盖，用于显示评论详情
:deep(.comment-detail-dialog) {
  border-radius: 16px;

  // 对话框头部样式
  .el-dialog__header {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    position: relative;

    // 对话框标题样式
    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    // 关闭按钮样式
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

  // 对话框主体内容样式
  .el-dialog__body {
    padding: 24px;
    max-height: 80vh;
    overflow-y: auto;
  }

  // 移动端对话框样式适配
  @media screen and (max-width: 767px) {
    width: 95% !important;

    .el-dialog__body {
      padding: 16px;
    }
  }
}

// ===== 评论详情内容区域 =====
// 包含评论基本信息的详情区域
.comment-detail {
  // 评论基本信息区域
  .comment-info-section {
    margin-bottom: 24px;

    // 评论详情头部布局
    .comment-detail-header {
      display: flex;
      gap: 24px;
      align-items: flex-start;

      // 评论详情信息区域
      .comment-detail-info {
        flex: 1;

        // 评论标题区域
        .comment-title-section {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;

          // 评论标题样式
          .comment-title-detail {
            margin: 0;
            font-size: 24px;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1.3;
            flex: 1;
          }

          // 评论ID标签样式
          .comment-id-detail {
            background: linear-gradient(45deg, #667eea, #764ba2);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            white-space: nowrap;
          }
        }

        // 作者信息区域
        .comment-author-section {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 12px;
          padding: 8px 12px;
          background-color: #f8f9fa;
          border-radius: 8px;

          .author-icon {
            color: #6c757d;
            font-size: 16px;
          }

          .author-name-detail {
            font-weight: 600;
            color: #495057;
          }
        }

        // 评论内容区域
        .comment-content-detail {
          display: flex;
          align-items: flex-start;
          gap: 8px;
          font-size: 14px;
          color: #6c757d;
          line-height: 1.6;
          margin-bottom: 16px;
          padding: 12px;
          background-color: #e3f2fd;
          border-radius: 8px;
          border-left: 4px solid #2196f3;

          .content-icon {
            color: #2196f3;
            font-size: 16px;
            margin-top: 2px;
            flex-shrink: 0;
          }

          span {
            flex: 1;
            word-wrap: break-word;
          }
        }

        // 评论标签和状态区域
        .comment-badges-detail {
          display: flex;
          flex-direction: column;
          gap: 12px;
          margin-bottom: 20px;

          // 标签组样式
          .badge-group {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;

            // 标签标题样式
            .badge-label {
              font-size: 13px;
              font-weight: 600;
              color: #495057;
              min-width: 80px;
            }

            // Element Plus 标签样式
            .el-tag {
              font-size: 11px;
              height: 24px;
              line-height: 22px;
            }
          }
        }
      }

      // 评论用户头像详情区域
      .comment-avatar-detail {
        flex-shrink: 0;
        width: 120px;
        height: 120px;
        display: flex;
        align-items: center;
        justify-content: center;

        // 头像图片样式
        .detail-avatar-img {
          border: 4px solid rgba(255, 255, 255, 0.3);
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        }

        // 无头像占位样式
        .no-avatar-detail {
          width: 100%;
          height: 100%;
          background: linear-gradient(45deg, #f8f9fa, #e9ecef);
          border: 2px dashed #dee2e6;
          border-radius: 50%;
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          font-size: 16px;
          color: #6c757d;
          gap: 8px;

          .avatar-icon {
            font-size: 32px;
            color: #adb5bd;
          }
        }
      }
    }

    // 评论统计数据区域
    .comment-stats-detail {
      border-top: 1px solid #e9ecef;
      padding-top: 20px;
      margin-top: 20px;

      // 统计数据组容器
      .stats-group {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        justify-content: space-evenly;

        // 统计项样式
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

          // 时间统计项特殊样式
          &.time-stat-item {
            background: linear-gradient(135deg, #e3f2fd, #bbdefb);
            border-color: #90caf9;

            .stat-icon {
              color: #1976d2;
            }

            .stat-label {
              color: #1976d2;
              font-weight: 600;
            }

            .stat-value {
              color: #0d47a1;
              font-size: 12px;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}

// ===== 其他通用组件样式 =====

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

  // 移动端按钮间距调整
  @media screen and (max-width: 768px) {
    gap: 4px;
  }

  // 按钮通用样式
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

// ===== 响应式设计 =====
// 按照不同屏幕尺寸进行适配的响应式样式

// 中等屏幕适配 (1400px 以下)
@media screen and (max-width: 1400px) {
  .management-container {
    .card {
      .card-header {
        .card-actions {
          .search-input {
            width: 180px;
          }
        }
      }
    }
  }
}

// 小屏幕适配 (1220px 以下)
@media screen and (max-width: 1220px) {
  .management-container {
    .card {
      .card-header {
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
    }
  }
}

// 平板和小屏设备适配 (768px 以下)
@media screen and (max-width: 768px) {
  // 管理页面容器响应式调整
  .management-container {
    .card {
      padding: 2px;

      // 卡片头部响应式调整
      .card-header {
        padding: 6px;

        .card-title {
          font-size: 16px;
        }

        .card-actions {
          .search-input {
            width: 100%;
          }
        }
      }

      // 桌面端表格响应式调整
      .desktop-view {
        .table {
          margin-top: 0;
          max-height: calc(100vh - 240px); // 为分页容器预留更多空间
        }
      }

      // 分页容器响应式调整
      .pagination-container {
        padding: 4px;

        :deep(.el-pagination) {
          .el-pager {
            display: none;
          }
        }
      }
    }
  }

  // 评论详情对话框响应式调整
  .comment-detail {
    .comment-info-section {
      .comment-detail-header {
        flex-direction: column;
        gap: 16px;

        // 评论头像响应式调整
        .comment-avatar-detail {
          align-self: center;
          width: 100px;
          height: 100px;

          // 确保头像图片保持正圆形
          .detail-avatar-img {
            width: 100px !important;
            height: 100px !important;
            border-radius: 50% !important;
          }

          // 无头像占位也保持正圆形
          .no-avatar-detail {
            width: 100px !important;
            height: 100px !important;
            border-radius: 50% !important;
          }
        }
      }

      // 统计数据响应式调整
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
}

// 手机端适配 (480px 以下)
@media screen and (max-width: 480px) {
  // 卡片头部超小屏适配
  .management-container {
    .card {
      .card-header {
        flex-direction: column;
        align-items: flex-start;

        .card-actions {
          width: 100%;
          justify-content: space-between;

          .search-input {
            width: 100%;
          }
        }
      }

      // 桌面端表格操作按钮超小屏适配
      .desktop-view {
        .table {
          .table-actions {
            .el-button {
              font-size: 11px;
              padding: 4px 8px;
            }
          }
        }
      }
    }
  }
}
</style>
