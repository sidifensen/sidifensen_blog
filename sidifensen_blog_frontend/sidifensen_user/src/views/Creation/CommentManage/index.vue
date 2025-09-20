<template>
  <div class="comment-manage-container">
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

      <!-- 评论列表区域 -->
      <div class="comment-list-container" ref="listContainer" @scroll="handleScroll">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>

        <div v-else-if="comments.length === 0" class="empty-container">
          <el-empty description="暂无评论数据"></el-empty>
        </div>

        <div v-else class="comment-cards">
          <el-card v-for="comment in comments" :key="comment.id" class="comment-card">
            <div class="comment-card-content">
              <!-- 文章信息区域 -->
              <div class="article-info">
                <el-image :src="comment.articleCoverUrl" alt="文章封面" class="article-cover">
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
                <div class="article-details">
                  <h4 class="article-title" @click="goToArticle(comment.articleId)">{{ comment.articleTitle }}</h4>
                </div>
              </div>

              <!-- 评论内容区域 -->
              <div class="comment-content">
                <!-- 回复信息区域 -->
                <div v-if="comment.replyUserId" class="reply-info">
                  <div class="reply-user">
                    <el-avatar :size="24" :src="comment.replyUserAvatar" class="reply-avatar" />
                    <el-tooltip v-if="comment.replyUserNickname && comment.replyUserNickname.length > 8" :content="comment.replyUserNickname" placement="top">
                      <span class="reply-username">{{ comment.replyUserNickname }}</span>
                    </el-tooltip>
                    <span v-else class="reply-username">{{ comment.replyUserNickname }}</span>
                  </div>
                  <div class="reply-content">回复了您的评论</div>
                </div>

                <!-- 我的评论内容 -->
                <div class="my-comment">
                  <div class="comment-author">
                    <span class="author-name">我</span>
                    <span class="comment-time">{{ formatTime(comment.createTime) }}</span>
                  </div>
                  <el-tooltip v-if="comment.content && comment.content.length > 120" :content="comment.content" placement="top" :show-after="500" :popper-style="{ maxWidth: '400px', wordWrap: 'break-word', whiteSpace: 'normal' }">
                    <div class="comment-text">{{ comment.content }}</div>
                  </el-tooltip>
                  <div v-else class="comment-text">{{ comment.content }}</div>
                </div>

                <!-- 评论统计信息 -->
                <div class="comment-stats">
                  <div class="stat-item">
                    <svg-icon name="like" width="16px" height="16px" color="#909399" />
                    <span>{{ comment.likeCount || 0 }}</span>
                  </div>
                  <div class="stat-item">
                    <el-icon>
                      <ChatLineRound />
                    </el-icon>
                    <span>{{ comment.replyCount || 0 }}</span>
                  </div>
                </div>

                <!-- 评论操作 -->
                <div class="comment-actions">
                  <el-button type="primary" text @click="goToArticle(comment.articleId)">查看详情</el-button>
                  <el-button type="danger" text @click="handleDeleteComment(comment.id)">删除评论</el-button>
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
import { Search, Picture, ChatLineRound } from "@element-plus/icons-vue";
import { getUserCommentManageList, deleteComment } from "@/api/comment";
import { useUserStore } from "@/stores/userStore";
import { formatTime } from "@/utils/formatTime";

const router = useRouter();
const userStore = useUserStore();

// 评论列表数据
const comments = ref([]);
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

// 加载评论列表
const loadComments = async (reset = false) => {
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

    // 年份筛选
    if (selectedYear.value) {
      filterParams.year = selectedYear.value;
    }

    // 月份筛选
    if (selectedMonth.value) {
      filterParams.month = selectedMonth.value;
    }

    // 发送请求获取评论列表
    const res = await getUserCommentManageList(currentPage.value, pageSize.value, filterParams);
    const newComments = res.data.data.data || [];
    const total = res.data.data.total || 0;

    if (reset) {
      // 初次加载或筛选条件改变时
      comments.value = newComments;
      // 从评论数据中提取年份信息并更新筛选选项
      updateDateFiltersFromComments(newComments);
    } else {
      // 无限滚动时加载下一页数据
      comments.value = [...comments.value, ...newComments];
      // 合并新数据后更新筛选选项
      updateDateFiltersFromComments(comments.value);
    }

    // 判断是否还有更多数据
    hasMore.value = comments.value.length < total;

    // 更新页码
    if (hasMore.value && newComments.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    console.error("加载评论列表失败:", error);
    ElMessage.error("加载评论列表失败");
  } finally {
    // 重置加载状态
    loading.value = false;
    loadingMore.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  currentPage.value = 1;
  comments.value = [];
  hasMore.value = true;
  loadComments(true);
};

// 处理日期筛选变化
const handleDateFilterChange = () => {
  currentPage.value = 1;
  comments.value = [];
  hasMore.value = true;
  loadComments(true);
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
    loadComments();
  }
};

// 从评论列表中提取年份选项
const updateDateFiltersFromComments = (commentList) => {
  if (!commentList || commentList.length === 0) {
    availableYears.value = [];
    return;
  }
  // 提取所有评论的年份并去重排序
  const years = [
    ...new Set(
      commentList.map((comment) => {
        const createTime = new Date(comment.createTime);
        return createTime.getFullYear();
      })
    ),
  ].sort((a, b) => b - a);
  availableYears.value = years;
};

// 跳转至文章详情页
const goToArticle = (articleId) => {
  const currentUser = userStore.user;
  if (currentUser && currentUser.id) {
    router.push(`/user/${currentUser.id}/article/${articleId}`);
  } else {
    ElMessage.error("获取用户信息失败，无法跳转");
  }
};

// 删除评论
const handleDeleteComment = async (commentId) => {
  try {
    await ElMessageBox.confirm("确定要删除这条评论吗？此操作不可恢复", "删除评论", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteComment(commentId);
    ElMessage.success("评论删除成功");

    // 重新加载评论列表
    currentPage.value = 1;
    comments.value = [];
    hasMore.value = true;
    loadComments(true);
  } catch (error) {
    if (error !== "cancel") {
      console.error("删除评论失败:", error);
      ElMessage.error("删除评论失败，请重试");
    }
  }
};

// 组件挂载时的处理
onMounted(() => {
  loadComments(true);
});

// 组件卸载时的处理
onUnmounted(() => {
  // 清理资源
  listContainer.value = null;
});
</script>

<style lang="scss" scoped>
.comment-manage-container {
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

    // 评论列表容器
    .comment-list-container {
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

      // 评论卡片列表
      .comment-cards {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
        gap: 16px;
        padding: 16px;

        :deep(.el-card__body) {
          height: 100% !important;
        }

        .comment-card {
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
          }

          .comment-card-content {
            display: flex;
            flex-direction: column;
            height: 100%;

            // 文章信息区域
            .article-info {
              display: flex;
              align-items: center;
              gap: 12px;
              margin-bottom: 16px;
              padding-bottom: 12px;
              border-bottom: 1px solid var(--el-border-color-light);

              .article-cover {
                width: 80px;
                height: 60px;
                border-radius: 4px;
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
                    font-size: 16px;
                    color: var(--el-text-color-placeholder);
                  }
                }
              }

              .article-details {
                flex: 1;
                min-width: 0;

                .article-title {
                  font-size: 15px;
                  font-weight: 500;
                  color: var(--el-text-color-primary);
                  margin: 0;
                  cursor: pointer;
                  transition: color 0.3s ease;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  line-clamp: 2;
                  -webkit-box-orient: vertical;
                  line-height: 1.4;

                  &:hover {
                    color: var(--el-color-primary);
                  }
                }
              }
            }

            // 评论内容区域
            .comment-content {
              flex: 1;
              display: flex;
              flex-direction: column;
              // 回复信息区域
              .reply-info {
                background-color: var(--el-bg-color-page);
                border-radius: 6px;
                padding: 12px;
                margin-bottom: 12px;

                .reply-user {
                  display: flex;
                  align-items: center;
                  gap: 8px;
                  margin-bottom: 6px;

                  .reply-avatar {
                    flex-shrink: 0;
                  }

                  .reply-username {
                    font-size: 14px;
                    font-weight: 500;
                    color: var(--el-text-color-primary);
                    max-width: 120px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                  }
                }

                .reply-content {
                  font-size: 13px;
                  color: var(--el-text-color-regular);
                  margin-left: 32px;
                }
              }

              // 我的评论内容
              .my-comment {
                flex: 1;
                .comment-author {
                  display: flex;
                  align-items: center;
                  gap: 12px;
                  margin-bottom: 8px;

                  .author-name {
                    font-size: 14px;
                    font-weight: 500;
                    color: var(--el-color-primary);
                  }

                  .comment-time {
                    font-size: 13px;
                    color: var(--el-text-color-secondary);
                  }
                }

                .comment-text {
                  font-size: 14px;
                  color: var(--el-text-color-primary);
                  line-height: 1.6;
                  margin-bottom: 12px;
                  overflow: hidden;
                  display: -webkit-box;
                  -webkit-line-clamp: 4;
                  line-clamp: 4;
                  -webkit-box-orient: vertical;
                  max-height: 89px;
                  cursor: pointer;
                }
              }

              // 评论统计信息
              .comment-stats {
                display: flex;
                gap: 16px;
                padding: 8px 0;
                border-top: 1px solid var(--el-border-color-lighter);
                border-bottom: 1px solid var(--el-border-color-lighter);

                .stat-item {
                  display: flex;
                  align-items: center;
                  gap: 4px;
                  color: var(--el-text-color-secondary);
                  font-size: 13px;

                  .el-icon {
                    font-size: 16px;
                  }
                }
              }

              // 评论操作
              .comment-actions {
                display: flex;
                justify-content: flex-end;
                gap: 8px;
                padding-top: 8px;
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
  .comment-manage-container {
    .main-content {
      .comment-list-container {
        .comment-cards {
          grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        }
      }
    }
  }
}

// 响应式设计 - 移动端
@media screen and (max-width: 768px) {
  .comment-manage-container {
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

      .comment-list-container {
        max-height: calc(100vh - 60px);

        .comment-cards {
          grid-template-columns: 1fr;
          padding: 10px;
          gap: 12px;

          .comment-card {
            height: 330px;

            .comment-card-content {
              .article-info {
                .article-cover {
                  width: 70px;
                  height: 53px;
                }

                .article-details {
                  .article-title {
                    font-size: 14px;
                    -webkit-line-clamp: 1;
                    line-clamp: 1;
                  }
                }
              }

              .comment-content {
                .reply-info {
                  padding: 8px;

                  .reply-user {
                    .reply-username {
                      font-size: 13px;
                    }
                  }

                  .reply-content {
                    font-size: 12px;
                    margin-left: 28px;
                  }
                }

                .my-comment {
                  .comment-author {
                    .author-name {
                      font-size: 13px;
                    }

                    .comment-time {
                      font-size: 12px;
                    }
                  }

                  .comment-text {
                    font-size: 13px;
                    -webkit-line-clamp: 3;
                    line-clamp: 3;
                    max-height: 60px;
                  }
                }

                .comment-stats {
                  .stat-item {
                    font-size: 12px;

                    .el-icon {
                      font-size: 14px;
                    }
                  }
                }

                .comment-actions {
                  gap: 4px;
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
