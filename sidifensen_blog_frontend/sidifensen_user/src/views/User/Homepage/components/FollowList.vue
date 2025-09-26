<template>
  <div class="follow-list-wrapper">
    <div class="follow-list-section">
      <!-- 关注标签页 -->
      <div class="follow-tabs">
        <el-tabs v-model="activeFollowTab" @tab-change="handleFollowTabChange">
          <el-tab-pane label="关注的人" name="following">
            <!-- 关注列表加载状态 -->
            <div v-if="followingLoading" class="loading-container">
              <el-skeleton animated :count="5">
                <template #template>
                  <div class="user-skeleton">
                    <el-skeleton-item variant="circle" style="width: 60px; height: 60px" />
                    <div class="skeleton-content">
                      <el-skeleton-item variant="h3" style="width: 70%" />
                      <el-skeleton-item variant="text" style="width: 100%" />
                    </div>
                  </div>
                </template>
              </el-skeleton>
            </div>

            <!-- 关注列表为空状态 -->
            <div v-else-if="followingList.length === 0" class="empty-state">
              <el-empty description="暂无关注的人" />
            </div>

            <!-- 关注列表内容 -->
            <div v-else class="follow-content">
              <div class="user-list">
                <div v-for="user in followingList" :key="user.id" class="user-item" @click="goToUserHomepage(user.id)">
                  <!-- 用户头像 -->
                  <el-avatar :size="60" :src="user.avatar" class="user-avatar">
                    <template #error>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-avatar>

                  <!-- 用户信息 -->
                  <div class="user-info">
                    <h4 class="username">{{ user.nickname }}</h4>
                    <p class="user-intro">{{ user.introduction || "这个人很懒，什么都没写~" }}</p>
                    <div class="user-stats">
                      <span class="stat-item">{{ user.fansCount || 0 }} 粉丝</span>
                      <span class="stat-item">{{ user.followCount || 0 }} 关注</span>
                    </div>
                  </div>

                  <!-- 操作按钮 -->
                  <div class="user-actions">
                    <el-button type="primary" size="small" @click.stop="handleToggleFollow(user.id)" :loading="followActionLoading">
                      {{ isCurrentUser ? "已关注" : "关注" }}
                    </el-button>
                  </div>
                </div>

                <!-- 加载更多指示器 -->
                <div v-if="followingLoadingMore" class="loading-more">
                  <div class="loading-spinner"></div>
                  <span>加载更多...</span>
                </div>
              </div>
            </div>
          </el-tab-pane>

          <el-tab-pane label="粉丝" name="fans">
            <!-- 粉丝列表加载状态 -->
            <div v-if="fansLoading" class="loading-container">
              <el-skeleton animated :count="5">
                <template #template>
                  <div class="user-skeleton">
                    <el-skeleton-item variant="circle" style="width: 60px; height: 60px" />
                    <div class="skeleton-content">
                      <el-skeleton-item variant="h3" style="width: 70%" />
                      <el-skeleton-item variant="text" style="width: 100%" />
                    </div>
                  </div>
                </template>
              </el-skeleton>
            </div>

            <!-- 粉丝列表为空状态 -->
            <div v-else-if="fansList.length === 0" class="empty-state">
              <el-empty description="暂无粉丝" />
            </div>

            <!-- 粉丝列表内容 -->
            <div v-else class="follow-content">
              <div class="user-list">
                <div v-for="user in fansList" :key="user.id" class="user-item" @click="goToUserHomepage(user.id)">
                  <!-- 用户头像 -->
                  <el-avatar :size="60" :src="user.avatar" class="user-avatar">
                    <template #error>
                      <el-icon><User /></el-icon>
                    </template>
                  </el-avatar>

                  <!-- 用户信息 -->
                  <div class="user-info">
                    <h4 class="username">{{ user.nickname }}</h4>
                    <p class="user-intro">{{ user.introduction || "这个人很懒，什么都没写~" }}</p>
                    <div class="user-stats">
                      <span class="stat-item">{{ user.fansCount || 0 }} 粉丝</span>
                      <span class="stat-item">{{ user.followCount || 0 }} 关注</span>
                    </div>
                  </div>

                  <!-- 操作按钮 -->
                  <div class="user-actions">
                    <el-button :type="checkIfFollowing(user.id) ? 'default' : 'primary'" size="small" @click.stop="handleToggleFollow(user.id)" :loading="followActionLoading">
                      {{ checkIfFollowing(user.id) ? "已关注" : "关注" }}
                    </el-button>
                  </div>
                </div>

                <!-- 加载更多指示器 -->
                <div v-if="fansLoadingMore" class="loading-more">
                  <div class="loading-spinner"></div>
                  <span>加载更多...</span>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import { User } from "@element-plus/icons-vue";
import { getFollowList, getFansList, toggleFollow, isFollowing } from "@/api/follow";
import { useUserStore } from "@/stores/userStore";

// 路由和状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 响应式数据 - 按功能分组并添加注释
const activeFollowTab = ref("following"); // 当前激活的关注标签
const followingLoading = ref(false); // 关注列表加载状态
const fansLoading = ref(false); // 粉丝列表加载状态
const followingLoadingMore = ref(false); // 关注列表加载更多状态
const fansLoadingMore = ref(false); // 粉丝列表加载更多状态
const followActionLoading = ref(false); // 关注操作加载状态
const followingList = ref([]); // 关注列表数据
const fansList = ref([]); // 粉丝列表数据
const followingCurrentPage = ref(1); // 关注列表当前页码
const fansCurrentPage = ref(1); // 粉丝列表当前页码
const followingHasMore = ref(true); // 关注列表是否还有更多数据
const fansHasMore = ref(true); // 粉丝列表是否还有更多数据
const followingTotal = ref(0); // 关注列表总数
const fansTotal = ref(0); // 粉丝列表总数
const pageSize = ref(10); // 每页数据量
const userFollowStatus = ref(new Map()); // 用户关注状态缓存

// 计算属性
const isCurrentUser = computed(() => {
  return userStore.user?.id === parseInt(route.params.userId);
});

// 获取关注列表
const fetchFollowingList = async (reset = false) => {
  if (!followingHasMore.value || followingLoading.value || followingLoadingMore.value) {
    return;
  }

  try {
    // 设置加载状态
    if (reset) {
      followingLoading.value = true;
    } else {
      followingLoadingMore.value = true;
    }

    const userId = route.params.userId;
    const res = await getFollowList(userId, followingCurrentPage.value, pageSize.value);
    const newUsers = res.data.data.data || [];
    followingTotal.value = res.data.data.total || 0;

    if (reset) {
      followingList.value = newUsers;
    } else {
      followingList.value = [...followingList.value, ...newUsers];
    }

    // 判断是否还有更多数据
    followingHasMore.value = followingList.value.length < followingTotal.value;

    // 更新页码
    if (followingHasMore.value && newUsers.length > 0) {
      followingCurrentPage.value++;
    }
  } catch (error) {
    ElMessage.error("获取关注列表失败");
    console.error("获取关注列表失败:", error);
  } finally {
    followingLoading.value = false;
    followingLoadingMore.value = false;
  }
};

// 获取粉丝列表
const fetchFansList = async (reset = false) => {
  if (!fansHasMore.value || fansLoading.value || fansLoadingMore.value) {
    return;
  }

  try {
    // 设置加载状态
    if (reset) {
      fansLoading.value = true;
    } else {
      fansLoadingMore.value = true;
    }

    const userId = route.params.userId;
    const res = await getFansList(userId, fansCurrentPage.value, pageSize.value);
    const newUsers = res.data.data.data || [];
    fansTotal.value = res.data.data.total || 0;

    if (reset) {
      fansList.value = newUsers;
    } else {
      fansList.value = [...fansList.value, ...newUsers];
    }

    // 判断是否还有更多数据
    fansHasMore.value = fansList.value.length < fansTotal.value;

    // 更新页码
    if (fansHasMore.value && newUsers.length > 0) {
      fansCurrentPage.value++;
    }

    // 如果是当前用户查看自己的粉丝，需要检查是否关注了这些粉丝
    if (isCurrentUser.value) {
      await checkFollowStatusForUsers(newUsers);
    }
  } catch (error) {
    ElMessage.error("获取粉丝列表失败");
    console.error("获取粉丝列表失败:", error);
  } finally {
    fansLoading.value = false;
    fansLoadingMore.value = false;
  }
};

// 检查用户关注状态
const checkFollowStatusForUsers = async (users) => {
  if (!isCurrentUser.value || !userStore.user?.id) return;

  const currentUserId = userStore.user.id;
  for (const user of users) {
    try {
      const res = await isFollowing(currentUserId, user.id);
      userFollowStatus.value.set(user.id, res.data.data);
    } catch (error) {
      console.error(`检查用户 ${user.id} 关注状态失败:`, error);
    }
  }
};

// 检查是否关注某个用户
const checkIfFollowing = (userId) => {
  return userFollowStatus.value.get(userId) || false;
};

// 切换关注标签
const handleFollowTabChange = (tabName) => {
  activeFollowTab.value = tabName;
  if (tabName === "following" && followingList.value.length === 0) {
    fetchFollowingList(true);
  } else if (tabName === "fans" && fansList.value.length === 0) {
    fetchFansList(true);
  }
};

// 页面滚动监听 - 无限滚动
const handlePageScroll = () => {
  // 检查当前是否在关注标签页且有更多数据
  if (activeFollowTab.value === "following" && followingHasMore.value && !followingLoading.value && !followingLoadingMore.value) {
    const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    const scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
    const clientHeight = document.documentElement.clientHeight || window.innerHeight;

    // 当滚动到页面底部附近时加载更多
    if (scrollTop + clientHeight >= scrollHeight - 100) {
      fetchFollowingList();
    }
  }

  // 检查当前是否在粉丝标签页且有更多数据
  if (activeFollowTab.value === "fans" && fansHasMore.value && !fansLoading.value && !fansLoadingMore.value) {
    const scrollTop = document.documentElement.scrollTop || document.body.scrollTop;
    const scrollHeight = document.documentElement.scrollHeight || document.body.scrollHeight;
    const clientHeight = document.documentElement.clientHeight || window.innerHeight;

    // 当滚动到页面底部附近时加载更多
    if (scrollTop + clientHeight >= scrollHeight - 100) {
      fetchFansList();
    }
  }
};

// 处理关注/取消关注操作
const handleToggleFollow = async (userId) => {
  if (!userStore.user?.id) {
    ElMessage.warning("请先登录");
    return;
  }

  try {
    followActionLoading.value = true;
    const currentStatus = userFollowStatus.value.get(userId) || false;

    await toggleFollow(userId);

    // 更新本地关注状态
    userFollowStatus.value.set(userId, !currentStatus);

    ElMessage.success(currentStatus ? "取消关注成功" : "关注成功");

    // 重新请求数据以获取最新状态
    if (activeFollowTab.value === "following") {
      // 重新获取关注列表
      followingCurrentPage.value = 1;
      followingHasMore.value = true;
      await fetchFollowingList(true);
    } else if (activeFollowTab.value === "fans") {
      // 重新获取粉丝列表（可能有新的关注状态变化）
      fansCurrentPage.value = 1;
      fansHasMore.value = true;
      await fetchFansList(true);
    }
  } catch (error) {
    ElMessage.error("操作失败");
    console.error("关注操作失败:", error);
  } finally {
    followActionLoading.value = false;
  }
};

// 跳转到用户主页
const goToUserHomepage = (userId) => {
  router.push(`/user/${userId}`);
};

// 监听路由参数变化
watch(
  () => route.params.userId,
  (newUserId) => {
    if (newUserId) {
      // 重置数据
      followingCurrentPage.value = 1;
      fansCurrentPage.value = 1;
      followingList.value = [];
      fansList.value = [];
      followingHasMore.value = true;
      fansHasMore.value = true;
      userFollowStatus.value.clear();

      // 根据当前标签重新加载数据
      if (activeFollowTab.value === "following") {
        fetchFollowingList(true);
      } else if (activeFollowTab.value === "fans") {
        fetchFansList(true);
      }
    }
  },
  { immediate: true }
);

// 组件挂载
onMounted(() => {
  // 默认加载关注列表
  fetchFollowingList(true);

  // 添加页面滚动监听
  window.addEventListener("scroll", handlePageScroll);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("scroll", handlePageScroll);
});
</script>

<style lang="scss" scoped>
// 关注列表容器
.follow-list-wrapper {
  background: var(--el-bg-color-page);
  border-radius: 8px;
  padding: 20px;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 12px var(--el-border-color-light);
  min-height: 580px; // 设置最小高度，与其他组件一致
  height: 100%; // 占据父容器的完整高度

  // 关注列表区域
  .follow-list-section {
    // 关注标签页
    .follow-tabs {
      .el-tabs {
        // 重写标签页样式
        :deep(.el-tabs__header) {
          margin-bottom: 16px;

          .el-tabs__nav-wrap {
            .el-tabs__nav {
              .el-tabs__item {
                font-size: 16px;
                font-weight: 500;
                padding: 0 20px;

                &.is-active {
                  color: var(--el-color-primary);
                  font-weight: 600;
                }

                &:hover {
                  color: var(--el-color-primary);
                }
              }
            }
          }

          .el-tabs__active-bar {
            background-color: var(--el-color-primary);
          }
        }

        :deep(.el-tabs__content) {
          .el-tab-pane {
            height: 100%;
          }
        }
      }
    }

    // 加载容器样式
    .loading-container {
      padding: 20px 0;
    }

    // 骨架屏样式
    .user-skeleton {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px 0;
      border-bottom: 1px solid var(--el-border-color-light);

      .skeleton-content {
        flex: 1;
        display: flex;
        flex-direction: column;
        gap: 8px;
      }
    }

    // 空状态
    .empty-state {
      padding: 60px 0;
      text-align: center;
    }

    // 关注内容容器
    .follow-content {
      // 移除固定高度，让内容自然展开
      // 移除滚动条，跟随页面滚动

      // 用户列表
      .user-list {
        // 用户项目
        .user-item {
          display: flex;
          align-items: center;
          gap: 16px;
          padding: 20px 0;
          border-bottom: 1px solid var(--el-border-color-light);
          cursor: pointer;
          transition: all 0.3s ease;

          &:last-child {
            border-bottom: none;
          }

          &:hover {
            background-color: var(--el-bg-color-page);
            transform: translateX(4px);
            border-radius: 8px;
            padding-left: 12px;
            padding-right: 12px;
          }

          // 用户头像
          .user-avatar {
            border: 2px solid rgba(255, 255, 255, 0.3);
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
            transition: transform 0.3s ease;

            &:hover {
              transform: scale(1.05);
            }
          }

          // 用户信息
          .user-info {
            flex: 1;
            min-width: 0;

            // 用户名
            .username {
              font-size: 16px;
              font-weight: 600;
              color: var(--el-text-color-primary);
              margin: 0 0 8px 0;
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
              word-break: break-word;
              max-width: 100%;
            }

            // 用户介绍
            .user-intro {
              font-size: 14px;
              color: var(--el-text-color-regular);
              margin: 0 0 8px 0;
              line-height: 1.4;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              word-break: break-word;
              white-space: normal;
              max-width: 100%;
            }

            // 用户统计信息
            .user-stats {
              display: flex;
              gap: 16px;

              .stat-item {
                font-size: 13px;
                color: var(--el-text-color-secondary);
              }
            }
          }

          // 用户操作
          .user-actions {
            .el-button {
              border-radius: 20px;
              padding: 8px 16px;
              font-size: 14px;
              min-width: 80px;
            }
          }
        }

        // 加载更多指示器
        .loading-more {
          display: flex;
          align-items: center;
          justify-content: center;
          padding: 30px;
          color: var(--el-text-color-regular);

          .loading-spinner {
            width: 20px;
            height: 20px;
            border: 2px solid #f3f3f3;
            border-top: 2px solid #409eff;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin-right: 10px;
          }
        }
      }
    }
  }
}

// 加载动画
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

// 响应式设计
@media (max-width: 768px) {
  .follow-list-wrapper {
    padding: 15px;
    min-height: 500px; // 移动端调整最小高度，与父容器一致

    .follow-list-section {
      // 移动端加载和空状态样式与桌面端一致

      .follow-content {
        // 移动端也移除固定高度，跟随页面滚动

        .user-list {
          .user-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 16px 0;

            .user-info {
              flex: 1;
              min-width: 0;

              .username {
                word-break: break-word;
                white-space: normal;
                overflow: visible;
                text-overflow: unset;
                font-size: 15px;
              }

              .user-intro {
                word-break: break-word;
                white-space: normal;
                -webkit-line-clamp: 3;
                line-clamp: 3;
                max-width: 100%;
                overflow: hidden;
                font-size: 13px;
              }

              .user-stats {
                justify-content: flex-start;
                flex-wrap: wrap;

                .stat-item {
                  font-size: 12px;
                }
              }
            }

            .user-actions {
              flex-shrink: 0;

              .el-button {
                font-size: 12px;
                padding: 6px 12px;
              }
            }
          }
        }
      }
    }
  }
}
</style>
