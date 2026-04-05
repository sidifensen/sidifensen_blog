<template>
  <div class="login-form-wrapper">
    <div class="login-header">
      <h2 class="login-title">账号登录</h2>
      <p class="login-subtitle">请使用您的账号密码登录系统</p>
    </div>

    <el-form
      :model="formData"
      :rules="rules"
      ref="formDataRef"
      class="login-form"
      @keyup.enter="loginBtn"
    >
      <!-- 用户名/邮箱 -->
      <div class="form-item-wrap">
        <label class="form-label">用户名 / 邮箱</label>
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            placeholder="请输入用户名或邮箱"
            maxlength="50"
            clearable
            class="login-input"
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <!-- 密码 -->
      <div class="form-item-wrap">
        <label class="form-label">密码</label>
        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            placeholder="请输入密码"
            maxlength="20"
            show-password
            clearable
            class="login-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <!-- 验证码 -->
      <div class="form-item-wrap">
        <label class="form-label">验证码</label>
        <el-form-item prop="checkCode">
          <div class="verify-code-wrap">
            <el-input
              v-model="formData.checkCode"
              placeholder="请输入验证码"
              maxlength="6"
              class="login-input verify-input"
              @keyup.enter="loginBtn"
            >
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
            <img
              class="verify-image"
              :src="checkCodeInfo.checkCodeBase64"
              alt="验证码"
              @click="changeCheckCode()"
              title="点击刷新验证码"
            />
          </div>
        </el-form-item>
      </div>

      <!-- 记住我 & 忘记密码 -->
      <div class="form-options">
        <el-checkbox v-model="formData.rememberMe" class="remember-checkbox"> 记住我 </el-checkbox>
        <el-button type="primary" link class="forgot-link" @click="router.push('/reset')">
          忘记密码？
        </el-button>
      </div>

      <!-- 登录按钮 -->
      <el-button
        type="primary"
        class="login-btn"
        :loading="loginLoading"
        :disabled="loginLoading"
        @click="loginBtn"
      >
        {{ loginLoading ? '登录中...' : '安全登录' }}
      </el-button>

      <!-- 注册链接 -->
      <div class="register-link-wrap">
        <span class="register-text">还没有账号？</span>
        <el-button type="primary" link class="register-link" @click="router.push('/register')">
          立即注册
        </el-button>
      </div>

      <!-- 分隔线 -->
      <div class="divider">
        <span class="divider-text">或使用第三方登录</span>
      </div>

      <!-- 第三方登录 -->
      <div class="social-login">
        <a :href="giteeLogin" class="social-btn" title="Gitee">
          <svg-icon name="gitee" width="20px" height="20px" />
          <span>Gitee</span>
        </a>
        <a :href="githubLogin" class="social-btn" title="GitHub">
          <svg-icon name="github" width="20px" height="20px" />
          <span>GitHub</span>
        </a>
        <a :href="qqLogin" class="social-btn" title="QQ">
          <svg-icon name="qq" width="20px" height="20px" />
          <span>QQ</span>
        </a>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { giteeLogin, githubLogin, qqLogin } from '@/api/oauth'
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { login, checkCode, info } from '@/api/user'
import { SetJwt } from '@/utils/Auth'
import { useUserStore } from '@/stores/userStore.js'

const userStore = useUserStore()

const router = useRouter()
const formDataRef = ref(null)
const formData = ref({
  username: '',
  password: '',
  rememberMe: false,
  checkCodeKey: '',
  checkCode: '',
})
const loginLoading = ref(false)

// 验证用户名（支持用户名或邮箱登录）
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名或邮箱'))
  } else if (value.length < 4 || value.length > 50) {
    callback(new Error('长度必须在 4-50 个字符之间'))
  } else {
    callback()
  }
}

// 验证密码字符类型
const validatePasswordCharacters = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (!/^[a-zA-Z0-9@]+$/.test(value)) {
    callback(new Error('密码只能包含英文、数字和@符号'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码的长度必须在 6-20 个字符之间'))
  } else {
    callback()
  }
}

// 验证验证码
const validateCheckCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入验证码'))
  } else {
    callback()
  }
}

const rules = ref({
  username: [{ validator: validateUsername, trigger: ['blur', 'change'] }],
  password: [{ validator: validatePasswordCharacters, trigger: ['blur', 'change'] }],
  checkCode: [{ validator: validateCheckCode, trigger: ['blur', 'change'] }],
})

// 登录按钮
const loginBtn = async () => {
  if (loginLoading.value) {
    return
  }

  loginLoading.value = true

  try {
    await formDataRef.value.validate()
  } catch {
    ElMessage.error('请填写完整信息')
    loginLoading.value = false
    return
  }

  try {
    const res = await login(formData.value)
    ElMessage.success('登录成功')
    // 将 jwt 存储到 localStorage
    SetJwt(res.data)
    // 等待 info() 获取用户信息完成后再跳转，避免 Header 组件挂载时 user 为空
    const userInfoRes = await info()
    userStore.user = userInfoRes.data
    router.push({ name: 'Home' })
  } catch {
    // 登录失败后刷新验证码，避免继续使用旧验证码
    await changeCheckCode()
  } finally {
    loginLoading.value = false
  }
}

const checkCodeInfo = ref({})
// 刷新验证码
const changeCheckCode = async () => {
  await checkCode().then((res) => {
    checkCodeInfo.value = res.data
    formData.value.checkCodeKey = res.data.checkCodeKey
  })
}

// 测试模式自动填充（仅开发环境生效）
const autoFillTestAccount = () => {
  if (import.meta.env.VITE_TEST_MODE === 'true') {
    formData.value.username = import.meta.env.VITE_TEST_USERNAME || 'test'
    formData.value.password = import.meta.env.VITE_TEST_PASSWORD || '123456'
  }
}

// 页面加载完成后刷新验证码
onMounted(() => {
  changeCheckCode()
  autoFillTestAccount()
})
</script>

<style lang="scss" scoped>
// CSS 变量定义
.login-form-wrapper {
  // 浅色模式
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --text-muted: #94a3b8;
  --input-bg: #f1f5f9;
  --input-border: transparent;
  --input-border-focus: #3b82f6;
  --btn-bg: #1e3a5f;
  --btn-bg-hover: #2c5282;
  --divider-bg: #e2e8f0;
  --social-bg: #f8fafc;
  --social-border: #e2e8f0;
  --social-text: #475569;
  --social-hover-bg: #f1f5f9;
  --link-color: #3b82f6;

  width: 100%;
  max-width: 400px;
}

// 黑夜模式
html.dark {
  .login-form-wrapper {
    --text-primary: #f1f5f9;
    --text-secondary: #94a3b8;
    --text-muted: #64748b;
    --input-bg: rgba(30, 41, 59, 0.6);
    --input-border: rgba(148, 163, 184, 0.2);
    --input-border-focus: #60a5fa;
    --btn-bg: #3b82f6;
    --btn-bg-hover: #60a5fa;
    --divider-bg: #334155;
    --social-bg: rgba(30, 41, 59, 0.6);
    --social-border: rgba(148, 163, 184, 0.2);
    --social-text: #cbd5e1;
    --social-hover-bg: rgba(59, 130, 246, 0.1);
    --link-color: #60a5fa;
  }
}

// 头部
.login-header {
  margin-bottom: 40px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

// 表单
.login-form {
  ::v-deep(.el-form-item) {
    margin-bottom: 0;
  }

  ::v-deep(.el-form-item__error) {
    padding-top: 4px;
  }
}

.form-item-wrap {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

// 输入框样式
.login-input {
  ::v-deep(.el-input__wrapper) {
    background: var(--input-bg);
    border: 2px solid var(--input-border);
    border-radius: 8px;
    box-shadow: none;
    padding: 4px 16px;
    transition: all 0.3s;

    &:hover {
      border-color: var(--input-border-focus);
    }

    &.is-focus {
      border-color: var(--input-border-focus);
      background: var(--el-bg-color);
      box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    }
  }

  ::v-deep(.el-input__inner) {
    height: 44px;
    font-size: 15px;
    color: var(--text-primary);

    &::placeholder {
      color: var(--text-muted);
    }
  }

  ::v-deep(.el-input__prefix) {
    color: var(--text-muted);
    margin-right: 8px;
  }
}

// 验证码区域
.verify-code-wrap {
  display: flex;
  gap: 12px;
  align-items: center;
}

.verify-input {
  flex: 1;
}

.verify-image {
  flex: 1;
  min-width: 120px;
  max-width: 160px;
  height: 54px;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid var(--divider-bg);
  transition: border-color 0.3s;
  object-fit: contain;
  background: var(--input-bg);

  &:hover {
    border-color: var(--input-border-focus);
  }
}

// 选项区（记住我 & 忘记密码）
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 8px 0 24px;
}

.remember-checkbox {
  ::v-deep(.el-checkbox__label) {
    font-size: 14px;
    color: var(--text-secondary);
  }

  ::v-deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
    color: var(--text-secondary);
  }

  ::v-deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: var(--btn-bg);
    border-color: var(--btn-bg);
  }
}

.forgot-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--link-color);

  &:hover {
    color: var(--link-color);
  }
}

// 登录按钮
.login-btn {
  width: 100%;
  height: 48px;
  background: var(--btn-bg);
  border-color: var(--btn-bg);
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s;

  &:hover {
    background: var(--btn-bg-hover);
    border-color: var(--btn-bg-hover);
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }

  &.is-loading {
    opacity: 0.8;
  }
}

// 注册链接
.register-link-wrap {
  text-align: center;
  margin-top: 24px;
}

.register-text {
  font-size: 14px;
  color: var(--text-secondary);
}

.register-link {
  font-size: 14px;
  font-weight: 600;
  color: var(--link-color);
  margin-left: 4px;

  &:hover {
    color: var(--link-color);
  }
}

// 分隔线
.divider {
  display: flex;
  align-items: center;
  margin: 30px 0;

  &::before,
  &::after {
    content: '';
    flex: 1;
    height: 1px;
    background: var(--divider-bg);
  }
}

.divider-text {
  padding: 0 16px;
  font-size: 13px;
  color: var(--text-muted);
  white-space: nowrap;
}

// 第三方登录
.social-login {
  display: flex;
  gap: 12px;
  justify-content: center;
}

.social-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px;
  background: var(--social-bg);
  border: 1px solid var(--social-border);
  border-radius: 8px;
  font-size: 14px;
  color: var(--social-text);
  text-decoration: none;
  cursor: pointer;
  transition: all 0.3s;

  &:hover {
    background: var(--social-hover-bg);
    border-color: var(--link-color);
    color: var(--link-color);
    text-decoration: none;
  }

  span {
    font-weight: 500;
  }
}

// 移动端适配
@media screen and (max-width: 480px) {
  .login-title {
    font-size: 24px;
  }

  .verify-code-wrap {
    flex-direction: column;
    align-items: stretch;
  }

  .verify-image {
    width: 100%;
    height: 48px;
    object-fit: contain;
    background: var(--input-bg);
  }

  .social-btn span {
    display: none;
  }
}
</style>
