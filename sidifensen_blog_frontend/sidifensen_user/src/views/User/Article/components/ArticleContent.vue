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
                <el-icon>
                  <Clock />
                </el-icon>
                {{ article.createTime }}
              </span>
              <span class="read-count">
                <el-icon>
                  <View />
                </el-icon>
                {{ article.readCount || 0 }} 阅读
              </span>
              <span class="like-count">
                <svg-icon name="like" width="16px" height="16px" color="#909399" />
                {{ article.likeCount || 0 }} 点赞
              </span>
              <span class="collect-count">
                <el-icon>
                  <Star />
                </el-icon>
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
        </div>
      </template>
    </el-skeleton>
  </div>

  <!-- 文章底部操作栏 -->
  <div class="article-actions" v-if="article">
    <div class="action-item">
      <el-button :type="article.isLiked ? 'primary' : 'default'" @click="handleLike">
        <svg-icon name="like" width="16px" height="16px" margin-right="6px" :color="article.isLiked ? '#ffffff' : '#909399'" />
        {{ article.likeCount || 0 }}
      </el-button>
    </div>
    <div class="action-item">
      <el-button :type="article.isCollected ? 'primary' : 'default'" :icon="article.isCollected ? StarFilled : Star" @click="handleCollect">
        {{ article.collectCount || 0 }}
      </el-button>
    </div>
    <div class="action-item">
      <el-button :icon="ChatLineRound" @click="handleComment">
        {{ article.commentCount || 0 }}
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { ElMessage } from "element-plus";
import { Clock, View, Star, StarFilled, Collection, CollectionTag, ChatLineRound, Picture, Loading } from "@element-plus/icons-vue";

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

// 渲染富文本内容，处理图片加载状态
const renderContent = computed(() => {
  if (!props.article?.content) return "";

  // 将img标签替换为带加载和错误处理的结构
  let content = props.article.content;

  // 匹配所有img标签并替换
  content = content.replace(/<img([^>]+)>/g, (match, attributes) => {
    // 提取src属性
    const srcMatch = attributes.match(/src\s*=\s*["']([^"']+)["']/);
    const altMatch = attributes.match(/alt\s*=\s*["']([^"']*)["']/);

    const src = srcMatch ? srcMatch[1] : "";
    const alt = altMatch ? altMatch[1] : "图片";

    if (!src) return match; // 如果没有src，保持原样

    // 生成唯一ID用于图片容器
    const imageId = "img_" + Math.random().toString(36).substr(2, 9);

    return `
      <div class="image-container" id="${imageId}">
        <div class="loading-placeholder" id="loading_${imageId}" style="display: flex;">
          <svg class="loading-icon" viewBox="0 0 1024 1024" width="32" height="32">
            <path d="M512 1024c-69.1 0-136.2-13.5-199.3-40.2C251.7 958 197 921 150.3 874.4c-46.7-46.7-83.7-101.4-109.9-162.4C13.5 648.2 0 581.1 0 512s13.5-136.2 40.2-199.3C66.4 251.7 103.4 197 150.1 150.3s101.4-83.7 162.4-109.9C375.8 13.5 442.9 0 512 0s136.2 13.5 199.3 40.2C772.3 66.4 827 103.4 873.7 150.1s83.7 101.4 109.9 162.4C1010.5 375.8 1024 442.9 1024 512s-13.5 136.2-40.2 199.3C957.6 772.3 920.6 827 873.9 873.7s-101.4 83.7-162.4 109.9C648.2 1010.5 581.1 1024 512 1024zM512 92.4c-231.3 0-419.6 188.3-419.6 419.6S280.7 931.6 512 931.6 931.6 743.3 931.6 512 743.3 92.4 512 92.4z" fill="currentColor"/>
            <path d="M744.7 821.8c13.6-15.6 32.7-23.6 52.6-18.8 19.9 4.8 36.1 19.2 42.8 39.1 6.7 19.9 2.3 41.7-11.3 57.3-13.6 15.6-32.7 23.6-52.6 18.8-19.9-4.8-36.1-19.2-42.8-39.1-6.7-19.9-2.3-41.7 11.3-57.3z" fill="currentColor"/>
          </svg>
          <span>加载中...</span>
        </div>
        <div class="error-placeholder" id="error_${imageId}" style="display: none;">
          <svg class="error-icon" viewBox="0 0 1024 1024" width="40" height="40">
            <path d="M832 64H192C87 64 0 151 0 256v512c0 105 87 192 192 192h640c105 0 192-87 192-192V256c0-105-87-192-192-192zm96 704c0 53-43 96-96 96H192c-53 0-96-43-96-96V256c0-53 43-96 96-96h640c53 0 96 43 96 96v512z" fill="currentColor"/>
            <path d="M304 456c52.9 0 96-43.1 96-96s-43.1-96-96-96-96 43.1-96 96 43.1 96 96 96zm0-128c17.7 0 32 14.3 32 32s-14.3 32-32 32-32-14.3-32-32 14.3-32 32-32z" fill="currentColor"/>
            <path d="M864 736H160c-17.7 0-32-14.3-32-32s14.3-32 32-32h704c17.7 0 32 14.3 32 32s-14.3 32-32 32z" fill="currentColor"/>
            <path d="M864 608H160c-17.7 0-32-14.3-32-32s14.3-32 32-32h704c17.7 0 32 14.3 32 32s-14.3 32-32 32z" fill="currentColor"/>
          </svg>
          <span>图片加载失败</span>
        </div>
        <img 
          src="${src}" 
          alt="${alt}"
          loading="lazy"
          style="width: 100%; height: auto; display: none;"
          onload="this.style.display='block'; document.getElementById('loading_${imageId}').style.display='none';"
          onerror="document.getElementById('loading_${imageId}').style.display='none'; document.getElementById('error_${imageId}').style.display='flex';"
        />
      </div>
    `;
  });

  return content;
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
  padding: 30px 30px; // 底部留出空间给固定操作栏
  position: relative;

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
          line-height: 1;
          // 确保文字垂直居中
          span,
          & {
            display: flex;
            align-items: center;
            line-height: 1;
          }
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
      margin-bottom: 100px; // 增加底部边距，避免被固定操作栏遮挡
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

      // 文章内容中的图片容器
      :deep(.image-container) {
        position: relative;
        display: block;
        width: 100%;
        margin: 16px 0;
        background-color: #f5f5f5;
        border-radius: 4px;
        overflow: hidden;

        img {
          width: 100%;
          height: auto;
          display: block;
          border-radius: 4px;
        }

        // 加载中样式
        .loading-placeholder {
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          min-height: 200px;
          background-color: #f5f5f5;
          color: #606266;
          font-size: 14px;

          .loading-icon {
            color: #606266;
            margin-bottom: 8px;
            animation: spin 2s linear infinite;
          }

          @keyframes spin {
            from {
              transform: rotate(0deg);
            }
            to {
              transform: rotate(360deg);
            }
          }

          span {
            margin-top: 8px;
          }
        }

        // 错误占位图标样式
        .error-placeholder {
          display: flex;
          flex-direction: column;
          justify-content: center;
          align-items: center;
          min-height: 200px;
          background-color: #f5f5f5;
          color: #909399;
          font-size: 14px;

          .error-icon {
            color: #909399;
            margin-bottom: 8px;
          }

          span {
            margin-top: 8px;
          }
        }
      }

      :deep(img:not(.image-container img)) {
        width: 100%;
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
  }
}

// 文章底部操作栏 - 固定在视窗底部，但限制在文章内容区域宽度内
.article-actions {
  position: fixed;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 999;
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 16px 24px;
  background: var(--el-bg-color);
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

// 响应式设计
@media (max-width: 768px) {
  .article-content {
    padding: 20px 15px;

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

        :deep(img) {
          width: 100%;
          max-width: 100%;
          border-radius: 4px;
        }

        :deep(pre) {
          max-width: 100%;
          overflow-x: auto;
        }

        :deep(table) {
          max-width: 100%;
          overflow-x: auto;
        }
      }
    }
  }

  // 移动端操作栏调整
  .article-actions {
    bottom: 56px;
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
}
</style>
