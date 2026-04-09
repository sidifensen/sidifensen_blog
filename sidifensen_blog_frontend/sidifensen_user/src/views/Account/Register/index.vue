<template>
  <div class="register-form-wrapper">
    <div class="register-header">
      <h2 class="register-title">创建账号</h2>
      <p class="register-subtitle">加入我们，开始创作之旅</p>
    </div>

    <el-form :model="formData" :rules="rules" ref="formDataRef" class="register-form">
      <!-- 用户名 -->
      <div class="form-item-wrap">
        <label class="form-label">用户名</label>
        <el-form-item prop="username">
          <el-input
            v-model="formData.username"
            maxlength="20"
            placeholder="请输入用户名"
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
            maxlength="20"
            placeholder="请输入密码"
            show-password
            class="login-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <!-- 重复密码 -->
      <div class="form-item-wrap">
        <label class="form-label">确认密码</label>
        <el-form-item prop="repeatPassword">
          <el-input
            v-model="formData.repeatPassword"
            maxlength="20"
            placeholder="请再次输入密码"
            show-password
            class="login-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <!-- 邮箱 -->
      <div class="form-item-wrap">
        <label class="form-label">邮箱地址</label>
        <el-form-item prop="email">
          <el-input
            v-model="formData.email"
            type="email"
            placeholder="请输入邮箱地址"
            class="login-input"
          >
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <!-- 验证码 -->
      <div class="form-item-wrap">
        <label class="form-label">邮箱验证码</label>
        <el-form-item prop="emailCheckCode">
          <div class="verify-code-wrap">
            <el-input
              v-model="formData.emailCheckCode"
              maxlength="6"
              placeholder="请输入验证码"
              class="login-input verify-input"
            >
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
            <el-button
              class="send-code-btn"
              :disabled="!isEmailValid || waitTime > 0"
              @click="sendEmailBtn"
            >
              {{ waitTime > 0 ? `${waitTime}s` : '获取验证码' }}
            </el-button>
          </div>
        </el-form-item>
      </div>

      <el-button class="login-btn" type="primary" @click="registerBtn"> 立即注册 </el-button>
    </el-form>

    <!-- 登录链接 -->
    <div class="login-link-wrap">
      <span class="login-text">已有账号？</span>
      <el-button type="primary" link class="login-link" @click="router.push('/login')">
        立即登录
      </el-button>
    </div>
  </div>
</template>
<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { EditPen, Lock, Message, User } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { register, sendEmail } from '@/api/user'

// 邮件发送等待时间
const waitTime = ref(0)
const formDataRef = ref()
const router = useRouter()
// 提交表单
const formData = ref({
  username: '',
  password: '',
  repeatPassword: '',
  email: '',
  emailCheckCode: '',
})

// 验证用户名
const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
    callback(new Error('用户名只能是英文和数字'))
  } else if (value.length < 4 || value.length > 20) {
    callback(new Error('用户名的长度必须在 4-20 个字符之间'))
  } else {
    callback()
  }
}

// 验证密码字符类型
const validatePasswordCharacters = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else if (!/^[a-zA-Z0-9!@#$%^&*._-]+$/.test(value)) {
    callback(new Error('密码只能包含英文、数字和特殊字符(!@#$%^&*._-)'))
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error('密码的长度必须在 6-20 个字符之间'))
  } else {
    callback()
  }
}

// 验证重复密码
const validatePassword = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== formData.value.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const EmailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/

// 验证邮箱格式
const validateEmailFormat = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入邮箱'))
  } else if (!EmailRegex.test(value)) {
    callback(new Error('请输入合法的邮箱'))
  } else {
    callback()
  }
}

// 验证邮箱验证码
const validateEmailCheckCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请输入获取的验证码'))
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error('验证码必须是6位数字'))
  } else {
    callback()
  }
}

// 判断邮箱是否正确
const isEmailValid = computed(() => {
  // 确保邮箱不为空且符合正则表达式
  return formData.value.email && EmailRegex.test(formData.value.email)
})

const rules = {
  username: [{ validator: validateUsername, trigger: ['blur', 'change'] }],
  password: [{ validator: validatePasswordCharacters, trigger: ['blur', 'change'] }],
  repeatPassword: [{ validator: validatePassword, trigger: ['blur', 'change'] }],
  email: [{ validator: validateEmailFormat, trigger: ['blur', 'change'] }],
  emailCheckCode: [{ validator: validateEmailCheckCode, trigger: ['blur', 'change'] }],
}

// 发送注册邮箱验证码
function sendEmailBtn() {
  if (isEmailValid.value) {
    const EmailDto = ref({
      email: formData.value.email,
      type: 'register',
    })
    waitTime.value = 60
    sendEmail(EmailDto.value).then(() => {
      ElMessage.success(`验证码已发送到邮箱：${formData.value.email}，请注意查收`)
      const interval = setInterval(() => {
        if (waitTime.value === 0) {
          clearInterval(interval)
        } else {
          waitTime.value--
        }
      }, 1000)
    })
  } else {
    ElMessage.warning('请输入正确的邮箱')
  }
}

// 注册按钮
function registerBtn() {
  formDataRef.value.validate((valid) => {
    if (valid) {
      // 去除首尾空格
      formData.value.username = formData.value.username.trim()
      formData.value.email = formData.value.email.trim()
      // 去掉repeatPassword字段，后端不需要
      const RegisterDto = { ...formData.value }
      delete RegisterDto.repeatPassword
      register(RegisterDto).then(() => {
        ElMessage.success('注册成功，欢迎进入社区')
        router.push('/login')
      })
    } else {
      ElMessage.warning('请完整填写注册内容')
    }
  })
}
</script>

<style lang="scss" scoped>
// CSS 变量定义
.register-form-wrapper {
  // 浅色模式
  --text-primary: #1e293b;
  --text-secondary: #64748b;
  --text-muted: #94a3b8;
  --input-bg: #f1f5f9;
  --input-border: transparent;
  --input-border-focus: #3b82f6;
  --btn-bg: #1e3a5f;
  --btn-bg-hover: #2c5282;
  --btn-secondary-bg: #f1f5f9;
  --btn-secondary-text: #475569;
  --link-color: #3b82f6;

  width: 100%;
  max-width: 400px;
}

// 黑夜模式
html.dark {
  .register-form-wrapper {
    --text-primary: #f1f5f9;
    --text-secondary: #94a3b8;
    --text-muted: #64748b;
    --input-bg: rgba(30, 41, 59, 0.6);
    --input-border: rgba(148, 163, 184, 0.2);
    --input-border-focus: #60a5fa;
    --btn-bg: #3b82f6;
    --btn-bg-hover: #60a5fa;
    --btn-secondary-bg: rgba(30, 41, 59, 0.6);
    --btn-secondary-text: #cbd5e1;
    --link-color: #60a5fa;
  }
}

// 头部
.register-header {
  margin-bottom: 32px;
}

.register-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.register-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

// 表单
.register-form {
  ::v-deep(.el-form-item) {
    margin-bottom: 0;
  }

  ::v-deep(.el-form-item__error) {
    padding-top: 4px;
  }
}

.form-item-wrap {
  margin-bottom: 16px;
}

.form-label {
  display: block;
  font-size: 12px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 6px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

// 输入框样式 - 复用登录页
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
    height: 42px;
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

.send-code-btn {
  height: 42px;
  padding: 0 16px;
  background: var(--btn-secondary-bg);
  border: 1px solid var(--input-border);
  border-radius: 8px;
  color: var(--btn-secondary-text);
  font-size: 13px;
  font-weight: 500;
  transition: all 0.3s;
  white-space: nowrap;

  &:hover:not(:disabled) {
    background: var(--input-bg);
    border-color: var(--input-border-focus);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

// 注册按钮
.login-btn {
  width: 100%;
  height: 48px;
  background: var(--btn-bg);
  border-color: var(--btn-bg);
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.3s;
  margin-top: 8px;

  &:hover {
    background: var(--btn-bg-hover);
    border-color: var(--btn-bg-hover);
    transform: translateY(-1px);
  }
}

// 登录链接
.login-link-wrap {
  text-align: center;
  margin-top: 24px;
}

.login-text {
  font-size: 14px;
  color: var(--text-secondary);
}

.login-link {
  font-size: 14px;
  font-weight: 600;
  color: var(--link-color);
  margin-left: 4px;

  &:hover {
    color: var(--link-color);
  }
}

// 移动端适配
@media screen and (max-width: 480px) {
  .register-title {
    font-size: 24px;
  }

  .verify-code-wrap {
    flex-direction: column;
    align-items: stretch;
  }

  .send-code-btn {
    width: 100%;
    height: 44px;
  }
}
</style>
