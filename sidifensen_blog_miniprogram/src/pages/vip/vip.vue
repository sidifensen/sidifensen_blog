<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getVipInfo, getVipStatus, createVipOrder, getAlipayUrl } from '@/api/vip'

const userStore = useUserStore()

const vipInfo = ref({
  name: 'VIP会员',
  price: 99,
  benefits: [
    { icon: 'file-text', text: '专属内容阅读' },
    { icon: 'close-circle', text: '无广告打扰' },
    { icon: 'flash', text: '优先体验新功能' },
    { icon: 'chat', text: '专属客服支持' }
  ]
})

const vipStatus = ref({
  isVip: false,
  vipEndTime: ''
})

const loading = ref(false)
const orderLoading = ref(false)

/**
 * 获取 VIP 信息
 */
async function fetchVipInfo() {
  try {
    const res = await getVipInfo()
    vipInfo.value = res.data || res
  } catch (err) {
    console.error('获取VIP信息失败', err)
  }
}

/**
 * 获取 VIP 状态
 */
async function fetchVipStatus() {
  try {
    const res = await getVipStatus()
    vipStatus.value = res.data || res
  } catch (err) {
    console.error('获取VIP状态失败', err)
  }
}

/**
 * 开通 VIP
 */
async function handleOpenVip() {
  if (!userStore.isLoggedIn) {
    uni.navigateTo({ url: '/pages/login/login' })
    return
  }

  try {
    orderLoading.value = true

    // 创建订单
    const orderRes = await createVipOrder({
      type: 1,
      price: vipInfo.value.price
    })

    const orderId = orderRes.data?.orderId

    if (orderId) {
      // 获取支付宝支付链接
      const payRes = await getAlipayUrl(orderId)

      // 打开支付页面
      if (payRes.data?.payUrl) {
        // #ifdef MP-WEIXIN
        uni.showToast({ title: '小程序不支持支付宝', icon: 'none' })
        // #endif

        // #ifdef H5
        window.location.href = payRes.data.payUrl
        // #endif
      }
    }
  } catch (err) {
    uni.showToast({ title: err.message || '创建订单失败', icon: 'none' })
  } finally {
    orderLoading.value = false
  }
}

onMounted(() => {
  fetchVipInfo()
  fetchVipStatus()
})
</script>

<template>
  <view class="vip-page">
    <!-- VIP 头部 -->
    <view class="vip-header">
      <view class="vip-badge">
        <uv-icon name="crown" color="#ffffff" size="24px" />
        <text class="vip-title">{{ vipStatus.isVip ? 'VIP会员' : '开通VIP' }}</text>
      </view>
      <view v-if="vipStatus.isVip" class="vip-expire">
        VIP有效期至：{{ vipStatus.vipEndTime }}
      </view>
    </view>

    <!-- 权益卡片 -->
    <view class="benefits-card card">
      <view class="benefits-title">会员权益</view>
      <view class="benefits-list">
        <view v-for="(benefit, index) in vipInfo.benefits" :key="index" class="benefit-item">
          <uv-icon :name="benefit.icon" color="var(--color-primary)" size="20px" />
          <text class="benefit-text">{{ benefit.text }}</text>
        </view>
      </view>
    </view>

    <!-- 套餐选择 -->
    <view class="price-card card">
      <view class="price-label">选择套餐</view>
      <view class="price-options">
        <view class="price-option selected">
          <view class="price-value">¥{{ vipInfo.price }}</view>
          <view class="price-period">年费会员</view>
        </view>
      </view>
    </view>

    <!-- 立即开通按钮 -->
    <view class="open-vip-btn">
      <button
        v-if="!vipStatus.isVip"
        class="btn-primary"
        :disabled="orderLoading"
        @click="handleOpenVip"
      >
        {{ orderLoading ? '跳转支付中...' : '立即开通' }}
      </button>
      <button v-else class="btn-disabled" disabled>
        当前已是VIP会员
      </button>
    </view>

    <!-- 支付说明 -->
    <view class="pay-notice">
      <view class="notice-title">支付说明</view>
      <view class="notice-text">
        1. VIP会员为年度订阅制，到期后可续费<br/>
        2. 支付方式：支付宝（安全可靠）<br/>
        3. 支付完成后请联系客服开通
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.vip-page {
  min-height: 100vh;
  background: var(--bg-page);
  padding: var(--spacing-lg);
}

.vip-header {
  text-align: center;
  padding: var(--spacing-2xl) 0;

  .vip-badge {
    display: inline-flex;
    align-items: center;
    gap: var(--spacing-sm);
    background: linear-gradient(135deg, #f59e0b, #d97706);
    padding: var(--spacing-md) var(--spacing-2xl);
    border-radius: var(--radius-full);

    .vip-title {
      font-size: 18px;
      font-weight: 600;
      color: #ffffff;
    }
  }

  .vip-expire {
    font-size: 14px;
    color: var(--text-muted);
    margin-top: var(--spacing-md);
  }
}

.benefits-card {
  .benefits-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: var(--spacing-lg);
  }

  .benefits-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-lg);

    .benefit-item {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);

      .benefit-text {
        font-size: 14px;
        color: var(--text-regular);
      }
    }
  }
}

.price-card {
  margin-top: var(--spacing-md);

  .price-label {
    font-size: 16px;
    font-weight: 600;
    color: var(--text-primary);
    margin-bottom: var(--spacing-lg);
  }

  .price-options {
    .price-option {
      padding: var(--spacing-lg);
      border: 2px solid var(--border);
      border-radius: var(--radius-md);
      text-align: center;

      &.selected {
        border-color: var(--color-primary);
        background: rgba(8, 145, 178, 0.05);
      }

      .price-value {
        font-size: 24px;
        font-weight: 700;
        color: var(--color-primary);
      }

      .price-period {
        font-size: 14px;
        color: var(--text-muted);
        margin-top: var(--spacing-sm);
      }
    }
  }
}

.open-vip-btn {
  margin-top: var(--spacing-xl);

  .btn-primary {
    width: 100%;
    height: 48px;
    background: var(--color-primary);
    color: #ffffff;
    border: none;
    border-radius: var(--radius-md);
    font-size: 16px;
    font-weight: 500;
  }

  .btn-disabled {
    width: 100%;
    height: 48px;
    background: var(--border);
    color: var(--text-muted);
    border: none;
    border-radius: var(--radius-md);
    font-size: 16px;
  }
}

.pay-notice {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--bg-card);
  border-radius: var(--radius-md);

  .notice-title {
    font-size: 14px;
    font-weight: 500;
    color: var(--text-primary);
    margin-bottom: var(--spacing-md);
  }

  .notice-text {
    font-size: 13px;
    color: var(--text-muted);
    line-height: 1.8;
  }
}
</style>
