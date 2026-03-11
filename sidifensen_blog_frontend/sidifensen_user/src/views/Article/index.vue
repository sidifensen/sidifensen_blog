<template>
  <div class="article-page">
    <!-- 内容区域 -->
    <div class="content-section">
      <div class="container">
        <!-- 页面头部 -->
        <div class="page-hero">
          <div class="hero-copy">
            <span class="hero-kicker">Article Square</span>
            <h1>文章广场</h1>
            <p>按发布时间浏览社区内容，持续发现值得读的技术文章。</p>
          </div>

          <div class="hero-metrics">
            <div class="metric-card">
              <span class="metric-label">已加载</span>
              <span class="metric-value">{{ articleList.length }}</span>
            </div>
            <div class="metric-card">
              <span class="metric-label">全部文章</span>
              <span class="metric-value">{{ total || "—" }}</span>
            </div>
          </div>
        </div>

        <div class="content-layout">
          <!-- 左侧主要内容 -->
          <div class="main-content">
            <!-- 文章列表 -->
            <div class="article-list-wrapper">
              <div class="article-list-section">
                <div class="section-head">
                  <div class="section-title-group">
                    <h2>最新发布</h2>
                    <p>优先展示社区最近更新的文章内容</p>
                  </div>
                  <span class="section-meta">共 {{ total }} 篇</span>
                </div>

                <div v-if="articleLoading" class="loading-container">
                  <el-skeleton animated :count="8">
                    <template #template>
                      <div class="article-skeleton">
                        <el-skeleton-item variant="image" style="width: 160px; height: 100px" />
                        <div class="skeleton-content">
                          <el-skeleton-item variant="h3" style="width: 70%" />
                          <el-skeleton-item variant="text" style="width: 100%" />
                          <el-skeleton-item variant="text" style="width: 60%" />
                        </div>
                      </div>
                    </template>
                  </el-skeleton>
                </div>

                <div v-else-if="articleList.length === 0" class="empty-state">
                  <el-empty description="暂无文章" />
                </div>

                <div v-else class="article-list">
                  <div v-for="article in articleList" :key="article.id" class="article-item" @click="goToArticle(article.id, article.userId)">
                    <!-- 文章封面 -->
                    <el-image :src="article.coverUrl" class="article-cover">
                      <template #placeholder>
                        <div class="loading-text">加载中...</div>
                      </template>
                      <template #error>
                        <div class="error">
                          <el-icon>
                            <Picture />
                          </el-icon>
                        </div>
                      </template>
                    </el-image>

                    <!-- 文章内容 -->
                    <div class="article-content">
                      <!-- 作者信息 -->
                      <div class="article-author" @click.stop="goToUserPage(article.userId)">
                        <el-avatar :size="24" :src="article.avatar" class="author-avatar" />
                        <span class="author-name">{{ article.nickname }}</span>
                      </div>

                      <h3 class="article-title">{{ article.title }}</h3>
                      <p class="article-description">{{ article.description }}</p>

                      <!-- 文章元信息 -->
                      <div class="article-meta">
                        <span class="article-date">{{ formatDate(article.createTime) }}</span>
                        <span class="article-readCount">
                          <el-icon> <View /> </el-icon>
                          {{ article.readCount }} 阅读</span
                        >
                        <span class="article-likes">
                          <svg-icon name="like" width="13px" height="13px" color="currentColor" />
                          {{ article.likeCount || 0 }} 点赞</span
                        >
                        <span class="article-collections">
                          <el-icon>
                            <Star />
                          </el-icon>
                          {{ article.collectCount || 0 }} 收藏</span
                        >
                      </div>
                    </div>
                  </div>

                  <!-- 加载更多指示器 -->
                  <div v-if="loadingMore" class="loading-more">
                    <div class="loading-spinner"></div>
                    <span>加载更多...</span>
                  </div>
                </div>

                <!-- 返回顶部按钮 -->
                <div v-show="showBackToTop" class="back-to-top" @click="scrollToTop">
                  <el-icon><ArrowUp /></el-icon>
                </div>
              </div>
            </div>
          </div>

          <!-- 右侧边栏 -->
          <div class="sidebar">
            <!-- 热门文章 -->
            <div class="sidebar-card">
              <h4 class="card-title">
                <el-icon><View /></el-icon>
                热门文章
              </h4>
              <p class="card-description">按阅读热度排序，适合快速补看社区焦点内容。</p>

              <!-- 加载中骨架屏 -->
              <div v-if="hotArticleLoading" class="hot-articles-loading">
                <el-skeleton animated :count="5">
                  <template #template>
                    <div class="hot-skeleton-item">
                      <el-skeleton-item variant="text" style="width: 100%; height: 16px; margin-bottom: 4px" />
                      <el-skeleton-item variant="text" style="width: 60%; height: 14px" />
                    </div>
                  </template>
                </el-skeleton>
              </div>

              <!-- 空状态 -->
              <div v-else-if="hotArticleList.length === 0" class="hot-articles-empty">
                <el-empty description="暂无热门文章" :image-size="60" />
              </div>

              <!-- 热门文章列表 -->
              <div v-else class="hot-articles">
                <div v-for="(article, index) in hotArticleList" :key="article.id" class="hot-article-item" @click="goToArticle(article.id, article.userId)">
                  <div class="hot-article-rank">{{ index + 1 }}</div>
                  <div class="hot-article-content">
                    <span class="hot-article-title">{{ article.title }}</span>
                    <div class="hot-article-meta">
                      <span class="hot-article-readCount">
                        <el-icon><View /></el-icon>
                        {{ article.readCount }}
                      </span>
                      <span class="hot-article-score">🔥 {{ article.hotScore }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Star, ArrowUp, Picture, View } from "@element-plus/icons-vue";
import { getAllArticleList, getHotArticleList } from "@/api/article";

// 路由
const router = useRouter();

// 响应式数据
const articleLoading = ref(false); // 文章列表加载状态
const loadingMore = ref(false); // 加载更多数据状态
const articleList = ref([]); // 文章列表数据
const total = ref(0); // 文章总数
const hasMore = ref(true); // 是否还有更多数据可加载
const currentPage = ref(1); // 当前页码
const showBackToTop = ref(false); // 是否显示返回顶部按钮

// 热门文章相关数据
const hotArticleLoading = ref(false); // 热门文章加载状态
const hotArticleList = ref([]); // 热门文章列表数据

// 每页数据量
const pageSize = ref(10);

// 获取文章列表
const fetchArticleList = async (reset = false) => {
  // 如果没有更多数据或者已经在加载中，则不再请求
  if (!hasMore.value || articleLoading.value || loadingMore.value) {
    return;
  }

  try {
    // 设置加载状态
    if (reset) {
      articleLoading.value = true;
    } else {
      loadingMore.value = true;
    }

    const res = await getAllArticleList(currentPage.value, pageSize.value);
    const newArticles = res.data.data.data || [];
    total.value = res.data.data.total || 0;

    if (reset) {
      // 初次加载时重置列表
      articleList.value = newArticles;
    } else {
      // 无限滚动时加载下一页数据
      articleList.value = [...articleList.value, ...newArticles];
    }

    // 判断是否还有更多数据
    hasMore.value = articleList.value.length < total.value;

    // 更新页码
    if (hasMore.value && newArticles.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取文章列表失败");
    console.error("获取文章列表失败:", error);
  } finally {
    articleLoading.value = false;
    loadingMore.value = false;
  }
};

// 返回顶部 - 滚动到页面顶部
const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 处理滚动事件 - 页面级无限滚动
const handleScroll = () => {
  if (articleLoading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  // 显示/隐藏返回顶部按钮
  showBackToTop.value = window.scrollY > 200;

  // 当滚动到页面底部附近时加载更多
  if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 100) {
    fetchArticleList();
  }
};

// 跳转至文章详情页
const goToArticle = (articleId, userId) => {
  router.push(`/user/${userId}/article/${articleId}`);
};

// 跳转到用户主页
const goToUserPage = (userId) => {
  router.push(`/user/${userId}`);
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) {
    return "";
  }

  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) {
    return dateString;
  }

  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 获取热门文章列表
const fetchHotArticleList = async () => {
  try {
    hotArticleLoading.value = true;
    const res = await getHotArticleList(1, 10);
    hotArticleList.value = res.data.data.data || [];
  } catch (error) {
    console.error("获取热门文章列表失败:", error);
  } finally {
    hotArticleLoading.value = false;
  }
};

// 组件挂载
onMounted(() => {
  fetchArticleList(true);
  fetchHotArticleList(); // 获取热门文章
  // 绑定页面滚动事件
  window.addEventListener("scroll", handleScroll);
});

// 组件卸载
onUnmounted(() => {
  // 清理页面滚动事件监听器
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
// 文章页面容器
.article-page {
  --bg-page: #f8fafc;
  --bg-hero: #eef4ff;
  --bg-card: #ffffff;
  --bg-soft: #f8fafc;
  --bg-hover: #f1f5f9;
  --text-primary: #0f172a;
  --text-regular: #334155;
  --text-secondary: #64748b;
  --border-color: #e2e8f0;
  --border-strong: #cbd5e1;
  --shadow-soft: 0 1px 3px rgba(15, 23, 42, 0.08);
  --shadow-medium: 0 12px 30px rgba(15, 23, 42, 0.08);
  --rank-top-bg: rgba(249, 115, 22, 0.12);
  --rank-top-color: #c2410c;
  --rank-second-bg: rgba(14, 165, 233, 0.12);
  --rank-second-color: #0369a1;
  --rank-third-bg: rgba(16, 185, 129, 0.12);
  --rank-third-color: #047857;

  // 黑夜模式适配
  html.dark & {
    --bg-page: #0f172a;
    --bg-hero: #162033;
    --bg-card: #1e293b;
    --bg-soft: #22314b;
    --bg-hover: rgba(255, 255, 255, 0.04);
    --text-primary: #f1f5f9;
    --text-regular: #cbd5e1;
    --text-secondary: #94a3b8;
    --border-color: #334155;
    --border-strong: #475569;
    --shadow-soft: 0 1px 3px rgba(0, 0, 0, 0.3);
    --shadow-medium: 0 18px 36px rgba(0, 0, 0, 0.28);
    --rank-top-bg: rgba(251, 146, 60, 0.18);
    --rank-top-color: #fdba74;
    --rank-second-bg: rgba(56, 189, 248, 0.18);
    --rank-second-color: #7dd3fc;
    --rank-third-bg: rgba(52, 211, 153, 0.18);
    --rank-third-color: #86efac;
  }

  min-height: 100vh;
  background: var(--bg-page);

  // 内容区域
  .content-section {
    padding: 32px 0 60px;

    // 容器
    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 24px;

      // 页面头部
      .page-hero {
        display: flex;
        justify-content: space-between;
        align-items: stretch;
        gap: 24px;
        margin-bottom: 24px;
        padding: 28px 32px;
        background: var(--bg-hero);
        border: 1px solid var(--border-color);
        border-radius: 24px;
        box-shadow: var(--shadow-soft);

        .hero-copy {
          display: flex;
          flex-direction: column;
          gap: 10px;
          max-width: 620px;

          .hero-kicker {
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 0.08em;
            text-transform: uppercase;
            color: var(--el-color-primary);
          }

          h1 {
            margin: 0;
            font-size: 34px;
            line-height: 1.2;
            color: var(--text-primary);
          }

          p {
            margin: 0;
            font-size: 15px;
            line-height: 1.7;
            color: var(--text-secondary);
          }
        }

        .hero-metrics {
          display: flex;
          gap: 12px;
          flex-shrink: 0;

          .metric-card {
            min-width: 128px;
            padding: 18px 20px;
            background: var(--bg-card);
            border: 1px solid var(--border-color);
            border-radius: 18px;
            box-shadow: var(--shadow-soft);
            display: flex;
            flex-direction: column;
            justify-content: center;
            gap: 8px;

            .metric-label {
              font-size: 12px;
              color: var(--text-secondary);
            }

            .metric-value {
              font-size: 28px;
              font-weight: 700;
              line-height: 1;
              color: var(--text-primary);
            }
          }
        }
      }

      // 内容布局
      .content-layout {
        display: grid;
        grid-template-columns: 1fr 320px;
        gap: 32px;

        // 响应式：小屏幕单列布局
        @media (max-width: 992px) {
          grid-template-columns: 1fr;
          gap: 24px;
        }

        // 左侧主要内容
        .main-content {
          // 文章列表包装器
          .article-list-wrapper {
            position: relative;

            // 文章列表区域
            .article-list-section {
              background: var(--bg-card);
              border: 1px solid var(--border-color);
              border-radius: 24px;
              padding: 24px;
              box-shadow: var(--shadow-soft);

              // 列表头部
              .section-head {
                display: flex;
                justify-content: space-between;
                align-items: flex-end;
                gap: 16px;
                padding-bottom: 20px;
                margin-bottom: 4px;
                border-bottom: 1px solid var(--border-color);

                .section-title-group {
                  display: flex;
                  flex-direction: column;
                  gap: 6px;

                  h2 {
                    margin: 0;
                    font-size: 22px;
                    line-height: 1.3;
                    color: var(--text-primary);
                  }

                  p {
                    margin: 0;
                    font-size: 13px;
                    color: var(--text-secondary);
                  }
                }

                .section-meta {
                  font-size: 13px;
                  color: var(--text-secondary);
                  padding: 6px 10px;
                  background: var(--bg-soft);
                  border: 1px solid var(--border-color);
                  border-radius: 999px;
                  white-space: nowrap;
                }
              }

              // 加载容器样式
              .loading-container {
                padding: 12px 0 0;
              }

              // 骨架屏样式
              .article-skeleton {
                display: flex;
                gap: 20px;
                padding: 24px 0;
                border-bottom: 1px solid var(--border-color);

                .skeleton-content {
                  flex: 1;
                  display: flex;
                  flex-direction: column;
                  gap: 10px;
                }
              }

              // 空状态
              .empty-state {
                padding: 80px 0;
                text-align: center;

                :deep(.el-empty__description) {
                  color: var(--text-secondary);
                }
              }

              // 文章列表
              .article-list {
                .article-item {
                  display: flex;
                  gap: 24px;
                  padding: 20px;
                  margin-top: 16px;
                  background: var(--bg-card);
                  border: 1px solid var(--border-color);
                  border-radius: 20px;
                  cursor: pointer;
                  transition: border-color 0.2s ease, background-color 0.2s ease, box-shadow 0.2s ease;

                  &:hover {
                    background: var(--bg-hover);
                    border-color: var(--border-strong);
                    box-shadow: var(--shadow-medium);

                    .article-title {
                      color: var(--el-color-primary);
                    }
                  }

                  // 文章封面
                  .article-cover {
                    width: 240px;
                    height: 150px;
                    border-radius: 16px;
                    flex-shrink: 0;
                    overflow: hidden;
                    border: 1px solid var(--border-color);
                    background: var(--bg-soft);

                    .loading-text {
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      width: 100%;
                      height: 100%;
                      font-size: 13px;
                      color: var(--text-secondary);
                      background-color: var(--bg-soft);
                    }

                    .error {
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      width: 100%;
                      height: 100%;
                      background-color: var(--bg-soft);

                      .el-icon {
                        font-size: 28px;
                        color: var(--text-secondary);
                      }
                    }
                  }

                  // 文章内容
                  .article-content {
                    flex: 1;
                    display: flex;
                    flex-direction: column;
                    justify-content: space-between;
                    min-width: 0;

                    // 作者信息
                    .article-author {
                      display: flex;
                      align-items: center;
                      gap: 10px;
                      margin-bottom: 14px;
                      cursor: pointer;

                      &:hover {
                        .author-name {
                          color: var(--el-color-primary);
                        }
                      }

                      .author-avatar {
                        border: 1px solid var(--border-color);
                        box-shadow: none;
                      }

                      .author-name {
                        font-size: 14px;
                        font-weight: 600;
                        color: var(--text-regular);
                        transition: color 0.2s ease;
                      }
                    }

                    .article-title {
                      font-size: 20px;
                      font-weight: 700;
                      color: var(--text-primary);
                      margin: 0 0 12px 0;
                      line-height: 1.5;
                      display: -webkit-box;
                      -webkit-line-clamp: 2;
                      line-clamp: 2;
                      -webkit-box-orient: vertical;
                      overflow: hidden;
                      transition: color 0.2s ease;
                    }

                    .article-description {
                      font-size: 14px;
                      color: var(--text-secondary);
                      margin: 0 0 16px 0;
                      line-height: 1.75;
                      display: -webkit-box;
                      -webkit-line-clamp: 2;
                      line-clamp: 2;
                      -webkit-box-orient: vertical;
                      overflow: hidden;
                    }

                    // 文章元信息
                    .article-meta {
                      font-size: 13px;
                      color: var(--text-secondary);
                      display: flex;
                      align-items: center;
                      flex-wrap: wrap;
                      gap: 16px;

                      .article-date {
                        color: var(--text-secondary);
                      }

                      .article-readCount,
                      .article-likes,
                      .article-collections {
                        display: flex;
                        align-items: center;
                        gap: 5px;
                        color: var(--text-secondary);

                        .el-icon {
                          font-size: 15px;
                        }
                      }
                    }
                  }
                }

                // 加载更多指示器
                .loading-more {
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  padding: 40px 0 20px;

                  span {
                    font-size: 13px;
                    color: var(--text-secondary);
                  }

                  .loading-spinner {
                    width: 20px;
                    height: 20px;
                    border: 2px solid var(--border-color);
                    border-top-color: var(--el-color-primary);
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                    margin-right: 10px;
                  }
                }
              }

              // 返回顶部按钮
              .back-to-top {
                position: fixed;
                right: 48px;
                bottom: 48px;
                width: 44px;
                height: 44px;
                background: var(--bg-card);
                border: 1px solid var(--border-color);
                box-shadow: var(--shadow-medium);
                border-radius: 50%;
                cursor: pointer;
                transition: background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 100;

                &:hover {
                  background: var(--bg-hover);
                  color: var(--el-color-primary);
                  border-color: var(--border-strong);
                }

                .el-icon {
                  font-size: 20px;
                }
              }
            }
          }
        }

        // 右侧边栏
        .sidebar {
          position: sticky;
          top: 80px;
          height: fit-content;

          // 侧边栏卡片
          .sidebar-card {
            background: var(--bg-card);
            border-radius: 24px;
            padding: 22px;
            margin-bottom: 20px;
            box-shadow: var(--shadow-soft);
            border: 1px solid var(--border-color);

            // 卡片标题
            .card-title {
              font-size: 16px;
              font-weight: 600;
              color: var(--text-primary);
              margin: 0 0 10px 0;
              display: flex;
              align-items: center;
              gap: 6px;

              .el-icon {
                color: var(--el-color-primary);
              }
            }

            .card-description {
              margin: 0 0 18px 0;
              font-size: 13px;
              line-height: 1.6;
              color: var(--text-secondary);
            }

            // 热门文章加载状态
            .hot-articles-loading {
              .hot-skeleton-item {
                padding: 14px;
                border-radius: 14px;
                background: var(--bg-soft);
                margin-bottom: 10px;
              }
            }

            // 热门文章空状态
            .hot-articles-empty {
              padding: 30px 0;
              text-align: center;
            }

            // 热门文章列表
            .hot-articles {
              display: flex;
              flex-direction: column;
              gap: 12px;

              .hot-article-item {
                display: flex;
                align-items: flex-start;
                gap: 12px;
                padding: 14px;
                background: var(--bg-soft);
                border: 1px solid var(--border-color);
                border-radius: 16px;
                cursor: pointer;
                transition: background-color 0.2s ease, border-color 0.2s ease;

                &:hover {
                  background: var(--bg-hover);
                  border-color: var(--border-strong);

                  .hot-article-title {
                    color: var(--el-color-primary);
                  }
                }

                // 排名数字
                .hot-article-rank {
                  flex-shrink: 0;
                  width: 26px;
                  height: 26px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-size: 14px;
                  font-weight: 700;
                  color: var(--text-regular);
                  background: var(--bg-card);
                  border: 1px solid var(--border-color);
                  border-radius: 8px;
                }

                // 第一名金色
                &:nth-child(1) .hot-article-rank {
                  background: var(--rank-top-bg);
                  color: var(--rank-top-color);
                  border-color: transparent;
                }

                // 第二名银色
                &:nth-child(2) .hot-article-rank {
                  background: var(--rank-second-bg);
                  color: var(--rank-second-color);
                  border-color: transparent;
                }

                // 第三名铜色
                &:nth-child(3) .hot-article-rank {
                  background: var(--rank-third-bg);
                  color: var(--rank-third-color);
                  border-color: transparent;
                }

                // 文章内容区域
                .hot-article-content {
                  flex: 1;
                  display: flex;
                  flex-direction: column;
                  gap: 8px;
                  min-width: 0;

                  .hot-article-title {
                    font-size: 14px;
                    font-weight: 500;
                    color: var(--text-primary);
                    line-height: 1.5;
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    line-clamp: 2;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                    transition: color 0.2s ease;
                  }

                  // 文章元信息
                  .hot-article-meta {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-size: 12px;
                    color: var(--text-secondary);

                    .hot-article-readCount {
                      display: flex;
                      align-items: center;
                      gap: 4px;

                      .el-icon {
                        font-size: 14px;
                      }
                    }

                    .hot-article-score {
                      display: flex;
                      align-items: center;
                      gap: 3px;
                      font-weight: 600;
                      color: var(--el-color-danger);
                      background: transparent;
                    }
                  }
                }
              }
            }
          }

          // 响应式：小屏幕隐藏侧边栏
          @media (max-width: 768px) {
            display: none;
          }
        }
      }
    }
  }
}

// 加载动画
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-page {
    .content-section {
      padding: 16px 0 40px;

      .container {
        padding: 0 16px;

        .page-hero {
          flex-direction: column;
          padding: 20px;
          border-radius: 20px;
          margin-bottom: 16px;

          .hero-copy {
            h1 {
              font-size: 28px;
            }

            p {
              font-size: 14px;
            }
          }

          .hero-metrics {
            .metric-card {
              flex: 1;
              min-width: 0;
              padding: 16px;

              .metric-value {
                font-size: 24px;
              }
            }
          }
        }

        .content-layout {
          gap: 16px;

          .main-content {
            .article-list-wrapper {
              .back-to-top {
                right: 16px;
                bottom: 80px;
                width: 40px;
                height: 40px;
              }

              .article-list-section {
                padding: 18px;
                border-radius: 20px;

                .section-head {
                  flex-direction: column;
                  align-items: flex-start;
                  padding-bottom: 16px;
                }

                .article-list {
                  .article-item {
                    gap: 14px;
                    padding: 16px;
                    border-radius: 16px;

                    .article-cover {
                      width: 120px;
                      height: 80px;
                      border-radius: 10px;
                    }

                    .article-content {
                      .article-author {
                        margin-bottom: 8px;
                      }

                      .article-title {
                        font-size: 16px;
                        margin-bottom: 8px;
                      }

                      .article-description {
                        font-size: 13px;
                        margin-bottom: 10px;
                      }

                      .article-meta {
                        font-size: 12px;
                        gap: 10px;

                        .article-date {
                          display: none;
                        }
                      }
                    }
                  }

                  .loading-more {
                    padding: 30px 0 16px;
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
