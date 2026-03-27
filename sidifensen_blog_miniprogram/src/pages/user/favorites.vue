<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getMyFavorites } from '@/api/favorite'

const userStore = useUserStore()
const favorites = ref([])
const loading = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const noMore = ref(false)
const loadMore = ref(false)

async function fetchFavorites() {
  try {
    const res = await getMyFavorites({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (pageNum.value === 1) {
      favorites.value = res.data || res
    } else {
      favorites.value = [...favorites.value, ...(res.data || res)]
    }
    if (res.data && res.data.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取收藏失败', err)
  }
}

function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1
  fetchFavorites().finally(() => {
    refreshing.value = false
  })
}

function onLoadMore() {
  if (noMore.value || loadMore.value) return
  loadMore.value = true
  pageNum.value++
  fetchFavorites().finally(() => {
    loadMore.value = false
  })
}

function goToFavoriteDetail(favoriteId) {
  uni.navigateTo({ url: `/pages/favoriteDetail/favoriteDetail?id=${favoriteId}` })
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }
  fetchFavorites()
})
</script>

<template>
  <view class="my-favorites-page">
    <scroll-view
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="favorites-list">
        <view
          v-for="item in favorites"
          :key="item.id"
          class="favorite-item card"
          @click="goToFavoriteDetail(item.id)"
        >
          <view class="favorite-icon">&#xe614;</view>
          <view class="favorite-info">
            <view class="favorite-name">{{ item.name }}</view>
            <view class="favorite-meta">{{ item.articleCount }} 篇文章</view>
          </view>
          <view class="favorite-arrow">&#xe603;</view>
        </view>
      </view>

      <view v-if="favorites.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无收藏夹</text>
      </view>

      <view v-if="loadMore" class="load-more">
        <uv-loading-icon mode="circle" />
      </view>

      <view v-if="noMore && favorites.length > 0" class="no-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.my-favorites-page {
  min-height: 100vh;
  background: var(--bg-page);
  padding: var(--spacing-lg);
}

.favorites-list {
  .favorite-item {
    display: flex;
    align-items: center;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
    padding: var(--spacing-md);

    .favorite-icon {
      font-size: 32px;
      color: var(--color-primary);
    }

    .favorite-info {
      flex: 1;

      .favorite-name {
        font-size: 15px;
        font-weight: 500;
        color: var(--text-primary);
      }

      .favorite-meta {
        font-size: 12px;
        color: var(--text-muted);
        margin-top: 4px;
      }
    }

    .favorite-arrow {
      font-size: 14px;
      color: var(--text-muted);
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--text-muted);
}

.load-more {
  @include flex-center;
  padding: var(--spacing-lg);
}

.no-more {
  @include flex-center;
  padding: var(--spacing-lg);
  color: var(--text-muted);
  font-size: 14px;
}
</style>
