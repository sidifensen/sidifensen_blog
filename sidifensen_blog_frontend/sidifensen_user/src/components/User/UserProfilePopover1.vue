<!--
  精致卡片感 (Card Style)
  设计理念：层次分明的卡片设计，柔和阴影，强调内容区块
-->
<template>
  <teleport to="body">
    <transition name="popover-b">
      <div v-if="visible" class="popover-overlay" @click="$emit('update:visible', false)">
        <div class="popover-container" @click.stop>
          <!-- 背景装饰 -->
          <div class="bg-decoration">
            <div class="circle circle-1"></div>
            <div class="circle circle-2"></div>
          </div>

          <!-- 用户信息卡片 -->
          <div class="profile-card">
            <div class="avatar-section">
              <div class="avatar-wrapper" @click="goToUserHomepage">
                <el-avatar class="user-avatar" :size="72" :src="user.avatar" />
                <div class="online-indicator"></div>
              </div>
              <div class="user-info">
                <div class="name-row">
                  <span class="nickname">{{ user.nickname }}</span>
                  <VipBadge v-if="user.isVip" type="header" />
                </div>
                <span class="user-bio">{{ user.bio || '这个人很懒，什么都没写' }}</span>
              </div>
            </div>

            <!-- 社交数据 -->
            <div class="social-stats">
              <div class="social-item" @click="goToUserHomepage">
                <span class="social-value">{{ user.fansCount || 0 }}</span>
                <span class="social-label">粉丝</span>
              </div>
              <div class="social-divider"></div>
              <div class="social-item" @click="goToUserHomepage">
                <span class="social-value">{{ user.followCount || 0 }}</span>
                <span class="social-label">关注</span>
              </div>
              <div class="social-divider"></div>
              <div class="social-item">
                <span class="social-value">{{ user.likeCount || 0 }}</span>
                <span class="social-label">获赞</span>
              </div>
            </div>
          </div>

          <!-- 会员卡片 (如果有会员) -->
          <div v-if="user.isVip" class="vip-banner">
            <div class="vip-icon">
              <svg viewBox="0 0 24 24" fill="currentColor" width="20" height="20">
                <polygon
                  points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
                />
              </svg>
            </div>
            <span class="vip-text">VIP 会员</span>
            <span class="vip-expire">有效期至 {{ user.vipExpireDate || '永久' }}</span>
          </div>

          <!-- 功能菜单 -->
          <div class="menu-section">
            <div class="menu-item" @click="goToUserHomepage">
              <div class="menu-icon-wrap primary">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" />
                  <circle cx="12" cy="7" r="4" />
                </svg>
              </div>
              <div class="menu-text-wrap">
                <span class="menu-title">个人主页</span>
                <span class="menu-desc">查看我的动态</span>
              </div>
              <svg
                class="arrow-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </div>

            <div class="menu-item" @click="goToSetting">
              <div class="menu-icon-wrap success">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <circle cx="12" cy="12" r="3" />
                  <path
                    d="M12 1v4M12 19v4M4.22 4.22l2.83 2.83M16.95 16.95l2.83 2.83M1 12h4M19 12h4M4.22 19.78l2.83-2.83M16.95 7.05l2.83-2.83"
                  />
                </svg>
              </div>
              <div class="menu-text-wrap">
                <span class="menu-title">个人设置</span>
                <span class="menu-desc">账号与安全</span>
              </div>
              <svg
                class="arrow-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </div>

            <div class="menu-item" @click="goToVipCenter">
              <div class="menu-icon-wrap warning">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <polygon
                    points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"
                  />
                </svg>
              </div>
              <div class="menu-text-wrap">
                <span class="menu-title">会员中心</span>
                <span class="menu-desc">尊享权益</span>
              </div>
              <svg
                class="arrow-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </div>

            <div class="menu-item" @click="goToVipArticles">
              <div class="menu-icon-wrap danger">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M4 19.5A2.5 2.5 0 0 1 6.5 17H20" />
                  <path d="M6.5 2H20v20H6.5A2.5 2.5 0 0 1 4 19.5v-15A2.5 2.5 0 0 1 6.5 2z" />
                </svg>
              </div>
              <div class="menu-text-wrap">
                <span class="menu-title">会员专区</span>
                <span class="menu-desc">专属内容</span>
              </div>
              <svg
                class="arrow-icon"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
              >
                <polyline points="9 18 15 12 9 6" />
              </svg>
            </div>
          </div>

          <!-- 底部退出 -->
          <div class="footer-section">
            <div class="menu-item logout" @click="logout">
              <div class="menu-icon-wrap">
                <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                  <path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4" />
                  <polyline points="16 17 21 12 16 7" />
                  <line x1="21" y1="12" x2="9" y2="12" />
                </svg>
              </div>
              <div class="menu-text-wrap">
                <span class="menu-title">退出登录</span>
              </div>
            </div>
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
}
</script>

<style lang="scss" scoped>
$primary: var(--el-color-primary);
$success: #67c23a;
$warning: #e6a23c;
$danger: #f56c6c;

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
  position: relative;
  width: 320px;
  background: var(--el-bg-color);
  border-radius: 20px;
  overflow: hidden;
  box-shadow:
    0 12px 40px rgba(0, 0, 0, 0.15),
    0 4px 12px rgba(0, 0, 0, 0.08);
}

// 背景装饰
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 100px;
  background: linear-gradient(135deg, var(--el-color-primary-light-8) 0%, transparent 100%);
  pointer-events: none;

  .circle {
    position: absolute;
    border-radius: 50%;
    opacity: 0.5;

    &.circle-1 {
      width: 120px;
      height: 120px;
      top: -40px;
      right: -20px;
      background: radial-gradient(circle, var(--el-color-primary-light-7) 0%, transparent 70%);
    }

    &.circle-2 {
      width: 80px;
      height: 80px;
      top: 20px;
      left: -20px;
      background: radial-gradient(circle, var(--el-color-primary-light-8) 0%, transparent 70%);
    }
  }
}

// 用户信息卡片
.profile-card {
  position: relative;
  padding: 24px 20px 16px;

  .avatar-section {
    display: flex;
    gap: 14px;
    margin-bottom: 16px;

    .avatar-wrapper {
      position: relative;
      cursor: pointer;
      transition: transform 0.2s ease;

      &:hover {
        transform: scale(1.05);
      }

      .online-indicator {
        position: absolute;
        bottom: 2px;
        right: 2px;
        width: 14px;
        height: 14px;
        background: $success;
        border: 3px solid var(--el-bg-color);
        border-radius: 50%;
      }
    }

    .user-info {
      flex: 1;
      display: flex;
      flex-direction: column;
      justify-content: center;
      gap: 6px;
      padding-top: 4px;

      .name-row {
        display: flex;
        align-items: center;
        gap: 8px;

        .nickname {
          font-size: 18px;
          font-weight: 700;
          color: var(--el-text-color-primary);
        }
      }

      .user-bio {
        font-size: 13px;
        color: var(--el-text-color-secondary);
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }

  .social-stats {
    display: flex;
    align-items: center;
    background: var(--el-fill-color-light);
    border-radius: 12px;
    padding: 12px 0;

    .social-item {
      flex: 1;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 2px;
      cursor: pointer;
      transition: transform 0.15s ease;

      &:hover {
        transform: translateY(-2px);
      }

      .social-value {
        font-size: 18px;
        font-weight: 700;
        color: var(--el-text-color-primary);
      }

      .social-label {
        font-size: 12px;
        color: var(--el-text-color-secondary);
      }
    }

    .social-divider {
      width: 1px;
      height: 32px;
      background: var(--el-border-color);
    }
  }
}

// VIP 横幅
.vip-banner {
  margin: 0 16px 12px;
  padding: 10px 14px;
  background: linear-gradient(135deg, #ffd700 0%, #ffb300 100%);
  border-radius: 10px;
  display: flex;
  align-items: center;
  gap: 10px;
  color: #5d4a00;

  .vip-icon {
    width: 28px;
    height: 28px;
    background: rgba(255, 255, 255, 0.5);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .vip-text {
    font-size: 14px;
    font-weight: 700;
    flex: 1;
  }

  .vip-expire {
    font-size: 11px;
    opacity: 0.8;
  }
}

// 菜单区域
.menu-section {
  padding: 8px 12px;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.15s ease;

  &:hover {
    background: var(--el-fill-color-light);

    .arrow-icon {
      opacity: 1;
      transform: translateX(0);
    }
  }

  .menu-icon-wrap {
    width: 38px;
    height: 38px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: var(--el-fill-color-light);
    color: var(--el-text-color-secondary);

    svg {
      width: 20px;
      height: 20px;
    }

    &.primary {
      background: var(--el-color-primary-light-9);
      color: var(--el-color-primary);
    }

    &.success {
      background: rgba($success, 0.1);
      color: $success;
    }

    &.warning {
      background: rgba($warning, 0.1);
      color: $warning;
    }

    &.danger {
      background: rgba($danger, 0.1);
      color: $danger;
    }
  }

  .menu-text-wrap {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 2px;

    .menu-title {
      font-size: 14px;
      font-weight: 600;
      color: var(--el-text-color-primary);
    }

    .menu-desc {
      font-size: 12px;
      color: var(--el-text-color-secondary);
    }
  }

  .arrow-icon {
    width: 16px;
    height: 16px;
    color: var(--el-text-color-secondary);
    opacity: 0;
    transform: translateX(-4px);
    transition: all 0.15s ease;
  }

  &.logout {
    .menu-icon-wrap {
      background: rgba($danger, 0.1);
      color: $danger;
    }

    .menu-title {
      color: $danger;
    }

    &:hover {
      background: rgba($danger, 0.05);
    }
  }
}

.footer-section {
  padding: 8px 12px 16px;
}

// 动画
.popover-b-enter-active {
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.popover-b-leave-active {
  transition: all 0.2s ease-in;
}

.popover-b-enter-from,
.popover-b-leave-to {
  opacity: 0;
  transform: scale(0.9) translateY(-20px);
}

html.dark {
  .vip-banner {
    background: linear-gradient(135deg, #b8860b 0%, #8b6914 100%);
    color: #fff5d9;
  }
}
</style>
