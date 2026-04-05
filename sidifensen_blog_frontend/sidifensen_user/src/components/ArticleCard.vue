<template>
  <div
    class="article-card"
    :class="[`mode-${mode}`, { 'is-clickable': clickable }]"
    @click="handleClick"
  >
    <!-- Grid 模式 -->
    <template v-if="mode === 'grid'">
      <div class="article-image">
        <el-image :src="article.coverUrl" class="article-cover" fit="cover">
          <template #placeholder>
            <div class="image-placeholder">
              <el-icon class="is-loading"><Loading /></el-icon>
            </div>
          </template>
          <template #error>
            <div class="image-error">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
      </div>
      <div class="article-content">
        <div class="article-tags">
          <span class="tag tag-hot" v-if="isHot">🔥 热门</span>
          <span class="tag tag-new" v-else-if="isNew">✨ 新作</span>
        </div>
        <h3 class="article-title">{{ article.title }}</h3>
        <p class="article-excerpt">{{ article.description || '暂无描述' }}</p>
        <div class="article-meta" v-if="showMeta">
          <span class="article-date">{{ formatDate(article.createTime) }}</span>
          <div class="article-stats">
            <span class="stat">👁 {{ formatNumber(article.readCount || 0) }}</span>
            <span class="stat"
              ><span class="heart-icon">♡</span> {{ formatNumber(article.likeCount || 0) }}</span
            >
          </div>
        </div>
      </div>
    </template>

    <!-- List 模式 -->
    <template v-else-if="mode === 'list'">
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

      <div class="article-content">
        <h3 class="article-title">{{ article.title }}</h3>
        <p class="article-description">{{ article.description }}</p>

        <!-- 作者信息 -->
        <div class="article-meta" v-if="showAuthor">
          <div class="article-author">
            <el-image :src="article.avatar" class="author-avatar" fit="cover">
              <template #placeholder>
                <div class="avatar-placeholder">
                  <el-icon><User /></el-icon>
                </div>
              </template>
              <template #error>
                <div class="avatar-placeholder">
                  <el-icon><User /></el-icon>
                </div>
              </template>
            </el-image>
            <span class="author-name">{{ article.nickname }}</span>
          </div>
        </div>

        <!-- 文章元信息 -->
        <div class="article-meta-primary" v-if="showMeta">
          <span class="article-type">{{ getArticleType(article.reprintType) }}</span>
          <span v-if="getVisibilityLabel(article.visibleRange)" class="article-visibility">
            {{ getVisibilityLabel(article.visibleRange) }}
          </span>
          <span
            v-if="article.examineStatus !== 1"
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

        <!-- 统计数据 -->
        <div class="article-meta-stats" v-if="showMeta">
          <span class="article-stat">
            <el-icon><View /></el-icon>
            {{ formatNumber(article.readCount || 0) }}
          </span>
          <span class="article-stat">
            <el-icon><Star /></el-icon>
            {{ formatNumber(article.likeCount || 0) }}
          </span>
          <span class="article-stat">
            <el-icon><Collection /></el-icon>
            {{ formatNumber(article.collectCount || 0) }}
          </span>
          <span class="article-stat">
            <el-icon><ChatDotRound /></el-icon>
            {{ formatNumber(article.commentCount || 0) }}
          </span>
        </div>
      </div>
    </template>

    <!-- Simple 模式 -->
    <template v-else-if="mode === 'simple'">
      <div class="article-cover">
        <el-image :src="article.coverUrl" class="cover-image">
          <template #placeholder>
            <div class="loading-text">加载中...</div>
          </template>
          <template #error>
            <div class="error">
              <el-icon><Picture /></el-icon>
            </div>
          </template>
        </el-image>
      </div>

      <div class="article-content">
        <div class="article-author" v-if="showAuthor">
          <el-image :src="article.avatar" class="author-avatar" fit="cover">
            <template #placeholder>
              <div class="avatar-placeholder">
                <el-icon><User /></el-icon>
              </div>
            </template>
            <template #error>
              <div class="avatar-placeholder">
                <el-icon><User /></el-icon>
              </div>
            </template>
          </el-image>
          <span>{{ article.nickname }}</span>
        </div>

        <h3 class="article-title">{{ article.title }}</h3>

        <p class="article-description">{{ article.description }}</p>

        <div class="article-meta" v-if="showMeta">
          <span>{{ formatDate(article.createTime) }}</span>
          <span
            ><el-icon><View /></el-icon> {{ formatNumber(article.readCount || 0) }}</span
          >
          <span
            ><svg-icon name="like" width="13px" height="13px" color="currentColor" />
            {{ formatNumber(article.likeCount || 0) }}</span
          >
          <span
            ><el-icon><Star /></el-icon> {{ formatNumber(article.collectCount || 0) }}</span
          >
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import {
  Picture,
  Loading,
  Clock,
  View,
  Star,
  Collection,
  ChatDotRound,
  User,
} from '@element-plus/icons-vue'
import { formatCompactNumber } from '@/utils/formatNumber'
import SvgIcon from '@/components/SvgIcon.vue'

// Props 定义
const props = defineProps({
  // 文章数据
  article: {
    type: Object,
    required: true,
    default: () => ({
      id: 0,
      title: '',
      description: '',
      coverUrl: '',
      userId: 0,
      nickname: '',
      avatar: '',
      reprintType: 0,
      visibleRange: 1,
      examineStatus: 1,
      readCount: 0,
      likeCount: 0,
      collectCount: 0,
      commentCount: 0,
      createTime: '',
      tag: '',
    }),
  },
  // 展示模式: grid | list | simple
  mode: {
    type: String,
    default: 'grid',
    validator: (value) => ['grid', 'list', 'simple'].includes(value),
  },
  // 是否显示作者信息（仅 list/simple 模式）
  showAuthor: {
    type: Boolean,
    default: false,
  },
  // 是否显示统计信息
  showMeta: {
    type: Boolean,
    default: true,
  },
  // 是否可点击
  clickable: {
    type: Boolean,
    default: true,
  },
})

// Emits 定义
const emit = defineEmits(['click'])

// 判断是否热门（阅读量 > 1000）
const isHot = computed(() => (props.article.readCount || 0) > 1000)

// 判断是否新文章（3天内）
const isNew = computed(() => {
  if (!props.article.createTime) return false
  const diffDays = (Date.now() - new Date(props.article.createTime)) / (1000 * 60 * 60 * 24)
  return diffDays <= 3
})

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  if (Number.isNaN(date.getTime())) return dateString
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
  })
}

// 格式化数字
const formatNumber = (value) => formatCompactNumber(value)

// 获取文章类型
const getArticleType = (type) => {
  const typeMap = {
    0: '原创',
    1: '转载',
  }
  return typeMap[type] || '原创'
}

// 获取可见范围标签
const getVisibilityLabel = (visibleRange) => {
  const visibilityMap = {
    1: '仅自己',
    2: '粉丝',
    3: 'VIP',
  }
  return visibilityMap[visibleRange] || ''
}

// 获取审核状态
const getExamineStatus = (status) => {
  const statusMap = {
    0: '待审核',
    1: '审核通过',
    2: '审核未通过',
  }
  return statusMap[status] || '审核通过'
}

// 处理点击事件
const handleClick = () => {
  if (props.clickable) {
    emit('click', props.article)
  }
}
</script>

<style lang="scss" scoped>
// ===== 通用样式变量 =====
.article-card {
  // ===== Grid 模式样式 =====
  &.mode-grid {
    display: flex;
    flex-direction: column;
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

    &.is-clickable {
      cursor: pointer;

      &:hover {
        transform: translateY(-4px);
        box-shadow: var(--shadow-md);
        border-color: rgba(0, 102, 255, 0.2);
      }
    }

    .article-image {
      width: 100%;
      height: 140px;
      overflow: hidden;
      background: var(--bg-subtle);

      .article-cover {
        width: 100%;
        height: 100%;
        transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);

        &:hover {
          transform: scale(1.05);
        }
      }

      .image-placeholder,
      .image-error {
        width: 100%;
        height: 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        color: var(--text-muted);
        font-size: 24px;
      }
    }

    .article-content {
      padding: 4px 12px 12px;
      display: flex;
      flex-direction: column;
      flex: 1;
    }

    .article-tags {
      display: flex;
      gap: 6px;
      margin-bottom: 8px;

      .tag {
        padding: 3px 8px;
        border-radius: 6px;
        font-size: 0.7rem;
        font-weight: 500;

        &.tag-hot {
          background: rgba(239, 68, 68, 0.1);
          color: #dc2626;
        }

        &.tag-new {
          background: rgba(0, 102, 255, 0.1);
          color: var(--accent);
        }
      }
    }

    .article-title {
      font-size: 0.95rem;
      font-weight: 600;
      line-height: 1.4;
      margin: 0 0 8px 0;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      color: var(--text-primary);
    }

    .article-excerpt {
      font-size: 0.8rem;
      color: var(--text-secondary);
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
      margin-bottom: 10px;
      flex: 1;
    }

    .article-meta {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-top: 10px;
      border-top: 1px solid var(--border);
    }

    .article-date {
      font-size: 0.7rem;
      color: var(--text-muted);
    }

    .article-stats {
      display: flex;
      gap: 8px;

      .stat {
        display: flex;
        align-items: center;
        gap: 4px;
        font-size: 0.75rem;
        color: var(--text-muted);
      }

      .heart-icon {
        font-size: 1rem;
      }
    }
  }

  // ===== List 模式样式 =====
  &.mode-list {
    display: flex;
    gap: 20px;
    padding: 20px;
    background: var(--article-bg);
    border-radius: 12px;
    border: 1px solid var(--article-border);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;

    &.is-clickable {
      cursor: pointer;

      &:hover {
        background: var(--article-hover-bg);
        border-color: var(--article-hover-border);
        box-shadow:
          0 8px 32px var(--article-hover-shadow),
          0 2px 8px var(--article-hover-shadow-light);
        transform: translateX(8px) scale(1.01);

        .article-cover {
          transform: scale(1.08);
        }

        .cover-overlay {
          opacity: 0.1;
        }
      }
    }

    // 左侧装饰条
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 3px;
      height: 100%;
      background: linear-gradient(
        180deg,
        var(--el-color-primary) 0%,
        var(--el-color-primary-light-3) 100%
      );
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover::before {
      opacity: 1;
    }

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
          background: linear-gradient(
            135deg,
            var(--el-fill-color-light) 0%,
            var(--el-fill-color) 100%
          );

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
          background: linear-gradient(
            135deg,
            var(--el-fill-color-light) 0%,
            var(--el-fill-color) 100%
          );

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

    .article-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: space-between;
      min-width: 0;
    }

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

    .article-author {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 13px;
      color: var(--el-text-color-secondary);
      margin-bottom: 10px;

      .author-avatar {
        width: 24px;
        height: 24px;
        border-radius: 50%;
        overflow: hidden;
      }

      .avatar-placeholder {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        background: var(--el-fill-color-light);
        color: var(--el-text-color-placeholder);

        .el-icon {
          font-size: 14px;
        }
      }
    }

    .article-meta-primary {
      display: flex;
      align-items: center;
      gap: 10px;
      flex-wrap: wrap;
      margin-bottom: 8px;
    }

    .article-type {
      background: linear-gradient(
        135deg,
        rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.15) 0%,
        rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.05) 100%
      );
      color: var(--el-color-primary);
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;
      border: 1px solid rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.2);
    }

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

    .article-examine-status {
      padding: 4px 10px;
      border-radius: 12px;
      font-size: 12px;
      font-weight: 500;

      &.status-0 {
        background-color: #fff7ed;
        color: #ea580c;
        border: 1px solid #fed7aa;
      }

      &.status-2 {
        background-color: #fef2f2;
        color: #dc2626;
        border: 1px solid #fecaca;
      }
    }

    .article-date,
    .article-stat {
      display: inline-flex;
      align-items: center;
      gap: 4px;
      font-size: 13px;
      color: var(--el-text-color-secondary);
      transition: color 0.3s ease;
    }

    .article-meta-stats {
      display: flex;
      align-items: center;
      gap: 12px;
    }
  }

  // ===== Simple 模式样式 =====
  &.mode-simple {
    display: flex;
    gap: 20px;
    padding: 20px;
    background: transparent;
    border: 1px solid var(--border-regular);
    border-radius: 6px;
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;

    &.is-clickable {
      cursor: pointer;

      &:hover {
        border-color: var(--border-hover);
        background: rgba(0, 0, 0, 0.01);

        .cover-image {
          transform: scale(1.03);
          filter: grayscale(0%);
        }
      }
    }

    // 顶部装饰线
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 1px;
      background: linear-gradient(90deg, rgba(0, 102, 255, 0.5), transparent);
      opacity: 0;
      transition: opacity 0.25s ease;
    }

    &:hover::before {
      opacity: 1;
    }

    .article-cover {
      width: 200px;
      height: 120px;
      background: var(--bg-subtle);
      border-radius: 4px;
      flex-shrink: 0;
      overflow: hidden;
      border: 1px solid var(--border-light);

      .loading-text {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        font-size: 12px;
        color: var(--text-muted);
        background: var(--bg-subtle);
      }

      .error {
        display: flex;
        justify-content: center;
        align-items: center;
        width: 100%;
        height: 100%;
        background: var(--bg-subtle);

        .el-icon {
          font-size: 24px;
          color: var(--text-muted);
        }
      }

      .cover-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        filter: grayscale(15%);
      }
    }

    .article-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      min-width: 0;
      height: 120px;

      .article-author {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 12px;
        font-weight: 500;
        color: var(--text-secondary);
        margin-bottom: 8px;
        cursor: pointer;

        .author-avatar {
          width: 22px;
          height: 22px;
          border-radius: 50%;
          overflow: hidden;
          background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
          border: 1px solid rgba(0, 0, 0, 0.1);

          .avatar-placeholder {
            display: flex;
            justify-content: center;
            align-items: center;
            width: 100%;
            height: 100%;
            color: var(--text-muted);

            .el-icon {
              font-size: 14px;
            }
          }
        }
      }

      .article-title {
        font-size: 17px;
        font-weight: 600;
        line-height: 1.45;
        color: var(--text-primary);
        margin: 0 0 6px 0;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
        letter-spacing: -0.01em;
        transition: color 0.2s ease;

        &:hover {
          color: var(--accent-color);
        }
      }

      .article-description {
        font-size: 13px;
        color: var(--text-secondary);
        line-height: 1.5;
        margin: 0 0 8px 0;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .article-meta {
        display: flex;
        align-items: center;
        gap: 20px;
        font-size: 11px;
        color: var(--text-muted);
        font-feature-settings: 'tnum';
        font-weight: 300;

        span {
          display: flex;
          align-items: center;
          gap: 5px;
          transition: color 0.2s ease;
        }

        .el-icon {
          font-size: 13px;
        }
      }
    }
  }
}

// ===== 响应式适配 =====
@media (max-width: 768px) {
  .article-card {
    &.mode-list {
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

    &.mode-simple {
      flex-direction: column;
      gap: 0;
      padding: 0;
      border-radius: 12px;
      overflow: hidden;

      .article-cover {
        width: 100%;
        height: 200px;
        border-radius: 0;
        border: none;

        .cover-image {
          border-radius: 0;
        }
      }

      .article-content {
        height: auto;
        min-height: auto;
        padding: 20px;
        width: 100%;
      }
    }
  }
}

// ===== 动画 =====
@keyframes pulse {
  0%,
  100% {
    opacity: 0.6;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.1);
  }
}
</style>

<style lang="scss">
// ===== 黑夜模式覆盖 =====
html.dark {
  .article-card {
    &.mode-grid {
      .article-tags .tag.tag-hot {
        background: rgba(239, 68, 68, 0.15);
        color: #fca5a5;
      }

      .article-tags .tag.tag-new {
        background: rgba(0, 212, 255, 0.15);
        color: #00d4ff;
      }
    }

    &.mode-list {
      .article-type {
        background: linear-gradient(
          135deg,
          rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.2) 0%,
          rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.08) 100%
        );
        border-color: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.3);
      }

      .article-examine-status {
        &.status-0 {
          background-color: rgba(234, 88, 12, 0.15);
          color: #fdba74;
          border-color: rgba(234, 88, 12, 0.3);
        }

        &.status-2 {
          background-color: rgba(220, 38, 38, 0.15);
          color: #fca5a5;
          border-color: rgba(220, 38, 38, 0.3);
        }
      }
    }

    &.mode-simple {
      .article-cover {
        .cover-image {
          filter: grayscale(20%);
        }
      }

      .article-content {
        .article-author .author-avatar {
          background: rgba(255, 255, 255, 0.1);

          .avatar-placeholder {
            background: rgba(255, 255, 255, 0.1);
          }
        }
      }
    }
  }
}
</style>
