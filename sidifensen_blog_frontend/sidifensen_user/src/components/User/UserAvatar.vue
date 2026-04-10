<template>
  <div class="user-avatar" :class="{ 'user-avatar--clickable': clickable }" @click="handleClick">
    <ElAvatar :src="src" :size="avatarSize" class="user-avatar__image">
      <slot>{{ fallbackText }}</slot>
    </ElAvatar>

    <!-- 在线状态指示器 -->
    <span v-if="isOnline" class="user-avatar__online" />

    <!-- VIP 徽章 -->
    <VipBadge v-if="showVipBadge" :type="vipType" class="user-avatar__vip" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import VipBadge from '@/components/User/VipBadge.vue'

// 用户头像组件
// props:
//   src: 头像地址
//   size: 头像尺寸 (number | 'small' | 'default' | 'large'), 默认 40
//   isOnline: 是否显示在线状态
//   showVipBadge: 是否显示VIP标识
//   vipType: VIP徽章类型 ('simple' | 'card' | 'header')
//   clickable: 是否可点击
//   userId: 用户ID，用于跳转
const props = defineProps({
  src: {
    type: String,
    default: '',
  },
  size: {
    type: [Number, String],
    default: 40,
  },
  isOnline: {
    type: Boolean,
    default: false,
  },
  showVipBadge: {
    type: Boolean,
    default: false,
  },
  vipType: {
    type: String,
    default: 'simple',
    validator: (v) => ['simple', 'card', 'header'].includes(v),
  },
  clickable: {
    type: Boolean,
    default: false,
  },
  userId: {
    type: Number,
    default: null,
  },
})

const emit = defineEmits(['click'])

const router = useRouter()

// 计算头像尺寸
const avatarSize = computed(() => {
  if (typeof props.size === 'number') {
    return props.size
  }
  const sizeMap = {
    small: 32,
    default: 40,
    large: 56,
  }
  return sizeMap[props.size] || 40
})

// 备用文本（当无头像时显示）
const fallbackText = computed(() => {
  return ''
})

// 点击处理
const handleClick = () => {
  if (props.clickable && props.userId) {
    router.push(`/user/${props.userId}`)
  }
  emit('click', props.userId)
}
</script>

<style lang="scss" scoped>
.user-avatar {
  position: relative;
  display: inline-flex;

  // 头像图片
  .user-avatar__image {
    background: var(--bg-avatar, #f0f2f5);
  }

  // 在线状态指示器 - 绿色圆点
  .user-avatar__online {
    position: absolute;
    right: 0;
    bottom: 0;
    width: 10px;
    height: 10px;
    background: var(--online-indicator);
    border: 2px solid var(--bg-card);
    border-radius: 50%;
    transform: translate(20%, 20%);
  }

  // VIP 徽章 - 定位在右下角
  .user-avatar__vip {
    position: absolute;
    right: -4px;
    bottom: -2px;
    transform: translate(10%, 10%);
  }

  // 可点击样式
  &--clickable {
    cursor: pointer;

    &:hover {
      .user-avatar__image {
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }
    }
  }
}
</style>

<style lang="scss">
// 全局样式，处理 dark 模式
.user-avatar {
  .user-avatar__online {
    html.dark & {
      border-color: var(--bg-page, #0f172a);
    }
  }

  .user-avatar--clickable {
    &:hover {
      .user-avatar__image {
        html.dark & {
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.4);
        }
      }
    }
  }
}
</style>
