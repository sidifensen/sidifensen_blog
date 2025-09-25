<template>
  <div class="article-list-wrapper">
    <div class="article-list-section">
      <div v-if="articleLoading" class="loading-container">
        <el-skeleton animated :count="5">
          <template #template>
            <div class="article-skeleton">
              <el-skeleton-item variant="image" style="width: 100px; height: 80px" />
              <div class="skeleton-content">
                <el-skeleton-item variant="h3" style="width: 70%" />
                <el-skeleton-item variant="text" style="width: 100%" />
                <el-skeleton-item variant="text" style="width: 60%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>

      <div v-else-if="articleList.length === 0" class="empty-state">
        <el-empty description="暂无文章" />
      </div>

      <div v-else class="article-list">
        <div v-for="article in articleList" :key="article.id" class="article-item" @click="handleArticleClick(article.id)">
          <!-- 文章封面 -->
          <el-image :src="article.coverUrl || ''" class="article-cover">
            <template #placeholder>
              <div class="loading-text">加载中...</div>
            </template>
            <template #error>
              <div class="error">
                <el-icon>
                  <Picture />
                </el-icon>
              </div>
            </template>
          </el-image>

          <!-- 文章内容 -->
          <div class="article-content">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-description">{{ article.description }}</p>

            <!-- 文章元信息 -->
            <div class="article-meta">
              <!-- 第一行：文章类型、审核状态、发布时间 -->
              <div class="article-meta-primary">
                <span class="article-type">{{ getArticleType(article.type) }}</span>
                <span v-if="isCurrentUser && article.examineStatus !== 1" class="article-examine-status" :class="'status-' + article.examineStatus">
                  {{ getExamineStatus(article.examineStatus) }}
                </span>
                <span class="article-date">{{ article.createTime }}</span>
              </div>
              <!-- 第二行：统计数据 -->
              <div class="article-meta-stats">
                <span class="article-readCount">{{ article.readCount }} 阅读</span>
                <span class="article-likes">{{ article.likeCount || 0 }} 点赞</span>
                <span class="article-favorites">{{ article.collectCount || 0 }} 收藏</span>
                <span class="article-comments">{{ article.commentCount }} 评论</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 加载更多指示器 -->
        <div v-if="loadingMore" class="loading-more">
          <div class="loading-spinner"></div>
          <span>加载更多...</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Picture } from "@element-plus/icons-vue";

// 定义 props
const props = defineProps({
  articleList: {
    type: Array,
    default: () => [],
  },
  articleLoading: {
    type: Boolean,
    default: false,
  },
  loadingMore: {
    type: Boolean,
    default: false,
  },
  isCurrentUser: {
    type: Boolean,
    default: false,
  },
});

// 定义 emits
const emit = defineEmits(["article-click"]);

// 处理文章点击事件
const handleArticleClick = (articleId) => {
  emit("article-click", articleId);
};

// 获取文章类型
const getArticleType = (type) => {
  const typeMap = {
    0: "原创",
    1: "转载",
  };
  return typeMap[type] || "原创";
};

// 获取审核状态
const getExamineStatus = (status) => {
  const statusMap = {
    0: "待审核",
    1: "审核通过",
    2: "审核未通过",
  };
  return statusMap[status] || "审核通过";
};
</script>

<style lang="scss" scoped>
// 全局变量
$primary-color: #409eff;
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$border-color: #dcdfe6;
$bg-color: #f5f7fa;

// 文章列表包装器
.article-list-wrapper {
  position: relative; // 为返回顶部按钮提供定位参考
}

// 文章列表区域
.article-list-section {
  background: var(--el-bg-color-page);
  border-radius: 8px;
  padding: 20px;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 12px var(--el-border-color-light);
  min-height: 580px; // 设置最小高度

  // 加载容器样式
  .loading-container {
    padding: 20px 0;
  }

  // 骨架屏样式
  .article-skeleton {
    display: flex;
    gap: 16px;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-light);

    &:last-child {
      border-bottom: none;
    }

    .skeleton-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
  }

  // 文章列表
  .article-list {
    .article-item {
      display: flex;
      gap: 16px;
      padding: 20px 0;
      border-bottom: 1px solid var(--el-border-color-light);
      cursor: pointer;
      transition: all 0.3s ease;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        background-color: var(--el-bg-color-page);
        transform: translateX(4px);
      }

      // 文章封面
      .article-cover {
        width: 160px;
        height: 100px;
        border-radius: 6px;
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.05);
        }

        .loading-text {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          font-size: 16px;
          color: var(--el-text-color-primary);
          background-color: var(--el-bg-color-page);
        }

        // 错误占位图标样式
        .error {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          background-color: var(--el-bg-color-page);

          .el-icon {
            font-size: 40px;
            color: var(--el-text-color-primary);
          }
        }
      }

      // 文章内容
      .article-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .article-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin: 0 0 8px 0;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .article-description {
          font-size: 14px;
          color: var(--el-text-color-regular);
          margin: 0 0 12px 0;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        // 文章元信息
        .article-meta {
          font-size: 13px;
          color: var(--el-text-color-secondary);

          // 第一行：文章类型、审核状态、发布时间
          .article-meta-primary {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;
          }

          // 第二行：统计数据
          .article-meta-stats {
            display: flex;
            align-items: center;
            gap: 10px;
          }

          // 桌面端单行显示
          @media (min-width: 769px) {
            .article-meta-primary {
              margin-bottom: 0;
            }

            .article-meta-primary,
            .article-meta-stats {
              display: inline-flex;
            }

            .article-meta-stats {
              margin-left: 10px;
            }

            .article-meta-stats::before {
              content: "•";
              margin-right: 10px;
              color: var(--el-text-color-placeholder);
            }
          }

          .article-type {
            background-color: #f0f9ff;
            color: $primary-color;
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 12px;
          }

          // 审核状态样式
          .article-examine-status {
            padding: 2px 8px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;

            // 待审核状态 - 橙色
            &.status-0 {
              background-color: #fff7ed;
              color: #ea580c;
              border: 1px solid #fed7aa;
            }

            // 审核未通过状态 - 红色
            &.status-2 {
              background-color: #fef2f2;
              color: #dc2626;
              border: 1px solid #fecaca;
            }
          }

          .article-date,
          .article-readCount,
          .article-likes,
          .article-favorites,
          .article-comments {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
    }
  }

  // 空状态
  .empty-state {
    padding: 60px 0;
    text-align: center;
  }

  // 加载更多指示器
  .loading-more {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 30px;
    color: var(--el-text-color-primary);

    .loading-spinner {
      margin-right: 10px;
    }
  }
}

// 自定义加载指示器样式
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #409eff;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  display: inline-block;
  vertical-align: middle;
}

// 加载动画
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-list-section {
    .article-list {
      .article-item {
        flex-direction: column;
        gap: 12px;

        .article-cover {
          width: 100%;
          height: 180px;
        }
      }
    }
  }
}
</style>
