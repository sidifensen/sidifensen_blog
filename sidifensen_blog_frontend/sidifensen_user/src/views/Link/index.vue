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
              <!-- 卡片背景装饰 -->
              <div class="card-bg-decoration"></div>

              <!-- 网站封面 -->
              <div class="link-cover">
                <el-image :src="link.coverUrl || ''" fit="contain" class="cover-image" :alt="link.name">
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
                  <div class="overlay-content">
                    <el-icon class="visit-icon"><View /></el-icon>
                    <span class="visit-text">访问网站</span>
                  </div>
                </div>

                <!-- 我的友链标识 -->
                <div v-if="isCurrentUserLink(link)" class="my-link-badge">
                  <el-icon><User /></el-icon>
                  <span>我的</span>
                </div>
              </div>

              <!-- 网站信息 -->
              <div class="link-info">
                <h3 class="link-name" :title="link.name">{{ link.name }}</h3>

                <p class="link-description" :title="link.description">
                  {{ link.description }}
                </p>

                <!-- 网站地址 -->
                <div class="link-footer">
                  <div class="link-url">
                    <el-icon class="url-icon"><Link /></el-icon>
                    <span>{{ formatUrl(link.url) }}</span>
                  </div>

                  <!-- 删除按钮 - 仅当前用户的友链显示 -->
                  <div v-if="isCurrentUserLink(link)" class="delete-action" @click.stop>
                    <el-button type="danger" size="small" circle class="delete-btn" @click="handleDeleteLink(link)" :loading="deletingLinkId === link.id">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </div>
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
import { Link, Plus, View, Picture, Loading, DocumentRemove, Delete, User } from "@element-plus/icons-vue";
import { getLinkList, deleteLink } from "@/api/link";
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

// 删除相关状态
const deletingLinkId = ref(null);

// 计算属性 - 判断用户是否已登录
const isUserLoggedIn = computed(() => {
  return userStore.user && userStore.user.id;
});

// 判断是否为当前用户的友链
const isCurrentUserLink = (link) => {
  return userStore.user && userStore.user.id === link.userId;
};

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

// 删除友链
const handleDeleteLink = async (link) => {
  try {
    await ElMessageBox.confirm(`确定要删除友链 "${link.name}" 吗？此操作不可恢复。`, "确认删除", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
      confirmButtonClass: "el-button--danger",
      lockScroll: false,
    });

    // 设置删除状态
    deletingLinkId.value = link.id;

    // 调用删除API
    await deleteLink(link.id);

    // 从列表中移除已删除的友链
    linkList.value = linkList.value.filter((item) => item.id !== link.id);

    // 更新总数
    total.value = Math.max(0, total.value - 1);

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
</script>

<style lang="scss" scoped>
// 友链页面容器
.link-page {
  min-height: calc(100vh - 48px);

  // 工具类
  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  // 页面头部
  .page-header {
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
          color: var(--el-text-color-primary);
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
          background: var(--el-bg-color-page);
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
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 28px;
      margin-bottom: 40px;

      .link-card {
        background: var(--el-bg-color-page);
        border-radius: 16px;
        overflow: hidden;
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08), 0 0 0 1px rgba(0, 0, 0, 0.04);
        transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
        cursor: pointer;
        position: relative;

        &:hover {
          transform: translateY(-8px) scale(1.02);
          box-shadow: 0 12px 32px rgba(64, 158, 255, 0.15), 0 8px 16px rgba(0, 0, 0, 0.1);
          border-color: rgba(64, 158, 255, 0.3);

          .card-bg-decoration {
            opacity: 1;
          }

          .cover-overlay {
            opacity: 1;
            backdrop-filter: blur(8px);

            .overlay-content {
              transform: translateY(0);
            }
          }

          .cover-image {
            transform: scale(1.08);
          }

          .link-name {
            color: var(--el-color-primary);
          }
        }

        // 卡片背景装饰
        .card-bg-decoration {
          position: absolute;
          top: -50%;
          right: -50%;
          width: 200%;
          height: 200%;
          background: linear-gradient(135deg, rgba(64, 158, 255, 0.05) 0%, transparent 50%);
          opacity: 0;
          transition: opacity 0.4s ease;
          pointer-events: none;
          z-index: 0;
        }

        // 网站封面
        .link-cover {
          position: relative;
          height: 240px;
          overflow: hidden;
          background: linear-gradient(135deg, var(--el-bg-color-page) 0%, var(--el-fill-color-light) 100%);

          .cover-image {
            width: 100%;
            height: 100%;
            transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            z-index: 1;
          }

          .cover-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg, rgba(64, 158, 255, 0.9) 0%, rgba(103, 194, 255, 0.8) 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: all 0.4s ease;
            z-index: 2;

            .overlay-content {
              display: flex;
              flex-direction: column;
              align-items: center;
              gap: 8px;
              color: white;
              transform: translateY(10px);
              transition: transform 0.4s ease;

              .visit-icon {
                font-size: 32px;
                animation: bounce 1s ease-in-out infinite;
              }

              .visit-text {
                font-size: 14px;
                font-weight: 500;
                letter-spacing: 0.5px;
              }
            }
          }

          // 我的友链标识
          .my-link-badge {
            position: absolute;
            top: 12px;
            left: 12px;
            display: flex;
            align-items: center;
            gap: 4px;
            padding: 6px 12px;
            background: linear-gradient(135deg, rgba(103, 194, 255, 0.95) 0%, rgba(64, 158, 255, 0.95) 100%);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            color: white;
            font-size: 12px;
            font-weight: 500;
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
            z-index: 3;

            .el-icon {
              font-size: 14px;
            }

            span {
              letter-spacing: 0.5px;
            }
          }

          .image-placeholder,
          .image-error {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            background: linear-gradient(135deg, var(--el-fill-color-lighter) 0%, var(--el-fill-color-light) 100%);
            color: var(--el-text-color-placeholder);

            .el-icon {
              font-size: 40px;
            }
          }
        }

        // 网站信息
        .link-info {
          padding: 13px;
          position: relative;
          z-index: 1;

          .link-name {
            font-size: 17px;
            font-weight: 600;
            margin: 0;
            color: var(--el-text-color-primary);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            transition: color 0.3s ease;
          }

          .link-description {
            font-size: 13px;
            color: var(--el-text-color-regular);
            line-height: 1.5;
            margin: 0;
            height: 39px;
            overflow: hidden;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .link-footer {
            display: flex;
            align-items: center;
            justify-content: space-between;

            .link-url {
              display: flex;
              align-items: center;
              gap: 6px;
              font-size: 12px;
              color: var(--el-text-color-secondary);
              flex: 1;
              min-width: 0;
              padding: 6px 10px;
              background: var(--el-fill-color-lighter);
              border-radius: 8px;
              transition: all 0.3s ease;

              &:hover {
                background: var(--el-fill-color-light);
              }

              .url-icon {
                font-size: 13px;
                color: var(--el-color-primary);
                flex-shrink: 0;
              }

              span {
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
                font-weight: 500;
              }
            }

            // 删除按钮区域
            .delete-action {
              margin-left: 8px;

              .delete-btn {
                width: 36px;
                height: 36px;
                background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
                border: none;
                box-shadow: 0 2px 8px rgba(245, 108, 108, 0.3);
                transition: all 0.3s ease;

                &:hover {
                  transform: scale(1.1) rotate(5deg);
                  background: linear-gradient(135deg, #f56c6c 0%, #f23030 100%);
                  box-shadow: 0 4px 16px rgba(245, 108, 108, 0.5);
                }

                &:active {
                  transform: scale(0.95) rotate(0deg);
                }

                .el-icon {
                  font-size: 16px;
                  color: white;
                }
              }
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
        background: transparent;

        &::before,
        &::after {
          content: "";
          position: absolute;
          top: 50%;
          width: 60px;
          height: 1px;
          background: var(--el-border-color);
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

// 渐入动画
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

// 弹跳动画
@keyframes bounce {
  0%,
  100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
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
            padding: 12px;
          }
        }
      }
    }
  }
}
</style>
