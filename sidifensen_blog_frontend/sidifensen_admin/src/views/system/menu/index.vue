<template>
  <ManagementCard title="菜单管理" :showTimeFilter="false" :showPagination="true" :modelCurrentPage="currentPage" :modelPageSize="pageSize" :total="total" @search="fetchMenus">
    <!-- 筛选器 -->
    <template #filters>
      <KeywordSearch v-model="searchQuery" placeholder="搜索菜单名称" :debounce="500" @search="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <div v-loading="loading" class="menu-table-wrapper">
        <el-table :data="paginatedMenuList" row-key="id" default-expand-all class="menu-table" style="height: 100%">
          <el-table-column prop="id" label="菜单id" width="120" />
          <el-table-column prop="parentId" label="父菜单id" />
          <el-table-column prop="name" label="菜单名称" />
          <el-table-column prop="path" label="路由路径" />
          <el-table-column prop="component" label="组件路径" />
          <el-table-column prop="icon" label="图标">
            <template #default="{ row }">
              <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="sort" sortable label="排序" />
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
          <el-table-column label="操作" width="330">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button size="small" type="success" @click="handleAddMenu(row)" :icon="Plus" v-if="row.children || row.parentId == 0" class="add-button"> 新增 </el-button>
                <el-button size="small" type="primary" @click="handleEditMenu(row)" :icon="Edit" class="edit-button"> 编辑 </el-button>
                <el-button size="small" type="danger" @click="handleDeleteMenu(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
                <el-button size="small" type="warning" @click="handleAuthorizeRole(row)" :icon="Avatar" class="role-button"> 分配角色 </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <div class="menu-cards" v-loading="loading">
        <div v-for="menu in flatMenuList" :key="menu.id" class="menu-card" :style="{ marginLeft: menu.level * 16 + 'px' }">
          <div class="menu-card-header">
            <div v-if="menu.hasChildren" class="expand-button" @click="toggleMenuExpand(menu.id)">
              <el-icon :class="['expand-icon', { expanded: isMenuExpanded(menu.id) }]">
                <ArrowRight />
              </el-icon>
            </div>
            <div class="menu-icon-wrapper">
              <el-icon v-if="menu.icon" class="menu-icon"><component :is="menu.icon" /></el-icon>
              <el-icon v-else class="menu-icon placeholder"><Document /></el-icon>
            </div>
            <div class="menu-main-info">
              <div class="menu-id-badge">ID: {{ menu.id }}</div>
              <div class="menu-name">{{ menu.name }}</div>
              <div v-if="menu.path" class="menu-path">{{ menu.path }}</div>
            </div>
          </div>
          <div class="menu-details">
            <div v-if="menu.parentId !== undefined" class="detail-item">
              <span class="label">父菜单ID:</span>
              <span class="value">{{ menu.parentId === 0 ? '无' : menu.parentId }}</span>
            </div>
            <div v-if="menu.component" class="detail-item">
              <span class="label">组件路径:</span>
              <span class="value">{{ menu.component }}</span>
            </div>
            <div class="detail-item">
              <span class="label">排序:</span>
              <span class="value">{{ menu.sort }}</span>
            </div>
            <div class="detail-item">
              <span class="label">创建时间:</span>
              <span class="value">{{ menu.createTime }}</span>
            </div>
            <div class="detail-item">
              <span class="label">更新时间:</span>
              <span class="value">{{ menu.updateTime }}</span>
            </div>
          </div>
          <div class="menu-status-section">
            <el-switch
              v-model="menu.status"
              size="large"
              active-color="var(--admin-primary)"
              inactive-color="#cccccc"
              active-text="正常"
              inactive-text="禁用"
              :active-value="0"
              :inactive-value="1"
              inline-prompt
              :loading="switchLoading"
              :before-change="() => handleStatusChange(menu.id, menu.status === 0 ? 1 : 0)"
            />
          </div>
          <div class="menu-actions">
            <el-button v-if="menu.hasChildren || menu.parentId == 0" text bg type="success" size="small" :icon="Plus" @click="handleAddMenu(menu)">新增</el-button>
            <el-button text bg type="primary" size="small" :icon="Edit" @click="handleEditMenu(menu)">编辑</el-button>
            <el-button text bg type="danger" size="small" :icon="Delete" @click="handleDeleteMenu(menu.id)">删除</el-button>
            <el-button text bg type="warning" size="small" :icon="Avatar" @click="handleAuthorizeRole(menu)">角色</el-button>
          </div>
        </div>
      </div>
    </template>
  </ManagementCard>

  <!-- 新增/编辑菜单对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose">
    <el-form ref="menuFormRef" :model="menuForm" :rules="rules" class="menu-form">
      <el-form-item prop="name" label="菜单名称">
        <el-input v-model="menuForm.name" placeholder="请输入菜单名称" />
      </el-form-item>
      <el-form-item prop="path" label="路由路径">
        <el-input v-model="menuForm.path" placeholder="请输入路由路径" />
      </el-form-item>
      <el-form-item prop="component" label="组件路径">
        <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
      </el-form-item>
      <el-form-item prop="icon" label="图标">
        <el-select v-model="menuForm.icon" placeholder="请选择图标" filterable clearable>
          <el-option v-for="icon in icons" :key="icon" :label="icon" :value="icon">
            <div style="display: flex; align-items: center">
              <el-icon style="margin-right: 10px"><component :is="icon" /></el-icon>
              <span>{{ icon }}</span>
            </div>
          </el-option>
          <template #label>
            <div style="display: flex; align-items: center">
              <el-icon style="margin-right: 10px"><component :is="menuForm.icon" /></el-icon>
              <span>{{ menuForm.icon }}</span>
            </div>
          </template>
        </el-select>
      </el-form-item>
      <el-form-item prop="parentId" label="父菜单">
        <CommonSelect v-model="menuForm.parentId" :options="[{ id: 0, name: '无父菜单' }, ...allMenus]" option-label="name" option-value="id" placeholder="请选择父菜单" width="100%" />
      </el-form-item>
      <el-form-item prop="sort"> 排序号 &nbsp<el-input-number v-model="menuForm.sort" :min="0" :max="999" placeholder="请输入排序号" /> </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 分配角色对话框 -->
  <el-dialog v-model="authorizeDialogVisible" title="菜单分配角色" :before-close="handleAuthorizeDialogClose" class="authorize-dialog">
    <div v-loading="authorizeLoading" class="authorize-dialog-content">
      <p class="menu-name">当前菜单: {{ currentMenu?.name }}</p>
      <template v-if="!authorizeLoading">
        <el-form ref="authorizeFormRef" class="authorize-form">
          <el-form-item label="选择角色">
            <el-checkbox-group v-model="selectedRoles" class="role-checkbox-group">
              <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id">{{ role.name }}</el-checkbox>
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
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from 'vue'
import { Plus, Edit, Delete, Avatar, Document, ArrowRight } from '@element-plus/icons-vue'
import { getAllMenuList, getMenuPage, addMenu, updateMenu, deleteMenu, queryMenuPage } from '@/api/menu'
import { addRoleMenu, getRolesByMenu } from '@/api/role-menu'
import { getRoleList } from '@/api/role'
import { icons } from '@/utils/Icon'
import { formatMenu } from '@/utils/Menu'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import CommonSelect from '@/components/search/CommonSelect.vue'

// 搜索
const searchQuery = ref('')

// 移动端检测
const isMobileView = ref(false)
const expandedMenus = ref(new Set())

const toggleMenuExpand = (menuId) => {
  if (expandedMenus.value.has(menuId)) {
    expandedMenus.value.delete(menuId)
  } else {
    expandedMenus.value.add(menuId)
  }
  expandedMenus.value = new Set(expandedMenus.value)
}

const isMenuExpanded = (menuId) => {
  return expandedMenus.value.has(menuId)
}

// 菜单列表数据
const menuList = ref([])
const paginatedMenuList = ref([])
const parentMenuOptions = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')

const menuFormRef = ref(null)
const authorizeFormRef = ref(null)
const menuForm = ref({
  id: null,
  name: '',
  path: '',
  component: '',
  icon: '',
  parentId: 0,
  sort: 0,
  status: 0,
})

const rules = {
  parentId: [{ required: true, message: '请选择父菜单', trigger: 'change' }],
  name: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  path: [{ required: true, message: '请输入路由路径', trigger: 'blur' }],
  component: [{ required: true, message: '请输入组件路径', trigger: 'blur' }],
  sort: [
    { required: true, message: '请输入排序号', trigger: 'blur' },
    { type: 'number', message: '排序号必须是数字', trigger: 'blur' },
  ],
}

// 当前页码
const currentPage = ref(1)
const pageSize = ref(10)

const hasSearchConditions = () => !!searchQuery.value.trim()

const applyPageData = (pageData) => {
  menuList.value = formatMenu(pageData?.data || [])
  paginatedMenuList.value = menuList.value
  total.value = Number(pageData?.total || 0)
}

const fetchMenus = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await queryMenuPage({
        name: searchQuery.value,
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    } else {
      const res = await getMenuPage({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索菜单失败' : '获取菜单列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = async () => {
  currentPage.value = 1
  await fetchMenus()
}

const handleReset = () => {
  searchQuery.value = ''
  handleSearch()
}

const allMenus = ref([])

const getAllMenus = async () => {
  try {
    const res = await getAllMenuList()
    allMenus.value = res.data
  } catch (error) {
    ElMessage.error('获取所有菜单失败')
  }
}

const handleAddMenu = async (row) => {
  await getAllMenus()

  if (row && row.id) {
    dialogTitle.value = '新增菜单'
    menuForm.value = {
      id: null,
      name: '',
      path: '',
      component: '',
      icon: '',
      parentId: row.id,
      sort: 0,
      status: 0,
    }
    dialogVisible.value = true
  } else {
    const topLevelMenus = allMenus.value.filter((menu) => menu.parentId === 0)
    const maxSort = topLevelMenus.length > 0 ? topLevelMenus.reduce((max, menu) => (menu.sort > max ? menu.sort : max), 0) : 0

    dialogTitle.value = '新增菜单'
    menuForm.value = {
      id: null,
      name: '',
      path: '',
      component: '',
      icon: '',
      parentId: 0,
      sort: maxSort + 1,
      status: 0,
    }
    dialogVisible.value = true
  }
}

const handleEditMenu = (row) => {
  getAllMenus()
  dialogTitle.value = '编辑菜单'
  menuForm.value = { ...row }
  dialogVisible.value = true
}

const handleDeleteMenu = (id) => {
  ElMessageBox.confirm('确定要删除该菜单吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true
      try {
        await deleteMenu(id)
        ElMessage.success('删除成功')
        fetchMenus()
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
    updateMenu({ id, status })
      .then(() => {
        ElMessage.success('状态更新成功')
        const menu = menuList.value.find((item) => item.id === id)
        if (menu) {
          menu.status = status
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
  menuFormRef.value.validate(async (valid) => {
    if (!valid) return
    try {
      if (menuForm.value.id) {
        await updateMenu(menuForm.value)
        ElMessage.success('编辑菜单成功')
      } else {
        await addMenu(menuForm.value)
        ElMessage.success('新增菜单成功')
      }
      dialogVisible.value = false
      fetchMenus()
    } catch (error) {
      ElMessage.error(menuForm.value.id ? '编辑菜单失败' : '新增菜单失败')
      handleDialogClose()
    }
  })
}

const handleDialogClose = () => {
  menuFormRef.value.resetFields()
  dialogVisible.value = false
}

// 授权角色弹窗
const authorizeDialogVisible = ref(false)
const currentMenu = ref(null)
const selectedRoles = ref([])
const allRoles = ref([])
const authorizeLoading = ref(false)

const handleAuthorizeRole = async (row) => {
  currentMenu.value = row
  selectedRoles.value = []

  authorizeDialogVisible.value = true
  authorizeLoading.value = true

  try {
    const [roleRes, menuRolesRes] = await Promise.all([getRoleList(), getRolesByMenu(row.id)])
    allRoles.value = roleRes.data
    selectedRoles.value = menuRolesRes.data.map((item) => item.id)
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    authorizeLoading.value = false
  }
}

const handleAuthorizeSubmit = async () => {
  try {
    await addRoleMenu({
      menuId: currentMenu.value.id,
      roleIds: selectedRoles.value,
    })
    ElMessage.success(`已为菜单 ${currentMenu.value.name} 授权角色`)
  } catch (error) {
    ElMessage.error(`为菜单 ${currentMenu.value.name} 授权角色失败`)
  } finally {
    authorizeDialogVisible.value = false
    selectedRoles.value = []
  }
}

const handleAuthorizeDialogClose = () => {
  authorizeDialogVisible.value = false
  selectedRoles.value = []
}

// 扁平化的菜单列表（用于移动端显示）
const flatMenuList = computed(() => {
  const flattenMenus = (menus, level = 0, result = []) => {
    menus.forEach((menu) => {
      const flatMenu = { ...menu, level, hasChildren: menu.children && menu.children.length > 0 }
      result.push(flatMenu)
      if (flatMenu.hasChildren && isMenuExpanded(menu.id)) {
        flattenMenus(menu.children, level + 1, result)
      }
    })
    return result
  }
  return flattenMenus(paginatedMenuList.value)
})

const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768
}

onMounted(() => {
  fetchMenus()
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
// 菜单表格包装器
.menu-table-wrapper {
  flex: 1;
  overflow: auto;
}

// 菜单表格
.menu-table {
  flex: 1;
  display: flex;
  flex-direction: column;
  max-height: calc(100vh - 220px);

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

    .add-button {
      background-color: #d1fae5;
      color: #059669;
      border-color: #d1fae5;
      border-radius: 6px;

      &:hover {
        background-color: #a7f3d0;
        border-color: #a7f3d0;
      }
    }

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
.menu-cards {
  display: flex;
  flex-direction: column;
  gap: 0;
  padding: 10px;

  .menu-card {
    background: var(--el-bg-color);
    border-radius: 8px;
    border: 1px solid var(--el-border-color-lighter);
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
    margin-bottom: 8px;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
    }

    .menu-card-header {
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background-color: var(--el-bg-color-page);
      border-bottom: 1px solid var(--el-border-color-lighter);

      .expand-button {
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 32px;
        height: 32px;
        border-radius: 6px;
        background-color: rgba(255, 255, 255, 0.8);
        flex-shrink: 0;

        &:hover {
          background-color: var(--admin-primary-lighter);
        }

        .expand-icon {
          font-size: 18px;
          color: #606266;
          transition: transform 0.3s ease;

          &.expanded {
            transform: rotate(90deg);
          }
        }
      }

      .menu-icon-wrapper {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 48px;
        height: 48px;
        border-radius: 12px;
        background: linear-gradient(135deg, var(--admin-primary) 0%, var(--admin-primary-dark) 100%);

        .menu-icon {
          font-size: 24px;
          color: white;

          &.placeholder {
            opacity: 0.6;
          }
        }
      }

      .menu-main-info {
        flex: 1;
        min-width: 0;

        .menu-id-badge {
          display: inline-block;
          padding: 2px 8px;
          background-color: #e3f2fd;
          color: #1976d2;
          border-radius: 12px;
          font-size: 10px;
          font-weight: 600;
          margin-bottom: 4px;
        }

        .menu-name {
          font-size: 16px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin-bottom: 4px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .menu-path {
          font-size: 12px;
          color: var(--el-text-color-secondary);
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }
      }
    }

    .menu-details {
      padding: 12px 16px;
      display: flex;
      flex-direction: column;
      gap: 8px;
      background-color: var(--el-bg-color);

      .detail-item {
        display: flex;
        align-items: flex-start;
        font-size: 12px;
        line-height: 1.5;

        .label {
          flex-shrink: 0;
          width: 80px;
          color: var(--el-text-color-secondary);
          font-weight: 500;
        }

        .value {
          flex: 1;
          color: var(--el-text-color-primary);
          word-break: break-all;
        }
      }
    }

    .menu-status-section {
      display: flex;
      justify-content: center;
      border-top: 1px solid var(--el-border-color-lighter);
      border-bottom: 1px solid var(--el-border-color-lighter);
      background-color: var(--el-bg-color);
    }

    .menu-actions {
      display: flex;
      gap: 6px;
      padding: 12px 16px;
      justify-content: space-between;
      background-color: var(--el-bg-color);

      .el-button {
        font-size: 12px;
        padding: 6px 10px;
        height: auto;
        border-radius: 4px;
        flex: 1;
        min-width: 0;
      }
    }
  }
}

// 对话框样式
:deep(.el-dialog) {
  border-radius: 16px;
  width: 500px;

  @media screen and (max-width: 767px) {
    width: 90%;
  }
}

.menu-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
}

.authorize-dialog-content {
  padding: 10px;

  .menu-name {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 16px;
    color: #1e293b;
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

// 响应式设计
@media screen and (max-width: 768px) {
  .menu-table-wrapper {
    max-height: calc(100vh - 200px);
  }
}
</style>
