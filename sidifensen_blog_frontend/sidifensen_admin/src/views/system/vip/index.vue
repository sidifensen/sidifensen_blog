<template>
  <div class="vip-management-page">
    <div class="hero-section">
      <div class="hero-section__copy">
        <p class="hero-section__eyebrow">MEMBERSHIP OPERATIONS</p>
        <h2 class="hero-section__title">会员管理</h2>
        <p class="hero-section__subtitle">统一查看会员状态、支付订单与套餐配置，人工调整只影响会员真相源，不伪造支付记录。</p>
      </div>
      <div class="hero-section__actions">
        <el-button class="vip-button vip-button--hero vip-button--primary" type="primary" :icon="RefreshRight" :loading="refreshing" @click="refreshAll">
          刷新数据
        </el-button>
      </div>
    </div>

    <div class="stats-section" v-loading="dashboardLoading">
      <div class="stats-section__grid">
        <div v-for="card in statCards" :key="card.key" class="stats-section__card">
          <div class="stats-section__card-top">
            <span class="stats-section__label">{{ card.label }}</span>
            <el-icon class="stats-section__icon">
              <component :is="card.icon" />
            </el-icon>
          </div>
          <div class="stats-section__value">{{ card.value }}</div>
          <div class="stats-section__hint">{{ card.hint }}</div>
        </div>
      </div>
    </div>

    <div class="workspace-section">
      <el-tabs v-model="activeTab" class="workspace-section__tabs">
        <el-tab-pane label="会员列表" name="members">
          <div class="pane-section">
            <div class="pane-section__toolbar">
              <div class="pane-section__toolbar-group">
                <el-input v-model="memberFilters.keyword" placeholder="搜索用户ID / 用户名 / 昵称 / 邮箱" clearable class="pane-section__input pane-section__input--keyword" :prefix-icon="Search" @keyup.enter="handleMemberSearch" />
                <el-select v-model="memberFilters.status" placeholder="会员状态" clearable class="pane-section__input" @change="handleMemberSearch">
                  <el-option label="未开通" value="NONE" />
                  <el-option label="有效" value="ACTIVE" />
                  <el-option label="已过期" value="EXPIRED" />
                </el-select>
                <el-date-picker
                  v-model="memberExpireRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="到期时间开始"
                  end-placeholder="到期时间结束"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  class="pane-section__range"
                />
              </div>
              <div class="pane-section__toolbar-group pane-section__toolbar-group--actions">
                <el-button class="vip-button vip-button--primary" :icon="Search" type="primary" @click="handleMemberSearch">查询</el-button>
                <el-button class="vip-button vip-button--secondary" @click="resetMemberFilters">重置</el-button>
              </div>
            </div>

            <div class="pane-section__table-wrapper">
              <el-table v-loading="memberLoading" :data="memberList" class="pane-section__table pane-section__table--members">
                <el-table-column prop="userId" label="用户ID" width="88" />
                <el-table-column label="用户" width="250">
                  <template #default="{ row }">
                    <div class="user-cell">
                      <el-avatar class="user-cell__avatar" :src="resolveAvatar(row.avatar)" :size="40">{{ getAvatarFallback(row) }}</el-avatar>
                      <div class="user-cell__meta">
                        <span class="user-cell__name">{{ row.nickname || row.username }}</span>
                        <span class="user-cell__sub">{{ row.username }}</span>
                        <span class="user-cell__sub" v-if="row.email">{{ row.email }}</span>
                      </div>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column label="用户状态" width="110">
                  <template #default="{ row }">
                    <el-tag :type="getUserStatusMeta(row.userStatus).type">{{ getUserStatusMeta(row.userStatus).text }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="会员状态" width="110">
                  <template #default="{ row }">
                    <el-tag :type="getVipStatusMeta(row.vipStatus).type">{{ getVipStatusMeta(row.vipStatus).text }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="vipExpireTime" label="到期时间" min-width="170" show-overflow-tooltip />
                <el-table-column label="剩余天数" width="108">
                  <template #default="{ row }">
                    <span class="metric-text">{{ row.remainingDays || 0 }} 天</span>
                  </template>
                </el-table-column>
                <el-table-column prop="latestPlanName" label="最近套餐" min-width="120" show-overflow-tooltip />
                <el-table-column prop="lastOrderNo" label="最后订单号" min-width="210" show-overflow-tooltip />
                <el-table-column prop="loginTime" label="最近登录" min-width="168" show-overflow-tooltip />
                <el-table-column label="操作" width="300" fixed="right">
                  <template #default="{ row }">
                    <div class="action-group">
                      <el-button class="vip-button vip-button--soft-info" type="primary" plain size="small" :icon="View" @click="openMemberDetail(row.userId)">详情</el-button>
                      <el-button class="vip-button vip-button--soft-accent" type="warning" plain size="small" :icon="Plus" @click="openAdjustDialog(row, row.vipStatus === 'ACTIVE' ? 'EXTEND' : 'ACTIVATE')">
                        {{ row.vipStatus === "ACTIVE" ? "延期" : "开通" }}
                      </el-button>
                      <el-button v-if="row.vipStatus !== 'NONE'" class="vip-button vip-button--soft-danger" type="danger" plain size="small" :icon="SwitchButton" @click="openAdjustDialog(row, 'EXPIRE_NOW')">失效</el-button>
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </div>

            <div class="pane-section__pagination">
              <el-pagination
                v-model:current-page="memberFilters.pageNum"
                v-model:page-size="memberFilters.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="memberTotal"
                @size-change="fetchMemberPage"
                @current-change="fetchMemberPage"
              />
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="订单记录" name="orders">
          <div class="pane-section">
            <div class="pane-section__toolbar">
              <div class="pane-section__toolbar-group">
                <el-input v-model="orderFilters.orderNo" placeholder="搜索订单号" clearable class="pane-section__input" :prefix-icon="Search" @keyup.enter="handleOrderSearch" />
                <el-input v-model="orderFilters.userKeyword" placeholder="搜索用户ID / 用户名 / 昵称 / 邮箱" clearable class="pane-section__input pane-section__input--keyword" :prefix-icon="UserFilled" @keyup.enter="handleOrderSearch" />
                <el-select v-model="orderFilters.planCode" placeholder="套餐" clearable class="pane-section__input pane-section__input--compact" @change="handleOrderSearch">
                  <el-option v-for="plan in planList" :key="plan.id" :label="plan.name" :value="plan.code" />
                </el-select>
                <el-select v-model="orderFilters.status" placeholder="订单状态" clearable class="pane-section__input pane-section__input--compact" @change="handleOrderSearch">
                  <el-option label="待支付" value="PAYING" />
                  <el-option label="已支付" value="PAID" />
                  <el-option label="已关闭" value="CLOSED" />
                  <el-option label="失败" value="FAILED" />
                </el-select>
                <el-select v-model="orderFilters.channel" placeholder="渠道" clearable class="pane-section__input pane-section__input--compact" @change="handleOrderSearch">
                  <el-option label="支付宝" value="ALIPAY" />
                </el-select>
                <el-select v-model="orderFilters.clientType" placeholder="客户端" clearable class="pane-section__input pane-section__input--compact" @change="handleOrderSearch">
                  <el-option label="PC" value="PC" />
                  <el-option label="H5" value="H5" />
                </el-select>
                <el-date-picker
                  v-model="orderCreateRange"
                  type="datetimerange"
                  range-separator="至"
                  start-placeholder="创建时间开始"
                  end-placeholder="创建时间结束"
                  format="YYYY-MM-DD HH:mm:ss"
                  value-format="YYYY-MM-DD HH:mm:ss"
                  class="pane-section__range"
                />
              </div>
              <div class="pane-section__toolbar-group pane-section__toolbar-group--actions">
                <el-button class="vip-button vip-button--primary" :icon="Search" type="primary" @click="handleOrderSearch">查询</el-button>
                <el-button class="vip-button vip-button--secondary" @click="resetOrderFilters">重置</el-button>
              </div>
            </div>

            <div class="pane-section__table-wrapper">
              <el-table v-loading="orderLoading" :data="orderList" class="pane-section__table pane-section__table--orders">
                <el-table-column prop="orderNo" label="订单号" min-width="220" show-overflow-tooltip />
                <el-table-column label="用户" min-width="180" show-overflow-tooltip>
                  <template #default="{ row }">
                    <div class="order-user">
                      <span class="order-user__name">{{ row.nickname || row.username || `用户 ${row.userId}` }}</span>
                      <span class="order-user__sub">#{{ row.userId }}</span>
                      <span class="order-user__sub" v-if="row.email">{{ row.email }}</span>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column prop="planName" label="套餐" min-width="120" show-overflow-tooltip />
                <el-table-column label="金额" width="110">
                  <template #default="{ row }">
                    <span class="metric-text">¥{{ normalizePrice(row.priceYuan) }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="110">
                  <template #default="{ row }">
                    <el-tag :type="getOrderStatusMeta(row.status).type">{{ getOrderStatusMeta(row.status).text }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="渠道" width="90">
                  <template #default="{ row }">
                    <span>{{ row.channel || "-" }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="客户端" width="90">
                  <template #default="{ row }">
                    <span>{{ row.clientType || "-" }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="paidTime" label="支付时间" min-width="168" show-overflow-tooltip />
                <el-table-column prop="createTime" label="创建时间" min-width="168" show-overflow-tooltip />
              </el-table>
            </div>

            <div class="pane-section__pagination">
              <el-pagination
                v-model:current-page="orderFilters.pageNum"
                v-model:page-size="orderFilters.pageSize"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                :total="orderTotal"
                @size-change="fetchOrderPage"
                @current-change="fetchOrderPage"
              />
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="套餐管理" name="plans">
          <div class="pane-section pane-section--plans" v-loading="planLoading">
            <div class="plan-grid">
              <div v-for="plan in planList" :key="plan.id" class="plan-card">
                <div class="plan-card__header">
                  <div class="plan-card__title-block">
                    <span class="plan-card__code">{{ plan.code }}</span>
                    <h3 class="plan-card__title">{{ plan.name }}</h3>
                  </div>
                  <el-tag :type="plan.enabled ? 'success' : 'info'">{{ plan.enabled ? "启用中" : "已停用" }}</el-tag>
                </div>
                <div class="plan-card__price">
                  <span class="plan-card__currency">¥</span>
                  <span class="plan-card__amount">{{ normalizePrice(plan.priceYuan) }}</span>
                </div>
                <div class="plan-card__meta">
                  <span>{{ plan.days }} 天</span>
                  <span>{{ plan.description || "暂无套餐说明" }}</span>
                </div>
                <div class="plan-card__footer">
                  <span class="plan-card__time">更新于 {{ plan.updateTime || plan.createTime || "-" }}</span>
                  <el-button class="vip-button vip-button--soft-info" type="primary" plain :icon="EditPen" @click="openPlanDialog(plan)">编辑套餐</el-button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <el-drawer v-model="detailVisible" :size="isMobileView ? '100%' : '700px'" destroy-on-close class="detail-drawer">
      <template #header>
        <div class="detail-drawer__header">
          <span class="detail-drawer__title">会员详情</span>
          <span class="detail-drawer__subtitle">查看会员真相源与最近支付订单</span>
        </div>
      </template>

      <div class="detail-drawer__body" v-loading="detailLoading">
        <template v-if="memberDetail">
          <div class="detail-drawer__hero">
            <div class="detail-drawer__hero-user">
              <el-avatar class="detail-drawer__avatar" :src="resolveAvatar(memberDetail.avatar)" :size="56">{{ getAvatarFallback(memberDetail) }}</el-avatar>
              <div class="detail-drawer__hero-meta">
                <h3>{{ memberDetail.nickname || memberDetail.username }}</h3>
                <p>#{{ memberDetail.userId }} · {{ memberDetail.username }}</p>
                <p v-if="memberDetail.email">{{ memberDetail.email }}</p>
              </div>
            </div>
            <div class="detail-drawer__hero-tags">
              <el-tag :type="getVipStatusMeta(memberDetail.vipStatus).type">{{ getVipStatusMeta(memberDetail.vipStatus).text }}</el-tag>
              <el-tag :type="getUserStatusMeta(memberDetail.userStatus).type">{{ getUserStatusMeta(memberDetail.userStatus).text }}</el-tag>
            </div>
          </div>

          <div class="detail-drawer__panel">
            <div class="detail-drawer__grid">
              <div class="detail-drawer__item"><span class="detail-drawer__label">开始时间</span><span class="detail-drawer__value">{{ memberDetail.vipStartTime || "-" }}</span></div>
              <div class="detail-drawer__item"><span class="detail-drawer__label">到期时间</span><span class="detail-drawer__value">{{ memberDetail.vipExpireTime || "-" }}</span></div>
              <div class="detail-drawer__item"><span class="detail-drawer__label">剩余天数</span><span class="detail-drawer__value">{{ memberDetail.remainingDays || 0 }} 天</span></div>
              <div class="detail-drawer__item"><span class="detail-drawer__label">最近套餐</span><span class="detail-drawer__value">{{ memberDetail.latestPlanName || "-" }}</span></div>
              <div class="detail-drawer__item"><span class="detail-drawer__label">最后订单号</span><span class="detail-drawer__value detail-drawer__value--mono">{{ memberDetail.lastOrderNo || "-" }}</span></div>
              <div class="detail-drawer__item"><span class="detail-drawer__label">最近登录</span><span class="detail-drawer__value">{{ memberDetail.loginTime || "-" }}</span></div>
            </div>
            <div class="detail-drawer__actions">
              <el-button class="vip-button vip-button--primary" type="primary" @click="openAdjustDialog(memberDetail, memberDetail.vipStatus === 'ACTIVE' ? 'EXTEND' : 'ACTIVATE')">
                {{ memberDetail.vipStatus === "ACTIVE" ? "延期会员" : "开通会员" }}
              </el-button>
              <el-button v-if="memberDetail.vipStatus !== 'NONE'" class="vip-button vip-button--soft-danger" type="danger" plain @click="openAdjustDialog(memberDetail, 'EXPIRE_NOW')">立即失效</el-button>
            </div>
          </div>

          <div class="detail-drawer__panel">
            <div class="detail-drawer__section-title">最近订单</div>
            <div class="detail-drawer__table-wrapper">
              <el-table :data="memberDetail.recentOrders || []" class="detail-drawer__table" empty-text="暂无支付订单">
                <el-table-column prop="orderNo" label="订单号" min-width="190" show-overflow-tooltip />
                <el-table-column prop="planName" label="套餐" min-width="110" show-overflow-tooltip />
                <el-table-column label="金额" width="90"><template #default="{ row }">¥{{ normalizePrice(row.priceYuan) }}</template></el-table-column>
                <el-table-column label="状态" width="100">
                  <template #default="{ row }">
                    <el-tag :type="getOrderStatusMeta(row.status).type">{{ getOrderStatusMeta(row.status).text }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="paidTime" label="支付时间" min-width="160" show-overflow-tooltip />
              </el-table>
            </div>
          </div>
        </template>
      </div>
    </el-drawer>

    <el-dialog v-model="adjustDialogVisible" :title="adjustDialogTitle" width="520px" class="adjust-dialog" destroy-on-close>
      <el-form ref="adjustFormRef" :model="adjustForm" :rules="adjustRules" label-width="88px">
        <el-form-item label="用户">
          <div class="dialog-user-card">
            <span class="dialog-user-card__name">{{ adjustTarget?.nickname || adjustTarget?.username || `用户 ${adjustTarget?.userId || ""}` }}</span>
            <span class="dialog-user-card__sub">#{{ adjustTarget?.userId }}</span>
          </div>
        </el-form-item>
        <el-form-item label="操作" prop="actionType">
          <el-radio-group v-model="adjustForm.actionType">
            <el-radio-button label="ACTIVATE">立即开通</el-radio-button>
            <el-radio-button label="EXTEND">延期续期</el-radio-button>
            <el-radio-button label="EXPIRE_NOW">立即失效</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item v-if="adjustForm.actionType !== 'EXPIRE_NOW'" label="天数" prop="days">
          <el-input-number v-model="adjustForm.days" :min="1" :max="3650" controls-position="right" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="adjustForm.remark" type="textarea" :rows="4" maxlength="200" show-word-limit placeholder="记录本次人工调整原因，便于后续审计。" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="vip-button vip-button--secondary" @click="adjustDialogVisible = false">取消</el-button>
          <el-button class="vip-button vip-button--primary" type="primary" :loading="adjustSubmitting" @click="submitAdjust">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <el-dialog v-model="planDialogVisible" title="编辑套餐" width="540px" class="plan-dialog" destroy-on-close>
      <el-form ref="planFormRef" :model="planForm" :rules="planRules" label-width="88px">
        <el-form-item label="套餐编码"><el-input :model-value="planForm.code" disabled /></el-form-item>
        <el-form-item label="套餐名称" prop="name"><el-input v-model="planForm.name" maxlength="50" show-word-limit /></el-form-item>
        <el-form-item label="时长(天)" prop="days"><el-input-number v-model="planForm.days" :min="1" :max="3650" controls-position="right" /></el-form-item>
        <el-form-item label="售价(元)" prop="priceYuan"><el-input v-model="planForm.priceYuan" placeholder="例如 29.90" /></el-form-item>
        <el-form-item label="启用状态" prop="enabled"><el-switch v-model="planForm.enabled" /></el-form-item>
        <el-form-item label="套餐说明" prop="description"><el-input v-model="planForm.description" type="textarea" :rows="4" maxlength="100" show-word-limit /></el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button class="vip-button vip-button--secondary" @click="planDialogVisible = false">取消</el-button>
          <el-button class="vip-button vip-button--primary" type="primary" :loading="planSubmitting" @click="submitPlan">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref } from "vue";
import { EditPen, Medal, Plus, RefreshRight, Search, ShoppingBag, SwitchButton, Timer, UserFilled, View, WalletFilled } from "@element-plus/icons-vue";
import { adjustVipMember, getVipDashboard, getVipMemberDetail, getVipMemberPage, getVipOrderPage, getVipPlanList, updateVipPlan } from "@/api/vip";

const activeTab = ref("members");
const refreshing = ref(false);
const isMobileView = ref(false);

const dashboardLoading = ref(false);
const dashboard = reactive({
  activeMemberCount: 0,
  expiredMemberCount: 0,
  expiringSoonCount: 0,
  paidOrderCount30d: 0,
  paidAmountYuan30d: "0.00",
});

const memberLoading = ref(false);
const memberList = ref([]);
const memberTotal = ref(0);
const memberExpireRange = ref([]);
const memberFilters = reactive({
  pageNum: 1,
  pageSize: 10,
  keyword: "",
  status: "",
});

const orderLoading = ref(false);
const orderList = ref([]);
const orderTotal = ref(0);
const orderCreateRange = ref([]);
const orderFilters = reactive({
  pageNum: 1,
  pageSize: 10,
  orderNo: "",
  userKeyword: "",
  planCode: "",
  status: "",
  channel: "",
  clientType: "",
});

const planLoading = ref(false);
const planList = ref([]);

const detailVisible = ref(false);
const detailLoading = ref(false);
const memberDetail = ref(null);

const adjustDialogVisible = ref(false);
const adjustSubmitting = ref(false);
const adjustTarget = ref(null);
const adjustFormRef = ref(null);
const adjustForm = reactive({
  userId: null,
  actionType: "ACTIVATE",
  days: 30,
  remark: "",
});

const planDialogVisible = ref(false);
const planSubmitting = ref(false);
const planFormRef = ref(null);
const planForm = reactive({
  id: null,
  code: "",
  name: "",
  days: 30,
  priceYuan: "",
  enabled: true,
  description: "",
});

const statCards = computed(() => [
  { key: "active", label: "有效会员", value: dashboard.activeMemberCount, hint: "当前拥有有效访问资格的会员数", icon: Medal },
  { key: "expired", label: "已过期会员", value: dashboard.expiredMemberCount, hint: "需要二次触达或重新召回的用户", icon: Timer },
  { key: "expiring", label: "7日内到期", value: dashboard.expiringSoonCount, hint: "临期用户，适合做续费提醒", icon: ShoppingBag },
  { key: "orders", label: "近30天订单", value: dashboard.paidOrderCount30d, hint: "仅统计已支付订单", icon: UserFilled },
  { key: "amount", label: "近30天收入", value: `¥${normalizePrice(dashboard.paidAmountYuan30d)}`, hint: "支付金额汇总", icon: WalletFilled },
]);

const adjustDialogTitle = computed(() => {
  return adjustForm.actionType === "EXTEND" ? "延期会员" : adjustForm.actionType === "EXPIRE_NOW" ? "立即失效" : "立即开通";
});

const adjustRules = {
  actionType: [{ required: true, message: "请选择调整动作", trigger: "change" }],
  days: [{
    validator: (_, value, callback) => {
      if (adjustForm.actionType === "EXPIRE_NOW") {
        callback();
        return;
      }
      if (!value || Number(value) < 1) {
        callback(new Error("请输入有效天数"));
        return;
      }
      callback();
    },
    trigger: "blur",
  }],
  remark: [{ max: 200, message: "备注长度不能超过200个字符", trigger: "blur" }],
};

const planRules = {
  name: [{ required: true, message: "请输入套餐名称", trigger: "blur" }],
  days: [{ required: true, message: "请输入有效时长", trigger: "blur" }],
  priceYuan: [{
    validator: (_, value, callback) => {
      const normalizedValue = String(value ?? "").trim();
      if (!normalizedValue) {
        callback(new Error("请输入售价"));
        return;
      }
      if (!/^\d+(\.\d{1,2})?$/.test(normalizedValue) || Number(normalizedValue) <= 0) {
        callback(new Error("请输入正确的金额格式"));
        return;
      }
      callback();
    },
    trigger: "blur",
  }],
};

const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

const normalizePrice = (value) => {
  const numericValue = Number(value || 0);
  return Number.isNaN(numericValue) ? "0.00" : numericValue.toFixed(2);
};

const resolveAvatar = (avatar) => {
  const normalizedAvatar = String(avatar ?? "").trim();
  return normalizedAvatar || undefined;
};

const getAvatarFallback = (target) => {
  const normalizedLabel = String(target?.nickname || target?.username || "U").trim();
  return normalizedLabel.slice(0, 1).toUpperCase();
};

const buildMemberParams = () => {
  const [expireTimeStart, expireTimeEnd] = memberExpireRange.value || [];
  return {
    pageNum: memberFilters.pageNum,
    pageSize: memberFilters.pageSize,
    keyword: memberFilters.keyword || undefined,
    status: memberFilters.status || undefined,
    expireTimeStart: expireTimeStart || undefined,
    expireTimeEnd: expireTimeEnd || undefined,
  };
};

const buildOrderParams = () => {
  const [createTimeStart, createTimeEnd] = orderCreateRange.value || [];
  return {
    pageNum: orderFilters.pageNum,
    pageSize: orderFilters.pageSize,
    orderNo: orderFilters.orderNo || undefined,
    userKeyword: orderFilters.userKeyword || undefined,
    planCode: orderFilters.planCode || undefined,
    status: orderFilters.status || undefined,
    channel: orderFilters.channel || undefined,
    clientType: orderFilters.clientType || undefined,
    createTimeStart: createTimeStart || undefined,
    createTimeEnd: createTimeEnd || undefined,
  };
};

const fetchDashboard = async () => {
  dashboardLoading.value = true;
  try {
    const res = await getVipDashboard();
    Object.assign(dashboard, res.data.data || {});
  } catch (error) {
    ElMessage.error("获取会员总览失败");
  } finally {
    dashboardLoading.value = false;
  }
};

const fetchMemberPage = async () => {
  memberLoading.value = true;
  try {
    const res = await getVipMemberPage(buildMemberParams());
    memberList.value = res.data.data?.data || [];
    memberTotal.value = Number(res.data.data?.total || 0);
  } catch (error) {
    ElMessage.error("获取会员列表失败");
  } finally {
    memberLoading.value = false;
  }
};

const fetchOrderPage = async () => {
  orderLoading.value = true;
  try {
    const res = await getVipOrderPage(buildOrderParams());
    orderList.value = res.data.data?.data || [];
    orderTotal.value = Number(res.data.data?.total || 0);
  } catch (error) {
    ElMessage.error("获取会员订单失败");
  } finally {
    orderLoading.value = false;
  }
};

const fetchPlanList = async () => {
  planLoading.value = true;
  try {
    const res = await getVipPlanList();
    planList.value = res.data.data || [];
  } catch (error) {
    ElMessage.error("获取套餐列表失败");
  } finally {
    planLoading.value = false;
  }
};

const refreshAll = async () => {
  refreshing.value = true;
  try {
    await Promise.all([fetchDashboard(), fetchMemberPage(), fetchOrderPage(), fetchPlanList()]);
  } finally {
    refreshing.value = false;
  }
};

const handleMemberSearch = async () => {
  memberFilters.pageNum = 1;
  await fetchMemberPage();
};

const resetMemberFilters = async () => {
  memberFilters.pageNum = 1;
  memberFilters.pageSize = 10;
  memberFilters.keyword = "";
  memberFilters.status = "";
  memberExpireRange.value = [];
  await fetchMemberPage();
};

const handleOrderSearch = async () => {
  orderFilters.pageNum = 1;
  await fetchOrderPage();
};

const resetOrderFilters = async () => {
  orderFilters.pageNum = 1;
  orderFilters.pageSize = 10;
  orderFilters.orderNo = "";
  orderFilters.userKeyword = "";
  orderFilters.planCode = "";
  orderFilters.status = "";
  orderFilters.channel = "";
  orderFilters.clientType = "";
  orderCreateRange.value = [];
  await fetchOrderPage();
};

const openMemberDetail = async (userId) => {
  detailVisible.value = true;
  detailLoading.value = true;
  try {
    const res = await getVipMemberDetail(userId);
    memberDetail.value = res.data.data;
  } catch (error) {
    detailVisible.value = false;
    ElMessage.error("获取会员详情失败");
  } finally {
    detailLoading.value = false;
  }
};

const openAdjustDialog = (target, actionType) => {
  adjustTarget.value = target;
  adjustForm.userId = target.userId;
  adjustForm.actionType = actionType;
  adjustForm.days = 30;
  adjustForm.remark = "";
  adjustDialogVisible.value = true;
};

const submitAdjust = async () => {
  if (!adjustFormRef.value) return;
  try {
    await adjustFormRef.value.validate();
    adjustSubmitting.value = true;
    await adjustVipMember({
      userId: adjustForm.userId,
      actionType: adjustForm.actionType,
      days: adjustForm.actionType === "EXPIRE_NOW" ? undefined : adjustForm.days,
      remark: adjustForm.remark || undefined,
    });
    ElMessage.success("会员状态调整成功");
    adjustDialogVisible.value = false;
    await Promise.all([fetchDashboard(), fetchMemberPage()]);
    if (detailVisible.value && memberDetail.value?.userId === adjustForm.userId) {
      await openMemberDetail(adjustForm.userId);
    }
  } catch (error) {
    if (!error?.message) {
      ElMessage.error("会员状态调整失败");
    }
  } finally {
    adjustSubmitting.value = false;
  }
};

const openPlanDialog = (plan) => {
  planForm.id = plan.id;
  planForm.code = plan.code;
  planForm.name = plan.name;
  planForm.days = plan.days;
  planForm.priceYuan = normalizePrice(plan.priceYuan);
  planForm.enabled = !!plan.enabled;
  planForm.description = plan.description || "";
  planDialogVisible.value = true;
};

const submitPlan = async () => {
  if (!planFormRef.value) return;
  try {
    await planFormRef.value.validate();
    planSubmitting.value = true;
    await updateVipPlan({
      id: planForm.id,
      name: planForm.name,
      days: Number(planForm.days),
      priceFen: Math.round(Number(planForm.priceYuan) * 100),
      enabled: planForm.enabled,
      description: planForm.description || undefined,
    });
    ElMessage.success("套餐更新成功");
    planDialogVisible.value = false;
    await fetchPlanList();
  } catch (error) {
    if (!error?.message) {
      ElMessage.error("套餐更新失败");
    }
  } finally {
    planSubmitting.value = false;
  }
};

const getVipStatusMeta = (status) => ({ NONE: { text: "未开通", type: "info" }, ACTIVE: { text: "有效", type: "success" }, EXPIRED: { text: "已过期", type: "warning" } }[status] || { text: "未知", type: "info" });
const getOrderStatusMeta = (status) => ({ PAYING: { text: "待支付", type: "warning" }, PAID: { text: "已支付", type: "success" }, CLOSED: { text: "已关闭", type: "info" }, FAILED: { text: "失败", type: "danger" } }[status] || { text: status || "未知", type: "info" });
const getUserStatusMeta = (status) => (status === 0 ? { text: "正常", type: "success" } : { text: "禁用", type: "danger" });

onMounted(() => {
  handleResize();
  refreshAll();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
.vip-management-page {
  --bg-page: #f5f7fb;
  --bg-panel: #ffffff;
  --bg-panel-soft: #f8fafc;
  --bg-card: rgba(255, 255, 255, 0.88);
  --bg-card-strong: rgba(255, 255, 255, 0.94);
  --bg-plan-card: linear-gradient(180deg, rgba(248, 250, 252, 0.92), rgba(255, 255, 255, 0.96));
  --bg-hero: linear-gradient(135deg, rgba(15, 23, 42, 0.96), rgba(30, 41, 59, 0.9));
  --text-primary: #0f172a;
  --text-regular: #334155;
  --text-muted: #64748b;
  --text-inverse: #f8fafc;
  --border-color: #dbe3ee;
  --accent: #c0841a;
  --accent-soft: rgba(192, 132, 26, 0.12);
  --shadow-soft: 0 12px 28px rgba(15, 23, 42, 0.08);
  --shadow-card: 0 8px 20px rgba(15, 23, 42, 0.06);
  --hero-border: rgba(255, 255, 255, 0.2);
  --hero-eyebrow: rgba(248, 250, 252, 0.68);
  --hero-subtitle: rgba(248, 250, 252, 0.78);
  --page-glow: rgba(192, 132, 26, 0.12);
  --page-overlay-top: rgba(255, 255, 255, 0.4);
  --page-overlay-bottom: rgba(245, 247, 251, 0.9);
  --button-primary-bg: #2563eb;
  --button-primary-hover: #1d4ed8;
  --button-primary-text: #eff6ff;
  --button-secondary-bg: rgba(255, 255, 255, 0.88);
  --button-secondary-border: #cbd5e1;
  --button-secondary-text: #334155;
  --button-secondary-hover-bg: #f3f7fd;
  --button-secondary-hover-border: #93b4e4;
  --button-info-bg: #edf4ff;
  --button-info-border: #bfdbfe;
  --button-info-text: #2563eb;
  --button-info-hover-bg: #ddeafe;
  --button-info-hover-border: #93c5fd;
  --button-accent-bg: #fff5dc;
  --button-accent-border: #f3cf85;
  --button-accent-text: #a16207;
  --button-accent-hover-bg: #ffedbf;
  --button-accent-hover-border: #f0ba54;
  --button-danger-bg: #fff1f0;
  --button-danger-border: #f2b1ab;
  --button-danger-text: #dc2626;
  --button-danger-hover-bg: #ffe0dd;
  --button-danger-hover-border: #f38b82;
  --avatar-bg: #e8eef7;
  --avatar-border: #d3deeb;
  --avatar-text: #28435e;
  --input-bg: #ffffff;
  --input-border: #dbe3ee;
  --input-focus: #2563eb;
  --table-header-bg: #f3f7fd;
  --table-row-hover: #eef4ff;
  --table-fixed-bg: #ffffff;
  --dialog-bg: #ffffff;
  --dialog-header-border: #e2e8f0;
  --mask-bg: rgba(15, 23, 42, 0.48);

  min-height: 100%;
  padding: 24px;
  background:
    radial-gradient(circle at top right, var(--page-glow), transparent 28%),
    linear-gradient(180deg, var(--page-overlay-top), var(--page-overlay-bottom)),
    var(--bg-page);

  .hero-section {
    display: flex;
    justify-content: space-between;
    gap: 24px;
    align-items: flex-end;
    padding: 28px 32px;
    border: 1px solid var(--hero-border);
    border-radius: 24px;
    background: var(--bg-hero);
    box-shadow: var(--shadow-soft);

    .hero-section__copy {
      max-width: 760px;

      .hero-section__eyebrow {
        margin: 0 0 10px;
        font-size: 12px;
        letter-spacing: 0.24em;
        color: var(--hero-eyebrow);
      }

      .hero-section__title {
        margin: 0;
        font-size: 34px;
        line-height: 1.1;
        color: var(--text-inverse);
      }

      .hero-section__subtitle {
        margin: 12px 0 0;
        line-height: 1.75;
        color: var(--hero-subtitle);
      }
    }

    .hero-section__actions {
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }
  }

  .stats-section {
    margin-top: 22px;

    .stats-section__grid {
      display: grid;
      grid-template-columns: repeat(5, minmax(0, 1fr));
      gap: 16px;
    }

    .stats-section__card {
      padding: 22px 20px;
      border: 1px solid var(--border-color);
      border-radius: 20px;
      background: var(--bg-card);
      box-shadow: var(--shadow-card);

      .stats-section__card-top {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 14px;

        .stats-section__label {
          font-size: 13px;
          color: var(--text-muted);
        }

        .stats-section__icon {
          display: inline-flex;
          width: 34px;
          height: 34px;
          align-items: center;
          justify-content: center;
          border-radius: 12px;
          background: var(--accent-soft);
          color: var(--accent);
        }
      }

      .stats-section__value {
        font-size: 28px;
        font-weight: 700;
        color: var(--text-primary);
      }

      .stats-section__hint {
        margin-top: 10px;
        font-size: 12px;
        line-height: 1.6;
        color: var(--text-muted);
      }
    }
  }

  .workspace-section {
    margin-top: 22px;
    padding: 20px 22px 24px;
    border: 1px solid var(--border-color);
    border-radius: 24px;
    background: var(--bg-card-strong);
    box-shadow: var(--shadow-card);

    .workspace-section__tabs {
      :deep(.el-tabs__header) {
        margin-bottom: 20px;
      }

      :deep(.el-tabs__nav-wrap::after) {
        background: var(--border-color);
      }

      :deep(.el-tabs__active-bar) {
        background: var(--accent);
      }

      :deep(.el-tabs__item) {
        height: 40px;
        color: var(--text-regular);
        font-weight: 500;
      }

      :deep(.el-tabs__item.is-active) {
        color: var(--text-primary);
      }
    }

    .pane-section {
      .pane-section__toolbar {
        display: flex;
        align-items: flex-start;
        justify-content: space-between;
        gap: 16px;
        margin-bottom: 18px;

        .pane-section__toolbar-group {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          flex: 1;

          .pane-section__input {
            width: 220px;
          }

          /* 关键词搜索框需要容纳完整占位文案 */
          .pane-section__input--keyword {
            width: 280px;
          }

          /* 订单页的短标签下拉项不需要占太多横向空间 */
          .pane-section__input--compact {
            width: 168px;
          }

          :deep(.pane-section__range) {
            width: 300px;
            min-width: 300px;
            max-width: 300px;
            flex: 0 0 300px;
          }
        }

        .pane-section__toolbar-group--actions {
          flex: none;
          justify-content: flex-end;
        }
      }

      .pane-section__table-wrapper {
        overflow-x: auto;
        border: 1px solid var(--border-color);
        border-radius: 18px;
        background: var(--bg-panel);

        .pane-section__table {
          width: 100%;

          .user-cell {
            display: flex;
            align-items: center;
            gap: 12px;

            :deep(.el-avatar.user-cell__avatar) {
              flex: none;
              background: var(--avatar-bg);
              border: 1px solid var(--avatar-border);
              color: var(--avatar-text);
              font-weight: 700;
            }

            :deep(.el-avatar.user-cell__avatar img) {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            .user-cell__meta {
              display: flex;
              flex-direction: column;
              gap: 3px;

              .user-cell__name {
                color: var(--text-primary);
                font-weight: 600;
              }

              .user-cell__sub {
                color: var(--text-muted);
                font-size: 12px;
              }
            }
          }

          .order-user {
            display: flex;
            flex-direction: column;
            gap: 3px;

            .order-user__name {
              color: var(--text-primary);
              font-weight: 600;
            }

            .order-user__sub {
              color: var(--text-muted);
              font-size: 12px;
            }
          }

          .metric-text {
            color: var(--text-primary);
            font-weight: 600;
          }

          .action-group {
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
          }
        }

        .pane-section__table--members {
          min-width: 1260px;
        }

        .pane-section__table--orders {
          min-width: 1200px;
        }

        :deep(.el-table__header-wrapper th) {
          background: var(--bg-panel-soft);
          color: var(--text-regular);
        }

        :deep(.el-table__row) {
          color: var(--text-regular);
        }
      }

      .pane-section__pagination {
        display: flex;
        justify-content: flex-end;
        margin-top: 18px;
      }
    }

    .pane-section--plans {
      .plan-grid {
        display: grid;
        grid-template-columns: repeat(3, minmax(0, 1fr));
        gap: 16px;
      }

      .plan-card {
        display: flex;
        flex-direction: column;
        gap: 18px;
        padding: 22px;
        border: 1px solid var(--border-color);
        border-radius: 20px;
        background: var(--bg-plan-card);
        box-shadow: var(--shadow-card);

        .plan-card__header {
          display: flex;
          justify-content: space-between;
          gap: 14px;
          align-items: flex-start;

          .plan-card__title-block {
            .plan-card__code {
              display: inline-block;
              margin-bottom: 10px;
              color: var(--text-muted);
              font-size: 12px;
              letter-spacing: 0.12em;
            }

            .plan-card__title {
              margin: 0;
              font-size: 20px;
              color: var(--text-primary);
            }
          }
        }

        .plan-card__price {
          display: flex;
          align-items: flex-start;
          gap: 4px;
          color: var(--text-primary);

          .plan-card__currency {
            margin-top: 6px;
            font-size: 18px;
          }

          .plan-card__amount {
            font-size: 34px;
            font-weight: 700;
            line-height: 1;
          }
        }

        .plan-card__meta {
          display: flex;
          flex-direction: column;
          gap: 8px;
          color: var(--text-regular);
          line-height: 1.7;
        }

        .plan-card__footer {
          display: flex;
          justify-content: space-between;
          gap: 12px;
          align-items: center;

          .plan-card__time {
            color: var(--text-muted);
            font-size: 12px;
          }
        }
      }
    }
  }

  .detail-drawer {
    .detail-drawer__header {
      display: flex;
      flex-direction: column;
      gap: 4px;
    }

    .detail-drawer__title {
      color: var(--text-primary);
      font-size: 18px;
      font-weight: 600;
    }

    .detail-drawer__subtitle {
      color: var(--text-muted);
      font-size: 13px;
    }

    .detail-drawer__body {
      .detail-drawer__hero {
        display: flex;
        justify-content: space-between;
        gap: 16px;
        align-items: flex-start;
        padding: 18px;
        border: 1px solid var(--border-color);
        border-radius: 18px;
        background: var(--bg-panel-soft);
      }

      .detail-drawer__hero-user {
        display: flex;
        gap: 14px;
        align-items: center;

        :deep(.el-avatar.detail-drawer__avatar) {
          flex: none;
          background: var(--avatar-bg);
          border: 1px solid var(--avatar-border);
          color: var(--avatar-text);
          font-weight: 700;
        }

        :deep(.el-avatar.detail-drawer__avatar img) {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .detail-drawer__hero-meta {
        h3 {
          margin: 0;
          color: var(--text-primary);
          font-size: 20px;
        }

        p {
          margin: 6px 0 0;
          color: var(--text-muted);
        }
      }

      .detail-drawer__hero-tags {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        justify-content: flex-end;
      }

      .detail-drawer__panel {
        margin-top: 18px;
        padding: 18px;
        border: 1px solid var(--border-color);
        border-radius: 18px;
        background: var(--bg-panel);
      }

      .detail-drawer__grid {
        display: grid;
        grid-template-columns: repeat(2, minmax(0, 1fr));
        gap: 14px;
      }

      .detail-drawer__item {
        padding: 14px;
        border-radius: 14px;
        background: var(--bg-panel-soft);
      }

      .detail-drawer__label {
        display: block;
        margin-bottom: 8px;
        color: var(--text-muted);
        font-size: 12px;
      }

      .detail-drawer__value {
        color: var(--text-primary);
        font-weight: 600;
        line-height: 1.6;
      }

      .detail-drawer__value--mono {
        font-family: Consolas, Monaco, monospace;
        word-break: break-all;
      }

      .detail-drawer__actions {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        margin-top: 16px;
      }

      .detail-drawer__section-title {
        margin-bottom: 14px;
        color: var(--text-primary);
        font-size: 15px;
        font-weight: 600;
      }

      .detail-drawer__table-wrapper {
        overflow-x: auto;
        border: 1px solid var(--border-color);
        border-radius: 16px;
      }

      .detail-drawer__table {
        min-width: 760px;
      }
    }
  }

  .adjust-dialog {
    .dialog-user-card {
      display: flex;
      flex-direction: column;
      gap: 4px;
      padding: 12px 14px;
      width: 100%;
      border: 1px solid var(--border-color);
      border-radius: 14px;
      background: var(--bg-panel-soft);
    }

    .dialog-user-card__name {
      color: var(--text-primary);
      font-weight: 600;
    }

    .dialog-user-card__sub {
      color: var(--text-muted);
      font-size: 12px;
    }
  }

  :deep(.el-textarea__inner),
  :deep(.el-select__wrapper),
  :deep(.el-date-editor.el-input__wrapper),
  :deep(.el-input-number__decrease),
  :deep(.el-input-number__increase) {
    box-shadow: none;
    border: 1px solid var(--input-border);
    background: var(--input-bg);
    color: var(--text-primary);
  }

  :deep(.el-input__inner),
  :deep(.el-textarea__inner),
  :deep(.el-select__selected-item),
  :deep(.el-range-input) {
    color: var(--text-primary);
  }

  :deep(.el-input__wrapper.is-focus),
  :deep(.el-select__wrapper.is-focused),
  :deep(.el-textarea__inner:focus),
  :deep(.el-date-editor.is-active) {
    border-color: var(--input-focus);
  }

  :deep(.el-input__inner::placeholder),
  :deep(.el-textarea__inner::placeholder),
  :deep(.el-range-input::placeholder) {
    color: var(--text-muted);
  }

  :deep(.el-date-editor .el-range-separator),
  :deep(.el-date-editor .el-range__icon),
  :deep(.el-select__placeholder),
  :deep(.el-input__prefix-inner),
  :deep(.el-input__suffix-inner) {
    color: var(--text-muted);
  }

  :deep(.el-input-number) {
    width: 100%;
    background: var(--input-bg);
    border-radius: 12px;
  }

  /* 页面按钮皮肤 */
  :deep(.el-button.vip-button) {
    gap: 6px;
    height: 38px;
    padding: 0 16px;
    border: 1px solid var(--button-secondary-border);
    border-radius: 12px;
    background: var(--button-secondary-bg);
    color: var(--button-secondary-text);
    font-weight: 600;
    letter-spacing: 0.02em;
    box-shadow: none;
    transition:
      transform 0.2s ease,
      border-color 0.2s ease,
      background 0.2s ease,
      color 0.2s ease;
  }

  :deep(.el-button.vip-button:hover),
  :deep(.el-button.vip-button:focus-visible) {
    transform: translateY(-1px);
    border-color: var(--button-secondary-hover-border);
    color: var(--text-primary);
    background: var(--button-secondary-hover-bg);
  }

  :deep(.el-button.vip-button.el-button--small) {
    height: 30px;
    padding: 0 12px;
    border-radius: 10px;
  }

  :deep(.el-button.vip-button.is-loading),
  :deep(.el-button.vip-button.is-disabled) {
    transform: none;
    opacity: 0.78;
  }

  :deep(.el-button.vip-button .el-icon) {
    font-size: 14px;
  }

  :deep(.el-button.vip-button.vip-button--hero) {
    height: 42px;
    padding: 0 18px;
    border-radius: 14px;
  }

  :deep(.el-button.vip-button.vip-button--primary) {
    border-color: var(--button-primary-bg);
    background: var(--button-primary-bg);
    color: var(--button-primary-text);
  }

  :deep(.el-button.vip-button.vip-button--primary:hover),
  :deep(.el-button.vip-button.vip-button--primary:focus-visible) {
    border-color: var(--button-primary-hover);
    background: var(--button-primary-hover);
    color: var(--button-primary-text);
  }

  :deep(.el-button.vip-button.vip-button--secondary) {
    border-color: var(--button-secondary-border);
    background: var(--button-secondary-bg);
    color: var(--button-secondary-text);
  }

  :deep(.el-button.vip-button.vip-button--soft-info) {
    border-color: var(--button-info-border);
    background: var(--button-info-bg);
    color: var(--button-info-text);
  }

  :deep(.el-button.vip-button.vip-button--soft-info:hover),
  :deep(.el-button.vip-button.vip-button--soft-info:focus-visible) {
    border-color: var(--button-info-hover-border);
    background: var(--button-info-hover-bg);
    color: var(--button-info-text);
  }

  :deep(.el-button.vip-button.vip-button--soft-accent) {
    border-color: var(--button-accent-border);
    background: var(--button-accent-bg);
    color: var(--button-accent-text);
  }

  :deep(.el-button.vip-button.vip-button--soft-accent:hover),
  :deep(.el-button.vip-button.vip-button--soft-accent:focus-visible) {
    border-color: var(--button-accent-hover-border);
    background: var(--button-accent-hover-bg);
    color: var(--button-accent-text);
  }

  :deep(.el-button.vip-button.vip-button--soft-danger) {
    border-color: var(--button-danger-border);
    background: var(--button-danger-bg);
    color: var(--button-danger-text);
  }

  :deep(.el-button.vip-button.vip-button--soft-danger:hover),
  :deep(.el-button.vip-button.vip-button--soft-danger:focus-visible) {
    border-color: var(--button-danger-hover-border);
    background: var(--button-danger-hover-bg);
    color: var(--button-danger-text);
  }

  :deep(.el-table) {
    --el-table-bg-color: var(--bg-panel);
    --el-table-tr-bg-color: var(--bg-panel);
    --el-table-border-color: var(--border-color);
    --el-table-header-bg-color: var(--table-header-bg);
    --el-table-header-text-color: var(--text-regular);
    --el-table-row-hover-bg-color: var(--table-row-hover);
    --el-table-text-color: var(--text-regular);
    --el-fill-color-lighter: var(--table-row-hover);
    background: var(--bg-panel);
    color: var(--text-regular);
  }

  :deep(.el-table__header-wrapper th),
  :deep(.el-table__body-wrapper td) {
    border-bottom-color: var(--border-color);
  }

  :deep(.el-table__body tr:hover > td.el-table__cell) {
    background: var(--table-row-hover);
  }

  :deep(.el-table__inner-wrapper::before),
  :deep(.el-table__fixed::before),
  :deep(.el-table__fixed-right::before) {
    background: var(--border-color);
  }

  :deep(.el-table__fixed-right-patch),
  :deep(.el-table__fixed .el-table__cell),
  :deep(.el-table__fixed-right .el-table__cell) {
    background: var(--table-fixed-bg);
  }

  :deep(.el-pagination) {
    --el-pagination-bg-color: var(--bg-panel);
    --el-pagination-button-bg-color: var(--bg-panel);
    --el-pagination-button-color: var(--text-regular);
    --el-pagination-hover-color: var(--accent);
    color: var(--text-regular);
  }

  :deep(.el-pagination .btn-prev),
  :deep(.el-pagination .btn-next),
  :deep(.el-pagination .el-pager li),
  :deep(.el-pagination .el-pagination__sizes .el-select__wrapper),
  :deep(.el-pagination .el-pagination__jump .el-input__wrapper) {
    background: var(--bg-panel);
    border-color: var(--border-color);
    color: var(--text-regular);
  }

  :deep(.el-form-item__label) {
    color: var(--text-regular);
  }

  :deep(.el-radio-button__inner) {
    background: var(--button-secondary-bg);
    border-color: var(--button-secondary-border);
    color: var(--button-secondary-text);
    box-shadow: none;
  }

  :deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
    background: var(--button-primary-bg);
    border-color: var(--button-primary-bg);
    color: var(--button-primary-text);
    box-shadow: none;
  }

  :deep(.el-dialog),
  :deep(.el-drawer),
  :deep(.el-drawer__body) {
    background: var(--dialog-bg);
    color: var(--text-regular);
  }

  :deep(.el-dialog__header),
  :deep(.el-drawer__header) {
    border-bottom: 1px solid var(--dialog-header-border);
  }

  :deep(.el-overlay) {
    background: var(--mask-bg);
  }

  :deep(.el-dialog__header),
  :deep(.el-dialog__body),
  :deep(.el-dialog__footer),
  :deep(.el-drawer__header) {
    padding-left: 24px;
    padding-right: 24px;
  }
}

html.dark {
  .vip-management-page {
    --bg-page: #0b1220;
    --bg-panel: #111c2e;
    --bg-panel-soft: #162235;
    --bg-card: rgba(17, 28, 46, 0.92);
    --bg-card-strong: rgba(17, 28, 46, 0.96);
    --bg-plan-card: linear-gradient(180deg, rgba(17, 28, 46, 0.96), rgba(22, 34, 53, 0.98));
    --bg-hero: linear-gradient(135deg, rgba(9, 14, 24, 0.96), rgba(21, 33, 53, 0.96));
    --text-primary: #e2e8f0;
    --text-regular: #cbd5e1;
    --text-muted: #8ea0b7;
    --text-inverse: #f8fafc;
    --border-color: #22314a;
    --accent: #d4a23a;
    --accent-soft: rgba(212, 162, 58, 0.14);
    --shadow-soft: 0 14px 36px rgba(2, 6, 23, 0.32);
    --shadow-card: 0 10px 24px rgba(2, 6, 23, 0.26);
    --hero-border: rgba(148, 163, 184, 0.14);
    --hero-eyebrow: rgba(248, 250, 252, 0.68);
    --hero-subtitle: rgba(226, 232, 240, 0.74);
    --page-glow: rgba(212, 162, 58, 0.12);
    --page-overlay-top: rgba(11, 18, 32, 0.3);
    --page-overlay-bottom: rgba(11, 18, 32, 0.96);
    --button-primary-bg: #3b82f6;
    --button-primary-hover: #2563eb;
    --button-primary-text: #eff6ff;
    --button-secondary-bg: rgba(22, 34, 53, 0.92);
    --button-secondary-border: #32455f;
    --button-secondary-text: #d5deea;
    --button-secondary-hover-bg: #1c2b42;
    --button-secondary-hover-border: #4c6588;
    --button-info-bg: rgba(37, 99, 235, 0.14);
    --button-info-border: rgba(96, 165, 250, 0.34);
    --button-info-text: #93c5fd;
    --button-info-hover-bg: rgba(37, 99, 235, 0.22);
    --button-info-hover-border: rgba(96, 165, 250, 0.52);
    --button-accent-bg: rgba(212, 162, 58, 0.14);
    --button-accent-border: rgba(245, 200, 108, 0.34);
    --button-accent-text: #f5d58a;
    --button-accent-hover-bg: rgba(212, 162, 58, 0.22);
    --button-accent-hover-border: rgba(245, 200, 108, 0.5);
    --button-danger-bg: rgba(220, 38, 38, 0.14);
    --button-danger-border: rgba(248, 113, 113, 0.32);
    --button-danger-text: #fca5a5;
    --button-danger-hover-bg: rgba(220, 38, 38, 0.22);
    --button-danger-hover-border: rgba(248, 113, 113, 0.5);
    --avatar-bg: #18243a;
    --avatar-border: #32455f;
    --avatar-text: #e2e8f0;
    --input-bg: #111c2e;
    --input-border: #22314a;
    --input-focus: #3b82f6;
    --table-header-bg: #162235;
    --table-row-hover: #162235;
    --table-fixed-bg: #111c2e;
    --dialog-bg: #111c2e;
    --dialog-header-border: #22314a;
    --mask-bg: rgba(2, 6, 23, 0.7);

    background:
      radial-gradient(circle at top right, var(--page-glow), transparent 30%),
      linear-gradient(180deg, var(--page-overlay-top), var(--page-overlay-bottom)),
      var(--bg-page);
  }
}

@media screen and (max-width: 1200px) {
  .vip-management-page {
    .stats-section {
      .stats-section__grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }
    }

    .workspace-section {
      .pane-section--plans {
        .plan-grid {
          grid-template-columns: repeat(2, minmax(0, 1fr));
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .vip-management-page {
    padding: 16px;

    .hero-section {
      flex-direction: column;
      align-items: stretch;
      padding: 22px 20px;
    }

    .stats-section {
      .stats-section__grid {
        grid-template-columns: 1fr;
      }
    }

    .workspace-section {
      padding: 16px;

      .pane-section {
        .pane-section__toolbar {
          flex-direction: column;

          .pane-section__toolbar-group {
            width: 100%;

            .pane-section__input,
            :deep(.pane-section__range) {
              width: 100%;
              min-width: 0;
              max-width: none;
              flex: 1 1 auto;
            }
          }

          .pane-section__toolbar-group--actions {
            width: 100%;
            justify-content: stretch;
          }
        }

        .pane-section__pagination {
          justify-content: center;

          :deep(.el-pagination) {
            flex-wrap: wrap;
            justify-content: center;
          }
        }
      }

      .pane-section--plans {
        .plan-grid {
          grid-template-columns: 1fr;
        }
      }
    }

    .detail-drawer {
      .detail-drawer__body {
        .detail-drawer__hero {
          flex-direction: column;
        }

        .detail-drawer__grid {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}
</style>

<!-- 全局样式：dialog teleport 到 body 后 scoped 无法穿透 -->
<style lang="scss">
.plan-dialog.el-dialog {
  .el-dialog__header {
    padding-bottom: 24px !important;
  }

  .el-dialog__body {
    padding-top: 20px !important;
  }
}
</style>
