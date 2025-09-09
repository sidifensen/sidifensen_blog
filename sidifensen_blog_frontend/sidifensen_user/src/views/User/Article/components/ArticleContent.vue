<template>
  <div class="article-content">
    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item variant="h1" style="width: 50%; margin-bottom: 20px" />
          <div class="article-meta-skeleton">
            <el-skeleton-item variant="text" style="width: 100px" />
            <el-skeleton-item variant="text" style="width: 100px" />
            <el-skeleton-item variant="text" style="width: 100px" />
          </div>
          <el-skeleton-item variant="text" style="width: 100%; height: 300px; margin: 20px 0" />
          <el-skeleton-item variant="text" style="width: 90%" />
          <el-skeleton-item variant="text" style="width: 95%" />
          <el-skeleton-item variant="text" style="width: 85%" />
        </div>
      </template>

      <!-- 实际内容 -->
      <template #default>
        <div class="article-main" v-if="article">
          <!-- 文章标题 -->
          <h1 class="article-title">{{ article.title }}</h1>

          <!-- 文章元信息 -->
          <div class="article-meta">
            <div class="meta-left">
              <span class="reprint-type">
                <el-tag :type="article.reprintType === 0 ? 'success' : 'warning'" size="small" effect="light">
                  {{ article.reprintType === 0 ? "原创" : "转载" }}
                </el-tag>
              </span>
              <span class="publish-time">
                <el-icon><Clock /></el-icon>
                {{ article.createTime }}
              </span>
              <span class="read-count">
                <el-icon><View /></el-icon>
                {{ article.readCount || 0 }} 阅读
              </span>
              <span class="like-count">
                <el-icon><Star /></el-icon>
                {{ article.likeCount || 0 }} 点赞
              </span>
              <span class="collect-count">
                <el-icon><Collection /></el-icon>
                {{ article.collectCount || 0 }} 收藏
              </span>
            </div>
            <div class="meta-right">
              <el-tag v-for="tag in tagList" :key="tag" size="small" effect="light">
                {{ tag }}
              </el-tag>
              <el-tag v-for="column in article.columns || []" :key="column.id" type="success" size="small" effect="light">
                {{ column.name }}
              </el-tag>
            </div>
          </div>

          <!-- 文章描述 -->
          <div class="article-desc" v-if="article.description">
            <el-alert :title="article.description" type="info" :closable="false" />
          </div>

          <!-- 文章内容 -->
          <div class="article-body" v-html="renderContent"></div>

          <!-- 文章底部操作栏 -->
          <div class="article-actions">
            <div class="action-item">
              <el-button :type="article.isLiked ? 'primary' : 'default'" :icon="article.isLiked ? Star : StarFilled" @click="handleLike">
                {{ article.likeCount || 0 }}
              </el-button>
            </div>
            <div class="action-item">
              <el-button :type="article.isCollected ? 'primary' : 'default'" :icon="article.isCollected ? Collection : CollectionTag" @click="handleCollect">
                {{ article.collectCount || 0 }}
              </el-button>
            </div>
            <div class="action-item">
              <el-button :icon="ChatLineRound" @click="handleComment">
                {{ article.commentCount || 0 }}
              </el-button>
            </div>
          </div>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { ElMessage } from "element-plus";
import { Clock, View, Star, StarFilled, Collection, CollectionTag, ChatLineRound } from "@element-plus/icons-vue";

// Props 定义
const props = defineProps({
  article: {
    type: Object,
    default: () => null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

// 渲染富文本内容（直接返回HTML）
const renderContent = computed(() => {
  if (!props.article?.content) return "";
  return props.article.content;
});

// 处理标签列表
const tagList = computed(() => {
  if (!props.article?.tag) return [];
  return props.article.tag.split(",").filter((tag) => tag.trim() !== "");
});

// 点赞文章
const handleLike = () => {
  ElMessage.info("点赞功能开发中...");
};

// 收藏文章
const handleCollect = () => {
  ElMessage.info("收藏功能开发中...");
};

// 评论文章
const handleComment = () => {
  ElMessage.info("评论功能开发中...");
};
</script>

<style lang="scss" scoped>
// 文章内容容器
.article-content {
  padding: 30px;

  // 骨架屏样式
  .skeleton-content {
    .article-meta-skeleton {
      display: flex;
      gap: 20px;
      margin: 16px 0;
    }
  }

  // 文章主体
  .article-main {
    // 文章标题
    .article-title {
      margin: 0 0 20px;
      font-size: 28px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      line-height: 1.4;
    }

    // 文章元信息
    .article-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 20px;
      padding-bottom: 20px;
      border-bottom: 1px solid var(--el-border-color-lighter);

      .meta-left {
        display: flex;
        gap: 20px;
        color: var(--el-text-color-secondary);
        font-size: 14px;

        .reprint-type {
          display: flex;
          align-items: center;
        }

        .publish-time,
        .read-count,
        .like-count,
        .collect-count {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }

      .meta-right {
        display: flex;
        gap: 8px;
      }
    }

    // 文章描述
    .article-desc {
      margin-bottom: 30px;
    }

    // 文章内容
    .article-body {
      margin-bottom: 40px;
      line-height: 1.8;
      font-size: 16px;
      color: var(--el-text-color-primary);

      :deep(h1, h2, h3, h4, h5, h6) {
        margin-top: 24px;
        margin-bottom: 16px;
        font-weight: 600;
        line-height: 1.25;
      }

      :deep(p) {
        margin-bottom: 16px;
      }

      :deep(img) {
        max-width: 100%;
        border-radius: 4px;
      }

      :deep(pre) {
        padding: 16px;
        overflow: auto;
        font-size: 14px;
        line-height: 1.45;
        background-color: var(--el-fill-color-light);
        border-radius: 4px;
      }

      :deep(code) {
        padding: 0.2em 0.4em;
        font-size: 85%;
        background-color: var(--el-fill-color-light);
        border-radius: 3px;
      }

      :deep(blockquote) {
        padding: 0 1em;
        color: var(--el-text-color-secondary);
        border-left: 0.25em solid var(--el-border-color);
      }
    }

    // 文章底部操作栏
    .article-actions {
      display: flex;
      justify-content: center;
      gap: 20px;
      padding-top: 20px;
      border-top: 1px solid var(--el-border-color-lighter);

      .action-item {
        .el-button {
          min-width: 100px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-content {
    padding: 20px;

    .article-main {
      .article-title {
        font-size: 24px;
      }

      .article-meta {
        flex-direction: column;
        align-items: flex-start;
        gap: 10px;

        .meta-left {
          gap: 15px;
          flex-wrap: wrap;
        }
      }

      .article-body {
        font-size: 15px;
      }
    }
  }
}
</style>
