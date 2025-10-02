<template>
  <div>
    <h2>重置密码</h2>
    <el-steps style="margin: 0 auto; margin-bottom: 10px" align-center :active="step" finish-status="success">
      <el-step title="验证邮箱" />
      <el-step title="重置密码" />
    </el-steps>
    <div class="reset">
      <el-form :model="formData" :rules="rules" ref="formDataRef" v-if="step === 0">
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
            <el-button class="checkCode" type="success" :disabled="!isEmailValid || waitTime > 0" @click="sendEmailBtn">
              {{ waitTime > 0 ? `请稍后 ${waitTime} 秒` : "获取验证码" }}
            </el-button>
          </div>
        </el-form-item>
        <el-button type="primary" plain :disabled="!hasRequestedCode" @click="verifyResetBtn">开始重置密码</el-button>
      </el-form>
      <el-form :model="formData" :rules="rules" ref="formDataRef" v-if="step === 1">
        <el-form-item prop="password">
          <el-input v-model="formData.password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="formData.repeatPassword" placeholder="确认密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-button type="primary" plain @click="resetPasswordBtn">重置密码</el-button>
      </el-form>
      <div style="width: 380px; margin-bottom: 232px; font-size: 22px; color: #409eff" v-if="step == 2">重置密码成功,{{ jumpTime }}秒后跳转到登录页面</div>
    </div>
    <el-button class="login" type="info" @click="router.push('/login')">返回登录</el-button>
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
.el-form {
  margin-bottom: 127px;
}
.el-steps {
  width: 400px;
}
.reset {
  display: flex;
  justify-content: center;
  align-items: center;
  flex-wrap: wrap;
  .login {
    margin-top: 20px;
  }
}
.check-code-panel {
  display: flex;
  width: 100%;
  .checkCode {
    margin-left: 10px;
  }
}
</style>
