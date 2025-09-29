<template>
  <div class="link-page">
    <!-- 页面头部区域 -->
    <div class="page-header">
      <div class="container">
        <!-- 头部内容 -->
        <div class="header-content">
          <div class="header-info">
            <h1 class="page-title">
              <el-icon class="title-icon"><Link /></el-icon>
              友情链接
            </h1>
            <p class="page-description">发现更多优质网站，与志同道合的朋友交换友链，共同成长</p>
          </div>

          <!-- 申请友链按钮 - 只在登录时显示 -->
          <div v-if="isUserLoggedIn" class="header-actions">
            <el-button type="primary" size="large" @click="showApplyDialog" class="apply-btn">
              <el-icon><Plus /></el-icon>
              申请友链
            </el-button>
          </div>

          <!-- 未登录时的提示 -->
          <div v-else class="header-actions">
            <el-button size="large" @click="goToLogin" class="login-btn">
              <el-icon><Plus /></el-icon>
              登录后申请友链
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <div class="container">
        <!-- 友链列表 -->
        <div class="links-section">
          <!-- 加载状态 -->
          <div v-if="loading" class="loading-container">
            <el-skeleton animated :count="8">
              <template #template>
                <div class="link-skeleton" :class="{ 'list-mode': viewMode === 'list' }">
                  <el-skeleton-item variant="image" class="skeleton-image" />
                  <div class="skeleton-content">
                    <el-skeleton-item variant="h3" style="width: 60%; margin-bottom: 8px" />
                    <el-skeleton-item variant="text" style="width: 100%" />
                    <el-skeleton-item variant="text" style="width: 80%" />
                  </div>
                </div>
              </template>
            </el-skeleton>
          </div>

          <!-- 空状态 -->
          <div v-else-if="linkList.length === 0" class="empty-state">
            <div class="empty-content">
              <div class="empty-icon">
                <el-icon><DocumentRemove /></el-icon>
              </div>
              <h3 class="empty-title">暂无友链</h3>
              <p class="empty-description">
                {{ isUserLoggedIn ? "快来申请第一个友链吧！" : "暂时还没有友链，期待更多精彩网站加入！" }}
              </p>
              <el-button v-if="isUserLoggedIn" type="primary" @click="showApplyDialog">
                <el-icon><Plus /></el-icon>
                申请友链
              </el-button>
            </div>
          </div>

          <!-- 友链网格视图 -->
          <div v-else class="links-grid">
            <div v-for="link in linkList" :key="link.id" class="link-card" @click="visitLink(link)">
              <!-- 网站封面 -->
              <div class="link-cover">
                <el-image :src="link.coverUrl || ''" fit="cover" class="cover-image" :alt="link.name">
                  <template #placeholder>
                    <div class="image-placeholder">
                      <el-icon><Loading /></el-icon>
                    </div>
                  </template>
                  <template #error>
                    <div class="image-error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>

                <!-- 悬浮操作 -->
                <div class="cover-overlay">
                  <el-button type="primary" size="small" circle>
                    <el-icon><View /></el-icon>
                  </el-button>
                </div>
              </div>

              <!-- 网站信息 -->
              <div class="link-info">
                <h3 class="link-name" :title="link.name">{{ link.name }}</h3>
                <p class="link-description" :title="link.description">
                  {{ link.description }}
                </p>

                <!-- 网站地址 -->
                <div class="link-url">
                  <el-icon><Link /></el-icon>
                  <span>{{ formatUrl(link.url) }}</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 加载更多指示器 -->
          <div v-if="loadingMore && linkList.length > 0" class="loading-more">
            <div class="loading-spinner"></div>
            <span>加载更多...</span>
          </div>

          <!-- 没有更多数据提示 -->
          <div v-if="!hasMore && linkList.length > 0 && !loading" class="no-more">
            <span>已经到底了~</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 友链申请对话框 -->
    <LinkApplyDialog v-model="applyDialogVisible" @success="handleApplySuccess" />

    <!-- 返回顶部 -->
    <el-backtop :right="30" :bottom="30" />
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch, nextTick } from "vue";
import { useRouter } from "vue-router";
import { Link, Plus, View, Picture, Loading, DocumentRemove } from "@element-plus/icons-vue";
import { getLinkList } from "@/api/link";
import LinkApplyDialog from "@/views/Link/LinkApplyDialog.vue";
import { useUserStore } from "@/stores/userStore";

// 路由和状态管理
const router = useRouter();
const userStore = useUserStore();

// 响应式数据
const loading = ref(false);
const loadingMore = ref(false);
const applyDialogVisible = ref(false);

// 分页数据
const currentPage = ref(1);
const pageSize = ref(24);
const total = ref(0);
const hasMore = ref(true);

// 友链数据
const linkList = ref([]);

// 计算属性 - 判断用户是否已登录
const isUserLoggedIn = computed(() => {
  return userStore.user && userStore.user.id;
});

// 获取友链列表
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

    // 判断是否还有更多数据
    hasMore.value = linkList.value.length < total.value;

    // 如果还有更多数据，准备下一页
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

// 无限滚动处理
const handleScroll = () => {
  if (loading.value || loadingMore.value || !hasMore.value) {
    return;
  }

  const { scrollTop, scrollHeight, clientHeight } = document.documentElement;

  // 当滚动到距离底部200px时开始加载更多
  if (scrollTop + clientHeight >= scrollHeight - 200) {
    fetchLinkList();
  }
};

// 显示申请对话框
const showApplyDialog = () => {
  applyDialogVisible.value = true;
};

// 跳转到登录页面
const goToLogin = () => {
  router.push("/login");
};

// 申请成功回调
const handleApplySuccess = () => {
  ElMessage.success("友链申请已提交，请耐心等待审核");
  // 可以选择刷新列表或显示提示
};

// 访问友链
const visitLink = (link) => {
  // 在新窗口打开链接
  window.open(link.url, "_blank", "noopener,noreferrer");

  // 这里可以添加访问统计逻辑
  console.log("访问友链:", link.name, link.url);
};

// 格式化URL显示
const formatUrl = (url) => {
  try {
    const urlObj = new URL(url);
    return urlObj.hostname;
  } catch {
    return url;
  }
};

// 组件挂载
onMounted(() => {
  // 获取友链列表
  fetchLinkList(true);

  // 添加滚动事件监听
  window.addEventListener("scroll", handleScroll);
});

// 组件卸载时移除事件监听
onUnmounted(() => {
  window.removeEventListener("scroll", handleScroll);
});

// 设置页面标题
document.title = "友情链接 - 思递分森的博客";
</script>

<style lang="scss" scoped>
// 友链页面容器
.link-page {
  min-height: calc(100vh - 48px);
  background-color: var(--el-bg-color-page);

  // 工具类
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  // 页面头部
  .page-header {
    background: white;
    border-bottom: 1px solid var(--el-border-color-lighter);
    padding: 40px 0;

    // 头部内容
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .header-info {
        .page-title {
          display: flex;
          align-items: center;
          gap: 12px;
          font-size: 28px;
          font-weight: 600;
          margin: 0 0 8px 0;
          color: var(--el-text-color-primary);

          .title-icon {
            font-size: 32px;
            color: var(--el-color-primary);
          }
        }

        .page-description {
          font-size: 16px;
          color: var(--el-text-color-regular);
          margin: 0;
          line-height: 1.5;
        }
      }

      .header-actions {
        .apply-btn {
          padding: 12px 24px;
          font-size: 16px;
          border-radius: 8px;
          font-weight: 500;
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
          }

          .el-icon {
            margin-right: 6px;
          }
        }

        .login-btn {
          padding: 12px 24px;
          font-size: 16px;
          border-radius: 8px;
          font-weight: 500;
          color: var(--el-text-color-regular);
          border: 2px solid var(--el-border-color);
          transition: all 0.3s ease;

          &:hover {
            transform: translateY(-2px);
            color: var(--el-color-primary);
            border-color: var(--el-color-primary);
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
          }

          .el-icon {
            margin-right: 6px;
          }
        }
      }
    }
  }

  // 主要内容
  .main-content {
    padding: 40px 0;
    background: var(--el-bg-color-page);
  }

  // 友链区域
  .links-section {
    // 加载状态
    .loading-container {
      .link-skeleton {
        display: flex;
        gap: 16px;
        padding: 20px;
        margin-bottom: 20px;
        background: white;
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        .skeleton-image {
          width: 120px;
          height: 120px;
          border-radius: 12px;
        }

        .skeleton-content {
          flex: 1;
          display: flex;
          flex-direction: column;
          gap: 8px;
        }
      }
    }

    // 空状态
    .empty-state {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 400px;

      .empty-content {
        text-align: center;
        max-width: 400px;

        .empty-icon {
          font-size: 64px;
          color: var(--el-text-color-placeholder);
          margin-bottom: 20px;
        }

        .empty-title {
          font-size: 20px;
          color: var(--el-text-color-primary);
          margin: 0 0 12px 0;
        }

        .empty-description {
          font-size: 14px;
          color: var(--el-text-color-regular);
          margin: 0 0 24px 0;
          line-height: 1.6;
        }
      }
    }

    // 网格视图
    .links-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 24px;
      margin-bottom: 40px;

      .link-card {
        background: white;
        border-radius: 12px;
        overflow: hidden;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        transition: all 0.3s ease;
        cursor: pointer;
        position: relative;

        &:hover {
          transform: translateY(-4px);
          box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

          .cover-overlay {
            opacity: 1;
          }
        }

        // 网站封面
        .link-cover {
          position: relative;
          height: 180px;
          overflow: hidden;

          .cover-image {
            width: 100%;
            height: 100%;
            transition: transform 0.3s ease;
          }

          .cover-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: rgba(0, 0, 0, 0.5);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: opacity 0.3s ease;

            .el-button {
              transform: scale(1.2);
            }
          }

          .image-placeholder,
          .image-error {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            background: var(--el-bg-color-page);
            color: var(--el-text-color-placeholder);

            .el-icon {
              font-size: 32px;
            }
          }
        }

        // 网站信息
        .link-info {
          padding: 20px;

          .link-name {
            font-size: 18px;
            font-weight: 600;
            margin: 0 0 8px 0;
            color: var(--el-text-color-primary);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
          }

          .link-description {
            font-size: 14px;
            color: var(--el-text-color-regular);
            line-height: 1.5;
            margin: 0 0 12px 0;
            height: 42px;
            overflow: hidden;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .link-url {
            display: flex;
            align-items: center;
            gap: 6px;
            font-size: 13px;
            color: var(--el-text-color-secondary);

            .el-icon {
              font-size: 14px;
              color: var(--el-color-primary);
            }

            span {
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }
        }
      }
    }

    // 加载更多指示器
    .loading-more {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px 20px;
      color: var(--el-text-color-regular);
      font-size: 14px;

      .loading-spinner {
        width: 20px;
        height: 20px;
        border: 2px solid var(--el-border-color-light);
        border-top: 2px solid var(--el-color-primary);
        border-radius: 50%;
        animation: spin 1s linear infinite;
        margin-right: 10px;
      }
    }

    // 没有更多数据提示
    .no-more {
      display: flex;
      justify-content: center;
      padding: 40px 20px;
      color: var(--el-text-color-placeholder);
      font-size: 14px;

      span {
        position: relative;
        padding: 0 20px;
        background: var(--el-bg-color-page);

        &::before,
        &::after {
          content: "";
          position: absolute;
          top: 50%;
          width: 60px;
          height: 1px;
          background: var(--el-border-color-light);
        }

        &::before {
          left: -70px;
        }

        &::after {
          right: -70px;
        }
      }
    }
  }
}

// 旋转动画
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 简单的渐入动画
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

// 为卡片添加渐入动画
.links-grid .link-card {
  animation: fadeInUp 0.5s ease-out forwards;
}

// 响应式设计
@media (max-width: 768px) {
  .link-page {
    .page-header {
      padding: 20px 0;

      .header-content {
        flex-direction: column;
        gap: 20px;
        text-align: center;

        .header-info {
          .page-title {
            font-size: 24px;
          }

          .page-description {
            font-size: 14px;
          }
        }
      }
    }

    .main-content {
      padding: 20px 0;

      .links-section {
        .links-grid {
          grid-template-columns: 1fr;
          gap: 16px;
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .link-page {
    .container {
      padding: 0 10px;
    }

    .links-section {
      .links-grid {
        .link-card {
          .link-info {
            padding: 15px;
          }
        }
      }
    }
  }
}
</style>
