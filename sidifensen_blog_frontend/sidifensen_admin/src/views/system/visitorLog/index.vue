<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">访客日志管理</h2>
        <div class="card-actions">
          <el-select v-model="searchForm.device" placeholder="设备类型" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="PC" value="PC" />
            <el-option label="Mobile" value="Mobile" />
          </el-select>
          <el-input v-model.number="searchForm.userId" placeholder="用户ID" :prefix-icon="Search" size="small" class="search-input" clearable @input="handleSearch" />
          <el-input v-model="searchForm.ip" placeholder="访客IP地址" :prefix-icon="Search" size="small" class="search-input" clearable @input="handleSearch" />
        </div>
      </div>

      <!-- 时间筛选区域 -->
      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker v-model="searchForm.visitTimeStart" type="datetime" placeholder="访问开始时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
          <el-date-picker v-model="searchForm.visitTimeEnd" type="datetime" placeholder="访问结束时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
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
              <span v-else class="visitor-guest">游客</span>
            </template>
          </el-table-column>
          <el-table-column prop="ip" label="访客IP" width="150">
            <template #default="{ row }">
              <el-tooltip v-if="row.ip" :content="row.ip" placement="top-start">
                <div class="log-ip">{{ row.ip }}</div>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="地理位置" min-width="150">
            <template #default="{ row }">
              <el-tooltip v-if="row.address" :content="row.address" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="log-address">{{ row.address }}</div>
              </el-tooltip>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column prop="device" label="设备类型" width="100">
            <template #default="{ row }">
              <div class="device-type" :class="getDeviceTypeClass(row.device)">
                {{ row.device }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="visitTime" label="访问时间" sortable width="180" />
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
          <el-card v-for="log in paginatedLogList" :key="log.id" class="log-card">
            <div class="log-card-content">
              <div class="log-header-section">
                <div class="log-info">
                  <div class="log-header">
                    <div class="log-id">#{{ log.id }}</div>
                    <div class="device-type" :class="getDeviceTypeClass(log.device)">
                      {{ log.device }}
                    </div>
                  </div>

                  <!-- 用户信息 -->
                  <div class="log-user-mobile">
                    <span class="user-label">用户:</span>
                    <span class="user-value">{{ log.username || "游客" }} {{ log.userId ? `(ID: ${log.userId})` : "" }}</span>
                  </div>

                  <!-- IP地址 -->
                  <div v-if="log.ip" class="log-ip-mobile">
                    <span class="ip-label">IP地址:</span>
                    <span class="ip-value">{{ log.ip }}</span>
                  </div>

                  <!-- 地理位置 -->
                  <div v-if="log.address" class="log-address-mobile">
                    <span class="address-label">地理位置:</span>
                    <span class="address-content">{{ log.address }}</span>
                  </div>

                  <!-- 时间信息 -->
                  <div class="log-meta">
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">访问时间:</span>
                        <span>{{ log.visitTime }}</span>
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
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout="prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import { Search, Delete } from "@element-plus/icons-vue";
import { getVisitorLogList, searchVisitorLog, deleteVisitorLogs } from "@/api/visitorLog";

// 访客日志列表数据
const logList = ref([]);
const paginatedLogList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 搜索表单
const searchForm = reactive({
  userId: null,
  ip: "",
  device: "",
  visitTimeStart: null,
  visitTimeEnd: null,
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

// 获取设备类型样式类
const getDeviceTypeClass = (device) => {
  if (device === "PC") return "device-pc";
  if (device === "Mobile") return "device-mobile";
  return "";
};

// 获取访客日志列表
const getVisitorLogs = async () => {
  loading.value = true;
  try {
    const res = await getVisitorLogList();
    logList.value = res.data.data;
    total.value = logList.value.length;
    currentPage.value = 1;
    updatePaginatedLogList();
  } catch (error) {
    ElMessage.error("获取访客日志列表失败");
    console.error("获取访客日志列表失败:", error);
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
    if (searchForm.ip) {
      searchData.ip = searchForm.ip.trim();
    }
    if (searchForm.device) {
      searchData.device = searchForm.device;
    }
    if (searchForm.visitTimeStart) {
      searchData.visitTimeStart = searchForm.visitTimeStart;
    }
    if (searchForm.visitTimeEnd) {
      searchData.visitTimeEnd = searchForm.visitTimeEnd;
    }

    const res = await searchVisitorLog(searchData);
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
  const hasSearchConditions = searchForm.userId || searchForm.ip || searchForm.device || searchForm.visitTimeStart || searchForm.visitTimeEnd;

  if (hasSearchConditions) {
    await handleSearch();
  } else {
    await getVisitorLogs();
  }
};

// 表格多选
const handleSelectionChange = (logs) => {
  selectedLogs.value = logs;
};

// 删除单个日志
const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除该访客日志吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteVisitorLogs([id]);
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedLogs.value.length} 条访客日志吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const logIds = selectedLogs.value.map((log) => log.id);
        await deleteVisitorLogs(logIds);
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
  getVisitorLogs();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 访客日志管理页面主容器
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

    // 访客日志表格
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

      // 游客标识
      .visitor-guest {
        font-size: 12px;
        color: #999;
        font-style: italic;
      }

      // 设备类型样式
      .device-type {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.device-pc {
          background-color: #dbeafe;
          color: #1d4ed8;
        }

        &.device-mobile {
          background-color: #fef3c7;
          color: #d97706;
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

      // 地理位置样式
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

    // 访客日志卡片列表容器
    .log-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      // 单个访客日志卡片
      .log-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        // 访客日志卡片内容容器
        .log-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 访客日志头部区域
          .log-header-section {
            display: flex;
            flex-direction: column;

            // 访客日志信息区域
            .log-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              // 访客日志头部 - ID和设备类型
              .log-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-bottom: 5px;

                .log-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }

                .device-type {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 500;

                  &.device-pc {
                    background-color: #dbeafe;
                    color: #1d4ed8;
                  }

                  &.device-mobile {
                    background-color: #fef3c7;
                    color: #d97706;
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

              // 移动端地理位置
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

              // 访客日志元信息
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

          // 访客日志操作按钮
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
