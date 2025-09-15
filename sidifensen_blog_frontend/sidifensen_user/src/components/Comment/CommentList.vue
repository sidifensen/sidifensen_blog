<template>
  <div class="comment-list">
    <!-- 评论标题和统计 -->
    <div class="comment-header">
      <h3 class="comment-title">
        <el-icon><ChatDotRound /></el-icon>
        评论 ({{ commentTotal }})
      </h3>
    </div>

    <!-- 发表评论区域 -->
    <div class="comment-form-section">
      <CommentForm v-if="userStore.user" :article-id="articleId" :parent-id="0" placeholder="写下你的评论..." @comment-added="handleCommentAdded" />
      <div v-else class="login-prompt">
        <p>请先登录后再发表评论</p>
        <el-button type="primary" @click="goToLogin">登录</el-button>
      </div>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list-section">
      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <el-skeleton animated :count="3">
          <template #template>
            <div class="comment-skeleton">
              <el-skeleton-item variant="circle" style="width: 40px; height: 40px" />
              <div class="skeleton-content">
                <el-skeleton-item variant="text" style="width: 100px; margin-bottom: 8px" />
                <el-skeleton-item variant="text" style="width: 100%" />
                <el-skeleton-item variant="text" style="width: 80%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>

      <!-- 空状态 -->
      <div v-else-if="commentList.length === 0" class="empty-state">
        <el-empty description="暂无评论，快来发表第一条评论吧！" />
      </div>

      <!-- 评论列表 -->
      <div v-else class="comment-items">
        <CommentItem v-for="comment in commentList" :key="comment.id" :comment="comment" :article-id="articleId" @reply-added="handleReplyAdded" @comment-deleted="handleCommentDeleted" />
      </div>

      <!-- 加载更多 -->
      <div v-if="hasMore && !loading" class="load-more">
        <el-button :loading="loadingMore" @click="loadMoreComments" text type="primary">
          {{ loadingMore ? "加载中..." : "加载更多评论" }}
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { ChatDotRound } from "@element-plus/icons-vue";
import { getCommentList } from "@/api/comment";
import { useUserStore } from "@/stores/userStore";
import CommentForm from "@/components/Comment/CommentForm.vue";
import CommentItem from "@/components/Comment/CommentItem.vue";

// Props
const props = defineProps({
  articleId: {
    type: Number,
    required: true,
  },
});

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
const isCurrentUser = computed(() => {
  return userStore.user?.id;
});

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
    const newComments = res.data.data || []; // 注意数据结构：res.data.data
    commentTotal.value = res.data.total || 0; // 分页信息在 res.data 层级

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

// 组件挂载时获取数据
onMounted(() => {
  fetchCommentList(true);
});
</script>

<style lang="scss" scoped>
// 评论列表容器
.comment-list {
  background: var(--el-bg-color-page);
  border-radius: 8px;
  padding: 24px;
  margin-top: 20px;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 12px var(--el-border-color-light);

  // 评论标题区域
  .comment-header {
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid var(--el-border-color-light);

    .comment-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 600;
      margin: 0;
      color: var(--el-text-color-primary);

      .el-icon {
        color: var(--el-color-primary);
      }
    }
  }

  // 发表评论区域
  .comment-form-section {
    margin-bottom: 24px;

    .login-prompt {
      text-align: center;
      padding: 32px;
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
    // 加载状态
    .loading-container {
      padding: 20px 0;

      .comment-skeleton {
        display: flex;
        gap: 12px;
        padding: 20px 0;
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
      padding: 60px 0;
      text-align: center;
    }

    // 评论项目
    .comment-items {
      // 评论项目之间的间距由CommentItem组件内部控制
    }

    // 加载更多
    .load-more {
      text-align: center;
      padding: 24px 0;
      border-top: 1px solid var(--el-border-color-light);
      margin-top: 16px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .comment-list {
    padding: 16px;
    margin-top: 16px;

    .comment-header {
      margin-bottom: 16px;

      .comment-title {
        font-size: 16px;
      }
    }

    .comment-form-section {
      margin-bottom: 16px;
    }

    .comment-list-section {
      .load-more {
        padding: 16px 0;
      }
    }
  }
}
</style>
