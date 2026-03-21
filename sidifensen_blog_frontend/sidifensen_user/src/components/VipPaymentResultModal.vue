<template>
  <div v-if="visible" class="vip-payment-result-modal-mask" @click.self="handleClose">
    <div class="vip-payment-result-modal">
      <!-- 关闭按钮 -->
      <button class="modal-close" @click="handleClose">
        <svg viewBox="0 0 24 24" width="20" height="20">
          <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
        </svg>
      </button>

      <!-- 支付结果卡片 -->
      <div class="result-card">
        <!-- 结果图标 -->
        <div class="result-icon" :class="iconClass">
          <svg v-if="orderStatus === 'PAID'" viewBox="0 0 24 24" width="48" height="48">
            <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
          </svg>
          <svg v-else-if="orderStatus === 'CLOSED' || orderStatus === 'FAILED'" viewBox="0 0 24 24" width="48" height="48">
            <path fill="currentColor" d="M12 2C6.47 2 2 6.47 2 12s4.47 10 10 10 10-4.47 10-10S17.53 2 12 2zm5 13.59L15.59 17 12 13.41 8.41 17 7 15.59 10.59 12 7 8.41 8.41 7 12 10.59 15.59 7 17 8.41 13.41 12 17 15.59z"/>
          </svg>
          <svg v-else viewBox="0 0 24 24" width="48" height="48">
            <path fill="currentColor" d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
          </svg>
        </div>

        <!-- 结果标题 -->
        <h2 class="result-title">{{ titleText }}</h2>

        <!-- 结果描述 -->
        <p class="result-description">{{ descriptionText }}</p>

        <!-- 倒计时提示 -->
        <div v-if="orderStatus === 'PAYING'" class="result-countdown">
          <span class="countdown-label">请在</span>
          <span class="countdown-time">{{ countdownText }}</span>
          <span class="countdown-label">内完成支付</span>
        </div>

        <!-- 支付成功关闭倒计时 -->
        <div v-if="orderStatus === 'PAID'" class="result-close-countdown">
          <span class="close-countdown-text">{{ closeCountdownText }}</span>
        </div>

        <!-- 订单号信息 -->
        <div class="result-order">
          <span class="order-label">订单号</span>
          <span class="order-value">{{ orderNo || "--" }}</span>
          <button v-if="orderNo" class="copy-button" @click="copyOrderNo">
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M16 1H4c-1.1 0-2 .9-2 2v14h2V3h12V1zm3 4H8c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h11c1.1 0 2-.9 2-2V7c0-1.1-.9-2-2-2zm0 16H8V7h11v14z"/>
            </svg>
          </button>
        </div>

        <!-- 操作按钮 -->
        <div class="result-actions">
          <button v-if="orderStatus === 'PAYING'" class="primary-button" @click="handleRefresh">
            刷新状态
          </button>
          <button class="secondary-button" @click="handleClose">
            {{ orderStatus === 'PAID' ? '返回会员中心' : '返回' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { getVipOrder } from "@/api/vip";

// Props
const props = defineProps({
  visible: {
    type: Boolean,
    default: false,
  },
  orderNo: {
    type: String,
    default: "",
  },
});

// Emits
const emit = defineEmits(["update:visible", "refresh", "close"]);

// 订单状态
const orderStatus = ref("PAYING");
const pollingTimer = ref(null);
const copyTooltip = ref(false);
const closeInterval = ref(null);

// 关闭倒计时配置（3 秒）
const CLOSE_COUNTDOWN_SECONDS = 3;
const closeCountdownSeconds = ref(CLOSE_COUNTDOWN_SECONDS);

// 倒计时配置（15 分钟 = 900 秒）
const COUNTDOWN_SECONDS = 15 * 60;
const countdownSeconds = ref(COUNTDOWN_SECONDS);
const countdownTimer = ref(null);

// 倒计时文案
const countdownText = computed(() => {
  const minutes = Math.floor(countdownSeconds.value / 60);
  const seconds = countdownSeconds.value % 60;
  return `${minutes.toString().padStart(2, "0")}:${seconds.toString().padStart(2, "0")}`;
});

// 支付成功关闭倒计时文案
const closeCountdownText = computed(() => {
  return `${closeCountdownSeconds.value}秒后关闭`;
});

// 根据订单状态切换标题
const titleText = computed(() => {
  if (orderStatus.value === "PAID") {
    return "支付成功";
  }
  if (orderStatus.value === "CLOSED" || orderStatus.value === "FAILED") {
    return "支付未完成";
  }
  return "正在确认支付状态";
});

// 根据订单状态切换说明文案
const descriptionText = computed(() => {
  if (orderStatus.value === "PAID") {
    return "异步回调已经到账，会员状态和 AI 配额会同步刷新。";
  }
  if (orderStatus.value === "CLOSED" || orderStatus.value === "FAILED") {
    return "订单已关闭或支付失败，可以回到会员中心重新发起支付。";
  }
  return "页面每 2 秒轮询一次订单状态，请在规定时间内完成支付。";
});

// 图标样式
const iconClass = computed(() => {
  if (orderStatus.value === "PAID") {
    return "icon-success";
  }
  if (orderStatus.value === "CLOSED" || orderStatus.value === "FAILED") {
    return "icon-failed";
  }
  return "icon-pending";
});

// 轮询订单状态
const pollOrder = async (attempt = 0) => {
  if (!props.orderNo) {
    return;
  }

  try {
    const response = await getVipOrder(props.orderNo);
    orderStatus.value = response.data.data?.status || "PAYING";

    // 支付成功
    if (orderStatus.value === "PAID") {
      emit("refresh");
      stopPolling();
      stopCountdown();
      // 重置关闭倒计时并从 3 开始倒数
      closeCountdownSeconds.value = CLOSE_COUNTDOWN_SECONDS;
      // 启动关闭倒计时
      const closeInterval = window.setInterval(() => {
        if (closeCountdownSeconds.value > 1) {
          closeCountdownSeconds.value--;
        } else {
          window.clearInterval(closeInterval);
          handleClose();
        }
      }, 1000);
      return;
    }

    // 订单关闭或失败
    if (["CLOSED", "FAILED"].includes(orderStatus.value)) {
      stopPolling();
      return;
    }

    // 轮询次数超限 (450 次 × 2 秒 = 900 秒 = 15 分钟，与订单超时时间一致)
    if (attempt >= 450) {
      stopPolling();
      return;
    }

    // 继续轮询
    pollingTimer.value = window.setTimeout(() => pollOrder(attempt + 1), 2000);
  } catch (error) {
    stopPolling();
    ElMessage.error(error?.msg || "获取订单状态失败");
  }
};

// 停止轮询
const stopPolling = () => {
  if (pollingTimer.value) {
    window.clearTimeout(pollingTimer.value);
    pollingTimer.value = null;
  }
};

// 停止关闭定时器
const stopCloseInterval = () => {
  if (closeInterval.value) {
    window.clearInterval(closeInterval.value);
    closeInterval.value = null;
  }
};

// 启动倒计时
const startCountdown = () => {
  stopCountdown();
  countdownSeconds.value = COUNTDOWN_SECONDS;
  countdownTimer.value = window.setInterval(() => {
    if (countdownSeconds.value > 0) {
      countdownSeconds.value--;
    } else {
      stopCountdown();
    }
  }, 1000);
};

// 停止倒计时
const stopCountdown = () => {
  if (countdownTimer.value) {
    window.clearInterval(countdownTimer.value);
    countdownTimer.value = null;
  }
};

// 刷新订单状态
const handleRefresh = async () => {
  stopPolling();
  await pollOrder();
};

// 复制订单号
const copyOrderNo = async () => {
  if (!props.orderNo) return;

  try {
    await navigator.clipboard.writeText(props.orderNo);
    copyTooltip.value = true;
    ElMessage.success("订单号已复制");
    setTimeout(() => {
      copyTooltip.value = false;
    }, 2000);
  } catch (error) {
    ElMessage.error("复制失败");
  }
};

// 关闭弹窗
const handleClose = () => {
  stopPolling();
  stopCountdown();
  stopCloseInterval();
  emit("update:visible", false);
  emit("close");
};

// 监听 visible 变化，打开时开始轮询
watch(
  () => props.visible,
  (newVal) => {
    if (newVal && props.orderNo) {
      // 延迟一下开始轮询，确保弹窗动画完成
      setTimeout(() => {
        pollOrder();
        startCountdown();
      }, 300);
    }
  }
);

onMounted(() => {
  if (props.visible && props.orderNo) {
    pollOrder();
    startCountdown();
  }
});

onBeforeUnmount(() => {
  stopPolling();
  stopCountdown();
  stopCloseInterval();
});
</script>

<style lang="scss" scoped>
.vip-payment-result-modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.2s ease;

  .vip-payment-result-modal {
    position: relative;
    width: 100%;
    max-width: 480px;
    margin: 20px;
    animation: slideIn 0.3s ease;

    .modal-close {
      position: absolute;
      top: 12px;
      right: 12px;
      width: 32px;
      height: 32px;
      border: none;
      background: transparent;
      color: #64748b;
      cursor: pointer;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: background-color 0.2s, color 0.2s;

      &:hover {
        background: #f1f5f9;
        color: #1e293b;
      }
    }

    .result-card {
      background: #fffdf8;
      border-radius: 28px;
      padding: 48px 36px 36px;
      text-align: center;
      box-shadow: 0 20px 60px rgba(24, 33, 22, 0.15);
      border: 1px solid #dde4d8;

      .result-icon {
        width: 80px;
        height: 80px;
        margin: 0 auto 24px;
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;

        &.icon-success {
          background: #dcfce7;
          color: #16a34a;
        }

        &.icon-failed {
          background: #fee2e2;
          color: #dc2626;
        }

        &.icon-pending {
          background: #fef3c7;
          color: #d97706;
        }
      }

      .result-title {
        margin: 0 0 12px;
        font-size: 28px;
        font-weight: 700;
        color: #182116;
      }

      .result-description {
        margin: 0 0 24px;
        font-size: 15px;
        line-height: 1.7;
        color: #5d695a;
      }

      .result-countdown {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 8px;
        padding: 12px 20px;
        background: #fef3c7;
        border-radius: 12px;
        margin-bottom: 24px;
        font-size: 14px;

        .countdown-label {
          color: #92400e;
        }

        .countdown-time {
          font-weight: 700;
          color: #dc2626;
          font-family: monospace;
          font-size: 18px;
        }
      }

      .result-close-countdown {
        padding: 10px 20px;
        margin-bottom: 24px;
        font-size: 14px;
        color: #16a34a;

        .close-countdown-text {
          font-weight: 500;
        }
      }

      .result-order {
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 12px;
        padding: 16px 20px;
        background: #eff7f2;
        border-radius: 18px;
        margin-bottom: 28px;
        position: relative;

        .order-label {
          font-size: 13px;
          color: #5d695a;
        }

        .order-value {
          font-size: 15px;
          font-weight: 600;
          color: #182116;
          word-break: break-all;
          max-width: 280px;
          overflow: hidden;
          text-overflow: ellipsis;
          white-space: nowrap;
        }

        .copy-button {
          width: 28px;
          height: 28px;
          border: none;
          background: transparent;
          color: #5d695a;
          cursor: pointer;
          border-radius: 6px;
          display: flex;
          align-items: center;
          justify-content: center;
          flex-shrink: 0;
          transition: background-color 0.2s, color 0.2s;

          &:hover {
            background: #dcfce7;
            color: #16a34a;
          }
        }
      }

      .result-actions {
        display: flex;
        justify-content: center;
        gap: 12px;

        .primary-button,
        .secondary-button {
          min-width: 120px;
          height: 44px;
          border-radius: 999px;
          font-size: 15px;
          cursor: pointer;
          transition: all 0.2s;
        }

        .primary-button {
          background: #1f6d4c;
          color: #f8fbf8;
          border: none;

          &:hover {
            background: #2e8a64;
          }

          &:active {
            transform: scale(0.98);
          }
        }

        .secondary-button {
          background: transparent;
          color: #182116;
          border: 1px solid #dde4d8;

          &:hover {
            background: #f8fafc;
          }

          &:active {
            transform: scale(0.98);
          }
        }
      }
    }
  }
}

html.dark {
  .vip-payment-result-modal-mask {
    .vip-payment-result-modal {
      .modal-close {
        &:hover {
          background: #334155;
          color: #f1f5f9;
        }
      }

      .result-card {
        background: #1e293b;
        border-color: #334155;

        .result-title {
          color: #f1f5f9;
        }

        .result-description {
          color: #cbd5e1;
        }

        .result-countdown {
          background: rgba(254, 243, 199, 0.1);

          .countdown-label {
            color: #fcd34d;
          }

          .countdown-time {
            color: #f87171;
          }
        }

        .result-close-countdown {
          color: #4ade80;
        }

        .result-order {
          background: rgba(46, 138, 100, 0.12);

          .order-label {
            color: #cbd5e1;
          }

          .order-value {
            color: #f1f5f9;
          }

          .copy-button {
            &:hover {
              background: rgba(46, 138, 100, 0.2);
              color: #2e8a64;
            }
          }
        }

        .secondary-button {
          color: #f1f5f9;
          border-color: #334155;

          &:hover {
            background: #334155;
          }
        }
      }
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

@media (max-width: 768px) {
  .vip-payment-result-modal-mask {
    .vip-payment-result-modal {
      margin: 20px;

      .result-card {
        padding: 36px 24px 24px;

        .result-title {
          font-size: 24px;
        }

        .result-actions {
          flex-direction: column;

          .primary-button,
          .secondary-button {
            width: 100%;
          }
        }
      }
    }
  }
}
</style>
