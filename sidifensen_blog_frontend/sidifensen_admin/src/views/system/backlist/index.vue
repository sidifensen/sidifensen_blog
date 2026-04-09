<template>
  <ManagementCard
    title="黑名单管理"
    :showTimeFilter="true"
    :showPagination="true"
    :modelCurrentPage="currentPage"
    :modelPageSize="pageSize"
    :total="total"
    @search="fetchBlacklists"
    @timeChange="handleTimeChange"
  >
    <!-- 筛选器 -->
    <template #filters>
      <CommonSelect v-model="searchForm.type" :options="blacklistTypeOptions" label="黑名单类型" placeholder="黑名单类型" width="140px" size="small" @change="handleSearch" />
      <KeywordSearch v-model.number="searchForm.userId" placeholder="用户ID" :debounce="0" @search="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
      <el-button type="primary" size="small" @click="handleAdd" :icon="Plus" class="add-button">新增黑名单</el-button>
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions :selectedCount="selectedBlacklists.length" :showBatchDelete="true" @batchDelete="handleBatchDelete" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        v-loading="loading"
        :data="paginatedBlacklistList"
        :show-selection="true"
        :show-id="true"
        :show-status="false"
        :show-create-time="false"
        :show-actions="true"
        :has-view-action="false"
        :has-edit-action="true"
        :has-delete-action="true"
        :actions-width="150"
        @selection-change="handleSelectionChange"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <!-- 类型列 -->
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <div class="blacklist-type" :class="row.type === 0 ? 'type-user' : 'type-ip'">
              {{ row.type === 0 ? '用户' : 'IP地址' }}
            </div>
          </template>
        </el-table-column>
        <!-- 用户ID列 -->
        <el-table-column prop="userId" label="用户ID" width="100">
          <template #default="{ row }">
            <span>{{ row.userId || '-' }}</span>
          </template>
        </el-table-column>
        <!-- IP地址列 -->
        <el-table-column prop="ip" label="IP地址" width="150">
          <template #default="{ row }">
            <el-tooltip v-if="row.ip" :content="row.ip" placement="top-start">
              <div class="blacklist-ip">{{ row.ip }}</div>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <!-- 拉黑原因列 -->
        <el-table-column prop="reason" label="拉黑原因" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.reason" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
              <div class="blacklist-reason">{{ row.reason }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
        <!-- 拉黑时间列 -->
        <el-table-column prop="banTime" label="拉黑时间" sortable width="110" />
        <!-- 到期时间列 -->
        <el-table-column prop="expireTime" label="到期时间" sortable width="110">
          <template #default="{ row }">
            <span :class="{ 'expired-time': isExpired(row.expireTime) }">
              {{ row.expireTime }}
            </span>
          </template>
        </el-table-column>
        <!-- 状态列 -->
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <div class="blacklist-status" :class="isExpired(row.expireTime) ? 'status-expired' : 'status-active'">
              {{ isExpired(row.expireTime) ? '已过期' : '生效中' }}
            </div>
          </template>
        </el-table-column>
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedBlacklistList"
        :selectedItems="selectedBlacklists"
        showSelection
        showMeta
        :hasEditAction="true"
        :hasDeleteAction="true"
        @select="handleMobileSelect"
        @edit="handleEdit"
        @delete="handleDelete"
      >
        <!-- 自定义卡片内容 -->
        <template #custom="{ item }">
          <div class="mobile-meta">
            <span class="meta-item">
              <span class="label">类型:</span>
              <span class="value">{{ item.type === 0 ? '用户' : 'IP地址' }}</span>
            </span>
            <span class="blacklist-status" :class="isExpired(item.expireTime) ? 'status-expired' : 'status-active'">
              {{ isExpired(item.expireTime) ? '已过期' : '生效中' }}
            </span>
          </div>
          <div class="mobile-ip" v-if="item.userId">用户ID: {{ item.userId }}</div>
          <div class="mobile-ip" v-if="item.ip">IP: {{ item.ip }}</div>
          <div class="mobile-reason">原因: {{ item.reason }}</div>
        </template>
      </MobileCardList>
    </template>
  </ManagementCard>

  <!-- 新增/编辑黑名单对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose" width="600px" class="blacklist-dialog" destroy-on-close>
    <el-form ref="blacklistFormRef" :model="blacklistForm" :rules="rules" label-width="90px">
      <el-form-item v-if="!isEdit" prop="userIds" label="用户ID">
        <el-input v-model="blacklistForm.userIds" placeholder="请输入用户ID，多个用逗号分隔，如：3,4,5" />
        <div class="form-tip">支持批量添加，多个用户ID用逗号分隔，如：3,4,5</div>
      </el-form-item>

      <el-form-item prop="reason" label="拉黑原因">
        <el-input v-model="blacklistForm.reason" type="textarea" :rows="3" placeholder="请输入拉黑原因" maxlength="500" show-word-limit />
      </el-form-item>

      <el-form-item prop="expireTime" label="到期时间">
        <el-date-picker
          v-model="blacklistForm.expireTime"
          type="datetime"
          format="YYYY-MM-DD HH:mm:ss"
          value-format="YYYY-MM-DD HH:mm:ss"
          placeholder="选择到期时间"
          style="width: 100%"
          :disabled-date="disabledDate"
        />
        <div class="form-tip">选择黑名单到期时间，过期后自动失效</div>
      </el-form-item>

      <el-form-item label="快捷设置">
        <el-button-group>
          <el-button size="small" @click="setExpireTime(1)">1小时</el-button>
          <el-button size="small" @click="setExpireTime(6)">6小时</el-button>
          <el-button size="small" @click="setExpireTime(24)">1天</el-button>
          <el-button size="small" @click="setExpireTime(24 * 7)">7天</el-button>
          <el-button size="small" @click="setExpireTime(24 * 30)">30天</el-button>
          <el-button size="small" @click="setExpireTime(24 * 365)">永久</el-button>
        </el-button-group>
      </el-form-item>
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleDialogClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Plus, Delete, Edit } from '@element-plus/icons-vue'
import { getBlacklistList, addBlacklist, searchBlacklist, updateBlacklist, deleteBlacklist } from '@/api/blacklist'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import DataTable from '@/components/data/DataTable.vue'
import MobileCardList from '@/components/data/MobileCardList.vue'
import BatchActions from '@/components/actions/BatchActions.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import CommonSelect from '@/components/search/CommonSelect.vue'

// 黑名单列表数据
const blacklistList = ref([])
const paginatedBlacklistList = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('批量新增黑名单')
const isEdit = ref(false)
const submitLoading = ref(false)
const blacklistFormRef = ref(null)

// 搜索表单
const searchForm = reactive({
  type: '',
  userId: null,
  banTimeStart: null,
  banTimeEnd: null,
  expireTimeStart: null,
  expireTimeEnd: null,
})

// 黑名单表单
const blacklistForm = reactive({
  id: null,
  userIds: '',
  reason: '',
  expireTime: '',
})

// 黑名单类型选项
const blacklistTypeOptions = [
  { label: '用户', value: 0 },
  { label: 'IP地址', value: 1 },
]

// 选中的黑名单
const selectedBlacklists = ref([])

// 批量操作加载状态
const batchDeleteLoading = ref(false)

// 表单验证规则
const rules = {
  userIds: [{ required: true, message: '请输入用户ID', trigger: 'change' }],
  reason: [{ required: true, message: '请输入拉黑原因', trigger: 'blur' }],
  expireTime: [{ required: true, message: '请选择到期时间', trigger: 'change' }],
}

// 判断是否过期
const isExpired = (expireTime) => {
  if (!expireTime) return false
  return new Date(expireTime) < new Date()
}

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000
}

// 快捷设置到期时间
const setExpireTime = (hours) => {
  const now = new Date()
  const expireDate = new Date(now.getTime() + hours * 60 * 60 * 1000)
  const year = expireDate.getFullYear()
  const month = String(expireDate.getMonth() + 1).padStart(2, '0')
  const day = String(expireDate.getDate()).padStart(2, '0')
  const hour = String(expireDate.getHours()).padStart(2, '0')
  const minute = String(expireDate.getMinutes()).padStart(2, '0')
  const second = String(expireDate.getSeconds()).padStart(2, '0')
  blacklistForm.expireTime = `${year}-${month}-${day} ${hour}:${minute}:${second}`
}

// 是否有搜索条件
const hasSearchConditions = () => searchForm.type !== '' || searchForm.userId || searchForm.banTimeStart || searchForm.banTimeEnd || searchForm.expireTimeStart || searchForm.expireTimeEnd

// 构建搜索参数
const buildSearchPayload = () => {
  const searchData = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  }
  if (searchForm.type !== '' && searchForm.type !== null && searchForm.type !== undefined) {
    searchData.type = searchForm.type
  }
  if (searchForm.userId) {
    searchData.userId = parseInt(searchForm.userId)
  }
  if (searchForm.banTimeStart) {
    searchData.banTimeStart = searchForm.banTimeStart
  }
  if (searchForm.banTimeEnd) {
    searchData.banTimeEnd = searchForm.banTimeEnd
  }
  if (searchForm.expireTimeStart) {
    searchData.expireTimeStart = searchForm.expireTimeStart
  }
  if (searchForm.expireTimeEnd) {
    searchData.expireTimeEnd = searchForm.expireTimeEnd
  }
  return searchData
}

// 应用分页数据
const applyPageData = (pageData) => {
  blacklistList.value = pageData?.data || []
  paginatedBlacklistList.value = blacklistList.value
  total.value = Number(pageData?.total || 0)
  selectedBlacklists.value = []
}

// 获取黑名单列表
const fetchBlacklists = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await searchBlacklist(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await getBlacklistList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索失败' : '获取黑名单列表失败')
  } finally {
    loading.value = false
  }
}

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchForm.banTimeStart = startTime
  searchForm.banTimeEnd = endTime
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchBlacklists()
}

// 重置处理
const handleReset = () => {
  searchForm.type = ''
  searchForm.userId = null
  searchForm.banTimeStart = null
  searchForm.banTimeEnd = null
  searchForm.expireTimeStart = null
  searchForm.expireTimeEnd = null
  handleSearch()
}

// 表格多选
const handleSelectionChange = (blacklists) => {
  selectedBlacklists.value = blacklists
}

// 移动端选择处理
const handleMobileSelect = (blacklist) => {
  const index = selectedBlacklists.value.findIndex((item) => item.id === blacklist.id)
  if (index > -1) {
    selectedBlacklists.value.splice(index, 1)
  } else {
    selectedBlacklists.value.push(blacklist)
  }
}

// 智能刷新列表
const refreshBlacklistList = async (deletedCount = 0) => {
  if (deletedCount > 0 && currentPage.value > 1 && blacklistList.value.length <= deletedCount) {
    currentPage.value -= 1
  }
  await fetchBlacklists()
}

// 新增黑名单
const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '批量新增黑名单'
  blacklistForm.id = null
  blacklistForm.userIds = ''
  blacklistForm.reason = ''
  blacklistForm.expireTime = ''
  dialogVisible.value = true
}

// 编辑黑名单
const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑黑名单'
  blacklistForm.id = row.id
  blacklistForm.userIds = row.userId ? String(row.userId) : ''
  blacklistForm.reason = row.reason
  blacklistForm.expireTime = row.expireTime
  dialogVisible.value = true
}

// 提交表单
const handleSubmit = async () => {
  try {
    await blacklistFormRef.value.validate()
    submitLoading.value = true

    if (isEdit.value) {
      const updateData = {
        id: blacklistForm.id,
        reason: blacklistForm.reason,
        expireTime: blacklistForm.expireTime,
      }
      await updateBlacklist(updateData)
      ElMessage.success('修改成功')
    } else {
      const userIdsArray = blacklistForm.userIds
        .split(',')
        .map((id) => id.trim())
        .filter((id) => id !== '')
        .map((id) => parseInt(id))

      const addData = {
        userIds: userIdsArray,
        reason: blacklistForm.reason,
        expireTime: blacklistForm.expireTime,
      }
      await addBlacklist(addData)
      ElMessage.success('添加成功')
    }

    dialogVisible.value = false
    await refreshBlacklistList()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '修改失败' : '添加失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 删除单个黑名单
const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除该黑名单记录吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      try {
        await deleteBlacklist([id])
        ElMessage.success('删除成功')
        await refreshBlacklistList()
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedBlacklists.value.length} 条黑名单记录吗？`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      batchDeleteLoading.value = true
      try {
        const blacklistIds = selectedBlacklists.value.map((blacklist) => blacklist.id)
        await deleteBlacklist(blacklistIds)
        ElMessage.success('批量删除成功')
        await refreshBlacklistList()
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

// 关闭对话框
const handleDialogClose = () => {
  blacklistFormRef.value?.resetFields()
  dialogVisible.value = false
}

// 初始化
onMounted(() => {
  fetchBlacklists()
})
</script>

<style lang="scss" scoped>
// 黑名单类型样式
.blacklist-type {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.type-user {
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
  }

  &.type-ip {
    background-color: var(--el-color-warning-light-9);
    color: var(--el-color-warning);
  }
}

// IP地址样式
.blacklist-ip {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;
  font-size: 12px;
  color: var(--text-regular);

  &:hover {
    color: var(--el-color-primary);
  }
}

// 拉黑原因样式
.blacklist-reason {
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

// 过期时间样式
.expired-time {
  color: var(--text-muted);
  text-decoration: line-through;
}

// 状态样式
.blacklist-status {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;

  &.status-active {
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
  }

  &.status-expired {
    background-color: var(--el-fill-color);
    color: var(--text-muted);
  }
}

// 表格操作按钮组
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 5px;

  .edit-button {
    border-radius: 6px;
    background-color: var(--el-color-primary-light-9);
    color: var(--el-color-primary);
    border-color: var(--el-color-primary-light-9);

    &:hover {
      background-color: var(--el-color-primary-light-8);
      border-color: var(--el-color-primary-light-8);
    }
  }

  .delete-button {
    border-radius: 6px;
    background-color: var(--el-color-danger-light-9);
    color: var(--el-color-danger);
    border-color: var(--el-color-danger-light-9);

    &:hover {
      background-color: var(--el-color-danger-light-8);
      border-color: var(--el-color-danger-light-8);
    }
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

.mobile-ip {
  font-size: 12px;
  color: var(--text-muted);
  margin-bottom: 4px;
}

.mobile-reason {
  font-size: 12px;
  color: var(--text-regular);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

// 表单提示
.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
  line-height: 1.4;
}

// 新增按钮
.add-button {
  border-radius: 6px;
  background-color: var(--admin-primary);
  border-color: var(--admin-primary);

  &:hover {
    background-color: var(--admin-primary-dark);
    border-color: var(--admin-primary-dark);
  }
}
</style>
