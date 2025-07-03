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
          <svg-icon name="gitee" width="35px" height="35px" color="#4E86F1" />
          <svg-icon name="github" width="35px" height="35px" color="#4E86F1" />
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { login } from "@/api/user";
import { SET_TOKEN } from "@/utils/Auth";
import { ElMessage } from "element-plus";
const router = useRouter();

const formDataRef = ref(null);

const formData = ref({
  username: "",
  password: "",
  rememberMe: false,
});

const rules = ref({
  username: [{ required: true, message: "请输入用户名/邮箱", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
});

const handleLogin = () => {
  formDataRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.error("请输入用户名和密码");
      return;
    }else{
      login(formData.value).then((res) => {
        ElMessage.success("登录成功");
        SET_TOKEN(res.data.data);
        router.push("/");
      });
    }
  });
};
</script>

<style lang="scss" scoped>
.login_container {
  display: flex;
  justify-content: flex-end;
  height: 100vh;
  background: url("../assets/img/bg.jpg") no-repeat center center / cover;
  .login_form {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    width: 400px;
    padding: 10px;
    background-color: #fff;
    .rememberMe {
      display: flex;
      justify-content: space-between;
    }
  }
  @media (max-width: 600px) {
    .login_form {
      display: flex;
      justify-content: center;
      align-items: center;
      flex-direction: column;
      width: 100%;
      height: 100vh;
      padding: 10px;
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
