<template>
  <div class="comment-item">
    <!-- 评论主体 -->
    <div class="comment-main">
      <!-- 用户头像 -->
      <div class="comment-avatar">
        <el-avatar :size="40" :src="comment.avatar" />
      </div>

      <!-- 评论内容 -->
      <div class="comment-content">
        <!-- 用户信息和时间 -->
        <div class="comment-meta">
          <span class="username">{{ comment.nickname }}</span>
          <span v-if="comment.replyUserNickname" class="reply-to">
            回复 <span class="reply-username">{{ comment.replyUserNickname }}</span>
          </span>
          <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
        </div>

        <!-- 评论文本 -->
        <div class="comment-text">
          <p>{{ comment.content }}</p>
        </div>

        <!-- 评论操作栏 -->
        <div class="comment-actions">
          <!-- 点赞按钮 -->
          <div class="action-item like-action">
            <el-button text size="small" :class="{ 'is-liked': comment.isLiked }" @click="handleLike">
              <svg-icon name="like" width="13px" height="13px" margin-right="7px" color="#909399" />
              <span>{{ comment.likeCount || 0 }}</span>
            </el-button>
          </div>

          <!-- 回复按钮 -->
          <div class="action-item">
            <el-button text size="small" @click="toggleReplyForm">
              <el-icon><ChatDotRound /></el-icon>
              <span>回复</span>
            </el-button>
          </div>

          <!-- 删除按钮（只有评论作者可见） -->
          <div v-if="canDelete" class="action-item">
            <el-button text size="small" type="danger" @click="handleDelete">
              <el-icon><Delete /></el-icon>
              <span>删除</span>
            </el-button>
          </div>

          <!-- 查看回复按钮 -->
          <div v-if="comment.replyCount > 0" class="action-item">
            <el-button text size="small" @click="toggleReplies">
              <el-icon><ArrowDown v-if="!showReplies" /><ArrowUp v-else /></el-icon>
              <span>{{ showReplies ? "收起" : "查看" }}回复 ({{ comment.replyCount }})</span>
            </el-button>
          </div>
        </div>

        <!-- 回复表单 -->
        <div v-if="showReplyForm" class="reply-form">
          <CommentForm :article-id="articleId" :parent-id="comment.id" :reply-user-id="comment.userId" :placeholder="`回复 ${comment.nickname}：`" @comment-added="handleReplyAdded" @cancel="hideReplyForm" />
        </div>

        <!-- 回复列表 -->
        <div v-if="showReplies && replyList.length > 0" class="reply-list">
          <CommentItem v-for="reply in replyList" :key="reply.id" :comment="reply" :article-id="articleId" :is-reply="true" @reply-added="handleSubReplyAdded" @comment-deleted="handleReplyDeleted" />

          <!-- 加载更多回复 -->
          <div v-if="hasMoreReplies" class="load-more-replies">
            <el-button text size="small" :loading="loadingReplies" @click="loadMoreReplies">
              {{ loadingReplies ? "加载中..." : "加载更多回复" }}
            </el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { ChatDotRound, Delete, ArrowDown, ArrowUp } from "@element-plus/icons-vue";
import { getReplyList, deleteComment } from "@/api/comment";
import { toggleLike } from "@/api/like";
import { useUserStore } from "@/stores/userStore";
import { formatTime } from "@/utils/formatTime";
import CommentForm from "./CommentForm.vue";
import SvgIcon from "@/components/SvgIcon.vue";

// Props
const props = defineProps({
  comment: {
    type: Object,
    required: true,
  },
  articleId: {
    type: Number,
    required: true,
  },
  isReply: {
    type: Boolean,
    default: false,
  },
});

// Emits
const emit = defineEmits(["reply-added", "comment-deleted"]);

// 响应式数据
const showReplyForm = ref(false); // 是否显示回复表单
const showReplies = ref(false); // 是否显示回复列表
const replyList = ref([]); // 回复列表
const loadingReplies = ref(false); // 加载回复状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(5); // 每页回复数量
const replyTotal = ref(0); // 回复总数
const hasMoreReplies = ref(false); // 是否还有更多回复

// 状态管理
const userStore = useUserStore();

// 计算属性
const canDelete = computed(() => {
  return userStore.user && userStore.user.id === props.comment.userId;
});

// 初始化回复列表（如果有子回复）
onMounted(() => {
  if (props.comment.children && props.comment.children.length > 0) {
    replyList.value = props.comment.children;
    hasMoreReplies.value = props.comment.replyCount > props.comment.children.length;
  }
});

// 切换回复表单显示
const toggleReplyForm = () => {
  if (!userStore.user) {
    ElMessage.warning("请先登录后再回复");
    return;
  }
  showReplyForm.value = !showReplyForm.value;
};

// 隐藏回复表单
const hideReplyForm = () => {
  showReplyForm.value = false;
};

// 切换回复列表显示
const toggleReplies = async () => {
  showReplies.value = !showReplies.value;

  // 如果是首次展开且没有加载过回复，则加载回复
  if (showReplies.value && replyList.value.length === 0) {
    await fetchReplies(true);
  }
};

// 获取回复列表
const fetchReplies = async (reset = false) => {
  try {
    if (reset) {
      currentPage.value = 1;
      replyList.value = [];
    }

    loadingReplies.value = true;
    const res = await getReplyList(props.comment.id, currentPage.value, pageSize.value);
    const newReplies = res.data.data.data || [];
    replyTotal.value = res.data.data.total || 0;

    if (reset) {
      replyList.value = newReplies;
    } else {
      replyList.value = [...replyList.value, ...newReplies];
    }

    // 判断是否还有更多数据
    hasMoreReplies.value = replyList.value.length < replyTotal.value;

    // 更新页码
    if (hasMoreReplies.value && newReplies.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取回复失败");
    console.error("获取回复失败:", error);
  } finally {
    loadingReplies.value = false;
  }
};

// 加载更多回复
const loadMoreReplies = () => {
  if (!hasMoreReplies.value || loadingReplies.value) {
    return;
  }
  fetchReplies(false);
};

// 处理回复添加
const handleReplyAdded = (newReply) => {
  // 添加新回复到列表
  replyList.value.push(newReply);

  // 更新总回复数量
  props.comment.replyCount = (props.comment.replyCount || 0) + 1;

  // 显示回复列表
  showReplies.value = true;

  // 隐藏回复表单
  hideReplyForm();

  // 通知父组件
  emit("reply-added", props.comment.id, newReply);
};

// 处理子回复添加
const handleSubReplyAdded = (commentId, newReply) => {
  // 子回复添加到当前评论的回复列表中
  replyList.value.push(newReply);

  // 更新总回复数量
  props.comment.replyCount = (props.comment.replyCount || 0) + 1;

  // 通知父组件
  emit("reply-added", props.comment.id, newReply);
};

// 处理回复删除
const handleReplyDeleted = (replyId) => {
  // 从回复列表中移除
  const index = replyList.value.findIndex((r) => r.id === replyId);
  if (index !== -1) {
    replyList.value.splice(index, 1);
    props.comment.replyCount = Math.max(0, (props.comment.replyCount || 0) - 1);
  }
};

// 处理点赞
const handleLike = async () => {
  if (!userStore.user) {
    ElMessage.warning("请先登录后再点赞");
    return;
  }

  try {
    if (props.comment.isLiked) {
      // 取消点赞
      await toggleLike(1, props.comment.id); // 1表示评论类型
      props.comment.isLiked = false;
      props.comment.likeCount = Math.max(0, (props.comment.likeCount || 0) - 1);
    } else {
      // 点赞
      await toggleLike(1, props.comment.id); // 1表示评论类型
      props.comment.isLiked = true;
      props.comment.likeCount = (props.comment.likeCount || 0) + 1;
    }
  } catch (error) {
    ElMessage.error("操作失败");
    console.error("点赞操作失败:", error);
  }
};

// 处理删除评论
const handleDelete = async () => {
  try {
    await ElMessageBox.confirm("确定要删除这条评论吗？删除后无法恢复。", "确认删除", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteComment(props.comment.id);
    emit("comment-deleted", props.comment.id);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
      console.error("删除评论失败:", error);
    }
  }
};
</script>

<style lang="scss" scoped>
// 评论项目
.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--el-border-color-light);

  &:last-child {
    border-bottom: none;
  }

  // 评论主体
  .comment-main {
    display: flex;
    gap: 12px;

    // 用户头像
    .comment-avatar {
      flex-shrink: 0;
    }

    // 评论内容
    .comment-content {
      flex: 1;
      min-width: 0;

      // 用户信息和时间
      .comment-meta {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;
        flex-wrap: wrap;

        .username {
          font-weight: 600;
          color: var(--el-text-color-primary);
          font-size: 14px;
        }

        .reply-to {
          font-size: 13px;
          color: var(--el-text-color-regular);

          .reply-username {
            color: var(--el-color-primary);
            font-weight: 500;
          }
        }

        .comment-time {
          font-size: 12px;
          color: var(--el-text-color-secondary);
          margin-left: auto;
        }
      }

      // 评论文本
      .comment-text {
        margin-bottom: 12px;

        p {
          margin: 0;
          line-height: 1.6;
          color: var(--el-text-color-primary);
          font-size: 14px;
          word-wrap: break-word;
          white-space: pre-wrap;
        }
      }

      // 评论操作栏
      .comment-actions {
        display: flex;
        align-items: center;
        gap: 16px;
        margin-bottom: 12px;

        .action-item {
          .el-button {
            padding: 4px 8px;
            height: auto;
            font-size: 13px;

            .el-icon {
              margin-right: 4px;
            }

            span {
              color: var(--el-text-color-regular);
            }

            &:hover {
              background-color: var(--el-bg-color-page);

              span {
                color: var(--el-color-primary);
              }
            }
          }

          // 点赞按钮特殊样式
          &.like-action {
            .el-button.is-liked {
              color: var(--el-color-primary);

              span {
                color: var(--el-color-primary);
              }
            }
          }
        }
      }

      // 回复表单
      .reply-form {
        margin-bottom: 16px;
        padding: 12px;
        background: var(--el-bg-color-page);
        border-radius: 6px;
        border: 1px solid var(--el-border-color-light);
      }

      // 回复列表
      .reply-list {
        margin-top: 12px;
        padding-left: 16px;
        border-left: 2px solid var(--el-border-color-light);

        .comment-item {
          padding: 12px 0;

          &:first-child {
            padding-top: 0;
          }

          &:last-child {
            border-bottom: none;
            padding-bottom: 0;
          }
        }

        // 加载更多回复
        .load-more-replies {
          padding: 8px 0;
          text-align: left;

          .el-button {
            font-size: 13px;
            padding: 4px 8px;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .comment-item {
    padding: 12px 0;

    .comment-main {
      gap: 8px;

      .comment-content {
        .comment-meta {
          .comment-time {
            margin-left: 0;
            order: 1;
            width: 100%;
            margin-top: 4px;
          }
        }

        .comment-actions {
          gap: 12px;
          flex-wrap: wrap;
        }

        .reply-list {
          padding-left: 8px;
        }
      }
    }
  }
}
</style>
