<template>
  <div class="album-detail-page">
    <!-- 顶部 Banner 区 -->
    <div class="album-hero">
      <div class="hero-content">
        <div class="hero-top">
          <button class="back-btn" @click="goBack">
            <el-icon><ArrowLeftBold /></el-icon>
          </button>
          <div class="hero-title-wrapper">
            <h1 class="hero-title">
              <span class="title-icon">🖼️</span>
              {{ albumForm.name }}
            </h1>
            <div v-if="albumForm.userName" class="user-info">
              <el-avatar :size="24" class="user-avatar">
                <el-icon><User /></el-icon>
              </el-avatar>
              <span class="user-name">@{{ albumForm.userName }}</span>
            </div>
          </div>
        </div>

        <!-- 操作按钮区 -->
        <div v-if="isAlbumOwner" class="hero-actions">
          <button class="btn-upload" @click="handleUploadPhoto">
            <el-icon><UploadFilled /></el-icon>
            <span>上传图片</span>
          </button>
          <button class="btn-edit" @click="handleEditAlbum">
            <el-icon><Edit /></el-icon>
            <span>编辑相册</span>
          </button>
          <button v-if="!isSelectMode" class="btn-select" @click="toggleSelectMode">
            选择图片
          </button>
          <template v-else>
            <button class="btn-select-all" @click="toggleAllSelection">
              {{ isAllSelected ? "取消全选" : "全选" }}
            </button>
            <button class="btn-select-active" @click="toggleSelectMode">
              完成
            </button>
          </template>
          <button
            v-show="selectedPhotos.length > 0"
            class="btn-delete"
            @click="handleBatchDelete"
          >
            <el-icon><Delete /></el-icon>
            <span>删除 ({{ selectedPhotos.length }})</span>
          </button>
          <el-dropdown trigger="hover">
            <button class="btn-more">
              <el-icon><MoreFilled /></el-icon>
            </button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleDeleteAlbum" class="danger-item">
                  <el-icon><Delete /></el-icon> 删除相册
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-switch
            v-model="switchShowStatus"
            @click="handleChangeAlbumShowStatus"
            active-text="公开"
            inactive-text="私密"
            inline-prompt
            class="show-status-switch"
          />
        </div>
      </div>
    </div>

    <!-- 图片内容区 -->
    <LoadingAnimation v-if="loading" />

    <div v-else class="album-content">
      <template v-for="(group, date) in groupedPhotos" :key="date">
        <div class="date-section">
          <div class="date-header">
            <div class="date-label">
              <el-icon><Calendar /></el-icon>
              <span>{{ date }}</span>
            </div>
            <el-checkbox
              v-if="isSelectMode"
              v-model="dateSelection[date]"
              @change="handleDateSelectionChange(date)"
              size="large"
            />
          </div>
          <div class="photo-grid">
            <div
              v-for="photo in group"
              :key="photo.id"
              class="photo-item"
              :class="{ selected: selectedPhotos.includes(photo.id) }"
              @click="handlePhotoClick(photo)"
            >
              <el-image
                :src="photo.url || ''"
                class="photo"
                fit="cover"
                :preview-src-list="isSelectMode ? [] : getAllPhotoUrls()"
                :preview-teleported="!isSelectMode"
                :initial-index="getPhotoIndex(photo)"
                show-progress
                close-on-press-escape
                lazy
              >
                <template #toolbar="{ actions, prev, next, activeIndex }">
                  <el-icon @click="prev"><Back /></el-icon>
                  <el-icon @click="next"><Right /></el-icon>
                  <el-icon @click="actions('zoomOut')"><ZoomOut /></el-icon>
                  <el-icon @click="actions('zoomIn', { enableTransition: false, zoomRate: 2 })">
                    <ZoomIn />
                  </el-icon>
                  <el-icon @click="download(activeIndex)"><Download /></el-icon>
                </template>
                <template #placeholder>
                  <div class="placeholder">
                    <el-icon class="spinner"><Loading /></el-icon>
                  </div>
                </template>
                <template #error>
                  <div class="error">
                    <el-icon><Picture /></el-icon>
                  </div>
                </template>
              </el-image>

              <!-- 审核状态 -->
              <div class="examine-status" :class="getExamineStatusClass(photo.examineStatus)">
                {{ getExamineStatusText(photo.examineStatus) }}
              </div>

              <!-- 选中标记 -->
              <div v-if="isSelectMode" class="check-mark">
                <el-icon v-if="selectedPhotos.includes(photo.id)"><Check /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </template>

      <el-empty v-if="Object.keys(groupedPhotos).length === 0 && !loading" description="暂无图片" :image-size="160" />
    </div>

    <!-- 上传图片对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传图片" class="upload-dialog" :close-on-click-modal="false" @close="handleUploadDialogClose">
      <div class="upload-area">
        <input
          type="file"
          ref="fileInputRef"
          accept=".jpg,.gif,.png,.jpeg,.webp"
          multiple
          class="file-input"
          @change="handleFileSelect"
        />
        <button class="btn-select-files" @click="triggerFileInput">
          <el-icon><UploadFilled /></el-icon>
          选择图片
        </button>
        <div class="upload-tip">jpg/gif/png/jpeg/webp 格式，单个文件不超过 5MB</div>
      </div>
      <div class="preview-container">
        <div v-for="(file, index) in fileList" :key="index" class="preview-item">
          <el-image
            :src="file.url"
            class="preview-image"
            fit="cover"
          />
          <button class="btn-remove" @click="removeFile(index)">
            <el-icon><Close /></el-icon>
          </button>
        </div>
      </div>
      <template #footer>
        <button class="btn-cancel" @click="uploadDialogVisible = false">取消</button>
        <button class="btn-confirm" @click="submitUpload" :loading="uploadLoading">开始上传</button>
      </template>
    </el-dialog>

    <!-- 编辑相册对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑相册" class="edit-dialog">
      <el-form :model="albumForm" label-position="top">
        <el-form-item label="相册名称">
          <el-input v-model="albumForm.name" placeholder="请输入相册名称" />
        </el-form-item>
        <el-form-item label="相册封面" v-if="albumForm.coverUrl">
          <div class="cover-container" @click="handleChangeCover">
            <el-image :src="albumForm.coverUrl" class="cover-image" fit="cover" />
            <div class="cover-overlay">
              <span>点击更换封面</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <button class="btn-cancel" @click="editDialogVisible = false">取消</button>
        <button class="btn-confirm" @click="submitEditAlbum">确定</button>
      </template>
    </el-dialog>

    <!-- 选择封面对话框 -->
    <el-dialog v-model="coverDialogVisible" title="选择封面图片" class="cover-dialog">
      <div class="cover-selection-container">
        <div
          v-for="(photo, index) in coverDialogPhotos"
          :key="index"
          class="cover-image-container"
          @click="selectedCoverUrl = photo.url"
          :class="{ selected: selectedCoverUrl === photo.url }"
        >
          <el-image :src="photo.url" class="cover-select-image" fit="cover" />
        </div>
      </div>
      <template #footer>
        <button class="btn-cancel" @click="coverDialogVisible = false">取消</button>
        <button class="btn-confirm" @click="confirmCoverChange">确定</button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue"
import { useRoute, useRouter } from "vue-router"
import { useUserStore } from "@/stores/userStore"
import { storeToRefs } from "pinia"
import { ElMessage, ElMessageBox } from "element-plus"
import {
  ArrowLeftBold, User, UploadFilled, Edit, Delete, MoreFilled, Calendar,
  Back, Right, ZoomOut, ZoomIn, Download, Picture, Loading, Check, Close
} from "@element-plus/icons-vue"
import { getAlbum, updateAlbum, deleteAlbum, changeShowStatus, changeCover } from "@/api/album"
import { uploadAlbumPhoto, batchDeletePhoto } from "@/api/photo"
import { compressImage } from "@/utils/PhotoUtils"
import LoadingAnimation from "@/components/LoadingAnimation.vue"

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const loading = ref(false)
const albumId = Number(route.params.albumId)
const albumForm = ref({
  id: albumId,
  name: "",
  coverUrl: "",
  showStatus: 0,
  createTime: "",
  userId: "",
  userName: "",
})

const photoList = ref([])

const getPhotoList = async () => {
  loading.value = true
  try {
    const res = await getAlbum(albumForm.value.id)
    photoList.value = res.data.data.photos || []
    albumForm.value.showStatus = res.data.data.showStatus || 0
    albumForm.value.name = res.data.data.name || ""
    albumForm.value.coverUrl = res.data.data.coverUrl || ""
    albumForm.value.userName = res.data.data.userName || ""
    albumForm.value.userId = res.data.data.userId || ""
    groupPhotosByDate()
  } catch (error) {
    ElMessage.error("获取相册图片失败")
  } finally {
    loading.value = false
  }
}

const groupPhotosByDate = () => {
  const groups = {}
  const sortedPhotos = [...photoList.value].sort((a, b) => new Date(b.createTime) - new Date(a.createTime))
  sortedPhotos.forEach((photo) => {
    const date = photo.createTime.split(" ")[0]
    if (!groups[date]) {
      groups[date] = []
    }
    groups[date].push(photo)
  })
  groupedPhotos.value = groups
}

const groupedPhotos = ref({})

// 是否所有者
const isAlbumOwner = computed(() => {
  if (!user.value) return false
  return user.value.id === albumForm.value.userId
})

// 开关状态
const switchShowStatus = computed(() => albumForm.value.showStatus === 0)

// 选择模式
const isSelectMode = ref(false)
const isAllSelected = ref(false)
const selectedPhotos = ref([])
const dateSelection = ref({})

const toggleSelectMode = () => {
  isSelectMode.value = !isSelectMode.value
  if (!isSelectMode.value) {
    selectedPhotos.value = []
    isAllSelected.value = false
    dateSelection.value = {}
  } else {
    for (const date in groupedPhotos.value) {
      dateSelection.value[date] = false
    }
  }
}

const toggleAllSelection = () => {
  isAllSelected.value = !isAllSelected.value
  if (isAllSelected.value) {
    selectedPhotos.value = []
    for (const date in groupedPhotos.value) {
      const group = groupedPhotos.value[date]
      group.forEach((photo) => selectedPhotos.value.push(photo.id))
      dateSelection.value[date] = true
    }
  } else {
    selectedPhotos.value = []
    for (const date in groupedPhotos.value) {
      dateSelection.value[date] = false
    }
  }
}

const handleDateSelectionChange = (date) => {
  const group = groupedPhotos.value[date]
  if (dateSelection.value[date]) {
    group.forEach((photo) => {
      if (!selectedPhotos.value.includes(photo.id)) {
        selectedPhotos.value.push(photo.id)
      }
    })
  } else {
    selectedPhotos.value = selectedPhotos.value.filter((id) => {
      return !group.some((photo) => photo.id === id)
    })
  }
  updateAllSelectedStatus()
}

const updateAllSelectedStatus = () => {
  let allSelected = true
  for (const date in groupedPhotos.value) {
    const group = groupedPhotos.value[date]
    const allInDateSelected = group.every((photo) => selectedPhotos.value.includes(photo.id))
    if (!allInDateSelected) {
      allSelected = false
    }
    dateSelection.value[date] = allInDateSelected
  }
  isAllSelected.value = allSelected
}

const handlePhotoClick = (photo) => {
  if (!isSelectMode.value) return
  const index = selectedPhotos.value.indexOf(photo.id)
  if (index === -1) {
    selectedPhotos.value.push(photo.id)
  } else {
    selectedPhotos.value.splice(index, 1)
  }
}

const handleBatchDelete = () => {
  if (selectedPhotos.value.length === 0) {
    ElMessage.warning("请先选择要删除的图片")
    return
  }
  ElMessageBox.confirm(`确定要删除这 ${selectedPhotos.value.length} 张图片吗？此操作不可恢复`, "删除图片", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await batchDeletePhoto(selectedPhotos.value)
        ElMessage.success("删除成功")
        selectedPhotos.value = []
        getPhotoList()
      } catch (error) {
        ElMessage.error("删除失败")
      }
    })
    .catch(() => {
      ElMessage.info("已取消删除")
    })
}

// 上传
const uploadDialogVisible = ref(false)
const fileList = ref([])
const uploadLoading = ref(false)
const fileInputRef = ref(null)

// 触发文件选择
const triggerFileInput = () => {
  fileInputRef.value?.click()
}

// 文件选择处理
const handleFileSelect = (event) => {
  const files = event.target.files
  if (!files || files.length === 0) return

  // 读取每个文件为 data URL 用于预览
  const newFiles = Array.from(files).map((file) => ({
    raw: file,
    url: "",
  }))

  // 使用 FileReader 读取文件为 data URL
  let loadedCount = 0
  newFiles.forEach((fileItem, index) => {
    const reader = new FileReader()
    reader.onload = (e) => {
      fileItem.url = e.target.result
      loadedCount++
      if (loadedCount === newFiles.length) {
        // 所有文件读取完成
        fileList.value = [...fileList.value, ...newFiles]
      }
    }
    reader.readAsDataURL(fileItem.raw)
  })

  // 清空 input 以便可以再次选择相同文件
  event.target.value = ""
}

// 关闭上传对话框时清理状态
const handleUploadDialogClose = () => {
  fileList.value = []
}

// 移除已选择的图片
const removeFile = (index) => {
  fileList.value.splice(index, 1)
}

const handleUploadPhoto = () => {
  fileList.value = []
  uploadDialogVisible.value = true
}

const submitUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning("请选择要上传的图片")
    return
  }
  uploadLoading.value = true
  try {
    const compressedFiles = []
    for (const file of fileList.value) {
      const compressedFile = await compressImage(file.raw)
      compressedFiles.push(compressedFile)
    }
    const uploadPromises = compressedFiles.map((file) => uploadAlbumPhoto(file, albumForm.value.id))
    await Promise.all(uploadPromises)
    ElMessage.success("图片上传成功")
    uploadDialogVisible.value = false
    setTimeout(() => {
      getPhotoList()
    }, 2000)
  } catch (error) {
    ElMessage.error("上传图片失败")
  } finally {
    uploadLoading.value = false
  }
}

// 编辑
const editDialogVisible = ref(false)

const handleEditAlbum = () => {
  editDialogVisible.value = true
}

const submitEditAlbum = async () => {
  if (!albumForm.value.name) {
    ElMessage.warning("请输入相册名称")
    return
  }
  try {
    await updateAlbum(albumForm.value)
    ElMessage.success("编辑相册成功")
    editDialogVisible.value = false
    getPhotoList()
  } catch (error) {
    ElMessage.error("编辑相册失败")
  }
}

// 删除相册
const handleDeleteAlbum = async () => {
  ElMessageBox.confirm("确定要删除这个相册吗？此操作不可恢复", "删除相册", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAlbum(albumForm.value.id)
        ElMessage.success("删除相册成功")
        router.push("/album")
      } catch (error) {
        ElMessage.error("删除相册失败")
      }
    })
    .catch(() => {
      ElMessage.info("已取消删除")
    })
}

// 修改展示状态
const handleChangeAlbumShowStatus = async () => {
  try {
    const newStatus = albumForm.value.showStatus === 0 ? 1 : 0
    await changeShowStatus({ id: albumForm.value.id, showStatus: newStatus })
    albumForm.value.showStatus = newStatus
    ElMessage.success("修改相册状态成功")
  } catch (error) {
    ElMessage.error("修改相册状态失败")
  }
}

// 更换封面
const coverDialogVisible = ref(false)
const selectedCoverUrl = ref("")
const coverDialogPhotos = ref([])

const handleChangeCover = async () => {
  try {
    const res = await getAlbum(albumId)
    const photos = res.data.data.photos
    if (!photos || photos.length === 0) {
      ElMessage.warning("该相册暂无图片，请先上传图片")
      return
    }
    coverDialogPhotos.value = [...photos].reverse()
    selectedCoverUrl.value = ""
    coverDialogVisible.value = true
  } catch (error) {
    ElMessage.error("更换封面失败")
  }
}

const confirmCoverChange = async () => {
  if (!selectedCoverUrl.value) {
    ElMessage.warning("请选择一张图片作为封面")
    return
  }
  try {
    await changeCover({ id: albumId, coverUrl: selectedCoverUrl.value })
    ElMessage.success("更换封面成功")
    albumForm.value.coverUrl = selectedCoverUrl.value
    coverDialogVisible.value = false
    getPhotoList()
  } catch (error) {
    ElMessage.error("更换封面失败")
  }
}

const goBack = () => {
  router.go(-1)
}

const getAllPhotoUrls = () => {
  const allUrls = []
  for (const date in groupedPhotos.value) {
    groupedPhotos.value[date].forEach((photo) => {
      allUrls.push(photo.url)
    })
  }
  return allUrls
}

const getPhotoIndex = (photo) => {
  const allPhotos = []
  for (const date in groupedPhotos.value) {
    allPhotos.push(...groupedPhotos.value[date])
  }
  return allPhotos.findIndex((p) => p.id === photo.id)
}

const getExamineStatusText = (status) => {
  const texts = { 0: "待审核", 1: "", 2: "审核未通过" }
  return texts[status] || "未知状态"
}

const getExamineStatusClass = (status) => {
  const classes = { 0: "status-pending", 1: "status-approved", 2: "status-rejected" }
  return classes[status] || "status-unknown"
}

const download = (number) => {
  const allUrls = getAllPhotoUrls()
  const url = allUrls[number]
  const suffix = url.slice(url.lastIndexOf("."))
  const filename = Date.now() + suffix
  fetch(url)
    .then((response) => response.blob())
    .then((blob) => {
      const blobUrl = URL.createObjectURL(new Blob([blob]))
      const link = document.createElement("a")
      link.href = blobUrl
      link.download = filename
      document.body.appendChild(link)
      link.click()
      URL.revokeObjectURL(blobUrl)
      link.remove()
    })
}

onMounted(() => {
  getPhotoList()
})
</script>

<style lang="scss" scoped>
.album-detail-page {
  --bg-primary: #fafafa;
  --bg-card: #ffffff;
  --text-main: #1a1a1a;
  --text-secondary: #666666;
  --text-muted: #999999;
  --border-color: #eeeeee;
  --hover-bg: #f5f5f5;
  --danger-color: #ef4444;

  min-height: 100vh;
  background: var(--bg-primary);
  overflow-x: hidden;

  /* Hero Banner */
  .album-hero {
    position: relative;
    min-height: 280px;
    background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
    padding: 24px 40px;
    box-sizing: border-box;

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

    .hero-content {
      position: relative;
      max-width: 1400px;
      margin: 0 auto;

      .hero-top {
        display: flex;
        align-items: center;
        gap: 20px;
        margin-bottom: 24px;

        .back-btn {
          width: 40px;
          height: 40px;
          border-radius: 8px;
          background: rgba(255, 255, 255, 0.1);
          border: none;
          color: #fff;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.2s ease;
          font-size: 20px;

          &:hover {
            background: rgba(255, 255, 255, 0.2);
            transform: translateX(-4px);
          }
        }

        .hero-title-wrapper {
          flex: 1;
          display: flex;
          align-items: center;
          gap: 16px;

          .hero-title {
            margin: 0;
            font-size: 32px;
            font-weight: 700;
            color: #fff;
            display: flex;
            align-items: center;
            gap: 12px;
            letter-spacing: 2px;

            .title-icon {
              font-size: 36px;
            }
          }

          .user-info {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 6px 12px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            backdrop-filter: blur(10px);

            .user-avatar {
              background: rgba(255, 255, 255, 0.2);
              color: #fff;
            }

            .user-name {
              color: rgba(255, 255, 255, 0.9);
              font-size: 14px;
              font-weight: 500;
            }
          }
        }
      }

      /* 操作按钮区 */
      .hero-actions {
        display: flex;
        gap: 12px;
        align-items: center;
        flex-wrap: wrap;
        padding: 16px 20px;
        background: rgba(255, 255, 255, 0.05);
        border-radius: 12px;
        border: 1px solid rgba(255, 255, 255, 0.1);

        .btn-upload,
        .btn-edit,
        .btn-select,
        .btn-select-all,
        .btn-select-active {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 14px;
          border-radius: 6px;
          font-size: 13px;
          font-weight: 500;
          border: none;
          cursor: pointer;
          transition: all 0.2s ease;

          .el-icon {
            font-size: 16px;
          }
        }

        .btn-upload {
          background: #fff;
          color: #000;

          &:hover {
            background: #f0f0f0;
            transform: translateY(-2px);
            box-shadow: 0 8px 24px rgba(255, 255, 255, 0.2);
          }
        }

        .btn-edit,
        .btn-select {
          background: rgba(255, 255, 255, 0.2);
          color: #fff;

          &:hover {
            background: rgba(255, 255, 255, 0.3);
          }
        }

        .btn-select-all,
        .btn-select-active {
          background: transparent;
          color: #fff;
          border: 1px solid rgba(255, 255, 255, 0.3);

          &:hover {
            background: rgba(255, 255, 255, 0.1);
            border-color: rgba(255, 255, 255, 0.5);
          }
        }

        .btn-delete {
          display: flex;
          align-items: center;
          gap: 6px;
          padding: 8px 14px;
          border-radius: 6px;
          font-size: 13px;
          font-weight: 500;
          background: transparent;
          color: #f56c6c;
          border: 1px solid rgba(245, 108, 108, 0.5);
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            background: rgba(245, 108, 108, 0.1);
            border-color: #f56c6c;
          }
        }

        .btn-more {
          background: rgba(255, 255, 255, 0.1);
          color: #fff;
          width: 32px;
          height: 32px;
          display: flex;
          justify-content: center;
          align-items: center;
          padding: 0;
          border: none;
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            background: rgba(255, 255, 255, 0.2);
          }
        }

        .show-status-switch {
          margin-left: 8px;

          :deep(.el-switch__core) {
            width: 60px;
            height: 24px;
            border-radius: 12px;
            background-color: #909399;
            border: none;
            transition: background-color 0.2s ease;
          }

          :deep(.is-checked .el-switch__core) {
            background-color: #409eff;
            border: none;
          }

          :deep(.el-switch__action) {
            width: 20px;
            height: 20px;
            background-color: #fff;
            border: none;
            transition: transform 0.2s ease;
          }

          :deep(.el-switch__label) {
            font-size: 12px;
            font-weight: 500;
            color: #fff;
          }
        }
      }
    }
  }

  /* 内容区 */
  .album-content {
    max-width: 1400px;
    margin: 0 auto;
    padding: 40px 24px;

    .date-section {
      margin-bottom: 40px;

      .date-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        padding: 12px 16px;
        background: var(--bg-card);
        border-radius: 8px;
        border: 1px solid var(--border-color);

        .date-label {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 16px;
          font-weight: 600;
          color: var(--text-main);

          .el-icon {
            color: var(--text-muted);
          }
        }
      }

      .photo-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 16px;

        .photo-item {
          position: relative;
          aspect-ratio: 4/3;
          border-radius: 8px;
          overflow: hidden;
          background: var(--hover-bg);
          cursor: pointer;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

          &:hover {
            transform: translateY(-4px);
            box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);

            .photo {
              transform: scale(1.08);
            }
          }

          &.selected {
            box-shadow: 0 0 0 3px #409eff;
          }

          .photo {
            width: 100%;
            height: 100%;
            transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);

            .placeholder,
            .error {
              width: 100%;
              height: 100%;
              background: #e4e7ed;
              display: flex;
              justify-content: center;
              align-items: center;

              .spinner {
                animation: rotate 1.5s linear infinite;
                font-size: 28px;
                color: #909399;
              }

              .el-icon {
                font-size: 40px;
                color: #909399;
              }
            }
          }

          .examine-status {
            position: absolute;
            bottom: 8px;
            right: 8px;
            padding: 4px 10px;
            border-radius: 12px;
            font-size: 12px;
            font-weight: 600;
            color: #fff;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);

            &.status-pending {
              background: linear-gradient(135deg, #909399, #606266);
            }

            &.status-rejected {
              background: linear-gradient(135deg, #f56c6c, #dd3e3e);
            }
          }

          .check-mark {
            position: absolute;
            top: 8px;
            right: 8px;
            width: 28px;
            height: 28px;
            background: #409eff;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            color: #fff;
            opacity: 0;
            transform: scale(0.8);
            transition: all 0.2s ease;

            .el-icon {
              font-size: 16px;
            }
          }

          &.selected .check-mark {
            opacity: 1;
            transform: scale(1);
          }
        }
      }
    }
  }
}

/* 黑夜模式 */
html.dark {
  .album-detail-page {
    --bg-primary: #0a0a0a;
    --bg-card: #141414;
    --text-main: #ffffff;
    --text-secondary: #a0a0a0;
    --text-muted: #666666;
    --border-color: #222222;
    --hover-bg: #1a1a1a;

    .album-hero {
      background: linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%);

      &::before {
        background: radial-gradient(circle, rgba(255,255,255,0.02) 0%, transparent 70%);
      }

      .hero-actions {
        background: rgba(255, 255, 255, 0.03);
        border-color: rgba(255, 255, 255, 0.08);
      }

      .show-status-switch {
        :deep(.el-switch__core) {
          background-color: #909399;
        }

        :deep(.is-checked .el-switch__core) {
          background-color: #409eff;
        }
      }
    }

    .album-content {
      .photo-grid {
        .photo-item {
          .photo {
            .placeholder,
            .error {
              background: #3a3a3a;

              .spinner {
                color: #787878;
              }

              .el-icon {
                color: #787878;
              }
            }
          }
        }
      }
    }
  }
}

/* 对话框样式 */
.upload-dialog,
.edit-dialog,
.cover-dialog {
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

  :deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid var(--border-color);
    display: flex;
    justify-content: flex-end;
    gap: 12px;
  }

  // 隐藏原生文件输入
  .file-input {
    position: absolute;
    width: 0;
    height: 0;
    opacity: 0;
    overflow: hidden;
    z-index: -1;
  }

  .upload-area {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
  }

  .btn-select-files {
    padding: 10px 20px;
    background: var(--el-color-primary);
    color: #fff;
    border: none;
    border-radius: 6px;
    font-size: 14px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      opacity: 0.9;
      transform: translateY(-2px);
    }
  }

  .btn-cancel,
  .btn-confirm {
    padding: 8px 16px;
    border-radius: 6px;
    font-size: 13px;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.2s ease;
  }

  .btn-cancel {
    background: var(--hover-bg);
    color: var(--text-main);
    border: 1px solid var(--border-color);
    margin-right: 10px;

    &:hover {
      background: var(--border-color);
    }
  }

  .btn-confirm {
    background: var(--el-color-primary);
    color: #fff;
    border: none;

    &:hover {
      opacity: 0.9;
    }
  }

  .upload-tip {
    margin-top: 8px;
    font-size: 13px;
    color: var(--text-muted);
  }

  .preview-container {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 16px;
    max-height: 300px;
    overflow-y: auto;

    .preview-item {
      width: 100px;
      height: 100px;
      border-radius: 8px;
      overflow: hidden;
      position: relative;

      .preview-image {
        width: 100%;
        height: 100%;
      }

      .btn-remove {
        position: absolute;
        top: 4px;
        right: 4px;
        width: 20px;
        height: 20px;
        border-radius: 50%;
        background: rgba(0, 0, 0, 0.5);
        color: #fff;
        border: none;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        transition: background 0.3s;

        &:hover {
          background: rgba(239, 68, 68, 0.8);
        }
      }

      .error {
        width: 100%;
        height: 100%;
        background: var(--hover-bg);
        display: flex;
        justify-content: center;
        align-items: center;

        .el-icon {
          font-size: 32px;
          color: var(--text-muted);
        }
      }
    }
  }

  .cover-container {
    position: relative;
    width: 120px;
    height: 120px;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;

    .cover-image {
      width: 100%;
      height: 100%;
    }

    .cover-overlay {
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      justify-content: center;
      align-items: center;
      color: #fff;
      font-size: 14px;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover .cover-overlay {
      opacity: 1;
    }
  }

  .cover-selection-container {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    max-height: 400px;
    overflow-y: auto;

    .cover-image-container {
      width: 100px;
      height: 100px;
      border-radius: 8px;
      overflow: hidden;
      cursor: pointer;
      transition: all 0.2s ease;

      &.selected {
        box-shadow: 0 0 0 3px #409eff;
      }

      .cover-select-image {
        width: 100%;
        height: 100%;
      }
    }
  }

  .danger-item {
    color: var(--danger-color);
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

/* 响应式 */
@media (max-width: 768px) {
  .album-hero {
    padding: 20px 16px !important;

    .hero-top {
      flex-direction: column;
      align-items: flex-start;

      .hero-title-wrapper {
        flex-direction: column;
        align-items: flex-start;
      }
    }

    .hero-actions {
      flex-wrap: wrap;
      justify-content: flex-start;
    }

    .select-toolbar {
      flex-wrap: wrap;
    }
  }

  .album-content {
    padding: 24px 16px !important;

    .photo-grid {
      grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
      gap: 12px;
    }
  }
}
</style>
