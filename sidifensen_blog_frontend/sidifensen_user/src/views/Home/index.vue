<template>
  <div class="home-container">
    <!-- 使用原有 Header 组件 -->
    <Header />

    <!-- Hero 区域 -->
    <section class="hero">
      <div class="hero-content">
        <div class="hero-badge">
          <span class="dot"></span>
          <span>欢迎来到斯蒂芬森社区</span>
        </div>

        <h1 class="hero-title">
          探索技术的<br>
          <span class="gradient">无限可能</span>
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
            <div v-for="i in 6" :key="i" class="article-card skeleton">
              <div class="article-cover-skeleton"></div>
              <div class="article-content">
                <div class="article-tags-skeleton"></div>
                <div class="article-title-skeleton"></div>
                <div class="article-excerpt-skeleton"></div>
                <div class="article-meta-skeleton"></div>
              </div>
            </div>
          </template>

          <!-- 空状态 -->
          <template v-else-if="articles.length === 0">
            <div class="empty-state">
              <el-empty description="暂无文章" />
            </div>
          </template>

          <!-- 文章列表 -->
          <template v-else>
            <div
              v-for="(article, index) in articles"
              :key="article.id"
              class="article-card"
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
              </div>
              <div class="article-content">
                <div class="article-tags">
                  <span class="tag tag-hot" v-if="article.readCount > 1000">🔥 热门</span>
                  <span class="tag tag-new" v-else-if="isNewArticle(article)">✨ 新作</span>
                </div>
                <h3 class="article-title">{{ article.title }}</h3>
                <p class="article-excerpt">{{ article.description || "暂无描述" }}</p>
                <div class="article-meta">
                  <span class="article-date">{{ formatDate(article.createTime) }}</span>
                  <div class="article-stats">
                    <span class="stat">👁 {{ formatNumber(article.readCount || 0) }}</span>
                    <span class="stat">❤️ {{ formatNumber(article.likeCount || 0) }}</span>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </div>

        <!-- 右侧边栏 -->
        <aside class="sidebar">
          <!-- 项目链接 -->
          <div class="sidebar-card">
            <h4 class="sidebar-title">📦 项目链接</h4>
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
          </div>

          <!-- 热门标签 -->
          <div class="sidebar-card">
            <h4 class="sidebar-title">🏷️ 热门标签</h4>
            <div class="tag-cloud">
              <span
                v-for="(tag, index) in hotTags"
                :key="index"
                class="tag-cloud-item"
                @click="navigateToTag(tag.name)"
              >
                {{ tag.name }}
              </span>
            </div>
          </div>

          <!-- 社区统计 -->
          <div class="sidebar-card">
            <h4 class="sidebar-title">📊 社区统计</h4>
            <div class="stats-list">
              <div class="stat-item">
                <div class="stat-value">{{ animatedStats.articleCount }}</div>
                <div class="stat-label">文章总数</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ animatedStats.userCount }}</div>
                <div class="stat-label">注册用户</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ animatedStats.viewCount }}</div>
                <div class="stat-label">总阅读量</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ animatedStats.authorCount }}</div>
                <div class="stat-label">活跃作者</div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="footer-content">
        <div class="footer-brand">
          <h3>sidifensen 社区</h3>
          <p>分享技术、经验和见解，用代码改变世界</p>
          <div class="social-links">
            <a href="https://github.com/sidifensen" target="_blank" class="social-link" title="GitHub">GH</a>
            <a href="https://gitee.com/sidifensen" target="_blank" class="social-link" title="Gitee">GT</a>
            <a href="#" class="social-link" title="Twitter">TW</a>
          </div>
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
            <li><router-link to="/about">关于</router-link></li>
            <li><router-link to="/contact">联系方式</router-link></li>
            <li><a href="#">隐私政策</a></li>
            <li><a href="#">服务条款</a></li>
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

// 社区统计数据
const stats = ref({
  articleCount: 0,
  userCount: 0,
  viewCount: 0,
  authorCount: 0,
});

// 动画显示的数字
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

// 副标题列表
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
    console.log('API 响应 res:', res);
    console.log('res.data:', res.data);
    console.log('res.data.data:', res.data.data);
    console.log('res.data.data.data:', res.data.data.data);

    articles.value = res.data?.data?.data || [];
    console.log('最终 articles:', articles.value);
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
    hotTags.value = res.data.data || [];
  } catch (error) {
    console.error("加载热门标签失败:", error);
  }
};

// 获取社区统计数据
const loadCommunityStats = async () => {
  try {
    const res = await getCommunityStats();
    const data = res.data.data || {};
    stats.value = {
      articleCount: data.articleCount || 0,
      userCount: data.userCount || 0,
      viewCount: data.viewCount || 0,
      authorCount: data.authorCount || 0,
    };
    // 数据加载完成后，开始数字动画
    setTimeout(() => {
      animateNumber("articleCount", stats.value.articleCount);
      animateNumber("userCount", stats.value.userCount);
      animateNumber("viewCount", stats.value.viewCount);
      animateNumber("authorCount", stats.value.authorCount);
    }, 500);
  } catch (error) {
    console.error("加载社区统计数据失败:", error);
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

    &:hover {
      background: #0052cc;
      transform: translateY(-1px);
      box-shadow: 0 4px 12px rgba(0, 102, 255, 0.4);
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
  min-height: calc(100vh - 48px); // 减去 Header 高度
  padding-top: 20px; // Header 已经占 48px，这里只需要少量 padding
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background:
    radial-gradient(ellipse at top, rgba(0, 102, 255, 0.08) 0%, transparent 60%),
    radial-gradient(ellipse at bottom right, rgba(0, 194, 255, 0.06) 0%, transparent 50%),
    var(--bg-page);
  overflow: hidden;

  // 网格背景
  &::before {
    content: '';
    position: absolute;
    inset: 0;
    background-image:
      linear-gradient(rgba(0, 0, 0, 0.03) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0, 0, 0, 0.03) 1px, transparent 1px);
    background-size: 40px 40px;
    pointer-events: none;

    html.dark & {
      background-image:
        linear-gradient(rgba(255, 255, 255, 0.03) 1px, transparent 1px),
        linear-gradient(90deg, rgba(255, 255, 255, 0.03) 1px, transparent 1px);
    }
  }

  .hero-content {
    position: relative;
    z-index: 1;
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
    font-size: 0.85rem;
    color: var(--text-secondary);
    margin-bottom: 24px;
    animation: fadeInUp 0.6s ease-out both;

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
    margin-bottom: 20px;
    animation: fadeInUp 0.6s ease-out 0.15s both;
    color: var(--text-primary);

    .gradient {
      background: var(--accent-gradient);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
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
    margin: 0 auto 32px;
    animation: fadeInUp 0.6s ease-out 0.3s both;

    .hero-typewriter {
      min-height: 1.6em;
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 4px;

      &-text {
        font-weight: 500;
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
    margin-top: 40px;
    animation: fadeInUp 0.6s ease-out 0.45s both;
  }

  // 快速导航
  .hero-nav {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 10px;
    margin-top: 48px;
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
      transition: all 0.2s ease;

      &:hover {
        background: var(--bg-subtle);
        border-color: var(--accent);
        color: var(--accent);
      }

      .icon {
        font-size: 1rem;
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
      margin-bottom: 20px;
    }

    .social-links {
      display: flex;
      gap: 12px;
    }

    .social-link {
      width: 40px;
      height: 40px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--bg-page);
      border: 1px solid var(--border);
      border-radius: 8px;
      color: var(--text-secondary);
      text-decoration: none;
      transition: all 0.2s ease;

      &:hover {
        border-color: var(--accent);
        color: var(--accent);
      }
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
