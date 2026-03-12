<template>
  <div class="vip-article-page">
    <div class="vip-article-shell">
      <!-- 页面头部 -->
      <section class="page-head">
        <p class="head-kicker">会员专区</p>
        <h1 class="head-title">仅对有效 VIP 开放的文章内容</h1>
        <p class="head-description">这里集中展示 VIP 可见 的已发布文章，充值vip会员后可直接访问。</p>
      </section>

      <!-- 会员文章列表区 -->
      <section class="page-body">
        <div v-if="accessDenied" class="state-card">
          <h2>{{ accessDenied }}</h2>
          <button class="state-button" @click="goToVipCenter">去开通 VIP</button>
        </div>
        <div v-else-if="loading" class="state-card">
          <h2>正在加载会员文章</h2>
        </div>
        <div v-else-if="articleList.length === 0" class="state-card">
          <h2>会员专区暂时还没有文章</h2>
        </div>
        <div v-else class="article-list">
          <article v-for="article in articleList" :key="article.id" class="article-card" @click="goToArticle(article)">
            <div class="article-cover">
              <img v-if="article.coverUrl" :src="article.coverUrl" :alt="article.title" />
              <div v-else class="cover-placeholder">VIP</div>
            </div>
            <div class="article-content">
              <div class="content-top">
                <span class="article-badge">会员文章</span>
                <span class="article-author">{{ article.nickname || "匿名作者" }}</span>
              </div>
              <h2 class="article-title">{{ article.title }}</h2>
              <p class="article-description">{{ article.description }}</p>
              <div class="content-meta">
                <span>{{ article.readCount || 0 }} 阅读</span>
                <span>{{ article.commentCount || 0 }} 评论</span>
                <span>{{ article.createTime || "--" }}</span>
              </div>
            </div>
          </article>
          <button v-if="hasMore" class="load-more" @click="fetchArticleList">加载更多</button>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { getVipArticleList } from "@/api/article";

const router = useRouter();

// 列表页状态
const articleList = ref([]);
const loading = ref(false);
const accessDenied = ref("");
const pageNum = ref(1);
const pageSize = ref(10);
const hasMore = ref(true);

// 会员专区只做追加分页，避免重复请求
const fetchArticleList = async () => {
  if (!hasMore.value || loading.value) {
    return;
  }
  try {
    loading.value = true;
    const response = await getVipArticleList(pageNum.value, pageSize.value);
    const records = response.data.data.data || [];
    const total = response.data.data.total || 0;
    articleList.value = [...articleList.value, ...records];
    hasMore.value = articleList.value.length < total;
    if (records.length > 0) {
      pageNum.value += 1;
    }
  } catch (error) {
    accessDenied.value = error?.msg || "暂无访问权限";
  } finally {
    loading.value = false;
  }
};

// 会员专区直接跳转到文章详情页
const goToArticle = (article) => {
  router.push(`/user/${article.userId}/article/${article.id}`);
};

const goToVipCenter = () => {
  router.push("/vip");
};

onMounted(async () => {
  await fetchArticleList();
});
</script>

<style lang="scss" scoped>
.vip-article-page {
  --bg-page: #f5f3ed;
  --bg-card: #fffdf8;
  --text-primary: #1a2117;
  --text-regular: #5b6858;
  --border: #dfe4dc;
  --accent: #8c5f16;
  min-height: 100vh;
  background: var(--bg-page);

  .vip-article-shell {
    max-width: 1040px;
    margin: 0 auto;
    padding: 84px 20px 40px;

    // 页面头部
    .page-head {
      margin-bottom: 24px;

      .head-kicker {
        margin: 0 0 8px;
        font-size: 13px;
        letter-spacing: 0.24em;
        text-transform: uppercase;
        color: var(--text-regular);
      }

      .head-title {
        margin: 0;
        font-size: 34px;
        color: var(--text-primary);
      }

      .head-description {
        margin: 12px 0 0;
        font-size: 15px;
        line-height: 1.8;
        color: var(--text-regular);
      }
    }

    // 列表主体
    .page-body {
      // 无权限、加载中、空列表的统一状态卡片
      .state-card {
        display: grid;
        gap: 14px;
        padding: 32px;
        border-radius: 24px;
        border: 1px solid var(--border);
        background: var(--bg-card);
        text-align: center;

        h2 {
          margin: 0;
          color: var(--text-primary);
        }

        .state-button {
          justify-self: center;
          min-width: 140px;
          height: 42px;
          border: 0;
          border-radius: 999px;
          background: var(--accent);
          color: #fffaf3;
          cursor: pointer;
        }
      }

      // 会员文章卡片列表
      .article-list {
        display: grid;
        gap: 16px;

        .article-card {
          display: grid;
          grid-template-columns: 220px minmax(0, 1fr);
          gap: 18px;
          padding: 18px;
          border-radius: 22px;
          border: 1px solid var(--border);
          background: var(--bg-card);
          cursor: pointer;

          .article-cover {
            height: 148px;
            border-radius: 16px;
            overflow: hidden;
            background: linear-gradient(135deg, rgba(140, 95, 22, 0.12), rgba(140, 95, 22, 0.02));

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
              display: block;
            }

            .cover-placeholder {
              display: flex;
              align-items: center;
              justify-content: center;
              width: 100%;
              height: 100%;
              font-size: 34px;
              font-weight: 800;
              color: var(--accent);
            }
          }

          .article-content {
            display: grid;
            gap: 12px;

            .content-top {
              display: flex;
              justify-content: space-between;
              gap: 12px;
              align-items: center;

              .article-badge {
                display: inline-flex;
                align-items: center;
                height: 28px;
                padding: 0 12px;
                border-radius: 999px;
                background: rgba(140, 95, 22, 0.1);
                color: var(--accent);
                font-size: 12px;
                font-weight: 700;
              }

              .article-author {
                font-size: 13px;
                color: var(--text-regular);
              }
            }

            .article-title {
              margin: 0;
              font-size: 24px;
              color: var(--text-primary);
            }

            .article-description {
              margin: 0;
              font-size: 14px;
              line-height: 1.8;
              color: var(--text-regular);
            }

            .content-meta {
              display: flex;
              flex-wrap: wrap;
              gap: 14px;
              font-size: 12px;
              color: var(--text-regular);
            }
          }
        }

        .load-more {
          justify-self: center;
          min-width: 144px;
          height: 42px;
          border: 1px solid var(--border);
          border-radius: 999px;
          background: transparent;
          color: var(--text-primary);
          cursor: pointer;
        }
      }
    }
  }
}

html.dark {
  .vip-article-page {
    --bg-page: #101712;
    --bg-card: #172019;
    --text-primary: #eff4eb;
    --text-regular: #afbea9;
    --border: #2a352b;
    --accent: #d2aa59;
  }
}

@media (max-width: 768px) {
  .vip-article-page {
    .vip-article-shell {
      padding: 72px 14px 28px;

      .page-head {
        .head-title {
          font-size: 28px;
        }
      }

      .page-body {
        .article-list {
          .article-card {
            grid-template-columns: 1fr;

            .article-cover {
              height: 180px;
            }
          }
        }
      }
    }
  }
}
</style>
