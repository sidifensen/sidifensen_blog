<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getMyArticles } from '@/api/user'
import { formatCount } from '@/utils/format'

const userStore = useUserStore()
const articles = ref([])
const loading = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const noMore = ref(false)
const loadMore = ref(false)

async function fetchArticles() {
  try {
    loading.value = true
    const res = await getMyArticles({ pageNum: pageNum.value, pageSize: pageSize.value })
    // res 格式: { code: 200, data: { data: [...], total: xxx }, message: "success" }
    const pageVo = res.data || res
    const listData = pageVo.data || []
    if (pageNum.value === 1) {
      articles.value = listData
    } else {
      articles.value = [...articles.value, ...listData]
    }
    if (listData.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取文章失败', err)
  } finally {
    loading.value = false
  }
}

function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1
  fetchArticles().finally(() => {
    refreshing.value = false
  })
}

function onLoadMore() {
  if (noMore.value || loadMore.value) return
  loadMore.value = true
  pageNum.value++
  fetchArticles().finally(() => {
    loadMore.value = false
  })
}

function goToArticle(articleId) {
  uni.navigateTo({ url: `/pages/article/article?id=${articleId}` })
}

onMounted(() => {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }
  fetchArticles()
})
</script>

<template>
  <view class="my-articles-page">
    <scroll-view
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
      @scrolltolower="onLoadMore"
    >
      <view class="article-list">
        <view
          v-for="article in articles"
          :key="article.id"
          class="article-item card"
          @click="goToArticle(article.id)"
        >
          <view class="article-title">{{ article.title }}</view>
          <view class="article-summary">{{ article.description || article.summary }}</view>
          <view class="article-meta">
            <text>{{ formatCount(article.viewCount || 0) }} 阅读</text>
            <text>{{ formatCount(article.likeCount || 0) }} 点赞</text>
            <text>{{ article.createTime }}</text>
          </view>
        </view>
      </view>

      <view v-if="articles.length === 0 && !loading" class="empty-state">
        <text class="empty-text">暂无文章</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.my-articles-page {
  min-height: 100vh;
  background: var(--u-bg-color);
  padding: var(--spacing-lg);
}

.article-list {
  .article-item {
    margin-bottom: var(--spacing-md);

    .article-title {
      font-size: 16px;
      font-weight: 500;
      color: var(--u-main-color);
    }

    .article-summary {
      font-size: 14px;
      color: var(--u-tips-color);
      margin-top: var(--spacing-sm);
      @include text-ellipsis(2);
    }

    .article-meta {
      display: flex;
      gap: var(--spacing-lg);
      margin-top: var(--spacing-md);
      font-size: 12px;
      color: var(--u-tips-color);
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--u-tips-color);
}
</style>
