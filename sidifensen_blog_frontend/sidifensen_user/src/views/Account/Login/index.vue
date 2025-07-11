<template>
  <el-form :model="formData" :rules="rules" ref="formDataRef">
    <h2>登录</h2>
    <el-form-item prop="username">
      <el-input clearable placeholder="请输入用户名" v-model="formData.username">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input clearable placeholder="请输入密码" v-model="formData.password" show-password>
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="checkCode">
      <div class="check-code-panel">
        <el-input placeholder="请输入验证码" v-model="formData.checkCode">
          <template #prefix>
            <el-icon><EditPen /></el-icon>
          </template>
        </el-input>
        <img class="check-code" :src="checkCodeInfo.checkCodeBase64" alt="验证码" @click="changeCheckCode()" />
      </div>
    </el-form-item>
    <div class="rememberMe">
      <el-checkbox v-model="formData.rememberMe">记住密码</el-checkbox>
      <el-button class="forgetPassword" type="primary" link @click="router.push('/reset')">忘记密码</el-button>
    </div>
    <el-button style="margin-bottom: 20px;" type="primary" plain @click="loginBtn">登录</el-button>
    <!-- 分割线 -->
    <el-divider>没有账号</el-divider>
    <el-button type="success" plain @click="router.push('/register')">注册</el-button>
    <!-- 分割线 -->
    <el-divider>其他登录方式</el-divider>
    <!-- 其他的登录方式 -->
    <div class="other-login">
      <svg-icon name="gitee" width="35px" height="35px" color="#4E86F1" cursor="pointer" />
      <svg-icon name="github" width="35px" height="35px" color="#4E86F1" cursor="pointer" />
    </div>
  </el-form>
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

// 登录按钮
const loginBtn = () => {
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
// 刷新验证码
const changeCheckCode = async () => {
  await checkCode().then((res) => {
    checkCodeInfo.value = res.data.data;
    formData.value.checkCodeKey = res.data.data.checkCodeKey;
  });
};

// 页面加载完成后刷新验证码
onMounted(() => {
  changeCheckCode();
});
</script>

<style lang="scss" scoped>
.check-code-panel {
  display: flex;
  width: 100%;
  .check-code {
    width: 100px;
    height: 32px;
    margin-left: 10px;
    cursor: pointer;
    border: 2px solid #dcdfe6;
    border-radius: 4px;
  }
}
.rememberMe {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.other-login {
  display: flex;
  justify-content: space-evenly;
  align-items: center;
  svg-icon {
    cursor: pointer;
  }
}
</style>
