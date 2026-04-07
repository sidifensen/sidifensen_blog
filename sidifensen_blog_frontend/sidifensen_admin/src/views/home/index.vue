<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <PageHeader title="控制台" description="斯蒂芬森社区管理系统">
      <template #actions>
        <el-button class="refresh-btn" @click="refreshData" :loading="loading">
          <el-icon><Refresh /></el-icon>
          刷新数据
        </el-button>
      </template>
    </PageHeader>

    <!-- 统计卡片区域 -->
    <div class="stats-section">
      <div class="stats-grid">
        <StatCard
          label="用户总数"
          :value="userCount"
          :icon="User"
          type="user"
          :loading="statisticsLoading"
          trend-type="positive"
          trend-text="较昨日 +12"
        />
        <StatCard
          label="文章总数"
          :value="articleStatistics?.totalCount || 0"
          :icon="Document"
          type="article"
          :loading="statisticsLoading"
          trend-type="positive"
          trend-text="较昨日 +5"
        />
        <StatCard
          label="总访问量"
          :value="totalVisits"
          :icon="Monitor"
          type="visits"
          :loading="statisticsLoading"
          trend-type="positive"
          trend-text="累计数据"
        />
        <StatCard
          label="今日访问"
          :value="todayVisits"
          :icon="View"
          type="today"
          :loading="statisticsLoading"
          trend-type="neutral"
          trend-text="实时数据"
        />
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
        <ArticleStatusCard :data="articleStatistics" :loading="statisticsLoading" />
        <VisitorTrendCard
          :data="visitorTrend"
          :loading="trendLoading"
          v-model:days="trendDays"
          @days-change="handleDaysChange"
        />
        <InteractionTrendCard :data="interactionTrend" :loading="interactionTrendLoading" />
        <UserGrowthCard :data="weeklyTrend" :loading="weeklyTrendLoading" />
        <VipStatisticsCard :data="vipStatisticsData" :loading="vipStatisticsLoading" />
      </div>
    </div>

    <!-- 快速操作区域 -->
    <QuickActions :actions="quickActions" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import {
  User,
  Document,
  ChatLineRound,
  View,
  Monitor,
  Picture,
  Refresh,
} from '@element-plus/icons-vue'
import {
  getDashboardStatistics,
  getExamineCount,
  getVipStatistics,
  getWeeklyTrend,
  getInteractionTrend,
  getVisitorTrend,
} from '@/api/dashboard'

// 公共组件
import PageHeader from '@/components/management/PageHeader.vue'
import StatCard from '@/components/cards/StatCard.vue'
import QuickActions from '@/components/actions/QuickActions.vue'

// 图表卡片组件
import UserActivityCard from '@/components/cards/UserActivityCard.vue'
import ArticleStatusCard from '@/components/cards/ArticleStatusCard.vue'
import VisitorTrendCard from '@/components/cards/VisitorTrendCard.vue'
import InteractionTrendCard from '@/components/cards/InteractionTrendCard.vue'
import UserGrowthCard from '@/components/cards/UserGrowthCard.vue'
import VipStatisticsCard from '@/components/cards/VipStatisticsCard.vue'

// 快速操作配置
const quickActions = computed(() => [
  {
    path: '/article/examine',
    title: '文章审核',
    description: '审核待发布文章',
    icon: Document,
    type: 'article',
    badge: examineCountData.value.articleCount,
  },
  {
    path: '/comment/examine',
    title: '评论审核',
    description: '管理用户评论',
    icon: ChatLineRound,
    type: 'comment',
    badge: examineCountData.value.commentCount,
  },
  {
    path: '/system/user',
    title: '用户管理',
    description: '管理系统用户',
    icon: User,
    type: 'user',
    badge: 0,
  },
  {
    path: '/photo/examine',
    title: '图片审核',
    description: '审核上传图片',
    icon: Picture,
    type: 'photo',
    badge: examineCountData.value.photoCount,
  },
])

// 响应式数据
const loading = ref(false)
const statisticsLoading = ref(true)
const userCount = ref(0)
const todayActiveUserCount = ref(0)
const articleStatistics = ref(null)
const totalVisits = ref(0)
const todayVisits = ref(0)
const trendDays = ref(7)
const visitorTrend = ref([])
const trendLoading = ref(false)
const examineCountData = ref({
  articleCount: 0,
  commentCount: 0,
  photoCount: 0,
})
const vipStatisticsData = ref({
  totalVipCount: 0,
  newVipCount: 0,
  newVipCount30Days: 0,
  normalUserCount: 0,
  vipPercentage: 0,
})
const vipStatisticsLoading = ref(false)
const weeklyTrend = ref([])
const weeklyTrendLoading = ref(false)
const interactionTrend = ref([])
const interactionTrendLoading = ref(false)

// 获取所有统计数据
const fetchAllStatistics = async () => {
  try {
    statisticsLoading.value = true
    const res = await getDashboardStatistics(7)
    const data = res.data

    userCount.value = data.userTotalCount || 0
    todayActiveUserCount.value = data.todayActiveUserCount || 0
    articleStatistics.value = data.articleStatistics || null
    totalVisits.value = data.totalVisits || 0
    todayVisits.value = data.todayVisits || 0
    visitorTrend.value = data.visitorTrend || []
  } catch (error) {
    ElMessage.error('获取统计数据失败')
    console.error('获取统计数据失败:', error)
  } finally {
    statisticsLoading.value = false
  }
}

// 获取访客趋势数据
const fetchVisitorTrend = async () => {
  try {
    trendLoading.value = true
    const res = await getVisitorTrend(trendDays.value)
    visitorTrend.value = res.data || []
  } catch (error) {
    ElMessage.error('获取访客趋势失败')
    console.error('获取访客趋势失败:', error)
  } finally {
    trendLoading.value = false
  }
}

// 获取待审核数量
const fetchExamineCount = async () => {
  try {
    const res = await getExamineCount()
    examineCountData.value = res.data || { articleCount: 0, commentCount: 0, photoCount: 0 }
  } catch (error) {
    console.error('获取待审核数量失败:', error)
  }
}

// 获取互动趋势数据
const fetchInteractionTrend = async () => {
  try {
    interactionTrendLoading.value = true
    const res = await getInteractionTrend()
    interactionTrend.value = res.data || []
  } catch (error) {
    console.error('获取互动趋势数据失败:', error)
  } finally {
    interactionTrendLoading.value = false
  }
}

// 获取周趋势数据
const fetchWeeklyTrend = async () => {
  try {
    weeklyTrendLoading.value = true
    const res = await getWeeklyTrend()
    weeklyTrend.value = res.data || []
  } catch (error) {
    console.error('获取周趋势数据失败:', error)
  } finally {
    weeklyTrendLoading.value = false
  }
}

// 获取 VIP 统计数据
const fetchVipStatistics = async () => {
  try {
    vipStatisticsLoading.value = true
    const res = await getVipStatistics()
    vipStatisticsData.value = res.data || { totalVipCount: 0, newVipCount: 0 }
  } catch (error) {
    console.error('获取VIP统计数据失败:', error)
  } finally {
    vipStatisticsLoading.value = false
  }
}

// 切换天数
const handleDaysChange = () => {
  fetchVisitorTrend()
}

// 刷新数据
const refreshData = () => {
  loading.value = true
  fetchAllStatistics().finally(() => {
    loading.value = false
    ElMessage.success('数据已刷新')
  })
}

// 生命周期
onMounted(async () => {
  await fetchAllStatistics()
  await fetchExamineCount()
  await fetchInteractionTrend()
  await fetchWeeklyTrend()
  await fetchVipStatistics()
})
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
    linear-gradient(180deg, var(--page-overlay-top), var(--page-overlay-bottom)), var(--bg-page);

  /* 统计卡片区域 */
  .stats-section {
    margin-bottom: 20px;

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: 16px;
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
  }
}
</style>
