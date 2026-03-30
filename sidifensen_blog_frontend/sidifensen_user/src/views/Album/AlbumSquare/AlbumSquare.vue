<template>
  <div class="album-page-body">
  <div class="container">
    <!-- 页面头部 -->
    <div class="page-hero">
      <div class="hero-content">
        <div class="hero-copy">
          <div class="hero-kicker">Album Square</div>
          <h1>相册广场</h1>
          <p>探索社区精彩瞬间，发现视觉之美。</p>
        </div>
        <div class="hero-actions">
          <button class="btn-my-album" @click="goToMyAlbum">
            <span>我的相册</span>
            <el-icon><ArrowRight /></el-icon>
          </button>
        </div>
      </div>
    </div>

    <!-- 内容布局 -->
    <div class="content-layout">
      <!-- 主内容区 -->
      <div class="main-content">
        <LoadingAnimation v-if="loading" />

        <div v-else-if="albums.length > 0" class="album-masonry">
          <div
            v-for="(album, index) in albums"
            :key="album.id"
            class="masonry-item"
            :style="{ animationDelay: `${index * 0.05}s` }"
            @click="handleViewAlbum(album.id)"
          >
            <div class="item-inner">
              <div class="image-container">
                <el-image
                  :src="album.coverUrl || ''"
                  class="album-cover"
                  fit="cover"
                  lazy
                >
                  <template #placeholder>
                    <div class="placeholder">
                      <el-icon class="spinner"><Loading /></el-icon>
                    </div>
                  </template>
                  <template #error>
                    <div class="error-state">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>

                <!-- 悬停效果层 -->
                <div class="hover-layer">
                  <div class="layer-content">
                    <div class="zoom-icon">
                      <el-icon><ZoomIn /></el-icon>
                    </div>
                  </div>
                </div>
              </div>

              <div class="item-footer">
                <div class="album-title-text">{{ album.name }}</div>
                <div class="album-author">
                  <div class="author-dot"></div>
                  {{ album.userName }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <el-empty v-else description="暂无相册" :image-size="160" />
      </div>
    </div>
  </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { Picture, Loading, ZoomIn, ArrowRight } from "@element-plus/icons-vue"
import { listAllAlbum } from "@/api/album"
import LoadingAnimation from "@/components/LoadingAnimation.vue"
import { useSeoMeta } from "@/plugins/seo"

// SEO - 相册广场
useSeoMeta({
  title: "相册广场",
  description: "浏览社区精彩相册，发现摄影作品和创意分享",
  keywords: "相册,图片,摄影,作品展示,社区图库"
});

const router = useRouter()
const loading = ref(false)
const albums = ref([])

const getAllAlbums = async () => {
  loading.value = true
  try {
    const res = await listAllAlbum()
    albums.value = res.data.sort((a, b) => a.id - b.id)
  } catch (error) {
    // 静默处理
  } finally {
    loading.value = false
  }
}

const handleViewAlbum = (albumId) => {
  router.push({ name: "AlbumDetail", params: { albumId } })
}

const goToMyAlbum = () => {
  router.push({ name: "MyAlbum" })
}

onMounted(() => {
  getAllAlbums()
})
</script>

<style lang="scss" scoped>
// 相册广场页面 - 现代极简主义科技风
// 完全匹配参考文件：Article/index.vue

// 全局盒模型
*,
*::before,
*::after {
  box-sizing: border-box;
}

.album-page-body {
  width: 100%;
  min-height: 100vh;
  background: #f8fafc;
  overflow-x: hidden;

  html.dark & {
    background: #000000;
  }
}

.container {
  // ===== CSS 变量定义 - 浅色模式 =====
  --bg-page: #f8fafc;
  --bg-hero-divider: rgba(0, 0, 0, 0.08);
  --bg-metric: rgba(0, 0, 0, 0.02);
  --bg-subtle: rgba(0, 0, 0, 0.02);
  --bg-card: rgba(0, 0, 0, 0.015);
  --bg-hover: rgba(0, 0, 0, 0.01);

  --text-primary: #0a0a0a;
  --text-secondary: #888888;
  --text-regular: #666666;
  --text-muted: #aaaaaa;

  --border-light: rgba(0, 0, 0, 0.05);
  --border-regular: rgba(0, 0, 0, 0.08);
  --border-hover: rgba(0, 0, 0, 0.15);

  --accent-color: #0066ff;
  --accent-soft: rgba(0, 102, 255, 0.04);
  --accent-border: rgba(0, 102, 255, 0.15);
  --accent-hover: rgba(0, 102, 255, 0.08);

  // ===== 黑夜模式覆盖 =====
  html.dark & {
    --bg-page: #000000;
    --bg-hero-divider: rgba(255, 255, 255, 0.1);
    --bg-metric: rgba(255, 255, 255, 0.03);
    --bg-subtle: rgba(255, 255, 255, 0.02);
    --bg-card: rgba(255, 255, 255, 0.02);
    --bg-hover: rgba(255, 255, 255, 0.02);

    --text-primary: #ffffff;
    --text-secondary: #888888;
    --text-regular: #dddddd;
    --text-muted: #666666;

    --border-light: rgba(255, 255, 255, 0.05);
    --border-regular: rgba(255, 255, 255, 0.08);
    --border-hover: rgba(255, 255, 255, 0.15);

    --accent-color: #00d4ff;
    --accent-soft: rgba(0, 212, 255, 0.05);
    --accent-border: rgba(0, 212, 255, 0.15);
    --accent-hover: rgba(0, 212, 255, 0.08);
  }

  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 100vh;
  background: var(--bg-page);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  line-height: 1.6;

  // 手机端适配
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

    @media (max-width: 768px) {
      padding: 40px 0 25px;
    }

    .hero-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      gap: 24px;

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

          @media (max-width: 768px) {
            font-size: 28px;
          }
        }

        p {
          font-size: 14px;
          color: var(--text-secondary);
          line-height: 1.7;
        }
      }

      .hero-actions {
        flex-shrink: 0;

        .btn-my-album {
          display: inline-flex;
          align-items: center;
          gap: 8px;
          padding: 10px 20px;
          background: var(--accent-soft);
          border: 1px solid var(--accent-border);
          border-radius: 8px;
          color: var(--accent-color);
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.2s ease;
          white-space: nowrap;

          &:hover {
            background: var(--accent-hover);
            border-color: var(--accent-color);
          }

          .el-icon {
            font-size: 16px;
          }
        }
      }
    }
  }

  // ===== 内容布局 =====
  .content-layout {
    padding: 40px 0 60px;

    @media (max-width: 768px) {
      padding: 30px 0 50px;
    }

    // ===== 主内容区 =====
    .main-content {
      width: 100%;
      max-width: 100%;

      // ===== 瀑布流布局 =====
      .album-masonry {
        column-count: 4;
        column-gap: 16px;

        @media (max-width: 1200px) {
          column-count: 3;
        }

        @media (max-width: 900px) {
          column-count: 2;
        }

        @media (max-width: 600px) {
          column-count: 2;
          column-gap: 12px;
          padding: 0;
        }

        .masonry-item {
          break-inside: avoid;
          margin-bottom: 16px;
          opacity: 0;
          animation: fadeInUp 0.5s ease forwards;

          @media (max-width: 600px) {
            margin-bottom: 12px;
          }

          .item-inner {
            background: transparent;
            border: 1px solid var(--border-regular);
            border-radius: 6px;
            overflow: hidden;
            cursor: pointer;
            transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;

            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              height: 1px;
              background: linear-gradient(90deg, var(--accent-color), transparent);
              opacity: 0;
              transition: opacity 0.25s ease;
            }

            &:hover::before {
              opacity: 1;
            }

            &:hover {
              border-color: var(--border-hover);
              background: var(--bg-hover);
            }

            html.dark & {
              &:hover {
                background: rgba(255, 255, 255, 0.02);
                border-color: rgba(255, 255, 255, 0.15);
              }
            }

            .image-container {
              position: relative;
              overflow: hidden;
              aspect-ratio: 4/3;

              .album-cover {
                width: 100%;
                height: 100%;
                display: block;
                object-fit: cover;
                transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
              }

              .placeholder,
              .error-state {
                width: 100%;
                height: 100%;
                aspect-ratio: 4/3;
                background: var(--bg-metric);
                display: flex;
                justify-content: center;
                align-items: center;

                .spinner {
                  animation: rotate 1.5s linear infinite;
                  font-size: 28px;
                  color: var(--text-muted);
                }

                .el-icon {
                  font-size: 40px;
                  color: var(--text-muted);
                }
              }

              .hover-layer {
                position: absolute;
                inset: 0;
                background: rgba(0, 0, 0, 0.4);
                display: flex;
                justify-content: center;
                align-items: center;
                opacity: 0;
                transition: opacity 0.3s ease;

                .layer-content {
                  display: flex;
                  flex-direction: column;
                  align-items: center;
                  color: #fff;

                  .zoom-icon {
                    width: 48px;
                    height: 48px;
                    border-radius: 50%;
                    background: rgba(255, 255, 255, 0.2);
                    backdrop-filter: blur(10px);
                    display: flex;
                    justify-content: center;
                    align-items: center;

                    .el-icon {
                      font-size: 24px;
                    }
                  }
                }
              }
            }

            .item-footer {
              padding: 16px;

              .album-title-text {
                font-size: 15px;
                font-weight: 600;
                color: var(--text-primary);
                margin: 0 0 6px 0;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }

              .album-author {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 13px;
                color: var(--text-secondary);

                .author-dot {
                  width: 4px;
                  height: 4px;
                  background: var(--text-muted);
                  border-radius: 50%;
                }
              }
            }
          }
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

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
