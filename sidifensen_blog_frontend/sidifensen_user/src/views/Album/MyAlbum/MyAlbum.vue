<template>
  <div class="my-album-page">
    <!-- 顶部 Banner 区 -->
    <div class="album-hero">
      <div class="hero-content">
        <div class="hero-header">
          <button class="btn-back" @click="goToAlbumSquare" title="返回相册广场">
            <el-icon><ArrowLeft /></el-icon>
          </button>
          <!-- 手机端：返回按钮在左上角 -->
          <button class="btn-back-mobile" @click="goToAlbumSquare" title="返回相册广场">
            <el-icon><ArrowLeft /></el-icon>
          </button>
          <h1 class="hero-title">
            <span class="title-icon">📁</span>
            MY ALBUMS
          </h1>
        </div>
        <p class="hero-subtitle">管理你的个人相册，记录美好时光</p>
        <div class="hero-actions">
          <span class="album-count">{{ albumList.length }} 个相册</span>
          <button class="btn-primary" @click="handleCreateAlbum">
            <el-icon><Plus /></el-icon>
            新建相册
          </button>
        </div>
      </div>
    </div>

    <!-- 主要内容区 -->
    <LoadingAnimation v-if="loading" />

    <div v-else-if="albumList.length > 0" class="album-masonry">
      <div
        v-for="(album, index) in albumList"
        :key="album.id"
        class="masonry-item"
        :style="{ animationDelay: `${index * 0.05}s` }"
      >
        <div class="item-inner">
          <div class="image-container" @click="handleViewAlbum(album.id)">
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
                <span class="view-text">查看相册</span>
              </div>
            </div>
          </div>

          <div class="item-footer">
            <div class="album-title-text">{{ album.name }}</div>
            <div class="album-meta">
              <span class="create-date">{{ formatDate(album.createTime) }}</span>
              <div class="actions">
                <el-button
                  class="btn-icon"
                  type="danger"
                  size="small"
                  circle
                  @click.stop="handleDeleteAlbum(album.id)"
                >
                  <el-icon><Delete /></el-icon>
                </el-button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-empty v-else description="暂无相册" :image-size="160" />

    <!-- 新建/编辑相册弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑相册' : '新建相册'" class="album-dialog">
      <el-form :model="albumForm" label-position="top">
        <el-form-item label="相册名称">
          <el-input v-model="albumForm.name" placeholder="请输入相册名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAlbumForm">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue"
import { useRouter } from "vue-router"
import { useUserStore } from "@/stores/userStore"
import { storeToRefs } from "pinia"
import { ElMessage, ElMessageBox } from "element-plus"
import { Plus, Picture, Loading, ZoomIn, Delete, ArrowLeft } from "@element-plus/icons-vue"
import { listAlbum, createAlbum, updateAlbum, deleteAlbum } from "@/api/album"
import LoadingAnimation from "@/components/LoadingAnimation.vue"

const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const loading = ref(false)
const albumList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const albumForm = ref({ id: null, name: "", coverUrl: "" })

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return ""
  const date = new Date(dateString)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
}

// 获取相册列表
const getAlbumList = async () => {
  loading.value = true
  try {
    const res = await listAlbum()
    albumList.value = res.data.data.sort((a, b) => a.id - b.id)
  } catch (error) {
    ElMessage.error("获取相册列表失败")
  } finally {
    loading.value = false
  }
}

// 新建相册
const handleCreateAlbum = () => {
  albumForm.value = { id: null, name: "", coverUrl: "" }
  isEdit.value = false
  dialogVisible.value = true
}

// 提交相册表单
const submitAlbumForm = async () => {
  if (!albumForm.value.name) {
    ElMessage.warning("请输入相册名称")
    return
  }

  try {
    if (albumForm.value.id) {
      const AlbumDto = {
        id: albumForm.value.id,
        name: albumForm.value.name,
        coverUrl: albumForm.value.coverUrl,
      }
      await updateAlbum(AlbumDto)
      ElMessage.success("更新相册成功")
    } else {
      await createAlbum(albumForm.value)
      ElMessage.success("创建相册成功")
    }
    dialogVisible.value = false
    getAlbumList()
  } catch (error) {
    ElMessage.error("操作相册失败")
  }
}

// 查看相册
const handleViewAlbum = (albumId) => {
  router.push({ name: "AlbumDetail", params: { albumId } })
}

// 返回相册广场
const goToAlbumSquare = () => {
  router.push({ name: "AlbumSquare" })
}

// 删除相册
const handleDeleteAlbum = async (albumId) => {
  ElMessageBox.confirm("确定要删除这个相册吗？此操作不可恢复", "删除相册", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAlbum(albumId)
        ElMessage.success("删除相册成功")
        getAlbumList()
      } catch (error) {
        ElMessage.error("删除相册失败")
      }
    })
    .catch(() => {
      ElMessage.info("已取消删除")
    })
}

onMounted(() => {
  if (!user.value) {
    ElMessage.error("请先登录")
    router.push("/login")
  } else {
    getAlbumList()
  }
})
</script>

<style lang="scss" scoped>
.my-album-page {
  --bg-primary: #fafafa;
  --bg-card: #ffffff;
  --text-main: #1a1a1a;
  --text-secondary: #666666;
  --text-muted: #999999;
  --border-color: #eeeeee;
  --accent-color: #000000;
  --hover-bg: #f5f5f5;
  --danger-color: #ef4444;

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

      .hero-header {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 16px;
        margin-bottom: 16px;
        position: relative;

        .btn-back {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 40px;
          height: 40px;
          background: rgba(255, 255, 255, 0.1);
          border: 1px solid rgba(255, 255, 255, 0.2);
          border-radius: 50%;
          color: #fff;
          cursor: pointer;
          transition: all 0.2s ease;
          backdrop-filter: blur(10px);

          // 手机端：隐藏返回按钮，使用绝对定位的版本
          @media (max-width: 600px) {
            display: none;
          }

          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(-4px);
          }

          &:active {
            transform: translateX(-4px) scale(0.95);
          }

          .el-icon {
            font-size: 18px;
          }
        }

        // 手机端：返回按钮绝对定位在左上角
        .btn-back-mobile {
          display: none;
          position: absolute;
          left: 0;
          top: 0;
          align-items: center;
          justify-content: center;
          width: 36px;
          height: 36px;
          background: rgba(255, 255, 255, 0.1);
          border: 1px solid rgba(255, 255, 255, 0.2);
          border-radius: 50%;
          color: #fff;
          cursor: pointer;
          transition: all 0.2s ease;
          backdrop-filter: blur(10px);

          @media (max-width: 600px) {
            display: flex;
          }

          &:hover {
            background: rgba(255, 255, 255, 0.2);
          }

          .el-icon {
            font-size: 16px;
          }
        }
      }

      .hero-title {
        margin: 0;
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
          cursor: pointer;

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
            background: rgba(0, 0, 0, 0.6);
            display: flex;
            justify-content: center;
            align-items: center;
            opacity: 0;
            transition: opacity 0.3s ease;
            backdrop-filter: blur(2px);

            .layer-content {
              display: flex;
              flex-direction: column;
              align-items: center;
              gap: 8px;
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

              .view-text {
                font-size: 14px;
                font-weight: 500;
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
            margin: 0 0 8px 0;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .album-meta {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .create-date {
              font-size: 12px;
              color: var(--text-muted);
            }

            .actions {
              .btn-icon {
                width: 28px;
                height: 28px;
                padding: 0;
                border-radius: 50%;
                opacity: 0;
                transform: translateX(10px);
                transition: all 0.2s ease;

                &:hover {
                  background: var(--danger-color);
                  color: #fff;
                }
              }
            }
          }

          &:hover .actions .btn-icon {
            opacity: 1;
            transform: translateX(0);
          }
        }
      }
    }
  }
}

/* 黑夜模式 */
html.dark {
  .my-album-page {
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

      .hero-content {
        .hero-header {
          .btn-back {
            &:hover {
              background: rgba(255, 255, 255, 0.15);
            }
          }
        }
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

    .album-dialog {
      background: var(--bg-card);
    }
  }
}

/* 弹窗样式 */
.album-dialog {
  :deep(.el-dialog__header) {
    padding: 20px 24px;
    border-bottom: 1px solid var(--border-color);
  }

  :deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-main);
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-main);
  }

  :deep(.el-input__wrapper) {
    background: var(--bg-card);
  }

  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid var(--border-color);
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
