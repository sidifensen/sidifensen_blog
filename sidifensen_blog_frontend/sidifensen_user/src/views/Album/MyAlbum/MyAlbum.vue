<template>
  <div class="my-album-page">
    <div class="container">
      <!-- Hero Banner -->
      <div class="page-hero">
        <div class="hero-copy">
          <div class="hero-kicker">My Album</div>
          <h1>我的相册</h1>
          <p>管理你的个人相册，记录美好时光。</p>
        </div>
        <div class="hero-actions">
          <button class="btn-square" @click="goToAlbumSquare">
            <el-icon><ArrowLeft /></el-icon>
            <span>返回相册广场</span>
          </button>
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
    albumList.value = res.data.sort((a, b) => a.id - b.id)
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
// 相册详情页 - 现代极简主义科技风
// 完全匹配参考文件：Article/index.vue 和 AlbumSquare.vue

.my-album-page {
  width: 100%;
  min-height: 100vh;
  background: var(--bg-page);
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

  // ===== 页面头部 =====
  .page-hero {
    padding: 60px 0 40px;
    border-bottom: 1px solid var(--bg-hero-divider);

    @media (max-width: 768px) {
      padding: 40px 0 25px;
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
      .btn-square {
        display: flex;
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

  /* 瀑布流布局 */
  .album-masonry {
    padding: 40px 0;
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
      padding: 24px 0;
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
            color: var(--text-primary);
            margin: 0 0 6px 0;
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
