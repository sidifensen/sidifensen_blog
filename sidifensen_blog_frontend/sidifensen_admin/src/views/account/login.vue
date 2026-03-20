<template>
  <div class="login-container">
    <!-- 左侧品牌区 -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-logo">
          <svg-icon name="logo" width="48px" height="48px" color="#42b983" />
        </div>
        <h1 class="brand-title">Sidifensen Blog</h1>
        <p class="brand-subtitle">管理后台系统</p>
      </div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-form-wrapper">
      <div class="login-card">
        <h2 class="card-title">管理员登录</h2>

        <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
          <el-form-item prop="username">
            <label class="form-label">用户名</label>
            <el-input v-model="loginForm.username" placeholder="请输入用户名" :prefix-icon="User" size="large" />
          </el-form-item>

          <el-form-item prop="password">
            <label class="form-label">密码</label>
            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" :prefix-icon="Lock" size="large" show-password />
          </el-form-item>

          <div class="form-remember">
            <el-checkbox v-model="loginForm.rememberMe">记住密码</el-checkbox>
          </div>

          <el-form-item>
            <el-button type="primary" size="large" class="login-button" :loading="loading" @click="handleLogin">
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <p class="test-account">测试账号: test / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import { login, info } from "@/api/user";
import { SetJwt } from "@/utils/Auth";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/userStore";
const userStore = useUserStore();

const router = useRouter();
const loginForm = ref({
  username: "",
  password: "",
  rememberMe: false,
});

const loading = ref(false);

const rules = {
  username: [{ required: true, message: "请输入用户名/邮箱", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
};

// 登录
const loginFormRef = ref(null);
const handleLogin = async () => {
  if (loading.value) {
    return;
  }

  try {
    await loginFormRef.value.validate();
  } catch {
    ElMessage.error("请填写完整信息");
    return;
  }

  loading.value = true;

  try {
    const res = await login(loginForm.value);
    // 登录成功后先持久化 token，再并行加载用户信息和菜单数据
    SetJwt(res.data.data);

    const [userInfoRes] = await Promise.all([info(), userStore.loadMenusAndRoutes()]);
    userStore.setUser(userInfoRes.data.data);

    await router.push("/home");
    ElMessage.success("登录成功");
  } finally {
    loading.value = false;
  }
};
</script>

<style lang="scss" scoped>
// ========================================
// 登录页面 - 分屏布局设计
// ========================================

.login-container {
  // CSS 变量定义 - 浅色模式默认值
  --bg-brand: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  --bg-form-wrapper: #f1f5f9;
  --bg-card: #ffffff;
  --text-brand-primary: #f1f5f9;
  --text-brand-secondary: #94a3b8;
  --text-primary: #1e293b;
  --text-regular: #475569;
  --text-muted: #64748b;
  --border: #e2e8f0;
  --input-bg: #f8fafc;
  --input-border: #e2e8f0;
  --input-focus-border: #42b983;
  --btn-bg: #1e293b;
  --btn-hover-bg: #334155;
  --btn-text: #ffffff;
  --shadow-card: 0 4px 12px rgba(0, 0, 0, 0.08);

  display: flex;
  min-height: 100vh;
}

// ========================================
// 左侧品牌区域
// ========================================
.login-brand {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-brand);
  padding: 40px;

  .brand-content {
    text-align: center;
  }

  .brand-logo {
    margin-bottom: 24px;
  }

  .brand-title {
    font-size: 32px;
    font-weight: 600;
    color: var(--text-brand-primary);
    margin: 0 0 12px 0;
  }

  .brand-subtitle {
    font-size: 16px;
    color: var(--text-brand-secondary);
    margin: 0;
  }
}

// ========================================
// 右侧表单区域
// ========================================
.login-form-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--bg-form-wrapper);
  padding: 40px;
}

.login-card {
  width: 100%;
  max-width: 400px;
  background: var(--bg-card);
  border-radius: 12px;
  box-shadow: var(--shadow-card);
  padding: 40px 32px;
}

.card-title {
  font-size: 24px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0 0 32px 0;
  text-align: center;
}

// ========================================
// 表单样式
// ========================================
.login-form {
  .form-label {
    display: block;
    font-size: 14px;
    font-weight: 500;
    color: var(--text-regular);
    margin-bottom: 8px;
  }

  .el-form-item {
    margin-bottom: 24px;
  }

  // 输入框样式覆盖
  :deep(.el-input__wrapper) {
    background: var(--input-bg);
    border: 1px solid var(--input-border);
    border-radius: 8px;
    box-shadow: none;
    padding: 4px 12px;

    &:hover {
      border-color: var(--input-focus-border);
    }

    &.is-focus {
      border-color: var(--input-focus-border);
      box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.15);
    }
  }

  :deep(.el-input__inner) {
    color: var(--text-primary);

    &::placeholder {
      color: var(--text-muted);
    }
  }
}

// ========================================
// 记住密码复选框
// ========================================
.form-remember {
  margin-bottom: 24px;

  :deep(.el-checkbox__label) {
    color: var(--text-regular);
    font-size: 14px;
  }

  :deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: #42b983;
    border-color: #42b983;
  }

  :deep(.el-checkbox__input.is-checked + .el-checkbox__label) {
    color: #42b983;
  }
}

// ========================================
// 登录按钮
// ========================================
.login-button {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: var(--btn-bg);
  border: none;
  color: var(--btn-text);

  &:hover {
    background: var(--btn-hover-bg);
  }

  &:active {
    background: var(--btn-bg);
  }
}

// ========================================
// 测试账号提示
// ========================================
.test-account {
  text-align: center;
  font-size: 13px;
  color: var(--text-muted);
  margin: 24px 0 0 0;
}

// ========================================
// 黑夜模式
// ========================================
html.dark {
  .login-container {
    --bg-form-wrapper: #0f172a;
    --bg-card: #1e293b;
    --text-primary: #f1f5f9;
    --text-regular: #cbd5e1;
    --text-muted: #94a3b8;
    --border: #334155;
    --input-bg: #334155;
    --input-border: #475569;
    --btn-bg: #f1f5f9;
    --btn-hover-bg: #e2e8f0;
    --btn-text: #1e293b;
    --shadow-card: 0 4px 12px rgba(0, 0, 0, 0.25);
  }
}

// ========================================
// 移动端响应式 (< 768px)
// ========================================
@media screen and (max-width: 768px) {
  .login-brand {
    display: none;
  }

  .login-form-wrapper {
    padding: 24px 16px;
  }

  .login-card {
    width: 90%;
    max-width: 400px;
    padding: 32px 24px;
  }

  .card-title {
    font-size: 22px;
    margin-bottom: 28px;
  }
}
</style>