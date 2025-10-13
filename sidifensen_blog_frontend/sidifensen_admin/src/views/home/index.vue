<template>
  <div class="dashboard-container">
    <!-- 项目链接展示区域 -->
    <ProjectLinks />

    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">管理后台首页</h1>
      <p class="page-description">欢迎使用斯蒂芬森博客管理系统</p>
    </div>

    <!-- 统计数据卡片区域 -->
    <div class="statistics-section">
      <el-row :gutter="20">
        <!-- 用户统计卡片 -->
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <div class="statistics-card">
            <div class="card-icon user-icon">
              <el-icon size="40"><User /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-title">用户总数</div>
              <div class="card-value" v-if="!statisticsLoading">{{ userCount }}</div>
              <el-skeleton v-else :rows="1" animated />
            </div>
          </div>
        </el-col>

        <!-- 文章统计卡片 -->
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <div class="statistics-card">
            <div class="card-icon article-icon">
              <el-icon size="40"><Document /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-title">文章总数</div>
              <div class="card-value" v-if="!statisticsLoading">{{ articleStatistics?.totalCount || 0 }}</div>
              <el-skeleton v-else :rows="1" animated />
            </div>
          </div>
        </el-col>

        <!-- 评论统计卡片 -->
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <div class="statistics-card">
            <div class="card-icon comment-icon">
              <el-icon size="40"><ChatLineRound /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-title">评论总数</div>
              <div class="card-value" v-if="!statisticsLoading">{{ commentCount }}</div>
              <el-skeleton v-else :rows="1" animated />
            </div>
          </div>
        </el-col>

        <!-- 今日访问统计卡片 -->
        <el-col :xs="12" :sm="12" :md="6" :lg="6">
          <div class="statistics-card">
            <div class="card-icon visit-icon">
              <el-icon size="40"><View /></el-icon>
            </div>
            <div class="card-content">
              <div class="card-title">今日访问</div>
              <div class="card-value" v-if="!statisticsLoading">{{ todayVisits }}</div>
              <el-skeleton v-else :rows="1" animated />
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 详细数据展示区域 -->
    <div class="details-section">
      <el-row :gutter="20">
        <!-- 文章状态分布 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-header-title">文章状态分布</span>
                <el-icon class="card-header-icon"><PieChart /></el-icon>
              </div>
            </template>
            <div class="article-status-list" v-if="!statisticsLoading">
              <div class="status-item">
                <div class="status-info">
                  <span class="status-label published">已发布</span>
                  <span class="status-count">{{ articleStatistics?.publishedCount || 0 }}</span>
                </div>
                <div class="status-progress">
                  <el-progress :percentage="getPercentage(articleStatistics?.publishedCount, articleStatistics?.totalCount)" color="#67c23a" :show-text="false" />
                </div>
              </div>
              <div class="status-item">
                <div class="status-info">
                  <span class="status-label reviewing">审核中</span>
                  <span class="status-count">{{ articleStatistics?.reviewingCount || 0 }}</span>
                </div>
                <div class="status-progress">
                  <el-progress :percentage="getPercentage(articleStatistics?.reviewingCount, articleStatistics?.totalCount)" color="#e6a23c" :show-text="false" />
                </div>
              </div>
              <div class="status-item">
                <div class="status-info">
                  <span class="status-label draft">草稿箱</span>
                  <span class="status-count">{{ articleStatistics?.draftCount || 0 }}</span>
                </div>
                <div class="status-progress">
                  <el-progress :percentage="getPercentage(articleStatistics?.draftCount, articleStatistics?.totalCount)" color="#909399" :show-text="false" />
                </div>
              </div>
              <div class="status-item">
                <div class="status-info">
                  <span class="status-label garbage">回收站</span>
                  <span class="status-count">{{ articleStatistics?.garbageCount || 0 }}</span>
                </div>
                <div class="status-progress">
                  <el-progress :percentage="getPercentage(articleStatistics?.garbageCount, articleStatistics?.totalCount)" color="#f56c6c" :show-text="false" />
                </div>
              </div>
            </div>
            <el-skeleton v-else :rows="4" animated />
          </el-card>
        </el-col>

        <!-- 网站访问趋势 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-header-title">网站访问趋势</span>
                <div class="card-header-actions">
                  <el-select v-model="trendDays" @change="handleDaysChange" size="small" style="width: 100px">
                    <el-option label="7天" :value="7" />
                    <el-option label="14天" :value="14" />
                    <el-option label="30天" :value="30" />
                  </el-select>
                  <el-icon class="card-header-icon"><TrendCharts /></el-icon>
                </div>
              </div>
            </template>
            <div class="trend-chart-container">
              <el-skeleton v-if="trendLoading" :rows="6" animated />
              <div v-else ref="chartRef" class="trend-chart"></div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

    <!-- 快速操作区域 -->
    <div class="quick-actions-section">
      <el-card class="detail-card">
        <template #header>
          <div class="card-header">
            <span class="card-header-title">快速操作</span>
            <el-icon class="card-header-icon"><Operation /></el-icon>
          </div>
        </template>
        <div class="quick-actions">
          <div class="action-button-wrapper">
            <el-button class="action-button article-btn" size="large" @click="navigateTo('/article/examine')">
              <div class="button-content">
                <div class="button-icon">
                  <el-icon><Document /></el-icon>
                </div>
                <div class="button-text">
                  <span class="button-title">文章审核</span>
                  <span class="button-subtitle">审核待发布文章</span>
                </div>
              </div>
            </el-button>
          </div>

          <div class="action-button-wrapper">
            <el-button class="action-button comment-btn" size="large" @click="navigateTo('/comment/examine')">
              <div class="button-content">
                <div class="button-icon">
                  <el-icon><ChatLineRound /></el-icon>
                </div>
                <div class="button-text">
                  <span class="button-title">评论审核</span>
                  <span class="button-subtitle">管理用户评论</span>
                </div>
              </div>
            </el-button>
          </div>

          <div class="action-button-wrapper">
            <el-button class="action-button user-btn" size="large" @click="navigateTo('/system/user')">
              <div class="button-content">
                <div class="button-icon">
                  <el-icon><User /></el-icon>
                </div>
                <div class="button-text">
                  <span class="button-title">用户管理</span>
                  <span class="button-subtitle">管理系统用户</span>
                </div>
              </div>
            </el-button>
          </div>

          <div class="action-button-wrapper">
            <el-button class="action-button photo-btn" size="large" @click="navigateTo('/photo/examine')">
              <div class="button-content">
                <div class="button-icon">
                  <el-icon><Picture /></el-icon>
                </div>
                <div class="button-text">
                  <span class="button-title">图片审核</span>
                  <span class="button-subtitle">审核上传图片</span>
                </div>
              </div>
            </el-button>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, computed, nextTick } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { User, Document, ChatLineRound, View, PieChart, Monitor, Operation, Picture, TrendCharts } from "@element-plus/icons-vue";
import { getUserTotalCount } from "@/api/user";
import { getArticleStatistics } from "@/api/article";
import { getCommentStatistics } from "@/api/comment";
import { getTodayVisitorCount, getVisitorTrend } from "@/api/visitorLog";
import * as echarts from "echarts";
import ProjectLinks from "@/components/ProjectLinks.vue";

// 路由和状态管理
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const statisticsLoading = ref(true); // 统计数据加载状态
const userCount = ref(0); // 用户总数
const articleStatistics = ref(null); // 文章统计数据
const commentCount = ref(0); // 评论总数
const todayVisits = ref(0); // 今日访问量
const trendDays = ref(7); // 访客趋势天数，默认7天
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
    const [userResult, articleResult, commentResult, visitorResult] = await Promise.allSettled([getUserTotalCount(), getArticleStatistics(), getCommentStatistics(), getTodayVisitorCount()]);

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

    // 处理评论统计
    if (commentResult.status === "fulfilled") {
      commentCount.value = commentResult.value.data.data || 0;
    } else {
      console.error("获取评论统计失败:", commentResult.reason);
    }

    // 处理今日访问量
    if (visitorResult.status === "fulfilled") {
      todayVisits.value = visitorResult.value.data.data || 0;
    } else {
      console.error("获取今日访问量失败:", visitorResult.reason);
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

  // 销毁旧实例，重新初始化（解决切换天数后白屏问题）
  if (chartInstance.value) {
    chartInstance.value.dispose();
    chartInstance.value = null;
  }

  // 初始化图表实例
  chartInstance.value = echarts.init(chartRef.value);

  // 准备数据
  const dates = visitorTrend.value.map((item) => item.date);
  const counts = visitorTrend.value.map((item) => item.count);

  // 配置图表选项
  const option = {
    tooltip: {
      trigger: "axis",
      axisPointer: {
        type: "cross",
        label: {
          backgroundColor: "#6a7985",
        },
      },
      formatter: "{b}<br/>访问量: {c}",
    },
    grid: {
      left: "3%",
      right: "4%",
      bottom: "3%",
      top: "10%",
      containLabel: true,
    },
    xAxis: {
      type: "category",
      boundaryGap: false,
      data: dates,
      axisLabel: {
        formatter: (value) => {
          // 格式化日期显示（只显示月-日）
          const date = new Date(value);
          return `${date.getMonth() + 1}-${date.getDate()}`;
        },
      },
    },
    yAxis: {
      type: "value",
      name: "访问量",
      minInterval: 1,
    },
    series: [
      {
        name: "访问量",
        type: "line",
        smooth: true,
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
                color: "rgba(64, 158, 255, 0.3)",
              },
              {
                offset: 1,
                color: "rgba(64, 158, 255, 0.05)",
              },
            ],
          },
        },
        lineStyle: {
          color: "#409eff",
          width: 2,
        },
        itemStyle: {
          color: "#409eff",
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
// 仪表盘容器
.dashboard-container {
  padding: 20px;
  background-color: var(--el-bg-color-page);
  min-height: calc(100vh - 88px);

  // 页面标题区域
  .page-header {
    margin-bottom: 30px;
    text-align: center;

    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: var(--el-text-color-primary);
      margin: 0 0 8px 0;
    }

    .page-description {
      font-size: 16px;
      color: var(--el-text-color-regular);
      margin: 0;
    }
  }

  // 统计数据卡片区域
  .statistics-section {
    margin-bottom: 30px;

    .statistics-card {
      display: flex;
      align-items: center;
      padding: 24px;
      background: var(--el-bg-color);
      border-radius: 12px;
      box-shadow: 0 2px 12px var(--el-border-color-light);
      transition: all 0.3s ease;
      height: 120px;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px var(--el-border-color);
      }

      // 卡片图标区域
      .card-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 80px;
        height: 80px;
        border-radius: 50%;
        margin-right: 20px;

        &.user-icon {
          background: linear-gradient(135deg, #5b86e5 0%, #36d1dc 100%);
          color: white;
        }

        &.article-icon {
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          color: white;
        }

        &.comment-icon {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          color: white;
        }

        &.visit-icon {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          color: white;
        }
      }

      // 卡片内容区域
      .card-content {
        flex: 1;

        .card-title {
          font-size: 16px;
          color: var(--el-text-color-regular);
          margin-bottom: 8px;
        }

        .card-value {
          font-size: 32px;
          font-weight: 700;
          color: var(--el-text-color-primary);
          line-height: 1;
        }
      }
    }
  }

  // 详细数据展示区域
  .details-section {
    margin-bottom: 30px;

    .detail-card {
      height: 100%;
      box-shadow: 0 2px 12px var(--el-border-color-light);
      border-radius: 12px;

      .card-header {
        display: flex;
        align-items: center;
        justify-content: space-between;

        .card-header-title {
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }

        .card-header-icon {
          color: var(--el-color-primary);
        }

        .card-header-actions {
          display: flex;
          align-items: center;
          gap: 12px;
        }
      }

      // 文章状态列表
      .article-status-list {
        .status-item {
          margin-bottom: 20px;

          &:last-child {
            margin-bottom: 0;
          }

          .status-info {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .status-label {
              font-size: 14px;
              font-weight: 500;

              &.published {
                color: #67c23a;
              }

              &.reviewing {
                color: #e6a23c;
              }

              &.draft {
                color: #909399;
              }

              &.garbage {
                color: #f56c6c;
              }
            }

            .status-count {
              font-size: 16px;
              font-weight: 600;
              color: var(--el-text-color-primary);
            }
          }

          .status-progress {
            margin-bottom: 4px;
          }
        }
      }

      // 访客趋势图表
      .trend-chart-container {
        min-height: 300px;

        .trend-chart {
          width: 100%;
          height: 300px;
        }
      }
    }
  }

  // 快速操作区域
  .quick-actions-section {
    .quick-actions {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 20px;

      // 按钮包装器
      .action-button-wrapper {
        position: relative;
        overflow: hidden;
        border-radius: 16px;
        transition: all 0.3s ease;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 12px 32px rgba(0, 0, 0, 0.15);
        }
      }

      // 自定义按钮样式
      .action-button {
        width: 100%;
        height: 80px;
        border: none;
        border-radius: 16px;
        padding: 0;
        position: relative;
        overflow: hidden;
        transition: all 0.3s ease;
        cursor: pointer;

        // 按钮内容布局
        .button-content {
          display: flex;
          align-items: center;
          padding: 20px;
          height: 100%;
          position: relative;
          z-index: 2;

          // 按钮图标
          .button-icon {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 48px;
            height: 48px;
            border-radius: 12px;
            background: rgba(255, 255, 255, 0.2);
            margin-right: 16px;
            backdrop-filter: blur(10px);
            transition: all 0.3s ease;

            .el-icon {
              font-size: 24px;
              color: white;
            }
          }

          // 按钮文字
          .button-text {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            flex: 1;

            .button-title {
              font-size: 16px;
              font-weight: 600;
              color: white;
              margin-bottom: 4px;
              line-height: 1.2;
            }

            .button-subtitle {
              font-size: 12px;
              color: rgba(255, 255, 255, 0.8);
              line-height: 1.2;
            }
          }
        }

        // 背景装饰效果
        &::before {
          content: "";
          position: absolute;
          top: 0;
          right: 0;
          width: 100px;
          height: 100px;
          border-radius: 50%;
          opacity: 0.1;
          transition: all 0.3s ease;
          transform: translate(30px, -30px);
        }

        &:hover {
          &::before {
            transform: translate(20px, -20px);
            opacity: 0.2;
          }

          .button-content {
            .button-icon {
              transform: scale(1.1);
              background: rgba(255, 255, 255, 0.3);
            }
          }
        }

        // 文章审核按钮样式
        &.article-btn {
          background: linear-gradient(135deg, #8e2de2 0%, #4a00e0 100%);

          &::before {
            background: radial-gradient(circle, #a78bfa 0%, transparent 70%);
          }
        }

        // 评论审核按钮样式
        &.comment-btn {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);

          &::before {
            background: radial-gradient(circle, #7dd3fc 0%, transparent 70%);
          }
        }

        // 用户管理按钮样式
        &.user-btn {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);

          &::before {
            background: radial-gradient(circle, #6ee7b7 0%, transparent 70%);
          }
        }

        // 图片审核按钮样式
        &.photo-btn {
          background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);

          &::before {
            background: radial-gradient(circle, #fbbf24 0%, transparent 70%);
          }
        }

        // 活跃状态
        &:active {
          transform: scale(0.98);
        }

        // 禁用Element Plus默认样式
        &.el-button {
          &:hover,
          &:focus {
            color: white;
            border-color: transparent;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .dashboard-container {
    padding: 10px;

    .page-header {
      margin-bottom: 20px;

      .page-title {
        font-size: 24px;
      }

      .page-description {
        font-size: 14px;
      }
    }

    .statistics-section {
      margin-bottom: 20px;

      // 调整手机端卡片间距
      :deep(.el-row) {
        margin-left: -8px !important;
        margin-right: -8px !important;

        .el-col {
          padding-left: 8px !important;
          padding-right: 8px !important;
          margin-bottom: 16px;
        }
      }

      .statistics-card {
        flex-direction: column;
        text-align: center;
        height: auto;
        padding: 16px 12px;

        .card-icon {
          margin: 0 0 12px 0;
          width: 50px;
          height: 50px;

          .el-icon {
            font-size: 26px;
          }
        }

        .card-content {
          .card-title {
            font-size: 13px;
            margin-bottom: 6px;
          }

          .card-value {
            font-size: 22px;
          }
        }
      }
    }

    // 详细数据展示区域
    .details-section {
      margin-bottom: 20px;

      :deep(.el-row) {
        .el-col {
          &:first-child {
            margin-bottom: 20px;
          }
        }
      }
    }

    .quick-actions-section {
      .quick-actions {
        grid-template-columns: 1fr;
        gap: 16px;

        .action-button-wrapper {
          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
          }
        }

        .action-button {
          height: 70px;

          .button-content {
            padding: 16px;

            .button-icon {
              width: 40px;
              height: 40px;
              margin-right: 12px;

              .el-icon {
                font-size: 20px;
              }
            }

            .button-text {
              .button-title {
                font-size: 14px;
              }

              .button-subtitle {
                font-size: 11px;
              }
            }
          }
        }
      }
    }
  }
}
</style>
