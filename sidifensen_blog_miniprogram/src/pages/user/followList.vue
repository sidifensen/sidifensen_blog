<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getMyFollowers, getMyFollowings } from '@/api/follow'

const userStore = useUserStore()
const users = ref([])
const loading = ref(false)
const refreshing = ref(false)
const activeTab = ref('followers')
const pageNum = ref(1)
const pageSize = ref(20)
const noMore = ref(false)
const loadMore = ref(false)

async function fetchUsers() {
  try {
    loading.value = true
    const userId = userStore.userInfo?.id
    if (!userId) {
      users.value = []
      return
    }
    const res = activeTab.value === 'followers'
      ? await getMyFollowers(userId, { pageNum: pageNum.value, pageSize: pageSize.value })
      : await getMyFollowings(userId, { pageNum: pageNum.value, pageSize: pageSize.value })

    if (pageNum.value === 1) {
      users.value = res.data || res
    } else {
      users.value = [...users.value, ...(res.data || res)]
    }

    if (res.data && res.data.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取用户列表失败', err)
  } finally {
    loading.value = false
  }
}

function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1
  fetchUsers().finally(() => {
    refreshing.value = false
  })
}

function onLoadMore() {
  if (noMore.value || loadMore.value) return
  loadMore.value = true
  pageNum.value++
  fetchUsers().finally(() => {
    loadMore.value = false
  })
}

function onTabChange(tab) {
  activeTab.value = tab
  onRefresh()
}

function goToProfile(userId) {
  uni.navigateTo({ url: `/pages/user/profile?userId=${userId}` })
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }
  fetchUsers()
})
</script>

<template>
  <view class="follow-list-page">
    <!-- 标签切换 -->
    <view class="tabs">
      <view
        class="tab-item"
        :class="{ active: activeTab === 'followers' }"
        @click="onTabChange('followers')"
      >
        粉丝
      </view>
      <view
        class="tab-item"
        :class="{ active: activeTab === 'followings' }"
        @click="onTabChange('followings')"
      >
        关注
      </view>
    </view>

    <!-- 用户列表 -->
    <scroll-view
      class="user-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view
        v-for="user in users"
        :key="user.id"
        class="user-item card"
        @click="goToProfile(user.id)"
      >
        <u-avatar :src="user.avatar" size="96" />
        <view class="user-info">
          <view class="user-name">{{ user.nickname }}</view>
          <view v-if="user.bio" class="user-bio">{{ user.bio }}</view>
        </view>
      </view>

      <view v-if="users.length === 0 && !loading" class="empty-state">
        <text class="empty-text">{{ activeTab === 'followers' ? '暂无粉丝' : '暂无关注' }}</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.follow-list-page {
  min-height: 100vh;
  background: var(--u-bg-color);
}

.tabs {
  display: flex;
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);

  .tab-item {
    flex: 1;
    text-align: center;
    padding: var(--spacing-md) 0;
    font-size: 15px;
    color: var(--u-tips-color);

    &.active {
      color: var(--u-type-primary);
      font-weight: 500;
    }
  }
}

.user-list {
  height: calc(100vh - 50px);
  padding: var(--spacing-lg);

  .user-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);

    .user-info {
      flex: 1;

      .user-name {
        font-size: 15px;
        font-weight: 500;
        color: var(--u-main-color);
      }

      .user-bio {
        font-size: 13px;
        color: var(--u-tips-color);
        margin-top: 4px;
        @include text-ellipsis;
      }
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--u-tips-color);
}
</style>
