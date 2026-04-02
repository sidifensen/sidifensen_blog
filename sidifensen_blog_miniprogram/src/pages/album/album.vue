<script setup>
import { ref, onMounted, computed } from 'vue'
import { getAlbumList } from '@/api/album'
import { useTheme } from 'uview-pro'
import TabBar from '@/components/TabBar/TabBar.vue'

const { darkMode } = useTheme()

// 深色模式
const isDark = computed(() => darkMode.value === 'dark')

// 相册列表
const albumList = ref([])
const loading = ref(false)
const refreshing = ref(false)
const loadMore = ref(false)
const noMore = ref(false)
const pageNum = ref(1)
const pageSize = ref(10)

/**
 * 获取相册列表
 */
async function fetchAlbumList() {
  try {
    const params = { pageNum: pageNum.value, pageSize: pageSize.value }
    const res = await getAlbumList(params)

    if (pageNum.value === 1) {
      albumList.value = res.data || res
    } else {
      albumList.value = [...albumList.value, ...(res.data || res)]
    }

    if (res.data && res.data.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取相册列表失败', err)
  }
}

/**
 * 下拉刷新
 */
function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1

  fetchAlbumList().finally(() => {
    refreshing.value = false
  })
}

/**
 * 上拉加载更多
 */
function onLoadMore() {
  if (noMore.value || loadMore.value) return

  loadMore.value = true
  pageNum.value++

  fetchAlbumList().finally(() => {
    loadMore.value = false
  })
}

/**
 * 跳转相册详情
 */
function goToAlbumDetail(albumId) {
  uni.navigateTo({ url: `/pages/albumDetail/albumDetail?id=${albumId}` })
}

onMounted(() => {
  fetchAlbumList()
})
</script>

<template>
  <view class="album-page" :class="{ 'dark-mode': isDark }">
    <!-- 相册列表 -->
    <scroll-view
      class="album-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="album-grid">
        <view
          v-for="album in albumList"
          :key="album.id"
          class="album-card"
          @click="goToAlbumDetail(album.id)"
        >
          <image class="album-cover" :src="album.coverUrl" mode="aspectFill" />
          <view class="album-info">
            <view class="album-name">{{ album.name }}</view>
          </view>
        </view>
      </view>

      <!-- 空状态 -->
      <view v-if="albumList.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无相册</text>
      </view>

      <!-- 加载更多 -->
      <view v-if="loadMore" class="load-more">
        <loading-icon />
      </view>

      <!-- 没有更多 -->
      <view v-if="noMore && albumList.length > 0" class="no-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>

    <!-- 自定义底部导航栏 -->
    <TabBar />
  </view>
</template>

<style lang="scss" scoped>
.album-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--u-bg-color);
}

/* 相册列表 */
.album-list {
  flex: 1;
  padding-bottom: 60px;

  .album-container {
    padding: var(--spacing-lg);
  }

  .album-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-md);
    padding: var(--spacing-lg) var(--spacing-lg) 0;

    .album-card {
      background: var(--u-bg-white);
      border-radius: var(--radius-md);
      overflow: hidden;
      border: 1px solid var(--u-border-color);

      .album-cover {
        width: 100%;
        height: 150px;
      }

      .album-info {
        padding: var(--spacing-md);

        .album-name {
          font-size: 14px;
          font-weight: 500;
          color: var(--u-main-color);
          @include text-ellipsis;
        }
      }
    }
  }

  .empty-state {
    @include flex-center-column;
    padding: var(--spacing-2xl);
    color: var(--u-tips-color);

    .empty-icon {
      font-size: 48px;
      margin-bottom: var(--spacing-lg);
    }
  }

  .load-more {
    @include flex-center;
    padding: var(--spacing-lg);
  }

  .no-more {
    @include flex-center;
    padding: var(--spacing-lg);
    color: var(--u-tips-color);
    font-size: 14px;
  }
}

/* 深色模式 */
.album-page.dark-mode {
  background: #0f172a;

  .album-card {
    background: #1e293b;
    border-color: #334155;

    .album-info {
      .album-name {
        color: #f1f5f9;
      }
    }
  }

  .empty-state,
  .no-more {
    color: #94a3b8;
  }
}
</style>
