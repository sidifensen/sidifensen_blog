<template>
  <ManagementCard
    title="相册管理"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchAlbums"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <el-input v-model="searchAlbumName" placeholder="搜索相册名称" :prefix-icon="Search" size="small" style="width: 140px" clearable @input="handleSearch" />
      <el-select v-model="searchStatus" placeholder="相册状态" filterable clearable size="small" style="width: 140px" @change="handleSearch">
        <el-option label="正常" value="0" />
        <el-option label="禁用" value="1" />
      </el-select>
      <UserSearchSelect v-model="searchUserId" @change="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions :selectedCount="selectedAlbums.length" :showBatchDelete="true" @batchDelete="handleBatchDelete" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        v-loading="loading"
        :data="paginatedAlbumList"
        :show-selection="true"
        :show-cover="true"
        :show-id="true"
        :show-actions="true"
        :has-view-action="false"
        :has-edit-action="true"
        :has-delete-action="true"
        :actions-width="180"
        @selection-change="handleSelectionChange"
        @edit="handleEditAlbum"
        @delete="handleDeleteAlbum"
      >
        <!-- 相册名称列 -->
        <el-table-column prop="name" label="相册名称" min-width="150">
          <template #default="{ row }">
            <el-tooltip :content="row.name" placement="top-start">
              <div class="album-name">{{ row.name }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <!-- 用户名列 -->
        <el-table-column prop="userName" label="用户名" min-width="100" />
        <!-- 状态列 -->
        <el-table-column prop="showStatus" label="状态" min-width="120">
          <template #default="{ row }">
            <el-switch
              v-model="row.showStatus"
              size="small"
              active-color="var(--el-color-primary)"
              inactive-color="#cccccc"
              active-text="正常"
              inactive-text="禁用"
              :active-value="0"
              :inactive-value="1"
              inline-prompt
              :loading="switchLoading"
              :before-change="() => handleStatusChange(row.id, row.showStatus === 0 ? 1 : 0)"
            />
          </template>
        </el-table-column>
        <!-- 创建时间列 -->
        <el-table-column prop="createTime" label="创建时间" sortable min-width="110" />
        <!-- 详情按钮 -->
        <el-table-column label="详情" width="80" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" text bg @click="handleAlbumDetail(row.id)">详情</el-button>
          </template>
        </el-table-column>
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedAlbumList"
        :selectedItems="selectedAlbums"
        showSelection
        showMeta
        :hasDetailAction="true"
        :hasEditAction="true"
        :hasDeleteAction="true"
        @select="handleMobileSelect"
        @detail="handleAlbumDetail"
        @edit="handleEditAlbum"
        @delete="handleDeleteAlbum"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-meta">
            <span class="meta-item">
              <span class="label">用户:</span>
              <span class="value">{{ item.userName }}</span>
            </span>
            <el-switch
              v-model="item.showStatus"
              size="small"
              active-color="var(--el-color-primary)"
              inactive-color="#cccccc"
              :active-value="0"
              :inactive-value="1"
              inline-prompt
              :loading="switchLoading"
              :before-change="() => handleStatusChange(item.id, item.showStatus === 0 ? 1 : 0)"
            />
          </div>
          <div class="mobile-time">创建: {{ item.createTime }}</div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 编辑相册对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" class="album-dialog" destroy-on-close>
    <el-form ref="albumFormRef" :model="albumForm" :rules="rules" label-width="80px">
      <el-form-item prop="name" label="相册名称">
        <el-input v-model="albumForm.name" placeholder="请输入相册名称" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleDialogClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 相册详情对话框 -->
  <el-dialog v-model="albumDetailDialogVisible" title="相册详情" class="album-detail-dialog" width="80%" destroy-on-close>
    <div v-if="albumDetail" class="album-detail-content">
      <div class="album-detail-header">
        <el-image :src="albumDetail.coverUrl" fit="cover" class="detail-cover" />
        <div class="detail-info">
          <h3 class="detail-title">{{ albumDetail.name }}</h3>
          <div class="detail-meta">
            <div class="meta-item"><span class="label">相册ID：</span>{{ albumDetail.id }}</div>
            <div class="meta-item"><span class="label">创建者：</span>{{ albumDetail.userName }}</div>
            <div class="meta-item"><span class="label">创建时间：</span>{{ albumDetail.createTime }}</div>
            <div class="meta-item"><span class="label">更新时间：</span>{{ albumDetail.updateTime }}</div>
            <div class="meta-item">
              <span class="label">状态：</span>
              <span :class="albumDetail.showStatus === 0 ? 'status-normal' : 'status-disabled'">{{ albumDetail.showStatus === 0 ? '正常' : '禁用' }}</span>
            </div>
            <div class="meta-item"><span class="label">图片数量：</span>{{ albumDetail.photos?.length || 0 }}</div>
          </div>
        </div>
      </div>

      <!-- 操作按钮栏 -->
      <div v-if="albumDetail.photos && albumDetail.photos.length > 0" class="action-bar">
        <div class="action-left">
          <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
          <span class="selected-count">{{ selectedPhotos.length }} 已选择</span>
        </div>
        <div class="action-right">
          <el-button type="primary" plain round @click="handleBatchAudit" :disabled="selectedPhotos.length === 0" :loading="batchAuditLoading">批量审核</el-button>
          <el-button type="warning" plain round @click="handleBatchReject" :disabled="selectedPhotos.length === 0" :loading="batchRejectLoading">批量拒绝</el-button>
          <el-button type="danger" plain round @click="handlePhotoBatchDelete" :disabled="selectedPhotos.length === 0" :loading="photoBatchDeleteLoading">批量删除</el-button>
        </div>
      </div>

      <!-- 相册图片网格 -->
      <div v-if="albumDetail.photos && albumDetail.photos.length > 0" class="photos-grid">
        <div v-for="photo in albumDetail.photos" :key="photo.id" class="photo-item" :class="{ 'photo-item-selected': isPhotoSelected(photo.id) }">
          <div class="photo-selector">
            <el-checkbox v-model="selectedPhotos" :value="photo.id" @change="handlePhotoSelect(photo.id, $event)" />
          </div>
          <el-image
            preview-teleported
            :src="photo.url"
            :preview-src-list="albumDetail.photos.map((p) => p.url)"
            fit="cover"
            class="photo-image"
            :class="{ 'photo-image-unaudited': photo.examineStatus === 0 }"
            lazy
          />
          <div class="photo-info">
            <div class="photo-id">ID: {{ photo.id }}</div>
            <div class="photo-status" :class="photo.examineStatus === 0 ? 'status-unaudited' : photo.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
              {{ photo.examineStatus === 0 ? '未审核' : photo.examineStatus === 1 ? '已审核' : '未通过' }}
            </div>
            <div class="photo-time">{{ photo.createTime }}</div>
            <div class="photo-actions">
              <el-button text bg type="primary" size="small" @click="handleAuditPhoto(photo.id)" v-if="photo.examineStatus === 0">审核</el-button>
              <el-button text bg type="warning" size="small" @click="handleRejectPhoto(photo.id)" v-if="photo.examineStatus === 0">拒绝</el-button>
              <el-button text bg type="danger" size="small" @click="handleDeletePhoto(photo.id)">删除</el-button>
            </div>
          </div>
        </div>
      </div>
      <div v-else class="no-photos">该相册暂无图片</div>
    </div>
    <div v-else class="loading-container">
      <el-loading v-loading="true" />
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { Search, Plus, InfoFilled, Edit, Delete, Avatar } from '@element-plus/icons-vue'
import { adminList, adminUpdateAlbum, adminDeleteAlbum, adminSearchAlbum, adminGetAlbumDetail } from '@/api/album'
import { adminDeletePhoto, adminDeleteBatchPhoto, adminAuditPhoto, adminAuditBatchPhoto } from '@/api/photo'
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import UserSearchSelect from '@/components/search/UserSearchSelect.vue'

// 相册列表数据
const albumList = ref([])
const paginatedAlbumList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('编辑相册')
const albumFormRef = ref(null)
const albumForm = reactive({
  id: null,
  name: '',
  coverUrl: '',
  showStatus: 0,
})

// 表单验证规则
const rules = {
  name: [{ required: true, message: '请输入相册名称', trigger: 'blur' }],
}

// 搜索条件
const searchAlbumName = ref('')
const searchUserId = ref('')
const searchStatus = ref('')
const searchCreateTimeStart = ref(null)
const searchCreateTimeEnd = ref(null)

// 选中的相册
const selectedAlbums = ref([])

// 批量操作加载状态
const batchDeleteLoading = ref(false)
const switchLoading = ref(false)

// 详情对话框
const albumDetailDialogVisible = ref(false)
const albumDetail = ref(null)
const selectedPhotos = ref([])
const selectAll = computed({
  get() {
    return albumDetail.value?.photos?.length > 0 && selectedPhotos.value.length === albumDetail.value.photos.length
  },
  set(value) {
    if (value) {
      selectedPhotos.value = albumDetail.value.photos.map((photo) => photo.id)
    } else {
      selectedPhotos.value = []
    }
  },
})
const batchAuditLoading = ref(false)
const batchRejectLoading = ref(false)
const photoBatchDeleteLoading = ref(false)

// 判断是否有搜索条件
const hasSearchConditions = () => !!(searchAlbumName.value || searchUserId.value || searchStatus.value || searchCreateTimeStart.value || searchCreateTimeEnd.value)

// 构建搜索参数
const buildSearchPayload = () => {
  const searchData = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  }
  if (searchAlbumName.value) {
    searchData.name = searchAlbumName.value
  }
  if (searchUserId.value) {
    searchData.userId = searchUserId.value
  }
  if (searchStatus.value) {
    searchData.showStatus = searchStatus.value
  }
  if (searchCreateTimeStart.value) {
    searchData.createTimeStart = searchCreateTimeStart.value
  }
  if (searchCreateTimeEnd.value) {
    searchData.createTimeEnd = searchCreateTimeEnd.value
  }
  return searchData
}

// 应用分页数据
const applyPageData = (pageData) => {
  albumList.value = pageData?.data || []
  paginatedAlbumList.value = albumList.value
  total.value = Number(pageData?.total || 0)
  selectedAlbums.value = []
}

// 获取相册列表
const fetchAlbums = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await adminSearchAlbum(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await adminList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索相册失败' : '获取相册列表失败')
  } finally {
    loading.value = false
  }
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchCreateTimeStart.value = startTime
  searchCreateTimeEnd.value = endTime
}

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1
  fetchAlbums()
}

// 重置处理
const handleReset = () => {
  searchAlbumName.value = ''
  searchUserId.value = ''
  searchStatus.value = ''
  searchCreateTimeStart.value = null
  searchCreateTimeEnd.value = null
  handleSearch()
}

// 表格多选
const handleSelectionChange = (albums) => {
  selectedAlbums.value = albums
}

// 移动端选择处理
const handleMobileSelect = (album) => {
  const index = selectedAlbums.value.findIndex((item) => item.id === album.id)
  if (index > -1) {
    selectedAlbums.value.splice(index, 1)
  } else {
    selectedAlbums.value.push(album)
  }
}

// 处理状态变更
const handleStatusChange = async (id, status) => {
  try {
    await adminUpdateAlbum({ id, showStatus: status })
    ElMessage.success('状态更新成功')
    const album = albumList.value.find((item) => item.id === id)
    if (album) {
      album.showStatus = status
    }
  } catch (error) {
    ElMessage.error('状态更新失败')
  }
}

// 编辑相册
const handleEditAlbum = (row) => {
  dialogTitle.value = '编辑相册'
  albumForm.id = row.id
  albumForm.name = row.name
  albumForm.coverUrl = row.coverUrl
  albumForm.showStatus = row.showStatus
  dialogVisible.value = true
}

// 删除相册
const handleDeleteAlbum = (id) => {
  ElMessageBox.confirm('确定要删除该相册吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeleteAlbum(id)
        ElMessage.success('删除成功')
        await refreshAlbumList()
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedAlbums.value.length} 个相册吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const albumIds = selectedAlbums.value.map((album) => album.id)
        await adminDeleteAlbum(albumIds)
        ElMessage.success('批量删除成功')
        await refreshAlbumList()
      } catch {
        ElMessage.error('批量删除失败')
      } finally {
        batchDeleteLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 提交表单
const handleSubmit = () => {
  albumFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      await adminUpdateAlbum({
        id: albumForm.id,
        name: albumForm.name,
      })
      ElMessage.success('编辑相册成功')
      dialogVisible.value = false
      await refreshAlbumList()
    } catch (error) {
      ElMessage.error('编辑相册失败')
    }
  })
}

// 关闭对话框
const handleDialogClose = () => {
  albumFormRef.value?.resetFields()
  dialogVisible.value = false
}

// 智能刷新列表
const refreshAlbumList = async (deletedCount = 0) => {
  if (deletedCount > 0 && currentPage.value > 1 && albumList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchAlbums()
}

// 相册详情
const handleAlbumDetail = async (id) => {
  loading.value = true
  try {
    const res = await adminGetAlbumDetail(id)
    albumDetail.value = res.data
    albumDetailDialogVisible.value = true
    selectedPhotos.value = []
  } catch (error) {
    ElMessage.error('获取相册详情失败')
  } finally {
    loading.value = false
  }
}

// 检查图片是否被选中
const isPhotoSelected = (photoId) => {
  return selectedPhotos.value.includes(photoId)
}

// 处理图片选择
const handlePhotoSelect = (photoId, selected) => {
  if (selected) {
    if (!selectedPhotos.value.includes(photoId)) {
      selectedPhotos.value.push(photoId)
    }
  } else {
    selectedPhotos.value = selectedPhotos.value.filter((id) => id !== photoId)
  }
}

// 处理全选
const handleSelectAll = (value) => {
  selectAll.value = value
}

// 审核单张图片
const handleAuditPhoto = (photoId) => {
  ElMessageBox.confirm('确定要审核通过该图片吗？', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      try {
        await adminAuditPhoto({ photoId: photoId, examineStatus: 1 })
        ElMessage.success('审核成功')
        if (albumDetail.value) {
          const photo = albumDetail.value.photos.find((p) => p.id === photoId)
          if (photo) {
            photo.examineStatus = 1
          }
        }
      } catch {
        ElMessage.error('审核失败')
      }
    })
    .catch(() => {
      ElMessage.info('审核已取消')
    })
}

// 批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedPhotos.value.length} 张图片吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      batchAuditLoading.value = true
      try {
        const data = selectedPhotos.value.map((id) => ({
          photoId: id,
          examineStatus: 1,
        }))
        await adminAuditBatchPhoto(data)
        ElMessage.success('批量审核成功')
        if (albumDetail.value) {
          albumDetail.value.photos.forEach((photo) => {
            if (selectedPhotos.value.includes(photo.id)) {
              photo.examineStatus = 1
            }
          })
          selectedPhotos.value = []
        }
      } catch {
        ElMessage.error('批量审核失败')
      } finally {
        batchAuditLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('审核已取消')
    })
}

// 拒绝单张图片
const handleRejectPhoto = (photoId) => {
  ElMessageBox.confirm('确定要拒绝该图片吗？', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminAuditPhoto({ photoId: photoId, examineStatus: 2 })
        ElMessage.success('拒绝成功')
        handleAlbumDetail(albumDetail.value.id)
      } catch {
        ElMessage.error('拒绝失败')
      }
    })
    .catch(() => {
      ElMessage.info('拒绝已取消')
    })
}

// 批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedPhotos.value.length} 张图片吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchRejectLoading.value = true
      try {
        const data = selectedPhotos.value.map((id) => ({
          photoId: id,
          examineStatus: 2,
        }))
        await adminAuditBatchPhoto(data)
        ElMessage.success('批量拒绝成功')
        handleAlbumDetail(albumDetail.value.id)
      } catch {
        ElMessage.error('批量拒绝失败')
      } finally {
        batchRejectLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('拒绝已取消')
    })
}

// 删除单张图片
const handleDeletePhoto = (photoId) => {
  ElMessageBox.confirm('确定要删除该图片吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeletePhoto(photoId)
        ElMessage.success('删除成功')
        handleAlbumDetail(albumDetail.value.id)
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 批量删除图片
const handlePhotoBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedPhotos.value.length} 张图片吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      photoBatchDeleteLoading.value = true
      try {
        await adminDeleteBatchPhoto(selectedPhotos.value)
        ElMessage.success('批量删除成功')
        handleAlbumDetail(albumDetail.value.id)
      } catch {
        ElMessage.error('批量删除失败')
      } finally {
        photoBatchDeleteLoading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 关闭详情对话框
const handleDetailDialogClose = () => {
  albumDetailDialogVisible.value = false
  albumDetail.value = null
  selectedPhotos.value = []
}

// 初始化
onMounted(() => {
  fetchAlbums()
})
</script>

<style lang="scss" scoped>
// 相册名称样式
.album-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    color: var(--el-color-primary);
  }
}

// 表格操作按钮组
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;

  .detail-button {
    border-radius: 6px;
    background-color: var(--el-color-info-light-9);
    color: var(--el-color-info);
    border-color: var(--el-color-info-light-9);
  }

  .edit-button {
    border-radius: 6px;
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    border-color: var(--el-color-primary-light-9);
  }

  .delete-button {
    border-radius: 6px;
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
    border-color: var(--el-color-danger-light-9);
  }
}

// 移动端元信息
.mobile-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: var(--text-muted);
  margin: 4px 0;
}

.mobile-time {
  font-size: 12px;
  color: var(--text-muted);
}

// 相册详情对话框
:deep(.album-detail-dialog) {
  .el-dialog__body {
    padding: 20px;
  }
}

.album-detail-content {
  .album-detail-header {
    display: flex;
    gap: 20px;
    margin-bottom: 20px;

    .detail-cover {
      width: 200px;
      height: 150px;
      border-radius: 8px;
      flex-shrink: 0;
    }

    .detail-info {
      flex: 1;

      .detail-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0 0 12px 0;
      }

      .detail-meta {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .meta-item {
          font-size: 14px;
          color: var(--text-regular);

          .label {
            color: var(--text-muted);
            margin-right: 8px;
          }

          .status-normal {
            color: var(--el-color-primary);
          }

          .status-disabled {
            color: var(--el-color-danger);
          }
        }
      }
    }
  }
}

// 操作按钮栏
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--el-border-color-lighter);

  .action-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .action-right {
    display: flex;
    gap: 8px;
  }

  .selected-count {
    color: var(--text-regular);
    font-size: 14px;
  }
}

// 图片网格
.photos-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;

  .photo-item {
    position: relative;
    border-radius: 8px;
    overflow: hidden;
    background: var(--el-bg-color-page);
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    &.photo-item-selected {
      box-shadow: 0 0 0 2px var(--el-color-primary);
    }

    .photo-selector {
      position: absolute;
      top: 8px;
      left: 8px;
      z-index: 10;
    }

    .photo-image {
      width: 100%;
      height: 140px;
      display: block;

      &.photo-image-unaudited {
        opacity: 0.7;
      }
    }

    .photo-info {
      padding: 8px;

      .photo-id {
        font-size: 12px;
        color: var(--text-muted);
        margin-bottom: 4px;
      }

      .photo-status {
        display: inline-block;
        padding: 2px 6px;
        border-radius: 8px;
        font-size: 11px;
        margin-bottom: 4px;

        &.status-unaudited {
          background-color: var(--el-color-danger-light-9);
          color: var(--el-color-danger);
        }

        &.status-audited {
          background-color: var(--el-color-success-light-9);
          color: var(--el-color-success);
        }

        &.status-rejected {
          background-color: var(--el-color-warning-light-9);
          color: var(--el-color-warning);
        }
      }

      .photo-time {
        font-size: 11px;
        color: var(--text-muted);
        margin-bottom: 4px;
      }

      .photo-actions {
        display: flex;
        gap: 4px;

        .el-button {
          font-size: 10px;
          padding: 2px 6px;
        }
      }
    }
  }
}

// 无图片状态
.no-photos {
  text-align: center;
  padding: 40px 0;
  color: var(--text-muted);
  font-size: 14px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

// 响应式 - 移动端一行一个
@media screen and (max-width: 768px) {
  .album-detail-content {
    .album-detail-header {
      flex-direction: column;

      .detail-cover {
        width: 100%;
        height: 150px;
      }
    }
  }

  .action-bar {
    flex-direction: column;
    gap: 12px;
  }

  .photos-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
}
</style>
