<template>
  <ManagementCard
    title="友链审核"
    :show-time-filter="true"
    :show-pagination="true"
    :model-current-page="currentPage"
    :model-page-size="pageSize"
    :total="total"
    @search="fetchLinks"
    @time-change="handleTimeChange"
  >
    <!-- 筛选条件（放在标题下方） -->
    <template #second-filters>
      <AdminSearchFilters
        v-model:examine-status="searchExamineStatus"
        v-model:user-id="searchUserId"
        v-model:keyword="searchKeyword"
        keyword-placeholder="搜索友链名称或描述"
        @change="handleSearch"
      <el-select
        v-model="searchExamineStatus"
        placeholder="审核状态"
        filterable
        clearable
        size="small"
        class="search-select"
        @change="handleSearch"
      >
        <el-option label="全部" value="" />
        <el-option label="待审核" value="0" />
        <el-option label="审核通过" value="1" />
        <el-option label="审核不通过" value="2" />
      </el-select>
      <el-select
        v-model="searchUserId"
        placeholder="输入用户名搜索"
        filterable
        remote
        reserve-keyword
        :remote-method="searchUsers"
        :loading="userLoading"
        clearable
        size="small"
        class="search-select"
        @change="handleSearch"
      >
        <el-option
          v-for="user in filteredUserList"
          :key="user.id"
          :label="user.nickname || user.username"
          :value="user.id"
        />
      </el-select>
      <el-input
        v-model="searchKeyword"
        placeholder="搜索友链名称或描述"
        :prefix-icon="Search"
        size="small"
        class="search-input"
        clearable
        @input="handleSearch"
      />
    </template>

    <!-- 头部搜索操作（仅放按钮） -->
    <template #header-actions>
    </template>

    <!-- 批量操作按钮 -->
    <template #batch-actions>
      <BatchActions
        :selected-count="selectedLinks.length"
        :show-batch-audit="true"
        :show-batch-reject="true"
        :show-batch-delete="true"
        :batch-audit-loading="batchAuditLoading"
        :batch-reject-loading="batchRejectLoading"
        :batch-delete-loading="batchDeleteLoading"
        @batch-audit="handleBatchAudit"
        @batch-reject="handleBatchReject"
        @batch-delete="handleBatchDelete"
      />
    </template>

    <!-- 桌面端表格视图 -->
    <template #table-view>
      <DataTable
        :data="paginatedLinkList"
        :loading="loading"
        show-selection
        show-cover
        show-name
        show-user
        show-examine-status
        show-create-time
        cover-width="120"
        name-min-width="150"
        user-width="120"
        examine-status-width="80"
        create-time-width="110"
        :has-edit-action="true"
        :has-delete-action="true"
        :has-audit-action="true"
        :has-reject-action="true"
        actions-width="320"
        @selection-change="handleSelectionChange"
        @view="handleViewLink"
        @audit="handleAuditLink"
        @reject="handleRejectLink"
        @delete="handleDeleteLink"
        @edit="handleEditLink"
      >
        <!-- URL列 -->
        <el-table-column prop="url" label="网站地址" min-width="200">
          <template #default="{ row }">
            <el-tooltip :content="row.url" placement="top-start">
              <a :href="row.url" target="_blank" class="link-url">{{ row.url }}</a>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 描述列 -->
        <el-table-column prop="description" label="网站描述" min-width="250">
          <template #default="{ row }">
            <el-tooltip :content="row.description" placement="top-start">
              <div class="link-description">{{ row.description || "暂无描述" }}</div>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 邮箱列 -->
        <el-table-column prop="email" label="联系邮箱" width="180">
          <template #default="{ row }">
            <el-tooltip :content="row.email" placement="top-start">
              <div class="link-email">{{ row.email }}</div>
            </el-tooltip>
          </template>
        </el-table-column>
      </DataTable>
    </template>

    <!-- 移动端卡片视图 -->
    <template #card-view>
      <MobileCardList
        :data="paginatedLinkList"
        :selected-items="selectedLinks"
        show-selection
        show-cover
        show-name
        show-description
        show-user
        show-examine-status
        show-meta
        :has-audit-action="true"
        :has-reject-action="true"
        :has-delete-action="true"
        @select="handleMobileSelect"
        @audit="handleAuditLink"
        @reject="handleRejectLink"
        @delete="handleDeleteLink"
      />
    </template>
  </ManagementCard>

  <!-- 编辑友链对话框 -->
  <el-dialog
    v-model="editDialogVisible"
    title="编辑友链"
    width="500px"
    destroy-on-close
  >
    <el-form
      ref="editFormRef"
      :model="editLinkForm"
      :rules="editFormRules"
      label-width="80px"
    >
      <el-form-item label="网站名称" prop="name">
        <el-input v-model="editLinkForm.name" placeholder="请输入网站名称" />
      </el-form-item>
      <el-form-item label="网站地址" prop="url">
        <el-input v-model="editLinkForm.url" placeholder="请输入网站地址" />
      </el-form-item>
      <el-form-item label="封面图片" prop="coverUrl">
        <el-input v-model="editLinkForm.coverUrl" placeholder="请输入封面图片URL" />
      </el-form-item>
      <el-form-item label="网站描述" prop="description">
        <el-input
          v-model="editLinkForm.description"
          type="textarea"
          :rows="3"
          placeholder="请输入网站描述"
        />
      </el-form-item>
      <el-form-item label="联系邮箱" prop="email">
        <el-input v-model="editLinkForm.email" placeholder="请输入联系邮箱" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="editDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="editLinkLoading" @click="submitEditLink">
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ref, onMounted, onUnmounted } from "vue";
import { Search } from "@element-plus/icons-vue";
import { useUserSearch } from "@/hooks/useUserSearch";
import { adminGetLinkList, adminSearchLink, adminExamineLink, adminBatchExamineLink, adminDeleteLink, adminBatchDeleteLink, adminUpdateLink } from "@/api/link";

// 组件
import ManagementCard from "@/components/ManagementCard.vue";
import AdminSearchFilters from "@/components/AdminSearchFilters.vue";
import BatchActions from "@/components/BatchActions.vue";
import DataTable from "@/components/DataTable.vue";
import MobileCardList from "@/components/MobileCardList.vue";

// 友链列表数据
const linkList = ref([]);
const paginatedLinkList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 用户搜索
const { filteredUserList, userLoading, searchUsers } = useUserSearch();

// 搜索条件
const searchExamineStatus = ref("");
const searchKeyword = ref("");
const searchStartTime = ref("");
const searchEndTime = ref("");
const searchUserId = ref("");

// 选中的友链
const selectedLinks = ref([]);

// 批量操作加载状态
const batchAuditLoading = ref(false);
const batchRejectLoading = ref(false);
const batchDeleteLoading = ref(false);

// 编辑对话框
const editDialogVisible = ref(false);
const editFormRef = ref(null);
const editLinkLoading = ref(false);
const editLinkForm = ref({
  id: null,
  name: "",
  url: "",
  coverUrl: "",
  description: "",
  email: "",
});

// 编辑表单验证规则
const editFormRules = {
  name: [{ required: true, message: "请输入网站名称", trigger: "blur" }],
  url: [{ required: true, message: "请输入网站地址", trigger: "blur" }],
  description: [{ required: true, message: "请输入网站描述", trigger: "blur" }],
  email: [{ required: true, message: "请输入联系邮箱", trigger: "blur" }],
};

// 搜索条件判断
const hasSearchConditions = () => !!(searchExamineStatus.value || searchKeyword.value || searchStartTime.value || searchEndTime.value || searchUserId.value);

// 构建搜索参数
const buildSearchPayload = () => ({
  pageNum: currentPage.value,
  pageSize: pageSize.value,
  userId: searchUserId.value || undefined,
  examineStatus: searchExamineStatus.value ? parseInt(searchExamineStatus.value, 10) : undefined,
  keyword: searchKeyword.value || undefined,
  createTimeStart: searchStartTime.value || undefined,
  createTimeEnd: searchEndTime.value || undefined,
});

// 应用分页数据
const applyPageData = (pageData) => {
  linkList.value = pageData?.data || [];
  paginatedLinkList.value = linkList.value;
  total.value = Number(pageData?.total || 0);
  selectedLinks.value = [];
};

// 获取友链列表
const fetchLinks = async () => {
  loading.value = true;
  try {
    let pageData = null;
    if (hasSearchConditions()) {
      const res = await adminSearchLink(buildSearchPayload());
      pageData = res.data;
    } else {
      const res = await adminGetLinkList({
        pageNum: currentPage.value,
        pageSize: pageSize.value,
      });
      pageData = res.data;
    }
    applyPageData(pageData);
  } catch (error) {
    ElMessage.error(hasSearchConditions() ? "搜索友链失败" : "获取友链列表失败");
  } finally {
    loading.value = false;
  }
};

// 搜索处理
const handleSearch = async () => {
  currentPage.value = 1;
  await fetchLinks();
};

// 时间筛选变化
const handleTimeChange = ({ startTime, endTime }) => {
  searchStartTime.value = startTime;
  searchEndTime.value = endTime;
  handleSearch();
};

// 表格多选
const handleSelectionChange = (links) => {
  selectedLinks.value = links;
};

// 移动端选择处理
const handleMobileSelect = (link) => {
  const index = selectedLinks.value.findIndex((item) => item.id === link.id);
  if (index > -1) {
    selectedLinks.value.splice(index, 1);
  } else {
    selectedLinks.value.push(link);
  }
};

// 查看友链
const handleViewLink = (link) => {
  // 可以跳转到详情页或打开详情对话框
  console.log("查看友链:", link);
};

// 编辑友链
const handleEditLink = (link) => {
  editLinkForm.value = {
    id: link.id,
    name: link.name || "",
    url: link.url || "",
    coverUrl: link.coverUrl || "",
    description: link.description || "",
    email: link.email || "",
  };
  editDialogVisible.value = true;
};

// 提交编辑友链
const submitEditLink = async () => {
  if (!editFormRef.value) return;

  await editFormRef.value.validate(async (valid) => {
    if (valid) {
      editLinkLoading.value = true;
      try {
        await adminUpdateLink(editLinkForm.value);
        ElMessage.success("编辑成功");
        editDialogVisible.value = false;
        await refreshLinkList();
      } catch (error) {
        ElMessage.error("编辑失败");
      } finally {
        editLinkLoading.value = false;
      }
    }
  });
};

// 智能刷新列表
const refreshLinkList = async () => {
  await fetchLinks();
};

// 处理单个友链审核
const handleAuditLink = async (link) => {
  try {
    await adminExamineLink(link.id, 1);
    ElMessage.success("审核成功");
    await refreshLinkList();
  } catch (error) {
    ElMessage.error("审核失败");
  }
};

// 处理批量审核
const handleBatchAudit = () => {
  if (selectedLinks.value.length === 0) return;

  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedLinks.value.length} 个友链吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      batchAuditLoading.value = true;
      try {
        const linkIds = selectedLinks.value.map((link) => link.id);
        await adminBatchExamineLink(linkIds, 1);
        ElMessage.success("批量审核成功");
        await refreshLinkList();
      } catch (error) {
        ElMessage.error("批量审核失败");
      } finally {
        batchAuditLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("审核已取消");
    });
};

// 处理单个友链拒绝
const handleRejectLink = async (link) => {
  try {
    await adminExamineLink(link.id, 2);
    ElMessage.success("拒绝成功");
    await refreshLinkList();
  } catch (error) {
    ElMessage.error("拒绝失败");
  }
};

// 处理批量拒绝
const handleBatchReject = () => {
  if (selectedLinks.value.length === 0) return;

  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedLinks.value.length} 个友链吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchRejectLoading.value = true;
      try {
        const linkIds = selectedLinks.value.map((link) => link.id);
        await adminBatchExamineLink(linkIds, 2);
        ElMessage.success("批量拒绝成功");
        await refreshLinkList();
      } catch (error) {
        ElMessage.error("批量拒绝失败");
      } finally {
        batchRejectLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("拒绝已取消");
    });
};

// 处理删除单个友链
const handleDeleteLink = (link) => {
  ElMessageBox.confirm("确定要删除该友链吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await adminDeleteLink(link.id);
        ElMessage.success("删除成功");
        await refreshLinkList();
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 处理批量删除
const handleBatchDelete = () => {
  if (selectedLinks.value.length === 0) return;

  ElMessageBox.confirm(`确定要删除选中的 ${selectedLinks.value.length} 个友链吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const linkIds = selectedLinks.value.map((link) => link.id);
        await adminBatchDeleteLink(linkIds);
        ElMessage.success("批量删除成功");
        await refreshLinkList();
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

// 初始化
onMounted(() => {
  fetchLinks();
});
</script>

<style lang="scss" scoped>
// 链接样式
.link-url {
  color: #409eff;
  text-decoration: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }
}

.link-description {
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

.link-email {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 12px;
  color: #666;
}

// 响应式
@media screen and (max-width: 768px) {
  .link-url,
  .link-description,
  .link-email {
    width: 100%;
  }
}
</style>
