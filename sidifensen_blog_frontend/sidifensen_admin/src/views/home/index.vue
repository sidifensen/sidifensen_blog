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
import { ref, onMounted, onBeforeUnmount, nextTick, watch } from "vue";
import { useRouter } from "vue-router";
import { useDarkStore } from "@/stores/darkStore";
import { storeToRefs } from "pinia";
import { User, Document, ChatLineRound, View, PieChart, Monitor, Operation, Picture, TrendCharts, Refresh, Top, Right, ArrowRight } from "@element-plus/icons-vue";
import { getDashboardStatistics } from "@/api/dashboard";
import * as echarts from "echarts";

// 路由和状态管理
const router = useRouter();
const darkStore = useDarkStore();
const { isDark } = storeToRefs(darkStore);

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

// 方法定义
// 获取所有统计数据（使用聚合接口）
const fetchAllStatistics = async () => {
  try {
    statisticsLoading.value = true;

    // 使用聚合接口一次性获取所有统计数据
    const res = await getDashboardStatistics(trendDays.value);
    const data = res.data.data;

    // 解析统计数据
    userCount.value = data.userTotalCount || 0;
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

// 计算百分比
const getPercentage = (value, total) => {
  if (!total || total === 0) return 0;
  return Math.round((value / total) * 100);
};

// 获取访客趋势数据（切换天数时调用聚合接口）
const fetchVisitorTrend = async () => {
  try {
    trendLoading.value = true;
    // 切换天数时重新调用聚合接口获取数据
    await fetchAllStatistics();
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
  const chartStyles = getComputedStyle(chartRef.value);

  // 读取页面主题变量，让图表跟随浅色/黑夜模式切换
  const getThemeValue = (name, fallback) => {
    const value = chartStyles.getPropertyValue(name).trim();
    return value || fallback;
  };

  // 配置图表选项 - 简约风格
  const option = {
    tooltip: {
      trigger: "axis",
      backgroundColor: getThemeValue("--chart-tooltip-bg", "rgba(255, 255, 255, 0.98)"),
      borderColor: getThemeValue("--chart-tooltip-border", "#e2e8f0"),
      borderWidth: 1,
      textStyle: {
        color: getThemeValue("--chart-tooltip-text", "#1e3a8a"),
      },
      axisPointer: {
        type: "line",
        lineStyle: {
          color: getThemeValue("--chart-line", "#1e40af"),
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
          color: getThemeValue("--chart-axis-line", "#e2e8f0"),
        },
      },
      axisLabel: {
        color: getThemeValue("--chart-axis-text", "#64748b"),
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
        color: getThemeValue("--chart-axis-text", "#64748b"),
        fontSize: 12,
        padding: [0, 0, 0, 0],
      },
      minInterval: 1,
      splitLine: {
        lineStyle: {
          color: getThemeValue("--chart-grid-line", "#f1f5f9"),
          type: "dashed",
        },
      },
      axisLine: {
        show: false,
      },
      axisLabel: {
        color: getThemeValue("--chart-axis-text", "#64748b"),
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
          color: getThemeValue("--chart-line", "#1e40af"),
          borderWidth: 2,
          borderColor: getThemeValue("--chart-point-border", "#ffffff"),
        },
        lineStyle: {
          color: getThemeValue("--chart-line", "#1e40af"),
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
                color: getThemeValue("--chart-area-start", "rgba(30, 64, 175, 0.12)"),
              },
              {
                offset: 1,
                color: getThemeValue("--chart-area-end", "rgba(30, 64, 175, 0.02)"),
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
  fetchAllStatistics().finally(() => {
    loading.value = false;
    ElMessage.success("数据已刷新");
  });
};

// 窗口大小变化时重新调整图表大小
const handleResize = () => {
  chartInstance.value?.resize();
};

// 生命周期
onMounted(async () => {
  await fetchAllStatistics();
  await nextTick();
  renderChart();

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

watch(isDark, async () => {
  await nextTick();
  renderChart();
});
</script>

<style lang="scss" scoped>
.dashboard-container {
  --bg-page: #f8fafc;
  --bg-card: #ffffff;
  --bg-card-muted: #f3f7fd;
  --text-primary: #1e3a8a;
  --text-regular: #475569;
  --text-muted: #64748b;
  --border: #e2e8f0;
  --border-light: #edf2f7;
  --shadow-card: 0 10px 24px rgba(15, 23, 42, 0.06);
  --shadow-hover: 0 16px 30px rgba(30, 64, 175, 0.1);
  --page-glow: rgba(59, 130, 246, 0.08);
  --page-overlay-top: rgba(255, 255, 255, 0.35);
  --page-overlay-bottom: rgba(248, 250, 252, 0.92);
  --button-bg: #ffffff;
  --button-hover-bg: #eff6ff;
  --button-border: #d6e3ff;
  --button-text: #1e3a8a;
  --title-icon-bg: rgba(30, 64, 175, 0.08);
  --trend-positive-bg: rgba(16, 185, 129, 0.12);
  --trend-neutral-bg: rgba(100, 116, 139, 0.12);
  --toggle-bg: #eef4ff;
  --toggle-hover-bg: #dbeafe;
  --toggle-active-text: #eff6ff;
  --action-article-bg: rgba(124, 58, 237, 0.12);
  --action-article-text: #7c3aed;
  --action-comment-bg: rgba(14, 165, 233, 0.12);
  --action-comment-text: #0284c7;
  --action-user-bg: rgba(16, 185, 129, 0.12);
  --action-user-text: #059669;
  --action-photo-bg: rgba(245, 158, 11, 0.12);
  --action-photo-text: #d97706;
  --chart-tooltip-bg: rgba(255, 255, 255, 0.98);
  --chart-tooltip-border: #e2e8f0;
  --chart-tooltip-text: #1e3a8a;
  --chart-axis-line: #d9e2ef;
  --chart-axis-text: #64748b;
  --chart-grid-line: #edf2f7;
  --chart-line: #1e40af;
  --chart-point-border: #ffffff;
  --chart-area-start: rgba(30, 64, 175, 0.14);
  --chart-area-end: rgba(30, 64, 175, 0.02);
  --success: #10b981;
  --warning: #f59e0b;
  --info: #64748b;
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
  .statistics-section {
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
          --card-accent: linear-gradient(90deg, #5b86e5, #36d1dc);
        }

        &.stat-card--article {
          --card-accent: linear-gradient(90deg, #f093fb, #f5576c);
        }

        &.stat-card--visits {
          --card-accent: linear-gradient(90deg, #4facfe, #00f2fe);
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

  /* 详情模块区域 */
  .details-section {
    margin-bottom: 20px;

    .details-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 16px;
    }
  }

  .detail-card {
    display: flex;
    flex-direction: column;
    min-height: 420px;
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

      .detail-card__actions {
        display: flex;
        align-items: center;
        gap: 8px;

        .trend-days {
          display: flex;
          gap: 4px;
          padding: 4px;
          border-radius: 12px;
          background: var(--toggle-bg);

          .trend-days__btn {
            padding: 6px 14px;
            border: none;
            border-radius: 8px;
            background: transparent;
            color: var(--text-regular);
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            transition:
              background-color 0.2s ease,
              color 0.2s ease;

            &:hover {
              background: var(--toggle-hover-bg);
              color: var(--text-primary);
            }

            &.is-active {
              background: var(--chart-line);
              color: var(--toggle-active-text);
            }
          }
        }
      }
    }

    .detail-card__content {
      flex: 1;
      padding: 20px;
      overflow: hidden;

      .status-list {
        .status-item {
          margin-bottom: 20px;

          &:last-child {
            margin-bottom: 0;
          }

          .status-item__header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;

            .status-item__label {
              display: inline-flex;
              align-items: center;
              gap: 8px;
              font-size: 14px;
              font-weight: 500;
              color: var(--text-regular);

              &.status-item__label--published {
                color: var(--success);
              }

              &.status-item__label--reviewing {
                color: var(--warning);
              }

              &.status-item__label--draft {
                color: var(--info);
              }

              &.status-item__label--garbage {
                color: var(--danger);
              }

              .status-dot {
                width: 8px;
                height: 8px;
                border-radius: 999px;

                &.status-dot--published {
                  background: var(--success);
                }

                &.status-dot--reviewing {
                  background: var(--warning);
                }

                &.status-dot--draft {
                  background: var(--info);
                }

                &.status-dot--garbage {
                  background: var(--danger);
                }
              }
            }

            .status-item__count {
              font-size: 16px;
              font-weight: 600;
              color: var(--text-primary);
            }
          }

          .status-item__bar {
            display: flex;
            align-items: center;
            gap: 12px;

            .progress-bar {
              flex: 1;
              height: 8px;
              border-radius: 999px;
              background: var(--bg-card-muted);
              overflow: hidden;

              .progress-bar__fill {
                height: 100%;
                border-radius: inherit;
                transition: width 0.6s ease;

                &.progress-bar__fill--published {
                  background: var(--success);
                }

                &.progress-bar__fill--reviewing {
                  background: var(--warning);
                }

                &.progress-bar__fill--draft {
                  background: var(--info);
                }

                &.progress-bar__fill--garbage {
                  background: var(--danger);
                }
              }
            }

            .progress-bar__text {
              min-width: 42px;
              text-align: right;
              font-size: 12px;
              font-weight: 600;
              color: var(--text-muted);
            }
          }
        }
      }

      .trend-chart-container {
        height: 340px;

        .trend-chart {
          width: 100%;
          height: 340px;
        }
      }

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
        }
      }
    }
  }
}

html.dark {
  .dashboard-container {
    --bg-page: #0b1220;
    --bg-card: #111b2d;
    --bg-card-muted: #162235;
    --text-primary: #e2e8f0;
    --text-regular: #cbd5e1;
    --text-muted: #8ea0b7;
    --border: #22314a;
    --border-light: #1c2940;
    --shadow-card: 0 14px 32px rgba(2, 6, 23, 0.28);
    --shadow-hover: 0 18px 36px rgba(2, 6, 23, 0.36);
    --page-glow: rgba(59, 130, 246, 0.12);
    --page-overlay-top: rgba(11, 18, 32, 0.64);
    --page-overlay-bottom: rgba(11, 18, 32, 0.96);
    --button-bg: #162235;
    --button-hover-bg: #1b2b44;
    --button-border: #32455f;
    --button-text: #dbeafe;
    --title-icon-bg: rgba(96, 165, 250, 0.14);
    --trend-positive-bg: rgba(16, 185, 129, 0.18);
    --trend-neutral-bg: rgba(148, 163, 184, 0.16);
    --toggle-bg: #162235;
    --toggle-hover-bg: #1b2b44;
    --toggle-active-text: #eff6ff;
    --action-article-bg: rgba(167, 139, 250, 0.16);
    --action-article-text: #c4b5fd;
    --action-comment-bg: rgba(56, 189, 248, 0.16);
    --action-comment-text: #7dd3fc;
    --action-user-bg: rgba(52, 211, 153, 0.16);
    --action-user-text: #6ee7b7;
    --action-photo-bg: rgba(251, 191, 36, 0.16);
    --action-photo-text: #fcd34d;
    --chart-tooltip-bg: rgba(17, 27, 45, 0.98);
    --chart-tooltip-border: #22314a;
    --chart-tooltip-text: #e2e8f0;
    --chart-axis-line: #22314a;
    --chart-axis-text: #8ea0b7;
    --chart-grid-line: #1c2940;
    --chart-line: #60a5fa;
    --chart-point-border: #111b2d;
    --chart-area-start: rgba(96, 165, 250, 0.18);
    --chart-area-end: rgba(96, 165, 250, 0.03);
  }
}

@media (max-width: 1200px) {
  .dashboard-container {
    .statistics-section {
      .stats-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
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

    .statistics-section {
      .stats-grid {
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
