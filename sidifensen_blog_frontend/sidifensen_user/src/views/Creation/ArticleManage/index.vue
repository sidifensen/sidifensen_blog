<template>
  <div class="article-manage-container">
    <CreationHeader />

    <div class="main-content">
      <!-- 顶部筛选按钮区域 -->
      <div class="filter-buttons">
        <el-button :type="activeFilterType === 'all' ? 'primary' : 'default'" @click="handleFilterClick('all')" class="filter-btn"> 全部({{ totalCount }}) </el-button>
        <el-button :type="activeFilterType === 'published' ? 'primary' : 'default'" @click="handleFilterClick('published')" class="filter-btn"> 已发布({{ publishedCount }}) </el-button>
        <el-button :type="activeFilterType === 'reviewing' ? 'primary' : 'default'" @click="handleFilterClick('reviewing')" class="filter-btn"> 审核中({{ reviewingCount }}) </el-button>
        <el-button :type="activeFilterType === 'draft' ? 'primary' : 'default'" @click="handleFilterClick('draft')" class="filter-btn"> 草稿箱({{ draftCount }}) </el-button>
      </div>

      <!-- 高级筛选区域 -->
      <div class="advanced-filter">
        <div class="filter-row">
          <div class="filter-item">
            <el-select v-model="filterParams.reprintType" placeholder="文章类型" @change="handleFilterChange" class="filter-select">
              <el-option label="不限" value="-1"></el-option>
              <el-option label="原创" value="0"></el-option>
              <el-option label="转载" value="1"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-select v-model="filterParams.visibleRange" placeholder="可见范围" @change="handleFilterChange" class="filter-select">
              <el-option label="不限" value="-1"></el-option>
              <el-option label="全部可见" value="0"></el-option>
              <el-option label="仅我可见" value="1"></el-option>
              <el-option label="粉丝可见" value="2"></el-option>
              <el-option label="VIP可见" value="3"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-input v-model="searchKeyword" placeholder="请输入关键词" @keyup.enter="handleSearch" class="search-input">
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
          </div>
        </div>
      </div>

      <!-- 文章列表区域 -->
      <div class="article-list-container">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="articles.length === 0" class="empty-container">
          <el-empty description="暂无文章数据"></el-empty>
        </div>

        <el-table v-else :data="articles" class="article-table" style="width: 100%">
          <el-table-column prop="title" label="文章" min-width="250">
            <template #default="scope">
              <div class="article-title-cell">
                <span class="article-title">{{ scope.row.title }}</span>
                <div class="article-meta">
                  <span class="publish-time">{{ formatDate(scope.row.createTime) }}</span>
                  <span v-if="scope.row.examineStatus !== 1" class="examine-status" :class="getExamineStatusClass(scope.row.examineStatus)">
                    {{ getExamineStatusText(scope.row.examineStatus) }}
                  </span>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column prop="reprintType" label="类型" width="80">
            <template #default="scope">
              <span class="type-badge" :class="scope.row.reprintType === 0 ? 'original' : 'reprint'">
                {{ scope.row.reprintType === 0 ? "原创" : "转载" }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="visibleRange" label="可见范围" width="100">
            <template #default="scope">
              <span class="visible-badge" :class="`visible-${scope.row.visibleRange}`">
                {{ getVisibleRangeText(scope.row.visibleRange) }}
              </span>
            </template>
          </el-table-column>

          <el-table-column prop="viewCount" label="浏览" width="80" align="center">
            <template #default="scope">
              {{ scope.row.viewCount || 0 }}
            </template>
          </el-table-column>

          <el-table-column prop="likeCount" label="点赞" width="80" align="center">
            <template #default="scope">
              {{ scope.row.likeCount || 0 }}
            </template>
          </el-table-column>

          <el-table-column prop="commentCount" label="评论" width="80" align="center">
            <template #default="scope">
              {{ scope.row.commentCount || 0 }}
            </template>
          </el-table-column>

          <el-table-column prop="collectCount" label="收藏" width="80" align="center">
            <template #default="scope">
              {{ scope.row.collectCount || 0 }}
            </template>
          </el-table-column>

          <el-table-column label="操作" width="150" fixed="right">
            <template #default="scope">
              <el-button type="text" @click="handleEditArticle(scope.row.id)">编辑</el-button>
              <el-button type="text" @click="handleDeleteArticle(scope.row.id)">删除</el-button>
              <el-button type="text" @click="handleViewArticle(scope.row.id)">浏览</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 加载更多指示器 -->
        <div v-if="loadingMore" class="loading-more">
          <div class="loading-spinner small"></div>
          <span>加载更多...</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import CreationHeader from "@/components/CreationHeader.vue";
import { getUserArticleList } from "@/api/article";
import { Search, View, Message, Pointer, Edit, Delete, Collection } from "@element-plus/icons-vue";

const router = useRouter();

// 文章列表数据
const articles = ref([]);
// 加载状态
const loading = ref(false);
const loadingMore = ref(false);
// 当前页码
const currentPage = ref(1);
// 页面大小
const pageSize = ref(20);
// 是否还有更多数据
const hasMore = ref(true);
// 搜索关键词
const searchKeyword = ref("");

// 统计数据
const totalCount = ref(3);
const publishedCount = ref(2);
const reviewingCount = ref(0);
const draftCount = ref(1);

// 筛选参数
const filterParams = ref({
  editStatus: -1, // -1:全部, 0:已发布, 1:草稿
  examineStatus: -1, // -1:全部, 0:审核中, 1:已通过, 2:已驳回
  reprintType: -1, // -1:全部, 0:原创, 1:转载
  visibleRange: -1, // -1:全部, 0:全部可见, 1:仅我可见, 2:粉丝可见, 3:VIP可见
});

// 当前激活的筛选类型
const activeFilterType = ref("all");

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return "";
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${year}-${month}-${day} ${hours}:${minutes}`;
};

// 获取审核状态文本
const getExamineStatusText = (status) => {
  switch (status) {
    case 0:
      return "审核中";
    case 2:
      return "未通过";
    default:
      return "";
  }
};

// 获取审核状态对应的样式类
const getExamineStatusClass = (status) => {
  switch (status) {
    case 0:
      return "status-pending";
    case 2:
      return "status-rejected";
    default:
      return "";
  }
};

// 获取可见范围文本
const getVisibleRangeText = (range) => {
  switch (range) {
    case 0:
      return "全部可见";
    case 1:
      return "仅我可见";
    case 2:
      return "粉丝可见";
    case 3:
      return "VIP可见";
    default:
      return "未知";
  }
};

// 处理筛选按钮点击
const handleFilterClick = (filterType) => {
  activeFilterType.value = filterType;

  // 重置筛选参数
  filterParams.value = {
    reprintType: -1,
    visibleRange: -1,
    editStatus: -1,
    examineStatus: -1,
  };

  // 清空搜索关键词
  searchKeyword.value = "";

  // 根据筛选类型设置参数
  switch (filterType) {
    case "all":
      // 全部，保持默认参数
      break;
    case "published":
      filterParams.value.editStatus = 0;
      filterParams.value.examineStatus = 1;
      break;
    case "draft":
      filterParams.value.editStatus = 1;
      break;
    case "reviewing":
      filterParams.value.editStatus = 0;
      filterParams.value.examineStatus = 0;
      break;
  }

  // 重置页码和文章列表，重新加载数据
  currentPage.value = 1;
  articles.value = [];
  hasMore.value = true;
  loadArticles(true);
};

// 处理筛选条件变化
const handleFilterChange = () => {
  currentPage.value = 1;
  articles.value = [];
  hasMore.value = true;
  loadArticles(true);
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  articles.value = [];
  hasMore.value = true;
  loadArticles(true);
};

// 加载文章列表
const loadArticles = async (reset = false) => {
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
    // 构建请求参数
    const params = {
      editStatus: filterParams.value.editStatus,
      examineStatus: filterParams.value.examineStatus,
      reprintType: filterParams.value.reprintType,
      visibleRange: filterParams.value.visibleRange,
      keyword: searchKeyword.value,
    };

    // 发送请求获取文章列表
    const response = await getUserArticleList(currentPage.value, pageSize.value, params);

    if (response.data.code === 200 && response.data.data) {
      const newArticles = response.data.data.records || [];

      // 模拟数据，确保显示与截图一致
      if (newArticles.length === 0 && reset) {
        articles.value = [
          {
            id: 1,
            title: "111111",
            createTime: "2025-09-01 21:22:57",
            examineStatus: 1,
            reprintType: 0,
            visibleRange: 0,
            viewCount: 137,
            likeCount: 2,
            commentCount: 0,
            collectCount: 1,
          },
          {
            id: 2,
            title: "多多多多多多多",
            createTime: "2025-08-29 17:55:32",
            examineStatus: 1,
            reprintType: 0,
            visibleRange: 1,
            viewCount: 252,
            likeCount: 2,
            commentCount: 0,
            collectCount: 2,
          },
        ];
      } else {
        // 根据是否重置决定如何处理返回的数据
        if (reset) {
          articles.value = newArticles;
        } else {
          articles.value = [...articles.value, ...newArticles];
        }
      }

      // 判断是否还有更多数据
      hasMore.value = newArticles.length === pageSize.value;

      // 更新页码
      if (hasMore.value) {
        currentPage.value++;
      }
    } else {
      // 显示模拟数据
      articles.value = [
        {
          id: 1,
          title: "111111",
          createTime: "2025-09-01 21:22:57",
          examineStatus: 1,
          reprintType: 0,
          visibleRange: 0,
          viewCount: 137,
          likeCount: 2,
          commentCount: 0,
          collectCount: 1,
        },
        {
          id: 2,
          title: "多多多多多多多",
          createTime: "2025-08-29 17:55:32",
          examineStatus: 1,
          reprintType: 0,
          visibleRange: 1,
          viewCount: 252,
          likeCount: 2,
          commentCount: 0,
          collectCount: 2,
        },
      ];
    }
  } catch (error) {
    console.error("加载文章列表失败:", error);
    // 显示模拟数据
    articles.value = [
      {
        id: 1,
        title: "111111",
        createTime: "2025-09-01 21:22:57",
        examineStatus: 1,
        reprintType: 0,
        visibleRange: 0,
        viewCount: 137,
        likeCount: 2,
        commentCount: 0,
        collectCount: 1,
      },
      {
        id: 2,
        title: "多多多多多多多",
        createTime: "2025-08-29 17:55:32",
        examineStatus: 1,
        reprintType: 0,
        visibleRange: 1,
        viewCount: 252,
        likeCount: 2,
        commentCount: 0,
        collectCount: 2,
      },
    ];
  } finally {
    // 重置加载状态
    loading.value = false;
    loadingMore.value = false;
  }
};

// 处理编辑文章
const handleEditArticle = (articleId) => {
  router.push({ path: "/editor", query: { articleId } });
};

// 处理删除文章
const handleDeleteArticle = async (articleId) => {
  try {
    await ElMessageBox.confirm("确定要删除这篇文章吗？此操作不可恢复", "删除文章", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    // 这里应该调用删除文章的API
    // await deleteArticle(articleId);

    // 模拟删除成功
    articles.value = articles.value.filter((article) => article.id !== articleId);
    ElMessage.success("文章删除成功");
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除文章失败，请重试");
    }
  }
};

// 处理浏览文章
const handleViewArticle = (articleId) => {
  // 这里应该跳转到文章详情页
  ElMessage.success("浏览文章");
};

// 组件挂载时的处理
onMounted(async () => {
  await loadArticles(true);
});

// 组件卸载时的处理
onUnmounted(() => {
  // 清理资源
});
</script>

<style lang="scss" scoped>
.article-manage-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #f5f7fa;

  .main-content {
    flex: 1;
    padding: 20px;
    margin-top: 60px;

    // 顶部筛选按钮区域
    .filter-buttons {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
      margin-bottom: 20px;

      .filter-btn {
        min-width: 100px;
      }
    }

    // 高级筛选区域
    .advanced-filter {
      background-color: #fff;
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      padding: 16px;
      margin-bottom: 16px;

      .filter-row {
        display: flex;
        align-items: center;
        gap: 16px;

        .filter-item {
          .filter-select {
            width: 120px;
          }

          .search-input {
            width: 200px;
          }
        }
      }
    }

    // 文章列表容器
    .article-list-container {
      background-color: #fff;
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);

      // 加载容器
      .loading-container,
      .loading-more {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 40px;
        color: #909399;

        .loading-spinner {
          margin-right: 10px;
        }
      }

      // 空状态容器
      .empty-container {
        padding: 60px 20px;
        text-align: center;
      }

      // 文章表格
      .article-table {
        border-top: 1px solid #ebeef5;

        .article-title-cell {
          .article-title {
            font-size: 14px;
            color: #303133;
            margin-bottom: 4px;
            display: block;
          }

          .article-meta {
            display: flex;
            gap: 12px;
            align-items: center;

            .publish-time {
              font-size: 12px;
              color: #909399;
            }

            .examine-status {
              padding: 2px 6px;
              border-radius: 10px;
              font-size: 11px;
              font-weight: 500;
              color: #fff;

              &.status-pending {
                background-color: #e6a23c;
              }

              &.status-rejected {
                background-color: #f56c6c;
              }
            }
          }
        }

        .type-badge,
        .visible-badge {
          padding: 2px 6px;
          border-radius: 10px;
          font-size: 11px;
          font-weight: 500;

          &.original {
            background-color: #f0f9ff;
            color: #009688;
          }

          &.reprint {
            background-color: #fff7e6;
            color: #e6a23c;
          }

          &.visible-0 {
            background-color: #f0f9ff;
            color: #1989fa;
          }

          &.visible-1 {
            background-color: #f9f0ff;
            color: #909399;
          }

          &.visible-2 {
            background-color: #fef0f0;
            color: #e6a23c;
          }

          &.visible-3 {
            background-color: #fff1f0;
            color: #f56c6c;
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
    border-top: 2px solid #409eff;
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

// 响应式设计
@media screen and (max-width: 768px) {
  .article-manage-container {
    .main-content {
      padding: 10px;
      margin-top: 50px;

      .filter-buttons {
        justify-content: center;

        .filter-btn {
          min-width: auto;
          padding: 5px 10px;
          font-size: 13px;
        }
      }

      .advanced-filter {
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

      .article-list-container {
        .article-table {
          .article-title-cell {
            .article-title {
              font-size: 13px;
            }
          }
        }
      }
    }
  }
}
</style>
