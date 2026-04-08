<template>
  <ManagementCard
    title="公告管理"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchAnnouncementList"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <el-select v-model="filterStatus" placeholder="状态筛选" filterable clearable size="small" style="width: 140px" @change="handleSearch">
        <el-option label="待发送" :value="0" />
        <el-option label="已发布" :value="2" />
        <el-option label="已取消" :value="1" />
      </el-select>
      <KeywordSearch v-model="searchKeyword" showLabel label="关键词" placeholder="搜索公告标题" @search="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        :data="announcementList"
        :loading="loading"
        showSelection
        showId
        showTitle
        showStatus
        showCreateTime
        :statusTypeMap="{ 0: 'warning', 1: 'info', 2: 'success' }"
        :statusTextMap="{ 0: '待发送', 1: '已取消', 2: '已发布' }"
        :hasEditAction="true"
        :hasDeleteAction="true"
        @selectionChange="handleSelectionChange"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <!-- 内容列 -->
        <el-table-column prop="content" label="内容" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.content" placement="top-start">
              <div class="announcement-content">{{ row.content }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 操作列扩展：增加取消按钮 -->
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 0" type="warning" link size="small" @click="handleCancel(row)">取消</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="announcementList"
        :selectedItems="selectedAnnouncements"
        showSelection
        showTitle
        showMeta
        :hasEditAction="true"
        :hasDeleteAction="true"
        @select="handleMobileSelect"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-content">{{ item.content }}</div>
          <el-tag :type="getStatusType(item.status)" size="small" class="mobile-status">{{ getStatusText(item.status) }}</el-tag>
          <el-button v-if="item.status === 0" type="warning" link size="small" @click.stop="handleCancel(item)">取消</el-button>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 新增/编辑公告对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" class="announcement-dialog" destroy-on-close>
    <el-form ref="announcementFormRef" :model="announcementForm" :rules="rules" label-width="80px">
      <el-form-item prop="title" label="标题">
        <el-input v-model="announcementForm.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
      </el-form-item>
      <el-form-item prop="content" label="内容">
        <el-input v-model="announcementForm.content" type="textarea" :rows="5" placeholder="请输入公告内容" maxlength="2000" show-word-limit />
      </el-form-item>
      <el-form-item prop="status" label="状态">
        <el-radio-group v-model="announcementForm.status">
          <el-radio :label="0">待发送</el-radio>
          <el-radio :label="2">立即发布</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确认</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getAnnouncementPage, createAnnouncement, updateAnnouncement, cancelAnnouncement, deleteAnnouncement } from '@/api/announcement'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 公告列表数据
const announcementList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件
const searchKeyword = ref('')
const filterStatus = ref('')
const searchTimeStart = ref('')
const searchTimeEnd = ref('')

// 选中的公告
const selectedAnnouncements = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('发布公告')
const submitLoading = ref(false)
const announcementFormRef = ref(null)
const isEdit = ref(false)

// 公告表单
const announcementForm = reactive({
  id: null,
  title: '',
  content: '',
  status: 2,
})

// 表单验证规则
const rules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
}

// 搜索条件是否为空
const hasSearchConditions = computed(() => !!(searchKeyword.value || filterStatus.value !== '' || searchTimeStart.value || searchTimeEnd.value))

// 构建搜索参数
const buildSearchPayload = computed(() => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  keyword: searchKeyword.value || undefined,
  status: filterStatus.value !== '' ? filterStatus.value : undefined,
  createTimeStart: searchTimeStart.value || undefined,
  createTimeEnd: searchTimeEnd.value || undefined,
}))

// 获取公告列表
const fetchAnnouncementList = async () => {
  loading.value = true
  try {
    const res = await getAnnouncementPage(buildSearchPayload.value)
    announcementList.value = res.data.data || []
    total.value = res.data.total || 0
    selectedAnnouncements.value = []
  } catch {
    ElMessage.error(hasSearchConditions.value ? '搜索公告失败' : '获取公告列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = async () => {
  currentPage.value = 1
  await fetchAnnouncementList()
}

// 重置处理
const handleReset = () => {
  searchKeyword.value = ''
  filterStatus.value = ''
  searchTimeStart.value = ''
  searchTimeEnd.value = ''
  handleSearch()
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchTimeStart.value = startTime
  searchTimeEnd.value = endTime
  handleSearch()
}

// 表格多选
const handleSelectionChange = (announcements) => {
  selectedAnnouncements.value = announcements
}

// 移动端选择处理
const handleMobileSelect = (announcement) => {
  const index = selectedAnnouncements.value.findIndex((item) => item.id === announcement.id)
  if (index > -1) {
    selectedAnnouncements.value.splice(index, 1)
  } else {
    selectedAnnouncements.value.push(announcement)
  }
}

// 获取状态标签类型
const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'info', 2: 'success' }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status) => {
  const texts = { 0: '待发送', 1: '已取消', 2: '已发布' }
  return texts[status] || '未知'
}

// 新增公告
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '发布公告'
  announcementForm.id = null
  announcementForm.title = ''
  announcementForm.content = ''
  announcementForm.status = 2
  dialogVisible.value = true
}

// 编辑公告
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑公告'
  announcementForm.id = row.id
  announcementForm.title = row.title
  announcementForm.content = row.content
  announcementForm.status = row.status === 1 ? 0 : row.status
  dialogVisible.value = true
}

// 取消公告
const handleCancel = (row) => {
  ElMessageBox.confirm(`确定要取消公告【${row.title}】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await cancelAnnouncement(row.id)
        ElMessage.success('取消成功')
        await fetchAnnouncementList()
      } catch {
        ElMessage.error('取消失败')
      }
    })
    .catch(() => {
      ElMessage.info('操作已取消')
    })
}

// 删除公告
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除公告【${row.title}】吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteAnnouncement(row.id)
        ElMessage.success('删除成功')
        await fetchAnnouncementList()
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 提交表单
const handleSubmit = async () => {
  try {
    await announcementFormRef.value.validate()
    submitLoading.value = true

    const data = {
      title: announcementForm.title,
      content: announcementForm.content,
      status: announcementForm.status,
    }

    if (isEdit.value) {
      data.id = announcementForm.id
      await updateAnnouncement(data)
    } else {
      await createAnnouncement(data)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '发布成功')
    dialogVisible.value = false
    await fetchAnnouncementList()
  } catch {
    ElMessage.error(isEdit.value ? '编辑失败' : '发布失败')
  } finally {
    submitLoading.value = false
  }
}

// 初始化
onMounted(() => {
  fetchAnnouncementList()
})
</script>

<style lang="scss" scoped>
// 公告内容样式
.announcement-content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: var(--text-regular);
  cursor: pointer;

  &:hover {
    color: var(--el-color-primary);
  }
}

// 移动端内容样式
.mobile-content {
  font-size: 13px;
  color: var(--text-muted);
  margin: 6px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.mobile-status {
  margin: 4px 0;
}

// 响应式
@media screen and (max-width: 768px) {
  .announcement-content {
    width: 100%;
  }
}
</style>
