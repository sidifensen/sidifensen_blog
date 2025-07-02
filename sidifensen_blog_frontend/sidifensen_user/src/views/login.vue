<template>
  <div class="login_container">
    <div class="login_form">
      <h2>斯蒂芬森的博客</h2>
      <el-form :model="formData" :rules="rules" ref="formDataRef">
        <el-form-item prop="username">
          <el-input clearable placeholder="请输入用户名" v-model.trim="formData.username">
            <template #prefix>
              <el-icon class="el-input__icon"><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input clearable placeholder="请输入密码" v-model.trim="formData.password" show-password>
            <template #prefix>
              <el-icon class="el-input__icon"><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <div class="rememberMe">
          <el-checkbox v-model="formData.rememberMe">记住密码</el-checkbox>
          <el-button clss="forgetPassword" type="text">忘记密码</el-button>
        </div>
        <el-form-item class="submit-btn">
          <el-button style="width: 100%" type="primary" @click="handleLogin">登录</el-button>
        </el-form-item>
        <!-- 分割线 -->
        <el-divider>没有账号</el-divider>
        <el-button style="width: 100%" type="success" @click="handleRegister">注册</el-button>
        <!-- 分割线 -->
        <el-divider>其他登录方式</el-divider>
        <!-- 其他的登录方式 -->
        <div class="other_login">
          <svg-icon name="gitee" width="35px" height="35px" color="#4E86F1"/>
          <svg-icon name="github" width="35px" height="35px" color="#4E86F1"/>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { login } from "@/api/user";
import { jwtDecode } from "jwt-decode";
import { SET_TOKEN } from "@/utils/Auth";

const router = useRouter();

const formData = ref({
  username: "",
  password: "",
  rememberMe: false,
});

const rules = ref({
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
});

const handleLogin = () => {
  console.log(formData.value);
  login(formData.value).then(res => {
    const jwt = jwtDecode(res.data.data);
    console.log(jwt);
    SET_TOKEN(jwt, formData.value.rememberMe);
    router.push("/");
  })
};
</script>

<style lang="scss" scoped>
.login_container {
  background: url("../assets/img/bg.jpg") no-repeat center center / cover;
  height: 100vh;
  display: flex;
  justify-content: flex-end;
  .login_form {
    width: 400px;
    padding: 10px;
    background-color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    .rememberMe {
      display: flex;
      justify-content: space-between;
    }
  }
  @media (max-width: 600px) {
    .login_form {
      width: 100%;
      height: 100vh;
      padding: 10px;
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
    }
  }
  .other_login {
    display: flex;
    justify-content: space-evenly;
    align-items: center;
  }
}
.el-form {
  width: 80%;
}
</style>
