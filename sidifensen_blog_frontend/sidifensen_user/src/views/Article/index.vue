<template>
  <div class="article-page-body">
  <div class="container">
    <!-- 页面头部 -->
    <div class="page-hero">
      <div class="hero-content">
        <div class="hero-copy">
          <div class="hero-kicker">Article Square</div>
          <h1>文章广场</h1>
          <p>按发布时间浏览社区内容，持续发现值得读的技术文章。</p>
        </div>
      </div>
    </div>

    <!-- 内容布局 -->
    <div class="content-layout">
      <!-- 主内容区 -->
      <div class="main-content">
        <div class="article-list" ref="articleListRef">
          <!-- 加载中骨架屏 -->
          <div v-if="articleLoading" class="article-list-loading">
            <el-skeleton animated :count="4">
              <template #template>
                <div class="article-skeleton">
                  <el-skeleton-item variant="image" style="width: 200px; height: 120px; border-radius: 4px" />
                  <div class="skeleton-content">
                    <el-skeleton-item variant="text" style="width: 80px; height: 16px" />
                    <el-skeleton-item variant="text" style="width: 100%; height: 20px" />
                    <el-skeleton-item variant="text" style="width: 90%; height: 16px" />
                    <el-skeleton-item variant="text" style="width: 60%; height: 14px" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- 空状态 -->
          <div v-else-if="articleList.length === 0" class="empty-state">
            <EmptyState type="article" />
          </div>

          <!-- 文章列表 -->
          <template v-else>
            <ArticleCard
              v-for="(article, index) in articleList"
              :key="article.id"
              :article="article"
              mode="simple"
              :show-author="true"
              :show-meta="true"
              @click="goToArticle(article.id, article.userId)"
            />
          </template>

          <!-- 加载更多指示器 -->
          <LoadingMore :loading="loadingMore" />
        </div>
      </div>

      <!-- 侧边栏 -->
      <aside class="sidebar">
        <!-- 热门文章 -->
        <SidebarCard title="热门文章" icon="🔥" description="按阅读热度排序">
          <HotArticleList
            :articles="hotArticleList"
            :loading="hotArticleLoading"
            @article-click="(article) => goToArticle(article.id, article.userId)"
          />
        </SidebarCard>

        <!-- 会员精选 -->
        <SidebarCard title="会员精选" icon="⭐" description="高质量付费内容">
          <template v-if="featuredArticleLoading">
            <div class="featured-articles-loading">
              <div v-for="index in 4" :key="index" class="featured-skeleton-item">
                <div class="featured-skeleton-cover"></div>
                <div class="featured-skeleton-content">
                  <div class="skeleton-title"></div>
                  <div class="skeleton-meta"></div>
                </div>
              </div>
            </div>
          </template>
          <template v-else-if="featuredArticleList.length === 0">
            <EmptyState type="article" description="暂无会员精选" :image-size="60" />
          </template>
          <template v-else>
            <div class="featured-articles">
              <div
                v-for="article in featuredArticleList"
                :key="article.id"
                class="featured-article-item"
                @click="goToArticle(article.id, article.userId)"
              >
                <div class="featured-article-cover">
                  <el-image :src="article.coverUrl" class="cover-image">
                    <template #placeholder>
                      <div class="loading-text">加载中...</div>
                    </template>
                    <template #error>
                      <div class="error">
                        <el-icon><Picture /></el-icon>
                      </div>
                    </template>
                  </el-image>
                </div>

                <div class="featured-article-content">
                  <div class="featured-article-title">{{ article.title }}</div>
                  <div class="featured-article-meta">
                    <span><el-icon><View /></el-icon> {{ formatCount(article.readCount) }}</span>
                    <span><svg-icon name="like" width="13px" height="13px" color="currentColor" /> {{ formatCount(article.likeCount) }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="featured-footer">
              <button class="featured-link" @click="goToVipArticles">进入会员专区</button>
            </div>
          </template>
        </SidebarCard>
      </aside>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from "vue";
import { useRouter } from "vue-router";
import { Star, Picture, View, User } from "@element-plus/icons-vue";
import { getAllArticleList, getHotArticleList, getVipPreviewArticleList } from "@/api/article";
import ArticleCard from "@/components/ArticleCard.vue";
import SidebarCard from "@/components/SidebarCard.vue";
import HotArticleList from "@/components/HotArticleList.vue";
import EmptyState from "@/components/EmptyState.vue";
import LoadingMore from "@/components/LoadingMore.vue";

// 路由
const router = useRouter();

// 响应式数据
const articleLoading = ref(false);
const loadingMore = ref(false);
const articleList = ref([]);
const total = ref(0);
const hasMore = ref(true);
const currentPage = ref(1);
// 文章列表容器引用
const articleListRef = ref(null);
// 文章元素引用数组
const articleRefs = ref([]);

// 热门文章相关数据
const hotArticleLoading = ref(false);
const hotArticleList = ref([]);

// 会员精选相关数据
const featuredArticleLoading = ref(false);
const featuredArticleList = ref([]);

// 每页数据量
const pageSize = ref(10);

// 计算属性 - 是否还有更多数据
const hasMoreData = computed(() => {
  return articleList.value.length < total.value && !articleLoading.value && !loadingMore.value;
});

// 节流函数
const throttle = (func, delay) => {
  let lastCall = 0;
  return function (...args) {
    const now = Date.now();
    if (now - lastCall >= delay) {
      lastCall = now;
      func.apply(this, args);
    }
  };
};

// 检查是否需要自动加载（处理内容不足一屏或最后一篇文章下方有大片空白的情况）
const checkAndAutoLoad = (forceThreshold = false, useNextTick = false) => {
  const check = () => {
    const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
    const windowHeight = window.innerHeight;

    // 使用最后一篇文章的实际位置来判断
    let lastArticleTop = 0;
    if (articleRefs.value.length > 0) {
      const lastArticle = articleRefs.value[articleRefs.value.length - 1];
      if (lastArticle) {
        const rect = lastArticle.getBoundingClientRect();
        lastArticleTop = rect.top + scrollTop;
      } else {
        lastArticleTop = document.documentElement.scrollHeight;
      }
    } else {
      lastArticleTop = document.documentElement.scrollHeight;
    }

    // 用户可见区域的底部位置
    const viewportBottom = scrollTop + windowHeight;
    // 最后一篇文章距离视口底部的距离（负值表示已经滚过）
    const distanceToLastArticle = lastArticleTop - viewportBottom;

    if (!hasMoreData.value || loadingMore.value) {
      return;
    }

    // forceThreshold 用于首次加载后的检查，使用更宽松的阈值
    const threshold = forceThreshold ? windowHeight : 100;

    // 两种情况触发加载:
    // 1. 用户可见区域底部已经接近或超过最后一篇文章顶部（distanceToLastArticle <= threshold）
    // 2. 最后一篇文章已经在视口内或上方
    if (distanceToLastArticle <= threshold) {
      fetchArticleList(false);
    }
  };

  if (useNextTick) {
    nextTick(() => check());
  } else {
    check();
  }
};

// 滚动加载更多
const handleScroll = throttle(() => {
  checkAndAutoLoad();
}, 300);

// 获取文章列表
const fetchArticleList = async (reset = false) => {
  if (!hasMore.value || articleLoading.value || loadingMore.value) {
    return;
  }

  try {
    if (reset) {
      articleLoading.value = true;
    } else {
      loadingMore.value = true;
    }

    const res = await getAllArticleList(currentPage.value, pageSize.value);
    const newArticles = res.data.data || [];
    total.value = res.data.total || 0;

    if (reset) {
      articleList.value = newArticles;
    } else {
      articleList.value = [...articleList.value, ...newArticles];
    }

    hasMore.value = articleList.value.length < total.value;

    if (hasMore.value && newArticles.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取文章列表失败");
  } finally {
    articleLoading.value = false;
    loadingMore.value = false;

    // 加载完成后立即检查，如果内容仍不足则自动加载下一页
    // reset=true 时使用更宽松的阈值（一个视口高度），确保首屏内容充足
    // useNextTick=true 确保 DOM 完全渲染后再检查
    checkAndAutoLoad(reset, true);
  }
};

// 跳转到文章详情页
const goToArticle = (articleId, userId) => {
  router.push(`/user/${userId}/article/${articleId}`);
};

// 跳转到会员专区
const goToVipArticles = () => {
  router.push("/vip/articles");
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  if (Number.isNaN(date.getTime())) return dateString;
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 格式化数量
const formatCount = (value) => {
  const count = Number(value) || 0;
  if (count < 1000) return `${count}`;
  if (count < 1000000) {
    const formatted = (count / 1000).toFixed(count >= 100000 ? 0 : 1);
    return `${formatted.replace(/\.0$/, "")}K`;
  }
  const formatted = (count / 1000000).toFixed(count >= 10000000 ? 0 : 1);
  return `${formatted.replace(/\.0$/, "")}M`;
};

// 获取热门文章列表
const fetchHotArticleList = async () => {
  try {
    hotArticleLoading.value = true;
    const res = await getHotArticleList(1, 10);
    hotArticleList.value = res.data.data || [];
  } catch (error) {
    // 静默处理
  } finally {
    hotArticleLoading.value = false;
  }
};

// 获取会员精选列表
const fetchFeaturedArticleList = async () => {
  try {
    featuredArticleLoading.value = true;
    const res = await getVipPreviewArticleList(1, 4);
    featuredArticleList.value = res.data.data || [];
  } catch (error) {
    // 静默处理
  } finally {
    featuredArticleLoading.value = false;
  }
};

// 组件挂载
onMounted(() => {
  fetchArticleList(true);
  fetchHotArticleList();
  fetchFeaturedArticleList();
  window.addEventListener("scroll", handleScroll, { passive: true });
});

// 组件卸载
onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
// 文章广场页面 - 现代极简主义科技风
// 完全匹配参考文件：白天模式 article-square-style-modern-tech-light.html，黑夜模式 article-square-style-modern-tech.html

// 全局盒模型
*,
*::before,
*::after {
  box-sizing: border-box;
}

.article-page-body {
  width: 100%;
  min-height: 100vh;
  background: #f8fafc;
  overflow-x: hidden;

  html.dark & {
    background: #000000;
  }
}

.container {
  // ===== CSS 变量定义 - 白天模式（默认，匹配 article-square-style-modern-tech-light.html）=====
  --bg-page: #f8fafc;
  --bg-hero-divider: rgba(0, 0, 0, 0.08);
  --bg-metric: rgba(0, 0, 0, 0.02);
  --bg-card: rgba(0, 0, 0, 0.015);
  --bg-subtle: rgba(0, 0, 0, 0.02);
  --bg-hover: rgba(0, 0, 0, 0.01);

  --text-primary: #0a0a0a;
  --text-secondary: #888888;
  --text-regular: #666666;
  --text-muted: #aaaaaa;
  --text-dim: #cccccc;

  --border-light: rgba(0, 0, 0, 0.05);
  --border-regular: rgba(0, 0, 0, 0.08);
  --border-hover: rgba(0, 0, 0, 0.15);

  --accent-color: #0066ff;
  --accent-soft: rgba(0, 102, 255, 0.04);
  --accent-border: rgba(0, 102, 255, 0.15);
  --accent-hover: rgba(0, 102, 255, 0.08);

  --rank-1-bg: rgba(0, 102, 255, 0.1);
  --rank-1-color: #0066ff;
  --rank-2-bg: rgba(124, 58, 237, 0.1);
  --rank-2-color: #7c3aed;
  --rank-3-bg: rgba(16, 185, 129, 0.1);
  --rank-3-color: #10b981;

  --fire-color: #ff4757;

  // ===== 黑夜模式覆盖（匹配 article-square-style-modern-tech.html）=====
  html.dark & {
    --bg-page: #000000;
    --bg-hero-divider: rgba(255, 255, 255, 0.1);
    --bg-metric: rgba(255, 255, 255, 0.03);
    --bg-card: rgba(255, 255, 255, 0.02);
    --bg-subtle: rgba(255, 255, 255, 0.02);
    --bg-hover: rgba(255, 255, 255, 0.02);

    --text-primary: #ffffff;
    --text-secondary: #888888;
    --text-regular: #dddddd;
    --text-muted: #666666;
    --text-dim: #444444;

    --border-light: rgba(255, 255, 255, 0.05);
    --border-regular: rgba(255, 255, 255, 0.08);
    --border-hover: rgba(255, 255, 255, 0.15);

    --accent-color: #00d4ff;
    --accent-soft: rgba(0, 212, 255, 0.05);
    --accent-border: rgba(0, 212, 255, 0.15);
    --accent-hover: rgba(0, 212, 255, 0.08);

    --rank-1-bg: rgba(0, 212, 255, 0.1);
    --rank-1-color: #00d4ff;
    --rank-2-bg: rgba(124, 58, 237, 0.1);
    --rank-2-color: #7c3aed;
    --rank-3-bg: rgba(16, 185, 129, 0.1);
    --rank-3-color: #10b981;

    --fire-color: #ff4757;
  }

  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 100vh;
  background: var(--bg-page);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  line-height: 1.6;

  // 手机端适配 - 防止页面溢出
  @media (max-width: 768px) {
    padding: 0 20px;
    box-sizing: border-box;
    width: 100%;
    max-width: 100vw;
  }

  // ===== 页面头部 - 极致简约 =====
  .page-hero {
    padding: 60px 0 40px;
    border-bottom: 1px solid var(--bg-hero-divider);

    // 手机端适配
    @media (max-width: 768px) {
      padding: 40px 0 25px;

      .hero-kicker {
        font-size: 10px;
      }

      h1 {
        font-size: 28px;
      }

      p {
        font-size: 13px;
      }
    }

    .hero-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      gap: 24px;

      // 手机端适配
      @media (max-width: 768px) {
        justify-content: flex-start;
        gap: 8px;
      }

      .hero-copy {
        max-width: 500px;

        .hero-kicker {
          font-size: 11px;
          font-weight: 600;
          letter-spacing: 0.2em;
          text-transform: uppercase;
          color: var(--accent-color);
          margin-bottom: 12px;
          display: flex;
          align-items: center;
          gap: 8px;

          &::before {
            content: '';
            width: 20px;
            height: 1px;
            background: linear-gradient(90deg, var(--accent-color), transparent);
          }
        }

        h1 {
          font-size: 40px;
          font-weight: 700;
          letter-spacing: -0.04em;
          margin-bottom: 12px;
          color: var(--text-primary);
        }

        p {
          font-size: 14px;
          color: var(--text-secondary);
          line-height: 1.7;
        }
      }
    }
  }

  // ===== 内容布局 =====
  .content-layout {
    display: grid;
    grid-template-columns: 1fr 280px;
    gap: 32px;
    padding: 40px 0 60px;

    @media (max-width: 992px) {
      grid-template-columns: 1fr;
    }

    // 手机端适配 - 防止页面水平滚动
    @media (max-width: 768px) {
      padding: 30px 0 50px;
      gap: 24px;
    }

    // ===== 主内容区 =====
    .main-content {
      width: 100%;
      max-width: 100%;

      // 手机端适配
      @media (max-width: 768px) {
        width: 100%;
      }
      // ===== 文章列表 - 极致简约 =====
      .article-list {
        display: flex;
        flex-direction: column;
        gap: 12px;
        width: 100%;
        max-width: 100%;

        // 手机端适配
        @media (max-width: 768px) {
          gap: 24px;
        }

        // 骨架屏加载
        .article-list-loading {
          .article-skeleton {
            display: flex;
            gap: 20px;
            padding: 20px 0;
            border-bottom: 1px solid var(--border-light);

            // 手机端适配
            @media (max-width: 768px) {
              flex-direction: column;
              gap: 0;
              padding: 0;
              border: none;

              :deep(.el-skeleton-item) {
                &[variant="image"] {
                  width: 100% !important;
                  height: 200px !important;
                  border-radius: 0;
                }
              }
            }

            .skeleton-content {
              flex: 1;
              display: flex;
              flex-direction: column;
              gap: 10px;
            }
          }
        }

        // 空状态
        .empty-state {
          padding: 80px 0;
          text-align: center;

          // 手机端适配
          @media (max-width: 768px) {
            padding: 60px 0;
          }

          :deep(.el-empty__description) {
            color: var(--text-secondary);
          }
        }

        // 文章卡片 - 完全匹配参考文件
        .article-item {
          display: flex;
          gap: 20px;
          padding: 20px;
          background: transparent;
          border: 1px solid var(--border-regular);
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
          position: relative;
          overflow: hidden;

          // 手机端适配 - 纵向布局
          @media (max-width: 768px) {
            flex-direction: column;
            gap: 0;
            padding: 0;
            border-radius: 12px;
            overflow: hidden;
            width: 100%;

            .article-cover {
              width: 100%;
              height: 200px;
              border-radius: 0;
              flex-shrink: 0;
              overflow: hidden;

              .cover-image {
                width: 100%;
                height: 100%;
                border-radius: 0;
                object-fit: cover;
              }
            }

            .article-content {
              height: auto;
              min-height: auto;
              padding: 20px;
              width: 100%;
            }
          }

          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            height: 1px;
            background: linear-gradient(90deg, rgba(0, 102, 255, 0.5), transparent);
            opacity: 0;
            transition: opacity 0.25s ease;
          }

          &:hover::before {
            opacity: 1;
          }

          &:hover {
            border-color: var(--border-hover);
            background: rgba(0, 0, 0, 0.01);

            .article-cover img {
              transform: scale(1.03);
              filter: grayscale(0%);
            }
          }

          // 黑夜模式覆盖
          html.dark & {
            background: transparent;

            &:hover {
              background: rgba(255, 255, 255, 0.02);
              border-color: rgba(255, 255, 255, 0.15);
            }
          }

          // 文章封面
          .article-cover {
            width: 200px;
            height: 120px;
            background: var(--bg-subtle);
            border-radius: 4px;
            flex-shrink: 0;
            overflow: hidden;
            border: 1px solid var(--border-light);

            // 手机端适配
            @media (max-width: 768px) {
              width: 100%;
              height: 200px;
              border-radius: 0;
              border: none;
            }

            .loading-text {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              font-size: 12px;
              color: var(--text-muted);
              background: var(--bg-subtle);
            }

            .error {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background: var(--bg-subtle);

              .el-icon {
                font-size: 24px;
                color: var(--text-muted);
              }
            }

            .cover-image {
              width: 100%;
              height: 100%;
              object-fit: cover;
              transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
              filter: grayscale(15%);
            }
          }

          // 黑夜模式封面图片覆盖
          html.dark & {
            .cover-image {
              filter: grayscale(20%);
            }
          }

          // 文章内容
          .article-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
            min-width: 0;
            height: 120px;

            .article-author {
              display: flex;
              align-items: center;
              gap: 8px;
              font-size: 12px;
              font-weight: 500;
              color: var(--text-secondary);
              margin-bottom: 8px;
              cursor: pointer;

              .author-avatar {
                width: 22px;
                height: 22px;
                border-radius: 50%;
                overflow: hidden;
                background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
                border: 1px solid rgba(0, 0, 0, 0.1);

                .avatar-placeholder {
                  display: flex;
                  justify-content: center;
                  align-items: center;
                  width: 100%;
                  height: 100%;
                  background: linear-gradient(135deg, #f0f0f0 0%, #e0e0e0 100%);
                  color: var(--text-muted);

                  .el-icon {
                    font-size: 14px;
                    color: var(--text-muted);
                  }
                }

                // 黑夜模式头像占位符
                html.dark & {
                  background: rgba(255, 255, 255, 0.1);

                  .avatar-placeholder {
                    background: rgba(255, 255, 255, 0.1);
                  }
                }
              }
            }

            .article-title {
              font-size: 17px;
              font-weight: 600;
              line-height: 1.45;
              color: var(--text-primary);
              margin: 0 0 6px 0;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              letter-spacing: -0.01em;
              transition: color 0.2s ease;

              // 手机端适配
              @media (max-width: 768px) {
                font-size: 16px;
                line-height: 1.4;
              }

              &:hover {
                color: var(--accent-color);
              }
            }

            .article-description {
              font-size: 13px;
              color: var(--text-secondary);
              line-height: 1.5;
              margin: 0 0 8px 0;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;

              // 手机端适配
              @media (max-width: 768px) {
                font-size: 14px;
              }
            }

            // 文章元信息
            .article-meta {
              display: flex;
              align-items: center;
              gap: 20px;
              font-size: 11px;
              color: var(--text-muted);
              font-feature-settings: "tnum";
              font-weight: 300;

              // 手机端适配 - 元信息换行
              @media (max-width: 768px) {
                flex-wrap: wrap;
                gap: 8px;
                font-size: 10px;
              }

              span {
                display: flex;
                align-items: center;
                gap: 5px;
                transition: color 0.2s ease;
              }

              &:hover span {
                color: var(--text-regular);
              }

              .el-icon {
                font-size: 13px;
              }
            }
          }
        }

        // 加载更多
        .loading-more {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 40px 0 20px;

          span {
            font-size: 12px;
            color: var(--text-muted);
          }

          .loading-spinner {
            width: 18px;
            height: 18px;
            border: 2px solid var(--border-light);
            border-top-color: var(--accent-color);
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin-right: 10px;
          }
        }
      }
    }

    // ===== 侧边栏 =====
    .sidebar {
      display: flex;
      flex-direction: column;
      gap: 20px;

      // 手机端适配
      @media (max-width: 768px) {
        gap: 16px;
      }

      // 侧边栏卡片
      .sidebar-card {
        background: var(--bg-card);
        border: 1px solid var(--border-light);
        border-radius: 8px;
        padding: 20px;

        // 手机端适配
        @media (max-width: 768px) {
          padding: 20px;
        }

        .card-title {
          font-size: 13px;
          font-weight: 600;
          margin-bottom: 8px;
          color: var(--text-primary);
          display: flex;
          align-items: center;
          gap: 8px;
          letter-spacing: -0.01em;

          .icon {
            width: 16px;
            height: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
          }
        }

        .card-description {
          font-size: 11px;
          color: var(--text-muted);
          margin-bottom: 16px;
          line-height: 1.6;
        }

        // 骨架屏加载状态
        .hot-articles-loading {
          .hot-skeleton-item {
            padding: 12px;
            border-radius: 6px;
            background: var(--bg-subtle);
            margin-bottom: 8px;
          }
        }

        .featured-articles-loading {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .featured-skeleton-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px;
            border-radius: 6px;
            background: var(--bg-subtle);
            border: 1px solid var(--border-light);

            .featured-skeleton-cover {
              width: 76px;
              height: 50px;
              background: var(--bg-subtle);
              border-radius: 4px;
              flex-shrink: 0;
            }

            .featured-skeleton-content {
              flex: 1;
              display: flex;
              flex-direction: column;
              gap: 8px;

              .skeleton-title {
                width: 100%;
                height: 16px;
                background: var(--border-light);
                border-radius: 4px;
              }

              .skeleton-meta {
                width: 72%;
                height: 14px;
                background: var(--border-light);
                border-radius: 4px;
              }
            }
          }
        }

        // 空状态
        .hot-articles-empty,
        .featured-articles-empty {
          padding: 30px 0;
          text-align: center;
        }

        // ===== 热门文章列表 - 完全匹配参考文件 =====
        .hot-articles {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .hot-article-item {
            display: flex;
            gap: 10px;
            padding: 12px;
            background: var(--bg-subtle);
            border: 1px solid var(--border-light);
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.2s ease;

            &:hover {
              background: rgba(0, 0, 0, 0.03);
              border-color: rgba(0, 0, 0, 0.1);

              .hot-article-title {
                color: #0a0a0a;
              }
            }

            // 黑夜模式覆盖
            html.dark & {
              background: rgba(255, 255, 255, 0.02);

              &:hover {
                background: rgba(255, 255, 255, 0.04);

                .hot-article-title {
                  color: #ffffff;
                }
              }

              .hot-article-title {
                color: #aaa;

                &:hover {
                  color: #ffffff;
                }
              }
            }

            // 排名徽章
            .hot-article-rank {
              width: 24px;
              height: 24px;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 11px;
              font-weight: 700;
              background: var(--bg-subtle);
              border-radius: 4px;
              flex-shrink: 0;
              color: var(--text-secondary);
              font-feature-settings: "tnum";
            }

            // 前三名特殊样式
            &:nth-child(1) .hot-article-rank {
              color: var(--rank-1-color);
              background: var(--rank-1-bg);
            }

            &:nth-child(2) .hot-article-rank {
              color: var(--rank-2-color);
              background: var(--rank-2-bg);
            }

            &:nth-child(3) .hot-article-rank {
              color: var(--rank-3-color);
              background: var(--rank-3-bg);
            }

            .hot-article-content {
              flex: 1;
              min-width: 0;

              .hot-article-title {
                font-size: 13px;
                font-weight: 500;
                color: #666;
                line-height: 1.4;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                transition: color 0.2s ease;

                &:hover {
                  color: #0a0a0a;
                }
              }

              // 黑夜模式热门文章标题覆盖
              html.dark & {
                .hot-article-title {
                  color: #aaa;

                  &:hover {
                    color: #ffffff;
                  }
                }
              }

              .hot-article-meta {
                display: flex;
                gap: 12px;
                font-size: 10px;
                color: var(--text-muted);
                margin-top: 6px;
                font-feature-settings: "tnum";

                .hot-article-score {
                  color: var(--fire-color) !important;
                  font-weight: 600;
                }
              }
            }
          }
        }

        // ===== 会员精选列表 =====
        .featured-articles {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .featured-article-item {
            display: flex;
            gap: 10px;
            padding: 10px;
            background: var(--accent-soft);
            border: 1px solid var(--accent-border);
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.2s ease;

            &:hover {
              background: var(--accent-hover);
              border-color: var(--accent-color);

              .featured-article-title {
                color: var(--accent-color);
              }
            }

            .featured-article-cover {
              width: 76px;
              height: 50px;
              background: var(--bg-subtle);
              border-radius: 4px;
              flex-shrink: 0;
              overflow: hidden;
              border: 1px solid var(--border-light);

              .loading-text {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                font-size: 11px;
                color: var(--text-muted);
                background: var(--bg-subtle);
              }

              .error {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                background: var(--bg-subtle);

                .el-icon {
                  font-size: 18px;
                  color: var(--text-muted);
                }
              }

              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                filter: grayscale(10%);
              }
            }

            .featured-article-content {
              flex: 1;
              min-width: 0;

              .featured-article-title {
                font-size: 12px;
                font-weight: 500;
                color: #333;
                line-height: 1.4;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                transition: color 0.2s ease;

                &:hover {
                  color: var(--accent-color);
                }
              }

              // 黑夜模式会员精选标题覆盖
              html.dark & {
                .featured-article-title {
                  color: #ddd;
                }
              }

              .featured-article-meta {
                display: flex;
                gap: 8px;
                font-size: 10px;
                color: var(--text-muted);
                margin-top: 4px;
                font-feature-settings: "tnum";
              }
            }
          }
        }

        // 会员精选底部
        .featured-footer {
          margin-top: 16px;
          text-align: center;

          .featured-link {
            display: inline-block;
            padding: 10px 28px;
            background: transparent;
            border: 1px solid var(--accent-border);
            border-radius: 4px;
            font-size: 11px;
            font-weight: 600;
            color: var(--accent-color);
            cursor: pointer;
            transition: all 0.2s ease;
            letter-spacing: 0.05em;
            text-transform: uppercase;

            &:hover {
              background: var(--accent-hover);
              border-color: var(--accent-color);
              color: var(--text-primary);
            }
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
</style>
