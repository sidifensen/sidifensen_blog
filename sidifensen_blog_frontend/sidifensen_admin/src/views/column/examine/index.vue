<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">专栏审核</h2>
        <div class="card-actions">
          <el-select v-model="searchExamineStatus" placeholder="审核状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="全部" value="" />
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="审核不通过" value="2" />
          </el-select>
          <el-select v-model="searchUserId" placeholder="用户名称" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option v-for="user in userList" :key="user.id" :label="user.nickname || user.username" :value="user.id" />
          </el-select>
        </div>
      </div>

      <!-- 时间筛选区域 -->
      <div class="card-time-filters">
        <div class="time-filter-group">
          <el-date-picker v-model="searchStartTime" type="datetime" placeholder="开始时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
          <el-date-picker v-model="searchEndTime" type="datetime" placeholder="结束时间" size="small" class="time-input" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" clearable @change="handleSearch" />
        </div>
      </div>

      <div class="card-second">
        <el-input v-model="searchKeyword" placeholder="搜索专栏名称或描述" class="search-input" size="small" clearable @input="handleSearch">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="card-third">
        <el-button type="primary" plain round @click="handleBatchAudit" :disabled="selectedColumns.length === 0" :loading="batchAuditLoading"> 批量审核 </el-button>
        <el-button type="warning" plain round @click="handleBatchReject" :disabled="selectedColumns.length === 0" :loading="batchRejectLoading"> 批量拒绝 </el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedColumns.length === 0" :loading="batchDeleteLoading"> 批量删除 </el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedColumnList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="coverUrl" label="封面" width="120">
            <template #default="{ row }">
              <div class="column-cover-container">
                <el-image v-if="row.coverUrl" :src="row.coverUrl" class="column-cover" :preview-src-list="[row.coverUrl]" fit="cover" preview-teleported />
                <div v-else class="no-cover">暂无封面</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="专栏名称" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="row.name" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="column-name">{{ row.name }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="专栏描述" min-width="300">
            <template #default="{ row }">
              <el-tooltip :content="row.description" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="column-description">{{ row.description || "暂无描述" }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="nickname" label="作者昵称" width="120" />
          <el-table-column prop="showStatus" label="展示状态" width="80">
            <template #default="{ row }">
              <div class="column-status" :class="row.showStatus === 0 ? 'status-public' : 'status-private'">
                {{ row.showStatus === 0 ? "公开" : "私密" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="examineStatus" label="审核状态" width="80">
            <template #default="{ row }">
              <div class="column-status" :class="row.examineStatus === 0 ? 'status-unaudited' : row.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                {{ row.examineStatus === 0 ? "待审核" : row.examineStatus === 1 ? "已审核" : "未通过" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="focusCount" label="关注数" width="80" />
          <el-table-column prop="articleCount" label="文章数" width="80" />
          <el-table-column prop="createTime" label="创建时间" sortable width="110" />
          <el-table-column label="操作" width="400">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="info" @click="handleViewColumn(row)" :icon="View" class="view-button" size="small">查看</el-button>
                <el-button type="success" @click="handleEditColumn(row)" :icon="Edit" class="edit-button" size="small">修改</el-button>
                <el-button type="primary" @click="handleAuditColumn(row.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectColumn(row.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteColumn(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="column-cards">
          <el-card v-for="column in paginatedColumnList" :key="column.id" class="column-card" :class="{ 'is-selected': isColumnSelected(column.id) }">
            <div class="column-card-content">
              <div class="column-header-section">
                <div class="column-cover-mobile">
                  <el-checkbox :model-value="isColumnSelected(column.id)" @change="handleMobileSelect(column)" class="mobile-checkbox" />
                  <el-image v-if="column.coverUrl" :src="column.coverUrl" class="column-cover-img" :preview-src-list="[column.coverUrl]" fit="cover" preview-teleported />
                  <div v-else class="no-cover-mobile">暂无封面</div>
                </div>
                <div class="column-info">
                  <div class="column-header">
                    <div class="column-id">#{{ column.id }}</div>
                    <div class="column-status" :class="column.examineStatus === 0 ? 'status-unaudited' : column.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                      {{ column.examineStatus === 0 ? "待审核" : column.examineStatus === 1 ? "已审核" : "未通过" }}
                    </div>
                  </div>
                  <div class="column-name-mobile">{{ column.name }}</div>

                  <!-- 作者信息 -->
                  <div class="column-author-mobile">
                    <span class="author-label">作者昵称:</span>
                    <span class="author-name">{{ column.nickname }}</span>
                  </div>

                  <!-- 专栏描述 -->
                  <div class="column-description-mobile" v-if="column.description">
                    <span class="description-label">描述:</span>
                    <span class="description-content">{{ column.description }}</span>
                  </div>

                  <!-- 其他元信息 -->
                  <div class="column-meta-mobile">
                    <div class="meta-item">
                      <span class="label">展示状态:</span>
                      <span :class="column.showStatus === 0 ? 'status-public' : 'status-private'">
                        {{ column.showStatus === 0 ? "公开" : "私密" }}
                      </span>
                    </div>
                  </div>
                  <div class="column-meta">
                    <div class="stats-row">
                      <div class="meta-item stat-item">
                        <span class="label">关注:</span>
                        <span>{{ column.focusCount || 0 }}</span>
                      </div>
                      <div class="meta-item stat-item">
                        <span class="label">文章:</span>
                        <span>{{ column.articleCount || 0 }}</span>
                      </div>
                    </div>
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">创建:</span>
                        <span>{{ column.createTime }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="column-actions">
                <el-button type="info" @click="handleViewColumn(column)" :icon="View" class="view-button" size="small">查看</el-button>
                <el-button type="success" @click="handleEditColumn(column)" :icon="Edit" class="edit-button" size="small">修改</el-button>
                <el-button type="primary" @click="handleAuditColumn(column.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectColumn(column.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteColumn(column.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <Pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 专栏详情对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="70%" class="column-detail-dialog" :close-on-click-modal="false" :close-on-press-escape="true" draggable align-center @close="handleDialogClose">
      <div v-if="currentColumn" class="column-detail" v-loading="detailLoading">
        <!-- 专栏基本信息 -->
        <div class="column-info-section">
          <div class="column-detail-header">
            <!-- 左侧：专栏信息 -->
            <div class="column-detail-info">
              <div class="column-title-section">
                <h2 class="column-title-detail">专栏详情</h2>
                <div class="column-id-detail">#{{ currentColumn?.id || "N/A" }}</div>
              </div>

              <!-- 专栏封面 -->
              <div class="column-cover-section" v-if="currentColumn?.coverUrl">
                <el-image :src="currentColumn.coverUrl" class="column-cover" fit="cover">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>

              <div class="column-name-detail">
                <h4>专栏名称</h4>
                <div class="name-text">{{ currentColumn?.name || "暂无名称" }}</div>
              </div>

              <div class="column-description-detail" v-if="currentColumn?.description">
                <h4>专栏描述</h4>
                <div class="description-text">{{ currentColumn.description }}</div>
              </div>

              <!-- 状态信息 -->
              <div class="column-badges-detail">
                <div class="badge-group">
                  <span class="badge-label">展示状态:</span>
                  <div class="column-status" :class="(currentColumn?.showStatus || 0) === 0 ? 'status-public' : 'status-private'">
                    {{ (currentColumn?.showStatus || 0) === 0 ? "公开" : "私密" }}
                  </div>
                </div>
                <div class="badge-group">
                  <span class="badge-label">审核状态:</span>
                  <div class="column-status" :class="(currentColumn?.examineStatus || 0) === 0 ? 'status-unaudited' : (currentColumn?.examineStatus || 0) === 1 ? 'status-audited' : 'status-rejected'">
                    {{ (currentColumn?.examineStatus || 0) === 0 ? "待审核" : (currentColumn?.examineStatus || 0) === 1 ? "已审核" : "未通过" }}
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 底部：统计数据 -->
          <div class="column-stats-detail">
            <div class="stats-group">
              <div class="stat-item">
                <el-icon class="stat-icon"><Star /></el-icon>
                <span class="stat-label">关注</span>
                <span class="stat-value">{{ currentColumn?.focusCount || 0 }}</span>
              </div>
              <div class="stat-item">
                <el-icon class="stat-icon"><Document /></el-icon>
                <span class="stat-label">文章</span>
                <span class="stat-value">{{ currentColumn?.articleCount || 0 }}</span>
              </div>
              <div class="stat-item time-stat-item">
                <el-icon class="stat-icon"><Clock /></el-icon>
                <span class="stat-label">创建时间:</span>
                <span class="stat-value">{{ currentColumn?.createTime || "未知" }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 专栏文章列表 -->
        <div class="column-articles-section" v-if="currentColumn?.articles && currentColumn.articles.length > 0">
          <h3 class="articles-title">
            <el-icon class="title-icon"><Document /></el-icon>
            专栏文章列表 ({{ currentColumn.articles.length }} 篇)
          </h3>
          <div class="articles-list">
            <div v-for="(article, index) in currentColumn.articles" :key="article.id" class="article-item">
              <div class="article-index">{{ index + 1 }}</div>
              <div class="article-cover-mini" v-if="article.coverUrl">
                <el-image :src="article.coverUrl" class="cover-img" fit="cover">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
              <div v-else class="article-no-cover">
                <el-icon><Document /></el-icon>
              </div>
              <div class="article-info">
                <div class="article-title-detail">{{ article.title }}</div>
                <div class="article-description-detail" v-if="article.description">
                  {{ article.description }}
                </div>
                <div class="article-meta-detail">
                  <div class="meta-item">
                    <el-icon class="meta-icon"><View /></el-icon>
                    <span>{{ article.readCount || 0 }} 阅读</span>
                  </div>
                  <div class="meta-item">
                    <svg-icon name="like" width="14px" height="14px" color="#909399" />
                    <span>{{ article.likeCount || 0 }} 点赞</span>
                  </div>
                  <div class="meta-item">
                    <el-icon class="meta-icon"><ChatDotRound /></el-icon>
                    <span>{{ article.commentCount || 0 }} 评论</span>
                  </div>
                  <div class="meta-item">
                    <el-icon class="meta-icon"><Star /></el-icon>
                    <span>{{ article.collectCount || 0 }} 收藏</span>
                  </div>
                  <div class="meta-item">
                    <el-icon class="meta-icon"><Clock /></el-icon>
                    <span>{{ article.createTime }}</span>
                  </div>
                  <div class="meta-item">
                    <span class="article-status" :class="article.examineStatus === 0 ? 'status-unaudited' : article.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                      {{ article.examineStatus === 0 ? "待审核" : article.examineStatus === 1 ? "已审核" : "未通过" }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 无文章提示 -->
        <div v-else-if="currentColumn && Array.isArray(currentColumn.articles) && currentColumn.articles.length === 0" class="no-articles">
          <el-empty description="该专栏暂无文章" />
        </div>

        <!-- 文章数据为null或undefined的情况 -->
        <div v-else-if="currentColumn && !currentColumn.articles" class="no-articles">
          <el-empty description="该专栏暂无文章数据" />
        </div>
      </div>

      <!-- 加载状态 -->
      <div v-else class="loading-container">
        <el-empty description="正在加载专栏详情..." />
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false" :icon="Close">关闭</el-button>
          <el-button type="primary" @click="handleAuditColumn(currentColumn?.id)" :icon="Check" :disabled="!currentColumn || (currentColumn?.examineStatus || 0) === 1"> 审核通过 </el-button>
          <el-button type="warning" @click="handleRejectColumn(currentColumn?.id)" :icon="Close" :disabled="!currentColumn || (currentColumn?.examineStatus || 0) === 2"> 审核拒绝 </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改专栏对话框 -->
    <el-dialog v-model="editDialogVisible" title="修改专栏" width="50%" class="edit-column-dialog" :close-on-click-modal="false" :close-on-press-escape="true" draggable align-center @close="handleCancelEdit">
      <el-form :model="editForm" label-width="80px" class="edit-form">
        <el-form-item label="专栏名称" required>
          <el-input v-model="editForm.name" placeholder="请输入专栏名称" maxlength="30" show-word-limit clearable />
        </el-form-item>
        <el-form-item label="专栏描述">
          <el-input v-model="editForm.description" type="textarea" placeholder="请输入专栏描述" :rows="4" maxlength="200" show-word-limit />
        </el-form-item>
        <el-form-item label="展示状态">
          <el-radio-group v-model="editForm.showStatus">
            <el-radio :value="0">公开</el-radio>
            <el-radio :value="1">私密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleCancelEdit" :disabled="editLoading">取消</el-button>
          <el-button type="primary" @click="handleSaveEdit" :loading="editLoading">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from "vue";
import { Delete, Close, Check, View, Search, Picture, User, Star, Clock, Document, Edit, ChatDotRound } from "@element-plus/icons-vue";
import { getUserList } from "@/api/user";
import { adminGetColumnList, adminSearchColumn, adminExamineColumn, adminBatchExamineColumn, adminDeleteColumn, adminBatchDeleteColumn, adminUpdateColumn, adminGetColumnDetail } from "@/api/column";
import Pagination from "@/components/Pagination.vue";

// 专栏列表数据
const columnList = ref([]);
const paginatedColumnList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);
const dialogVisible = ref(false);
const dialogTitle = ref("专栏详情");
const currentColumn = ref(null);
const detailLoading = ref(false);

// 用户列表
const userList = ref([]);

// 搜索条件
const searchExamineStatus = ref("");
const searchKeyword = ref("");
const searchStartTime = ref("");
const searchEndTime = ref("");
const searchUserId = ref("");

// 选中的专栏
const selectedColumns = ref([]);

// 批量操作加载状态
const batchAuditLoading = ref(false);
const batchRejectLoading = ref(false);
const batchDeleteLoading = ref(false);

// 移动端检测
const isMobileView = ref(false);

// 修改专栏相关变量
const editDialogVisible = ref(false);
const editForm = ref({
  id: null,
  name: "",
  description: "",
  showStatus: 0,
});
const editLoading = ref(false);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 获取用户列表
const getUsers = async () => {
  try {
    const res = await getUserList();
    userList.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取用户列表失败");
  }
};

// 获取专栏列表
const getColumns = async () => {
  loading.value = true;
  try {
    // 传递空的筛选条件对象
    const res = await adminGetColumnList({});
    columnList.value = res.data.data.sort((a, b) => b.id - a.id);
    total.value = columnList.value.length;
    currentPage.value = 1;
    updatePaginatedColumnList();
  } catch (error) {
    ElMessage.error("获取专栏列表失败");
  } finally {
    loading.value = false;
  }
};

// 更新分页数据
const updatePaginatedColumnList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedColumnList.value = columnList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  updatePaginatedColumnList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedColumnList();
};

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    // 构建搜索参数
    const searchData = {
      userId: searchUserId.value,
      examineStatus: searchExamineStatus.value ? parseInt(searchExamineStatus.value) : undefined,
      keyword: searchKeyword.value,
      createTimeStart: searchStartTime.value,
      createTimeEnd: searchEndTime.value,
    };

    const res = await adminSearchColumn(searchData);
    columnList.value = res.data.data;
    total.value = columnList.value.length;
    currentPage.value = 1;
    updatePaginatedColumnList();
  } catch (error) {
    ElMessage.error("搜索专栏失败");
  } finally {
    loading.value = false;
  }
};

// 智能刷新列表
const refreshColumnList = async () => {
  // 检查是否有任何搜索条件
  const hasSearchConditions = searchExamineStatus.value || searchKeyword.value || searchStartTime.value || searchEndTime.value || searchUserId.value;

  if (hasSearchConditions) {
    await handleSearch();
  } else {
    await getColumns();
  }
};

// 表格多选
const handleSelectionChange = (columns) => {
  selectedColumns.value = columns;
};

// 检查专栏是否被选中
const isColumnSelected = (columnId) => {
  return selectedColumns.value.some((column) => column.id === columnId);
};

// 移动端选择处理
const handleMobileSelect = (column) => {
  const index = selectedColumns.value.findIndex((item) => item.id === column.id);
  if (index > -1) {
    // 已选中，取消选中
    selectedColumns.value.splice(index, 1);
  } else {
    // 未选中，添加到选中列表
    selectedColumns.value.push(column);
  }
};

// 对话框关闭处理
const handleDialogClose = () => {
  currentColumn.value = null;
  detailLoading.value = false;
};

// 查看专栏详情
const handleViewColumn = async (column) => {
  currentColumn.value = column;
  dialogTitle.value = "专栏详情";
  dialogVisible.value = true;
  detailLoading.value = true;

  try {
    const res = await adminGetColumnDetail(column.id);
    currentColumn.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取专栏详情失败");
    console.error("获取专栏详情失败:", error);
  } finally {
    detailLoading.value = false;
  }
};

// 修改专栏
const handleEditColumn = (column) => {
  editForm.value = {
    id: column.id,
    name: column.name,
    description: column.description || "",
    showStatus: column.showStatus,
  };
  editDialogVisible.value = true;
};

// 保存专栏修改
const handleSaveEdit = async () => {
  if (!editForm.value.name.trim()) {
    ElMessage.warning("专栏名称不能为空");
    return;
  }

  editLoading.value = true;
  try {
    await adminUpdateColumn(editForm.value);
    ElMessage.success("修改成功");
    editDialogVisible.value = false;
    await refreshColumnList();
  } catch (error) {
    ElMessage.error("修改失败");
  } finally {
    editLoading.value = false;
  }
};

// 取消修改
const handleCancelEdit = () => {
  editDialogVisible.value = false;
  editForm.value = {
    id: null,
    name: "",
    description: "",
    showStatus: 0,
  };
};

// 处理单个专栏审核
const handleAuditColumn = async (columnId) => {
  try {
    await adminExamineColumn(columnId, 1);
    ElMessage.success("审核成功");
    await refreshColumnList();
    if (dialogVisible.value) {
      dialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error("审核失败");
  }
};

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedColumns.value.length} 个专栏吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      batchAuditLoading.value = true;
      try {
        const columnIds = selectedColumns.value.map((column) => column.id);
        await adminBatchExamineColumn(columnIds, 1);
        ElMessage.success("批量审核成功");
        await refreshColumnList();
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

// 处理单个专栏拒绝
const handleRejectColumn = async (columnId) => {
  try {
    await adminExamineColumn(columnId, 2);
    ElMessage.success("拒绝成功");
    await refreshColumnList();
    if (dialogVisible.value) {
      dialogVisible.value = false;
    }
  } catch (error) {
    ElMessage.error("拒绝失败");
  }
};

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedColumns.value.length} 个专栏吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchRejectLoading.value = true;
      try {
        const columnIds = selectedColumns.value.map((column) => column.id);
        await adminBatchExamineColumn(columnIds, 2);
        ElMessage.success("批量拒绝成功");
        await refreshColumnList();
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

// 处理删除单个专栏
const handleDeleteColumn = (columnId) => {
  ElMessageBox.confirm("确定要删除该专栏吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await adminDeleteColumn(columnId);
        ElMessage.success("删除成功");
        await refreshColumnList();
        if (dialogVisible.value) {
          dialogVisible.value = false;
        }
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
  ElMessageBox.confirm(`确定要删除选中的 ${selectedColumns.value.length} 个专栏吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const columnIds = selectedColumns.value.map((column) => column.id);
        await adminBatchDeleteColumn(columnIds);
        ElMessage.success("批量删除成功");
        await refreshColumnList();
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
  getColumns();
  getUsers();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 专栏审核页面主容器
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
          background-color: #e6a23c;
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
              box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
              border-color: #e6a23c;
            }
          }

          :deep(.el-select__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
              border-color: #e6a23c;
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
                box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
                border-color: #e6a23c;
              }
            }
          }
        }
      }
    }

    // 卡片第二行 - 关键词搜索区域
    .card-second {
      display: flex;
      justify-content: flex-end;
      padding: 5px 5px 0 5px;
      gap: 10px;

      .search-input {
        width: 240px; // 设置固定宽度
      }

      :deep(.el-input__wrapper) {
        border-radius: 8px;
        &:focus-within {
          box-shadow: 0 0 0 3px rgba(230, 162, 60, 0.2);
          border-color: #e6a23c;
        }
      }
    }

    // 卡片第三行 - 批量操作按钮区域
    .card-third {
      display: flex;
      justify-content: flex-end;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
    }
  }

  // 桌面端表格视图 - 当 isMobileView = false 时显示
  .desktop-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    padding-bottom: 60px; // 为分页容器预留空间

    // 专栏表格
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

      // 专栏名称样式
      .column-name {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        cursor: pointer;

        &:hover {
          color: #e6a23c;
        }
      }

      // 专栏描述样式
      .column-description {
        overflow: hidden;
        cursor: pointer;
        display: -webkit-box;
        text-overflow: ellipsis;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;

        &:hover {
          color: #e6a23c;
        }
      }

      // 专栏封面容器样式
      .column-cover-container {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;

        // 有封面图片样式
        .column-cover {
          width: 100px;
          height: 60px;
          border-radius: 6px;
          cursor: pointer;
          transition: all 0.3s ease;

          &:hover {
            transform: scale(1.05);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
          }
        }

        // 无封面占位样式
        .no-cover {
          width: 100px;
          height: 60px;
          background-color: #f5f5f5;
          border: 1px dashed #ddd;
          border-radius: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 12px;
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
        .view-button,
        .edit-button,
        .examine-button,
        .reject-button,
        .delete-button {
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
          }
        }

        // 查看按钮
        .view-button {
          background-color: #f0f9ff;
          color: #0369a1;
          border-color: #f0f9ff;

          &:hover {
            background-color: #dbeafe;
            border-color: #dbeafe;
            box-shadow: 0 2px 8px rgba(3, 105, 161, 0.3);
          }
        }

        // 修改按钮
        .edit-button {
          background-color: #f0fdf4;
          color: #16a34a;
          border-color: #f0fdf4;

          &:hover {
            background-color: #dcfce7;
            border-color: #dcfce7;
            box-shadow: 0 2px 8px rgba(22, 163, 74, 0.3);
          }
        }

        // 审核按钮
        .examine-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;

          &:hover {
            background-color: #bae6fd;
            border-color: #bae6fd;
            box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
          }
        }

        // 拒绝按钮
        .reject-button {
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;

          &:hover {
            background-color: #fde68a;
            border-color: #fde68a;
            box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
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

  // 移动端卡片视图 - 当 isMobileView = true 时显示
  .mobile-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    padding-bottom: 60px;
    overflow-y: auto;

    // 专栏卡片列表容器
    .column-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      // 单个专栏卡片
      .column-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        // 选中状态样式
        &.is-selected {
          border: 2px solid #e6a23c;
          box-shadow: 0 0 12px rgba(230, 162, 60, 0.3);
        }

        // 专栏卡片内容容器
        .column-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 专栏头部区域
          .column-header-section {
            display: flex;
            flex-direction: column;

            // 移动端专栏封面容器
            .column-cover-mobile {
              width: 100%;
              margin-bottom: 8px;
              position: relative;

              // 移动端复选框样式
              .mobile-checkbox {
                position: absolute;
                top: 8px;
                left: 8px;
                z-index: 10;
                background-color: rgba(255, 255, 255, 0.9);
                border-radius: 4px;
                padding: 4px;

                :deep(.el-checkbox__inner) {
                  width: 18px;
                  height: 18px;
                }
              }

              // 封面图片样式
              .column-cover-img {
                width: 100%;
                height: 120px;
                border-radius: 6px;
                cursor: pointer;
                transition: all 0.3s ease;
                object-fit: cover;

                &:hover {
                  transform: scale(1.02);
                  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
                }
              }

              // 无封面占位样式
              .no-cover-mobile {
                width: 100%;
                height: 120px;
                background-color: #f5f5f5;
                border: 1px dashed #ddd;
                border-radius: 6px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 12px;
                color: #999;
                text-align: center;
              }
            }

            // 专栏信息区域
            .column-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              // 专栏头部 - ID和状态
              .column-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-bottom: 5px;

                .column-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }
              }

              // 移动端专栏名称
              .column-name-mobile {
                font-size: 16px;
                font-weight: 600;
                color: #333;
                line-height: 1.4;
                margin-bottom: 8px;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              // 移动端作者信息样式
              .column-author-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .author-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .author-name {
                  color: #555;
                  font-weight: 500;
                }
              }

              // 移动端专栏描述
              .column-description-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: flex-start;

                .description-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                  flex-shrink: 0;
                }

                .description-content {
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

              // 移动端专栏元信息
              .column-meta-mobile {
                display: flex;
                flex-direction: column;
                gap: 4px;
                margin-bottom: 8px;

                .meta-item {
                  font-size: 12px;
                  color: #666;
                  display: flex;
                  align-items: center;

                  .label {
                    font-weight: 500;
                    margin-right: 4px;
                    color: #888;
                  }

                  .status-public {
                    color: #67c23a;
                    font-weight: 500;
                  }

                  .status-private {
                    color: #e6a23c;
                    font-weight: 500;
                  }
                }
              }

              // 专栏元信息
              .column-meta {
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

                // 统计数据行
                .stats-row {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 6px 8px;

                  .stat-item {
                    background-color: #f8f9fa;
                    padding: 3px 8px;
                    border-radius: 12px;
                    font-size: 11px;
                    color: #555;
                    border: 1px solid #e9ecef;
                    white-space: nowrap;

                    .label {
                      color: #666;
                      margin-right: 2px;
                    }
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

          // 专栏操作按钮
          .column-actions {
            display: flex;
            gap: 3px;
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

  // 通用的专栏状态样式
  .column-status {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;

    &.status-unaudited {
      background-color: #fff1f0;
      color: #f56c6c;
    }

    &.status-audited {
      background-color: #f0f9eb;
      color: #67c23a;
    }

    &.status-rejected {
      background-color: #fdf6ec;
      color: #e6a23c;
    }

    &.status-public {
      background-color: #f0f9eb;
      color: #67c23a;
    }

    &.status-private {
      background-color: #fdf6ec;
      color: #e6a23c;
    }
  }
}

// 专栏详情对话框样式
:deep(.column-detail-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #e6a23c 0%, #d19d00 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;
    position: relative;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      position: absolute;
      top: 50%;
      right: 20px;
      transform: translateY(-50%);
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
        transform: translateY(-50%) scale(1.1);
      }

      .el-dialog__close {
        color: white;
        font-size: 18px;
        font-weight: bold;

        &:hover {
          color: #ff6b6b;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
    max-height: 80vh;
    overflow-y: auto;
  }

  @media screen and (max-width: 767px) {
    width: 95% !important;

    .el-dialog__body {
      padding: 16px;
    }
  }
}

// 专栏详情容器
.column-detail {
  .column-info-section {
    margin-bottom: 24px;

    .column-detail-header {
      display: flex;
      gap: 24px;
      align-items: flex-start;

      .column-detail-info {
        flex: 1;

        .column-title-section {
          display: flex;
          align-items: center;
          gap: 12px;
          margin-bottom: 16px;

          .column-title-detail {
            margin: 0;
            font-size: 20px;
            font-weight: 700;
            color: #2c3e50;
            line-height: 1.3;
            flex: 1;
          }

          .column-id-detail {
            background: linear-gradient(45deg, #e6a23c, #d19d00);
            color: white;
            padding: 4px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
            white-space: nowrap;
          }
        }

        .column-cover-section {
          margin-bottom: 16px;

          .column-cover {
            width: 200px;
            height: 120px;
            border-radius: 8px;
            border: 1px solid var(--el-border-color);

            .loading-text {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              font-size: 12px;
              color: var(--el-text-color-regular);
              background-color: var(--el-bg-color-page);
            }

            .error {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background-color: var(--el-bg-color-page);

              .el-icon {
                font-size: 24px;
                color: var(--el-text-color-placeholder);
              }
            }
          }
        }

        .column-name-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }

          .name-text {
            padding: 12px;
            background-color: #f8f9fa;
            border-radius: 8px;
            line-height: 1.6;
            color: #333;
            border-left: 4px solid #e6a23c;
            font-weight: 600;
            font-size: 16px;
          }
        }

        .column-description-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }

          .description-text {
            padding: 12px;
            background-color: #f8f9fa;
            border-radius: 8px;
            line-height: 1.6;
            color: #333;
            border-left: 4px solid #e6a23c;
          }
        }

        .column-author-detail {
          margin-bottom: 16px;

          h4 {
            margin: 0 0 8px 0;
            font-size: 14px;
            font-weight: 600;
            color: #2c3e50;
          }

          .author-info {
            display: flex;
            align-items: center;
            gap: 8px;
            padding: 8px 12px;
            background-color: #f8f9fa;
            border-radius: 8px;

            .author-icon {
              color: #6c757d;
              font-size: 16px;
            }

            .author-name-detail {
              font-weight: 600;
              color: #495057;
            }
          }
        }

        .column-badges-detail {
          display: flex;
          flex-direction: column;
          gap: 12px;
          margin-bottom: 20px;

          .badge-group {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;

            .badge-label {
              font-size: 13px;
              font-weight: 600;
              color: #495057;
              min-width: 80px;
            }
          }
        }
      }
    }

    .column-stats-detail {
      border-top: 1px solid #e9ecef;
      padding-top: 20px;
      margin-top: 20px;

      .stats-group {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        justify-content: space-evenly;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 10px 16px;
          background: linear-gradient(135deg, #f8f9fa, #e9ecef);
          border-radius: 12px;
          border: 1px solid #dee2e6;
          transition: all 0.3s ease;
          flex: 1;
          min-width: 180px;
          max-width: 200px;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .stat-icon {
            font-size: 16px;
            color: #6c757d;
          }

          .stat-label {
            font-size: 12px;
            color: #6c757d;
            font-weight: 500;
          }

          .stat-value {
            flex: 1;
            font-size: 14px;
            font-weight: 700;
            color: #495057;
            margin-left: auto;
          }

          &.time-stat-item {
            background: linear-gradient(135deg, #fff3cd, #ffeaa7);
            border-color: #f39c12;

            .stat-icon {
              color: #e67e22;
            }

            .stat-label {
              color: #e67e22;
              font-weight: 600;
            }

            .stat-value {
              color: #d68910;
              font-size: 12px;
              font-weight: 600;
            }
          }
        }
      }
    }
  }
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 120px;
  padding: 16px 0;

  @media screen and (max-width: 768px) {
    gap: 4px;
  }

  .el-button {
    margin-left: 0px;
    padding: 10px 20px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
    }
  }
}

// 修改专栏对话框样式
:deep(.edit-column-dialog) {
  border-radius: 16px;

  .el-dialog__header {
    background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
    color: white;
    border-radius: 16px 16px 0 0;
    padding: 20px 24px;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
    }

    .el-dialog__headerbtn {
      position: absolute;
      top: 50%;
      right: 20px;
      transform: translateY(-50%);
      width: 32px;
      height: 32px;
      border-radius: 50%;
      background-color: rgba(255, 255, 255, 0.1);
      display: flex;
      align-items: center;
      justify-content: center;
      transition: all 0.3s ease;

      &:hover {
        background-color: rgba(255, 255, 255, 0.2);
        transform: translateY(-50%) scale(1.1);
      }

      .el-dialog__close {
        color: white;
        font-size: 18px;
        font-weight: bold;

        &:hover {
          color: #ff6b6b;
        }
      }
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  @media screen and (max-width: 767px) {
    width: 95% !important;

    .el-dialog__body {
      padding: 16px;
    }
  }
}

.edit-form {
  .el-form-item__label {
    font-weight: 600;
    color: #374151;
  }

  .el-input__wrapper {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:focus-within {
      box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.2);
      border-color: #16a34a;
    }
  }

  .el-textarea__inner {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:focus {
      box-shadow: 0 0 0 3px rgba(22, 163, 74, 0.2);
      border-color: #16a34a;
    }
  }

  .el-radio-group {
    .el-radio {
      margin-right: 20px;

      .el-radio__input.is-checked + .el-radio__label {
        color: #16a34a;
      }

      .el-radio__input.is-checked .el-radio__inner {
        border-color: #16a34a;
        background-color: #16a34a;
      }
    }
  }
}

// 专栏文章列表区域
.column-articles-section {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;

  .articles-title {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 16px;
    font-weight: 600;
    color: #2c3e50;
    margin: 0 0 16px 0;
    padding-bottom: 8px;
    border-bottom: 2px solid #e6a23c;

    .title-icon {
      color: #e6a23c;
      font-size: 18px;
    }
  }

  .articles-list {
    display: flex;
    flex-direction: column;
    gap: 0;
    max-height: 450px;
    overflow-y: auto;
    padding: 0;
    background: var(--el-bg-color);
    border-radius: 8px;
    border: 1px solid #e9ecef;

    // 自定义滚动条
    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-thumb {
      background: linear-gradient(180deg, #e6a23c, #d19d00);
      border-radius: 4px;
      border: 1px solid rgba(255, 255, 255, 0.2);

      &:hover {
        background: linear-gradient(180deg, #d19d00, #b8860b);
      }
    }

    &::-webkit-scrollbar-track {
      background: #f8f9fa;
      border-radius: 4px;
    }

    .article-item {
      display: flex;
      gap: 16px;
      padding: 16px 20px;
      background-color: var(--el-bg-color);
      border-bottom: 1px solid #f0f0f0;
      transition: all 0.3s ease;
      position: relative;

      &:last-child {
        border-bottom: none;
      }

      &:hover {
        border-bottom-color: #e6a23c;
        box-shadow: inset 0 0 0 1px rgba(230, 162, 60, 0.1);

        .article-index {
          transform: scale(1.1);
          box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);
        }

        .article-cover-mini .cover-img {
          transform: scale(1.05);
          box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
        }
      }

      &:nth-child(odd) {
        background-color: var(--el-bg-color);
      }

      .article-index {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 28px;
        height: 28px;
        background: linear-gradient(135deg, #e6a23c, #d19d00);
        color: white;
        border-radius: 50%;
        font-size: 13px;
        font-weight: 700;
        flex-shrink: 0;
        align-self: flex-start;
        margin-top: 2px;
        box-shadow: 0 2px 8px rgba(230, 162, 60, 0.2);
        transition: all 0.3s ease;
      }

      .article-cover-mini {
        flex-shrink: 0;

        .cover-img {
          width: 48px;
          height: 36px;
          border-radius: 6px;
          border: 2px solid #f0f0f0;
          transition: all 0.3s ease;
          object-fit: cover;
        }

        .loading-text {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 48px;
          height: 36px;
          font-size: 9px;
          color: #999;
          background: linear-gradient(135deg, #f8f9fa, #e9ecef);
          border-radius: 6px;
          border: 2px solid #f0f0f0;
        }

        .error {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 48px;
          height: 36px;
          background: linear-gradient(135deg, #f8f9fa, #e9ecef);
          border-radius: 6px;
          border: 2px solid #f0f0f0;

          .el-icon {
            font-size: 14px;
            color: #ccc;
          }
        }
      }

      .article-no-cover {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 48px;
        height: 36px;
        background: linear-gradient(135deg, #f1f3f4, #e8eaed);
        border: 2px dashed #d0d7de;
        border-radius: 6px;
        flex-shrink: 0;

        .el-icon {
          font-size: 16px;
          color: #8c959f;
        }
      }

      .article-info {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 8px;
        min-width: 0;

        .article-title-detail {
          font-size: 15px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          line-height: 1.4;
          margin-bottom: 4px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          word-break: break-word;
          cursor: pointer;
          transition: color 0.3s ease;

          &:hover {
            color: #e6a23c;
          }
        }

        .article-description-detail {
          font-size: 12px;
          color: #718096;
          line-height: 1.4;
          margin-bottom: 6px;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          line-clamp: 2;
          -webkit-box-orient: vertical;
          overflow: hidden;
          word-break: break-word;
        }

        .article-meta-detail {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          align-items: center;

          .meta-item {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 11px;
            color: #a0aec0;
            background: rgba(160, 174, 192, 0.1);
            padding: 3px 8px;
            border-radius: 12px;
            transition: all 0.3s ease;

            &:hover {
              background: rgba(230, 162, 60, 0.1);
              color: #e6a23c;

              .meta-icon {
                color: #e6a23c;
              }
            }

            .meta-icon {
              font-size: 12px;
              color: #cbd5e0;
              transition: color 0.3s ease;
            }

            .article-status {
              display: inline-block;
              padding: 3px 8px;
              border-radius: 12px;
              font-size: 10px;
              font-weight: 600;
              letter-spacing: 0.5px;

              &.status-unaudited {
                background: linear-gradient(135deg, #fed7d7, #feb2b2);
                color: #c53030;
                border: 1px solid rgba(197, 48, 48, 0.2);
              }

              &.status-audited {
                background: linear-gradient(135deg, #c6f6d5, #9ae6b4);
                color: #22543d;
                border: 1px solid rgba(34, 84, 61, 0.2);
              }

              &.status-rejected {
                background: linear-gradient(135deg, #feebc8, #fbd38d);
                color: #c05621;
                border: 1px solid rgba(192, 86, 33, 0.2);
              }
            }

            span {
              font-weight: 500;
            }
          }
        }
      }

      // 添加左侧装饰线
      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 0;
        width: 3px;
        height: 100%;
        background: linear-gradient(180deg, #e6a23c, #d19d00);
        opacity: 0;
        transition: opacity 0.3s ease;
      }

      &:hover::before {
        opacity: 1;
      }
    }
  }
}

// 无文章提示
.no-articles {
  margin-top: 24px;
  padding: 40px 0;
  text-align: center;
  border-top: 1px solid #e9ecef;
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

  // 时间筛选区域响应式
  .management-container .card .card-time-filters {
    .time-filter-group {
      .time-input {
        width: 140px;
      }
    }
  }

  // 在中屏幕上也保持搜索框的合理宽度
  .management-container .card .card-second {
    .search-input {
      width: 280px; // 中屏幕稍微增加宽度
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

    // 移动端时间筛选器
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

    // 移动端搜索框撑满宽度
    .card-second {
      .search-input {
        width: 100% !important;
      }
    }

    .table {
      margin-top: 0;
      max-height: calc(100vh - 240px);
    }
  }

  .column-detail .column-info-section {
    .column-detail-header {
      flex-direction: column;
      gap: 16px;
    }

    .column-stats-detail .stats-group {
      flex-direction: column;

      .stat-item {
        min-width: auto;
        max-width: none;
      }
    }
  }

  // 移动端文章列表布局优化
  .column-articles-section {
    .articles-list {
      .article-item {
        // 移动端改为纵向布局
        flex-direction: column;
        gap: 8px;
        padding: 12px;

        .article-index {
          width: 20px;
          height: 20px;
          font-size: 11px;
          margin-top: 0;
          align-self: flex-start;
        }

        .article-cover-mini {
          .cover-img {
            width: 160px;
            height: 100px;
          }

          .loading-text,
          .error {
            width: 32px;
            height: 24px;
            font-size: 8px;
          }
        }

        .article-no-cover {
          width: 32px;
          height: 24px;

          .el-icon {
            font-size: 12px;
          }
        }

        .article-info {
          gap: 4px;

          .article-title-detail {
            font-size: 13px;
            -webkit-line-clamp: 1;
            line-clamp: 1;
          }

          .article-description-detail {
            font-size: 11px;
            -webkit-line-clamp: 1;
            line-clamp: 1;
          }

          .article-meta-detail {
            gap: 4px;
            flex-wrap: wrap;
            justify-content: flex-start;

            .meta-item {
              font-size: 10px;
              padding: 2px 6px;
              flex-shrink: 0;

              .meta-icon {
                font-size: 10px;
              }

              .article-status {
                font-size: 9px;
                padding: 2px 6px;
              }
            }
          }
        }
      }
    }
  }
}
</style>
