<script setup>
import { ref } from 'vue'
import { searchArticles } from '@/api/article'
import { getAlbumList } from '@/api/album'
import { getColumnList } from '@/api/column'

const searchKey = ref('')
const searchResults = ref({
  articles: [],
  albums: [],
  columns: []
})
const activeTab = ref('articles')
const loading = ref(false)
const hasSearched = ref(false)

const tabs = [
  { key: 'articles', name: '文章' },
  { key: 'albums', name: '相册' },
  { key: 'columns', name: '专栏' }
]

/**
 * 执行搜索
 */
async function handleSearch() {
  if (!searchKey.value.trim()) {
    uni.showToast({ title: '请输入关键词', icon: 'none' })
    return
  }

  try {
    loading.value = true
    hasSearched.value = true

    const keyword = searchKey.value.trim()

    const [articleRes, albumRes, columnRes] = await Promise.all([
      searchArticles({ keyword }),
      getAlbumList({ keyword }),
      getColumnList({ keyword })
    ])

    searchResults.value = {
      articles: articleRes.data || [],
      albums: albumRes.data || [],
      columns: columnRes.data || []
    }
  } catch (err) {
    uni.showToast({ title: '搜索失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 切换标签
 */
function onTabChange(tab) {
  activeTab.value = tab
}

/**
 * 跳转到文章详情
 */
function goToArticle(articleId) {
  uni.navigateTo({ url: `/pages/article/article?id=${articleId}` })
}

/**
 * 跳转到相册详情
 */
function goToAlbum(albumId) {
  uni.navigateTo({ url: `/pages/albumDetail/albumDetail?id=${albumId}` })
}

/**
 * 跳转到专栏详情
 */
function goToColumn(columnId) {
  uni.navigateTo({ url: `/pages/columnDetail/columnDetail?id=${columnId}` })
}
</script>

<template>
  <view class="search-page">
    <!-- 搜索框 -->
    <view class="search-bar">
      <uv-input
        v-model="searchKey"
        placeholder="搜索文章、相册、专栏..."
        confirm-type="search"
        @confirm="handleSearch"
      >
        <template #prefix>
          <uv-icon name="search" color="var(--text-muted)" />
        </template>
      </uv-input>
      <button class="search-btn" @click="handleSearch">搜索</button>
    </view>

    <!-- 标签切换 -->
    <view class="tabs">
      <view
        v-for="tab in tabs"
        :key="tab.key"
        class="tab-item"
        :class="{ active: activeTab === tab.key }"
        @click="onTabChange(tab.key)"
      >
        {{ tab.name }}
      </view>
    </view>

    <!-- 搜索结果 -->
    <scroll-view class="search-results" scroll-y>
      <!-- 文章结果 -->
      <view v-if="activeTab === 'articles'" class="result-list">
        <view
          v-for="article in searchResults.articles"
          :key="article.id"
          class="result-item card"
          @click="goToArticle(article.id)"
        >
          <view class="item-title">{{ article.title }}</view>
          <view class="item-summary">{{ article.summary }}</view>
        </view>

        <view v-if="searchResults.articles.length === 0 && hasSearched" class="empty-state">
          <text>未找到相关文章</text>
        </view>
      </view>

      <!-- 相册结果 -->
      <view v-if="activeTab === 'albums'" class="result-list">
        <view
          v-for="album in searchResults.albums"
          :key="album.id"
          class="result-item card"
          @click="goToAlbum(album.id)"
        >
          <view class="item-cover">
            <image :src="album.coverUrl" mode="aspectFill" />
          </view>
          <view class="item-info">
            <view class="item-title">{{ album.name }}</view>
            <view class="item-count">{{ album.photoCount }} 张照片</view>
          </view>
        </view>

        <view v-if="searchResults.albums.length === 0 && hasSearched" class="empty-state">
          <text>未找到相关相册</text>
        </view>
      </view>

      <!-- 专栏结果 -->
      <view v-if="activeTab === 'columns'" class="result-list">
        <view
          v-for="column in searchResults.columns"
          :key="column.id"
          class="result-item card"
          @click="goToColumn(column.id)"
        >
          <view class="item-title">{{ column.name }}</view>
          <view class="item-summary">{{ column.description }}</view>
        </view>

        <view v-if="searchResults.columns.length === 0 && hasSearched" class="empty-state">
          <text>未找到相关专栏</text>
        </view>
      </view>

      <!-- 初始状态 -->
      <view v-if="!hasSearched" class="empty-state">
        <text class="empty-icon">&#xe601;</text>
        <text class="empty-text">输入关键词搜索</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  background: var(--bg-page);
}

.search-bar {
  display: flex;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);

  .search-btn {
    padding: 0 var(--spacing-lg);
    height: 36px;
    line-height: 36px;
    background: var(--color-primary);
    color: #ffffff;
    border: none;
    border-radius: var(--radius-full);
    font-size: 14px;
  }
}

.tabs {
  display: flex;
  background: var(--bg-card);
  border-bottom: 1px solid var(--border);

  .tab-item {
    flex: 1;
    text-align: center;
    padding: var(--spacing-md) 0;
    font-size: 15px;
    color: var(--text-muted);

    &.active {
      color: var(--color-primary);
      font-weight: 500;
    }
  }
}

.search-results {
  height: calc(100vh - 120px);
  padding: var(--spacing-lg);
}

.result-list {
  .result-item {
    display: flex;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);

    .item-cover {
      width: 80px;
      height: 80px;
      border-radius: var(--radius-md);
      overflow: hidden;
      flex-shrink: 0;

      image {
        width: 100%;
        height: 100%;
      }
    }

    .item-info {
      flex: 1;
    }

    .item-title {
      font-size: 15px;
      font-weight: 500;
      color: var(--text-primary);
    }

    .item-summary,
    .item-count {
      font-size: 14px;
      color: var(--text-muted);
      margin-top: var(--spacing-sm);
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
</style>
