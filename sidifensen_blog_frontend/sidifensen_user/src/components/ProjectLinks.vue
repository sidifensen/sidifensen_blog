<template>
  <div class="project-links-section">
    <el-card class="project-card">
      <div class="project-header">
        <div class="project-title">
          <el-icon class="title-icon"><Star /></el-icon>
          <span class="title-text">开源项目</span>
        </div>
        <p class="project-description">如果这个项目对您有帮助，请给个 Star ⭐ 支持一下吧！</p>
      </div>
      <div class="project-links">
        <a href="https://gitee.com/sidifensen/sidifensen_blog" target="_blank" class="project-link gitee-link">
          <div class="link-icon">
            <svg viewBox="0 0 1024 1024" width="32" height="32">
              <path
                d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.293-25.267z"
                fill="#C71D23"
              />
            </svg>
          </div>
          <div class="link-content">
            <div class="link-name">Gitee</div>
            <div class="link-subtitle">国内访问更快</div>
          </div>
          <el-icon class="link-arrow"><ArrowRight /></el-icon>
        </a>
        <a href="https://github.com/sidifensen/sidifensen_blog" target="_blank" class="project-link github-link">
          <div class="link-icon">
            <svg viewBox="0 0 1024 1024" width="32" height="32">
              <path
                d="M512 0C229.12 0 0 229.12 0 512c0 226.56 146.56 417.92 350.08 485.76 25.6 4.48 35.2-10.88 35.2-24.32 0-12.16-0.64-52.48-0.64-95.36-128.64 23.68-161.92-31.36-172.16-60.16-5.76-14.72-30.72-60.16-52.48-72.32-17.92-9.6-43.52-33.28-0.64-33.92 40.32-0.64 69.12 37.12 78.72 52.48 46.08 77.44 119.68 55.68 149.12 42.24 4.48-33.28 17.92-55.68 32.64-68.48-113.92-12.8-232.96-56.96-232.96-252.8 0-55.68 19.84-101.76 52.48-137.6-5.12-12.8-23.04-65.28 5.12-135.68 0 0 42.88-13.44 140.8 52.48 40.96-11.52 84.48-17.28 128-17.28 43.52 0 87.04 5.76 128 17.28 97.92-66.56 140.8-52.48 140.8-52.48 28.16 70.4 10.24 122.88 5.12 135.68 32.64 35.84 52.48 81.28 52.48 137.6 0 196.48-119.68 240-233.6 252.8 18.56 16 34.56 46.72 34.56 94.72 0 68.48-0.64 123.52-0.64 140.8 0 13.44 9.6 29.44 35.2 24.32C877.44 929.92 1024 737.92 1024 512 1024 229.12 794.88 0 512 0z"
                fill="#181717"
              />
            </svg>
          </div>
          <div class="link-content">
            <div class="link-name">GitHub</div>
            <div class="link-subtitle">全球开源平台</div>
          </div>
          <el-icon class="link-arrow"><ArrowRight /></el-icon>
        </a>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { Star, ArrowRight } from "@element-plus/icons-vue";
</script>

<style lang="scss" scoped>
// 项目链接展示区域
.project-links-section {
  // 项目卡片
  .project-card {
    background: linear-gradient(135deg, #3498db 0%, #2ecc71 100%);
    border: none;
    box-shadow: 0 4px 20px rgba(52, 152, 219, 0.3);
    border-radius: 16px;
    overflow: hidden;

    // 覆盖 Element Plus 默认样式
    :deep(.el-card__body) {
      padding: 24px;
    }

    // 项目头部
    .project-header {
      text-align: center;
      margin-bottom: 20px;

      // 项目标题
      .project-title {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 12px;

        // 标题图标
        .title-icon {
          font-size: 24px;
          color: #ffd700;
          animation: pulse 2s ease-in-out infinite;
        }

        // 标题文字
        .title-text {
          font-size: 20px;
          font-weight: 700;
          color: white;
        }
      }

      // 项目描述
      .project-description {
        font-size: 14px;
        color: rgba(255, 255, 255, 0.9);
        margin: 0;
        line-height: 1.6;
      }
    }

    // 项目链接列表
    .project-links {
      display: flex;
      flex-direction: column;
      gap: 12px;

      // 单个项目链接
      .project-link {
        display: flex;
        align-items: center;
        padding: 16px;
        background: rgba(255, 255, 255, 0.15);
        backdrop-filter: blur(10px);
        border-radius: 12px;
        border: 1px solid rgba(255, 255, 255, 0.2);
        text-decoration: none;
        transition: all 0.3s ease;
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

        &:hover {
          transform: translateY(-4px);
          background: rgba(255, 255, 255, 0.25);
          box-shadow: 0 8px 24px rgba(0, 0, 0, 0.2);

          &::before {
            left: 100%;
          }

          // 链接图标
          .link-icon {
            transform: scale(1.1) rotate(5deg);
          }

          // 箭头图标
          .link-arrow {
            transform: translateX(4px);
          }
        }

        // 链接图标容器
        .link-icon {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 48px;
          height: 48px;
          background: rgba(255, 255, 255, 0.2);
          border-radius: 10px;
          margin-right: 12px;
          transition: all 0.3s ease;
          flex-shrink: 0;

          svg {
            display: block;
          }
        }

        // 链接内容
        .link-content {
          flex: 1;
          min-width: 0;

          // 链接名称
          .link-name {
            font-size: 16px;
            font-weight: 600;
            color: white;
            margin-bottom: 4px;
            line-height: 1.2;
          }

          // 链接副标题
          .link-subtitle {
            font-size: 12px;
            color: rgba(255, 255, 255, 0.8);
            line-height: 1.2;
          }
        }

        // 箭头图标
        .link-arrow {
          font-size: 20px;
          color: rgba(255, 255, 255, 0.8);
          transition: all 0.3s ease;
          flex-shrink: 0;
        }

        // Gitee 链接特殊样式
        &.gitee-link {
          &:hover {
            border-color: rgba(199, 29, 35, 0.5);
          }
        }

        // GitHub 链接特殊样式
        &.github-link {
          &:hover {
            border-color: rgba(24, 23, 23, 0.5);
          }
        }
      }
    }
  }
}

// 星星脉冲动画
@keyframes pulse {
  0%,
  100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}
</style>
