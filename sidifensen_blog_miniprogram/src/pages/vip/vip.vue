<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getVipPlans, getVipStatus, createVipOrder } from '@/api/vip'

const userStore = useUserStore()

// 权益信息（静态）
const benefits = [
  { icon: 'file-text', text: '专属内容阅读' },
  { icon: 'close-circle', text: '无广告打扰' },
  { icon: 'flash', text: '优先体验新功能' },
  { icon: 'chat', text: '专属客服支持' }
]

// 套餐列表
const plans = ref([])
const selectedPlan = ref(null)

const vipStatus = ref({
  isVip: false,
  vipEndTime: ''
})

const loading = ref(false)
const orderLoading = ref(false)

/**
 * 获取 VIP 套餐列表
 */
async function fetchVipPlans() {
  try {
    const res = await getVipPlans()
    plans.value = res.data || res || []
    // 默认选中第一个套餐
    if (plans.value.length > 0 && !selectedPlan.value) {
      selectedPlan.value = plans.value[0]
    }
  } catch (err) {
    console.error('获取VIP套餐失败', err)
  }
}

/**
 * 获取 VIP 状态
 */
async function fetchVipStatus() {
  try {
    const res = await getVipStatus()
    const data = res.data || res
    vipStatus.value = {
      isVip: data?.isVip || false,
      vipEndTime: data?.vipExpireTime ? formatDate(data.vipExpireTime) : ''
    }
  } catch (err) {
    console.error('获取VIP状态失败', err)
  }
}

/**
 * 格式化日期
 */
function formatDate(dateStr) {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
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
      planCode: selectedPlan.value.code,
      clientType: 'H5'
    })

    const orderData = orderRes.data

    // 打开支付页面
    if (orderData?.payUrl) {
      // #ifdef MP-WEIXIN
      uni.showToast({ title: '小程序不支持支付宝', icon: 'none' })
      // #endif

      // #ifdef H5
      window.location.href = orderData.payUrl
      // #endif
    } else if (orderData?.formHtml) {
      // 如果返回的是表单HTML，写入页面并自动提交
      // #ifdef H5
      const div = document.createElement('div')
      div.innerHTML = orderData.formHtml
      document.body.appendChild(div)
      div.querySelector('form')?.submit()
      // #endif
    }
  } catch (err) {
    uni.showToast({ title: err.message || '创建订单失败', icon: 'none' })
  } finally {
    orderLoading.value = false
  }
}

onMounted(() => {
  fetchVipPlans()
  fetchVipStatus()
})
</script>

<template>
  <view class="vip-page">
    <!-- VIP 头部 -->
    <view class="vip-header">
      <view class="vip-badge">
        <u-icon name="crown" color="#ffffff" size="24px" />
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
        <view v-for="(benefit, index) in benefits" :key="index" class="benefit-item">
          <u-icon :name="benefit.icon" color="var(--u-type-primary)" size="20px" />
          <text class="benefit-text">{{ benefit.text }}</text>
        </view>
      </view>
    </view>

    <!-- 套餐选择 -->
    <view class="price-card card">
      <view class="price-label">选择套餐</view>
      <view class="price-options">
        <view
          v-for="plan in plans"
          :key="plan.code"
          class="price-option"
          :class="{ selected: selectedPlan?.code === plan.code }"
          @click="selectedPlan = plan"
        >
          <view class="price-value">¥{{ plan.priceYuan }}</view>
          <view class="price-period">{{ plan.name }}</view>
          <view class="price-desc">{{ plan.description }}</view>
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
  background: var(--u-bg-color);
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
    color: var(--u-tips-color);
    margin-top: var(--spacing-md);
  }
}

.benefits-card {
  .benefits-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--u-main-color);
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
        color: var(--u-content-color);
      }
    }
  }
}

.price-card {
  margin-top: var(--spacing-md);

  .price-label {
    font-size: 16px;
    font-weight: 600;
    color: var(--u-main-color);
    margin-bottom: var(--spacing-lg);
  }

  .price-options {
    .price-option {
      padding: var(--spacing-lg);
      border: 2px solid var(--u-border-color);
      border-radius: var(--radius-md);
      text-align: center;
      margin-bottom: var(--spacing-md);
      cursor: pointer;

      &:last-child {
        margin-bottom: 0;
      }

      &.selected {
        border-color: var(--u-type-primary);
        background: rgba(8, 145, 178, 0.05);
      }

      .price-value {
        font-size: 24px;
        font-weight: 700;
        color: var(--u-type-primary);
      }

      .price-period {
        font-size: 14px;
        color: var(--u-tips-color);
        margin-top: var(--spacing-sm);
      }

      .price-desc {
        font-size: 12px;
        color: var(--u-tips-color);
        margin-top: var(--spacing-xs);
      }
    }
  }
}

.open-vip-btn {
  margin-top: var(--spacing-xl);

  .btn-primary {
    width: 100%;
    height: 48px;
    background: var(--u-type-primary);
    color: #ffffff;
    border: none;
    border-radius: var(--radius-md);
    font-size: 16px;
    font-weight: 500;
  }

  .btn-disabled {
    width: 100%;
    height: 48px;
    background: var(--u-border-color);
    color: var(--u-tips-color);
    border: none;
    border-radius: var(--radius-md);
    font-size: 16px;
  }
}

.pay-notice {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--u-bg-white);
  border-radius: var(--radius-md);

  .notice-title {
    font-size: 14px;
    font-weight: 500;
    color: var(--u-main-color);
    margin-bottom: var(--spacing-md);
  }

  .notice-text {
    font-size: 13px;
    color: var(--u-tips-color);
    line-height: 1.8;
  }
}
</style>
