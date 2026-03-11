<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">操作日志管理</h2>
        <div class="card-actions">
          <el-select v-model="searchForm.operatorRole" placeholder="操作角色" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="管理员" value="admin" />
            <el-option label="查看者" value="viewer" />
          </el-select>
          <el-select v-model="searchForm.status" placeholder="操作状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="成功" :value="0" />
            <el-option label="失败" :value="1" />
            <el-option label="异常" :value="2" />
          </el-select>
          <el-input v-model.number="searchForm.operatorId" placeholder="操作人员 ID" :prefix-icon="Search" size="small" class="search-input" clearable @input="handleSearch" />
        </div>
      </div>

      <!-- 时间筛选区域 -->
      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker v-model="searchForm.createTimeStart" type="datetime" placeholder="操作开始时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
          <el-date-picker v-model="searchForm.createTimeEnd" type="datetime" placeholder="操作结束时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
        </div>
      </div>

      <!-- 批量操作按钮区域 -->
      <div class="card-third">
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedLogs.length === 0" :loading="batchDeleteLoading">批量删除</el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="logList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }" max-height="calc(100vh - 300px)">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="id" label="ID" width="55" />
          <el-table-column prop="operatorName" label="操作人员" width="120">
            <template #default="{ row }">
              <span>{{ row.operatorName || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="operatorRole" label="角色" width="80">
            <template #default="{ row }">
              <div class="role-type" :class="getRoleTypeClass(row.operatorRole)">
                {{ row.operatorRole === 'admin' ? '管理员' : '查看者' }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="module" label="功能模块" min-width="100">
            <template #default="{ row }">
              <div class="log-module">{{ row.module || "-" }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="操作描述" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <span>{{ row.description || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="operation" label="操作类型" width="80">
            <template #default="{ row }">
              <div class="operation-type" :class="getOperationTypeClass(row.operation)">
                {{ row.operation }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="requestMethod" label="请求方式" width="80">
            <template #default="{ row }">
              <span class="request-method">{{ row.requestMethod || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="requestUrl" label="请求 URL" min-width="150" show-overflow-tooltip>
            <template #default="{ row }">
              <span class="request-url">{{ row.requestUrl || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="ip" label="操作 IP" width="110">
            <template #default="{ row }">
              <span>{{ row.ip || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="address" label="操作地址" min-width="100" show-overflow-tooltip>
            <template #default="{ row }">
              <span>{{ row.address || "-" }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="75">
            <template #default="{ row }">
              <div class="log-status" :class="getStatusClass(row.status)">
                {{ getStatusText(row.status) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="耗时(ms)" width="107" sortable>
            <template #default="{ row }">
              <span :class="getTimeClass(row.time)">{{ row.time }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="操作时间" sortable width="110" />
          <el-table-column label="操作" width="170" fixed="right">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="info" size="small" @click="handleViewDetail(row.id)" :icon="InfoFilled" class="detail-button">详情</el-button>
                <el-button type="danger" size="small" @click="handleDelete(row.id)" :icon="Delete" class="delete-button">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="log-cards">
          <el-card v-for="log in logList" :key="log.id" class="log-card" :class="{ 'is-selected': isLogSelected(log.id) }">
            <div class="log-card-content">
              <div class="log-header-section">
                <div class="log-info">
                  <div class="log-header">
                    <el-checkbox :model-value="isLogSelected(log.id)" @change="handleMobileSelect(log)" class="mobile-checkbox" />
                    <div class="log-id">#{{ log.id }}</div>
                    <div class="role-type" :class="getRoleTypeClass(log.operatorRole)">
                      {{ log.operatorRole === 'admin' ? '管理员' : '查看者' }}
                    </div>
                    <div class="log-status" :class="getStatusClass(log.status)">
                      {{ getStatusText(log.status) }}
                    </div>
                  </div>

                  <!-- 操作人员信息 -->
                  <div class="log-operator-mobile">
                    <span class="operator-label">操作人员:</span>
                    <span class="operator-value">{{ log.operatorName || "未知" }} {{ log.operatorId ? `(ID: ${log.operatorId})` : "" }}</span>
                  </div>

                  <!-- 功能模块 -->
                  <div v-if="log.module" class="log-module-mobile">
                    <span class="module-label">功能模块:</span>
                    <span class="module-value">{{ log.module }}</span>
                  </div>

                  <!-- 操作描述 -->
                  <div v-if="log.description" class="log-description-mobile">
                    <span class="description-label">操作描述:</span>
                    <span class="description-value">{{ log.description }}</span>
                  </div>

                  <!-- 请求方式和 URL -->
                  <div v-if="log.requestMethod || log.requestUrl" class="log-request-mobile">
                    <span class="request-label">请求:</span>
                    <span class="request-value">
                      <span class="request-method-badge" :class="'method-' + log.requestMethod?.toLowerCase()">{{ log.requestMethod || '-' }}</span>
                      <span class="request-url-text">{{ log.requestUrl || '-' }}</span>
                    </span>
                  </div>

                  <!-- 操作 IP 和地址 -->
                  <div v-if="log.ip || log.address" class="log-location-mobile">
                    <span class="location-label">位置:</span>
                    <span class="location-value">{{ log.ip || '-' }} {{ log.address ? `(${log.address})` : '' }}</span>
                  </div>

                  <!-- 操作类型 -->
                  <div class="log-operation-mobile">
                    <span class="operation-label">操作类型:</span>
                    <span class="operation-value">{{ log.operation }}</span>
                  </div>

                  <!-- 时间信息 -->
                  <div class="log-meta">
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">操作时间:</span>
                        <span>{{ log.createTime }}</span>
                      </div>
                      <div class="meta-item time-item">
                        <span class="label">耗时:</span>
                        <span :class="getTimeClass(log.time)">{{ log.time }} ms</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="log-actions">
                <el-button type="info" size="small" @click="handleViewDetail(log.id)" :icon="InfoFilled" class="detail-button">详情</el-button>
                <el-button type="danger" size="small" @click="handleDelete(log.id)" :icon="Delete" class="delete-button">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <Pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="操作日志详情" width="600px" class="detail-dialog">
      <div v-if="detailData" class="detail-content">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="操作 ID">{{ detailData.id }}</el-descriptions-item>
          <el-descriptions-item label="操作人员">{{ detailData.operatorName || '-' }} (ID: {{ detailData.operatorId }})</el-descriptions-item>
          <el-descriptions-item label="操作角色">
            <el-tag :type="detailData.operatorRole === 'admin' ? 'danger' : 'info'" size="small">
              {{ detailData.operatorRole === 'admin' ? '管理员' : '查看者' }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="功能模块">{{ detailData.module || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作描述">{{ detailData.description || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作类型">
            <el-tag :type="getOperationTypeTag(detailData.operation)" size="small">{{ detailData.operation }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="请求方式">
            <span class="request-method">{{ detailData.requestMethod || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="请求 URL">{{ detailData.requestUrl || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作 IP">{{ detailData.ip || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作地址">{{ detailData.address || '-' }}</el-descriptions-item>
          <el-descriptions-item label="操作状态">
            <el-tag :type="getStatusTag(detailData.status)" size="small">{{ getStatusText(detailData.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="耗时 (ms)">{{ detailData.time }}</el-descriptions-item>
          <el-descriptions-item label="操作时间">{{ detailData.createTime }}</el-descriptions-item>
          <el-descriptions-item v-if="detailData.requestParam" label="请求参数">
            <div class="json-content">
              <pre>{{ formatJson(detailData.requestParam) }}</pre>
            </div>
          </el-descriptions-item>
          <el-descriptions-item v-if="detailData.responseResult" label="返回结果">
            <div class="json-content">
              <pre>{{ formatJson(detailData.responseResult) }}</pre>
            </div>
          </el-descriptions-item>
          <el-descriptions-item v-if="detailData.exception" label="异常信息">
            <div class="exception-content">{{ detailData.exception }}</div>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import { Search, Delete, InfoFilled } from "@element-plus/icons-vue";
import { getOperationLogList, searchOperationLog, deleteOperationLogs, getOperationLogDetail } from "@/api/operationLog";
import Pagination from "@/components/Pagination.vue";

// 操作日志列表数据
const logList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 搜索表单
const searchForm = reactive({
  operatorId: null,
  operatorRole: "",
  status: "",
  createTimeStart: null,
  createTimeEnd: null,
});

// 选中的日志
const selectedLogs = ref([]);

// 批量操作加载状态
const batchDeleteLoading = ref(false);

// 移动端检测
const isMobileView = ref(false);

// 详情弹窗
const detailDialogVisible = ref(false);
const detailData = ref(null);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 获取角色类型样式类
const getRoleTypeClass = (role) => {
  if (role === "admin") return "role-admin";
  if (role === "viewer") return "role-viewer";
  return "";
};

// 获取操作类型样式类
const getOperationTypeClass = (operation) => {
  if (operation === "新增") return "operation-add";
  if (operation === "修改") return "operation-edit";
  if (operation === "删除") return "operation-delete";
  if (operation === "查询") return "operation-search";
  return "";
};

// 获取操作类型标签颜色
const getOperationTypeTag = (operation) => {
  if (operation === "新增") return "success";
  if (operation === "修改") return "warning";
  if (operation === "删除") return "danger";
  if (operation === "查询") return "info";
  return "";
};

// 获取状态样式类
const getStatusClass = (status) => {
  if (status === 0) return "status-success";
  if (status === 1) return "status-failed";
  if (status === 2) return "status-error";
  return "";
};

// 获取状态标签颜色
const getStatusTag = (status) => {
  if (status === 0) return "success";
  if (status === 1) return "danger";
  if (status === 2) return "warning";
  return "";
};

// 获取状态文本
const getStatusText = (status) => {
  if (status === 0) return "成功";
  if (status === 1) return "失败";
  if (status === 2) return "异常";
  return "未知";
};

// 获取耗时样式类
const getTimeClass = (time) => {
  if (time < 100) return "time-fast";
  if (time < 500) return "time-normal";
  return "time-slow";
};

// 格式化 JSON
const formatJson = (json) => {
  try {
    if (typeof json === "string") {
      return JSON.stringify(JSON.parse(json), null, 2);
    }
    return JSON.stringify(json, null, 2);
  } catch {
    return json;
  }
};

// 判断是否存在搜索条件
const hasSearchConditions = () => {
  return !!(
    searchForm.operatorId ||
    searchForm.operatorRole ||
    searchForm.status !== "" ||
    searchForm.createTimeStart ||
    searchForm.createTimeEnd
  );
};

// 构建搜索参数
const buildSearchPayload = () => {
  const searchData = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
  };

  if (searchForm.operatorId) {
    searchData.operatorId = Number(searchForm.operatorId);
  }
  if (searchForm.operatorRole) {
    searchData.operatorRole = searchForm.operatorRole;
  }
  if (searchForm.status !== "" && searchForm.status !== null && searchForm.status !== undefined) {
    searchData.status = searchForm.status;
  }
  if (searchForm.createTimeStart) {
    searchData.createTimeStart = searchForm.createTimeStart;
  }
  if (searchForm.createTimeEnd) {
    searchData.createTimeEnd = searchForm.createTimeEnd;
  }

  return searchData;
};

// 应用分页响应
const applyPageData = (pageData) => {
  logList.value = pageData?.data || [];
  total.value = Number(pageData?.total || 0);
  selectedLogs.value = [];
};

// 获取操作日志列表
const fetchOperationLogs = async () => {
  loading.value = true;
  try {
    let pageData = null;
    if (hasSearchConditions()) {
      const res = await searchOperationLog(buildSearchPayload());
      pageData = res.data.data;
    } else {
      const res = await getOperationLogList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      });
      pageData = res.data.data;
    }
    applyPageData(pageData);
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? "搜索失败" : "获取操作日志列表失败");
    console.error(hasSearchConditions() ? "搜索失败:" : "获取操作日志列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 获取操作日志列表并重置分页
const getOperationLogs = async () => {
  currentPage.value = 1;
  await fetchOperationLogs();
};

// 处理分页大小变化
const handleSizeChange = async (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  await fetchOperationLogs();
};

// 处理当前页码变化
const handleCurrentChange = async (current) => {
  currentPage.value = current;
  await fetchOperationLogs();
};

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1;
  await fetchOperationLogs();
};

// 智能刷新列表
const refreshLogList = async (deletedCount = 0) => {
  if (deletedCount > 0 && currentPage.value > 1 && logList.value.length <= deletedCount) {
    currentPage.value -= 1;
  }
  await fetchOperationLogs();
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

// 查看详情
const handleViewDetail = async (id) => {
  try {
    const res = await getOperationLogDetail(id);
    detailData.value = res.data.data;
    detailDialogVisible.value = true;
  } catch (error) {
    ElMessage.error("获取详情失败");
    console.error("获取详情失败:", error);
  }
};

// 删除单个日志
const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除该操作日志吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteOperationLogs([id]);
        ElMessage.success("删除成功");
        await refreshLogList(1);
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedLogs.value.length} 条操作日志吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const logIds = selectedLogs.value.map((log) => log.id);
        await deleteOperationLogs(logIds);
        ElMessage.success("批量删除成功");
        await refreshLogList(logIds.length);
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
  getOperationLogs();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 操作日志管理页面主容器
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

    // 操作日志表格
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

      // 功能模块样式
      .log-module {
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

      // 操作描述样式
      .log-description {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        color: #666;
        max-width: 170px;

        &:hover {
          color: #409eff;
        }
      }

      // 角色类型样式
      .role-type {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.role-admin {
          background-color: #fee2e2;
          color: #dc2626;
        }

        &.role-viewer {
          background-color: #e0f2fe;
          color: #0284c7;
        }
      }

      // 操作类型样式
      .operation-type {
        font-size: 12px;
        color: #666;
      }

      // 请求方式样式
      .request-method {
        font-family: 'Courier New', monospace;
        font-size: 12px;
        font-weight: 600;
        color: #666;
      }

      // 请求 URL 样式
      .request-url {
        font-size: 12px;
        color: #666;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
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
          color: #dc2626;
        }

        &.status-error {
          background-color: #fef3c7;
          color: #d97706;
        }
      }

      // 耗时样式
      .time-fast {
        color: #059669;
        font-weight: 500;
      }

      .time-normal {
        color: #d97706;
        font-weight: 500;
      }

      .time-slow {
        color: #dc2626;
        font-weight: 600;
      }

      // 表格操作按钮组
      .table-actions {
        display: flex;
        align-items: center;
        justify-content: center;
        flex-wrap: wrap;
        gap: 8px;

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

        .delete-button {
          margin-left: 0 !important;
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
  }

  // 移动端卡片视图
  .mobile-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    padding-bottom: 60px;
    overflow-y: auto;

    // 操作日志卡片列表容器
    .log-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      // 单个操作日志卡片
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

        // 操作日志卡片内容容器
        .log-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 操作日志头部区域
          .log-header-section {
            display: flex;
            flex-direction: column;

            // 操作日志信息区域
            .log-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              // 操作日志头部 - ID、角色和状态
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

                .role-type {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 500;

                  &.role-admin {
                    background-color: #fee2e2;
                    color: #dc2626;
                  }

                  &.role-viewer {
                    background-color: #e0f2fe;
                    color: #0284c7;
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
                    color: #dc2626;
                  }

                  &.status-error {
                    background-color: #fef3c7;
                    color: #d97706;
                  }
                }
              }

              // 移动端操作人员样式
              .log-operator-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .operator-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .operator-value {
                  color: #555;
                  font-weight: 500;
                }
              }

              // 移动端功能模块样式
              .log-module-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .module-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .module-value {
                  color: #555;
                  font-weight: 400;
                }
              }

              // 移动端操作描述样式
              .log-description-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .description-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .description-value {
                  color: #555;
                  font-weight: 400;
                }
              }

              // 移动端操作类型样式
              .log-operation-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .operation-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .operation-value {
                  color: #555;
                  font-weight: 500;
                }
              }

              // 移动端请求信息样式
              .log-request-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: flex-start;

                .request-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                  flex-shrink: 0;
                }

                .request-value {
                  display: flex;
                  flex-direction: column;
                  gap: 4px;
                  width: 100%;

                  .request-method-badge {
                    font-family: 'Courier New', monospace;
                    font-size: 12px;
                    font-weight: 600;
                    color: #666;
                    width: fit-content;
                  }

                  .request-url-text {
                    font-size: 12px;
                    color: #666;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                  }
                }
              }

              // 移动端位置信息样式
              .log-location-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .location-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .location-value {
                  color: #555;
                  font-size: 12px;
                }
              }

              // 操作日志元信息
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

          // 操作日志操作按钮
          .log-actions {
            display: flex;
            gap: 8px;
            justify-content: center;
            padding-top: 8px;
            border-top: 1px solid #f0f0f0;

            .detail-button {
              background-color: #f5f5f5;
              color: #606266;
              border-color: #dcdfe6;

              &:hover {
                background-color: #e9e9e9;
                border-color: #c0c4cc;
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
          }
        }
      }
    }
  }
}

// 详情弹窗样式
.detail-dialog {
  .detail-content {
    max-height: 70vh;
    overflow-y: auto;

    .json-content {
      background-color: #f5f7fa;
      padding: 8px;
      border-radius: 4px;
      max-height: 200px;
      overflow-y: auto;

      pre {
        margin: 0;
        font-family: 'Courier New', monospace;
        font-size: 12px;
        white-space: pre-wrap;
        word-wrap: break-word;
      }
    }

    .exception-content {
      background-color: #fef0f0;
      color: #f56c6c;
      padding: 8px;
      border-radius: 4px;
      font-family: 'Courier New', monospace;
      font-size: 12px;
      max-height: 200px;
      overflow-y: auto;
    }

    :deep(.el-descriptions__label) {
      font-weight: 600;
      width: 100px;
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
