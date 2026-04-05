<template>
  <div class="oauth-callback">
    <div class="callback-card">
      <el-icon class="callback-icon" :class="{ 'is-error': status === 'error' }">
        <Loading v-if="status === 'loading'" />
        <CircleCheckFilled v-else-if="status === 'success'" />
        <CircleCloseFilled v-else />
      </el-icon>
      <h2>{{ title }}</h2>
      <p>{{ description }}</p>
      <el-button v-if="status === 'error'" type="primary" @click="goToLogin">返回登录页</el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { CircleCheckFilled, CircleCloseFilled, Loading } from '@element-plus/icons-vue'
import { exchangeOauthTicket, info } from '@/api/user'
import { SetJwt } from '@/utils/Auth'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const status = ref('loading')

const title = computed(() => {
  if (status.value === 'success') {
    return '登录成功'
  }
  if (status.value === 'error') {
    return '登录失败'
  }
  return '正在完成登录'
})

const description = computed(() => {
  if (status.value === 'success') {
    return '正在跳转到首页，请稍候。'
  }
  if (status.value === 'error') {
    return 'OAuth 登录票据无效或已过期，请重新发起第三方登录。'
  }
  return '正在校验 OAuth 票据并同步你的登录状态。'
})

const goToLogin = () => {
  router.replace('/login')
}

const handleOauthCallback = async () => {
  const ticket = route.query.ticket
  if (typeof ticket !== 'string' || !ticket) {
    status.value = 'error'
    return
  }

  try {
    const exchangeRes = await exchangeOauthTicket({ ticket })
    SetJwt(exchangeRes.data)

    const userRes = await info()
    userStore.user = userRes.data
    status.value = 'success'

    await router.replace({ name: 'Home' })
  } catch (error) {
    status.value = 'error'
  }
}

onMounted(() => {
  handleOauthCallback()
})
</script>

<style lang="scss" scoped>
.oauth-callback {
  --callback-bg: var(--el-bg-color);
  --callback-border: var(--el-border-color);
  --callback-title: var(--el-text-color-primary);
  --callback-text: var(--el-text-color-regular);
  --callback-shadow: 0 12px 30px rgba(15, 23, 42, 0.08);

  .callback-card {
    // OAuth 回调状态卡片
    display: flex;
    width: 420px;
    min-height: 240px;
    padding: 32px 28px;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 16px;
    background: var(--callback-bg);
    border: 1px solid var(--callback-border);
    border-radius: 20px;
    box-shadow: var(--callback-shadow);

    .callback-icon {
      // 状态图标
      font-size: 40px;
      color: var(--el-color-primary);

      &.is-error {
        color: var(--el-color-danger);
      }
    }

    h2 {
      margin: 0;
      color: var(--callback-title);
      font-size: 24px;
      font-weight: 600;
    }

    p {
      margin: 0;
      color: var(--callback-text);
      line-height: 1.7;
    }
  }
}

html.dark {
  .oauth-callback {
    --callback-shadow: 0 16px 36px rgba(2, 6, 23, 0.28);
  }
}
</style>
