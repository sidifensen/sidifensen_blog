<template>
  <div class="menu-management-container">
    <div class="menu-card">
      <div class="card-header">
        <h2 class="card-title">菜单管理</h2>
        <div class="card-actions">
          <el-input v-model="searchQuery" placeholder="搜索菜单名称" :prefix-icon="Search" size="small" class="search-input" />
          <el-button type="primary" size="small" @click="handleAddMenu" :icon="Plus" class="add"> 新增菜单 </el-button>
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedMenuList" row-key="id" default-expand-all class="menu-table" style="height: 100%">
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

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="menu-cards" v-loading="loading">
          <!-- 展平的菜单列表，用缩进显示层级 -->
          <div v-for="menu in flatMenuList" :key="menu.id" class="menu-card" :style="{ marginLeft: menu.level * 16 + 'px' }">
            <div class="menu-card-header">
              <!-- 展开/收起按钮 -->
              <div v-if="menu.hasChildren" class="expand-button" @click="toggleMenuExpand(menu.id)">
                <el-icon :class="['expand-icon', { expanded: isMenuExpanded(menu.id) }]">
                  <ArrowRight />
                </el-icon>
              </div>

              <!-- 菜单图标 -->
              <div class="menu-icon-wrapper">
                <el-icon v-if="menu.icon" class="menu-icon">
                  <component :is="menu.icon" />
                </el-icon>
                <el-icon v-else class="menu-icon placeholder">
                  <Document />
                </el-icon>
              </div>

              <!-- 菜单主要信息 -->
              <div class="menu-main-info">
                <div class="menu-id-badge">ID: {{ menu.id }}</div>
                <div class="menu-name">{{ menu.name }}</div>
                <div v-if="menu.path" class="menu-path">{{ menu.path }}</div>
              </div>
            </div>

            <!-- 菜单详细信息 -->
            <div class="menu-details">
              <div v-if="menu.parentId !== undefined" class="detail-item">
                <span class="label">父菜单ID:</span>
                <span class="value">{{ menu.parentId === 0 ? "无" : menu.parentId }}</span>
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

            <!-- 状态区域 -->
            <div class="menu-status-section">
              <el-switch v-model="menu.status" active-color="#42b983" inactive-color="#cccccc" active-text="正常" inactive-text="禁用" :active-value="0" :inactive-value="1" inline-prompt :loading="switchLoading" :before-change="() => handleStatusChange(menu.id, menu.status === 0 ? 1 : 0)" />
            </div>

            <!-- 操作按钮 -->
            <div class="menu-actions">
              <el-button v-if="menu.hasChildren || menu.parentId == 0" text bg type="success" size="small" :icon="Plus" @click="handleAddMenu(menu)">新增</el-button>
              <el-button text bg type="primary" size="small" :icon="Edit" @click="handleEditMenu(menu)">编辑</el-button>
              <el-button text bg type="danger" size="small" :icon="Delete" @click="handleDeleteMenu(menu.id)">删除</el-button>
              <el-button text bg type="warning" size="small" :icon="Avatar" @click="handleAuthorizeRole(menu)">角色</el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <Pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

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
                <el-icon style="margin-right: 10px">
                  <component :is="icon" />
                </el-icon>
                <span>{{ icon }}</span>
              </div>
            </el-option>
            <template #label>
              <div style="display: flex; align-items: center">
                <el-icon style="margin-right: 10px">
                  <component :is="menuForm.icon" />
                </el-icon>
                <span>{{ menuForm.icon }}</span>
              </div>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item prop="parentId" label="父菜单">
          <el-select v-model="menuForm.parentId" placeholder="请选择父菜单">
            <el-option :value="0" label="无父菜单" />
            <el-option v-for="menu in allMenus" :key="menu.id" :label="menu.name" :value="menu.id" />
          </el-select>
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
    <el-dialog v-model="authorizeDialogVisible" title="菜单分配角色" :before-close="handleAuthorizeDialogClose" width="500px">
      <div class="authorize-dialog-content">
        <p class="menu-name">当前菜单: {{ currentMenu?.name }}</p>
        <el-form ref="authorizeFormRef" class="authorize-form">
          <el-form-item label="选择角色">
            <el-checkbox-group v-model="selectedRoles" class="role-checkbox-group">
              <el-checkbox v-for="role in allRoles" :key="role.id" :label="role.id">{{ role.name }}</el-checkbox>
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
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from "vue";
import { Search, Plus, Edit, Delete, Avatar, Document, ArrowRight } from "@element-plus/icons-vue";
import { getAllMenuList, addMenu, updateMenu, deleteMenu, queryMenu } from "@/api/menu";
import { addRoleMenu, getRolesByMenu } from "@/api/role-menu";
import { getRoleList } from "@/api/role";
import { icons } from "@/utils/Icon";
import { formatMenu } from "@/utils/Menu";
import Pagination from "@/components/Pagination.vue";

// 搜索查询
const searchQuery = ref("");
// 移动端检测
const isMobileView = ref(false);
// 移动端展开的菜单ID集合
const expandedMenus = ref(new Set());

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 切换菜单展开状态
const toggleMenuExpand = (menuId) => {
  if (expandedMenus.value.has(menuId)) {
    expandedMenus.value.delete(menuId);
  } else {
    expandedMenus.value.add(menuId);
  }
  // 触发响应式更新
  expandedMenus.value = new Set(expandedMenus.value);
};

// 检查菜单是否展开
const isMenuExpanded = (menuId) => {
  return expandedMenus.value.has(menuId);
};
// 菜单列表数据
const menuList = ref([]);
// 分页后的菜单列表
const paginatedMenuList = ref([]);
// 父菜单选项
const parentMenuOptions = ref([]);
// 加载状态
const loading = ref(false);
// 总条数
const total = ref(0);

// 对话框可见性
const dialogVisible = ref(false);
// 对话框标题
const dialogTitle = ref("新增菜单");

// 表单引用
const menuFormRef = ref(null);
const authorizeFormRef = ref(null);
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
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});

// 获取菜单列表
const getMenuList = async () => {
  loading.value = true;
  try {
    const res = await getAllMenuList();

    // 按照sort排序并树形化
    menuList.value = formatMenu(res.data.data);

    // 树形化后的菜单列表长度为总数
    total.value = menuList.value.length;

    // 更新分页数据
    updatePaginatedMenuList();
  } catch (error) {
    ElMessage.error("获取菜单列表失败");
    console.error("获取菜单列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 当前页码
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);

// 更新分页数据
const updatePaginatedMenuList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedMenuList.value = menuList.value.slice(startIndex, endIndex);
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

const allMenus = ref([]);
// 获取所有菜单
const getAllMenus = async () => {
  try {
    const res = await getAllMenuList();
    allMenus.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取所有菜单失败");
    console.error("获取所有菜单失败:", error);
  }
};

// 处理添加菜单
const handleAddMenu = async (row) => {
  await getAllMenus();

  // 判断 row 是否是真正的菜单对象（而不是事件对象）
  if (row && row.id) {
    dialogTitle.value = "新增菜单";
    // 如果传值了说明是在表格行内的新增按钮（添加子菜单）
    menuForm.value = {
      id: null,
      name: "",
      path: "",
      component: "",
      icon: "",
      parentId: row.id,
      sort: 0, // 子菜单排序从0开始
      status: 0,
    };
    dialogVisible.value = true;
  } else {
    // 点击右上角的新增菜单按钮，计算顶级菜单的最大排序号
    const topLevelMenus = allMenus.value.filter((menu) => menu.parentId === 0);

    const maxSort =
      topLevelMenus.length > 0
        ? topLevelMenus.reduce((max, menu) => {
            return menu.sort > max ? menu.sort : max;
          }, 0)
        : 0;

    dialogTitle.value = "新增菜单";
    menuForm.value = {
      id: null,
      name: "",
      path: "",
      component: "",
      icon: "",
      parentId: 0,
      sort: maxSort + 1, // 顶级菜单排序为最大值+1
      status: 0,
    };
    dialogVisible.value = true;
  }
};

// 处理编辑菜单
const handleEditMenu = (row) => {
  getAllMenus();
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
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除
      ElMessage.info("删除已取消");
    });
};

const switchLoading = ref(false);
// 处理状态变更
const handleStatusChange = async (id, status) => {
  return new Promise((resolve, reject) => {
    switchLoading.value = true;
    updateMenu({ id, status })
      .then(() => {
        ElMessage.success("状态更新成功");
        // 手动更新本地数据状态
        const menu = menuList.value.find((item) => item.id === id);
        if (menu) {
          menu.status = status;
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
  menuFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
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
      handleDialogClose();
    }
  });
};

// 处理新增/编辑对话框关闭
const handleDialogClose = () => {
  menuFormRef.value.resetFields();
  dialogVisible.value = false;
};

// 授权角色弹窗
const authorizeDialogVisible = ref(false);
// 当前菜单
const currentMenu = ref(null);
// 选择的角色
const selectedRoles = ref([]);
// 所有角色
const allRoles = ref([]);

// 处理授权角色
const handleAuthorizeRole = async (row) => {
  currentMenu.value = row;
  authorizeDialogVisible.value = true;
  // 清空已选角色
  selectedRoles.value = [];

  const res = await getRoleList();
  allRoles.value = res.data.data;

  // 获取已经有菜单的角色
  const res1 = await getRolesByMenu(row.id);
  // 把数组里的id取出来
  res1.data.data.forEach((item) => {
    // 默认选中已有菜单的角色
    selectedRoles.value.push(item.id);
  });
};

// 处理授权提交
const handleAuthorizeSubmit = async () => {
  try {
    await addRoleMenu({
      menuId: currentMenu.value.id,
      roleIds: selectedRoles.value,
    });
    ElMessage.success(`已为菜单 ${currentMenu.value.name} 授权角色`);
  } catch (error) {
    ElMessage.error(`为菜单 ${currentMenu.value.name} 授权角色失败`);
    console.error("授权失败:", error);
  } finally {
    authorizeDialogVisible.value = false;
    // 重置选择的角色和禁用的角色
    selectedRoles.value = [];
  }
};

// 处理授权对话框关闭
const handleAuthorizeDialogClose = () => {
  authorizeDialogVisible.value = false;
  selectedRoles.value = [];
};

// 计算扁平化的菜单列表（用于移动端显示）
const flatMenuList = computed(() => {
  const flattenMenus = (menus, level = 0, result = []) => {
    menus.forEach((menu) => {
      // 添加层级信息
      const flatMenu = {
        ...menu,
        level,
        hasChildren: menu.children && menu.children.length > 0,
      };
      result.push(flatMenu);

      // 如果有子菜单且处于展开状态，递归添加子菜单
      if (flatMenu.hasChildren && isMenuExpanded(menu.id)) {
        flattenMenus(menu.children, level + 1, result);
      }
    });
    return result;
  };

  return flattenMenus(paginatedMenuList.value);
});
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
      border-bottom: 1px solid var(--el-border-color);

      .card-title {
        font-size: 20px;
        font-weight: 600;
        // color: #1e293b;
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

        .add {
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

    // 批量操作按钮区域
    .card-third {
      display: flex;
      justify-content: flex-end;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
    }
  }

  // 桌面端表格视图
  .desktop-view {
    flex: 1;
    display: flex;
    flex-direction: column;

    // 菜单表格
    .menu-table {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      max-height: calc(100vh - 220px); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */

      :deep(.el-table__header-wrapper) {
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

        .add-button {
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
          margin-left: 0;
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
  }

  // 移动端卡片视图
  .mobile-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    padding-bottom: 60px; // 为分页容器预留空间
    overflow-y: auto;

    // 菜单卡片列表容器
    .menu-cards {
      display: flex;
      flex-direction: column;
      gap: 0;
      padding: 10px;

      // 菜单树项容器
      .menu-tree-item {
        // 子菜单容器
        .menu-children {
          margin-top: 0;
          padding-left: 0;

          .menu-card {
            margin-left: 0;
          }
        }
      }

      // 单个菜单卡片
      .menu-card {
        background: var(--el-bg-color);
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        transition: all 0.3s ease;
        margin-bottom: 8px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
        }

        // 菜单卡片头部
        .menu-card-header {
          display: flex;
          align-items: center;
          gap: 12px;
          padding: 16px;
          background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
          border-bottom: 1px solid var(--el-border-color-lighter);
          position: relative;

          // 展开/收起按钮
          .expand-button {
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 32px;
            height: 32px;
            border-radius: 6px;
            background-color: rgba(255, 255, 255, 0.8);
            transition: all 0.3s ease;
            flex-shrink: 0;

            &:hover {
              background-color: rgba(66, 185, 131, 0.1);
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

          // 菜单图标包装器
          .menu-icon-wrapper {
            display: flex;
            align-items: center;
            justify-content: center;
            width: 48px;
            height: 48px;
            border-radius: 12px;
            background: linear-gradient(135deg, #42b983 0%, #36a970 100%);
            box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);

            .menu-icon {
              font-size: 24px;
              color: white;

              &.placeholder {
                opacity: 0.6;
              }
            }
          }

          // 菜单主要信息
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

        // 菜单详细信息
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

        // 菜单状态区域
        .menu-status-section {
          display: flex;
          justify-content: center;
          padding: 12px 16px;
          border-top: 1px solid var(--el-border-color-lighter);
          border-bottom: 1px solid var(--el-border-color-lighter);
          background-color: var(--el-bg-color-page);
        }

        // 菜单操作按钮
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
  }

}

// 对话框样式
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
}

// 授权角色对话框样式
.authorize-dialog-content {
  padding: 10px;

  .menu-name {
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

// 对话框内表单元素样式
.menu-management-container {
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
  :deep(.el-input-number__decrease),
  :deep(.el-input-number__increase) {
    border-radius: 16px;
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
    .menu-card {
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
        }
      }

      .menu-table {
        max-height: calc(100vh - 200px); /* 调整为视口高度减去固定值，确保有足够空间不被分页器遮挡 */
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
      // flex-direction: column;

      .search-input {
        width: 100% !important;
      }
    }
  }
}
</style>
