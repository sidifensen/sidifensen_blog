<template>
  <div class="hot-article-list">
    <!-- 加载中骨架屏 -->
    <div v-if="loading" class="hot-articles-loading">
      <el-skeleton animated :count="5">
        <template #template>
          <div class="hot-skeleton-item" />
        </template>
      </el-skeleton>
    </div>

    <!-- 空状态 -->
    <div v-else-if="articles.length === 0" class="hot-articles-empty">
      <el-empty :description="emptyText" :image-size="60" />
    </div>

    <!-- 热门文章列表 -->
    <div v-else class="hot-articles">
      <div
        v-for="(article, index) in articles"
        :key="article.id"
        class="hot-article-item"
        @click="handleArticleClick(article)"
      >
        <div class="hot-article-rank">{{ index + 1 }}</div>
        <div class="hot-article-content">
          <div class="hot-article-title">{{ article.title }}</div>
          <div class="hot-article-meta">
            <span
              ><el-icon><View /></el-icon> {{ formatCompactNumber(article.readCount) }}</span
            >
            <span class="hot-article-score">🔥 {{ formatCompactNumber(article.hotScore) }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { View } from '@element-plus/icons-vue'
import { ElSkeleton, ElSkeletonItem, ElEmpty } from 'element-plus'
import { formatCompactNumber } from '@/utils/formatNumber'

/**
 * 热门文章列表组件
 * @displayName HotArticleList
 */
defineOptions({
  name: 'HotArticleList',
})

const props = defineProps({
  /**
   * 文章列表数据
   */
  articles: {
    type: Array,
    required: true,
    default: () => [],
  },
  /**
   * 加载状态
   */
  loading: {
    type: Boolean,
    default: false,
  },
  /**
   * 空状态文本
   */
  emptyText: {
    type: String,
    default: '暂无热门文章',
  },
})

const emit = defineEmits(['article-click'])

/**
 * 处理文章点击事件
 * @param {Object} article - 被点击的文章对象
 */
const handleArticleClick = (article) => {
  emit('article-click', article)
}
</script>

<style lang="scss" scoped>
.hot-article-list {
  // ===== CSS 变量定义 - 浅色模式 =====
  --bg-subtle: rgba(0, 0, 0, 0.02);
  --border-light: rgba(0, 0, 0, 0.05);
  --text-muted: var(--text-muted);
  --rank-1-bg: rgba(var(--accent-rgb, 59, 130, 246), 0.1);
  --rank-1-color: var(--accent);
  --rank-2-bg: rgba(124, 58, 237, 0.1);
  --rank-2-color: #7c3aed;
  --rank-3-bg: rgba(16, 185, 129, 0.1);
  --rank-3-color: var(--success);
  --fire-color: var(--error);

  // ===== 骨架屏加载状态 =====
  .hot-articles-loading {
    .hot-skeleton-item {
      padding: 12px;
      border-radius: 6px;
      background: var(--bg-subtle);
      margin-bottom: 8px;
    }
  }

  // ===== 空状态 =====
  .hot-articles-empty {
    padding: 30px 0;
    text-align: center;
  }

  // ===== 热门文章列表 =====
  .hot-articles {
    display: flex;
    flex-direction: column;
    gap: 8px;

    .hot-article-item {
      display: flex;
      gap: 10px;
      padding: 12px;
      background: var(--bg-subtle);
      border: 1px solid var(--border-light);
      border-radius: 6px;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(0, 0, 0, 0.03);
        border-color: rgba(0, 0, 0, 0.1);

        .hot-article-title {
          color: var(--text-primary);
        }
      }

      // 排名徽章 - 24x24 圆角方块
      .hot-article-rank {
        width: 24px;
        height: 24px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 11px;
        font-weight: 700;
        background: var(--bg-subtle);
        border-radius: 4px;
        flex-shrink: 0;
        color: var(--text-muted);
        font-feature-settings: 'tnum';
      }

      // 前三名特殊颜色标记
      &:nth-child(1) .hot-article-rank {
        color: var(--rank-1-color);
        background: var(--rank-1-bg);
      }

      &:nth-child(2) .hot-article-rank {
        color: var(--rank-2-color);
        background: var(--rank-2-bg);
      }

      &:nth-child(3) .hot-article-rank {
        color: var(--rank-3-color);
        background: var(--rank-3-bg);
      }

      .hot-article-content {
        flex: 1;
        min-width: 0;

        .hot-article-title {
          font-size: 13px;
          font-weight: 500;
          color: var(--text-regular);
          line-height: 1.4;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          transition: color 0.2s ease;

          &:hover {
            color: var(--text-primary);
          }
        }

        .hot-article-meta {
          display: flex;
          gap: 12px;
          font-size: 10px;
          color: var(--text-muted);
          margin-top: 6px;
          font-feature-settings: 'tnum';

          .hot-article-score {
            color: var(--fire-color) !important;
            font-weight: 600;
          }

          .el-icon {
            font-size: 13px;
          }
        }
      }
    }
  }

  // ===== 黑夜模式覆盖 =====
  html.dark & {
    --bg-subtle: rgba(255, 255, 255, 0.02);
    --border-light: rgba(255, 255, 255, 0.05);
    --text-muted: var(--text-muted);
    --rank-1-bg: rgba(var(--accent-rgb, 59, 130, 246), 0.1);
    --rank-1-color: var(--accent);
    --rank-2-bg: rgba(124, 58, 237, 0.1);
    --rank-2-color: #7c3aed;
    --rank-3-bg: rgba(16, 185, 129, 0.1);
    --rank-3-color: var(--success);
    --fire-color: var(--error);

    .hot-articles {
      .hot-article-item {
        background: rgba(255, 255, 255, 0.02);

        &:hover {
          background: rgba(255, 255, 255, 0.04);

          .hot-article-title {
            color: var(--text-primary);
          }
        }

        .hot-article-title {
          color: var(--text-muted);

          &:hover {
            color: var(--text-primary);
          }
        }
      }
    }
  }
}
</style>
