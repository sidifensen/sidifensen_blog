<template>
  <div class="vip-result">
    <div class="result-shell">
      <!-- 未登录提示 -->
      <div v-if="!isLoggedIn" class="login-reminder">
        <p class="reminder-text">检测到您未登录，请先登录后再查看订单状态</p>
        <button class="primary-button" @click="goToLogin">去登录</button>
      </div>

      <!-- 支付结果卡片 -->
      <div class="result-card">
        <p class="result-kicker">支付结果</p>
        <h1 class="result-title">{{ titleText }}</h1>
        <p class="result-description">{{ descriptionText }}</p>
        <div class="result-order">
          <span class="order-label">订单号</span>
          <span class="order-value">{{ orderNo || "--" }}</span>
        </div>
        <div class="result-actions">
          <button class="primary-button" @click="refreshOrder">刷新状态</button>
          <button class="secondary-button" @click="goToVipCenter">返回会员中心</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onBeforeUnmount, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { info } from "@/api/user";
import { getVipOrder } from "@/api/vip";
import { useUserStore } from "@/stores/userStore";

// 路由与全局用户态
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 兼容后端主动拼接 orderNo 和支付宝原生 out_trade_no 两种参数
const orderNo = ref(route.query.orderNo || route.query.out_trade_no || "");
const orderStatus = ref("PAYING");
const pollingTimer = ref(null);
const pollingExceeded = ref(false);

// 根据订单状态切换标题
const titleText = computed(() => {
  if (orderStatus.value === "PAID") {
    return "VIP 已开通";
  }
  if (orderStatus.value === "CLOSED" || orderStatus.value === "FAILED") {
    return "支付未完成";
  }
  return "正在确认支付状态";
});

// 检查是否已登录
const isLoggedIn = computed(() => !!userStore.user?.id);

// 跳转到登录页
const goToLogin = () => {
  router.push("/login");
};

// 根据订单状态切换说明文案
const descriptionText = computed(() => {
  if (orderStatus.value === "PAID") {
    return "异步回调已经到账，会员状态和 AI 配额会同步刷新。";
  }
  if (orderStatus.value === "CLOSED" || orderStatus.value === "FAILED") {
    return "订单已关闭或支付失败，可以回到会员中心重新发起支付。";
  }
  if (pollingExceeded.value) {
    return "如果你已经完成支付但状态仍未更新，点击“刷新状态”会主动向支付宝补查这笔订单。";
  }
  return "支付宝回跳页不直接发放会员资格，页面会轮询订单状态。";
});

// 支付成功后刷新用户信息，确保会员权益马上可见
const refreshUserInfo = async () => {
  const response = await info();
  userStore.user = response.data.data;
};

// 轮询结束或页面卸载时统一清理定时器
const stopPolling = () => {
  if (pollingTimer.value) {
    window.clearTimeout(pollingTimer.value);
    pollingTimer.value = null;
  }
};

// 订单结果页只负责轮询异步回调状态，不直接信任同步回跳
const pollOrder = async (attempt = 0) => {
  if (!orderNo.value) {
    return;
  }
  try {
    const response = await getVipOrder(orderNo.value);
    orderStatus.value = response.data.data?.status || "PAYING";
    pollingExceeded.value = false;
    if (orderStatus.value === "PAID") {
      await refreshUserInfo();
      stopPolling();
      return;
    }
    if (["CLOSED", "FAILED"].includes(orderStatus.value)) {
      stopPolling();
      return;
    }
    if (attempt >= 12) {
      pollingExceeded.value = true;
      stopPolling();
      return;
    }
    pollingTimer.value = window.setTimeout(() => pollOrder(attempt + 1), 2000);
  } catch (error) {
    stopPolling();
    ElMessage.error(error?.msg || "获取订单状态失败");
  }
};

const refreshOrder = async () => {
  stopPolling();
  await pollOrder();
};

const goToVipCenter = () => {
  router.push("/vip");
};

onMounted(async () => {
  // 1. 先检查是否有订单号参数
  if (!orderNo.value) {
    ElMessage.error("缺少订单号");
    return;
  }

  // 2. 检查登录状态 - 未登录时不强制跳转，而是提示用户
  if (!userStore.user?.id) {
    ElMessage.warning("请先登录后再查看订单状态");
    // 不强制跳转，让用户看到页面，可以手动刷新或登录后回来
    return;
  }

  // 3. 已登录则开始轮询订单状态
  await pollOrder();
});

onBeforeUnmount(() => {
  stopPolling();
});
</script>

<style lang="scss" scoped>
.vip-result {
  --bg-page: #f4f3ec;
  --bg-card: #fffdf8;
  --text-primary: #182116;
  --text-regular: #5d695a;
  --border: #dde4d8;
  --accent: #1f6d4c;
  --accent-light: #eff7f2;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(180deg, rgba(31, 109, 76, 0.08), transparent 30%), var(--bg-page);

  .result-shell {
    width: 100%;
    max-width: 720px;
    padding: 24px;

    // 未登录提示卡片
    .login-reminder {
      display: grid;
      gap: 16px;
      padding: 24px;
      border-radius: 18px;
      border: 1px solid var(--border);
      background: var(--bg-card);
      text-align: center;
      margin-bottom: 24px;

      .reminder-text {
        margin: 0;
        font-size: 15px;
        color: var(--text-regular);
      }

      .primary-button {
        min-width: 120px;
        height: 44px;
        border-radius: 999px;
        border: 0;
        background: var(--accent);
        color: #f8fbf8;
        cursor: pointer;
      }
    }

    // 结果展示卡片
    .result-card {
      display: grid;
      gap: 18px;
      padding: 36px;
      border-radius: 28px;
      border: 1px solid var(--border);
      background: var(--bg-card);
      box-shadow: 0 12px 40px rgba(24, 33, 22, 0.08);
      text-align: center;

      .result-kicker {
        margin: 0;
        font-size: 13px;
        letter-spacing: 0.24em;
        text-transform: uppercase;
        color: var(--text-regular);
      }

      .result-title {
        margin: 0;
        font-size: 34px;
        color: var(--text-primary);
      }

      .result-description {
        margin: 0;
        font-size: 15px;
        line-height: 1.8;
        color: var(--text-regular);
      }

      .result-order {
        display: grid;
        gap: 8px;
        padding: 18px;
        border-radius: 18px;
        background: var(--accent-light);

        .order-label {
          font-size: 12px;
          color: var(--text-regular);
        }

        .order-value {
          font-size: 15px;
          font-weight: 700;
          color: var(--text-primary);
          word-break: break-all;
        }
      }

      // 操作按钮区
      .result-actions {
        display: flex;
        justify-content: center;
        gap: 12px;

        .primary-button,
        .secondary-button {
          min-width: 140px;
          height: 44px;
          border-radius: 999px;
          cursor: pointer;
        }

        .primary-button {
          border: 0;
          background: var(--accent);
          color: #f8fbf8;
        }

        .secondary-button {
          border: 1px solid var(--border);
          background: transparent;
          color: var(--text-primary);
        }
      }
    }
  }
}

html.dark {
  .vip-result {
    --bg-page: #101712;
    --bg-card: #172019;
    --text-primary: #eff4eb;
    --text-regular: #afbea9;
    --border: #2a352b;
    --accent: #2e8a64;
    --accent-light: rgba(46, 138, 100, 0.12);
  }
}

@media (max-width: 768px) {
  .vip-result {
    .result-shell {
      padding: 14px;

      .result-card {
        padding: 24px;

        .result-title {
          font-size: 28px;
        }

        .result-actions {
          flex-direction: column;
        }
      }
    }
  }
}
</style>
