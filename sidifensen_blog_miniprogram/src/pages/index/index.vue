<script setup>
import { ref, onMounted } from 'vue'
import { getArticleList } from '@/api/article'

// 文章列表
const articleList = ref([])
// 加载状态
const loading = ref(false)
// 下拉刷新状态
const refreshing = ref(false)
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
// 分类列表
const categoryList = ref([
  { id: '', name: '全部' },
  { id: 'tech', name: '技术' },
  { id: 'life', name: '生活' },
  { id: 'other', name: '其他' }
])

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
 * 下拉刷新
 */
function onRefresh() {
  refreshing.value = true
  noMore.value = false
  pageNum.value = 1

  fetchArticleList().finally(() => {
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

  fetchArticleList().finally(() => {
    loadMore.value = false
  })
}

/**
 * 切换分类
 */
function onCategoryChange(categoryId) {
  selectedCategory.value = categoryId
  onRefresh()
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
      <uv-input
        placeholder="搜索文章..."
        :disabled="true"
        prefixIcon="search"
        shape="circle"
      />
    </view>

    <!-- 分类标签 -->
    <scroll-view class="category-scroll" scroll-x>
      <view class="category-list">
        <view
          v-for="category in categoryList"
          :key="category.id"
          class="category-item"
          :class="{ active: selectedCategory === category.id }"
          @click="onCategoryChange(category.id)"
        >
          {{ category.name }}
        </view>
      </view>
    </scroll-view>

    <!-- 文章列表 -->
    <scroll-view
      class="article-list"
      scroll-y
      :refresher-enabled="true"
      :refresher-triggered="refreshing"
      @refresherrefresh="onRefresh"
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
            <view class="article-summary">{{ article.summary }}</view>
            <view class="article-meta">
              <view class="article-author">
                <uv-avatar :src="article.authorAvatar" size="20px" />
                <text class="author-name">{{ article.authorName }}</text>
              </view>
              <view class="article-stats">
                <text class="stat-item">
                  <text class="icon">&#xe8f8;</text>
                  {{ article.likeCount || 0 }}
                </text>
                <text class="stat-item">
                  <text class="icon">&#xe8f9;</text>
                  {{ article.commentCount || 0 }}
                </text>
              </view>
            </view>
          </view>
        </view>

        <!-- 空状态 -->
        <view v-if="articleList.length === 0 && !loading" class="empty-state">
          <text class="empty-icon">&#xe601;</text>
          <text class="empty-text">暂无文章</text>
        </view>

        <!-- 加载更多 -->
        <view v-if="loadMore" class="load-more">
          <uv-loading-icon mode="circle" />
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
  background: var(--bg-page);
}

/* 搜索栏 */
.search-bar {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);
}

/* 分类滚动 */
.category-scroll {
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);

  .category-list {
    display: flex;
    padding: var(--spacing-sm) var(--spacing-lg);
    gap: var(--spacing-md);
  }

  .category-item {
    flex-shrink: 0;
    padding: var(--spacing-sm) var(--spacing-md);
    border-radius: var(--radius-full);
    font-size: 14px;
    color: var(--text-regular);
    background: var(--bg-page);

    &.active {
      color: #ffffff;
      background: var(--color-primary);
    }
  }
}

/* 文章列表 */
.article-list {
  flex: 1;

  .article-container {
    padding: var(--spacing-lg);
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
        color: var(--text-primary);
        margin-bottom: var(--spacing-sm);
        @include text-ellipsis(2);
      }

      .article-summary {
        font-size: 14px;
        color: var(--text-muted);
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
            color: var(--text-regular);
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
            color: var(--text-muted);

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
    color: var(--text-muted);

    .empty-icon {
      font-size: 48px;
      margin-bottom: var(--spacing-lg);
    }
  }

  .load-more {
    @include flex-center;
    padding: var(--spacing-lg);
    gap: var(--spacing-sm);

    .load-text {
      font-size: 14px;
      color: var(--text-muted);
    }
  }

  .no-more {
    @include flex-center;
    padding: var(--spacing-lg);

    .no-more-text {
      font-size: 14px;
      color: var(--text-muted);
    }
  }
}
</style>
