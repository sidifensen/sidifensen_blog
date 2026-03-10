<template>
  <div class="article-page">
    <!-- 内容区域 -->
    <div class="content-section">
      <div class="container">
        <div class="content-layout">
          <!-- 左侧主要内容 -->
          <div class="main-content">
            <!-- 文章列表 -->
            <div class="article-list-wrapper">
              <div class="article-list-section" ref="listContainer">
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
                        <span class="article-date">{{ article.createTime }}</span>
                        <span class="article-readCount">
                          <el-icon> <View /> </el-icon>
                          {{ article.readCount }} 阅读</span
                        >
                        <span class="article-likes">
                          <svg-icon name="like" width="13px" height="13px" color="#909399" />
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
                <el-icon style="margin-right: 4px"><View /></el-icon>
                热门文章
              </h4>

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
import { useRoute, useRouter } from "vue-router";
import { Plus, Message, Star, Edit, ArrowUp, Picture, View } from "@element-plus/icons-vue";
import { getAllArticleList, getHotArticleList } from "@/api/article";
import { useUserStore } from "@/stores/userStore";

// 路由和状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

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

// 文章列表容器引用
const listContainer = ref(null);

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
// ===== 文章列表页面 - 精致极简风格 =====
// 设计参考：掘金、知乎、Medium

// 文章页面容器
.article-page {
  --bg-page: #f8f9fa;
  --bg-card: #ffffff;
  --text-primary: #1f2d3d;
  --text-regular: #606266;
  --text-secondary: #909399;
  --border-color: #ebeef5;
  --shadow-color: rgba(0, 0, 0, 0.04);

  // 黑夜模式适配
  html.dark & {
    --bg-page: #0a0a0a;
    --bg-card: #1a1a1a;
    --text-primary: #e5e5e5;
    --text-regular: #a3a3a3;
    --text-secondary: #737373;
    --border-color: #262626;
    --shadow-color: rgba(0, 0, 0, 0.3);
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
              // 添加白色背景，确保内容不透明
              background: var(--bg-card);
              border-radius: 12px;
              padding: 24px;
              box-shadow: 0 2px 12px var(--shadow-color);

              // 加载容器样式
              .loading-container {
                padding: 24px 0;
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
              }

              // 文章列表
              .article-list {
                .article-item {
                  display: flex;
                  gap: 24px;
                  padding: 24px 0;
                  border-bottom: 1px solid var(--border-color);
                  cursor: pointer;
                  transition: all 0.25s ease;

                  &:last-child {
                    border-bottom: none;
                  }

                  &:hover {
                    .article-title {
                      color: var(--el-color-primary);
                    }

                    .article-cover {
                      transform: scale(1.02);
                    }
                  }

                  // 文章封面
                  .article-cover {
                    width: 240px;
                    height: 150px;
                    border-radius: 8px;
                    flex-shrink: 0;
                    overflow: hidden;
                    background: var(--el-fill-color-light);
                    transition: transform 0.3s ease;

                    &:hover {
                      transform: scale(1.02);
                    }

                    .loading-text {
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      width: 100%;
                      height: 100%;
                      font-size: 13px;
                      color: var(--text-secondary);
                      background-color: var(--el-fill-color-light);
                    }

                    .error {
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      width: 100%;
                      height: 100%;
                      background-color: var(--el-fill-color-light);

                      .el-icon {
                        font-size: 28px;
                        color: var(--el-text-color-placeholder);
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
                        border: 2px solid var(--bg-card);
                        box-shadow: 0 2px 8px var(--shadow-color);
                      }

                      .author-name {
                        font-size: 14px;
                        font-weight: 500;
                        color: var(--text-regular);
                        transition: color 0.2s ease;
                      }
                    }

                    .article-title {
                      font-size: 18px;
                      font-weight: 600;
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
                      line-height: 1.7;
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
                      gap: 16px;

                      .article-date {
                        color: var(--text-secondary);
                      }

                      .article-readCount,
                      .article-likes,
                      .article-collections {
                        color: var(--text-secondary);
                        display: flex;
                        align-items: center;
                        gap: 5px;

                        .el-icon {
                          font-size: 15px;
                        }
                      }

                      &:hover {
                        color: var(--text-regular);
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
                box-shadow: 0 4px 16px var(--shadow-color);
                border-radius: 50%;
                cursor: pointer;
                transition: all 0.25s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 100;

                &:hover {
                  background: var(--el-color-primary);
                  color: #fff;
                  transform: translateY(-4px);
                  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.25);
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
            border-radius: 12px;
            padding: 20px;
            margin-bottom: 20px;
            box-shadow: 0 2px 12px var(--shadow-color);
            border: 1px solid var(--border-color);

            // 卡片标题
            .card-title {
              font-size: 16px;
              font-weight: 600;
              color: var(--text-primary);
              margin: 0 0 18px 0;
              padding-bottom: 14px;
              border-bottom: 2px solid var(--border-color);
              display: flex;
              align-items: center;

              .el-icon {
                color: var(--el-color-primary);
              }
            }

            // 热门文章加载状态
            .hot-articles-loading {
              .hot-skeleton-item {
                padding: 14px;
                border-radius: 8px;
                background: var(--el-fill-color-light);
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
              gap: 10px;

              .hot-article-item {
                display: flex;
                align-items: flex-start;
                gap: 12px;
                padding: 14px;
                background: var(--el-fill-color-light);
                border-radius: 8px;
                cursor: pointer;
                transition: all 0.2s ease;

                &:hover {
                  background: var(--el-color-primary-light-9);

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
                  background: var(--el-fill-color);
                  border-radius: 4px;
                }

                // 第一名金色
                &:nth-child(1) .hot-article-rank {
                  background: linear-gradient(135deg, #ffd700, #ffec8b);
                  color: #8b6914;
                }

                // 第二名银色
                &:nth-child(2) .hot-article-rank {
                  background: linear-gradient(135deg, #e0e0e0, #f5f5f5);
                  color: #5a5a5a;
                }

                // 第三名铜色
                &:nth-child(3) .hot-article-rank {
                  background: linear-gradient(135deg, #d4a574, #e9c499);
                  color: #7a4f2a;
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
                      background: var(--el-color-danger-light-9);
                      padding: 2px 6px;
                      border-radius: 4px;
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
    background: var(--bg-page);

    .content-section {
      padding: 0;

      .container {
        padding: 0 16px;

        .content-layout {
          gap: 16px;

          .main-content {
            .article-list-wrapper {
              .back-to-top {
                right: 16px;
                bottom: 80px;
                width: 40px;
                height: 40px;
                box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

                &:hover {
                  transform: translateY(-2px);
                }
              }

              .article-list-section {
                .article-list {
                  .article-item {
                    gap: 16px;
                    padding: 16px 0;

                    .article-cover {
                      width: 120px;
                      height: 80px;
                      border-radius: 6px;
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
