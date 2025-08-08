<template>
  <div class="menu-management-container">
    <div class="menu-card">
      <div class="card-header">
        <h2 class="card-title">菜单管理</h2>
        <div class="card-actions">
          <el-input v-model="searchQuery" placeholder="搜索菜单名称" :prefix-icon="Search" size="small" class="search-input" />
          <el-button type="primary" size="small" @click="handleAddMenu" :icon="Plus" class="add-button"> 新增菜单 </el-button>
        </div>
      </div>

      <!-- 菜单表格 -->
      <el-table v-loading="loading" :data="paginatedMenuList" class="menu-table" style="height: 100%">
        <el-table-column fixed prop="id" label="id" width="60" />
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
        <el-table-column prop="sort" label="排序" />
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
              @change="(value) => handleStatusChange(row.id, value)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="170">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="primary" size="small" @click="handleEditMenu(row)" :icon="Edit" class="edit-button"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="handleDeleteMenu(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout=" prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 新增/编辑菜单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose">
      <el-form ref="menuFormRef" :model="menuForm" :rules="rules" class="menu-form">
        <el-form-item prop="name">
          <el-input v-model="menuForm.name" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item prop="path">
          <el-input v-model="menuForm.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item prop="component">
          <el-input v-model="menuForm.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item prop="icon">
          <el-select v-model="menuForm.icon" placeholder="请选择图标">
            <el-option v-for="icon in icons" :key="icon" :label="icon" :value="icon" />
          </el-select>
        </el-form-item>
        <el-form-item prop="parentId">
          <el-select v-model="menuForm.parentId" placeholder="请选择父菜单">
            <el-option :value="0" label="无父菜单" />
            <el-option v-for="menu in parentMenuOptions" :key="menu.id" :label="menu.name" :value="menu.id" />
          </el-select>
        </el-form-item>
        <el-form-item prop="sort"> 排序号 &nbsp<el-input-number v-model="menuForm.sort" :min="1" :max="999" placeholder="请输入排序号" /> </el-form-item>
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
import { Search, Plus, Edit, Delete, Menu } from "@element-plus/icons-vue";
import { getAllMenuList, addMenu, updateMenu, deleteMenu, queryMenu } from "@/api/menu";

// 图标列表
const icons = ["Menu", "HomeFilled", "User", "Settings", "Role", "Logout", "FileText", "Layers", "Message", "Database"];

// 搜索查询
const searchQuery = ref("");
// 菜单列表数据
const menuList = ref([]);
// 分页后的菜单列表
const paginatedMenuList = ref([]);
// 父菜单选项
const parentMenuOptions = ref([]);
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
const dialogTitle = ref("新增菜单");

// 表单引用
const menuFormRef = ref(null);
// 表单数据
const menuForm = ref({
  id: null,
  name: "",
  path: "",
  component: "",
  icon: "",
  parentId: 0,
  sort: 0,
  status: 0,
});
// 表单验证规则
const rules = {
  parentId: [{ required: true, message: "请选择父菜单", trigger: "change" }],
  name: [{ required: true, message: "请输入菜单名称", trigger: "blur" }],
  path: [{ required: true, message: "请输入路由路径", trigger: "blur" }],
  component: [{ required: true, message: "请输入组件路径", trigger: "blur" }],
  sort: [
    { required: true, message: "请输入排序号", trigger: "blur" },
    { type: "number", message: "排序号必须是数字", trigger: "blur" },
  ],
};

// 初始化
onMounted(() => {
  getMenuList();
  getParentMenuOptions();
});

// 获取菜单列表
const getMenuList = async () => {
  loading.value = true;
  try {
    const res = await getAllMenuList();
    menuList.value = res.data.data;
    total.value = menuList.value.length;
    // 对菜单进行排序
    menuList.value.sort((a, b) => a.id - b.id);
    // 更新分页数据
    updatePaginatedMenuList();
  } catch (error) {
    ElMessage.error("获取菜单列表失败");
    console.error("获取菜单列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 更新分页数据
const updatePaginatedMenuList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedMenuList.value = menuList.value.slice(startIndex, endIndex);
};

// 获取父菜单选项
const getParentMenuOptions = async () => {
  try {
    const res = await getAllMenuList();
    // 只选择顶级菜单作为父菜单选项
    parentMenuOptions.value = res.data.data.filter((menu) => menu.parentId === 0);
  } catch (error) {
    ElMessage.error("获取父菜单选项失败");
    console.error("获取父菜单选项失败:", error);
  }
};

// 处理搜索
const handleSearch = async () => {
  if (searchQuery.value.trim() === "") {
    getMenuList();
    return;
  }

  loading.value = true;
  try {
    const res = await queryMenu(searchQuery.value);
    menuList.value = res.data.data;
    total.value = menuList.value.length;
    // 更新分页数据
    updatePaginatedMenuList();
  } catch (error) {
    ElMessage.error("搜索菜单失败");
    console.error("搜索菜单失败:", error);
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
  }, 500);
});

// 处理添加菜单
const handleAddMenu = () => {
  dialogTitle.value = "新增菜单";
  menuForm.value = {
    id: null,
    name: "",
    path: "",
    component: "",
    icon: "",
    parentId: 0,
    sort: 0,
    status: 1,
  };
  dialogVisible.value = true;
};

// 处理编辑菜单
const handleEditMenu = (row) => {
  dialogTitle.value = "编辑菜单";
  // 深拷贝行数据
  menuForm.value = { ...row };
  dialogVisible.value = true;
};

// 处理删除菜单
const handleDeleteMenu = (id) => {
  ElMessageBox.confirm("确定要删除该菜单吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      loading.value = true;
      try {
        await deleteMenu(id);
        ElMessage.success("删除成功");
        getMenuList();
      } catch (error) {
        ElMessage.error("删除失败");
        console.error("删除菜单失败:", error);
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除
    });
};

// 处理状态变更
const handleStatusChange = async (id, status) => {
  try {
    await updateMenu({ id, status });
    ElMessage.success("状态更新成功");
  } catch (error) {
    ElMessage.error("状态更新失败");
    console.error("更新菜单状态失败:", error);
    // 恢复原始状态
    const menu = menuList.value.find((item) => item.id === id);
    if (menu) {
      menu.status = !status;
    }
  }
};

// 处理表单提交
const handleSubmit = () => {
  menuFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }

    loading.value = true;
    try {
      if (menuForm.value.id) {
        // 编辑菜单
        await updateMenu(menuForm.value);
        ElMessage.success("编辑菜单成功");
      } else {
        // 新增菜单
        await addMenu(menuForm.value);
        ElMessage.success("新增菜单成功");
      }
      dialogVisible.value = false;
      getMenuList();
    } catch (error) {
      ElMessage.error(menuForm.value.id ? "编辑菜单失败" : "新增菜单失败");
      console.error(menuForm.value.id ? "编辑菜单失败:" : "新增菜单失败:", error);
    } finally {
      loading.value = false;
    }
  });
};

// 处理对话框关闭
const handleDialogClose = () => {
  menuFormRef.value.resetFields();
  dialogVisible.value = false;
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  updatePaginatedMenuList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedMenuList();
};
</script>

<style lang="scss" scoped>
.menu-management-container {
  padding: 20px;
  height: 100%;
  box-sizing: border-box;
  position: relative;

  .menu-card {
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

    // 菜单表格
    .menu-table {
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
        display: flex;
        gap: 8px; // 设置按钮之间的间距
        // flex-wrap: wrap; // 允许按钮在空间不足时换行

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
  .menu-form {
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
  .menu-management-container {
    .menu-card {
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
  .menu-management-container {
    .menu-card {
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
  .menu-management-container {
    padding: 12px;

    .menu-card {
      .card-header {
        padding: 12px 16px;

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

      .menu-table {
        :deep(.el-table) {
          display: block;
          width: 100%;
          overflow-x: auto;
        }
      }

      .pagination-container {
        padding: 5px;
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
  .menu-form {
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
