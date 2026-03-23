<template>
  <div class="my-album-page">
    <!-- Hero Banner -->
    <div class="page-hero">
      <div class="hero-content">
        <div class="hero-copy">
          <div class="hero-kicker">My Album</div>
          <h1>我的相册</h1>
          <p>管理你的个人相册，记录美好时光。</p>
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
  // 浅色模式
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

  min-height: 100vh;
  background: var(--bg-page);

  // Hero Banner
  .page-hero {
    position: relative;
    padding: 80px 24px 64px;
    background: linear-gradient(to bottom, var(--bg-card), var(--bg-page));
    border-bottom: 1px solid var(--border-regular);

    // 顶部渐变线条
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      height: 3px;
      background: linear-gradient(90deg, var(--accent-color), var(--accent-soft));
    }

    .hero-content {
      max-width: 1200px;
      margin: 0 auto;

      .hero-copy {
        max-width: 600px;

        .hero-kicker {
          font-size: 12px;
          font-weight: 600;
          letter-spacing: 2px;
          text-transform: uppercase;
          color: var(--accent-color);
          margin-bottom: 12px;
        }

        h1 {
          font-size: 42px;
          font-weight: 800;
          color: var(--text-primary);
          margin: 0 0 16px 0;
          line-height: 1.2;
        }

        p {
          font-size: 16px;
          color: var(--text-secondary);
          margin: 0;
          line-height: 1.6;
        }
      }
    }
  }

  // 统计指标栏
  .hero-metrics {
    display: flex;
    gap: 32px;
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid var(--border-light);

    .metric {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .metric-value {
        font-size: 24px;
        font-weight: 700;
        color: var(--text-primary);
      }

      .metric-label {
        font-size: 13px;
        color: var(--text-muted);
      }
    }
  }

  // 操作按钮区
  .hero-actions {
    display: flex;
    align-items: center;
    gap: 16px;
    margin-top: 32px;

    .album-count {
      font-size: 14px;
      color: var(--text-secondary);
    }

    .btn-primary {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 10px 20px;
      background: var(--accent-color);
      color: #ffffff;
      border: none;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: var(--accent-hover);
        box-shadow: 0 4px 12px var(--accent-border);
      }

      &:active {
        transform: scale(0.98);
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
        border: 1px solid var(--border-regular);
        border-radius: 12px;
        overflow: hidden;
        transition: all 0.25s ease;

        // 底部渐变线条
        &::after {
          content: '';
          position: absolute;
          bottom: 0;
          left: 0;
          right: 0;
          height: 2px;
          background: linear-gradient(90deg, var(--accent-color), var(--accent-soft));
          opacity: 0;
          transition: opacity 0.25s ease;
        }

        &:hover {
          border-color: var(--border-hover);
          box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
          transform: translateY(-2px);

          &::after {
            opacity: 1;
          }

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
            background: var(--bg-subtle);
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
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            justify-content: center;
            align-items: center;
            opacity: 0;
            transition: opacity 0.3s ease;

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
          position: relative;

          .album-title-text {
            font-size: 15px;
            font-weight: 600;
            color: var(--text-primary);
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
                  background: #ef4444;
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

// 黑夜模式
html.dark {
  .my-album-page {
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

    .page-hero {
      background: linear-gradient(to bottom, var(--bg-card), var(--bg-page));
      border-bottom-color: var(--border-regular);
    }

    .album-masonry {
      .masonry-item {
        .item-inner {
          &:hover {
            box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
          }
        }
      }
    }
  }
}

/* 弹窗样式 */
.album-dialog {
  :deep(.el-dialog__header) {
    padding: 20px 24px;
    border-bottom: 1px solid var(--border-regular);
  }

  :deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary);
  }

  :deep(.el-dialog__body) {
    padding: 24px;
  }

  :deep(.el-form-item__label) {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-primary);
  }

  :deep(.el-input__wrapper) {
    background: var(--bg-card);
  }

  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid var(--border-regular);
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
