<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getUserAlbums } from '@/api/user'
import { formatCount } from '@/utils/format'

const userStore = useUserStore()
const albums = ref([])
const loading = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const noMore = ref(false)
const loadMore = ref(false)

async function fetchAlbums() {
  try {
    const res = await getUserAlbums({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (pageNum.value === 1) {
      albums.value = res.data || res
    } else {
      albums.value = [...albums.value, ...(res.data || res)]
    }
    if (res.data && res.data.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取相册失败', err)
  }
}

function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1
  fetchAlbums().finally(() => {
    refreshing.value = false
  })
}

function onLoadMore() {
  if (noMore.value || loadMore.value) return
  loadMore.value = true
  pageNum.value++
  fetchAlbums().finally(() => {
    loadMore.value = false
  })
}

function goToAlbum(albumId) {
  uni.navigateTo({ url: `/pages/albumDetail/albumDetail?id=${albumId}` })
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }
  fetchAlbums()
})
</script>

<template>
  <view class="my-albums-page">
    <scroll-view
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="album-grid">
        <view
          v-for="album in albums"
          :key="album.id"
          class="album-item"
          @click="goToAlbum(album.id)"
        >
          <image class="album-cover" :src="album.coverUrl" mode="aspectFill" />
          <view class="album-name">{{ album.name }}</view>
          <view class="album-count">{{ formatCount(album.photoCount) }} 张</view>
        </view>
      </view>

      <view v-if="albums.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无相册</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.my-albums-page {
  min-height: 100vh;
  background: var(--u-bg-color);
  padding: var(--spacing-lg);
}

.album-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: var(--spacing-md);

  .album-item {
    background: var(--u-bg-white);
    border-radius: var(--radius-md);
    overflow: hidden;
    border: 1px solid var(--u-border-color);

    .album-cover {
      width: 100%;
      height: 150px;
    }

    .album-name {
      font-size: 14px;
      font-weight: 500;
      color: var(--u-main-color);
      padding: var(--spacing-sm) var(--spacing-md);
      @include text-ellipsis;
    }

    .album-count {
      font-size: 12px;
      color: var(--u-tips-color);
      padding: 0 var(--spacing-md) var(--spacing-sm);
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--u-tips-color);
}
</style>
