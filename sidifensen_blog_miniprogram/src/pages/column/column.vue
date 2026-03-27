<script setup>
import { ref, onMounted } from 'vue'
import { getColumnList } from '@/api/column'
import { timeAgo } from '@/utils/format'

const columnList = ref([])
const loading = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const noMore = ref(false)
const loadMore = ref(false)

/**
 * 获取专栏列表
 */
async function fetchColumnList() {
  try {
    const res = await getColumnList({ pageNum: pageNum.value, pageSize: pageSize.value })
    if (pageNum.value === 1) {
      columnList.value = res.data || res
    } else {
      columnList.value = [...columnList.value, ...(res.data || res)]
    }
    if (res.data && res.data.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取专栏列表失败', err)
  }
}

function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1
  fetchColumnList().finally(() => {
    refreshing.value = false
  })
}

function onLoadMore() {
  if (noMore.value || loadMore.value) return
  loadMore.value = true
  pageNum.value++
  fetchColumnList().finally(() => {
    loadMore.value = false
  })
}

function goToColumnDetail(columnId) {
  uni.navigateTo({ url: `/pages/columnDetail/columnDetail?id=${columnId}` })
}

onMounted(() => {
  fetchColumnList()
})
</script>

<template>
  <view class="column-page">
    <scroll-view
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="column-list">
        <view
          v-for="column in columnList"
          :key="column.id"
          class="column-card card"
          @click="goToColumnDetail(column.id)"
        >
          <image class="column-cover" :src="column.coverUrl" mode="aspectFill" />
          <view class="column-info">
            <view class="column-name">{{ column.name }}</view>
            <view class="column-desc">{{ column.description }}</view>
            <view class="column-meta">
              <text class="article-count">{{ column.articleCount }} 篇文章</text>
              <text class="update-time">{{ timeAgo(column.updateTime) }}</text>
            </view>
          </view>
        </view>
      </view>

      <view v-if="columnList.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无专栏</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.column-page {
  min-height: 100vh;
  background: var(--bg-page);
  padding: var(--spacing-lg);
}

.column-list {
  .column-card {
    display: flex;
    padding: 0;
    overflow: hidden;
    margin-bottom: var(--spacing-md);

    .column-cover {
      width: 120px;
      height: 120px;
      flex-shrink: 0;
    }

    .column-info {
      flex: 1;
      padding: var(--spacing-md);

      .column-name {
        font-size: 16px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .column-desc {
        font-size: 14px;
        color: var(--text-muted);
        margin-top: var(--spacing-sm);
        @include text-ellipsis(2);
      }

      .column-meta {
        display: flex;
        justify-content: space-between;
        margin-top: var(--spacing-md);
        font-size: 12px;
        color: var(--text-muted);
      }
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--text-muted);
}
</style>
