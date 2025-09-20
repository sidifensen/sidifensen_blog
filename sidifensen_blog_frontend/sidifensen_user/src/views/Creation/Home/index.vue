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
  background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
  padding: 20px;
  overflow-y: auto; // 如果内容超出则可滚动

  // 欢迎横幅
  .welcome-banner {
    background: var(--el-bg-color);
    backdrop-filter: blur(10px);
    border-radius: 16px;
    padding: 40px 30px;
    margin-bottom: 30px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);

    .banner-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: 30px;

      .welcome-text {
        flex: 1;

        .welcome-title {
          font-size: 32px;
          font-weight: 700;
          color: var(--el-text-color-primary);
          margin: 0 0 8px 0;
          background: linear-gradient(135deg, #74b9ff, #0984e3);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }

        .welcome-subtitle {
          font-size: 16px;
          color: var(--el-text-color-regular);
          margin: 0;
        }
      }

      .quick-actions {
        display: flex;
        gap: 16px;

        .action-btn {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px 24px;
          border-radius: 12px;
          text-decoration: none;
          font-weight: 600;
          transition: all 0.3s ease;
          // border: 2px solid transparent;

          &.primary {
            background: linear-gradient(135deg, #74b9ff, #0984e3);
            color: white;

            &:hover {
              background: linear-gradient(135deg, #6c5ce7, #0984e3);
              transform: translateY(-2px);
              box-shadow: 0 8px 25px rgba(116, 185, 255, 0.4);
            }
          }

          &.secondary {
            background: var(--el-border-color-light);
            color: var(--el-text-color-primary);
            border-color: var(--el-border-color);

            &:hover {
              background: var(--el-color-primary);
              color: white;
              border-color: var(--el-color-primary);
              transform: translateY(-2px);
              box-shadow: 0 8px 25px rgba(64, 158, 255, 0.4);
            }
          }
        }
      }

      // 响应式：移动端
      @media (max-width: 768px) {
        flex-direction: column;
        text-align: center;

        .welcome-text .welcome-title {
          font-size: 24px;
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
    margin-bottom: 30px;

    h2 {
      font-size: 28px;
      font-weight: 700;
      color: white;
      margin: 0 0 8px 0;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    p {
      font-size: 16px;
      color: rgba(255, 255, 255, 0.8);
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
        background: var(--el-bg-color);
        backdrop-filter: blur(10px);
        border-radius: 16px;
        padding: 24px;
        transition: all 0.3s ease;
        border: 1px solid var(--el-border-color-light);

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
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
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;

            &.article {
              background: linear-gradient(135deg, #74b9ff, #0984e3);
              color: white;
            }

            &.column {
              background: linear-gradient(135deg, #f093fb, #f5576c);
              color: white;
            }

            &.comment {
              background: linear-gradient(135deg, #4facfe, #00f2fe);
              color: white;
            }

            &.read {
              background: linear-gradient(135deg, #43e97b, #38f9d7);
              color: white;
            }

            &.like {
              background: linear-gradient(135deg, #fa709a, #fee140);
              color: white;
            }

            &.fans {
              background: linear-gradient(135deg, #a8edea, #fed6e3);
              color: var(--el-text-color-primary);
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
        background: var(--el-bg-color);
        backdrop-filter: blur(10px);
        border-radius: 16px;
        padding: 24px;
        display: flex;
        align-items: center;
        gap: 16px;

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
        background: var(--el-bg-color);
        backdrop-filter: blur(10px);
        border-radius: 16px;
        padding: 24px;
        cursor: pointer;
        transition: all 0.3s ease;
        border: 1px solid var(--el-border-color-light);

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
        }

        .tool-icon {
          width: 60px;
          height: 60px;
          border-radius: 12px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 24px;
          margin-bottom: 16px;

          &.create {
            background: linear-gradient(135deg, #74b9ff, #0984e3);
            color: white;
          }

          &.manage {
            background: linear-gradient(135deg, #f093fb, #f5576c);
            color: white;
          }

          &.column {
            background: linear-gradient(135deg, #4facfe, #00f2fe);
            color: white;
          }

          &.comment {
            background: linear-gradient(135deg, #43e97b, #38f9d7);
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
        background: var(--el-bg-color);
        backdrop-filter: blur(10px);
        border-radius: 16px;
        padding: 24px;
        border: 1px solid var(--el-border-color-light);

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
            gap: 8px;

            .topic-tag {
              padding: 6px 12px;
              background: var(--el-color-primary-light-9);
              color: var(--el-color-primary);
              border-radius: 16px;
              font-size: 12px;
              font-weight: 500;
              transition: all 0.3s ease;
              cursor: pointer;

              &:hover {
                background: var(--el-color-primary-light-7);
                transform: translateY(-1px);
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
