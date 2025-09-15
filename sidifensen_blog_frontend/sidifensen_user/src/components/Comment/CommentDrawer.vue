<template>
  <el-drawer v-model="drawerVisible" :title="`评论 ${commentTotal}`" direction="rtl" size="420px" :z-index="2000" :modal="true" :show-close="true" :close-on-click-modal="true" :close-on-press-escape="true" :append-to-body="true" class="comment-drawer">
    <!-- 抽屉头部自定义 -->
    <template #header="{ titleId, titleClass }">
      <div class="drawer-header">
        <h3 :id="titleId" :class="titleClass" class="drawer-title">
          <el-icon><ChatDotRound /></el-icon>
          <span>评论 {{ commentTotal }}</span>
        </h3>
      </div>
    </template>

    <!-- 抽屉内容 -->
    <div class="drawer-content">
      <!-- 发表评论区域 -->
      <div class="comment-form-section">
        <CommentForm v-if="userStore.user" :article-id="articleId" :parent-id="0" placeholder="写下你的评论..." @comment-added="handleCommentAdded" />
        <div v-else class="login-prompt">
          <p>请先登录后再发表评论</p>
          <el-button type="primary" size="small" @click="goToLogin">登录</el-button>
        </div>
      </div>

      <!-- 评论列表 -->
      <div class="comment-list-section">
        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton animated :count="3">
            <template #template>
              <div class="comment-skeleton">
                <el-skeleton-item variant="circle" style="width: 36px; height: 36px" />
                <div class="skeleton-content">
                  <el-skeleton-item variant="text" style="width: 80px; margin-bottom: 6px" />
                  <el-skeleton-item variant="text" style="width: 100%" />
                  <el-skeleton-item variant="text" style="width: 70%" />
                </div>
              </div>
            </template>
          </el-skeleton>
        </div>

        <!-- 空状态 -->
        <div v-else-if="commentList.length === 0" class="empty-state">
          <el-empty description="暂无评论" :image-size="120" />
        </div>

        <!-- 评论列表 -->
        <div v-else class="comment-items">
          <CommentItem v-for="comment in commentList" :key="comment.id" :comment="comment" :article-id="articleId" :compact="true" @reply-added="handleReplyAdded" @comment-deleted="handleCommentDeleted" />
        </div>

        <!-- 加载更多 -->
        <div v-if="hasMore && !loading && commentList.length > 0" class="load-more">
          <el-button :loading="loadingMore" @click="loadMoreComments" text type="primary" size="small">
            {{ loadingMore ? "加载中..." : "加载更多" }}
          </el-button>
        </div>
      </div>
    </div>
  </el-drawer>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { ChatDotRound } from "@element-plus/icons-vue";
import { getCommentList } from "@/api/comment";
import { useUserStore } from "@/stores/userStore";
import CommentForm from "./CommentForm.vue";
import CommentItem from "./CommentItem.vue";

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  articleId: {
    type: Number,
    required: true,
  },
});

// Emits
const emit = defineEmits(["update:visible"]);

// 响应式数据
const loading = ref(false); // 初始加载状态
const loadingMore = ref(false); // 加载更多状态
const commentList = ref([]); // 评论列表
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页数量
const commentTotal = ref(0); // 评论总数
const hasMore = ref(true); // 是否还有更多数据

// 路由和状态管理
const router = useRouter();
const userStore = useUserStore();

// 计算属性
const drawerVisible = computed({
  get: () => props.visible, // 从父组件接收的visible值
  set: (value) => emit("update:visible", value), // 更新父组件的visible值
});

// 监听抽屉开关，初次打开时加载数据
watch(
  () => props.visible,
  (newVisible) => {
    if (newVisible && commentList.value.length === 0) {
      fetchCommentList(true);
    }
  },
  { immediate: true }
);

// 获取评论列表
const fetchCommentList = async (reset = false) => {
  try {
    if (reset) {
      loading.value = true;
      currentPage.value = 1;
      commentList.value = [];
      hasMore.value = true;
    } else {
      loadingMore.value = true;
    }

    const res = await getCommentList(props.articleId, currentPage.value, pageSize.value);
    const newComments = res.data.data.data || [];
    commentTotal.value = res.data.data.total || 0;

    if (reset) {
      commentList.value = newComments;
    } else {
      commentList.value = [...commentList.value, ...newComments];
    }

    // 判断是否还有更多数据
    hasMore.value = commentList.value.length < commentTotal.value;

    // 更新页码
    if (hasMore.value && newComments.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取评论列表失败");
    console.error("获取评论列表失败:", error);
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

// 加载更多评论
const loadMoreComments = () => {
  if (!hasMore.value || loadingMore.value) {
    return;
  }
  fetchCommentList(false);
};

// 处理评论添加
const handleCommentAdded = (newComment) => {
  // 重新加载评论列表以获取最新数据
  fetchCommentList(true);
  ElMessage.success("评论发表成功");
};

// 处理回复添加
const handleReplyAdded = (commentId, newReply) => {
  // 找到对应的评论并更新其回复数
  const comment = commentList.value.find((c) => c.id === commentId);
  if (comment) {
    comment.replyCount = (comment.replyCount || 0) + 1;
    // 如果回复列表已加载，添加新回复
    if (comment.children) {
      comment.children.push(newReply);
    }
  }
  ElMessage.success("回复发表成功");

  // 更新总评论数
  commentTotal.value++;
};

// 处理评论删除
const handleCommentDeleted = (commentId) => {
  // 从列表中移除删除的评论
  const index = commentList.value.findIndex((c) => c.id === commentId);
  if (index !== -1) {
    commentList.value.splice(index, 1);
    commentTotal.value = Math.max(0, commentTotal.value - 1);
  }
  ElMessage.success("评论删除成功");
};

// 跳转到登录页
const goToLogin = () => {
  router.push("/login");
};

// 暴露方法给父组件
defineExpose({
  refreshComments: () => fetchCommentList(true),
});
</script>

<style lang="scss" scoped>
// 评论抽屉样式
:deep(.comment-drawer) {
  .el-drawer {
    background: var(--el-bg-color-page);
    border-left: 1px solid var(--el-border-color-light);
    box-shadow: -2px 0 12px rgba(0, 0, 0, 0.1);
  }

  .el-drawer__header {
    padding: 20px 24px 16px 24px;
    margin-bottom: 0;
    border-bottom: 1px solid var(--el-border-color-light);
    background: var(--el-bg-color);

    .drawer-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      .drawer-title {
        display: flex;
        align-items: center;
        gap: 10px;
        font-size: 18px;
        font-weight: 700;
        margin: 0;
        color: var(--el-text-color-primary);

        .el-icon {
          color: var(--el-color-primary);
          font-size: 20px;
        }
      }
    }
  }

  .el-drawer__close-btn {
    color: var(--el-text-color-regular);
    font-size: 18px;

    &:hover {
      color: var(--el-text-color-primary);
    }
  }

  .el-drawer__body {
    padding: 0;
    display: flex;
    flex-direction: column;
    height: 100%;
    background: var(--el-bg-color-page);
  }
}

// 抽屉内容
.drawer-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;

  // 发表评论区域
  .comment-form-section {
    padding: 20px 24px;
    border-bottom: 1px solid var(--el-border-color-light);
    flex-shrink: 0;
    background: var(--el-bg-color);

    .login-prompt {
      text-align: center;
      padding: 24px;
      background: var(--el-bg-color-page);
      border: 1px dashed var(--el-border-color);
      border-radius: 8px;

      p {
        margin: 0 0 16px 0;
        color: var(--el-text-color-regular);
        font-size: 14px;
      }
    }
  }

  // 评论列表区域
  .comment-list-section {
    flex: 1;
    overflow-y: auto;
    padding: 20px 24px;

    // 加载状态
    .loading-container {
      .comment-skeleton {
        display: flex;
        gap: 10px;
        padding: 12px 0;
        border-bottom: 1px solid var(--el-border-color-light);

        .skeleton-content {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 4px;
        }
      }
    }

    // 空状态
    .empty-state {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 200px;
    }

    // 评论项目
    .comment-items {
      // 评论项目之间的间距由CommentItem组件内部控制
    }

    // 加载更多
    .load-more {
      text-align: center;
      padding: 16px 0;
      border-top: 1px solid var(--el-border-color-light);
      margin-top: 12px;
    }

    // 滚动条样式
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--el-border-color);
      border-radius: 3px;

      &:hover {
        background: var(--el-border-color-dark);
      }
    }

    &::-webkit-scrollbar-track {
      background: transparent;
    }
  }
}

// 紧凑模式下的评论样式调整
:deep(.comment-item) {
  .comment-main {
    .comment-avatar {
      .el-avatar {
        width: 32px !important;
        height: 32px !important;
      }
    }

    .comment-content {
      .comment-meta {
        .username {
          font-size: 13px;
        }

        .reply-to {
          font-size: 12px;
        }

        .comment-time {
          font-size: 11px;
        }
      }

      .comment-text {
        p {
          font-size: 13px;
          line-height: 1.4;
        }
      }

      .comment-actions {
        gap: 12px;

        .action-item .el-button {
          font-size: 12px;
          padding: 2px 6px;
        }
      }

      .reply-form {
        padding: 8px;
        margin-bottom: 12px;
      }

      .reply-list {
        padding-left: 12px;

        .comment-item {
          padding: 8px 0;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  :deep(.comment-drawer) {
    .el-drawer {
      width: 90vw !important;
    }
  }

  .drawer-content {
    .comment-form-section {
      padding: 12px 16px;
    }

    .comment-list-section {
      padding: 12px 16px;
    }
  }
}
</style>
