<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">黑名单管理</h2>
        <div class="card-actions">
          <el-select v-model="searchForm.type" placeholder="黑名单类型" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="用户" :value="0" />
            <el-option label="IP地址" :value="1" />
          </el-select>
          <el-input v-model.number="searchForm.userId" placeholder="用户ID" :prefix-icon="Search" size="small" class="search-input" clearable @input="handleSearch" />
        </div>
      </div>

      <!-- 时间筛选区域 -->
      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker v-model="searchForm.banTimeStart" type="datetime" placeholder="拉黑开始时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
          <el-date-picker v-model="searchForm.banTimeEnd" type="datetime" placeholder="拉黑结束时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
        </div>
      </div>

      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker v-model="searchForm.expireTimeStart" type="datetime" placeholder="到期开始时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
          <el-date-picker v-model="searchForm.expireTimeEnd" type="datetime" placeholder="到期结束时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
        </div>
      </div>

      <!-- 批量操作按钮区域 -->
      <div class="card-third">
        <el-button type="primary" plain round @click="handleAdd" :icon="Plus">新增黑名单</el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedBlacklists.length === 0" :loading="batchDeleteLoading">批量删除</el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedBlacklistList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <div class="blacklist-type" :class="row.type === 0 ? 'type-user' : 'type-ip'">
                {{ row.type === 0 ? "用户" : "IP地址" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="userId" label="用户ID" width="100">
            <template #default="{ row }">
              <span>{{ row.userId || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="ip" label="IP地址" width="150">
            <template #default="{ row }">
              <el-tooltip v-if="row.ip" :content="row.ip" placement="top-start">
                <div class="blacklist-ip">{{ row.ip }}</div>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="reason" label="拉黑原因" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="row.reason" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="blacklist-reason">{{ row.reason }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="banTime" label="拉黑时间" sortable width="110" />
          <el-table-column prop="expireTime" label="到期时间" sortable width="110">
            <template #default="{ row }">
              <span :class="{ 'expired-time': isExpired(row.expireTime) }">
                {{ row.expireTime }}
              </span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <div class="blacklist-status" :class="isExpired(row.expireTime) ? 'status-expired' : 'status-active'">
                {{ isExpired(row.expireTime) ? "已过期" : "生效中" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="primary" @click="handleEdit(row)" :icon="Edit" class="edit-button" size="small">编辑</el-button>
                <el-button type="danger" @click="handleDelete(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="blacklist-cards">
          <el-card v-for="blacklist in paginatedBlacklistList" :key="blacklist.id" class="blacklist-card">
            <div class="blacklist-card-content">
              <div class="blacklist-header-section">
                <div class="blacklist-info">
                  <div class="blacklist-header">
                    <div class="blacklist-id">#{{ blacklist.id }}</div>
                    <div class="blacklist-type" :class="blacklist.type === 0 ? 'type-user' : 'type-ip'">
                      {{ blacklist.type === 0 ? "用户" : "IP地址" }}
                    </div>
                    <div class="blacklist-status" :class="isExpired(blacklist.expireTime) ? 'status-expired' : 'status-active'">
                      {{ isExpired(blacklist.expireTime) ? "已过期" : "生效中" }}
                    </div>
                  </div>

                  <!-- 用户ID -->
                  <div v-if="blacklist.userId" class="blacklist-user-mobile">
                    <span class="user-label">用户ID:</span>
                    <span class="user-value">{{ blacklist.userId }}</span>
                  </div>

                  <!-- IP地址 -->
                  <div v-if="blacklist.ip" class="blacklist-ip-mobile">
                    <span class="ip-label">IP地址:</span>
                    <span class="ip-value">{{ blacklist.ip }}</span>
                  </div>

                  <!-- 拉黑原因 -->
                  <div class="blacklist-reason-mobile">
                    <span class="reason-label">拉黑原因:</span>
                    <span class="reason-content">{{ blacklist.reason }}</span>
                  </div>

                  <!-- 时间信息 -->
                  <div class="blacklist-meta">
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">拉黑:</span>
                        <span>{{ blacklist.banTime }}</span>
                      </div>
                      <div class="meta-item time-item" :class="{ 'expired-time': isExpired(blacklist.expireTime) }">
                        <span class="label">到期:</span>
                        <span>{{ blacklist.expireTime }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="blacklist-actions">
                <el-button type="primary" @click="handleEdit(blacklist)" :icon="Edit" class="edit-button" size="small">编辑</el-button>
                <el-button type="danger" @click="handleDelete(blacklist.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout="prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 新增/编辑黑名单对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose" width="600px">
      <el-form ref="blacklistFormRef" :model="blacklistForm" :rules="rules" label-width="100px">
        <el-form-item v-if="!isEdit" prop="userIds" label="用户ID">
          <el-select v-model="blacklistForm.userIds" multiple filterable allow-create default-first-option placeholder="请输入用户ID（支持批量）" style="width: 100%">
            <el-option v-for="id in blacklistForm.userIds" :key="id" :label="id" :value="id" />
          </el-select>
          <div class="form-tip">支持批量添加，输入用户ID后按回车添加</div>
        </el-form-item>

        <el-form-item prop="reason" label="拉黑原因">
          <el-input v-model="blacklistForm.reason" type="textarea" :rows="3" placeholder="请输入拉黑原因" maxlength="500" show-word-limit />
        </el-form-item>

        <el-form-item prop="expireTime" label="到期时间">
          <el-date-picker v-model="blacklistForm.expireTime" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="选择到期时间" style="width: 100%" :disabled-date="disabledDate" />
          <div class="form-tip">选择黑名单到期时间，过期后自动失效</div>
        </el-form-item>

        <el-form-item label="快捷设置">
          <el-button-group>
            <el-button size="small" @click="setExpireTime(1)">1小时</el-button>
            <el-button size="small" @click="setExpireTime(6)">6小时</el-button>
            <el-button size="small" @click="setExpireTime(24)">1天</el-button>
            <el-button size="small" @click="setExpireTime(24 * 7)">7天</el-button>
            <el-button size="small" @click="setExpireTime(24 * 30)">30天</el-button>
            <el-button size="small" @click="setExpireTime(24 * 365)">永久</el-button>
          </el-button-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import { Search, Plus, Delete, Edit } from "@element-plus/icons-vue";
import { getBlacklistList, addBlacklist, searchBlacklist, updateBlacklist, deleteBlacklist } from "@/api/blacklist";

// 黑名单列表数据
const blacklistList = ref([]);
const paginatedBlacklistList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("批量新增黑名单");
const isEdit = ref(false);
const submitLoading = ref(false);
const blacklistFormRef = ref(null);

// 搜索表单
const searchForm = reactive({
  type: "",
  userId: null,
  banTimeStart: null,
  banTimeEnd: null,
  expireTimeStart: null,
  expireTimeEnd: null,
});

// 黑名单表单
const blacklistForm = reactive({
  id: null,
  userIds: [],
  reason: "",
  expireTime: "",
});

// 选中的黑名单
const selectedBlacklists = ref([]);

// 批量操作加载状态
const batchDeleteLoading = ref(false);

// 移动端检测
const isMobileView = ref(false);

// 表单验证规则
const rules = {
  userIds: [{ required: true, message: "请输入用户ID", trigger: "change" }],
  reason: [{ required: true, message: "请输入拉黑原因", trigger: "blur" }],
  expireTime: [{ required: true, message: "请选择到期时间", trigger: "change" }],
};

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 判断是否过期
const isExpired = (expireTime) => {
  if (!expireTime) return false;
  return new Date(expireTime) < new Date();
};

// 禁用过去的日期
const disabledDate = (time) => {
  return time.getTime() < Date.now() - 24 * 60 * 60 * 1000;
};

// 快捷设置到期时间
const setExpireTime = (hours) => {
  const now = new Date();
  const expireDate = new Date(now.getTime() + hours * 60 * 60 * 1000);
  const year = expireDate.getFullYear();
  const month = String(expireDate.getMonth() + 1).padStart(2, "0");
  const day = String(expireDate.getDate()).padStart(2, "0");
  const hour = String(expireDate.getHours()).padStart(2, "0");
  const minute = String(expireDate.getMinutes()).padStart(2, "0");
  const second = String(expireDate.getSeconds()).padStart(2, "0");
  blacklistForm.expireTime = `${year}-${month}-${day} ${hour}:${minute}:${second}`;
};

// 获取黑名单列表
const getBlacklists = async () => {
  loading.value = true;
  try {
    const res = await getBlacklistList();
    blacklistList.value = res.data.data.sort((a, b) => new Date(b.banTime) - new Date(a.banTime));
    total.value = blacklistList.value.length;
    currentPage.value = 1;
    updatePaginatedBlacklistList();
  } catch (error) {
    ElMessage.error("获取黑名单列表失败");
    console.error("获取黑名单列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 更新分页数据
const updatePaginatedBlacklistList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedBlacklistList.value = blacklistList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  updatePaginatedBlacklistList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedBlacklistList();
};

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    // 构建搜索参数
    const searchData = {};
    if (searchForm.type !== "" && searchForm.type !== null && searchForm.type !== undefined) {
      searchData.type = searchForm.type;
    }
    if (searchForm.userId) {
      searchData.userId = parseInt(searchForm.userId);
    }
    if (searchForm.banTimeStart) {
      searchData.banTimeStart = searchForm.banTimeStart;
    }
    if (searchForm.banTimeEnd) {
      searchData.banTimeEnd = searchForm.banTimeEnd;
    }
    if (searchForm.expireTimeStart) {
      searchData.expireTimeStart = searchForm.expireTimeStart;
    }
    if (searchForm.expireTimeEnd) {
      searchData.expireTimeEnd = searchForm.expireTimeEnd;
    }

    const res = await searchBlacklist(searchData);
    blacklistList.value = res.data.data.sort((a, b) => new Date(b.banTime) - new Date(a.banTime));
    total.value = blacklistList.value.length;
    currentPage.value = 1;
    updatePaginatedBlacklistList();
  } catch (error) {
    ElMessage.error("搜索失败");
    console.error("搜索失败:", error);
  } finally {
    loading.value = false;
  }
};

// 智能刷新列表
const refreshBlacklistList = async () => {
  // 检查是否有任何搜索条件
  const hasSearchConditions = searchForm.type !== "" || searchForm.userId || searchForm.banTimeStart || searchForm.banTimeEnd || searchForm.expireTimeStart || searchForm.expireTimeEnd;

  if (hasSearchConditions) {
    await handleSearch();
  } else {
    await getBlacklists();
  }
};

// 表格多选
const handleSelectionChange = (blacklists) => {
  selectedBlacklists.value = blacklists;
};

// 新增黑名单
const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = "批量新增黑名单";
  blacklistForm.id = null;
  blacklistForm.userIds = [];
  blacklistForm.reason = "";
  blacklistForm.expireTime = "";
  dialogVisible.value = true;
};

// 编辑黑名单
const handleEdit = (row) => {
  isEdit.value = true;
  dialogTitle.value = "编辑黑名单";
  blacklistForm.id = row.id;
  blacklistForm.userIds = row.userId ? [row.userId] : [];
  blacklistForm.reason = row.reason;
  blacklistForm.expireTime = row.expireTime;
  dialogVisible.value = true;
};

// 提交表单
const handleSubmit = async () => {
  try {
    await blacklistFormRef.value.validate();
    submitLoading.value = true;

    if (isEdit.value) {
      // 编辑黑名单
      const updateData = {
        id: blacklistForm.id,
        reason: blacklistForm.reason,
        expireTime: blacklistForm.expireTime,
      };
      await updateBlacklist(updateData);
      ElMessage.success("修改成功");
    } else {
      // 批量新增黑名单
      const addData = {
        userIds: blacklistForm.userIds.map((id) => parseInt(id)),
        reason: blacklistForm.reason,
        expireTime: blacklistForm.expireTime,
      };
      await addBlacklist(addData);
      ElMessage.success("添加成功");
    }

    dialogVisible.value = false;
    await refreshBlacklistList();
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? "修改失败" : "添加失败");
      console.error("操作失败:", error);
    }
  } finally {
    submitLoading.value = false;
  }
};

// 删除单个黑名单
const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除该黑名单记录吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteBlacklist([id]);
        ElMessage.success("删除成功");
        await refreshBlacklistList();
      } catch (error) {
        ElMessage.error("删除失败");
        console.error("删除失败:", error);
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedBlacklists.value.length} 条黑名单记录吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const blacklistIds = selectedBlacklists.value.map((blacklist) => blacklist.id);
        await deleteBlacklist(blacklistIds);
        ElMessage.success("批量删除成功");
        await refreshBlacklistList();
      } catch (error) {
        ElMessage.error("批量删除失败");
        console.error("批量删除失败:", error);
      } finally {
        batchDeleteLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 关闭对话框
const handleDialogClose = () => {
  blacklistFormRef.value?.resetFields();
  dialogVisible.value = false;
};

// 初始化
onMounted(() => {
  getBlacklists();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 黑名单管理页面主容器
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  // 主卡片容器 - 包含所有内容
  .card {
    height: 100%;
    padding: 20px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    // 卡片头部 - 标题和搜索区域
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
          background-color: #409eff;
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;
        gap: 10px;

        .search-input {
          width: 200px;
          border-radius: 8px;

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2);
              border-color: #409eff;
            }
          }

          :deep(.el-select__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2);
              border-color: #409eff;
            }
          }
        }
      }
    }

    // 时间筛选区域
    .card-time-filters {
      display: flex;
      justify-content: flex-end;
      padding: 10px 10px 0 10px;

      .time-filter-group {
        display: flex;
        align-items: center;
        gap: 12px;

        .time-input {
          width: 160px;
          border-radius: 8px;

          :deep(.el-date-editor.el-input) {
            .el-input__wrapper {
              border-radius: 8px;
              transition: all 0.3s ease;

              &:focus-within {
                box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2);
                border-color: #409eff;
              }
            }
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
    padding-bottom: 60px;

    // 黑名单表格
    .table {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      max-height: calc(100vh - 300px);

      // 表格头部样式
      :deep(.el-table__header-wrapper) {
        background-color: var(--el-bg-color);
        th {
          font-weight: 600;
          color: #475569;
        }
      }

      // 表格主体样式
      :deep(.el-table__body-wrapper) {
        tr td {
          color: #64748b;
          padding: 12px 0;
          vertical-align: middle;

          .cell {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            min-height: 40px;
          }
        }
      }

      // 黑名单类型样式
      .blacklist-type {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.type-user {
          background-color: #e0f2fe;
          color: #0284c7;
        }

        &.type-ip {
          background-color: #fef3c7;
          color: #d97706;
        }
      }

      // IP地址样式
      .blacklist-ip {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        cursor: pointer;
        font-size: 12px;
        color: #666;

        &:hover {
          color: #409eff;
        }
      }

      // 拉黑原因样式
      .blacklist-reason {
        overflow: hidden;
        cursor: pointer;
        display: -webkit-box;
        text-overflow: ellipsis;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;

        &:hover {
          color: #409eff;
        }
      }

      // 过期时间样式
      .expired-time {
        color: #999;
        text-decoration: line-through;
      }

      // 状态样式
      .blacklist-status {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.status-active {
          background-color: #fee2e2;
          color: #ef4444;
        }

        &.status-expired {
          background-color: #f0f0f0;
          color: #999;
        }
      }

      // 表格操作按钮组
      .table-actions {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        height: 100%;
        min-height: 60px;
        gap: 5px;

        :deep(.el-button) {
          margin-left: 0;
        }

        // 通用按钮样式
        .edit-button,
        .delete-button {
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
          }
        }

        // 编辑按钮
        .edit-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;

          &:hover {
            background-color: #bae6fd;
            border-color: #bae6fd;
            box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
          }
        }

        // 删除按钮
        .delete-button {
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;

          &:hover {
            background-color: #fecaca;
            border-color: #fecaca;
            box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
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
    padding-bottom: 60px;
    overflow-y: auto;

    // 黑名单卡片列表容器
    .blacklist-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      // 单个黑名单卡片
      .blacklist-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        // 黑名单卡片内容容器
        .blacklist-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 黑名单头部区域
          .blacklist-header-section {
            display: flex;
            flex-direction: column;

            // 黑名单信息区域
            .blacklist-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              // 黑名单头部 - ID、类型和状态
              .blacklist-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-bottom: 5px;

                .blacklist-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }

                .blacklist-type {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 500;

                  &.type-user {
                    background-color: #e0f2fe;
                    color: #0284c7;
                  }

                  &.type-ip {
                    background-color: #fef3c7;
                    color: #d97706;
                  }
                }

                .blacklist-status {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 500;

                  &.status-active {
                    background-color: #fee2e2;
                    color: #ef4444;
                  }

                  &.status-expired {
                    background-color: #f0f0f0;
                    color: #999;
                  }
                }
              }

              // 移动端用户ID样式
              .blacklist-user-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .user-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .user-value {
                  color: #555;
                  font-weight: 500;
                }
              }

              // 移动端IP地址样式
              .blacklist-ip-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .ip-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .ip-value {
                  color: #555;
                  font-weight: 400;
                  word-break: break-all;
                }
              }

              // 移动端拉黑原因
              .blacklist-reason-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: flex-start;

                .reason-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                  flex-shrink: 0;
                }

                .reason-content {
                  color: #555;
                  font-weight: 400;
                  flex: 1;
                  word-break: break-all;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                }
              }

              // 黑名单元信息
              .blacklist-meta {
                display: flex;
                flex-direction: column;
                gap: 8px;

                .meta-item {
                  font-size: 12px;
                  color: #666;
                  display: flex;
                  align-items: center;

                  .label {
                    font-weight: 500;
                    margin-right: 4px;
                  }

                  &.expired-time {
                    color: #999;
                    text-decoration: line-through;
                  }
                }

                // 时间信息行
                .time-row {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 8px;
                  margin-top: 4px;

                  .time-item {
                    font-size: 11px;
                    color: #888;

                    .label {
                      color: #999;
                    }
                  }
                }
              }
            }
          }

          // 黑名单操作按钮
          .blacklist-actions {
            display: flex;
            gap: 6px;
            justify-content: center;
            padding-top: 8px;
            border-top: 1px solid #f0f0f0;

            .el-button {
              margin-left: 0;
              font-size: 12px;
              padding: 6px 10px;
              height: auto;
              border-radius: 4px;
              flex: 1;
            }
          }
        }
      }
    }
  }

  // 分页容器
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

// 表单提示
.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

// 响应式设计
@media screen and (max-width: 1400px) {
  .management-container .card .card-header .card-actions .search-input {
    width: 140px;
  }
}

@media screen and (max-width: 1220px) {
  .management-container .card .card-header {
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

  .management-container .card .card-time-filters {
    .time-filter-group {
      .time-input {
        width: 140px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .management-container .card {
    padding: 2px;

    .card-header {
      padding: 6px;

      .card-title {
        font-size: 16px;
      }
    }

    .card-time-filters {
      padding: 8px;

      .time-filter-group {
        display: flex;
        flex-direction: row;
        gap: 8px;
        width: 100%;

        .time-input {
          flex: 1;
          width: auto !important;
        }
      }
    }

    .table {
      margin-top: 0;
      max-height: calc(100vh - 240px);
    }

    .pagination-container {
      padding: 4px;

      :deep(.el-pagination .el-pager) {
        display: none;
      }
    }
  }
}
</style>
