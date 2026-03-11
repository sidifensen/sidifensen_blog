<template>
  <div class="link-page">
    <div class="link-page__hero">
      <div class="link-page__hero-inner container">
        <div class="link-page__hero-copy">
          <p class="link-page__eyebrow">Directory</p>
          <h1 class="link-page__title">友情链接</h1>
          <p class="link-page__description">收录长期维护、内容稳定更新的网站。这里不是堆砌卡片的展示墙，而是一份可以持续回访的站点目录。</p>
          <div class="link-page__meta">
            <span class="link-page__meta-item">已收录 {{ totalDisplay }} 个站点</span>
            <span class="link-page__meta-item">点击卡片可直接访问</span>
          </div>
        </div>

        <div class="link-page__hero-side">
          <div class="link-page__hero-panel">
            <div class="link-page__hero-panel-top">
              <span class="link-page__hero-panel-label">站点目录</span>
              <strong class="link-page__hero-panel-value">{{ totalDisplay }}</strong>
            </div>
            <p class="link-page__hero-panel-description">如果你的网站也在持续写作、更新或提供稳定服务，可以申请加入这里。</p>
            <div class="link-page__hero-panel-actions">
              <el-button v-if="isUserLoggedIn" type="primary" class="link-page__apply-button" @click="showApplyDialog">申请友链</el-button>
              <el-button v-else class="link-page__login-button" @click="goToLogin">登录后申请</el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="link-page__content">
      <div class="link-page__content-inner container">
        <div class="link-page__section">
          <div class="link-page__section-head">
            <div class="link-page__section-title">
              <h2>站点目录</h2>
              <p>优先展示内容明确、可长期访问的网站。</p>
            </div>
            <div class="link-page__section-side">
              <span class="link-page__section-count">{{ linkList.length }} / {{ totalDisplay }}</span>
            </div>
          </div>

          <div v-if="loading" class="link-page__loading">
            <el-skeleton animated :count="6">
              <template #template>
                <div class="link-page__skeleton-card">
                  <el-skeleton-item variant="image" class="link-page__skeleton-cover" />
                  <div class="link-page__skeleton-body">
                    <el-skeleton-item variant="h3" style="width: 52%; margin-bottom: 10px" />
                    <el-skeleton-item variant="text" style="width: 100%; margin-bottom: 8px" />
                    <el-skeleton-item variant="text" style="width: 74%" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <div v-else-if="linkList.length === 0" class="link-page__empty">
            <div class="link-page__empty-body">
              <div class="link-page__empty-icon">
                <el-icon><DocumentRemove /></el-icon>
              </div>
              <h3>暂时还没有友链</h3>
              <p>{{ isUserLoggedIn ? "如果你的网站适合长期展示，可以成为这里的第一条友链。" : "登录后可以提交你的网站，我们会在审核通过后展示。" }}</p>
              <el-button v-if="isUserLoggedIn" type="primary" class="link-page__apply-button" @click="showApplyDialog">申请友链</el-button>
              <el-button v-else class="link-page__login-button" @click="goToLogin">前往登录</el-button>
            </div>
          </div>

          <div v-else class="link-page__grid">
            <article v-for="(link, index) in linkList" :key="link.id" class="link-card" :style="{ animationDelay: `${index * 0.04}s` }" @click="visitLink(link)">
              <div class="link-card__cover">
                <el-image :src="link.coverUrl || ''" fit="cover" class="link-card__cover-image" :alt="link.name">
                  <template #placeholder>
                    <div class="link-card__cover-state">
                      <el-icon><Picture /></el-icon>
                      <span>封面加载中</span>
                    </div>
                  </template>
                  <template #error>
                    <div class="link-card__cover-state">
                      <el-icon><Picture /></el-icon>
                      <span>暂无封面</span>
                    </div>
                  </template>
                </el-image>
              </div>

              <div class="link-card__body">
                <div class="link-card__top">
                  <div class="link-card__identity">
                    <h3 class="link-card__name" :title="link.name">{{ link.name }}</h3>
                    <p class="link-card__domain">{{ formatUrl(link.url) }}</p>
                  </div>

                  <div v-if="isCurrentUserLink(link)" class="link-card__actions" @click.stop>
                    <el-tooltip content="删除友链" placement="top">
                      <el-button circle text class="link-card__delete" :loading="deletingLinkId === link.id" @click="handleDeleteLink(link)">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </el-tooltip>
                  </div>
                </div>

                <p class="link-card__description" :title="link.description">{{ link.description }}</p>

                <div class="link-card__footer">
                  <span class="link-card__visit">
                    <el-icon><View /></el-icon>
                    <span>访问网站</span>
                  </span>
                  <span v-if="isCurrentUserLink(link)" class="link-card__owner">我的友链</span>
                </div>
              </div>
            </article>
          </div>

          <div v-if="loadingMore && linkList.length > 0" class="link-page__loading-more">
            <span>加载更多...</span>
          </div>

          <div v-if="!hasMore && linkList.length > 0 && !loading" class="link-page__no-more">
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
import { computed, onMounted, onUnmounted, ref } from "vue";
import { useRouter } from "vue-router";
import { Delete, DocumentRemove, Link, Picture, View } from "@element-plus/icons-vue";
import { deleteLink, getLinkList } from "@/api/link";
import LinkApplyDialog from "@/views/Link/LinkApplyDialog.vue";
import { useUserStore } from "@/stores/userStore";

const router = useRouter();
const userStore = useUserStore();

const loading = ref(false);
const loadingMore = ref(false);
const applyDialogVisible = ref(false);

const currentPage = ref(1);
const pageSize = ref(24);
const total = ref(0);
const hasMore = ref(true);

const linkList = ref([]);
const deletingLinkId = ref(null);

const isUserLoggedIn = computed(() => {
  return userStore.user && userStore.user.id;
});

const totalDisplay = computed(() => {
  return total.value || linkList.value.length;
});

const isCurrentUserLink = (link) => {
  return userStore.user && userStore.user.id === link.userId;
};

const fetchLinkList = async (reset = false) => {
  if (loadingMore.value || (!hasMore.value && !reset)) {
    return;
  }

  try {
    if (reset) {
      loading.value = true;
      currentPage.value = 1;
      hasMore.value = true;
    } else {
      loadingMore.value = true;
    }

    const res = await getLinkList(currentPage.value, pageSize.value);
    const data = res.data.data;
    const newLinks = data.data || [];

    if (reset) {
      linkList.value = newLinks;
    } else {
      linkList.value = [...linkList.value, ...newLinks];
    }

    total.value = data.total || 0;
    hasMore.value = linkList.value.length < total.value;

    if (hasMore.value && newLinks.length > 0) {
      currentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取友链列表失败");
    console.error("获取友链列表失败:", error);
  } finally {
    loading.value = false;
    loadingMore.value = false;
  }
};

const handleScroll = () => {
  if (loading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  const { scrollTop, scrollHeight, clientHeight } = document.documentElement;
  if (scrollTop + clientHeight >= scrollHeight - 200) {
    fetchLinkList();
  }
};

const showApplyDialog = () => {
  applyDialogVisible.value = true;
};

const goToLogin = () => {
  router.push("/login");
};

const handleApplySuccess = () => {
  fetchLinkList(true);
};

const visitLink = (link) => {
  window.open(link.url, "_blank", "noopener,noreferrer");
};

const formatUrl = (url) => {
  try {
    const urlObj = new URL(url);
    return urlObj.hostname;
  } catch {
    return url;
  }
};

const handleDeleteLink = async (link) => {
  try {
    await ElMessageBox.confirm(`确定要删除友链 "${link.name}" 吗？此操作不可恢复。`, "确认删除", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
      confirmButtonClass: "el-button--danger",
      lockScroll: false,
    });

    deletingLinkId.value = link.id;
    await deleteLink(link.id);

    linkList.value = linkList.value.filter((item) => item.id !== link.id);
    total.value = Math.max(0, total.value - 1);
    hasMore.value = linkList.value.length < total.value;

    ElMessage.success("友链删除成功");
  } catch (error) {
    if (error !== "cancel") {
      ElMessage.error("删除友链失败");
      console.error("删除友链失败:", error);
    }
  } finally {
    deletingLinkId.value = null;
  }
};

onMounted(() => {
  fetchLinkList(true);
  window.addEventListener("scroll", handleScroll);
});

onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});
</script>

<style lang="scss" scoped>
.link-page {
  --link-page-bg: #f3f5f7;
  --link-page-panel: #ffffff;
  --link-page-panel-soft: #f7f9fb;
  --link-page-text-primary: #18212b;
  --link-page-text-regular: #53606d;
  --link-page-text-muted: #7f8a96;
  --link-page-border: #dce3e9;
  --link-page-border-soft: #ebf0f4;
  --link-page-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  --link-page-shadow-soft: 0 1px 3px rgba(15, 23, 42, 0.08);

  min-height: 100vh;
  background:
    radial-gradient(circle at top left, var(--link-page-panel-soft) 0%, transparent 30%),
    linear-gradient(180deg, var(--link-page-bg) 0%, var(--link-page-bg) 100%);

  .container {
    max-width: 1180px;
    margin: 0 auto;
    padding: 0 24px;
  }

  .link-page__hero {
    .link-page__hero-inner {
      display: grid;
      grid-template-columns: minmax(0, 1.6fr) minmax(280px, 360px);
      gap: 24px;
      padding-top: 40px;
      padding-bottom: 28px;

      .link-page__hero-copy {
        .link-page__eyebrow {
          margin: 0 0 12px;
          font-size: 12px;
          letter-spacing: 0.16em;
          text-transform: uppercase;
          color: var(--link-page-text-muted);
        }

        .link-page__title {
          margin: 0;
          font-size: 40px;
          line-height: 1.05;
          font-weight: 700;
          color: var(--link-page-text-primary);
        }

        .link-page__description {
          max-width: 720px;
          margin: 16px 0 0;
          font-size: 16px;
          line-height: 1.9;
          color: var(--link-page-text-regular);
        }

        .link-page__meta {
          display: flex;
          flex-wrap: wrap;
          gap: 10px;
          margin-top: 20px;

          .link-page__meta-item {
            display: inline-flex;
            align-items: center;
            min-height: 34px;
            padding: 0 12px;
            border-radius: 999px;
            border: 1px solid var(--link-page-border);
            background: var(--link-page-panel);
            color: var(--link-page-text-regular);
            font-size: 13px;
            box-shadow: var(--link-page-shadow-soft);
          }
        }
      }

      .link-page__hero-side {
        .link-page__hero-panel {
          height: 100%;
          padding: 24px;
          border: 1px solid var(--link-page-border);
          border-radius: 24px;
          background: var(--link-page-panel);
          box-shadow: var(--link-page-shadow);

          .link-page__hero-panel-top {
            display: flex;
            align-items: flex-end;
            justify-content: space-between;
            gap: 12px;
            margin-bottom: 16px;

            .link-page__hero-panel-label {
              font-size: 13px;
              color: var(--link-page-text-muted);
            }

            .link-page__hero-panel-value {
              font-size: 34px;
              line-height: 1;
              color: var(--link-page-text-primary);
            }
          }

          .link-page__hero-panel-description {
            margin: 0;
            font-size: 14px;
            line-height: 1.8;
            color: var(--link-page-text-regular);
          }

          .link-page__hero-panel-actions {
            margin-top: 20px;

            .link-page__apply-button,
            .link-page__login-button {
              min-width: 132px;
              border-radius: 12px;
            }

            .link-page__apply-button {
              box-shadow: none;
            }

            .link-page__login-button {
              border-color: var(--link-page-border);
              color: var(--link-page-text-primary);
              background: var(--link-page-panel);
            }
          }
        }
      }
    }
  }

  .link-page__content {
    .link-page__content-inner {
      padding-bottom: 40px;

      .link-page__section {
        .link-page__section-head {
          display: flex;
          align-items: flex-end;
          justify-content: space-between;
          gap: 16px;
          margin-bottom: 18px;

          .link-page__section-title {
            h2 {
              margin: 0;
              font-size: 22px;
              font-weight: 600;
              color: var(--link-page-text-primary);
            }

            p {
              margin: 8px 0 0;
              font-size: 14px;
              color: var(--link-page-text-regular);
            }
          }

          .link-page__section-side {
            .link-page__section-count {
              font-size: 13px;
              color: var(--link-page-text-muted);
            }
          }
        }

        .link-page__loading {
          display: grid;
          grid-template-columns: repeat(3, minmax(0, 1fr));
          gap: 18px;

          .link-page__skeleton-card {
            overflow: hidden;
            border: 1px solid var(--link-page-border);
            border-radius: 20px;
            background: var(--link-page-panel);
            box-shadow: var(--link-page-shadow-soft);

            .link-page__skeleton-cover {
              width: 100%;
              height: 180px;
            }

            .link-page__skeleton-body {
              padding: 18px;
            }
          }
        }

        .link-page__empty {
          padding: 40px 0 12px;

          .link-page__empty-body {
            display: flex;
            flex-direction: column;
            align-items: center;
            padding: 40px 24px;
            border: 1px solid var(--link-page-border);
            border-radius: 24px;
            background: var(--link-page-panel);
            text-align: center;

            .link-page__empty-icon {
              display: inline-flex;
              align-items: center;
              justify-content: center;
              width: 64px;
              height: 64px;
              margin-bottom: 18px;
              border-radius: 20px;
              background: var(--link-page-panel-soft);
              color: var(--link-page-text-muted);
              font-size: 28px;
            }

            h3 {
              margin: 0;
              font-size: 24px;
              color: var(--link-page-text-primary);
            }

            p {
              max-width: 520px;
              margin: 14px 0 0;
              font-size: 14px;
              line-height: 1.8;
              color: var(--link-page-text-regular);
            }

            .el-button {
              margin-top: 20px;
            }
          }
        }

        .link-page__grid {
          display: grid;
          grid-template-columns: repeat(3, minmax(0, 1fr));
          gap: 18px;

          .link-card {
            display: flex;
            flex-direction: column;
            overflow: hidden;
            border: 1px solid var(--link-page-border);
            border-radius: 22px;
            background: var(--link-page-panel);
            box-shadow: var(--link-page-shadow-soft);
            cursor: pointer;
            opacity: 0;
            transform: translateY(10px);
            animation: link-card-enter 0.45s ease forwards;
            transition: transform 0.2s ease, box-shadow 0.2s ease, border-color 0.2s ease;

            &:hover {
              transform: translateY(-3px);
              border-color: var(--link-page-text-muted);
              box-shadow: var(--link-page-shadow);
            }

            .link-card__cover {
              position: relative;
              aspect-ratio: 16 / 9;
              overflow: hidden;
              background: var(--link-page-panel-soft);
              border-bottom: 1px solid var(--link-page-border-soft);

              .link-card__cover-image {
                width: 100%;
                height: 100%;
              }

              .link-card__cover-state {
                display: flex;
                width: 100%;
                height: 100%;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                gap: 8px;
                color: var(--link-page-text-muted);
                font-size: 13px;

                .el-icon {
                  font-size: 22px;
                }
              }
            }

            .link-card__body {
              display: flex;
              min-height: 188px;
              flex: 1;
              flex-direction: column;
              padding: 18px;

              .link-card__top {
                display: flex;
                align-items: flex-start;
                justify-content: space-between;
                gap: 12px;

                .link-card__identity {
                  min-width: 0;

                  .link-card__name {
                    margin: 0;
                    font-size: 18px;
                    font-weight: 600;
                    color: var(--link-page-text-primary);
                    white-space: nowrap;
                    overflow: hidden;
                    text-overflow: ellipsis;
                  }

                  .link-card__domain {
                    margin: 8px 0 0;
                    font-size: 13px;
                    color: var(--link-page-text-muted);
                  }
                }

                .link-card__actions {
                  .link-card__delete {
                    color: var(--link-page-text-muted);
                  }
                }
              }

              .link-card__description {
                margin: 14px 0 0;
                font-size: 14px;
                line-height: 1.8;
                color: var(--link-page-text-regular);
                display: -webkit-box;
                overflow: hidden;
                -webkit-box-orient: vertical;
                -webkit-line-clamp: 3;
              }

              .link-card__footer {
                display: flex;
                align-items: center;
                justify-content: space-between;
                gap: 12px;
                margin-top: auto;
                padding-top: 18px;
                border-top: 1px solid var(--link-page-border-soft);

                .link-card__visit {
                  display: inline-flex;
                  align-items: center;
                  gap: 8px;
                  font-size: 13px;
                  color: var(--link-page-text-primary);
                }

                .link-card__owner {
                  display: inline-flex;
                  align-items: center;
                  min-height: 28px;
                  padding: 0 10px;
                  border-radius: 999px;
                  background: var(--link-page-panel-soft);
                  color: var(--link-page-text-regular);
                  font-size: 12px;
                }
              }
            }
          }
        }

        .link-page__loading-more,
        .link-page__no-more {
          display: flex;
          justify-content: center;
          padding: 20px 0 10px;
          font-size: 13px;
          color: var(--link-page-text-muted);
        }
      }
    }
  }
}

html.dark {
  .link-page {
    --link-page-bg: #0f1720;
    --link-page-panel: #16202a;
    --link-page-panel-soft: #1a2631;
    --link-page-text-primary: #e8edf2;
    --link-page-text-regular: #bcc6cf;
    --link-page-text-muted: #8d98a5;
    --link-page-border: #2a3544;
    --link-page-border-soft: #212c38;
    --link-page-shadow: 0 16px 34px rgba(0, 0, 0, 0.22);
    --link-page-shadow-soft: 0 1px 3px rgba(0, 0, 0, 0.28);
  }
}

@media (max-width: 992px) {
  .link-page {
    .link-page__hero {
      .link-page__hero-inner {
        grid-template-columns: 1fr;
      }
    }

    .link-page__content {
      .link-page__content-inner {
        .link-page__section {
          .link-page__loading,
          .link-page__grid {
            grid-template-columns: repeat(2, minmax(0, 1fr));
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .link-page {
    .container {
      padding: 0 16px;
    }

    .link-page__hero {
      .link-page__hero-inner {
        gap: 18px;
        padding-top: 24px;
        padding-bottom: 20px;

        .link-page__hero-copy {
          .link-page__title {
            font-size: 32px;
          }

          .link-page__description {
            font-size: 15px;
          }
        }

        .link-page__hero-side {
          .link-page__hero-panel {
            padding: 20px;
          }
        }
      }
    }

    .link-page__content {
      .link-page__content-inner {
        padding-bottom: 28px;

        .link-page__section {
          .link-page__section-head {
            align-items: flex-start;
            flex-direction: column;
          }

          .link-page__loading,
          .link-page__grid {
            grid-template-columns: 1fr;
          }
        }
      }
    }
  }
}

@keyframes link-card-enter {
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
