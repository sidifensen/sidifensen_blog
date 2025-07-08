<template>
  <div class="login_container">
    <div class="left_panel"></div>
    <div class="welcome_title">
      <div>欢迎来到斯蒂芬森的博客</div>
      <div>在这里，你可以找到我喜欢的文章，分享自己的想法，与志同道合的朋友交流。</div>
    </div>
    <div class="right_panel">
      <h2>登录</h2>
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
        <el-form-item prop="checkCode">
          <div class="check-code-panel">
            <el-input placeholder="请输入验证码" v-model.trim="formData.checkCode"> </el-input>
            <img class="check-code" :src="checkCodeInfo.checkCodeBase64" alt="" @click="changeCheckCode()" />
          </div>
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
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { login, checkCode } from "@/api/user";
import { SET_TOKEN } from "@/utils/Auth";
import { ElMessage } from "element-plus";
const router = useRouter();

const formDataRef = ref(null);

const formData = ref({
  username: "",
  password: "",
  rememberMe: false,
  checkCodeKey: "",
  checkCode: "",
});

const rules = ref({
  username: [{ required: true, message: "请输入用户名/邮箱", trigger: "blur" }],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }],
  checkCode: [{ required: true, message: "请输入验证码", trigger: "blur" }],
});

const handleLogin = () => {
  formDataRef.value.validate((valid) => {
    if (!valid) {
      ElMessage.error("请填写完整信息");
      return;
    } else {
      login(formData.value)
        .then((res) => {
          ElMessage.success("登录成功");
          //将jwt存储到localStorage
          SET_TOKEN(res.data.data);
          router.push("/");
        })
        .catch(() => {
          //刷新验证码
          changeCheckCode();
        });
    }
  });
};

const checkCodeInfo = ref({});
const changeCheckCode = async () => {
  await checkCode().then((res) => {
    checkCodeInfo.value = res.data.data;
    formData.value.checkCodeKey = res.data.data.checkCodeKey;
  });
};
onMounted(() => {
  changeCheckCode();
});
</script>

<style lang="scss" scoped>
.login_container {
  display: flex;
  justify-content: flex-end;
  height: 100vh;
  .left_panel {
    flex: 1;
    background: url("@/assets/img/bg.jpg") no-repeat center center / cover;
  }
  .welcome_title {
    position: absolute;
    left: 20px;
    bottom: 20px;
    color: white;
    text-shadow: 0 0 10px white;
    div:first-child {
      font-size: 20px;
      font-weight: bold;
      margin-top: 10px;
    }
  }
  .right_panel {
    z-index: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    width: 400px;
    padding: 10px;
    background-color: #fff;
    .check-code-panel {
      display: flex;
      width: 100%;
      img {
        cursor: pointer;
      }
    }
    .rememberMe {
      display: flex;
      justify-content: space-between;
      margin-bottom: 10px;
    }
  }
  @media screen and (max-width: 600px) {
    .left_panel {
      display: none;
    }
    .right_panel {
      width: 100vw;
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
