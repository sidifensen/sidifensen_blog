<template>
  <div class="vip-center">
    <div class="vip-shell">
      <!-- 顶部会员状态区 -->
      <section class="vip-hero">
        <div class="hero-content">
          <div class="hero-copy">
            <p class="hero-kicker">会员中心</p>
            <h1 class="hero-title">开通 VIP，解锁会员文章与更高 AI 配额</h1>
            <p class="hero-description">VIP 用户可以查看会员文章，可用 AI 配额提升到 {{ vipInfo.aiDailyQuota || 100 }} 次/天。</p>
          </div>
          <div class="hero-status">
            <div class="status-card">
              <span class="status-label">当前状态</span>
              <strong class="status-value">{{ statusText }}</strong>
              <span class="status-meta">{{ expireText }}</span>
            </div>
            <div class="status-card">
              <span class="status-label">AI 配额</span>
              <strong class="status-value">{{ vipInfo.aiRemainingQuota ?? 0 }}/{{ vipInfo.aiDailyQuota ?? 0 }}</strong>
              <span class="status-meta">今日剩余 / 每日总量</span>
            </div>
          </div>
        </div>
      </section>

      <!-- 套餐与订单内容区 -->
      <section class="vip-content">
        <div class="content-grid">
          <div class="main-panel">
            <div class="section-card">
              <div class="section-head">
                <div class="head-copy">
                  <h2>选择套餐</h2>
                  <p>首版支持月卡、季卡、年卡，支付成功后立即生效。</p>
                </div>
              </div>

              <div class="vip-entry">
                <div class="entry-copy">
                  <span class="entry-kicker">会员专区</span>
                  <strong class="entry-title">开通后直接进入会员专区查看 VIP 文章</strong>
                  <p class="entry-description">这里是会员文章的专属入口，集中查看所有范围为 VIP 可见的内容。</p>
                </div>
                <button class="entry-button" @click="goToVipArticles">前往会员专区</button>
              </div>

              <div class="plan-grid">
                <article
                  v-for="plan in plans"
                  :key="plan.code"
                  class="plan-card"
                  :class="{ active: selectedPlanCode === plan.code }"
                  @click="selectedPlanCode = plan.code"
                >
                  <div class="plan-card-top">
                    <span class="plan-name">{{ plan.name }}</span>
                    <span class="plan-days">{{ plan.days }} 天</span>
                  </div>
                  <div class="plan-price">
                    <span class="price-unit">¥</span>
                    <span class="price-value">{{ plan.priceYuan }}</span>
                  </div>
                  <p class="plan-description">{{ plan.description || "适合稳定阅读和持续创作使用。" }}</p>
                  <button class="plan-button" :disabled="submitting && selectedPlanCode === plan.code" @click.stop="handleCreateOrder(plan)">
                    {{ submitting && selectedPlanCode === plan.code ? "跳转中..." : "立即开通" }}
                  </button>
                </article>
              </div>
            </div>

            <div class="section-card">
              <div class="section-head">
                <div class="head-copy">
                  <h2>订单记录</h2>
                  <p>支付成功后会员资格只以异步回调发放，支付页返回后可在这里查看状态。</p>
                </div>
              </div>

              <div v-if="orders.length" class="order-list">
                <article v-for="order in orders" :key="order.orderNo" class="order-item">
                  <div class="order-main">
                    <strong class="order-name">{{ order.planName }}</strong>
                    <span class="order-no">{{ order.orderNo }}</span>
                    <span class="order-time">下单时间 {{ formatDate(order.createTime) }}</span>
                  </div>
                  <div class="order-side">
                    <span class="order-price">¥{{ order.priceYuan }}</span>
                    <span class="order-status" :class="`status-${(order.status || '').toLowerCase()}`">{{ order.status }}</span>
                  </div>
                </article>
              </div>
              <div v-else class="empty-state">暂无订单记录</div>
            </div>
          </div>

          <aside class="side-panel">
            <div class="section-card">
              <div class="benefit-head">
                <h2>VIP 权益</h2>
                <p>开通会员后立即解锁以下内容与站内能力。</p>
              </div>

              <div class="benefit-list">
                <div class="benefit-item">
                  <span class="benefit-title">VIP 文章可见</span>
                  <span class="benefit-text">查看文章范围为vip可见的文章内容。</span>
                </div>
                <div class="benefit-item">
                  <span class="benefit-title">AI 配额翻倍</span>
                  <span class="benefit-text">从 50 次/天提升到 100 次/天。</span>
                </div>
                <div class="benefit-item">
                  <span class="benefit-title">会员标识</span>
                  <span class="benefit-text">主页、文章侧栏、站点头部展示 VIP 身份。</span>
                </div>
              </div>
            </div>
          </aside>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { info } from "@/api/user";
import { createVipOrder, getVipMe, getVipOrderList, getVipPlans } from "@/api/vip";
import { useUserStore } from "@/stores/userStore";

// 路由与全局状态
const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

// 页面核心状态
const plans = ref([]);
const orders = ref([]);
const vipInfo = ref({});
const submitting = ref(false);
const selectedPlanCode = ref("");

// 会员状态展示文案
const statusText = computed(() => {
  if (vipInfo.value?.isVip) {
    return "有效会员";
  }
  if (vipInfo.value?.vipStatus === "EXPIRED") {
    return "已过期";
  }
  return "未开通";
});

// 会员到期时间展示文案
const expireText = computed(() => {
  if (!vipInfo.value?.vipExpireTime) {
    return "开通后立即生效";
  }
  return `到期时间 ${formatDate(vipInfo.value.vipExpireTime)}`;
});

// 根据 UA 粗略区分 PC 和 H5 支付入口
const detectClientType = () => {
  return /Android|iPhone|iPad|Mobile/i.test(window.navigator.userAgent) ? "H5" : "PC";
};

// PC 支付先打开空白页，避免异步接口返回后被浏览器拦截弹窗
const openPaymentWindow = () => {
  const paymentWindow = window.open("", `vip-alipay-${Date.now()}`);
  if (!paymentWindow) {
    ElMessage.error("浏览器拦截了支付窗口，请允许弹窗后重试");
    return null;
  }
  return paymentWindow;
};

// 支付宝 PC 场景返回表单时，前端需要主动插入并提交
const submitPaymentForm = (formHtml, paymentWindow) => {
  const wrapper = document.createElement("div");
  wrapper.style.display = "none";
  wrapper.innerHTML = formHtml;
  document.body.appendChild(wrapper);
  const form = wrapper.querySelector("form");
  if (!form) {
    ElMessage.error("支付跳转失败，请稍后重试");
    return;
  }
  if (paymentWindow) {
    form.setAttribute("target", paymentWindow.name);
    paymentWindow.focus();
  }
  form.submit();
};

// 支付成功后刷新站点用户态，保证 VIP 标识和额度即时生效
const refreshUserInfo = async () => {
  const response = await info();
  userStore.user = response.data.data;
};

// 会员中心初始化时并行拉取套餐、会员信息和订单记录
const fetchVipData = async () => {
  const [plansRes, vipRes, orderRes] = await Promise.all([getVipPlans(), getVipMe(), getVipOrderList(1, 10)]);
  plans.value = plansRes.data.data || [];
  vipInfo.value = vipRes.data.data || {};
  orders.value = orderRes.data.data.data || [];
  if (!selectedPlanCode.value && plans.value.length) {
    selectedPlanCode.value = plans.value[0].code;
  }
};

// 创建订单后按后端返回的 formHtml / payUrl 决定跳转方式
const handleCreateOrder = async (plan) => {
  const clientType = detectClientType();
  const paymentWindow = clientType === "PC" ? openPaymentWindow() : null;
  if (clientType === "PC" && !paymentWindow) {
    return;
  }
  try {
    submitting.value = true;
    selectedPlanCode.value = plan.code;
    const response = await createVipOrder({
      planCode: plan.code,
      clientType,
    });
    const payload = response.data.data;
    if (payload.formHtml) {
      submitPaymentForm(payload.formHtml, paymentWindow);
      return;
    }
    if (payload.payUrl) {
      window.location.href = payload.payUrl;
      return;
    }
    ElMessage.error("支付参数异常，请稍后重试");
  } catch (error) {
    if (paymentWindow && !paymentWindow.closed) {
      paymentWindow.close();
    }
    ElMessage.error(error?.msg || "创建订单失败");
  } finally {
    submitting.value = false;
  }
};

const goToVipArticles = () => {
  router.push("/vip/articles");
};

// 未登录用户先跳登录，登录后再回到会员中心
onMounted(async () => {
  if (!userStore.user?.id) {
    router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`);
    return;
  }
  try {
    await fetchVipData();
    await refreshUserInfo();
  } catch (error) {
    ElMessage.error(error?.msg || "加载会员中心失败");
  }
});

// 统一格式化会员到期时间
const formatDate = (value) => {
  if (!value) {
    return "--";
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, "0")}-${String(date.getDate()).padStart(2, "0")} ${String(date.getHours()).padStart(2, "0")}:${String(date.getMinutes()).padStart(2, "0")}`;
};
</script>

<style lang="scss" scoped>
.vip-center {
  --bg-page: #f6f7f3;
  --bg-card: #fffdf7;
  --bg-hero: #122217;
  --text-primary: #142013;
  --text-regular: #4d5c4a;
  --text-light: #f7f6ef;
  --border: #dde4d6;
  --accent: #b98b2f;
  --accent-strong: #8e6821;
  --accent-soft: rgba(185, 139, 47, 0.12);
  --accent-soft-strong: rgba(185, 139, 47, 0.18);
  --shadow: rgba(17, 24, 39, 0.08);
  min-height: 100vh;
  background: var(--bg-page);

  .vip-shell {
    max-width: 1240px;
    margin: 0 auto;
    padding: 84px 20px 48px;

    // 顶部会员状态区
    .vip-hero {
      background: radial-gradient(circle at top right, rgba(185, 139, 47, 0.18), transparent 30%), var(--bg-hero);
      border-radius: 28px;
      padding: 36px;
      color: var(--text-light);

      .hero-content {
        display: grid;
        grid-template-columns: minmax(0, 1.5fr) 320px;
        gap: 24px;

        .hero-copy {
          .hero-kicker {
            margin: 0 0 10px;
            font-size: 13px;
            letter-spacing: 0.24em;
            text-transform: uppercase;
            color: rgba(247, 246, 239, 0.72);
          }

          .hero-title {
            margin: 0;
            font-size: 36px;
            line-height: 1.15;
          }

          .hero-description {
            margin: 14px 0 0;
            max-width: 620px;
            font-size: 15px;
            line-height: 1.8;
            color: rgba(247, 246, 239, 0.82);
          }
        }

        .hero-status {
          display: grid;
          gap: 14px;

          .status-card {
            display: flex;
            flex-direction: column;
            gap: 8px;
            padding: 18px;
            border: 1px solid rgba(247, 246, 239, 0.14);
            border-radius: 18px;
            background: rgba(255, 255, 255, 0.04);

            .status-label {
              font-size: 12px;
              color: rgba(247, 246, 239, 0.64);
            }

            .status-value {
              font-size: 24px;
            }

            .status-meta {
              font-size: 13px;
              color: rgba(247, 246, 239, 0.72);
            }
          }
        }
      }
    }

    // 套餐、订单与权益区
    .vip-content {
      margin-top: 24px;

      .content-grid {
        display: grid;
        grid-template-columns: minmax(0, 1fr) 300px;
        gap: 24px;

        .main-panel,
        .side-panel {
          display: grid;
          gap: 24px;
        }

        .section-card {
          background: var(--bg-card);
          border: 1px solid var(--border);
          border-radius: 24px;
          padding: 24px;
          box-shadow: 0 10px 30px var(--shadow);

          // 标题栏
          .section-head {
            display: flex;
            justify-content: space-between;
            gap: 16px;
            align-items: center;
            margin-bottom: 20px;

            .head-copy {
              h2 {
                margin: 0;
                font-size: 24px;
                color: var(--text-primary);
              }

              p {
                margin: 8px 0 0;
                font-size: 14px;
                line-height: 1.7;
                color: var(--text-regular);
              }
            }
          }

          // 会员专区入口
          .vip-entry {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 20px;
            margin-bottom: 20px;
            padding: 18px 20px;
            border: 1px solid var(--accent-soft-strong);
            border-radius: 20px;
            background: linear-gradient(135deg, var(--accent-soft), transparent 85%);

            .entry-copy {
              display: grid;
              gap: 6px;

              .entry-kicker {
                font-size: 12px;
                letter-spacing: 0.16em;
                text-transform: uppercase;
                color: var(--accent-strong);
              }

              .entry-title {
                font-size: 18px;
                color: var(--text-primary);
              }

              .entry-description {
                margin: 0;
                font-size: 13px;
                line-height: 1.7;
                color: var(--text-regular);
              }
            }

            .entry-button {
              min-width: 144px;
              height: 42px;
              border: 0;
              border-radius: 999px;
              background: var(--accent);
              color: var(--text-light);
              cursor: pointer;
              flex-shrink: 0;
            }
          }

          // 套餐卡片列表
          .plan-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 16px;

            .plan-card {
              display: flex;
              flex-direction: column;
              gap: 18px;
              padding: 20px;
              border: 1px solid var(--border);
              border-radius: 18px;
              cursor: pointer;
              transition: border-color 0.2s ease, transform 0.2s ease, box-shadow 0.2s ease;

              &.active {
                border-color: var(--accent);
                box-shadow: 0 12px 24px rgba(185, 139, 47, 0.12);
                transform: translateY(-2px);
              }

              .plan-card-top {
                display: flex;
                justify-content: space-between;
                gap: 10px;

                .plan-name {
                  font-size: 18px;
                  font-weight: 700;
                  color: var(--text-primary);
                }

                .plan-days {
                  font-size: 12px;
                  color: var(--text-regular);
                }
              }

              .plan-price {
                display: flex;
                align-items: baseline;
                color: var(--accent-strong);

                .price-unit {
                  font-size: 18px;
                }

                .price-value {
                  font-size: 38px;
                  font-weight: 800;
                  line-height: 1;
                }
              }

              .plan-description {
                min-height: 48px;
                margin: 0;
                font-size: 13px;
                line-height: 1.7;
                color: var(--text-regular);
              }

              .plan-button {
                height: 42px;
                border: 0;
                border-radius: 999px;
                background: var(--accent);
                color: var(--text-light);
                cursor: pointer;

                &:disabled {
                  cursor: not-allowed;
                  opacity: 0.6;
                }
              }
            }
          }

          // 订单记录列表
          .order-list {
            display: grid;
            gap: 12px;

            .order-item {
              display: flex;
              justify-content: space-between;
              gap: 16px;
              padding: 16px 0;
              border-bottom: 1px solid var(--border);

              &:last-child {
                border-bottom: 0;
                padding-bottom: 0;
              }

              .order-main {
                display: grid;
                gap: 6px;

                .order-name {
                  color: var(--text-primary);
                }

                .order-no {
                  font-size: 12px;
                  color: var(--text-regular);
                }

                .order-time {
                  font-size: 12px;
                  color: var(--text-regular);
                }
              }

              .order-side {
                display: grid;
                gap: 6px;
                justify-items: end;

                .order-price {
                  font-weight: 700;
                  color: var(--accent-strong);
                }

                .order-status {
                  font-size: 12px;
                  color: var(--text-regular);

                  &.status-paid {
                    color: #2f7f41;
                  }

                  &.status-paying {
                    color: #a36a07;
                  }

                  &.status-closed,
                  &.status-failed {
                    color: #b74d4d;
                  }
                }
              }
            }
          }

          // 空状态
          .empty-state {
            padding: 24px 0 8px;
            color: var(--text-regular);
            text-align: center;
          }

          // 会员权益说明
          .benefit-head {
            display: grid;
            gap: 8px;
            margin-bottom: 18px;

            h2 {
              margin: 0;
              font-size: 22px;
              color: var(--text-primary);
            }

            p {
              margin: 0;
              font-size: 13px;
              line-height: 1.7;
              color: var(--text-regular);
            }
          }

          .benefit-list {
            display: grid;
            gap: 14px;

            .benefit-item {
              display: grid;
              gap: 6px;
              padding: 16px;
              border-radius: 18px;
              background: rgba(185, 139, 47, 0.08);

              .benefit-title {
                font-size: 16px;
                font-weight: 700;
                color: var(--text-primary);
              }

              .benefit-text {
                font-size: 13px;
                line-height: 1.7;
                color: var(--text-regular);
              }
            }
          }
        }
      }
    }
  }
}

html.dark {
  .vip-center {
    --bg-page: #101712;
    --bg-card: #172019;
    --bg-hero: #0d120f;
    --text-primary: #eff4eb;
    --text-regular: #afbea9;
    --text-light: #f5f7f2;
    --border: #2a352b;
    --accent: #c59a44;
    --accent-strong: #e1ba6a;
    --accent-soft: rgba(197, 154, 68, 0.12);
    --accent-soft-strong: rgba(197, 154, 68, 0.18);
    --shadow: rgba(0, 0, 0, 0.24);
  }
}

@media (max-width: 1024px) {
  .vip-center {
    .vip-shell {
      .vip-hero {
        .hero-content {
          grid-template-columns: 1fr;
        }
      }

      .vip-content {
        .content-grid {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .vip-center {
    .vip-shell {
      padding: 72px 14px 32px;

      .vip-hero {
        padding: 24px;

        .hero-content {
          .hero-copy {
            .hero-title {
              font-size: 28px;
            }
          }
        }
      }

      .vip-content {
        .content-grid {
          .section-card {
            padding: 20px;

            .vip-entry {
              flex-direction: column;
              align-items: flex-start;

              .entry-button {
                width: 100%;
              }
            }

            .plan-grid {
              grid-template-columns: 1fr;
            }
          }
        }
      }
    }
  }
}
</style>
