<template>
  <div class="notification-center">
    <div class="container">
      <div class="page-header">
        <div class="header-copy">
          <p class="header-copy__eyebrow">Notification Center</p>
          <h2 class="header-copy__title">消息中心</h2>
          <p class="header-copy__description">把系统提醒、评论互动和关注动态集中收进一个更清晰的收件箱。</p>
        </div>
        <div class="header-actions">
          <el-button plain :icon="RefreshRight" @click="handleRefreshNotifications">刷新列表</el-button>
          <el-button plain :icon="Select" :disabled="!hasUnreadInView" @click="handleMarkCurrentAsRead">全部已读</el-button>
        </div>
      </div>

      <div class="summary-grid">
        <article v-for="card in summaryCards" :key="card.key" class="summary-card">
          <div class="summary-card__label">{{ card.label }}</div>
          <div class="summary-card__value">{{ card.value }}</div>
          <div class="summary-card__hint">{{ card.hint }}</div>
        </article>
      </div>

      <div class="notification-tabs">
        <el-tabs v-model="activeTab" @tab-change="handleTabChange">
          <el-tab-pane v-for="tab in tabOptions" :key="tab.name" :name="tab.name">
            <template #label>
              <span class="tab-label">
                <span>{{ tab.label }}</span>
                <el-badge v-if="unreadCount[tab.name] > 0" :value="unreadCount[tab.name]" :max="99" class="tab-label__badge" />
              </span>
            </template>
          </el-tab-pane>
        </el-tabs>
      </div>

      <div class="notification-list" v-loading="loading">
        <div class="list-toolbar">
          <span class="list-toolbar__meta">{{ currentTabLabel }}通知</span>
          <span class="list-toolbar__meta">{{ total }} 条</span>
        </div>

        <div v-if="notificationList.length === 0 && !loading" class="empty-state">
          <el-empty :description="`${currentTabLabel}通知暂时为空`" />
        </div>

        <div v-else class="notification-items">
          <article v-for="notification in notificationList" :key="notification.id" class="notification-item" :class="{ unread: isUnread(notification) }">
            <div class="notification-item__avatar" @click.stop="goToUserPage(notification.contentData?.userId)">
              <el-avatar :size="46" :src="notification.contentData?.avatar" class="user-avatar">
                <el-icon v-if="notification.type === 0"><Bell /></el-icon>
                <el-icon v-else><User /></el-icon>
              </el-avatar>
            </div>

            <div class="notification-item__content" @click="handleNotificationClick(notification)">
              <div class="notification-item__top">
                <span class="type-pill">{{ getTypeLabel(notification.type) }}</span>
                <span class="notification-time">{{ formatTime(notification.createTime) }}</span>
              </div>
              <div class="notification-item__text">
                <span class="user-nickname" @click.stop="goToUserPage(notification.contentData?.userId)">
                  {{ notification.contentData?.nickname || "系统" }}
                </span>
                <span class="message-text">{{ getMessageText(notification) }}</span>
                <span
                  v-if="notification.contentData?.articleTitle"
                  class="article-title"
                  @click.stop="goToArticle(notification.contentData?.authorId, notification.contentData?.articleId)"
                >
                  《{{ notification.contentData?.articleTitle }}》
                </span>
              </div>
              <div v-if="notification.contentData?.commentContent" class="comment-content">
                <span class="comment-content__text">{{ notification.contentData.commentContent }}</span>
              </div>
            </div>

            <div class="notification-item__actions">
              <el-button
                v-if="isUnread(notification)"
                plain
                size="small"
                :icon="Select"
                class="notification-action"
                @click.stop="handleSingleRead(notification)"
              >
                已读
              </el-button>
              <el-button plain size="small" :icon="Delete" class="notification-action" @click.stop="handleDelete(notification.id)">删除</el-button>
            </div>
          </article>

          <div v-if="loadingMore" class="loading-more">
            <div class="loading-more__spinner"></div>
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
import { computed, onMounted, onUnmounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { Bell, Delete, RefreshRight, Select, User } from "@element-plus/icons-vue";
import { deleteNotifications, getUnreadNotificationCount, getUserNotifications, markNotificationsAsRead } from "@/api/notification";
import { formatTimeAgo } from "@/utils/timeUtils";

const router = useRouter();

const activeTab = ref("system");
const loading = ref(false);
const loadingMore = ref(false);
const notificationList = ref([]);
const currentPage = ref(1);
const pageSize = ref(20);
const total = ref(0);
const hasMore = ref(true);

const unreadCount = reactive({
  total: 0,
  system: 0,
  comment: 0,
  like: 0,
  collect: 0,
  follow: 0,
});

const tabOptions = [
  { name: "system", label: "系统", type: 0 },
  { name: "comment", label: "评论", type: 1 },
  { name: "like", label: "点赞", type: 2 },
  { name: "collect", label: "收藏", type: 3 },
  { name: "follow", label: "关注", type: 4 },
];

const typeMap = {
  system: 0,
  comment: 1,
  like: 2,
  collect: 3,
  follow: 4,
};

const currentTabLabel = computed(() => tabOptions.find((item) => item.name === activeTab.value)?.label || "系统");
const hasUnreadInView = computed(() => notificationList.value.some((item) => isUnread(item)));
const summaryCards = computed(() => [
  { key: "total", label: "全部未读", value: unreadCount.total || 0, hint: "跨分类汇总" },
  { key: "current", label: `${currentTabLabel.value}未读`, value: unreadCount[activeTab.value] || 0, hint: "当前筛选" },
  { key: "loaded", label: "当前列表", value: total.value || 0, hint: "已同步通知" },
]);

const isUnread = (notification) => Number(notification.isRead) !== 1;

const getTypeLabel = (type) => tabOptions.find((item) => item.type === type)?.label || "系统";

const parseNotificationContent = (notification) => {
  try {
    const parsedContent = JSON.parse(notification.content);
    return {
      ...notification,
      contentData: {
        ...parsedContent,
        title: parsedContent.title || parsedContent.text || notification.content,
      },
    };
  } catch (error) {
    return {
      ...notification,
      contentData: {
        title: notification.content,
        nickname: "系统",
      },
    };
  }
};

const fetchNotifications = async (reset = false) => {
  try {
    if (reset) {
      loading.value = true;
      currentPage.value = 1;
      notificationList.value = [];
      hasMore.value = true;
    } else {
      loadingMore.value = true;
    }

    const res = await getUserNotifications(typeMap[activeTab.value], currentPage.value, pageSize.value);
    const data = res.data || {};
    const parsedData = (data.data || []).map((item) => parseNotificationContent(item));

    if (reset) {
      notificationList.value = parsedData;
    } else {
      notificationList.value = [...notificationList.value, ...parsedData];
    }

    total.value = data.total || 0;
    hasMore.value = notificationList.value.length < total.value;
    autoMarkAsRead(parsedData);
  } catch (error) {
    // 静默处理
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

const autoMarkAsRead = async (newNotifications) => {
  const unreadIds = (newNotifications || []).filter((item) => isUnread(item)).map((item) => item.id);
  if (!unreadIds.length) {
    return;
  }

  try {
    await markNotificationsAsRead(unreadIds);
    notificationList.value.forEach((item) => {
      if (unreadIds.includes(item.id)) {
        item.isRead = 1;
      }
    });
    await fetchUnreadCount();
    window.dispatchEvent(new CustomEvent("notification-read"));
  } catch (error) {
    // 静默处理
  }
};

const fetchUnreadCount = async () => {
  try {
    const res = await getUnreadNotificationCount();
    const data = res.data || {};
    Object.assign(unreadCount, {
      total: data.total || 0,
      system: data.system || 0,
      comment: data.comment || 0,
      like: data.like || 0,
      collect: data.collect || 0,
      follow: data.follow || 0,
    });
  } catch (error) {
    // 静默处理
  }
};

const handleTabChange = (tabName) => {
  activeTab.value = tabName;
  fetchNotifications(true);
};

const handleScroll = () => {
  if (loading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  const scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop;
  const windowHeight = window.innerHeight;
  const documentHeight = document.documentElement.scrollHeight;

  if (scrollTop + windowHeight >= documentHeight - 300) {
    currentPage.value += 1;
    fetchNotifications(false);
  }
};

const handleMarkCurrentAsRead = async () => {
  const unreadIds = notificationList.value.filter((item) => isUnread(item)).map((item) => item.id);
  if (!unreadIds.length) {
    ElMessage.info("当前分类没有未读通知");
    return;
  }

  try {
    await markNotificationsAsRead(unreadIds);
    notificationList.value.forEach((item) => {
      if (unreadIds.includes(item.id)) {
        item.isRead = 1;
      }
    });
    await fetchUnreadCount();
    window.dispatchEvent(new CustomEvent("notification-read"));
    ElMessage.success("已标记为已读");
  } catch (error) {
    // 静默处理
  }
};

const handleSingleRead = async (notification) => {
  try {
    await markNotificationsAsRead([notification.id]);
    notification.isRead = 1;
    await fetchUnreadCount();
    window.dispatchEvent(new CustomEvent("notification-read"));
    ElMessage.success("已标记为已读");
  } catch (error) {
    // 静默处理
  }
};

const handleDelete = async (notificationId) => {
  try {
    await ElMessageBox.confirm("确定要删除这条通知吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await deleteNotifications([notificationId]);
    notificationList.value = notificationList.value.filter((item) => item.id !== notificationId);
    total.value = Math.max(total.value - 1, 0);
    hasMore.value = notificationList.value.length < total.value;
    await fetchUnreadCount();
    ElMessage.success("删除成功");
  } catch (error) {
    if (error !== "cancel") {
      // 静默处理
    }
  }
};

const getMessageText = (notification) => {
  const contentData = notification.contentData;
  if (!contentData) {
    return notification.content;
  }

  let text = contentData.title || contentData.text || notification.content;
  if (contentData.nickname) {
    text = text.replace(contentData.nickname, "").trim();
  }
  if (contentData.articleTitle) {
    text = text.replace(`《${contentData.articleTitle}》`, "").trim();
  }
  return text;
};

const goToUserPage = (userId) => {
  if (userId) {
    router.push(`/user/${userId}`);
  }
};

const goToArticle = (userId, articleId) => {
  if (userId && articleId) {
    router.push(`/user/${userId}/article/${articleId}`);
  }
};

const handleNotificationClick = (notification) => {
  const contentData = notification.contentData;
  if (!contentData) {
    return;
  }

  if (contentData.articleId && contentData.authorId) {
    goToArticle(contentData.authorId, contentData.articleId);
  } else if (contentData.userId) {
    goToUserPage(contentData.userId);
  }
};

const formatTime = (time) => formatTimeAgo(time);

const handleRefreshNotifications = async () => {
  currentPage.value = 1;
  hasMore.value = true;
  await fetchNotifications(true);
  await fetchUnreadCount();
};

onMounted(async () => {
  await handleRefreshNotifications();
  window.addEventListener("scroll", handleScroll);
  window.addEventListener("refresh-notifications", handleRefreshNotifications);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
  window.removeEventListener("refresh-notifications", handleRefreshNotifications);
});
</script>

<style lang="scss" scoped>
.notification-center {
  --bg-page: #f5f7fb;
  --bg-card: #ffffff;
  --bg-soft: #f8fafc;
  --bg-accent: #eef4ff;
  --text-title: #162033;
  --text-primary: #344054;
  --text-secondary: #667085;
  --text-muted: #98a2b3;
  --border: #dbe3ef;
  --border-strong: #c6d2e3;
  --accent: #2f6fec;
  --accent-strong: #1f5bd0;
  --shadow: 0 1px 3px rgba(17, 24, 39, 0.08);
  --shadow-hover: 0 10px 24px rgba(17, 24, 39, 0.08);

  min-height: 100vh;
  padding-top: 100px !important;
  padding-bottom: 48px;
  background:
    radial-gradient(circle at top left, var(--bg-accent), transparent 34%),
    linear-gradient(180deg, var(--bg-page) 0%, var(--bg-soft) 100%);

  .container {
    max-width: 1180px;
    margin: 0 auto;
    padding: 0 20px;

    .page-header {
      display: flex;
      justify-content: space-between;
      gap: 20px;
      padding: 28px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 24px;
      box-shadow: var(--shadow);

      .header-copy {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .header-copy__eyebrow {
          margin: 0;
          font-size: 12px;
          font-weight: 700;
          letter-spacing: 0.16em;
          text-transform: uppercase;
          color: var(--accent);
        }

        .header-copy__title {
          margin: 0;
          font-family: "AlimamaShuHei", "Helvetica Neue", Arial, sans-serif;
          font-size: 34px;
          color: var(--text-title);
        }

        .header-copy__description {
          margin: 0;
          max-width: 620px;
          font-size: 15px;
          line-height: 1.7;
          color: var(--text-secondary);
        }
      }

      .header-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 12px;
        align-content: flex-start;

        :deep(.el-button) {
          height: 40px;
          padding: 0 16px;
          border-radius: 999px;
          border-color: var(--border-strong);
          color: var(--text-primary);
          background: var(--bg-soft);
        }
      }
    }

    .summary-grid {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: 14px;
      margin-top: 18px;

      .summary-card {
        padding: 18px;
        background: var(--bg-card);
        border: 1px solid var(--border);
        border-radius: 18px;
        box-shadow: var(--shadow);

        .summary-card__label {
          font-size: 13px;
          color: var(--text-secondary);
        }

        .summary-card__value {
          margin-top: 10px;
          font-size: 30px;
          line-height: 1;
          color: var(--text-title);
        }

        .summary-card__hint {
          margin-top: 8px;
          font-size: 12px;
          color: var(--text-muted);
        }
      }
    }

    .notification-tabs {
      margin-top: 18px;
      padding: 0 24px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-bottom: none;
      border-radius: 24px 24px 0 0;
      box-shadow: var(--shadow);

      :deep(.el-tabs__header) {
        margin: 0;
      }

      :deep(.el-tabs__nav-wrap::after) {
        display: none;
      }

      :deep(.el-tabs__active-bar) {
        height: 3px;
        border-radius: 999px;
        background: var(--accent);
      }

      :deep(.el-tabs__item) {
        height: 64px;
        color: var(--text-secondary);
      }

      :deep(.el-tabs__item.is-active) {
        color: var(--text-title);
      }

      .tab-label {
        display: inline-flex;
        align-items: center;
        gap: 8px;
        font-size: 14px;
        font-weight: 600;

        .tab-label__badge {
          display: inline-flex;
        }
      }
    }

    .notification-list {
      min-height: 420px;
      padding: 24px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-top: none;
      border-radius: 0 0 24px 24px;
      box-shadow: var(--shadow);

      .list-toolbar {
        display: flex;
        justify-content: space-between;
        gap: 12px;
        margin-bottom: 18px;

        .list-toolbar__meta {
          font-size: 13px;
          color: var(--text-muted);
        }
      }

      .empty-state {
        display: flex;
        align-items: center;
        justify-content: center;
        min-height: 320px;
      }

      .notification-items {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .notification-item {
          display: grid;
          grid-template-columns: 56px minmax(0, 1fr) auto;
          gap: 16px;
          padding: 18px;
          border: 1px solid var(--border);
          border-radius: 18px;
          background: var(--bg-card);
          box-shadow: var(--shadow);
          transition: box-shadow 0.2s ease, border-color 0.2s ease;

          &:hover {
            border-color: var(--border-strong);
            box-shadow: var(--shadow-hover);
          }

          &.unread {
            background: var(--bg-accent);
          }

          .notification-item__avatar {
            display: flex;
            align-items: flex-start;
            justify-content: center;
            cursor: pointer;

            .user-avatar {
              border: 1px solid var(--border);
              background: var(--bg-soft);
              color: var(--accent);
            }
          }

          .notification-item__content {
            display: flex;
            flex-direction: column;
            gap: 10px;
            min-width: 0;
            cursor: pointer;

            .notification-item__top {
              display: flex;
              justify-content: space-between;
              gap: 12px;

              .type-pill {
                display: inline-flex;
                align-items: center;
                min-height: 26px;
                padding: 0 10px;
                border-radius: 999px;
                background: var(--bg-soft);
                color: var(--accent-strong);
                font-size: 12px;
                font-weight: 700;
              }

              .notification-time {
                font-size: 12px;
                color: var(--text-muted);
              }
            }

            .notification-item__text {
              display: flex;
              flex-wrap: wrap;
              gap: 6px;
              font-size: 15px;
              line-height: 1.8;

              .user-nickname {
                color: var(--text-title);
                font-weight: 700;
              }

              .message-text {
                color: var(--text-primary);
              }

              .article-title {
                max-width: 320px;
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                color: var(--accent-strong);
                font-weight: 600;
              }
            }

            .comment-content {
              padding: 12px 14px;
              background: var(--bg-soft);
              border: 1px solid var(--border);
              border-radius: 14px;

              .comment-content__text {
                display: -webkit-box;
                overflow: hidden;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 3;
                line-clamp: 3;
                font-size: 13px;
                line-height: 1.7;
                color: var(--text-secondary);
                word-break: break-word;
              }
            }
          }

          .notification-item__actions {
            display: flex;
            flex-direction: column;
            gap: 10px;

            :deep(.el-button) {
              min-width: 84px;
              height: 36px;
              padding: 0 14px;
              border-radius: 999px;
              border-color: var(--border-strong);
              color: var(--text-primary);
              background: var(--bg-soft);
            }
          }
        }

        .loading-more {
          display: flex;
          justify-content: center;
          align-items: center;
          gap: 10px;
          padding: 24px 0 12px;
          color: var(--text-secondary);

          .loading-more__spinner {
            width: 18px;
            height: 18px;
            border: 2px solid var(--border);
            border-top-color: var(--accent);
            border-radius: 50%;
            animation: spin 1s linear infinite;
          }
        }

        .no-more {
          padding-top: 8px;
          text-align: center;
          color: var(--text-muted);
          font-size: 13px;
        }
      }
    }
  }
}

html.dark {
  .notification-center {
    --bg-page: #0b1220;
    --bg-card: #111a2d;
    --bg-soft: #152238;
    --bg-accent: #132a4e;
    --text-title: #f3f6fc;
    --text-primary: #d7deea;
    --text-secondary: #a7b3c7;
    --text-muted: #8292ac;
    --border: #25344d;
    --border-strong: #355070;
    --accent: #7fb0ff;
    --accent-strong: #a7c7ff;
    --shadow: 0 1px 3px rgba(2, 6, 23, 0.35);
    --shadow-hover: 0 12px 28px rgba(2, 6, 23, 0.34);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

@media (max-width: 900px) {
  .notification-center {
    .container {
      .page-header {
        flex-direction: column;
      }

      .summary-grid {
        grid-template-columns: 1fr;
      }

      .notification-list {
        .notification-items {
          .notification-item {
            grid-template-columns: 48px minmax(0, 1fr);

            .notification-item__actions {
              grid-column: 1 / -1;
              flex-direction: row;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 640px) {
  .notification-center {
    padding-top: 80px !important;
    padding-bottom: 36px;

    .container {
      padding: 0 14px;

      .page-header {
        padding: 20px;

        .header-copy {
          .header-copy__title {
            font-size: 28px;
          }
        }
      }

      .notification-tabs {
        padding: 0 16px;
      }

      .notification-list {
        padding: 18px;

        .notification-items {
          .notification-item {
            padding: 14px;

            .notification-item__content {
              .notification-item__top {
                flex-direction: column;
                align-items: flex-start;
              }

              .notification-item__text {
                font-size: 14px;

                .article-title {
                  max-width: 100%;
                }
              }
            }

            .notification-item__actions {
              width: 100%;

              :deep(.el-button) {
                flex: 1;
                min-width: 0;
              }
            }
          }
        }
      }
    }
  }
}
</style>
