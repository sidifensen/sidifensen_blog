<!--
  简洁工具感 (Linear Style)
  设计理念：功能至上，克制的视觉元素，Sharp 边缘，强调信息层次
-->
<template>
  <teleport to="body">
    <transition name="popover-a">
      <div v-if="visible" class="popover-overlay" @click="$emit('update:visible', false)">
        <div class="popover-container" @click.stop>
          <!-- 头部信息区 -->
          <div class="user-header">
            <div class="user-avatar-wrap" @click="goToUserHomepage">
              <el-avatar class="user-avatar" :size="56" :src="user.avatar" />
              <div class="avatar-ring"></div>
            </div>
            <div class="user-meta">
              <div class="name-row">
                <span class="nickname">{{ user.nickname }}</span>
                <VipBadge v-if="user.isVip" type="header" />
              </div>
              <span class="user-id">ID: {{ user.id }}</span>
            </div>
          </div>

          <!-- 统计数据 -->
          <div class="stats-bar">
            <div class="stat-item" @click="goToUserHomepage">
              <span class="stat-value">{{ user.fansCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item" @click="goToUserHomepage">
              <span class="stat-value">{{ user.followCount || 0 }}</span>
              <span class="stat-label">关注</span>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <span class="stat-value">{{ user.likeCount || 0 }}</span>
              <span class="stat-label">获赞</span>
            </div>
          </div>

          <!-- 操作列表 -->
          <div class="action-list">
            <div class="action-item" @click="goToUserHomepage">
              <svg
                class="action-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                <circle cx="12" cy="7" r="4" />
              </svg>
              <span>个人主页</span>
            </div>
            <div class="action-item" @click="goToSetting">
              <svg
                class="action-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <circle cx="12" cy="12" r="3" />
                <path
                  d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"
                />
              </svg>
              <span>个人设置</span>
            </div>
            <div class="action-item" @click="goToVipCenter">
              <svg
                class="action-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <polygon
                  points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
                />
              </svg>
              <span>会员中心</span>
            </div>
            <div class="action-item" @click="goToVipArticles">
              <svg
                class="action-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="1.5"
              >
                <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
              </svg>
              <span>会员专区</span>
            </div>
          </div>

          <!-- 底部退出 -->
          <div class="action-divider"></div>
          <div class="action-item logout" @click="logout">
            <svg
              class="action-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="1.5"
            >
              <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
              <polyline points="16 17 21 12 16 7" />
              <line x1="21" y1="12" x2="9" y2="12" />
            </svg>
            <span>退出登录</span>
          </div>
        </div>
      </div>
    </transition>
  </teleport>
</template>

<script setup>
import VipBadge from './VipBadge.vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  visible: Boolean,
  user: Object,
})

const emit = defineEmits(['update:visible'])

const router = useRouter()

const goToUserHomepage = () => {
  if (props.user?.id) {
    router.push(`/user/${props.user.id}`)
  }
  emit('update:visible', false)
}

const goToSetting = () => {
  router.push('/setting')
  emit('update:visible', false)
}

const goToVipCenter = () => {
  router.push('/vip')
  emit('update:visible', false)
}

const goToVipArticles = () => {
  router.push('/vip/articles')
  emit('update:visible', false)
}

const logout = () => {
  emit('update:visible', false)
  emit('logout')
}
</script>

<style lang="scss" scoped>
.popover-overlay {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  justify-content: flex-end;
  align-items: flex-start;
  padding: 60px 20px 0 0;
}

.popover-container {
  width: 280px;
  background: var(--el-bg-color);
  border: 1px solid var(--el-border-color);
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
}

// 用户头部
.user-header {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 20px 18px 16px;
  background: linear-gradient(180deg, var(--el-fill-color-light) 0%, transparent 100%);

  .user-avatar-wrap {
    position: relative;
    cursor: pointer;

    .user-avatar {
      transition: transform 0.2s ease;
    }

    .avatar-ring {
      position: absolute;
      inset: -3px;
      border-radius: 50%;
      border: 2px solid transparent;
      background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-5))
        border-box;
      -webkit-mask:
        linear-gradient(#fff 0 0) padding-box,
        linear-gradient(#fff 0 0);
      mask:
        linear-gradient(#fff 0 0) padding-box,
        linear-gradient(#fff 0 0);
      -webkit-mask-composite: xor;
      mask-composite: exclude;
      opacity: 0;
      transition: opacity 0.2s ease;
    }

    &:hover .avatar-ring {
      opacity: 1;
    }

    &:hover .user-avatar {
      transform: scale(1.05);
    }
  }

  .user-meta {
    display: flex;
    flex-direction: column;
    gap: 4px;

    .name-row {
      display: flex;
      align-items: center;
      gap: 8px;

      .nickname {
        font-size: 16px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }

    .user-id {
      font-size: 12px;
      color: var(--el-text-color-secondary);
      letter-spacing: 0.5px;
    }
  }
}

// 统计数据条
.stats-bar {
  display: flex;
  align-items: center;
  padding: 12px 18px;
  background: var(--el-fill-color-lighter);

  .stat-item {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2px;
    cursor: pointer;
    padding: 4px 8px;
    border-radius: 6px;
    transition: background 0.15s ease;

    &:hover {
      background: var(--el-fill-color-light);
    }

    .stat-value {
      font-size: 16px;
      font-weight: 700;
      color: var(--el-text-color-primary);
    }

    .stat-label {
      font-size: 11px;
      color: var(--el-text-color-secondary);
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }
  }

  .stat-divider {
    width: 1px;
    height: 28px;
    background: var(--el-border-color);
  }
}

// 操作列表
.action-list {
  padding: 8px 10px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.15s ease;
  color: var(--el-text-color-regular);

  &:hover {
    background: var(--el-fill-color-light);
    color: var(--el-text-color-primary);
  }

  .action-icon {
    width: 18px;
    height: 18px;
    flex-shrink: 0;
  }

  span {
    font-size: 14px;
    font-weight: 500;
  }

  &.logout {
    margin: 0 10px 10px;
    color: var(--el-color-danger);

    &:hover {
      background: var(--el-color-danger-light-9);
    }
  }
}

.action-divider {
  height: 1px;
  background: var(--el-border-color);
  margin: 4px 18px;
}

// 动画
.popover-a-enter-active {
  transition: all 0.2s cubic-bezier(0.16, 1, 0.3, 1);
}

.popover-a-leave-active {
  transition: all 0.15s ease-in;
}

.popover-a-enter-from,
.popover-a-leave-to {
  opacity: 0;
  transform: translateY(-8px) scale(0.96);
}

.popover-a-enter-from .popover-container,
.popover-a-leave-to .popover-container {
  transform: translateY(-12px) scale(0.97);
}
</style>
