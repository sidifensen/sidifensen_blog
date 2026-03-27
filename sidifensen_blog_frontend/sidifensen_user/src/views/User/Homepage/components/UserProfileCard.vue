<template>
  <div class="user-profile-section">
    <div class="container">
      <!-- 用户信息区域卡片 -->
      <div class="user-profile-card" :class="{ 'card-loaded': !userLoading }">
        <el-skeleton :loading="userLoading" animated>
          <template #template>
            <div class="skeleton-profile">
              <el-skeleton-item variant="circle" style="width: 88px; height: 88px" />
              <div class="skeleton-info">
                <el-skeleton-item variant="h3" style="width: 180px; margin: 12px 0" />
                <el-skeleton-item variant="text" style="width: 280px" />
                <el-skeleton-item variant="text" style="width: 220px; margin-top: 8px" />
              </div>
            </div>
          </template>

          <!-- 实际用户信息 -->
          <template #default>
            <div class="user-profile-content" v-if="userInfo">
              <div class="profile-shell">
                <div class="profile-main">
                  <!-- 用户基本信息 -->
                  <div class="user-basic-info">
                    <div class="avatar-wrapper">
                      <el-avatar :size="88" :src="userInfo.avatar" class="user-avatar" />
                    </div>
                    <div class="user-details">
                      <h2 class="username">
                        <span class="name-text">{{ userInfo.nickname }}</span>
                        <span v-if="userInfo.isVip" class="vip-badge">VIP</span>
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

                <!-- 用户统计信息 -->
                <div class="stats-panel">
                  <div class="stats-panel-title">数据概览</div>
                  <div class="user-stats">
                    <div class="stat-item" v-for="stat in stats" :key="stat.label">
                      <span class="stat-number">
                        <CountTo :start="0" :end="stat.value" :duration="2000" :compact="true" />
                      </span>
                      <span class="stat-label">{{ stat.label }}</span>
                    </div>
                  </div>
                </div>
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
  --bg-card: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.92);
  --border-color: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.7);
  --shadow-color: rgba(15, 23, 42, 0.08);
  --info-bg: rgba(var(--el-color-info-rgb, 144, 147, 153), 0.08);
  --info-bg-hover: rgba(var(--el-color-info-rgb, 144, 147, 153), 0.12);
  --stats-border: rgba(var(--el-border-color-rgb, 226, 232, 240), 0.6);
  --btn-bg: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.9);
  --avatar-border: rgba(var(--el-bg-color-rgb, 255, 255, 255), 0.95);
  --hero-overhang: 24px;
  padding: 20px 0 0 0;
  overflow: hidden;

  // 黑夜模式适配
  html.dark & {
    --bg-card: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.96);
    --border-color: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.75);
    --shadow-color: rgba(0, 0, 0, 0.22);
    --avatar-border: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.95);
    --info-bg: rgba(var(--el-color-info-rgb, 148, 163, 184), 0.1);
    --info-bg-hover: rgba(var(--el-color-info-rgb, 148, 163, 184), 0.15);
    --stats-border: rgba(var(--el-border-color-rgb, 51, 65, 85), 0.6);
    --btn-bg: rgba(var(--el-bg-color-rgb, 30, 41, 59), 0.95);
  }

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 16px;
  }

  // 用户信息卡片
  .user-profile-card {
    box-sizing: border-box;
    width: calc(100% + var(--hero-overhang));
    margin-left: calc(var(--hero-overhang) / -2);
    position: relative;
    background: var(--bg-card);
    border-radius: 20px;
    padding: 24px 28px;
    border: 1px solid var(--border-color);
    box-shadow: 0 8px 28px var(--shadow-color);
    transition: border-color 0.2s ease, box-shadow 0.2s ease;

    // 卡片加载完成后的动画
    &.card-loaded {
      animation: cardSlideUp 0.35s ease forwards;
    }

    // 骨架屏样式
    .skeleton-profile {
      display: flex;
      align-items: center;
      gap: 18px;

      .skeleton-info {
        flex: 1;
      }
    }

    // 用户信息内容
    .user-profile-content {
      .profile-shell {
        display: grid;
        grid-template-columns: minmax(0, 1.35fr) 280px;
        gap: 18px;
        align-items: stretch;

        .profile-main {
          display: flex;
          flex-direction: column;
          justify-content: space-between;
          gap: 18px;
          min-width: 0;

          .user-basic-info {
            display: flex;
            align-items: center;
            gap: 18px;
            min-width: 0;
            flex: 1;

            .avatar-wrapper {
              flex-shrink: 0;

              .user-avatar {
                border: 3px solid var(--avatar-border);
                box-shadow: 0 6px 20px var(--shadow-color);
              }
            }

            .user-details {
              flex: 1;
              min-width: 0;

              .username {
                margin: 0 0 8px 0;
                display: flex;
                align-items: center;
                gap: 10px;
                flex-wrap: wrap;
                font-size: 30px;
                line-height: 1.2;
                font-weight: 700;
                color: var(--el-text-color-primary);

                .name-text {
                  display: inline-block;
                  max-width: 100%;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }

                .vip-badge {
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                  height: 28px;
                  padding: 0 10px;
                  border-radius: 999px;
                  background: rgba(var(--el-color-warning-rgb, 230, 162, 60), 0.14);
                  color: var(--el-color-warning);
                  font-size: 12px;
                  font-weight: 700;
                }
              }

              .user-intro-container {
                position: relative;
                margin: 0 0 12px 0;

                .user-intro {
                  margin: 0;
                  font-size: 14px;
                  line-height: 1.65;
                  color: var(--el-text-color-regular);
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

                .intro-expand-btn {
                  display: inline-flex;
                  align-items: center;
                  justify-content: center;
                  position: absolute;
                  right: 0;
                  top: 2px;
                  width: 22px;
                  height: 22px;
                  padding: 0;
                  background: var(--btn-bg);
                  border: 1px solid var(--border-color);
                  border-radius: 999px;
                  color: var(--el-color-primary);
                  cursor: pointer;

                  .el-icon {
                    font-size: 12px;
                  }
                }
              }

              .user-meta {
                display: flex;
                gap: 10px;
                flex-wrap: wrap;

                .login-address,
                .join-time {
                  display: inline-flex;
                  align-items: center;
                  gap: 6px;
                  font-size: 12px;
                  color: var(--el-text-color-secondary);
                  padding: 5px 10px;
                  background: var(--info-bg);
                  border-radius: 999px;

                  .el-icon {
                    font-size: 13px;
                  }
                }
              }
            }
          }

          .user-actions {
            display: flex;
            gap: 10px;
            flex-shrink: 0;

            .action-btn {
              min-width: 96px;
              height: 38px;
              padding: 0 18px;
              font-size: 14px;
              font-weight: 500;
              border-radius: 10px;
              transition: background-color 0.2s ease, border-color 0.2s ease, color 0.2s ease;
              box-shadow: none !important;

              &.follow-btn {
                &.followed-btn {
                  border-color: var(--el-color-success);
                  color: var(--el-color-success);
                  background: rgba(var(--el-color-success-rgb, 103, 194, 58), 0.08);

                  &:hover {
                    border-color: var(--el-color-danger);
                    color: var(--el-color-danger);
                    background: rgba(var(--el-color-danger-rgb, 245, 108, 108), 0.08);
                  }
                }
              }

              &.message-btn {
                background: var(--btn-bg);
                border: 1px solid var(--border-color);

                &:hover {
                  border-color: var(--el-color-primary);
                  color: var(--el-color-primary);
                }
              }
            }
          }
        }

        .stats-panel {
          padding: 16px;
          background: var(--info-bg);
          border: 1px solid var(--stats-border);
          border-radius: 16px;

          .stats-panel-title {
            margin-bottom: 12px;
            font-size: 13px;
            font-weight: 600;
            color: var(--el-text-color-secondary);
          }

          .user-stats {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 10px;

            .stat-item {
              display: flex;
              flex-direction: column;
              gap: 6px;
              padding: 14px 10px;
              background: var(--bg-card);
              border: 1px solid var(--stats-border);
              border-radius: 14px;
              text-align: center;

              .stat-number {
                display: block;
                font-size: 24px;
                line-height: 1;
                font-weight: 700;
                color: var(--el-color-primary);
              }

              .stat-label {
                font-size: 12px;
                color: var(--el-text-color-secondary);
              }
            }
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
    transform: translateY(18px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 响应式设计
@media (max-width: 992px) {
  .user-profile-section {
    --hero-overhang: 0px;

    .user-profile-card {
      width: 100%;
      margin-left: 0;
      .user-profile-content {
        .profile-shell {
          grid-template-columns: 1fr;

          .profile-main {
            .user-actions {
              justify-content: flex-start;
            }
          }
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
      border-radius: 16px;

      .user-profile-content {
        .profile-shell {
          gap: 14px;

          .profile-main {
            margin-bottom: 0;

            .user-basic-info {
              align-items: flex-start;
              gap: 14px;

              .avatar-wrapper {
                margin: 0;
              }

              .user-details {
                .username {
                  font-size: 22px;
                }

                .user-intro-container {
                  .user-intro {
                    font-size: 13px;
                  }
                }
              }
            }

            .user-actions {
              width: 100%;

              .action-btn {
                flex: 1;
              }
            }
          }

          .stats-panel {
            padding: 14px;

            .user-stats {
              gap: 8px;

              .stat-item {
                padding: 12px 8px;

                .stat-number {
                  font-size: 22px;
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
