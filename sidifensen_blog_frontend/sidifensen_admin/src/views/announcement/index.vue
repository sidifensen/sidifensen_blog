<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">公告管理</h2>
        <div class="card-actions">
          <el-input v-model="searchKeyword" placeholder="搜索公告标题" :prefix-icon="Search" size="small" class="search-input" clearable @keyup.enter="handleSearch" />
          <el-select v-model="filterStatus" placeholder="状态筛选" size="small" clearable class="status-select">
            <el-option label="待发送" :value="0" />
            <el-option label="已发布" :value="2" />
            <el-option label="已取消" :value="1" />
          </el-select>
          <el-button type="primary" plain round @click="handleAdd" :icon="Plus">发布公告</el-button>
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="announcementList" stripe style="width: 100%" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" />
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
          <el-table-column prop="content" label="内容" min-width="300" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)" size="small">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
              <el-button v-if="row.status === 0" type="warning" size="small" link @click="handleCancel(row)">取消</el-button>
              <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div v-loading="loading" class="announcement-cards-mobile">
          <div v-if="announcementList.length === 0" class="empty-state">
            <el-empty description="暂无公告数据" />
          </div>
          <el-card v-for="item in announcementList" :key="item.id" class="announcement-card" @click="handleEdit(item)">
            <div class="card-header-mobile">
              <span class="card-title-mobile">{{ item.title }}</span>
              <el-tag :type="getStatusType(item.status)" size="small">{{ getStatusText(item.status) }}</el-tag>
            </div>
            <div class="card-content-mobile">{{ item.content }}</div>
            <div class="card-footer-mobile">
              <span class="card-time">{{ item.createTime }}</span>
              <div class="card-actions-mobile">
                <el-button type="danger" size="small" link @click.stop="handleDelete(item)">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" @size-change="handleSizeChange" @current-change="handlePageChange" />
      </div>
    </div>

    <!-- 新增/编辑公告对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px" class="announcement-dialog" :before-close="handleDialogClose">
      <el-form ref="announcementFormRef" :model="announcementForm" :rules="rules" label-width="80px">
        <el-form-item prop="title" label="标题">
          <el-input v-model="announcementForm.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item prop="content" label="内容">
          <el-input v-model="announcementForm.content" type="textarea" :rows="5" placeholder="请输入公告内容" maxlength="2000" show-word-limit />
        </el-form-item>
        <el-form-item prop="status" label="状态">
          <el-radio-group v-model="announcementForm.status">
            <el-radio :label="0">待发送</el-radio>
            <el-radio :label="2">立即发布</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted, onUnmounted } from "vue";
import { Search, Plus } from "@element-plus/icons-vue";
import { getAnnouncementPage, createAnnouncement, cancelAnnouncement, deleteAnnouncement } from "@/api/announcement";
import { ElMessage, ElMessageBox } from "element-plus";

// 公告列表数据
const announcementList = ref([]);
const loading = ref(false);
const searchKeyword = ref("");
const filterStatus = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 移动端检测
const isMobileView = ref(false);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("发布公告");
const submitLoading = ref(false);
const announcementFormRef = ref(null);
const isEdit = ref(false);

// 公告表单
const announcementForm = reactive({
  id: null,
  title: "",
  content: "",
  status: 2,
});

// 表单验证规则
const rules = {
  title: [{ required: true, message: "请输入公告标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入公告内容", trigger: "blur" }],
};

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 获取公告列表
const fetchAnnouncementList = async () => {
  loading.value = true;
  try {
    const params = {
      pageNum: currentPage.value,
      pageSize: pageSize.value,
    };
    if (filterStatus.value !== "") {
      params.status = filterStatus.value;
    }
    const res = await getAnnouncementPage(params);
    announcementList.value = res.data.data || [];
    total.value = res.data.total || 0;
  } catch (error) {
    ElMessage.error("获取公告列表失败");
    console.error("获取公告列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 获取状态标签类型
const getStatusType = (status) => {
  const types = { 0: "warning", 1: "info", 2: "success" };
  return types[status] || "info";
};

// 获取状态文本
const getStatusText = (status) => {
  const texts = { 0: "待发送", 1: "已取消", 2: "已发布" };
  return texts[status] || "未知";
};

// 搜索
const handleSearch = () => {
  currentPage.value = 1;
  fetchAnnouncementList();
};

// 新增公告
const handleAdd = () => {
  isEdit.value = false;
  dialogTitle.value = "发布公告";
  announcementForm.id = null;
  announcementForm.title = "";
  announcementForm.content = "";
  announcementForm.status = 2;
  dialogVisible.value = true;
};

// 编辑公告
const handleEdit = (row) => {
  isEdit.value = true;
  dialogTitle.value = "编辑公告";
  announcementForm.id = row.id;
  announcementForm.title = row.title;
  announcementForm.content = row.content;
  announcementForm.status = row.status === 1 ? 0 : row.status; // 已取消的显示为待发送
  dialogVisible.value = true;
};

// 取消公告
const handleCancel = (row) => {
  ElMessageBox.confirm(`确定要取消公告【${row.title}】吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await cancelAnnouncement(row.id);
        ElMessage.success("取消成功");
        await fetchAnnouncementList();
      } catch (error) {
        ElMessage.error("取消失败");
        console.error("取消失败:", error);
      }
    })
    .catch(() => {
      ElMessage.info("操作已取消");
    });
};

// 删除公告
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除公告【${row.title}】吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAnnouncement(row.id);
        ElMessage.success("删除成功");
        await fetchAnnouncementList();
      } catch (error) {
        ElMessage.error("删除失败");
        console.error("删除失败:", error);
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 提交表单
const handleSubmit = async () => {
  try {
    await announcementFormRef.value.validate();
    submitLoading.value = true;

    const data = {
      title: announcementForm.title,
      content: announcementForm.content,
      status: announcementForm.status,
    };

    await createAnnouncement(data);
    ElMessage.success(isEdit.value ? "编辑成功" : "发布成功");
    dialogVisible.value = false;
    await fetchAnnouncementList();
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? "编辑失败" : "发布失败");
      console.error("提交失败:", error);
    }
  } finally {
    submitLoading.value = false;
  }
};

// 关闭对话框
const handleDialogClose = () => {
  announcementFormRef.value?.resetFields();
  dialogVisible.value = false;
};

// 分页大小变化
const handleSizeChange = () => {
  currentPage.value = 1;
  fetchAnnouncementList();
};

// 页码变化
const handlePageChange = () => {
  fetchAnnouncementList();
};

// 选择变化
const handleSelectionChange = (selection) => {
  console.log("selection:", selection);
};

// 初始化
onMounted(() => {
  fetchAnnouncementList();
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
      padding: 10px 10px 16px 10px;
      flex-wrap: wrap;
      gap: 12px;

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
        flex-wrap: wrap;

        .search-input {
          width: 200px;
          border-radius: 8px;
        }

        .status-select {
          width: 140px;
          border-radius: 8px;
        }
      }
    }

    .desktop-view {
      flex: 1;
      overflow-y: auto;
    }

    .mobile-view {
      flex: 1;
      overflow-y: auto;

      .announcement-cards-mobile {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .empty-state {
          padding: 60px 0;
          text-align: center;
        }

        .announcement-card {
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .card-header-mobile {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 8px;

            .card-title-mobile {
              font-size: 16px;
              font-weight: 600;
              color: var(--el-text-color-primary);
              flex: 1;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          .card-content-mobile {
            font-size: 14px;
            color: var(--el-text-color-regular);
            margin-bottom: 8px;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .card-footer-mobile {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .card-time {
              font-size: 12px;
              color: var(--el-text-color-secondary);
            }

            .card-actions-mobile {
              display: flex;
              gap: 8px;
            }
          }
        }
      }
    }

    .pagination-wrapper {
      padding-top: 16px;
      display: flex;
      justify-content: flex-end;
    }
  }
}

// 响应式设计
@media screen and (max-width: 768px) {
  .management-container .card {
    padding: 12px;

    .card-header {
      flex-direction: column;
      align-items: flex-start;

      .card-actions {
        width: 100%;
        flex-direction: column;

        .search-input,
        .status-select {
          width: 100%;
        }
      }
    }

    .pagination-wrapper {
      justify-content: center;
    }
  }
}
</style>
