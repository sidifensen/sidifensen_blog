<template>
  <ManagementCard title="角色管理" :showTimeFilter="false" :showPagination="true" :modelCurrentPage="currentPage" :modelPageSize="pageSize" :total="total" @search="fetchRoles">
    <!-- 筛选器 -->
    <template #filters>
      <KeywordSearch v-model="searchQuery" placeholder="搜索角色名称" :debounce="500" @search="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
      <el-button type="primary" size="small" @click="handleAddRole" :icon="Plus" class="add-button"> 新增角色 </el-button>
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <div v-loading="loading" class="role-table-wrapper">
        <el-table :data="paginatedRoleList" class="table" style="height: 100%">
          <el-table-column prop="id" label="角色id" width="70" />
          <el-table-column prop="role" label="角色标识" />
          <el-table-column prop="name" label="角色名称" />
          <el-table-column prop="description" label="角色描述" />
          <el-table-column prop="status" label="状态" width="120">
            <template #default="{ row }">
              <el-switch
                v-model="row.status"
                size="large"
                active-color="var(--admin-primary)"
                inactive-color="#cccccc"
                active-text="正常"
                inactive-text="禁用"
                :active-value="0"
                :inactive-value="1"
                inline-prompt
                :loading="switchLoading"
                :before-change="() => handleStatusChange(row.id, row.status === 0 ? 1 : 0)"
              />
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" sortable width="120" />
          <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
          <el-table-column label="操作" width="340">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="primary" size="small" @click="handleEditRole(row)" :icon="Edit" class="edit-button"> 编辑 </el-button>
                <el-button type="danger" size="small" @click="handleDeleteRole(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
                <el-button size="small" type="warning" @click="handleAuthorizeUser(row)" :icon="User" class="user-button"> 分配用户 </el-button>
                <el-button size="small" type="success" @click="handleAuthorizeMenu(row)" :icon="Key" class="menu-button"> 分配权限 </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <div v-loading="loading" class="role-cards">
        <el-card v-for="role in paginatedRoleList" :key="role.id" class="role-card">
          <div class="role-card-content">
            <div class="role-header">
              <div class="header-left">
                <div class="role-id">#{{ role.id }}</div>
                <el-tag :type="role.status === 0 ? 'success' : 'danger'">
                  {{ role.status === 0 ? '正常' : '禁用' }}
                </el-tag>
              </div>
              <el-switch
                v-model="role.status"
                size="small"
                active-color="var(--admin-primary)"
                inactive-color="#cccccc"
                :active-value="0"
                :inactive-value="1"
                :loading="switchLoading"
                :before-change="() => handleStatusChange(role.id, role.status === 0 ? 1 : 0)"
              />
            </div>
            <div class="role-info">
              <div class="info-row">
                <span class="label">角色标识:</span>
                <span class="value role-text">{{ role.role }}</span>
              </div>
              <div class="info-row">
                <span class="label">角色名称:</span>
                <span class="value">{{ role.name }}</span>
              </div>
              <div class="info-row">
                <span class="label">角色描述:</span>
                <span class="value">{{ role.description }}</span>
              </div>
              <div class="info-row">
                <span class="label">创建时间:</span>
                <span class="value time-text">{{ role.createTime }}</span>
              </div>
              <div class="info-row">
                <span class="label">更新时间:</span>
                <span class="value time-text">{{ role.updateTime }}</span>
              </div>
            </div>
            <div class="role-actions">
              <el-button type="primary" size="small" @click="handleEditRole(role)" :icon="Edit" class="edit-button">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDeleteRole(role.id)" :icon="Delete" class="delete-button">删除</el-button>
              <el-button size="small" type="warning" @click="handleAuthorizeUser(role)" :icon="User" class="user-button">分配用户</el-button>
              <el-button size="small" type="success" @click="handleAuthorizeMenu(role)" :icon="Key" class="menu-button">分配权限</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </template>
  </ManagementCard>

  <!-- 新增/编辑角色对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose">
    <el-form ref="roleFormRef" :model="roleForm" :rules="rules" class="editForm">
      <el-form-item prop="role" label="角色标识">
        <el-input v-model="roleForm.role" placeholder="请输入角色标识" />
      </el-form-item>
      <el-form-item prop="name" label="角色名称">
        <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item prop="description" label="角色描述">
        <el-input v-model="roleForm.description" placeholder="请输入角色描述" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 分配用户弹窗对话框 -->
  <el-dialog v-model="authorizeDialogVisible" title="角色分配用户" :before-close="handleAuthorizeDialogClose" class="authorize-dialog">
    <div v-loading="authorizeLoading" class="authorize-dialog-content">
      <p class="role-name">当前角色: {{ currentRole?.name }}</p>
      <template v-if="!authorizeLoading">
        <el-transfer
          v-model="selectedUser"
          :data="transferUserData"
          :filterable="true"
          :filter-method="filterMethod"
          filter-placeholder="搜索用户"
          :titles="['待分配用户', '已分配用户']"
          :button-texts="['取消分配', '分配用户']"
          class="user-transfer"
        />
      </template>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleAuthorizeDialogClose">取消</el-button>
        <el-button type="primary" @click="handleAuthorizeSubmit">确认分配</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 分配权限弹窗对话框 -->
  <el-dialog v-model="authorizeMenuDialogVisible" :title="'分配 ' + currentMenuRole?.name + ' 的菜单权限'" :before-close="handleAuthorizeMenuDialogClose" width="600px">
    <div class="authorize-menu-dialog-content">
      <div class="menu-toolbar">
        <el-button size="small" @click="handleExpandAll(false)">全部收起</el-button>
        <el-button size="small" type="primary" @click="handleExpandAll(true)">全部展开</el-button>
      </div>
      <el-tree
        ref="menuTreeRef"
        :data="menuTreeData"
        :props="menuTreeProps"
        node-key="id"
        :default-expand-all="false"
        :expand-on-click-node="false"
        :check-strictly="false"
        show-checkbox
        :default-checked-keys="selectedMenuIds"
        :default-expanded-keys="defaultExpandedKeys"
        @node-click="handleNodeClick"
        class="menu-tree"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span class="node-icon">
              <el-icon v-if="data.icon"><component :is="data.icon" /></el-icon>
            </span>
            <span class="node-label">{{ node.label }}</span>
          </span>
        </template>
      </el-tree>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleAuthorizeMenuDialogClose">取消</el-button>
        <el-button type="primary" @click="handleAuthorizeMenuSubmit">提交</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, User, Key } from '@element-plus/icons-vue'
import { getRoleList, getRolePage, addRole, updateRole, deleteRole, queryRolePage } from '@/api/role'
import { addUser, getUsersByRole } from '@/api/user-role'
import { getUserList } from '@/api/user'
import { getAllMenuList } from '@/api/menu'
import { getMenusByRole, assignMenus } from '@/api/role-menu'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'

// 搜索
const searchQuery = ref('')

// 角色列表数据
const roleList = ref([])
const paginatedRoleList = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')

const roleFormRef = ref(null)
const roleForm = ref({
  id: null,
  name: '',
  role: '',
  description: '',
  status: 0,
})

const rules = {
  role: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入角色描述', trigger: 'blur' }],
}

// 当前页码
const currentPage = ref(1)
const pageSize = ref(10)

const hasSearchConditions = () => !!searchQuery.value.trim()

const applyPageData = (pageData) => {
  roleList.value = pageData?.data || []
  paginatedRoleList.value = roleList.value
  total.value = Number(pageData?.total || 0)
}

const fetchRoles = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await queryRolePage({
        name: searchQuery.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    } else {
      const res = await getRolePage({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索角色失败' : '获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  currentPage.value = 1
  await fetchRoles()
}

const handleReset = () => {
  searchQuery.value = ''
  handleSearch()
}

const handleAddRole = () => {
  dialogTitle.value = '新增角色'
  roleForm.value = {
    id: null,
    role: '',
    name: '',
    description: '',
    status: 0,
  }
  dialogVisible.value = true
}

const handleEditRole = (row) => {
  dialogTitle.value = '编辑角色'
  roleForm.value = { ...row }
  dialogVisible.value = true
}

const handleDeleteRole = (id) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true
      try {
        await deleteRole(id)
        ElMessage.success('删除成功')
        fetchRoles()
      } catch (error) {
        ElMessage.error('删除失败')
      } finally {
        loading.value = false
      }
    })
    .catch(() => {
      ElMessage.info('删除已取消')
    })
}

const switchLoading = ref(false)
const handleStatusChange = async (id, status) => {
  return new Promise((resolve, reject) => {
    switchLoading.value = true
    updateRole({ id, status })
      .then(() => {
        ElMessage.success('状态更新成功')
        const role = roleList.value.find((item) => item.id === id)
        if (role) {
          role.status = status
        }
        resolve()
      })
      .catch((error) => {
        ElMessage.error('状态更新失败')
        reject(error)
      })
      .finally(() => {
        switchLoading.value = false
      })
  })
}

const handleSubmit = () => {
  roleFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (roleForm.value.id) {
        await updateRole(roleForm.value)
        ElMessage.success('编辑角色成功')
      } else {
        await addRole(roleForm.value)
        ElMessage.success('新增角色成功')
      }
      dialogVisible.value = false
      fetchRoles()
    } catch (error) {
      ElMessage.error(roleForm.value.id ? '编辑角色失败' : '新增角色失败')
      handleDialogClose()
    }
  })
}

const handleDialogClose = () => {
  roleFormRef.value.resetFields()
  dialogVisible.value = false
}

// 授权角色弹窗
const authorizeDialogVisible = ref(false)
const authorizeLoading = ref(false)
const currentRole = ref(null)
const selectedUser = ref([])
const allUser = ref([])

const transferUserData = computed(() => {
  return allUser.value.map((user) => ({
    key: user.id,
    label: user.username,
  }))
})

const filterMethod = (query, item) => {
  return item.label.toLowerCase().includes(query.toLowerCase())
}

const handleAuthorizeUser = async (row) => {
  currentRole.value = row
  selectedUser.value = []

  authorizeDialogVisible.value = true
  authorizeLoading.value = true

  try {
    const [userRes, roleUsersRes] = await Promise.all([getUserList(), getUsersByRole(row.id)])
    allUser.value = userRes.data
    selectedUser.value = roleUsersRes.data.map((item) => item.id)
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    authorizeDialogVisible.value = false
  } finally {
    authorizeLoading.value = false
  }
}

const handleAuthorizeSubmit = async () => {
  try {
    await addUser({
      roleId: currentRole.value.id,
      userIds: selectedUser.value,
    })
    ElMessage.success(`已为角色 ${currentRole.value.name} 分配用户`)
  } catch (error) {
    ElMessage.error(`为角色 ${currentRole.value.name} 分配用户失败`)
  } finally {
    authorizeDialogVisible.value = false
    authorizeLoading.value = false
    selectedUser.value = []
  }
}

const handleAuthorizeDialogClose = () => {
  authorizeDialogVisible.value = false
  authorizeLoading.value = false
  selectedUser.value = []
}

// 分配权限弹窗
const authorizeMenuDialogVisible = ref(false)
const menuTreeRef = ref(null)
const currentMenuRole = ref(null)
const menuTreeData = ref([])
const menuTreeProps = {
  children: 'children',
  label: 'name',
}
const selectedMenuIds = ref([])
const defaultExpandedKeys = ref([])

const handleAuthorizeMenu = async (row) => {
  currentMenuRole.value = row
  selectedMenuIds.value = []
  defaultExpandedKeys.value = []

  const res = await getAllMenuList()
  menuTreeData.value = buildMenuTree(res.data)
  defaultExpandedKeys.value = res.data.map((menu) => menu.id)

  try {
    const menuRes = await getMenusByRole(row.id)
    selectedMenuIds.value = menuRes.data || []
  } catch (error) {
    console.error('获取角色菜单权限失败:', error)
  }

  authorizeMenuDialogVisible.value = true
}

const buildMenuTree = (menus) => {
  const menuMap = new Map()
  const result = []

  menus.forEach((menu) => {
    menuMap.set(menu.id, { ...menu, children: [] })
  })

  menus.forEach((menu) => {
    const menuNode = menuMap.get(menu.id)
    if (menu.parentId === 0) {
      result.push(menuNode)
    } else {
      const parentNode = menuMap.get(menu.parentId)
      if (parentNode) {
        parentNode.children.push(menuNode)
      }
    }
  })

  return result
}

const handleExpandAll = (expand) => {
  const tree = menuTreeRef.value
  if (tree) {
    const nodes = tree.store.nodesMap
    Object.values(nodes).forEach((node) => {
      node.expanded = expand
    })
  }
}

const handleNodeClick = (data) => {}

const handleAuthorizeMenuSubmit = async () => {
  try {
    const checkedKeys = menuTreeRef.value?.getCheckedKeys() || []
    const halfCheckedKeys = menuTreeRef.value?.getHalfCheckedKeys() || []
    const allSelectedKeys = [...checkedKeys, ...halfCheckedKeys]

    await assignMenus({
      roleId: currentMenuRole.value.id,
      menuIds: allSelectedKeys,
    })
    ElMessage.success(`已为角色 ${currentMenuRole.value.name} 分配菜单权限`)
  } catch (error) {
    ElMessage.error(`为角色 ${currentMenuRole.value.name} 分配菜单权限失败`)
  } finally {
    authorizeMenuDialogVisible.value = false
  }
}

const handleAuthorizeMenuDialogClose = () => {
  authorizeMenuDialogVisible.value = false
  selectedMenuIds.value = []
  menuTreeData.value = []
}

onMounted(() => {
  fetchRoles()
})
</script>

<style lang="scss" scoped>
// 表格包装器
.role-table-wrapper {
  flex: 1;
  overflow: auto;
}

// 表格
.table {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 220px);

  :deep(.el-table__header-wrapper) th {
    font-weight: 600;
    color: var(--text-regular);
  }

  :deep(.el-table__body-wrapper) tr td {
    color: var(--text-muted);
    padding: 12px 0;
  }

  :deep(.el-table__fixed-right) {
    box-shadow: -3px 0 10px var(--shadow-card);
  }

  .table-actions {
    height: 30px;
    display: flex;
    align-items: center;
    gap: 8px;

    .edit-button {
      background-color: var(--action-audit-bg);
      color: var(--action-audit-color);
      border-color: var(--action-audit-border);
      border-radius: 6px;

      &:hover {
        background-color: var(--action-audit-hover-bg);
        border-color: var(--action-audit-hover-border);
      }
    }

    .delete-button {
      background-color: var(--action-delete-bg);
      color: var(--action-delete-color);
      border-color: var(--action-delete-border);
      border-radius: 6px;

      &:hover {
        background-color: var(--action-delete-hover-bg);
        border-color: var(--action-delete-hover-border);
      }
    }

    .user-button {
      background-color: var(--action-user-bg);
      color: var(--action-user-color);
      border-color: var(--action-user-border);
      border-radius: 6px;

      &:hover {
        background-color: var(--action-user-hover-bg);
        border-color: var(--action-user-hover-border);
      }
    }

    .menu-button {
      background-color: var(--action-edit-bg);
      color: var(--action-edit-color);
      border-color: var(--action-edit-border);
      border-radius: 6px;

      &:hover {
        background-color: var(--action-edit-hover-bg);
        border-color: var(--action-edit-hover-border);
      }
    }
  }
}

// 移动端卡片视图
.role-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px;

  .role-card {
    transition: all 0.3s ease;
    border-radius: 8px;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px var(--shadow-hover);
    }

    .role-card-content {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .role-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-bottom: 12px;
        border-bottom: 1px solid var(--el-border-color-lighter);

        .header-left {
          display: flex;
          align-items: center;
          gap: 8px;

          .role-id {
            font-size: 12px;
            color: var(--text-muted);
            background-color: var(--bg-input);
            padding: 2px 6px;
            border-radius: 4px;
          }
        }
      }

      .role-info {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .info-row {
          display: flex;
          font-size: 14px;

          .label {
            font-weight: 500;
            color: var(--text-muted);
            margin-right: 8px;
            flex-shrink: 0;
          }

          .value {
            color: var(--text-regular);
            flex: 1;
            word-break: break-all;
          }

          .role-text {
            color: var(--admin-primary);
            font-family: 'Courier New', monospace;
          }

          .time-text {
            font-size: 12px;
            color: var(--text-muted);
          }
        }
      }

      .role-actions {
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
          background-color: var(--action-audit-bg);
          color: var(--action-audit-color);
          border-color: var(--action-audit-border);

          &:hover {
            background-color: var(--action-audit-hover-bg);
            border-color: var(--action-audit-hover-border);
          }
        }

        .delete-button {
          background-color: var(--action-delete-bg);
          color: var(--action-delete-color);
          border-color: var(--action-delete-border);

          &:hover {
            background-color: var(--action-delete-hover-bg);
            border-color: var(--action-delete-hover-border);
          }
        }

        .user-button {
          background-color: var(--action-user-bg);
          color: var(--action-user-color);
          border-color: var(--action-user-border);

          &:hover {
            background-color: var(--action-user-hover-bg);
            border-color: var(--action-user-hover-border);
          }
        }

        .menu-button {
          background-color: var(--action-edit-bg);
          color: var(--action-edit-color);
          border-color: var(--action-edit-border);

          &:hover {
            background-color: var(--action-edit-hover-bg);
            border-color: var(--action-edit-hover-border);
          }
        }
      }
    }
  }
}

// 对话框样式
:deep(.el-dialog) {
  border-radius: 16px;
  width: fit-content;
  max-width: 90%;
}

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

.authorize-dialog-content {
  padding: 10px;

  .role-name {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: var(--text-primary);
  }

  .user-transfer {
    width: 100%;
    display: flex;
    justify-content: center;

    :deep(.el-transfer__panel) {
      width: 280px;
      flex: none;
    }

    :deep(.el-transfer__buttons) {
      display: flex;
      flex-direction: column;
      justify-content: center;
      padding: 0 16px;
    }
  }
}

.authorize-menu-dialog-content {
  padding: 10px;

  .menu-toolbar {
    display: flex;
    gap: 12px;
    margin-bottom: 16px;
    padding-bottom: 12px;
    border-bottom: 1px solid var(--el-border-color-lighter);
  }

  .menu-tree {
    max-height: 400px;
    overflow-y: auto;

    :deep(.el-tree-node__content) {
      height: 36px;
    }

    .tree-node {
      display: flex;
      align-items: center;
      gap: 8px;

      .node-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        border-radius: 4px;
        background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-primary-dark) 100%);
        color: var(--badge-text-inverse);
        font-size: 14px;
      }

      .node-label {
        font-size: 14px;
        color: var(--el-text-color-regular);
      }
    }
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
  .role-table-wrapper {
    max-height: calc(100vh - 180px);
  }
}
</style>
