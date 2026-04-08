<template>
  <ManagementCard
    title="图片审核"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchPhotos"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <ExamineStatusSelect v-model="searchExamineStatus" width="140px" />
      <UserSearchSelect v-model="searchUserId" width="180px" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions
        :selectedCount="selectedPhotos.length"
        :showBatchAudit="true"
        :showBatchReject="true"
        :showBatchDelete="true"
        @batchAudit="handleBatchAudit"
        @batchReject="handleBatchReject"
        @batchDelete="handleBatchDelete"
      />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        :data="paginatedPhotoList"
        :loading="loading"
        showSelection
        :hasAuditAction="true"
        :hasRejectAction="true"
        :hasDeleteAction="true"
        actionsWidth="280"
        @selectionChange="handleSelectionChange"
        @audit="handleAuditPhoto"
        @reject="handleRejectPhoto"
        @delete="handleDeletePhoto"
      >
        <!-- 图片列 -->
        <el-table-column prop="url" label="图片" width="360">
          <template #default="{ row }">
            <el-image preview-teleported :src="row.url" style="width: 320px; height: 180px" :preview-src-list="[row.url]" fit="cover" />
          </template>
        </el-table-column>

        <!-- ID列 -->
        <el-table-column prop="id" label="ID" width="80" />

        <!-- 用户名列 -->
        <el-table-column prop="username" label="用户名" width="120" />

        <!-- 状态列 -->
        <el-table-column prop="examineStatus" label="状态" width="100">
          <template #default="{ row }">
            <div class="photo-status" :class="row.examineStatus === 0 ? 'status-unaudited' : row.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
              {{ row.examineStatus === 0 ? '待审核' : row.examineStatus === 1 ? '已审核' : '未通过' }}
            </div>
          </template>
        </el-table-column>

        <!-- 创建时间列 -->
        <el-table-column prop="createTime" label="创建时间" sortable width="160" />

        <!-- 更新时间列 -->
        <el-table-column prop="updateTime" label="更新时间" sortable width="160" />
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedPhotoList"
        :selectedItems="selectedPhotos"
        showSelection
        :hasAuditAction="true"
        :hasRejectAction="true"
        :hasDeleteAction="true"
        @select="handleMobileSelect"
        @audit="handleAuditPhoto"
        @reject="handleRejectPhoto"
        @delete="handleDeletePhoto"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <el-image preview-teleported :src="item.url" style="width: 100%; height: 150px" :preview-src-list="[item.url]" fit="cover" />
          <div class="mobile-info">
            <span class="photo-status" :class="item.examineStatus === 0 ? 'status-unaudited' : item.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
              {{ item.examineStatus === 0 ? '待审核' : item.examineStatus === 1 ? '已审核' : '未通过' }}
            </span>
            <span class="mobile-time">{{ item.createTime }}</span>
          </div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUserSearch } from '@/hooks/useUserSearch'
import { adminDeletePhoto, adminDeleteBatchPhoto, adminAuditPhoto, adminAuditBatchPhoto, adminSearchPhoto, adminGetPhotoList } from '@/api/photo'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import UserSearchSelect from '@/components/search/UserSearchSelect.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 用户搜索
const { filteredUserList, userLoading, searchUsers } = useUserSearch()

// 图片列表数据
const photoList = ref([])
const paginatedPhotoList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件
const searchExamineStatus = ref('')
const searchUserId = ref('')
const searchStartTime = ref('')
const searchEndTime = ref('')

// 选中的图片
const selectedPhotos = ref([])

// 批量操作加载状态
const batchAuditLoading = ref(false)
const batchRejectLoading = ref(false)
const batchDeleteLoading = ref(false)

// 构建搜索参数
const buildSearchPayload = () => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  userId: searchUserId.value || undefined,
  examineStatus: searchExamineStatus.value ? parseInt(searchExamineStatus.value, 10) : undefined,
  createTimeStart: searchStartTime.value || undefined,
  createTimeEnd: searchEndTime.value || undefined,
})

// 应用分页数据
const applyPageData = (pageData) => {
  photoList.value = pageData?.data || []
  paginatedPhotoList.value = photoList.value
  total.value = Number(pageData?.total || 0)
  selectedPhotos.value = []
}

// 获取图片列表
const fetchPhotos = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await adminSearchPhoto(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await adminGetPhotoList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch {
    ElMessage.error(hasSearchConditions() ? '搜索图片失败' : '获取图片列表失败')
  } finally {
    loading.value = false
  }
}

// 是否有搜索条件
const hasSearchConditions = () => !!(searchExamineStatus.value || searchUserId.value || searchStartTime.value || searchEndTime.value)

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchStartTime.value = startTime
  searchEndTime.value = endTime
}

// 搜索处理
const handleSearch = async () => {
  currentPage.value = 1
  await fetchPhotos()
}

// 重置处理
const handleReset = () => {
  searchExamineStatus.value = ''
  searchUserId.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  handleSearch()
}

// 表格多选
const handleSelectionChange = (photos) => {
  selectedPhotos.value = photos
}

// 移动端选择处理
const handleMobileSelect = (photo) => {
  const index = selectedPhotos.value.findIndex((item) => item.id === photo.id)
  if (index > -1) {
    selectedPhotos.value.splice(index, 1)
  } else {
    selectedPhotos.value.push(photo)
  }
}

// 处理单个图片审核
const handleAuditPhoto = (row) => {
  ElMessageBox.confirm('确定要审核通过该图片吗？', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      try {
        await adminAuditPhoto({ photoId: row.id, examineStatus: 1 })
        ElMessage.success('审核成功')
        await fetchPhotos()
      } catch {
        ElMessage.error('审核失败')
      }
    })
    .catch(() => {
      ElMessage.info('审核已取消')
    })
}

// 处理批量审核
const handleBatchAudit = () => {
  if (selectedPhotos.value.length === 0) return
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedPhotos.value.length} 张图片吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info',
  })
    .then(async () => {
      batchAuditLoading.value = true
      try {
        const data = selectedPhotos.value
          .filter((p) => p.examineStatus !== 1)
          .map((photo) => ({
            photoId: photo.id,
            examineStatus: 1,
          }))
        if (data.length === 0) {
          ElMessage.info('所选图片均已审核')
          return
        }
        data.sort((a, b) => a.photoId - b.photoId)
        await adminAuditBatchPhoto(data)
        ElMessage.success('批量审核成功')
        await fetchPhotos()
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

// 处理单个图片拒绝
const handleRejectPhoto = (row) => {
  ElMessageBox.confirm('确定要拒绝该图片吗？', '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminAuditPhoto({ photoId: row.id, examineStatus: 2 })
        ElMessage.success('拒绝成功')
        await fetchPhotos()
      } catch {
        ElMessage.error('拒绝失败')
      }
    })
    .catch(() => {
      ElMessage.info('拒绝已取消')
    })
}

// 处理批量拒绝
const handleBatchReject = () => {
  if (selectedPhotos.value.length === 0) return
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedPhotos.value.length} 张图片吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchRejectLoading.value = true
      try {
        const data = selectedPhotos.value
          .filter((p) => p.examineStatus !== 2)
          .map((photo) => ({
            photoId: photo.id,
            examineStatus: 2,
          }))
        if (data.length === 0) {
          ElMessage.info('所选图片均已拒绝')
          return
        }
        await adminAuditBatchPhoto(data)
        ElMessage.success('批量拒绝成功')
        await fetchPhotos()
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

// 处理删除单个图片
const handleDeletePhoto = (row) => {
  ElMessageBox.confirm('确定要删除该图片吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeletePhoto(row.id)
        ElMessage.success('删除成功')
        await fetchPhotos()
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedPhotos.value.length === 0) return
  ElMessageBox.confirm(`确定要删除选中的 ${selectedPhotos.value.length} 张图片吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const data = selectedPhotos.value.map((photo) => ({
          photoId: photo.id,
        }))
        await adminDeleteBatchPhoto(data)
        ElMessage.success('批量删除成功')
        await fetchPhotos()
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

// 初始化
onMounted(() => {
  fetchPhotos()
})
</script>

<style lang="scss" scoped>
// 图片状态样式
.photo-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

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

// 移动端信息
.mobile-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;

  .mobile-time {
    font-size: 12px;
    color: var(--text-muted);
  }
}
</style>
