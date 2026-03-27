<template>
  <!-- 使用 Teleport 将操作栏传送到 body，避免受祖先元素 overflow 影响 -->
  <Teleport to="body">
    <div v-if="article">
      <!-- 文章底部操作栏 - 固定在视窗底部 -->
      <div class="article-actions">
        <div class="action-item">
          <el-button :type="article.isLiked ? 'primary' : 'default'" :loading="likeLoading" @click="handleLike">
            <svg-icon name="like" width="16px" height="16px" margin-right="6px" :color="article.isLiked ? '#ffffff' : '#909399'" />
            {{ formatCount(article.likeCount) }}
          </el-button>
        </div>
        <div class="action-item">
          <el-button :type="article.isCollected ? 'primary' : 'default'" :icon="article.isCollected ? StarFilled : Star" @click="handleCollect">
            {{ formatCount(article.collectCount) }}
          </el-button>
        </div>
        <div class="action-item">
          <el-button :icon="ChatLineRound" @click="handleComment">
            {{ formatCount(commentTotal || article.commentCount) }}
          </el-button>
        </div>
      </div>

      <!-- 返回顶部按钮 -->
      <div class="back-to-top" @click="scrollToTop">
        <el-icon>
          <ArrowUp />
        </el-icon>
      </div>

      <!-- 评论抽屉 -->
      <CommentDrawer v-model="commentDrawerVisible" :article-id="article.id" :article-title="article.title" ref="commentDrawerRef" />

      <!-- 收藏对话框 -->
      <FavoriteDialog v-model="favoriteDialogVisible" :article-id="article.id" @success="handleFavoriteSuccess" />
    </div>
  </Teleport>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Star, StarFilled, ChatLineRound, ArrowUp } from "@element-plus/icons-vue";
import { toggleLike } from "@/api/like";
import { formatCompactNumber } from "@/utils/formatNumber";
import CommentDrawer from "@/views/User/Article/components/CommentDrawer.vue";
import FavoriteDialog from "./FavoriteDialog.vue";

// 路由
const router = useRouter();

// Props 定义
const props = defineProps({
  article: {
    type: Object,
    default: () => null,
  },
});

// Emits 定义
const emit = defineEmits(["updateArticle"]);

// 响应式数据
const likeLoading = ref(false); // 点赞加载状态
const commentDrawerVisible = ref(false); // 评论抽屉显示状态
const commentTotal = ref(0); // 评论总数
const commentDrawerRef = ref(null); // 评论抽屉引用
const favoriteDialogVisible = ref(false); // 收藏对话框显示状态

// 使用统一的数字格式化工具函数
const formatCount = (value) => {
  return formatCompactNumber(value);
};

// 点赞文章
const handleLike = async () => {
  if (!props.article?.id) {
    ElMessage.warning("文章信息异常");
    return;
  }

  if (likeLoading.value) {
    return; // 防止重复点击
  }

  try {
    likeLoading.value = true;

    // 调用后端接口切换点赞状态
    await toggleLike(0, props.article.id); // 0 表示文章类型

    // 更新本地文章数据
    const updatedArticle = { ...props.article };
    if (updatedArticle.isLiked) {
      // 取消点赞
      updatedArticle.isLiked = false;
      updatedArticle.likeCount = Math.max(0, (updatedArticle.likeCount || 0) - 1);
      ElMessage.success("取消点赞成功");
    } else {
      // 点赞
      updatedArticle.isLiked = true;
      updatedArticle.likeCount = (updatedArticle.likeCount || 0) + 1;
      ElMessage.success("点赞成功");
    }

    // 通知父组件更新文章数据
    emit("updateArticle", updatedArticle);
  } catch (error) {
    // 静默处理
  } finally {
    likeLoading.value = false;
  }
};

// 收藏文章
const handleCollect = () => {
  if (!props.article?.id) {
    ElMessage.warning("文章信息异常");
    return;
  }
  favoriteDialogVisible.value = true;
};

// 处理收藏成功
const handleFavoriteSuccess = (result) => {
  // 更新文章的收藏状态和收藏数
  const updatedArticle = { ...props.article };

  if (result.action === "add") {
    // 添加收藏：如果之前没有收藏过任何收藏夹，现在收藏了
    if (!updatedArticle.isCollected) {
      updatedArticle.isCollected = true;
      updatedArticle.collectCount = (updatedArticle.collectCount || 0) + 1;
    }
    // 如果之前已经收藏过（在其他收藏夹中），只更新状态，不增加数量
  } else if (result.action === "remove") {
    // 取消收藏：只有当没有其他收藏夹收藏了这篇文章时，才更新状态和数量
    if (!result.hasOtherCollected) {
      updatedArticle.isCollected = false;
      updatedArticle.collectCount = Math.max(0, (updatedArticle.collectCount || 0) - 1);
    }
    // 如果还有其他收藏夹收藏了这篇文章，保持 isCollected=true 和 collectCount 不变
  }

  // 通知父组件更新文章数据
  emit("updateArticle", updatedArticle);
};

// 评论文章
const handleComment = () => {
  commentDrawerVisible.value = true;
};

// 返回顶部
const scrollToTop = () => {
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: "smooth" });
};
</script>

<style lang="scss" scoped>
// 文章底部操作栏 - 固定在视窗底部，使用稳定的居中定位
.article-actions {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 9999;
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 16px 24px;
  background: var(--el-bg-color);
  backdrop-filter: blur(2px);
  background-color: color-mix(in srgb, var(--el-bg-color) 50%, transparent);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 24px;
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  max-width: 400px;
  width: auto;

  .action-item {
    .el-button {
      min-width: 100px;
      border-radius: 20px;
    }
  }
}

// 返回顶部按钮样式 - 极简设计
.back-to-top {
  position: fixed;
  z-index: 9999;
  right: 24px;
  bottom: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  backdrop-filter: blur(8px);
  background-color: var(--el-bg-color);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
  color: var(--el-text-color-secondary);

  &:hover {
    background-color: var(--el-color-primary);
    color: var(--el-color-white);
    border-color: var(--el-color-primary);
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    transform: translateY(-2px);
  }

  .el-icon {
    font-size: 16px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-actions {
    left: 50%;
    transform: translateX(-50%);
    bottom: 30px;
    padding: 12px 20px;
    gap: 16px;
    max-width: 320px;

    .action-item {
      .el-button {
        min-width: 80px;
        font-size: 14px;
        padding: 8px 16px;
      }
    }
  }

  // 移动端返回顶部按钮调整
  .back-to-top {
    right: 16px;
    bottom: 100px;
    width: 40px;
    height: 40px;
    border-radius: 8px;

    .el-icon {
      font-size: 16px;
    }
  }
}
</style>
