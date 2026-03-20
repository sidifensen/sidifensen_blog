<template>
  <div class="reset-form-wrapper">
    <div class="reset-header">
      <h2 class="reset-title">重置密码</h2>
      <p class="reset-subtitle">验证邮箱后即可重置密码</p>
    </div>

    <!-- 步骤指示器 -->
    <div class="step-indicator">
      <div class="step" :class="{ active: step >= 0, completed: step > 0 }">
        <div class="step-number">1</div>
        <div class="step-label">验证邮箱</div>
      </div>
      <div class="step-line" :class="{ completed: step > 0 }"></div>
      <div class="step" :class="{ active: step >= 1, completed: step > 1 }">
        <div class="step-number">2</div>
        <div class="step-label">重置密码</div>
      </div>
    </div>

    <!-- 步骤1: 验证邮箱 -->
    <el-form
      v-if="step === 0"
      :model="formData"
      :rules="rules"
      ref="formDataRef"
      class="reset-form"
    >
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

      <div class="form-item-wrap">
        <label class="form-label">验证码</label>
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
              {{ waitTime > 0 ? `${waitTime}s` : "获取验证码" }}
            </el-button>
          </div>
        </el-form-item>
      </div>

      <el-button
        class="login-btn"
        type="primary"
        :disabled="!hasRequestedCode"
        @click="verifyResetBtn"
      >
        开始重置密码
      </el-button>
    </el-form>

    <!-- 步骤2: 重置密码 -->
    <el-form
      v-if="step === 1"
      :model="formData"
      :rules="rules"
      ref="formDataRef"
      class="reset-form"
    >
      <div class="form-item-wrap">
        <label class="form-label">新密码</label>
        <el-form-item prop="password">
          <el-input
            v-model="formData.password"
            placeholder="请输入新密码"
            show-password
            class="login-input"
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </div>

      <div class="form-item-wrap">
        <label class="form-label">确认密码</label>
        <el-form-item prop="repeatPassword">
          <el-input
            v-model="formData.repeatPassword"
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

      <el-button class="login-btn" type="primary" @click="resetPasswordBtn">
        重置密码
      </el-button>
    </el-form>

    <!-- 步骤3: 成功 -->
    <div v-if="step === 2" class="success-panel">
      <div class="success-icon">✓</div>
      <div class="success-title">重置密码成功</div>
      <div class="success-text">{{ jumpTime }}秒后跳转到登录页面</div>
    </div>

    <!-- 返回登录 -->
    <div class="back-link-wrap" v-if="step < 2">
      <el-button type="primary" link class="back-link" @click="router.push('/login')">
        ← 返回登录
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { EditPen, Lock, Message } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { sendEmail, verifyResetPassword, resetPassword } from "@/api/user";

const router = useRouter();
const formData = ref({
  email: "",
  emailCheckCode: "",
  password: "",
  repeatPassword: "",
});
const formDataRef = ref(null);

// 发送邮箱验证码倒计时
const waitTime = ref(0);

// 是否已请求过验证码
const hasRequestedCode = ref(false);

// 邮箱正则表达式
const EmailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

// 判断邮箱是否正确
const isEmailValid = computed(() => EmailRegex.test(formData.value.email));

// 验证邮箱格式
const validateEmailFormat = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入邮箱"));
  } else if (!EmailRegex.test(value)) {
    callback(new Error("请输入合法的邮箱"));
  } else {
    callback();
  }
};

// 验证邮箱验证码
const validateEmailCheckCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入获取的验证码"));
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error("验证码必须是6位数字"));
  } else {
    callback();
  }
};

// 验证密码字符类型
const validatePasswordCharacters = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请输入密码"));
  } else if (!/^[a-zA-Z0-9@]+$/.test(value)) {
    callback(new Error("密码只能包含英文、数字和@符号"));
  } else if (value.length < 6 || value.length > 20) {
    callback(new Error("密码的长度必须在 6-20 个字符之间"));
  } else {
    callback();
  }
};

// 验证重复密码
const validatePassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== formData.value.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 表单验证规则
const rules = {
  password: [{ validator: validatePasswordCharacters, trigger: ["blur", "change"] }],
  repeatPassword: [{ validator: validatePassword, trigger: ["blur", "change"] }],
  email: [{ validator: validateEmailFormat, trigger: ["blur", "change"] }],
  emailCheckCode: [{ validator: validateEmailCheckCode, trigger: ["blur", "change"] }],
};

// 发送验证码
function sendEmailBtn() {
  if (isEmailValid.value) {
    const EmailDto = ref({
      email: formData.value.email,
      type: "resetPassword",
    });
    sendEmail(EmailDto.value).then(() => {
      ElMessage.success(`验证码已发送到邮箱：${formData.value.email}，请注意查收`);
      hasRequestedCode.value = true; // 标记已请求验证码
      waitTime.value = 60;
      const interval = setInterval(() => {
        if (waitTime.value === 0) {
          clearInterval(interval);
        } else {
          waitTime.value--;
        }
      }, 1000);
    });
  } else {
    ElMessage.warning("请输入正确的邮箱");
  }
}

// 步骤
const step = ref(0);

// 验证重置密码
const verifyResetBtn = () => {
  if (isEmailValid.value) {
    const VerifyResetDto = ref({
      email: formData.value.email,
      emailCheckCode: formData.value.emailCheckCode,
    });
    verifyResetPassword(VerifyResetDto.value).then(() => {
      step.value++;
    });
  }
};

// 跳转倒计时
const jumpTime = ref(3);

// 重置密码
const resetPasswordBtn = () => {
  formDataRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.error("请填写完整信息");
      return;
    } else {
      resetPassword(formData.value).then(() => {
        step.value++;
        const interval = setInterval(() => {
          jumpTime.value--;
          if (jumpTime.value === 0) {
            clearInterval(interval);
          }
        }, 1000);
        setTimeout(() => {
          step.value = 0; // 重置步骤
          router.push("/login");
        }, 3000);
      });
    }
  });
};
</script>

<style lang="scss" scoped>
// CSS 变量定义
.reset-form-wrapper {
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
  --btn-secondary-hover: #e2e8f0;
  --step-active: #1e3a5f;
  --step-inactive: #cbd5e1;
  --success-color: #10b981;
  --link-color: #3b82f6;

  width: 100%;
  max-width: 400px;
}

// 黑夜模式
html.dark {
  .reset-form-wrapper {
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
    --btn-secondary-hover: rgba(59, 130, 246, 0.1);
    --step-active: #60a5fa;
    --step-inactive: #475569;
    --success-color: #34d399;
    --link-color: #60a5fa;
  }
}

// 头部
.reset-header {
  margin-bottom: 32px;
}

.reset-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 8px;
}

.reset-subtitle {
  font-size: 14px;
  color: var(--text-secondary);
}

// 步骤指示器
.step-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 40px;
  padding: 0 20px;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.step-number {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--step-inactive);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s;
}

.step.active .step-number {
  background: var(--step-active);
}

.step.completed .step-number {
  background: var(--success-color);
}

.step-label {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.step.active .step-label {
  color: var(--text-primary);
}

.step-line {
  flex: 1;
  height: 2px;
  background: var(--step-inactive);
  margin: 0 16px;
  margin-bottom: 24px;
  transition: all 0.3s;
}

.step-line.completed {
  background: var(--success-color);
}

// 表单
.reset-form {
  :deep(.el-form-item) {
    margin-bottom: 0;
  }

  :deep(.el-form-item__error) {
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

// 输入框样式 - 复用登录页样式
.login-input {
  :deep(.el-input__wrapper) {
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

  :deep(.el-input__inner) {
    height: 44px;
    font-size: 15px;
    color: var(--text-primary);

    &::placeholder {
      color: var(--text-muted);
    }
  }

  :deep(.el-input__prefix) {
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
  height: 44px;
  padding: 0 20px;
  background: var(--btn-secondary-bg);
  border: 1px solid var(--input-border);
  border-radius: 8px;
  color: var(--btn-secondary-text);
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;

  &:hover:not(:disabled) {
    background: var(--btn-secondary-hover);
    border-color: var(--input-border-focus);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

// 按钮
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

  &:hover:not(:disabled) {
    background: var(--btn-bg-hover);
    border-color: var(--btn-bg-hover);
    transform: translateY(-1px);
  }

  &:disabled {
    opacity: 0.6;
    cursor: not-allowed;
  }
}

// 成功面板
.success-panel {
  text-align: center;
  padding: 40px 20px;
}

.success-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  background: var(--success-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto 24px;
}

.success-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.success-text {
  font-size: 14px;
  color: var(--text-secondary);
}

// 返回链接
.back-link-wrap {
  text-align: center;
  margin-top: 24px;
}

.back-link {
  font-size: 14px;
  font-weight: 500;
  color: var(--link-color);

  &:hover {
    color: var(--link-color);
  }
}

// 移动端适配
@media screen and (max-width: 480px) {
  .reset-title {
    font-size: 24px;
  }

  .step-indicator {
    padding: 0;
  }

  .step-label {
    font-size: 11px;
  }

  .verify-code-wrap {
    flex-direction: column;
    align-items: stretch;
  }

  .send-code-btn {
    width: 100%;
  }
}
</style>
