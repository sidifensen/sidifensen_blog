<script setup>
import { ref, onMounted } from 'vue'
import { getAlbumDetail } from '@/api/album'

const albumId = ref('')
const album = ref({})
const photos = ref([])
const loading = ref(false)
const currentIndex = ref(0)

/**
 * 获取相册详情（已包含照片列表）
 */
async function fetchAlbumDetail() {
  try {
    loading.value = true
    const res = await getAlbumDetail(albumId.value)
    const data = res.data || res
    album.value = data
    photos.value = data.photos || []
  } catch (err) {
    console.error('获取相册详情失败', err)
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 预览照片
 */
function previewPhoto(index) {
  currentIndex.value = index
  uni.previewImage({
    current: index,
    urls: photos.value.map(p => p.url)
  })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  albumId.value = currentPage.options?.id || ''

  if (albumId.value) {
    fetchAlbumDetail()
  }
})
</script>

<template>
  <view class="album-detail-page">
    <!-- 相册信息 -->
    <view class="album-header">
      <view class="album-title">{{ album.name }}</view>
      <view class="album-desc">{{ album.description || '暂无描述' }}</view>
      <view class="album-stats">
        <text>{{ photos.length }} 张照片</text>
      </view>
    </view>

    <!-- 照片网格 -->
    <view class="photo-grid">
      <view
        v-for="(photo, index) in photos"
        :key="photo.id"
        class="photo-item"
        @click="previewPhoto(index)"
      >
        <image :src="photo.url" mode="aspectFill" />
      </view>
    </view>

    <!-- 空状态 -->
    <view v-if="photos.length === 0 && !loading" class="empty-state">
      <text class="empty-icon">&#xe601;</text>
      <text class="empty-text">暂无照片</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.album-detail-page {
  min-height: 100vh;
  background: var(--u-bg-color);
}

.album-header {
  padding: var(--spacing-lg);
  background: var(--u-bg-white);
  border-bottom: 1px solid var(--u-border-color);

  .album-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--u-main-color);
  }

  .album-desc {
    font-size: 14px;
    color: var(--u-tips-color);
    margin-top: var(--spacing-sm);
  }

  .album-stats {
    font-size: 12px;
    color: var(--u-tips-color);
    margin-top: var(--spacing-md);
  }
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2px;
  padding: 2px;

  .photo-item {
    aspect-ratio: 1;
    overflow: hidden;

    image {
      width: 100%;
      height: 100%;
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
</style>
