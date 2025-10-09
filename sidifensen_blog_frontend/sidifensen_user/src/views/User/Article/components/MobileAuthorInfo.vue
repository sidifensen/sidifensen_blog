<template>
  <div class="mobile-author-info">
    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item variant="circle" style="width: 50px; height: 50px" />
          <div class="skeleton-text">
            <el-skeleton-item variant="h3" style="width: 80px; margin: 0 0 8px 0" />
          </div>
          <el-skeleton-item variant="button" style="width: 60px; height: 32px" />
        </div>
      </template>

      <!-- 实际内容 -->
      <template #default>
        <div class="author-content" v-if="userInfo">
          <!-- 作者头像 -->
          <div class="author-avatar" @click="goToUserHomepage">
            <el-avatar :size="40" :src="userInfo.avatar" class="clickable-avatar" />
          </div>

          <!-- 作者名称 -->
          <div class="author-name" @click="goToUserHomepage">
            {{ userInfo.nickname }}
          </div>

          <!-- 关注按钮 -->
          <div class="follow-button" v-if="!isCurrentUser">
            <el-button :type="isFollowed ? 'default' : 'primary'" :loading="followLoading" size="small" :class="{ 'followed-btn': isFollowed }" @click="handleFollow" @mouseenter="handleFollowButtonHover(true)" @mouseleave="handleFollowButtonHover(false)">
              {{ followButtonText }}
            </el-button>
          </div>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { toggleFollow, isFollowing } from "@/api/follow";
import { useUserStore } from "@/stores/userStore";

// 路由和状态管理
const router = useRouter();
const userStore = useUserStore();

// Props 定义
const props = defineProps({
  userInfo: {
    type: Object,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

// 关注状态
const isFollowed = ref(false);
const followLoading = ref(false);
const isHoveringFollowButton = ref(false);

// 计算属性
const isCurrentUser = computed(() => {
  return userStore.user?.id === props.userInfo?.id;
});

// 计算关注按钮显示的文字
const followButtonText = computed(() => {
  if (!isFollowed.value) {
    return "关注";
  }
  return isHoveringFollowButton.value ? "取消关注" : "已关注";
});

// 检查用户关注状态
const checkUserFollowStatus = async () => {
  if (!userStore.user || !props.userInfo || isCurrentUser.value) {
    return;
  }

  try {
    const followerId = userStore.user.id;
    const followedId = props.userInfo.id;
    const res = await isFollowing(followerId, followedId);
    isFollowed.value = res.data.data;
  } catch (error) {
    console.error("检查关注状态失败:", error);
    isFollowed.value = false;
  }
};

// 关注用户
const handleFollow = async () => {
  if (!userStore.user) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }

  try {
    followLoading.value = true;
    const followedId = props.userInfo.id;
    const wasFollowed = isFollowed.value;

    // 调用切换关注状态接口
    await toggleFollow(followedId);

    // 切换状态
    isFollowed.value = !wasFollowed;

    // 显示操作结果
    ElMessage.success(isFollowed.value ? "关注成功" : "取消关注成功");
  } catch (error) {
    ElMessage.error("操作失败，请重试");
    console.error("关注操作失败:", error);
  } finally {
    followLoading.value = false;
  }
};

// 处理关注按钮悬停状态
const handleFollowButtonHover = (isHovering) => {
  isHoveringFollowButton.value = isHovering;
};

// 跳转到用户主页
const goToUserHomepage = () => {
  if (props.userInfo?.id) {
    router.push(`/user/${props.userInfo.id}`);
  }
};

// 监听用户信息变化
watch(
  () => props.userInfo,
  (newUserInfo) => {
    if (newUserInfo && !isCurrentUser.value) {
      checkUserFollowStatus();
    } else {
      isFollowed.value = false;
    }
  },
  { immediate: true }
);

// 组件挂载
onMounted(() => {
  if (props.userInfo && !isCurrentUser.value) {
    checkUserFollowStatus();
  }
});
</script>

<style lang="scss" scoped>
// 移动端作者信息容器
.mobile-author-info {
  margin: 16px 0;
  border-radius: 8px;

  // 骨架屏样式
  .skeleton-content {
    display: flex;
    align-items: center;
    gap: 12px;

    .skeleton-text {
      flex: 1;
    }
  }

  // 作者信息内容
  .author-content {
    display: flex;
    align-items: center;
    gap: 5px;

    // 作者头像
    .author-avatar {
      cursor: pointer;
      transition: transform 0.2s ease;

      &:hover {
        transform: scale(1.05);
      }

      .clickable-avatar {
        border: 2px solid var(--el-border-color);
        transition: border-color 0.2s ease;

        &:hover {
          border-color: var(--el-color-primary);
        }
      }
    }

    // 作者名称
    .author-name {
      flex: 1;
      font-size: 14px;
      font-weight: 550;
      color: var(--el-text-color-primary);
      cursor: pointer;
      transition: color 0.2s ease;
      min-width: 0;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;

      &:hover {
        color: var(--el-color-primary);
      }
    }

    // 关注按钮
    .follow-button {
      flex-shrink: 0;

      .el-button {
        border-radius: 16px;
        font-size: 14px;
        padding: 6px 16px;
        min-width: 60px;
        transition: all 0.3s ease;

        // 已关注按钮样式
        &.followed-btn {
          border-color: var(--el-color-success);
          color: var(--el-color-success);
          background-color: transparent;

          &:hover {
            background-color: var(--el-color-danger);
            border-color: var(--el-color-danger);
            color: white;
          }
        }

        // 未关注按钮样式
        &:not(.followed-btn) {
          &:hover {
            transform: translateY(-1px);
            box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
          }
        }
      }
    }
  }
}

// 响应式设计 - 只在移动端显示
@media (min-width: 1025px) {
  .mobile-author-info {
    display: none;
  }
}
</style>
