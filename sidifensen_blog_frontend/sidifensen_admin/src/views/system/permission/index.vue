<template>
  <ManagementCard title="权限管理" :showTimeFilter="false" :showPagination="true" :modelCurrentPage="currentPage" :modelPageSize="pageSize" :total="total" @search="fetchPermissions">
    <!-- 筛选器 -->
    <template #filters>
      <KeywordSearch v-model="searchDescription" placeholder="搜索权限描述" :debounce="500" @search="handleSearch" />
      <KeywordSearch v-model="searchPermission" placeholder="搜索权限标识" :debounce="0" @search="handleSearch" />
      <CommonSelect v-model="searchMenuId" :options="menuList" option-label="name" option-value="id" label="菜单名称" placeholder="请选择菜单" width="160px" @change="handleSearch" />
      <el-button size="small" type="warning" :disabled="currentPermissionList.length === 0" @click="handleAuthorizeBatchRole" :icon="Avatar" class="authorize-button">批量授权</el-button>
      <el-button type="primary" size="small" @click="exportPermission" :icon="Download" class="export-button">导出</el-button>
      <el-button type="primary" size="small" @click="handleAddPermission" :icon="Plus" class="add-button">新增权限</el-button>
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <div v-loading="loading" class="permission-table-wrapper">
        <el-table :data="paginatedPermissionList" class="table" style="height: 100%" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="权限id" width="70" />
          <el-table-column prop="description" label="权限描述" />
          <el-table-column prop="permission" label="权限标识" />
          <el-table-column prop="menuName" label="菜单名称">
            <template #default="{ row }">
              <el-tag>
                <el-icon><component :is="row.icon" /></el-icon>
                {{ row.menuName }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" sortable width="120" />
          <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
          <el-table-column label="操作" width="260">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="primary" size="small" @click="handleEditPermission(row)" :icon="Edit" class="edit-button">编辑</el-button>
                <el-button type="danger" size="small" @click="handleDeletePermission(row.id)" :icon="Delete" class="delete-button">删除</el-button>
                <el-button size="small" type="warning" @click="handleAuthorizeRole(row)" :icon="Avatar" class="role-button">授权角色</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <div v-loading="loading" class="permission-cards">
        <el-card v-for="permission in paginatedPermissionList" :key="permission.id" class="permission-card" :class="{ 'is-selected': isPermissionSelected(permission.id) }">
          <div class="permission-card-content">
            <!-- 卡片头部 -->
            <div class="permission-header">
              <div class="header-left">
                <el-checkbox :model-value="isPermissionSelected(permission.id)" @change="handleMobileSelect(permission)" class="mobile-checkbox" />
                <div class="permission-id">#{{ permission.id }}</div>
              </div>
              <el-tag>
                <el-icon><component :is="permission.icon" /></el-icon>
                {{ permission.menuName }}
              </el-tag>
            </div>

            <!-- 权限信息 -->
            <div class="permission-info">
              <div class="info-row">
                <span class="label">权限描述:</span>
                <span class="value">{{ permission.description }}</span>
              </div>
              <div class="info-row">
                <span class="label">权限标识:</span>
                <span class="value permission-text">{{ permission.permission }}</span>
              </div>
              <div class="info-row">
                <span class="label">创建时间:</span>
                <span class="value time-text">{{ permission.createTime }}</span>
              </div>
              <div class="info-row">
                <span class="label">更新时间:</span>
                <span class="value time-text">{{ permission.updateTime }}</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="permission-actions">
              <el-button type="primary" size="small" @click="handleEditPermission(permission)" :icon="Edit" class="edit-button">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDeletePermission(permission.id)" :icon="Delete" class="delete-button">删除</el-button>
              <el-button size="small" type="warning" @click="handleAuthorizeRole(permission)" :icon="Avatar" class="role-button">授权角色</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </template>
  </ManagementCard>

  <!-- 新增/编辑权限对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose">
    <el-form ref="permissionFormRef" :model="permissionForm" :rules="rules" class="editForm">
      <el-form-item prop="description" label="权限描述">
        <el-input v-model="permissionForm.description" placeholder="请输入权限描述" />
      </el-form-item>
      <el-form-item prop="permission" label="权限标识">
        <el-input v-model="permissionForm.permission" placeholder="请输入权限标识" />
      </el-form-item>
      <CommonSelect v-model="permissionForm.menuId" :options="menuList" option-label="name" option-value="id" label="对应菜单" placeholder="请选择菜单名称" width="100%" />
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 授权角色弹窗对话框 -->
  <el-dialog v-model="authorizeDialogVisible" title="权限授权角色" :before-close="handleAuthorizeDialogClose" class="authorize-dialog">
    <div v-loading="authorizeLoading" class="authorize-dialog-content">
      <p class="role-name">当前权限: {{ currentPermission?.description }}</p>
      <template v-if="!authorizeLoading">
        <el-form ref="authorizeFormRef" class="authorize-form">
          <el-form-item label="选择角色">
            <el-checkbox-group v-model="selectedRole" class="role-checkbox-group">
              <el-checkbox v-for="role in allRole" :key="role.id" :label="role.id">{{ role.role }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </template>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleAuthorizeDialogClose">取消</el-button>
        <el-button type="primary" @click="handleAuthorizeSubmit">确认分配</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 批量授权角色弹窗对话框 -->
  <el-dialog v-model="authorizeBatchDialogVisible" title="权限批量授权角色" :before-close="handleAuthorizeBatchDialogClose" width="500px">
    <div class="authorize-dialog-content">
      <p class="role-name">当前权限: {{ currentPermissionList.map((item) => item.description).join(', ') }}</p>
      <el-form ref="authorizeBatchFormRef" class="authorize-form">
        <el-form-item label="选择角色">
          <el-checkbox-group v-model="selectedRole" class="role-checkbox-group" :disabled="authorizeBatchLoading">
            <el-checkbox v-for="role in allRole" :key="role.id" :label="role.id">{{ role.role }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleAuthorizeBatchDialogClose">取消</el-button>
        <el-button type="primary" @click="handleAuthorizeBatchSubmit" :disabled="authorizeBatchLoading">确认分配</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Plus, Edit, Delete, Avatar, Download } from '@element-plus/icons-vue'
import { getPermissionList, getPermissionPage, addPermission, updatePermission, deletePermission, queryPermissionPage } from '@/api/permission'
import { getRoleList } from '@/api/role'
import { addBatchRolePermission, addRolePermission, getRolesByPermission } from '@/api/role-permission'
import { getAllMenuList } from '@/api/menu'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import CommonSelect from '@/components/search/CommonSelect.vue'

import FileSaver from 'file-saver'
import * as XLSX from 'xlsx'

const exportPermission = async () => {
  const res = await getPermissionList()
  const fullPermissionList = res.data || []
  const data = fullPermissionList.map((item) => {
    return {
      权限id: item.id,
      权限描述: item.description,
      权限标识: item.permission,
      菜单名称: item.menuName,
      创建时间: item.createTime,
      更新时间: item.updateTime,
    }
  })

  // 创建工作表
  const ws = XLSX.utils.json_to_sheet(data)

  // 创建工作簿
  const wb = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(wb, ws, '权限列表')

  // 导出工作簿
  const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'array' })
  FileSaver.saveAs(new Blob([wbout], { type: 'application/octet-stream' }), '权限列表.xlsx')
}

// 权限列表数据
const permissionList = ref([])
// 分页后的权限列表
const paginatedPermissionList = ref([])
// 加载状态
const loading = ref(false)
// 当前页码
const currentPage = ref(1)
// 每页条数
const pageSize = ref(10)
// 总条数
const total = ref(0)
// 对话框可见性
const dialogVisible = ref(false)
// 对话框标题
const dialogTitle = ref('新增权限')

// 表单引用
const permissionFormRef = ref(null)
// 表单数据
const permissionForm = ref({
  id: null,
  description: '',
  permission: '',
  menuId: null,
})
// 表单验证规则
const rules = {
  description: [{ required: true, message: '请输入权限描述', trigger: 'blur' }],
  permission: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
  menuId: [{ required: true, message: '请选择菜单名称', trigger: 'change' }],
}

// 获取权限列表
const getPermissions = async () => {
  currentPage.value = 1
  await fetchPermissions()
}

// 初始化
onMounted(() => {
  getPermissions()
  getMenuList()
})

// 菜单列表
const menuList = ref([])

// 获取菜单列表
const getMenuList = async () => {
  try {
    const res = await getAllMenuList()
    menuList.value = res.data.data
  } catch (error) {
    ElMessage.error('获取菜单列表失败')
    console.error('获取菜单列表失败:', error)
  }
}

// 搜索权限描述
const searchDescription = ref('')
// 搜索权限标识
const searchPermission = ref('')
// 搜索菜单id
const searchMenuId = ref('')

const hasSearchConditions = () => !!(searchDescription.value || searchPermission.value || searchMenuId.value)

const buildSearchPayload = () => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  description: searchDescription.value || undefined,
  permission: searchPermission.value || undefined,
  menuId: searchMenuId.value || undefined,
})

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchPermissions()
}

const applyPageData = (pageData) => {
  permissionList.value = pageData?.data || []
  paginatedPermissionList.value = permissionList.value
  total.value = Number(pageData?.total || 0)
}

const fetchPermissions = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await queryPermissionPage(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await getPermissionPage({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索权限失败' : '获取权限列表失败')
  } finally {
    loading.value = false
  }
}

// 更新分页数据
const updatePaginatedPermissionList = () => {
  paginatedPermissionList.value = permissionList.value
}

// 处理添加权限
const handleAddPermission = () => {
  dialogTitle.value = '新增权限'
  permissionForm.value = {
    id: null,
    description: '',
    permission: '',
    menuId: null,
  }
  dialogVisible.value = true
}

// 处理编辑权限
const handleEditPermission = (row) => {
  dialogTitle.value = '编辑权限'
  // 深拷贝行数据
  permissionForm.value = { ...row }
  dialogVisible.value = true
}

// 处理删除权限
const handleDeletePermission = (id) => {
  ElMessageBox.confirm('确定要删除该权限吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true
      try {
        await deletePermission(id)
        ElMessage.success('删除成功')
        getPermissions()
      } catch (error) {
        ElMessage.error('删除失败')
      } finally {
        loading.value = false
      }
    })
    .catch(() => {
      // 取消删除
      ElMessage.info('删除已取消')
    })
}

// 处理表单提交
const handleSubmit = () => {
  permissionFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    try {
      if (permissionForm.value.id) {
        // 编辑权限
        await updatePermission(permissionForm.value)
        ElMessage.success('编辑权限成功')
      } else {
        // 新增权限
        await addPermission(permissionForm.value)
        ElMessage.success('新增权限成功')
      }
      dialogVisible.value = false
      getPermissions()
    } catch (error) {
      ElMessage.error(permissionForm.value.id ? '编辑权限失败' : '新增权限失败')
      handleDialogClose()
    }
  })
}

// 处理对话框关闭
const handleDialogClose = () => {
  permissionFormRef.value.resetFields()
  dialogVisible.value = false
}

// 授权角色弹窗
const authorizeDialogVisible = ref(false)
// 当前权限
const currentPermission = ref(null)

// 选择的角色
const selectedRole = ref([])
// 所有角色
const allRole = ref([])
// 授权弹窗加载状态
const authorizeLoading = ref(false)

// 授权角色弹窗（批量）
const authorizeBatchDialogVisible = ref(false)
// 当前权限
const currentPermissionList = ref([])
// 批量授权弹窗加载状态
const authorizeBatchLoading = ref(false)

// 处理授权角色
const handleAuthorizeRole = async (row) => {
  currentPermission.value = row
  selectedRole.value = []

  // 先打开弹窗并显示 loading
  authorizeDialogVisible.value = true
  authorizeLoading.value = true

  try {
    // 并行加载角色列表和权限已有角色
    const [roleRes, permissionRolesRes] = await Promise.all([getRoleList(), getRolesByPermission(row.id)])

    allRole.value = roleRes.data
    selectedRole.value = permissionRolesRes.data.map((item) => item.id)
  } catch (error) {
    ElMessage.error('获取角色列表失败')
    console.error('获取角色列表失败:', error)
  } finally {
    authorizeLoading.value = false
  }
}

// 处理授权提交
const handleAuthorizeSubmit = async () => {
  try {
    await addRolePermission({
      permissionId: currentPermission.value.id,
      roleIds: selectedRole.value,
    })
    ElMessage.success(`已为权限 ${currentPermission.value.permission} 分配角色`)
  } catch (error) {
    ElMessage.error(`为权限 ${currentPermission.value.permission} 分配角色失败`)
    console.error('分配角色失败:', error)
  } finally {
    authorizeDialogVisible.value = false
    authorizeLoading.value = false
    selectedRole.value = []
  }
}

// 处理授权对话框关闭
const handleAuthorizeDialogClose = () => {
  authorizeDialogVisible.value = false
  authorizeLoading.value = false
  selectedRole.value = []
}

// 表格多选
const handleSelectionChange = async (permission) => {
  currentPermissionList.value = permission
  // 获取角色列表
  const res = await getRoleList()
  allRole.value = res.data
}

// 检查权限是否被选中
const isPermissionSelected = (permissionId) => {
  return currentPermissionList.value.some((permission) => permission.id === permissionId)
}

// 移动端选择处理
const handleMobileSelect = (permission) => {
  const index = currentPermissionList.value.findIndex((item) => item.id === permission.id)
  if (index > -1) {
    // 已选中，取消选中
    currentPermissionList.value.splice(index, 1)
  } else {
    // 未选中，添加到选中列表
    currentPermissionList.value.push(permission)
  }
}

const handleAuthorizeBatchRole = () => {
  authorizeBatchDialogVisible.value = true
  // 清空已选角色
  selectedRole.value = []
  // 开始加载
  authorizeBatchLoading.value = true

  // 获取角色列表
  getRoleList()
    .then((res) => {
      allRole.value = res.data
    })
    .catch((error) => {
      ElMessage.error('获取角色列表失败')
      console.error('获取角色列表失败:', error)
    })
    .finally(() => {
      // 加载完成
      authorizeBatchLoading.value = false
    })
}

// 处理授权提交
const handleAuthorizeBatchSubmit = async () => {
  try {
    addBatchRolePermission({
      roleIds: selectedRole.value,
      permissionIds: currentPermissionList.value.map((item) => item.id),
    })
    ElMessage.success(`已为权限 ${currentPermissionList.value.map((item) => item.description).join(', ')} 分配角色`)
  } catch (error) {
    ElMessage.error(`为权限 ${currentPermissionList.value.map((item) => item.description).join(', ')} 分配角色失败`)
    console.error('分配角色失败:', error)
  } finally {
    authorizeBatchDialogVisible.value = false
    // 重置选择的角色和禁用的角色
    selectedRole.value = []
  }
}

// 处理授权对话框关闭
const handleAuthorizeBatchDialogClose = () => {
  authorizeBatchDialogVisible.value = false
  currentPermissionList.value = []
  selectedRole.value = []
}
</script>

<style lang="scss" scoped>
// 表格包装器
.permission-table-wrapper {
  flex: 1;
  overflow: auto;
}

// 搜索输入框
.search-input {
  width: 160px;
  border-radius: 8px;

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:focus-within {
      box-shadow: 0 0 0 3px var(--admin-primary-light);
      border-color: var(--admin-primary);
    }
  }
}

// 搜索选择框
.search-select {
  width: 160px;
  border-radius: 8px;

  :deep(.el-select__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:focus-within {
      box-shadow: 0 0 0 3px var(--admin-primary-light);
      border-color: var(--admin-primary);
    }
  }
}

// 表格
.table {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 220px);

  :deep(.el-tag__content) {
    display: flex;
    align-items: center;
  }

  :deep(.el-table__header-wrapper) th {
    font-weight: 600;
    color: #475569;
  }

  :deep(.el-table__body-wrapper) tr td {
    color: #64748b;
    padding: 12px 0;
  }

  :deep(.el-table__fixed-right) {
    box-shadow: -3px 0 10px rgba(0, 0, 0, 0.05);
  }

  .table-actions {
    height: 30px;
    display: flex;
    align-items: center;
    gap: 8px;

    .edit-button {
      background-color: #e0f2fe;
      color: #0284c7;
      border-color: #e0f2fe;
      border-radius: 6px;

      &:hover {
        background-color: #bae6fd;
        border-color: #bae6fd;
      }
    }

    .delete-button {
      background-color: #fee2e2;
      color: #ef4444;
      border-color: #fee2e2;
      border-radius: 6px;

      &:hover {
        background-color: #fecaca;
        border-color: #fecaca;
      }
    }

    .role-button {
      background-color: #fef3c7;
      color: #d97706;
      border-color: #fef3c7;
      border-radius: 6px;

      &:hover {
        background-color: #fde68a;
        border-color: #fde68a;
      }
    }
  }
}

// 移动端卡片视图
.permission-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px;

  .permission-card {
    transition: all 0.3s ease;
    border-radius: 8px;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    &.is-selected {
      border: 2px solid var(--admin-primary);
      box-shadow: 0 0 12px var(--admin-primary-light);
    }

    .permission-card-content {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .permission-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-bottom: 12px;
        border-bottom: 1px solid var(--el-border-color-lighter);

        .header-left {
          display: flex;
          align-items: center;
          gap: 8px;

          .mobile-checkbox {
            :deep(.el-checkbox__inner) {
              width: 18px;
              height: 18px;
            }
          }

          .permission-id {
            font-size: 12px;
            color: #666;
            background-color: #f5f5f5;
            padding: 2px 6px;
            border-radius: 4px;
          }
        }

        :deep(.el-tag) {
          display: flex;
          align-items: center;
          gap: 4px;
        }
      }

      .permission-info {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .info-row {
          display: flex;
          font-size: 14px;

          .label {
            font-weight: 500;
            color: #888;
            margin-right: 8px;
            flex-shrink: 0;
          }

          .value {
            color: #555;
            flex: 1;
            word-break: break-all;
          }

          .permission-text {
            color: var(--admin-primary);
            font-family: 'Courier New', monospace;
          }

          .time-text {
            font-size: 12px;
            color: #999;
          }
        }
      }

      .permission-actions {
        display: flex;
        gap: 8px;
        justify-content: center;
        padding-top: 12px;
        border-top: 1px solid var(--el-border-color-lighter);
        flex-wrap: wrap;

        .el-button {
          margin-left: 0;
          flex: 1;
          min-width: 70px;
        }

        .edit-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;

          &:hover {
            background-color: #bae6fd;
            border-color: #bae6fd;
          }
        }

        .delete-button {
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;

          &:hover {
            background-color: #fecaca;
            border-color: #fecaca;
          }
        }

        .role-button {
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;

          &:hover {
            background-color: #fde68a;
            border-color: #fde68a;
          }
        }
      }
    }
  }
}

// 对话框样式
:deep(.el-dialog) {
  border-radius: 16px;
  width: 500px;
  max-width: 90%;
}

// 新增/编辑菜单对话框
.editForm {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    border-radius: 16px;

    &:focus-within {
      box-shadow: 0 0 0 3px var(--admin-primary-light);
      border-color: var(--admin-primary);
    }
  }
}

// 授权角色对话框样式
.authorize-dialog-content {
  padding: 10px;

  .role-name {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: #1e293b;
  }

  .authorize-form {
    :deep(.el-form-item) {
      margin-bottom: 20px;
    }

    .role-checkbox-group {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;

      :deep(.el-checkbox) {
        margin-right: 16px;
        margin-bottom: 8px;
      }
    }
  }
}

.authorize-dialog {
  :deep(.el-dialog) {
    width: fit-content;
    max-width: 90%;
  }
}

// 按钮样式
.authorize-button {
  background-color: #fef3c7;
  color: #d97706;
  border-color: #fef3c7;
  border-radius: 6px;

  &:hover {
    background-color: #fde68a;
    border-color: #fde68a;
  }

  &.is-disabled {
    background-color: #f5f5f5;
    border-color: #d9d9d9;
    color: #bfbfbf;
    cursor: not-allowed;
    opacity: 0.6;
  }
}

.export-button {
  background-color: #e0f2fe;
  color: #0284c7;
  border-color: #e0f2fe;
  border-radius: 6px;

  &:hover {
    background-color: #bae6fd;
    border-color: #bae6fd;
  }
}

.add-button {
  border-radius: 8px;
  background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-primary-dark) 100%);
  border: none;

  &:hover {
    background: linear-gradient(135deg, var(--admin-primary-dark) 0%, var(--admin-primary-active) 100%);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px var(--admin-primary-light);
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .permission-table-wrapper {
    max-height: calc(100vh - 180px);
  }
}
</style>
