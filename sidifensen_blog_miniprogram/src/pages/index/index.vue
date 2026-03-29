<script setup>
import { ref, onMounted } from 'vue'
import { getArticleList } from '@/api/article'
import { formatCount } from '@/utils/format'

// 文章列表
const articleList = ref([])
// 加载状态
const loading = ref(false)
// 上拉加载更多状态
const loadMore = ref(false)
// 没有更多数据
const noMore = ref(false)
// 当前页码
const pageNum = ref(1)
// 每页数量
const pageSize = ref(10)
// 搜索关键词
const searchKey = ref('')
// 选中分类
const selectedCategory = ref('')

/**
 * 获取文章列表
 */
async function fetchArticleList() {
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      category: selectedCategory.value,
      keyword: searchKey.value
    }

    const res = await getArticleList(params)
    const listData = res.data?.data || []

    if (pageNum.value === 1) {
      articleList.value = listData
    } else {
      articleList.value = [...articleList.value, ...listData]
    }

    // 判断是否还有更多数据
    if (listData.length < pageSize.value) {
      noMore.value = true
    }
  } catch (err) {
    console.error('获取文章列表失败', err)
  }
}

/**
 * 上拉加载更多
 */
function onLoadMore() {
  if (noMore.value || loadMore.value) return

  loadMore.value = true
  pageNum.value++

  fetchArticleList().finally(() => {
    loadMore.value = false
  })
}

/**
 * 跳转搜索页
 */
function goToSearch() {
  uni.navigateTo({ url: '/pages/search/search' })
}

/**
 * 跳转文章详情
 */
function goToArticle(articleId) {
  uni.navigateTo({ url: `/pages/article/article?id=${articleId}` })
}

/**
 * 跳转登录
 */
function goToLogin() {
  uni.navigateTo({ url: '/pages/login/login' })
}

onMounted(() => {
  fetchArticleList()
})
</script>

<template>
  <view class="index-page">
    <!-- 顶部搜索栏 -->
    <view class="search-bar" @click="goToSearch">
      <view class="search-box">
        <text class="search-icon">🔍</text>
        <text class="search-placeholder">搜索文章...</text>
      </view>
    </view>

    <!-- 文章列表 -->
    <scroll-view
      class="article-list"
      scroll-y
      :scrolltolower-threshold="50"
      @scrolltolower="onLoadMore"
    >

      <view class="article-container">
        <view
          v-for="article in articleList"
          :key="article.id"
          class="article-card card"
          @click="goToArticle(article.id)"
        >
          <!-- 文章封面 -->
          <image
            v-if="article.coverUrl"
            class="article-cover"
            :src="article.coverUrl"
            mode="aspectFill"
          />

          <!-- 文章信息 -->
          <view class="article-info">
            <view class="article-title">{{ article.title }}</view>
            <view class="article-summary">{{ article.description || article.summary }}</view>
            <view class="article-meta">
              <view class="article-author">
                <uv-avatar :src="article.avatar" size="40" />
                <text class="author-name">{{ article.nickname }}</text>
              </view>
              <view class="article-stats">
                <text class="stat-item">
                  <text class="icon">👁</text>
                  {{ formatCount(article.readCount) }}
                </text>
                <text class="stat-item">
                  <text class="icon">♥</text>
                  {{ formatCount(article.likeCount) }}
                </text>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="articleList.length === 0 && !loading" class="empty-state">
          <text class="empty-text">暂无文章</text>
        </view>

        <!-- 加载更多 -->
        <view v-if="loadMore" class="load-more">
          <u-loading mode="circle" />
          <text class="load-text">加载中...</text>
        </view>

        <!-- 没有更多 -->
        <view v-if="noMore && articleList.length > 0" class="no-more">
          <text class="no-more-text">没有更多了</text>
        </view>
      </view>
    </scroll-view>

  </view>
</template>

<style lang="scss" scoped>
.index-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  overflow: hidden;
  background: var(--u-bg-color);
}

/* 搜索栏 */
.search-bar {
  width: 100%;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);
  box-sizing: border-box;

  .search-box {
    display: flex;
    align-items: center;
    width: 100%;
    height: 36px;
    padding: 0 var(--spacing-md);
    background: var(--u-bg-color);
    border-radius: 18px;
    box-sizing: border-box;

    .search-icon {
      flex-shrink: 0;
      font-size: 14px;
      margin-right: var(--spacing-sm);
    }

    .search-placeholder {
      flex: 1;
      font-size: 14px;
      color: var(--u-tips-color);
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }
  }
}

/* 文章列表 */
.article-list {
  flex: 1;
  overflow: hidden;
  padding-bottom: 60px;

  .article-container {
    padding: var(--spacing-lg);
  }
}

.article-card {
  margin-bottom: var(--spacing-lg);
  padding: 0;
  overflow: hidden;

  .article-cover {
    width: 100%;
    height: 180px;
  }

  .article-info {
    padding: var(--spacing-md);

    .article-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--u-main-color);
      margin-bottom: var(--spacing-sm);
      @include text-ellipsis(2);
    }

    .article-summary {
      font-size: 14px;
      color: var(--u-tips-color);
      margin-bottom: var(--spacing-md);
      @include text-ellipsis(2);
    }

    .article-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .article-author {
        display: flex;
        align-items: center;
        gap: var(--spacing-sm);

        .author-name {
          font-size: 12px;
          color: var(--u-content-color);
          max-width: 120px;
          line-height: 1.4;
          word-break: break-all;
        }
      }

      .article-stats {
        display: flex;
        gap: var(--spacing-lg);

        .stat-item {
          display: flex;
          align-items: center;
          gap: 4px;
          font-size: 12px;
          color: var(--u-tips-color);

          .icon {
            font-size: 14px;
          }
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
  gap: var(--spacing-sm);

  .load-text {
    font-size: 14px;
    color: var(--u-tips-color);
  }
}

.no-more {
  @include flex-center;
  padding: var(--spacing-lg);

  .no-more-text {
    font-size: 14px;
    color: var(--u-tips-color);
  }
}
</style>
