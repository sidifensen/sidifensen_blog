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
        <ArticleContent :article="articleInfo" :loading="articleLoading" />
      </div>

      <!-- 右侧文章目录 -->
      <div class="right-sidebar">
        <ArticleCatalog v-if="articleInfo && articleInfo.content" :content="articleInfo.content" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { getArticleDetail, getUserArticleStatistics } from "@/api/article";
import { getUserInfoById } from "@/api/user";
import UserInfoCard from "./components/UserInfoCard.vue";
import ArticleContent from "./components/ArticleContent.vue";
import ArticleCatalog from "./components/ArticleCatalog.vue";

// 路由参数
const route = useRoute();
const { userId, articleId } = route.params;

// 响应式数据
const userInfo = ref(null);
const articleInfo = ref(null);
const userLoading = ref(false);
const articleLoading = ref(false);

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    userLoading.value = true;
    const response = await getUserInfoById(userId);
    userInfo.value = response.data.data;

    // 获取用户文章统计信息
    const statisticsResponse = await getUserArticleStatistics();
    userInfo.value.articleCount = statisticsResponse.data.data.totalCount || 0;
  } catch (error) {
    ElMessage.error("获取用户信息失败");
  } finally {
    userLoading.value = false;
  }
};

// 获取文章详情
const fetchArticleDetail = async () => {
  try {
    articleLoading.value = true;
    const response = await getArticleDetail(articleId);
    articleInfo.value = response.data.data;
  } catch (error) {
    ElMessage.error("获取文章详情失败");
  } finally {
    articleLoading.value = false;
  }
};

// 页面初始化
onMounted(async () => {
  // 并行获取用户信息和文章详情
  await Promise.all([fetchUserInfo(), fetchArticleDetail()]);
});
</script>

<style lang="scss" scoped>
// 文章详情页面主容器
.article-detail {
  height: 100%;
  background-color: var(--el-bg-color-page);
  padding: 20px 0;

  // 三栏布局容器
  .article-container {
    max-width: 1600px;
    margin: 0 auto;
    display: grid;
    grid-template-columns: 280px 1fr 280px;
    gap: 20px;
    padding: 0 20px;

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
      padding: 0;
      gap: 10px;
      width: 100%;
      .main-content {
        border-radius: 0;
      }
    }
  }
}
</style>
