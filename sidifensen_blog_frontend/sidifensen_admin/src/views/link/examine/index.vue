<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">友链审核</h2>
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
        <el-input v-model="searchKeyword" placeholder="搜索友链名称或描述" class="search-input" size="small" clearable @input="handleSearch">
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
      </div>

      <div class="card-third">
        <el-button type="primary" plain round @click="handleBatchAudit" :disabled="selectedLinks.length === 0" :loading="batchAuditLoading"> 批量审核 </el-button>
        <el-button type="warning" plain round @click="handleBatchReject" :disabled="selectedLinks.length === 0" :loading="batchRejectLoading"> 批量拒绝 </el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedLinks.length === 0" :loading="batchDeleteLoading"> 批量删除 </el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedLinkList" class="table" @selection-change="handleSelectionChange" :row-style="{ height: 'auto' }" :cell-style="{ padding: '8px 0' }">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="coverUrl" label="网站封面" width="120">
            <template #default="{ row }">
              <div class="link-cover-container">
                <el-image v-if="row.coverUrl" :src="row.coverUrl" class="link-cover" :preview-src-list="[row.coverUrl]" fit="cover" preview-teleported />
                <div v-else class="no-cover">暂无封面</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" label="ID" width="60" />
          <el-table-column prop="name" label="网站名称" min-width="150">
            <template #default="{ row }">
              <el-tooltip :content="row.name" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="link-name">{{ row.name }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="url" label="网站地址" min-width="200">
            <template #default="{ row }">
              <el-tooltip :content="row.url" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <a :href="row.url" target="_blank" class="link-url">{{ row.url }}</a>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="description" label="网站描述" min-width="250">
            <template #default="{ row }">
              <el-tooltip :content="row.description" placement="top-start" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                <div class="link-description">{{ row.description || "暂无描述" }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="userNickname" label="申请用户" width="120" />
          <el-table-column prop="email" label="联系邮箱" width="150">
            <template #default="{ row }">
              <el-tooltip :content="row.email" placement="top-start">
                <div class="link-email">{{ row.email }}</div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column prop="examineStatus" label="审核状态" width="80">
            <template #default="{ row }">
              <div class="link-status" :class="row.examineStatus === 0 ? 'status-unaudited' : row.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                {{ row.examineStatus === 0 ? "待审核" : row.examineStatus === 1 ? "已审核" : "未通过" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" sortable width="110" />
          <el-table-column label="操作" width="280">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="primary" @click="handleAuditLink(row.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectLink(row.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteLink(row.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="link-cards">
          <el-card v-for="link in paginatedLinkList" :key="link.id" class="link-card">
            <div class="link-card-content">
              <div class="link-header-section">
                <div class="link-cover-mobile">
                  <el-image v-if="link.coverUrl" :src="link.coverUrl" class="link-cover-img" :preview-src-list="[link.coverUrl]" fit="cover" preview-teleported />
                  <div v-else class="no-cover-mobile">暂无封面</div>
                </div>
                <div class="link-info">
                  <div class="link-header">
                    <div class="link-id">#{{ link.id }}</div>
                    <div class="link-status" :class="link.examineStatus === 0 ? 'status-unaudited' : link.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                      {{ link.examineStatus === 0 ? "待审核" : link.examineStatus === 1 ? "已审核" : "未通过" }}
                    </div>
                  </div>
                  <div class="link-name-mobile">{{ link.name }}</div>

                  <!-- 网站地址 -->
                  <div class="link-url-mobile">
                    <span class="url-label">网站地址:</span>
                    <a :href="link.url" target="_blank" class="url-link">{{ link.url }}</a>
                  </div>

                  <!-- 申请用户 -->
                  <div class="link-user-mobile">
                    <span class="user-label">申请用户:</span>
                    <span class="user-name">{{ link.userNickname }}</span>
                  </div>

                  <!-- 联系邮箱 -->
                  <div class="link-email-mobile">
                    <span class="email-label">联系邮箱:</span>
                    <span class="email-content">{{ link.email }}</span>
                  </div>

                  <!-- 网站描述 -->
                  <div class="link-description-mobile" v-if="link.description">
                    <span class="description-label">描述:</span>
                    <span class="description-content">{{ link.description }}</span>
                  </div>

                  <!-- 创建时间 -->
                  <div class="link-meta">
                    <div class="time-row">
                      <div class="meta-item time-item">
                        <span class="label">创建:</span>
                        <span>{{ link.createTime }}</span>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
              <div class="link-actions">
                <el-button type="primary" @click="handleAuditLink(link.id)" :icon="Check" class="examine-button" size="small">审核</el-button>
                <el-button type="warning" @click="handleRejectLink(link.id)" :icon="Close" class="reject-button" size="small">拒绝</el-button>
                <el-button type="danger" @click="handleDeleteLink(link.id)" :icon="Delete" class="delete-button" size="small">删除</el-button>
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
import { ref, onMounted, onUnmounted, computed } from "vue";
import { Delete, Close, Check, Search } from "@element-plus/icons-vue";
import { getUserList } from "@/api/user";
import { adminGetLinkList, adminSearchLink, adminExamineLink, adminBatchExamineLink, adminDeleteLink, adminBatchDeleteLink } from "@/api/link";

// 友链列表数据
const linkList = ref([]);
const paginatedLinkList = ref([]);
const loading = ref(false);
const currentPage = ref(1);
const pageSize = ref(10);
const total = ref(0);

// 用户列表
const userList = ref([]);

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

// 移动端检测
const isMobileView = ref(false);

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

// 获取友链列表
const getLinks = async () => {
  loading.value = true;
  try {
    const res = await adminGetLinkList();
    linkList.value = res.data.data.sort((a, b) => b.id - a.id);
    total.value = linkList.value.length;
    currentPage.value = 1;
    updatePaginatedLinkList();
  } catch (error) {
    ElMessage.error("获取友链列表失败");
  } finally {
    loading.value = false;
  }
};

// 更新分页数据
const updatePaginatedLinkList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedLinkList.value = linkList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  updatePaginatedLinkList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedLinkList();
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
      createTimeStart: searchStartTime.value ? new Date(searchStartTime.value) : undefined,
      createTimeEnd: searchEndTime.value ? new Date(searchEndTime.value) : undefined,
    };

    const res = await adminSearchLink(searchData);
    linkList.value = res.data.data;
    total.value = linkList.value.length;
    currentPage.value = 1;
    updatePaginatedLinkList();
  } catch (error) {
    ElMessage.error("搜索友链失败");
  } finally {
    loading.value = false;
  }
};

// 智能刷新列表
const refreshLinkList = async () => {
  // 检查是否有任何搜索条件
  const hasSearchConditions = searchExamineStatus.value || searchKeyword.value || searchStartTime.value || searchEndTime.value || searchUserId.value;

  if (hasSearchConditions) {
    await handleSearch();
  } else {
    await getLinks();
  }
};

// 表格多选
const handleSelectionChange = (links) => {
  selectedLinks.value = links;
};

// 处理单个友链审核
const handleAuditLink = async (linkId) => {
  try {
    await adminExamineLink(linkId, 1);
    ElMessage.success("审核成功");
    await refreshLinkList();
  } catch (error) {
    ElMessage.error("审核失败");
  }
};

// 处理批量审核
const handleBatchAudit = () => {
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
const handleRejectLink = async (linkId) => {
  try {
    await adminExamineLink(linkId, 2);
    ElMessage.success("拒绝成功");
    await refreshLinkList();
  } catch (error) {
    ElMessage.error("拒绝失败");
  }
};

// 处理批量拒绝
const handleBatchReject = () => {
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
const handleDeleteLink = (linkId) => {
  ElMessageBox.confirm("确定要删除该友链吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await adminDeleteLink(linkId);
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
  getLinks();
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
// 友链审核页面主容器
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
          box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2);
          border-color: #409eff;
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

    // 友链表格
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

      // 友链名称样式
      .link-name {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        cursor: pointer;

        &:hover {
          color: #409eff;
        }
      }

      // 友链地址样式
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

      // 友链描述样式
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

      // 邮箱样式
      .link-email {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        font-size: 12px;
        color: #666;
      }

      // 友链封面容器样式
      .link-cover-container {
        display: flex;
        justify-content: center;
        align-items: center;
        height: 100%;

        // 有封面图片样式
        .link-cover {
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
        .examine-button,
        .reject-button,
        .delete-button {
          border-radius: 6px;
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
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

    // 友链卡片列表容器
    .link-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 10px;

      // 单个友链卡片
      .link-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        // 友链卡片内容容器
        .link-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 友链头部区域
          .link-header-section {
            display: flex;
            flex-direction: column;

            // 移动端友链封面容器
            .link-cover-mobile {
              width: 100%;
              margin-bottom: 8px;

              // 封面图片样式
              .link-cover-img {
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

            // 友链信息区域
            .link-info {
              width: 100%;
              display: flex;
              flex-direction: column;

              // 友链头部 - ID和状态
              .link-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                flex-wrap: wrap;
                gap: 8px;
                margin-bottom: 5px;

                .link-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }
              }

              // 移动端友链名称
              .link-name-mobile {
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

              // 移动端网站地址样式
              .link-url-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .url-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .url-link {
                  color: #409eff;
                  text-decoration: none;
                  font-weight: 500;
                  word-break: break-all;

                  &:hover {
                    text-decoration: underline;
                  }
                }
              }

              // 移动端申请用户样式
              .link-user-mobile {
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

                .user-name {
                  color: #555;
                  font-weight: 500;
                }
              }

              // 移动端联系邮箱样式
              .link-email-mobile {
                font-size: 13px;
                color: #666;
                margin-bottom: 8px;
                display: flex;
                align-items: center;

                .email-label {
                  font-weight: 500;
                  color: #888;
                  margin-right: 4px;
                }

                .email-content {
                  color: #555;
                  font-weight: 400;
                  word-break: break-all;
                }
              }

              // 移动端友链描述
              .link-description-mobile {
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

              // 友链元信息
              .link-meta {
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

          // 友链操作按钮
          .link-actions {
            display: flex;
            gap: px;
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

  // 通用的友链状态样式
  .link-status {
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

    .pagination-container {
      padding: 4px;

      :deep(.el-pagination .el-pager) {
        display: none;
      }
    }
  }

  .link-detail .link-info-section {
    .link-detail-header {
      flex-direction: column;
      gap: 16px;
    }

    .link-stats-detail .stats-group {
      flex-direction: column;

      .stat-item {
        min-width: auto;
        max-width: none;
      }
    }
  }
}
</style>
