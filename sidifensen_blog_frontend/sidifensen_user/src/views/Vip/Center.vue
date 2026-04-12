<template>
  <div class="vip-square-body">
    <div class="vip-container">
      <!-- 页面头部 -->
      <div class="page-hero">
        <div class="hero-content">
          <div class="hero-kicker">VIP Center</div>
          <h1>会员中心</h1>
          <p>解锁专属权益，提升创作体验</p>
        </div>
      </div>

      <!-- 内容布局 -->
      <div class="content-layout">
        <!-- 主内容区 -->
        <div class="main-content">
          <!-- 会员状态卡片 -->
          <div class="status-cards">
            <div class="status-card">
              <span class="card-label">会员状态</span>
              <strong class="card-value" :class="{ active: vipInfo.isVip }">{{
                statusText
              }}</strong>
              <span class="card-meta">{{ expireText }}</span>
            </div>
            <div class="status-card">
              <span class="card-label">AI 配额</span>
              <strong class="card-value"
                >{{ vipInfo.aiRemainingQuota ?? 0 }} / {{ vipInfo.aiDailyQuota || 100 }}</strong
              >
              <span class="card-meta">今日剩余 / 总量</span>
            </div>
          </div>

          <!-- 套餐选择 -->
          <div class="section-card">
            <div class="section-title">
              <span class="title-text">选择套餐</span>
              <span class="title-hint">支付成功后立即生效</span>
            </div>

            <!-- 测试提示 -->
            <div class="test-hint">
              <span>测试账号：kmawrv8806@sandbox.com / 111111</span>
            </div>

            <!-- 会员专区入口 -->
            <div class="vip-entry" @click="goToVipArticles">
              <div class="entry-info">
                <span class="entry-icon">⭐</span>
                <div class="entry-text">
                  <strong>会员专区</strong>
                  <span>开通后直接进入会员专区查看 VIP 文章</span>
                </div>
              </div>
              <div class="entry-arrow">→</div>
            </div>

            <!-- 套餐列表 -->
            <div class="plans-list">
              <div
                v-for="plan in plans"
                :key="plan.code"
                class="plan-item"
                :class="{ selected: selectedPlanCode === plan.code }"
                @click="selectedPlanCode = plan.code"
              >
                <div class="plan-info">
                  <span class="plan-name">{{ plan.name }}</span>
                  <span class="plan-days">{{ plan.days }} 天</span>
                </div>
                <div class="plan-price">
                  <span class="price-unit">¥</span>
                  <span class="price-value">{{ plan.priceYuan }}</span>
                </div>
                <p class="plan-desc">{{ plan.description || '适合稳定阅读和持续创作使用' }}</p>
                <button
                  class="plan-btn"
                  :disabled="submitting && selectedPlanCode === plan.code"
                  @click.stop="handleCreateOrder(plan)"
                >
                  {{ submitting && selectedPlanCode === plan.code ? '跳转中...' : '开通' }}
                </button>
              </div>
            </div>
          </div>

          <!-- 订单记录 -->
          <div class="section-card">
            <div class="section-title">
              <span class="title-text">订单记录</span>
              <span class="title-hint">支付页返回后可在这里查看状态</span>
            </div>

            <div v-if="orders.length" class="orders-list">
              <div v-for="order in orders" :key="order.orderNo" class="order-item">
                <div class="order-info">
                  <strong class="order-name">{{ order.planName }}</strong>
                  <span class="order-meta"
                    >{{ order.orderNo }} · {{ formatDate(order.createTime) }}</span
                  >
                </div>
                <div class="order-right">
                  <span class="order-price">¥{{ order.priceYuan }}</span>
                  <span class="order-status" :class="order.status?.toLowerCase()">
                    {{ orderStatusText(order.status) }}
                  </span>
                  <div v-if="order.status === 'PAYING'" class="order-actions">
                    <button class="btn-cancel" @click="handleCancelOrder(order)">取消</button>
                    <button class="btn-pay" @click="handleRepayOrder(order)">去支付</button>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="empty-state">
              <span>暂无订单记录</span>
            </div>
          </div>
        </div>

        <!-- 侧边栏 -->
        <aside class="sidebar">
          <!-- VIP 权益 -->
          <div class="sidebar-card">
            <div class="card-title">VIP 权益</div>
            <div class="benefit-list">
              <div class="benefit-item">
                <span class="benefit-icon">📖</span>
                <div class="benefit-text">
                  <strong>VIP 文章可见</strong>
                  <span>查看范围为 VIP 可见的文章</span>
                </div>
              </div>
              <div class="benefit-item">
                <span class="benefit-icon">🤖</span>
                <div class="benefit-text">
                  <strong>AI 配额翻倍</strong>
                  <span>从 50 次/天提升到 100 次/天</span>
                </div>
              </div>
              <div class="benefit-item">
                <span class="benefit-icon">✨</span>
                <div class="benefit-text">
                  <strong>会员标识</strong>
                  <span>展示 VIP 身份标识</span>
                </div>
              </div>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <!-- 支付结果弹窗 -->
    <VipPaymentResultModal
      v-model:visible="paymentResultVisible"
      :order-no="currentOrderNo"
      @refresh="handlePaymentSuccess"
      @close="closePaymentResultModal"
    />

    <!-- 取消订单弹窗 -->
    <CancelOrderDialog
      v-model:visible="cancelDialogVisible"
      :order-no="cancelOrderNo"
      @confirmed="handleCancelConfirmed"
    />
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import VipPaymentResultModal from '@/components/Payment/VipPaymentResultModal.vue'
import CancelOrderDialog from '@/components/Payment/CancelOrderDialog.vue'
import { useVipCenter } from '@/composables/useVipCenter'

const {
  plans,
  orders,
  vipInfo,
  submitting,
  selectedPlanCode,
  paymentResultVisible,
  currentOrderNo,
  cancelDialogVisible,
  cancelOrderNo,
  statusText,
  expireText,
  handleCreateOrder,
  handlePaymentSuccess,
  closePaymentResultModal,
  goToVipArticles,
  orderStatusText,
  handleRepayOrder,
  handleCancelOrder,
  handleCancelConfirmed,
  init,
  formatDate,
} = useVipCenter()

onMounted(() => {
  init()
})
</script>

<style lang="scss" scoped>
.vip-square-body {
  width: 100%;
  min-height: 100vh;
  background: #f8fafc;

  html.dark & {
    background: #000000;
  }
}

.vip-container {
  // ===== CSS 变量 - 匹配文章广场 =====
  --bg-page: #f8fafc;
  --bg-card: rgba(0, 0, 0, 0.015);
  --bg-subtle: rgba(0, 0, 0, 0.02);
  --text-primary: #0a0a0a;
  --text-secondary: #888888;
  --text-regular: #666666;
  --text-muted: #aaaaaa;
  --border-light: rgba(0, 0, 0, 0.05);
  --border-regular: rgba(0, 0, 0, 0.08);
  --border-hover: rgba(0, 0, 0, 0.15);
  --accent-color: #0066ff;
  --accent-soft: rgba(0, 102, 255, 0.04);
  --accent-border: rgba(0, 102, 255, 0.15);
  --accent-hover: rgba(0, 102, 255, 0.08);

  // 深色模式
  html.dark & {
    --bg-page: #000000;
    --bg-card: rgba(255, 255, 255, 0.02);
    --bg-subtle: rgba(255, 255, 255, 0.02);
    --text-primary: #ffffff;
    --text-secondary: #888888;
    --text-regular: #dddddd;
    --text-muted: #666666;
    --border-light: rgba(255, 255, 255, 0.05);
    --border-regular: rgba(255, 255, 255, 0.08);
    --border-hover: rgba(255, 255, 255, 0.15);
    --accent-color: #00d4ff;
    --accent-soft: rgba(0, 212, 255, 0.05);
    --accent-border: rgba(0, 212, 255, 0.15);
    --accent-hover: rgba(0, 212, 255, 0.08);
  }

  max-width: 1200px;
  margin: 0 auto;
  padding: 0 24px;
  min-height: 100vh;
  background: var(--bg-page);

  // ===== 页面头部 =====
  .page-hero {
    padding: 60px 0 40px;
    border-bottom: 1px solid var(--border-regular);

    .hero-content {
      .hero-kicker {
        font-size: 11px;
        font-weight: 600;
        letter-spacing: 0.2em;
        text-transform: uppercase;
        color: var(--accent-color);
        margin-bottom: 12px;
        display: flex;
        align-items: center;
        gap: 8px;

        &::before {
          content: '';
          width: 20px;
          height: 1px;
          background: linear-gradient(90deg, var(--accent-color), transparent);
        }
      }

      h1 {
        font-size: 40px;
        font-weight: 700;
        letter-spacing: -0.04em;
        margin-bottom: 12px;
        color: var(--text-primary);
      }

      p {
        font-size: 14px;
        color: var(--text-secondary);
        line-height: 1.7;
      }
    }
  }

  // ===== 内容布局 =====
  .content-layout {
    display: grid;
    grid-template-columns: 1fr 280px;
    gap: 32px;
    padding: 40px 0 60px;

    @media (max-width: 992px) {
      grid-template-columns: 1fr;
    }
  }

  // ===== 主内容区 =====
  .main-content {
    display: flex;
    flex-direction: column;
    gap: 20px;
    width: 100%;
  }

  // ===== 状态卡片 =====
  .status-cards {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;

    @media (max-width: 600px) {
      grid-template-columns: 1fr;
    }
  }

  .status-card {
    display: flex;
    flex-direction: column;
    gap: 6px;
    padding: 20px;
    background: var(--bg-card);
    border: 1px solid var(--border-regular);
    border-radius: 6px;

    .card-label {
      font-size: 11px;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.1em;
      color: var(--text-muted);
    }

    .card-value {
      font-size: 24px;
      font-weight: 700;
      color: var(--text-primary);

      &.active {
        color: var(--accent-color);
      }
    }

    .card-meta {
      font-size: 12px;
      color: var(--text-secondary);
    }
  }

  // ===== 通用卡片 =====
  .section-card {
    background: var(--bg-card);
    border: 1px solid var(--border-regular);
    border-radius: 6px;
    padding: 20px;

    .section-title {
      display: flex;
      align-items: baseline;
      gap: 12px;
      margin-bottom: 12px;

      .title-text {
        font-size: 15px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .title-hint {
        font-size: 12px;
        color: var(--text-muted);
      }
    }

    .test-hint {
      padding: 10px 12px;
      background: rgba(251, 191, 36, 0.1);
      border: 1px solid rgba(251, 191, 36, 0.2);
      border-radius: 4px;
      margin-bottom: 16px;
      font-size: 12px;
      color: #92400e;

      html.dark & {
        background: rgba(251, 191, 36, 0.08);
        border-color: rgba(251, 191, 36, 0.15);
        color: #fbbf24;
      }
    }
  }

  // ===== 会员专区入口 =====
  .vip-entry {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
    padding: 16px;
    background: var(--accent-soft);
    border: 1px solid var(--accent-border);
    border-radius: 6px;
    margin-bottom: 16px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      background: var(--accent-hover);
      border-color: var(--accent-color);
    }

    .entry-info {
      display: flex;
      align-items: center;
      gap: 12px;

      .entry-icon {
        font-size: 20px;
      }

      .entry-text {
        display: flex;
        flex-direction: column;
        gap: 2px;

        strong {
          font-size: 14px;
          color: var(--text-primary);
        }

        span {
          font-size: 12px;
          color: var(--text-secondary);
        }
      }
    }

    .entry-arrow {
      font-size: 18px;
      color: var(--accent-color);
    }
  }

  // ===== 套餐列表 =====
  .plans-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;

    @media (max-width: 768px) {
      grid-template-columns: 1fr;
    }
  }

  .plan-item {
    display: flex;
    flex-direction: column;
    gap: 8px;
    padding: 16px;
    background: var(--bg-page);
    border: 1px solid var(--border-light);
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.2s ease;

    &:hover {
      border-color: var(--border-hover);
    }

    &.selected {
      border-color: var(--accent-color);
      background: var(--accent-soft);

      .plan-btn {
        background: var(--accent-color);
        color: #fff;
        border-color: var(--accent-color);

        &:hover:not(:disabled) {
          background: #0052cc;
          border-color: #0052cc;
          color: #fff;
        }
      }
    }

    .plan-info {
      display: flex;
      justify-content: space-between;
      align-items: baseline;

      .plan-name {
        font-size: 14px;
        font-weight: 600;
        color: var(--text-primary);
      }

      .plan-days {
        font-size: 11px;
        color: var(--text-muted);
      }
    }

    .plan-price {
      display: flex;
      align-items: baseline;

      .price-unit {
        font-size: 14px;
        color: var(--accent-color);
      }

      .price-value {
        font-size: 28px;
        font-weight: 700;
        color: var(--accent-color);
        line-height: 1;
      }
    }

    .plan-desc {
      font-size: 12px;
      color: var(--text-secondary);
      margin: 0;
      min-height: 32px;
      line-height: 1.5;
    }

    .plan-btn {
      width: 100%;
      padding: 10px;
      background: var(--bg-page);
      border: 1px solid var(--border-regular);
      border-radius: 4px;
      font-size: 12px;
      font-weight: 600;
      color: var(--text-primary);
      cursor: pointer;
      transition: all 0.2s ease;

      &:hover:not(:disabled) {
        border-color: var(--accent-color);
        color: var(--accent-color);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }

  // ===== 订单列表 =====
  .orders-list {
    .order-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 14px 0;
      border-bottom: 1px solid var(--border-light);

      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }
    }

    .order-info {
      display: flex;
      flex-direction: column;
      gap: 4px;

      .order-name {
        font-size: 14px;
        color: var(--text-primary);
      }

      .order-meta {
        font-size: 11px;
        color: var(--text-muted);
      }
    }

    .order-right {
      display: flex;
      align-items: center;
      gap: 12px;

      .order-price {
        font-size: 14px;
        font-weight: 600;
        color: var(--accent-color);
      }

      .order-status {
        font-size: 11px;
        padding: 4px 10px;
        border-radius: 4px;
        background: var(--bg-subtle);
        color: var(--text-secondary);

        &.paid {
          background: rgba(34, 197, 94, 0.1);
          color: #22c55e;
        }

        &.paying {
          background: var(--accent-soft);
          color: var(--accent-color);
        }

        &.closed,
        &.failed {
          background: rgba(239, 68, 68, 0.1);
          color: #dc2626;
        }
      }
    }

    .order-actions {
      display: flex;
      gap: 8px;

      .btn-cancel,
      .btn-pay {
        padding: 6px 12px;
        border-radius: 4px;
        font-size: 11px;
        cursor: pointer;
        transition: all 0.2s ease;
      }

      .btn-cancel {
        background: rgba(239, 68, 68, 0.1);
        border: none;
        color: #dc2626;

        &:hover {
          background: rgba(239, 68, 68, 0.2);
        }
      }

      .btn-pay {
        background: var(--accent-color);
        border: none;
        color: #fff;

        &:hover {
          opacity: 0.9;
        }
      }
    }
  }

  .empty-state {
    padding: 40px;
    text-align: center;
    color: var(--text-muted);
    font-size: 13px;
  }

  // ===== 侧边栏 =====
  .sidebar {
    display: flex;
    flex-direction: column;
    gap: 20px;

    @media (max-width: 992px) {
      order: -1;
    }
  }

  .sidebar-card {
    background: var(--bg-card);
    border: 1px solid var(--border-light);
    border-radius: 8px;
    padding: 20px;

    .card-title {
      font-size: 13px;
      font-weight: 600;
      color: var(--text-primary);
      margin-bottom: 16px;
    }
  }

  .benefit-list {
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .benefit-item {
    display: flex;
    gap: 12px;
    padding: 12px;
    background: var(--bg-subtle);
    border: 1px solid var(--border-light);
    border-radius: 6px;

    .benefit-icon {
      font-size: 18px;
      flex-shrink: 0;
    }

    .benefit-text {
      display: flex;
      flex-direction: column;
      gap: 2px;

      strong {
        font-size: 13px;
        color: var(--text-primary);
      }

      span {
        font-size: 11px;
        color: var(--text-secondary);
      }
    }
  }
}

// 响应式
@media (max-width: 768px) {
  .vip-container {
    padding: 0 20px;

    .page-hero {
      padding: 40px 0 25px;

      .hero-content h1 {
        font-size: 28px;
      }
    }

    .content-layout {
      padding: 30px 0 50px;
      gap: 24px;
    }
  }
}
</style>
