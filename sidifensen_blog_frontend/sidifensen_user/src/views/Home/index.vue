<template>
  <div class="home-container">

    <!-- Hero 区域 -->
    <section class="hero" ref="heroRef" @mousemove="handleMouseMove" @mouseleave="handleMouseLeave">
      <!-- 粒子背景层 -->
      <div class="hero-particles">
        <div v-for="(style, i) in particleStyles" :key="i" class="particle" :style="style"></div>
      </div>
      <!-- 星星闪烁层 -->
      <div class="hero-stars">
        <div v-for="(star, i) in stars" :key="i" class="star" :style="star.style"></div>
      </div>
      <!-- 网格背景 -->
      <div class="hero-grid"></div>

      <!-- 鼠标跟随光效 -->
      <div class="hero-glow" ref="heroGlow"></div>

      <div class="hero-content">
        <div class="hero-badge">
          <span class="dot"></span>
          <span>欢迎来到斯蒂芬森社区</span>
        </div>

        <h1 class="hero-title">
          <span class="title-line" @mouseenter="onTitleHover(true)" @mouseleave="onTitleHover(false)">
            探索技术的<br>
            <span class="underline-wrap">
              <span class="gradient">无限可能</span>
              <span class="underline"></span>
            </span>
          </span>
        </h1>

        <div class="hero-subtitle">
          <div class="hero-typewriter">
            <span class="hero-typewriter-text">{{ typedSubtitle }}</span>
            <span class="hero-typewriter-cursor"></span>
          </div>
        </div>

        <div class="hero-cta">
          <button class="btn btn-primary btn-lg" @click="navigateTo('/article')">
            开始探索
          </button>
        </div>

        <div class="hero-nav">
          <div
            class="hero-nav-item"
            v-for="(item, index) in quickNavItems"
            :key="index"
            @click="navigateTo(item.path)"
          >
            <span class="icon">{{ item.icon }}</span>
            <span>{{ item.title }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 主内容区 -->
    <main class="main-content" ref="contentSection">
      <div class="section-header">
        <h2 class="section-title">最新文章</h2>
        <router-link to="/article" class="section-link">
          查看全部
          <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M5 12h14M12 5l7 7-7 7"/>
          </svg>
        </router-link>
      </div>

      <div class="content-wrapper">
        <div class="article-grid" ref="articlesSectionRef">
          <!-- 加载状态 -->
          <template v-if="articleLoading">
            <SkeletonLoader type="article" :count="6" />
          </template>

          <!-- 空状态 -->
          <template v-else-if="articles.length === 0">
            <div class="empty-state">
              <EmptyState type="article" />
            </div>
          </template>

          <!-- 文章列表 -->
          <template v-else>
            <ArticleCard
              v-for="(article, index) in articles"
              :key="article.id"
              :article="article"
              mode="grid"
              @click="goToArticle(article)"
            />
          </template>
        </div>

        <!-- 右侧边栏 -->
        <aside class="sidebar">
          <!-- 项目链接 -->
          <SidebarCard title="项目链接" icon="📦">
            <div class="project-links">
              <a href="https://github.com/sidifensen//sidifensen_blog" target="_blank" class="project-link">
                <svg-icon name="github" width="20px" height="20px" class="github-icon" />
                <div class="project-link-info">
                  <div class="project-link-name">GitHub</div>
                  <div class="project-link-desc">开源项目仓库</div>
                </div>
              </a>
              <a href="https://gitee.com/sidifensen/sidifensen_blog" target="_blank" class="project-link">
                <svg-icon name="gitee" width="20px" height="20px" class="gitee-icon" />
                <div class="project-link-info">
                  <div class="project-link-name">Gitee</div>
                  <div class="project-link-desc">国内镜像</div>
                </div>
              </a>
            </div>
          </SidebarCard>

          <!-- 热门标签 -->
          <SidebarCard title="热门标签" icon="🏷️">
            <TagCloud :tags="hotTags" />
          </SidebarCard>

          <!-- 社区统计 -->
          <SidebarCard title="社区统计" icon="📊">
            <StatsCard :stats="statsList" :animated="true" :columns="2" />
          </SidebarCard>
        </aside>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <h3>sidifensen 社区</h3>
          <p>分享技术、经验和见解，用代码改变世界</p>
        </div>

        <div class="footer-column">
          <h4>快速导航</h4>
          <ul>
            <li><router-link to="/">首页</router-link></li>
            <li><router-link to="/article">文章</router-link></li>
            <li><router-link to="/album">相册</router-link></li>
            <li><router-link to="/link">友链</router-link></li>
          </ul>
        </div>

        <div class="footer-column">
          <h4>关于我们</h4>
          <ul>
            <li><a href="javascript:void(0)" class="footer-link">关于</a></li>
            <li><a href="javascript:void(0)" class="footer-link">联系方式</a></li>
            <li><a href="javascript:void(0)" class="footer-link">隐私政策</a></li>
            <li><a href="javascript:void(0)" class="footer-link">服务条款</a></li>
          </ul>
        </div>
      </div>

      <div class="footer-bottom">
        <p>© {{ currentYear }} sidifensen 社区。All Rights Reserved.</p>
        <p>
          <a href="https://beian.miit.gov.cn/" target="_blank" rel="noopener noreferrer">粤 ICP 备 2024324512 号 -2</a>
        </p>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Loading, Picture } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { getAllArticleList, getHotTags } from "@/api/article";
import { formatCompactNumber } from "@/utils/formatNumber";
import { useUserStore } from "@/stores/userStore";
import { getCommunityStats } from "@/api/user";
import Header from "@/components/Header.vue";
import ArticleCard from "@/components/ArticleCard.vue";
import SidebarCard from "@/components/SidebarCard.vue";
import TagCloud from "@/components/TagCloud.vue";
import StatsCard from "@/components/StatsCard.vue";
import EmptyState from "@/components/EmptyState.vue";
import SkeletonLoader from "@/components/SkeletonLoader.vue";
import { useSeoMeta } from "@/plugins/seo";

// SEO - 首页
useSeoMeta({
  title: "首页",
  description: "探索技术的无限可能，斯蒂芬森社区是一个分享技术文章、生活感悟和创意作品的多元化平台",
  keywords: "技术博客,程序员,前端,后端,全栈,编程,开发者社区,斯蒂芬森"
});

// 路由和用户状态
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const articles = ref([]);
const articleLoading = ref(false);
const currentYear = ref(new Date().getFullYear());
const contentSection = ref(null);
const articlesSectionRef = ref(null);
const hotTags = ref([]); // 热门标签
const heroRef = ref(null);
const heroGlow = ref(null);

// 社区统计数据
const stats = ref({
  articleCount: 0,
  userCount: 0,
  viewCount: 0,
  authorCount: 0,
});

// 用于 StatsCard 组件的统计数据格式
const statsList = ref([
  { value: 0, label: "文章总数" },
  { value: 0, label: "注册用户" },
  { value: 0, label: "总阅读量" },
  { value: 0, label: "活跃作者" },
]);

// 动画显示的数字（用于本地显示）
const animatedStats = ref({
  articleCount: 0,
  userCount: 0,
  viewCount: 0,
  authorCount: 0,
});

// 打字机效果
const typedSubtitle = ref("");
let subtitleIndex = 0;
let charIndex = 0;
let isDeleting = false;

// 快速导航项
const quickNavItems = ref([
  { title: "技术文章", path: "/article", icon: "📚" },
  { title: "相册空间", path: "/album", icon: "📷" },
  { title: "友情链接", path: "/link", icon: "🔗" },
]);

// 粒子样式 - 响应式引用，mounted 时生成一次真正的随机值
const particleStyles = ref([]);

// 星星数据 - 每个星星有独立的状态、位置和定时器
const stars = ref([]);
let starTimers = [];

const generateParticleStyles = () => {
  particleStyles.value = Array.from({ length: 30 }, () => ({
    left: `${Math.random() * 100}%`,
    animationDelay: `${Math.random() * 15}s`,
    animationDuration: `${15 + Math.random() * 10}s`,
    width: `${2 + Math.random() * 3}px`,
    height: `${2 + Math.random() * 3}px`,
  }));

  // 生成星星 - JS 控制闪烁和位置切换
const createStar = () => ({
  style: {
    left: `${Math.random() * 100}%`,
    top: `${12 + Math.random() * 16}%`,
    width: `${1 + Math.random() * 2}px`,
    height: `${1 + Math.random() * 2}px`,
    opacity: 0,
  },
});

stars.value = Array.from({ length: 12 }, createStar);

// 启动星星闪烁定时器
starTimers.forEach(clearInterval);
starTimers = [];

stars.value.forEach((star, index) => {
  const flashStar = () => {
    // 随机闪烁次数（1-4次）
    const flashCount = 1 + Math.floor(Math.random() * 4);
    let currentFlash = 0;

    const doFlash = () => {
      currentFlash++;
      // 亮起 - 每次亮度都随机
      star.style.opacity = 0.5 + Math.random() * 0.5;
      star.style.transform = 'scale(1)';

      // 亮持续时间（2-4秒）
      const lightDuration = 2000 + Math.random() * 2000;
      setTimeout(() => {
        // 熄灭
        star.style.opacity = 0;
        star.style.transform = 'scale(0.8)';

        // 如果还没到次数，等待后再次亮起
        if (currentFlash < flashCount) {
          const offDuration = 1500 + Math.random() * 2000;
          starTimers[index] = setTimeout(doFlash, offDuration);
        } else {
          // 次数用完了，消失并换位置重新生成
          starTimers[index] = setTimeout(() => {
            star.style.left = `${Math.random() * 100}%`;
            star.style.top = `${12 + Math.random() * 16}%`;
            flashStar(); // 重新开始
          }, 10000 + Math.random() * 10000); // 10-20秒后再出现
        }
      }, lightDuration);
    };

    doFlash();
  };

  // 初始延迟启动
  starTimers[index] = setTimeout(flashStar, Math.random() * 5000);
});
};

const subtitles = [
  "在这里，发现知识 · 分享经验 · 连接开发者",
  "探索代码的无限可能",
  "与开发者共同成长",
  "记录技术旅程的点点滴滴",
];

// 打字机速度
const TYPE_SPEED = 100;
const DELETE_SPEED = 50;
const PAUSE_AFTER_TYPE = 2000;
const PAUSE_AFTER_DELETE = 500;

// 打字机逻辑
const typeSubtitle = () => {
  const current = subtitles[subtitleIndex];
  if (!isDeleting) {
    if (charIndex < current.length) {
      typedSubtitle.value = current.substring(0, charIndex + 1);
      charIndex++;
      setTimeout(typeSubtitle, TYPE_SPEED);
    } else {
      isDeleting = true;
      setTimeout(typeSubtitle, PAUSE_AFTER_TYPE);
    }
  } else {
    if (charIndex > 0) {
      typedSubtitle.value = current.substring(0, charIndex - 1);
      charIndex--;
      setTimeout(typeSubtitle, DELETE_SPEED);
    } else {
      isDeleting = false;
      subtitleIndex = (subtitleIndex + 1) % subtitles.length;
      charIndex = 0;
      setTimeout(typeSubtitle, PAUSE_AFTER_DELETE);
    }
  }
};

// 鼠标跟随光效
const handleMouseMove = (e) => {
  if (!heroRef.value || !heroGlow.value) return;

  const rect = heroRef.value.getBoundingClientRect();
  const x = e.clientX - rect.left;
  const y = e.clientY - rect.top;

  heroGlow.value.style.background = `radial-gradient(
    300px circle at ${x}px ${y}px,
    rgba(0, 102, 255, 0.12),
    transparent 40%
  )`;
  heroGlow.value.style.opacity = '1';
};

const handleMouseLeave = () => {
  if (heroGlow.value) {
    heroGlow.value.style.opacity = '0';
  }
};

// 滚动入场动画
const observeElements = () => {
  const observer = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          entry.target.classList.add("visible");
          observer.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.1 }
  );

  // 观察每个文章卡片
  const articleCards = document.querySelectorAll('.article-card:not(.skeleton)');
  articleCards.forEach((card, index) => {
    card.style.transitionDelay = `${index * 80}ms`;
    observer.observe(card);
  });
};

// 获取文章列表
const fetchArticleList = async () => {
  try {
    articleLoading.value = true;
    const res = await getAllArticleList(1, 6);
    articles.value = res.data?.data || [];
  } catch (error) {
    ElMessage.error("获取文章列表失败");
  } finally {
    articleLoading.value = false;
  }
};

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  return new Date(dateString).toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 格式化数字
const formatNumber = (num) => formatCompactNumber(num);

// 判断新文章（3 天内）
const isNewArticle = (article) => {
  const diffDays = (Date.now() - new Date(article.createTime)) / (1000 * 60 * 60 * 24);
  return diffDays <= 3;
};

// 路由跳转
const getArticleDetailRoute = (article) => `/user/${article.userId}/article/${article.id}`;
const goToArticle = (article) => router.push(getArticleDetailRoute(article));
const navigateTo = (path) => router.push(path);

// 跳转到标签搜索
const navigateToTag = (tagName) => {
  router.push({
    path: "/search",
    query: {
      keyword: tagName,
      type: "tag",
    },
  });
};

// 登录注册处理
const handleLogin = () => {
  if (userStore.isLoggedIn) {
    router.push("/account");
  } else {
    router.push("/login");
  }
};

const handleRegister = () => {
  if (userStore.isLoggedIn) {
    router.push("/account");
  } else {
    router.push("/register");
  }
};

// 加载热门标签
const loadHotTags = async () => {
  try {
    const res = await getHotTags(10);
    hotTags.value = res.data || [];
  } catch (error) {
    // 静默处理
  }
};

// 获取社区统计数据
const loadCommunityStats = async () => {
  try {
    const res = await getCommunityStats();
    const data = res.data || {};
    stats.value = {
      articleCount: data.articleCount || 0,
      userCount: data.userCount || 0,
      viewCount: data.viewCount || 0,
      authorCount: data.authorCount || 0,
    };
    // 更新 statsList（用于 StatsCard 组件）
    statsList.value = [
      { value: data.articleCount || 0, label: "文章总数" },
      { value: data.userCount || 0, label: "注册用户" },
      { value: data.viewCount || 0, label: "总阅读量" },
      { value: data.authorCount || 0, label: "活跃作者" },
    ];
    // 数据加载完成后，开始数字动画
    setTimeout(() => {
      animateNumber("articleCount", stats.value.articleCount);
      animateNumber("userCount", stats.value.userCount);
      animateNumber("viewCount", stats.value.viewCount);
      animateNumber("authorCount", stats.value.authorCount);
    }, 500);
  } catch (error) {
    // 静默处理
  }
};

// 数字动画 - 从 0 逐渐增加到目标值，显示整数
const animateNumber = (key, target) => {
  if (target <= 0) {
    animatedStats.value[key] = 0;
    return;
  }

  const duration = 2000; // 动画持续时间 2 秒
  const frameRate = 16; // 约 60fps
  const totalFrames = duration / frameRate;
  const increment = target / totalFrames;
  let current = 0;
  let frame = 0;

  const timer = setInterval(() => {
    frame++;
    current += increment;

    if (frame >= totalFrames) {
      current = target;
      clearInterval(timer);
    }

    // 使用 Math.floor 确保显示整数，不显示小数点
    animatedStats.value[key] = Math.floor(current);
  }, frameRate);
};

onMounted(async () => {
  generateParticleStyles();
  await Promise.all([
    fetchArticleList(),
    loadHotTags(),
    loadCommunityStats()
  ]);
  // 数据加载完成后，等待 DOM 更新再观察元素
  setTimeout(() => {
    observeElements();
  }, 100);
  setTimeout(typeSubtitle, 1000);
});
</script>

<style lang="scss" scoped>
// ===== CSS 变量 =====
.home-container {
  --bg-page: #ffffff;
  --bg-card: #fafafa;
  --bg-subtle: #f5f5f5;
  --text-primary: #1a1a1a;
  --text-secondary: #666666;
  --text-muted: #999999;
  --border: #e5e5e5;
  --accent: #0066ff;
  --accent-gradient: linear-gradient(135deg, #0066ff 0%, #00c2ff 100%);
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06);
  --shadow-md: 0 8px 24px rgba(0, 0, 0, 0.08);
  --shadow-lg: 0 12px 48px rgba(0, 0, 0, 0.12);

  min-height: 100vh;
  background: var(--bg-page);
  color: var(--text-primary);
}

// ===== 黑夜模式 =====
html.dark {
  .home-container {
    --bg-page: #0a0a0a;
    --bg-card: #111111;
    --bg-subtle: #1a1a1a;
    --text-primary: #ededed;
    --text-secondary: #a0a0a0;
    --text-muted: #666666;
    --border: #333333;
    --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.3);
    --shadow-md: 0 8px 24px rgba(0, 0, 0, 0.4);
    --shadow-lg: 0 12px 48px rgba(0, 0, 0, 0.5);
  }
}

// ===== 按钮 =====
.btn {
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
  border: none;

  &.btn-ghost {
    background: transparent;
    color: var(--text-secondary);

    &:hover {
      background: var(--bg-subtle);
    }
  }

  &.btn-primary {
    background: var(--accent);
    color: #fff;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      inset: 0;
      background: linear-gradient(
        135deg,
        rgba(255, 255, 255, 0.2) 0%,
        transparent 50%,
        rgba(255, 255, 255, 0.1) 100%
      );
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover {
      background: #0052cc;
      transform: translateY(-2px);
      box-shadow:
        0 4px 12px rgba(0, 102, 255, 0.4),
        0 0 20px rgba(0, 102, 255, 0.3);

      &::before {
        opacity: 1;
      }
    }

    &:active {
      transform: translateY(0);
    }
  }

  &.btn-lg {
    padding: 14px 40px;
    font-size: 1rem;
  }
}

// ===== Hero 区域 =====
.hero {
  position: relative;
  min-height: calc(100vh - 48px);
  padding-top: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: var(--bg-page);
  overflow: hidden;

  // 粒子层 - 从下往上缓缓升起
  .hero-particles {
    position: absolute;
    inset: 0;
    pointer-events: none;
    overflow: hidden;
  }

  .particle {
    position: absolute;
    bottom: -10px;
    border-radius: 50%;
    background: rgba(0, 102, 255, 0.5);
    animation: particleRise linear infinite;
    box-shadow: 0 0 4px rgba(0, 102, 255, 0.3);
    opacity: 0.5; // 统一透明度

    html.dark & {
      background: rgba(100, 180, 255, 0.4);
      box-shadow: 0 0 6px rgba(100, 180, 255, 0.3);
    }
  }

  // 星星闪烁层
  .hero-stars {
    position: absolute;
    inset: 0;
    pointer-events: none;
    z-index: 0;
  }

  .star {
    position: absolute;
    border-radius: 50%;
    background: rgba(255, 255, 255, 1);
    box-shadow: 0 0 6px 2px rgba(255, 255, 255, 0.8), 0 0 12px 4px rgba(200, 220, 255, 0.4);
    transition: opacity 0.3s ease, transform 0.3s ease;

    html.dark & {
      background: rgba(220, 235, 255, 1);
      box-shadow: 0 0 8px 2px rgba(200, 220, 255, 0.9), 0 0 16px 6px rgba(150, 180, 255, 0.5);
    }
  }

  // 网格背景
  .hero-grid {
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(0, 0, 0, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0, 0, 0, 0.03) 1px, transparent 1px);
    background-size: 40px 40px;
    pointer-events: none;

    html.dark & {
      background-image:
        linear-gradient(rgba(255, 255, 255, 0.02) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255, 255, 255, 0.02) 1px, transparent 1px);
    }
  }

  // 鼠标跟随光效
  .hero-glow {
    position: absolute;
    inset: 0;
    pointer-events: none;
    opacity: 0;
    transition: opacity 0.3s ease;
    z-index: 1;
  }

  .hero-content {
    position: relative;
    z-index: 2;
    text-align: center;
    max-width: 900px;
    padding: 0 24px;
  }

  // 徽章
  .hero-badge {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 6px 16px;
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-radius: 9999px;
    font-size: 0.9rem;
    color: var(--text-secondary);
    margin-bottom: 40px;
    animation: fadeInUp 0.6s ease-out both;
    transform: translateY(-8px);

    .dot {
      width: 6px;
      height: 6px;
      background: #10b981;
      border-radius: 50%;
      animation: pulse 2s ease-in-out infinite;
    }
  }

  // 标题
  .hero-title {
    font-size: clamp(2.5rem, 6vw, 4rem);
    font-weight: 700;
    letter-spacing: -0.02em;
    line-height: 1.1;
    margin-bottom: 32px;
    animation: fadeInUp 0.6s ease-out 0.15s both;
    color: var(--text-primary);

    .title-line {
      display: inline-block;
      position: relative;
      cursor: default;
    }

    .underline-wrap {
      position: relative;
      display: inline-block;

      .underline {
        position: absolute;
        bottom: -4px;
        left: 0;
        width: 0;
        height: 3px;
        background: var(--accent-gradient);
        border-radius: 2px;
        transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
      }

      &:hover .underline {
        width: 100%;
      }
    }

    .gradient {
      background: var(--accent-gradient);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      display: inline-block;
      transition: transform 0.3s ease;

      &:hover {
        transform: translateY(-2px);
      }
    }

    @media (max-width: 768px) {
      font-size: 2.5rem;
    }
  }

  // 副标题
  .hero-subtitle {
    font-size: 1.125rem;
    color: var(--text-secondary);
    max-width: 600px;
    margin: 0 auto 48px;
    animation: fadeInUp 0.6s ease-out 0.3s both;

    .hero-typewriter {
      min-height: 1.6em;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;

      &-text {
        font-weight: 500;
        position: relative;
        transition: color 0.3s ease;

        &::after {
          content: '';
          position: absolute;
          bottom: -2px;
          left: 0;
          width: 0;
          height: 2px;
          background: var(--accent);
          transition: width 0.3s ease;
        }

        &:hover {
          color: var(--accent);

          &::after {
            width: 100%;
          }
        }
      }

      &-cursor {
        width: 2px;
        height: 1.2em;
        background: var(--accent);
        animation: cursorBlink 1s step-end infinite;
      }
    }
  }

  // CTA 按钮
  .hero-cta {
    display: flex;
    gap: 12px;
    justify-content: center;
    margin-top: 56px;
    animation: fadeInUp 0.6s ease-out 0.45s both;
  }

  // 快速导航
  .hero-nav {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 14px;
    margin-top: 64px;
    animation: fadeInUp 0.6s ease-out 0.6s both;

    &-item {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 10px 20px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 9999px;
      font-size: 0.875rem;
      color: var(--text-secondary);
      cursor: pointer;
      transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);

      .icon {
        font-size: 1rem;
        transition: transform 0.25s ease;
      }

      &:hover {
        background: var(--bg-subtle);
        border-color: var(--accent);
        color: var(--accent);
        transform: translateY(-3px);
        box-shadow: 0 4px 12px rgba(0, 102, 255, 0.15);

        .icon {
          transform: scale(1.2);
        }
      }
    }
  }
}

// ===== 主内容区 =====
.main-content {
  padding: 80px 24px;
  max-width: 1200px;
  margin: 0 auto;

  .section-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 32px;
    padding-bottom: 20px;
    border-bottom: 1px solid var(--border);
  }

  .section-title {
    font-size: 1.5rem;
    font-weight: 600;
    letter-spacing: -0.01em;
    display: flex;
    align-items: center;
    gap: 12px;
    color: var(--text-primary);

    &::before {
      content: '';
      width: 4px;
      height: 20px;
      background: var(--accent-gradient);
      border-radius: 2px;
    }
  }

  .section-link {
    color: var(--text-secondary);
    text-decoration: none;
    font-size: 0.875rem;
    font-weight: 500;
    display: flex;
    align-items: center;
    gap: 6px;
    transition: color 0.2s ease;

    &:hover {
      color: var(--accent);
    }
  }
}

// ===== 内容网格 =====
.content-wrapper {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 40px;
  margin-top: 60px;

  @media (max-width: 992px) {
    grid-template-columns: 1fr;
  }
}

// ===== 文章网格 =====
.article-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;

  @media (max-width: 992px) {
    grid-template-columns: repeat(2, 1fr);
  }

  @media (max-width: 768px) {
    grid-template-columns: 1fr;
  }
}

// ===== 文章卡片 =====
.article-card {
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  opacity: 0;
  transform: translateY(20px);

  &.visible {
    opacity: 1;
    transform: translateY(0);
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: var(--shadow-md);
    border-color: rgba(0, 102, 255, 0.2);
  }

  // 骨架屏
  &.skeleton {
    .article-cover-skeleton {
      width: 100%;
      height: 140px;
      background: var(--bg-subtle);
      animation: skeleton-pulse 1.5s ease-in-out infinite;
    }

    .article-content {
      padding: 12px 16px;
    }

    .article-tags-skeleton {
      width: 60px;
      height: 18px;
      background: var(--bg-subtle);
      border-radius: 4px;
      margin-bottom: 8px;
      animation: skeleton-pulse 1.5s ease-in-out infinite;
    }

    .article-title-skeleton {
      width: 100%;
      height: 20px;
      background: var(--bg-subtle);
      border-radius: 4px;
      margin-bottom: 8px;
      animation: skeleton-pulse 1.5s ease-in-out infinite;
    }

    .article-excerpt-skeleton {
      width: 100%;
      height: 32px;
      background: var(--bg-subtle);
      border-radius: 4px;
      margin-bottom: 10px;
      animation: skeleton-pulse 1.5s ease-in-out infinite;
    }

    .article-meta-skeleton {
      width: 100%;
      height: 14px;
      background: var(--bg-subtle);
      border-radius: 4px;
      animation: skeleton-pulse 1.5s ease-in-out infinite;
    }
  }

  .article-image {
    width: 100%;
    height: 140px;
    overflow: hidden;
    background: var(--bg-subtle);

    .article-cover {
      width: 100%;
      height: 100%;
      transition: transform 0.4s ease;
    }

    &:hover .article-cover {
      transform: scale(1.05);
    }

    .image-placeholder,
    .image-error {
      width: 100%;
      height: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: var(--text-muted);
      font-size: 24px;
    }
  }

  .article-content {
    padding: 4px 12px 12px;
    display: flex;
    flex-direction: column;
    margin-top: auto; // 将内容推到底部
  }

  .article-tags {
    display: flex;
    gap: 6px;
    margin-bottom: 8px;

    .tag {
      padding: 3px 8px;
      border-radius: 6px;
      font-size: 0.7rem;
      font-weight: 500;

      &.tag-hot {
        background: rgba(239, 68, 68, 0.1);
        color: #dc2626;
      }

      &.tag-new {
        background: rgba(0, 102, 255, 0.1);
        color: var(--accent);
      }
    }
  }

  .article-title {
    font-size: 0.95rem;
    font-weight: 600;
    line-height: 1.4;
    margin-top: 0;
    margin-bottom: 8px;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    color: var(--text-primary);
  }

  .article-excerpt {
    font-size: 0.8rem;
    color: var(--text-secondary);
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 2;
    -webkit-box-orient: vertical;
    overflow: hidden;
    margin-bottom: 10px;
  }

  .article-meta {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding-top: 10px;
    border-top: 1px solid var(--border);
  }

  .article-date {
    font-size: 0.7rem;
    color: var(--text-muted);
  }

  .article-stats {
    display: flex;
    gap: 8px;

    .stat {
      display: flex;
      align-items: center;
      gap: 4px;
      font-size: 0.75rem;
      color: var(--text-muted);
    }

    .heart-icon {
      font-size: 1rem;
    }
  }
}

// ===== 空状态 =====
.empty-state {
  grid-column: 1 / -1;
  padding: 80px 0;
  text-align: center;
}

// ===== 侧边栏 =====
.sidebar {
  display: flex;
  flex-direction: column;
  gap: 24px;

  @media (max-width: 992px) {
    display: none;
  }
}

// ===== 侧边栏卡片 =====
.sidebar-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 14px;
}

.sidebar-title {
  font-size: 0.875rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  color: var(--text-muted);
  margin-bottom: 10px;
}

// ===== 项目链接 =====
.project-links {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.project-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: var(--bg-page);
  border: 1px solid var(--border);
  border-radius: 8px;
  text-decoration: none;
  color: var(--text-primary);
  transition: all 0.2s ease;

  &:hover {
    border-color: var(--accent);
    background: rgba(0, 102, 255, 0.04);
  }

  .project-link-icon {
    flex-shrink: 0;

    &.github-icon {
      color: #24292e;

      html.dark & {
        color: #94a3b8;
      }
    }

    &.gitee-icon {
      color: #c71d23;

      html.dark & {
        color: #c71d23;
      }
    }
  }

  .project-link-info {
    flex: 1;

    .project-link-name {
      font-size: 0.875rem;
      font-weight: 500;
      color: var(--text-primary);
    }

    .project-link-desc {
      font-size: 0.75rem;
      color: var(--text-muted);
    }
  }
}

// ===== 标签云 =====
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-cloud-item {
  padding: 6px 14px;
  background: var(--bg-page);
  border: 1px solid var(--border);
  border-radius: 9999px;
  font-size: 0.8rem;
  color: var(--text-secondary);
  cursor: pointer;
  transition: all 0.2s ease;

  &:hover {
    border-color: var(--accent);
    color: var(--accent);
  }
}

// ===== 社区统计 =====
.stats-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: var(--bg-page);
  border-radius: 8px;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: var(--accent);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.75rem;
  color: var(--text-muted);
}

// ===== 页脚 =====
.footer {
  background: var(--bg-card);
  border-top: 1px solid var(--border);
  padding: 60px 24px 32px;
  margin-top: 80px;

  .footer-content {
    max-width: 1200px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 2fr 1fr 1fr;
    gap: 60px;
    margin-bottom: 40px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
      gap: 32px;
    }
  }

  .footer-brand {
    h3 {
      font-size: 1.125rem;
      font-weight: 700;
      margin-bottom: 12px;
      color: var(--text-primary);
    }

    p {
      font-size: 0.875rem;
      color: var(--text-secondary);
      line-height: 1.7;
    }
  }

  .footer-column {
    h4 {
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      color: var(--text-muted);
      margin-bottom: 16px;
    }

    ul {
      list-style: none;
      margin: 0;
      padding: 0;

      li {
        margin-bottom: 10px;

        a {
          color: var(--text-secondary);
          text-decoration: none;
          font-size: 0.875rem;
          transition: color 0.2s ease;
          cursor: pointer;

          &:hover {
            color: var(--accent);
          }
        }
      }
    }
  }

  .footer-bottom {
    max-width: 1200px;
    margin: 0 auto;
    padding-top: 32px;
    border-top: 1px solid var(--border);
    display: flex;
    align-items: center;
    justify-content: space-between;
    flex-wrap: wrap;
    gap: 12px;

    p {
      font-size: 0.75rem;
      color: var(--text-muted);

      a {
        color: var(--text-muted);
        text-decoration: none;
        transition: color 0.2s ease;

        &:hover {
          color: var(--accent);
        }
      }
    }
  }
}

// ===== 动画 =====
// 粒子从下往上缓缓升起
@keyframes particleRise {
  0% {
    transform: translateY(0) scale(1);
    opacity: 0;
  }
  5% {
    opacity: 0.7;
  }
  90% {
    opacity: 0.7;
  }
  100% {
    transform: translateY(-100vh) scale(0.5);
    opacity: 0;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}

@keyframes cursorBlink {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

@keyframes scrollDrop {
  0% {
    transform: scaleY(0);
    transform-origin: top;
    opacity: 0;
  }
  40% {
    transform: scaleY(1);
    transform-origin: top;
    opacity: 1;
  }
  100% {
    transform: scaleY(1);
    transform-origin: top;
    opacity: 0;
  }
}

@keyframes skeleton-pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
