<template>
  <div>
    <el-form :model="formData" :rules="rules" ref="formDataRef">
      <h2>注册</h2>
      <el-form-item prop="username">
        <el-input v-model="formData.username" maxlength="10" type="text" placeholder="用户名">
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input v-model="formData.password" maxlength="20" type="password" placeholder="密码">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="repeatPassword">
        <el-input v-model="formData.repeatPassword" maxlength="20" type="password" placeholder="重复密码">
          <template #prefix>
            <el-icon><Lock /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="formData.email" type="email" placeholder="邮箱">
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="checkCode">
        <div class="check-code-panel">
          <el-input v-model="formData.emailCheckCode" maxlength="6" placeholder="请输入验证码">
            <template #prefix>
              <el-icon><EditPen /></el-icon>
            </template>
          </el-input>
          <el-button class="checkCode" type="success" :disabled="!isEmailValid || waitTime != 0" @click="sendEmailBtn">
            {{ waitTime > 0 ? `请稍后 ${waitTime} 秒` : "获取验证码" }}
          </el-button>
        </div>
      </el-form-item>
    </el-form>
    <el-button type="danger" style="" plain @click="registerBtn">立即注册</el-button>
    <div style="margin-top: 20px; display: flex; align-items: center; justify-content: center">
      <span style="color: grey">已有账号?</span>
      <el-button style="" type="primary" link @click="router.push('/login')">立即登录</el-button>
    </div>
  </div>
</template>
<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { EditPen, Lock, Message, User } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { register, sendEmail } from "@/api/user";

// 邮件发送等待时间
const waitTime = ref(0);
const formDataRef = ref();
const router = useRouter();
// 提交表单
const formData = ref({
  username: "",
  password: "",
  repeatPassword: "",
  email: "",
  checkCode: "",
});

// 验证用户名
const validateUsername = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请输入用户名"));
  } else if (!/[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error("用户名不能包含特殊字符，只能是中/英文"));
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

const rules = {
  username: [{ validator: validateUsername, trigger: ["blur", "change"] }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, max: 20, message: "密码的长度必须在 6-20 个字符之间", trigger: ["blur", "change"] },
  ],
  repeatPassword: [{ validator: validatePassword, trigger: ["blur", "change"] }],
  email: [
    { required: true, message: "请输入邮件地址", trigger: "blur" },
    { type: "email", message: "请输入合法的邮箱", trigger: ["blur", "change"] },
  ],
  emailCheckCode: [{ required: true, message: "请输入获取的验证码", trigger: "blur" }],
};

// 判断邮箱是否正确
const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.value.email));

// 发送注册邮箱验证码
function sendEmailBtn() {
  if (isEmailValid) {
    const EmailDto = ref({
      email: formData.value.email,
      type: "register",
    });
    waitTime.value = 60;
    sendEmail(EmailDto.value).then(() => {
      ElMessage.success(`验证码已发送到邮箱：${formData.value.email}，请注意查收`);
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

// 注册按钮
function registerBtn() {
  formDataRef.value.validate((valid) => {
    if (valid) {
      // 去掉password_repeat字段
      const RegisterDto = { ...formData.value };
      delete RegisterDto.password_repeat;
      register(RegisterDto).then(() => {
        ElMessage.success("注册成功，欢迎进入博客");
        router.push("/login");
      });
    } else {
      ElMessage.warning("请完整填写注册内容");
    }
  });
}
</script>

<style lang="scss" scoped>
.check-code-panel {
  display: flex;
  width: 100%;
  .checkCode {
    margin-left: 10px;
    width: 110px;
  }
}
</style>
