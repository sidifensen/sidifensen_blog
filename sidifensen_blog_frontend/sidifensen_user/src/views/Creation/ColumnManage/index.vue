<template>
  <div class="column-manage-container">
    <div class="main-content">
      <!-- 筛选区域 -->
      <div class="filter-section">
        <div class="filter-row">
          <div class="filter-item">
            <el-select v-model="selectedYear" placeholder="年份" @change="handleDateFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">年份:</span>
              </template>
              <el-option label="不限" :value="null"></el-option>
              <el-option v-for="year in availableYears" :key="year" :label="year + '年'" :value="year"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-select v-model="selectedMonth" placeholder="月份" @change="handleDateFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">月份:</span>
              </template>
              <el-option label="不限" :value="null"></el-option>
              <el-option v-for="month in 12" :key="month" :label="month + '月'" :value="month"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-select v-model="showStatus" placeholder="展示状态" @change="handleStatusFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">状态:</span>
              </template>
              <el-option label="全部" :value="null"></el-option>
              <el-option label="公开" :value="0"></el-option>
              <el-option label="私密" :value="1"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-input v-model="searchKeyword" placeholder="请输入关键词" @keyup.enter="handleSearch" class="search-input">
              <template #prefix>
                <el-icon>
                  <Search />
                </el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </div>

      <!-- 专栏列表区域 -->
      <div class="column-list-container" ref="listContainer" @scroll="handleScroll">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="columns.length === 0" class="empty-container">
          <el-empty description="暂无专栏数据"></el-empty>
        </div>

        <div v-else class="column-cards">
          <el-card v-for="column in columns" :key="column.id" class="column-card">
            <div class="column-card-content">
              <!-- 专栏封面和基本信息 -->
              <div class="column-info">
                <el-image :src="column.coverUrl" alt="专栏封面" class="column-cover">
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error">
                      <el-icon>
                        <Picture />
                      </el-icon>
                    </div>
                  </template>
                </el-image>

                <div class="column-details">
                  <h4 class="column-title">{{ column.name }}</h4>
                  <el-tooltip v-if="column.description && column.description.length > 50" :content="column.description" placement="top">
                    <p class="column-description">{{ column.description }}</p>
                  </el-tooltip>
                  <p v-else class="column-description">{{ column.description || "暂无描述" }}</p>
                </div>
              </div>

              <!-- 专栏统计信息 -->
              <div class="column-stats">
                <div class="stat-item">
                  <el-icon>
                    <Document />
                  </el-icon>
                  <span>{{ column.articleCount || 0 }} 文章</span>
                </div>
                <div class="stat-item">
                  <el-icon>
                    <Star />
                  </el-icon>
                  <span>{{ column.focusCount || 0 }} 关注</span>
                </div>
                <div class="stat-item">
                  <span class="status-badge" :class="column.showStatus === 0 ? 'public' : 'private'">
                    {{ column.showStatus === 0 ? "公开" : "私密" }}
                  </span>
                </div>
              </div>

              <!-- 专栏元信息 -->
              <div class="column-meta">
                <span class="column-date">创建时间：{{ formatTime(column.createTime) }}</span>
                <span class="column-sort">排序：{{ column.sort }}</span>
              </div>

              <!-- 专栏操作 -->
              <div class="column-actions">
                <div class="sort-actions">
                  <el-button type="primary" text size="small" @click="handleSortUp(column)" :disabled="sortLoading || isFirstColumn(column.id)">
                    <el-icon><ArrowUp /></el-icon>
                    上移
                  </el-button>
                  <el-button type="primary" text size="small" @click="handleSortDown(column)" :disabled="sortLoading || isLastColumn(column.id)">
                    <el-icon><ArrowDown /></el-icon>
                    下移
                  </el-button>
                </div>
                <div class="operation-actions">
                  <el-button type="primary" text @click="handleEditColumn(column)">编辑专栏</el-button>
                  <el-button type="danger" text @click="handleDeleteColumn(column.id)">删除专栏</el-button>
                </div>
              </div>
            </div>
          </el-card>

          <!-- 加载更多指示器 -->
          <div v-if="loadingMore" class="loading-more">
            <div class="loading-spinner small"></div>
            <span>加载更多...</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Search, Picture, Document, Star, ArrowUp, ArrowDown } from "@element-plus/icons-vue";
import { getUserColumnManageList, updateColumn, deleteColumn } from "@/api/column";
import { useUserStore } from "@/stores/userStore";
import { formatTime } from "@/utils/formatTime";

const router = useRouter();
const userStore = useUserStore();

// 专栏列表数据
const columns = ref([]);
// 加载状态
const loading = ref(false);
const loadingMore = ref(false);
const sortLoading = ref(false);
// 当前页码
const currentPage = ref(1);
// 页面大小
const pageSize = ref(20);
// 是否还有更多数据
const hasMore = ref(true);
// 搜索关键词
const searchKeyword = ref("");
// 列表容器引用
const listContainer = ref(null);

// 年份月份筛选
const selectedYear = ref(null);
const selectedMonth = ref(null);
const availableYears = ref([]);
const showStatus = ref(null);

// 加载专栏列表
const loadColumns = async (reset = false) => {
  // 如果没有更多数据或者已经在加载中，则不再请求
  if (!hasMore.value || loading.value || loadingMore.value) {
    return;
  }

  // 设置加载状态
  if (reset) {
    loading.value = true;
  } else {
    loadingMore.value = true;
  }

  try {
    // 构建筛选参数
    const filterParams = {};

    // 搜索关键词
    if (searchKeyword.value.trim()) {
      filterParams.keyword = searchKeyword.value.trim();
    }

    // 展示状态筛选
    if (showStatus.value !== null) {
      filterParams.showStatus = showStatus.value;
    }

    // 年份筛选
    if (selectedYear.value) {
      filterParams.year = selectedYear.value;
    }

    // 月份筛选
    if (selectedMonth.value) {
      filterParams.month = selectedMonth.value;
    }

    // 发送请求获取专栏列表
    const res = await getUserColumnManageList(currentPage.value, pageSize.value, filterParams);
    const newColumns = res.data.data.data || [];
    const total = res.data.data.total || 0;

    if (reset) {
      // 初次加载或筛选条件改变时
      columns.value = newColumns;
      // 从专栏数据中提取年份信息并更新筛选选项
      updateDateFiltersFromColumns(newColumns);
    } else {
      // 无限滚动时加载下一页数据
      columns.value = [...columns.value, ...newColumns];
      // 合并新数据后更新筛选选项
      updateDateFiltersFromColumns(columns.value);
    }

    // 判断是否还有更多数据
    hasMore.value = columns.value.length < total;

    // 更新页码
    if (hasMore.value && newColumns.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    console.error("加载专栏列表失败:", error);
    ElMessage.error("加载专栏列表失败");
  } finally {
    // 重置加载状态
    loading.value = false;
    loadingMore.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  columns.value = [];
  hasMore.value = true;
  loadColumns(true);
};

// 处理状态筛选变化
const handleStatusFilterChange = () => {
  currentPage.value = 1;
  columns.value = [];
  hasMore.value = true;
  loadColumns(true);
};

// 处理日期筛选变化
const handleDateFilterChange = () => {
  currentPage.value = 1;
  columns.value = [];
  hasMore.value = true;
  loadColumns(true);
};

// 处理滚动事件 - 自定义无限滚动
const handleScroll = () => {
  // 如果没有列表容器或正在加载中或加载更多中或没有更多内容时,不用加载下一页了
  if (!listContainer.value || loading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  const container = listContainer.value;
  // 当滚动到底部附近时加载更多
  if (container.scrollTop + container.clientHeight >= container.scrollHeight - 100) {
    loadColumns();
  }
};

// 从专栏列表中提取年份选项
const updateDateFiltersFromColumns = (columnList) => {
  if (!columnList || columnList.length === 0) {
    availableYears.value = [];
    return;
  }
  // 提取所有专栏的年份并去重排序
  const years = [
    ...new Set(
      columnList.map((column) => {
        const createTime = new Date(column.createTime);
        return createTime.getFullYear();
      })
    ),
  ].sort((a, b) => b - a);
  availableYears.value = years;
};

// 判断是否是第一个专栏（排序值最小，不能上移）
const isFirstColumn = (columnId) => {
  if (columns.value.length === 0) return true;
  const column = columns.value.find((c) => c.id === columnId);
  if (!column) return true;
  return columns.value.every((c) => c.sort >= column.sort);
};

// 判断是否是最后一个专栏（排序值最大，不能下移）
const isLastColumn = (columnId) => {
  if (columns.value.length === 0) return true;
  const column = columns.value.find((c) => c.id === columnId);
  if (!column) return true;
  return columns.value.every((c) => c.sort <= column.sort);
};

// 处理专栏上移
const handleSortUp = async (column) => {
  if (sortLoading.value || isFirstColumn(column.id)) return;

  try {
    sortLoading.value = true;

    // 找到当前专栏在数组中的位置
    const currentIndex = columns.value.findIndex((c) => c.id === column.id);
    if (currentIndex <= 0) return;

    // 找到上一个专栏
    const prevColumn = columns.value[currentIndex - 1];

    // 交换排序值
    const tempSort = column.sort;
    column.sort = prevColumn.sort;
    prevColumn.sort = tempSort;

    // 更新后端数据
    await updateColumn({
      id: column.id,
      name: column.name,
      description: column.description,
      coverUrl: column.coverUrl,
      showStatus: column.showStatus,
      sort: column.sort,
    });

    await updateColumn({
      id: prevColumn.id,
      name: prevColumn.name,
      description: prevColumn.description,
      coverUrl: prevColumn.coverUrl,
      showStatus: prevColumn.showStatus,
      sort: prevColumn.sort,
    });

    // 交换数组中的位置
    columns.value[currentIndex] = prevColumn;
    columns.value[currentIndex - 1] = column;

    ElMessage.success("专栏排序调整成功");
  } catch (error) {
    console.error("专栏排序失败:", error);
    ElMessage.error("专栏排序失败，请重试");
    // 恢复原始排序
    loadColumns(true);
  } finally {
    sortLoading.value = false;
  }
};

// 处理专栏下移
const handleSortDown = async (column) => {
  if (sortLoading.value || isLastColumn(column.id)) return;

  try {
    sortLoading.value = true;

    // 找到当前专栏在数组中的位置
    const currentIndex = columns.value.findIndex((c) => c.id === column.id);
    if (currentIndex >= columns.value.length - 1) return;

    // 找到下一个专栏
    const nextColumn = columns.value[currentIndex + 1];

    // 交换排序值
    const tempSort = column.sort;
    column.sort = nextColumn.sort;
    nextColumn.sort = tempSort;

    // 更新后端数据
    await updateColumn({
      id: column.id,
      name: column.name,
      description: column.description,
      coverUrl: column.coverUrl,
      showStatus: column.showStatus,
      sort: column.sort,
    });

    await updateColumn({
      id: nextColumn.id,
      name: nextColumn.name,
      description: nextColumn.description,
      coverUrl: nextColumn.coverUrl,
      showStatus: nextColumn.showStatus,
      sort: nextColumn.sort,
    });

    // 交换数组中的位置
    columns.value[currentIndex] = nextColumn;
    columns.value[currentIndex + 1] = column;

    ElMessage.success("专栏排序调整成功");
  } catch (error) {
    console.error("专栏排序失败:", error);
    ElMessage.error("专栏排序失败，请重试");
    // 恢复原始排序
    loadColumns(true);
  } finally {
    sortLoading.value = false;
  }
};

// 编辑专栏
const handleEditColumn = (column) => {
  // 这里可以跳转到编辑页面或者打开编辑对话框
  ElMessage.info("编辑专栏功能待开发");
};

// 删除专栏
const handleDeleteColumn = async (columnId) => {
  try {
    await ElMessageBox.confirm("确定要删除这个专栏吗？此操作不可恢复", "删除专栏", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteColumn(columnId);
    ElMessage.success("专栏删除成功");

    // 重新加载专栏列表
    currentPage.value = 1;
    columns.value = [];
    hasMore.value = true;
    loadColumns(true);
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除专栏失败:", error);
      ElMessage.error("删除专栏失败，请重试");
    }
  }
};

// 组件挂载时的处理
onMounted(() => {
  loadColumns(true);
});

// 组件卸载时的处理
onUnmounted(() => {
  // 清理资源
  listContainer.value = null;
});
</script>

<style lang="scss" scoped>
.column-manage-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 48px);

  .main-content {
    // 筛选区域
    .filter-section {
      background-color: var(--el-bg-color);
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      padding: 16px;
      margin-bottom: 16px;

      .filter-row {
        display: flex;
        align-items: center;
        gap: 16px;
        flex-wrap: wrap;

        .filter-item {
          .filter-select {
            width: 180px;
          }

          .search-input {
            width: 220px;
          }

          .select-prefix {
            margin-right: 5px;
            font-size: 14px;
            color: var(--el-text-color-regular);
          }
        }
      }
    }

    // 专栏列表容器
    .column-list-container {
      background-color: var(--el-bg-color);
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      flex: 1;
      overflow-y: auto;
      position: relative;
      max-height: calc(100vh - 200px);

      // 加载容器
      .loading-container,
      .loading-more {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 40px;
        color: var(--el-text-color-secondary);

        .loading-spinner {
          margin-right: 10px;
        }
      }

      // 空状态容器
      .empty-container {
        padding: 60px 20px;
        text-align: center;
      }

      // 专栏卡片列表
      .column-cards {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
        gap: 16px;
        padding: 16px;

        :deep(.el-card__body) {
          height: 100% !important;
        }

        .column-card {
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .column-card-content {
            display: flex;
            flex-direction: column;
            height: 100%;

            // 专栏信息区域
            .column-info {
              display: flex;
              align-items: flex-start;
              gap: 12px;
              margin-bottom: 16px;
              padding-bottom: 12px;
              border-bottom: 1px solid var(--el-border-color-light);

              .column-cover {
                width: 100px;
                height: 75px;
                border-radius: 6px;
                flex-shrink: 0;

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

                // 错误占位图标样式
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

              .column-details {
                flex: 1;
                min-width: 0;

                .column-title {
                  font-size: 16px;
                  font-weight: 600;
                  color: var(--el-text-color-primary);
                  margin: 0 0 8px 0;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  line-clamp: 2;
                  -webkit-box-orient: vertical;
                  line-height: 1.4;
                }

                .column-description {
                  font-size: 14px;
                  color: var(--el-text-color-regular);
                  margin: 0;
                  line-height: 1.5;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  line-clamp: 2;
                  -webkit-box-orient: vertical;
                }
              }
            }

            // 专栏统计信息
            .column-stats {
              display: flex;
              gap: 16px;
              padding: 8px 0;
              border-bottom: 1px solid var(--el-border-color-lighter);
              margin-bottom: 12px;

              .stat-item {
                display: flex;
                align-items: center;
                gap: 4px;
                color: var(--el-text-color-secondary);
                font-size: 13px;

                .el-icon {
                  font-size: 16px;
                }

                .status-badge {
                  padding: 2px 8px;
                  border-radius: 4px;
                  font-size: 12px;
                  font-weight: 500;

                  &.public {
                    background-color: #e8f5e8;
                    color: #52c41a;
                  }

                  &.private {
                    background-color: #fff7e6;
                    color: #fa8c16;
                  }
                }
              }
            }

            // 专栏元信息
            .column-meta {
              display: flex;
              justify-content: space-between;
              align-items: center;
              font-size: 13px;
              color: var(--el-text-color-secondary);
              margin-bottom: 12px;

              .column-sort {
                font-weight: 500;
                color: var(--el-color-primary);
              }
            }

            // 专栏操作
            .column-actions {
              display: flex;
              justify-content: space-between;
              align-items: center;
              gap: 8px;
              padding-top: 8px;
              border-top: 1px solid var(--el-border-color-lighter);

              .sort-actions {
                display: flex;
                gap: 4px;
              }

              .operation-actions {
                display: flex;
                gap: 8px;
              }
            }
          }
        }
      }
    }
  }

  // 自定义加载指示器样式
  .loading-spinner {
    width: 20px;
    height: 20px;
    border: 2px solid #f3f3f3;
    border-top: 2px solid var(--el-color-primary);
    border-radius: 50%;
    animation: spin 1s linear infinite;

    // 小尺寸加载指示器
    &.small {
      width: 16px;
      height: 16px;
      border-width: 1px;
      margin-right: 8px;
      display: inline-block;
      vertical-align: middle;
    }
  }

  // 加载动画
  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }

    100% {
      transform: rotate(360deg);
    }
  }
}

// 响应式设计 - 平板端
@media screen and (max-width: 992px) and (min-width: 769px) {
  .column-manage-container {
    .main-content {
      .column-list-container {
        .column-cards {
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        }
      }
    }
  }
}

// 响应式设计 - 移动端
@media screen and (max-width: 768px) {
  .column-manage-container {
    height: calc(100vh);

    .main-content {
      .filter-section {
        padding: 10px;
        margin-bottom: 10px;

        .filter-row {
          flex-direction: column;
          align-items: stretch;
          gap: 10px;

          .filter-item {
            .filter-select,
            .search-input {
              width: 100%;
            }
          }
        }
      }

      .column-list-container {
        max-height: calc(100vh - 60px);

        .column-cards {
          grid-template-columns: 1fr;
          padding: 10px;
          gap: 12px;

          .column-card {
            .column-card-content {
              .column-info {
                .column-cover {
                  width: 80px;
                  height: 60px;
                }

                .column-details {
                  .column-title {
                    font-size: 15px;
                    -webkit-line-clamp: 1;
                    line-clamp: 1;
                  }

                  .column-description {
                    font-size: 13px;
                    -webkit-line-clamp: 1;
                    line-clamp: 1;
                  }
                }
              }

              .column-stats {
                flex-wrap: wrap;
                gap: 8px;

                .stat-item {
                  font-size: 12px;

                  .el-icon {
                    font-size: 14px;
                  }

                  .status-badge {
                    font-size: 11px;
                    padding: 1px 6px;
                  }
                }
              }

              .column-meta {
                flex-direction: column;
                align-items: flex-start;
                gap: 4px;
                font-size: 12px;
              }

              .column-actions {
                flex-direction: column;
                align-items: stretch;
                gap: 8px;

                .sort-actions,
                .operation-actions {
                  justify-content: center;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
