<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getMyFavorites } from '@/api/favorite'
import { formatCount } from '@/utils/format'

const userStore = useUserStore()
const favoriteId = ref('')
const favoriteName = ref('')
const articles = ref([])
const loading = ref(false)
const refreshing = ref(false)
const pageNum = ref(1)
const pageSize = ref(20)
const noMore = ref(false)
const loadMore = ref(false)

/**
 * 获取收藏夹详情
 */
async function fetchFavoriteDetail() {
  try {
    loading.value = true
    // 获取收藏夹列表，找到对应的收藏夹
    const res = await getMyFavorites({ pageNum: 1, pageSize: 100 })
    const favorites = res.data || res || []
    const favorite = favorites.find(f => f.id === favoriteId.value)
    if (favorite) {
      favoriteName.value = favorite.name
    }
  } catch (err) {
    console.error('获取收藏夹详情失败', err)
  } finally {
    loading.value = false
  }
}

/**
 * 获取收藏夹内的文章列表
 */
async function fetchArticles() {
  try {
    const res = await getMyFavorites({ pageNum: pageNum.value, pageSize: pageSize.value })
    const list = res.data || res || []
    if (pageNum.value === 1) {
      articles.value = list
    } else {
      articles.value = [...articles.value, ...list]
    }
    if (list.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取文章列表失败', err)
  }
}

function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1
  fetchFavoriteDetail()
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

/**
 * 跳转文章详情
 */
function goToArticle(articleId) {
  uni.navigateTo({ url: `/pages/article/article?id=${articleId}` })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  favoriteId.value = currentPage.options?.id || ''
  favoriteName.value = currentPage.options?.name || '我的收藏'

  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  if (favoriteId.value) {
    fetchFavoriteDetail()
    fetchArticles()
  }
})
</script>

<template>
  <view class="favorite-detail-page">
    <view class="page-header">
      <view class="favorite-icon">&#xe614;</view>
      <view class="favorite-name">{{ favoriteName }}</view>
    </view>

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
          <view class="article-cover" v-if="article.coverUrl">
            <image :src="article.coverUrl" mode="aspectFill" />
          </view>
          <view class="article-info">
            <view class="article-title">{{ article.title }}</view>
            <view class="article-summary">{{ article.summary }}</view>
            <view class="article-meta">
              <text class="meta-text">{{ formatCount(article.likeCount || 0) }} 点赞</text>
              <text class="meta-text">{{ formatCount(article.commentCount || 0) }} 评论</text>
            </view>
          </view>
        </view>

        <view v-if="articles.length === 0 && !loading" class="empty-state">
          <text class="empty-text">暂无收藏内容</text>
        </view>
      </view>

      <view v-if="loadMore" class="load-more">
        <loading-icon />
      </view>

      <view v-if="noMore && articles.length > 0" class="no-more">
        <text>没有更多了</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.favorite-detail-page {
  min-height: 100vh;
  background: var(--u-bg-color);
}

.page-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-2xl) var(--spacing-lg);
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);

  .favorite-icon {
    font-size: 48px;
    color: var(--u-type-primary);
    margin-bottom: var(--spacing-md);
  }

  .favorite-name {
    font-size: 18px;
    font-weight: 600;
    color: var(--u-main-color);
  }
}

.article-list {
  padding: var(--spacing-lg);

  .article-item {
    display: flex;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
    padding: var(--spacing-md);

    .article-cover {
      width: 100px;
      height: 80px;
      border-radius: var(--radius-md);
      overflow: hidden;
      flex-shrink: 0;

      image {
        width: 100%;
        height: 100%;
      }
    }

    .article-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;

      .article-title {
        font-size: 15px;
        font-weight: 500;
        color: var(--u-main-color);
        @include text-ellipsis(1);
      }

      .article-summary {
        font-size: 13px;
        color: var(--u-tips-color);
        margin-top: 4px;
        @include text-ellipsis(2);
      }

      .article-meta {
        display: flex;
        gap: var(--spacing-md);
        margin-top: var(--spacing-sm);

        .meta-text {
          font-size: 12px;
          color: var(--u-tips-color);
        }
      }
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--u-tips-color);
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
</style>
