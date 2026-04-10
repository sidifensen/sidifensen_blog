<template>
  <ManagementCard title="用户管理" :showTimeFilter="false" :showPagination="true" :modelCurrentPage="currentPage" :modelPageSize="pageSize" :total="total" @search="fetchUsers">
    <!-- 筛选器 -->
    <template #filters>
      <KeywordSearch v-model="searchUsername" placeholder="搜索用户名称" label="用户名" :debounce="500" @search="handleSearch" />
      <KeywordSearch v-model="searchEmail" placeholder="搜索用户邮箱" label="邮箱" width="140px" :debounce="0" @search="handleSearch" />
      <ExamineStatusSelect v-model="searchStatus" :options="statusOptions" width="120px" labelText="状态" />
      <TimeRangePicker v-model:startTime="searchCreateTimeStart" v-model:endTime="searchCreateTimeEnd" @change="handleSearch" />
      <SearchButtons @search="handleSearch" @reset="handleReset" />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <div v-loading="loading" class="user-table-wrapper">
        <el-table :data="paginatedUserList" class="table" style="height: 100%">
          <el-table-column prop="id" label="用户id" width="70" />
          <el-table-column prop="avatar" label="用户头像">
            <template #default="{ row }">
              <div style="display: flex; align-items: center">
                <el-image preview-teleported :src="row.avatar" style="width: 40px; height: 40px" :preview-src-list="[row.avatar]" fit="fill" />
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="用户名称" />
          <el-table-column prop="nickname" label="用户昵称" />
          <el-table-column prop="email" label="用户邮箱" width="170" />
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
          <el-table-column prop="loginType" label="登录方式" width="110">
            <template #default="{ row }">
              <el-tag>
                {{ row.loginType === 0 ? '用户名/邮箱' : row.loginType === 1 ? 'gitee' : row.loginType === 2 ? 'github' : row.loginType === 3 ? 'QQ' : '未知登录方式' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="loginAddress" label="登录地址" />
          <el-table-column prop="loginTime" label="最近登录时间" sortable width="135" />
          <el-table-column prop="createTime" label="创建时间" sortable width="120" />
          <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
          <el-table-column label="操作" width="280">
            <template #default="{ row }">
              <TableActions
                :show-detail="true"
                :show-edit="true"
                :show-delete="true"
                :detail-text="'详情'"
                @detail="handleDetailUser(row.id)"
                @edit="handleEditUser(row)"
                @delete="handleDeleteUser(row.id)"
              >
                <el-button size="small" type="warning" class="action-btn role-btn" @click="handleAuthorizeRole(row)" :icon="Avatar">添加角色</el-button>
              </TableActions>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <div v-loading="loading" class="user-cards">
        <el-card v-for="user in paginatedUserList" :key="user.id" class="user-card">
          <div class="user-card-content">
            <!-- 卡片头部 -->
            <div class="user-header">
              <div class="header-left">
                <el-avatar :src="user.avatar" :size="50" />
                <div class="user-basic">
                  <div class="user-name">{{ user.nickname || user.username }}</div>
                  <div class="user-id">#{{ user.id }}</div>
                </div>
              </div>
              <div class="header-right">
                <el-tag :type="user.status === 0 ? 'success' : 'danger'">
                  {{ user.status === 0 ? '正常' : '禁用' }}
                </el-tag>
                <el-switch
                  v-model="user.status"
                  size="small"
                  active-color="var(--admin-primary)"
                  inactive-color="#cccccc"
                  :active-value="0"
                  :inactive-value="1"
                  :loading="switchLoading"
                  :before-change="() => handleStatusChange(user.id, user.status === 0 ? 1 : 0)"
                />
              </div>
            </div>

            <!-- 用户信息 -->
            <div class="user-info">
              <div class="info-row">
                <span class="label">用户名:</span>
                <span class="value">{{ user.username }}</span>
              </div>
              <div class="info-row">
                <span class="label">邮箱:</span>
                <span class="value">{{ user.email }}</span>
              </div>
              <div class="info-row">
                <span class="label">登录方式:</span>
                <el-tag size="small">
                  {{ user.loginType === 0 ? '用户名/邮箱' : user.loginType === 1 ? 'gitee' : user.loginType === 2 ? 'github' : user.loginType === 3 ? 'QQ' : '未知' }}
                </el-tag>
              </div>
              <div class="info-row">
                <span class="label">登录地址:</span>
                <span class="value">{{ user.loginAddress || '-' }}</span>
              </div>
              <div class="info-row">
                <span class="label">最近登录:</span>
                <span class="value time-text">{{ user.loginTime }}</span>
              </div>
              <div class="info-row">
                <span class="label">创建时间:</span>
                <span class="value time-text">{{ user.createTime }}</span>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="user-actions">
              <el-button type="info" size="small" @click="handleDetailUser(user.id)" :icon="InfoFilled" class="detail-button">详情</el-button>
              <el-button type="primary" size="small" @click="handleEditUser(user)" :icon="Edit" class="edit-button">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDeleteUser(user.id)" :icon="Delete" class="delete-button">删除</el-button>
              <el-button size="small" type="warning" @click="handleAuthorizeRole(user)" :icon="Avatar" class="role-button">添加角色</el-button>
            </div>
          </div>
        </el-card>
      </div>
    </template>
  </ManagementCard>

  <!-- 新增/编辑权限对话框 -->
  <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose">
    <el-form ref="userFormRef" :model="userForm" :rules="rules" class="editForm">
      <el-form-item prop="username" label="用户名称">
        <el-input v-model="userForm.username" placeholder="请输入用户名称" />
      </el-form-item>
      <el-form-item prop="email" label="用户邮箱">
        <el-input v-model="userForm.email" placeholder="请输入用户邮箱" />
      </el-form-item>
      <el-form-item prop="nickname" label="用户昵称">
        <el-input v-model="userForm.nickname" placeholder="请输入用户昵称" maxlength="20" show-word-limit />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确认</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 添加角色弹窗对话框 -->
  <el-dialog v-model="authorizeDialogVisible" title="用户添加角色" :before-close="handleAuthorizeDialogClose" class="authorize-dialog">
    <div v-loading="authorizeLoading" class="authorize-dialog-content">
      <p class="role-name">当前用户: {{ currentUser?.username }}</p>
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

  <!-- 用户详情 -->
  <el-dialog v-model="userDetailDialogVisible" title="用户详情" class="user-detail">
    <el-card v-if="userDetail" class="user-detail-card">
      <!-- 用户基本信息 -->
      <h3>基本信息</h3>
      <el-avatar :src="userDetail.avatar" size="80" class="user-avatar" />
      <p>昵称:{{ userDetail.nickname }}</p>
      <p>用户名:{{ userDetail.username }}</p>
      <div class="info-grid">
        <div class="info-item">
          <span class="label">用户ID:</span>
          <span class="value">{{ userDetail.id }}</span>
        </div>
        <div class="info-item">
          <span class="label">邮箱:</span>
          <span class="value">{{ userDetail.email }}</span>
        </div>
        <div class="info-item">
          <span class="label">性别:</span>
          <span class="value">{{ userDetail.sex === 0 ? '男' : userDetail.sex === 1 ? '女' : '未知' }}</span>
        </div>
        <div class="info-item">
          <span class="label">状态:</span>
          <span class="value">
            <el-tag :type="userDetail.status === 0 ? 'success' : 'danger'">{{ userDetail.status === 0 ? '启用' : '禁用' }}</el-tag>
          </span>
        </div>
        <div class="info-item">
          <span class="label">简介:</span>
          <span class="value">{{ userDetail.introduction || '暂无简介' }}</span>
        </div>
      </div>

      <!-- 登录信息 -->
      <h3>登录信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="label">登录类型:</span>
          <span class="value">{{
            userDetail.loginType === 0 ? '用户名/邮箱' : userDetail.loginType === 1 ? 'gitee' : userDetail.loginType === 2 ? 'github' : userDetail.loginType === 3 ? 'QQ' : '未知登录方式'
          }}</span>
        </div>
        <div class="info-item">
          <span class="label">登录IP:</span>
          <span class="value">{{ userDetail.loginIp }}</span>
        </div>
        <div class="info-item">
          <span class="label">登录地址:</span>
          <span class="value">{{ userDetail.loginAddress }}</span>
        </div>
        <div class="info-item">
          <span class="label">最后登录时间:</span>
          <span class="value">{{ userDetail.loginTime }}</span>
        </div>
      </div>

      <!-- 注册信息 -->
      <h3>注册信息</h3>
      <div class="info-grid">
        <div class="info-item">
          <span class="label">注册类型:</span>
          <span class="value">{{
            userDetail.registerType === 0 ? '用户名/邮箱' : userDetail.registerType === 1 ? 'gitee' : userDetail.registerType === 2 ? 'github' : userDetail.registerType === 3 ? 'QQ' : '未知注册方式'
          }}</span>
        </div>
        <div class="info-item">
          <span class="label">注册IP:</span>
          <span class="value">{{ userDetail.registerIp }}</span>
        </div>
        <div class="info-item">
          <span class="label">注册地址:</span>
          <span class="value">{{ userDetail.registerAddress }}</span>
        </div>
        <div class="info-item">
          <span class="label">注册时间:</span>
          <span class="value">{{ userDetail.createTime }}</span>
        </div>
        <div class="info-item">
          <span class="label">更新时间:</span>
          <span class="value">{{ userDetail.updateTime }}</span>
        </div>
      </div>

      <!-- 角色信息 -->
      <h3>角色信息</h3>
      <div v-if="userDetail.sysRoles && userDetail.sysRoles.length > 0" class="role-list">
        <el-tag v-for="role in userDetail.sysRoles" :key="role.id" type="primary" style="margin-right: 10px; margin-bottom: 10px"> {{ role.name }} ({{ role.role }}) </el-tag>
      </div>
      <div v-else class="no-data">暂无角色信息</div>

      <!-- 权限信息 -->
      <h3>权限信息</h3>
      <div v-if="userDetail.sysPermissions && userDetail.sysPermissions.length > 0" class="permission-container">
        <el-table :data="userDetail.sysPermissions" size="small" class="permission-table">
          <el-table-column prop="id" label="权限ID" width="80" />
          <el-table-column prop="description" label="权限描述" width="180" />
          <el-table-column prop="permission" label="权限标识" width="220" />
          <el-table-column prop="menuId" label="菜单ID" width="80" />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="updateTime" label="更新时间" width="180" />
        </el-table>
      </div>
      <div v-else class="no-data">暂无权限信息</div>

      <!-- 菜单信息 -->
      <h3>菜单信息</h3>
      <div v-if="userDetail.sysMenus && userDetail.sysMenus.length > 0" class="permission-container">
        <el-table :data="userDetail.sysMenus" size="small" row-key="id" default-expand-all class="permission-table">
          <el-table-column prop="id" label="菜单ID" width="100" />
          <el-table-column prop="parentId" label="父菜单ID" width="100" />
          <el-table-column prop="name" label="菜单名称" width="180" />
          <el-table-column prop="sort" label="排序" width="100" />
          <el-table-column prop="path" label="路由路径" width="220" />
          <el-table-column prop="component" label="组件路径" width="180" />
          <el-table-column prop="icon" label="图标" width="100">
            <template #default="{ row }">
              <div v-if="row.icon" style="display: flex; align-items: center">
                <el-icon style="margin-right: 10px">
                  <component :is="row.icon" />
                </el-icon>
                <span>{{ row.icon }}</span>
              </div>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '启用' : '禁用' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column prop="updateTime" label="更新时间" width="180" />
        </el-table>
      </div>
      <div v-else class="no-data">暂无菜单权限</div>
    </el-card>
    <div v-else class="loading-container"><el-loading v-loading="true" />加载中...</div>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, InfoFilled, Edit, Delete, Avatar } from '@element-plus/icons-vue'
import { getRoleList } from '@/api/role'
import { getUserList, getUserPage, updateUser, deleteUser, queryUserPage, getUserDetail } from '@/api/user'
import { getRolesByUser, addRole } from '@/api/user-role'
import { formatMenu } from '@/utils/Menu'

// 组件
import ManagementCard from '@/components/management/ManagementCard.vue'
import KeywordSearch from '@/components/search/KeywordSearch.vue'
import ExamineStatusSelect from '@/components/search/ExamineStatusSelect.vue'
import SearchButtons from '@/components/search/SearchButtons.vue'
import TableActions from '@/components/data/TableActions.vue'
import TimeRangePicker from '@/components/search/TimeRangePicker.vue'

// 状态选项
const statusOptions = [
  { label: '正常', value: 0 },
  { label: '禁用', value: 1 },
]

// 用户列表数据
const userList = ref([])
// 分页后的用户列表
const paginatedUserList = ref([])
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
const userFormRef = ref(null)
// 表单数据
const userForm = ref({
  id: null,
  username: '',
  nickname: '',
  email: '',
  avatar: '',
  status: 0,
})
// 表单验证规则
const rules = {
  username: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
  email: [{ required: true, message: '请输入用户邮箱', trigger: 'blur' }],
  nickname: [
    { required: true, message: '请输入用户昵称', trigger: 'blur' },
    { min: 4, max: 20, message: '昵称长度必须在4-20个字符之间', trigger: 'blur' },
  ],
}

// 获取用户列表
const getUsers = async () => {
  currentPage.value = 1
  await fetchUsers()
}

// 初始化
onMounted(() => {
  getUsers()
})

const switchLoading = ref(false)
// 处理状态变更
const handleStatusChange = async (id, status) => {
  return new Promise((resolve, reject) => {
    switchLoading.value = true
    updateUser({ id, status })
      .then(() => {
        ElMessage.success('状态更新成功')
        // 手动更新本地数据状态
        const user = userList.value.find((item) => item.id === id)
        if (user) {
          user.status = status
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

// 搜索用户名称
const searchUsername = ref('')
// 搜索用户邮箱
const searchEmail = ref('')
// 搜索用户状态
const searchStatus = ref('')
// 搜索创建时间开始
const searchCreateTimeStart = ref(null)
// 搜索创建时间结束
const searchCreateTimeEnd = ref(null)

const hasSearchConditions = () => !!(searchUsername.value || searchEmail.value || searchStatus.value !== '' || searchCreateTimeStart.value || searchCreateTimeEnd.value)

const buildSearchPayload = () => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  username: searchUsername.value || undefined,
  email: searchEmail.value || undefined,
  status: searchStatus.value !== '' ? searchStatus.value : undefined,
  createTimeStart: searchCreateTimeStart.value || undefined,
  createTimeEnd: searchCreateTimeEnd.value || undefined,
})

const applyPageData = (pageData) => {
  userList.value = pageData?.data || []
  paginatedUserList.value = userList.value
  total.value = Number(pageData?.total || 0)
}

const fetchUsers = async () => {
  loading.value = true
  try {
    let pageData = null
    if (hasSearchConditions()) {
      const res = await queryUserPage(buildSearchPayload())
      pageData = res.data
    } else {
      const res = await getUserPage({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      })
      pageData = res.data
    }
    applyPageData(pageData)
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? '搜索用户失败' : '获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 更新分页数据
const updatePaginatedUserList = () => {
  paginatedUserList.value = userList.value
}

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1
  await fetchUsers()
}

// 处理重置
const handleReset = () => {
  searchUsername.value = ''
  searchEmail.value = ''
  searchStatus.value = ''
  searchCreateTimeStart.value = null
  searchCreateTimeEnd.value = null
  handleSearch()
}

// 处理编辑用户
const handleEditUser = (row) => {
  dialogTitle.value = '编辑用户'
  // 深拷贝行数据
  userForm.value = { ...row }
  dialogVisible.value = true
}

// 处理删除用户
const handleDeleteUser = (id) => {
  ElMessageBox.confirm('确定要删除该用户吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  })
    .then(async () => {
      loading.value = true
      try {
        await deleteUser(id)
        ElMessage.success('删除成功')
        getUsers()
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
  userFormRef.value.validate(async (valid) => {
    if (!valid) {
      return
    }
    try {
      if (userForm.value.id) {
        // 编辑用户
        await updateUser(userForm.value)
        ElMessage.success('编辑用户成功')
      } else {
        // 新增用户
        await addUser(userForm.value)
        ElMessage.success('新增用户成功')
        getUsers()
      }
      dialogVisible.value = false
      getUsers()
    } catch (error) {
      ElMessage.error(userForm.value.id ? '编辑用户失败' : '新增用户失败')
      handleDialogClose()
    }
  })
}

// 处理对话框关闭
const handleDialogClose = () => {
  userFormRef.value.resetFields()
  dialogVisible.value = false
}

// 用户详情弹窗
const userDetailDialogVisible = ref(false)

// 用户详情
const userDetail = ref()

// 用户详情
const handleDetailUser = async (id) => {
  userDetailDialogVisible.value = true
  const res = await getUserDetail(id)
  userDetail.value = res.data.data
  // 检查菜单数据是否存在，避免空值调用formatMenu导致错误
  if (userDetail.value.sysMenus && userDetail.value.sysMenus.length > 0) {
    userDetail.value.sysMenus = formatMenu(userDetail.value.sysMenus)
  } else {
    userDetail.value.sysMenus = []
  }
}

// 授权角色弹窗
const authorizeDialogVisible = ref(false)
// 当前用户
const currentUser = ref(null)

// 选择的角色
const selectedRole = ref([])
// 所有角色
const allRole = ref([])
// 授权弹窗加载状态
const authorizeLoading = ref(false)

// 处理授权角色
const handleAuthorizeRole = async (row) => {
  currentUser.value = row
  selectedRole.value = []

  // 先打开弹窗并显示 loading
  authorizeDialogVisible.value = true
  authorizeLoading.value = true

  try {
    // 并行加载角色列表和用户已有角色
    const [roleRes, userRolesRes] = await Promise.all([getRoleList(), getRolesByUser(row.id)])

    allRole.value = roleRes.data
    selectedRole.value = userRolesRes.data.map((item) => item.id)
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
    await addRole({
      userId: currentUser.value.id,
      roleIds: selectedRole.value,
    })
    ElMessage.success(`已为用户 ${currentUser.value.username} 分配角色`)
  } catch (error) {
    ElMessage.error(`为用户 ${currentUser.value.username} 分配角色失败`)
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
</script>

<style lang="scss" scoped>
// 表格包装器
.user-table-wrapper {
  flex: 1;
  overflow: auto;
}

// 搜索输入框
.search-input {
  width: 140px;
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
  width: 120px;
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
    color: var(--text-regular);
  }

  :deep(.el-table__body-wrapper) tr td {
    color: var(--text-muted);
    padding: 12px 0;
  }

  :deep(.el-table__fixed-right) {
    box-shadow: -3px 0 10px var(--shadow-card);
  }
}

// 角色按钮
.role-btn {
  background-color: var(--action-user-bg);
  color: var(--action-user-color);
  border-color: var(--action-user-border);
  border-radius: 6px;
  margin-left: 0 !important;

  &:hover {
    background-color: var(--action-user-hover-bg);
    border-color: var(--action-user-hover-border);
  }
}

// 移动端卡片视图
.user-cards {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px;

  .user-card {
    transition: all 0.3s ease;
    border-radius: 8px;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .user-card-content {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .user-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        padding-bottom: 12px;
        border-bottom: 1px solid var(--el-border-color-lighter);

        .header-left {
          display: flex;
          align-items: center;
          gap: 12px;
          flex: 1;
          min-width: 0;

          :deep(.el-avatar) {
            flex-shrink: 0;
          }

          .user-basic {
            display: flex;
            flex-direction: column;
            gap: 4px;
            flex: 1;
            min-width: 0;

            .user-name {
              font-size: 16px;
              font-weight: 600;
              color: var(--text-primary);
              word-break: break-all;
              line-height: 1.3;
            }

            .user-id {
              font-size: 12px;
              color: var(--text-muted);
              background-color: var(--bg-input);
              padding: 2px 6px;
              border-radius: 4px;
              display: inline-block;
              width: fit-content;
            }
          }
        }

        .header-right {
          display: flex;
          flex-direction: column;
          align-items: flex-end;
          gap: 8px;
        }
      }

      .user-info {
        display: flex;
        flex-direction: column;
        gap: 8px;

        .info-row {
          display: flex;
          align-items: center;
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

          .time-text {
            font-size: 12px;
            color: var(--text-muted);
          }
        }
      }

      .user-actions {
        display: flex;
        gap: 6px;
        justify-content: center;
        padding-top: 12px;
        border-top: 1px solid var(--el-border-color-lighter);
        flex-wrap: wrap;

        .el-button {
          margin-left: 0;
          flex: 1;
          min-width: 60px;
        }

        .detail-button {
          background-color: var(--bg-input);
          color: var(--text-light);
          border-color: var(--border);

          &:hover {
            background-color: var(--border-light);
            border-color: var(--border-light);
          }
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

        .role-button {
          background-color: var(--action-user-bg);
          color: var(--action-user-color);
          border-color: var(--action-user-border);

          &:hover {
            background-color: var(--action-user-hover-bg);
            border-color: var(--action-user-hover-border);
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

// 新增/编辑对话框
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
    color: var(--text-primary);
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

:deep(.user-detail) {
  width: 1200px !important;

  @media screen and (max-width: 767px) {
    width: 90% !important;
  }
}

.user-detail {
  .user-detail-card {
    .role-list {
      padding: 10px 0;
    }

    .permission-container {
      padding: 10px 0;

      .permission-table {
        width: 100%;
        margin-top: 10px;
        max-height: 300px;
        overflow-y: auto;
      }
    }

    .no-data {
      padding: 10px;
      color: var(--text-muted);
      text-align: center;
    }
  }

  .loading-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 300px;
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .user-table-wrapper {
    max-height: calc(100vh - 180px);
  }
}
</style>
