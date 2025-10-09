<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">用户管理</h2>
        <div class="card-actions">
          <el-input v-model="searchUsername" placeholder="搜索用户名称" :prefix-icon="Search" size="small" class="search-input" clearable />
          <el-input v-model="searchEmail" placeholder="搜索用户邮箱" :prefix-icon="Search" size="small" class="search-input" clearable />
          <el-select v-model="searchStatus" placeholder="用户状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="正常" value="0" />
            <el-option label="禁用" value="1" />
          </el-select>
        </div>
      </div>
      <div class="card-second">
        <el-date-picker v-model="searchCreateTimeStart" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="创建时间开始" :prefix-icon="Calendar" size="small" class="search-input" clearable @change="handleSearch" />
        <el-date-picker v-model="searchCreateTimeEnd" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="创建时间结束" :prefix-icon="Calendar" size="small" class="search-input" clearable @change="handleSearch" />
      </div>

      <!-- 权限表格 -->
      <el-table v-loading="loading" :data="paginatedUserList" class="table" style="height: 100%">
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
            <el-switch v-model="row.status" size="large" active-color="#42b983" inactive-color="#cccccc" active-text="正常" inactive-text="禁用" :active-value="0" :inactive-value="1" inline-prompt :loading="switchLoading" :before-change="() => handleStatusChange(row.id, row.status === 0 ? 1 : 0)" />
          </template>
        </el-table-column>
        <el-table-column prop="loginType" label="登录方式" width="110">
          <template #default="{ row }">
            <el-tag>
              {{ row.loginType === 0 ? "用户名/邮箱" : row.loginType === 1 ? "gitee" : row.loginType === 2 ? "github" : row.loginType === 3 ? "QQ" : "未知登录方式" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loginAddress" label="登录地址" />
        <el-table-column prop="loginTime" label="最近登录时间" sortable width="135" />
        <el-table-column prop="createTime" label="创建时间" sortable width="120" />
        <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
        <el-table-column label="操作" width="330">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="info" size="small" @click="handleDetailUser(row.id)" :icon="InfoFilled" class="detail-button">详情</el-button>
              <el-button type="primary" size="small" @click="handleEditUser(row)" :icon="Edit" class="edit-button"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="handleDeleteUser(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
              <el-button size="small" type="warning" @click="handleAuthorizeRole(row)" :icon="Avatar" class="role-button"> 添加角色 </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout=" prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

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
    <el-dialog v-model="authorizeDialogVisible" title="用户添加角色" :before-close="handleAuthorizeDialogClose" width="500px">
      <div class="authorize-dialog-content">
        <p class="role-name">当前用户: {{ currentUser?.username }}</p>
        <el-form ref="authorizeFormRef" class="authorize-form">
          <el-form-item label="选择角色">
            <el-checkbox-group v-model="selectedRole" class="role-checkbox-group">
              <el-checkbox v-for="role in allRole" :key="role.id" :label="role.id">{{ role.role }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
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
            <span class="value">{{ userDetail.sex === 0 ? "男" : userDetail.sex === 1 ? "女" : "未知" }}</span>
          </div>
          <div class="info-item">
            <span class="label">状态:</span>
            <span class="value">
              <el-tag :type="userDetail.status === 0 ? 'success' : 'danger'">{{ userDetail.status === 0 ? "启用" : "禁用" }}</el-tag>
            </span>
          </div>
          <div class="info-item">
            <span class="label">简介:</span>
            <span class="value">{{ userDetail.introduction || "暂无简介" }}</span>
          </div>
        </div>

        <!-- 登录信息 -->
        <h3>登录信息</h3>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">登录类型:</span>
            <span class="value">{{ userDetail.loginType === 0 ? "用户名/邮箱" : userDetail.loginType === 1 ? "gitee" : userDetail.loginType === 2 ? "github" : userDetail.loginType === 3 ? "QQ" : "未知登录方式" }}</span>
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
            <span class="value">{{ userDetail.registerType === 0 ? "用户名/邮箱" : userDetail.registerType === 1 ? "gitee" : userDetail.registerType === 2 ? "github" : userDetail.registerType === 3 ? "QQ" : "未知注册方式" }}</span>
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
                <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? "启用" : "禁用" }}</el-tag>
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from "vue";
import { Search, Plus, InfoFilled, Edit, Delete, Avatar, Calendar } from "@element-plus/icons-vue";
import { getRoleList } from "@/api/role";
import { getUserList, updateUser, deleteUser, queryUser, getUserDetail } from "@/api/user";
import { getRolesByUser } from "@/api/user-role";
import { formatMenu } from "@/utils/Menu";

// 用户列表数据
const userList = ref([]);
// 分页后的用户列表
const paginatedUserList = ref([]);
// 加载状态
const loading = ref(false);
// 当前页码
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);
// 总条数
const total = ref(0);
// 对话框可见性
const dialogVisible = ref(false);
// 对话框标题
const dialogTitle = ref("新增权限");

// 表单引用
const userFormRef = ref(null);
// 表单数据
const userForm = ref({
  id: null,
  username: "",
  nickname: "",
  email: "",
  avatar: "",
  status: 0,
});
// 表单验证规则
const rules = {
  username: [{ required: true, message: "请输入用户名称", trigger: "blur" }],
  email: [{ required: true, message: "请输入用户邮箱", trigger: "blur" }],
  nickname: [
    { required: true, message: "请输入用户昵称", trigger: "blur" },
    { min: 4, max: 20, message: "昵称长度必须在4-20个字符之间", trigger: "blur" },
  ],
};

// 获取用户列表
const getUsers = async () => {
  loading.value = true;
  try {
    const res = await getUserList();
    userList.value = res.data.data;
    total.value = userList.value.length;
    // 对角色列表进行排序
    userList.value.sort((a, b) => a.id - b.id);
    // 更新分页数据
    updatePaginatedUserList();
  } catch (error) {
    ElMessage.error("获取用户列表失败");
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  getUsers();
});

// 更新分页数据
const updatePaginatedUserList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedUserList.value = userList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  updatePaginatedUserList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedUserList();
};

const switchLoading = ref(false);
// 处理状态变更
const handleStatusChange = async (id, status) => {
  return new Promise((resolve, reject) => {
    switchLoading.value = true;
    updateUser({ id, status })
      .then(() => {
        ElMessage.success("状态更新成功");
        // 手动更新本地数据状态
        const user = userList.value.find((item) => item.id === id);
        if (user) {
          user.status = status;
        }
        resolve();
      })
      .catch((error) => {
        ElMessage.error("状态更新失败");
        reject(error);
      })
      .finally(() => {
        switchLoading.value = false;
      });
  });
};

// 搜索用户名称
const searchUsername = ref("");
// 搜索用户邮箱
const searchEmail = ref("");
// 搜索用户状态
const searchStatus = ref("");
// 搜索创建时间开始
const searchCreateTimeStart = ref(null);
// 搜索创建时间结束
const searchCreateTimeEnd = ref(null);

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    const res = await queryUser({
      username: searchUsername.value,
      email: searchEmail.value,
      status: searchStatus.value,
      createTimeStart: searchCreateTimeStart.value,
      createTimeEnd: searchCreateTimeEnd.value,
    });
    userList.value = res.data.data;
    total.value = userList.value.length;
    // 更新分页数据
    updatePaginatedUserList();
  } catch (error) {
    ElMessage.error("搜索用户失败");
  } finally {
    loading.value = false;
  }
};

// 监听搜索输入变化
const searchTimeout = ref(null);
watch(searchUsername, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

watch(searchEmail, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

watch(searchStatus, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

// 处理编辑用户
const handleEditUser = (row) => {
  dialogTitle.value = "编辑用户";
  // 深拷贝行数据
  userForm.value = { ...row };
  dialogVisible.value = true;
};

// 处理删除用户
const handleDeleteUser = (id) => {
  ElMessageBox.confirm("确定要删除该用户吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      loading.value = true;
      try {
        await deleteUser(id);
        ElMessage.success("删除成功");
        getUsers();
      } catch (error) {
        ElMessage.error("删除失败");
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除
      ElMessage.info("删除已取消");
    });
};

// 处理表单提交
const handleSubmit = () => {
  userFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    try {
      if (userForm.value.id) {
        // 编辑用户
        await updateUser(userForm.value);
        ElMessage.success("编辑用户成功");
      } else {
        // 新增用户
        await addUser(userForm.value);
        ElMessage.success("新增用户成功");
        getUsers();
      }
      dialogVisible.value = false;
      getUsers();
    } catch (error) {
      ElMessage.error(userForm.value.id ? "编辑用户失败" : "新增用户失败");
      handleDialogClose();
    }
  });
};

// 处理对话框关闭
const handleDialogClose = () => {
  userFormRef.value.resetFields();
  dialogVisible.value = false;
};

// 用户详情弹窗
const userDetailDialogVisible = ref(false);

// 用户详情
const userDetail = ref();

// 用户详情
const handleDetailUser = async (id) => {
  userDetailDialogVisible.value = true;
  const res = await getUserDetail(id);
  userDetail.value = res.data.data;
  console.log(userDetail.value.sysMenus);
  // 检查菜单数据是否存在，避免空值调用formatMenu导致错误
  if (userDetail.value.sysMenus && userDetail.value.sysMenus.length > 0) {
    userDetail.value.sysMenus = formatMenu(userDetail.value.sysMenus);
  } else {
    userDetail.value.sysMenus = [];
  }
  console.log(userDetail.value);
};

// 授权角色弹窗
const authorizeDialogVisible = ref(false);
// 当前用户
const currentUser = ref(null);

// 选择的角色
const selectedRole = ref([]);
// 所有角色
const allRole = ref([]);

// 处理授权角色
const handleAuthorizeRole = async (row) => {
  currentUser.value = row;
  authorizeDialogVisible.value = true;
  // 清空已选角色和禁用角色数组
  selectedRole.value = [];

  // 获取角色列表
  const res = await getRoleList();
  allRole.value = res.data.data;

  // 已获得当前用户授权的角色
  const res1 = await getRolesByUser(row.id);
  console.log(res1.data.data);
  // 把数组里的id取出来
  res1.data.data.forEach((item) => {
    // 默认选中已经有权限授权的用户
    selectedRole.value.push(item.id);
  });
};

// 处理授权提交
const handleAuthorizeSubmit = async () => {
  try {
    await addRole({
      userId: currentUser.value.id,
      roleIds: selectedRole.value,
    });
    ElMessage.success(`已为用户 ${currentUser.value.username} 分配角色`);
  } catch (error) {
    ElMessage.error(`为用户 ${currentUser.value.username} 分配角色失败`);
    console.error("分配角色失败:", error);
  } finally {
    authorizeDialogVisible.value = false;
    // 重置选择的角色和禁用的角色
    selectedRole.value = [];
  }
};

// 处理授权对话框关闭
const handleAuthorizeDialogClose = () => {
  authorizeDialogVisible.value = false;
  selectedRole.value = [];
};
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
      padding: 10px 10px 0 10px;
      .card-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;

        &::before {
          content: "";
          display: inline-block;
          width: 4px;
          height: 20px;
          background-color: #42b983;
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;

        .search-input {
          width: 240px;
          border-radius: 8px;
          margin-left: 10px;

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            &:focus-within {
              box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
              border-color: #42b983;
            }
          }
          :deep(.el-select__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
              border-color: #42b983;
            }
          }
        }
      }
    }
    .card-second {
      display: flex;
      justify-content: flex-end;
      gap: 10px;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
      :deep(.el-input__wrapper) {
        border-radius: 8px;
        &:focus-within {
          box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
          border-color: #42b983;
        }
      }
    }
  }

  //表格
  .table {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    max-height: calc(100vh - 220px); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */

    :deep(.el-tag__content) {
      display: flex;
      align-items: center;
    }

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
      .detail-button {
        background-color: #f5f5f5;
        color: #606266;
        border-color: #dcdfe6;
        border-radius: 6px;
        transition: all 0.3s ease;
        &:hover {
          background-color: #e9e9e9;
          border-color: #c0c4cc;
          transform: translateY(-2px);
        }
      }
      .edit-button {
        margin-left: 0;
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
      .role-button {
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
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding: 10px;
    background-color: var(--el-bg-color);
    border-radius: 0 0 12px 12px;
    z-index: 10;
    width: 100%;
    box-sizing: border-box;
    position: absolute;
    bottom: 0;
    left: 0;
  }
}

:deep(.el-dialog) {
  border-radius: 16px;
  // 默认宽度
  width: 500px;

  // 屏幕宽度小于768px时的宽度
  @media screen and (max-width: 767px) {
    width: 90%;
  }
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
      box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
      border-color: #42b983;
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

    .user-checkbox-group {
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

:deep(.user-detail) {
  width: 1200px !important;
  // 屏幕宽度小于768px时的宽度
  @media screen and (max-width: 767px) {
    width: 90% !important;
  }
}

// 用户详情
.user-detail {
  .user-detail-card {
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 16px;
      font-weight: bold;
      color: #333;
    }

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
      color: #999;
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

// 响应式设计
@media screen and (max-width: 1400px) {
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

@media screen and (max-width: 1220px) {
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
            width: 100%;
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
          // gap: 8px;

          .search-input {
            width: 100%;
          }

          .add-button {
            width: 100%;
          }
        }
      }

      .table {
        margin-top: 0;
        max-height: calc(100vh - 180px); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */
      }

      .pagination-container {
        padding: 4px;
        :deep(.el-pagination) {
          .el-pager {
            display: none;
          }
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

    .card-actions {
      width: 100%;
      justify-content: space-between;

      .search-input {
        width: 100%;
      }
    }
  }
}
</style>
