<template>
  <div class="creation-home">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-content">
        <div class="welcome-text">
          <h1 class="welcome-title">创作中心</h1>
          <p class="welcome-desc">管理您的内容，洞察创作数据</p>
        </div>
        <div class="welcome-actions">
          <a href="/editor" target="_blank" class="btn btn-primary">
            <el-icon><EditPen /></el-icon>
            <span>写文章</span>
          </a>
          <router-link to="/creation/articlemanage" class="btn btn-ghost">
            <el-icon><Document /></el-icon>
            <span>文章管理</span>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 数据概览 -->
    <section class="stats-section">
      <div class="section-header">
        <h2>数据概览</h2>
      </div>

      <div class="stats-grid" v-if="!statisticsLoading">
        <!-- 文章 -->
        <div class="stat-card">
          <div class="stat-icon icon-article">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-main">
            <span class="stat-label">文章</span>
            <span class="stat-value">{{
              formatDisplayNumber(statistics?.articleStatistics?.totalCount)
            }}</span>
          </div>
          <div class="stat-breakdown">
            <span class="breakdown-item">
              <span class="dot dot-published"></span>
              <span class="label">已发布</span>
              <span class="value">{{
                formatDisplayNumber(statistics?.articleStatistics?.publishedCount)
              }}</span>
            </span>
            <span class="breakdown-item">
              <span class="dot dot-draft"></span>
              <span class="label">草稿</span>
              <span class="value">{{
                formatDisplayNumber(statistics?.articleStatistics?.draftCount)
              }}</span>
            </span>
            <span class="breakdown-item">
              <span class="dot dot-reviewing"></span>
              <span class="label">审核中</span>
              <span class="value">{{
                formatDisplayNumber(statistics?.articleStatistics?.reviewingCount)
              }}</span>
            </span>
          </div>
        </div>

        <!-- 专栏 -->
        <router-link to="/creation/columnmanage" class="stat-card clickable">
          <div class="stat-icon icon-column">
            <el-icon><Folder /></el-icon>
          </div>
          <div class="stat-main">
            <span class="stat-label">专栏</span>
            <span class="stat-value">{{ formatDisplayNumber(statistics?.columnCount) }}</span>
          </div>
          <div class="stat-action">
            <span>管理</span>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </router-link>

        <!-- 评论 -->
        <router-link to="/creation/commentmanage" class="stat-card clickable">
          <div class="stat-icon icon-comment">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="stat-main">
            <span class="stat-label">评论</span>
            <span class="stat-value">{{ formatDisplayNumber(statistics?.commentCount) }}</span>
          </div>
          <div class="stat-action">
            <span>管理</span>
            <el-icon><ArrowRight /></el-icon>
          </div>
        </router-link>

        <!-- 阅读量 -->
        <div class="stat-card">
          <div class="stat-icon icon-read">
            <el-icon><View /></el-icon>
          </div>
          <div class="stat-main">
            <span class="stat-label">总阅读</span>
            <span class="stat-value">{{ formatDisplayNumber(statistics?.totalReadCount) }}</span>
          </div>
          <div class="stat-trend trend-positive">
            <el-icon><Top /></el-icon>
            <span>持续增长</span>
          </div>
        </div>

        <!-- 点赞 -->
        <div class="stat-card">
          <div class="stat-icon icon-like">
            <el-icon><Star /></el-icon>
          </div>
          <div class="stat-main">
            <span class="stat-label">获赞</span>
            <span class="stat-value">{{ formatDisplayNumber(statistics?.totalLikeCount) }}</span>
          </div>
          <div class="stat-trend">
            <span>感谢支持</span>
          </div>
        </div>

        <!-- 粉丝 -->
        <div class="stat-card">
          <div class="stat-icon icon-fans">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-main">
            <span class="stat-label">粉丝</span>
            <span class="stat-value">{{ formatDisplayNumber(statistics?.fansCount) }}</span>
          </div>
          <div class="stat-trend">
            <span>影响力</span>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div class="stats-loading" v-else>
        <el-skeleton animated :count="6">
          <template #template>
            <div class="skeleton-card">
              <el-skeleton-item variant="circle" style="width: 40px; height: 40px" />
              <div class="skeleton-content">
                <el-skeleton-item variant="text" style="width: 60px; margin: 8px 0" />
                <el-skeleton-item variant="h3" style="width: 80px" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </section>

    <!-- 快捷操作 -->
    <section class="quick-section">
      <div class="section-header">
        <h2>快捷操作</h2>
      </div>

      <div class="quick-grid">
        <div class="quick-item" @click="goToEditor">
          <div class="quick-icon">
            <el-icon><EditPen /></el-icon>
          </div>
          <span class="quick-label">写文章</span>
        </div>

        <div class="quick-item" @click="goToArticleManage">
          <div class="quick-icon">
            <el-icon><Files /></el-icon>
          </div>
          <span class="quick-label">文章管理</span>
        </div>

        <div class="quick-item" @click="goToColumnManage">
          <div class="quick-icon">
            <el-icon><Collection /></el-icon>
          </div>
          <span class="quick-label">专栏管理</span>
        </div>

        <div class="quick-item" @click="goToCommentManage">
          <div class="quick-icon">
            <el-icon><Message /></el-icon>
          </div>
          <span class="quick-label">评论管理</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  EditPen,
  Document,
  Folder,
  ChatDotRound,
  View,
  User,
  ArrowRight,
  Files,
  Collection,
  Message,
  Top,
  Star,
} from '@element-plus/icons-vue'
import { getCreationStatistics } from '@/api/article'
import { formatCompactNumber } from '@/utils/formatNumber'

const router = useRouter()

const statisticsLoading = ref(false)
const statistics = ref(null)

const formatDisplayNumber = (num) => {
  return formatCompactNumber(num)
}

const fetchStatistics = async () => {
  try {
    statisticsLoading.value = true
    const res = await getCreationStatistics()
    statistics.value = res.data
  } catch (error) {
    // 静默处理
  } finally {
    statisticsLoading.value = false
  }
}

const goToEditor = () => {
  window.open('/editor', '_blank')
}

const goToArticleManage = () => {
  router.push('/creation/articlemanage')
}

const goToColumnManage = () => {
  router.push('/creation/columnmanage')
}

const goToCommentManage = () => {
  router.push('/creation/commentmanage')
}

onMounted(() => {
  fetchStatistics()
})
</script>

<style lang="scss" scoped>
// ========================================
// 创作中心首页 - 极简设计风格
// 设计原则：克制、清晰、专注内容
// ========================================

.creation-home {
  background: var(--el-bg-color-page);
  min-height: 100%;
  padding: 0;
  transition: background-color 0.3s ease;
}

// ========================================
// 欢迎区域
// ========================================
.welcome-section {
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-lighter);
  padding: 32px 0;
  transition:
    background-color 0.3s ease,
    border-color 0.3s ease;

  .welcome-content {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .welcome-text {
    .welcome-title {
      font-size: 28px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin: 0 0 8px 0;
      letter-spacing: -0.3px;
      transition: color 0.3s ease;
    }

    .welcome-desc {
      font-size: 14px;
      color: var(--el-text-color-secondary);
      margin: 0;
      transition: color 0.3s ease;
    }
  }

  .welcome-actions {
    display: flex;
    gap: 12px;

    .btn {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      text-decoration: none;
      transition: all 0.2s ease;
      cursor: pointer;
      border: none;

      .el-icon {
        font-size: 16px;
      }

      &.btn-primary {
        background: var(--el-color-primary);
        color: var(--el-color-white);

        &:hover {
          background: var(--el-color-primary-light-3);
        }
      }

      &.btn-ghost {
        background: var(--el-fill-color-light);
        color: var(--el-text-color-regular);

        &:hover {
          background: var(--el-fill-color);
        }
      }
    }
  }
}

// ========================================
// 通用 Section 样式
// ========================================
section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 32px 24px;

  .section-header {
    margin-bottom: 20px;

    h2 {
      font-size: 18px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin: 0;
      letter-spacing: -0.2px;
      transition: color 0.3s ease;
    }
  }
}

// ========================================
// 数据统计区域
// ========================================
.stats-section {
  .stats-grid {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 16px;
  }

  .stat-card {
    background: var(--el-bg-color);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 12px;
    padding: 20px;
    transition:
      all 0.2s ease,
      border-color 0.3s ease;

    &.clickable {
      cursor: pointer;

      &:hover {
        border-color: var(--el-border-color);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      }
    }

    .stat-icon {
      width: 40px;
      height: 40px;
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 12px;
      font-size: 20px;
      transition: all 0.3s ease;

      &.icon-article {
        background: var(--el-fill-color);
        color: var(--el-color-primary);
      }

      &.icon-column {
        background: var(--el-fill-color);
        color: var(--el-color-warning);
      }

      &.icon-comment {
        background: var(--el-fill-color);
        color: var(--el-color-success);
      }

      &.icon-read {
        background: var(--el-fill-color);
        color: var(--el-color-info);
      }

      &.icon-like {
        background: var(--el-fill-color);
        color: var(--el-color-danger);
      }

      &.icon-fans {
        background: var(--el-fill-color);
        color: var(--el-color-primary-light-3);
      }
    }

    .stat-main {
      display: flex;
      flex-direction: column;
      gap: 4px;
      margin-bottom: 16px;

      .stat-label {
        font-size: 13px;
        color: var(--el-text-color-secondary);
        transition: color 0.3s ease;
      }

      .stat-value {
        font-size: 28px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        letter-spacing: -0.5px;
        transition: color 0.3s ease;
      }
    }

    .stat-breakdown {
      display: flex;
      flex-direction: column;
      gap: 8px;
      padding-top: 16px;
      border-top: 1px solid var(--el-border-color-lighter);
      transition: border-color 0.3s ease;

      .breakdown-item {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 12px;

        .dot {
          width: 6px;
          height: 6px;
          border-radius: 50%;
          flex-shrink: 0;

          &.dot-published {
            background: var(--el-color-success);
          }

          &.dot-draft {
            background: var(--el-color-warning);
          }

          &.dot-reviewing {
            background: var(--el-color-primary);
          }
        }

        .label {
          color: var(--el-text-color-placeholder);
          min-width: 36px;
          transition: color 0.3s ease;
        }

        .value {
          color: var(--el-text-color-regular);
          font-weight: 500;
          transition: color 0.3s ease;
        }
      }
    }

    .stat-action {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding-top: 16px;
      border-top: 1px solid var(--el-border-color-lighter);
      font-size: 13px;
      color: var(--el-color-primary);
      font-weight: 500;
      transition: border-color 0.3s ease;

      .el-icon {
        font-size: 14px;
      }
    }

    .stat-trend {
      display: flex;
      align-items: center;
      gap: 4px;
      padding-top: 16px;
      border-top: 1px solid var(--el-border-color-lighter);
      font-size: 12px;
      color: var(--el-text-color-placeholder);
      transition: border-color 0.3s ease;

      &.trend-positive {
        color: var(--el-color-success);

        .el-icon {
          font-size: 12px;
        }
      }
    }
  }

  .stats-loading {
    display: grid;
    grid-template-columns: repeat(6, 1fr);
    gap: 16px;

    .skeleton-card {
      background: var(--el-bg-color);
      border: 1px solid var(--el-border-color-lighter);
      border-radius: 12px;
      padding: 20px;
      display: flex;
      align-items: center;
      gap: 12px;
      transition:
        background-color 0.3s ease,
        border-color 0.3s ease;

      .skeleton-content {
        flex: 1;
      }
    }
  }
}

// ========================================
// 快捷操作区域
// ========================================
.quick-section {
  padding-bottom: 40px;

  .quick-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
  }

  .quick-item {
    background: var(--el-bg-color);
    border: 1px solid var(--el-border-color-lighter);
    border-radius: 12px;
    padding: 24px;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    cursor: pointer;
    transition:
      all 0.2s ease,
      border-color 0.3s ease,
      background-color 0.3s ease;

    &:hover {
      border-color: var(--el-border-color);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
      transform: translateY(-2px);

      .quick-icon {
        background: var(--el-color-primary);
        color: var(--el-color-white);
      }
    }

    .quick-icon {
      width: 48px;
      height: 48px;
      border-radius: 10px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 22px;
      background: var(--el-fill-color);
      color: var(--el-text-color-regular);
      transition: all 0.2s ease;
    }

    .quick-label {
      font-size: 14px;
      font-weight: 500;
      color: var(--el-text-color-regular);
      transition: color 0.3s ease;
    }
  }
}

// ========================================
// 响应式设计
// ========================================
@media (max-width: 1024px) {
  .stats-section .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }

  .quick-section .quick-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .welcome-section .welcome-content {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .welcome-actions {
    justify-content: center;
  }

  .stats-section .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .quick-section .quick-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  section {
    padding: 24px 16px;
  }
}

@media (max-width: 480px) {
  .stats-section .stats-grid {
    grid-template-columns: 1fr;
  }

  .quick-section .quick-grid {
    grid-template-columns: 1fr;
  }

  .welcome-section {
    padding: 24px 0;
  }

  .welcome-section .welcome-content {
    padding: 0 16px;
  }

  .welcome-title {
    font-size: 24px !important;
  }
}
</style>
