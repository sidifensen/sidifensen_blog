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
        <!-- 内容网格布局 -->
        <div class="content-grid">
          <!-- 最新文章 -->
          <section class="latest-articles">
            <h2>最新文章</h2>

            <!-- 文章内容包装容器 -->
            <div class="articles-wrapper">
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
                        <svg-icon name="like" width="13px" height="13px" margin-right="1px" :color="article.isLiked ? '#ffffff' : '#666666'" />
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
            </div>

            <!-- 手机端项目链接（仅在手机端显示） -->
            <div class="mobile-project-links">
              <ProjectLinks />
            </div>
          </section>

          <!-- 右侧边栏 -->
          <aside class="sidebar">
            <ProjectLinks />
          </aside>
        </div>
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
                <li><router-link to="/album">相册</router-link></li>
                <li><router-link to="/link">友链</router-link></li>
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
                <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">粤ICP备2024324512号-2</a>
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
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Loading, Picture, View, Star, Compass } from "@element-plus/icons-vue";
import { getAllArticleList } from "@/api/article";
import ProjectLinks from "@/components/ProjectLinks.vue";

// 路由
const router = useRouter();

// 响应式数据
const articles = ref([]); // 文章列表
const articleLoading = ref(false); // 文章加载状态
const currentYear = ref(new Date().getFullYear());
const contentSection = ref(null); // 内容区域引用

// 获取文章列表 - 固定只加载12条
const fetchArticleList = async (reset = false) => {
  // 只在初始化时加载一次
  if (!reset || articles.value.length > 0) {
    return;
  }

  try {
    articleLoading.value = true;
    const res = await getAllArticleList(1, 12);
    articles.value = res.data.data.data || [];
  } catch (error) {
    ElMessage.error("获取文章列表失败");
    console.error("获取文章列表失败:", error);
  } finally {
    articleLoading.value = false;
  }
};

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

// 组件挂载时获取文章列表（固定12条）
onMounted(async () => {
  await fetchArticleList(true);
  // 不再需要滚动监听，因为固定只加载12条
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

// 工具类（全局复用）
.container {
  width: 100%;
  max-width: 1500px;
  margin: 0 auto;
  padding: 0 20px;
}

// 首页容器（对应 template 最外层）
.home-container {
  position: relative;
  z-index: 1;

  // 添加固定背景覆盖层，确保完全覆盖 layout 背景
  &::after {
    content: "";
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: var(--el-bg-color-page);
    z-index: -1;
    pointer-events: none;
  }

  // 第一部分：全屏沉浸式首屏 section.fullscreen-hero
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
    z-index: 2; // 确保首屏在背景覆盖层之上

    // 背景图片层 .hero-background
    .hero-background {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: url("@/assets/img/th.jpg") no-repeat center center / cover fixed;
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

    // 内容层 .hero-content
    .hero-content {
      position: relative;
      z-index: 2;
      text-align: center;
      color: $white;
      animation: fadeInUp 1.2s ease-out;

      // 主标题 h1.hero-title
      .hero-title {
        font-size: 5rem;
        font-weight: 700;
        margin-bottom: 20px;
        color: $white;
        text-shadow: 0 4px 30px rgba(0, 0, 0, 0.8), 0 2px 10px rgba(0, 0, 0, 0.6);
        letter-spacing: 2px;
        animation: slideInUp 1s ease-out;
      }

      // 副标题 p.hero-subtitle
      .hero-subtitle {
        font-size: 1.5rem;
        font-weight: 300;
        color: rgba(255, 255, 255, 0.98);
        text-shadow: 0 2px 20px rgba(0, 0, 0, 0.7), 0 1px 5px rgba(0, 0, 0, 0.5);
        animation: slideInUp 1s ease-out 0.2s both;
      }
    }

    // 滚动指示器 .scroll-indicator
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

      // 鼠标滚轮 .mouse
      .mouse {
        width: 26px;
        height: 40px;
        border: 2px solid rgba(255, 255, 255, 0.95);
        border-radius: 13px;
        position: relative;
        margin: 0 auto 10px;

        // 滚轮 .wheel
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

      // 箭头 .arrow
      .arrow {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 3px;

        // 箭头片段 span
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

  // 第二部分：主要内容区域 main.main-content
  .main-content {
    padding: 60px 0;
    background-color: var(--el-bg-color-page);
    position: relative;
    z-index: 2; // 确保内容区域在背景覆盖层之上
    min-height: 100vh; // 确保至少占满整个视口高度

    // .container 已在全局定义

    // 内容网格布局 .content-grid
    .content-grid {
      display: grid;
      grid-template-columns: 1fr 320px;
      gap: 30px;
      align-items: start;

      // 响应式：平板及以下单列布局
      @media screen and (max-width: 992px) {
        grid-template-columns: 1fr;
      }
    }

    // 最新文章区域 section.latest-articles
    .latest-articles {
      background-color: var(--el-bg-color-page);
      border-radius: 12px;
      padding: 30px;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
      position: relative;
      z-index: 2; // 确保在背景之上
      min-height: 85vh; // 确保整个区域有足够的高度

      // 标题 h2
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

      // 文章内容包装容器 .articles-wrapper
      .articles-wrapper {
        background-color: transparent; // 使用透明背景，因为父容器已有背景
        border-radius: 8px;
        padding: 0; // 移除内边距，因为父容器已有 padding
        box-shadow: none; // 移除阴影，因为父容器已有阴影
        min-height: 70vh; // 确保有足够的高度
        position: relative;
        z-index: 2; // 确保在背景之上
      }

      // 加载状态容器 .loading-container
      .loading-container {
        margin-bottom: 0; // 移除底部边距，因为父容器已有 padding
        background-color: transparent; // 使用透明背景
        border-radius: 0; // 移除圆角
        padding: 0; // 移除内边距
        box-shadow: none; // 移除阴影

        // 文章骨架屏 .article-skeleton
        .article-skeleton {
          background-color: $white;
          border-radius: 8px;
          padding: 20px;
          margin-bottom: 30px;
          box-shadow: $shadow;

          // 骨架屏内容 .skeleton-content
          .skeleton-content {
            margin-top: 15px;

            // 骨架屏元信息 .skeleton-meta
            .skeleton-meta {
              display: flex;
              justify-content: space-between;
              margin-bottom: 10px;
            }
          }
        }
      }

      // 空状态 .empty-state
      .empty-state {
        padding: 60px 0;
        text-align: center;
        margin-bottom: 0; // 移除底部边距，因为父容器已有 padding
        background-color: transparent; // 使用透明背景
        border-radius: 0; // 移除圆角
        box-shadow: none; // 移除阴影
      }

      // 文章列表 .article-list
      .article-list {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 30px;
        margin-bottom: 0; // 移除底部边距，因为父容器已有 padding

        // 响应式：平板及以下自适应列数
        @media screen and (max-width: 992px) {
          grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        }

        // 文章卡片 article.article-card
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

          // 文章图片区域 .article-image
          .article-image {
            height: 200px;
            overflow: hidden;
            cursor: pointer;
            position: relative;

            // 文章封面 .article-cover
            .article-cover {
              width: 100%;
              height: 100%;
              transition: transform 0.5s ease;

              &:hover {
                transform: scale(1.08);
              }
            }

            // 图片占位符 .image-placeholder
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

            // 图片错误 .image-error
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

          // 文章内容区域 .article-content
          .article-content {
            padding: 17px;
            display: flex;
            flex-direction: column;
            flex: 1; // 占用剩余空间

            // 文章元信息 .article-meta
            .article-meta {
              display: flex;
              justify-content: space-between;
              align-items: center;
              font-size: 0.85rem;
              height: 1.36rem; // 固定高度：0.85rem * 1.6 = 1.36rem

              // 日期 .date
              .date {
                color: $dark-gray;
                font-weight: 400;
              }
            }

            // 文章标题 h3
            h3 {
              margin: 0;
              font-size: 1.25rem;
              line-height: 1.4;
              height: 3.5rem; // 固定高度：1.25rem * 1.4 * 2行 = 3.5rem
              display: flex;
              align-items: flex-start;

              // 标题链接 .article-title-link
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

            // 文章描述 .article-description
            .article-description {
              margin: 5px 0 6px 0;
              color: $dark-gray;
              line-height: 1.6;
              height: 3.04rem; // 固定高度：0.95rem * 1.6 * 2行 = 3.04rem
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              text-overflow: ellipsis;
              font-size: 0.95rem;
            }

            // 文章统计信息 .article-stats
            .article-stats {
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding-top: 12px;
              border-top: 1px solid var(--el-border-color);
              margin-top: auto; // 推到底部

              // 阅读量、点赞数、收藏数
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

              // 点赞图标单独处理，确保垂直居中
              .like-count {
                :deep(.svg-icon) {
                  display: flex;
                  align-items: center;
                }
              }

              // 阅读更多链接 .read-more
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
      }

      // 手机端项目链接 .mobile-project-links
      .mobile-project-links {
        display: none; // 默认隐藏（桌面端）
        margin-top: 30px;

        // 响应式：平板及以下显示
        @media screen and (max-width: 992px) {
          display: block;
        }
      }
    }

    // 右侧边栏 aside.sidebar
    .sidebar {
      margin-top: 95px; // 向下移动 20px
      // 响应式：平板及以下隐藏侧边栏
      @media screen and (max-width: 992px) {
        display: none;
      }
    }
  }

  // 第三部分：页脚 footer.footer
  .footer {
    position: relative;
    background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #1e3c72 100%);
    color: $white;
    padding: 0;
    overflow: hidden;
    z-index: 2; // 确保页脚在背景覆盖层之上

    // 顶部波浪效果 .footer-wave
    .footer-wave {
      position: absolute;
      top: -1px;
      left: 0;
      width: 100%;
      height: 60px;
      overflow: hidden;
      line-height: 0;

      // SVG 波浪
      svg {
        position: relative;
        display: block;
        width: calc(100% + 1.3px);
        height: 60px;
        fill: var(--el-bg-color-page);
      }
    }

    // 容器 .container
    .container {
      padding-top: 80px;
      padding-bottom: 20px;

      // 页脚内容区域 .footer-content
      .footer-content {
        display: grid;
        grid-template-columns: 1.5fr 2fr;
        gap: 60px;
        margin-bottom: 10px;
        padding: 0 20px;

        // 品牌信息区域 .footer-brand
        .footer-brand {
          // 品牌标题 .brand-title
          .brand-title {
            font-size: 1.8rem;
            font-weight: 700;
            margin-bottom: 16px;
            color: $white;
            display: flex;
            align-items: center;
            gap: 8px;

            // 品牌图标 .brand-icon
            .brand-icon {
              font-size: 2rem;
              animation: sparkle 2s ease-in-out infinite;
            }
          }

          // 品牌描述 .brand-description
          .brand-description {
            color: rgba(255, 255, 255, 0.85);
            font-size: 1rem;
            line-height: 1.8;
            margin-bottom: 24px;
            font-weight: 300;
          }

          // 社交链接 .social-links
          .social-links {
            display: flex;
            gap: 12px;

            // 社交链接项 .social-link
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

        // 导航链接区域 .footer-links
        .footer-links {
          display: flex;
          justify-content: flex-end;

          // 导航列 .footer-column
          .footer-column {
            // 列标题 .column-title
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

            // 链接列表 .links-list
            .links-list {
              list-style: none;
              padding: 0;
              margin: 0;
              text-align: center;

              // 列表项 li
              li {
                margin-bottom: 12px;

                // 链接 a
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
            }
          }
        }
      }

      // 页脚底部区域 .footer-bottom
      .footer-bottom {
        padding: 0 20px;

        // 分割线 .footer-divider
        .footer-divider {
          height: 1px;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3) 20%, rgba(255, 255, 255, 0.3) 80%, transparent);
          margin-bottom: 24px;
        }

        // 底部内容 .footer-bottom-content
        .footer-bottom-content {
          display: flex;
          justify-content: space-between;
          align-items: center;
          flex-wrap: wrap;
          gap: 20px;
          padding-bottom: 30px;

          // 左侧版权信息 .footer-left
          .footer-left {
            display: flex;
            flex-direction: column;
            gap: 8px;

            // 版权声明 .copyright
            .copyright {
              color: rgba(255, 255, 255, 0.7);
              font-size: 0.9rem;
              margin: 0;

              strong {
                color: $white;
                font-weight: 600;
              }
            }

            // 备案信息 .beian
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

          // 页脚元信息 .footer-meta
          .footer-meta {
            display: flex;
            align-items: center;
            gap: 12px;
            font-size: 0.85rem;
            color: rgba(255, 255, 255, 0.6);

            // 分隔符 .separator
            .separator {
              color: rgba(255, 255, 255, 0.3);
            }

            // 链接 a
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
  }
}

// ===== 响应式样式（按屏幕尺寸组织） =====

// 中等屏幕（平板）
@media screen and (max-width: 992px) {
  .home-container {
    // 主要内容区域响应式
    .main-content {
      .latest-articles {
        .article-list {
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        }
      }
    }

    // 页脚响应式
    .footer {
      .container {
        padding-top: 60px;

        .footer-content {
          grid-template-columns: 1fr;
          gap: 40px;
        }

        .footer-bottom {
          .footer-bottom-content {
            flex-direction: column;
            text-align: center;
          }
        }
      }
    }
  }
}

// 小屏幕（手机）
@media screen and (max-width: 768px) {
  // 容器响应式
  .container {
    padding: 0 15px;
  }

  .home-container {
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

    // 主要内容区域响应式
    .main-content {
      padding: 40px 0;

      .latest-articles {
        padding: 20px; // 手机端减少内边距
        min-height: 80vh; // 手机端保持足够高度

        .articles-wrapper {
          min-height: 60vh; // 手机端减少最小高度
        }

        .loading-container {
          .article-skeleton {
            padding: 15px;
            margin-bottom: 20px;
          }
        }

        .article-list {
          grid-template-columns: 1fr;
          gap: 20px;

          .article-card {
            .article-content {
              .article-stats {
                gap: 10px;

                .read-more {
                  align-self: flex-end;
                }
              }
            }
          }
        }
      }
    }

    // 页脚响应式
    .footer {
      .footer-wave {
        height: 40px;

        svg {
          height: 40px;
        }
      }

      .container {
        padding-top: 50px;

        .footer-content {
          display: flex;
          flex-direction: row;
          gap: 30px;
          align-items: flex-start;

          .footer-brand {
            flex: 1;

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
            flex: 1;
            display: flex;
            justify-content: flex-end;

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
  }
}

// 超小屏幕（小手机）
@media screen and (max-width: 480px) {
  .home-container {
    // 全屏首屏响应式
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

    // 主要内容区域响应式
    .main-content {
      padding: 30px 0;

      .latest-articles {
        padding: 15px; // 超小屏幕进一步减少内边距
        min-height: 75vh; // 超小屏幕保持足够高度

        .articles-wrapper {
          min-height: 55vh; // 超小屏幕进一步减少最小高度
        }

        .article-list {
          gap: 15px;

          .article-card {
            .article-image {
              height: 160px;
            }

            .article-content {
              padding: 10px;

              .article-meta {
                height: 1.2rem; // 移动端固定高度

                .category {
                  font-size: 0.75rem;
                  padding: 3px 8px;
                }
              }

              h3 {
                font-size: 1.1rem;
                height: 3.08rem; // 移动端固定高度：1.1rem * 1.4 * 2行 = 3.08rem
              }

              .article-description {
                font-size: 0.9rem;
                height: 2.88rem; // 移动端固定高度：0.9rem * 1.6 * 2行 = 2.88rem
                -webkit-line-clamp: 2;
                line-clamp: 2;
              }
            }
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
