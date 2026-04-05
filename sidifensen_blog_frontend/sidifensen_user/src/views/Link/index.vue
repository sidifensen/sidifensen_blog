<template>
  <div class="link-page">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-hero">
        <div class="hero-content">
          <div class="hero-copy">
            <div class="hero-kicker">Friend Links</div>
            <h1>友情链接</h1>
            <p>收录长期维护、内容稳定更新的网站。</p>
          </div>
          <div class="hero-actions">
            <button v-if="isUserLoggedIn" class="btn-apply" @click="showApplyDialog">
              <span>申请友链</span>
              <el-icon><ArrowRight /></el-icon>
            </button>
            <button v-else class="btn-login" @click="goToLogin">
              <span>登录后申请</span>
              <el-icon><ArrowRight /></el-icon>
            </button>
          </div>
        </div>
      </div>

      <!-- 内容区域 -->
      <div class="content-layout">
        <div class="main-content">
          <!-- 加载状态 -->
          <div v-if="loading" class="link-masonry">
            <div v-for="n in 8" :key="n" class="masonry-item">
              <div class="item-inner skeleton">
                <div class="image-container">
                  <div class="skeleton-cover"></div>
                </div>
                <div class="item-footer">
                  <div class="skeleton-title"></div>
                  <div class="skeleton-text"></div>
                  <div class="skeleton-text short"></div>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div v-else-if="linkList.length === 0" class="empty-state">
            <div class="empty-icon">
              <el-icon><DocumentRemove /></el-icon>
            </div>
            <h3>暂时还没有友链</h3>
            <p>
              {{
                isUserLoggedIn
                  ? '如果你的网站适合长期展示，可以成为这里的第一条友链。'
                  : '登录后可以提交你的网站，我们会在审核通过后展示。'
              }}
            </p>
            <button v-if="isUserLoggedIn" class="btn-apply" @click="showApplyDialog">
              申请友链
            </button>
            <button v-else class="btn-login" @click="goToLogin">前往登录</button>
          </div>

          <!-- 瀑布流列表 -->
          <div v-else class="link-masonry">
            <div
              v-for="(link, index) in linkList"
              :key="link.id"
              class="masonry-item"
              :style="{ animationDelay: `${index * 0.05}s` }"
            >
              <div class="item-inner" @click="visitLink(link)">
                <!-- 封面图 -->
                <div class="image-container">
                  <img
                    v-if="link.coverUrl"
                    :src="link.coverUrl"
                    class="link-cover"
                    alt="封面"
                    @load="onImageLoad(link.id)"
                    @error="onImageError(link.id)"
                  />
                  <div v-else class="placeholder">
                    <el-icon><Picture /></el-icon>
                  </div>
                  <div v-if="imageLoading[link.id]" class="loading-overlay">
                    <el-icon class="spinner"><Loading /></el-icon>
                  </div>
                </div>

                <!-- 内容区 -->
                <div class="item-footer">
                  <div class="link-header">
                    <div class="link-name">{{ link.name }}</div>
                    <div
                      v-if="isCurrentUserLink(link)"
                      class="link-delete"
                      @click.stop="handleDeleteLink(link)"
                    >
                      <el-tooltip content="删除友链" placement="top">
                        <el-icon><Delete /></el-icon>
                      </el-tooltip>
                    </div>
                  </div>
                  <div class="link-domain">{{ formatUrl(link.url) }}</div>
                  <div class="link-description">{{ link.description }}</div>
                  <div v-if="isCurrentUserLink(link)" class="link-owner">我的友链</div>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载更多 -->
          <div v-if="loadingMore && linkList.length > 0" class="loading-more">
            <span>加载更多...</span>
          </div>

          <!-- 没有更多 -->
          <div v-if="!hasMore && linkList.length > 0 && !loading" class="no-more">
            <span>没有更多了</span>
          </div>
        </div>
      </div>
    </div>

    <LinkApplyDialog v-model="applyDialogVisible" @success="handleApplySuccess" />

    <el-backtop :right="30" :bottom="30" />
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { deleteLink, getLinkList } from '@/api/link'
import LinkApplyDialog from '@/views/Link/LinkApplyDialog.vue'
import { useUserStore } from '@/stores/userStore'
import { useSeoMeta } from '@/plugins/seo'

// SEO - 友链
useSeoMeta({
  title: '友链',
  description: '交换友链，结识志同道合的开发者朋友',
  keywords: '友链,交换链接,开发者,博客,网站导航',
})

const router = useRouter()
const userStore = useUserStore()

const loading = ref(false)
const loadingMore = ref(false)
const applyDialogVisible = ref(false)

const currentPage = ref(1)
const pageSize = ref(24)
const total = ref(0)
const hasMore = ref(true)

const linkList = ref([])
const deletingLinkId = ref(null)
const imageLoading = ref({})

const onImageLoad = (linkId) => {
  imageLoading.value[linkId] = false
}

const onImageError = (linkId) => {
  imageLoading.value[linkId] = false
}

const isUserLoggedIn = computed(() => {
  return userStore.user && userStore.user.id
})

const totalDisplay = computed(() => {
  return total.value || linkList.value.length
})

const isCurrentUserLink = (link) => {
  return userStore.user && userStore.user.id === link.userId
}

const fetchLinkList = async (reset = false) => {
  if (loadingMore.value || (!hasMore.value && !reset)) {
    return
  }

  try {
    if (reset) {
      loading.value = true
      currentPage.value = 1
      hasMore.value = true
    } else {
      loadingMore.value = true
    }

    const res = await getLinkList(currentPage.value, pageSize.value)
    const newLinks = res.data?.data || []
    const totalCount = res.data?.total || 0

    if (reset) {
      linkList.value = newLinks
    } else {
      linkList.value = [...linkList.value, ...newLinks]
    }

    total.value = totalCount
    hasMore.value = linkList.value.length < total.value

    if (hasMore.value && newLinks.length > 0) {
      currentPage.value++
    }
  } catch (error) {
    // 静默处理
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

const handleScroll = () => {
  if (loading.value || loadingMore.value || !hasMore.value) {
    return
  }

  const { scrollTop, scrollHeight, clientHeight } = document.documentElement
  if (scrollTop + clientHeight >= scrollHeight - 200) {
    fetchLinkList()
  }
}

const showApplyDialog = () => {
  applyDialogVisible.value = true
}

const goToLogin = () => {
  router.push('/login')
}

const handleApplySuccess = () => {
  fetchLinkList(true)
}

const visitLink = (link) => {
  window.open(link.url, '_blank', 'noopener,noreferrer')
}

const formatUrl = (url) => {
  try {
    const urlObj = new URL(url)
    return urlObj.hostname
  } catch {
    return url
  }
}

const handleDeleteLink = async (link) => {
  try {
    await ElMessageBox.confirm(`确定要删除友链 "${link.name}" 吗？此操作不可恢复。`, '确认删除', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      confirmButtonClass: 'el-button--danger',
      lockScroll: false,
    })

    deletingLinkId.value = link.id
    await deleteLink(link.id)

    linkList.value = linkList.value.filter((item) => item.id !== link.id)
    total.value = Math.max(0, total.value - 1)
    hasMore.value = linkList.value.length < total.value

    ElMessage.success('友链删除成功')
  } catch (error) {
    if (error !== 'cancel') {
      // 静默处理
    }
  } finally {
    deletingLinkId.value = null
  }
}

onMounted(() => {
  fetchLinkList(true)
  window.addEventListener('scroll', handleScroll)
})

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style lang="scss" scoped>
// 友链页面 - 现代极简科技风（匹配相册广场风格）

*,
*::before,
*::after {
  box-sizing: border-box;
}

.link-page {
  width: 100%;
  min-height: 100vh;
  background: #f8fafc;
  overflow-x: hidden;

  html.dark & {
    background: #000000;
  }
}

.container {
  // ===== CSS 变量定义 - 浅色模式 =====
  --bg-page: #f8fafc;
  --bg-hero-divider: rgba(0, 0, 0, 0.08);
  --bg-metric: rgba(0, 0, 0, 0.02);
  --bg-subtle: rgba(0, 0, 0, 0.02);
  --bg-card: rgba(0, 0, 0, 0.015);
  --bg-hover: rgba(0, 0, 0, 0.01);

  --text-primary: #0a0a0a;
  --text-secondary: #888888;
  --text-regular: #666666;
  --text-muted: #aaaaaa;

  --border-light: rgba(0, 0, 0, 0.05);
  --border-regular: rgba(0, 0, 0, 0.08);
  --border-hover: rgba(0, 0, 0, 0.15);

  --accent-color: #0066ff;
  --accent-soft: rgba(0, 102, 255, 0.04);
  --accent-border: rgba(0, 102, 255, 0.15);
  --accent-hover: rgba(0, 102, 255, 0.08);

  // ===== 黑夜模式覆盖 =====
  html.dark & {
    --bg-page: #000000;
    --bg-hero-divider: rgba(255, 255, 255, 0.1);
    --bg-metric: rgba(255, 255, 255, 0.03);
    --bg-subtle: rgba(255, 255, 255, 0.02);
    --bg-card: rgba(255, 255, 255, 0.02);
    --bg-hover: rgba(255, 255, 255, 0.02);

    --text-primary: #ffffff;
    --text-secondary: #888888;
    --text-regular: #dddddd;
    --text-muted: #666666;

    --border-light: rgba(255, 255, 255, 0.05);
    --border-regular: rgba(255, 255, 255, 0.08);
    --border-hover: rgba(255, 255, 255, 0.15);

    --accent-color: #00d4ff;
    --accent-soft: rgba(0, 212, 255, 0.05);
    --accent-border: rgba(0, 212, 255, 0.15);
    --accent-hover: rgba(0, 212, 255, 0.08);
  }

  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 100vh;
  background: var(--bg-page);
  font-family:
    -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  line-height: 1.6;

  // 手机端适配
  @media (max-width: 768px) {
    padding: 0 20px;
    box-sizing: border-box;
    width: 100%;
    max-width: 100vw;
  }

  // ===== 页面头部 - 极致简约 =====
  .page-hero {
    padding: 60px 0 40px;
    border-bottom: 1px solid var(--bg-hero-divider);

    @media (max-width: 768px) {
      padding: 40px 0 25px;
    }

    .hero-content {
      display: flex;
      justify-content: space-between;
      align-items: flex-end;
      gap: 24px;

      @media (max-width: 768px) {
        justify-content: flex-start;
        gap: 8px;
      }

      .hero-copy {
        max-width: 500px;

        .hero-kicker {
          font-size: 11px;
          font-weight: 600;
          letter-spacing: 0.2em;
          text-transform: uppercase;
          color: var(--accent-color);
          margin-bottom: 12px;
          display: flex;
          align-items: center;
          gap: 8px;

          &::before {
            content: '';
            width: 20px;
            height: 1px;
            background: linear-gradient(90deg, var(--accent-color), transparent);
          }
        }

        h1 {
          font-size: 40px;
          font-weight: 700;
          letter-spacing: -0.04em;
          margin-bottom: 12px;
          color: var(--text-primary);

          @media (max-width: 768px) {
            font-size: 28px;
          }
        }

        p {
          font-size: 14px;
          color: var(--text-secondary);
          line-height: 1.7;
        }
      }

      .hero-actions {
        .btn-apply,
        .btn-login {
          display: flex;
          align-items: center;
          gap: 8px;
          padding: 10px 20px;
          background: var(--accent-soft);
          border: 1px solid var(--accent-border);
          border-radius: 8px;
          color: var(--accent-color);
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            background: var(--accent-hover);
            border-color: var(--accent-color);
          }

          .el-icon {
            font-size: 16px;
          }
        }
      }
    }
  }

  // ===== 内容布局 =====
  .content-layout {
    padding: 40px 0 60px;

    @media (max-width: 768px) {
      padding: 30px 0 50px;
    }

    // ===== 主内容区 =====
    .main-content {
      width: 100%;
      max-width: 100%;

      // ===== 空状态 =====
      .empty-state {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 80px 24px;
        text-align: center;

        .empty-icon {
          width: 80px;
          height: 80px;
          border-radius: 20px;
          background: var(--bg-metric);
          display: flex;
          justify-content: center;
          align-items: center;
          margin-bottom: 24px;

          .el-icon {
            font-size: 36px;
            color: var(--text-muted);
          }
        }

        h3 {
          font-size: 24px;
          font-weight: 600;
          color: var(--text-primary);
          margin: 0 0 12px;
        }

        p {
          font-size: 14px;
          color: var(--text-secondary);
          max-width: 400px;
          margin: 0 0 24px;
        }

        // 空状态按钮样式
        .btn-apply,
        .btn-login {
          display: inline-flex;
          align-items: center;
          gap: 8px;
          padding: 10px 20px;
          background: var(--accent-soft);
          border: 1px solid var(--accent-border);
          border-radius: 8px;
          color: var(--accent-color);
          font-size: 14px;
          font-weight: 500;
          cursor: pointer;
          transition: all 0.2s ease;

          &:hover {
            background: var(--accent-hover);
            border-color: var(--accent-color);
          }
        }
      }

      // ===== 骨架屏 =====
      .skeleton {
        .skeleton-cover {
          width: 100%;
          height: 180px;
          background: var(--bg-metric);
        }

        .skeleton-title {
          width: 60%;
          height: 20px;
          background: var(--bg-metric);
          border-radius: 4px;
          margin-bottom: 12px;
        }

        .skeleton-text {
          width: 100%;
          height: 14px;
          background: var(--bg-metric);
          border-radius: 4px;
          margin-bottom: 8px;

          &.short {
            width: 40%;
          }
        }
      }

      // ===== 瀑布流布局 =====
      .link-masonry {
        column-count: 4;
        column-gap: 16px;

        @media (max-width: 1200px) {
          column-count: 3;
        }

        @media (max-width: 900px) {
          column-count: 2;
        }

        @media (max-width: 600px) {
          column-count: 2;
          column-gap: 12px;
          padding: 0;
        }

        .masonry-item {
          break-inside: avoid;
          margin-bottom: 16px;
          opacity: 0;
          animation: fadeInUp 0.5s ease forwards;

          @media (max-width: 600px) {
            margin-bottom: 12px;
          }

          .item-inner {
            background: transparent;
            border: 1px solid var(--border-regular);
            border-radius: 6px;
            overflow: hidden;
            cursor: pointer;
            transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;

            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              height: 1px;
              background: linear-gradient(90deg, var(--accent-color), transparent);
              opacity: 0;
              transition: opacity 0.25s ease;
              z-index: 1;
            }

            &:hover::before {
              opacity: 1;
            }

            &:hover {
              border-color: var(--border-hover);
              background: var(--bg-hover);
            }

            html.dark & {
              &:hover {
                background: rgba(255, 255, 255, 0.02);
                border-color: rgba(255, 255, 255, 0.15);
              }
            }

            .image-container {
              position: relative;
              overflow: hidden;
              height: 180px;
              background: var(--bg-metric);

              .link-cover {
                width: 100%;
                height: 180px;
                object-fit: cover;
                display: block;
                transition: transform 0.6s cubic-bezier(0.4, 0, 0.2, 1);
              }

              .placeholder {
                width: 100%;
                height: 180px;
                display: flex;
                justify-content: center;
                align-items: center;
                background: var(--bg-metric);

                .el-icon {
                  font-size: 40px;
                  color: var(--text-muted);
                }
              }

              .loading-overlay {
                position: absolute;
                top: 0;
                left: 0;
                right: 0;
                bottom: 0;
                display: flex;
                justify-content: center;
                align-items: center;
                background: var(--bg-metric);

                .spinner {
                  animation: rotate 1.5s linear infinite;
                  font-size: 28px;
                  color: var(--text-muted);
                }
              }
            }

            .item-footer {
              padding: 16px;

              .link-header {
                display: flex;
                align-items: flex-start;
                justify-content: space-between;
                gap: 8px;
                margin-bottom: 6px;

                .link-name {
                  font-size: 15px;
                  font-weight: 600;
                  color: var(--text-primary);
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                  flex: 1;
                }

                .link-delete {
                  color: var(--text-muted);
                  cursor: pointer;
                  padding: 4px;
                  border-radius: 4px;
                  transition: all 0.2s ease;

                  &:hover {
                    color: #ff4d4f;
                    background: rgba(255, 77, 79, 0.1);
                  }
                }
              }

              .link-domain {
                font-size: 12px;
                color: var(--text-muted);
                margin-bottom: 10px;
              }

              .link-description {
                font-size: 13px;
                color: var(--text-regular);
                line-height: 1.6;
                display: -webkit-box;
                overflow: hidden;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 2;
              }

              .link-owner {
                display: inline-flex;
                align-items: center;
                margin-top: 12px;
                padding: 4px 10px;
                border-radius: 999px;
                background: var(--bg-subtle);
                border: 1px solid var(--border-light);
                color: var(--text-secondary);
                font-size: 12px;
              }
            }
          }
        }
      }

      // ===== 加载更多 =====
      .loading-more,
      .no-more {
        display: flex;
        justify-content: center;
        padding: 30px 0 10px;
        font-size: 13px;
        color: var(--text-muted);
      }
    }
  }
}

// ===== 动画 =====
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}
</style>
