<template>
  <div class="creation-home">
    <!-- 欢迎横幅 -->
    <div class="welcome-banner">
      <div class="banner-content">
        <div class="welcome-text">
          <h1 class="welcome-title">欢迎回到创作中心</h1>
          <p class="welcome-subtitle">分享您的想法，记录美好时光</p>
        </div>
        <div class="quick-actions">
          <a href="/editor" target="_blank" class="action-btn primary">
            <el-icon><Edit /></el-icon>
            <span>开始创作</span>
          </a>
          <router-link to="/creation/articlemanage" class="action-btn secondary">
            <el-icon><Management /></el-icon>
            <span>管理文章</span>
          </router-link>
        </div>
      </div>
    </div>

    <!-- 数据统计区域 -->
    <div class="statistics-section">
      <div class="section-title">
        <h2>数据概览</h2>
        <p>了解您的创作表现</p>
      </div>

      <div class="statistics-grid" v-if="!statisticsLoading">
        <!-- 文章统计 -->
        <div class="stat-card article-stats">
          <div class="stat-header">
            <div class="stat-icon article">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <h3>文章</h3>
              <p class="stat-total">{{ statistics?.articleStatistics?.totalCount || 0 }}</p>
            </div>
          </div>
          <div class="stat-details">
            <div class="detail-item">
              <span class="label">已发布</span>
              <span class="value published">{{ statistics?.articleStatistics?.publishedCount || 0 }}</span>
            </div>
            <div class="detail-item">
              <span class="label">草稿</span>
              <span class="value draft">{{ statistics?.articleStatistics?.draftCount || 0 }}</span>
            </div>
            <div class="detail-item">
              <span class="label">审核中</span>
              <span class="value reviewing">{{ statistics?.articleStatistics?.reviewingCount || 0 }}</span>
            </div>
          </div>
        </div>

        <!-- 专栏统计 -->
        <div class="stat-card">
          <div class="stat-header">
            <div class="stat-icon column">
              <el-icon><FolderOpened /></el-icon>
            </div>
            <div class="stat-info">
              <h3>专栏</h3>
              <p class="stat-total">{{ statistics?.columnCount || 0 }}</p>
            </div>
          </div>
          <div class="stat-action">
            <router-link to="/creation/columnmanage" class="manage-link">
              <span>管理专栏</span>
              <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
        </div>

        <!-- 评论统计 -->
        <div class="stat-card">
          <div class="stat-header">
            <div class="stat-icon comment">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stat-info">
              <h3>评论</h3>
              <p class="stat-total">{{ statistics?.commentCount || 0 }}</p>
            </div>
          </div>
          <div class="stat-action">
            <router-link to="/creation/commentmanage" class="manage-link">
              <span>管理评论</span>
              <el-icon><ArrowRight /></el-icon>
            </router-link>
          </div>
        </div>

        <!-- 阅读量统计 -->
        <div class="stat-card">
          <div class="stat-header">
            <div class="stat-icon read">
              <el-icon><View /></el-icon>
            </div>
            <div class="stat-info">
              <h3>总阅读量</h3>
              <p class="stat-total">{{ formatNumber(statistics?.totalReadCount || 0) }}</p>
            </div>
          </div>
          <div class="stat-trend">
            <span class="trend-text">持续增长中</span>
          </div>
        </div>

        <!-- 点赞统计 -->
        <div class="stat-card">
          <div class="stat-header">
            <div class="stat-icon like">
              <svg-icon name="like" width="18px" height="18px" color="#fff" />
            </div>
            <div class="stat-info">
              <h3>获赞数</h3>
              <p class="stat-total">{{ statistics?.totalLikeCount || 0 }}</p>
            </div>
          </div>
          <div class="stat-trend">
            <span class="trend-text">感谢支持</span>
          </div>
        </div>

        <!-- 粉丝统计 -->
        <div class="stat-card">
          <div class="stat-header">
            <div class="stat-icon fans">
              <el-icon color="#fff"><User /></el-icon>
            </div>
            <div class="stat-info">
              <h3>粉丝数</h3>
              <p class="stat-total">{{ statistics?.fansCount || 0 }}</p>
            </div>
          </div>
          <div class="stat-trend">
            <span class="trend-text">影响力提升</span>
          </div>
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-else class="statistics-loading">
        <el-skeleton animated :count="6">
          <template #template>
            <div class="skeleton-card">
              <el-skeleton-item variant="circle" style="width: 60px; height: 60px" />
              <div class="skeleton-content">
                <el-skeleton-item variant="h3" style="width: 80px; margin: 8px 0" />
                <el-skeleton-item variant="text" style="width: 120px" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>

    <!-- 快捷操作区域 -->
    <div class="quick-tools-section">
      <div class="section-title">
        <h2>快捷工具</h2>
        <p>高效管理您的内容</p>
      </div>

      <div class="tools-grid">
        <div class="tool-card" @click="goToEditor">
          <div class="tool-icon create">
            <el-icon><EditPen /></el-icon>
          </div>
          <div class="tool-info">
            <h3>写文章</h3>
            <p>记录您的想法和见解</p>
          </div>
        </div>

        <div class="tool-card" @click="goToArticleManage">
          <div class="tool-icon manage">
            <el-icon><Files /></el-icon>
          </div>
          <div class="tool-info">
            <h3>文章管理</h3>
            <p>管理已发布的文章</p>
          </div>
        </div>

        <div class="tool-card" @click="goToColumnManage">
          <div class="tool-icon column">
            <el-icon><Collection /></el-icon>
          </div>
          <div class="tool-info">
            <h3>专栏管理</h3>
            <p>组织和管理专栏内容</p>
          </div>
        </div>

        <div class="tool-card" @click="goToCommentManage">
          <div class="tool-icon comment">
            <el-icon><Message /></el-icon>
          </div>
          <div class="tool-info">
            <h3>评论管理</h3>
            <p>回复和管理评论互动</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 创作助手区域 -->
    <div class="creation-assistant">
      <div class="section-title">
        <h2>创作助手</h2>
        <p>提升您的创作效率</p>
      </div>

      <div class="assistant-cards">
        <div class="assistant-card tips">
          <div class="card-header">
            <h3>写作建议</h3>
          </div>
          <div class="card-content">
            <ul class="tips-list">
              <li>保持文章结构清晰，使用标题和段落分隔</li>
              <li>添加合适的配图能让文章更生动</li>
              <li>定期更新内容，保持读者的关注度</li>
              <li>回应读者评论，建立良好的互动关系</li>
            </ul>
          </div>
        </div>

        <div class="assistant-card trends">
          <div class="card-header">
            <h3>热门话题</h3>
          </div>
          <div class="card-content">
            <div class="topic-tags">
              <span class="topic-tag">前端开发</span>
              <span class="topic-tag">人工智能</span>
              <span class="topic-tag">生活随笔</span>
              <span class="topic-tag">技术分享</span>
              <span class="topic-tag">读书笔记</span>
              <span class="topic-tag">职场感悟</span>
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
import { Edit, Management, Document, FolderOpened, ChatDotRound, View, Star, User, ArrowRight, EditPen, Files, Collection, Message } from "@element-plus/icons-vue";
import { getCreationStatistics } from "@/api/article";

// 路由
const router = useRouter();

// 响应式数据
const statisticsLoading = ref(false);
const statistics = ref(null);

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + "万";
  }
  return num.toString();
};

// 获取统计数据
const fetchStatistics = async () => {
  try {
    statisticsLoading.value = true;
    const res = await getCreationStatistics();
    statistics.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取统计数据失败");
    console.error("获取统计数据失败:", error);
  } finally {
    statisticsLoading.value = false;
  }
};

// 快捷操作方法
const goToEditor = () => {
  window.open("/editor", "_blank");
};

const goToArticleManage = () => {
  router.push("/creation/articlemanage");
};

const goToColumnManage = () => {
  router.push("/creation/columnmanage");
};

const goToCommentManage = () => {
  router.push("/creation/commentmanage");
};

// 组件挂载
onMounted(() => {
  fetchStatistics();
});
</script>

<style lang="scss" scoped>
// 创作中心首页样式
.creation-home {
  background: #f5f7fa;
  padding: 24px;
  overflow-y: auto;
  min-height: 100%;

  // 滚动条样式
  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: var(--el-border-color);
    border-radius: 4px;

    &:hover {
      background: var(--el-border-color-dark);
    }
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  // 欢迎横幅
  .welcome-banner {
    background: #ffffff;
    border-radius: 16px;
    padding: 48px 40px;
    margin-bottom: 32px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    border: 1px solid var(--el-border-color-lighter);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
    }

    .banner-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 40px;

      .welcome-text {
        flex: 1;

        .welcome-title {
          font-size: 36px;
          font-weight: 700;
          color: #303133;
          margin: 0 0 12px 0;
          letter-spacing: -0.5px;
          line-height: 1.2;
        }

        .welcome-subtitle {
          font-size: 18px;
          color: #606266;
          margin: 0;
          font-weight: 400;
        }
      }

      .quick-actions {
        display: flex;
        gap: 12px;

        .action-btn {
          display: flex;
          align-items: center;
          gap: 10px;
          padding: 14px 28px;
          border-radius: 10px;
          text-decoration: none;
          font-weight: 600;
          font-size: 15px;
          transition: all 0.3s ease;

          .el-icon {
            font-size: 18px;
          }

          &.primary {
            background: #409eff;
            color: white;
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

            &:hover {
              background: #66b1ff;
              transform: translateY(-2px);
              box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
            }

            &:active {
              background: #3a8ee6;
              transform: translateY(0);
            }
          }

          &.secondary {
            background: #f4f4f5;
            color: #606266;
            border: 1px solid #dcdfe6;

            &:hover {
              background: #ecf5ff;
              color: #409eff;
              border-color: #c6e2ff;
              transform: translateY(-2px);
            }

            &:active {
              transform: translateY(0);
            }
          }
        }
      }

      // 响应式：移动端
      @media (max-width: 768px) {
        flex-direction: column;
        text-align: center;
        gap: 24px;

        .welcome-text .welcome-title {
          font-size: 28px;
        }

        .welcome-text .welcome-subtitle {
          font-size: 16px;
        }

        .quick-actions {
          justify-content: center;
          flex-wrap: wrap;
        }
      }
    }
  }

  // 通用节标题
  .section-title {
    text-align: center;
    margin-bottom: 32px;

    h2 {
      font-size: 28px;
      font-weight: 600;
      color: #303133;
      margin: 0 0 8px 0;
    }

    p {
      font-size: 16px;
      color: #909399;
      margin: 0;
    }
  }

  // 数据统计区域
  .statistics-section {
    margin-bottom: 40px;

    .statistics-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 20px;
      max-width: 1200px;
      margin: 0 auto;

      // 统计卡片
      .stat-card {
        background: #ffffff;
        border-radius: 12px;
        padding: 24px;
        transition: all 0.3s ease;
        border: 1px solid var(--el-border-color-lighter);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        }

        // 文章统计卡片特殊样式
        &.article-stats {
          .stat-details {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 12px;
            margin-top: 16px;
            padding-top: 16px;
            border-top: 1px solid var(--el-border-color-light);

            .detail-item {
              text-align: center;

              .label {
                display: block;
                font-size: 12px;
                color: var(--el-text-color-secondary);
                margin-bottom: 4px;
              }

              .value {
                display: block;
                font-size: 18px;
                font-weight: 600;

                &.published {
                  color: var(--el-color-success);
                }

                &.draft {
                  color: var(--el-color-warning);
                }

                &.reviewing {
                  color: var(--el-color-info);
                }
              }
            }
          }
        }

        .stat-header {
          display: flex;
          align-items: center;
          gap: 16px;

          .stat-icon {
            width: 60px;
            height: 60px;
            border-radius: 10px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;

            &.article {
              background: #409eff;
              color: white;
            }

            &.column {
              background: #e6a23c;
              color: white;
            }

            &.comment {
              background: #67c23a;
              color: white;
            }

            &.read {
              background: #909399;
              color: white;
            }

            &.like {
              background: #f56c6c;
              color: white;
            }

            &.fans {
              background: #606266;
              color: white;
            }
          }

          .stat-info {
            flex: 1;

            h3 {
              font-size: 16px;
              color: var(--el-text-color-regular);
              margin: 0 0 4px 0;
              font-weight: 500;
            }

            .stat-total {
              font-size: 32px;
              font-weight: 700;
              color: var(--el-text-color-primary);
              margin: 0;
            }
          }
        }

        .stat-action {
          margin-top: 16px;
          padding-top: 16px;
          border-top: 1px solid var(--el-border-color-light);

          .manage-link {
            display: flex;
            align-items: center;
            justify-content: space-between;
            color: var(--el-color-primary);
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;

            &:hover {
              color: var(--el-color-primary-dark-2);
            }
          }
        }

        .stat-trend {
          margin-top: 16px;
          padding-top: 16px;
          border-top: 1px solid var(--el-border-color-light);

          .trend-text {
            font-size: 14px;
            color: var(--el-text-color-secondary);
          }
        }
      }
    }

    // 统计加载状态
    .statistics-loading {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
      gap: 20px;
      max-width: 1200px;
      margin: 0 auto;

      .skeleton-card {
        background: #ffffff;
        border-radius: 12px;
        padding: 24px;
        display: flex;
        align-items: center;
        gap: 16px;
        border: 1px solid var(--el-border-color-lighter);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

        .skeleton-content {
          flex: 1;
        }
      }
    }
  }

  // 快捷工具区域
  .quick-tools-section {
    margin-bottom: 40px;

    .tools-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 20px;
      max-width: 1200px;
      margin: 0 auto;

      .tool-card {
        background: #ffffff;
        border-radius: 12px;
        padding: 24px;
        cursor: pointer;
        transition: all 0.3s ease;
        border: 1px solid var(--el-border-color-lighter);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        }

        .tool-icon {
          width: 60px;
          height: 60px;
          border-radius: 10px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          margin-bottom: 16px;

          &.create {
            background: #409eff;
            color: white;
          }

          &.manage {
            background: #e6a23c;
            color: white;
          }

          &.column {
            background: #67c23a;
            color: white;
          }

          &.comment {
            background: #909399;
            color: white;
          }
        }

        .tool-info {
          h3 {
            font-size: 18px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            margin: 0 0 8px 0;
          }

          p {
            font-size: 14px;
            color: var(--el-text-color-regular);
            margin: 0;
            line-height: 1.5;
          }
        }
      }
    }
  }

  // 创作助手区域
  .creation-assistant {
    max-width: 1000px;
    margin: 0 auto;

    .assistant-cards {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
      gap: 20px;

      .assistant-card {
        background: #ffffff;
        border-radius: 12px;
        padding: 24px;
        border: 1px solid var(--el-border-color-lighter);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

        .card-header {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 20px;

          .el-icon {
            font-size: 24px;
            color: var(--el-color-primary);
          }

          h3 {
            font-size: 18px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            margin: 0;
          }
        }

        .card-content {
          .tips-list {
            list-style: none;
            padding: 0;
            margin: 0;

            li {
              position: relative;
              padding: 8px 0 8px 20px;
              color: var(--el-text-color-regular);
              line-height: 1.6;

              &::before {
                content: "•";
                position: absolute;
                left: 0;
                color: var(--el-color-primary);
                font-weight: bold;
              }
            }
          }

          .topic-tags {
            display: flex;
            flex-wrap: wrap;
            gap: 10px;

            .topic-tag {
              padding: 8px 16px;
              background: #f4f4f5;
              color: #606266;
              border-radius: 20px;
              font-size: 13px;
              font-weight: 500;
              transition: all 0.3s ease;
              cursor: pointer;
              border: 1px solid #e4e7ed;

              &:hover {
                background: #409eff;
                color: white;
                border-color: #409eff;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(64, 158, 255, 0.3);
              }
            }
          }
        }
      }

      // 响应式：小屏幕单列布局
      @media (max-width: 768px) {
        grid-template-columns: 1fr;

        .assistant-card {
          min-width: auto;
        }
      }
    }
  }

  // 响应式：移动端整体优化
  @media (max-width: 768px) {
    padding: 15px;

    .welcome-banner {
      padding: 24px 20px;
    }

    .section-title h2 {
      font-size: 24px;
    }

    .statistics-grid,
    .tools-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style>
