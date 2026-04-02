<script setup>
import { ref, onMounted } from 'vue'
import { getAnnouncementPage, readAnnouncement, getReadAnnouncementIds } from '@/api/announcement'

// 公告列表
const announcementList = ref([])
const readIds = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const hasMore = ref(true)

// 判断是否已读
function isRead(id) {
  return readIds.value.includes(id)
}

// 加载已读ID列表
async function fetchReadIds() {
  try {
    const res = await getReadAnnouncementIds()
    readIds.value = res.data || []
  } catch (err) {
    console.error('获取已读列表失败', err)
  }
}

// 加载公告列表
async function fetchAnnouncements(reset = false) {
  if (reset) {
    loading.value = true
    currentPage.value = 1
    announcementList.value = []
    hasMore.value = true
  }

  try {
    const res = await getAnnouncementPage(currentPage.value, pageSize.value)
    const list = res.data?.data || []
    if (reset) {
      announcementList.value = list
    } else {
      announcementList.value = [...announcementList.value, ...list]
    }
    hasMore.value = list.length >= pageSize.value
  } catch (err) {
    console.error('获取公告列表失败', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 点击公告卡片：标记已读
async function handleCardClick(item) {
  if (isRead(item.id)) return
  try {
    await readAnnouncement(item.id)
    if (!readIds.value.includes(item.id)) {
      readIds.value.push(item.id)
    }
  } catch (err) {
    console.error('标记已读失败', err)
  }
}

// 格式化时间
function formatTime(time) {
  if (!time) return ''
  const date = new Date(time)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 滚动到底部加载更多
function onReachBottom() {
  if (!hasMore.value || loading.value) return
  currentPage.value++
  fetchAnnouncements(false)
}

// 下拉刷新
function onPullDownRefresh() {
  fetchAnnouncements(true).then(() => {
    fetchReadIds()
  }).finally(() => {
    uni.stopPullDownRefresh()
  })
}

onMounted(async () => {
  await fetchAnnouncements(true)
  await fetchReadIds()
})
</script>

<template>
  <view class="announcement-page">
    <!-- 页面标题 -->
    <view class="page-header">
      <view class="header-content">
        <text class="header-title">网站公告</text>
        <text class="header-desc">了解平台的最新动态和重要通知</text>
      </view>
    </view>

    <!-- 公告列表 -->
    <view class="announcement-list" v-if="announcementList.length > 0">
      <view
        v-for="item in announcementList"
        :key="item.id"
        class="announcement-card"
        :class="{ unread: !isRead(item.id) }"
        @click="handleCardClick(item)"
      >
        <view class="card-icon">
          <u-icon name="bell" :size="24" :color="isRead(item.id) ? '#64748b' : '#0891b2'" />
        </view>
        <view class="card-body">
          <view class="card-header">
            <text class="card-title">{{ item.title }}</text>
            <text class="card-time">{{ formatTime(item.sendTime || item.createTime) }}</text>
          </view>
          <text class="card-content">{{ item.content }}</text>
          <view class="card-footer">
            <text v-if="!isRead(item.id)" class="unread-badge">未读</text>
            <text v-else class="read-badge">已读</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <view v-else class="empty-state">
      <u-empty text="暂无公告" icon="bell" />
    </view>

    <!-- 加载状态 -->
    <view v-if="loading && announcementList.length > 0" class="loading-more">
      <loading-icon />
      <text>加载中...</text>
    </view>

    <!-- 没有更多 -->
    <view v-if="!hasMore && announcementList.length > 0" class="no-more">
      <text>没有更多了</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.announcement-page {
  min-height: 100vh;
  background: var(--u-bg-color);
  padding-bottom: 20px;
}

.page-header {
  padding: 20px;
  background: linear-gradient(135deg, #0891b2 0%, #06b6d4 100%);

  .header-content {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .header-title {
    font-size: 22px;
    font-weight: 600;
    color: #ffffff;
  }

  .header-desc {
    font-size: 13px;
    color: rgba(255, 255, 255, 0.8);
  }
}

.announcement-list {
  padding: 12px;

  .announcement-card {
    display: flex;
    gap: 12px;
    padding: 16px;
    margin-bottom: 12px;
    background: var(--u-bg-white);
    border-radius: 12px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);

    &.unread {
      background: linear-gradient(135deg, rgba(8, 145, 178, 0.08) 0%, rgba(6, 182, 212, 0.08) 100%);
      border: 1px solid rgba(8, 145, 178, 0.2);
    }

    .card-icon {
      flex-shrink: 0;
      width: 44px;
      height: 44px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--u-bg-gray-light);
      border-radius: 10px;
    }

    .card-body {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
      gap: 6px;

      .card-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        gap: 8px;

        .card-title {
          font-size: 15px;
          font-weight: 600;
          color: var(--u-main-color);
          flex: 1;
        }

        .card-time {
          flex-shrink: 0;
          font-size: 12px;
          color: var(--u-tips-color);
        }
      }

      .card-content {
        font-size: 13px;
        color: var(--u-content-color);
        line-height: 1.5;
        overflow: hidden;
        text-overflow: ellipsis;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        line-clamp: 2;
        -webkit-box-orient: vertical;
      }

      .card-footer {
        .unread-badge {
          display: inline-block;
          padding: 2px 8px;
          border-radius: 999px;
          font-size: 11px;
          font-weight: 500;
          background: #0891b2;
          color: #ffffff;
        }

        .read-badge {
          display: inline-block;
          padding: 2px 8px;
          border-radius: 999px;
          font-size: 11px;
          font-weight: 500;
          background: var(--u-bg-gray-light);
          color: var(--u-tips-color);
        }
      }
    }
  }
}

.empty-state {
  padding: 60px 0;
  display: flex;
  justify-content: center;
}

.loading-more {
  padding: 16px 0;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  color: var(--u-tips-color);
  font-size: 13px;
}

.no-more {
  padding: 16px 0;
  text-align: center;
  color: var(--u-tips-color);
  font-size: 13px;
}
</style>
