/**
 * 会员中心公共逻辑 Composable
 * 抽离所有三个风格版本共用的数据和方法
 */
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { info } from '@/api/user'
import { createVipOrder, getVipMe, getVipOrderList, getVipPlans, repayVipOrder } from '@/api/vip'
import { useUserStore } from '@/stores/userStore'
import { GetJwt } from '@/utils/Auth'
import { formatVipExpireDate, formatDate } from '@/utils/formatTime'

export function useVipCenter() {
  const router = useRouter()
  const route = useRoute()
  const userStore = useUserStore()

  // 页面核心状态
  const plans = ref([])
  const orders = ref([])
  const vipInfo = ref({})
  const submitting = ref(false)
  const selectedPlanCode = ref('')

  // 支付结果弹窗控制
  const paymentResultVisible = ref(false)
  const currentOrderNo = ref('')

  // 取消订单弹窗控制
  const cancelDialogVisible = ref(false)
  const cancelOrderNo = ref('')

  // 会员状态展示文案
  const statusText = computed(() => {
    if (vipInfo.value?.isVip) {
      return '有效会员'
    }
    if (vipInfo.value?.vipStatus === 'EXPIRED') {
      return '已过期'
    }
    return '未开通'
  })

  // 会员到期时间展示文案
  const expireText = computed(() => {
    if (!vipInfo.value?.vipExpireTime) {
      return '开通后立即生效'
    }
    return `到期时间 ${formatVipExpireDate(vipInfo.value.vipExpireTime)}`
  })

  // 根据 UA 粗略区分 PC 和 H5 支付入口
  const detectClientType = () => {
    return /Android|iPhone|iPad|Mobile/i.test(window.navigator.userAgent) ? 'H5' : 'PC'
  }

  // 支付宝 PC 场景返回表单时，前端需要主动插入并提交
  const submitPaymentForm = (formHtml, targetName) => {
    const wrapper = document.createElement('div')
    wrapper.style.display = 'none'
    wrapper.innerHTML = formHtml
    document.body.appendChild(wrapper)
    const form = wrapper.querySelector('form')
    if (!form) {
      ElMessage.error('支付跳转失败，请稍后重试')
      return
    }
    form.setAttribute('target', targetName)
    form.submit()
    setTimeout(() => {
      document.body.removeChild(wrapper)
    }, 1000)
  }

  // 刷新站点用户态
  const refreshUserInfo = async () => {
    const response = await info()
    userStore.user = response.data
  }

  // 刷新订单列表
  const refreshOrderList = async () => {
    const response = await getVipOrderList(1, 10)
    orders.value = response.data?.data || []
  }

  // 关闭支付结果弹窗
  const closePaymentResultModal = () => {
    paymentResultVisible.value = false
    currentOrderNo.value = ''
    refreshOrderList()
  }

  // 支付成功后的回调
  const handlePaymentSuccess = async () => {
    await refreshUserInfo()
    await refreshOrderList()
  }

  // 会员中心初始化时并行拉取套餐、会员信息和订单记录
  const fetchVipData = async () => {
    const [plansRes, vipRes, orderRes] = await Promise.all([
      getVipPlans(),
      getVipMe(),
      getVipOrderList(1, 10),
    ])
    plans.value = plansRes.data || []
    vipInfo.value = vipRes.data || {}
    orders.value = orderRes.data.data || []
    if (!selectedPlanCode.value && plans.value.length) {
      selectedPlanCode.value = plans.value[0].code
    }
  }

  // 创建订单后按后端返回的 formHtml / payUrl 决定跳转方式
  const handleCreateOrder = async (plan) => {
    const clientType = detectClientType()
    try {
      submitting.value = true
      selectedPlanCode.value = plan.code
      const response = await createVipOrder({
        planCode: plan.code,
        clientType,
      })
      const payload = response.data
      currentOrderNo.value = payload.orderNo
      paymentResultVisible.value = true

      if (payload.formHtml) {
        const targetName = `alipay_${Date.now()}`
        const paymentWindow = window.open('about:blank', targetName)
        if (!paymentWindow) {
          ElMessage.error('浏览器拦截了支付窗口，请允许弹窗后重试')
          return
        }
        submitPaymentForm(payload.formHtml, targetName)
        return
      }
      if (payload.payUrl) {
        window.open(payload.payUrl, '_blank')
        return
      }
      ElMessage.error('支付参数异常，请稍后重试')
    } catch (error) {
      closePaymentResultModal()
      ElMessage.error(error?.msg || '创建订单失败')
    } finally {
      submitting.value = false
    }
  }

  // 跳转到会员文章专区
  const goToVipArticles = () => {
    router.push('/vip/articles')
  }

  // 初始化
  const init = async () => {
    const jwt = GetJwt()
    if (!userStore.user?.id) {
      if (jwt) {
        try {
          const response = await info()
          userStore.user = response.data
        } catch {
          router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
          return
        }
      } else {
        router.push(`/login?redirect=${encodeURIComponent(route.fullPath)}`)
        return
      }
    }
    try {
      await fetchVipData()
      await refreshUserInfo()
      const orderNoFromQuery = route.query.orderNo || route.query.out_trade_no
      if (orderNoFromQuery) {
        currentOrderNo.value = String(orderNoFromQuery)
        paymentResultVisible.value = true
      }
    } catch (error) {
      ElMessage.error(error?.msg || '加载会员中心失败')
    }
  }

  // 订单状态中文映射
  const orderStatusText = (status) => {
    const map = {
      PAID: '已支付',
      PAYING: '待支付',
      CLOSED: '已关闭',
      FAILED: '失败',
    }
    return map[status] || status || '未知'
  }

  // 处理待支付订单的再次支付
  const handleRepayOrder = async (order) => {
    try {
      const response = await repayVipOrder(order.orderNo)
      const payload = response.data
      currentOrderNo.value = payload.orderNo
      paymentResultVisible.value = true
      if (payload.formHtml) {
        const targetName = `alipay_${Date.now()}`
        const paymentWindow = window.open('about:blank', targetName)
        if (!paymentWindow) {
          ElMessage.error('浏览器拦截了支付窗口，请允许弹窗后重试')
          return
        }
        submitPaymentForm(payload.formHtml, targetName)
        return
      }
      if (payload.payUrl) {
        window.open(payload.payUrl, '_blank')
        return
      }
      ElMessage.error('支付参数异常，请稍后重试')
    } catch (error) {
      ElMessage.error(error?.msg || '支付失败')
    }
  }

  // 处理待支付订单的取消
  const handleCancelOrder = (order) => {
    cancelOrderNo.value = order.orderNo
    cancelDialogVisible.value = true
  }

  // 取消订单确认后的回调
  const handleCancelConfirmed = async () => {
    ElMessage.success('订单已取消')
    await refreshOrderList()
  }

  return {
    // 状态
    plans,
    orders,
    vipInfo,
    submitting,
    selectedPlanCode,
    paymentResultVisible,
    currentOrderNo,
    cancelDialogVisible,
    cancelOrderNo,
    // 计算属性
    statusText,
    expireText,
    // 方法
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
  }
}
