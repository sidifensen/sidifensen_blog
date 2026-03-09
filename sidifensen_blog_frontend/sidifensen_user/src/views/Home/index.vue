<template>
  <div class="home-container">
    <!-- 全屏沉浸式首屏 - 现代化动态版本 -->
    <section class="fullscreen-hero">
      <!-- 动态渐变背景 -->
      <div class="hero-background">
        <div class="gradient-orb orb-1"></div>
        <div class="gradient-orb orb-2"></div>
        <div class="gradient-orb orb-3"></div>
        <div class="grid-overlay"></div>
      </div>

      <!-- 粒子效果容器 -->
      <div class="particles-container" ref="particlesRef"></div>

      <div class="hero-content">
        <div class="title-wrapper">
          <span class="title-badge">
            <span class="badge-icon">✨</span>
            <span class="badge-text">欢迎来到斯蒂芬森社区</span>
          </span>
          <h1 class="hero-title">
            <span class="title-gradient">探索技术的无限可能</span>
          </h1>
          <p class="hero-subtitle">
            <span class="subtitle-text">{{ typedSubtitle }}</span>
            <span class="cursor-blink"></span>
          </p>
          <!-- CTA 按钮组 -->
          <div class="hero-cta">
            <button class="cta-btn cta-primary" @click="navigateTo('/article')">
              <span>开始探索</span>
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>

        <!-- 快速导航卡片 -->
        <div class="quick-nav" v-if="showQuickNav">
          <div class="nav-card" v-for="(item, index) in quickNavItems" :key="index" :style="{ animationDelay: `${0.6 + index * 0.12}s` }" @click="navigateTo(item.path)">
            <div class="nav-card-bg"></div>
            <div class="nav-icon-wrapper">
              <div class="nav-icon">{{ item.icon }}</div>
              <div class="nav-icon-glow"></div>
            </div>
            <div class="nav-content">
              <div class="nav-title">{{ item.title }}</div>
              <div class="nav-desc">{{ item.description }}</div>
            </div>
            <div class="nav-arrow">
              <el-icon><ArrowRight /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- 主要内容区域 -->
    <main class="main-content" ref="contentSection">
      <div class="container">
        <!-- 内容网格布局 -->
        <div class="content-grid">
          <!-- 最新文章 -->
          <section class="latest-articles" ref="articlesSectionRef">
            <div class="section-header">
              <h2 class="section-title">
                <span class="title-icon">📝</span>
                最新文章
                <span class="title-decoration"></span>
              </h2>
              <router-link to="/article" class="view-all-link">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>

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
                <article
                  v-for="(article, index) in articles"
                  :key="article.id"
                  class="article-card"
                  :style="{ animationDelay: `${index * 0.08}s` }"
                  @click="goToArticle(article)"
                >
                  <div class="article-image">
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
                    <div class="article-overlay">
                      <div class="overlay-content">
                        <el-icon><View /></el-icon>
                        <span>阅读</span>
                      </div>
                    </div>
                  </div>
                  <div class="article-content">
                    <div class="article-tags">
                      <span class="tag tag-hot" v-if="article.readCount > 1000">🔥 热门</span>
                      <span class="tag tag-new" v-else-if="isNewArticle(article)">✨ 新作</span>
                    </div>
                    <div class="article-meta">
                      <span class="date">
                        <el-icon><Calendar /></el-icon>
                        {{ formatDate(article.createTime) }}
                      </span>
                    </div>
                    <h3>
                      <router-link :to="getArticleDetailRoute(article)" class="article-title-link">
                        {{ article.title }}
                      </router-link>
                    </h3>
                    <p class="article-description">{{ article.description || "暂无描述" }}</p>
                    <div class="article-stats">
                      <div class="stats-left">
                        <span class="stat-item">
                          <el-icon><View /></el-icon>
                          {{ formatNumber(article.readCount || 0) }}
                        </span>
                        <span class="stat-item like">
                          <svg-icon name="like" width="13px" height="13px" margin-right="1px" :color="article.isLiked ? '#ff6b6b' : '#999999'" />
                          {{ formatNumber(article.likeCount || 0) }}
                        </span>
                        <span class="stat-item">
                          <el-icon><Star /></el-icon>
                          {{ formatNumber(article.collectCount || 0) }}
                        </span>
                      </div>
                      <span class="read-more">
                        阅读全文
                        <el-icon><ArrowRight /></el-icon>
                      </span>
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
              sidifensen 社区
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
              <p class="copyright">&copy; {{ currentYear }} <strong>sidifensen 社区</strong>. All Rights Reserved.</p>
              <p class="beian">
                <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">粤 ICP 备 2024324512 号 -2</a>
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
import { Loading, Picture, View, Star, Compass, Calendar, ArrowRight } from "@element-plus/icons-vue";
import { getAllArticleList } from "@/api/article";
import ProjectLinks from "@/components/ProjectLinks.vue";

// 路由
const router = useRouter();

// 响应式数据
const articles = ref([]); // 文章列表
const articleLoading = ref(false); // 文章加载状态
const currentYear = ref(new Date().getFullYear());
const contentSection = ref(null); // 内容区域引用
const particlesRef = ref(null); // 粒子容器引用
const articlesSectionRef = ref(null); // 文章区域引用
const showQuickNav = ref(false); // 是否显示快速导航

// 打字机效果的副标题
const typedSubtitle = ref("");
let subtitleIndex = 0;
let charIndex = 0;
let isDeleting = false;

// 快速导航项
const quickNavItems = ref([
  { icon: "📝", title: "技术文章", description: "探索优质技术内容", path: "/article" },
  { icon: "🖼️", title: "相册空间", description: "浏览精美图片", path: "/album" },
  { icon: "🔗", title: "友情链接", description: "连接更多资源", path: "/link" },
]);

// 打字机效果函数 - 循环播放多个副标题

// 副标题列表
const subtitles = [
  "在这里，发现知识 · 分享经验 · 连接开发者",
  "探索代码的无限可能",
  "与开发者共同成长",
  "记录技术旅程的点点滴滴",
  "打造属于你的技术生态圈",
];

// 打字机速度配置
const TYPE_SPEED = 150; // 打字速度 (ms/字)
const DELETE_SPEED = 80; // 删除速度 (ms/字)
const PAUSE_AFTER_TYPE = 800; // 输入完成后暂停时间 (ms)
const PAUSE_AFTER_DELETE = 300; // 删除完成后暂停时间 (ms)

const typeSubtitle = () => {
  const currentSubtitle = subtitles[subtitleIndex];

  if (!isDeleting) {
    // 正在输入
    if (charIndex < currentSubtitle.length) {
      typedSubtitle.value = currentSubtitle.substring(0, charIndex + 1);
      charIndex++;
      setTimeout(typeSubtitle, TYPE_SPEED);
    } else {
      // 输入完成，暂停后开始删除
      isDeleting = true;
      setTimeout(typeSubtitle, PAUSE_AFTER_TYPE);
    }
  } else {
    // 正在删除
    if (charIndex > 0) {
      typedSubtitle.value = currentSubtitle.substring(0, charIndex - 1);
      charIndex--;
      setTimeout(typeSubtitle, DELETE_SPEED);
    } else {
      // 删除完成，切换到下一个副标题
      isDeleting = false;
      subtitleIndex = (subtitleIndex + 1) % subtitles.length;
      charIndex = 0;
      setTimeout(typeSubtitle, PAUSE_AFTER_DELETE);
    }
  }
};

// 创建粒子效果
const createParticles = (container, count = 30) => {
  if (!container) return;

  container.innerHTML = "";
  const particles = [];

  for (let i = 0; i < count; i++) {
    const particle = document.createElement("div");
    particle.className = "particle";
    particle.style.left = `${Math.random() * 100}%`;
    particle.style.top = `${Math.random() * 100}%`;
    particle.style.width = `${Math.random() * 4 + 2}px`;
    particle.style.height = particle.style.width;
    particle.style.animationDelay = `${Math.random() * 5}s`;
    particle.style.animationDuration = `${Math.random() * 10 + 10}s`;
    container.appendChild(particle);
    particles.push(particle);
  }

  return particles;
};

// 观察元素进入视口
const observeElements = () => {
  const observerOptions = {
    threshold: 0.1,
    rootMargin: "0px 0px -100px 0px",
  };

  const observer = new IntersectionObserver((entries) => {
    entries.forEach((entry) => {
      if (entry.isIntersecting) {
        const target = entry.target;
        target.classList.add("visible");

        // 数字动画
        const numberElements = target.querySelectorAll("[data-target]");
        numberElements.forEach((el) => {
          const targetValue = parseInt(el.getAttribute("data-target"));
          animateNumber(el, targetValue);
        });

        observer.unobserve(target);
      }
    });
  }, observerOptions);

  // 观察文章区域
  if (articlesSectionRef.value) {
    observer.observe(articlesSectionRef.value);
  }
};

// 获取文章列表 - 固定只加载 12 条
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

// 格式化数字
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + "w";
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + "k";
  }
  return num.toString();
};

// 判断是否为新文章
const isNewArticle = (article) => {
  const createTime = new Date(article.createTime);
  const now = new Date();
  const diffTime = now - createTime;
  const diffDays = diffTime / (1000 * 60 * 60 * 24);
  return diffDays <= 3;
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

// 快速导航跳转
const navigateTo = (path) => {
  router.push(path);
};

// 组件挂载时的初始化
onMounted(async () => {
  // 获取文章列表
  await fetchArticleList(true);

  // 初始化粒子效果
  createParticles(particlesRef.value, 30);

  // 启动打字机效果
  setTimeout(typeSubtitle, 1000);

  // 显示快速导航（延迟一点让布局稳定）
  setTimeout(() => {
    showQuickNav.value = true;
  }, 800);

  // 启动观察器
  setTimeout(() => {
    observeElements();
  }, 500);
});
</script>

<style lang="scss" scoped>
// ===== 全局样式变量 - 2026 现代化 Glassmorphism 设计系统 =====
// 主色调：深邃夜空蓝 + 活力珊瑚橙 + 极光紫点缀
$deep-navy: #0f172a;
$ocean-blue: #1e3a5f;
$azure-blue: #3b82f6;
$electric-blue: #60a5fa;
$coral-orange: #ff6b35;
$sunrise-orange: #f97316;
$aurora-purple: #8b5cf6;
$warm-sand: #fef7ed;
$slate-50: #f8fafc;
$slate-100: #f1f5f9;
$slate-200: #e2e8f0;
$slate-300: #cbd5e1;
$slate-400: #94a3b8;
$slate-500: #64748b;
$slate-600: #475569;
$slate-700: #334155;
$slate-800: #1e293b;
$slate-900: #0f172a;

$primary-color: $azure-blue;
$primary-gradient: linear-gradient(135deg, $azure-blue 0%, $ocean-blue 100%);
$primary-gradient-light: linear-gradient(135deg, rgba(59, 130, 246, 0.08) 0%, rgba(30, 58, 95, 0.08) 100%);
$accent-gradient: linear-gradient(135deg, $coral-orange 0%, $sunrise-orange 100%);
$warm-gradient: linear-gradient(135deg, $coral-orange 0%, $sunrise-orange 100%);
$aurora-gradient: linear-gradient(135deg, $aurora-purple 0%, $azure-blue 100%);
$ocean-gradient: linear-gradient(135deg, $ocean-blue 0%, $azure-blue 50%, $electric-blue 100%);
$sunset-gradient: linear-gradient(135deg, $coral-orange 0%, $sunrise-orange 50%, $aurora-purple 100%);

$secondary-color: $ocean-blue;
$accent-color: $coral-orange;
$text-color: $slate-800;
$text-color-light: $slate-600;
$light-gray: $slate-50;
$medium-gray: $slate-200;
$dark-gray: $slate-500;
$white: #fff;

// 阴影层次 - 2026 趋势：更柔和、更有深度
$shadow-xs: 0 1px 2px rgba(0, 0, 0, 0.04);
$shadow-sm: 0 2px 4px rgba(0, 0, 0, 0.06);
$shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
$shadow-md: 0 8px 16px rgba(0, 0, 0, 0.1);
$shadow-lg: 0 12px 32px rgba(0, 0, 0, 0.12);
$shadow-xl: 0 20px 48px rgba(0, 0, 0, 0.15);
$shadow-2xl: 0 32px 64px rgba(0, 0, 0, 0.18);
$shadow-glow-blue: 0 8px 32px rgba(59, 130, 246, 0.25);
$shadow-glow-orange: 0 8px 32px rgba(255, 107, 53, 0.25);
$shadow-glow-purple: 0 8px 32px rgba(139, 92, 246, 0.25);

// 玻璃态效果 - 2026 增强版
$glass-bg: rgba(255, 255, 255, 0.85);
$glass-bg-light: rgba(255, 255, 255, 0.7);
$glass-bg-strong: rgba(255, 255, 255, 0.95);
$glass-bg-subtle: rgba(255, 255, 255, 0.6);
$glass-border: rgba(255, 255, 255, 0.2);
$glass-border-strong: rgba(255, 255, 255, 0.35);
$glass-border-accent: rgba(59, 130, 246, 0.3);
$glass-shadow: 0 8px 32px rgba(0, 0, 0, 0.08), inset 0 1px 0 rgba(255, 255, 255, 0.3);

// 高级玻璃态 - 带色彩倾向
$glass-navy: rgba(15, 23, 42, 0.7);
$glass-blue: rgba(59, 130, 246, 0.15);
$glass-coral: rgba(255, 107, 53, 0.1);

// 过渡动画 - 更流畅自然
$transition-fast: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
$transition-base: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
$transition-smooth: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
$transition-slow: all 0.5s cubic-bezier(0.4, 0, 0.2, 1);
$transition-bounce: all 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
$transition-elastic: all 0.5s cubic-bezier(0.34, 1.56, 0.64, 1);

// ===== 容器响应式优化 =====
.container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;

  @media screen and (max-width: 768px) {
    padding: 0 16px;
  }
}

// ===== 首页容器 =====
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

  // ===== 第一部分：全屏沉浸式首屏 =====
  .fullscreen-hero {
    position: relative;
    width: 100%;
    height: 100vh;
    margin-top: -48px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    overflow: hidden;
    padding-top: 48px;
    z-index: 2;

    // 动态渐变背景 - 2026 增强版
    .hero-background {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: url("@/assets/img/th.jpg") no-repeat center center / cover fixed;
      z-index: 1;

      // 增强深色遮罩层 - 多层渐变营造深度
      &::before {
        content: "";
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: linear-gradient(
          135deg,
          rgba(15, 23, 42, 0.85) 0%,
          rgba(30, 58, 95, 0.75) 35%,
          rgba(59, 130, 246, 0.55) 65%,
          rgba(15, 23, 42, 0.85) 100%
        );
        pointer-events: none;
      }

      // 装饰性光斑 - 增强版，更多层次
      .gradient-orb {
        position: absolute;
        border-radius: 50%;
        filter: blur(140px);
        opacity: 0.4;
        animation: orbFloat 30s ease-in-out infinite;
        mix-blend-mode: screen;

        &.orb-1 {
          width: 700px;
          height: 700px;
          background: radial-gradient(circle, rgba(59, 130, 246, 0.5) 0%, rgba(96, 165, 250, 0.3) 40%, transparent 70%);
          top: -15%;
          left: -8%;
          animation-delay: 0s;
        }

        &.orb-2 {
          width: 600px;
          height: 600px;
          background: radial-gradient(circle, rgba(255, 107, 53, 0.35) 0%, rgba(249, 115, 22, 0.2) 40%, transparent 70%);
          bottom: -15%;
          right: -8%;
          animation-delay: -10s;
        }

        &.orb-3 {
          width: 500px;
          height: 500px;
          background: radial-gradient(circle, rgba(139, 92, 246, 0.3) 0%, rgba(167, 139, 250, 0.15) 40%, transparent 70%);
          top: 35%;
          right: 15%;
          animation-delay: -20s;
        }

        &.orb-4 {
          width: 400px;
          height: 400px;
          background: radial-gradient(circle, rgba(59, 130, 246, 0.35) 0%, transparent 60%);
          top: 60%;
          left: 20%;
          animation-delay: -25s;
        }
      }

      // 增强细微网格纹理 - 更精致
      .grid-overlay {
        position: absolute;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-image:
          linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
          linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
        background-size: 80px 80px;
        pointer-events: none;
        opacity: 0.4;
      }

      // 底部渐变过渡 - 更平滑
      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        height: 250px;
        background: linear-gradient(
          to bottom,
          transparent 0%,
          rgba(248, 250, 252, 0.3) 30%,
          rgba(241, 245, 249, 0.6) 60%,
          #f1f5f9 100%
        );
        pointer-events: none;
      }
    }

    // 粒子效果容器
    .particles-container {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      z-index: 2;
      pointer-events: none;
      overflow: hidden;

      .particle {
        position: absolute;
        background: radial-gradient(
          circle,
          rgba(255, 255, 255, 0.8) 0%,
          rgba(255, 255, 255, 0.2) 40%,
          transparent 70%
        );
        border-radius: 50%;
        animation: particleFloat linear infinite;
        box-shadow:
          0 0 10px rgba(255, 255, 255, 0.5),
          0 0 20px rgba(59, 130, 246, 0.3);
      }
    }

    // 英雄内容区域
    .hero-content {
      position: relative;
      z-index: 3;
      text-align: center;
      color: $white;
      animation: fadeInUp 1.2s ease-out;
      padding: 0 20px;
      margin-top: -10vh;
      max-width: 1000px;

      // 标题包装器 - 2026 增强版
      .title-wrapper {
        margin-bottom: 0;

        // 标题徽章 - 极简玻璃拟态
        .title-badge {
          display: inline-flex;
          align-items: center;
          gap: 12px;
          padding: 14px 32px;
          background: linear-gradient(
            135deg,
            rgba(255, 255, 255, 0.18) 0%,
            rgba(255, 255, 255, 0.1) 100%
          );
          border: 1px solid rgba(255, 255, 255, 0.3);
          border-radius: 50px;
          font-size: 1rem;
          font-weight: 600;
          backdrop-filter: blur(20px) saturate(180%);
          -webkit-backdrop-filter: blur(20px) saturate(180%);
          margin-bottom: 20px;
          animation: fadeInDown 1s ease-out 0.2s both;
          letter-spacing: 1px;
          color: rgba(255, 255, 255, 0.98);
          box-shadow:
            0 4px 20px rgba(0, 0, 0, 0.15),
            inset 0 1px 0 rgba(255, 255, 255, 0.35),
            0 0 40px rgba(59, 130, 246, 0.2);
          position: relative;
          overflow: hidden;
          transition: $transition-base;

          .badge-icon {
            font-size: 1.3rem;
            animation: iconPulse 2s ease-in-out infinite;
          }

          .badge-text {
            position: relative;
            z-index: 1;
          }

          // 添加微妙的光泽扫过效果
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(
              90deg,
              transparent 0%,
              rgba(255, 255, 255, 0.3) 50%,
              transparent 100%
            );
            animation: shimmer 3s infinite;
          }

          &:hover {
            background: linear-gradient(
              135deg,
              rgba(255, 255, 255, 0.25) 0%,
              rgba(255, 255, 255, 0.15) 100%
            );
            border-color: rgba(255, 255, 255, 0.45);
            transform: translateY(-3px);
            box-shadow:
              0 12px 40px rgba(0, 0, 0, 0.25),
              0 0 60px rgba(59, 130, 246, 0.3),
              inset 0 1px 0 rgba(255, 255, 255, 0.45);
          }
        }

        // 主标题 - 渐变色增强版
        .hero-title {
          font-size: 5.5rem;
          font-weight: 800;
          margin-bottom: 28px;
          letter-spacing: 2px;
          line-height: 1.1;
          position: relative;

          @media screen and (max-width: 768px) {
            font-size: 3rem;
            letter-spacing: 1px;
          }

          .title-gradient {
            background: linear-gradient(
              135deg,
              #ffffff 0%,
              rgba(255, 255, 255, 0.95) 50%,
              rgba(96, 165, 250, 0.85) 100%
            );
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            text-shadow: none;
            filter: drop-shadow(0 4px 40px rgba(59, 130, 246, 0.4));
            animation: titleGlow 4s ease-in-out infinite;
          }
        }

        // 副标题 - 打字机效果
        .hero-subtitle {
          font-size: 1.35rem;
          font-weight: 400;
          color: rgba(255, 255, 255, 0.75);
          display: inline-flex;
          align-items: center;
          gap: 10px;
          padding: 12px 20px;
          background: rgba(255, 255, 255, 0.05);
          border-radius: 12px;
          backdrop-filter: blur(10px);
          -webkit-backdrop-filter: blur(10px);
          border: 1px solid rgba(255, 255, 255, 0.1);
          box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
          position: relative;

          .subtitle-text {
            position: relative;
          }

          .cursor-blink {
            display: inline-block;
            background: rgba(255, 255, 255, 0.6);
            width: 2px;
            height: 1.4em;
            margin-left: 6px;
            animation: cursorBlink 1s step-end infinite;
            border-radius: 2px;
            box-shadow: 0 0 10px rgba(255, 107, 53, 0.5);
          }
        }

        // CTA 按钮组
        .hero-cta {
          display: flex;
          gap: 16px;
          justify-content: center;
          margin-top: 48px;
          flex-wrap: wrap;

          @media screen and (max-width: 768px) {
            margin-top: 32px;
          }

          .cta-btn {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            padding: 16px 36px;
            border-radius: 50px;
            font-size: 1rem;
            font-weight: 600;
            cursor: pointer;
            transition: $transition-base;
            border: none;
            outline: none;
            letter-spacing: 0.5px;

            .el-icon {
              font-size: 18px;
              transition: $transition-base;
            }

            &:hover .el-icon {
              transform: translateX(4px);
            }
          }

          .cta-primary {
            background: linear-gradient(135deg, $azure-blue 0%, $ocean-blue 100%);
            color: $white;
            box-shadow:
              0 4px 20px rgba(59, 130, 246, 0.35),
              inset 0 1px 0 rgba(255, 255, 255, 0.2);

            &:hover {
              transform: translateY(-3px);
              box-shadow:
                0 12px 40px rgba(59, 130, 246, 0.45),
                inset 0 1px 0 rgba(255, 255, 255, 0.3);
              background: linear-gradient(135deg, lighten($azure-blue, 5%) 0%, lighten($ocean-blue, 3%) 100%);
            }

            &:active {
              transform: translateY(-1px);
            }
          }

          .cta-secondary {
            background: rgba(255, 255, 255, 0.1);
            color: $white;
            border: 1px solid rgba(255, 255, 255, 0.3);
            backdrop-filter: blur(10px);

            &:hover {
              background: rgba(255, 255, 255, 0.2);
              border-color: rgba(255, 255, 255, 0.45);
              transform: translateY(-3px);
              box-shadow: 0 8px 30px rgba(0, 0, 0, 0.2);
            }

            &:active {
              transform: translateY(-1px);
            }
          }
        }
      }

      // 快速导航卡片 - 2026 玻璃拟态增强版
      .quick-nav {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 20px;
        margin-top: 50px;
        padding: 0;
        width: 100%;
        max-width: 1100px;
        margin-left: auto;
        margin-right: auto;

        .nav-card {
          flex: 0 0 calc(25% - 24px);
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 14px;
          padding: 24px 20px;
          background: linear-gradient(
            135deg,
            rgba(255, 255, 255, 0.12) 0%,
            rgba(255, 255, 255, 0.06) 100%
          );
          border-radius: 24px;
          backdrop-filter: blur(20px) saturate(180%);
          -webkit-backdrop-filter: blur(20px) saturate(180%);
          border: 1px solid rgba(255, 255, 255, 0.25);
          box-shadow:
            0 8px 32px rgba(0, 0, 0, 0.12),
            inset 0 1px 0 rgba(255, 255, 255, 0.2);
          cursor: pointer;
          opacity: 0;
          animation: fadeInUp 0.8s ease-out forwards;
          animation-delay: var(--animation-delay, 0s);
          transition: $transition-smooth;
          position: relative;
          overflow: hidden;
          min-width: 220px;
          max-width: 260px;

          @media screen and (max-width: 992px) {
            flex: 0 0 calc(50% - 20px);
            padding: 20px 16px;
            gap: 12px;
          }

          @media screen and (max-width: 768px) {
            flex: 0 0 calc(50% - 16px);
            gap: 10px;
            padding: 20px 14px;
            min-width: calc(50% - 16px);
          }

          @media screen and (max-width: 480px) {
            flex: 0 0 100%;
            padding: 18px 12px;
            gap: 8px;
            min-width: 100%;
          }

          // 背景效果层
          .nav-card-bg {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(
              135deg,
              rgba(59, 130, 246, 0.08) 0%,
              rgba(139, 92, 246, 0.06) 100%
            );
            opacity: 0;
            transition: $transition-base;
            z-index: 0;
          }

          // 添加微妙的光泽扫过效果
          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(
              90deg,
              transparent 0%,
              rgba(255, 255, 255, 0.3) 50%,
              transparent 100%
            );
            opacity: 0;
            transition: $transition-base;
            z-index: 2;
          }

          &:hover {
            transform: translateY(-8px) scale(1.02);
            background: linear-gradient(
              135deg,
              rgba(255, 255, 255, 0.18) 0%,
              rgba(255, 255, 255, 0.1) 100%
            );
            border-color: rgba(255, 255, 255, 0.4);
            box-shadow:
              0 20px 60px rgba(0, 0, 0, 0.2),
              0 0 80px rgba(59, 130, 246, 0.2),
              inset 0 1px 0 rgba(255, 255, 255, 0.35);

            .nav-card-bg {
              opacity: 1;
            }

            &::before {
              opacity: 1;
            }

            .nav-icon-wrapper .nav-icon {
              transform: scale(1.2) rotate(10deg);
              filter: drop-shadow(0 12px 30px rgba(59, 130, 246, 0.5));
            }

            .nav-icon-glow {
              opacity: 1;
              transform: scale(1.5);
            }

            .nav-arrow {
              transform: translateX(8px);
              opacity: 1;
              background: linear-gradient(135deg, $coral-orange 0%, $sunrise-orange 100%);
              box-shadow: 0 4px 20px rgba(255, 107, 53, 0.5);
              color: $white;
            }

            .nav-title {
              color: #ffffff;
            }
          }

          .nav-icon-wrapper {
            position: relative;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 80px;
            height: 80px;
            z-index: 1;

            @media screen and (max-width: 768px) {
              width: 65px;
              height: 65px;
            }

            @media screen and (max-width: 480px) {
              width: 55px;
              height: 55px;
            }

            .nav-icon {
              font-size: 2.5rem;
              transition: $transition-smooth;
              filter: drop-shadow(0 4px 15px rgba(0, 0, 0, 0.3));
              z-index: 2;

              @media screen and (max-width: 768px) {
                font-size: 2rem;
              }

              @media screen and (max-width: 480px) {
                font-size: 1.8rem;
              }
            }

            .nav-icon-glow {
              position: absolute;
              width: 100%;
              height: 100%;
              background: radial-gradient(
                circle,
                rgba(59, 130, 246, 0.4) 0%,
                transparent 70%
              );
              border-radius: 50%;
              opacity: 0;
              transition: $transition-smooth;
              z-index: 1;
            }
          }

          .nav-content {
            flex: 1 1 auto;
            display: flex;
            flex-direction: column;
            gap: 8px;
            text-align: center;
            width: 100%;
            z-index: 1;

            .nav-title {
              font-size: 1.1rem;
              font-weight: 700;
              color: rgba(255, 255, 255, 0.95);
              transition: $transition-base;
              letter-spacing: 0.5px;
              line-height: 1.3;

              @media screen and (max-width: 768px) {
                font-size: 1rem;
              }

              @media screen and (max-width: 480px) {
                font-size: 0.95rem;
              }
            }

            .nav-desc {
              font-size: 0.85rem;
              color: rgba(255, 255, 255, 0.7);
              font-weight: 400;
              letter-spacing: 0.2px;
              line-height: 1.5;

              @media screen and (max-width: 768px) {
                font-size: 0.8rem;
              }

              @media screen and (max-width: 480px) {
                font-size: 0.75rem;
              }
            }
          }

          .nav-arrow {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.1);
            display: flex;
            align-items: center;
            justify-content: center;
            color: rgba(255, 255, 255, 0.8);
            transition: $transition-smooth;
            flex-shrink: 0;
            z-index: 1;
            opacity: 0.7;

            .el-icon {
              font-size: 18px;
            }

            &:hover {
              background: linear-gradient(135deg, $coral-orange 0%, $sunrise-orange 100%);
            }
          }
        }
      }
    }

  }

  // ===== 第二部分：主要内容区域 =====
  .main-content {
    padding: 80px 0;
    background-color: var(--el-bg-color-page);
    position: relative;
    z-index: 2;
    min-height: 100vh;

    // 内容网格布局
    .content-grid {
      display: grid;
      grid-template-columns: 1fr 320px;
      gap: 40px;
      align-items: start;

      @media screen and (max-width: 992px) {
        grid-template-columns: 1fr;
        gap: 32px;
      }
    }

    // 最新文章区域 - 增强版
    .latest-articles {
      background: linear-gradient(
        135deg,
        var(--el-bg-color) 0%,
        rgba(248, 250, 252, 0.5) 50%,
        var(--el-bg-color) 100%
      );
      border-radius: 24px;
      padding: 40px;
      box-shadow:
        0 8px 40px rgba(0, 0, 0, 0.06),
        0 2px 8px rgba(0, 0, 0, 0.04);
      position: relative;
      z-index: 2;
      min-height: 85vh;
      border: 1px solid var(--el-border-color-light);
      backdrop-filter: blur(10px);

      // 章节标题 - 增强版
      .section-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 35px;
        padding-bottom: 22px;
        border-bottom: 2px solid var(--el-border-color-lighter);
        position: relative;

        .section-title {
          font-size: 1.9rem;
          font-weight: 700;
          color: $text-color;
          display: flex;
          align-items: center;
          gap: 12px;
          position: relative;

          .title-icon {
            font-size: 2.2rem;
            filter: drop-shadow(0 2px 8px rgba(59, 130, 246, 0.3));
          }

          .title-decoration {
            position: absolute;
            bottom: -24px;
            left: 0;
            width: 100px;
            height: 4px;
            background: linear-gradient(
              90deg,
              #3b82f6 0%,
              #1e40af 50%,
              #dc2626 100%
            );
            border-radius: 2px;
            box-shadow: 0 2px 10px rgba(59, 130, 246, 0.4);
          }
        }

        .view-all-link {
          display: flex;
          align-items: center;
          gap: 8px;
          color: $primary-color;
          font-weight: 600;
          font-size: 0.95rem;
          text-decoration: none;
          padding: 10px 20px;
          border-radius: 10px;
          background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.08) 100%);
          transition: $transition-base;
          border: 1px solid rgba(102, 126, 234, 0.15);
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            inset: 0;
            background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.12) 100%);
            opacity: 0;
            transition: $transition-base;
          }

          &:hover {
            &::before {
              opacity: 1;
            }
            transform: translateX(6px);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.25);
          }

          .el-icon {
            transition: transform 0.3s ease;
          }

          &:hover .el-icon {
            transform: translateX(4px);
          }
        }
      }

      // 文章内容包装容器 - 增强版
      .articles-wrapper {
        background-color: transparent;
        border-radius: 8px;
        padding: 0;
        box-shadow: none;
        min-height: 70vh;
        position: relative;
        z-index: 2;

        // 加载状态 - 增强版
        .loading-container {
          margin-bottom: 0;
          background-color: transparent;
          border-radius: 0;
          padding: 0;
          box-shadow: none;
          transition: $transition-base;

          .article-skeleton {
            background: linear-gradient(
              90deg,
              $slate-50 0%,
              $slate-100 50%,
              $slate-50 100%
            );
            background-size: 200% 100%;
            animation: skeleton-loading 1.5s infinite;
            border-radius: 16px;
            padding: 24px;
            margin-bottom: 28px;
            box-shadow:
              0 2px 8px rgba(0, 0, 0, 0.04),
              0 1px 2px rgba(0, 0, 0, 0.02);

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

        // 空状态 - 增强版
        .empty-state {
          padding: 100px 0;
          text-align: center;
          margin-bottom: 0;
          background-color: transparent;
          border-radius: 0;
          box-shadow: none;

          :deep(.el-empty__description) {
            color: $slate-500;
            font-size: 1rem;
          }
        }

        // 文章列表 - 增强版
        .article-list {
          display: grid;
          grid-template-columns: repeat(3, 1fr);
          gap: 32px;
          margin-bottom: 0;

          @media screen and (max-width: 1200px) {
            grid-template-columns: repeat(2, 1fr);
          }

          @media screen and (max-width: 768px) {
            grid-template-columns: 1fr;
          }

          // 文章卡片 - 2026 玻璃拟态增强版
          .article-card {
            background: linear-gradient(
              135deg,
              var(--el-bg-color) 0%,
              rgba(248, 250, 252, 0.8) 50%,
              var(--el-bg-color) 100%
            );
            border-radius: 20px;
            overflow: hidden;
            box-shadow:
              0 4px 20px rgba(0, 0, 0, 0.08),
              0 1px 4px rgba(0, 0, 0, 0.05);
            transition: $transition-smooth;
            display: flex;
            flex-direction: column;
            height: 100%;
            cursor: pointer;
            opacity: 0;
            transform: translateY(25px);
            animation: fadeInUp 0.6s ease-out forwards;
            animation-delay: var(--animation-delay, 0s);
            border: 1px solid rgba(59, 130, 246, 0.1);
            position: relative;

            // 添加微妙的内发光
            &::after {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              bottom: 0;
              background: linear-gradient(
                135deg,
                rgba(59, 130, 246, 0.03) 0%,
                transparent 50%,
                rgba(255, 107, 53, 0.02) 100%
              );
              pointer-events: none;
              opacity: 0;
              transition: $transition-base;
            }

            // 顶部进度条（悬停时显示）
            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              height: 4px;
              background: linear-gradient(
                90deg,
                $azure-blue 0%,
                $electric-blue 25%,
                $coral-orange 75%,
                $sunrise-orange 100%
              );
              transform: scaleX(0);
              transform-origin: left;
              transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
              z-index: 10;
            }

            // 文章卡片悬停效果 - 增强版
            &:hover {
              transform: translateY(-16px) scale(1.02);
              box-shadow:
                0 24px 60px rgba(15, 23, 42, 0.18),
                0 12px 40px rgba(59, 130, 246, 0.2),
                0 0 80px rgba(59, 130, 246, 0.1);
              border-color: rgba(59, 130, 246, 0.4);

              &::before {
                transform: scaleX(1);
              }

              &::after {
                opacity: 1;
              }

              .article-cover {
                transform: scale(1.1);
              }

              .article-overlay {
                opacity: 1;

                .overlay-content {
                  transform: translateY(0);
                }
              }

              .read-more {
                color: $primary-color;
                background: linear-gradient(
                  135deg,
                  rgba(59, 130, 246, 0.15) 0%,
                  rgba(139, 92, 246, 0.12) 100%
                );
                transform: translateX(8px);
                box-shadow: 0 6px 20px rgba(59, 130, 246, 0.3);
              }
            }

            // 文章图片区域
            .article-image {
              height: 200px;
              overflow: hidden;
              position: relative;

              .article-cover {
                width: 100%;
                height: 100%;
                transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
                object-fit: cover;
              }

              // 图片悬停遮罩层
              .article-overlay {
                position: absolute;
                top: 0;
                left: 0;
                width: 100%;
                height: 100%;
                background: linear-gradient(
                  to bottom,
                  rgba(0, 0, 0, 0) 0%,
                  rgba(0, 0, 0, 0.4) 60%,
                  rgba(15, 23, 42, 0.85) 100%
                );
                display: flex;
                align-items: flex-end;
                justify-content: center;
                padding-bottom: 30px;
                opacity: 0;
                transition: $transition-base;

                .overlay-content {
                  display: flex;
                  align-items: center;
                  gap: 8px;
                  color: $white;
                  font-weight: 600;
                  transform: translateY(20px);
                  transition: $transition-base;
                  padding: 8px 16px;
                  background: rgba(255, 255, 255, 0.15);
                  border-radius: 20px;
                  backdrop-filter: blur(10px);
                  border: 1px solid rgba(255, 255, 255, 0.25);

                  .el-icon {
                    font-size: 18px;
                  }
                }
              }

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

            // 文章内容区域 - 增强版
            .article-content {
              padding: 20px;
              display: flex;
              flex-direction: column;
              flex: 1;

              // 文章标签
              .article-tags {
                display: flex;
                gap: 8px;
                margin-bottom: 12px;
                flex-wrap: wrap;

                .tag {
                  padding: 5px 12px;
                  border-radius: 20px;
                  font-size: 0.78rem;
                  font-weight: 600;
                  transition: $transition-base;
                  letter-spacing: 0.3px;

                  &.tag-hot {
                    background: $warm-gradient;
                    color: $white;
                    box-shadow: 0 2px 10px rgba(255, 107, 53, 0.3);

                    &:hover {
                      transform: translateY(-2px);
                      box-shadow: 0 4px 15px rgba(255, 107, 53, 0.45);
                    }
                  }

                  &.tag-new {
                    background: $accent-gradient;
                    color: $white;
                    box-shadow: 0 2px 10px rgba(255, 107, 53, 0.3);

                    &:hover {
                      transform: translateY(-2px);
                      box-shadow: 0 4px 15px rgba(255, 107, 53, 0.45);
                    }
                  }

                  &.tag-default {
                    background: linear-gradient(135deg, rgba(59, 130, 246, 0.12) 0%, rgba(30, 58, 95, 0.08) 100%);
                    color: $primary-color;
                    border: 1px solid rgba(59, 130, 246, 0.2);

                    &:hover {
                      background: $primary-gradient;
                      color: $white;
                      transform: translateY(-2px);
                      box-shadow: 0 4px 15px rgba(59, 130, 246, 0.3);
                    }
                  }
                }
              }

              // 文章元信息
              .article-meta {
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 0.82rem;
                height: 1.3rem;
                margin-bottom: 12px;
                padding-bottom: 10px;
                border-bottom: 1px solid var(--el-border-color-lighter);

                .date {
                  color: $text-color-light;
                  font-weight: 500;
                  display: flex;
                  align-items: center;
                  gap: 6px;

                  .el-icon {
                    font-size: 15px;
                    color: $primary-color;
                  }
                }
              }

              // 文章标题
              h3 {
                margin: 0;
                font-size: 1.25rem;
                line-height: 1.5;
                min-height: 3.75rem;
                display: flex;
                align-items: flex-start;
                margin-bottom: 10px;

                .article-title-link {
                  color: $text-color;
                  text-decoration: none;
                  transition: $transition-base;
                  font-weight: 700;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                  letter-spacing: 0.3px;

                  &:hover {
                    color: $primary-color;
                  }
                }
              }

              // 文章描述 - 增强版
              .article-description {
                margin: 8px 0 10px 0;
                color: $text-color-light;
                line-height: 1.7;
                min-height: 3.4rem;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                text-overflow: ellipsis;
                font-size: 0.93rem;
                letter-spacing: 0.2px;
              }

              // 文章统计信息
              .article-stats {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding-top: 15px;
                border-top: 1px solid var(--el-border-color-lighter);
                margin-top: auto;
                background: linear-gradient(135deg, transparent 0%, rgba(59, 130, 246, 0.04) 100%);

                .stats-left {
                  display: flex;
                  gap: 14px;

                  .stat-item {
                    display: flex;
                    align-items: center;
                    gap: 5px;
                    font-size: 0.83rem;
                    color: $text-color-light;
                    font-weight: 500;
                    transition: $transition-base;

                    .el-icon {
                      font-size: 15px;
                    }

                    &:hover {
                      color: $primary-color;
                    }

                    &.like {
                      :deep(.svg-icon) {
                        display: flex;
                        align-items: center;
                      }
                    }
                  }
                }

                // 阅读全文链接
                .read-more {
                  color: $primary-color;
                  text-decoration: none;
                  font-weight: 600;
                  font-size: 0.88rem;
                  transition: $transition-base;
                  padding: 6px 12px;
                  border-radius: 20px;
                  display: flex;
                  align-items: center;
                  gap: 5px;
                  background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%);
                  border: 1px solid rgba(102, 126, 234, 0.15);
                  letter-spacing: 0.3px;

                  &:hover {
                    background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
                    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.25);
                    transform: translateX(3px);
                  }

                  .el-icon {
                    transition: transform 0.3s ease;
                  }
                }
              }
            }
          }
        }
      }

      // 手机端项目链接
      .mobile-project-links {
        display: none;
        margin-top: 32px;
        padding-top: 32px;
        border-top: 1px solid var(--el-border-color-lighter);

        @media screen and (max-width: 992px) {
          display: block;
        }
      }
    }

    // 右侧边栏
    .sidebar {
      position: sticky;
      top: 100px;
      margin-top: 0;

      @media screen and (max-width: 992px) {
        display: none;
      }
    }
  }

  // ===== 第三部分：页脚 - 增强版 =====
  .footer {
    position: relative;
    background: linear-gradient(135deg, #0f172a 0%, #1e293b 35%, #1e3c72 65%, #2a5298 100%);
    background-size: 200% 100%;
    animation: footerGradient 15s ease-in-out infinite;
    color: $white;
    padding: 0;
    overflow: hidden;
    z-index: 2;

    // 添加背景装饰效果
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background:
        radial-gradient(circle at 20% 50%, rgba(59, 130, 246, 0.08) 0%, transparent 50%),
        radial-gradient(circle at 80% 80%, rgba(255, 107, 53, 0.05) 0%, transparent 50%);
      pointer-events: none;
    }

    // 顶部波浪效果 - 增强版
    .footer-wave {
      position: absolute;
      top: -1px;
      left: 0;
      width: 100%;
      height: 80px;
      overflow: hidden;
      line-height: 0;

      svg {
        position: relative;
        display: block;
        width: calc(100% + 1.3px);
        height: 80px;
        fill: var(--el-bg-color-page);
        filter: drop-shadow(0 -2px 10px rgba(0, 0, 0, 0.1));
      }
    }

    // 容器
    .container {
      padding-top: 90px;
      padding-bottom: 25px;
      position: relative;
      z-index: 1;

      // 页脚内容区域 - 增强版
      .footer-content {
        display: grid;
        grid-template-columns: 1.5fr 2fr;
        gap: 70px;
        margin-bottom: 15px;
        padding: 0 20px;

        @media screen and (max-width: 768px) {
          grid-template-columns: 1fr;
          gap: 50px;
        }

        // 品牌信息 - 增强版
        .footer-brand {
          .brand-title {
            font-size: 2rem;
            font-weight: 800;
            margin-bottom: 18px;
            color: $white;
            display: flex;
            align-items: center;
            gap: 10px;
            text-shadow: 0 2px 20px rgba(0, 0, 0, 0.3);

            .brand-icon {
              font-size: 2.2rem;
              animation: sparkle 2s ease-in-out infinite;
              filter: drop-shadow(0 2px 10px rgba(255, 255, 255, 0.3));
            }
          }

          .brand-description {
            color: rgba(255, 255, 255, 0.8);
            font-size: 1.05rem;
            line-height: 1.9;
            margin-bottom: 28px;
            font-weight: 300;
            letter-spacing: 0.3px;
          }

          .social-links {
            display: flex;
            gap: 14px;
            flex-wrap: wrap;

            .social-link {
              display: flex;
              align-items: center;
              justify-content: center;
              width: 46px;
              height: 46px;
              background: linear-gradient(135deg, rgba(255, 255, 255, 0.15) 0%, rgba(255, 255, 255, 0.08) 100%);
              border-radius: 50%;
              color: $white;
              text-decoration: none;
              transition: $transition-smooth;
              backdrop-filter: blur(10px);
              border: 1px solid rgba(255, 255, 255, 0.25);
              position: relative;
              overflow: hidden;

              // 添加光晕效果
              &::before {
                content: '';
                position: absolute;
                inset: 0;
                background: linear-gradient(135deg, $coral-orange 0%, $sunrise-orange 100%);
                opacity: 0;
                transition: $transition-base;
              }

              .el-icon {
                font-size: 20px;
                position: relative;
                z-index: 1;
                transition: $transition-base;
              }

              &:hover {
                transform: translateY(-5px) scale(1.1);
                box-shadow:
                  0 10px 30px rgba(255, 107, 53, 0.4),
                  0 0 20px rgba(255, 107, 53, 0.3);
                border-color: rgba(255, 255, 255, 0.4);

                &::before {
                  opacity: 0.9;
                }

                .el-icon {
                  transform: scale(1.15) rotate(10deg);
                }
              }
            }
          }
        }

        // 导航链接 - 增强版
        .footer-links {
          display: flex;
          justify-content: flex-end;

          .footer-column {
            .column-title {
              font-size: 1.15rem;
              font-weight: 700;
              margin-bottom: 24px;
              color: $white;
              display: flex;
              align-items: center;
              gap: 10px;
              padding-bottom: 14px;
              border-bottom: 2px solid rgba(255, 255, 255, 0.25);
              text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);

              .el-icon {
                font-size: 20px;
                filter: drop-shadow(0 2px 8px rgba(255, 255, 255, 0.3));
              }
            }

            .links-list {
              list-style: none;
              padding: 0;
              margin: 0;
              text-align: center;

              li {
                margin-bottom: 14px;

                a {
                  color: rgba(255, 255, 255, 0.75);
                  text-decoration: none;
                  font-size: 0.98rem;
                  transition: $transition-base;
                  display: inline-block;
                  position: relative;
                  padding: 4px 8px;
                  border-radius: 6px;

                  &::before {
                    content: "";
                    position: absolute;
                    bottom: 6px;
                    left: 50%;
                    width: 0;
                    height: 2px;
                    background: linear-gradient(90deg, transparent, $white, transparent);
                    transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
                    transform: translateX(-50%);
                    opacity: 0;
                  }

                  &:hover {
                    color: $white;
                    background: rgba(255, 255, 255, 0.1);
                    padding-left: 14px;
                    padding-right: 14px;

                    &::before {
                      width: 80%;
                      opacity: 1;
                    }
                  }
                }
              }
            }
          }
        }
      }

      // 页脚底部 - 增强版
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
  }
}

// ===== 动画定义 - 2026 增强版 =====

// 标题光晕动画
@keyframes titleGlow {
  0%, 100% {
    filter: drop-shadow(0 4px 40px rgba(59, 130, 246, 0.4));
  }
  50% {
    filter: drop-shadow(0 8px 60px rgba(139, 92, 246, 0.6));
  }
}

// 图标脉冲动画
@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.15);
  }
}

// 渐变球体浮动动画 - 增强版，更自然流畅
@keyframes orbFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1) rotate(0deg);
  }
  25% {
    transform: translate(60px, -40px) scale(1.08) rotate(5deg);
  }
  50% {
    transform: translate(-30px, 30px) scale(0.92) rotate(-3deg);
  }
  75% {
    transform: translate(40px, 20px) scale(1.05) rotate(2deg);
  }
}

// 页脚背景渐变动画
@keyframes footerGradient {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

// 光泽扫过动画 - 增强版
@keyframes shimmer {
  0% {
    left: -100%;
    opacity: 0;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    left: 100%;
    opacity: 0;
  }
}

// 图标浮动动画 - 增强版
@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-10px) rotate(5deg);
  }
}

// 徽章光晕动画 - 增强版
@keyframes badgeGlow {
  0%, 100% {
    box-shadow:
      0 4px 20px rgba(0, 0, 0, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  }
  50% {
    box-shadow:
      0 4px 30px rgba(0, 0, 0, 0.25),
      0 0 50px rgba(59, 130, 246, 0.5),
      inset 0 1px 0 rgba(255, 255, 255, 0.4);
  }
}

// 星光闪烁动画
@keyframes sparkle {
  0%, 100% {
    transform: scale(1) rotate(0deg);
    opacity: 1;
    filter: drop-shadow(0 2px 10px rgba(255, 255, 255, 0.3));
  }
  50% {
    transform: scale(1.15) rotate(180deg);
    opacity: 0.8;
    filter: drop-shadow(0 4px 20px rgba(255, 255, 255, 0.5));
  }
}

// 标题下划线动画
@keyframes titleUnderline {
  from {
    transform: scaleX(0);
    opacity: 0;
  }
  to {
    transform: scaleX(1);
    opacity: 1;
  }
}

// 骨架屏加载动画
@keyframes skeleton-loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

// 淡入上升动画 - 增强版
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(50px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 淡入下降动画 - 增强版
@keyframes fadeInDown {
  from {
    opacity: 0;
    transform: translateY(-40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 滑入上升动画
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

// 光标闪烁动画
@keyframes cursorBlink {
  0%, 50% {
    opacity: 1;
  }
  51%, 100% {
    opacity: 0;
  }
}

// 粒子漂浮动画 - 增强版
@keyframes particleFloat {
  0%, 100% {
    transform: translate(0, 0) scale(1) rotate(0deg);
    opacity: 0.3;
  }
  25% {
    transform: translate(40px, -40px) scale(1.15) rotate(90deg);
    opacity: 0.6;
  }
  50% {
    transform: translate(-30px, 30px) scale(0.85) rotate(180deg);
    opacity: 0.4;
  }
  75% {
    transform: translate(30px, 20px) scale(1.05) rotate(270deg);
    opacity: 0.5;
  }
}

// 脉冲发光动画
@keyframes pulseGlow {
  0%, 100% {
    box-shadow: 0 0 20px rgba(59, 130, 246, 0.3);
    opacity: 0.8;
  }
  50% {
    box-shadow: 0 0 40px rgba(59, 130, 246, 0.6);
    opacity: 1;
  }
}

// ===== 响应式样式 =====

// 中等屏幕（平板）
@media screen and (max-width: 992px) {
  .home-container {
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
  .container {
    padding: 0 15px;
  }

  .home-container {
    .fullscreen-hero {
      // 竖屏时减少顶部空白
      padding-top: calc(5vh + 10px);
      height: auto;
      min-height: 100vh;
      margin-top: 0;

      .hero-background {
        background-attachment: scroll;

        .gradient-orb {
          &.orb-1 {
            width: 250px;
            height: 250px;
          }
          &.orb-2 {
            width: 300px;
            height: 300px;
          }
          &.orb-3 {
            width: 200px;
            height: 200px;
          }
        }
      }

      .hero-content {
        margin-top: 0;
        padding-top: 20px;
        margin-bottom: 30px;
        width: 100%;

        .hero-title {
          font-size: 2.5rem;
          letter-spacing: 1px;
          margin-bottom: 20px;

          @media screen and (max-width: 480px) {
            font-size: 2rem;
          }
        }

        .title-badge {
          margin-bottom: 16px;
          padding: 10px 20px;
          font-size: 0.9rem;

          @media screen and (max-width: 480px) {
            padding: 8px 16px;
            font-size: 0.85rem;
          }
        }

        .hero-subtitle {
          font-size: 1.1rem;
          display: flex;
          flex-wrap: wrap;
          justify-content: center;
          max-width: 95%;
          margin: 0 auto;
          padding: 10px 16px;

          @media screen and (max-width: 480px) {
            font-size: 1rem;
            padding: 8px 12px;
          }
        }

        .hero-cta {
          margin-top: 30px;
          gap: 12px;

          @media screen and (max-width: 480px) {
            margin-top: 24px;
          }

          .cta-btn {
            padding: 12px 24px;
            font-size: 0.9rem;

            @media screen and (max-width: 480px) {
              padding: 10px 20px;
              font-size: 0.85rem;
            }
          }
        }

        .hero-stats {
          grid-template-columns: repeat(2, 1fr);
          gap: 15px;
          padding: 20px;
          margin-top: 30px;

          .stat-item {
            padding: 15px 10px;

            .stat-icon {
              font-size: 2rem;
            }

            .stat-value {
              .number {
                font-size: 1.8rem;
              }
              .suffix {
                font-size: 1.2rem;
              }
            }

            .stat-label {
              font-size: 0.85rem;
            }
          }
        }
      }
    }

    .main-content {
      padding: 40px 0;

      .latest-articles {
        padding: 20px;
        min-height: 80vh;

        .articles-wrapper {
          min-height: 60vh;
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

    // 快速导航在 768px 以下调整间距
    .fullscreen-hero {
      .quick-nav {
        gap: 16px;
        margin-top: 40px;
      }
    }

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
    .fullscreen-hero {
      // 超小屏幕减少顶部空白
      padding-top: calc(5vh + 10px);
      height: auto;
      min-height: 100vh;
      margin-top: 0;
      overflow: visible;

      .hero-content {
        margin-top: 0;
        padding: 0 15px;
        margin-bottom: 25px;

        .hero-title {
          font-size: 2rem;
          letter-spacing: 0.5px;
        }

        .hero-subtitle {
          font-size: 0.95rem;
          padding: 8px 12px;
        }

        .hero-stats {
          grid-template-columns: 1fr;
          gap: 12px;
          padding: 15px;
        }
      }
    }

    .main-content {
      padding: 30px 0;

      .latest-articles {
        padding: 15px;
        min-height: 75vh;

        .articles-wrapper {
          min-height: 55vh;
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
                height: 1.2rem;

                .category {
                  font-size: 0.75rem;
                  padding: 3px 8px;
                }
              }

              h3 {
                font-size: 1.1rem;
                height: 3.08rem;
              }

              .article-description {
                font-size: 0.9rem;
                height: 2.88rem;
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
</style>
