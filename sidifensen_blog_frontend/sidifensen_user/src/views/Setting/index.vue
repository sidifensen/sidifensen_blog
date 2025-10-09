<template>
  <div class="setting-container">
    <div class="setting-content">
      <!-- 页面标题 -->
      <div class="setting-header">
        <h2>个人设置</h2>
        <p class="header-desc">管理您的个人信息和账户设置</p>
      </div>

      <!-- 设置表单 -->
      <div class="setting-form-container">
        <el-skeleton :loading="userLoading" animated>
          <template #template>
            <div class="skeleton-container">
              <el-skeleton-item variant="circle" style="width: 120px; height: 120px; margin: 0 auto 24px" />
              <el-skeleton-item variant="text" style="width: 60%; margin: 0 auto 16px" />
              <el-skeleton-item variant="text" style="width: 80%; margin: 0 auto 16px" />
              <el-skeleton-item variant="text" style="width: 70%; margin: 0 auto 16px" />
            </div>
          </template>
          <template #default>
            <div v-if="userInfo" class="user-info-container">
              <!-- 头像上传 -->
              <div class="info-item avatar-item">
                <div class="item-label">头像</div>
                <div class="item-content">
                  <div class="avatar-upload-container">
                    <el-upload class="avatar-uploader" :action="''" :http-request="handleAvatarUpload" :show-file-list="false" :before-upload="beforeAvatarUpload" accept="image/*">
                      <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar-preview" />
                      <div v-else class="avatar-placeholder">
                        <el-icon class="avatar-uploader-icon">
                          <Plus />
                        </el-icon>
                        <div class="upload-text">点击上传头像</div>
                      </div>
                    </el-upload>
                    <div class="avatar-tips">
                      <p>支持 jpg, jpeg, png, webp 格式</p>
                      <p>建议尺寸：200x200 像素，大小不超过 1MB</p>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 昵称 -->
              <div class="info-item">
                <div class="item-label">昵称</div>
                <div class="item-content">
                  <!-- 显示模式 -->
                  <div v-if="!editingField.nickname" class="display-mode">
                    <span class="display-value">{{ userInfo.nickname || "未设置" }}</span>
                    <el-button type="primary" text @click="startEdit('nickname')">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                  </div>
                  <!-- 编辑模式 -->
                  <div v-else class="edit-mode">
                    <el-input v-model="editingData.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit clearable>
                      <template #prefix>
                        <el-icon>
                          <User />
                        </el-icon>
                      </template>
                    </el-input>
                    <div class="edit-actions">
                      <el-button type="primary" size="small" :loading="saveLoading.nickname" @click="saveField('nickname')">保存</el-button>
                      <el-button size="small" @click="cancelEdit('nickname')">取消</el-button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 邮箱 -->
              <div class="info-item">
                <div class="item-label">邮箱</div>
                <div class="item-content">
                  <div class="display-mode">
                    <span class="display-value">{{ userInfo.email || "未设置" }}</span>
                    <el-button type="primary" text @click="openEmailDialog">
                      <el-icon><Edit /></el-icon>
                      修改邮箱
                    </el-button>
                  </div>
                </div>
              </div>

              <!-- 性别 -->
              <div class="info-item">
                <div class="item-label">性别</div>
                <div class="item-content">
                  <!-- 显示模式 -->
                  <div v-if="!editingField.sex" class="display-mode">
                    <span class="display-value">{{ userInfo.sex === 0 ? "男" : userInfo.sex === 1 ? "女" : "未设置" }}</span>
                    <el-button type="primary" text @click="startEdit('sex')">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                  </div>
                  <!-- 编辑模式 -->
                  <div v-else class="edit-mode">
                    <el-radio-group v-model="editingData.sex">
                      <el-radio :label="0">男</el-radio>
                      <el-radio :label="1">女</el-radio>
                    </el-radio-group>
                    <div class="edit-actions">
                      <el-button type="primary" size="small" :loading="saveLoading.sex" @click="saveField('sex')">保存</el-button>
                      <el-button size="small" @click="cancelEdit('sex')">取消</el-button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 个人简介 -->
              <div class="info-item">
                <div class="item-label">个人简介</div>
                <div class="item-content">
                  <!-- 显示模式 -->
                  <div v-if="!editingField.introduction" class="display-mode">
                    <span class="display-value">{{ userInfo.introduction || "这个人很懒，什么都没写~" }}</span>
                    <el-button type="primary" text @click="startEdit('introduction')">
                      <el-icon><Edit /></el-icon>
                      编辑
                    </el-button>
                  </div>
                  <!-- 编辑模式 -->
                  <div v-else class="edit-mode">
                    <el-input v-model="editingData.introduction" type="textarea" placeholder="请输入个人简介" :autosize="{ minRows: 4, maxRows: 8 }" maxlength="200" show-word-limit resize="none" />
                    <div class="edit-actions">
                      <el-button type="primary" size="small" :loading="saveLoading.introduction" @click="saveField('introduction')">保存</el-button>
                      <el-button size="small" @click="cancelEdit('introduction')">取消</el-button>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 修改密码 -->
              <div class="info-item">
                <div class="item-label">密码</div>
                <div class="item-content">
                  <div class="display-mode">
                    <span class="display-value">••••••••</span>
                    <el-button type="primary" text @click="openPasswordDialog">
                      <el-icon><Edit /></el-icon>
                      修改密码
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>
    </div>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" :close-on-click-modal="false" @close="resetPasswordDialog">
      <!-- 步骤条 -->
      <el-steps :active="passwordStep" finish-status="success" align-center style="margin-bottom: 30px">
        <el-step title="验证邮箱" />
        <el-step title="修改密码" />
      </el-steps>

      <!-- 第一步：验证邮箱 -->
      <el-form v-if="passwordStep === 0" ref="emailFormRef" :model="passwordForm" :rules="passwordRules" label-width="0">
        <el-form-item prop="email">
          <el-input v-model="passwordForm.email" type="email" placeholder="邮箱" :disabled="true">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="emailCheckCode">
          <div class="check-code-panel">
            <el-input v-model="passwordForm.emailCheckCode" maxlength="6" placeholder="请输入验证码">
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
            <el-button type="success" :disabled="waitTime > 0" @click="sendEmailCode" style="margin-left: 10px">
              {{ waitTime > 0 ? `请稍后 ${waitTime} 秒` : "获取验证码" }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <!-- 第二步：修改密码 -->
      <el-form v-if="passwordStep === 1" ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="0">
        <el-form-item prop="password">
          <el-input v-model="passwordForm.password" placeholder="新密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="repeatPassword">
          <el-input v-model="passwordForm.repeatPassword" placeholder="确认新密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <!-- 对话框底部按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button v-if="passwordStep === 0" type="primary" :loading="verifyLoading" :disabled="!hasRequestedPasswordCode" @click="verifyEmail">下一步</el-button>
          <el-button v-if="passwordStep === 1" type="primary" :loading="resetLoading" @click="resetPasswordSubmit">确认修改</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改邮箱对话框 -->
    <el-dialog v-model="emailDialogVisible" title="修改邮箱" :close-on-click-modal="false" @close="resetEmailDialog">
      <!-- 步骤条 -->
      <el-steps :active="emailStep" finish-status="success" align-center style="margin-bottom: 30px">
        <el-step title="验证原邮箱" />
        <el-step title="输入新邮箱" />
      </el-steps>

      <!-- 第一步：验证原邮箱 -->
      <el-form v-if="emailStep === 0" ref="oldEmailFormRef" :model="emailForm" :rules="oldEmailRules" label-width="0">
        <el-form-item prop="email">
          <el-input v-model="emailForm.email" type="email" placeholder="原邮箱" :disabled="true">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="emailCheckCode">
          <div class="check-code-panel">
            <el-input v-model="emailForm.emailCheckCode" maxlength="6" placeholder="请输入验证码">
              <template #prefix>
                <el-icon><EditPen /></el-icon>
              </template>
            </el-input>
            <el-button type="success" :disabled="emailWaitTime > 0" @click="sendOldEmailCode" style="margin-left: 10px">
              {{ emailWaitTime > 0 ? `请稍后 ${emailWaitTime} 秒` : "获取验证码" }}
            </el-button>
          </div>
        </el-form-item>
      </el-form>

      <!-- 第二步：输入新邮箱 -->
      <el-form v-if="emailStep === 1" ref="newEmailFormRef" :model="emailForm" :rules="newEmailRules" label-width="0">
        <el-form-item prop="newEmail">
          <el-input v-model="emailForm.newEmail" type="email" placeholder="请输入新邮箱">
            <template #prefix>
              <el-icon><Message /></el-icon>
            </template>
          </el-input>
        </el-form-item>
      </el-form>

      <!-- 对话框底部按钮 -->
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="emailDialogVisible = false">取消</el-button>
          <el-button v-if="emailStep === 0" type="primary" :loading="emailVerifyLoading" :disabled="!hasRequestedEmailCode" @click="verifyOldEmail">下一步</el-button>
          <el-button v-if="emailStep === 1" type="primary" :loading="emailUpdateLoading" @click="updateEmailSubmit">确认修改</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { Plus, User, Message, Edit, EditPen, Lock } from "@element-plus/icons-vue";
import { info, updateUserInfo, sendEmail, verifyResetPassword, resetPassword, updateEmail, verifyResetEmail } from "@/api/user";
import { uploadAvatar } from "@/api/photo";
import { validateAvatarImageFile, compressImage } from "@/utils/PhotoUtils";
import { useUserStore } from "@/stores/userStore";

// Store
const userStore = useUserStore();

// 加载状态
const userLoading = ref(false);

// 用户信息数据
const userInfo = ref(null);

// 编辑状态管理（每个字段独立控制）
const editingField = reactive({
  nickname: false,
  sex: false,
  introduction: false,
});

// 编辑中的数据
const editingData = reactive({
  nickname: "",
  sex: 0,
  introduction: "",
});

// 保存加载状态（每个字段独立）
const saveLoading = reactive({
  nickname: false,
  sex: false,
  introduction: false,
});

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    userLoading.value = true;
    const res = await info();
    userInfo.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取用户信息失败");
    console.error("获取用户信息失败:", error);
  } finally {
    userLoading.value = false;
  }
};

// 开始编辑某个字段
const startEdit = (field) => {
  // 将当前值复制到编辑数据中
  editingData[field] = userInfo.value[field] ?? "";
  // 设置编辑状态
  editingField[field] = true;
};

// 取消编辑某个字段
const cancelEdit = (field) => {
  // 重置编辑状态
  editingField[field] = false;
  // 清空编辑数据
  editingData[field] = "";
};

// 保存某个字段
const saveField = async (field) => {
  // 字段验证
  if (field === "nickname") {
    if (!editingData.nickname || editingData.nickname.trim() === "") {
      ElMessage.warning("昵称不能为空");
      return;
    }
    if (editingData.nickname.length < 4 || editingData.nickname.length > 20) {
      ElMessage.warning("昵称长度在 4 到 20 个字符");
      return;
    }
  }

  if (field === "introduction") {
    if (editingData.introduction && editingData.introduction.length > 200) {
      ElMessage.warning("简介长度不能超过 200 个字符");
      return;
    }
  }

  try {
    saveLoading[field] = true;

    // 构建更新数据
    const updateData = {
      nickname: userInfo.value.nickname,
      sex: userInfo.value.sex,
      introduction: userInfo.value.introduction,
      avatar: userInfo.value.avatar,
      [field]: editingData[field],
    };

    // 调用更新接口
    await updateUserInfo(updateData);

    // 更新本地用户信息
    userInfo.value[field] = editingData[field];

    // 更新 store 中的用户信息
    const res = await info();
    userStore.user = res.data.data;

    // 取消编辑状态
    editingField[field] = false;

    ElMessage.success("修改成功");
  } catch (error) {
    ElMessage.error("修改失败");
    console.error("修改失败:", error);
  } finally {
    saveLoading[field] = false;
  }
};

// 头像上传前的校验
const beforeAvatarUpload = (file) => {
  const validation = validateAvatarImageFile(file);
  if (!validation) {
    return false;
  }
  return true;
};

// 处理头像上传
const handleAvatarUpload = async (options) => {
  const { file } = options;
  try {
    // 压缩图片
    const compressedFile = await compressImage(file, 0.8, 800, 800);

    // 上传到服务器（使用专门的头像上传接口）
    ElMessage.info("头像上传中...");
    await uploadAvatar(compressedFile);

    // 调用成功回调
    options.onSuccess && options.onSuccess();
    ElMessage.success("头像上传成功，正在审核中，审核通过后将自动更新");
  } catch (error) {
    console.error("头像上传失败:", error);
    ElMessage.error("头像上传失败，请重试");
    options.onError && options.onError();
  }
};

// ==================== 修改密码相关 ====================
// 修改密码对话框显示状态
const passwordDialogVisible = ref(false);

// 修改密码步骤（0: 验证邮箱, 1: 修改密码）
const passwordStep = ref(0);

// 发送邮箱验证码倒计时
const waitTime = ref(0);

// 标记是否在当前会话中请求过验证码
const hasRequestedPasswordCode = ref(false);

// 验证和重置加载状态
const verifyLoading = ref(false);
const resetLoading = ref(false);

// 修改密码表单数据
const passwordForm = reactive({
  email: "",
  emailCheckCode: "",
  password: "",
  repeatPassword: "",
});

// 表单引用
const emailFormRef = ref(null);
const passwordFormRef = ref(null);

// 邮箱正则表达式
const EmailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

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
const validateRepeatPassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== passwordForm.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

// 修改密码表单验证规则
const passwordRules = {
  email: [{ validator: validateEmailFormat, trigger: ["blur", "change"] }],
  emailCheckCode: [{ validator: validateEmailCheckCode, trigger: ["blur", "change"] }],
  password: [{ validator: validatePasswordCharacters, trigger: ["blur", "change"] }],
  repeatPassword: [{ validator: validateRepeatPassword, trigger: ["blur", "change"] }],
};

// 打开修改密码对话框
const openPasswordDialog = () => {
  passwordDialogVisible.value = true;
  passwordStep.value = 0;
  passwordForm.email = userInfo.value.email;
  passwordForm.emailCheckCode = "";
  passwordForm.password = "";
  passwordForm.repeatPassword = "";
  waitTime.value = 0;
  hasRequestedPasswordCode.value = false; // 重置验证码请求标志
};

// 重置密码对话框
const resetPasswordDialog = () => {
  passwordStep.value = 0;
  passwordForm.email = "";
  passwordForm.emailCheckCode = "";
  passwordForm.password = "";
  passwordForm.repeatPassword = "";
  waitTime.value = 0;
  hasRequestedPasswordCode.value = false; // 重置验证码请求标志
};

// 发送邮箱验证码
const sendEmailCode = async () => {
  try {
    const emailDto = {
      email: passwordForm.email,
      type: "resetPassword",
    };
    await sendEmail(emailDto);
    ElMessage.success(`验证码已发送到邮箱：${passwordForm.email}，请注意查收`);
    hasRequestedPasswordCode.value = true; // 标记已请求验证码
    waitTime.value = 60;
    const interval = setInterval(() => {
      if (waitTime.value === 0) {
        clearInterval(interval);
      } else {
        waitTime.value--;
      }
    }, 1000);
  } catch (error) {
    ElMessage.error("发送验证码失败");
    console.error("发送验证码失败:", error);
  }
};

// 验证邮箱
const verifyEmail = async () => {
  if (!emailFormRef.value) return;

  try {
    await emailFormRef.value.validate();
    verifyLoading.value = true;

    const verifyResetDto = {
      email: passwordForm.email,
      emailCheckCode: passwordForm.emailCheckCode,
    };

    await verifyResetPassword(verifyResetDto);
    passwordStep.value = 1;
    ElMessage.success("邮箱验证成功");
  } catch (error) {
    if (error !== false) {
      // false 是表单验证失败，不需要提示
      ElMessage.error("验证失败，请检查验证码是否正确");
      console.error("邮箱验证失败:", error);
    }
  } finally {
    verifyLoading.value = false;
  }
};

// 提交修改密码
const resetPasswordSubmit = async () => {
  if (!passwordFormRef.value) return;

  try {
    await passwordFormRef.value.validate();
    resetLoading.value = true;

    const resetData = {
      email: passwordForm.email,
      emailCheckCode: passwordForm.emailCheckCode,
      password: passwordForm.password,
    };

    await resetPassword(resetData);
    ElMessage.success("密码修改成功");

    // 关闭对话框
    passwordDialogVisible.value = false;
    resetPasswordDialog();
  } catch (error) {
    if (error !== false) {
      ElMessage.error("密码修改失败，请检查验证码是否正确");
      console.error("密码修改失败:", error);
    }
  } finally {
    resetLoading.value = false;
  }
};

// ==================== 修改邮箱相关 ====================
// 修改邮箱对话框显示状态
const emailDialogVisible = ref(false);

// 修改邮箱步骤（0: 验证原邮箱, 1: 输入新邮箱）
const emailStep = ref(0);

// 发送邮箱验证码倒计时
const emailWaitTime = ref(0);

// 标记是否在当前会话中请求过验证码
const hasRequestedEmailCode = ref(false);

// 验证原邮箱加载状态
const emailVerifyLoading = ref(false);

// 邮箱更新加载状态
const emailUpdateLoading = ref(false);

// 修改邮箱表单数据
const emailForm = reactive({
  email: "",
  newEmail: "",
  emailCheckCode: "",
});

// 原邮箱表单引用
const oldEmailFormRef = ref(null);

// 新邮箱表单引用
const newEmailFormRef = ref(null);

// 验证新邮箱格式
const validateNewEmailFormat = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入新邮箱"));
  } else if (!EmailRegex.test(value)) {
    callback(new Error("请输入合法的邮箱"));
  } else {
    callback();
  }
};

// 原邮箱表单验证规则
const oldEmailRules = {
  email: [{ required: true, message: "原邮箱不能为空", trigger: "blur" }],
  emailCheckCode: [{ validator: validateEmailCheckCode, trigger: ["blur", "change"] }],
};

// 新邮箱表单验证规则
const newEmailRules = {
  newEmail: [{ validator: validateNewEmailFormat, trigger: ["blur", "change"] }],
};

// 打开修改邮箱对话框
const openEmailDialog = () => {
  emailDialogVisible.value = true;
  emailStep.value = 0;
  emailForm.email = userInfo.value.email;
  emailForm.newEmail = "";
  emailForm.emailCheckCode = "";
  emailWaitTime.value = 0;
  hasRequestedEmailCode.value = false; // 重置验证码请求标志
};

// 重置邮箱对话框
const resetEmailDialog = () => {
  emailStep.value = 0;
  emailForm.email = "";
  emailForm.newEmail = "";
  emailForm.emailCheckCode = "";
  emailWaitTime.value = 0;
  hasRequestedEmailCode.value = false; // 重置验证码请求标志
};

// 发送原邮箱验证码
const sendOldEmailCode = async () => {
  try {
    const emailDto = {
      email: emailForm.email,
      type: "resetEmail",
    };
    await sendEmail(emailDto);
    ElMessage.success(`验证码已发送到原邮箱：${emailForm.email}，请注意查收`);
    hasRequestedEmailCode.value = true; // 标记已请求验证码
    emailWaitTime.value = 60;
    const interval = setInterval(() => {
      if (emailWaitTime.value === 0) {
        clearInterval(interval);
      } else {
        emailWaitTime.value--;
      }
    }, 1000);
  } catch (error) {
    ElMessage.error("发送验证码失败");
    console.error("发送验证码失败:", error);
  }
};

// 验证原邮箱
const verifyOldEmail = async () => {
  if (!oldEmailFormRef.value) return;

  try {
    await oldEmailFormRef.value.validate();
    emailVerifyLoading.value = true;

    const verifyData = {
      email: emailForm.email,
      emailCheckCode: emailForm.emailCheckCode,
    };

    // 调用验证接口
    await verifyResetEmail(verifyData);
    ElMessage.success("原邮箱验证成功");

    // 进入下一步（保留验证码，用于最终提交）
    emailStep.value = 1;
  } catch (error) {
    if (error !== false) {
      ElMessage.error("验证失败，请检查验证码是否正确");
      console.error("验证原邮箱失败:", error);
    }
  } finally {
    emailVerifyLoading.value = false;
  }
};

// 提交修改邮箱
const updateEmailSubmit = async () => {
  if (!newEmailFormRef.value) return;

  try {
    await newEmailFormRef.value.validate();
    emailUpdateLoading.value = true;

    const updateData = {
      email: emailForm.email,
      newEmail: emailForm.newEmail,
      emailCheckCode: emailForm.emailCheckCode,
    };

    await updateEmail(updateData);
    ElMessage.success("邮箱修改成功");

    // 更新本地用户信息
    userInfo.value.email = emailForm.newEmail;

    // 更新 store 中的用户信息
    const res = await info();
    userStore.user = res.data.data;

    // 关闭对话框
    emailDialogVisible.value = false;
    resetEmailDialog();
  } catch (error) {
    if (error !== false) {
      ElMessage.error("邮箱修改失败");
      console.error("邮箱修改失败:", error);
    }
  } finally {
    emailUpdateLoading.value = false;
  }
};

// 组件挂载时获取用户信息
onMounted(() => {
  fetchUserInfo();
});
</script>

<style lang="scss" scoped>
// 设置页面容器
.setting-container {
  min-height: 100vh;
  background-color: var(--el-bg-color-page);

  // 响应式：移动端优化背景
  @media (max-width: 768px) {
    background-attachment: scroll;
    padding: 16px 0;
  }

  // 设置内容区域
  .setting-content {
    max-width: 900px;
    margin: 0 auto;
    padding: 20px 20px 0 20px;

    // 页面标题
    .setting-header {
      background: var(--el-bg-color-page);
      backdrop-filter: blur(10px);
      border-radius: 8px;
      padding: 24px;
      margin-bottom: 20px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);

      h2 {
        font-size: 24px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 8px 0;
      }

      .header-desc {
        font-size: 14px;
        color: var(--el-text-color-regular);
        margin: 0;
      }
    }

    // 设置表单容器
    .setting-form-container {
      background: var(--el-bg-color-page);
      backdrop-filter: blur(10px);
      border-radius: 8px;
      padding: 32px;
      border: 1px solid var(--el-border-color);
      box-shadow: 0 2px 12px var(--el-border-color-light);

      // 响应式：移动端调整内边距
      @media (max-width: 768px) {
        padding: 20px;
      }

      // 骨架屏容器
      .skeleton-container {
        padding: 40px 20px;
      }

      // 用户信息容器
      .user-info-container {
        // 信息项
        .info-item {
          display: flex;
          padding: 24px 0;
          border-bottom: 1px solid var(--el-border-color-lighter);

          &:last-child {
            border-bottom: none;
          }

          // 响应式：移动端垂直布局
          @media (max-width: 768px) {
            flex-direction: column;
            gap: 12px;
          }

          // 头像项特殊处理
          &.avatar-item {
            flex-direction: column;
            gap: 0;

            .item-label {
              width: 100%;
              margin-bottom: 16px;
            }

            .item-content {
              width: 100%;
            }
          }

          // 标签
          .item-label {
            width: 120px;
            font-size: 15px;
            font-weight: 600;
            color: var(--el-text-color-primary);
            flex-shrink: 0;
          }

          // 内容
          .item-content {
            flex: 1;

            // 显示模式
            .display-mode {
              display: flex;
              align-items: center;
              justify-content: space-between;
              gap: 16px;

              .display-value {
                flex: 1;
                font-size: 14px;
                color: var(--el-text-color-regular);
                line-height: 1.6;
                word-break: break-word;
              }
            }

            // 编辑模式
            .edit-mode {
              display: flex;
              flex-direction: column;
              gap: 12px;

              .edit-actions {
                display: flex;
                gap: 8px;
              }
            }

            // 头像上传容器
            .avatar-upload-container {
              display: flex;
              align-items: flex-start;
              gap: 24px;

              // 响应式：移动端垂直布局
              @media (max-width: 768px) {
                flex-direction: column;
                align-items: center;
              }

              // 头像上传器
              .avatar-uploader {
                :deep(.el-upload) {
                  border: 2px dashed var(--el-border-color);
                  border-radius: 50%;
                  cursor: pointer;
                  position: relative;
                  overflow: hidden;
                  transition: all 0.3s;
                  width: 120px;
                  height: 120px;
                  display: flex;
                  align-items: center;
                  justify-content: center;

                  &:hover {
                    border-color: var(--el-color-primary);
                  }
                }
              }

              // 头像预览
              .avatar-preview {
                width: 120px;
                height: 120px;
                border-radius: 50%;
                object-fit: cover;
                display: block;
              }

              // 头像占位符
              .avatar-placeholder {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                width: 100%;
                height: 100%;
                color: var(--el-text-color-placeholder);

                .avatar-uploader-icon {
                  font-size: 32px;
                  margin-bottom: 8px;
                }

                .upload-text {
                  font-size: 12px;
                }
              }

              // 头像上传提示
              .avatar-tips {
                flex: 1;
                color: var(--el-text-color-secondary);
                font-size: 13px;
                line-height: 1.6;

                p {
                  margin: 0 0 4px 0;

                  &:last-child {
                    margin-bottom: 0;
                  }
                }

                // 响应式：移动端居中
                @media (max-width: 768px) {
                  text-align: center;
                }
              }
            }
          }
        }
      }
    }
  }
}

// 对话框样式优化
:deep(.el-dialog) {
  // 默认宽度
  width: 500px;
  // 垂直居中显示
  margin: 15vh auto !important;

  // 屏幕宽度小于768px时的宽度
  @media screen and (max-width: 767px) {
    width: 90% !important;
    margin: 20vh auto !important;
    max-height: 80vh;
    overflow-y: auto;
  }

  // 验证码面板样式
  .check-code-panel {
    display: flex;
    width: 100%;
    align-items: center;

    // 移动端验证码面板优化
    @media (max-width: 768px) {
      flex-direction: column;
      gap: 12px;

      .el-button {
        width: 100%;
        margin-left: 0 !important;
      }
    }
  }

  // 成功消息样式
  .success-message {
    text-align: center;
    padding: 20px 0;

    // 跳转提示
    .jump-tip {
      font-size: 14px;
      color: var(--el-text-color-regular);
      margin: 0;
    }
  }

  // 对话框底部按钮
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;

    // 移动端按钮优化
    @media (max-width: 768px) {
      flex-direction: column;
      gap: 8px;

      .el-button {
        width: 100%;
      }
    }
  }
}

// 移动端步骤条优化
:deep(.el-steps) {
  @media (max-width: 768px) {
    .el-step__title {
      font-size: 12px;
    }
  }
}

// 移动端表单优化
:deep(.el-form) {
  @media (max-width: 768px) {
    .el-form-item {
      margin-bottom: 16px;
    }
  }
}

// 对话框按钮样式优化
:deep(.dialog-footer) {
  .el-button {
    margin-left: 0 !important;
  }
}
</style>
