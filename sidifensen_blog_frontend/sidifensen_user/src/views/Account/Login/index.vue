<template>
  <el-form :model="formData" :rules="rules" ref="formDataRef">
    <h2>登录</h2>
    <el-form-item prop="username">
      <el-input clearable placeholder="请输入用户名" maxlength="20" v-model="formData.username">
        <template #prefix>
          <el-icon><User /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input clearable placeholder="请输入密码" maxlength="20" v-model="formData.password" show-password>
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="checkCode">
      <div class="check-code-panel">
        <el-input placeholder="请输入验证码" maxlength="6" v-model="formData.checkCode">
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
    <el-button style="margin-bottom: 20px" type="primary" plain @click="loginBtn">登录</el-button>
    <!-- 分割线 -->
    <el-divider>没有账号</el-divider>
    <el-button type="success" plain @click="router.push('/register')">注册</el-button>
    <!-- 分割线 -->
    <el-divider>其他登录方式</el-divider>
    <!-- 其他的登录方式 -->
    <div class="other-login">
      <el-link underline="never" :href="giteeLogin">
        <svg-icon @click="giteeLogin" name="gitee" width="35px" height="35px" color="#4E86F1" cursor="pointer" />
      </el-link>
      <el-link underline="never" :href="githubLogin">
        <svg-icon name="github" width="36px" height="36px" color="#4E86F1" cursor="pointer" />
      </el-link>
    </div>
  </el-form>
</template>

<script setup>
import { giteeLogin, githubLogin } from "@/api/oauth";
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { login, checkCode, info } from "@/api/user";
import { SetJwt } from "@/utils/Auth";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/userStore.js";

const userStore = useUserStore();

const router = useRouter();
const formDataRef = ref(null);
const formData = ref({
  username: "",
  password: "",
  rememberMe: false,
  checkCodeKey: "",
  checkCode: "",
});

// 验证用户名
const validateUsername = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请输入用户名"));
  } else if (!/^[a-zA-Z0-9]+$/.test(value)) {
    callback(new Error("用户名只能是英文和数字"));
  } else if (value.length < 4 || value.length > 20) {
    callback(new Error("用户名的长度必须在 4-20 个字符之间"));
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

// 验证验证码
const validateCheckCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入验证码"));
  } else {
    callback();
  }
};

const rules = ref({
  username: [{ validator: validateUsername, trigger: ["blur", "change"] }],
  password: [{ validator: validatePasswordCharacters, trigger: ["blur", "change"] }],
  checkCode: [{ validator: validateCheckCode, trigger: ["blur", "change"] }],
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
          SetJwt(res.data.data);
          info().then((res) => {
            userStore.user = res.data.data;
          });
          router.push({ name: "Home" });
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
