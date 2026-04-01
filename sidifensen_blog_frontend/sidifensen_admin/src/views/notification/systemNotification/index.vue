<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">系统通知管理</h2>
        <div class="card-actions">
          <el-select v-model="searchForm.isRead" placeholder="消息状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="未读" :value="0" />
            <el-option label="已读" :value="1" />
          </el-select>
        </div>
      </div>

      <!-- 时间筛选区域 -->
      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker v-model="searchForm.startTime" type="datetime" placeholder="开始时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
          <el-date-picker v-model="searchForm.endTime" type="datetime" placeholder="结束时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
        </div>
      </div>

      <!-- 批量操作按钮区域 -->
      <div class="card-third">
        <el-button type="primary" plain round @click="handleBatchRead" :disabled="selectedMessages.length === 0" :loading="batchReadLoading">标记已读</el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedMessages.length === 0" :loading="batchDeleteLoading">批量删除</el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedMessageList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="senderId" label="发送者ID" width="100" />
          <el-table-column prop="receiverId" label="接收者ID" width="100" />
          <el-table-column prop="content" label="消息内容" min-width="300">
            <template #default="{ row }">
              <el-tooltip :content="getMessageContent(row)" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="message-content">{{ getMessageContent(row) }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="isRead" label="状态" width="80">
            <template #default="{ row }">
              <div class="message-status" :class="row.isRead === 0 ? 'status-unread' : 'status-read'">
                {{ row.isRead === 0 ? "未读" : "已读" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" sortable width="160" />
          <el-table-column label="操作" width="200">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="primary" @click="handleRead(row)" :icon="Check" class="read-button" size="small" :disabled="row.isRead === 1">已读</el-button>
                <el-button type="danger" @click="handleDelete(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="message-cards">
          <el-card v-for="message in paginatedMessageList" :key="message.id" class="message-card" :class="{ 'is-selected': isMessageSelected(message.id) }">
            <div class="message-card-content">
              <div class="message-header-section">
                <div class="message-info">
                  <div class="message-header">
                    <el-checkbox :model-value="isMessageSelected(message.id)" @change="handleMobileSelect(message)" class="mobile-checkbox" />
                    <div class="message-id">#{{ message.id }}</div>
                    <div class="message-status" :class="message.isRead === 0 ? 'status-unread' : 'status-read'">
                      {{ message.isRead === 0 ? "未读" : "已读" }}
                    </div>
                  </div>

                  <!-- 发送者/接收者 -->
                  <div class="message-meta-mobile">
                    <span class="meta-label">发送者:</span>
                    <span class="meta-value">{{ message.senderId }}</span>
                    <span class="meta-label" style="margin-left: 8px;">接收者:</span>
                    <span class="meta-value">{{ message.receiverId }}</span>
                  </div>

                  <!-- 消息内容 -->
                  <div class="message-content-mobile">
                    <span class="content-label">内容:</span>
                    <span class="content-text">{{ getMessageContent(message) }}</span>
                  </div>

                  <!-- 时间 -->
                  <div class="message-time-mobile">
                    <span class="time-label">时间:</span>
                    <span class="time-value">{{ message.createTime }}</span>
                  </div>
                </div>
              </div>
              <div class="message-actions">
                <el-button type="primary" @click="handleRead(message)" :icon="Check" class="read-button" size="small" :disabled="message.isRead === 1">已读</el-button>
                <el-button type="danger" @click="handleDelete(message.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
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
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import { Check, Delete } from "@element-plus/icons-vue";
import { getMessagePage, readAdminMessages, deleteAdminMessages } from "@/api/message";
import Pagination from "@/components/Pagination.vue";

// 系统通知列表数据
const messageList = ref([]);
const paginatedMessageList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 搜索表单
const searchForm = reactive({
  isRead: "",
  startTime: null,
  endTime: null,
});

// 选中的消息
const selectedMessages = ref([]);

// 批量操作加载状态
const batchReadLoading = ref(false);
const batchDeleteLoading = ref(false);

// 移动端检测
const isMobileView = ref(false);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 解析消息内容（兼容 JSON 和纯文本）
const getMessageContent = (row) => {
  if (!row.content) return "-";
  try {
    const parsed = JSON.parse(row.content);
    return parsed.text || row.content;
  } catch {
    return row.content;
  }
};

// 是否有搜索条件
const hasSearchConditions = () => searchForm.isRead !== "" || searchForm.startTime || searchForm.endTime;

// 获取消息列表
const getMessages = async () => {
  currentPage.value = 1;
  await fetchMessages();
};

const buildQueryParams = () => {
  return {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    isRead: searchForm.isRead !== "" && searchForm.isRead !== null ? searchForm.isRead : undefined,
    startTime: searchForm.startTime || undefined,
    endTime: searchForm.endTime || undefined,
  };
};

const applyPageData = (pageData) => {
  messageList.value = pageData?.data || [];
  paginatedMessageList.value = messageList.value;
  total.value = Number(pageData?.total || 0);
  selectedMessages.value = [];
};

const fetchMessages = async () => {
  loading.value = true;
  try {
    const params = buildQueryParams();
    const res = await getMessagePage(params);
    applyPageData(res.data);
  } catch (error) {
    ElMessage.error("获取系统通知列表失败");
  } finally {
    loading.value = false;
  }
};

// 处理分页大小变化
const handleSizeChange = async (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  await fetchMessages();
};

// 处理当前页码变化
const handleCurrentChange = async (current) => {
  currentPage.value = current;
  await fetchMessages();
};

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1;
  await fetchMessages();
};

// 表格多选
const handleSelectionChange = (messages) => {
  selectedMessages.value = messages;
};

// 检查消息是否被选中
const isMessageSelected = (messageId) => {
  return selectedMessages.value.some((msg) => msg.id === messageId);
};

// 移动端选择处理
const handleMobileSelect = (message) => {
  const index = selectedMessages.value.findIndex((item) => item.id === message.id);
  if (index > -1) {
    selectedMessages.value.splice(index, 1);
  } else {
    selectedMessages.value.push(message);
  }
};

// 标记单条消息已读
const handleRead = async (row) => {
  if (row.isRead === 1) return;
  try {
    await readAdminMessages([row.id]);
    ElMessage.success("标记已读成功");
    await fetchMessages();
  } catch (error) {
    ElMessage.error("标记已读失败");
  }
};

// 批量标记已读
const handleBatchRead = async () => {
  if (selectedMessages.value.length === 0) return;
  ElMessageBox.confirm(`确定要将选中的 ${selectedMessages.value.length} 条消息标记为已读吗？`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      batchReadLoading.value = true;
      try {
        const messageIds = selectedMessages.value.filter((m) => m.isRead === 0).map((m) => m.id);
        if (messageIds.length === 0) {
          ElMessage.info("所选消息均已读");
          return;
        }
        await readAdminMessages(messageIds);
        ElMessage.success("批量标记已读成功");
        await fetchMessages();
      } catch (error) {
        ElMessage.error("批量标记已读失败");
      } finally {
        batchReadLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("操作已取消");
    });
};

// 删除单条消息
const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除该消息吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAdminMessages([id]);
        ElMessage.success("删除成功");
        await refreshMessageList();
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedMessages.value.length} 条消息吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const messageIds = selectedMessages.value.map((m) => m.id);
        await deleteAdminMessages(messageIds);
        ElMessage.success("批量删除成功");
        await refreshMessageList();
      } catch (error) {
        ElMessage.error("批量删除失败");
      } finally {
        batchDeleteLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 智能刷新列表
const refreshMessageList = async (deletedCount = 0) => {
  if (deletedCount > 0 && currentPage.value > 1 && messageList.value.length <= deletedCount) {
    currentPage.value -= 1;
  }
  await fetchMessages();
};

// 初始化
onMounted(() => {
  getMessages();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
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
        }
      }
    }

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
        }
      }
    }

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

    .table {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      max-height: calc(100vh - 300px);

      :deep(.el-table__header-wrapper) {
        background-color: var(--el-bg-color);
        th {
          font-weight: 600;
          color: #475569;
        }
      }

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

      .message-content {
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

      .message-status {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.status-unread {
          background-color: #fef3c7;
          color: #d97706;
        }

        &.status-read {
          background-color: #f0f0f0;
          color: #999;
        }
      }

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

        .read-button,
        .delete-button {
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
          }
        }

        .read-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;

          &:hover {
            background-color: #bae6fd;
            border-color: #bae6fd;
          }

          &:disabled {
            background-color: #f0f0f0;
            color: #999;
            border-color: #f0f0f0;
            transform: none;
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

  // 移动端卡片视图
  .mobile-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    padding-bottom: 60px;
    overflow-y: auto;

    .message-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      .message-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        &.is-selected {
          border: 2px solid #409eff;
          box-shadow: 0 0 12px rgba(64, 158, 255, 0.3);
        }

        .message-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          .message-header-section {
            display: flex;
            flex-direction: column;

            .message-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              .message-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-bottom: 5px;

                .mobile-checkbox {
                  flex-shrink: 0;

                  :deep(.el-checkbox__inner) {
                    width: 18px;
                    height: 18px;
                  }
                }

                .message-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }

                .message-status {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  font-size: 12px;
                  font-weight: 500;

                  &.status-unread {
                    background-color: #fef3c7;
                    color: #d97706;
                  }

                  &.status-read {
                    background-color: #f0f0f0;
                    color: #999;
                  }
                }
              }

              .message-meta-mobile,
              .message-content-mobile,
              .message-time-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: flex-start;

                .meta-label,
                .content-label,
                .time-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                  flex-shrink: 0;
                }

                .meta-value,
                .content-text,
                .time-value {
                  color: #555;
                  font-weight: 400;
                  word-break: break-all;
                }

                .content-text {
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                }
              }
            }
          }

          .message-actions {
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
    .time-filter-group .time-input {
      width: 140px;
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
