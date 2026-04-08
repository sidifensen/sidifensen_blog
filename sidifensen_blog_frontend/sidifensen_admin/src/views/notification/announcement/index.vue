<template>
  <ManagementCard title="公告管理" :showTimeFilter="false" :showPagination="true" :modelCurrentPage="currentPage" :modelPageSize="pageSize" :total="total" @search="fetchAnnouncements">
    <!-- 筛选器 -->
    <template #filters>
      <el-select v-model="searchForm.status" placeholder="状态筛选" filterable clearable size="small" style="width: 160px" @change="handleSearch">
        <el-option label="全部" value="" />
        <el-option label="待发送" :value="0" />
        <el-option label="发送中" :value="1" />
        <el-option label="已发送" :value="2" />
        <el-option label="发送失败" :value="3" />
      </el-select>
      <SearchButtons @search="handleSearch" @reset="handleReset" />
      <el-button type="primary" @click="handleSend" class="send-btn">
        <svg class="send-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M12 2L12 6M12 18L12 22M4.93 4.93L7.76 7.76M16.24 16.24L19.07 19.07M2 12L6 12M18 12L22 12M4.93 19.07L7.76 16.24M16.24 7.76L19.07 4.93" stroke-linecap="round" />
          <circle cx="12" cy="12" r="4" />
        </svg>
        发送公告
      </el-button>
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable :data="paginatedList" :loading="loading" showId :hasEditAction="true" :hasDeleteAction="true" @edit="handleEdit" @delete="handleDelete">
        <!-- 标题列 -->
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.title" placement="top-start">
              <div class="announcement-title">{{ row.title }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 内容列 -->
        <el-table-column prop="content" label="内容" min-width="250">
          <template #default="{ row }">
            <el-tooltip :content="row.content" placement="top-start">
              <div class="announcement-content">{{ row.content }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 发送方式列 -->
        <el-table-column prop="sendMethod" label="发送方式" width="180">
          <template #default="{ row }">
            <div class="send-methods">
              <span v-for="method in parseSendMethod(row.sendMethod)" :key="method" class="method-tag" :class="'method-' + method">
                {{ methodLabel(method) }}
              </span>
            </div>
          </template>
        </el-table-column>

        <!-- 发送对象列 -->
        <el-table-column prop="targetType" label="发送对象" width="100">
          <template #default="{ row }">
            <span>{{ row.targetType === 1 ? '全部用户' : '指定用户' }}</span>
          </template>
        </el-table-column>

        <!-- 状态列 -->
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <div class="status-badge" :class="'status-' + row.status">
              {{ statusLabel(row.status) }}
            </div>
          </template>
        </el-table-column>

        <!-- 发送时间列 -->
        <el-table-column prop="sendTime" label="发送时间" width="160" />

        <!-- 创建时间列 -->
        <el-table-column prop="createTime" label="创建时间" sortable width="160" />

        <!-- 操作列 -->
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 0" type="warning" link size="small" @click="handleCancel(row)">取消</el-button>
              <el-button type="danger" link size="small" @click="handleDelete(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList :data="paginatedList" :selectedItems="[]" showId :hasEditAction="true" :hasDeleteAction="true" @edit="handleEdit" @delete="handleDelete">
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="card-title-row">{{ item.title }}</div>
          <div class="card-content-row">{{ item.content }}</div>
          <div class="card-meta-row">
            <span class="send-methods">
              <span v-for="method in parseSendMethod(item.sendMethod)" :key="method" class="method-tag" :class="'method-' + method">
                {{ methodLabel(method) }}
              </span>
            </span>
            <span class="card-time">{{ item.createTime }}</span>
          </div>
          <div class="status-badge" :class="'status-' + item.status">
            {{ statusLabel(item.status) }}
          </div>
          <el-button v-if="item.status === 0" type="warning" link size="small" @click.stop="handleCancel(item)">取消</el-button>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 发送公告对话框 -->
  <el-dialog v-model="dialogVisible" title="发送公告" width="600px" class="announcement-dialog" destroy-on-close>
    <el-form ref="announcementFormRef" :model="announcementForm" :rules="rules" label-width="100px">
      <!-- 发送方式 -->
      <el-form-item label="发送方式" prop="sendMethod">
        <el-checkbox-group v-model="announcementForm.sendMethod">
          <el-checkbox label="system">系统通知（指定用户）</el-checkbox>
          <el-checkbox label="website">公告（全部用户）</el-checkbox>
          <el-checkbox label="email">邮件</el-checkbox>
        </el-checkbox-group>
      </el-form-item>

      <!-- 标题 -->
      <el-form-item label="标题" prop="title">
        <el-input v-model="announcementForm.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
      </el-form-item>

      <!-- 内容 -->
      <el-form-item label="内容" prop="content">
        <el-input v-model="announcementForm.content" type="textarea" :rows="4" placeholder="请输入公告内容" maxlength="2000" show-word-limit />
      </el-form-item>

      <!-- 指定用户选择 -->
      <el-form-item v-if="announcementForm.sendMethod.includes('system') || announcementForm.sendMethod.includes('email')" label="发送对象">
        <el-radio-group v-model="announcementForm.targetType">
          <el-radio :value="1">全部用户</el-radio>
          <el-radio :value="2">指定用户</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item v-if="announcementForm.targetType === 2 && (announcementForm.sendMethod.includes('system') || announcementForm.sendMethod.includes('email'))" label="选择用户">
        <el-select v-model="announcementForm.targetUsers" multiple filterable placeholder="搜索或选择用户" style="width: 100%">
          <el-option v-for="user in userList" :key="user.id" :label="user.nickname + ' (ID:' + user.id + ')'" :value="user.id" />
        </el-select>
        <div class="form-tip">支持多选，也可直接输入用户ID搜索</div>
      </el-form-item>

      <!-- website 方式时显示提示 -->
      <div v-if="announcementForm.sendMethod.includes('website') && announcementForm.sendMethod.length === 1" class="website-tip">
        <el-alert type="info" :closable="false"> 公告将发送给全部用户，无需选择发送对象 </el-alert>
      </div>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleDialogClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认发送</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAnnouncementPage, createAnnouncement, updateAnnouncement, cancelAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { getUserList } from '@/api/user'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 列表数据
const list = ref([])
const paginatedList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const submitLoading = ref(false)
const announcementFormRef = ref(null)
const isEdit = ref(false)
const editingId = ref(null)

// 搜索表单
const searchForm = reactive({
  status: '',
})

// 公告表单
const announcementForm = reactive({
  sendMethod: [],
  title: '',
  content: '',
  targetType: 1,
  targetUsers: [],
})

// 用户列表
const userList = ref([])

// 表单验证规则
const rules = {
  sendMethod: [
    {
      required: true,
      validator: (rule, value, callback) => {
        if (!value || value.length === 0) {
          callback(new Error('请至少选择一个发送方式'))
        } else {
          callback()
        }
      },
      trigger: 'change',
    },
  ],
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
}

// 解析发送方式
const parseSendMethod = (sendMethodStr) => {
  try {
    return JSON.parse(sendMethodStr)
  } catch {
    return []
  }
}

// 发送方式标签
const methodLabel = (method) => {
  const labels = { system: '系统通知', website: '公告', email: '邮件' }
  return labels[method] || method
}

// 状态标签
const statusLabel = (status) => {
  const labels = { 0: '待发送', 1: '发送中', 2: '已发送', 3: '发送失败' }
  return labels[status] ?? status
}

// 构建查询参数
const buildQueryParams = () => {
  return {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    status: searchForm.status !== '' && searchForm.status !== null ? searchForm.status : undefined,
  }
}

// 应用分页数据
const applyPageData = (pageData) => {
  list.value = pageData?.data || []
  paginatedList.value = list.value
  total.value = Number(pageData?.total || 0)
}

// 获取公告列表
const fetchAnnouncements = async () => {
  loading.value = true
  try {
    const params = buildQueryParams()
    const res = await getAnnouncementPage(params)
    applyPageData(res.data)
  } catch {
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
  }
}

// 获取用户列表（用于指定用户选择）
const fetchUsers = async () => {
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 1000 })
    userList.value = res.data?.data || []
  } catch {
    console.error('获取用户列表失败')
  }
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchAnnouncements()
}

// 重置处理
const handleReset = () => {
  searchForm.status = ''
  handleSearch()
}

// 打开发送对话框
const handleSend = async () => {
  isEdit.value = false
  editingId.value = null
  announcementForm.sendMethod = []
  announcementForm.title = ''
  announcementForm.content = ''
  announcementForm.targetType = 1
  announcementForm.targetUsers = []
  dialogVisible.value = true
  await fetchUsers()
}

// 编辑公告
const handleEdit = (row) => {
  isEdit.value = true
  editingId.value = row.id
  announcementForm.sendMethod = parseSendMethod(row.sendMethod)
  announcementForm.title = row.title
  announcementForm.content = row.content
  announcementForm.targetType = row.targetType || 1
  announcementForm.targetUsers = []
  dialogVisible.value = true
}

// 关闭对话框
const handleDialogClose = () => {
  announcementFormRef.value?.resetFields()
  dialogVisible.value = false
  isEdit.value = false
  editingId.value = null
}

// 提交表单
const handleSubmit = async () => {
  try {
    await announcementFormRef.value.validate()
    submitLoading.value = true

    const data = {
      title: announcementForm.title,
      content: announcementForm.content,
      sendMethod: JSON.stringify(announcementForm.sendMethod),
      targetType: announcementForm.targetType,
      targetUsers: JSON.stringify(announcementForm.targetUsers || []),
    }
    if (isEdit.value && editingId.value) {
      data.id = editingId.value
      await updateAnnouncement(data)
    } else {
      await createAnnouncement(data)
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '发送成功')
    dialogVisible.value = false
    await fetchAnnouncements()
  } catch {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '编辑失败' : '发送失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 取消公告
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await cancelAnnouncement(row.id)
        ElMessage.success('取消成功')
        await fetchAnnouncements()
      } catch {
        ElMessage.error('取消失败')
      }
    })
    .catch(() => {
      ElMessage.info('操作已取消')
    })
}

// 删除公告
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该公告吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteAnnouncement(id)
        ElMessage.success('删除成功')
        await fetchAnnouncements()
      } catch {
        ElMessage.error('删除失败')
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

// 初始化
onMounted(() => {
  fetchAnnouncements()
})
</script>

<style lang="scss" scoped>
// 公告标题样式
.announcement-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 公告内容样式
.announcement-content {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 发送方式标签
.send-methods {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;

  .method-tag {
    display: inline-block;
    padding: 2px 6px;
    border-radius: 8px;
    font-size: 11px;
    font-weight: 500;

    &.method-system {
      background: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }
    &.method-website {
      background: var(--el-color-success-light-9);
      color: var(--el-color-success);
    }
    &.method-email {
      background: var(--el-color-warning-light-9);
      color: var(--el-color-warning);
    }
  }
}

// 状态徽章
.status-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.status-0 {
    background: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
  }
  &.status-1 {
    background: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }
  &.status-2 {
    background: var(--el-color-success-light-9);
    color: var(--el-color-success);
  }
  &.status-3 {
    background: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }
}

// 移动端卡片样式
.card-title-row {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.card-content-row {
  font-size: 13px;
  color: var(--text-muted);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  margin-bottom: 8px;
}

.card-meta-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 11px;
  color: var(--text-muted);
  margin-bottom: 8px;
}

// 发送按钮样式
.send-btn {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  margin-left: auto;

  .send-icon {
    width: 16px;
    height: 16px;
  }
}

// 表单提示
.website-tip {
  margin-bottom: 18px;
}

.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
  line-height: 1.4;
}
</style>
