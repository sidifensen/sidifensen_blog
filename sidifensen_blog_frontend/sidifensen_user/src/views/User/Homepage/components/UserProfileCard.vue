<template>
  <div class="user-profile-section">
    <div class="container">
      <!-- 用户基本信息卡片 -->
      <div class="user-profile-card">
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
              <!-- 用户头像和基本信息 -->
              <div class="user-basic-info">
                <el-avatar :size="120" :src="userInfo.avatar" class="user-avatar" />
                <div class="user-details">
                  <h2 class="username">{{ userInfo.nickname }}</h2>
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
                    <span class="login-address" v-if="userInfo.loginAddress">IP属地：{{ userInfo.loginAddress }}</span>
                    <span class="join-time">加入时间：{{ userInfo.createTime }}</span>
                  </div>
                </div>
              </div>

              <!-- 用户统计信息 -->
              <div class="user-stats">
                <div class="stat-item">
                  <span class="stat-number">{{ articleStatistics?.publishedCount || 0 }}</span>
                  <span class="stat-label">文章</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userInfo.fansCount || 0 }}</span>
                  <span class="stat-label">粉丝</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ userInfo.followCount || 0 }}</span>
                  <span class="stat-label">关注</span>
                </div>
                <div class="stat-item">
                  <span class="stat-number">{{ totalViews }}</span>
                  <span class="stat-label">阅读量</span>
                </div>
              </div>

              <!-- 操作按钮 -->
              <div class="user-actions" v-if="!isCurrentUser">
                <el-button type="primary" :icon="Plus" @click="handleFollow" :loading="followLoading">
                  {{ isFollowed ? "已关注" : "关注" }}
                </el-button>
                <el-button :icon="Message" @click="handleMessage">私信</el-button>
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { Plus, Message, ArrowDown, ArrowUp } from "@element-plus/icons-vue";

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
  articleStatistics: {
    type: Object,
    default: () => null,
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

// 切换个人介绍展开状态
const toggleIntroExpand = () => {
  isIntroExpanded.value = !isIntroExpanded.value;
};

// 处理关注事件
const handleFollow = () => {
  emit("follow");
};

// 处理私信事件
const handleMessage = () => {
  emit("message");
};
</script>

<style lang="scss" scoped>
// 全局变量
$primary-color: #409eff;
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$border-color: #dcdfe6;
$bg-color: #f5f7fa;

// 工具类
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 10px;
}

// 用户信息区域
.user-profile-section {
  padding: 10px 0 0 0px;
  color: var(--el-text-color-primary);

  // 用户信息卡片
  .user-profile-card {
    background: var(--el-border-color-lighter);
    backdrop-filter: blur(10px);
    border-radius: 8px;
    padding: 30px;
    border: 1px solid var(--el-border-color);
    box-shadow: 0 2px 12px var(--el-border-color-light);

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
        align-items: center;
        gap: 24px;
        margin-bottom: 30px;

        .user-avatar {
          border: 4px solid rgba(255, 255, 255, 0.3);
          box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
        }

        .user-details {
          flex: 1;
          min-width: 0; // 允许flex子元素收缩到内容宽度以下
          overflow: hidden; // 防止内容溢出

          .username {
            font-size: 24px;
            font-weight: 700;
            margin: 0 0 8px 0;
            color: var(--el-text-color-primary);
          }

          // 个人介绍容器
          .user-intro-container {
            position: relative;
            margin: 0 0 12px 0;

            // 个人介绍文本
            .user-intro {
              font-size: 14px;
              margin: 0;
              color: var(--el-text-color-primary);
              line-height: 1;
              word-wrap: break-word;
              word-break: break-all;
              overflow-wrap: break-word;
              max-width: 100%;

              // 手机端：默认限制两行，并为箭头按钮预留空间
              @media (max-width: 768px) {
                display: -webkit-box; // 使用webkit-box布局
                -webkit-line-clamp: 2; // 限制两行
                line-clamp: 2;
                -webkit-box-orient: vertical; // 垂直方向排列
                overflow: hidden;
                text-overflow: ellipsis;
                padding-right: 32px; // 为箭头按钮预留空间

                // 展开状态：显示全部内容，移除右侧padding
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

              // 只在手机端显示
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
                background-color: rgba(255, 255, 255, 0.9);
                box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
              }
            }
          }

          .user-meta {
            font-size: 13px;
            color: var(--el-text-color-regular);
            display: flex;
            gap: 10px;

            .join-time,
            .login-address {
              display: inline-flex;
              align-items: center;
              gap: 4px;
              white-space: nowrap;
            }

            // 手机端分两行显示
            @media (max-width: 768px) {
              font-size: 10px;
            }
          }
        }
      }

      // 统计信息
      .user-stats {
        display: flex;
        justify-content: space-around;
        padding: 10px 0;
        border-top: 1px solid var(--el-border-color);
        border-bottom: 1px solid var(--el-border-color);
        margin-bottom: 10px;

        .stat-item {
          text-align: center;

          .stat-number {
            display: block;
            font-size: 24px;
            font-weight: 700;
            color: var(--el-text-color-primary);
            margin-bottom: 4px;
          }

          .stat-label {
            font-size: 14px;
            color: var(--el-text-color-primary);
          }
        }
      }

      // 操作按钮
      .user-actions {
        display: flex;
        gap: 16px;
        justify-content: center;

        .el-button {
          padding: 12px 24px;
          font-size: 14px;
          border-radius: 20px;
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 992px) {
  .user-profile-section {
    .user-profile-card {
      .user-profile-content {
        .user-basic-info {
          gap: 5px;
        }

        .user-stats {
          flex-wrap: wrap;
          gap: 20px;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .user-profile-section {
    padding: 5px 0;

    .user-profile-card {
      padding: 10px;
      .user-profile-content {
        .user-basic-info {
          margin-bottom: 5px;
          .user-avatar {
            width: 70px;
            height: 70px;
          }
        }
      }
    }
  }
}
</style>
