<template>
  <div class="notification-center">
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h2 class="page-title">消息中心</h2>
      </div>

      <!-- 标签页 -->
      <div class="notification-tabs">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <!-- 系统通知 -->
          <el-tab-pane name="system">
            <template #label>
              <span class="tab-label">
                系统
                <el-badge v-if="unreadCount.system > 0" :value="unreadCount.system" :max="99" class="tab-badge" />
              </span>
            </template>
          </el-tab-pane>

          <!-- 评论通知 -->
          <el-tab-pane name="comment">
            <template #label>
              <span class="tab-label">
                评论
                <el-badge v-if="unreadCount.comment > 0" :value="unreadCount.comment" :max="99" class="tab-badge" />
              </span>
            </template>
          </el-tab-pane>

          <!-- 点赞通知 -->
          <el-tab-pane name="like">
            <template #label>
              <span class="tab-label">
                点赞
                <el-badge v-if="unreadCount.like > 0" :value="unreadCount.like" :max="99" class="tab-badge" />
              </span>
            </template>
          </el-tab-pane>

          <!-- 收藏通知 -->
          <el-tab-pane name="collect">
            <template #label>
              <span class="tab-label">
                收藏
                <el-badge v-if="unreadCount.collect > 0" :value="unreadCount.collect" :max="99" class="tab-badge" />
              </span>
            </template>
          </el-tab-pane>

          <!-- 关注通知 -->
          <el-tab-pane name="follow">
            <template #label>
              <span class="tab-label">
                关注
                <el-badge v-if="unreadCount.follow > 0" :value="unreadCount.follow" :max="99" class="tab-badge" />
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <!-- 通知列表 -->
      <div class="notification-list" v-loading="loading">
        <div v-if="notificationList.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无通知" />
        </div>

        <div v-else class="notification-items">
          <div v-for="notification in notificationList" :key="notification.id" class="notification-item" :class="{ unread: !notification.isRead }">
            <!-- 左侧用户头像 -->
            <div class="notification-avatar" @click.stop="goToUserPage(notification.contentData?.userId)">
              <el-avatar :size="48" :src="notification.contentData?.avatar" class="user-avatar">
                <el-icon v-if="notification.type === 0" class="icon-system"><Bell /></el-icon>
                <el-icon v-else><User /></el-icon>
              </el-avatar>
            </div>

            <!-- 通知内容 -->
            <div class="notification-content" @click="handleNotificationClick(notification)">
              <div class="notification-text">
                <!-- 用户昵称 -->
                <span class="user-nickname" @click.stop="goToUserPage(notification.contentData?.userId)">{{ notification.contentData?.nickname }}</span>
                <!-- 消息内容 -->
                <span class="message-text">{{ getMessageText(notification) }}</span>
                <!-- 文章标题（如果有） -->
                <span v-if="notification.contentData?.articleTitle" class="article-title" @click.stop="goToArticle(notification.contentData?.authorId, notification.contentData?.articleId)"> 《{{ notification.contentData?.articleTitle }}》 </span>
              </div>
              <!-- 评论内容（如果有） -->
              <div v-if="notification.contentData?.commentContent" class="comment-content">
                <span class="comment-text">{{ notification.contentData.commentContent }}</span>
              </div>
              <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
            </div>

            <!-- 右侧操作 -->
            <div class="notification-actions">
              <el-button type="danger" size="small" circle :icon="Delete" @click.stop="handleDelete(notification.id)" title="删除" />
            </div>
          </div>

          <!-- 加载更多提示 -->
          <div v-if="loadingMore" class="loading-more">
            <div class="loading-spinner"></div>
            <span>加载中...</span>
          </div>
          <div v-else-if="!hasMore && notificationList.length > 0" class="no-more">
            <span>没有更多了</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { ChatDotRound, Star, Collection, User, Bell, Delete } from "@element-plus/icons-vue";
import { getUserNotifications, getUnreadNotificationCount, markNotificationsAsRead, deleteNotifications } from "@/api/notification";
import { formatTimeAgo } from "@/utils/timeUtils";

const router = useRouter();

// 响应式数据
const activeTab = ref("system"); // 当前标签页
const loading = ref(false); // 加载状态
const loadingMore = ref(false); // 加载更多状态
const notificationList = ref([]); // 通知列表
const currentPage = ref(1); // 当前页码
const pageSize = ref(20); // 每页数量
const total = ref(0); // 总数量
const hasMore = ref(true); // 是否还有更多

// 未读数量统计
const unreadCount = reactive({
  total: 0,
  system: 0,
  comment: 0,
  like: 0,
  collect: 0,
  follow: 0,
});

// 消息类型映射
const typeMap = {
  system: 0, // 系统消息
  comment: 1, // 评论通知
  like: 2, // 点赞通知
  collect: 3, // 收藏通知
  follow: 4, // 关注通知
};

// 解析通知内容 JSON
const parseNotificationContent = (notification) => {
  try {
    const contentData = JSON.parse(notification.content);
    return {
      ...notification,
      contentData,
    };
  } catch (error) {
    // 如果解析失败，说明是旧数据或纯文本，保留原样
    console.warn("解析通知内容失败:", error);
    return {
      ...notification,
      contentData: {
        title: notification.content,
        nickname: "系统",
      },
    };
  }
};

// 获取通知列表
const fetchNotifications = async (reset = false) => {
  try {
    if (reset) {
      loading.value = true;
      currentPage.value = 1;
      notificationList.value = [];
    } else {
      loadingMore.value = true;
    }

    const type = typeMap[activeTab.value];
    const res = await getUserNotifications(type, currentPage.value, pageSize.value);
    const data = res.data.data;

    // 解析通知内容
    const parsedData = (data.data || []).map((item) => parseNotificationContent(item));

    if (reset) {
      notificationList.value = parsedData;
    } else {
      notificationList.value = [...notificationList.value, ...parsedData];
    }

    total.value = data.total || 0;
    hasMore.value = notificationList.value.length < total.value;

    // 自动标记新加载的未读消息为已读
    autoMarkAsRead(parsedData);
  } catch (error) {
    ElMessage.error("获取通知列表失败");
    console.error("获取通知列表失败:", error);
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

// 自动标记消息为已读
const autoMarkAsRead = async (newNotifications) => {
  if (!newNotifications || newNotifications.length === 0) return;

  // 筛选出未读消息
  const unreadIds = newNotifications.filter((item) => !item.isRead).map((item) => item.id);

  if (unreadIds.length === 0) return;

  try {
    await markNotificationsAsRead(unreadIds);
    // 更新本地数据
    notificationList.value.forEach((item) => {
      if (unreadIds.includes(item.id)) {
        item.isRead = 1;
      }
    });
    // 刷新未读数量（这会更新页面内的徽章）
    await fetchUnreadCount();

    // 触发全局事件，通知 Header 更新未读数量
    window.dispatchEvent(new CustomEvent("notification-read"));
  } catch (error) {
    console.error("自动标记已读失败:", error);
  }
};

// 获取未读数量
const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadNotificationCount();
    const data = res.data.data;
    Object.assign(unreadCount, data);
  } catch (error) {
    console.error("获取未读数量失败:", error);
  }
};

// 标签页切换
const handleTabChange = (tabName) => {
  activeTab.value = tabName;
  fetchNotifications(true);
};

// 处理滚动事件 - 无限滚动（整个窗口滚动）
const handleScroll = () => {
  if (loading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight;

  // 当滚动到距离底部 300px 时加载更多
  if (scrollTop + windowHeight >= documentHeight - 300) {
    currentPage.value++;
    fetchNotifications(false);
  }
};

// 删除通知
const handleDelete = async (notificationId) => {
  try {
    await ElMessageBox.confirm("确定要删除这条通知吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteNotifications([notificationId]);
    // 从列表中移除
    notificationList.value = notificationList.value.filter((item) => item.id !== notificationId);
    total.value--;
    // 刷新未读数量
    fetchUnreadCount();
    ElMessage.success("删除成功");
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除失败");
      console.error("删除通知失败:", error);
    }
  }
};

// 提取消息文本（去掉用户昵称和文章标题）
const getMessageText = (notification) => {
  const contentData = notification.contentData;
  if (!contentData || !contentData.title) {
    return notification.content;
  }

  let text = contentData.title;

  // 去掉用户昵称
  if (contentData.nickname) {
    text = text.replace(contentData.nickname, "").trim();
  }

  // 去掉文章标题
  if (contentData.articleTitle) {
    text = text.replace(`《${contentData.articleTitle}》`, "").trim();
  }

  return text;
};

// 跳转到用户主页
const goToUserPage = (userId) => {
  if (!userId) return;
  router.push(`/user/${userId}`);
};

// 跳转到文章详情页
const goToArticle = (userId, articleId) => {
  if (!userId || !articleId) return;
  router.push(`/user/${userId}/article/${articleId}`);
};

// 点击通知（根据类型跳转）
const handleNotificationClick = (notification) => {
  const contentData = notification.contentData;
  if (!contentData) return;

  // 如果有文章ID，跳转到文章详情（使用文章作者ID）
  if (contentData.articleId && contentData.authorId) {
    goToArticle(contentData.authorId, contentData.articleId);
  }
  // 如果没有文章ID但有用户ID（如关注通知），跳转到用户主页
  else if (contentData.userId) {
    goToUserPage(contentData.userId);
  }
};

// 格式化时间
const formatTime = (time) => {
  return formatTimeAgo(time);
};

// 处理刷新通知列表事件
const handleRefreshNotifications = async () => {
  // 重置到第一页并重新加载
  currentPage.value = 1;
  hasMore.value = true;
  await fetchNotifications(true);
  await fetchUnreadCount();
};

// 组件挂载
onMounted(async () => {
  await fetchNotifications(true);
  fetchUnreadCount();

  // 添加窗口滚动监听
  window.addEventListener("scroll", handleScroll);
  // 添加刷新通知列表事件监听
  window.addEventListener("refresh-notifications", handleRefreshNotifications);
});

// 组件卸载
onUnmounted(() => {
  // 移除窗口滚动监听
  window.removeEventListener("scroll", handleScroll);
  // 移除刷新通知列表事件监听
  window.removeEventListener("refresh-notifications", handleRefreshNotifications);
});
</script>

<style lang="scss" scoped>
// 通知中心容器
.notification-center {
  min-height: 100vh;
  padding-top: 60px !important;

  // 工具类
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 10px;
  }

  // 页面标题
  .page-header {
    margin-bottom: 10px;
    background: var(--el-bg-color-page);
    backdrop-filter: blur(10px);
    border-radius: 8px;
    padding: 24px 30px;
    border: 1px solid var(--el-border-color);
    box-shadow: 0 2px 12px var(--el-border-color-light);

    .page-title {
      font-size: 28px;
      font-weight: 700;
      color: var(--el-text-color-primary);
      margin: 0;
    }
  }

  // 标签页容器
  .notification-tabs {
    background: var(--el-bg-color-page);
    backdrop-filter: blur(10px);
    border-radius: 8px 8px 0 0; // 只保留顶部圆角
    padding: 20px;
    padding-bottom: 0;
    margin-bottom: 0; // 移除底部间距
    border: 1px solid var(--el-border-color);
    border-bottom: none; // 移除底部边框，与列表合并
    box-shadow: none; // 移除阴影，让整体更统一
    position: relative;

    // 标签标题
    .tab-label {
      display: flex;
      align-items: center;
      gap: 8px;

      .tab-badge {
        margin-left: 4px;
      }
    }
  }

  // 通知列表
  .notification-list {
    background: var(--el-bg-color-page);
    backdrop-filter: blur(10px);
    border-radius: 0 0 8px 8px; // 只保留底部圆角
    padding: 20px;
    padding-top: 10px;
    border: 1px solid var(--el-border-color);
    border-top: none; // 移除顶部边框，与标签页无缝连接
    box-shadow: 0 2px 12px var(--el-border-color-light); // 整体阴影
    min-height: 400px;

    // 空状态
    .empty-state {
      padding: 60px 0;
      text-align: center;
    }

    // 通知项目容器
    .notification-items {
      // 通知项目
      .notification-item {
        display: flex;
        align-items: center;
        gap: 16px;
        padding: 16px;
        border-radius: 8px;
        margin-bottom: 12px;
        cursor: pointer;
        transition: all 0.3s ease;
        background: var(--el-bg-color);

        &:hover {
          background: var(--el-fill-color-light);
          transform: translateX(4px);
        }

        &.unread {
          background: var(--el-color-primary-light-9);
          border-left: 4px solid var(--el-color-primary);
        }

        // 通知用户头像
        .notification-avatar {
          flex-shrink: 0;
          cursor: pointer;
          transition: transform 0.3s ease;

          &:hover {
            transform: scale(1.1);
          }

          .user-avatar {
            border: 2px solid var(--el-border-color);
            transition: border-color 0.3s ease;

            &:hover {
              border-color: var(--el-color-primary);
            }

            .icon-system {
              color: var(--el-color-info);
              font-size: 24px;
            }
          }
        }

        // 通知内容
        .notification-content {
          flex: 1;
          min-width: 0;
          cursor: pointer;

          .notification-text {
            font-size: 14px;
            color: var(--el-text-color-primary);
            line-height: 1.8;
            margin-bottom: 4px;
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 4px;

            // 用户昵称
            .user-nickname {
              font-weight: 600;
              color: var(--el-color-primary);
              cursor: pointer;
              transition: color 0.3s ease;

              &:hover {
                color: var(--el-color-primary-light-3);
              }
            }

            // 消息文本
            .message-text {
              color: var(--el-text-color-regular);
            }

            // 文章标题
            .article-title {
              color: var(--el-color-primary);
              font-weight: 500;
              cursor: pointer;
              transition: color 0.3s ease;
              max-width: 300px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;

              &:hover {
                color: var(--el-color-primary-light-3);
              }
            }
          }

          // 评论内容
          .comment-content {
            margin-top: 8px;
            margin-bottom: 8px;
            padding: 8px 12px;
            background: var(--el-fill-color-lighter);
            border-radius: 4px;
            border-left: 3px solid var(--el-color-primary);

            .comment-text {
              font-size: 13px;
              color: var(--el-text-color-regular);
              line-height: 1.6;
              display: -webkit-box;
              -webkit-line-clamp: 3;
              line-clamp: 3;
              -webkit-box-orient: vertical;
              overflow: hidden;
              word-break: break-word;
            }
          }

          .notification-time {
            font-size: 12px;
            color: var(--el-text-color-secondary);
          }
        }

        // 通知操作
        .notification-actions {
          flex-shrink: 0;
          display: flex;
          gap: 8px;
          opacity: 0;
          transition: opacity 0.3s ease;
        }

        &:hover .notification-actions {
          opacity: 1;
        }
      }

      // 加载更多提示
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

      // 没有更多提示
      .no-more {
        text-align: center;
        padding: 20px;
        color: var(--el-text-color-secondary);
        font-size: 14px;
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
  .notification-center {
    background-attachment: scroll;

    .page-header {
      padding: 18px 20px;

      .page-title {
        font-size: 24px;
      }
    }

    .notification-tabs {
      padding: 15px;

      .tab-actions {
        position: static;
        margin-top: 10px;
      }
    }

    .notification-list {
      .notification-items {
        .notification-item {
          gap: 12px;

          .notification-avatar {
            .user-avatar {
              width: 40px;
              height: 40px;
            }
          }

          .notification-content {
            .notification-text {
              font-size: 13px;

              .article-title {
                max-width: 200px;
              }
            }
          }

          .notification-actions {
            opacity: 1;
          }
        }
      }
    }
  }
}
</style>
