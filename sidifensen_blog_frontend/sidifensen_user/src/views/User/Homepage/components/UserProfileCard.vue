<template>
  <div class="user-profile-section">
    <div class="container">
      <!-- 用户信息区域卡片 -->
      <div class="user-profile-card" :class="{ 'card-loaded': !userLoading }">
        <el-skeleton :loading="userLoading" animated>
          <template #template>
            <div class="skeleton-profile">
              <el-skeleton-item variant="circle" style="width: 120px; height: 120px" />
              <div class="skeleton-info">
                <el-skeleton-item variant="h3" style="width: 200px; margin: 16px 0" />
                <el-skeleton-item variant="text" style="width: 300px" />
                <el-skeleton-item variant="text" style="width: 250px; margin-top: 8px" />
              </div>
            </div>
          </template>

          <!-- 实际用户信息 -->
          <template #default>
            <div class="user-profile-content" v-if="userInfo">
              <!-- 用户基本信息 -->
              <div class="user-basic-info">
                <div class="avatar-wrapper">
                  <el-avatar :size="120" :src="userInfo.avatar" class="user-avatar" />
                  <div class="avatar-glow"></div>
                </div>
                <div class="user-details">
                  <h2 class="username">
                    <span class="name-text">{{ userInfo.nickname }}</span>
                  </h2>
                  <div class="user-intro-container">
                    <p class="user-intro" :class="{ expanded: isIntroExpanded }">{{ userInfo.introduction || "这个人很懒，什么都没写~" }}</p>
                    <button v-if="userInfo.introduction && userInfo.introduction.length > 50" class="intro-expand-btn" @click="toggleIntroExpand">
                      <el-icon>
                        <ArrowDown v-if="!isIntroExpanded" />
                        <ArrowUp v-else />
                      </el-icon>
                    </button>
                  </div>
                  <div class="user-meta">
                    <span class="login-address" v-if="userInfo.loginAddress">
                      <el-icon><Location /></el-icon>
                      {{ userInfo.loginAddress }}
                    </span>
                    <span class="join-time">
                      <el-icon><Calendar /></el-icon>
                      {{ userInfo.createTime }}
                    </span>
                  </div>
                </div>
              </div>

              <!-- 用户统计信息 -->
              <div class="user-stats">
                <div class="stat-item" v-for="(stat, index) in stats" :key="stat.label" :style="{ animationDelay: (index * 0.1) + 's' }">
                  <span class="stat-number">
                    <CountTo :start="0" :end="stat.value" :duration="2000" />
                  </span>
                  <span class="stat-label">{{ stat.label }}</span>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="user-actions" v-if="!isCurrentUser">
                <el-button
                  :type="isFollowed ? 'default' : 'primary'"
                  :icon="isFollowed ? null : Plus"
                  @click="handleFollow"
                  :loading="followLoading"
                  :class="{ 'followed-btn': isFollowed }"
                  @mouseenter="handleFollowButtonHover(true)"
                  @mouseleave="handleFollowButtonHover(false)"
                  class="action-btn follow-btn"
                >
                  {{ followButtonText }}
                </el-button>
                <el-button :icon="Message" @click="handleMessage" class="action-btn message-btn">私信</el-button>
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import { Plus, Message, ArrowDown, ArrowUp, Location, Calendar } from "@element-plus/icons-vue";
import CountTo from "@/components/CountTo.vue";

// 定义 props
const props = defineProps({
  userInfo: {
    type: Object,
    default: () => null,
  },
  userLoading: {
    type: Boolean,
    default: false,
  },
  totalViews: {
    type: Number,
    default: 0,
  },
  isCurrentUser: {
    type: Boolean,
    default: false,
  },
  isFollowed: {
    type: Boolean,
    default: false,
  },
  followLoading: {
    type: Boolean,
    default: false,
  },
});

// 定义 emits
const emit = defineEmits(["follow", "message"]);

// 个人介绍展开状态
const isIntroExpanded = ref(false);

// 关注按钮文字状态
const isHoveringFollowButton = ref(false);

// 统计数字（用于动画）
const stats = computed(() => [
  { label: "文章", value: props.userInfo?.articleCount || 0 },
  { label: "粉丝", value: props.userInfo?.fansCount || 0 },
  { label: "关注", value: props.userInfo?.followCount || 0 },
  { label: "阅读量", value: props.totalViews || 0 },
]);

// 计算关注按钮显示的文字
const followButtonText = computed(() => {
  if (!props.isFollowed) {
    return "关注";
  }
  return isHoveringFollowButton.value ? "取消关注" : "已关注";
});

// 切换个人介绍展开状态
const toggleIntroExpand = () => {
  isIntroExpanded.value = !isIntroExpanded.value;
};

// 处理关注按钮悬停状态
const handleFollowButtonHover = (isHovering) => {
  isHoveringFollowButton.value = isHovering;
};

// 处理关注事件
const handleFollow = () => {
  emit("follow");
};

// 处理私信事件
const handleMessage = () => {
  emit("message");
};

// 监听 userInfo 变化，重置展开状态
watch(
  () => props.userInfo,
  () => {
    isIntroExpanded.value = false;
  },
  { immediate: true }
);
</script>

<style lang="scss" scoped>
// 用户信息区域
.user-profile-section {
  --bg-card: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.85);
  --border-color: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.5);
  --shadow-color: rgba(0, 0, 0, 0.06);
  --shadow-color-light: rgba(0, 0, 0, 0.04);
  --avatar-border: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.9);
  --glow-color: rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.2);
  --info-bg: rgba(var(--el-color-info-rgb, 144, 147, 153), 0.08);
  --info-bg-hover: rgba(var(--el-color-info-rgb, 144, 147, 153), 0.12);
  --stats-border: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.6);
  --btn-bg: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.8);
  --primary-rgb: var(--el-color-primary-rgb, 64, 158, 255);
  --success-rgb: var(--el-color-success-rgb, 103, 194, 58);
  --danger-rgb: var(--el-color-danger-rgb, 245, 108, 108);
  padding: 20px 0 0 0;

  // 黑夜模式适配
  html.dark & {
    --bg-card: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.85);
    --border-color: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.5);
    --shadow-color: rgba(0, 0, 0, 0.2);
    --shadow-color-light: rgba(0, 0, 0, 0.1);
    --avatar-border: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.9);
    --glow-color: rgba(var(--el-color-primary-rgb, 96, 168, 255), 0.15);
    --info-bg: rgba(var(--el-color-info-rgb, 148, 163, 184), 0.1);
    --info-bg-hover: rgba(var(--el-color-info-rgb, 148, 163, 184), 0.15);
    --stats-border: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.6);
    --btn-bg: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.8);
  }

  // 用户信息卡片
  .user-profile-card {
    position: relative;
    background: var(--bg-card);
    backdrop-filter: blur(20px);
    border-radius: 16px;
    padding: 32px;
    border: 1px solid var(--border-color);
    box-shadow:
      0 4px 24px var(--shadow-color),
      0 1px 3px var(--shadow-color-light);
    transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: hidden;

    // 卡片加载完成后的动画
    &.card-loaded {
      animation: cardSlideUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) forwards;
    }

    // 装饰性背景光晕
    &::before {
      content: "";
      position: absolute;
      top: -50%;
      right: -10%;
      width: 300px;
      height: 300px;
      background: radial-gradient(circle, var(--glow-color) 0%, transparent 70%);
      border-radius: 50%;
      pointer-events: none;
      transition: transform 0.6s ease;
    }

    &:hover::before {
      transform: scale(1.1);
    }

    // 骨架屏样式
    .skeleton-profile {
      display: flex;
      align-items: center;
      gap: 24px;

      .skeleton-info {
        flex: 1;
      }
    }

    // 用户信息内容
    .user-profile-content {
      // 用户基本信息
      .user-basic-info {
        display: flex;
        align-items: flex-start;
        gap: 28px;
        margin-bottom: 32px;

        // 头像包装器
        .avatar-wrapper {
          position: relative;
          flex-shrink: 0;

          .user-avatar {
            position: relative;
            z-index: 2;
            border: 4px solid var(--avatar-border);
            box-shadow: 0 8px 32px var(--shadow-color);
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);

            &:hover {
              transform: scale(1.05) rotate(2deg);
              box-shadow: 0 12px 40px var(--shadow-color);
            }
          }

          // 头像光晕效果
          .avatar-glow {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 140px;
            height: 140px;
            border-radius: 50%;
            background: radial-gradient(circle, var(--glow-color) 0%, transparent 70%);
            animation: pulseGlow 3s ease-in-out infinite;
            z-index: 1;
          }
        }

        .user-details {
          flex: 1;
          min-width: 0;
          overflow: hidden;

          .username {
            font-size: 28px;
            font-weight: 700;
            margin: 0 0 10px 0;
            color: var(--el-text-color-primary);
            background: linear-gradient(135deg, var(--el-text-color-primary) 0%, var(--el-text-color-regular) 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: var(--el-text-color-primary);
            background-clip: text;
            display: inline-block;
            animation: textReveal 0.8s cubic-bezier(0.4, 0, 0.2, 1) 0.2s both;

            .name-text {
              position: relative;

              &::after {
                content: "";
                position: absolute;
                bottom: -4px;
                left: 0;
                width: 40px;
                height: 3px;
                background: linear-gradient(90deg, var(--el-color-primary) 0%, transparent 100%);
                border-radius: 2px;
                animation: underlineGrow 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.5s both;
              }
            }
          }

          // 个人介绍容器
          .user-intro-container {
            position: relative;
            margin: 0 0 14px 0;
            animation: fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.3s both;

            // 个人介绍文本
            .user-intro {
              font-size: 14px;
              margin: 0;
              color: var(--el-text-color-regular);
              line-height: 1.6;
              word-wrap: break-word;
              word-break: break-all;
              overflow-wrap: break-word;
              max-width: 100%;

              // 手机端：默认限制两行
              @media (max-width: 768px) {
                display: -webkit-box;
                -webkit-line-clamp: 2;
                line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                text-overflow: ellipsis;
                padding-right: 32px;

                &.expanded {
                  display: block;
                  -webkit-line-clamp: unset;
                  line-clamp: unset;
                  overflow: visible;
                  padding-right: 0;
                }
              }
            }

            // 展开/收起按钮
            .intro-expand-btn {
              display: none;
              background: none;
              border: none;
              cursor: pointer;
              padding: 4px;
              margin-left: 8px;
              color: var(--el-color-primary);
              transition: all 0.3s ease;
              border-radius: 50%;

              &:hover {
                background-color: var(--el-color-primary-light-9);
                transform: scale(1.1);
              }

              .el-icon {
                font-size: 14px;
              }

              @media (max-width: 768px) {
                display: inline-flex;
                align-items: center;
                justify-content: center;
                position: absolute;
                right: 4px;
                top: 50%;
                transform: translateY(-50%);
                width: 20px;
                height: 20px;
                background-color: var(--btn-bg);
                box-shadow: 0 1px 3px var(--shadow-color);
              }
            }
          }

          .user-meta {
            display: flex;
            gap: 16px;
            flex-wrap: wrap;
            animation: fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.4s both;

            .login-address,
            .join-time {
              display: inline-flex;
              align-items: center;
              gap: 6px;
              font-size: 13px;
              color: var(--el-text-color-secondary);
              padding: 4px 10px;
              background: var(--info-bg);
              border-radius: 12px;
              transition: all 0.3s ease;

              &:hover {
                background: var(--info-bg-hover);
              }

              .el-icon {
                font-size: 14px;
              }
            }

            @media (max-width: 768px) {
              font-size: 11px;
              gap: 8px;

              .login-address,
              .join-time {
                padding: 3px 8px;

                .el-icon {
                  font-size: 12px;
                }
              }
            }
          }
        }
      }

      // 统计信息
      .user-stats {
        display: grid;
        grid-template-columns: repeat(4, 1fr);
        gap: 16px;
        padding: 24px 0;
        border-top: 1px solid var(--stats-border);
        border-bottom: 1px solid var(--stats-border);
        margin-bottom: 24px;

        .stat-item {
          text-align: center;
          opacity: 0;
          animation: statFadeIn 0.5s cubic-bezier(0.4, 0, 0.2, 1) forwards;

          .stat-number {
            display: block;
            font-size: 32px;
            font-weight: 700;
            background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 6px;
            letter-spacing: -0.5px;
          }

          .stat-label {
            font-size: 13px;
            color: var(--el-text-color-secondary);
            text-transform: uppercase;
            letter-spacing: 0.5px;
          }
        }

        @media (max-width: 992px) {
          gap: 12px;

          .stat-item {
            .stat-number {
              font-size: 24px;
            }
          }
        }

        @media (max-width: 768px) {
          grid-template-columns: repeat(2, 1fr);
          gap: 20px;
          padding: 16px 0;

          .stat-item {
            .stat-number {
              font-size: 28px;
            }

            .stat-label {
              font-size: 12px;
            }
          }
        }
      }

      // 操作按钮
      .user-actions {
        display: flex;
        gap: 14px;
        justify-content: center;
        animation: fadeInUp 0.6s cubic-bezier(0.4, 0, 0.2, 1) 0.5s both;

        .action-btn {
          padding: 12px 32px;
          font-size: 15px;
          font-weight: 500;
          border-radius: 24px;
          min-width: 110px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          position: relative;
          overflow: hidden;

          // 按钮光晕效果
          &::before {
            content: "";
            position: absolute;
            top: 50%;
            left: 50%;
            width: 0;
            height: 0;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.2);
            transform: translate(-50%, -50%);
            transition: width 0.6s ease, height 0.6s ease;
          }

          &:active::before {
            width: 200px;
            height: 200px;
          }

          &.follow-btn {
            &.el-button--primary {
              background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
              border: none;
              box-shadow: 0 4px 16px rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.3);

              &:hover {
                box-shadow: 0 6px 24px rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.4);
                transform: translateY(-2px);
              }
            }

            &.followed-btn {
              border-color: var(--el-color-success);
              color: var(--el-color-success);
              background: rgba(var(--el-color-success-rgb, 103, 194, 58), 0.08);

              &:hover {
                background-color: var(--el-color-danger);
                border-color: var(--el-color-danger);
                color: white;
                box-shadow: 0 4px 16px rgba(var(--el-color-danger-rgb, 245, 108, 108), 0.3);
                transform: translateY(-2px);
              }
            }
          }

          &.message-btn {
            background: var(--btn-bg);
            border: 1px solid var(--el-border-color);

            &:hover {
              border-color: var(--el-color-primary);
              color: var(--el-color-primary);
              box-shadow: 0 4px 16px rgba(var(--el-color-primary-rgb, 64, 158, 255), 0.2);
              transform: translateY(-2px);
            }
          }
        }

        @media (max-width: 768px) {
          gap: 10px;

          .action-btn {
            padding: 10px 20px;
            min-width: 90px;
            font-size: 14px;
          }
        }
      }
    }
  }
}

// 动画定义
@keyframes cardSlideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes pulseGlow {
  0%, 100% {
    opacity: 0.6;
    transform: translate(-50%, -50%) scale(1);
  }
  50% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(1.05);
  }
}

@keyframes textReveal {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes underlineGrow {
  from {
    width: 0;
  }
  to {
    width: 40px;
  }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(15px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes statFadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: 992px) {
  .user-profile-section {
    .user-profile-card {
      .user-profile-content {
        .user-basic-info {
          gap: 20px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .user-profile-section {
    padding: 10px 0;

    .user-profile-card {
      padding: 20px;
      border-radius: 12px;

      .user-profile-content {
        .user-basic-info {
          margin-bottom: 20px;
          flex-direction: column;
          align-items: center;
          text-align: center;

          .avatar-wrapper {
            margin: 0 auto 16px;
          }

          .user-details {
            .username {
              font-size: 22px;
            }

            .user-meta {
              justify-content: center;
            }
          }
        }

        .user-actions {
          flex-direction: column;

          .action-btn {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>
