<template>
  <div class="article-manage-container">
    <div class="main-content">
      <!-- 顶部筛选按钮区域 -->
      <div class="filter-buttons">
        <el-button :type="activeFilterType === 'all' ? 'primary' : 'default'" @click="handleFilterClick('all')" class="filter-btn"> 全部({{ totalCount }}) </el-button>
        <el-button :type="activeFilterType === 'published' ? 'primary' : 'default'" @click="handleFilterClick('published')" class="filter-btn"> 已发布({{ publishedCount }}) </el-button>
        <el-button :type="activeFilterType === 'reviewing' ? 'primary' : 'default'" @click="handleFilterClick('reviewing')" class="filter-btn"> 审核中({{ reviewingCount }}) </el-button>
        <el-button :type="activeFilterType === 'draft' ? 'primary' : 'default'" @click="handleFilterClick('draft')" class="filter-btn"> 草稿箱({{ draftCount }}) </el-button>
        <el-button :type="activeFilterType === 'garbage' ? 'primary' : 'default'" @click="handleFilterClick('garbage')" class="filter-btn"> 回收站({{ garbageCount }}) </el-button>
      </div>

      <!-- 高级筛选区域 -->
      <div class="advanced-filter">
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
            <el-select v-model="filterParams.reprintType" placeholder="文章类型" @change="handleFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">文章类型:</span>
              </template>
              <el-option label="不限" :value="-1"></el-option>
              <el-option label="原创" :value="0"></el-option>
              <el-option label="转载" :value="1"></el-option>
            </el-select>
          </div>

          <div class="filter-item">
            <el-select v-model="filterParams.visibleRange" placeholder="可见范围" @change="handleFilterChange" class="filter-select">
              <template #prefix>
                <span class="select-prefix">可见范围:</span>
              </template>
              <el-option label="不限" :value="-1"></el-option>
              <el-option label="全部可见" :value="0"></el-option>
              <el-option label="仅我可见" :value="1"></el-option>
              <el-option label="粉丝可见" :value="2"></el-option>
              <el-option label="VIP可见" :value="3"></el-option>
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

      <!-- 文章列表区域 -->
      <div class="article-list-container" ref="listContainer" @scroll="handleScroll">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="articles.length === 0" class="empty-container">
          <el-empty description="暂无文章数据"></el-empty>
        </div>

        <div v-else class="article-cards">
          <el-card v-for="article in articles" :key="article.id" class="article-card">
            <div class="article-card-content">
              <el-image :src="article.coverUrl" alt="文章封面" class="article-cover">
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
              <div class="article-info">
                <div class="article-header">
                  <span class="article-title">{{ article.title }}</span>
                  <div class="article-badges">
                    <span v-if="article.examineStatus !== 1" class="examine-status" :class="getExamineStatusClass(article.examineStatus)">
                      {{ getExamineStatusText(article.examineStatus) }}
                    </span>
                    <span class="type-badge" :class="article.reprintType === 0 ? 'original' : 'reprint'">
                      {{ article.reprintType === 0 ? "原创" : "转载" }}
                    </span>
                    <span class="visible-badge" :class="`visible-${article.visibleRange}`">
                      {{ getVisibleRangeText(article.visibleRange) }}
                    </span>
                  </div>
                </div>
                <div class="article-meta">
                  <span class="publish-time">{{ article.createTime }}</span>
                </div>
                <div class="article-stats">
                  <div class="stat-item">
                    <el-icon>
                      <View />
                    </el-icon>
                    <span>{{ article.readCount || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon>
                      <Pointer />
                    </el-icon>
                    <span>{{ article.likeCount || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon>
                      <Message />
                    </el-icon>
                    <span>{{ article.commentCount || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon>
                      <Collection />
                    </el-icon>
                    <span>{{ article.collectCount || 0 }}</span>
                  </div>
                </div>
                <div class="article-actions">
                  <template v-if="article.editStatus !== 2">
                    <el-button type="primary" text @click="handleEditArticle(article.id)">编辑</el-button>
                    <el-button type="primary" text @click="handleViewArticle(article.id)">浏览</el-button>
                    <el-button type="danger" text @click="handleDeleteToDraftArticle(article.id)">删除</el-button>
                  </template>
                  <template v-else>
                    <el-button type="primary" text @click="handleRecyleToDraftArticle(article.id)">回收至草稿箱</el-button>
                    <el-button type="danger" text @click="handleDeleteArticle(article.id)">彻底删除</el-button>
                  </template>
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
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import { deleteArticle, getUserArticleList, getUserArticleStatistics, updateArticle } from "@/api/article";
import { Search, View, Message, Pointer, Edit, Delete, Collection } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const userStore = useUserStore();

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
// 列表容器引用
const listContainer = ref(null);

// 年份月份筛选
const selectedYear = ref(null);
const selectedMonth = ref(null);
const availableYears = ref([]);
// 月份固定为1-12

// 筛选参数
const filterParams = ref({
  editStatus: -1, //  -1:全部, 0:已发布, 1:草稿箱, 2:回收站
  examineStatus: -1, // -1:全部, 0:审核中, 1:已通过, 2:已驳回
  reprintType: -1, // -1:全部, 0:原创, 1:转载
  visibleRange: -1, // -1:全部, 0:全部可见, 1:仅我可见, 2:粉丝可见, 3:VIP可见
});

// 当前激活的筛选类型
const activeFilterType = ref("all");

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

// 统计数据
const totalCount = ref(0);
const publishedCount = ref(0);
const reviewingCount = ref(0);
const draftCount = ref(0);
const garbageCount = ref(0);

// 处理筛选按钮点击
const handleFilterClick = (filterType) => {
  activeFilterType.value = filterType;

  // 重置筛选参数，保持默认值为-1
  filterParams.value = {};

  // 清空搜索关键词
  searchKeyword.value = "";

  // 重置日期筛选
  selectedYear.value = null;
  selectedMonth.value = null;

  // 根据筛选类型设置参数
  switch (filterType) {
    case "all":
      // 全部，保持默认参数
      break;
    case "published":
      filterParams.value.editStatus = 0; //已发布
      filterParams.value.examineStatus = 1; //审核通过
      break;
    case "draft":
      filterParams.value.editStatus = 1; //草稿箱
      break;
    case "garbage":
      filterParams.value.editStatus = 2; //回收站
      break;
    case "reviewing":
      filterParams.value.editStatus = 0; //已发布
      filterParams.value.examineStatus = 0; //审核中
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

// 处理日期筛选变化
const handleDateFilterChange = () => {
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
    // 构建请求参数 - 只传递非-1的参数，这样后端会返回全部数据
    const params = {};

    // 只有当值不为-1时才传递该参数
    if (filterParams.value.editStatus !== -1) {
      params.editStatus = filterParams.value.editStatus;
    }
    if (filterParams.value.examineStatus !== -1) {
      params.examineStatus = filterParams.value.examineStatus;
    }
    if (filterParams.value.reprintType !== -1) {
      params.reprintType = filterParams.value.reprintType;
    }
    if (filterParams.value.visibleRange !== -1) {
      params.visibleRange = filterParams.value.visibleRange;
    }

    // 搜索关键词不为空时才传递
    if (searchKeyword.value) {
      params.keyword = searchKeyword.value;
    }

    // 日期筛选参数
    if (selectedYear.value) {
      params.year = selectedYear.value;
      if (selectedMonth.value) {
        params.month = selectedMonth.value;
      }
    }
    // 发送请求获取文章列表
    const response = await getUserArticleList(currentPage.value, pageSize.value, params);
    const result = response.data || {};
    const newArticles = result.data ? result.data.data || [] : [];
    const total = result.data ? result.data.total || 0 : 0;
    // console.log("加载数据:", { currentPage: currentPage.value, newArticlesCount: newArticles.length, total });

    if (reset) {
      // 初次加载或筛选条件改变时
      articles.value = newArticles;
      // 从文章数据中提取年月信息并更新筛选选项
      updateDateFiltersFromArticles(newArticles);
    } else {
      // 无限滚动时加载下一页数据
      articles.value = [...articles.value, ...newArticles];
      // 合并新数据后更新筛选选项
      updateDateFiltersFromArticles(articles.value);
    }

    // 判断是否还有更多数据
    hasMore.value = articles.value.length < total;

    // 更新页码
    if (hasMore.value && newArticles.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    console.error("加载文章列表失败:", error);
    ElMessage.error("加载文章列表失败");
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

// 删除到回收站
const handleDeleteToDraftArticle = async (articleId) => {
  const ArticleDto = {
    id: articleId,
    editStatus: 1,
  };
  updateArticle(ArticleDto)
    .then((res) => {
      ElMessage.success("文章删除成功");
      // 重新加载文章列表和统计数据
      loadArticles(true);
      loadStatistics();
    })
    .catch((err) => {
      ElMessage.error("文章删除失败");
    });
};

// 回收至回收站
const handleRecyleToDraftArticle = async (articleId) => {
  const ArticleDto = {
    id: articleId,
    editStatus: 1,
  };
  updateArticle(ArticleDto)
    .then((res) => {
      ElMessage.success("文章回收成功");
      // 重新加载文章列表和统计数据
      loadArticles(true);
      loadStatistics();
    })
    .catch((err) => {
      ElMessage.error("文章回收失败");
    });
};

// 处理删除文章
const handleDeleteArticle = async (articleId) => {
  try {
    await ElMessageBox.confirm("确定要删除这篇文章吗？此操作不可恢复", "删除文章", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    await deleteArticle(articleId)
      .then(() => {
        ElMessage.success("文章删除成功");
        // 重新加载文章列表和统计数据
        loadArticles(true);
        loadStatistics();
      })
      .catch((err) => {
        ElMessage.error("文章删除失败");
      });
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除文章失败，请重试");
    }
  }
};

// 处理浏览文章
const handleViewArticle = (articleId) => {
  // 获取当前用户ID，跳转到文章详情页
  const currentUser = userStore.user;
  if (currentUser && currentUser.id) {
    router.push(`/user/${currentUser.id}/article/${articleId}`);
  } else {
    ElMessage.error("获取用户信息失败，无法跳转");
  }
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
    loadArticles();
  }
};

// 从文章列表中提取年份选项
const updateDateFiltersFromArticles = (articleList) => {
  if (!articleList || articleList.length === 0) {
    availableYears.value = [];
    return;
  }
  // 提取所有文章的年份并去重排序
  const years = [
    ...new Set(
      articleList.map((article) => {
        const createTime = new Date(article.createTime);
        return createTime.getFullYear();
      })
    ),
  ].sort((a, b) => b - a);
  availableYears.value = years;
};

// 加载文章状态统计数据
const loadStatistics = async () => {
  try {
    const res = await getUserArticleStatistics();
    const statistics = res.data.data || {};

    // 更新统计数据
    totalCount.value = statistics.totalCount || 0;
    publishedCount.value = statistics.publishedCount || 0;
    reviewingCount.value = statistics.reviewingCount || 0;
    draftCount.value = statistics.draftCount || 0;
    garbageCount.value = statistics.garbageCount || 0;
  } catch (error) {
    console.error("加载统计数据失败:", error);
    // 如果统计接口失败，使用默认值
    totalCount.value = 0;
    publishedCount.value = 0;
    reviewingCount.value = 0;
    draftCount.value = 0;
    garbageCount.value = 0;
  }
};

// 组件挂载时的处理
onMounted(async () => {
  // 同时加载文章列表和统计数据
  await Promise.all([loadArticles(true), loadStatistics()]);
});

// 组件卸载时的处理
onUnmounted(() => {
  // 清理资源
  listContainer.value = null;
});
</script>

<style lang="scss" scoped>
.article-manage-container {
  display: flex;
  flex-direction: column;
  height: calc(100vh - 48px);

  .main-content {
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
      background-color: var(--el-bg-color);
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
            width: 200px;
          }

          .search-input {
            width: 200px;
          }

          .select-prefix {
            margin-right: 5px;
          }
        }
      }
    }

    // 文章列表容器
    .article-list-container {
      background-color: var(--el-bg-color);
      border-radius: 4px;
      box-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
      flex: 1;
      overflow-y: auto;
      position: relative;
      max-height: calc(100vh - 240px);

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

      // 文章卡片列表
      .article-cards {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        gap: 16px;
        padding: 16px;

        .article-card {
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .article-card-content {
            display: flex;
            flex-direction: column;

            .article-cover {
              width: 100%;
              height: 160px;
              object-fit: cover;
              border-radius: 4px;
              margin-bottom: 12px;

              .loading-text {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                font-size: 16px;
                color: #606266;
                background-color: #f5f5f5;
              }

              // 错误占位图标样式
              .error {
                display: flex;
                justify-content: center;
                align-items: center;
                width: 100%;
                height: 100%;
                background-color: #f5f5f5;

                .el-icon {
                  font-size: 40px;
                  color: #909399;
                }
              }
            }

            .article-info {
              flex: 1;
              display: flex;
              flex-direction: column;
              gap: 8px;

              .article-header {
                .article-title {
                  font-size: 16px;
                  font-weight: 500;
                  color: var(--el-text-color-regular);
                  margin-bottom: 8px;
                  display: block;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                }

                .article-badges {
                  display: flex;
                  flex-wrap: wrap;
                  gap: 6px;
                  margin-bottom: 8px;
                }
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

              .article-meta {
                display: flex;
                gap: 12px;
                align-items: center;

                .publish-time {
                  font-size: 12px;
                  color: #909399;
                }
              }

              .article-stats {
                display: flex;
                justify-content: space-between;
                padding: 8px 0;
                border-top: 1px solid #f0f0f0;
                border-bottom: 1px solid #f0f0f0;

                .stat-item {
                  display: flex;
                  align-items: center;
                  gap: 4px;
                  color: #909399;
                  font-size: 13px;

                  .el-icon {
                    font-size: 16px;
                  }
                }
              }

              .article-actions {
                display: flex;
                justify-content: flex-end;
                gap: 8px;
                margin-top: 8px;
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
