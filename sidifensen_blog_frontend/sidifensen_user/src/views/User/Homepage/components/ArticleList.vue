<template>
  <div class="article-list-wrapper">
    <!-- 文章筛选标签 -->
    <div class="article-filters">
      <!-- 文章筛选条件 -->
      <div class="filter-controls">
        <!-- 可见范围筛选 -->
        <div v-if="isCurrentUser" class="visibility-filter">
          <el-select v-model="visibilityType" @change="handleVisibilityChange" placeholder="可见范围" size="default">
            <el-option label="全部可见" value="all" />
            <el-option label="仅我可见" value="private" />
            <el-option label="审核中&失败" value="pending" />
          </el-select>
        </div>
        <!-- 排序筛选条件 -->
        <div class="sort-filters">
          <el-radio-group v-model="sortType" @change="handleSortChange">
            <el-radio value="time">时间排序</el-radio>
            <el-radio value="views">阅读量排序</el-radio>
          </el-radio-group>
        </div>
      </div>
    </div>

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
        <div
          v-for="(article, index) in articleList"
          :key="article.id"
          class="article-item"
          :style="{ animationDelay: `${index * 0.05}s` }"
          @click="handleArticleClick(article.id)"
        >
          <!-- 文章封面 -->
          <div class="article-cover-wrapper">
            <el-image :src="article.coverUrl || ''" class="article-cover">
              <template #placeholder>
                <div class="loading-text">
                  <el-icon class="loading-icon"><Picture /></el-icon>
                </div>
              </template>
              <template #error>
                <div class="error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="cover-overlay"></div>
          </div>

          <!-- 文章内容 -->
          <div class="article-content">
            <h3 class="article-title">{{ article.title }}</h3>
            <p class="article-description">{{ article.description }}</p>

            <!-- 文章元信息 -->
            <div class="article-meta">
              <!-- 第一行：文章类型、审核状态、发布时间 -->
              <div class="article-meta-primary">
                <span class="article-type">{{ getArticleType(article.type) }}</span>
                <span
                  v-if="isCurrentUser && article.examineStatus !== 1"
                  class="article-examine-status"
                  :class="'status-' + article.examineStatus"
                >
                  {{ getExamineStatus(article.examineStatus) }}
                </span>
                <span class="article-date">
                  <el-icon><Clock /></el-icon>
                  {{ article.createTime }}
                </span>
              </div>
              <!-- 第二行：统计数据 -->
              <div class="article-meta-stats">
                <span class="article-stat">
                  <el-icon><View /></el-icon>
                  {{ article.readCount }}
                </span>
                <span class="article-stat">
                  <el-icon><Star /></el-icon>
                  {{ article.likeCount || 0 }}
                </span>
                <span class="article-stat">
                  <el-icon><Collection /></el-icon>
                  {{ article.collectCount || 0 }}
                </span>
                <span class="article-stat">
                  <el-icon><ChatDotRound /></el-icon>
                  {{ article.commentCount }}
                </span>
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
import { ref, watch } from "vue";
import { Picture, Clock, View, Star, Collection, ChatDotRound } from "@element-plus/icons-vue";

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
  sortType: {
    type: String,
    default: "time",
  },
  visibilityType: {
    type: String,
    default: "all",
  },
});

// 定义 emits
const emit = defineEmits(["article-click", "sort-change", "visibility-change"]);

// 响应式数据
const sortType = ref(props.sortType);
const visibilityType = ref(props.visibilityType);

// 监听 props 变化
watch(
  () => props.sortType,
  (newValue) => {
    sortType.value = newValue;
  }
);

watch(
  () => props.visibilityType,
  (newValue) => {
    visibilityType.value = newValue;
  }
);

// 处理文章点击事件
const handleArticleClick = (articleId) => {
  emit("article-click", articleId);
};

// 处理排序条件变化
const handleSortChange = (value) => {
  sortType.value = value;
  emit("sort-change", value);
};

// 处理可见范围变化
const handleVisibilityChange = (value) => {
  visibilityType.value = value;
  emit("visibility-change", value);
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
.article-list-wrapper {
  --filter-bg: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.9);
  --filter-border: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.6);
  --filter-shadow: rgba(0, 0, 0, 0.04);
  --filter-shadow-light: rgba(0, 0, 0, 0.02);
  --radio-hover-bg: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.08);
  --radio-checked-bg: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.1);
  position: relative;

  // 黑夜模式适配
  html.dark & {
    --filter-bg: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.9);
    --filter-border: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.6);
    --filter-shadow: rgba(0, 0, 0, 0.2);
    --filter-shadow-light: rgba(0, 0, 0, 0.1);
    --radio-hover-bg: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.1);
    --radio-checked-bg: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.15);
  }

  // 文章筛选标签
  .article-filters {
    background: var(--filter-bg);
    backdrop-filter: blur(20px);
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 16px;
    border: 1px solid var(--filter-border);
    box-shadow:
      0 4px 24px var(--filter-shadow),
      0 1px 3px var(--filter-shadow-light);

    .filter-controls {
      display: flex;
      align-items: center;
      gap: 16px;
      flex-wrap: wrap;

      .visibility-filter {
        .el-select {
          width: 110px;
        }
      }

      .sort-filters {
        .el-radio-group {
          display: flex;
          gap: 20px;

          .el-radio {
            margin-right: 0;
            font-size: 14px;
            color: var(--el-text-color-regular);
            padding: 6px 12px;
            border-radius: 8px;
            transition: all 0.3s ease;

            &:hover {
              background: var(--radio-hover-bg);
            }

            &.is-checked {
              color: var(--el-color-primary);
              background: var(--radio-checked-bg);
              font-weight: 500;
            }
          }
        }
      }
    }
  }
}

// 文章列表区域
.article-list-section {
  --section-bg: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.9);
  --section-border: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.6);
  --section-shadow: rgba(0, 0, 0, 0.04);
  --section-shadow-light: rgba(0, 0, 0, 0.02);
  --article-bg: linear-gradient(135deg, rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.8) 0%, rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.4) 100%);
  --article-border: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.5);
  --article-hover-bg: linear-gradient(135deg, rgba(var(--el-bg-color-rgb, 255, 255, 255), 1) 0%, rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.7) 100%);
  --article-hover-border: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.3);
  --article-hover-shadow: rgba(0, 0, 0, 0.08);
  --article-hover-shadow-light: rgba(0, 0, 0, 0.04);
  --primary-rgb: var(--el-color-primary-rgb, 64, 158, 255);
  --overlay-bg: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.2);
  background: var(--section-bg);
  backdrop-filter: blur(20px);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid var(--section-border);
  box-shadow:
    0 4px 24px var(--section-shadow),
    0 1px 3px var(--section-shadow-light);
  min-height: 580px;

  // 黑夜模式适配
  html.dark & {
    --section-bg: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.9);
    --section-border: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.6);
    --section-shadow: rgba(0, 0, 0, 0.2);
    --section-shadow-light: rgba(0, 0, 0, 0.1);
    --article-bg: linear-gradient(135deg, rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.8) 0%, rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.4) 100%);
    --article-border: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.5);
    --article-hover-bg: linear-gradient(135deg, rgba(var(--el-bg-color-rgb, 30, 41, 59), 1) 0%, rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.7) 100%);
    --article-hover-border: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.3);
    --article-hover-shadow: rgba(0, 0, 0, 0.3);
    --article-hover-shadow-light: rgba(0, 0, 0, 0.15);
    --overlay-bg: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.15);
  }

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
      gap: 20px;
      padding: 20px;
      margin-bottom: 16px;
      background: var(--article-bg);
      border-radius: 12px;
      border: 1px solid var(--article-border);
      cursor: pointer;
      transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      opacity: 0;
      animation: articleSlideIn 0.5s cubic-bezier(0.4, 0, 0.2, 1) forwards;
      position: relative;
      overflow: hidden;

      // 装饰性光晕
      &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        width: 3px;
        height: 100%;
        background: linear-gradient(180deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
        opacity: 0;
        transition: opacity 0.3s ease;
      }

      &:hover {
        background: var(--article-hover-bg);
        border-color: var(--article-hover-border);
        box-shadow:
          0 8px 32px var(--article-hover-shadow),
          0 2px 8px var(--article-hover-shadow-light);
        transform: translateX(8px) scale(1.01);

        &::before {
          opacity: 1;
        }

        .article-cover {
          transform: scale(1.08);
        }

        .cover-overlay {
          opacity: 0.1;
        }
      }

      // 文章封面包装器
      .article-cover-wrapper {
        position: relative;
        flex-shrink: 0;

        .article-cover {
          width: 180px;
          height: 120px;
          border-radius: 10px;
          transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
          overflow: hidden;
          box-shadow: 0 4px 16px var(--section-shadow);

          .loading-text {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, var(--el-fill-color-light) 0%, var(--el-fill-color) 100%);

            .loading-icon {
              font-size: 32px;
              color: var(--el-text-color-placeholder);
              animation: pulse 1.5s ease-in-out infinite;
            }
          }

          .error {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, var(--el-fill-color-light) 0%, var(--el-fill-color) 100%);

            .el-icon {
              font-size: 40px;
              color: var(--el-text-color-placeholder);
            }
          }
        }

        .cover-overlay {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          background: linear-gradient(135deg, var(--overlay-bg) 0%, transparent 50%);
          opacity: 0;
          transition: opacity 0.4s ease;
          pointer-events: none;
          border-radius: 10px;
        }
      }

      // 文章内容
      .article-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;
        min-width: 0;

        .article-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin: 0 0 10px 0;
          line-height: 1.5;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          transition: color 0.3s ease;
        }

        .article-description {
          font-size: 14px;
          color: var(--el-text-color-regular);
          margin: 0 0 14px 0;
          line-height: 1.6;
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
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          align-items: center;

          .article-meta-primary {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
          }

          .article-meta-stats {
            display: flex;
            align-items: center;
            gap: 12px;
          }

          .article-type {
            --type-bg-start: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.15);
            --type-bg-end: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.05);
            --type-border: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.2);
            background: linear-gradient(135deg, var(--type-bg-start) 0%, var(--type-bg-end) 100%);
            color: var(--el-color-primary);
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;
            border: 1px solid var(--type-border);

            // 黑夜模式适配
            html.dark & {
              --type-bg-start: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.2);
              --type-bg-end: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.08);
              --type-border: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.3);
            }
          }

          .article-examine-status {
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 500;

            // 待审核 - 橙色
            &.status-0 {
              --pending-bg: #fff7ed;
              --pending-color: #ea580c;
              --pending-border: #fed7aa;
              background-color: var(--pending-bg);
              color: var(--pending-color);
              border: 1px solid var(--pending-border);
            }

            // 审核未通过 - 红色
            &.status-2 {
              --rejected-bg: #fef2f2;
              --rejected-color: #dc2626;
              --rejected-border: #fecaca;
              background-color: var(--rejected-bg);
              color: var(--rejected-color);
              border: 1px solid var(--rejected-border);
            }

            // 黑夜模式适配
            html.dark & {
              // 待审核 - 橙色（深色模式）
              &.status-0 {
                --pending-bg: rgba(234, 88, 12, 0.15);
                --pending-color: #fdba74;
                --pending-border: rgba(234, 88, 12, 0.3);
              }

              // 审核未通过 - 红色（深色模式）
              &.status-2 {
                --rejected-bg: rgba(220, 38, 38, 0.15);
                --rejected-color: #fca5a5;
                --rejected-border: rgba(220, 38, 38, 0.3);
              }
            }
          }

          .article-date,
          .article-stat {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            transition: color 0.3s ease;
          }

          .article-date {
            .el-icon {
              font-size: 14px;
            }
          }

          .article-stat {
            .el-icon {
              font-size: 15px;
            }
          }
        }
      }
    }
  }

  // 空状态
  .empty-state {
    padding: 80px 0;
    text-align: center;
  }

  // 加载更多指示器
  .loading-more {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 30px;
    color: var(--el-text-color-primary);
    gap: 10px;
  }
}

// 自定义加载指示器样式
.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid var(--el-border-color-light);
  border-top-color: var(--el-color-primary);
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

// 动画定义
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}

@keyframes articleSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-list-section {
    padding: 16px;

    .article-list {
      .article-item {
        flex-direction: column;
        gap: 14px;
        padding: 16px;

        .article-cover-wrapper {
          .article-cover {
            width: 100%;
            height: 180px;
          }
        }
      }
    }
  }
}
</style>
