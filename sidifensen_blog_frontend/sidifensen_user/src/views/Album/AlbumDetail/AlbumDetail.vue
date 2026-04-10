<template>
  <div class="album-detail-page">
    <div class="container">
      <!-- Hero Banner -->
      <div class="page-hero">
        <button class="btn-back" @click="goBack">
          <el-icon><ArrowLeftBold /></el-icon>
          <span>返回</span>
        </button>
        <div class="hero-copy">
          <div class="hero-kicker">Album Detail</div>
          <h1>{{ albumForm.name }}</h1>
          <p>创建者：@{{ albumForm.userName }}</p>
        </div>
      </div>

      <!-- 操作按钮区 -->
      <div class="hero-actions" v-if="isAlbumOwner">
        <button class="btn-upload" @click="handleUploadPhoto">
          <el-icon><UploadFilled /></el-icon>
          <span>上传图片</span>
        </button>
        <button class="btn-edit" @click="handleEditAlbum">
          <el-icon><Edit /></el-icon>
          <span>编辑相册</span>
        </button>
        <button v-if="!isSelectMode" class="btn-select" @click="toggleSelectMode">选择图片</button>
        <template v-else>
          <button class="btn-select" @click="toggleAllSelection">
            {{ isAllSelected ? '取消全选' : '全选' }}
          </button>
          <button class="btn-select" @click="toggleSelectMode">完成</button>
        </template>
        <button v-show="selectedPhotos.length > 0" class="btn-delete" @click="handleBatchDelete">
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
                <div
                  v-if="photo.examineStatus !== 1"
                  class="examine-status"
                  :class="getExamineStatusClass(photo.examineStatus)"
                >
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

        <el-empty
          v-if="Object.keys(groupedPhotos).length === 0 && !loading"
          description="暂无图片"
          :image-size="160"
        />
      </div>

      <!-- 上传图片对话框 -->
      <el-dialog
        v-model="uploadDialogVisible"
        title="上传图片"
        class="upload-dialog"
        :close-on-click-modal="false"
        @close="handleUploadDialogClose"
      >
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
            <el-image :src="file.url" class="preview-image" fit="cover" />
            <button class="btn-remove" @click="removeFile(index)">
              <el-icon><Close /></el-icon>
            </button>
          </div>
        </div>
        <template #footer>
          <button class="btn-cancel" @click="uploadDialogVisible = false">取消</button>
          <button class="btn-confirm" @click="submitUpload" :loading="uploadLoading">
            开始上传
          </button>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/userStore'
import { storeToRefs } from 'pinia'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeftBold,
  User,
  UploadFilled,
  Edit,
  Delete,
  MoreFilled,
  Calendar,
  Back,
  Right,
  ZoomOut,
  ZoomIn,
  Download,
  Picture,
  Loading,
  Check,
  Close,
} from '@element-plus/icons-vue'
import { getAlbum, updateAlbum, deleteAlbum, changeShowStatus, changeCover } from '@/api/album'
import { uploadAlbumPhoto, batchDeletePhoto } from '@/api/photo'
import { compressImage } from '@/utils/PhotoUtils'
import LoadingAnimation from '@/components/Loading/LoadingAnimation.vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const { user } = storeToRefs(userStore)

const loading = ref(false)
const albumId = Number(route.params.albumId)
const albumForm = ref({
  id: albumId,
  name: '',
  coverUrl: '',
  showStatus: 0,
  createTime: '',
  userId: '',
  userName: '',
})

const photoList = ref([])

const getPhotoList = async () => {
  loading.value = true
  try {
    const res = await getAlbum(albumForm.value.id)
    photoList.value = res.data.photos || []
    albumForm.value.showStatus = res.data.showStatus || 0
    albumForm.value.name = res.data.name || ''
    albumForm.value.coverUrl = res.data.coverUrl || ''
    albumForm.value.userName = res.data.userName || ''
    albumForm.value.userId = res.data.userId || ''
    groupPhotosByDate()
  } catch (error) {
    ElMessage.error('获取相册图片失败')
  } finally {
    loading.value = false
  }
}

const groupPhotosByDate = () => {
  const groups = {}
  const sortedPhotos = [...photoList.value].sort(
    (a, b) => new Date(b.createTime) - new Date(a.createTime),
  )
  sortedPhotos.forEach((photo) => {
    const date = photo.createTime.split(' ')[0]
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
    ElMessage.warning('请先选择要删除的图片')
    return
  }
  ElMessageBox.confirm(
    `确定要删除这 ${selectedPhotos.value.length} 张图片吗？此操作不可恢复`,
    '删除图片',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    },
  )
    .then(async () => {
      try {
        await batchDeletePhoto(selectedPhotos.value)
        ElMessage.success('删除成功')
        selectedPhotos.value = []
        getPhotoList()
      } catch (error) {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
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
    url: '',
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
  event.target.value = ''
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
    ElMessage.warning('请选择要上传的图片')
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
    ElMessage.success('图片上传成功')
    uploadDialogVisible.value = false
    setTimeout(() => {
      getPhotoList()
    }, 2000)
  } catch (error) {
    ElMessage.error('上传图片失败')
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
    ElMessage.warning('请输入相册名称')
    return
  }
  try {
    await updateAlbum(albumForm.value)
    ElMessage.success('编辑相册成功')
    editDialogVisible.value = false
    getPhotoList()
  } catch (error) {
    ElMessage.error('编辑相册失败')
  }
}

// 删除相册
const handleDeleteAlbum = async () => {
  ElMessageBox.confirm('确定要删除这个相册吗？此操作不可恢复', '删除相册', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteAlbum(albumForm.value.id)
        ElMessage.success('删除相册成功')
        router.push('/album')
      } catch (error) {
        ElMessage.error('删除相册失败')
      }
    })
    .catch(() => {
      ElMessage.info('已取消删除')
    })
}

// 修改展示状态
const handleChangeAlbumShowStatus = async () => {
  try {
    const newStatus = albumForm.value.showStatus === 0 ? 1 : 0
    await changeShowStatus({ id: albumForm.value.id, showStatus: newStatus })
    albumForm.value.showStatus = newStatus
    ElMessage.success('修改相册状态成功')
  } catch (error) {
    ElMessage.error('修改相册状态失败')
  }
}

// 更换封面
const coverDialogVisible = ref(false)
const selectedCoverUrl = ref('')
const coverDialogPhotos = ref([])

const handleChangeCover = async () => {
  try {
    const res = await getAlbum(albumId)
    const photos = res.data.photos
    if (!photos || photos.length === 0) {
      ElMessage.warning('该相册暂无图片，请先上传图片')
      return
    }
    coverDialogPhotos.value = [...photos].reverse()
    selectedCoverUrl.value = ''
    coverDialogVisible.value = true
  } catch (error) {
    ElMessage.error('更换封面失败')
  }
}

const confirmCoverChange = async () => {
  if (!selectedCoverUrl.value) {
    ElMessage.warning('请选择一张图片作为封面')
    return
  }
  try {
    await changeCover({ id: albumId, coverUrl: selectedCoverUrl.value })
    ElMessage.success('更换封面成功')
    albumForm.value.coverUrl = selectedCoverUrl.value
    coverDialogVisible.value = false
    getPhotoList()
  } catch (error) {
    ElMessage.error('更换封面失败')
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
  const texts = { 0: '待审核', 1: '', 2: '审核未通过' }
  return texts[status] || '未知状态'
}

const getExamineStatusClass = (status) => {
  const classes = { 0: 'status-pending', 1: '', 2: 'status-rejected' }
  return classes[status] || 'status-unknown'
}

const download = (number) => {
  const allUrls = getAllPhotoUrls()
  const url = allUrls[number]
  const suffix = url.slice(url.lastIndexOf('.'))
  const filename = Date.now() + suffix
  fetch(url)
    .then((response) => response.blob())
    .then((blob) => {
      const blobUrl = URL.createObjectURL(new Blob([blob]))
      const link = document.createElement('a')
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
// 相册详情页 - 现代极简主义科技风
// 完全匹配参考文件：Article/index.vue 和 AlbumSquare.vue

.album-detail-page {
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
  --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.05);
  --shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.1);

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
    --shadow-sm: 0 1px 2px rgba(0, 0, 0, 0.3);
    --shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
    --shadow-md: 0 4px 6px rgba(0, 0, 0, 0.4);
  }

  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 100vh;
  background: var(--bg-page) !important;
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
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
    padding: 60px 0 30px;
    border-bottom: 1px solid var(--bg-hero-divider);

    @media (max-width: 768px) {
      padding: 40px 0 20px;
    }

    .btn-back {
      display: flex;
      align-items: center;
      gap: 6px;
      padding: 8px 16px;
      background: transparent;
      border: 1px solid var(--border-regular);
      border-radius: 6px;
      color: var(--text-secondary);
      font-size: 14px;
      cursor: pointer;
      transition: all 0.2s ease;
      margin-bottom: 16px;

      &:hover {
        border-color: var(--accent-color);
        color: var(--accent-color);
      }

      .el-icon {
        font-size: 16px;
      }
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
  }

  /* 操作按钮区 */
  .hero-actions {
    display: flex;
    justify-content: center;
    flex-wrap: wrap;
    gap: 12px;
    padding: 10px 0;
    margin-top: 0;

    .btn-upload,
    .btn-edit,
    .btn-select {
      padding: 8px 16px;
      background: transparent;
      border: 1px solid var(--border-regular);
      border-radius: 6px;
      color: var(--text-primary);
      font-size: 13px;
      font-weight: 500;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        border-color: var(--accent-color);
        color: var(--accent-color);
      }
    }

    .btn-upload {
      background: var(--accent-soft);
      border-color: var(--accent-border);
      color: var(--accent-color);

      &:hover {
        background: var(--accent-hover);
      }
    }

    .btn-delete {
      padding: 8px 16px;
      background: transparent;
      border: 1px solid rgba(239, 68, 68, 0.3);
      border-radius: 6px;
      color: #ef4444;
      font-size: 13px;
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        background: rgba(239, 68, 68, 0.1);
        border-color: #ef4444;
      }
    }

    html.dark & {
      .btn-delete {
        border-color: rgba(239, 68, 68, 0.5);
        &:hover {
          background: rgba(239, 68, 68, 0.15);
        }
      }
    }

    .btn-more {
      padding: 8px;
      background: transparent;
      border: 1px solid var(--border-regular);
      border-radius: 6px;
      color: var(--text-primary);
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover {
        border-color: var(--accent-color);
        color: var(--accent-color);
      }
    }

    .show-status-switch {
      &.is-checked {
        ::v-deep(.el-switch__core) {
          background-color: var(--accent-color) !important;
        }
      }

      ::v-deep(.el-switch__core) {
        width: 58px !important;
        height: 26px;
        border-radius: 13px;
        background-color: var(--border-regular);
        border: none;
        overflow: hidden;
      }

      ::v-deep(.el-switch__action) {
        width: 18px;
        height: 18px;
        background-color: #fff;
        border-radius: 50%;
        box-shadow: var(--shadow-sm);
        box-sizing: border-box;
      }

      ::v-deep(.el-switch__label) {
        font-size: 11px;
        font-weight: 500;
        line-height: 26px;
      }
    }
  }

  /* 内容区 */
  .album-content {
    padding: 12px 0;

    .date-section {
      margin-bottom: 24px;

      .date-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 20px;
        padding: 12px 16px;
        background: var(--bg-card);
        border-radius: 6px;
        border: 1px solid var(--border-regular);

        .date-label {
          display: flex;
          align-items: center;
          gap: 8px;
          font-size: 16px;
          font-weight: 600;
          color: var(--text-primary);

          .el-icon {
            color: var(--text-muted);
          }
        }
      }

      .photo-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
        gap: 16px;

        @media (max-width: 1200px) {
          grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
        }

        @media (max-width: 768px) {
          grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
          gap: 12px;
        }

        .photo-item {
          position: relative;
          aspect-ratio: 4/3;
          border-radius: 6px;
          overflow: hidden;
          background: var(--bg-hover);
          cursor: pointer;
          transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
          border: 1px solid var(--border-regular);

          &:hover {
            border-color: var(--border-hover);
            transform: translateY(-2px);

            .photo {
              transform: scale(1.05);
            }
          }

          &.selected {
            border-color: var(--accent-color);
            box-shadow: 0 0 0 2px var(--accent-soft);
          }

          .photo {
            width: 100%;
            height: 100%;
            transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);

            ::v-deep(.el-image) {
              width: 100%;
              height: 100%;
            }

            .placeholder,
            .error {
              width: 100%;
              height: 100%;
              background: var(--bg-page);
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
            box-shadow: var(--shadow);

            &.status-pending {
              background: #64748b;
            }

            &.status-approved {
              background: #22c55e;
            }

            &.status-rejected {
              background: #ef4444;
            }
          }

          .check-mark {
            position: absolute;
            top: 8px;
            right: 8px;
            width: 28px;
            height: 28px;
            background: var(--accent-color);
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

/* 对话框样式 */
.upload-dialog,
.edit-dialog,
.cover-dialog {
  ::v-deep(.el-dialog__header) {
    padding: 20px 24px;
    border-bottom: 1px solid var(--border-regular);
  }

  ::v-deep(.el-dialog__title) {
    font-size: 18px;
    font-weight: 600;
    color: var(--text-primary);
  }

  ::v-deep(.el-dialog__body) {
    padding: 24px;
  }

  ::v-deep(.el-dialog__footer) {
    padding: 16px 24px;
    border-top: 1px solid var(--border-regular);
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
    background: var(--accent-color);
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
    background: var(--bg-hover);
    color: var(--text-primary);
    border: 1px solid var(--border-regular);
    margin-right: 10px;

    &:hover {
      background: var(--border-light);
    }
  }

  .btn-confirm {
    background: var(--accent-color);
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
        background: var(--bg-hover);
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
      border: 2px solid transparent;

      &.selected {
        border-color: var(--accent-color);
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
  .page-hero {
    padding: 24px 16px !important;

    .hero-content {
      padding: 0 16px;
    }

    .hero-copy {
      h1 {
        font-size: 24px;
      }
    }
  }

  .hero-actions {
    flex-wrap: wrap;
    justify-content: flex-start;
  }

  .album-content {
    padding: 16px 20px !important;

    .photo-grid {
      grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
      gap: 12px;
    }
  }
}
</style>
