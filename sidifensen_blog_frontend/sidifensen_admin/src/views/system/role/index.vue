<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">角色管理</h2>
        <div class="card-actions">
          <el-input
            v-model="searchQuery"
            placeholder="搜索角色名称"
            :prefix-icon="Search"
            size="small"
            class="search-input"
          />
          <el-button
            type="primary"
            size="small"
            @click="handleAddRole"
            :icon="Plus"
            class="add-button"
          >
            新增角色
          </el-button>
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedRoleList" class="table" style="height: 100%">
          <el-table-column prop="id" label="角色id" width="70" />
          <el-table-column prop="role" label="角色标识" />
          <el-table-column prop="name" label="角色名称" />
          <el-table-column prop="description" label="角色描述" />
          <el-table-column prop="status" label="状态">
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
                <el-button
                  type="primary"
                  size="small"
                  @click="handleEditRole(row)"
                  :icon="Edit"
                  class="edit-button"
                >
                  编辑
                </el-button>
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteRole(row.id)"
                  :icon="Delete"
                  class="delete-button"
                >
                  删除
                </el-button>
                <el-button
                  size="small"
                  type="warning"
                  @click="handleAuthorizeUser(row)"
                  :icon="User"
                  class="user-button"
                >
                  分配用户
                </el-button>
                <el-button
                  size="small"
                  type="success"
                  @click="handleAuthorizeMenu(row)"
                  :icon="Key"
                  class="menu-button"
                >
                  分配权限
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div v-loading="loading" class="role-cards">
          <el-card v-for="role in paginatedRoleList" :key="role.id" class="role-card">
            <div class="role-card-content">
              <!-- 卡片头部 -->
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

              <!-- 角色信息 -->
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

              <!-- 操作按钮 -->
              <div class="role-actions">
                <el-button
                  type="primary"
                  size="small"
                  @click="handleEditRole(role)"
                  :icon="Edit"
                  class="edit-button"
                  >编辑</el-button
                >
                <el-button
                  type="danger"
                  size="small"
                  @click="handleDeleteRole(role.id)"
                  :icon="Delete"
                  class="delete-button"
                  >删除</el-button
                >
                <el-button
                  size="small"
                  type="warning"
                  @click="handleAuthorizeUser(role)"
                  :icon="User"
                  class="user-button"
                  >分配用户</el-button
                >
                <el-button
                  size="small"
                  type="success"
                  @click="handleAuthorizeMenu(role)"
                  :icon="Key"
                  class="menu-button"
                  >分配权限</el-button
                >
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <Pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

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
    <el-dialog
      v-model="authorizeDialogVisible"
      title="角色分配用户"
      :before-close="handleAuthorizeDialogClose"
      class="authorize-dialog"
    >
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
    <el-dialog
      v-model="authorizeMenuDialogVisible"
      :title="'分配 ' + currentMenuRole?.name + ' 的菜单权限'"
      :before-close="handleAuthorizeMenuDialogClose"
      width="600px"
    >
      <div class="authorize-menu-dialog-content">
        <!-- 操作按钮区域 -->
        <div class="menu-toolbar">
          <el-button size="small" @click="handleExpandAll(false)">全部收起</el-button>
          <el-button size="small" type="primary" @click="handleExpandAll(true)">全部展开</el-button>
        </div>
        <!-- 菜单树形控件 -->
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
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, User, Key } from '@element-plus/icons-vue'
import {
  getRoleList,
  getRolePage,
  addRole,
  updateRole,
  deleteRole,
  queryRolePage,
} from '@/api/role'
import { addUser, getUsersByRole } from '@/api/user-role'
import { getUserList } from '@/api/user'
import { getAllMenuList } from '@/api/menu'
import { getMenusByRole, assignMenus } from '@/api/role-menu'
import Pagination from '@/components/data/Pagination.vue'

// 搜索查询
const searchQuery = ref('')
// 角色列表数据
const roleList = ref([])
// 分页后的角色列表
const paginatedRoleList = ref([])
// 加载状态
const loading = ref(false)
// 总条数
const total = ref(0)
// 对话框可见性
const dialogVisible = ref(false)
// 对话框标题
const dialogTitle = ref('新增角色')

// 表单引用
const roleFormRef = ref(null)
// 表单数据
const roleForm = ref({
  id: null,
  name: '',
  role: '',
  description: '',
  status: 0,
})
// 表单验证规则
const rules = {
  role: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
  name: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  description: [{ required: true, message: '请输入角色描述', trigger: 'blur' }],
}

// 获取角色列表
const getRoles = async () => {
  currentPage.value = 1
  await fetchRoles()
}

// 初始化
onMounted(() => {
  getRoles()
  handleResize()
  window.addEventListener('resize', handleResize)
})

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})

// 当前页码
const currentPage = ref(1)
// 每页条数
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

// 更新分页数据
const updatePaginatedRoleList = () => {
  paginatedRoleList.value = roleList.value
}

// 处理分页大小变化
const handleSizeChange = async (size) => {
  pageSize.value = size
  currentPage.value = 1
  await fetchRoles()
}

// 处理当前页码变化
const handleCurrentChange = async (current) => {
  currentPage.value = current
  await fetchRoles()
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchRoles()
}

// 监听搜索输入变化
const searchTimeout = ref(null)
watch(searchQuery, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value)
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch()
  }, 100)
})

// 处理添加角色
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

// 处理编辑角色
const handleEditRole = (row) => {
  dialogTitle.value = '编辑角色'
  // 深拷贝行数据
  roleForm.value = { ...row }
  dialogVisible.value = true
}

// 处理删除角色
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
        getRoles()
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

const switchLoading = ref(false)
// 处理状态变更
const handleStatusChange = async (id, status) => {
  return new Promise((resolve, reject) => {
    switchLoading.value = true
    updateRole({ id, status })
      .then(() => {
        ElMessage.success('状态更新成功')
        // 手动更新本地数据状态
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

// 处理表单提交
const handleSubmit = () => {
  roleFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    try {
      if (roleForm.value.id) {
        // 编辑角色
        await updateRole(roleForm.value)
        ElMessage.success('编辑角色成功')
      } else {
        // 新增角色
        await addRole(roleForm.value)
        ElMessage.success('新增角色成功')
      }
      dialogVisible.value = false
      getRoles()
    } catch (error) {
      ElMessage.error(roleForm.value.id ? '编辑角色失败' : '新增角色失败')
      handleDialogClose()
    }
  })
}

// 处理对话框关闭
const handleDialogClose = () => {
  roleFormRef.value.resetFields()
  dialogVisible.value = false
}

// 授权角色弹窗
const authorizeDialogVisible = ref(false)
const authorizeLoading = ref(false)
// 当前角色
const currentRole = ref(null)
// 选择的用户
const selectedUser = ref([])
// 所有用户
const allUser = ref([])

// 穿梭框数据格式
const transferUserData = computed(() => {
  return allUser.value.map((user) => ({
    key: user.id,
    label: user.username,
  }))
})

// 穿梭框筛选方法
const filterMethod = (query, item) => {
  return item.label.toLowerCase().includes(query.toLowerCase())
}

// 处理授权角色
const handleAuthorizeUser = async (row) => {
  currentRole.value = row
  selectedUser.value = []

  // 先打开弹窗并显示 loading
  authorizeDialogVisible.value = true
  authorizeLoading.value = true

  try {
    // 并行加载用户列表和已分配用户
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

// 处理授权提交
const handleAuthorizeSubmit = async () => {
  try {
    await addUser({
      roleId: currentRole.value.id,
      userIds: selectedUser.value,
    })
    ElMessage.success(`已为角色 ${currentRole.value.name} 分配用户`)
  } catch (error) {
    ElMessage.error(`为角色 ${currentRole.value.name} 分配用户失败`)
    console.error('分配用户失败:', error)
  } finally {
    authorizeDialogVisible.value = false
    authorizeLoading.value = false
    selectedUser.value = []
  }
}

// 处理授权对话框关闭
const handleAuthorizeDialogClose = () => {
  authorizeDialogVisible.value = false
  authorizeLoading.value = false
  selectedUser.value = []
}

// 分配权限弹窗
const authorizeMenuDialogVisible = ref(false)
// 菜单树形控件引用
const menuTreeRef = ref(null)
// 当前角色
const currentMenuRole = ref(null)
// 菜单树形数据
const menuTreeData = ref([])
// 菜单树形配置
const menuTreeProps = {
  children: 'children',
  label: 'name',
}
// 选中的菜单ID列表
const selectedMenuIds = ref([])
// 默认展开的菜单ID列表
const defaultExpandedKeys = ref([])

// 处理分配权限
const handleAuthorizeMenu = async (row) => {
  currentMenuRole.value = row
  selectedMenuIds.value = []
  defaultExpandedKeys.value = []

  // 获取所有菜单
  const res = await getAllMenuList()
  menuTreeData.value = buildMenuTree(res.data)

  // 默认展开所有节点
  defaultExpandedKeys.value = res.data.map((menu) => menu.id)

  // 获取当前角色已有的菜单权限
  try {
    const menuRes = await getMenusByRole(row.id)
    selectedMenuIds.value = menuRes.data || []
  } catch (error) {
    console.error('获取角色菜单权限失败:', error)
  }

  // 打开弹窗
  authorizeMenuDialogVisible.value = true
}

// 构建菜单树形结构
const buildMenuTree = (menus) => {
  const menuMap = new Map()
  const result = []

  // 将菜单转换为 map 结构
  menus.forEach((menu) => {
    menuMap.set(menu.id, { ...menu, children: [] })
  })

  // 构建树形结构
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

// 处理展开/收起全部
const handleExpandAll = (expand) => {
  const tree = menuTreeRef.value
  if (tree) {
    const nodes = tree.store.nodesMap
    Object.values(nodes).forEach((node) => {
      node.expanded = expand
    })
  }
}

// 处理节点点击
const handleNodeClick = (data) => {
  // 点击节点时不进行选中操作，仅展开/收起
}

// 处理分配权限提交
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
    console.error('分配菜单权限失败:', error)
  } finally {
    authorizeMenuDialogVisible.value = false
  }
}

// 处理分配权限对话框关闭
const handleAuthorizeMenuDialogClose = () => {
  authorizeMenuDialogVisible.value = false
  selectedMenuIds.value = []
  menuTreeData.value = []
}

// 监听弹窗打开状态，等树渲染完成后设置选中状态
watch([authorizeMenuDialogVisible, menuTreeData], async ([isOpen, data]) => {
  if (isOpen && data.length > 0) {
    await nextTick()
    await nextTick() // 确保树组件完全渲染
    if (menuTreeRef.value && selectedMenuIds.value.length > 0) {
      menuTreeRef.value.setCheckedKeys(selectedMenuIds.value)
    }
  }
})

// 移动端检测
const isMobileView = ref(false)

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768
}
</script>

<style lang="scss" scoped>
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  .card {
    height: 100%;
    padding: 20px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);

      .card-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;

        &::before {
          content: '';
          display: inline-block;
          width: 4px;
          height: 20px;
          background-color: var(--admin-primary);
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;
        gap: 12px;

        .search-input {
          width: 240px;
          border-radius: 8px;

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px var(--admin-primary-light);
              border-color: var(--admin-primary);
            }
          }
        }

        .add-button {
          border-radius: 8px;
          background: linear-gradient(
            135deg,
            var(--admin-primary) 0%,
            var(--admin-primary-dark) 100%
          );
          border: none;
          transition: all 0.3s ease;

          &:hover {
            background: linear-gradient(
              135deg,
              var(--admin-primary-dark) 0%,
              var(--admin-primary-active) 100%
            );
            transform: translateY(-2px);
            box-shadow: 0 4px 12px var(--admin-primary-light);
          }
        }
      }
    }

    // 桌面端表格视图
    .desktop-view {
      flex: 1;
      display: flex;
      flex-direction: column;
      padding-bottom: 60px;
    }

    //表格
    .table {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      max-height: calc(100vh - 220px); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */

      :deep(.el-table__header-wrapper) {
        background-color: var(--el-bg-color);
        th {
          font-weight: 600;
          color: #475569;
        }
      }

      :deep(.el-table__body-wrapper) {
        tr {
          td {
            color: #64748b;
            padding: 12px 0;
          }
        }
      }

      :deep(.el-table__fixed-right) {
        box-shadow: -3px 0 10px rgba(0, 0, 0, 0.05);
      }

      .table-actions {
        height: 30px;
        display: flex;
        align-items: center;
        gap: 8px; // 设置按钮之间的间距
        // flex-wrap: wrap; // 允许按钮在空间不足时换行
        @media screen and (max-width: 480px) {
          gap: 4px;
        }
        .edit-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            background-color: #bae6fd;
            border-color: #bae6fd;
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
          }
        }
        .delete-button {
          margin-left: 0 !important; // 覆盖可能的默认边距
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            background-color: #fecaca;
            border-color: #fecaca;
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
          }
        }
        .user-button {
          margin-left: 0;
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            background-color: #fde68a;
            border-color: #fde68a;
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
          }
        }

        .menu-button {
          margin-left: 0;
          background-color: #d1fae5;
          color: #059669;
          border-color: #d1fae5;
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            background-color: #a7f3d0;
            border-color: #a7f3d0;
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3);
          }
        }
      }
    }

    // 移动端卡片视图
    .mobile-view {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      padding-bottom: 60px;
      overflow-y: auto;

      // 角色卡片列表容器
      .role-cards {
        display: flex;
        flex-direction: column;
        gap: 12px;
        padding: 10px;

        // 单个角色卡片
        .role-card {
          transition: all 0.3s ease;
          border-radius: 8px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          // 角色卡片内容容器
          .role-card-content {
            display: flex;
            flex-direction: column;
            gap: 12px;

            // 卡片头部
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
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }
              }
            }

            // 角色信息区域
            .role-info {
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

                .role-text {
                  color: var(--admin-primary);
                  font-family: 'Courier New', monospace;
                }

                .time-text {
                  font-size: 12px;
                  color: #999;
                }
              }
            }

            // 操作按钮区域
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

              .user-button {
                background-color: #fef3c7;
                color: #d97706;
                border-color: #fef3c7;

                &:hover {
                  background-color: #fde68a;
                  border-color: #fde68a;
                }
              }

              .menu-button {
                background-color: #d1fae5;
                color: #059669;
                border-color: #d1fae5;

                &:hover {
                  background-color: #a7f3d0;
                  border-color: #a7f3d0;
                }
              }
            }
          }
        }
      }
    }
  }

  :deep(.el-dialog) {
    border-radius: 16px;
    width: fit-content;
    max-width: 90%;
  }

  // 新增/编辑菜单对话框
  .editForm {
    :deep(.el-form-item) {
      margin-bottom: 20px;
    }
    :deep(.el-input-number),
    :deep(.el-input__wrapper),
    :deep(.el-select__wrapper) {
      border-radius: 16px;
      transition: all 0.3s ease;
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

  // 分配权限对话框样式
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
          background: linear-gradient(
            135deg,
            var(--admin-primary) 0%,
            var(--admin-primary-dark) 100%
          );
          color: #fff;
          font-size: 14px;
        }

        .node-label {
          font-size: 14px;
          color: var(--el-text-color-regular);
        }
      }
    }
  }
}

// 响应式设计
@media screen and (max-width: 1200px) {
  .management-container {
    .card {
      .card-header {
        .card-actions {
          .search-input {
            width: 180px;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 992px) {
  .management-container {
    .card {
      .card-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;

        .card-actions {
          width: 100%;
          justify-content: space-between;

          .search-input {
            flex: 1;
            min-width: 0;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .management-container {
    .card {
      padding: 2px;
      .card-header {
        padding: 6px;

        .card-title {
          font-size: 16px;
        }

        .card-actions {
          // flex-direction: column;
          gap: 8px;

          .search-input {
            width: 100%;
          }

          .add-button {
            // width: 100%;
          }
        }
      }

      .table {
        margin-top: 0;
        max-height: calc(
          100vh - 180px
        ); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */
        :deep(.el-table) {
          display: block;
          width: 100%;
          overflow-x: auto;
        }
      }
    }
  }
}

// 响应式样式
@media screen and (max-width: 768px) {
  .editForm {
    :deep(.el-form-item) {
      margin-bottom: 15px;
    }

    :deep(.el-input),
    :deep(.el-select),
    :deep(.el-input-number) {
      width: 100% !important;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 10px;

    :deep(.el-button) {
      flex: 1;
      max-width: 120px;
    }
  }
}

@media screen and (max-width: 480px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;

    .card-actions {
      width: 100%;
      // flex-direction: column;

      .search-input {
        width: 100% !important;
      }

      .add-button {
        // width: 100%;
      }
    }
  }
}
</style>
