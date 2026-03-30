<script setup>
import { ref, onMounted } from 'vue'
import { searchArticles } from '@/api/article'
import { searchAlbums } from '@/api/album'
import { searchColumns } from '@/api/column'
import { formatCount } from '@/utils/format'

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
      searchArticles({ title: keyword }),
      searchAlbums({ keyword }),
      searchColumns({ keyword })
    ])

    // 提取分页数据（后端返回 PageVo<PageVo<T>> 结构）
    const articleList = articleRes.data?.data || articleRes.data || []
    const columnList = columnRes.data?.data || columnRes.data || []
    const albumList = albumRes.data || albumRes || []

    searchResults.value = {
      articles: articleList,
      albums: albumList,
      columns: columnList
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

// 初始化主题
onMounted(() => {
})
</script>

<template>
  <view class="search-page">
    <!-- 搜索框 -->
    <view class="search-bar">
      <view class="search-box" @click="focusInput = true">
        <text class="search-icon">🔍</text>
        <input
          v-model="searchKey"
          class="search-input"
          type="text"
          placeholder="搜索文章、相册、专栏..."
          confirm-type="search"
          @confirm="handleSearch"
        />
      </view>
      <view class="search-btn" @click="handleSearch">
        <u-icon name="search" size="20px" color="#ffffff" />
      </view>
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
          class="result-item"
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
          class="result-item"
          @click="goToAlbum(album.id)"
        >
          <view class="item-cover">
            <image :src="album.coverUrl" mode="aspectFill" />
          </view>
          <view class="item-info">
            <view class="item-title">{{ album.name }}</view>
            <view class="item-count">{{ formatCount(album.photoCount) }} 张照片</view>
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
          class="result-item"
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
        <u-icon name="search" size="48px" color="var(--text-muted)" />
        <text class="empty-text">输入关键词搜索</text>
      </view>
    </scroll-view>
  </view>
</template>

<style lang="scss" scoped>
.search-page {
  min-height: 100vh;
  background: var(--u-bg-color);
}

.search-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);

  .search-box {
    flex: 1;
    display: flex;
    align-items: center;
    gap: var(--spacing-sm);
    padding: var(--spacing-sm) var(--spacing-md);
    background: var(--u-bg-color);
    border: 1px solid var(--u-border-color);
    border-radius: var(--radius-full);

    .search-icon {
      font-size: 16px;
      color: var(--u-tips-color);
    }

    .search-input {
      flex: 1;
      height: 32px;
      font-size: 14px;
      color: var(--u-main-color);
      background: transparent;

      &::placeholder {
        color: var(--u-tips-color);
      }
    }
  }

  .search-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 36px;
    height: 36px;
    background: var(--u-type-primary);
    border: none;
    border-radius: 50%;
  }
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

.search-results {
  height: calc(100vh - 120px);
  padding: var(--spacing-lg);
  box-sizing: border-box;
}

.result-list {
  .result-item {
    display: flex;
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
    padding: var(--spacing-md);
    background: var(--u-bg-white);
    border: 1px solid var(--u-border-color);
    border-radius: var(--radius-md);
    box-sizing: border-box;
    overflow: hidden;

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
      min-width: 0;
    }

    .item-title {
      font-size: 15px;
      font-weight: 500;
      color: var(--u-main-color);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .item-summary,
    .item-count {
      font-size: 14px;
      color: var(--u-tips-color);
      margin-top: var(--spacing-sm);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
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

  .empty-text {
    font-size: 14px;
  }
}
</style>
