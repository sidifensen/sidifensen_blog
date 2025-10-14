<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">登录日志管理</h2>
        <div class="card-actions">
          <el-select v-model="searchForm.loginType" placeholder="登录方式" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="用户名/邮箱" :value="0" />
            <el-option label="Gitee" :value="1" />
            <el-option label="GitHub" :value="2" />
            <el-option label="QQ" :value="3" />
          </el-select>
          <el-select v-model="searchForm.status" placeholder="登录状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="成功" :value="0" />
            <el-option label="失败" :value="1" />
          </el-select>
          <el-input v-model.number="searchForm.userId" placeholder="用户ID" :prefix-icon="Search" size="small" class="search-input" clearable @input="handleSearch" />
        </div>
      </div>

      <!-- 时间筛选区域 -->
      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker v-model="searchForm.loginTimeStart" type="datetime" placeholder="登录开始时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
          <el-date-picker v-model="searchForm.loginTimeEnd" type="datetime" placeholder="登录结束时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
        </div>
      </div>

      <!-- 批量操作按钮区域 -->
      <div class="card-third">
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedLogs.length === 0" :loading="batchDeleteLoading">批量删除</el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedLogList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="userId" label="用户ID" width="80">
            <template #default="{ row }">
              <span>{{ row.userId || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="username" label="用户名" width="170">
            <template #default="{ row }">
              <el-tooltip v-if="row.username" :content="row.username" placement="top-start">
                <div class="log-username">{{ row.username }}</div>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="loginType" label="登录方式" width="120">
            <template #default="{ row }">
              <div class="login-type" :class="getLoginTypeClass(row.loginType)">
                {{ row.loginTypeDesc }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="loginIp" label="登录IP" width="150">
            <template #default="{ row }">
              <el-tooltip v-if="row.loginIp" :content="row.loginIp" placement="top-start">
                <div class="log-ip">{{ row.loginIp }}</div>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="loginAddress" label="登录地址" min-width="150">
            <template #default="{ row }">
              <el-tooltip v-if="row.loginAddress" :content="row.loginAddress" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="log-address">{{ row.loginAddress }}</div>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <div class="log-status" :class="row.status === 0 ? 'status-success' : 'status-failed'">
                {{ row.statusDesc }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="loginTime" label="登录时间" sortable width="180" />
          <el-table-column label="操作" width="100">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="danger" @click="handleDelete(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="log-cards">
          <el-card v-for="log in paginatedLogList" :key="log.id" class="log-card" :class="{ 'is-selected': isLogSelected(log.id) }">
            <div class="log-card-content">
              <div class="log-header-section">
                <div class="log-info">
                  <div class="log-header">
                    <el-checkbox :model-value="isLogSelected(log.id)" @change="handleMobileSelect(log)" class="mobile-checkbox" />
                    <div class="log-id">#{{ log.id }}</div>
                    <div class="login-type" :class="getLoginTypeClass(log.loginType)">
                      {{ log.loginTypeDesc }}
                    </div>
                    <div class="log-status" :class="log.status === 0 ? 'status-success' : 'status-failed'">
                      {{ log.statusDesc }}
                    </div>
                  </div>

                  <!-- 用户信息 -->
                  <div class="log-user-mobile">
                    <span class="user-label">用户:</span>
                    <span class="user-value">{{ log.username || "未知" }} (ID: {{ log.userId || "-" }})</span>
                  </div>

                  <!-- IP地址 -->
                  <div v-if="log.loginIp" class="log-ip-mobile">
                    <span class="ip-label">IP地址:</span>
                    <span class="ip-value">{{ log.loginIp }}</span>
                  </div>

                  <!-- 登录地址 -->
                  <div v-if="log.loginAddress" class="log-address-mobile">
                    <span class="address-label">登录地址:</span>
                    <span class="address-content">{{ log.loginAddress }}</span>
                  </div>

                  <!-- 时间信息 -->
                  <div class="log-meta">
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">登录时间:</span>
                        <span>{{ log.loginTime }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="log-actions">
                <el-button type="danger" @click="handleDelete(log.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <Pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import { Search, Delete } from "@element-plus/icons-vue";
import { getLoginLogList, searchLoginLog, deleteLoginLogs } from "@/api/loginLog";
import Pagination from "@/components/Pagination.vue";

// 登录日志列表数据
const logList = ref([]);
const paginatedLogList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 搜索表单
const searchForm = reactive({
  userId: null,
  loginType: "",
  status: "",
  loginTimeStart: null,
  loginTimeEnd: null,
});

// 选中的日志
const selectedLogs = ref([]);

// 批量操作加载状态
const batchDeleteLoading = ref(false);

// 移动端检测
const isMobileView = ref(false);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 获取登录方式样式类
const getLoginTypeClass = (loginType) => {
  if (loginType === 0) return "type-email";
  if (loginType === 1) return "type-gitee";
  if (loginType === 2) return "type-github";
  if (loginType === 3) return "type-qq";
  return "";
};

// 获取登录日志列表
const getLoginLogs = async () => {
  loading.value = true;
  try {
    const res = await getLoginLogList();
    logList.value = res.data.data;
    total.value = logList.value.length;
    currentPage.value = 1;
    updatePaginatedLogList();
  } catch (error) {
    ElMessage.error("获取登录日志列表失败");
    console.error("获取登录日志列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 更新分页数据
const updatePaginatedLogList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedLogList.value = logList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  updatePaginatedLogList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedLogList();
};

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    // 构建搜索参数
    const searchData = {};
    if (searchForm.userId) {
      searchData.userId = parseInt(searchForm.userId);
    }
    if (searchForm.loginType !== "" && searchForm.loginType !== null && searchForm.loginType !== undefined) {
      searchData.loginType = searchForm.loginType;
    }
    if (searchForm.status !== "" && searchForm.status !== null && searchForm.status !== undefined) {
      searchData.status = searchForm.status;
    }
    if (searchForm.loginTimeStart) {
      searchData.loginTimeStart = searchForm.loginTimeStart;
    }
    if (searchForm.loginTimeEnd) {
      searchData.loginTimeEnd = searchForm.loginTimeEnd;
    }

    const res = await searchLoginLog(searchData);
    logList.value = res.data.data;
    total.value = logList.value.length;
    currentPage.value = 1;
    updatePaginatedLogList();
  } catch (error) {
    ElMessage.error("搜索失败");
    console.error("搜索失败:", error);
  } finally {
    loading.value = false;
  }
};

// 智能刷新列表
const refreshLogList = async () => {
  // 检查是否有任何搜索条件
  const hasSearchConditions = searchForm.userId || searchForm.loginType !== "" || searchForm.status !== "" || searchForm.loginTimeStart || searchForm.loginTimeEnd;

  if (hasSearchConditions) {
    await handleSearch();
  } else {
    await getLoginLogs();
  }
};

// 表格多选
const handleSelectionChange = (logs) => {
  selectedLogs.value = logs;
};

// 检查日志是否被选中
const isLogSelected = (logId) => {
  return selectedLogs.value.some((log) => log.id === logId);
};

// 移动端选择处理
const handleMobileSelect = (log) => {
  const index = selectedLogs.value.findIndex((item) => item.id === log.id);
  if (index > -1) {
    // 已选中，取消选中
    selectedLogs.value.splice(index, 1);
  } else {
    // 未选中，添加到选中列表
    selectedLogs.value.push(log);
  }
};

// 删除单个日志
const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除该登录日志吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteLoginLogs([id]);
        ElMessage.success("删除成功");
        await refreshLogList();
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedLogs.value.length} 条登录日志吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const logIds = selectedLogs.value.map((log) => log.id);
        await deleteLoginLogs(logIds);
        ElMessage.success("批量删除成功");
        await refreshLogList();
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

// 初始化
onMounted(() => {
  getLoginLogs();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 登录日志管理页面主容器
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
          width: 160px;
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

    // 登录日志表格
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

      // 用户名样式
      .log-username {
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

      // 登录方式样式
      .login-type {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.type-email {
          background-color: #e0f2fe;
          color: #0284c7;
        }

        &.type-gitee {
          background-color: #fee2e2;
          color: #dc2626;
        }

        &.type-github {
          background-color: #f3f4f6;
          color: #374151;
        }

        &.type-qq {
          background-color: #dbeafe;
          color: #1e40af;
        }
      }

      // IP地址样式
      .log-ip {
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

      // 登录地址样式
      .log-address {
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

      // 状态样式
      .log-status {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.status-success {
          background-color: #d1fae5;
          color: #059669;
        }

        &.status-failed {
          background-color: #fee2e2;
          color: #ef4444;
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

        // 删除按钮
        .delete-button {
          border-radius: 6px;
          transition: all 0.3s ease;
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;

          &:hover {
            background-color: #fecaca;
            border-color: #fecaca;
            box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
            transform: translateY(-2px);
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

    // 登录日志卡片列表容器
    .log-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      // 单个登录日志卡片
      .log-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        // 选中状态样式
        &.is-selected {
          border: 2px solid #409eff;
          box-shadow: 0 0 12px rgba(64, 158, 255, 0.3);
        }

        // 登录日志卡片内容容器
        .log-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 登录日志头部区域
          .log-header-section {
            display: flex;
            flex-direction: column;

            // 登录日志信息区域
            .log-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              // 登录日志头部 - ID、类型和状态
              .log-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-bottom: 5px;

                // 移动端复选框样式
                .mobile-checkbox {
                  flex-shrink: 0;

                  :deep(.el-checkbox__inner) {
                    width: 18px;
                    height: 18px;
                  }
                }

                .log-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }

                .login-type {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 500;

                  &.type-email {
                    background-color: #e0f2fe;
                    color: #0284c7;
                  }

                  &.type-gitee {
                    background-color: #fee2e2;
                    color: #dc2626;
                  }

                  &.type-github {
                    background-color: #f3f4f6;
                    color: #374151;
                  }

                  &.type-qq {
                    background-color: #dbeafe;
                    color: #1e40af;
                  }
                }

                .log-status {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 500;

                  &.status-success {
                    background-color: #d1fae5;
                    color: #059669;
                  }

                  &.status-failed {
                    background-color: #fee2e2;
                    color: #ef4444;
                  }
                }
              }

              // 移动端用户信息样式
              .log-user-mobile {
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
              .log-ip-mobile {
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

              // 移动端登录地址
              .log-address-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: flex-start;

                .address-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                  flex-shrink: 0;
                }

                .address-content {
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

              // 登录日志元信息
              .log-meta {
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

          // 登录日志操作按钮
          .log-actions {
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

  }
}
</style>
