<template>
  <div class="home-container">
    <!-- 全屏首屏 Hero -->
    <section class="fullscreen-hero">
      <!-- 单层遮罩（替代原来的 3 个渐变光球 + 粒子系统） -->
      <div class="hero-overlay"></div>

      <div class="hero-content">
        <div class="title-wrapper">
          <!-- 标题徽章：简洁边框，无动画光泽 -->
          <span class="title-badge">欢迎来到斯蒂芬森社区</span>
          <h1 class="hero-title">探索技术的无限可能</h1>
          <!-- 打字机副标题：纯文本容器，无毛玻璃背景 -->
          <p class="hero-subtitle">
            <span class="subtitle-text">{{ typedSubtitle }}</span>
            <span class="cursor-blink"></span>
          </p>
          <!-- CTA 按钮：纯色，单层阴影 -->
          <div class="hero-cta">
            <button class="cta-btn" @click="navigateTo('/article')">
              <span>开始探索</span>
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>

        <!-- 快速导航：精简横向标签式，不用浮夸的卡片 -->
        <div class="quick-nav">
          <div
            class="nav-card"
            v-for="(item, index) in quickNavItems"
            :key="index"
            @click="navigateTo(item.path)"
          >
            <span class="nav-title">{{ item.title }}</span>
            <el-icon class="nav-arrow"><ArrowRight /></el-icon>
          </div>
        </div>
      </div>

      <!-- 滚动指示器：引导用户向下探索 -->
      <div class="scroll-indicator" @click="scrollToContent">
        <div class="scroll-line"></div>
      </div>
    </section>

    <!-- 主要内容区域 -->
    <main class="main-content" ref="contentSection">
      <div class="container">
        <div class="content-grid">
          <!-- 最新文章 -->
          <section class="latest-articles" ref="articlesSectionRef">
            <div class="section-header">
              <!-- 章节标题：左侧色块装饰，替代渐变下划线 -->
              <h2 class="section-title">最新文章</h2>
              <router-link to="/article" class="view-all-link">
                查看全部
                <el-icon><ArrowRight /></el-icon>
              </router-link>
            </div>

            <div class="articles-wrapper">
              <!-- 加载状态 -->
              <div v-if="articleLoading" class="loading-container">
                <el-skeleton animated :count="6">
                  <template #template>
                    <div class="article-skeleton">
                      <el-skeleton-item variant="image" style="width: 100%; height: 180px; border-radius: 8px" />
                      <div class="skeleton-content">
                        <el-skeleton-item variant="h3" style="width: 80%; height: 20px; margin: 12px 0 8px" />
                        <el-skeleton-item variant="text" style="width: 100%; height: 14px" />
                        <el-skeleton-item variant="text" style="width: 70%; height: 14px; margin-top: 6px" />
                      </div>
                    </div>
                  </template>
                </el-skeleton>
              </div>

              <!-- 空状态 -->
              <div v-else-if="articles.length === 0" class="empty-state">
                <el-empty description="暂无文章" />
              </div>

              <!-- 文章列表：卡片 hover 仅改变阴影，无位移/缩放/光效叠加 -->
              <div v-else class="article-list">
                <article
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
                      <span class="tag tag-hot" v-if="article.readCount > 1000">热门</span>
                      <span class="tag tag-new" v-else-if="isNewArticle(article)">新作</span>
                    </div>
                    <h3>
                      <router-link :to="getArticleDetailRoute(article)" class="article-title-link">
                        {{ article.title }}
                      </router-link>
                    </h3>
                    <p class="article-description">{{ article.description || "暂无描述" }}</p>
                    <div class="article-footer">
                      <span class="date">{{ formatDate(article.createTime) }}</span>
                      <div class="article-stats">
                        <span class="stat-item">
                          <el-icon><View /></el-icon>
                          {{ formatNumber(article.readCount || 0) }}
                        </span>
                        <span class="stat-item">
                          <svg-icon name="like" width="13px" height="13px" :color="article.isLiked ? '#ff6b6b' : '#94a3b8'" />
                          {{ formatNumber(article.likeCount || 0) }}
                        </span>
                        <span class="stat-item">
                          <el-icon><Star /></el-icon>
                          {{ formatNumber(article.collectCount || 0) }}
                        </span>
                      </div>
                    </div>
                  </div>
                </article>
              </div>
            </div>

            <!-- 手机端项目链接（仅移动端显示） -->
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
      <div class="container">
        <div class="footer-content">
          <!-- 品牌信息 -->
          <div class="footer-brand">
            <h3 class="brand-title">sidifensen 社区</h3>
            <p class="brand-description">分享技术、经验和见解，用代码改变世界</p>
            <div class="social-links">
              <a href="https://github.com/sidifensen" class="social-link" title="GitHub">
                <svg-icon name="github" width="20px" height="20px" color="#94a3b8" />
              </a>
              <a href="https://gitee.com/sidifensen" class="social-link" title="Gitee">
                <svg-icon name="gitee" width="20px" height="20px" color="#94a3b8" />
              </a>
            </div>
          </div>

          <!-- 导航链接 -->
          <div class="footer-links">
            <div class="footer-column">
              <h4 class="column-title">快速导航</h4>
              <ul class="links-list">
                <li><router-link to="/">首页</router-link></li>
                <li><router-link to="/article">文章</router-link></li>
                <li><router-link to="/album">相册</router-link></li>
                <li><router-link to="/link">友链</router-link></li>
              </ul>
            </div>
          </div>
        </div>

        <!-- 版权信息 -->
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
import { Loading, Picture, View, Star, ArrowRight } from "@element-plus/icons-vue";
import { getAllArticleList } from "@/api/article";
import { formatCompactNumber } from "@/utils/formatNumber";
import ProjectLinks from "@/components/ProjectLinks.vue";

// 路由
const router = useRouter();

// 响应式数据
const articles = ref([]); // 文章列表
const articleLoading = ref(false); // 加载状态
const currentYear = ref(new Date().getFullYear()); // 年份（页脚版权）
const contentSection = ref(null); // 内容区域引用
const articlesSectionRef = ref(null); // 文章区域引用（IntersectionObserver）

// 打字机效果状态
const typedSubtitle = ref("");
let subtitleIndex = 0;
let charIndex = 0;
let isDeleting = false;

// 快速导航项（精简版，不需要 description）
const quickNavItems = ref([
  { title: "技术文章", path: "/article" },
  { title: "相册空间", path: "/album" },
  { title: "友情链接", path: "/link" },
]);

// 打字机副标题列表
const subtitles = [
  "在这里，发现知识 · 分享经验 · 连接开发者",
  "探索代码的无限可能",
  "与开发者共同成长",
  "记录技术旅程的点点滴滴",
];

// 打字机速度配置
const TYPE_SPEED = 150; // 打字速度（ms/字）
const DELETE_SPEED = 80; // 删除速度（ms/字）
const PAUSE_AFTER_TYPE = 800; // 输入完成后暂停（ms）
const PAUSE_AFTER_DELETE = 300; // 删除完成后暂停（ms）

// 打字机循环逻辑
const typeSubtitle = () => {
  const currentSubtitle = subtitles[subtitleIndex];
  if (!isDeleting) {
    if (charIndex < currentSubtitle.length) {
      typedSubtitle.value = currentSubtitle.substring(0, charIndex + 1);
      charIndex++;
      setTimeout(typeSubtitle, TYPE_SPEED);
    } else {
      isDeleting = true;
      setTimeout(typeSubtitle, PAUSE_AFTER_TYPE);
    }
  } else {
    if (charIndex > 0) {
      typedSubtitle.value = currentSubtitle.substring(0, charIndex - 1);
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

// 使用 IntersectionObserver 为文章区域添加入场动画 class
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
    { threshold: 0.08, rootMargin: "0px 0px -80px 0px" }
  );

  if (articlesSectionRef.value) {
    observer.observe(articlesSectionRef.value);
  }
};

// 获取文章列表（首次加载固定 12 条）
const fetchArticleList = async (reset = false) => {
  if (!reset || articles.value.length > 0) return;
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
  return new Date(dateString).toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "2-digit",
    day: "2-digit",
  });
};

// 使用统一数字格式化工具
const formatNumber = (num) => formatCompactNumber(num);

// 判断 3 天内为新文章
const isNewArticle = (article) => {
  const diffDays = (Date.now() - new Date(article.createTime)) / (1000 * 60 * 60 * 24);
  return diffDays <= 3;
};

// 获取文章详情路由
const getArticleDetailRoute = (article) => `/user/${article.userId}/article/${article.id}`;
const goToArticle = (article) => router.push(getArticleDetailRoute(article));
const navigateTo = (path) => router.push(path);

// 点击滚动指示器时平滑滚动到主内容区域
const scrollToContent = () => {
  contentSection.value?.scrollIntoView({ behavior: "smooth" });
};

onMounted(async () => {
  await fetchArticleList(true);
  setTimeout(typeSubtitle, 1000); // 延迟启动打字机，等页面稳定
  setTimeout(observeElements, 400); // 启动可见性观察
});
</script>

<style lang="scss" scoped>
// ===== CSS 变量定义（浅色默认值） =====
.home-container {
  --bg-page: #f8fafc;
  --bg-card: #ffffff;
  --bg-subtle: #f1f5f9;
  --text-primary: #1e293b;
  --text-regular: #475569;
  --text-muted: #94a3b8;
  --border: #e2e8f0;
  --border-light: #f1f5f9;
  --accent: #3b82f6;
  --accent-hover: #2563eb;
  --shadow-card: 0 1px 3px rgba(0, 0, 0, 0.08), 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-card-hover: 0 8px 24px rgba(0, 0, 0, 0.1);

  background-color: var(--bg-page);
  min-height: 100vh;
}

// ===== 黑夜模式覆盖（文件末尾统一管理） =====
html.dark {
  .home-container {
    --bg-page: #0f172a;
    --bg-card: #1e293b;
    --bg-subtle: #1a2540;
    --text-primary: #f1f5f9;
    --text-regular: #cbd5e1;
    --text-muted: #64748b;
    --border: #334155;
    --border-light: #2d3748;
    --shadow-card: 0 1px 3px rgba(0, 0, 0, 0.3);
    --shadow-card-hover: 0 8px 24px rgba(0, 0, 0, 0.4);
  }
}

// ===== 通用容器 =====
.container {
  width: 100%;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 24px;

  @media screen and (max-width: 768px) {
    padding: 0 16px;
  }
}

// ===== 全屏首屏 Hero =====
.fullscreen-hero {
  position: relative;
  width: 100%;
  height: 100vh;
  margin-top: -48px;
  padding-top: 48px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  overflow: hidden;
  // 真实背景图，单层，不堆叠装饰性光球
  background: url("@/assets/img/th.jpg") no-repeat center center / cover;

  // 单层半透明遮罩（替代原有的多层渐变 + 3 个 orb + 粒子）
  .hero-overlay {
    position: absolute;
    inset: 0;
    background: rgba(10, 18, 35, 0.68);
    z-index: 1;

    // 底部渐变过渡到页面背景色
    &::after {
      content: "";
      position: absolute;
      bottom: 0;
      left: 0;
      right: 0;
      height: 200px;
      background: linear-gradient(to bottom, transparent 0%, var(--bg-page) 100%);
    }
  }

  // 内容层
  .hero-content {
    position: relative;
    z-index: 2;
    text-align: center;
    color: #fff;
    padding: 0 24px;
    max-width: 960px;
    width: 100%;
    animation: fadeInUp 0.7s ease-out both;

    // 标题区域
    .title-wrapper {
      // 徽章：简单边框，无光泽扫过动画
      .title-badge {
        display: inline-block;
        padding: 6px 22px;
        border: 1px solid rgba(255, 255, 255, 0.32);
        border-radius: 50px;
        font-size: 0.875rem;
        font-weight: 500;
        color: rgba(255, 255, 255, 0.82);
        letter-spacing: 0.5px;
        margin-bottom: 24px;
      }

      // 主标题：白色，无渐变文字，无 glow 动画
      .hero-title {
        font-size: 3.8rem;
        font-weight: 700;
        color: #ffffff;
        line-height: 1.15;
        margin-bottom: 18px;
        letter-spacing: 1px;

        @media screen and (max-width: 768px) {
          font-size: 2.4rem;
        }

        @media screen and (max-width: 480px) {
          font-size: 1.9rem;
        }
      }

      // 副标题：纯文本区域，无毛玻璃背景容器
      .hero-subtitle {
        font-size: 1.05rem;
        color: rgba(255, 255, 255, 0.68);
        min-height: 1.7em;
        display: inline-flex;
        align-items: center;
        gap: 2px;
        margin: 0;

        .cursor-blink {
          display: inline-block;
          width: 2px;
          height: 1.1em;
          background: rgba(255, 255, 255, 0.65);
          animation: cursorBlink 1s step-end infinite;
        }
      }

      // CTA 按钮：纯色 + 单层阴影，hover 只变颜色
      .hero-cta {
        display: flex;
        justify-content: center;
        margin-top: 40px;

        .cta-btn {
          display: inline-flex;
          align-items: center;
          gap: 8px;
          padding: 13px 32px;
          background: var(--accent);
          color: #fff;
          border: none;
          border-radius: 8px;
          font-size: 0.95rem;
          font-weight: 600;
          cursor: pointer;
          box-shadow: 0 2px 8px rgba(59, 130, 246, 0.4);
          // hover 只改颜色，不加位移/缩放
          transition: background 0.2s ease;

          &:hover {
            background: var(--accent-hover);
          }

          .el-icon {
            font-size: 15px;
          }
        }
      }
    }

    // 快速导航：横向文字标签，简洁高效
    .quick-nav {
      display: flex;
      flex-wrap: wrap;
      justify-content: center;
      gap: 12px;
      margin-top: 44px;

      .nav-card {
        display: inline-flex;
        align-items: center;
        gap: 6px;
        padding: 9px 20px;
        background: rgba(255, 255, 255, 0.08);
        border: 1px solid rgba(255, 255, 255, 0.16);
        border-radius: 50px;
        cursor: pointer;
        // hover 只变背景
        transition: background 0.2s ease, border-color 0.2s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.15);
          border-color: rgba(255, 255, 255, 0.28);
        }

        .nav-title {
          font-size: 0.875rem;
          font-weight: 500;
          color: rgba(255, 255, 255, 0.88);
          white-space: nowrap;
        }

        .nav-arrow {
          font-size: 12px;
          color: rgba(255, 255, 255, 0.45);
        }
      }
    }
  }

  // 滚动指示器：单条线段动画，引导用户向下
  .scroll-indicator {
    position: absolute;
    bottom: 210px;
    left: 50%;
    transform: translateX(-50%);
    z-index: 2;
    cursor: pointer;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 6px;
    opacity: 0;
    animation: indicatorFadeIn 1s ease-out 1.5s forwards;

    .scroll-line {
      width: 1px;
      height: 44px;
      background: linear-gradient(to bottom, rgba(255, 255, 255, 0.55), transparent);
      animation: scrollLineDrop 2s ease-in-out 2s infinite;
    }

    @media screen and (max-width: 768px) {
      display: none;
    }
  }
}

// ===== 主要内容区域 =====
.main-content {
  padding: 64px 0 80px;
  background-color: var(--bg-page);
  position: relative;
  z-index: 2;
  min-height: 100vh;

  // 两栏网格：左侧文章 + 右侧边栏
  .content-grid {
    display: grid;
    grid-template-columns: 1fr 300px;
    gap: 32px;
    align-items: start;

    @media screen and (max-width: 992px) {
      grid-template-columns: 1fr;
    }
  }

  // 最新文章区域（不加卡片外壳，避免卡片套卡片的视觉压迫感）
  .latest-articles {
    min-height: 80vh;

    // 章节标题行
    .section-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 28px;
      padding-bottom: 20px;
      border-bottom: 1px solid var(--border);

      // 左侧色块装饰（替代多色渐变下划线）
      .section-title {
        font-size: 1.15rem;
        font-weight: 700;
        color: var(--text-primary);
        margin: 0;
        padding-left: 14px;
        position: relative;

        &::before {
          content: "";
          position: absolute;
          left: 0;
          top: 50%;
          transform: translateY(-50%);
          width: 4px;
          height: 16px;
          background: var(--accent);
          border-radius: 2px;
        }
      }

      // 查看全部链接：hover 只变颜色
      .view-all-link {
        display: flex;
        align-items: center;
        gap: 4px;
        color: var(--text-muted);
        font-size: 0.875rem;
        text-decoration: none;
        transition: color 0.2s ease;

        &:hover {
          color: var(--accent);
        }
      }
    }

    // 文章内容包装区域
    .articles-wrapper {
      min-height: 55vh;

      // 骨架屏
      .article-skeleton {
        border: 1px solid var(--border);
        border-radius: 8px;
        padding: 16px;
        margin-bottom: 20px;

        .skeleton-content {
          margin-top: 12px;
        }
      }

      // 空状态
      .empty-state {
        padding: 80px 0;
        text-align: center;
      }

      // 文章网格
      .article-list {
        display: grid;
        grid-template-columns: repeat(3, 1fr);
        gap: 20px;

        @media screen and (max-width: 1200px) {
          grid-template-columns: repeat(2, 1fr);
        }

        @media screen and (max-width: 768px) {
          grid-template-columns: 1fr;
        }

        // 文章卡片：hover 仅改变阴影，无位移/缩放/进度条/光效
        .article-card {
          background: var(--bg-card);
          border: 1px solid var(--border);
          border-radius: 10px;
          overflow: hidden;
          display: flex;
          flex-direction: column;
          cursor: pointer;
          // 只有阴影一个属性变化
          transition: box-shadow 0.2s ease;

          &:hover {
            box-shadow: var(--shadow-card-hover);
          }

          // 封面图
          .article-image {
            height: 175px;
            overflow: hidden;
            background: var(--bg-subtle);
            position: relative;

            .article-cover {
              width: 100%;
              height: 100%;
              // 图片 scale 是可接受的单一效果
              transition: transform 0.3s ease;
            }

            // 卡片 hover 时图片轻微放大
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

          &:hover .article-image .article-cover {
            transform: scale(1.04);
          }

          // 文章文字内容
          .article-content {
            padding: 14px 16px 16px;
            flex: 1;
            display: flex;
            flex-direction: column;
            gap: 8px;

            // 标签区（热门 / 新作）
            .article-tags {
              min-height: 22px;
              display: flex;
              gap: 6px;

              .tag {
                display: inline-block;
                padding: 2px 8px;
                border-radius: 4px;
                font-size: 0.72rem;
                font-weight: 500;

                &.tag-hot {
                  background: rgba(239, 68, 68, 0.1);
                  color: #dc2626;
                }

                &.tag-new {
                  background: rgba(59, 130, 246, 0.1);
                  color: var(--accent);
                }
              }
            }

            // 文章标题
            h3 {
              margin: 0;
              font-size: 0.92rem;
              font-weight: 600;
              line-height: 1.5;

              .article-title-link {
                color: var(--text-primary);
                text-decoration: none;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                transition: color 0.2s ease;

                &:hover {
                  color: var(--accent);
                }
              }
            }

            // 文章摘要
            .article-description {
              font-size: 0.8rem;
              color: var(--text-regular);
              line-height: 1.65;
              margin: 0;
              flex: 1;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
            }

            // 底部：日期 + 统计数据
            .article-footer {
              display: flex;
              align-items: center;
              justify-content: space-between;
              padding-top: 10px;
              border-top: 1px solid var(--border-light);
              margin-top: 2px;

              .date {
                font-size: 0.72rem;
                color: var(--text-muted);
              }

              .article-stats {
                display: flex;
                align-items: center;
                gap: 10px;

                .stat-item {
                  display: flex;
                  align-items: center;
                  gap: 3px;
                  font-size: 0.72rem;
                  color: var(--text-muted);

                  .el-icon {
                    font-size: 12px;
                  }
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

      @media screen and (max-width: 992px) {
        display: block;
      }
    }
  }

  // 右侧边栏
  .sidebar {
    position: sticky;
    top: 72px;

    @media screen and (max-width: 992px) {
      display: none;
    }
  }
}

// ===== 页脚 =====
.footer {
  background: var(--bg-card);
  border-top: 1px solid var(--border);
  padding: 48px 0 32px;

  // 品牌 + 导航并排
  .footer-content {
    display: flex;
    gap: 60px;
    margin-bottom: 40px;

    @media screen and (max-width: 768px) {
      flex-direction: column;
      gap: 28px;
    }

    .footer-brand {
      flex: 1;

      .brand-title {
        font-size: 1rem;
        font-weight: 700;
        color: var(--text-primary);
        margin: 0 0 8px;
      }

      .brand-description {
        font-size: 0.85rem;
        color: var(--text-regular);
        line-height: 1.6;
        margin: 0 0 18px;
      }

      .social-links {
        display: flex;
        gap: 14px;

        // hover 只改透明度
        .social-link {
          display: flex;
          align-items: center;
          opacity: 0.65;
          transition: opacity 0.2s ease;

          &:hover {
            opacity: 1;
          }
        }
      }
    }

    .footer-links {
      .footer-column {
        .column-title {
          font-size: 0.78rem;
          font-weight: 600;
          color: var(--text-muted);
          margin: 0 0 14px;
          text-transform: uppercase;
          letter-spacing: 0.8px;
        }

        .links-list {
          list-style: none;
          margin: 0;
          padding: 0;
          display: flex;
          flex-direction: column;
          gap: 10px;

          li a {
            font-size: 0.875rem;
            color: var(--text-regular);
            text-decoration: none;
            transition: color 0.2s ease;

            &:hover {
              color: var(--accent);
            }
          }
        }
      }
    }
  }

  // 版权行
  .footer-bottom {
    .footer-divider {
      height: 1px;
      background: var(--border);
      margin-bottom: 24px;
    }

    .footer-bottom-content {
      display: flex;
      align-items: center;
      justify-content: space-between;
      flex-wrap: wrap;
      gap: 12px;

      .footer-left {
        .copyright,
        .beian {
          font-size: 0.78rem;
          color: var(--text-muted);
          margin: 0 0 4px;

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

      .footer-meta {
        font-size: 0.78rem;
        color: var(--text-muted);
      }
    }
  }
}

// ===== 动画关键帧 =====
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes cursorBlink {
  0%,
  100% {
    opacity: 1;
  }
  50% {
    opacity: 0;
  }
}

// 滚动指示器：延迟出现 + 线段下落循环
@keyframes indicatorFadeIn {
  to {
    opacity: 1;
  }
}

@keyframes scrollLineDrop {
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
</style>
