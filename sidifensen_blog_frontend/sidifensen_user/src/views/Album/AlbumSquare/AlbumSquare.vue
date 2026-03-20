<template>
  <div class="album-page">
    <!-- 顶部 Banner 区 -->
    <div class="album-hero">
      <div class="hero-content">
        <h1 class="hero-title">
          <span class="title-icon">📸</span>
          ALBUM GALLERY
        </h1>
        <p class="hero-subtitle">探索社区精彩瞬间，发现视觉之美</p>
        <div class="hero-actions">
          <span class="album-count">{{ albums.length }} 个相册</span>
          <button class="btn-primary" @click="toMyAlbum">
            <el-icon><User /></el-icon>
            我的相册
          </button>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
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
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { User, Picture, Loading, ZoomIn } from "@element-plus/icons-vue"
import { listAllAlbum } from "@/api/album"
import LoadingAnimation from "@/components/LoadingAnimation.vue"

const router = useRouter()
const loading = ref(false)
const albums = ref([])

const getAllAlbums = async () => {
  loading.value = true
  try {
    const res = await listAllAlbum()
    albums.value = res.data.data.sort((a, b) => a.id - b.id)
  } catch (error) {
    console.error("获取相册列表失败:", error)
  } finally {
    loading.value = false
  }
}

const handleViewAlbum = (albumId) => {
  router.push({ name: "AlbumDetail", params: { albumId } })
}

const toMyAlbum = () => {
  router.push({ name: "MyAlbum" })
}

onMounted(() => {
  getAllAlbums()
})
</script>

<style lang="scss" scoped>
.album-page {
  --bg-primary: #fafafa;
  --bg-card: #ffffff;
  --text-main: #1a1a1a;
  --text-secondary: #666666;
  --text-muted: #999999;
  --border-color: #eeeeee;
  --accent-color: #000000;
  --hover-bg: #f5f5f5;

  min-height: 100vh;
  background: var(--bg-primary);

  /* Hero Banner */
  .album-hero {
    position: relative;
    height: 400px;
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;

    // 装饰性背景元素
    &::before {
      content: '';
      position: absolute;
      top: -50%;
      right: -20%;
      width: 600px;
      height: 600px;
      background: radial-gradient(circle, rgba(255,255,255,0.03) 0%, transparent 70%);
      border-radius: 50%;
    }

    &::after {
      content: '';
      position: absolute;
      bottom: -30%;
      left: -10%;
      width: 400px;
      height: 400px;
      background: radial-gradient(circle, rgba(255,255,255,0.02) 0%, transparent 70%);
      border-radius: 50%;
    }

    .hero-content {
      position: relative;
      z-index: 1;
      text-align: center;
      color: #fff;

      .hero-title {
        margin: 0 0 16px 0;
        font-size: 42px;
        font-weight: 800;
        letter-spacing: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 16px;
        text-transform: uppercase;

        // 手机端：图标放在上面
        @media (max-width: 600px) {
          flex-direction: column;
          gap: 8px;
          font-size: 28px;
          letter-spacing: 4px;
        }

        .title-icon {
          font-size: 48px;
          opacity: 0.9;

          @media (max-width: 600px) {
            font-size: 36px;
          }
        }
      }

      .hero-subtitle {
        margin: 0 0 32px 0;
        font-size: 16px;
        font-weight: 300;
        letter-spacing: 1px;
        opacity: 0.7;
      }

      .hero-actions {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 24px;

        .album-count {
          font-size: 14px;
          font-weight: 500;
          opacity: 0.6;
        }

        .btn-primary {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 12px 28px;
          background: #fff;
          color: #000;
          border: none;
          border-radius: 8px;
          font-size: 14px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            background: #f0f0f0;
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(255, 255, 255, 0.2);
          }

          &:active {
            transform: translateY(0);
          }
        }
      }
    }
  }

  /* 瀑布流布局 */
  .album-masonry {
    max-width: 1600px;
    margin: 0 auto;
    padding: 48px 24px;
    column-count: 4;
    column-gap: 20px;

    @media (max-width: 1200px) {
      column-count: 3;
    }

    @media (max-width: 900px) {
      column-count: 2;
    }

    @media (max-width: 600px) {
      column-count: 2;
      column-gap: 12px;
      padding: 24px 16px;
    }

    .masonry-item {
      break-inside: avoid;
      margin-bottom: 20px;
      opacity: 0;
      animation: fadeInUp 0.5s ease forwards;

      @media (max-width: 600px) {
        margin-bottom: 12px;
      }

      .item-inner {
        background: var(--bg-card);
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        cursor: pointer;

        &:hover {
          box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
          transform: translateY(-2px);

          .hover-layer {
            opacity: 1;
          }

          .album-cover {
            transform: scale(1.05);
          }
        }

        .image-container {
          position: relative;
          overflow: hidden;

          .album-cover {
            width: 100%;
            display: block;
            transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
          }

          .placeholder,
          .error-state {
            width: 100%;
            height: 280px;
            background: var(--hover-bg);
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
            color: var(--text-main);
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

/* 黑夜模式 */
html.dark {
  .album-page {
    --bg-primary: #0a0a0a;
    --bg-card: #141414;
    --text-main: #ffffff;
    --text-secondary: #a0a0a0;
    --text-muted: #666666;
    --border-color: #222222;
    --accent-color: #ffffff;
    --hover-bg: #1a1a1a;

    .album-hero {
      background: linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%);

      &::before {
        background: radial-gradient(circle, rgba(255,255,255,0.02) 0%, transparent 70%);
      }

      &::after {
        background: radial-gradient(circle, rgba(255,255,255,0.01) 0%, transparent 70%);
      }
    }

    .album-masonry {
      .masonry-item {
        .item-inner {
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);

          &:hover {
            box-shadow: 0 12px 40px rgba(0, 0, 0, 0.5);
          }
        }
      }
    }
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

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
