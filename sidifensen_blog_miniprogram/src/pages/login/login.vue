<script setup>
import { ref, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { wxLogin, passwordLogin, exchangeTicket } from '@/api/oauth'
import { getCheckCode, getMyInfo } from '@/api/user'
import { getWxLoginCode } from '@/utils/auth'

const userStore = useUserStore()

// 登录方式：weixin / password
const loginType = ref('weixin')
// 表单数据
const formData = ref({
  username: '',
  password: '',
  rememberMe: false,
  checkCodeKey: '',
  checkCode: ''
})
// 验证码图片
const checkCodeUrl = ref('')
// 按钮加载状态
const loading = ref(false)
// 验证码加载状态
const checkCodeLoading = ref(false)

/**
 * 获取验证码
 */
async function fetchCheckCode() {
  try {
    checkCodeLoading.value = true
    const res = await getCheckCode()
    // 验证码图片 base64
    checkCodeUrl.value = res.data.checkCodeBase64
    // 验证码 key
    formData.value.checkCodeKey = res.data.checkCodeKey
  } catch (err) {
    console.error('获取验证码失败', err)
  } finally {
    checkCodeLoading.value = false
  }
}

/**
 * 刷新验证码
 */
function refreshCheckCode() {
  fetchCheckCode()
}

/**
 * 微信登录
 */
async function handleWxLogin() {
  try {
    const code = await getWxLoginCode()
    loading.value = true

    // 第一步：用 code 获取一次性票据
    const ticketRes = await wxLogin(code)
    const ticket = ticketRes.data

    // 第二步：用票据兑换 JWT token
    const jwtRes = await exchangeTicket(ticket)
    const token = jwtRes.data

    // 第三步：先保存 token，再获取用户信息（getMyInfo 需要带 token）
    userStore.setToken(token)

    // 第四步：获取用户信息
    const userInfoRes = await getMyInfo()
    const userInfo = userInfoRes.data

    // 第五步：存储登录状态
    userStore.login(token, userInfo)

    uni.showToast({ title: '登录成功', icon: 'success' })

    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1500)
  } catch (err) {
    uni.showToast({
      title: err.message || '微信登录失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

/**
 * 账号密码登录
 */
async function handlePasswordLogin() {
  if (!formData.value.username || !formData.value.password) {
    uni.showToast({ title: '请输入用户名和密码', icon: 'none' })
    return
  }
  if (!formData.value.checkCode) {
    uni.showToast({ title: '请输入验证码', icon: 'none' })
    return
  }

  try {
    loading.value = true

    const res = await passwordLogin({
      username: formData.value.username,
      password: formData.value.password,
      rememberMe: formData.value.rememberMe,
      checkCodeKey: formData.value.checkCodeKey,
      checkCode: formData.value.checkCode
    })

    // 获取用户信息（先保存 token，再获取用户信息）
    userStore.setToken(res.data)
    const userInfoRes = await getMyInfo()
    const userInfo = userInfoRes.data

    userStore.login(res.data, userInfo)

    uni.showToast({ title: '登录成功', icon: 'success' })

    setTimeout(() => {
      uni.switchTab({ url: '/pages/index/index' })
    }, 1500)
  } catch (err) {
    // 登录失败，刷新验证码
    refreshCheckCode()
    formData.value.checkCode = ''
    uni.showToast({
      title: err.message || '登录失败',
      icon: 'none'
    })
  } finally {
    loading.value = false
  }
}

/**
 * 切换登录方式
 */
function switchLoginType(type) {
  loginType.value = type
  if (type === 'password') {
    fetchCheckCode()
  }
}

onMounted(() => {
  if (loginType.value === 'password') {
    fetchCheckCode()
  }
})
</script>

<template>
  <view class="login-page">
    <!-- 背景装饰 -->
    <view class="bg-decoration">
      <view class="circle circle-1"></view>
      <view class="circle circle-2"></view>
    </view>

    <!-- 登录卡片 -->
    <view class="login-card">
      <!-- 标题 -->
      <view class="login-header">
        <view class="logo">&#xe8f0;</view>
        <view class="title">斯蒂芬森博客</view>
        <view class="subtitle">欢迎回来</view>
      </view>

      <!-- 微信登录 -->
      <view v-if="loginType === 'weixin'" class="login-content">
        <button
          class="btn-weixin"
          :disabled="loading"
          @click="handleWxLogin"
        >
          <text class="weixin-icon">&#xe8f1;</text>
          <text>微信授权登录</text>
        </button>

        <view class="switch-tip">
          <text>其他登录方式</text>
          <text class="link" @click="switchLoginType('password')">账号密码</text>
        </view>
      </view>

      <!-- 账号密码登录 -->
      <view v-else class="login-content">
        <view class="form-item">
          <input
            v-model="formData.username"
            class="form-input"
            placeholder="用户名"
            type="text"
          />
        </view>
        <view class="form-item">
          <input
            v-model="formData.password"
            class="form-input"
            placeholder="密码"
            type="password"
            password
          />
        </view>
        <view class="form-item check-code-item">
          <input
            v-model="formData.checkCode"
            class="form-input check-code-input"
            placeholder="验证码"
            type="text"
            maxlength="4"
          />
          <view
            class="check-code-img"
            :class="{ loading: checkCodeLoading }"
            @click="refreshCheckCode"
          >
            <image
              v-if="checkCodeUrl"
              :src="checkCodeUrl"
              mode="aspectFit"
              class="check-code-image"
            />
            <text v-else class="check-code-placeholder">加载中</text>
          </view>
        </view>

        <view class="form-item remember-me">
          <label @click="formData.rememberMe = !formData.rememberMe">
            <checkbox :checked="formData.rememberMe" />
            <text>记住我</text>
          </label>
        </view>

        <button
          class="btn-primary"
          :disabled="loading"
          @click="handlePasswordLogin"
        >
          {{ loading ? '登录中...' : '登录' }}
        </button>

        <view class="switch-tip">
          <text>其他登录方式</text>
          <text class="link" @click="switchLoginType('weixin')">微信登录</text>
        </view>
      </view>
    </view>

    <!-- 底部协议 -->
    <view class="agreement">
      <text>登录即表示同意</text>
      <text class="link">《用户协议》</text>
      <text>和</text>
      <text class="link">《隐私政策》</text>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  background: var(--bg-page);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;

  .circle {
    position: absolute;
    border-radius: 50%;
    background: var(--color-primary);
    opacity: 0.1;
  }

  .circle-1 {
    width: 300px;
    height: 300px;
    top: -100px;
    right: -100px;
  }

  .circle-2 {
    width: 200px;
    height: 200px;
    bottom: -50px;
    left: -50px;
  }
}

/* 登录卡片 */
.login-card {
  width: 320px;
  background: var(--bg-card);
  border-radius: var(--radius-lg);
  padding: var(--spacing-2xl);
  box-shadow: var(--shadow-md);
  position: relative;
  z-index: 1;
}

/* 登录头部 */
.login-header {
  text-align: center;
  margin-bottom: var(--spacing-2xl);

  .logo {
    font-size: 48px;
    color: var(--color-primary);
    margin-bottom: var(--spacing-md);
  }

  .title {
    font-size: 24px;
    font-weight: 600;
    color: var(--text-primary);
  }

  .subtitle {
    font-size: 14px;
    color: var(--text-muted);
    margin-top: var(--spacing-sm);
  }
}

/* 登录内容 */
.login-content {
  .form-item {
    margin-bottom: var(--spacing-md);
  }

  .form-input {
    width: 100%;
    height: 44px;
    padding: 0 var(--spacing-md);
    border: 1px solid var(--border);
    border-radius: var(--radius-md);
    font-size: 14px;
    background: var(--bg-page);
    color: var(--text-primary);
    box-sizing: border-box;

    &:focus {
      border-color: var(--color-primary);
    }
  }

  .check-code-item {
    display: flex;
    gap: var(--spacing-sm);

    .check-code-input {
      flex: 1;
    }

    .check-code-img {
      width: 100px;
      height: 44px;
      border: 1px solid var(--border);
      border-radius: var(--radius-md);
      background: var(--bg-page);
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      cursor: pointer;

      &.loading {
        opacity: 0.6;
      }

      .check-code-image {
        width: 100%;
        height: 100%;
      }

      .check-code-placeholder {
        font-size: 12px;
        color: var(--text-muted);
      }
    }
  }

  .remember-me {
    margin-bottom: var(--spacing-lg);

    label {
      display: flex;
      align-items: center;
      gap: var(--spacing-sm);
      font-size: 14px;
      color: var(--text-regular);
    }
  }

  .btn-weixin {
    width: 100%;
    height: 48px;
    background: #07c160;
    color: #ffffff;
    border: none;
    border-radius: var(--radius-md);
    font-size: 16px;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: var(--spacing-sm);

    .weixin-icon {
      font-size: 20px;
    }
  }

  .btn-primary {
    width: 100%;
    height: 48px;
    background: var(--color-primary);
    color: #ffffff;
    border: none;
    border-radius: var(--radius-md);
    font-size: 16px;
  }

  .switch-tip {
    margin-top: var(--spacing-lg);
    text-align: center;
    font-size: 14px;
    color: var(--text-muted);

    .link {
      color: var(--color-primary);
      margin-left: 4px;
    }
  }
}

/* 底部协议 */
.agreement {
  position: absolute;
  bottom: var(--spacing-2xl);
  font-size: 12px;
  color: var(--text-muted);
  text-align: center;

  .link {
    color: var(--color-primary);
  }
}
</style>
