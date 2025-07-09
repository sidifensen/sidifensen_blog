<template>
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
    <el-form-item prop="password_repeat">
      <el-input v-model="formData.password_repeat" maxlength="20" type="password" placeholder="重复密码">
        <template #prefix>
          <el-icon><Lock /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="email">
      <el-input v-model="formData.email" type="email" placeholder="电子邮件地址">
        <template #prefix>
          <el-icon><Message /></el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="code">
      <el-row :gutter="10" style="width: 100%">
        <el-col :span="17">
          <el-input v-model="formData.emailCheckCode" maxlength="6" placeholder="请输入验证码">
            <template #prefix>
              <el-icon><EditPen /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="5">
          <el-button type="success" :disabled="!isEmailValid || coldTime != 0" @click="sendEmailBtn">
            {{ coldTime > 0 ? `请稍后 ${coldTime} 秒` : "获取验证码" }}
          </el-button>
        </el-col>
      </el-row>
    </el-form-item>
    <el-button type="danger" style="margin-top: 30px; width: 90%" plain @click="registerBtn">立即注册</el-button>
    <div style="margin-top: 20px">
      <span style="color: grey">已有账号?</span>
      <el-button type="text" @click="router.push('/login')">立即登录</el-button>
    </div>
  </el-form>
</template>
<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { EditPen, Lock, Message, User } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { register, sendEmail } from "@/api/user";

// 邮件发送冷却时间
const coldTime = ref(0);
const formDataRef = ref();

const router = useRouter();

// 提交表单
const formData = ref({
  username: "",
  password: "",
  password_repeat: "",
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
  password_repeat: [{ validator: validatePassword, trigger: ["blur", "change"] }],
  email: [
    { required: true, message: "请输入邮件地址", trigger: "blur" },
    { type: "email", message: "请输入合法的电子邮件地址", trigger: ["blur", "change"] },
  ],
  emailCheckCode: [{ required: true, message: "请输入获取的验证码", trigger: "blur" }],
};

// 判断邮箱是否正确
const isEmailValid = computed(() => /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test(formData.value.email));

function sendEmailBtn() {
  if (isEmailValid) {
    const EmailDto = ref({
      email: formData.value.email,
      type: "register",
    });
    console.log("提交的数据:", EmailDto.value);
    coldTime.value = 60;
    sendEmail(EmailDto.value).then((res) => {
      if (res.data.code === 200) {
        ElMessage.success(`验证码已发送到邮箱：${formData.value.email}，请注意查收`);
        const intervalId = setInterval(() => {
          if (coldTime.value === 0) {
            clearInterval(intervalId);
          } else {
            coldTime.value--;
          }
        }, 1000);
      } else {
        ElMessage.warning(res.data.msg);
        coldTime.value = 0;
      }
    });
  } else {
    ElMessage.warning("请输入正确的电子邮件");
  }
}

function registerBtn() {
  formDataRef.value.validate((valid) => {
    if (valid) {
      // 去掉password_repeat字段
      const RegisterDto = {...formData.value };
      delete RegisterDto.password_repeat;
      console.log("提交的数据:", RegisterDto);
      register(RegisterDto).then((res) => {
        ElMessage.success("注册成功，欢迎加入我们");
        router.push("/login");
      }).catch((error) => {
        console.log(error);
        // ElMessage.warning(error.response.data.msg);
      });
    } else {
      ElMessage.warning("请完整填写注册表单内容");
    }
  });
}
</script>

<style scoped></style>
