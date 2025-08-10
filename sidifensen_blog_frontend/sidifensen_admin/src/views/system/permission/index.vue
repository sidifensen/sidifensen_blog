<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">权限管理</h2>
        <div class="card-actions">
          <div class="search">
            <el-input v-model="searchDescription" placeholder="搜索权限描述" :prefix-icon="Search" size="small" class="search-input" clearable />
            <el-input v-model="searchPermission" placeholder="搜索权限标识" :prefix-icon="Search" size="small" class="search-input" clearable />
          </div>
          <div>
            <el-select v-model="searchMenuId" placeholder="请选择菜单名称" filterable clearable size="small" class="search-input" @change="handleSearch">
              <el-option v-for="menu in menuList" :key="menu.id" :label="menu.name" :value="menu.id" />
            </el-select>
            <el-button size="small" type="warning" :disabled="currentPermissionList.length === 0" @click="handleAuthorizeBatchRole" :icon="Avatar" class="authorize-button"> 批量授权角色 </el-button>
          </div>
          <el-button type="primary" size="small" @click="handleAddPermission" :icon="Plus" class="add-button"> 新增权限 </el-button>
        </div>
      </div>

      <!-- 权限表格 -->
      <el-table v-loading="loading" :data="paginatedPermissionList" class="table" style="height: 100%" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column fixed prop="id" label="权限id" width="70" />
        <el-table-column prop="description" label="权限描述" />
        <el-table-column prop="permission" label="权限标识" />
        <el-table-column prop="menuName" label="菜单名称">
          <template v-slot="scope">
            <el-tag>
              <el-icon><component :is="scope.row.icon" /></el-icon>
              {{ scope.row.menuName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" sortable width="120" />
        <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
        <el-table-column label="操作" width="260">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" size="small" @click="handleEditPermission(row)" :icon="Edit" class="edit-button"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="handleDeletePermission(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
              <el-button size="small" type="warning" @click="handleAuthorizeRole(row)" :icon="Avatar" class="role-button"> 授权角色 </el-button>
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
      <el-form ref="permissionFormRef" :model="permissionForm" :rules="rules" class="editForm">
        <el-form-item prop="description">
          <el-input v-model="permissionForm.description" placeholder="请输入权限描述" />
        </el-form-item>
        <el-form-item prop="permission">
          <el-input v-model="permissionForm.permission" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item prop="menuId">
          <el-select v-model="permissionForm.menuId" placeholder="请选择菜单名称">
            <el-option v-for="menu in menuList" :key="menu.id" :label="menu.name" :value="menu.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 授权角色弹窗对话框 -->
    <el-dialog v-model="authorizeDialogVisible" title="权限授权角色" :before-close="handleAuthorizeDialogClose" width="500px">
      <div class="authorize-dialog-content">
        <p class="role-name">当前权限: {{ currentPermission?.description }}</p>
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

    <!-- 批量授权角色弹窗对话框 -->
    <el-dialog v-model="authorizeBatchDialogVisible" title="权限批量授权角色" :before-close="handleAuthorizeBatchDialogClose" width="500px">
      <div class="authorize-dialog-content">
        <p class="role-name">当前权限: {{ currentPermissionList.map((item) => item.description).join(", ") }}</p>
        <el-form ref="authorizeBatchFormRef" class="authorize-form">
          <el-form-item label="选择角色">
            <el-checkbox-group v-model="selectedRole" class="role-checkbox-group">
              <el-checkbox v-for="role in allRole" :key="role.id" :label="role.id">{{ role.role }}</el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleAuthorizeBatchDialogClose">取消</el-button>
          <el-button type="primary" @click="handleAuthorizeBatchSubmit">确认分配</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { Search, Plus, Edit, Delete, Avatar } from "@element-plus/icons-vue";
import { getPermissionList, addPermission, updatePermission, deletePermission, queryPermission } from "@/api/permission";
import { getRoleList } from "@/api/role";
import { addBatchRolePermission, addRolePermission, getRolesByPermission } from "@/api/role-permission";
import { getAllMenuList } from "@/api/menu";

// 权限列表数据
const permissionList = ref([]);
// 分页后的权限列表
const paginatedPermissionList = ref([]);
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
const permissionFormRef = ref(null);
// 表单数据
const permissionForm = ref({
  id: null,
  description: "",
  permission: "",
  menuId: null,
});
// 表单验证规则
const rules = {
  description: [{ required: true, message: "请输入权限描述", trigger: "blur" }],
  permission: [{ required: true, message: "请输入权限标识", trigger: "blur" }],
  menuId: [{ required: true, message: "请选择菜单名称", trigger: "change" }],
};

// 获取权限列表
const getPermissions = async () => {
  loading.value = true;
  try {
    const res = await getPermissionList();
    permissionList.value = res.data.data;
    total.value = permissionList.value.length;
    // 对角色列表进行排序
    permissionList.value.sort((a, b) => a.id - b.id);
    // 更新分页数据
    updatePaginatedPermissionList();
  } catch (error) {
    ElMessage.error("获取权限列表失败");
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  getPermissions();
  getMenuList();
});

// 更新分页数据
const updatePaginatedPermissionList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedPermissionList.value = permissionList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  updatePaginatedPermissionList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedPermissionList();
};

// 菜单列表
const menuList = ref([]);

// 获取菜单列表
const getMenuList = async () => {
  try {
    const res = await getAllMenuList();
    menuList.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取菜单列表失败");
    console.error("获取菜单列表失败:", error);
  }
};

// 搜索权限描述
const searchDescription = ref("");
// 搜索权限标识
const searchPermission = ref("");
// 搜索菜单id
const searchMenuId = ref("");

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    const res = await queryPermission({ description: searchDescription.value, permission: searchPermission.value, menuId: searchMenuId.value });
    permissionList.value = res.data.data;
    total.value = permissionList.value.length;
    // 更新分页数据
    updatePaginatedPermissionList();
  } catch (error) {
    ElMessage.error("搜索权限失败");
  } finally {
    loading.value = false;
  }
};

// 监听搜索输入变化
const searchTimeout = ref(null);
watch(searchDescription, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

watch(searchPermission, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

// 处理添加权限
const handleAddPermission = () => {
  dialogTitle.value = "新增权限";
  permissionForm.value = {
    id: null,
    description: "",
    permission: "",
    menuId: null,
  };
  dialogVisible.value = true;
};

// 处理编辑权限
const handleEditPermission = (row) => {
  dialogTitle.value = "编辑权限";
  // 深拷贝行数据
  permissionForm.value = { ...row };
  dialogVisible.value = true;
};

// 处理删除权限
const handleDeletePermission = (id) => {
  ElMessageBox.confirm("确定要删除该权限吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      loading.value = true;
      try {
        await deletePermission(id);
        ElMessage.success("删除成功");
        getPermissions();
      } catch (error) {
        ElMessage.error("删除失败");
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除
    });
};

// 处理表单提交
const handleSubmit = () => {
  permissionFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    try {
      if (permissionForm.value.id) {
        // 编辑权限
        await updatePermission(permissionForm.value);
        ElMessage.success("编辑权限成功");
      } else {
        // 新增权限
        await addPermission(permissionForm.value);
        ElMessage.success("新增权限成功");
      }
      dialogVisible.value = false;
      getPermissions();
    } catch (error) {
      ElMessage.error(permissionForm.value.id ? "编辑权限失败" : "新增权限失败");
      handleDialogClose();
    }
  });
};

// 处理对话框关闭
const handleDialogClose = () => {
  permissionFormRef.value.resetFields();
  dialogVisible.value = false;
};

// 授权角色弹窗
const authorizeDialogVisible = ref(false);
// 当前权限
const currentPermission = ref(null);

// 选择的角色
const selectedRole = ref([]);
// 所有角色
const allRole = ref([]);

// 处理授权角色
const handleAuthorizeRole = async (row) => {
  currentPermission.value = row;
  authorizeDialogVisible.value = true;
  // 清空已选角色和禁用角色数组
  selectedRole.value = [];

  // 获取角色列表
  const res = await getRoleList();
  allRole.value = res.data.data;

  // 已获得当前权限授权的角色
  const res1 = await getRolesByPermission(row.id);
  // 把数组里的id取出来
  res1.data.data.forEach((item) => {
    // 默认选中已经有权限授权的用户
    selectedRole.value.push(item.id);
  });
};

// 处理授权提交
const handleAuthorizeSubmit = async () => {
  try {
    await addRolePermission({
      permissionId: currentPermission.value.id,
      roleIds: selectedRole.value,
    });
    ElMessage.success(`已为权限 ${currentPermission.value.permission} 分配角色`);
  } catch (error) {
    ElMessage.error(`为权限 ${currentPermission.value.permission} 分配角色失败`);
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

// 授权角色弹窗
const authorizeBatchDialogVisible = ref(false);
// 当前权限
const currentPermissionList = ref([]);

// 表格多选
const handleSelectionChange = async (permission) => {
  currentPermissionList.value = permission;
  // 获取角色列表
  const res = await getRoleList();
  allRole.value = res.data.data;
  console.log(currentPermissionList.value);
};

const handleAuthorizeBatchRole = () => {
  authorizeBatchDialogVisible.value = true;
};

// 处理授权提交
const handleAuthorizeBatchSubmit = async () => {
  try {
    addBatchRolePermission({
      roleIds: selectedRole.value,
      permissionIds: currentPermissionList.value.map((item) => item.id),
    });
    ElMessage.success(`已为权限 ${currentPermissionList.value.map((item) => item.description).join(", ")} 分配角色`);
  } catch (error) {
    ElMessage.error(`为权限 ${currentPermissionList.value.map((item) => item.description).join(", ")} 分配角色失败`);
    console.error("分配角色失败:", error);
  } finally {
    authorizeBatchDialogVisible.value = false;
    // 重置选择的角色和禁用的角色
    selectedRole.value = [];
  }
};

// 处理授权对话框关闭
const handleAuthorizeBatchDialogClose = () => {
  authorizeBatchDialogVisible.value = false;
  currentPermissionList.value = [];
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
      padding: 10px;
      border-bottom: 1px solid #f0f0f0;

      .card-title {
        font-size: 20px;
        font-weight: 600;
        color: #1e293b;
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
        // gap: 12px;

        .search-input {
          width: 240px;
          border-radius: 8px;
          margin-left: 10px;

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

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
        .authorize-button {
          margin-left: 10px;
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

          &.is-disabled {
            background-color: #f5f5f5;
            border-color: #d9d9d9;
            color: #bfbfbf;
            cursor: not-allowed;
            opacity: 0.6;
          }
        }

      }

      .add-button {
        margin-left: 10px;
        border-radius: 8px;
        background: linear-gradient(135deg, #42b983 0%, #3aa17e 100%);
        border: none;
        transition: all 0.3s ease;

        &:hover {
          background: linear-gradient(135deg, #3aa17e 0%, #2d8f6a 100%);
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(66, 185, 131, 0.4);
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
    max-height: calc(100vh - 250px); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */

    :deep(.el-tag__content) {
      display: flex;
      align-items: center;
    }

    :deep(.el-table__header-wrapper) {
      background-color: #f8fafc;
      th {
        font-weight: 600;
        color: #475569;
        background-color: #f8fafc !important;
        border-bottom: 1px solid #e2e8f0;
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
    background-color: #f8fafc;
    border-radius: 0 0 12px 12px;
    border-top: 1px solid #f0f0f0;
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
          gap: 8px;

          .search-input {
            width: 100%;
          }

          .add-button {
            width: 100%;
          }
        }
      }

      table {
        margin-top: 0;
        max-height: calc(100vh - 220px); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */
        :deep(.el-table) {
          display: block;
          width: 100%;
          overflow-x: auto;
        }
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
      gap: 10px;
      height: 50px;
      justify-content: center;
      // flex-direction: column;

      .search{
        width: 600px;
      }

      .search-input {
        margin-left: 0;
        padding-bottom: 10px;
      }
      .authorize-button {
        margin-bottom: 10px;
        
      }
    }
  }
}
</style>
