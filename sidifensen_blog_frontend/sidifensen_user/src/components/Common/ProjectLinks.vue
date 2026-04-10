<template>
  <div class="project-links-section">
    <div class="project-card">
      <!-- 卡片头部 -->
      <div class="project-header">
        <div class="project-title">
          <el-icon class="title-icon"><Star /></el-icon>
          <span class="title-text">开源项目</span>
        </div>
        <p class="project-description">如果这个项目对你有帮助，欢迎给个 Star 支持</p>
      </div>

      <!-- 链接列表 -->
      <div class="project-links">
        <a href="https://gitee.com/sidifensen/sidifensen_blog" target="_blank" class="project-link">
          <div class="link-icon gitee-icon">
            <!-- Gitee SVG -->
            <svg viewBox="0 0 1024 1024" width="20" height="20">
              <path
                d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.293-25.267z"
                fill="#C71D23"
              />
            </svg>
          </div>
          <div class="link-content">
            <span class="link-name">Gitee</span>
            <span class="link-subtitle">国内访问更快</span>
          </div>
          <el-icon class="link-arrow"><ArrowRight /></el-icon>
        </a>

        <a
          href="https://github.com/sidifensen/sidifensen_blog"
          target="_blank"
          class="project-link"
        >
          <div class="link-icon github-icon">
            <!-- GitHub SVG -->
            <svg viewBox="0 0 1024 1024" width="20" height="20">
              <path
                d="M512 0C229.12 0 0 229.12 0 512c0 226.56 146.56 417.92 350.08 485.76 25.6 4.48 35.2-10.88 35.2-24.32 0-12.16-0.64-52.48-0.64-95.36-128.64 23.68-161.92-31.36-172.16-60.16-5.76-14.72-30.72-60.16-52.48-72.32-17.92-9.6-43.52-33.28-0.64-33.92 40.32-0.64 69.12 37.12 78.72 52.48 46.08 77.44 119.68 55.68 149.12 42.24 4.48-33.28 17.92-55.68 32.64-68.48-113.92-12.8-232.96-56.96-232.96-252.8 0-55.68 19.84-101.76 52.48-137.6-5.12-12.8-23.04-65.28 5.12-135.68 0 0 42.88-13.44 140.8 52.48 40.96-11.52 84.48-17.28 128-17.28 43.52 0 87.04 5.76 128 17.28 97.92-66.56 140.8-52.48 140.8-52.48 28.16 70.4 10.24 122.88 5.12 135.68 32.64 35.84 52.48 81.28 52.48 137.6 0 196.48-119.68 240-233.6 252.8 18.56 16 34.56 46.72 34.56 94.72 0 68.48-0.64 123.52-0.64 140.8 0 13.44 9.6 29.44 35.2 24.32C877.44 929.92 1024 737.92 1024 512 1024 229.12 794.88 0 512 0z"
                fill="#24292e"
              />
            </svg>
          </div>
          <div class="link-content">
            <span class="link-name">GitHub</span>
            <span class="link-subtitle">全球开源平台</span>
          </div>
          <el-icon class="link-arrow"><ArrowRight /></el-icon>
        </a>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Star, ArrowRight } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
// ===== 开源项目链接卡片 - CSS 变量支持黑夜模式 =====
.project-links-section {
  --bg-card: #ffffff;
  --bg-link: #f8fafc;
  --bg-link-hover: #f1f5f9;
  --text-primary: #1e293b;
  --text-muted: #94a3b8;
  --border: #e2e8f0;
  --accent: #3b82f6;
  --star-color: #f59e0b;

  // 卡片主体：顶部品牌色线条，无渐变背景
  .project-card {
    background: var(--bg-card);
    border: 1px solid var(--border);
    border-top: 3px solid var(--accent);
    border-radius: 12px;
    padding: 20px;

    // 卡片头部
    .project-header {
      margin-bottom: 16px;

      .project-title {
        display: flex;
        align-items: center;
        gap: 7px;
        margin-bottom: 6px;

        // 星星图标：固定颜色，无脉冲动画
        .title-icon {
          font-size: 15px;
          color: var(--star-color);
        }

        .title-text {
          font-size: 0.9rem;
          font-weight: 600;
          color: var(--text-primary);
        }
      }

      .project-description {
        font-size: 0.775rem;
        color: var(--text-muted);
        margin: 0;
        line-height: 1.5;
      }
    }

    // 链接列表
    .project-links {
      display: flex;
      flex-direction: column;
      gap: 8px;

      // 单个链接行：hover 只变背景色
      .project-link {
        display: flex;
        align-items: center;
        gap: 12px;
        padding: 11px 12px;
        background: var(--bg-link);
        border: 1px solid var(--border);
        border-radius: 8px;
        text-decoration: none;
        transition: background 0.18s ease;

        &:hover {
          background: var(--bg-link-hover);
        }

        // 图标容器：各平台用对应浅色背景
        .link-icon {
          width: 34px;
          height: 34px;
          border-radius: 7px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;

          &.gitee-icon {
            background: rgba(199, 29, 35, 0.07);
          }

          &.github-icon {
            background: rgba(36, 41, 46, 0.06);
          }
        }

        // 链接文字
        .link-content {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 2px;

          .link-name {
            font-size: 0.875rem;
            font-weight: 600;
            color: var(--text-primary);
          }

          .link-subtitle {
            font-size: 0.72rem;
            color: var(--text-muted);
          }
        }

        // 箭头图标
        .link-arrow {
          font-size: 13px;
          color: var(--text-muted);
          flex-shrink: 0;
        }
      }
    }
  }
}

// 黑夜模式覆盖
html.dark {
  .project-links-section {
    --bg-card: #1e293b;
    --bg-link: #0f172a;
    --bg-link-hover: #1a2540;
    --text-primary: #f1f5f9;
    --text-muted: #64748b;
    --border: #334155;

    // GitHub 图标在深色背景下需要调亮
    .github-icon svg path {
      fill: #94a3b8;
    }
  }
}
</style>
