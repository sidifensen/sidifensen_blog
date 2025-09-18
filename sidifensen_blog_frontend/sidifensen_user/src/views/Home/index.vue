<template>
  <div class="home-container">
    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 英雄区域 -->
        <section class="hero">
          <div class="hero-content">
            <h1 class="hero-title">欢迎来到sidifensen博客</h1>
            <p class="hero-subtitle">分享技术、经验和见解</p>
            <router-link to="/article" class="hero-btn">
              <span>浏览文章</span>
              <svg class="btn-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 7l5 5m0 0l-5 5m5-5H6" />
              </svg>
            </router-link>
          </div>
          <div class="hero-decoration">
            <div class="floating-shapes">
              <div class="shape shape-1"></div>
              <div class="shape shape-2"></div>
              <div class="shape shape-3"></div>
            </div>
          </div>
        </section>

        <!-- 最新文章 -->
        <section class="latest-articles">
          <h2>最新文章</h2>

          <!-- 加载状态 -->
          <div v-if="articleLoading" class="loading-container">
            <el-skeleton animated :count="6">
              <template #template>
                <div class="article-skeleton">
                  <el-skeleton-item variant="image" style="width: 100%; height: 200px; border-radius: 8px" />
                  <div class="skeleton-content">
                    <div class="skeleton-meta">
                      <el-skeleton-item variant="text" style="width: 80px; height: 20px" />
                      <el-skeleton-item variant="text" style="width: 100px; height: 20px" />
                    </div>
                    <el-skeleton-item variant="h3" style="width: 80%; height: 24px; margin: 10px 0" />
                    <el-skeleton-item variant="text" style="width: 100%; height: 16px" />
                    <el-skeleton-item variant="text" style="width: 90%; height: 16px" />
                    <el-skeleton-item variant="text" style="width: 70%; height: 16px" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- 空状态 -->
          <div v-else-if="articles.length === 0" class="empty-state">
            <el-empty description="暂无文章" />
          </div>

          <!-- 文章列表 -->
          <div v-else class="article-list">
            <article v-for="article in articles" :key="article.id" class="article-card">
              <div class="article-image" @click="goToArticle(article)">
                <el-image :src="article.coverUrl" class="article-cover" fit="cover">
                  <template #placeholder>
                    <div class="image-placeholder">
                      <el-icon class="is-loading"><Loading /></el-icon>
                    </div>
                  </template>
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div class="article-content">
                <div class="article-meta">
                  <span class="date">{{ formatDate(article.createTime) }}</span>
                </div>
                <h3>
                  <router-link :to="getArticleDetailRoute(article)" class="article-title-link">
                    {{ article.title }}
                  </router-link>
                </h3>
                <p class="article-description">{{ article.description || "暂无描述" }}</p>
                <div class="article-stats">
                  <span class="like-count">
                    <svg-icon name="like" width="14px" height="14px" margin-right="0px" :color="article.isLiked ? '#ffffff' : '#909399'" />
                    {{ article.likeCount || 0 }}
                  </span>
                  <span class="read-count">
                    <el-icon><View /></el-icon>
                    {{ article.readCount || 0 }}
                  </span>
                  <span class="collect-count">
                    <el-icon><Star /></el-icon>
                    {{ article.collectCount || 0 }}
                  </span>
                  <router-link :to="getArticleDetailRoute(article)" class="read-more">阅读更多</router-link>
                </div>
              </div>
            </article>
          </div>

          <!-- 加载更多指示器 -->
          <div v-if="articles.length > 0" class="load-more-indicator">
            <div v-if="loadingMore" class="loading-more">
              <div class="loading-spinner"></div>
              <span>正在加载更多文章...</span>
            </div>
            <div v-else-if="!hasMore" class="no-more">
              <el-divider>已加载全部文章</el-divider>
            </div>
          </div>
        </section>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-logo">
            <h3>sidifensen博客</h3>
            <p>分享技术、经验和见解</p>
          </div>
          <div class="footer-links">
            <div class="footer-column">
              <h4>导航</h4>
              <ul>
                <li><router-link to="/">首页</router-link></li>
                <li><router-link to="/articles">文章</router-link></li>
                <li><router-link to="/categories">分类</router-link></li>
                <li><router-link to="/tags">标签</router-link></li>
                <li><router-link to="/about">关于</router-link></li>
              </ul>
            </div>
            <div class="footer-column">
              <h4>联系我们</h4>
              <ul>
                <li>邮箱: contact@example.com</li>
                <li>微信: example_wechat</li>
                <li>微博: example_weibo</li>
              </ul>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; {{ currentYear }} sidifensen博客. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Loading, Picture, View, Star } from "@element-plus/icons-vue";
import { getAllArticleList } from "@/api/article";

// 路由
const router = useRouter();

// 响应式数据
const articles = ref([]); // 文章列表
const articleLoading = ref(false); // 文章加载状态
const loadingMore = ref(false); // 加载更多状态
const currentPage = ref(1); // 当前页码
const pageSize = ref(9); // 每页数据量
const total = ref(0); // 总数据量
const currentYear = ref(new Date().getFullYear());

// 计算属性
const hasMore = computed(() => {
  return articles.value.length < total.value;
});

// 获取文章列表
const fetchArticleList = async (reset = false) => {
  try {
    // 设置加载状态
    if (reset) {
      articleLoading.value = true;
      currentPage.value = 1;
    } else {
      loadingMore.value = true;
    }

    const res = await getAllArticleList(currentPage.value, pageSize.value);
    const newArticles = res.data.data.data || [];
    total.value = res.data.data.total || 0;

    if (reset) {
      articles.value = newArticles;
    } else {
      articles.value = [...articles.value, ...newArticles];
    }

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

// 节流函数
const throttle = (func, delay) => {
  let timeoutId;
  let lastExecTime = 0;
  return function (...args) {
    const currentTime = Date.now();

    if (currentTime - lastExecTime > delay) {
      func.apply(this, args);
      lastExecTime = currentTime;
    } else {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => {
        func.apply(this, args);
        lastExecTime = Date.now();
      }, delay - (currentTime - lastExecTime));
    }
  };
};

// 处理滚动事件 - 自动加载更多
const handleScroll = throttle(() => {
  // 如果正在加载或没有更多数据，则不处理
  if (loadingMore.value || !hasMore.value || articleLoading.value) {
    return;
  }

  // 获取页面滚动信息
  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight;

  // 当滚动到距离底部 300px 时开始加载更多
  if (scrollTop + windowHeight >= documentHeight - 300) {
    fetchArticleList();
  }
}, 200); // 200ms 节流

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 获取文章详情路由
const getArticleDetailRoute = (article) => {
  // 根据路由配置：/user/:userId/article/:articleId
  return `/user/${article.userId}/article/${article.id}`;
};

// 跳转到文章详情页
const goToArticle = (article) => {
  const route = getArticleDetailRoute(article);
  router.push(route);
};

// 组件挂载时获取文章列表并绑定滚动事件
onMounted(async () => {
  await fetchArticleList(true);
  // 等待DOM更新后绑定滚动事件
  await nextTick();
  window.addEventListener("scroll", handleScroll, { passive: true });
});

// 组件卸载时移除滚动事件监听
onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
// 全局样式变量
$primary-color: #3498db;
$secondary-color: #2ecc71;
$text-color: #333;
$light-gray: #f5f5f5;
$medium-gray: #e0e0e0;
$dark-gray: #666;
$white: #fff;
$shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

// 工具类
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.btn {
  display: inline-block;
  padding: 10px 20px;
  background-color: $primary-color;
  color: $white;
  border: none;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;

  &:hover {
    background-color: #2980b9;
    transform: translateY(-2px);
  }
}

// 导航栏样式
.navbar {
  background-color: $white;
  box-shadow: $shadow;
  position: sticky;
  top: 0;
  z-index: 100;

  .container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 70px;
  }

  .logo {
    font-size: 1.5rem;
    font-weight: 700;

    a {
      color: $primary-color;
      text-decoration: none;
    }
  }

  .nav-links {
    ul {
      display: flex;
      list-style: none;

      li {
        margin-left: 25px;

        a {
          color: $text-color;
          text-decoration: none;
          font-weight: 500;
          transition: color 0.3s ease;

          &:hover {
            color: $primary-color;
          }
        }
      }
    }
  }

  .mobile-menu-btn {
    display: none;
    background: none;
    border: none;
    font-size: 1.5rem;
    color: $text-color;
    cursor: pointer;
  }
}

// 移动端菜单
.mobile-menu {
  position: fixed;
  top: 70px;
  left: 0;
  right: 0;
  background-color: $white;
  box-shadow: $shadow;
  z-index: 99;

  ul {
    list-style: none;
    padding: 20px;

    li {
      margin-bottom: 15px;

      a {
        color: $text-color;
        text-decoration: none;
        font-size: 1.1rem;
        font-weight: 500;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }
}

// 主要内容区域样式
.main-content {
  padding: 40px 0;

  .hero {
    position: relative;
    text-align: center;
    padding: 80px 0;
    background: linear-gradient(135deg, #2196f3 0%, #21cbf3 100%);
    border-radius: 20px;
    margin-bottom: 40px;
    overflow: hidden;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);

    // 动态背景效果
    &::before {
      content: "";
      position: absolute;
      top: -50%;
      left: -50%;
      width: 200%;
      height: 200%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
      animation: heroFloat 8s ease-in-out infinite;
      pointer-events: none;
    }

    &::after {
      content: "";
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(255, 255, 255, 0.05);
      backdrop-filter: blur(1px);
    }

    .hero-content {
      position: relative;
      z-index: 3;
    }

    .hero-title {
      font-size: 3.2rem;
      font-weight: 700;
      margin-bottom: 20px;
      color: $white;
      text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      animation: slideInUp 1s ease-out;

      background: linear-gradient(45deg, $white, rgba(255, 255, 255, 0.8));
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .hero-subtitle {
      font-size: 1.3rem;
      color: rgba(255, 255, 255, 0.9);
      margin-bottom: 35px;
      font-weight: 400;
      animation: slideInUp 1s ease-out 0.2s both;
      text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    }

    .hero-btn {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      padding: 16px 32px;
      font-size: 1.1rem;
      font-weight: 600;
      background: rgba(255, 255, 255, 0.2);
      color: $white;
      border: 2px solid rgba(255, 255, 255, 0.3);
      border-radius: 50px;
      backdrop-filter: blur(10px);
      box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
      text-decoration: none;
      transition: all 0.3s ease;
      animation: slideInUp 1s ease-out 0.4s both;
      position: relative;
      overflow: hidden;

      &::before {
        content: "";
        position: absolute;
        top: 0;
        left: -100%;
        width: 100%;
        height: 100%;
        background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
        transition: left 0.5s ease;
      }

      .btn-icon {
        width: 20px;
        height: 20px;
        transition: transform 0.3s ease;
      }

      &:hover {
        background: rgba(255, 255, 255, 0.3);
        border-color: rgba(255, 255, 255, 0.5);
        transform: translateY(-3px);
        box-shadow: 0 12px 35px rgba(0, 0, 0, 0.15);

        .btn-icon {
          transform: translateX(4px);
        }

        &::before {
          left: 100%;
        }
      }
    }

    // 装饰元素
    .hero-decoration {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      z-index: 1;
      pointer-events: none;

      .floating-shapes {
        position: relative;
        width: 100%;
        height: 100%;

        .shape {
          position: absolute;
          border-radius: 50%;
          background: rgba(255, 255, 255, 0.1);
          animation: shapeFloat 6s ease-in-out infinite;

          &.shape-1 {
            width: 60px;
            height: 60px;
            top: 20%;
            left: 15%;
            animation-delay: 0s;
          }

          &.shape-2 {
            width: 80px;
            height: 80px;
            top: 60%;
            right: 20%;
            animation-delay: -2s;
          }

          &.shape-3 {
            width: 40px;
            height: 40px;
            top: 30%;
            right: 15%;
            animation-delay: -4s;
          }
        }
      }
    }
  }

  .latest-articles {
    margin-bottom: 40px;

    h2 {
      font-size: 1.8rem;
      margin-bottom: 25px;
      color: $text-color;
      position: relative;
      padding-bottom: 10px;

      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        left: 0;
        width: 80px;
        height: 3px;
        background-color: $primary-color;
      }
    }

    // 加载状态样式
    .loading-container {
      margin-bottom: 30px;

      .article-skeleton {
        background-color: $white;
        border-radius: 8px;
        padding: 20px;
        margin-bottom: 30px;
        box-shadow: $shadow;

        .skeleton-content {
          margin-top: 15px;

          .skeleton-meta {
            display: flex;
            justify-content: space-between;
            margin-bottom: 10px;
          }
        }
      }
    }

    // 空状态样式
    .empty-state {
      padding: 60px 0;
      text-align: center;
      background-color: $white;
      border-radius: 8px;
      box-shadow: $shadow;
    }

    // 文章列表网格布局
    .article-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 30px;
      margin-bottom: 30px;
    }

    // 文章卡片样式
    .article-card {
      background-color: $white;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: $shadow;
      transition: all 0.3s ease;
      border: 1px solid transparent;
      display: flex;
      flex-direction: column; // 垂直布局
      height: 100%; // 占满容器高度

      &:hover {
        transform: translateY(-8px);
        box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
        border-color: rgba(52, 152, 219, 0.2);
      }

      // 文章封面图片区域
      .article-image {
        height: 200px;
        overflow: hidden;
        cursor: pointer;
        position: relative;

        .article-cover {
          width: 100%;
          height: 100%;
          transition: transform 0.5s ease;

          &:hover {
            transform: scale(1.08);
          }
        }

        // 图片占位符样式
        .image-placeholder {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          background-color: $light-gray;
          color: $dark-gray;

          .el-icon {
            font-size: 24px;
          }
        }

        // 图片错误样式
        .image-error {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          background-color: $light-gray;
          color: $dark-gray;

          .el-icon {
            font-size: 32px;
          }
        }
      }

      // 文章内容区域
      .article-content {
        padding: 24px;
        display: flex;
        flex-direction: column;
        flex: 1; // 占用剩余空间

        // 文章元信息（分类和日期）
        .article-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;
          font-size: 0.85rem;

          .date {
            color: $dark-gray;
            font-weight: 400;
          }
        }

        // 文章标题
        h3 {
          margin-bottom: 12px;
          font-size: 1.25rem;
          line-height: 1.4;

          .article-title-link {
            color: $text-color;
            text-decoration: none;
            transition: color 0.3s ease;
            font-weight: 600;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;

            &:hover {
              color: $primary-color;
            }
          }
        }

        // 文章描述
        .article-description {
          color: $dark-gray;
          margin-bottom: 16px;
          line-height: 1.6;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
          font-size: 0.95rem;
          flex: 1; // 占用剩余空间，推动统计栏到底部
        }

        // 文章统计信息和操作
        .article-stats {
          display: flex;
          align-items: center;
          justify-content: space-between;
          padding-top: 12px;
          border-top: 1px solid #f0f0f0;

          .read-count,
          .like-count,
          .collect-count {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 0.85rem;
            color: $dark-gray;

            .el-icon {
              font-size: 14px;
            }
          }

          .read-more {
            color: $primary-color;
            text-decoration: none;
            font-weight: 500;
            font-size: 0.9rem;
            transition: all 0.3s ease;
            padding: 4px 8px;
            border-radius: 6px;

            &:hover {
              color: #2980b9;
              background-color: rgba(52, 152, 219, 0.1);
            }
          }
        }
      }
    }

    // 加载更多指示器区域
    .load-more-indicator {
      text-align: center;
      margin-top: 40px;
      padding: 20px 0;

      // 加载中状态
      .loading-more {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        color: $primary-color;
        font-size: 0.95rem;
        padding: 20px;

        .loading-spinner {
          width: 20px;
          height: 20px;
          border: 2px solid rgba(52, 152, 219, 0.2);
          border-top: 2px solid $primary-color;
          border-radius: 50%;
          animation: spin 1s linear infinite;
        }

        span {
          font-weight: 500;
        }
      }

      // 没有更多数据状态
      .no-more {
        margin: 20px 0;

        .el-divider {
          color: $dark-gray;
          font-size: 0.9rem;
          font-weight: 400;
        }
      }
    }
  }
}

// 页脚样式
.footer {
  background-color: #2c3e50;
  color: $white;
  padding: 40px 0 20px;

  .footer-content {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30px;

    .footer-logo {
      max-width: 300px;

      h3 {
        font-size: 1.5rem;
        margin-bottom: 10px;
      }

      p {
        color: #bdc3c7;
      }
    }

    .footer-links {
      display: flex;
      gap: 40px;

      .footer-column {
        h4 {
          font-size: 1.1rem;
          margin-bottom: 15px;
          color: #ecf0f1;
        }

        ul {
          list-style: none;

          li {
            margin-bottom: 10px;

            a {
              color: #bdc3c7;
              text-decoration: none;
              transition: color 0.3s ease;

              &:hover {
                color: $white;
              }
            }
          }
        }
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid #34495e;
    color: #bdc3c7;
    font-size: 0.9rem;
  }
}

// 响应式样式
@media screen and (max-width: 992px) {
  .navbar {
    .nav-links {
      display: none;
    }

    .mobile-menu-btn {
      display: block;
    }
  }

  .main-content {
    .latest-articles {
      .article-list {
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      }
    }
  }

  .footer {
    .footer-content {
      flex-direction: column;
      gap: 30px;

      .footer-links {
        flex-direction: column;
        gap: 20px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .container {
    padding: 0 15px;
  }

  .main-content {
    padding: 20px 0;

    .hero {
      padding: 50px 0;
      margin-bottom: 30px;
      border-radius: 15px;

      .hero-title {
        font-size: 2.2rem;
      }

      .hero-subtitle {
        font-size: 1.1rem;
        margin-bottom: 25px;
      }

      .hero-btn {
        padding: 12px 24px;
        font-size: 1rem;
      }

      // 移动端隐藏装饰元素
      .hero-decoration {
        display: none;
      }
    }
  }

  .latest-articles {
    .article-list {
      grid-template-columns: 1fr;
      gap: 20px;
    }

    .article-card {
      .article-content {
        padding: 18px;

        .article-stats {
          flex-direction: column;
          align-items: flex-start;
          gap: 10px;

          .read-more {
            align-self: flex-end;
          }
        }
      }
    }

    .loading-container {
      .article-skeleton {
        padding: 15px;
        margin-bottom: 20px;
      }
    }
  }
}

// 超小屏幕优化
@media screen and (max-width: 480px) {
  .main-content {
    .hero {
      padding: 40px 0;
      border-radius: 12px;

      .hero-title {
        font-size: 1.8rem;
      }

      .hero-subtitle {
        font-size: 1rem;
        margin-bottom: 20px;
      }

      .hero-btn {
        padding: 10px 20px;
        font-size: 0.95rem;
        gap: 6px;

        .btn-icon {
          width: 16px;
          height: 16px;
        }
      }
    }

    .latest-articles {
      .article-list {
        gap: 15px;
      }

      .article-card {
        .article-image {
          height: 160px;
        }

        .article-content {
          padding: 15px;

          .article-meta {
            .category {
              font-size: 0.75rem;
              padding: 3px 8px;
            }
          }

          h3 {
            font-size: 1.1rem;
          }

          .article-description {
            font-size: 0.9rem;
            -webkit-line-clamp: 2;
            line-clamp: 2;
          }
        }
      }
    }
  }
}

// 动画定义
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 英雄区域动画
@keyframes slideInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes heroFloat {
  0%,
  100% {
    transform: translateY(0px) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes shapeFloat {
  0%,
  100% {
    transform: translateY(0px) scale(1);
    opacity: 0.7;
  }
  50% {
    transform: translateY(-15px) scale(1.1);
    opacity: 1;
  }
}
</style>
