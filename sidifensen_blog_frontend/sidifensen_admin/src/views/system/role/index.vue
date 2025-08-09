<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">角色管理</h2>
        <div class="card-actions">
          <el-input v-model="searchQuery" placeholder="搜索角色名称" :prefix-icon="Search" size="small" class="search-input" />
          <el-button type="primary" size="small" @click="handleAddRole" :icon="Plus" class="add-button"> 新增角色 </el-button>
        </div>
      </div>

      <!-- 角色表格 -->
      <el-table v-loading="loading" :data="paginatedRoleList" class="table" style="height: 100%">
        <el-table-column fixed prop="id" label="角色id" width="60" />
        <el-table-column prop="role" label="角色标识" />
        <el-table-column prop="name" label="角色名称" />
        <el-table-column prop="description" label="角色描述" />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              size="large"
              active-color="#42b983"
              inactive-color="#cccccc"
              active-text="正常"
              inactive-text="禁用"
              :active-value="0"
              :inactive-value="1"
              inline-prompt
              :loading="switchLoading"
              :before-change="() => handleStatusChange(row.id, row.status === 0 ? 1 : 0)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" sortable width="120" />
        <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
        <el-table-column label="操作" width="170">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" size="small" @click="handleEditRole(row)" :icon="Edit" class="edit-button"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="handleDeleteRole(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout=" prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 新增/编辑角色对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose">
      <el-form ref="roleFormRef" :model="roleForm" :rules="rules" class="editForm">
        <el-form-item prop="role">
          <el-input v-model="roleForm.role" placeholder="请输入角色标识" />
        </el-form-item>
        <el-form-item prop="name">
          <el-input v-model="roleForm.name" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item prop="description">
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Search, Plus, Edit, Delete } from "@element-plus/icons-vue";
import { getRoles, addRole, updateRole, deleteRole, queryRole } from "@/api/role";

// 搜索查询
const searchQuery = ref("");
// 角色列表数据
const roleList = ref([]);
// 分页后的角色列表
const paginatedRoleList = ref([]);
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
const dialogTitle = ref("新增角色");

// 表单引用
const roleFormRef = ref(null);
// 表单数据
const roleForm = ref({
  id: null,
  name: "",
  role: "",
  description: "",
  status: 0,
});
// 表单验证规则
const rules = {
  role: [{ required: true, message: "请输入角色标识", trigger: "blur" }],
  name: [{ required: true, message: "请输入角色名称", trigger: "blur" }],
  description: [{ required: true, message: "请输入角色描述", trigger: "blur" }],
};

// 获取角色列表
const getRoleList = async () => {
  loading.value = true;
  try {
    const res = await getRoles();
    roleList.value = res.data.data;
    total.value = roleList.value.length;
    // 对角色列表进行排序
    roleList.value.sort((a, b) => a.id - b.id);
    // 更新分页数据
    updatePaginatedRoleList();
  } catch (error) {
    ElMessage.error("获取角色列表失败");
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  getRoleList();
});

// 更新分页数据
const updatePaginatedRoleList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedRoleList.value = roleList.value.slice(startIndex, endIndex);
};

// 处理搜索
const handleSearch = async () => {
  if (searchQuery.value.trim() === "") {
    getRoleList();
    return;
  }

  loading.value = true;
  try {
    const res = await queryRole(searchQuery.value);
    roleList.value = res.data.data;
    total.value = roleList.value.length;
    // 更新分页数据
    updatePaginatedRoleList();
  } catch (error) {
    ElMessage.error("搜索角色失败");
  } finally {
    loading.value = false;
  }
};

// 监听搜索输入变化
const searchTimeout = ref(null);
watch(searchQuery, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 100);
});

// 处理添加角色
const handleAddRole = () => {
  dialogTitle.value = "新增角色";
  roleForm.value = {
    id: null,
    role: "",
    name: "",
    description: "",
    status: 0,
  };
  dialogVisible.value = true;
};

// 处理编辑角色
const handleEditRole = (row) => {
  dialogTitle.value = "编辑角色";
  // 深拷贝行数据
  roleForm.value = { ...row };
  dialogVisible.value = true;
};

// 处理删除角色
const handleDeleteRole = (id) => {
  ElMessageBox.confirm("确定要删除该角色吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      loading.value = true;
      try {
        await deleteRole(id);
        ElMessage.success("删除成功");
        getRoleList();
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

const switchLoading = ref(false);
// 处理状态变更
const handleStatusChange = async (id, status) => {
  console.log("handleStatusChange", id, status);
  return new Promise((resolve, reject) => {
    switchLoading.value = true;
    updateRole({ id, status })
      .then(() => {
        ElMessage.success("状态更新成功");
        // 手动更新本地数据状态
        const role = roleList.value.find((item) => item.id === id);
        if (role) {
          role.status = status;
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

// 处理表单提交
const handleSubmit = () => {
  roleFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    try {
      if (roleForm.value.id) {
        // 编辑角色
        await updateRole(roleForm.value);
        ElMessage.success("编辑角色成功");
      } else {
        // 新增角色
        await addRole(roleForm.value);
        ElMessage.success("新增角色成功");
      }
      dialogVisible.value = false;
      getRoleList();
    } catch (error) {
      ElMessage.error(roleForm.value.id ? "编辑角色失败" : "新增角色失败");
      handleDialogClose();
    }
  });
};

// 处理对话框关闭
const handleDialogClose = () => {
  roleFormRef.value.resetFields();
  dialogVisible.value = false;
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  updatePaginatedRoleList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedRoleList();
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
        gap: 12px;

        .search-input {
          width: 240px;
          border-radius: 8px;

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
              border-color: #42b983;
            }
          }
        }

        .add-button {
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
          flex-direction: column;
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
    gap: 10px;

    .card-actions {
      width: 100%;
      flex-direction: column;

      .search-input {
        width: 100% !important;
      }

      .add-button {
        width: 100%;
      }
    }
  }
}
</style>
