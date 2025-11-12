<template>
  <div class="comment-form">
    <!-- 用户头像 -->
    <div class="form-avatar">
      <el-avatar :size="36" :src="userStore.user?.avatar" />
    </div>

    <!-- 表单内容 -->
    <div class="form-content">
      <!-- 文本输入框 -->
      <el-input v-model="commentContent" :placeholder="placeholder" type="textarea" :rows="3" :maxlength="255" show-word-limit resize="none" @keydown.ctrl.enter="handleSubmit" @keydown.meta.enter="handleSubmit" />

      <!-- 表单操作栏 -->
      <div class="form-actions">
        <div class="form-tips">
          <span class="tips-text">支持 Ctrl + Enter 快捷发送</span>
        </div>

        <div class="form-buttons">
          <!-- AI 智能回复按钮 -->
          <div v-if="enableAiReply" class="ai-suggestion">
            <el-popover placement="top-end" width="320" trigger="manual" :teleported="false" :visible="aiPopoverVisible" popper-class="ai-suggestion-popover" @update:visible="handleAiPopoverVisibleChange">
              <template #reference>
                <el-button size="small" :loading="aiLoading" @click="toggleAiPopover">
                  <el-icon>
                    <MagicStick />
                  </el-icon>
                  智能回复
                </el-button>
              </template>

              <div class="ai-suggestion-content">
                <div v-if="aiLoading" class="suggestion-loading">
                  <el-icon class="loading-icon is-loading">
                    <Loading />
                  </el-icon>
                  <span>AI 正在生成回复...</span>
                </div>

                <div v-else-if="aiSuggestions.length > 0" class="suggestion-list">
                  <div class="suggestion-items">
                    <el-button v-for="suggestion in aiSuggestions" :key="suggestion" class="suggestion-item" size="small" @click="applySuggestion(suggestion)">
                      {{ suggestion }}
                    </el-button>
                  </div>
                  <div class="suggestion-actions">
                    <el-button text size="small" :loading="aiLoading" @click="refreshSuggestions">
                      <el-icon>
                        <RefreshRight />
                      </el-icon>
                      换一批
                    </el-button>
                  </div>
                </div>

                <div v-else class="suggestion-empty">
                  <p>{{ aiError || "暂无可用建议，请稍后重试" }}</p>
                  <el-button text size="small" @click="refreshSuggestions"> 重新生成 </el-button>
                </div>
              </div>
            </el-popover>
          </div>

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
import { computed, ref } from "vue";
import { ElMessage } from "element-plus";
import { addComment } from "@/api/comment";
import { useUserStore } from "@/stores/userStore";
import { generateCommentReplySuggestions } from "@/api/ai";
import { MagicStick, RefreshRight, Loading } from "@element-plus/icons-vue";

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
  articleTitle: {
    type: String,
    default: "",
  },
  replyUserId: {
    type: Number,
    default: null,
  },
  replyUserNickname: {
    type: String,
    default: null,
  },
  replyCommentContent: {
    type: String,
    default: "",
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
const aiLoading = ref(false); // AI 生成状态
const aiSuggestions = ref([]); // AI 建议列表
const aiError = ref(""); // AI 错误信息
const aiPopoverVisible = ref(false); // AI 弹层显示状态

// 状态管理
const userStore = useUserStore();

// 是否启用 AI 回复建议
const enableAiReply = computed(() => {
  return props.parentId > 0 && props.articleTitle && props.replyCommentContent;
});

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
    const commentId = res.data.data;

    // 构建新评论对象（用于界面显示）
    const newComment = {
      id: commentId,
      parentId: props.parentId,
      articleId: props.articleId,
      userId: userStore.user.id,
      nickname: userStore.user.nickname,
      avatar: userStore.user.avatar,
      replyUserId: props.replyUserId,
      replyUserNickname: props.replyUserNickname, // 被回复用户的昵称
      content: content,
      examineStatus: 1,
      likeCount: 0,
      replyCount: 0,
      createTime: new Date(),
      isLiked: false,
      children: [],
    };

    commentContent.value = "";
    aiPopoverVisible.value = false;
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
  aiPopoverVisible.value = false;
  emit("cancel");
};

// 切换 AI 弹层
const toggleAiPopover = async () => {
  if (!enableAiReply.value) {
    ElMessage.warning("仅在回复他人评论时可使用智能回复");
    return;
  }

  aiPopoverVisible.value = !aiPopoverVisible.value;
  if (aiPopoverVisible.value && aiSuggestions.value.length === 0 && !aiLoading.value) {
    await fetchAiSuggestions();
  }
};

// 重新生成 AI 建议
const refreshSuggestions = async () => {
  await fetchAiSuggestions(true);
};

// 应用 AI 建议
const applySuggestion = (suggestion) => {
  commentContent.value = suggestion;
  aiPopoverVisible.value = false;
  ElMessage.success("已填充 AI 建议");
};

// 获取 AI 建议
const fetchAiSuggestions = async (force = false) => {
  if (!enableAiReply.value) {
    return;
  }

  if (aiLoading.value) {
    return;
  }

  if (!force && aiSuggestions.value.length > 0) {
    return;
  }

  try {
    aiLoading.value = true;
    aiError.value = "";
    const res = await generateCommentReplySuggestions(props.articleTitle, props.replyCommentContent);
    aiSuggestions.value = res.data.data || [];
    if (aiSuggestions.value.length === 0) {
      aiError.value = "AI 暂未生成有效建议";
    }
  } catch (error) {
    aiSuggestions.value = [];
    aiError.value = "生成失败，请稍后重试";
    ElMessage.error("AI 回复生成失败");
    console.error("AI 回复生成失败:", error);
  } finally {
    aiLoading.value = false;
  }
};

// 同步 AI 弹层显隐状态
const handleAiPopoverVisibleChange = (visible) => {
  aiPopoverVisible.value = visible;
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
        align-items: center;

        // AI 智能回复按钮区域
        .ai-suggestion {
          display: flex;
          align-items: center;
        }

        .el-button {
          font-size: 13px;
          padding: 6px 16px;
          border-radius: 6px;
        }
      }
    }
  }
}

// AI 智能回复弹层样式
:deep(.ai-suggestion-popover) {
  padding: 12px;
  max-width: 320px;

  // AI 弹层内容容器
  .ai-suggestion-content {
    display: flex;
    flex-direction: column;
    gap: 12px;

    // 加载状态
    .suggestion-loading {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: var(--el-text-color-regular);

      .loading-icon {
        font-size: 16px;
      }
    }

    // 建议列表
    .suggestion-list {
      display: flex;
      flex-direction: column;
      gap: 8px;

      .suggestion-items {
        display: flex;
        flex-direction: column;
        gap: 6px;

        .suggestion-item {
          display: flex;
          justify-content: flex-start;
          align-items: flex-start;
          width: 100%;
          height: auto;
          padding: 10px 12px;
          line-height: 1.6;
          text-align: left;
          white-space: normal;
          margin: 0;
        }

        :deep(.suggestion-item .el-button__text) {
          display: block;
          width: 100%;
          text-align: left;
          line-height: 1.6;
          white-space: normal;
          word-break: break-word;
          overflow-wrap: break-word;
        }
      }

      .suggestion-actions {
        display: flex;
        justify-content: flex-end;
        margin-top: 4px;
      }
    }

    // 空状态
    .suggestion-empty {
      display: flex;
      flex-direction: column;
      gap: 8px;
      align-items: flex-start;
      font-size: 13px;
      color: var(--el-text-color-secondary);
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
          display: none;
        }

        .form-buttons {
          justify-content: center;
        }
      }
    }
  }
}
</style>
