<template>
  <div class="comment-form">
    <!-- 用户头像 -->
    <div class="form-avatar">
      <el-avatar :size="36" :src="userStore.user?.avatar" />
    </div>

    <!-- 表单内容 -->
    <div class="form-content">
      <!-- 文本输入框 -->
      <el-input v-model="commentContent" :placeholder="placeholder" type="textarea" :rows="3" :maxlength="500" show-word-limit resize="none" @keydown.ctrl.enter="handleSubmit" @keydown.meta.enter="handleSubmit" />

      <!-- 表单操作栏 -->
      <div class="form-actions">
        <div class="form-tips">
          <span class="tips-text">支持 Ctrl + Enter 快捷发送</span>
        </div>

        <div class="form-buttons">
          <!-- 取消按钮（仅在回复时显示） -->
          <el-button v-if="parentId > 0" size="small" @click="handleCancel"> 取消 </el-button>

          <!-- 发送按钮 -->
          <el-button type="primary" size="small" :loading="submitting" :disabled="!commentContent.trim()" @click="handleSubmit">
            {{ submitting ? "发送中..." : "发送" }}
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
import { addComment } from "@/api/comment";
import { useUserStore } from "@/stores/userStore";

// Props
const props = defineProps({
  articleId: {
    type: Number,
    required: true,
  },
  parentId: {
    type: Number,
    default: 0,
  },
  replyUserId: {
    type: Number,
    default: null,
  },
  placeholder: {
    type: String,
    default: "写下你的评论...",
  },
});

// Emits
const emit = defineEmits(["comment-added", "cancel"]);

// 响应式数据
const commentContent = ref(""); // 评论内容
const submitting = ref(false); // 提交状态

// 状态管理
const userStore = useUserStore();

// 提交评论
const handleSubmit = async () => {
  // 验证内容
  const content = commentContent.value.trim();
  if (!content) {
    ElMessage.warning("请输入评论内容");
    return;
  }

  if (content.length > 500) {
    ElMessage.warning("评论内容不能超过500字");
    return;
  }

  try {
    submitting.value = true;

    // 构建评论数据
    const commentData = {
      articleId: props.articleId,
      parentId: props.parentId,
      content: content,
    };

    // 如果是回复评论，添加回复用户ID
    if (props.replyUserId) {
      commentData.replyUserId = props.replyUserId;
    }

    // 提交评论
    const res = await addComment(commentData);
    const commentId = res.data.data; // 注意数据结构：res.data.data

    // 构建新评论对象（用于界面显示）
    const newComment = {
      id: commentId,
      parentId: props.parentId,
      articleId: props.articleId,
      userId: userStore.user.id,
      nickname: userStore.user.nickname,
      avatar: userStore.user.avatar,
      replyUserId: props.replyUserId,
      replyUserNickname: props.replyUserId ? userStore.user.nickname : null,
      content: content,
      examineStatus: 1,
      likeCount: 0,
      replyCount: 0,
      createTime: new Date(),
      isLiked: false,
      children: [],
    };

    // 清空表单
    commentContent.value = "";

    // 通知父组件
    emit("comment-added", newComment);
  } catch (error) {
    ElMessage.error("评论发送失败，请重试");
    console.error("发送评论失败:", error);
  } finally {
    submitting.value = false;
  }
};

// 取消操作
const handleCancel = () => {
  commentContent.value = "";
  emit("cancel");
};
</script>

<style lang="scss" scoped>
// 评论表单
.comment-form {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: var(--el-bg-color-page);
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);

  // 用户头像
  .form-avatar {
    flex-shrink: 0;
  }

  // 表单内容
  .form-content {
    flex: 1;
    min-width: 0;

    // 文本输入框样式调整
    :deep(.el-textarea) {
      .el-textarea__inner {
        padding: 12px;
        font-size: 14px;
        line-height: 1.5;
        border-radius: 6px;
        border: 1px solid var(--el-border-color);
        transition: border-color 0.3s ease;

        &:focus {
          border-color: var(--el-color-primary);
          box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
        }

        &::placeholder {
          color: var(--el-text-color-placeholder);
        }
      }

      .el-input__count {
        background: transparent;
        color: var(--el-text-color-secondary);
        font-size: 12px;
        right: 8px;
        bottom: 4px;
      }
    }

    // 表单操作栏
    .form-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 12px;

      // 提示文本
      .form-tips {
        .tips-text {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }

      // 按钮组
      .form-buttons {
        display: flex;
        gap: 8px;

        .el-button {
          font-size: 13px;
          padding: 6px 16px;
          border-radius: 6px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .comment-form {
    padding: 12px;
    gap: 8px;

    .form-content {
      :deep(.el-textarea) {
        .el-textarea__inner {
          padding: 10px;
        }
      }

      .form-actions {
        flex-direction: column;
        align-items: stretch;
        gap: 8px;

        .form-tips {
          text-align: center;
        }

        .form-buttons {
          justify-content: center;
        }
      }
    }
  }
}
</style>
