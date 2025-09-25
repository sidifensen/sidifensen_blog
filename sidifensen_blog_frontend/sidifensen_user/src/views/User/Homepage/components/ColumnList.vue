<template>
  <div class="column-list-wrapper">
    <div class="column-list-section">
      <div v-if="columnLoading" class="loading-container">
        <el-skeleton animated :count="3">
          <template #template>
            <div class="column-skeleton">
              <el-skeleton-item variant="image" style="width: 120px; height: 90px" />
              <div class="skeleton-content">
                <el-skeleton-item variant="h3" style="width: 60%" />
                <el-skeleton-item variant="text" style="width: 100%" />
                <el-skeleton-item variant="text" style="width: 40%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>

      <div v-else-if="columnList.length === 0" class="empty-state">
        <el-empty description="暂无专栏" />
      </div>

      <div v-else class="column-list">
        <div v-for="column in columnList" :key="column.id" class="column-item" @click="handleColumnClick(column.id)">
          <!-- 专栏封面 -->
          <el-image :src="column.coverUrl || ''" class="column-cover">
            <template #placeholder>
              <div class="loading-text">加载中...</div>
            </template>
            <template #error>
              <div class="error">
                <el-icon>
                  <Collection />
                </el-icon>
              </div>
            </template>
          </el-image>

          <!-- 专栏内容 -->
          <div class="column-content">
            <h3 class="column-title">{{ column.name }}</h3>
            <p class="column-description">{{ column.description || "暂无描述" }}</p>

            <!-- 专栏元信息 -->
            <div class="column-meta">
              <!-- 第一行：统计数据 -->
              <div class="column-meta-stats">
                <span class="column-articles">
                  <el-icon><Document /></el-icon>
                  {{ column.articleCount || 0 }} 文章
                </span>
                <span class="column-focus">
                  <el-icon><Star /></el-icon>
                  {{ column.focusCount || 0 }} 关注
                </span>
              </div>
              <!-- 第二行：创建时间 -->
              <div class="column-meta-time">
                <span class="column-date">创建于 {{ column.createTime }}</span>
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
import { Collection, Document, Star } from "@element-plus/icons-vue";

// 定义 props
const props = defineProps({
  columnList: {
    type: Array,
    default: () => [],
  },
  columnLoading: {
    type: Boolean,
    default: false,
  },
  loadingMore: {
    type: Boolean,
    default: false,
  },
});

// 定义 emits
const emit = defineEmits(["column-click"]);

// 处理专栏点击事件
const handleColumnClick = (columnId) => {
  emit("column-click", columnId);
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

// 专栏列表包装器
.column-list-wrapper {
  position: relative; // 为返回顶部按钮提供定位参考
}

// 专栏列表区域
.column-list-section {
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
  .column-skeleton {
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

  // 专栏列表
  .column-list {
    .column-item {
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

      // 专栏封面
      .column-cover {
        width: 120px;
        height: 90px;
        border-radius: 8px;
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
          font-size: 12px;
          color: var(--el-text-color-regular);
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
            font-size: 24px;
            color: var(--el-text-color-placeholder);
          }
        }
      }

      // 专栏内容
      .column-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        justify-content: space-between;

        .column-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin: 0 0 8px 0;
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 1;
          line-clamp: 1;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .column-description {
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

        // 专栏元信息
        .column-meta {
          display: flex;
          align-items: center;
          font-size: 13px;
          color: var(--el-text-color-secondary);

          // 第一行：统计数据
          .column-meta-stats {
            gap: 16px;
            margin-bottom: 8px;

            .column-articles,
            .column-focus {
              display: flex;
              align-items: center;
              gap: 4px;

              .el-icon {
                font-size: 14px;
              }
            }
          }

          // 第二行：创建时间
          .column-meta-time {
            .column-date {
              color: var(--el-text-color-placeholder);
            }
          }

          // 桌面端单行显示
          @media (min-width: 769px) {
            .column-meta-stats {
              margin-bottom: 0;
            }

            .column-meta-stats,
            .column-meta-time {
              display: inline-flex;
            }

            .column-meta-time {
              margin-left: 16px;
            }

            .column-meta-time::before {
              content: "•";
              margin-right: 8px;
              color: var(--el-text-color-placeholder);
            }
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
  .column-list-section {
    .column-list {
      .column-item {
        flex-direction: column;
        gap: 12px;

        .column-cover {
          width: 100%;
          height: 180px;
        }
      }
    }
  }
}
</style>
