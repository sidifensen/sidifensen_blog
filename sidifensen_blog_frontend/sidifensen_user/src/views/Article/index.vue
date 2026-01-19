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
// 文章页面容器
.article-page {
  min-height: 100vh;

  // 全局a标签样式
  a {
    color: #409eff;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }

  // 内容区域
  .content-section {
    padding: 20px 0;

    // 容器
    .container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 0px;

      // 内容布局
      .content-layout {
        display: grid;
        grid-template-columns: 1fr 300px;
        gap: 20px;

        // 响应式：小屏幕单列布局
        @media (max-width: 992px) {
          grid-template-columns: 1fr;
        }

        // 左侧主要内容
        .main-content {
          // 文章列表包装器
          .article-list-wrapper {
            position: relative;

            // 文章列表区域
            .article-list-section {
              background: var(--el-bg-color-page);
              border-radius: 8px;
              padding: 20px;
              border: 1px solid var(--el-border-color);
              box-shadow: 0 2px 12px var(--el-border-color-light);
              min-height: calc(100vh - 120px);
              overflow: visible;

              // 加载容器样式
              .loading-container {
                padding: 20px 0;
              }

              // 骨架屏样式
              .article-skeleton {
                display: flex;
                gap: 16px;
                padding: 20px 0;
                border-bottom: 1px solid var(--el-border-color-light);

                .skeleton-content {
                  flex: 1;
                  display: flex;
                  flex-direction: column;
                  gap: 8px;
                }
              }

              // 空状态
              .empty-state {
                padding: 60px 0;
                text-align: center;
              }

              // 文章列表
              .article-list {
                .article-item {
                  display: flex;
                  gap: 16px;
                  padding: 20px 0;
                  border-bottom: 1px solid var(--el-border-color-light);
                  cursor: pointer;
                  transition: all 0.3s ease;

                  &:last-child {
                    border-bottom: none;
                  }

                  &:hover {
                    background-color: var(--el-bg-color-page);
                    transform: translateX(4px);
                  }

                  // 文章封面
                  .article-cover {
                    width: 250px;
                    height: 156px;
                    border-radius: 6px;
                    transition: transform 0.3s ease;

                    &:hover {
                      transform: scale(1.05);
                    }

                    .loading-text {
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      width: 100%;
                      height: 100%;
                      font-size: 12px;
                      color: var(--el-text-color-regular);
                      background-color: var(--el-bg-color-page);
                    }

                    .error {
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      width: 100%;
                      height: 100%;
                      background-color: var(--el-bg-color-page);

                      .el-icon {
                        font-size: 24px;
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

                    // 作者信息
                    .article-author {
                      display: flex;
                      align-items: center;
                      gap: 8px;
                      margin-bottom: 8px;
                      cursor: pointer;
                      transition: all 0.2s ease;

                      &:hover {
                        color: var(--el-color-primary);
                      }

                      .author-avatar {
                        transition: transform 0.2s ease;
                      }

                      &:hover .author-avatar {
                        transform: scale(1.1);
                      }

                      .author-name {
                        font-size: 14px;
                        font-weight: 500;
                        color: var(--el-text-color-regular);
                      }
                    }

                    .article-title {
                      font-size: 18px;
                      font-weight: 600;
                      color: var(--el-text-color-primary);
                      margin: 0 0 8px 0;
                      line-height: 1.4;
                      display: -webkit-box;
                      -webkit-line-clamp: 2;
                      line-clamp: 2;
                      -webkit-box-orient: vertical;
                      overflow: hidden;
                    }

                    .article-description {
                      font-size: 14px;
                      color: var(--el-text-color-regular);
                      margin: 0 0 12px 0;
                      line-height: 1.5;
                      display: -webkit-box;
                      -webkit-line-clamp: 2;
                      line-clamp: 2;
                      -webkit-box-orient: vertical;
                      overflow: hidden;
                    }

                    // 文章元信息
                    .article-meta {
                      font-size: 13px;
                      color: var(--el-text-color-secondary);
                      display: flex;
                      align-items: center;
                      gap: 12px;

                      .article-date {
                        color: var(--el-text-color-secondary);
                      }

                      .article-readCount,
                      .article-likes,
                      .article-collections {
                        color: var(--el-text-color-secondary);
                        display: flex;
                        align-items: center;
                        gap: 4px;
                      }
                    }
                  }
                }

                // 加载更多指示器
                .loading-more {
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  padding: 30px;
                  color: var(--el-text-color-regular);

                  .loading-spinner {
                    width: 20px;
                    height: 20px;
                    border: 2px solid #f3f3f3;
                    border-top: 2px solid #409eff;
                    border-radius: 50%;
                    animation: spin 1s linear infinite;
                    margin-right: 10px;
                  }
                }
              }

              // 返回顶部按钮
              .back-to-top {
                position: fixed;
                right: 150px;
                bottom: 150px;
                width: 50px;
                height: 50px;
                backdrop-filter: blur(2px);
                background-color: color-mix(in srgb, var(--el-bg-color-page) 90%, transparent);
                border: 1px solid var(--el-border-color);
                box-shadow: 0 2px 12px var(--el-border-color-light);
                border-radius: 50%;
                cursor: pointer;
                transition: all 0.3s ease;
                display: flex;
                align-items: center;
                justify-content: center;
                z-index: 10;

                &:hover {
                  background: var(--el-color-primary);
                  color: white;
                  transform: translateY(-4px);
                }

                .el-icon {
                  font-size: 24px;
                }
              }
            }
          }
        }

        // 右侧边栏
        .sidebar {
          // 侧边栏卡片
          .sidebar-card {
            background: var(--el-bg-color-page);
            border-radius: 8px;
            padding: 20px;
            margin-bottom: 20px;
            border: 1px solid var(--el-border-color);
            box-shadow: 0 2px 12px var(--el-border-color-light);

            // 卡片标题
            .card-title {
              font-size: 16px;
              font-weight: 600;
              color: var(--el-text-color-primary);
              margin: 0 0 16px 0;
              padding-bottom: 8px;
              border-bottom: 2px solid var(--el-color-primary);
            }

            // 热门文章加载状态
            .hot-articles-loading {
              .hot-skeleton-item {
                padding: 12px;
                border-radius: 6px;
                background-color: var(--el-fill-color-lighter);
                margin-bottom: 8px;
              }
            }

            // 热门文章空状态
            .hot-articles-empty {
              padding: 20px 0;
              text-align: center;
            }

            // 热门文章列表
            .hot-articles {
              display: flex;
              flex-direction: column;
              gap: 8px;

              .hot-article-item {
                display: flex;
                align-items: flex-start;
                gap: 10px;
                padding: 12px;
                background-color: var(--el-fill-color-lighter);
                border-radius: 6px;
                cursor: pointer;
                transition: all 0.2s ease;

                &:hover {
                  background-color: var(--el-color-primary-light-9);
                  transform: translateX(4px);
                }

                // 排名数字（默认样式）
                .hot-article-rank {
                  flex-shrink: 0;
                  width: 24px;
                  height: 24px;
                  display: flex;
                  align-items: center;
                  justify-content: center;
                  font-size: 14px;
                  font-weight: 700;
                  color: var(--el-text-color-primary);
                  background: linear-gradient(135deg, var(--el-color-primary-light-5), var(--el-color-primary-light-3));
                  border-radius: 4px;
                }

                // 第一名金色
                &:nth-child(1) .hot-article-rank {
                  background: linear-gradient(135deg, #ffd700, #ffed4e);
                  color: #b8860b;
                }

                // 第二名银色
                &:nth-child(2) .hot-article-rank {
                  background: linear-gradient(135deg, #c0c0c0, #e8e8e8);
                  color: #696969;
                }

                // 第三名铜色
                &:nth-child(3) .hot-article-rank {
                  background: linear-gradient(135deg, #cd7f32, #e9a76e);
                  color: #8b4513;
                }

                // 文章内容区域
                .hot-article-content {
                  flex: 1;
                  display: flex;
                  flex-direction: column;
                  gap: 6px;
                  min-width: 0;

                  .hot-article-title {
                    font-size: 14px;
                    font-weight: 500;
                    color: var(--el-text-color-primary);
                    line-height: 1.4;
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    line-clamp: 2;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                    word-break: break-word;
                  }

                  // 文章元信息
                  .hot-article-meta {
                    display: flex;
                    align-items: center;
                    gap: 12px;
                    font-size: 12px;
                    color: var(--el-text-color-secondary);

                    .hot-article-readCount {
                      display: flex;
                      align-items: center;
                      gap: 4px;

                      .el-icon {
                        font-size: 13px;
                      }
                    }

                    .hot-article-score {
                      display: flex;
                      align-items: center;
                      gap: 2px;
                      font-weight: 600;
                      color: var(--el-color-danger);
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
    background-attachment: scroll;

    .content-section {
      padding: 0;

      .container {
        .content-layout {
          .main-content {
            .article-list-wrapper {
              .back-to-top {
                width: 40px !important;
                height: 40px !important;
                right: 15px !important;
                bottom: 15px !important;
              }

              .article-list-section {
                border-radius: 0;
                min-height: calc(100vh - 80px);
                overflow: visible;
                padding: 15px;

                .article-list {
                  .article-item {
                    flex-direction: row;
                    gap: 12px;
                    padding: 15px 0;

                    .article-content {
                      flex: 1;
                      min-width: 0;
                      order: 1;

                      .article-author {
                        margin-bottom: 6px;

                        .author-avatar {
                          flex-shrink: 0;
                        }

                        .author-name {
                          font-size: 13px;
                        }
                      }

                      .article-title {
                        font-size: 16px;
                        margin: 0 0 6px 0;
                        -webkit-line-clamp: 2;
                        line-clamp: 2;
                      }

                      .article-description {
                        font-size: 13px;
                        margin: 0 0 8px 0;
                        -webkit-line-clamp: 2;
                        line-clamp: 2;
                      }

                      .article-meta {
                        font-size: 12px;
                        gap: 8px;
                        flex-wrap: wrap;

                        .article-date {
                          display: none;
                        }
                      }
                    }

                    .article-cover {
                      width: 120px;
                      height: 75px;
                      flex-shrink: 0;
                      order: 2;
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
}
</style>
