<template>
  <div class="article-meta" :class="{ 'compact-mode': compact }">
    <!-- 第一行：文章类型、审核状态、发布时间 -->
    <div v-if="showType" class="meta-row primary-row">
      <span class="article-type">
        <el-tag
          :type="article.reprintType === 0 ? 'success' : 'warning'"
          size="small"
          effect="light"
        >
          {{ article.reprintType === 0 ? '原创' : '转载' }}
        </el-tag>
      </span>
      <span v-if="visibleRangeLabel" class="article-visibility">{{ visibleRangeLabel }}</span>
      <span
        v-if="article.examineStatus !== undefined && article.examineStatus !== 1"
        class="article-examine-status"
        :class="'status-' + article.examineStatus"
      >
        {{ examineStatusLabel }}
      </span>
      <span class="article-date" v-if="article.createTime">
        <el-icon><Clock /></el-icon>
        {{ article.createTime }}
      </span>
    </div>

    <!-- 第二行：统计数据 -->
    <div v-if="showStats" class="meta-row stats-row">
      <span class="article-stat">
        <el-icon><View /></el-icon>
        {{ formatCompactNumber(article.readCount || 0) }}
      </span>
      <span class="article-stat">
        <el-icon><Star /></el-icon>
        {{ formatCompactNumber(article.likeCount || 0) }}
      </span>
      <span class="article-stat">
        <el-icon><Collection /></el-icon>
        {{ formatCompactNumber(article.collectCount || 0) }}
      </span>
      <span class="article-stat">
        <el-icon><ChatDotRound /></el-icon>
        {{ formatCompactNumber(article.commentCount || 0) }}
      </span>
    </div>

    <!-- 第三行：标签信息 -->
    <div v-if="showTags && tagList.length > 0" class="meta-row tags-row">
      <div class="article-tags">
        <span>文章标签：</span>
        <div class="tags-container">
          <el-tag v-for="tag in tagList" :key="tag" size="small" effect="light" class="tag-item">
            {{ tag }}
          </el-tag>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { Clock, View, Star, Collection, ChatDotRound } from '@element-plus/icons-vue'
import { formatCompactNumber } from '@/utils/formatNumber'

// Props 定义
const props = defineProps({
  article: {
    type: Object,
    default: () => ({}),
  },
  showStats: {
    type: Boolean,
    default: true,
  },
  showType: {
    type: Boolean,
    default: true,
  },
  showTags: {
    type: Boolean,
    default: false,
  },
  compact: {
    type: Boolean,
    default: false,
  },
})

// 可见范围标签
const visibleRangeLabel = computed(() => {
  const visibilityMap = {
    1: '仅自己',
    2: '粉丝',
    3: 'VIP',
  }
  return visibilityMap[props.article.visibleRange] || ''
})

// 审核状态标签
const examineStatusLabel = computed(() => {
  const statusMap = {
    0: '待审核',
    1: '审核通过',
    2: '审核未通过',
  }
  return statusMap[props.article.examineStatus] || ''
})

// 标签列表
const tagList = computed(() => {
  if (!props.article?.tag) return []
  return props.article.tag.split(',').filter((tag) => tag.trim() !== '')
})
</script>

<style lang="scss" scoped>
.article-meta {
  display: flex;
  flex-direction: column;
  gap: 10px;
  font-size: 13px;
  color: var(--el-text-color-secondary);

  // 紧凑模式
  &.compact-mode {
    gap: 8px;

    .meta-row {
      gap: 8px;
    }

    .article-type {
      .el-tag {
        padding: 0 6px;
        font-size: 11px;
      }
    }

    .article-stat {
      font-size: 12px;

      .el-icon {
        font-size: 13px;
      }
    }

    .article-date {
      font-size: 12px;

      .el-icon {
        font-size: 13px;
      }
    }
  }

  // 元信息行
  .meta-row {
    display: flex;
    align-items: center;
    gap: 10px;
    flex-wrap: wrap;

    &.primary-row {
      gap: 10px;
    }

    &.stats-row {
      gap: 14px;
    }
  }

  // 文章类型标签
  .article-type {
    display: inline-flex;
    align-items: center;

    .el-tag {
      --el-tag-bg-color: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.1);
      --el-tag-border-color: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.2);
      --el-tag-text-color: var(--el-color-primary);
      --el-tag-font-size: 12px;
    }
  }

  // 可见范围标签
  .article-visibility {
    display: inline-flex;
    align-items: center;
    height: 26px;
    padding: 0 10px;
    border-radius: 12px;
    background: rgba(var(--el-color-warning-rgb, 230, 162, 60), 0.12);
    color: var(--el-color-warning);
    font-size: 12px;
    font-weight: 600;
  }

  // 审核状态标签
  .article-examine-status {
    display: inline-flex;
    align-items: center;
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
  }

  // 日期
  .article-date {
    display: inline-flex;
    align-items: center;
    gap: 4px;

    .el-icon {
      font-size: 14px;
    }
  }

  // 统计数据
  .article-stat {
    display: inline-flex;
    align-items: center;
    gap: 4px;

    .el-icon {
      font-size: 15px;
    }
  }

  // 标签行
  .tags-row {
    .article-tags {
      display: flex;
      align-items: center;
      gap: 8px;
      flex-wrap: wrap;

      > span {
        flex-shrink: 0;
      }

      .tags-container {
        display: flex;
        gap: 6px;
        flex-wrap: wrap;

        .tag-item {
          cursor: default;
        }
      }
    }
  }

  // 黑夜模式适配
  html.dark & {
    .article-type {
      .el-tag {
        --el-tag-bg-color: rgba(var(--el-color-primary-rgb, 96, 165, 255), 0.15);
        --el-tag-border-color: rgba(var(--el-color-primary-rgb, 96, 165, 255), 0.3);
        --el-tag-text-color: var(--el-color-primary-light-3);
      }
    }

    .article-visibility {
      background: rgba(var(--el-color-warning-rgb, 234, 179, 8), 0.15);
      color: var(--el-color-warning-light-3);
    }

    .article-examine-status {
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
}
</style>
