<template>
  <div class="article-detail">
    <!-- 三栏布局容器 -->
    <div class="article-container">
      <!-- 左侧用户信息 -->
      <div class="left-sidebar">
        <UserInfoCard v-if="userInfo" :user-info="userInfo" :loading="userLoading" />
      </div>

      <!-- 中间文章内容 -->
      <div class="main-content">
        <div v-if="accessState === 'vip'" class="access-panel vip-panel">
          <span class="panel-kicker">会员文章</span>
          <h2 class="panel-title">这篇文章仅对 VIP 开放</h2>
          <p class="panel-description">
            支付成功后，账号会自动获得 VIP 身份，可以直接查看当前文章和会员专区内容。
          </p>
          <div class="panel-actions">
            <button class="primary-button" @click="goToVipCenter">去开通 VIP</button>
            <button class="secondary-button" @click="goToVipArticles">会员专区</button>
          </div>
        </div>
        <div v-else-if="accessState" class="access-panel denied-panel">
          <span class="panel-kicker">访问受限</span>
          <h2 class="panel-title">{{ accessMessage }}</h2>
          <p class="panel-description">当前账号没有这篇文章的查看权限。</p>
        </div>
        <ArticleContent
          v-else
          :article="articleInfo"
          :loading="articleLoading"
          :user-info="userInfo"
          :user-loading="userLoading"
          @updateArticle="handleUpdateArticle"
        />
      </div>

      <!-- 右侧文章目录 -->
      <div class="right-sidebar">
        <ArticleCatalog
          v-if="!accessState && articleInfo && articleInfo.content"
          :content="articleInfo.content"
        />
      </div>
    </div>

    <!-- 文章底部操作栏 - 移到这里避免受 overflow 影响 -->
    <ArticleActions
      v-if="articleInfo"
      :article="articleInfo"
      @updateArticle="handleUpdateArticle"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticleDetail } from '@/api/article'
import { getUserInfoById } from '@/api/user'
import UserInfoCard from './components/UserInfoCard.vue'
import ArticleContent from './components/ArticleContent.vue'
import ArticleCatalog from './components/ArticleCatalog.vue'
import ArticleActions from './components/ArticleActions.vue'
import { useSeoMeta } from '@/plugins/seo'

// 路由参数
const route = useRoute()
const router = useRouter()
const userId = route.params.userId
const articleId = route.params.articleId

// 调试：打印路由参数
// 防御性检查：确保参数存在
if (!articleId) {
  // 静默处理
}

// 响应式数据
const userInfo = ref(null)
const articleInfo = ref(null)
const userLoading = ref(false)
const articleLoading = ref(false)
const accessState = ref('')
const accessMessage = ref('')

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    userLoading.value = true
    const response = await getUserInfoById(userId)
    userInfo.value = response.data
  } catch (error) {
    // 静默处理
    ElMessage.error('获取用户信息失败')
  } finally {
    userLoading.value = false
  }
}

// 获取文章详情
const fetchArticleDetail = async () => {
  try {
    articleLoading.value = true
    accessState.value = ''
    accessMessage.value = ''
    const response = await getArticleDetail(articleId)
    articleInfo.value = response.data
    // 注意：阅读量统计已集成到后端获取文章详情接口中，会自动异步统计，无需前端单独调用
  } catch (error) {
    // 静默处理
    articleInfo.value = null
    const message = error?.msg || error?.message || '获取文章详情失败'
    accessMessage.value = message
    if (message === '该文章仅VIP可见') {
      accessState.value = 'vip'
      return
    }
    if (['该文章仅粉丝可见', '该文章仅作者本人可见'].includes(message)) {
      accessState.value = 'denied'
      return
    }
    ElMessage.error(message)
  } finally {
    articleLoading.value = false
  }
}

// 处理文章信息更新
const handleUpdateArticle = (updatedArticle) => {
  articleInfo.value = updatedArticle
}

const goToVipCenter = () => {
  router.push('/vip')
}

const goToVipArticles = () => {
  router.push('/vip/articles')
}

// SEO - 文章详情（动态）
watch(articleInfo, (article) => {
  if (article) {
    const baseUrl = window.location.origin
    useSeoMeta({
      title: article.title,
      description:
        article.summary ||
        article.content?.replace(/<[^>]+>/g, '').slice(0, 150) ||
        '斯蒂芬森社区文章',
      keywords: article.tags?.join(',') || '技术文章,博客',
      image: article.coverImage,
      url: `${baseUrl}/user/${userId}/article/${articleId}`,
    })
  }
})

// 页面初始化
onMounted(async () => {
  // 并行获取用户信息和文章详情
  await Promise.all([fetchUserInfo(), fetchArticleDetail()])
})
</script>

<style lang="scss" scoped>
// 文章详情页面主容器
.article-detail {
  height: 100%;
  background-color: var(--el-bg-color-page);
  background: var(--el-bg-color-page);
  padding: 20px 0;

  // 三栏布局容器
  .article-container {
    padding-top: 70px;
    max-width: 1600px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 280px 1fr 280px;
    gap: 20px;
    // padding: 0 20px;

    // 左侧用户信息栏
    .left-sidebar {
      position: sticky;
      top: 70px;
      height: fit-content;
    }

    // 中间文章内容区域
    .main-content {
      width: 100%;
      overflow: hidden;
      background-color: var(--el-bg-color);
      border-radius: 8px;
      box-shadow: var(--el-box-shadow-light);

      .access-panel {
        --panel-bg: var(--el-bg-color);
        --panel-border: var(--el-border-color);
        --panel-text: var(--el-text-color-primary);
        --panel-muted: var(--el-text-color-secondary);
        display: grid;
        gap: 14px;
        padding: 40px;
        background: var(--panel-bg);
        border: 1px solid var(--panel-border);
        border-radius: 8px;

        .panel-kicker {
          font-size: 13px;
          letter-spacing: 0.2em;
          text-transform: uppercase;
          color: var(--panel-muted);
        }

        .panel-title {
          margin: 0;
          font-size: 30px;
          color: var(--panel-text);
        }

        .panel-description {
          margin: 0;
          max-width: 520px;
          line-height: 1.8;
          color: var(--panel-muted);
        }

        .panel-actions {
          display: flex;
          gap: 12px;

          .primary-button,
          .secondary-button {
            min-width: 132px;
            height: 42px;
            border-radius: 999px;
            cursor: pointer;
          }

          .primary-button {
            border: 0;
            background: var(--el-color-warning);
            color: var(--el-color-white);
          }

          .secondary-button {
            border: 1px solid var(--panel-border);
            background: transparent;
            color: var(--panel-text);
          }
        }

        &.vip-panel {
          --panel-bg: rgba(var(--el-color-warning-rgb, 230, 162, 60), 0.06);
          --panel-border: rgba(var(--el-color-warning-rgb, 230, 162, 60), 0.2);
        }
      }
    }

    // 右侧文章目录栏
    .right-sidebar {
      position: sticky;
      top: 70px;
      height: fit-content;
    }
  }
}

// 响应式设计
@media (max-width: 1024px) {
  .article-detail {
    .article-container {
      grid-template-columns: 1fr;
      max-width: 800px;

      // 移动端隐藏侧边栏
      .left-sidebar,
      .right-sidebar {
        display: none;
      }
    }
  }
}

@media (max-width: 768px) {
  .article-detail {
    padding: 0;

    .article-container {
      padding-top: 0px;
      gap: 10px;
      width: 100%;
      .main-content {
        margin-top: 50px;
        border-radius: 0;

        .access-panel {
          padding: 28px 20px;

          .panel-title {
            font-size: 24px;
          }

          .panel-actions {
            flex-direction: column;
          }
        }
      }
    }
  }
}
</style>
