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

    <!-- 统计数据卡片区域 -->
    <div class="statistics-section">
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

    <!-- 详细数据展示区域 -->
    <div class="details-section">
      <div class="details-grid">
        <!-- 文章状态分布 -->
        <div class="detail-card">
          <div class="detail-card__header">
            <div class="detail-card__title">
              <span class="title-icon">
                <el-icon><PieChart /></el-icon>
              </span>
              <span>文章状态分布</span>
            </div>
          </div>
          <div class="detail-card__content">
            <div class="status-list" v-if="!statisticsLoading">
              <div class="status-item">
                <div class="status-item__header">
                  <span class="status-item__label status-item__label--published">
                    <span class="status-dot status-dot--published"></span>
                    已发布
                  </span>
                  <span class="status-item__count">{{ articleStatistics?.publishedCount || 0 }}</span>
                </div>
                <div class="status-item__bar">
                  <div class="progress-bar">
                    <div class="progress-bar__fill progress-bar__fill--published" :style="{ width: getPercentage(articleStatistics?.publishedCount, articleStatistics?.totalCount) + '%' }"></div>
                  </div>
                  <span class="progress-bar__text">{{ getPercentage(articleStatistics?.publishedCount, articleStatistics?.totalCount) }}%</span>
                </div>
              </div>

              <div class="status-item">
                <div class="status-item__header">
                  <span class="status-item__label status-item__label--reviewing">
                    <span class="status-dot status-dot--reviewing"></span>
                    审核中
                  </span>
                  <span class="status-item__count">{{ articleStatistics?.reviewingCount || 0 }}</span>
                </div>
                <div class="status-item__bar">
                  <div class="progress-bar">
                    <div class="progress-bar__fill progress-bar__fill--reviewing" :style="{ width: getPercentage(articleStatistics?.reviewingCount, articleStatistics?.totalCount) + '%' }"></div>
                  </div>
                  <span class="progress-bar__text">{{ getPercentage(articleStatistics?.reviewingCount, articleStatistics?.totalCount) }}%</span>
                </div>
              </div>

              <div class="status-item">
                <div class="status-item__header">
                  <span class="status-item__label status-item__label--draft">
                    <span class="status-dot status-dot--draft"></span>
                    草稿箱
                  </span>
                  <span class="status-item__count">{{ articleStatistics?.draftCount || 0 }}</span>
                </div>
                <div class="status-item__bar">
                  <div class="progress-bar">
                    <div class="progress-bar__fill progress-bar__fill--draft" :style="{ width: getPercentage(articleStatistics?.draftCount, articleStatistics?.totalCount) + '%' }"></div>
                  </div>
                  <span class="progress-bar__text">{{ getPercentage(articleStatistics?.draftCount, articleStatistics?.totalCount) }}%</span>
                </div>
              </div>

              <div class="status-item">
                <div class="status-item__header">
                  <span class="status-item__label status-item__label--garbage">
                    <span class="status-dot status-dot--garbage"></span>
                    回收站
                  </span>
                  <span class="status-item__count">{{ articleStatistics?.garbageCount || 0 }}</span>
                </div>
                <div class="status-item__bar">
                  <div class="progress-bar">
                    <div class="progress-bar__fill progress-bar__fill--garbage" :style="{ width: getPercentage(articleStatistics?.garbageCount, articleStatistics?.totalCount) + '%' }"></div>
                  </div>
                  <span class="progress-bar__text">{{ getPercentage(articleStatistics?.garbageCount, articleStatistics?.totalCount) }}%</span>
                </div>
              </div>
            </div>
            <el-skeleton v-else :rows="4" animated />
          </div>
        </div>

        <!-- 网站访问趋势 -->
        <div class="detail-card detail-card--chart">
          <div class="detail-card__header">
            <div class="detail-card__title">
              <span class="title-icon">
                <el-icon><TrendCharts /></el-icon>
              </span>
              <span>访问趋势</span>
            </div>
            <div class="detail-card__actions">
              <div class="trend-days">
                <button
                  :class="['trend-days__btn', trendDays === 7 ? 'is-active' : '']"
                  @click="trendDays = 7; handleDaysChange()">7 天</button>
                <button
                  :class="['trend-days__btn', trendDays === 14 ? 'is-active' : '']"
                  @click="trendDays = 14; handleDaysChange()">14 天</button>
                <button
                  :class="['trend-days__btn', trendDays === 30 ? 'is-active' : '']"
                  @click="trendDays = 30; handleDaysChange()">30 天</button>
              </div>
            </div>
          </div>
          <div class="detail-card__content">
            <div class="trend-chart-container">
              <el-skeleton v-if="trendLoading" :rows="6" animated />
              <div v-else ref="chartRef" class="trend-chart"></div>
            </div>
          </div>
        </div>
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
import { ref, onMounted, onBeforeUnmount, computed, nextTick } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { User, Document, ChatLineRound, View, PieChart, Monitor, Operation, Picture, TrendCharts, Refresh, Top, Right, ArrowRight } from "@element-plus/icons-vue";
import { getUserTotalCount } from "@/api/user";
import { getArticleStatistics } from "@/api/article";
import { getTodayVisitorCount, getTotalVisitorCount, getVisitorTrend } from "@/api/visitorLog";
import * as echarts from "echarts";
import ProjectLinks from "@/components/ProjectLinks.vue";

// 路由和状态管理
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const loading = ref(false); // 刷新数据 loading
const statisticsLoading = ref(true); // 统计数据加载状态
const userCount = ref(0); // 用户总数
const articleStatistics = ref(null); // 文章统计数据
const totalVisits = ref(0); // 总访问量
const todayVisits = ref(0); // 今日访问量
const trendDays = ref(7); // 访客趋势天数，默认 7 天
const visitorTrend = ref([]); // 访客趋势数据
const trendLoading = ref(false); // 趋势图加载状态
const chartInstance = ref(null); // ECharts 图表实例
const chartRef = ref(null); // 图表 DOM 引用

// 计算属性
const currentUser = computed(() => {
  return userStore.user?.nickname || "管理员";
});

// 方法定义
// 获取所有统计数据
const fetchAllStatistics = async () => {
  try {
    statisticsLoading.value = true;

    // 并行获取所有统计数据
    const [userResult, articleResult, totalVisitorResult, todayVisitorResult] = await Promise.allSettled([getUserTotalCount(), getArticleStatistics(), getTotalVisitorCount(), getTodayVisitorCount()]);

    // 处理用户总数
    if (userResult.status === "fulfilled") {
      userCount.value = userResult.value.data.data || 0;
    } else {
      console.error("获取用户统计失败:", userResult.reason);
    }

    // 处理文章统计
    if (articleResult.status === "fulfilled") {
      articleStatistics.value = articleResult.value.data.data;
    } else {
      console.error("获取文章统计失败:", articleResult.reason);
    }

    // 处理总访问量
    if (totalVisitorResult.status === "fulfilled") {
      totalVisits.value = totalVisitorResult.value.data.data || 0;
    } else {
      console.error("获取总访问量失败:", totalVisitorResult.reason);
    }

    // 处理今日访问量
    if (todayVisitorResult.status === "fulfilled") {
      todayVisits.value = todayVisitorResult.value.data.data || 0;
    } else {
      console.error("获取今日访问量失败:", todayVisitorResult.reason);
    }
  } catch (error) {
    ElMessage.error("获取统计数据失败");
    console.error("获取统计数据失败:", error);
  } finally {
    statisticsLoading.value = false;
  }
};

// 计算百分比
const getPercentage = (value, total) => {
  if (!total || total === 0) return 0;
  return Math.round((value / total) * 100);
};

// 获取访客趋势数据
const fetchVisitorTrend = async () => {
  try {
    trendLoading.value = true;
    const res = await getVisitorTrend(trendDays.value);
    visitorTrend.value = res.data.data || [];
  } catch (error) {
    ElMessage.error("获取访客趋势失败");
    console.error("获取访客趋势失败:", error);
  } finally {
    trendLoading.value = false;
    // 等待 DOM 更新完成后再渲染图表
    await nextTick();
    renderChart();
  }
};

// 渲染 ECharts 折线图
const renderChart = () => {
  if (!chartRef.value) return;

  // 销毁旧实例，重新初始化
  if (chartInstance.value) {
    chartInstance.value.dispose();
    chartInstance.value = null;
  }

  // 初始化图表实例
  chartInstance.value = echarts.init(chartRef.value);

  // 准备数据
  const dates = visitorTrend.value.map((item) => item.date);
  const counts = visitorTrend.value.map((item) => item.count);

  // 配置图表选项 - 简约风格
  const option = {
    tooltip: {
      trigger: "axis",
      backgroundColor: "rgba(255, 255, 255, 0.98)",
      borderColor: "#e2e8f0",
      borderWidth: 1,
      textStyle: {
        color: "#1e3a8a",
      },
      axisPointer: {
        type: "line",
        lineStyle: {
          color: "#3b82f6",
          width: 1,
        },
      },
    },
    grid: {
      left: "4%",
      right: "2%",
      bottom: "4%",
      top: "8%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: dates,
      axisLine: {
        lineStyle: {
          color: "#e2e8f0",
        },
      },
      axisLabel: {
        color: "#64748b",
        fontSize: 12,
        formatter: (value) => {
          const date = new Date(value);
          return `${date.getMonth() + 1}/${date.getDate()}`;
        },
      },
      axisTick: {
        show: false,
      },
    },
    yAxis: {
      type: "value",
      name: "访问量",
      nameTextStyle: {
        color: "#64748b",
        fontSize: 12,
        padding: [0, 0, 0, 0],
      },
      minInterval: 1,
      splitLine: {
        lineStyle: {
          color: "#f1f5f9",
          type: "dashed",
        },
      },
      axisLine: {
        show: false,
      },
      axisLabel: {
        color: "#64748b",
        fontSize: 12,
        padding: [0, 8, 0, 0],
      },
      axisTick: {
        show: false,
      },
    },
    series: [
      {
        name: "访问量",
        type: "line",
        smooth: true,
        symbol: "circle",
        symbolSize: 6,
        itemStyle: {
          color: "#1e40af",
          borderWidth: 2,
          borderColor: "#fff",
        },
        lineStyle: {
          color: "#1e40af",
          width: 2.5,
        },
        areaStyle: {
          color: {
            type: "linear",
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              {
                offset: 0,
                color: "rgba(30, 64, 175, 0.12)",
              },
              {
                offset: 1,
                color: "rgba(30, 64, 175, 0.02)",
              },
            ],
          },
        },
        data: counts,
      },
    ],
  };

  chartInstance.value.setOption(option);
};

// 切换天数
const handleDaysChange = () => {
  fetchVisitorTrend();
};

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path);
};

// 刷新数据
const refreshData = () => {
  loading.value = true;
  Promise.all([fetchAllStatistics(), fetchVisitorTrend()]).finally(() => {
    loading.value = false;
    ElMessage.success("数据已刷新");
  });
};

// 窗口大小变化时重新调整图表大小
const handleResize = () => {
  chartInstance.value?.resize();
};

// 生命周期
onMounted(() => {
  fetchAllStatistics();
  fetchVisitorTrend();

  // 监听窗口大小变化
  window.addEventListener("resize", handleResize);
});

onBeforeUnmount(() => {
  // 销毁图表实例
  if (chartInstance.value) {
    chartInstance.value.dispose();
    chartInstance.value = null;
  }

  // 移除窗口大小变化监听
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 主色调变量
$primary: #1e40af;
$primary-light: #3b82f6;
$accent: #f59e0b;
$bg-page: #f8fafc;
$bg-card: #ffffff;
$text-primary: #1e3a8a;
$text-regular: #475569;
$text-muted: #64748b;
$border: #e2e8f0;
$border-light: #f1f5f9;

// 状态颜色
$success: #10b981;
$warning: #f59e0b;
$info: #64748b;
$danger: #ef4444;

// 仪表盘容器
.dashboard-container {
  padding: 24px;
  background-color: $bg-page;
  min-height: calc(100vh - 88px);

  // 页面标题区域
  .page-header {
    margin-bottom: 24px;

    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 16px 20px;
      background: $bg-card;
      border-radius: 8px;
      border: 1px solid $border;

      .page-title {
        font-size: 20px;
        font-weight: 600;
        color: $text-primary;
        margin: 0 0 4px 0;
        letter-spacing: -0.02em;
      }

      .page-description {
        font-size: 13px;
        color: $text-muted;
        margin: 0;
      }

      .refresh-btn {
        display: flex;
        align-items: center;
        gap: 6px;
        padding: 8px 16px;
        font-size: 13px;
        border-radius: 6px;

        .el-icon {
          font-size: 14px;
        }
      }
    }
  }

  // 统计数据卡片区域
  .statistics-section {
    margin-bottom: 20px;

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 16px;
    }

    .stat-card {
      background: $bg-card;
      border: 1px solid $border;
      border-radius: 8px;
      padding: 20px;
      transition: all 0.2s ease;
      position: relative;
      overflow: hidden;

      &:hover {
        border-color: $primary-light;
        box-shadow: 0 4px 12px rgba(30, 64, 175, 0.08);
      }

      // 顶部装饰线
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
      }

      &--user::before {
        background: linear-gradient(90deg, #5b86e5, #36d1dc);
      }

      &--article::before {
        background: linear-gradient(90deg, #f093fb, #f5576c);
      }

      &--visits::before {
        background: linear-gradient(90deg, #4facfe, #00f2fe);
      }

      &--today::before {
        background: linear-gradient(90deg, #43e97b, #38f9d7);
      }

      &__header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 12px;

        &__label {
          font-size: 13px;
          color: $text-muted;
          font-weight: 500;
        }

        &__icon {
          display: inline-flex !important;
          align-items: center !important;
          justify-content: center !important;
          width: 28px;
          height: 28px;
          border-radius: 5px;
          color: $text-muted;
          line-height: 0;
          vertical-align: middle;

          .el-icon {
            width: 16px;
            height: 16px;
            display: inline-block !important;
            vertical-align: middle !important;
          }
        }
      }

      &__value {
        margin-bottom: 12px;

        .number {
          font-size: 32px;
          font-weight: 700;
          color: $text-primary;
          letter-spacing: -0.03em;
          line-height: 1;
        }
      }

      &__trend {
        display: flex;
        align-items: center;
        gap: 4px;

        .trend-indicator {
          display: flex;
          align-items: center;
          gap: 3px;
          font-size: 12px;
          padding: 4px 8px;
          border-radius: 4px;
          font-weight: 500;

          &--positive {
            color: $success;
            background: rgba(16, 185, 129, 0.08);
          }

          &--neutral {
            color: $text-muted;
            background: rgba(100, 116, 139, 0.08);
          }

          .el-icon {
            font-size: 12px;
          }
        }
      }
    }
  }

  // 详细数据展示区域
  .details-section {
    margin-bottom: 20px;

    .details-grid {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 16px;
    }

    .detail-card {
      background: $bg-card;
      border: 1px solid $border;
      border-radius: 8px;
      overflow: hidden;
      height: 420px;
      display: flex;
      flex-direction: column;

      &--chart {
        min-height: 420px;
      }

      &__header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px 20px;
        border-bottom: 1px solid $border-light;
        flex-shrink: 0;

        &__title {
          display: inline-flex;
          align-items: center;
          gap: 10px;
          font-size: 15px;
          font-weight: 600;
          color: $text-primary;
          vertical-align: middle;

          .title-icon {
            display: inline-flex !important;
            align-items: center !important;
            justify-content: center !important;
            width: 28px;
            height: 28px;
            border-radius: 5px;
            background: rgba(30, 64, 175, 0.06);
            color: $primary;
            vertical-align: middle;
            line-height: 0;

            .el-icon {
              width: 16px;
              height: 16px;
              display: inline-block !important;
              vertical-align: middle !important;
            }
          }
        }

        &__actions {
          display: flex;
          gap: 8px;
        }
      }

      &__content {
        padding: 20px;
        flex: 1;
        overflow: hidden;
      }
    }

    // 文章状态列表
    .status-list {
      .status-item {
        margin-bottom: 20px;

        &:last-child {
          margin-bottom: 0;
        }

        &__header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;

          &__label {
            display: flex;
            align-items: center;
            gap: 8px;
            font-size: 14px;
            font-weight: 500;
            color: $text-regular;

            &--published { color: $success; }
            &--reviewing { color: $warning; }
            &--draft { color: $info; }
            &--garbage { color: $danger; }
          }

          &__count {
            font-size: 16px;
            font-weight: 600;
            color: $text-primary;
          }
        }

        &__bar {
          display: flex;
          align-items: center;
          gap: 12px;
        }
      }

      .status-dot {
        width: 8px;
        height: 8px;
        border-radius: 50%;

        &--published { background: $success; }
        &--reviewing { background: $warning; }
        &--draft { background: $info; }
        &--garbage { background: $danger; }
      }

      .progress-bar {
        flex: 1;
        height: 8px;
        background: $bg-page;
        border-radius: 4px;
        overflow: hidden;

        &__fill {
          height: 100%;
          border-radius: 4px;
          transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);

          &--published { background: $success; }
          &--reviewing { background: $warning; }
          &--draft { background: $info; }
          &--garbage { background: $danger; }
        }

        &__text {
          font-size: 12px;
          font-weight: 600;
          color: $text-muted;
          min-width: 42px;
          text-align: right;
        }
      }
    }

    // 访客趋势图表
    .trend-chart-container {
      height: 340px;

      .trend-chart {
        width: 100%;
        height: 340px;
      }
    }

    // 切换天数按钮
    .trend-days {
      display: flex;
      gap: 4px;
      background: $bg-page;
      padding: 4px;
      border-radius: 6px;

      &__btn {
        padding: 6px 14px;
        font-size: 13px;
        font-weight: 500;
        color: $text-regular;
        background: transparent;
        border: none;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.2s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.5);
          color: $text-primary;
        }

        &.is-active {
          background: $primary;
          color: white;
          box-shadow: 0 1px 3px rgba(30, 64, 175, 0.2);
        }
      }
    }
  }

  // 快速操作区域
  .quick-actions-section {
    .action-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
      gap: 12px;
    }

    .action-item {
      display: flex;
      align-items: center;
      gap: 14px;
      padding: 16px;
      background: $bg-card;
      border: 1px solid $border;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s ease;
      position: relative;

      &:hover {
        border-color: $primary-light;
        box-shadow: 0 2px 8px rgba(30, 64, 175, 0.06);

        .action-item__arrow {
          transform: translateX(4px);
          color: $primary;
        }
      }

      &:active {
        transform: scale(0.98);
      }

      &__icon {
        display: inline-flex !important;
        align-items: center !important;
        justify-content: center !important;
        width: 36px;
        height: 36px;
        border-radius: 6px;
        transition: all 0.2s ease;
        vertical-align: middle;
        line-height: 0;

        .el-icon {
          width: 18px;
          height: 18px;
          display: inline-block !important;
          vertical-align: middle !important;
        }

        &--article {
          color: #7c3aed;
          background: rgba(124, 58, 237, 0.08);
        }

        &--comment {
          color: #0ea5e9;
          background: rgba(14, 165, 233, 0.08);
        }

        &--user {
          color: #10b981;
          background: rgba(16, 185, 129, 0.08);
        }

        &--photo {
          color: #f59e0b;
          background: rgba(245, 158, 11, 0.08);
        }
      }

      &__content {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 2px;
        justify-content: center;

        &__title {
          font-size: 14px;
          font-weight: 600;
          color: $text-primary;
        }

        &__desc {
          font-size: 12px;
          color: $text-muted;
        }
      }

      &__arrow {
        display: flex;
        align-items: center;
        color: $text-muted;
        transition: all 0.2s ease;
        font-size: 16px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 1200px) {
  .dashboard-container {
    .statistics-section {
      .stats-grid {
        grid-template-columns: repeat(2, 1fr);
      }
    }

    .details-section {
      .details-grid {
        grid-template-columns: 1fr;
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
        align-items: flex-start;
        gap: 12px;

        .refresh-btn {
          width: 100%;
          justify-content: center;
        }
      }
    }

    .statistics-section {
      .stats-grid {
        grid-template-columns: 1fr;
        gap: 12px;
      }

      .stat-card {
        padding: 16px;

        &__value {
          .number {
            font-size: 28px;
          }
        }
      }
    }

    .details-section {
      .detail-card {
        &__header {
          padding: 14px 16px;
        }

        &__content {
          padding: 16px;
        }
      }
    }

    .quick-actions-section {
      .action-grid {
        grid-template-columns: 1fr;
      }

      .action-item {
        padding: 14px;
      }
    }
  }
}
</style>
