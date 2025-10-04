<template>
  <div class="home-container">
    <!-- 全屏沉浸式首屏 -->
    <section class="fullscreen-hero">
      <div class="hero-background"></div>
      <div class="hero-content">
        <h1 class="hero-title">sidifensen-blog</h1>
        <p class="hero-subtitle">分享技术、经验和见解</p>
      </div>
      <div class="scroll-indicator" @click="scrollToContent">
        <div class="mouse">
          <div class="wheel"></div>
        </div>
        <div class="arrow">
          <span></span>
          <span></span>
          <span></span>
        </div>
      </div>
    </section>

    <!-- 主要内容区域 -->
    <main class="main-content" ref="contentSection">
      <div class="container">
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
      <div class="footer-wave">
        <svg viewBox="0 0 1200 120" preserveAspectRatio="none">
          <path d="M321.39,56.44c58-10.79,114.16-30.13,172-41.86,82.39-16.72,168.19-17.73,250.45-.39C823.78,31,906.67,72,985.66,92.83c70.05,18.48,146.53,26.09,214.34,3V0H0V27.35A600.21,600.21,0,0,0,321.39,56.44Z"></path>
        </svg>
      </div>
      <div class="container">
        <div class="footer-content">
          <!-- 左侧品牌信息 -->
          <div class="footer-brand">
            <h3 class="brand-title">
              <span class="brand-icon">✨</span>
              sidifensen博客
            </h3>
            <p class="brand-description">分享技术、经验和见解<br />用代码改变世界</p>
            <div class="social-links">
              <a href="https://github.com/sidifensen" class="social-link" title="GitHub">
                <svg-icon color="#ffffff" name="github" width="20px" height="20px" />
              </a>
              <a href="https://gitee.com/sidifensen" class="social-link" title="Gitee">
                <svg-icon color="#ffffff" name="gitee" width="20px" height="20px" />
              </a>
            </div>
          </div>

          <!-- 中间导航链接 -->
          <div class="footer-links">
            <div class="footer-column">
              <h4 class="column-title">
                <el-icon><Compass /></el-icon>
                快速导航
              </h4>
              <ul class="links-list">
                <li><router-link to="/">首页</router-link></li>
                <li><router-link to="/article">文章</router-link></li>
                <li><router-link to="/link">友链</router-link></li>
              </ul>
            </div>

            <div class="footer-column">
              <h4 class="column-title">
                <el-icon><Phone /></el-icon>
                联系方式
              </h4>
              <ul class="links-list contact-list">
                <li>
                  <el-icon><Message /></el-icon>
                  <span>contact@example.com</span>
                </li>
                <li>
                  <el-icon><ChatDotSquare /></el-icon>
                  <span>example_wechat</span>
                </li>
                <li>
                  <el-icon><Location /></el-icon>
                  <span>中国·北京</span>
                </li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 底部版权信息 -->
        <div class="footer-bottom">
          <div class="footer-divider"></div>
          <div class="footer-bottom-content">
            <div class="footer-left">
              <p class="copyright">&copy; {{ currentYear }} <strong>sidifensen博客</strong>. All Rights Reserved.</p>
              <p class="beian">
                <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">粤ICP备xxxxxxxx号</a>
              </p>
            </div>
            <div class="footer-meta">
              <span>Made with ❤️ by sidifensen</span>
              <span class="separator">|</span>
              <a href="#">隐私政策</a>
              <span class="separator">|</span>
              <a href="#">使用条款</a>
            </div>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, nextTick } from "vue";
import { useRouter } from "vue-router";
import { Loading, Picture, View, Star, ChatDotSquare, Message, Notification, Compass, Phone, Location } from "@element-plus/icons-vue";
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
const contentSection = ref(null); // 内容区域引用

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

// 滚动到内容区域
const scrollToContent = () => {
  if (contentSection.value) {
    contentSection.value.scrollIntoView({ behavior: "smooth" });
  }
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

// 全屏沉浸式首屏
.fullscreen-hero {
  position: relative;
  width: 100%;
  height: 100vh;
  margin-top: -48px; // 向上延伸覆盖导航栏高度
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  overflow: hidden;
  padding-top: calc(25vh + 48px); // 从顶部开始25%的视口高度加上导航栏高度，让内容更居中

  // 背景图片层
  .hero-background {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: url("@/assets/img/th (2).jpg") no-repeat center center / cover fixed;
    z-index: 1;

    // 底部渐变过渡效果
    &::after {
      content: "";
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 150px; // 渐变高度
      background: linear-gradient(to bottom, transparent 0%, color-mix(in srgb, var(--el-bg-color-page) 30%, transparent) 30%, color-mix(in srgb, var(--el-bg-color-page) 70%, transparent) 60%, color-mix(in srgb, var(--el-bg-color-page) 95%, transparent) 90%, var(--el-bg-color-page) 100%);
      pointer-events: none;
    }
  }

  // 内容层
  .hero-content {
    position: relative;
    z-index: 2;
    text-align: center;
    color: $white;
    animation: fadeInUp 1.2s ease-out;

    .hero-title {
      font-size: 5rem;
      font-weight: 700;
      margin-bottom: 20px;
      color: $white;
      text-shadow: 0 4px 30px rgba(0, 0, 0, 0.8), 0 2px 10px rgba(0, 0, 0, 0.6);
      letter-spacing: 2px;
      animation: slideInUp 1s ease-out;
    }

    .hero-subtitle {
      font-size: 1.5rem;
      font-weight: 300;
      color: rgba(255, 255, 255, 0.98);
      text-shadow: 0 2px 20px rgba(0, 0, 0, 0.7), 0 1px 5px rgba(0, 0, 0, 0.5);
      animation: slideInUp 1s ease-out 0.2s both;
    }
  }

  // 滚动指示器
  .scroll-indicator {
    position: absolute;
    bottom: 80px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 2;
    cursor: pointer;
    animation: fadeInUp 1s ease-out 0.6s both;
    transition: all 0.3s ease;
    filter: drop-shadow(0 2px 10px rgba(0, 0, 0, 0.6));

    &:hover {
      transform: translateX(-50%) scale(1.1);
    }

    // 鼠标滚轮样式
    .mouse {
      width: 26px;
      height: 40px;
      border: 2px solid rgba(255, 255, 255, 0.95);
      border-radius: 13px;
      position: relative;
      margin: 0 auto 10px;

      .wheel {
        width: 4px;
        height: 8px;
        background: rgba(255, 255, 255, 0.95);
        border-radius: 2px;
        position: absolute;
        top: 8px;
        left: 50%;
        transform: translateX(-50%);
        animation: scrollWheel 1.5s ease-in-out infinite;
      }
    }

    // 箭头样式
    .arrow {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 3px;

      span {
        display: block;
        width: 12px;
        height: 12px;
        border-bottom: 2px solid rgba(255, 255, 255, 0.95);
        border-right: 2px solid rgba(255, 255, 255, 0.95);
        transform: rotate(45deg);
        animation: arrowBounce 1.5s ease-in-out infinite;

        &:nth-child(2) {
          animation-delay: 0.15s;
        }

        &:nth-child(3) {
          animation-delay: 0.3s;
        }
      }
    }
  }
}

// 主要内容区域样式
.main-content {
  padding: 60px 0;
  background-color: var(--el-bg-color-page);

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
      background-color: var(--el-bg-color);
      border-radius: 12px;
      overflow: hidden;
      box-shadow: $shadow;
      transition: all 0.3s ease;
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
            color: var(--el-text-color);
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
          border-top: 1px solid var(--el-border-color);

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
        background: transparent;

        .el-divider {
          color: var(--el-border-color);
          font-size: 0.9rem;
          font-weight: 400;

          :deep(.el-divider__text) {
            background-color: transparent;
          }
        }
      }
    }
  }
}

// 页脚样式
.footer {
  position: relative;
  background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #1e3c72 100%);
  color: $white;
  padding: 0;
  overflow: hidden;

  // 顶部波浪效果
  .footer-wave {
    position: absolute;
    top: -1px;
    left: 0;
    width: 100%;
    height: 60px;
    overflow: hidden;
    line-height: 0;

    svg {
      position: relative;
      display: block;
      width: calc(100% + 1.3px);
      height: 60px;
      fill: var(--el-bg-color-page);
    }
  }

  .container {
    padding-top: 80px;
    padding-bottom: 20px;
  }

  .footer-content {
    display: grid;
    grid-template-columns: 1.5fr 2fr;
    gap: 60px;
    margin-bottom: 40px;
    padding: 0 20px;

    // 左侧品牌信息区域
    .footer-brand {
      .brand-title {
        font-size: 1.8rem;
        font-weight: 700;
        margin-bottom: 16px;
        color: $white;
        display: flex;
        align-items: center;
        gap: 8px;

        .brand-icon {
          font-size: 2rem;
          animation: sparkle 2s ease-in-out infinite;
        }
      }

      .brand-description {
        color: rgba(255, 255, 255, 0.85);
        font-size: 1rem;
        line-height: 1.8;
        margin-bottom: 24px;
        font-weight: 300;
      }

      // 社交链接
      .social-links {
        display: flex;
        gap: 12px;

        .social-link {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 42px;
          height: 42px;
          background: rgba(255, 255, 255, 0.1);
          border-radius: 50%;
          color: $white;
          text-decoration: none;
          transition: all 0.3s ease;
          backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.2);

          .el-icon {
            font-size: 18px;
          }

          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateY(-3px);
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
          }
        }
      }
    }

    // 导航链接区域
    .footer-links {
      display: grid;
      grid-template-columns: repeat(2, 1fr);
      gap: 40px;

      .footer-column {
        .column-title {
          font-size: 1.1rem;
          font-weight: 600;
          margin-bottom: 20px;
          color: $white;
          display: flex;
          align-items: center;
          gap: 8px;
          padding-bottom: 12px;
          border-bottom: 2px solid rgba(255, 255, 255, 0.2);

          .el-icon {
            font-size: 18px;
          }
        }

        .links-list {
          list-style: none;
          padding: 0;
          margin: 0;

          li {
            margin-bottom: 12px;

            a {
              color: rgba(255, 255, 255, 0.8);
              text-decoration: none;
              font-size: 0.95rem;
              transition: all 0.3s ease;
              display: inline-block;
              position: relative;

              &::before {
                content: "";
                position: absolute;
                bottom: -2px;
                left: 0;
                width: 0;
                height: 2px;
                background: $white;
                transition: width 0.3s ease;
              }

              &:hover {
                color: $white;
                padding-left: 8px;

                &::before {
                  width: 100%;
                }
              }
            }
          }

          // 联系方式列表特殊样式
          &.contact-list {
            li {
              display: flex;
              align-items: center;
              gap: 10px;
              color: rgba(255, 255, 255, 0.8);
              font-size: 0.95rem;

              .el-icon {
                font-size: 16px;
                color: rgba(255, 255, 255, 0.6);
              }
            }
          }
        }
      }
    }
  }

  // 底部区域
  .footer-bottom {
    padding: 0 20px;

    .footer-divider {
      height: 1px;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3) 20%, rgba(255, 255, 255, 0.3) 80%, transparent);
      margin-bottom: 24px;
    }

    .footer-bottom-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 20px;
      padding-bottom: 30px;

      .footer-left {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .copyright {
          color: rgba(255, 255, 255, 0.7);
          font-size: 0.9rem;
          margin: 0;

          strong {
            color: $white;
            font-weight: 600;
          }
        }

        .beian {
          margin: 0;
          font-size: 0.85rem;

          a {
            color: rgba(255, 255, 255, 0.6);
            text-decoration: none;
            transition: color 0.3s ease;

            &:hover {
              color: rgba(255, 255, 255, 0.9);
            }
          }
        }
      }

      .footer-meta {
        display: flex;
        align-items: center;
        gap: 12px;
        font-size: 0.85rem;
        color: rgba(255, 255, 255, 0.6);

        .separator {
          color: rgba(255, 255, 255, 0.3);
        }

        a {
          color: rgba(255, 255, 255, 0.7);
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
    .container {
      padding-top: 60px;
    }

    .footer-content {
      grid-template-columns: 1fr;
      gap: 40px;

      .footer-links {
        grid-template-columns: 1fr;
        gap: 30px;
      }
    }

    .footer-bottom {
      .footer-bottom-content {
        flex-direction: column;
        text-align: center;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .container {
    padding: 0 15px;
  }

  // 全屏首屏响应式
  .fullscreen-hero {
    padding-top: calc(20vh + 48px); // 移动端稍微降低一点，但仍需考虑导航栏高度

    .hero-background {
      background-attachment: scroll; // 移动端优化
    }

    .hero-content {
      .hero-title {
        font-size: 3rem;
        letter-spacing: 1px;
      }

      .hero-subtitle {
        font-size: 1.2rem;
      }
    }

    .scroll-indicator {
      bottom: 60px;

      .mouse {
        width: 22px;
        height: 35px;
      }
    }
  }

  .main-content {
    padding: 40px 0;
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

  // 页脚响应式
  .footer {
    .container {
      padding-top: 50px;
    }

    .footer-wave {
      height: 40px;

      svg {
        height: 40px;
      }
    }

    .footer-content {
      gap: 30px;

      .footer-brand {
        .brand-title {
          font-size: 1.5rem;

          .brand-icon {
            font-size: 1.5rem;
          }
        }

        .brand-description {
          font-size: 0.9rem;
        }

        .social-links {
          gap: 10px;

          .social-link {
            width: 38px;
            height: 38px;

            .el-icon {
              font-size: 16px;
            }
          }
        }
      }

      .footer-links {
        grid-template-columns: repeat(2, 1fr);
        gap: 24px;

        .footer-column {
          .column-title {
            font-size: 1rem;
          }
        }
      }
    }

    .footer-bottom {
      .footer-bottom-content {
        .footer-meta {
          flex-direction: column;
          gap: 8px;

          .separator {
            display: none;
          }
        }
      }
    }
  }
}

// 超小屏幕优化
@media screen and (max-width: 480px) {
  .fullscreen-hero {
    padding-top: calc(22vh + 48px); // 超小屏幕也调整位置，考虑导航栏高度

    .hero-content {
      padding: 0 20px;

      .hero-title {
        font-size: 2.2rem;
        letter-spacing: 0.5px;
      }

      .hero-subtitle {
        font-size: 1rem;
      }
    }

    .scroll-indicator {
      bottom: 80px;

      .mouse {
        width: 20px;
        height: 32px;

        .wheel {
          width: 3px;
          height: 6px;
        }
      }

      .arrow {
        span {
          width: 10px;
          height: 10px;
        }
      }
    }
  }

  .main-content {
    padding: 30px 0;

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

// 闪烁动画
@keyframes sparkle {
  0%,
  100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.7;
    transform: scale(1.1);
  }
}

// 淡入上升动画
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
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

// 滚轮动画
@keyframes scrollWheel {
  0% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
  50% {
    opacity: 0.5;
    transform: translateX(-50%) translateY(12px);
  }
  100% {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

// 箭头弹跳动画
@keyframes arrowBounce {
  0%,
  100% {
    opacity: 0.3;
    transform: rotate(45deg) translateY(0);
  }
  50% {
    opacity: 1;
    transform: rotate(45deg) translateY(5px);
  }
}
</style>
