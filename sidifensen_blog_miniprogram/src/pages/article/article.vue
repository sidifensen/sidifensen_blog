<script setup>
import { ref, onMounted, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { marked } from 'marked'
import { useUserStore } from '@/store/user'
import { getArticleDetail, likeArticle, unlikeArticle, favoriteArticle, unfavoriteArticle } from '@/api/article'
import { getArticleComments, addComment, replyComment, deleteComment } from '@/api/comment'
import { likeComment, unlikeComment } from '@/api/like'
import { formatDate, timeAgo, formatCount } from '@/utils/format'

// 配置 marked 选项
marked.setOptions({
  gfm: true,
  breaks: true
})

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
  avatar: '',
  nickname: '',
  likeCount: 0,
  commentCount: 0,
  collectCount: 0,
  isLiked: false,
  isCollected: false,
  createTime: ''
})

// 评论列表
const comments = ref([])
const commentText = ref('')
const commentLoading = ref(false)

// 回复相关
const replyToComment = ref(null)
const replyText = ref('')
const replyLoading = ref(false)

// 加载状态
const loading = ref(false)

// 返回顶部
const showBackTop = ref(false)
const scrollTop = ref(0)

/**
 * 滚动事件处理
 */
function handleScroll(e) {
  showBackTop.value = e.detail.scrollTop > 300
}

/**
 * 返回顶部
 */
function scrollBackTop() {
  scrollTop.value = 1
  setTimeout(() => {
    scrollTop.value = 0
  }, 10)
  setTimeout(() => {
    scrollTop.value = 1
  }, 100)
}

// 文章内容 HTML（由 markdown 转换而来）
const articleContentHtml = ref('')

/**
 * 获取文章详情
 */
async function fetchArticleDetail() {
  try {
    loading.value = true
    console.log('开始获取文章详情, articleId:', articleId.value)
    const res = await getArticleDetail(articleId.value)
    console.log('文章API响应:', res)
    console.log('res.data:', res?.data)
    if (res?.data) {
      article.value = res.data
      // 将 markdown 内容转换为 HTML
      articleContentHtml.value = marked(article.value.content || '')
      console.log('设置后的article:', article.value)
    } else if (res && typeof res === 'object' && !Array.isArray(res)) {
      // 兜底：如果res本身就是要的对象
      article.value = res
      articleContentHtml.value = marked(article.value.content || '')
    } else {
      console.error('文章数据格式异常:', res)
      uni.showToast({ title: '数据格式异常', icon: 'none' })
    }
  } catch (err) {
    console.error('获取文章详情失败:', err)
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
    const res = await getArticleComments(articleId.value, { pageNum: 1, pageSize: 50 })
    comments.value = res.data?.data || []
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
    if (article.value.isCollected) {
      await unfavoriteArticle(articleId.value)
      article.value.collectCount--
    } else {
      await favoriteArticle(articleId.value)
      article.value.collectCount++
    }
    article.value.isCollected = !article.value.isCollected
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
      parentId: 0,
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
 * 显示回复输入框
 */
function showReplyInput(comment) {
  replyToComment.value = comment
  replyText.value = ''
}

/**
 * 取消回复
 */
function cancelReply() {
  replyToComment.value = null
  replyText.value = ''
}

/**
 * 提交回复
 */
async function handleSubmitReply() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  if (!replyText.value.trim()) {
    uni.showToast({ title: '请输入回复', icon: 'none' })
    return
  }

  try {
    replyLoading.value = true
    await replyComment({
      articleId: articleId.value,
      parentId: replyToComment.value.id,
      replyUserId: replyToComment.value.userId,
      content: replyText.value
    })

    replyText.value = ''
    replyToComment.value = null
    uni.showToast({ title: '回复成功', icon: 'success' })

    // 刷新评论
    fetchComments()
  } catch (err) {
    uni.showToast({ title: '回复失败', icon: 'none' })
  } finally {
    replyLoading.value = false
  }
}

/**
 * 删除评论
 */
async function handleDeleteComment(comment) {
  uni.showModal({
    title: '提示',
    content: '确定要删除这条评论吗？',
    success: async (res) => {
      if (res.confirm) {
        try {
          await deleteComment(comment.id)
          uni.showToast({ title: '删除成功', icon: 'success' })
          fetchComments()
        } catch (err) {
          uni.showToast({ title: '删除失败', icon: 'none' })
        }
      }
    }
  })
}

/**
 * 点赞评论
 */
async function handleLikeComment(comment) {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  try {
    if (comment.isLiked) {
      await unlikeComment(comment.id)
      comment.likeCount--
    } else {
      await likeComment(comment.id)
      comment.likeCount++
    }
    comment.isLiked = !comment.isLiked
  } catch (err) {
    uni.showToast({ title: '操作失败', icon: 'none' })
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

/**
 * 滚动到评论区域
 */
function scrollToComments() {
  // 使用评论数量元素作为锚点
  const commentSection = document.querySelector('.comment-section')
  if (commentSection) {
    commentSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

/**
 * 判断评论是否属于当前用户
 */
function isOwnComment(comment) {
  return userStore.userInfo?.id === comment.userId
}

onLoad((options) => {
  console.log('article page onLoad, options:', options)
  // 确保 articleId 是整数类型
  articleId.value = parseInt(options?.id, 10) || 0
  console.log('parsed articleId:', articleId.value)

  if (articleId.value) {
    console.log('开始获取文章详情...')
    fetchArticleDetail()
    fetchComments()
  } else {
    console.error('articleId 无效:', options?.id)
  }
})
</script>

<template>
  <view class="article-page">
    <!-- 文章内容 -->
    <scroll-view class="article-content" scroll-y :scroll-top="scrollTop" @scroll="handleScroll">
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
            <u-avatar :src="article.avatar" size="72" />
            <view class="author-detail">
              <view class="author-name">{{ article.nickname || '匿名' }}</view>
              <view class="publish-time">{{ timeAgo(article.createTime) }}</view>
            </view>
          </view>
        </view>
      </view>

      <!-- 文章正文 -->
      <view class="article-body">
        <rich-text :nodes="articleContentHtml"></rich-text>
      </view>

      <!-- 操作栏 -->
      <view class="action-bar">
        <view class="action-item" :class="{ active: article.isLiked }" @click="handleLike">
          <text class="action-icon">{{ article.isLiked ? '❤️' : '🤍' }}</text>
          <text class="action-text">{{ formatCount(article.likeCount) }}</text>
        </view>
        <view class="action-item" :class="{ active: article.isCollected }" @click="handleFavorite">
          <text class="action-icon">{{ article.isCollected ? '⭐' : '☆' }}</text>
          <text class="action-text">{{ formatCount(article.collectCount) }}</text>
        </view>
        <view class="action-item" @click="scrollToComments">
          <text class="action-icon">💬</text>
          <text class="action-text">{{ formatCount(article.commentCount) }}</text>
        </view>
        <view class="action-item" @click="handleShare">
          <text class="action-icon">↗️</text>
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
            <u-avatar :src="comment.avatar" size="64" />
            <view class="comment-content">
              <view class="comment-header">
                <view class="comment-user">{{ comment.nickname }}</view>
                <view class="comment-actions">
                  <view class="action-btn" @click="handleLikeComment(comment)">
                    <text class="action-icon">{{ comment.isLiked ? '❤️' : '🤍' }}</text>
                    <text class="action-text">赞 {{ formatCount(comment.likeCount || 0) }}</text>
                  </view>
                  <view class="action-btn" @click="showReplyInput(comment)">
                    <text class="action-icon">💬</text>
                    <text class="action-text">回复</text>
                  </view>
                  <view v-if="isOwnComment(comment)" class="action-btn delete" @click="handleDeleteComment(comment)">
                    <text class="action-icon">🗑️</text>
                    <text class="action-text">删除</text>
                  </view>
                </view>
              </view>
              <view class="comment-text">{{ comment.content }}</view>
              <view class="comment-footer">
                <view class="comment-time">{{ timeAgo(comment.createTime) }}</view>
                <view v-if="comment.replyCount > 0" class="reply-count">{{ comment.replyCount }} 条回复</view>
              </view>
              <!-- 回复列表 -->
              <view v-if="comment.children && comment.children.length > 0" class="reply-list">
                <view v-for="reply in comment.children" :key="reply.id" class="reply-item">
                  <u-avatar :src="reply.avatar" size="48" />
                  <view class="reply-content">
                    <view class="reply-header">
                      <view class="reply-user">{{ reply.nickname }}</view>
                      <view class="reply-actions">
                        <view class="action-btn" @click="handleLikeComment(reply)">
                          <text class="action-icon-sm">{{ reply.isLiked ? '❤️' : '🤍' }}</text>
                          <text class="action-text-sm">赞 {{ formatCount(reply.likeCount || 0) }}</text>
                        </view>
                        <view class="action-btn" @click="showReplyInput(comment, reply)">
                          <text class="action-icon-sm">💬</text>
                          <text class="action-text-sm">回复</text>
                        </view>
                      </view>
                    </view>
                    <view class="reply-text">{{ reply.content }}</view>
                    <view class="reply-time">{{ timeAgo(reply.createTime) }}</view>
                  </view>
                </view>
              </view>
            </view>
          </view>

          <view v-if="comments.length === 0" class="empty-tip">
            暂无评论，快来抢沙发
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- 返回顶部 -->
    <view v-if="showBackTop" class="back-top" @click="scrollBackTop">
      <text class="back-top-icon">↑</text>
    </view>

    <!-- 回复输入框 -->
    <view v-if="replyToComment" class="reply-input-bar">
      <view class="reply-to">回复 @{{ replyToComment.nickname }}</view>
      <view class="reply-form">
        <input
          v-model="replyText"
          class="reply-input"
          placeholder="写下你的回复..."
          confirm-type="send"
          @confirm="handleSubmitReply"
        />
        <button class="reply-btn cancel" @click="cancelReply">取消</button>
        <button class="reply-btn submit" :disabled="replyLoading" @click="handleSubmitReply">发送</button>
      </view>
    </view>

    <!-- 底部评论输入 -->
    <view v-else class="comment-input-bar">
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
  background: var(--u-bg-color);
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
      color: var(--u-main-color);
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
          color: var(--u-main-color);
        }

        .publish-time {
          font-size: 12px;
          color: var(--u-tips-color);
          margin-top: 2px;
        }
      }
    }
  }

  .article-body {
    padding: var(--spacing-xl) var(--spacing-xl) var(--spacing-xl);
    font-size: 16px;
    line-height: 1.8;
    color: var(--u-content-color);
  }

  .action-bar {
    display: flex;
    justify-content: space-around;
    padding: var(--spacing-lg);
    border-top: 1px solid var(--u-border-color);
    border-bottom: 1px solid var(--u-border-color);
    background: var(--u-bg-white);

    .action-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      color: var(--u-tips-color);
      padding: var(--spacing-sm) var(--spacing-md);
      border-radius: var(--radius-full);

      &.active {
        color: var(--u-type-primary);
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
      color: var(--u-main-color);
      margin-bottom: var(--spacing-lg);

      .comment-count {
        margin-left: var(--spacing-sm);
        font-weight: 400;
        color: var(--u-tips-color);
      }
    }

    .comment-list {
      .comment-item {
        display: flex;
        gap: var(--spacing-md);
        padding: var(--spacing-md) 0;
        border-bottom: 1px solid var(--u-border-color);

        .comment-content {
          flex: 1;

          .comment-header {
            display: flex;
            justify-content: space-between;
            align-items: center;

            .comment-user {
              font-size: 14px;
              font-weight: 500;
              color: var(--u-main-color);
              flex-shrink: 0;
              max-width: 120px;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }

            .comment-actions {
              display: flex;
              flex-wrap: nowrap;
              flex-shrink: 0;
              gap: var(--spacing-md);

              .action-btn {
                display: flex;
                align-items: center;
                gap: 4px;
                font-size: 12px;
                color: var(--u-tips-color);

                .action-icon {
                  font-size: 14px;
                }

                .action-text {
                  font-size: 12px;
                }

                &.delete {
                  color: var(--u-type-error);
                }

                .liked {
                  color: var(--u-type-primary);
                }
              }
            }
          }

          .comment-text {
            font-size: 14px;
            color: var(--u-content-color);
            margin-top: 4px;
            line-height: 1.5;
          }

          .comment-footer {
            display: flex;
            align-items: center;
            gap: var(--spacing-md);
            margin-top: 4px;

            .comment-time {
              font-size: 12px;
              color: var(--u-tips-color);
            }

            .reply-count {
              font-size: 12px;
              color: var(--u-type-primary);
            }
          }

          .reply-list {
            margin-top: var(--spacing-md);
            padding-left: var(--spacing-md);
            border-left: 2px solid var(--u-border-color);

            .reply-item {
              display: flex;
              gap: var(--spacing-sm);
              padding: var(--spacing-sm) 0;

              .reply-content {
                flex: 1;

                .reply-header {
                  display: flex;
                  justify-content: space-between;
                  align-items: center;

                  .reply-user {
                    font-size: 13px;
                    font-weight: 500;
                    color: var(--u-main-color);
                    flex-shrink: 0;
                    max-width: 100px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                  }

                  .reply-actions {
                    display: flex;
                    flex-wrap: nowrap;
                    flex-shrink: 0;
                    gap: var(--spacing-sm);

                    .action-btn {
                      display: flex;
                      align-items: center;
                      gap: 2px;
                      font-size: 11px;
                      color: var(--u-tips-color);

                      .action-icon-sm {
                        font-size: 12px;
                      }

                      .action-text-sm {
                        font-size: 11px;
                      }

                      .liked {
                        color: var(--u-type-primary);
                      }
                    }
                  }
                }

                .reply-text {
                  font-size: 13px;
                  color: var(--u-content-color);
                  margin-top: 2px;
                }

                .reply-time {
                  font-size: 11px;
                  color: var(--u-tips-color);
                  margin-top: 2px;
                }
              }
            }
          }
        }
      }

      .empty-tip {
        text-align: center;
        padding: var(--spacing-xl);
        color: var(--u-tips-color);
        font-size: 14px;
      }
    }
  }
}

.back-top {
  position: fixed;
  right: 20px;
  bottom: 100px;
  width: 44px;
  height: 44px;
  background: var(--u-bg-white);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  z-index: 100;

  .back-top-icon {
    font-size: 20px;
    color: var(--u-content-color);
  }
}

.reply-input-bar {
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--u-bg-white);
  border-top: 1px solid var(--u-border-color);

  .reply-to {
    font-size: 13px;
    color: var(--u-type-primary);
    margin-bottom: var(--spacing-sm);
  }

  .reply-form {
    display: flex;
    gap: var(--spacing-sm);

    .reply-input {
      flex: 1;
      height: 36px;
      padding: 0 var(--spacing-md);
      background: var(--u-bg-color);
      border: 1px solid var(--u-border-color);
      border-radius: var(--radius-full);
      font-size: 14px;
    }

    .reply-btn {
      padding: 0 var(--spacing-md);
      height: 36px;
      line-height: 36px;
      border: none;
      border-radius: var(--radius-full);
      font-size: 14px;

      &.cancel {
        background: var(--u-bg-color);
        color: var(--u-content-color);
      }

      &.submit {
        background: var(--u-type-primary);
        color: #ffffff;
      }

      &[disabled] {
        opacity: 0.6;
      }
    }
  }
}

.comment-input-bar {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--u-bg-white);
  border-top: 1px solid var(--u-border-color);

  .comment-input {
    flex: 1;
    height: 36px;
    padding: 0 var(--spacing-md);
    background: var(--u-bg-color);
    border: 1px solid var(--u-border-color);
    border-radius: var(--radius-full);
    font-size: 14px;
  }

  .send-btn {
    padding: 0 var(--spacing-lg);
    height: 36px;
    line-height: 36px;
    background: var(--u-type-primary);
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
