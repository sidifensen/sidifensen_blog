<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getArticleDetail, likeArticle, unlikeArticle, favoriteArticle, unfavoriteArticle } from '@/api/article'
import { getArticleComments, addComment } from '@/api/comment'
import { formatDate, timeAgo } from '@/utils/format'

// 获取页面参数
const articleId = ref('')
const userStore = useUserStore()

// 文章详情
const article = ref({
  id: '',
  title: '',
  content: '',
  summary: '',
  coverImage: '',
  author: {},
  likeCount: 0,
  commentCount: 0,
  favoriteCount: 0,
  isLiked: false,
  isFavorited: false,
  createTime: ''
})

// 评论列表
const comments = ref([])
const commentText = ref('')
const commentLoading = ref(false)

// 加载状态
const loading = ref(false)

/**
 * 获取文章详情
 */
async function fetchArticleDetail() {
  try {
    loading.value = true
    const res = await getArticleDetail(articleId.value)
    article.value = res.data || res
  } catch (err) {
    uni.showToast({ title: '加载失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

/**
 * 获取评论列表
 */
async function fetchComments() {
  try {
    const res = await getArticleComments(articleId.value, { pageNum: 1, pageSize: 20 })
    comments.value = res.data || res
  } catch (err) {
    console.error('获取评论失败', err)
  }
}

/**
 * 点赞/取消点赞
 */
async function handleLike() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  try {
    if (article.value.isLiked) {
      await unlikeArticle(articleId.value)
      article.value.likeCount--
    } else {
      await likeArticle(articleId.value)
      article.value.likeCount++
    }
    article.value.isLiked = !article.value.isLiked
  } catch (err) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

/**
 * 收藏/取消收藏
 */
async function handleFavorite() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  try {
    if (article.value.isFavorited) {
      await unfavoriteArticle(articleId.value)
      article.value.favoriteCount--
    } else {
      await favoriteArticle(articleId.value)
      article.value.favoriteCount++
    }
    article.value.isFavorited = !article.value.isFavorited
  } catch (err) {
    uni.showToast({ title: '操作失败', icon: 'none' })
  }
}

/**
 * 发布评论
 */
async function handlePublishComment() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  if (!commentText.value.trim()) {
    uni.showToast({ title: '请输入评论', icon: 'none' })
    return
  }

  try {
    commentLoading.value = true
    await addComment({
      articleId: articleId.value,
      content: commentText.value
    })

    commentText.value = ''
    uni.showToast({ title: '评论成功', icon: 'success' })

    // 刷新评论
    fetchComments()
  } catch (err) {
    uni.showToast({ title: '评论失败', icon: 'none' })
  } finally {
    commentLoading.value = false
  }
}

/**
 * 分享文章
 */
function handleShare() {
  uni.showShareMenu({
    withShareTicket: true,
    menus: ['shareAppMessage', 'shareTimeline']
  })
}

onMounted(() => {
  const pages = getCurrentPages()
  const currentPage = pages[pages.length - 1]
  articleId.value = currentPage.options?.id || ''

  if (articleId.value) {
    fetchArticleDetail()
    fetchComments()
  }
})
</script>

<template>
  <view class="article-page">
    <!-- 文章内容 -->
    <scroll-view class="article-content" scroll-y>
      <!-- 封面图 -->
      <image
        v-if="article.coverUrl"
        class="cover-image"
        :src="article.coverUrl"
        mode="widthFix"
      />

      <!-- 文章信息 -->
      <view class="article-header">
        <view class="article-title">{{ article.title }}</view>
        <view class="article-meta">
          <view class="author-info">
            <uv-avatar :src="article.author?.avatar" size="36px" />
            <view class="author-detail">
              <view class="author-name">{{ article.author?.nickname || '匿名' }}</view>
              <view class="publish-time">{{ timeAgo(article.createTime) }}</view>
            </view>
          </view>
        </view>
      </view>

      <!-- 文章正文 -->
      <view class="article-body">
        <rich-text :nodes="article.content"></rich-text>
      </view>

      <!-- 操作栏 -->
      <view class="action-bar">
        <view class="action-item" :class="{ active: article.isLiked }" @click="handleLike">
          <text class="action-icon">&#xe8f8;</text>
          <text class="action-text">{{ article.likeCount || 0 }}</text>
        </view>
        <view class="action-item" :class="{ active: article.isFavorited }" @click="handleFavorite">
          <text class="action-icon">&#xe8fa;</text>
          <text class="action-text">{{ article.favoriteCount || 0 }}</text>
        </view>
        <view class="action-item" @click="handleShare">
          <text class="action-icon">&#xe8fb;</text>
          <text class="action-text">分享</text>
        </view>
      </view>

      <!-- 评论区域 -->
      <view class="comment-section">
        <view class="section-title">
          评论
          <text class="comment-count">{{ comments.length }}</text>
        </view>

        <!-- 评论列表 -->
        <view class="comment-list">
          <view v-for="comment in comments" :key="comment.id" class="comment-item">
            <uv-avatar :src="comment.userAvatar" size="32px" />
            <view class="comment-content">
              <view class="comment-user">{{ comment.userName }}</view>
              <view class="comment-text">{{ comment.content }}</view>
              <view class="comment-time">{{ timeAgo(comment.createTime) }}</view>
            </view>
          </view>

          <view v-if="comments.length === 0" class="empty-tip">
            暂无评论，快来抢沙发
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 底部评论输入 -->
    <view class="comment-input-bar">
      <input
        v-model="commentText"
        class="comment-input"
        placeholder="写评论..."
        confirm-type="send"
        @confirm="handlePublishComment"
      />
      <button class="send-btn" :disabled="commentLoading" @click="handlePublishComment">
        发送
      </button>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.article-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--bg-page);
}

.article-content {
  flex: 1;

  .cover-image {
    width: 100%;
  }

  .article-header {
    padding: var(--spacing-lg);

    .article-title {
      font-size: 22px;
      font-weight: 700;
      color: var(--text-primary);
      line-height: 1.4;
      margin-bottom: var(--spacing-lg);
    }

    .article-meta {
      .author-info {
        display: flex;
        align-items: center;
        gap: var(--spacing-md);

        .author-name {
          font-size: 14px;
          font-weight: 500;
          color: var(--text-primary);
        }

        .publish-time {
          font-size: 12px;
          color: var(--text-muted);
          margin-top: 2px;
        }
      }
    }
  }

  .article-body {
    padding: 0 var(--spacing-lg) var(--spacing-lg);
    font-size: 16px;
    line-height: 1.8;
    color: var(--text-regular);
  }

  .action-bar {
    display: flex;
    justify-content: space-around;
    padding: var(--spacing-lg);
    border-top: 1px solid var(--border);
    border-bottom: 1px solid var(--border);
    background: var(--bg-card);

    .action-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      color: var(--text-muted);
      padding: var(--spacing-sm) var(--spacing-md);
      border-radius: var(--radius-full);

      &.active {
        color: var(--color-primary);
      }

      .action-icon {
        font-size: 18px;
      }

      .action-text {
        font-size: 14px;
      }
    }
  }

  .comment-section {
    padding: var(--spacing-lg);

    .section-title {
      font-size: 16px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: var(--spacing-lg);

      .comment-count {
        margin-left: var(--spacing-sm);
        font-weight: 400;
        color: var(--text-muted);
      }
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: var(--spacing-md);
        padding: var(--spacing-md) 0;
        border-bottom: 1px solid var(--border);

        .comment-content {
          flex: 1;

          .comment-user {
            font-size: 14px;
            font-weight: 500;
            color: var(--text-primary);
          }

          .comment-text {
            font-size: 14px;
            color: var(--text-regular);
            margin-top: 4px;
            line-height: 1.5;
          }

          .comment-time {
            font-size: 12px;
            color: var(--text-muted);
            margin-top: 4px;
          }
        }
      }

      .empty-tip {
        text-align: center;
        padding: var(--spacing-xl);
        color: var(--text-muted);
        font-size: 14px;
      }
    }
  }
}

.comment-input-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--bg-card);
  border-top: 1px solid var(--border);

  .comment-input {
    flex: 1;
    height: 36px;
    padding: 0 var(--spacing-md);
    background: var(--bg-page);
    border: 1px solid var(--border);
    border-radius: var(--radius-full);
    font-size: 14px;
  }

  .send-btn {
    padding: 0 var(--spacing-lg);
    height: 36px;
    line-height: 36px;
    background: var(--color-primary);
    color: #ffffff;
    border: none;
    border-radius: var(--radius-full);
    font-size: 14px;

    &[disabled] {
      opacity: 0.6;
    }
  }
}
</style>
