<script setup>
import { ref, onMounted } from 'vue'
import { getColumnDetail, getColumnArticles } from '@/api/column'

const columnId = ref('')
const column = ref({})
const articles = ref([])
const loading = ref(false)

async function fetchColumnDetail() {
  try {
    loading.value = true
    const res = await getColumnDetail(columnId.value)
    column.value = res.data || res
  } catch (err) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

async function fetchArticles() {
  try {
    const res = await getColumnArticles(columnId.value, { pageNum: 1, pageSize: 50 })
    articles.value = res.data || res
  } catch (err) {
    console.error('获取文章失败', err)
  }
}

function goToArticle(articleId) {
  uni.navigateTo({ url: `/pages/article/article?id=${articleId}` })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  columnId.value = currentPage.options?.id || ''
  if (columnId.value) {
    fetchColumnDetail()
    fetchArticles()
  }
})
</script>

<template>
  <view class="column-detail-page">
    <view class="column-header">
      <image class="column-cover" :src="column.coverUrl" mode="aspectFill" />
      <view class="column-info">
        <view class="column-name">{{ column.name }}</view>
        <view class="column-desc">{{ column.description }}</view>
      </view>
    </view>

    <view class="article-list">
      <view
        v-for="article in articles"
        :key="article.id"
        class="article-item card"
        @click="goToArticle(article.id)"
      >
        <view class="article-title">{{ article.title }}</view>
        <view class="article-summary">{{ article.summary }}</view>
      </view>

      <view v-if="articles.length === 0" class="empty-state">
        <text class="empty-text">暂无文章</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.column-detail-page {
  min-height: 100vh;
  background: var(--bg-page);
}

.column-header {
  background: var(--bg-card);
  padding: var(--spacing-lg);

  .column-cover {
    width: 100%;
    height: 180px;
    border-radius: var(--radius-md);
  }

  .column-name {
    font-size: 20px;
    font-weight: 600;
    color: var(--text-primary);
    margin-top: var(--spacing-md);
  }

  .column-desc {
    font-size: 14px;
    color: var(--text-muted);
    margin-top: var(--spacing-sm);
  }
}

.article-list {
  padding: var(--spacing-lg);

  .article-item {
    margin-bottom: var(--spacing-md);

    .article-title {
      font-size: 16px;
      font-weight: 500;
      color: var(--text-primary);
    }

    .article-summary {
      font-size: 14px;
      color: var(--text-muted);
      margin-top: var(--spacing-sm);
      @include text-ellipsis(2);
    }
  }
}

.empty-state {
  @include flex-center-column;
  padding: var(--spacing-2xl);
  color: var(--text-muted);
}
</style>
