<template>
  <div class="login-container">
    <div class="background-animation"></div>
    <div class="login-box">
      <div class="login-title">
        <div class="logo-container">
          <div class="logo-icon"></div>
        </div>
        <h2>管理员登录</h2>
        <p>请输入您的账号和密码</p>
      </div>
      <el-form ref="loginFormRef" :model="loginForm" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <div class="input-container">
            <el-input v-model="loginForm.username" clearable placeholder="请输入用户名" :prefix-icon="User" size="large" />
          </div>
        </el-form-item>
        <el-form-item prop="password">
          <div class="input-container">
            <el-input v-model="loginForm.password" type="password" clearable show-password placeholder="请输入密码" :prefix-icon="Lock" size="large" />
          </div>
        </el-form-item>
        <div class="form-item-remember">
          <el-checkbox v-model="loginForm.rememberMe" class="remember-me">记住密码</el-checkbox>
        </div>
        <el-form-item>
          <el-button type="primary" size="large" class="login-button" @click="handleLogin" :loading="loading">
            <span v-if="!loading">登录</span>
            <span v-else>登录中...</span>
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { ElMessage } from "element-plus";
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
const handleLogin = () => {
  loginFormRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.error("请填写完整信息");
      return;
    } else {
      loading.value = true;
      login(loginForm.value)
        .then((res) => {
          loading.value = false;
          ElMessage.success("登录成功");
          //将jwt存储到localStorage
          SetJwt(res.data.data);
          info().then((res) => {
            userStore.setUser(res.data.data);
            // 加载菜单和动态路由
            userStore.loadMenusAndRoutes().then(() => {
              router.push("/home");
            });
          });
        })
        .catch(() => {
          loading.value = false;
        });
    }
  });
};
</script>

<style lang="scss" scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  position: relative;
  overflow: hidden;

  .background-animation {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(45deg, #42b983, #3aa17e, #2c3e50, #42b983);
    background-size: 400% 400%;
    animation: gradientAnimation 15s ease infinite;
    z-index: 0;
  }
}
@keyframes gradientAnimation {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

.login-box {
  width: 450px; // 增加宽度
  padding: 40px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.15);
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  z-index: 1;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: floatAnimation 6s ease-in-out infinite;

  &:hover {
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
    transform: translateY(-5px);
  }
}

@keyframes floatAnimation {
  0% {
    transform: translateY(0px);
  }
  50% {
    transform: translateY(-10px);
  }
  100% {
    transform: translateY(0px);
  }
}

.login-title {
  text-align: center;
  margin-bottom: 30px;

  .logo-container {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;
  }

  // 小圆环图标
  .logo-icon {
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, #42b983 0%, #3aa17e 100%);
    border-radius: 50%;
    position: relative;
    // display: flex;
    // align-items: center;
    // justify-content: center;
    box-shadow: 0 5px 15px rgba(66, 185, 131, 0.4);

    // 白色圆环
    &::after {
      content: "";
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 40px;
      height: 40px;
      border: 4px solid white;
      border-radius: 50%;
    }
  }

  h2 {
    font-size: 28px;
    font-weight: 700;
    color: #2c3e50;
    margin-bottom: 8px;
    background: linear-gradient(135deg, #2c3e50, #34495e);
    -webkit-background-clip: text;
    background-clip: text;
    color: transparent;
  }

  p {
    font-size: 14px;
    color: #7f8c8d;
  }
}

.login-form {
  width: 100%;
}

.input-container {
  position: relative;
  overflow: hidden;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease;
  border: 1px solid rgba(221, 221, 221, 0.6);
  width: 100%;
  box-sizing: border-box;

  &:focus-within {
    border-color: #42b983;
    box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
    transform: translateY(-2px);
  }

  .el-input {
    height: 50px;
    border: none !important;
    box-shadow: none !important;
    background: transparent;
    width: 100%;
  }
}

// 输入框圆角
:deep(.input-container .el-input__wrapper) {
  border-radius: 8px !important;
}

:deep(.el-input__inner.is-focus) {
  box-shadow: 0 0 0 0 !important;
}

.form-item-remember {
  margin-bottom: 25px;
  .remember-me {
    font-size: 14px;
    color: #7f8c8d;
    transition: color 0.3s ease;
    &:hover {
      color: #42b983;
    }
  }

  // 记住密码复选框选中样式
  :deep(.remember-me .el-checkbox__input.is-checked .el-checkbox__inner) {
    background-color: #42b983 !important;
    border-color: #42b983 !important;
  }
  // 记住密码复选框选中文字样式
  :deep(.remember-me .el-checkbox__input.is-checked + .el-checkbox__label) {
    color: #42b983 !important;
  }
}

.login-button {
  width: 100%;
  height: 50px;
  font-size: 16px;
  font-weight: 600;
  border-radius: 25px;
  background: linear-gradient(135deg, #42b983 0%, #3aa17e 100%);
  border: none;
  color: white;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(66, 185, 131, 0.4);
  animation: pulseAnimation 2s infinite;

  &:hover {
    background: linear-gradient(135deg, #3aa17e 0%, #2d8f6a 100%);
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(66, 185, 131, 0.5);
  }

  &:active {
    transform: translateY(0);
    box-shadow: 0 2px 10px rgba(66, 185, 131, 0.3);
  }

  &::after {
    content: "";
    position: absolute;
    top: 50%;
    left: 50%;
    width: 5px;
    height: 5px;
    background: rgba(255, 255, 255, 0.5);
    border-radius: 50%;
    transform: translate(-50%, -50%);
    animation: rippleAnimation 1.5s infinite;
  }
}

@keyframes pulseAnimation {
  0% {
    box-shadow: 0 4px 15px rgba(66, 185, 131, 0.4);
  }
  50% {
    box-shadow: 0 6px 20px rgba(66, 185, 131, 0.6);
  }
  100% {
    box-shadow: 0 4px 15px rgba(66, 185, 131, 0.4);
  }
}

@keyframes rippleAnimation {
  0% {
    width: 5px;
    height: 5px;
    opacity: 0.5;
  }
  100% {
    width: 300px;
    height: 300px;
    opacity: 0;
  }
}

// 响应式设计 - 手机端适配
@media screen and (max-width: 768px) {
  .login-box {
    width: 90% !important;
    padding: 30px 20px !important;
    margin: 0 15px !important;
  }

  .login-title h2 {
    font-size: 24px !important;
  }

  .login-title .logo-icon {
    width: 60px !important;
    height: 60px !important;
  }

  .login-title .logo-icon::after {
    width: 30px !important;
    height: 30px !important;
    border-width: 3px !important;
  }

  .input-container .el-input {
    height: 45px !important;
  }

  .login-button {
    height: 45px !important;
    font-size: 15px !important;
  }
}
</style>
