<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="header-content">
        <div>
          <h1 class="page-title">控制台</h1>
          <p class="page-description">斯蒂芬森社区管理系统</p>
        </div>
        <div class="header-actions">
          <el-button class="refresh-btn" @click="refreshData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
        </div>
      </div>
    </div>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <!-- 用户统计卡片 -->
        <div class="stat-card stat-card--user">
          <div class="stat-card__header">
            <span class="stat-card__label">用户总数</span>
            <div class="stat-card__icon">
              <el-icon><User /></el-icon>
            </div>
          </div>
          <div class="stat-card__value" v-if="!statisticsLoading">
            <span class="number">{{ userCount }}</span>
          </div>
          <el-skeleton v-else :rows="1" animated />
          <div class="stat-card__trend">
            <span class="trend-indicator trend-indicator--positive">
              <el-icon><Top /></el-icon>
              较昨日 +12
            </span>
          </div>
        </div>

        <!-- 文章统计卡片 -->
        <div class="stat-card stat-card--article">
          <div class="stat-card__header">
            <span class="stat-card__label">文章总数</span>
            <div class="stat-card__icon">
              <el-icon><Document /></el-icon>
            </div>
          </div>
          <div class="stat-card__value" v-if="!statisticsLoading">
            <span class="number">{{ articleStatistics?.totalCount || 0 }}</span>
          </div>
          <el-skeleton v-else :rows="1" animated />
          <div class="stat-card__trend">
            <span class="trend-indicator trend-indicator--positive">
              <el-icon><Top /></el-icon>
              较昨日 +5
            </span>
          </div>
        </div>

        <!-- 总访问统计卡片 -->
        <div class="stat-card stat-card--visits">
          <div class="stat-card__header">
            <span class="stat-card__label">总访问量</span>
            <div class="stat-card__icon">
              <el-icon><Monitor /></el-icon>
            </div>
          </div>
          <div class="stat-card__value" v-if="!statisticsLoading">
            <span class="number">{{ totalVisits }}</span>
          </div>
          <el-skeleton v-else :rows="1" animated />
          <div class="stat-card__trend">
            <span class="trend-indicator trend-indicator--positive">
              <el-icon><Top /></el-icon>
              累计数据
            </span>
          </div>
        </div>

        <!-- 今日访问统计卡片 -->
        <div class="stat-card stat-card--today">
          <div class="stat-card__header">
            <span class="stat-card__label">今日访问</span>
            <div class="stat-card__icon">
              <el-icon><View /></el-icon>
            </div>
          </div>
          <div class="stat-card__value" v-if="!statisticsLoading">
            <span class="number">{{ todayVisits }}</span>
          </div>
          <el-skeleton v-else :rows="1" animated />
          <div class="stat-card__trend">
            <span class="trend-indicator trend-indicator--neutral">
              <el-icon><Right /></el-icon>
              实时数据
            </span>
          </div>
        </div>
      </div>
    </div>

    <!-- 图表 -->
    <div class="chart-section">
      <div class="card-grid">
        <UserActivityCard
          :active-user-count="todayActiveUserCount"
          :total-user-count="userCount"
          :loading="statisticsLoading"
        />
        <ArticleStatusCard
          :data="articleStatistics"
          :loading="statisticsLoading"
        />
        <VisitorTrendCard
          :data="visitorTrend"
          :loading="trendLoading"
          v-model:days="trendDays"
          @days-change="handleDaysChange"
        />
        <InteractionTrendCard
          :data="interactionTrend"
          :loading="interactionTrendLoading"
        />
        <UserGrowthCard
          :data="weeklyTrend"
          :loading="weeklyTrendLoading"
        />
        <VipStatisticsCard
          :data="vipStatisticsData"
          :loading="vipStatisticsLoading"
        />
      </div>
    </div>

    <!-- 快速操作区域 -->
    <div class="quick-actions-section">
      <div class="detail-card">
        <div class="detail-card__header">
          <div class="detail-card__title">
            <span class="title-icon">
              <el-icon><Operation /></el-icon>
            </span>
            <span>快速操作</span>
          </div>
        </div>
        <div class="detail-card__content">
          <div class="action-grid">
            <div class="action-item" @click="navigateTo('/article/examine')">
              <div class="action-item__icon action-item__icon--article">
                <el-icon><Document /></el-icon>
              </div>
              <div class="action-item__content">
                <span class="action-item__title">文章审核</span>
                <span class="action-item__desc">审核待发布文章</span>
              </div>
              <div class="action-item__badge" v-if="examineCountData.articleCount > 0">
                {{ examineCountData.articleCount }}
              </div>
              <div class="action-item__arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>

            <div class="action-item" @click="navigateTo('/comment/examine')">
              <div class="action-item__icon action-item__icon--comment">
                <el-icon><ChatLineRound /></el-icon>
              </div>
              <div class="action-item__content">
                <span class="action-item__title">评论审核</span>
                <span class="action-item__desc">管理用户评论</span>
              </div>
              <div class="action-item__badge" v-if="examineCountData.commentCount > 0">
                {{ examineCountData.commentCount }}
              </div>
              <div class="action-item__arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>

            <div class="action-item" @click="navigateTo('/system/user')">
              <div class="action-item__icon action-item__icon--user">
                <el-icon><User /></el-icon>
              </div>
              <div class="action-item__content">
                <span class="action-item__title">用户管理</span>
                <span class="action-item__desc">管理系统用户</span>
              </div>
              <div class="action-item__arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>

            <div class="action-item" @click="navigateTo('/photo/examine')">
              <div class="action-item__icon action-item__icon--photo">
                <el-icon><Picture /></el-icon>
              </div>
              <div class="action-item__content">
                <span class="action-item__title">图片审核</span>
                <span class="action-item__desc">审核上传图片</span>
              </div>
              <div class="action-item__badge" v-if="examineCountData.photoCount > 0">
                {{ examineCountData.photoCount }}
              </div>
              <div class="action-item__arrow">
                <el-icon><ArrowRight /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { User, Document, ChatLineRound, View, Monitor, Operation, Picture, Refresh, Top, Right, ArrowRight } from "@element-plus/icons-vue";
import { getDashboardStatistics, getExamineCount, getVipStatistics, getWeeklyTrend, getInteractionTrend, getVisitorTrend } from "@/api/dashboard";

// 图表卡片组件
import UserActivityCard from "@/components/cards/UserActivityCard.vue";
import ArticleStatusCard from "@/components/cards/ArticleStatusCard.vue";
import VisitorTrendCard from "@/components/cards/VisitorTrendCard.vue";
import InteractionTrendCard from "@/components/cards/InteractionTrendCard.vue";
import UserGrowthCard from "@/components/cards/UserGrowthCard.vue";
import VipStatisticsCard from "@/components/cards/VipStatisticsCard.vue";

// 路由和状态管理
const router = useRouter();

// 响应式数据
const loading = ref(false);
const statisticsLoading = ref(true);
const userCount = ref(0);
const todayActiveUserCount = ref(0);
const articleStatistics = ref(null);
const totalVisits = ref(0);
const todayVisits = ref(0);
const trendDays = ref(7);
const visitorTrend = ref([]);
const trendLoading = ref(false);
const examineCountData = ref({
  articleCount: 0,
  commentCount: 0,
  photoCount: 0
});
const vipStatisticsData = ref({
  totalVipCount: 0,
  newVipCount: 0,
  newVipCount30Days: 0,
  normalUserCount: 0,
  vipPercentage: 0
});
const vipStatisticsLoading = ref(false);
const weeklyTrend = ref([]);
const weeklyTrendLoading = ref(false);
const interactionTrend = ref([]);
const interactionTrendLoading = ref(false);

// 获取所有统计数据
const fetchAllStatistics = async () => {
  try {
    statisticsLoading.value = true;
    const res = await getDashboardStatistics(7);
    const data = res.data;

    userCount.value = data.userTotalCount || 0;
    todayActiveUserCount.value = data.todayActiveUserCount || 0;
    articleStatistics.value = data.articleStatistics || null;
    totalVisits.value = data.totalVisits || 0;
    todayVisits.value = data.todayVisits || 0;
    visitorTrend.value = data.visitorTrend || [];

  } catch (error) {
    ElMessage.error("获取统计数据失败");
    console.error("获取统计数据失败:", error);
  } finally {
    statisticsLoading.value = false;
  }
};

// 获取访客趋势数据
const fetchVisitorTrend = async () => {
  try {
    trendLoading.value = true;
    const res = await getVisitorTrend(trendDays.value);
    visitorTrend.value = res.data || [];
  } catch (error) {
    ElMessage.error("获取访客趋势失败");
    console.error("获取访客趋势失败:", error);
  } finally {
    trendLoading.value = false;
  }
};

// 获取待审核数量
const fetchExamineCount = async () => {
  try {
    const res = await getExamineCount();
    examineCountData.value = res.data || { articleCount: 0, commentCount: 0, photoCount: 0 };
  } catch (error) {
    console.error('获取待审核数量失败:', error);
  }
};

// 获取 VIP 统计数据
const fetchVipStatistics = async () => {
  try {
    vipStatisticsLoading.value = true;
    const res = await getVipStatistics();
    vipStatisticsData.value = res.data || { totalVipCount: 0, newVipCount: 0 };
  } catch (error) {
    console.error('获取VIP统计数据失败:', error);
  } finally {
    vipStatisticsLoading.value = false;
  }
};

// 获取周趋势数据
const fetchWeeklyTrend = async () => {
  try {
    weeklyTrendLoading.value = true;
    const res = await getWeeklyTrend();
    weeklyTrend.value = res.data || [];
  } catch (error) {
    console.error('获取周趋势数据失败:', error);
  } finally {
    weeklyTrendLoading.value = false;
  }
};

// 获取互动趋势数据
const fetchInteractionTrend = async () => {
  try {
    interactionTrendLoading.value = true;
    const res = await getInteractionTrend();
    interactionTrend.value = res.data || [];
  } catch (error) {
    console.error('获取互动趋势数据失败:', error);
  } finally {
    interactionTrendLoading.value = false;
  }
};

// 切换天数
const handleDaysChange = () => {
  fetchVisitorTrend();
};

// 导航
const navigateTo = (path) => {
  router.push(path);
};

// 刷新数据
const refreshData = () => {
  loading.value = true;
  fetchAllStatistics().finally(() => {
    loading.value = false;
    ElMessage.success("数据已刷新");
  });
};

// 生命周期
onMounted(async () => {
  await fetchAllStatistics();
  await fetchExamineCount();
  await fetchVipStatistics();
  await fetchWeeklyTrend();
  await fetchInteractionTrend();
});
</script>

<style lang="scss" scoped>
.dashboard-container {
  /* 页面背景 */
  --bg-page: #f5f5f5;
  /* 卡片背景 */
  --bg-card: #ffffff;
  /* 次级卡片背景 */
  --bg-card-muted: #f0f0f0;
  /* 主文字颜色 */
  --text-primary: #1e2938;
  /* 常规文字颜色 */
  --text-regular: #475569;
  /* 次要文字颜色 */
  --text-muted: #64748b;
  /* 边框颜色 */
  --border: #e2e8f0;
  /* 浅色边框 */
  --border-light: #edf2f7;
  /* 卡片阴影 */
  --shadow-card: 0 10px 24px rgba(15, 23, 42, 0.06);
  /* 悬停阴影 */
  --shadow-hover: 0 16px 30px rgba(71, 85, 105, 0.1);
  /* 页面光晕效果 */
  --page-glow: rgba(0, 0, 0, 0.02);
  /* 页面渐变遮罩（顶部） */
  --page-overlay-top: rgba(255, 255, 255, 0.35);
  /* 页面渐变遮罩（底部） */
  --page-overlay-bottom: rgba(245, 245, 245, 0.92);
  /* 按钮背景 */
  --button-bg: #ffffff;
  /* 按钮悬停背景 */
  --button-hover-bg: #f5f5f5;
  /* 按钮边框颜色 */
  --button-border: #d0d0d0;
  /* 按钮文字颜色 */
  --button-text: #475569;
  /* 标题图标背景 */
  --title-icon-bg: rgba(71, 85, 105, 0.08);
  /* 正向趋势背景（上涨） */
  --trend-positive-bg: rgba(16, 185, 129, 0.12);
  /* 中性趋势背景（持平） */
  --trend-neutral-bg: rgba(100, 116, 139, 0.12);
  /* 开关组件背景 */
  --toggle-bg: #f0f0f0;
  /* 开关悬停背景 */
  --toggle-hover-bg: #e0e0e0;
  /* 开关激活文字颜色 */
  --toggle-active-text: #ffffff;
  /* 文章操作背景 */
  --action-article-bg: rgba(124, 58, 237, 0.12);
  /* 文章操作文字颜色 */
  --action-article-text: #7c3aed;
  /* 评论操作背景 */
  --action-comment-bg: rgba(14, 165, 233, 0.12);
  /* 评论操作文字颜色 */
  --action-comment-text: #0284c7;
  /* 用户操作背景 */
  --action-user-bg: rgba(16, 185, 129, 0.12);
  /* 用户操作文字颜色 */
  --action-user-text: #059669;
  /* 图片操作背景 */
  --action-photo-bg: rgba(245, 158, 11, 0.12);
  /* 图片操作文字颜色 */
  --action-photo-text: #d97706;
  /* 成功状态色 */
  --success: #10b981;
  /* 危险/错误状态色 */
  --danger: #ef4444;

  min-height: calc(100vh - 88px);
  padding: 24px;
  background:
    radial-gradient(circle at top right, var(--page-glow), transparent 28%),
    linear-gradient(180deg, var(--page-overlay-top), var(--page-overlay-bottom)),
    var(--bg-page);

  /* 页面标题区域 */
  .page-header {
    margin-bottom: 24px;

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;
      padding: 18px 20px;
      border: 1px solid var(--border);
      border-radius: 18px;
      background: var(--bg-card);
      box-shadow: var(--shadow-card);

      .page-title {
        margin: 0 0 6px;
        font-size: 22px;
        font-weight: 700;
        color: var(--text-primary);
        letter-spacing: -0.03em;
      }

      .page-description {
        margin: 0;
        font-size: 13px;
        color: var(--text-muted);
      }

      .header-actions {
        display: flex;
        align-items: center;

        .refresh-btn {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          height: 38px;
          padding: 0 16px;
          border: 1px solid var(--button-border);
          border-radius: 12px;
          background: var(--button-bg);
          color: var(--button-text);
          box-shadow: none;
          transition:
            border-color 0.2s ease,
            background-color 0.2s ease,
            color 0.2s ease;

          &:hover,
          &:focus-visible {
            border-color: var(--chart-line);
            background: var(--button-hover-bg);
            color: var(--text-primary);
          }

          .el-icon {
            font-size: 14px;
          }
        }
      }
    }
  }

  /* 统计卡片区域 */
  .stats-section {
    margin-bottom: 20px;

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: 16px;

      .stat-card {
        position: relative;
        overflow: hidden;
        padding: 20px;
        border: 1px solid var(--border);
        border-radius: 18px;
        background: var(--bg-card);
        box-shadow: var(--shadow-card);
        transition:
          transform 0.2s ease,
          border-color 0.2s ease,
          box-shadow 0.2s ease;

        &::before {
          content: "";
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 3px;
          background: var(--card-accent);
        }

        &:hover {
          transform: translateY(-2px);
          border-color: var(--chart-line);
          box-shadow: var(--shadow-hover);
        }

        &.stat-card--user {
          --card-accent: linear-gradient(90deg, #64748b, #94a3b8);
        }

        &.stat-card--article {
          --card-accent: linear-gradient(90deg, #f093fb, #f5576c);
        }

        &.stat-card--visits {
          --card-accent: linear-gradient(90deg, #475569, #64748b);
        }

        &.stat-card--today {
          --card-accent: linear-gradient(90deg, #43e97b, #38f9d7);
        }

        .stat-card__header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 14px;

          .stat-card__label {
            font-size: 13px;
            font-weight: 500;
            color: var(--text-muted);
          }

          .stat-card__icon {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 32px;
            height: 32px;
            border-radius: 10px;
            background: var(--bg-card-muted);
            color: var(--text-muted);
            line-height: 0;
          }
        }

        .stat-card__value {
          margin-bottom: 14px;

          .number {
            font-size: 32px;
            font-weight: 700;
            line-height: 1;
            letter-spacing: -0.04em;
            color: var(--text-primary);
          }
        }

        .stat-card__trend {
          .trend-indicator {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 4px 8px;
            border-radius: 999px;
            font-size: 12px;
            font-weight: 600;

            &.trend-indicator--positive {
              color: var(--success);
              background: var(--trend-positive-bg);
            }

            &.trend-indicator--neutral {
              color: var(--text-muted);
              background: var(--trend-neutral-bg);
            }
          }
        }
      }
    }
  }

  /* 卡片网格 */
  .card-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
  }

  /* 图表区域 */
  .chart-section {
    margin-bottom: 20px;
  }

  /* 快速操作区域 */
  .quick-actions-section {
    margin-bottom: 20px;

    .detail-card {
      min-height: auto;

      .detail-card__content {
        padding: 16px;
      }
    }
  }

  .detail-card {
    display: flex;
    flex-direction: column;
    border: 1px solid var(--border);
    border-radius: 18px;
    background: var(--bg-card);
    box-shadow: var(--shadow-card);
    overflow: hidden;

    .detail-card__header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
      padding: 16px 20px;
      border-bottom: 1px solid var(--border-light);

      .detail-card__title {
        display: inline-flex;
        align-items: center;
        gap: 10px;
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);

        .title-icon {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          width: 30px;
          height: 30px;
          border-radius: 10px;
          background: var(--title-icon-bg);
          color: var(--chart-line);
          line-height: 0;
        }
      }
    }

    .detail-card__content {
      flex: 1;
      padding: 20px;
      overflow: hidden;

      .action-grid {
        display: grid;
        grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
        gap: 12px;

        .action-item {
          display: flex;
          align-items: center;
          gap: 14px;
          padding: 16px;
          border: 1px solid var(--border);
          border-radius: 16px;
          background: var(--bg-card);
          cursor: pointer;
          transition:
            transform 0.2s ease,
            border-color 0.2s ease,
            box-shadow 0.2s ease;

          &:hover {
            transform: translateY(-2px);
            border-color: var(--chart-line);
            box-shadow: var(--shadow-card);

            .action-item__arrow {
              color: var(--chart-line);
              transform: translateX(4px);
            }
          }

          .action-item__icon {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 38px;
            height: 38px;
            border-radius: 12px;
            line-height: 0;

            &.action-item__icon--article {
              color: var(--action-article-text);
              background: var(--action-article-bg);
            }

            &.action-item__icon--comment {
              color: var(--action-comment-text);
              background: var(--action-comment-bg);
            }

            &.action-item__icon--user {
              color: var(--action-user-text);
              background: var(--action-user-bg);
            }

            &.action-item__icon--photo {
              color: var(--action-photo-text);
              background: var(--action-photo-bg);
            }
          }

          .action-item__content {
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 3px;

            .action-item__title {
              font-size: 14px;
              font-weight: 600;
              color: var(--text-primary);
            }

            .action-item__desc {
              font-size: 12px;
              color: var(--text-muted);
            }
          }

          .action-item__arrow {
            color: var(--text-muted);
            transition:
              color 0.2s ease,
              transform 0.2s ease;
          }

          .action-item__badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-width: 22px;
            height: 22px;
            padding: 0 6px;
            border-radius: 999px;
            background: var(--danger);
            color: #fff;
            font-size: 12px;
            font-weight: 600;
          }
        }
      }
    }
  }
}

html.dark {
  .dashboard-container {
    --bg-page: #0a0a0a;
    --bg-card: #1a1a1a;
    --bg-card-muted: #262626;
    --text-primary: #e2e8f0;
    --text-regular: #cbd5e1;
    --text-muted: #8ea0b7;
    --border: #333333;
    --border-light: #2a2a2a;
    --shadow-card: 0 14px 32px rgba(0, 0, 0, 0.28);
    --shadow-hover: 0 18px 36px rgba(0, 0, 0, 0.36);
    --page-glow: rgba(0, 0, 0, 0.2);
    --page-overlay-top: rgba(10, 10, 10, 0.64);
    --page-overlay-bottom: rgba(10, 10, 10, 0.96);
    --button-bg: #262626;
    --button-hover-bg: #333333;
    --button-border: #404040;
    --button-text: #dbeafe;
    --title-icon-bg: rgba(148, 163, 184, 0.14);
    --trend-positive-bg: rgba(16, 185, 129, 0.18);
    --trend-neutral-bg: rgba(148, 163, 184, 0.16);
    --toggle-bg: #262626;
    --toggle-hover-bg: #333333;
    --toggle-active-text: #eff6ff;
    --action-article-bg: rgba(167, 139, 250, 0.16);
    --action-article-text: #c4b5fd;
    --action-comment-bg: rgba(56, 189, 248, 0.16);
    --action-comment-text: #7dd3fc;
    --action-user-bg: rgba(52, 211, 153, 0.16);
    --action-user-text: #6ee7b7;
    --action-photo-bg: rgba(251, 191, 36, 0.16);
    --action-photo-text: #fcd34d;
    --danger: #ef4444;
  }
}

@media (max-width: 1200px) {
  .dashboard-container {
    .stats-section {
      .stats-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }
    }

    .chart-section {
      .card-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }
    }
  }
}

@media (max-width: 768px) {
  .dashboard-container {
    padding: 16px;

    .page-header {
      .header-content {
        flex-direction: column;
        align-items: stretch;

        .header-actions {
          width: 100%;

          .refresh-btn {
            width: 100%;
            justify-content: center;
          }
        }
      }
    }

    .stats-section {
      .stats-grid {
        grid-template-columns: 1fr;
      }
    }

    .chart-section {
      .card-grid {
        grid-template-columns: 1fr;
      }
    }

    .detail-card {
      .detail-card__header {
        flex-direction: column;
        align-items: flex-start;
        padding: 14px 16px;
      }

      .detail-card__content {
        padding: 16px;

        .action-grid {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>
