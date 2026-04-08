<template>
  <ManagementCard
    title="友链审核"
    :show-time-filter="true"
    :show-pagination="true"
    :model-current-page="currentPage"
    :model-page-size="pageSize"
    :total="total"
    @search="fetchLinks"
    @time-change="handleTimeChange"
  >
    <!-- 筛选条件 -->
    <template #filters>
      <ExamineStatusSelect v-model="searchExamineStatus" width="140px" />
      <UserSearchSelect v-model="searchUserId" width="180px" />
      <KeywordSearch v-model="searchKeyword" show-label label="关键词" placeholder="搜索友链名称或描述" auto-width />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions
        :selected-count="selectedLinks.length"
        :show-batch-audit="true"
        :show-batch-reject="true"
        :show-batch-delete="true"
        @batch-audit="handleBatchAudit"
        @batch-reject="handleBatchReject"
        @batch-delete="handleBatchDelete"
      />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        :data="paginatedLinkList"
        :loading="loading"
        show-selection
        show-cover
        show-name
        show-user
        show-examine-status
        show-create-time
        cover-width="120"
        name-min-width="150"
        user-width="120"
        examine-status-width="80"
        create-time-width="110"
        :has-edit-action="true"
        :has-delete-action="true"
        :has-audit-action="true"
        :has-reject-action="true"
        actions-width="320"
        @selection-change="handleSelectionChange"
        @view="handleViewLink"
        @audit="handleAuditLink"
        @reject="handleRejectLink"
        @delete="handleDeleteLink"
        @edit="handleEditLink"
      >
        <!-- URL列 -->
        <el-table-column prop="url" label="网站地址" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.url" placement="top-start">
              <a :href="row.url" target="_blank" class="link-url">{{ row.url }}</a>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 描述列 -->
        <el-table-column prop="description" label="网站描述" min-width="250">
          <template #default="{ row }">
            <el-tooltip :content="row.description" placement="top-start">
              <div class="link-description">{{ row.description || '暂无描述' }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 邮箱列 -->
        <el-table-column prop="email" label="联系邮箱" width="180">
          <template #default="{ row }">
            <el-tooltip :content="row.email" placement="top-start">
              <div class="link-email">{{ row.email }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedLinkList"
        :selected-items="selectedLinks"
        show-selection
        show-cover
        show-name
        show-description
        show-user
        show-examine-status
        show-meta
        :has-audit-action="true"
        :has-reject-action="true"
        :has-delete-action="true"
        @select="handleMobileSelect"
        @audit="handleAuditLink"
        @reject="handleRejectLink"
        @delete="handleDeleteLink"
      />
    </template>
  </ManagementCard>

  <!-- 编辑友链对话框 -->
  <el-dialog v-model="editDialogVisible" title="编辑友链" width="500px" destroy-on-close>
    <el-form ref="editFormRef" :model="editLinkForm" :rules="editFormRules" label-width="80px">
      <el-form-item label="网站名称" prop="name">
        <el-input v-model="editLinkForm.name" placeholder="请输入网站名称" />
      </el-form-item>
      <el-form-item label="网站地址" prop="url">
        <el-input v-model="editLinkForm.url" placeholder="请输入网站地址" />
      </el-form-item>
      <el-form-item label="封面图片" prop="coverUrl">
        <el-input v-model="editLinkForm.coverUrl" placeholder="请输入封面图片URL" />
      </el-form-item>
      <el-form-item label="网站描述" prop="description">
        <el-input v-model="editLinkForm.description" type="textarea" :rows="3" placeholder="请输入网站描述" />
      </el-form-item>
      <el-form-item label="联系邮箱" prop="email">
        <el-input v-model="editLinkForm.email" placeholder="请输入联系邮箱" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="editLinkLoading" @click="submitEditLink"> 确定 </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useUserSearch } from '@/hooks/useUserSearch'
import { adminGetLinkList, adminSearchLink, adminExamineLink, adminBatchExamineLink, adminDeleteLink, adminBatchDeleteLink, adminUpdateLink } from '@/api/link'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import UserSearchSelect from '@/components/search/UserSearchSelect.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 用户搜索（预留扩展）
useUserSearch()

// 友链列表数据
const linkList = ref([])
const paginatedLinkList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 搜索条件
const searchExamineStatus = ref('')
const searchKeyword = ref('')
const searchStartTime = ref('')
const searchEndTime = ref('')
const searchUserId = ref('')

// 选中的友链
const selectedLinks = ref([])

// 编辑对话框
const editDialogVisible = ref(false)
const editFormRef = ref(null)
const editLinkLoading = ref(false)
const editLinkForm = ref({
  id: null,
  name: '',
  url: '',
  coverUrl: '',
  description: '',
  email: '',
})

// 编辑表单验证规则
const editFormRules = {
  name: [{ required: true, message: '请输入网站名称', trigger: 'blur' }],
  url: [{ required: true, message: '请输入网站地址', trigger: 'blur' }],
  description: [{ required: true, message: '请输入网站描述', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入联系邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
}

// 搜索条件是否为空
const hasSearchConditions = computed(() => !!(searchExamineStatus.value || searchKeyword.value || searchStartTime.value || searchEndTime.value || searchUserId.value))

// 构建搜索参数
const buildSearchPayload = computed(() => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  userId: searchUserId.value || undefined,
  examineStatus: searchExamineStatus.value ? parseInt(searchExamineStatus.value, 10) : undefined,
  keyword: searchKeyword.value || undefined,
  createTimeStart: searchStartTime.value || undefined,
  createTimeEnd: searchEndTime.value || undefined,
}))

// 应用分页数据
const applyPageData = (pageData) => {
  linkList.value = pageData?.data || []
  paginatedLinkList.value = linkList.value
  total.value = Number(pageData?.total || 0)
  selectedLinks.value = []
}

// 获取友链列表
const fetchLinks = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions.value) {
      const res = await adminSearchLink(buildSearchPayload.value)
      pageData = res.data
    } else {
      const res = await adminGetLinkList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch {
    ElMessage.error(hasSearchConditions.value ? '搜索友链失败' : '获取友链列表失败')
  } finally {
    loading.value = false
  }
}

// 搜索处理
const handleSearch = async () => {
  currentPage.value = 1
  await fetchLinks()
}

// 重置处理
const handleReset = () => {
  searchExamineStatus.value = ''
  searchKeyword.value = ''
  searchStartTime.value = ''
  searchEndTime.value = ''
  searchUserId.value = ''
  handleSearch()
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchStartTime.value = startTime
  searchEndTime.value = endTime
  handleSearch()
}

// 表格多选
const handleSelectionChange = (links) => {
  selectedLinks.value = links
}

// 移动端选择处理
const handleMobileSelect = (link) => {
  const index = selectedLinks.value.findIndex((item) => item.id === link.id)
  if (index > -1) {
    selectedLinks.value.splice(index, 1)
  } else {
    selectedLinks.value.push(link)
  }
}

// 查看友链
const handleViewLink = (link) => {
  // 可以跳转到详情页或打开详情对话框
  console.log('查看友链:', link)
}

// 编辑友链
const handleEditLink = (link) => {
  editLinkForm.value = {
    id: link.id,
    name: link.name || '',
    url: link.url || '',
    coverUrl: link.coverUrl || '',
    description: link.description || '',
    email: link.email || '',
  }
  editDialogVisible.value = true
}

// 提交编辑友链
const submitEditLink = async () => {
  if (!editFormRef.value) return

  try {
    await editFormRef.value.validate()
    editLinkLoading.value = true
    await adminUpdateLink(editLinkForm.value)
    ElMessage.success('编辑成功')
    editDialogVisible.value = false
    await fetchLinks()
  } catch {
    ElMessage.error('编辑失败')
  } finally {
    editLinkLoading.value = false
  }
}

// 批量操作通用处理
const handleBatchOperation = (operation, apiFn, successMsg, errorMsg) => {
  if (selectedLinks.value.length === 0) return

  ElMessageBox.confirm(`确定要${operation}选中的 ${selectedLinks.value.length} 个友链吗？`, '确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        const linkIds = selectedLinks.value.map((link) => link.id)
        await apiFn(linkIds)
        ElMessage.success(successMsg)
        await fetchLinks()
      } catch {
        ElMessage.error(errorMsg)
      }
    })
    .catch(() => {
      ElMessage.info('操作已取消')
    })
}

// 单个友链操作通用处理
const handleSingleOperation = (apiFn, successMsg, errorMsg) => {
  return async (link) => {
    try {
      await apiFn(link.id)
      ElMessage.success(successMsg)
      await fetchLinks()
    } catch {
      ElMessage.error(errorMsg)
    }
  }
}

// 审核单个友链
const handleAuditLink = handleSingleOperation((id) => adminExamineLink(id, 1), '审核成功', '审核失败')

// 拒绝单个友链
const handleRejectLink = handleSingleOperation((id) => adminExamineLink(id, 2), '拒绝成功', '拒绝失败')

// 批量审核
const handleBatchAudit = () => handleBatchOperation('审核通过', (ids) => adminBatchExamineLink(ids, 1), '批量审核成功', '批量审核失败')

// 批量拒绝
const handleBatchReject = () => handleBatchOperation('拒绝', (ids) => adminBatchExamineLink(ids, 2), '批量拒绝成功', '批量拒绝失败')

// 删除单个友链
const handleDeleteLink = (link) => {
  ElMessageBox.confirm('确定要删除该友链吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await adminDeleteLink(link.id)
        ElMessage.success('删除成功')
        await fetchLinks()
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 批量删除
const handleBatchDelete = () => handleBatchOperation('删除', adminBatchDeleteLink, '批量删除成功', '批量删除失败')

// 初始化
onMounted(() => {
  fetchLinks()
})
</script>

<style lang="scss" scoped>
// 链接样式
.link-url {
  color: var(--el-color-primary);
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.link-description {
  overflow: hidden;
  cursor: pointer;
  display: -webkit-box;
  text-overflow: ellipsis;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

.link-email {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  color: var(--text-muted);
}

// 响应式
@media screen and (max-width: 768px) {
  .link-url,
  .link-description,
  .link-email {
    width: 100%;
  }
}
</style>
