<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">公告管理</h2>
        <div class="card-actions">
          <el-button @click="handleSend" class="send-btn">
            <svg class="send-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M12 2L12 6M12 18L12 22M4.93 4.93L7.76 7.76M16.24 16.24L19.07 19.07M2 12L6 12M18 12L22 12M4.93 19.07L7.76 16.24M16.24 7.76L19.07 4.93" stroke-linecap="round"/>
              <circle cx="12" cy="12" r="4"/>
            </svg>
            发送公告
          </el-button>
          <el-select v-model="searchForm.status" placeholder="状态筛选" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="待发送" :value="0" />
            <el-option label="发送中" :value="1" />
            <el-option label="已发送" :value="2" />
            <el-option label="发送失败" :value="3" />
          </el-select>
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedList" class="table" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="title" label="标题" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="row.title" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="announcement-title">{{ row.title }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="content" label="内容" min-width="250">
            <template #default="{ row }">
              <el-tooltip :content="row.content" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="announcement-content">{{ row.content }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="sendMethod" label="发送方式" width="180">
            <template #default="{ row }">
              <div class="send-methods">
                <span v-for="method in parseSendMethod(row.sendMethod)" :key="method" class="method-tag" :class="'method-' + method">
                  {{ methodLabel(method) }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="targetType" label="发送对象" width="100">
            <template #default="{ row }">
              <span>{{ row.targetType === 1 ? '全部用户' : '指定用户' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <div class="status-badge" :class="'status-' + row.status">
                {{ statusLabel(row.status) }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="sendTime" label="发送时间" width="160" />
          <el-table-column prop="createTime" label="创建时间" sortable width="160" />
          <el-table-column label="操作" width="150">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button v-if="row.status === 0" type="warning" @click="handleCancel(row)" :icon="Close" class="cancel-button" size="small">取消</el-button>
                <el-button type="danger" @click="handleDelete(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="announcement-cards">
          <el-card v-for="item in paginatedList" :key="item.id" class="announcement-card">
            <div class="card-content">
              <div class="card-header-row">
                <div class="card-id">#{{ item.id }}</div>
                <div class="status-badge" :class="'status-' + item.status">
                  {{ statusLabel(item.status) }}
                </div>
              </div>
              <div class="card-title-row">{{ item.title }}</div>
              <div class="card-content-row">{{ item.content }}</div>
              <div class="card-meta-row">
                <span class="send-methods">
                  <span v-for="method in parseSendMethod(item.sendMethod)" :key="method" class="method-tag" :class="'method-' + method">
                    {{ methodLabel(method) }}
                  </span>
                </span>
                <span class="card-time">{{ item.createTime }}</span>
              </div>
              <div class="card-actions">
                <el-button v-if="item.status === 0" type="warning" @click="handleCancel(item)" size="small">取消</el-button>
                <el-button type="danger" @click="handleDelete(item.id)" size="small">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <Pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 发送公告对话框 -->
    <el-dialog v-model="dialogVisible" title="发送公告" width="600px" class="announcement-dialog">
      <el-form ref="announcementFormRef" :model="announcementForm" :rules="rules" label-width="100px">
        <!-- 发送方式 -->
        <el-form-item label="发送方式" prop="sendMethod">
          <el-checkbox-group v-model="announcementForm.sendMethod">
            <el-checkbox label="system">系统通知（指定用户）</el-checkbox>
            <el-checkbox label="website">公告（全部用户）</el-checkbox>
            <el-checkbox label="email">邮件</el-checkbox>
          </el-checkbox-group>
        </el-form-item>

        <!-- 标题 -->
        <el-form-item label="标题" prop="title">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>

        <!-- 内容 -->
        <el-form-item label="内容" prop="content">
          <el-input v-model="announcementForm.content" type="textarea" :rows="4" placeholder="请输入公告内容" maxlength="2000" show-word-limit />
        </el-form-item>

        <!-- 指定用户选择（system 方式时显示） -->
        <el-form-item v-if="announcementForm.sendMethod.includes('system') || announcementForm.sendMethod.includes('email')" label="发送对象">
          <el-radio-group v-model="announcementForm.targetType">
            <el-radio :value="1">全部用户</el-radio>
            <el-radio :value="2">指定用户</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="announcementForm.targetType === 2 && (announcementForm.sendMethod.includes('system') || announcementForm.sendMethod.includes('email'))" label="选择用户">
          <el-select v-model="announcementForm.targetUsers" multiple filterable placeholder="搜索或选择用户" style="width: 100%">
            <el-option v-for="user in userList" :key="user.id" :label="user.nickname + ' (ID:' + user.id + ')'" :value="user.id" />
          </el-select>
          <div class="form-tip">支持多选，也可直接输入用户ID搜索</div>
        </el-form-item>

        <!-- website 方式时显示提示 -->
        <div v-if="announcementForm.sendMethod.includes('website') && announcementForm.sendMethod.length === 1" class="website-tip">
          <el-alert type="info" :closable="false">
            公告将发送给全部用户，无需选择发送对象
          </el-alert>
        </div>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认发送</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import { Delete, Close } from "@element-plus/icons-vue";
import { getAnnouncementPage, createAnnouncement, cancelAnnouncement, deleteAnnouncement } from "@/api/announcement";
import { getUserList } from "@/api/user";
import Pagination from "@/components/Pagination.vue";

// 列表数据
const list = ref([]);
const paginatedList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 对话框
const dialogVisible = ref(false);
const submitLoading = ref(false);
const announcementFormRef = ref(null);

// 搜索表单
const searchForm = reactive({
  status: "",
});

// 公告表单
const announcementForm = reactive({
  sendMethod: [],
  title: "",
  content: "",
  targetType: 1,
  targetUsers: [],
});

// 用户列表
const userList = ref([]);

// 移动端检测
const isMobileView = ref(false);

// 表单验证规则
const rules = {
  sendMethod: [{ required: true, validator: (rule, value, callback) => {
    if (!value || value.length === 0) {
      callback(new Error("请至少选择一个发送方式"));
    } else {
      callback();
    }
  }, trigger: "change" }],
  title: [{ required: true, message: "请输入公告标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入公告内容", trigger: "blur" }],
};

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 解析发送方式
const parseSendMethod = (sendMethodStr) => {
  try {
    return JSON.parse(sendMethodStr);
  } catch {
    return [];
  }
};

// 发送方式标签
const methodLabel = (method) => {
  const labels = { system: "系统通知", website: "公告", email: "邮件" };
  return labels[method] || method;
};

// 状态标签
const statusLabel = (status) => {
  const labels = { 0: "待发送", 1: "发送中", 2: "已发送", 3: "发送失败" };
  return labels[status] ?? status;
};

// 获取公告列表
const getAnnouncements = async () => {
  currentPage.value = 1;
  await fetchAnnouncements();
};

const buildQueryParams = () => {
  return {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    status: searchForm.status !== "" && searchForm.status !== null ? searchForm.status : undefined,
  };
};

const applyPageData = (pageData) => {
  list.value = pageData?.data || [];
  paginatedList.value = list.value;
  total.value = Number(pageData?.total || 0);
};

const fetchAnnouncements = async () => {
  loading.value = true;
  try {
    const params = buildQueryParams();
    const res = await getAnnouncementPage(params);
    applyPageData(res.data);
  } catch (error) {
    ElMessage.error("获取公告列表失败");
  } finally {
    loading.value = false;
  }
};

// 获取用户列表（用于指定用户选择）
const fetchUsers = async () => {
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 1000 });
    userList.value = res.data?.data || [];
  } catch (error) {
    console.error("获取用户列表失败", error);
  }
};

// 处理分页大小变化
const handleSizeChange = async (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  await fetchAnnouncements();
};

// 处理当前页码变化
const handleCurrentChange = async (current) => {
  currentPage.value = current;
  await fetchAnnouncements();
};

// 处理搜索
const handleSearch = async () => {
  currentPage.value = 1;
  await fetchAnnouncements();
};

// 打开发送对话框
const handleSend = async () => {
  announcementForm.sendMethod = [];
  announcementForm.title = "";
  announcementForm.content = "";
  announcementForm.targetType = 1;
  announcementForm.targetUsers = [];
  dialogVisible.value = true;
  await fetchUsers();
};

// 关闭对话框
const handleDialogClose = () => {
  announcementFormRef.value?.resetFields();
  dialogVisible.value = false;
};

// 提交表单
const handleSubmit = async () => {
  try {
    await announcementFormRef.value.validate();
    submitLoading.value = true;

    // 构建请求数据
    const data = {
      title: announcementForm.title,
      content: announcementForm.content,
      sendMethod: JSON.stringify(announcementForm.sendMethod),
      targetType: announcementForm.targetType,
      targetUsers: JSON.stringify(announcementForm.targetUsers || []),
    };

    await createAnnouncement(data);
    ElMessage.success("公告发送成功");
    dialogVisible.value = false;
    await fetchAnnouncements();
  } catch (error) {
    if (error !== false) {
      ElMessage.error("发送失败");
    }
  } finally {
    submitLoading.value = false;
  }
};

// 取消公告
const handleCancel = (row) => {
  ElMessageBox.confirm("确定要取消该公告吗？", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await cancelAnnouncement(row.id);
        ElMessage.success("取消成功");
        await fetchAnnouncements();
      } catch (error) {
        ElMessage.error("取消失败");
      }
    })
    .catch(() => {
      ElMessage.info("操作已取消");
    });
};

// 删除公告
const handleDelete = (id) => {
  ElMessageBox.confirm("确定要删除该公告吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAnnouncement(id);
        ElMessage.success("删除成功");
        await fetchAnnouncements();
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 初始化
onMounted(() => {
  getAnnouncements();
  handleResize();
  window.addEventListener("resize", handleResize);
});

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
        gap: 12px;

        .search-input {
          width: 200px;
        }

        .send-btn {
          display: inline-flex;
          align-items: center;
          gap: 6px;
          padding: 10px 18px;
          background: #409eff;
          border: none;
          border-radius: 8px;
          color: #fff;
          font-weight: 500;
          font-size: 14px;
          transition: background 0.2s, box-shadow 0.2s;

          &:hover {
            background: #66b1ff;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.35);
          }

          .send-icon {
            width: 16px;
            height: 16px;
          }
        }
      }
    }
  }

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
      max-height: calc(100vh - 280px);

      :deep(.el-table__header-wrapper) {
        th { font-weight: 600; color: #475569; }
      }

      :deep(.el-table__body-wrapper) {
        tr td { color: #64748b; padding: 12px 0; vertical-align: middle; }
      }

      .announcement-title,
      .announcement-content {
        overflow: hidden;
        cursor: pointer;
        display: -webkit-box;
        text-overflow: ellipsis;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;

        &:hover { color: #409eff; }
      }

      .send-methods {
        display: flex;
        flex-wrap: wrap;
        gap: 4px;

        .method-tag {
          display: inline-block;
          padding: 2px 6px;
          border-radius: 8px;
          font-size: 11px;
          font-weight: 500;

          &.method-system { background: #e0f2fe; color: #0284c7; }
          &.method-website { background: #dcfce7; color: #16a34a; }
          &.method-email { background: #fef3c7; color: #d97706; }
        }
      }

      .status-badge {
        display: inline-block;
        padding: 2px 8px;
        border-radius: 12px;
        font-size: 12px;
        font-weight: 500;

        &.status-0 { background: #fef3c7; color: #d97706; }
        &.status-1 { background: #dbeafe; color: #2563eb; }
        &.status-2 { background: #dcfce7; color: #16a34a; }
        &.status-3 { background: #fee2e2; color: #ef4444; }
      }

      .table-actions {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 5px;

        :deep(.el-button) { margin-left: 0; }

        .cancel-button {
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;
          border-radius: 6px;
        }

        .delete-button {
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;
          border-radius: 6px;
        }
      }
    }
  }

  .mobile-view {
    flex: 1;
    margin-top: 16px;
    padding-bottom: 60px;
    overflow-y: auto;

    .announcement-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      .announcement-card {
        border-radius: 8px;

        .card-content {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .card-header-row {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .card-id { font-size: 12px; color: #666; background: #f5f5f5; padding: 2px 6px; border-radius: 4px; }
          }

          .card-title-row { font-weight: 600; font-size: 14px; color: #333; }

          .card-content-row {
            font-size: 13px;
            color: #666;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .card-meta-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            font-size: 11px;
            color: #999;
          }

          .card-actions {
            display: flex;
            gap: 6px;
            padding-top: 8px;
            border-top: 1px solid #f0f0f0;

            .el-button { flex: 1; font-size: 12px; padding: 6px 10px; margin-left: 0; }
          }
        }
      }
    }
  }
}

.website-tip {
  margin-bottom: 18px;
}

.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
  line-height: 1.4;
}

html.dark {
  .management-container .card .card-header .card-actions .send-btn {
    background: #409eff;
    color: #fff;

    &:hover {
      background: #66b1ff;
      box-shadow: 0 2px 8px rgba(64, 158, 255, 0.4);
    }
  }
}

@media screen and (max-width: 768px) {
  .management-container .card {
    padding: 2px;

    .card-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
      padding: 6px;

      .card-title { font-size: 16px; }
      .card-actions { width: 100%; }
    }
  }
}
</style>
