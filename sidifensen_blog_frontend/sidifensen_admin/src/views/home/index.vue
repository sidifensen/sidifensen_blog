<template>
  <div class="dashboard-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1 class="page-title">管理后台首页</h1>
      <p class="page-description">欢迎使用斯蒂芬森博客管理系统</p>
    </div>

    <!-- 统计数据卡片区域 -->
    <div class="statistics-section">
      <el-row :gutter="20">
        <!-- 用户统计卡片 -->
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
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
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
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
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
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
        <el-col :xs="24" :sm="12" :md="6" :lg="6">
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

        <!-- 系统信息 -->
        <el-col :xs="24" :sm="24" :md="12" :lg="12">
          <el-card class="detail-card">
            <template #header>
              <div class="card-header">
                <span class="card-header-title">系统信息</span>
                <el-icon class="card-header-icon"><Monitor /></el-icon>
              </div>
            </template>
            <div class="system-info">
              <div class="info-item">
                <span class="info-label">系统版本</span>
                <span class="info-value">v1.0.0</span>
              </div>
              <div class="info-item">
                <span class="info-label">运行时间</span>
                <span class="info-value">{{ runTime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">当前用户</span>
                <span class="info-value">{{ currentUser }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">登录时间</span>
                <span class="info-value">{{ loginTime }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">系统状态</span>
                <el-tag type="success" size="small">正常运行</el-tag>
              </div>
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
          <el-button type="primary" size="large" @click="navigateTo('/article/examine')">
            <el-icon><Document /></el-icon>
            文章审核
          </el-button>
          <el-button type="success" size="large" @click="navigateTo('/comment/examine')">
            <el-icon><ChatLineRound /></el-icon>
            评论审核
          </el-button>
          <el-button type="warning" size="large" @click="navigateTo('/user/manage')">
            <el-icon><User /></el-icon>
            用户管理
          </el-button>
          <el-button type="info" size="large" @click="navigateTo('/photo/examine')">
            <el-icon><Picture /></el-icon>
            图片审核
          </el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { User, Document, ChatLineRound, View, PieChart, Monitor, Operation, Picture } from "@element-plus/icons-vue";
import { getUserTotalCount } from "@/api/user";
import { getArticleStatistics } from "@/api/article";
import { getCommentStatistics } from "@/api/comment";

// 路由和状态管理
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const statisticsLoading = ref(true); // 统计数据加载状态
const userCount = ref(0); // 用户总数
const articleStatistics = ref(null); // 文章统计数据
const commentCount = ref(0); // 评论总数
const todayVisits = ref(0); // 今日访问量
const runTime = ref(""); // 系统运行时间

// 计算属性
const currentUser = computed(() => {
  return userStore.user?.nickname || "管理员";
});

const loginTime = computed(() => {
  const now = new Date();
  return now.toLocaleString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
    hour: "2-digit",
    minute: "2-digit",
  });
});

// 方法定义
// 获取所有统计数据
const fetchAllStatistics = async () => {
  try {
    statisticsLoading.value = true;

    // 并行获取所有统计数据
    const [userResult, articleResult, commentResult] = await Promise.allSettled([getUserTotalCount(), getArticleStatistics(), getCommentStatistics()]);

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

    // 模拟今日访问数据（实际项目中应该从后端获取）
    todayVisits.value = Math.floor(Math.random() * 1000) + 100;
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

// 计算系统运行时间
const calculateRunTime = () => {
  // 假设系统启动时间（实际项目中应该从后端获取）
  const startTime = new Date("2025-08-04");
  const now = new Date();
  const diff = now - startTime;
  const days = Math.floor(diff / (1000 * 60 * 60 * 24));
  const hours = Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
  runTime.value = `${days}天${hours}小时`;
};

// 导航到指定页面
const navigateTo = (path) => {
  router.push(path);
};

// 生命周期
onMounted(() => {
  fetchAllStatistics();
  calculateRunTime();
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
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

      // 系统信息
      .system-info {
        .info-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 12px 0;
          border-bottom: 1px solid var(--el-border-color-lighter);

          &:last-child {
            border-bottom: none;
          }

          .info-label {
            font-size: 14px;
            color: var(--el-text-color-regular);
          }

          .info-value {
            font-size: 14px;
            font-weight: 500;
            color: var(--el-text-color-primary);
          }
        }
      }
    }
  }

  // 快速操作区域
  .quick-actions-section {
    .quick-actions {
      display: flex;
      gap: 16px;
      flex-wrap: wrap;

      .el-button {
        flex: 1;
        min-width: 140px;
        height: 50px;

        .el-icon {
          margin-right: 8px;
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
      .statistics-card {
        flex-direction: column;
        text-align: center;
        height: auto;
        padding: 20px;

        .card-icon {
          margin: 0 0 16px 0;
          width: 60px;
          height: 60px;

          .el-icon {
            font-size: 30px;
          }
        }

        .card-content {
          .card-title {
            font-size: 14px;
            margin-bottom: 4px;
          }

          .card-value {
            font-size: 24px;
          }
        }
      }
    }

    .quick-actions-section {
      .quick-actions {
        .el-button {
          flex: 1 1 calc(50% - 8px);
          min-width: auto;
        }
      }
    }
  }
}
</style>
