<template>
  <div class="history-list-section">
    <!-- 操作栏 -->
    <div class="history-controls">
      <div class="control-left">
        <span class="history-count">共 {{ total }} 条浏览记录</span>
      </div>
      <div class="control-right">
        <el-button type="danger" size="small" :icon="Delete" :loading="clearLoading" :disabled="historyList.length === 0" @click="handleClearHistory"> 清空历史 </el-button>
      </div>
    </div>

    <!-- 历史记录列表 -->
    <div class="history-content" ref="historyContainer">
      <!-- 加载状态 -->
      <div v-if="historyLoading" class="loading-container">
        <el-skeleton animated :count="5">
          <template #template>
            <div class="history-skeleton">
              <el-skeleton-item variant="image" style="width: 120px; height: 90px" />
              <div class="skeleton-content">
                <el-skeleton-item variant="h3" style="width: 80%; margin-bottom: 8px" />
                <el-skeleton-item variant="text" style="width: 60%; margin-bottom: 6px" />
                <el-skeleton-item variant="text" style="width: 40%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>

      <!-- 空状态 -->
      <div v-else-if="historyList.length === 0" class="empty-state">
        <el-empty description="暂无浏览记录" />
      </div>

      <!-- 历史记录列表 -->
      <div v-else class="history-list">
        <div v-for="history in historyList" :key="history.id" class="history-item" @click="goToArticle(history.articleId)">
          <!-- 文章封面 -->
          <el-image :src="history.coverUrl" class="history-cover">
            <template #placeholder>
              <div class="loading-text">加载中...</div>
            </template>
            <template #error>
              <div class="error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>

          <!-- 文章内容 -->
          <div class="history-content-info">
            <h3 class="history-title">{{ history.title }}</h3>

            <!-- 作者信息 -->
            <div class="author-info">
              <el-avatar :size="24" :src="history.authorAvatar" class="author-avatar" />
              <span class="author-name">{{ history.authorNickname }}</span>
            </div>

            <!-- 浏览时间 -->
            <div class="view-time">
              <el-icon class="time-icon"><Clock /></el-icon>
              <span>{{ formatViewTime(history.viewTime) }}</span>
            </div>
          </div>
        </div>

        <!-- 加载更多指示器 -->
        <div v-if="loadingMore" class="loading-more">
          <div class="loading-spinner"></div>
          <span>加载更多...</span>
        </div>

        <!-- 没有更多数据提示 -->
        <div v-if="!hasMore && historyList.length > 0" class="no-more">
          <span>没有更多记录了</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { Delete, Picture, Clock } from "@element-plus/icons-vue";
import { getUserHistoryList, clearUserHistory } from "@/api/history";
import { formatDate } from "@/utils/formatTime";

// 路由
const router = useRouter();

// 响应式数据
const historyLoading = ref(false); // 历史记录加载状态
const loadingMore = ref(false); // 加载更多状态
const clearLoading = ref(false); // 清空历史加载状态
const historyList = ref([]); // 历史记录列表
const total = ref(0); // 总记录数
const currentPage = ref(1); // 当前页码
const pageSize = ref(10); // 每页数据量
const hasMore = ref(true); // 是否还有更多数据

// 容器引用
const historyContainer = ref(null);

// 获取历史记录列表
const fetchHistoryList = async (reset = false) => {
  // 如果没有更多数据或者已经在加载中，则不再请求
  if (!hasMore.value || historyLoading.value || loadingMore.value) {
    return;
  }

  try {
    // 设置加载状态
    if (reset) {
      historyLoading.value = true;
    } else {
      loadingMore.value = true;
    }

    const res = await getUserHistoryList(currentPage.value, pageSize.value);
    const newHistoryList = res.data.data.data || [];
    total.value = res.data.data.total || 0;

    if (reset) {
      // 初次加载或重置时
      historyList.value = newHistoryList;
    } else {
      // 无限滚动时加载下一页数据
      historyList.value = [...historyList.value, ...newHistoryList];
    }

    // 判断是否还有更多数据
    hasMore.value = historyList.value.length < total.value;

    // 更新页码
    if (hasMore.value && newHistoryList.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取浏览历史失败");
    console.error("获取浏览历史失败:", error);
  } finally {
    // 重置加载状态
    historyLoading.value = false;
    loadingMore.value = false;
  }
};

// 处理清空历史记录
const handleClearHistory = async () => {
  try {
    await ElMessageBox.confirm("确定要清空所有浏览记录吗？此操作不可恢复。", "确认清空", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    clearLoading.value = true;
    const res = await clearUserHistory();
    const clearedCount = res.data.data;

    // 清空本地数据
    historyList.value = [];
    total.value = 0;
    currentPage.value = 1;
    hasMore.value = true;

    ElMessage.success(`成功清空 ${clearedCount} 条浏览记录`);
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("清空历史记录失败");
      console.error("清空历史记录失败:", error);
    }
  } finally {
    clearLoading.value = false;
  }
};

// 处理滚动事件 - 无限滚动（移到父组件处理）
// 这个函数现在不再使用，滚动监听由父组件统一处理

// 格式化浏览时间
const formatViewTime = (viewTime) => {
  if (!viewTime) return "";

  const now = new Date();
  const view = new Date(viewTime);
  const diffTime = now - view;
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24));
  const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
  const diffMinutes = Math.floor(diffTime / (1000 * 60));

  if (diffDays > 0) {
    if (diffDays === 1) {
      return "昨天浏览";
    } else if (diffDays < 7) {
      return `${diffDays}天前浏览`;
    } else {
      return formatDate(viewTime, "MM-DD");
    }
  } else if (diffHours > 0) {
    return `${diffHours}小时前浏览`;
  } else if (diffMinutes > 0) {
    return `${diffMinutes}分钟前浏览`;
  } else {
    return "刚刚浏览";
  }
};

// 跳转到文章详情页
const goToArticle = (articleId) => {
  router.push(`/article/${articleId}`);
};

// 组件挂载时获取数据
onMounted(() => {
  fetchHistoryList(true);
});

// 暴露给父组件的方法
defineExpose({
  refresh: () => {
    currentPage.value = 1;
    hasMore.value = true;
    fetchHistoryList(true);
  },
  loadMore: () => {
    if (!historyLoading.value && !loadingMore.value && hasMore.value) {
      fetchHistoryList();
    }
  },
});
</script>

<style lang="scss" scoped>
// 历史记录列表区域
.history-list-section {
  background: var(--el-bg-color-page);
  border-radius: 8px;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 12px var(--el-border-color-light);
  overflow: hidden;

  // 操作栏
  .history-controls {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid var(--el-border-color-light);
    background-color: var(--el-bg-color);

    .control-left {
      .history-count {
        font-size: 14px;
        color: var(--el-text-color-regular);
      }
    }

    .control-right {
      .el-button {
        font-size: 13px;
      }
    }
  }

  // 历史记录内容区域
  .history-content {
    padding: 0;

    // 加载容器样式
    .loading-container {
      padding: 20px;
    }

    // 骨架屏样式
    .history-skeleton {
      display: flex;
      gap: 16px;
      padding: 20px 0;
      border-bottom: 1px solid var(--el-border-color-light);

      .skeleton-content {
        flex: 1;
        display: flex;
        flex-direction: column;
      }
    }

    // 空状态
    .empty-state {
      padding: 60px 20px;
      text-align: center;
    }

    // 历史记录列表
    .history-list {
      padding: 0;

      .history-item {
        display: flex;
        gap: 16px;
        padding: 20px;
        border-bottom: 1px solid var(--el-border-color-light);
        cursor: pointer;
        transition: all 0.3s ease;

        &:last-child {
          border-bottom: none;
        }

        &:hover {
          background-color: var(--el-bg-color);
          transform: translateX(4px);
        }

        // 历史记录封面
        .history-cover {
          width: 120px;
          height: 90px;
          border-radius: 6px;
          flex-shrink: 0;
          transition: transform 0.3s ease;

          &:hover {
            transform: scale(1.05);
          }

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

        // 历史记录内容信息
        .history-content-info {
          flex: 1;
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          min-width: 0;

          .history-title {
            font-size: 16px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            margin: 0 0 12px 0;
            line-height: 1.4;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
          }

          // 作者信息
          .author-info {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;

            .author-avatar {
              flex-shrink: 0;
            }

            .author-name {
              font-size: 13px;
              color: var(--el-text-color-regular);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          // 浏览时间
          .view-time {
            display: flex;
            align-items: center;
            gap: 4px;
            font-size: 12px;
            color: var(--el-text-color-secondary);

            .time-icon {
              font-size: 12px;
            }
          }
        }
      }

      // 加载更多指示器
      .loading-more {
        display: flex;
        align-items: center;
        justify-content: center;
        padding: 30px;
        color: var(--el-text-color-regular);

        .loading-spinner {
          width: 20px;
          height: 20px;
          border: 2px solid #f3f3f3;
          border-top: 2px solid #409eff;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin-right: 10px;
        }
      }

      // 没有更多数据提示
      .no-more {
        display: flex;
        justify-content: center;
        padding: 20px;
        color: var(--el-text-color-placeholder);
        font-size: 13px;
      }
    }
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

// 响应式设计
@media (max-width: 768px) {
  .history-list-section {
    .history-controls {
      padding: 12px 15px;
      flex-direction: row; // 移动端也保持水平布局
      justify-content: space-between; // 两端对齐
      align-items: center; // 垂直居中
      gap: 10px;

      .control-left {
        flex: 1; // 左侧占据剩余空间
        min-width: 0; // 允许文字收缩

        .history-count {
          font-size: 13px; // 移动端稍微减小字体
        }
      }

      .control-right {
        flex-shrink: 0; // 右侧按钮不收缩
      }
    }

    .history-content {
      .history-list {
        .history-item {
          padding: 15px;
          flex-direction: column;
          gap: 12px;

          .history-cover {
            width: 100%;
            height: 180px;
          }

          .history-content-info {
            .history-title {
              font-size: 15px;
            }
          }
        }
      }
    }
  }
}
</style>
