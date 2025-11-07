<template>
  <div class="user-info-card">
    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item variant="circle" style="width: 80px; height: 80px" />
          <el-skeleton-item variant="h3" style="width: 50%; margin: 12px 0" />
          <el-skeleton-item variant="text" style="width: 80%" />
          <el-skeleton-item variant="text" style="width: 60%" />
        </div>
      </template>

      <!-- 实际内容 -->
      <template #default>
        <div class="user-card-content" v-if="userInfo">
          <!-- 用户基本信息 -->
          <div class="user-basic-info">
            <div class="avatar-container" @mousemove="handleMouseMove" @mouseleave="handleMouseLeave" @click="goToUserHomepage">
              <div class="avatar-wrapper" ref="avatarWrapper">
                <el-avatar :size="100" :src="userInfo.avatar" class="clickable-avatar" />
                <div class="shine-effect" ref="shineEffect"></div>
              </div>
            </div>
            <h3 class="nickname clickable-nickname" @click="goToUserHomepage">
              {{ userInfo.nickname }}
            </h3>
          </div>

          <!-- 用户统计信息 -->
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.articleCount || 0 }}</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.fansCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.followCount || 0 }}</span>
              <span class="stat-label">关注</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="user-actions" v-if="!isCurrentUser">
            <el-button :type="isFollowed ? 'default' : 'primary'" :icon="isFollowed ? null : Plus" @click="handleFollow" :loading="followLoading" :class="{ 'followed-btn': isFollowed }" @mouseenter="handleFollowButtonHover(true)" @mouseleave="handleFollowButtonHover(false)">
              {{ followButtonText }}
            </el-button>
            <el-button :icon="Message" @click="handleMessage"> 私信 </el-button>
          </div>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from "vue";
import { useRouter } from "vue-router";
import { Message, Plus } from "@element-plus/icons-vue";
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

// 头像3D效果相关
const avatarWrapper = ref(null);
const shineEffect = ref(null);

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

// 发送私信
const handleMessage = () => {
  if (!userStore.user) {
    ElMessage.warning("请先登录");
    router.push("/login");
    return;
  }
  // 跳转到聊天窗口
  router.push(`/message/chat/${props.userInfo.id}`);
};

// 处理关注按钮悬停状态
const handleFollowButtonHover = (isHovering) => {
  isHoveringFollowButton.value = isHovering;
};

// 处理鼠标移动事件 - 3D效果和闪光
const handleMouseMove = (event) => {
  if (!avatarWrapper.value || !shineEffect.value) return;

  const rect = event.currentTarget.getBoundingClientRect();
  const x = event.clientX - rect.left;
  const y = event.clientY - rect.top;

  const centerX = rect.width / 2;
  const centerY = rect.height / 2;

  // 计算旋转角度 (限制在-20到20度之间)
  const rotateX = ((y - centerY) / centerY) * -20;
  const rotateY = ((x - centerX) / centerX) * 20;

  // 应用3D变换
  avatarWrapper.value.style.transform = `
    perspective(1000px) 
    rotateX(${rotateX}deg) 
    rotateY(${rotateY}deg) 
    translateZ(20px)
  `;

  // 计算闪光位置
  const shineX = (x / rect.width) * 100;
  const shineY = (y / rect.height) * 100;

  // 应用闪光效果
  shineEffect.value.style.background = `
    radial-gradient(circle at ${shineX}% ${shineY}%, 
    rgba(255, 255, 255, 0.8) 0%, 
    rgba(255, 255, 255, 0.3) 30%, 
    transparent 60%)
  `;
  shineEffect.value.style.opacity = "1";
};

// 处理鼠标离开事件 - 重置效果
const handleMouseLeave = () => {
  if (!avatarWrapper.value || !shineEffect.value) return;

  // 重置3D变换
  avatarWrapper.value.style.transform = "perspective(1000px) rotateX(0deg) rotateY(0deg) translateZ(0px)";

  // 隐藏闪光效果
  shineEffect.value.style.opacity = "0";
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
// 用户信息卡片容器
.user-info-card {
  background-color: var(--el-bg-color);
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--el-box-shadow-light);

  // 骨架屏样式
  .skeleton-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 20px 0;
  }

  // 用户卡片内容
  .user-card-content {
    // 基本信息区域
    .user-basic-info {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      margin-bottom: 20px;

      .nickname {
        margin: 12px 0 8px;
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      // 头像容器 - 3D效果区域
      .avatar-container {
        cursor: pointer;
        padding: 20px;
        perspective: 1000px;

        // 头像包装器 - 3D变换载体
        .avatar-wrapper {
          position: relative;
          display: inline-block;
          border-radius: 50%;
          transition: all 0.3s cubic-bezier(0.23, 1, 0.32, 1);
          transform-style: preserve-3d;
          overflow: hidden;

          // 头像本体
          .clickable-avatar {
            display: block;
            position: relative;
            z-index: 1;
            border-radius: 50%;
            transition: all 0.3s ease;
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);

            &:hover {
              box-shadow: 0 15px 35px rgba(0, 0, 0, 0.25);
            }
          }

          // 闪光效果层
          .shine-effect {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            border-radius: 50%;
            pointer-events: none;
            opacity: 0;
            transition: opacity 0.3s ease;
            z-index: 2;
            background: radial-gradient(circle at 50% 50%, rgba(255, 255, 255, 0) 0%, transparent 60%);
            mix-blend-mode: overlay;
          }

          // 3D变换时的额外效果
          &:hover {
            .clickable-avatar {
              filter: brightness(1.1) contrast(1.05);
            }
          }
        }

        // 鼠标悬停时的容器效果
        &:hover {
          .avatar-wrapper {
            filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.2));
          }
        }
      }

      // 可点击的昵称样式
      .clickable-nickname {
        cursor: pointer;
        transition: color 0.2s ease;

        &:hover {
          color: var(--el-color-primary);
        }
      }
    }

    // 统计信息区域
    .user-stats {
      display: flex;
      justify-content: space-around;
      padding: 15px 0;
      border-top: 1px solid var(--el-border-color-lighter);
      border-bottom: 1px solid var(--el-border-color-lighter);
      margin-bottom: 20px;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;

        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }

        .stat-label {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }
    }

    // 操作按钮区域
    .user-actions {
      display: flex;
      gap: 12px;

      .el-button {
        flex: 1;
      }

      // 已关注按钮样式
      :deep(.followed-btn) {
        border-color: var(--el-color-success);
        color: var(--el-color-success);

        &:hover {
          background-color: var(--el-color-danger);
          border-color: var(--el-color-danger);
          color: white;
        }
      }
    }
  }
}
</style>
