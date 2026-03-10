<template>
  <div class="link-page">
    <!-- 页面头部区域 -->
    <div class="page-header">
      <div class="header-bg-decoration"></div>
      <div class="container">
        <!-- 头部内容 -->
        <div class="header-content">
          <div class="header-info">
            <div class="title-badge">
              <el-icon class="badge-icon"><Link /></el-icon>
            </div>
            <h1 class="page-title">友情链接</h1>
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
            <div v-for="(link, index) in linkList" :key="link.id" class="link-card" @click="visitLink(link)" :style="{ animationDelay: `${index * 0.05}s` }">
              <!-- 卡片背景装饰 -->
              <div class="card-bg-gradient"></div>
              <div class="card-bg-grid"></div>

              <!-- 网站封面 -->
              <div class="link-cover">
                <el-image :src="link.coverUrl || ''" fit="cover" class="cover-image" :alt="link.name">
                  <template #placeholder>
                    <div class="image-placeholder">
                      <div class="placeholder-icon">
                        <el-icon><Picture /></el-icon>
                      </div>
                      <span>封面预览</span>
                    </div>
                  </template>
                  <template #error>
                    <div class="image-error">
                      <div class="error-icon">
                        <el-icon><Picture /></el-icon>
                      </div>
                      <span>图片加载失败</span>
                    </div>
                  </template>
                </el-image>

                <!-- 悬浮操作 -->
                <div class="cover-overlay">
                  <div class="overlay-content">
                    <div class="visit-button">
                      <el-icon class="visit-icon"><View /></el-icon>
                      <span class="visit-text">立即访问</span>
                    </div>
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
                <div class="link-header">
                  <h3 class="link-name" :title="link.name">{{ link.name }}</h3>
                  <!-- 删除按钮 - 仅当前用户的友链显示 -->
                  <div v-if="isCurrentUserLink(link)" class="delete-action" @click.stop>
                    <el-tooltip content="删除友链" placement="top">
                      <el-button type="danger" size="small" circle class="delete-btn" @click="handleDeleteLink(link)" :loading="deletingLinkId === link.id">
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </el-tooltip>
                  </div>
                </div>

                <p class="link-description" :title="link.description">
                  {{ link.description }}
                </p>

                <!-- 网站地址 -->
                <div class="link-footer">
                  <div class="link-url">
                    <el-icon class="url-icon"><Link /></el-icon>
                    <span>{{ formatUrl(link.url) }}</span>
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
  min-height: 100vh;
  background: linear-gradient(180deg, var(--el-bg-color-page) 0%, var(--el-fill-color-lighter) 100%);
  position: relative;
  overflow-x: hidden;

  // 工具类
  .container {
    max-width: 1280px;
    margin: 0 auto;
    padding: 0 24px;
    position: relative;
    z-index: 1;
  }

  // 页面头部 - 现代化渐变设计
  .page-header {
    position: relative;
    padding: 80px 0 60px;
    overflow: hidden;

    // 头部背景装饰 - 不透明纯色背景
    .header-bg-decoration {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: var(--el-color-primary-light-9);
      z-index: 0;

      &::before {
        content: '';
        position: absolute;
        top: -50%;
        right: -20%;
        width: 80%;
        height: 200%;
        background: radial-gradient(circle, var(--el-color-primary-light-8) 0%, var(--el-color-primary-light-9) 70%);
        animation: float 20s ease-in-out infinite;
      }

      &::after {
        content: '';
        position: absolute;
        top: 30%;
        left: -10%;
        width: 60%;
        height: 150%;
        background: radial-gradient(circle, var(--el-color-primary-light-8) 0%, var(--el-color-primary-light-9) 70%);
        animation: float 25s ease-in-out infinite reverse;
      }
    }

    // 头部内容
    .header-content {
      position: relative;
      z-index: 1;
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
      gap: 24px;

      .header-info {
        flex: 1;
        min-width: 0;

        .title-badge {
          display: inline-flex;
          align-items: center;
          justify-content: center;
          width: 56px;
          height: 56px;
          background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
          border-radius: 16px;
          margin-bottom: 20px;
          box-shadow: 0 8px 24px rgba(64, 158, 255, 0.35), inset 0 -2px 4px rgba(0, 0, 0, 0.1);
          animation: badgeFloat 3s ease-in-out infinite;

          .badge-icon {
            font-size: 28px;
            color: white;
          }
        }

        .page-title {
          font-size: 36px;
          font-weight: 700;
          margin: 0 0 12px 0;
          background: linear-gradient(135deg, var(--el-text-color-primary) 0%, var(--el-color-primary) 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
          letter-spacing: -0.5px;
        }

        .page-description {
          font-size: 16px;
          color: var(--el-text-color-regular);
          margin: 0;
          line-height: 1.7;
          opacity: 0.9;
        }
      }

      .header-actions {
        flex-shrink: 0;

        .apply-btn {
          padding: 14px 32px;
          font-size: 16px;
          font-weight: 600;
          border-radius: 12px;
          background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-dark-2) 100%);
          border: none;
          box-shadow: 0 6px 20px rgba(64, 158, 255, 0.35), inset 0 -2px 4px rgba(0, 0, 0, 0.1);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          position: relative;
          overflow: hidden;

          &::before {
            content: '';
            position: absolute;
            top: 0;
            left: -100%;
            width: 100%;
            height: 100%;
            background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
            transition: left 0.5s ease;
          }

          &:hover {
            transform: translateY(-3px);
            box-shadow: 0 10px 32px rgba(64, 158, 255, 0.45), inset 0 -2px 4px rgba(0, 0, 0, 0.1);

            &::before {
              left: 100%;
            }
          }

          &:active {
            transform: translateY(-1px);
          }

          .el-icon {
            margin-right: 8px;
            font-size: 18px;
          }
        }

        .login-btn {
          padding: 14px 32px;
          font-size: 16px;
          font-weight: 600;
          border-radius: 12px;
          background: var(--el-bg-color);
          color: var(--el-text-color-primary);
          border: 2px solid var(--el-border-color-lighter);
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);

          &:hover {
            transform: translateY(-3px);
            color: var(--el-color-primary);
            border-color: var(--el-color-primary-light-5);
            box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2);
          }

          .el-icon {
            margin-right: 8px;
            font-size: 18px;
          }
        }
      }
    }
  }

  // 主要内容
  .main-content {
    padding: 20px 0 80px;
    position: relative;
    z-index: 1;
  }

  // 友链区域
  .links-section {
    // 加载状态
    .loading-container {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 28px;

      .link-skeleton {
        display: flex;
        flex-direction: column;
        gap: 12px;
        padding: 20px;
        background: var(--el-bg-color);
        border-radius: 20px;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);

        .skeleton-image {
          width: 100%;
          height: 200px;
          border-radius: 14px;
        }

        .skeleton-content {
          display: flex;
          flex-direction: column;
          gap: 10px;
        }
      }
    }

    // 空状态
    .empty-state {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 450px;

      .empty-content {
        text-align: center;
        max-width: 420px;
        padding: 40px;
        background: var(--el-bg-color);
        border-radius: 24px;
        box-shadow: 0 8px 32px rgba(0, 0, 0, 0.08);

        .empty-icon {
          width: 80px;
          height: 80px;
          margin: 0 auto 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-info-light-9) 100%);
          border-radius: 20px;
          color: var(--el-color-primary);

          .el-icon {
            font-size: 40px;
          }
        }

        .empty-title {
          font-size: 22px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          margin: 0 0 12px 0;
        }

        .empty-description {
          font-size: 15px;
          color: var(--el-text-color-regular);
          margin: 0 0 28px 0;
          line-height: 1.7;
        }
      }
    }

    // 网格视图 - 现代化卡片设计
    .links-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 32px;
      margin-bottom: 40px;

      .link-card {
        background: var(--el-bg-color);
        border-radius: 20px;
        overflow: hidden;
        box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06), 0 0 0 1px rgba(0, 0, 0, 0.04);
        transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
        cursor: pointer;
        position: relative;
        animation: cardEnter 0.5s ease-out forwards;
        opacity: 0;
        transform: translateY(30px);

        // 卡片背景渐变
        .card-bg-gradient {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          height: 4px;
          background: linear-gradient(90deg,
            var(--el-color-primary) 0%,
            var(--el-color-success) 50%,
            var(--el-color-warning) 100%);
          opacity: 0;
          transition: opacity 0.4s ease;
        }

        // 卡片背景网格装饰
        .card-bg-grid {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background-image:
            linear-gradient(rgba(64, 158, 255, 0.03) 1px, transparent 1px),
            linear-gradient(90deg, rgba(64, 158, 255, 0.03) 1px, transparent 1px);
          background-size: 20px 20px;
          opacity: 0;
          transition: opacity 0.4s ease;
          pointer-events: none;
        }

        &:hover {
          transform: translateY(-12px) scale(1.02);
          box-shadow: 0 20px 48px rgba(64, 158, 255, 0.15), 0 8px 24px rgba(0, 0, 0, 0.1);
          border-color: rgba(64, 158, 255, 0.3);

          .card-bg-gradient {
            opacity: 1;
          }

          .card-bg-grid {
            opacity: 1;
          }

          .cover-overlay {
            opacity: 1;
            backdrop-filter: blur(12px);

            .overlay-content {
              transform: translateY(0);
            }
          }

          .cover-image {
            transform: scale(1.1);
          }

          .link-name {
            color: var(--el-color-primary);
          }
        }

        // 网站封面
        .link-cover {
          position: relative;
          height: 200px;
          overflow: hidden;
          background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-info-light-9) 100%);

          .cover-image {
            width: 100%;
            height: 100%;
            transition: transform 0.6s cubic-bezier(0.34, 1.56, 0.64, 1);
            position: relative;
            z-index: 1;

            :deep(.el-image__inner) {
              object-fit: cover;
            }
          }

          .cover-overlay {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(135deg,
              rgba(64, 158, 255, 0.85) 0%,
              rgba(103, 194, 255, 0.75) 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            opacity: 0;
            transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
            z-index: 2;

            .overlay-content {
              display: flex;
              align-items: center;
              justify-content: center;

              .visit-button {
                display: flex;
                align-items: center;
                gap: 10px;
                padding: 12px 24px;
                background: rgba(255, 255, 255, 0.95);
                border-radius: 30px;
                box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
                transform: translateY(20px);
                transition: transform 0.4s ease;

                .visit-icon {
                  font-size: 22px;
                  color: var(--el-color-primary);
                  animation: bounce 1.5s ease-in-out infinite;
                }

                .visit-text {
                  font-size: 15px;
                  font-weight: 600;
                  color: var(--el-text-color-primary);
                  letter-spacing: 0.5px;
                }
              }
            }
          }

          // 我的友链标识
          .my-link-badge {
            position: absolute;
            top: 14px;
            left: 14px;
            display: flex;
            align-items: center;
            gap: 5px;
            padding: 7px 14px;
            background: linear-gradient(135deg, rgba(255, 255, 255, 0.95) 0%, rgba(255, 255, 255, 0.85) 100%);
            backdrop-filter: blur(10px);
            border-radius: 20px;
            color: var(--el-color-primary);
            font-size: 12px;
            font-weight: 600;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
            z-index: 3;
            border: 1px solid rgba(64, 158, 255, 0.2);

            .el-icon {
              font-size: 15px;
            }

            span {
              letter-spacing: 0.5px;
            }
          }

          .image-placeholder,
          .image-error {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            height: 100%;
            background: linear-gradient(135deg,
              var(--el-color-primary-light-9) 0%,
              var(--el-color-info-light-9) 100%);
            color: var(--el-color-primary);
            gap: 10px;

            .placeholder-icon,
            .error-icon {
              width: 50px;
              height: 50px;
              display: flex;
              align-items: center;
              justify-content: center;
              background: rgba(255, 255, 255, 0.7);
              border-radius: 50%;

              .el-icon {
                font-size: 24px;
              }
            }

            span {
              font-size: 13px;
              font-weight: 500;
              opacity: 0.8;
            }
          }
        }

        // 网站信息
        .link-info {
          padding: 18px;
          position: relative;
          z-index: 1;
          background: var(--el-bg-color);

          .link-header {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            margin-bottom: 10px;
          }

          .link-name {
            font-size: 17px;
            font-weight: 600;
            margin: 0;
            color: var(--el-text-color-primary);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            transition: color 0.3s ease;
            flex: 1;
            min-width: 0;
          }

          // 删除按钮区域
          .delete-action {
            flex-shrink: 0;

            .delete-btn {
              width: 34px;
              height: 34px;
              background: linear-gradient(135deg, #f56c6c 0%, #f89898 100%);
              border: none;
              box-shadow: 0 3px 12px rgba(245, 108, 108, 0.35);
              transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

              &:hover {
                transform: scale(1.15) rotate(8deg);
                background: linear-gradient(135deg, #f56c6c 0%, #f23030 100%);
                box-shadow: 0 6px 20px rgba(245, 108, 108, 0.5);
              }

              &:active {
                transform: scale(0.95) rotate(0deg);
              }

              .el-icon {
                font-size: 17px;
                color: white;
              }
            }
          }

          .link-description {
            font-size: 14px;
            color: var(--el-text-color-regular);
            line-height: 1.6;
            margin: 0 0 14px 0;
            height: 44px;
            overflow: hidden;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .link-footer {
            .link-url {
              display: inline-flex;
              align-items: center;
              gap: 7px;
              font-size: 13px;
              color: var(--el-text-color-secondary);
              padding: 8px 12px;
              background: var(--el-fill-color-lighter);
              border-radius: 10px;
              transition: all 0.3s ease;
              max-width: 100%;

              &:hover {
                background: var(--el-color-primary-light-9);
                color: var(--el-color-primary);
              }

              .url-icon {
                font-size: 15px;
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
          }
        }
      }
    }

    // 加载更多指示器
    .loading-more {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 50px 20px;
      gap: 16px;

      .loading-spinner {
        width: 32px;
        height: 32px;
        border: 3px solid var(--el-border-color-lighter);
        border-top: 3px solid var(--el-color-primary);
        border-radius: 50%;
        animation: spin 0.8s linear infinite;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
      }

      span {
        font-size: 14px;
        color: var(--el-text-color-regular);
        font-weight: 500;
        letter-spacing: 0.5px;
      }
    }

    // 没有更多数据提示
    .no-more {
      display: flex;
      justify-content: center;
      padding: 50px 20px;

      span {
        position: relative;
        padding: 0 24px;
        font-size: 14px;
        color: var(--el-text-color-placeholder);
        font-weight: 500;
        letter-spacing: 1px;

        &::before,
        &::after {
          content: '';
          position: absolute;
          top: 50%;
          width: 50px;
          height: 2px;
          background: linear-gradient(90deg, transparent, var(--el-border-color), transparent);
        }

        &::before {
          left: -60px;
        }

        &::after {
          right: -60px;
        }
      }
    }
  }
}

// 动画定义
@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

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

@keyframes cardEnter {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@keyframes badgeFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(10px, -15px) rotate(5deg);
  }
  66% {
    transform: translate(-5px, 10px) rotate(-3deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .link-page {
    .container {
      padding: 0 16px;
    }

    .page-header {
      padding: 50px 0 40px;

      .header-content {
        flex-direction: column;
        text-align: center;
        gap: 20px;

        .header-info {
          .title-badge {
            width: 48px;
            height: 48px;

            .badge-icon {
              font-size: 24px;
            }
          }

          .page-title {
            font-size: 26px;
          }

          .page-description {
            font-size: 14px;
          }
        }

        .header-actions {
          .apply-btn,
          .login-btn {
            width: 100%;
            padding: 12px 24px;
          }
        }
      }
    }

    .main-content {
      padding: 0 0 50px;

      .links-section {
        .links-grid {
          grid-template-columns: 1fr;
          gap: 20px;

          .link-card {
            .link-cover {
              height: 180px;
            }
          }
        }
      }
    }
  }
}

@media (max-width: 480px) {
  .link-page {
    .page-header {
      padding: 40px 0 30px;
    }

    .main-content {
      .links-section {
        .links-grid {
          .link-card {
            .link-info {
              padding: 14px;

              .link-name {
                font-size: 16px;
              }

              .link-description {
                font-size: 13px;
                height: 40px;
              }
            }
          }
        }
      }
    }
  }
}
</style>
