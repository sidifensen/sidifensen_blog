<template>
  <div class="setting-container">
    <div class="setting-content">
      <div class="setting-header">
        <div class="setting-header__intro">
          <p class="setting-header__eyebrow">Account</p>
          <h2>个人设置</h2>
          <p class="setting-header__desc">在这里维护你的公开资料、登录邮箱和账号安全信息。</p>
        </div>
        <div class="setting-header__meta">
          <span class="setting-header__meta-label">资料完整度</span>
          <strong class="setting-header__meta-value">{{ profileCompletion }}%</strong>
        </div>
      </div>

      <el-skeleton :loading="userLoading" animated>
        <template #template>
          <div class="setting-skeleton">
            <el-skeleton-item variant="p" style="width: 180px; height: 18px" />
            <el-skeleton-item variant="text" style="width: 68%; margin-top: 10px" />
            <div class="setting-skeleton__card">
              <el-skeleton-item variant="circle" style="width: 88px; height: 88px" />
              <div class="setting-skeleton__info">
                <el-skeleton-item variant="h3" style="width: 120px; margin-bottom: 12px" />
                <el-skeleton-item variant="text" style="width: 200px; margin-bottom: 10px" />
                <el-skeleton-item variant="text" style="width: 160px" />
              </div>
            </div>
          </div>
        </template>

        <template #default>
          <div v-if="userInfo" class="setting-main">
            <div class="setting-main__overview">
              <div class="setting-main__overview-card profile-card">
                <div class="profile-card__main">
                  <div class="profile-card__avatar">
                    <el-upload class="avatar-uploader" :action="''" :http-request="handleAvatarUpload" :show-file-list="false" :before-upload="beforeAvatarUpload" accept="image/*">
                      <img v-if="userInfo.avatar" :src="userInfo.avatar" class="avatar-preview" />
                      <div v-else class="avatar-placeholder">
                        <el-icon class="avatar-placeholder__icon">
                          <Plus />
                        </el-icon>
                        <span class="avatar-placeholder__text">上传头像</span>
                      </div>
                    </el-upload>
                  </div>

                  <div class="profile-card__info">
                    <h3>{{ userInfo.nickname || "未设置昵称" }}</h3>
                    <p>{{ introductionPreview }}</p>
                    <div class="profile-card__meta">
                      <span>{{ maskedEmail }}</span>
                      <span>{{ sexLabel }}</span>
                    </div>
                  </div>
                </div>

                <div class="profile-card__foot">
                  <span>支持 jpg、jpeg、png、webp，建议 200 x 200，大小不超过 1MB</span>
                </div>
              </div>

              <div class="setting-main__overview-card security-card">
                <div class="security-card__item">
                  <span class="security-card__label">登录邮箱</span>
                  <strong class="security-card__value">{{ maskedEmail }}</strong>
                </div>
                <div class="security-card__item">
                  <span class="security-card__label">密码状态</span>
                  <strong class="security-card__value">已设置</strong>
                </div>
                <div class="security-card__item">
                  <span class="security-card__label">资料完整度</span>
                  <strong class="security-card__value">{{ profileCompletion }}%</strong>
                </div>
              </div>
            </div>

            <div class="setting-main__sections">
              <div class="setting-main__section section-card">
                <div class="section-card__header">
                  <div class="section-card__title">
                    <h3>基础资料</h3>
                    <p>这些信息会影响你的个人主页展示。</p>
                  </div>
                </div>

                <div class="section-card__body">
                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>昵称</span>
                      <p>建议使用 4-20 个字符的常用名称。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div v-if="!editingField.nickname" class="display-mode">
                        <span class="display-value">{{ userInfo.nickname || "未设置" }}</span>
                        <el-button class="inline-action" text @click="startEdit('nickname')">
                          修改
                          <el-icon><Edit /></el-icon>
                        </el-button>
                      </div>
                      <div v-else class="edit-mode">
                        <div class="edit-surface">
                          <el-input v-model="editingData.nickname" placeholder="请输入昵称" maxlength="20" show-word-limit clearable @keydown.enter.prevent="saveField('nickname')" @keydown.esc.stop.prevent="cancelEdit('nickname')">
                            <template #prefix>
                              <el-icon>
                                <User />
                              </el-icon>
                            </template>
                          </el-input>
                        </div>
                        <div class="edit-toolbar">
                          <span class="edit-toolbar__hint">回车保存，Esc 取消</span>
                          <div class="edit-actions">
                            <el-button type="primary" :loading="saveLoading.nickname" @click="saveField('nickname')">保存更改</el-button>
                            <el-button text @click="cancelEdit('nickname')">取消</el-button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>性别</span>
                      <p>仅用于资料展示，可随时调整。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div v-if="!editingField.sex" class="display-mode">
                        <span class="display-value">{{ sexLabel }}</span>
                        <el-button class="inline-action" text @click="startEdit('sex')">
                          修改
                          <el-icon><Edit /></el-icon>
                        </el-button>
                      </div>
                      <div v-else class="edit-mode">
                        <div class="edit-surface edit-surface--choice">
                          <el-radio-group v-model="editingData.sex">
                            <el-radio :label="0">男</el-radio>
                            <el-radio :label="1">女</el-radio>
                          </el-radio-group>
                        </div>
                        <div class="edit-toolbar">
                          <span class="edit-toolbar__hint">修改后会同步展示在个人主页</span>
                          <div class="edit-actions">
                            <el-button type="primary" :loading="saveLoading.sex" @click="saveField('sex')">保存更改</el-button>
                            <el-button text @click="cancelEdit('sex')">取消</el-button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>个人简介</span>
                      <p>一句话说明你在这里关注什么、擅长什么。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div v-if="!editingField.introduction" class="display-mode display-mode--multiline">
                        <span class="display-value">{{ introductionPreview }}</span>
                        <el-button class="inline-action" text @click="startEdit('introduction')">
                          修改
                          <el-icon><Edit /></el-icon>
                        </el-button>
                      </div>
                      <div v-else class="edit-mode">
                        <div class="edit-surface edit-surface--textarea">
                          <el-input v-model="editingData.introduction" type="textarea" placeholder="请输入个人简介" :autosize="{ minRows: 4, maxRows: 8 }" maxlength="200" show-word-limit resize="none" @keydown.ctrl.enter.prevent="saveField('introduction')" @keydown.esc.stop.prevent="cancelEdit('introduction')" />
                        </div>
                        <div class="edit-toolbar">
                          <span class="edit-toolbar__hint">{{ editingData.introduction?.length || 0 }}/200，Ctrl + Enter 保存</span>
                          <div class="edit-actions">
                            <el-button type="primary" :loading="saveLoading.introduction" @click="saveField('introduction')">保存更改</el-button>
                            <el-button text @click="cancelEdit('introduction')">取消</el-button>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="setting-main__section section-card">
                <div class="section-card__header">
                  <div class="section-card__title">
                    <h3>账号安全</h3>
                    <p>建议保持邮箱可用，并定期更新密码。</p>
                  </div>
                </div>

                <div class="section-card__body">
                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>邮箱</span>
                      <p>用于登录通知、验证码和找回密码。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div class="display-mode">
                        <span class="display-value">{{ userInfo.email || "未设置" }}</span>
                        <el-button class="inline-action" text @click="openEmailDialog">
                          修改邮箱
                          <el-icon><Edit /></el-icon>
                        </el-button>
                      </div>
                    </div>
                  </div>

                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>密码</span>
                      <p>建议使用不重复的高强度密码。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div class="display-mode">
                        <span class="display-value">••••••••</span>
                        <el-button class="inline-action" text @click="openPasswordDialog">
                          修改密码
                          <el-icon><Edit /></el-icon>
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <div class="setting-main__section section-card">
                <div class="section-card__header">
                  <div class="section-card__title">
                    <h3>账号设置</h3>
                    <p>管理你的账号通知偏好设置。</p>
                  </div>
                </div>

                <div class="section-card__body">
                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>私信邮件通知</span>
                      <p>收到新私信时（第一条未读消息），通过邮件通知你。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div class="display-mode">
                        <el-switch
                          v-model="isReceivePrivateMessageEmail"
                          :active-value="1"
                          :inactive-value="0"
                          @change="handlePrivateMessageEmailChange"
                        />
                      </div>
                    </div>
                  </div>

                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>评论邮件通知</span>
                      <p>收到新评论时，通过邮件通知你。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div class="display-mode">
                        <el-switch
                          v-model="isReceiveCommentEmail"
                          :active-value="1"
                          :inactive-value="0"
                          @change="handleCommentEmailChange"
                        />
                      </div>
                    </div>
                  </div>

                  <div class="section-card__row">
                    <div class="section-card__row-label">
                      <span>系统邮件通知</span>
                      <p>收到系统通知时（审核结果、系统公告等），通过邮件通知你。</p>
                    </div>
                    <div class="section-card__row-content">
                      <div class="display-mode">
                        <el-switch
                          v-model="isReceiveSystemEmail"
                          :active-value="1"
                          :inactive-value="0"
                          @change="handleSystemEmailChange"
                        />
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </template>
      </el-skeleton>
    </div>

    <el-dialog v-model="passwordDialogVisible" title="修改密码" class="setting-dialog" :close-on-click-modal="false" @close="resetPasswordDialog">
      <div class="setting-dialog__panel">
        <div class="setting-dialog__hero">
          <span class="setting-dialog__eyebrow">Security</span>
          <h3>{{ passwordStep === 0 ? "验证身份" : "设置新密码" }}</h3>
          <p>{{ passwordStep === 0 ? "先验证当前绑定邮箱，再继续修改密码。" : "使用新的登录密码，建议包含字母和数字。" }}</p>
        </div>

        <div class="setting-dialog__steps">
          <el-steps :active="passwordStep" finish-status="success" align-center>
            <el-step title="验证邮箱" />
            <el-step title="修改密码" />
          </el-steps>
        </div>

        <div class="setting-dialog__body">
          <el-form v-if="passwordStep === 0" ref="emailFormRef" :model="passwordForm" :rules="passwordRules" label-width="0">
            <div class="setting-dialog__group">
              <span class="setting-dialog__group-label">当前邮箱</span>
              <el-form-item prop="email">
                <el-input v-model="passwordForm.email" type="email" placeholder="邮箱" :disabled="true">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>

            <div class="setting-dialog__group">
              <span class="setting-dialog__group-label">验证码</span>
              <el-form-item prop="emailCheckCode">
                <div class="check-code-panel">
                  <el-input v-model="passwordForm.emailCheckCode" maxlength="6" placeholder="请输入验证码">
                    <template #prefix>
                      <el-icon><EditPen /></el-icon>
                    </template>
                  </el-input>
                  <el-button type="success" :disabled="waitTime > 0" @click="sendEmailCode">
                    {{ waitTime > 0 ? `请稍后 ${waitTime} 秒` : "获取验证码" }}
                  </el-button>
                </div>
              </el-form-item>
            </div>
          </el-form>

          <el-form v-if="passwordStep === 1" ref="passwordFormRef" :model="passwordForm" :rules="passwordRules" label-width="0">
            <div class="setting-dialog__group">
              <span class="setting-dialog__group-label">新密码</span>
              <el-form-item prop="password">
                <el-input v-model="passwordForm.password" placeholder="新密码" show-password>
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>

            <div class="setting-dialog__group">
              <span class="setting-dialog__group-label">确认密码</span>
              <el-form-item prop="repeatPassword">
                <el-input v-model="passwordForm.repeatPassword" placeholder="确认新密码" show-password>
                  <template #prefix>
                    <el-icon><Lock /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
          </el-form>
        </div>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button v-if="passwordStep === 0" type="primary" :loading="verifyLoading" :disabled="!hasRequestedPasswordCode" @click="verifyEmail">下一步</el-button>
          <el-button v-if="passwordStep === 1" type="primary" :loading="resetLoading" @click="resetPasswordSubmit">确认修改</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog v-model="emailDialogVisible" title="修改邮箱" class="setting-dialog" :close-on-click-modal="false" @close="resetEmailDialog">
      <div class="setting-dialog__panel">
        <div class="setting-dialog__hero">
          <span class="setting-dialog__eyebrow">Account</span>
          <h3>{{ emailStep === 0 ? "验证原邮箱" : "绑定新邮箱" }}</h3>
          <p>{{ emailStep === 0 ? "为保证账号安全，需要先确认当前邮箱可用。" : "新邮箱将用于通知提醒、验证码和密码找回。" }}</p>
        </div>

        <div class="setting-dialog__steps">
          <el-steps :active="emailStep" finish-status="success" align-center>
            <el-step title="验证原邮箱" />
            <el-step title="输入新邮箱" />
          </el-steps>
        </div>

        <div class="setting-dialog__body">
          <el-form v-if="emailStep === 0" ref="oldEmailFormRef" :model="emailForm" :rules="oldEmailRules" label-width="0">
            <div class="setting-dialog__group">
              <span class="setting-dialog__group-label">原邮箱</span>
              <el-form-item prop="email">
                <el-input v-model="emailForm.email" type="email" placeholder="原邮箱" :disabled="true">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>

            <div class="setting-dialog__group">
              <span class="setting-dialog__group-label">验证码</span>
              <el-form-item prop="emailCheckCode">
                <div class="check-code-panel">
                  <el-input v-model="emailForm.emailCheckCode" maxlength="6" placeholder="请输入验证码">
                    <template #prefix>
                      <el-icon><EditPen /></el-icon>
                    </template>
                  </el-input>
                  <el-button type="success" :disabled="emailWaitTime > 0" @click="sendOldEmailCode">
                    {{ emailWaitTime > 0 ? `请稍后 ${emailWaitTime} 秒` : "获取验证码" }}
                  </el-button>
                </div>
              </el-form-item>
            </div>
          </el-form>

          <el-form v-if="emailStep === 1" ref="newEmailFormRef" :model="emailForm" :rules="newEmailRules" label-width="0">
            <div class="setting-dialog__group">
              <span class="setting-dialog__group-label">新邮箱</span>
              <el-form-item prop="newEmail">
                <el-input v-model="emailForm.newEmail" type="email" placeholder="请输入新邮箱">
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
          </el-form>
        </div>
      </div>

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
import { computed, onMounted, reactive, ref } from "vue";
import { Edit, EditPen, Lock, Message, Plus, User } from "@element-plus/icons-vue";
import { info, resetPassword, sendEmail, updateEmail, updateUserInfo, verifyResetEmail, verifyResetPassword, updateCommentEmailSetting, updateSystemEmailSetting } from "@/api/user";
import { uploadAvatar } from "@/api/photo";
import { compressImage, validateAvatarImageFile } from "@/utils/PhotoUtils";
import { useUserStore } from "@/stores/userStore";

const userStore = useUserStore();

const userLoading = ref(false);
const userInfo = ref(null);

// 私信邮件通知开关
const isReceivePrivateMessageEmail = ref(0);

// 评论邮件通知开关
const isReceiveCommentEmail = ref(0);

// 系统邮件通知开关
const isReceiveSystemEmail = ref(0);

const editingField = reactive({
  nickname: false,
  sex: false,
  introduction: false,
});

const editingData = reactive({
  nickname: "",
  sex: 0,
  introduction: "",
});

const saveLoading = reactive({
  nickname: false,
  sex: false,
  introduction: false,
});

const sexLabel = computed(() => {
  if (!userInfo.value) {
    return "未设置";
  }

  return userInfo.value.sex === 0 ? "男" : userInfo.value.sex === 1 ? "女" : "未设置";
});

const introductionPreview = computed(() => {
  return userInfo.value?.introduction || "这个人很懒，什么都没写。";
});

const maskedEmail = computed(() => {
  const email = userInfo.value?.email;
  if (!email) {
    return "未设置邮箱";
  }

  const [name, domain] = email.split("@");
  if (!name || !domain) {
    return email;
  }

  if (name.length <= 2) {
    return `${name[0] || ""}***@${domain}`;
  }

  return `${name.slice(0, 2)}***@${domain}`;
});

const profileCompletion = computed(() => {
  if (!userInfo.value) {
    return 0;
  }

  const fields = [userInfo.value.avatar, userInfo.value.nickname, userInfo.value.email, userInfo.value.introduction];
  const completedCount = fields.filter((item) => (typeof item === "string" ? item.trim() !== "" : Boolean(item))).length;
  return Math.round((completedCount / fields.length) * 100);
});

const fetchUserInfo = async () => {
  try {
    userLoading.value = true;
    const res = await info();
    userInfo.value = res.data.data;
    // 初始化私信邮件通知开关状态
    isReceivePrivateMessageEmail.value = res.data.data.isReceivePrivateMessageEmail || 0;
    // 初始化评论邮件通知开关状态
    isReceiveCommentEmail.value = res.data.data.isReceiveCommentEmail || 0;
    // 初始化系统邮件通知开关状态
    isReceiveSystemEmail.value = res.data.data.isReceiveSystemEmail || 0;
  } catch (error) {
    ElMessage.error("获取用户信息失败");
    console.error("获取用户信息失败:", error);
  } finally {
    userLoading.value = false;
  }
};

const startEdit = (field) => {
  editingData[field] = userInfo.value[field] ?? "";
  editingField[field] = true;
};

const cancelEdit = (field) => {
  editingField[field] = false;
  editingData[field] = "";
};

const saveField = async (field) => {
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

  if (field === "introduction" && editingData.introduction && editingData.introduction.length > 200) {
    ElMessage.warning("简介长度不能超过 200 个字符");
    return;
  }

  try {
    saveLoading[field] = true;

    const updateData = {
      nickname: userInfo.value.nickname,
      sex: userInfo.value.sex,
      introduction: userInfo.value.introduction,
      avatar: userInfo.value.avatar,
      [field]: editingData[field],
    };

    await updateUserInfo(updateData);
    userInfo.value[field] = editingData[field];

    const res = await info();
    userStore.user = res.data.data;

    editingField[field] = false;
    ElMessage.success("修改成功");
  } catch (error) {
    ElMessage.error("修改失败");
    console.error("修改失败:", error);
  } finally {
    saveLoading[field] = false;
  }
};

const beforeAvatarUpload = (file) => {
  const validation = validateAvatarImageFile(file);
  if (!validation) {
    return false;
  }
  return true;
};

const handleAvatarUpload = async (options) => {
  const { file } = options;
  try {
    const compressedFile = await compressImage(file, 0.8, 800, 800);
    ElMessage.info("头像上传中...");
    await uploadAvatar(compressedFile);

    options.onSuccess && options.onSuccess();
    ElMessage.success("头像上传成功，正在审核中，审核通过后将自动更新");
  } catch (error) {
    console.error("头像上传失败:", error);
    ElMessage.error("头像上传失败，请重试");
    options.onError && options.onError();
  }
};

const passwordDialogVisible = ref(false);
const passwordStep = ref(0);
const waitTime = ref(0);
const hasRequestedPasswordCode = ref(false);
const verifyLoading = ref(false);
const resetLoading = ref(false);

const passwordForm = reactive({
  email: "",
  emailCheckCode: "",
  password: "",
  repeatPassword: "",
});

const emailFormRef = ref(null);
const passwordFormRef = ref(null);

const EmailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

const validateEmailFormat = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入邮箱"));
  } else if (!EmailRegex.test(value)) {
    callback(new Error("请输入合法的邮箱"));
  } else {
    callback();
  }
};

const validateEmailCheckCode = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入获取的验证码"));
  } else if (!/^\d{6}$/.test(value)) {
    callback(new Error("验证码必须是6位数字"));
  } else {
    callback();
  }
};

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

const validateRepeatPassword = (rule, value, callback) => {
  if (value === "") {
    callback(new Error("请再次输入密码"));
  } else if (value !== passwordForm.password) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const passwordRules = {
  email: [{ validator: validateEmailFormat, trigger: ["blur", "change"] }],
  emailCheckCode: [{ validator: validateEmailCheckCode, trigger: ["blur", "change"] }],
  password: [{ validator: validatePasswordCharacters, trigger: ["blur", "change"] }],
  repeatPassword: [{ validator: validateRepeatPassword, trigger: ["blur", "change"] }],
};

const openPasswordDialog = () => {
  passwordDialogVisible.value = true;
  passwordStep.value = 0;
  passwordForm.email = userInfo.value.email;
  passwordForm.emailCheckCode = "";
  passwordForm.password = "";
  passwordForm.repeatPassword = "";
  waitTime.value = 0;
  hasRequestedPasswordCode.value = false;
};

const resetPasswordDialog = () => {
  passwordStep.value = 0;
  passwordForm.email = "";
  passwordForm.emailCheckCode = "";
  passwordForm.password = "";
  passwordForm.repeatPassword = "";
  waitTime.value = 0;
  hasRequestedPasswordCode.value = false;
};

const sendEmailCode = async () => {
  try {
    const emailDto = {
      email: passwordForm.email,
      type: "resetPassword",
    };
    await sendEmail(emailDto);
    ElMessage.success(`验证码已发送到邮箱：${passwordForm.email}，请注意查收`);
    hasRequestedPasswordCode.value = true;
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

const verifyEmail = async () => {
  if (!emailFormRef.value) {
    return;
  }

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
      ElMessage.error("验证失败，请检查验证码是否正确");
      console.error("邮箱验证失败:", error);
    }
  } finally {
    verifyLoading.value = false;
  }
};

const resetPasswordSubmit = async () => {
  if (!passwordFormRef.value) {
    return;
  }

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

const emailDialogVisible = ref(false);
const emailStep = ref(0);
const emailWaitTime = ref(0);
const hasRequestedEmailCode = ref(false);
const emailVerifyLoading = ref(false);
const emailUpdateLoading = ref(false);

const emailForm = reactive({
  email: "",
  newEmail: "",
  emailCheckCode: "",
});

const oldEmailFormRef = ref(null);
const newEmailFormRef = ref(null);

const validateNewEmailFormat = (rule, value, callback) => {
  if (!value) {
    callback(new Error("请输入新邮箱"));
  } else if (!EmailRegex.test(value)) {
    callback(new Error("请输入合法的邮箱"));
  } else {
    callback();
  }
};

const oldEmailRules = {
  email: [{ required: true, message: "原邮箱不能为空", trigger: "blur" }],
  emailCheckCode: [{ validator: validateEmailCheckCode, trigger: ["blur", "change"] }],
};

const newEmailRules = {
  newEmail: [{ validator: validateNewEmailFormat, trigger: ["blur", "change"] }],
};

const openEmailDialog = () => {
  emailDialogVisible.value = true;
  emailStep.value = 0;
  emailForm.email = userInfo.value.email;
  emailForm.newEmail = "";
  emailForm.emailCheckCode = "";
  emailWaitTime.value = 0;
  hasRequestedEmailCode.value = false;
};

const resetEmailDialog = () => {
  emailStep.value = 0;
  emailForm.email = "";
  emailForm.newEmail = "";
  emailForm.emailCheckCode = "";
  emailWaitTime.value = 0;
  hasRequestedEmailCode.value = false;
};

const sendOldEmailCode = async () => {
  try {
    const emailDto = {
      email: emailForm.email,
      type: "resetEmail",
    };
    await sendEmail(emailDto);
    ElMessage.success(`验证码已发送到原邮箱：${emailForm.email}，请注意查收`);
    hasRequestedEmailCode.value = true;
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

const verifyOldEmail = async () => {
  if (!oldEmailFormRef.value) {
    return;
  }

  try {
    await oldEmailFormRef.value.validate();
    emailVerifyLoading.value = true;

    const verifyData = {
      email: emailForm.email,
      emailCheckCode: emailForm.emailCheckCode,
    };

    await verifyResetEmail(verifyData);
    ElMessage.success("原邮箱验证成功");
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

const updateEmailSubmit = async () => {
  if (!newEmailFormRef.value) {
    return;
  }

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

    userInfo.value.email = emailForm.newEmail;

    const res = await info();
    userStore.user = res.data.data;

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

// 处理私信邮件通知开关变化
const handlePrivateMessageEmailChange = async (value) => {
  try {
    await updateUserInfo({
      isReceivePrivateMessageEmail: value,
    });
    ElMessage.success(value === 1 ? "已开启私信邮件通知" : "已关闭私信邮件通知");
  } catch (error) {
    ElMessage.error("设置失败");
    console.error("设置私信邮件通知失败:", error);
    // 恢复状态
    isReceivePrivateMessageEmail.value = value === 1 ? 0 : 1;
  }
};

// 处理评论邮件通知开关变化
const handleCommentEmailChange = async (value) => {
  try {
    await updateCommentEmailSetting(value);
    ElMessage.success(value === 1 ? "已开启评论邮件通知" : "已关闭评论邮件通知");
  } catch (error) {
    ElMessage.error("设置失败");
    console.error("设置评论邮件通知失败:", error);
    // 恢复状态
    isReceiveCommentEmail.value = value === 1 ? 0 : 1;
  }
};

// 处理系统邮件通知开关变化
const handleSystemEmailChange = async (value) => {
  try {
    await updateSystemEmailSetting(value);
    ElMessage.success(value === 1 ? "已开启系统邮件通知" : "已关闭系统邮件通知");
  } catch (error) {
    ElMessage.error("设置失败");
    console.error("设置系统邮件通知失败:", error);
    // 恢复状态
    isReceiveSystemEmail.value = value === 1 ? 0 : 1;
  }
};

onMounted(() => {
  fetchUserInfo();
});
</script>

<style lang="scss" scoped>
.setting-container {
  --setting-bg: #f4f6f8;
  --setting-panel: #ffffff;
  --setting-panel-soft: #f8fafb;
  --setting-text-primary: #18222c;
  --setting-text-regular: #536170;
  --setting-text-muted: #7f8a96;
  --setting-border: #dbe2e8;
  --setting-border-soft: #edf1f4;
  --setting-shadow: 0 12px 32px rgba(15, 23, 42, 0.06);
  --setting-shadow-soft: 0 1px 3px rgba(15, 23, 42, 0.08);

  min-height: 100vh;
  background:
    radial-gradient(circle at top left, var(--setting-panel-soft) 0%, transparent 34%),
    linear-gradient(180deg, var(--setting-bg) 0%, var(--setting-bg) 100%);

  .setting-content {
    max-width: 1120px;
    margin: 0 auto;
    padding: 32px 24px 40px;

    .setting-header {
      display: flex;
      align-items: flex-end;
      justify-content: space-between;
      gap: 24px;
      margin-bottom: 24px;

      .setting-header__intro {
        .setting-header__eyebrow {
          margin: 0 0 10px;
          font-size: 12px;
          letter-spacing: 0.16em;
          text-transform: uppercase;
          color: var(--setting-text-muted);
        }

        h2 {
          margin: 0;
          font-size: 34px;
          line-height: 1.1;
          font-weight: 600;
          color: var(--setting-text-primary);
        }

        .setting-header__desc {
          margin: 12px 0 0;
          font-size: 15px;
          line-height: 1.7;
          color: var(--setting-text-regular);
        }
      }

      .setting-header__meta {
        min-width: 160px;
        padding: 18px 20px;
        border: 1px solid var(--setting-border);
        border-radius: 18px;
        background: var(--setting-panel);
        box-shadow: var(--setting-shadow-soft);
        text-align: right;

        .setting-header__meta-label {
          display: block;
          margin-bottom: 8px;
          font-size: 13px;
          color: var(--setting-text-muted);
        }

        .setting-header__meta-value {
          font-size: 28px;
          line-height: 1;
          color: var(--setting-text-primary);
        }
      }
    }

    .setting-skeleton {
      padding: 28px;
      border: 1px solid var(--setting-border);
      border-radius: 24px;
      background: var(--setting-panel);
      box-shadow: var(--setting-shadow);

      .setting-skeleton__card {
        display: flex;
        gap: 20px;
        align-items: center;
        margin-top: 28px;
        padding-top: 24px;
        border-top: 1px solid var(--setting-border-soft);

        .setting-skeleton__info {
          flex: 1;
        }
      }
    }

    .setting-main {
      .setting-main__overview {
        display: grid;
        grid-template-columns: minmax(0, 1.8fr) minmax(280px, 1fr);
        gap: 20px;
        margin-bottom: 20px;

        .setting-main__overview-card {
          border: 1px solid var(--setting-border);
          border-radius: 24px;
          background: var(--setting-panel);
          box-shadow: var(--setting-shadow);
        }

        .profile-card {
          padding: 24px;

          .profile-card__main {
            display: flex;
            gap: 24px;
            align-items: center;
            margin-bottom: 18px;

            .profile-card__avatar {
              flex-shrink: 0;

              .avatar-uploader {
                :deep(.el-upload) {
                  width: 88px;
                  height: 88px;
                  border-radius: 24px;
                  border: 1px dashed var(--setting-border);
                  background: var(--setting-panel-soft);
                  overflow: hidden;
                  transition: border-color 0.2s ease, box-shadow 0.2s ease;

                  &:hover {
                    border-color: var(--el-color-primary);
                    box-shadow: 0 0 0 4px var(--el-color-primary-light-9);
                  }
                }
              }

              .avatar-preview {
                width: 88px;
                height: 88px;
                object-fit: cover;
                display: block;
              }

              .avatar-placeholder {
                display: flex;
                width: 88px;
                height: 88px;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                color: var(--setting-text-muted);

                .avatar-placeholder__icon {
                  margin-bottom: 6px;
                  font-size: 22px;
                }

                .avatar-placeholder__text {
                  font-size: 12px;
                }
              }
            }
          }

          .profile-card__info {
            min-width: 0;
            flex: 1;

            h3 {
              margin: 0 0 10px;
              font-size: 26px;
              font-weight: 600;
              color: var(--setting-text-primary);
            }

            p {
              margin: 0;
              font-size: 14px;
              line-height: 1.8;
              color: var(--setting-text-regular);
            }

            .profile-card__meta {
              display: flex;
              flex-wrap: wrap;
              gap: 10px;
              margin-top: 14px;

              span {
                display: inline-flex;
                align-items: center;
                min-height: 32px;
                padding: 0 12px;
                border-radius: 999px;
                background: var(--setting-panel-soft);
                color: var(--setting-text-regular);
                font-size: 13px;
              }
            }
          }

          .profile-card__foot {
            padding-top: 18px;
            border-top: 1px solid var(--setting-border-soft);
            font-size: 13px;
            line-height: 1.7;
            color: var(--setting-text-muted);
          }
        }

        .security-card {
          display: flex;
          flex-direction: column;
          justify-content: center;
          padding: 24px;
          gap: 18px;

          .security-card__item {
            padding-bottom: 18px;
            border-bottom: 1px solid var(--setting-border-soft);

            &:last-child {
              padding-bottom: 0;
              border-bottom: none;
            }

            .security-card__label {
              display: block;
              margin-bottom: 8px;
              font-size: 13px;
              color: var(--setting-text-muted);
            }

            .security-card__value {
              font-size: 20px;
              font-weight: 600;
              color: var(--setting-text-primary);
            }
          }
        }
      }

      .setting-main__sections {
        display: grid;
        gap: 20px;

        .setting-main__section {
          &.section-card {
            border: 1px solid var(--setting-border);
            border-radius: 24px;
            background: var(--setting-panel);
            box-shadow: var(--setting-shadow);
            overflow: hidden;

            .section-card__header {
              padding: 24px 28px 18px;
              border-bottom: 1px solid var(--setting-border-soft);

              .section-card__title {
                h3 {
                  margin: 0;
                  font-size: 20px;
                  font-weight: 600;
                  color: var(--setting-text-primary);
                }

                p {
                  margin: 8px 0 0;
                  font-size: 14px;
                  color: var(--setting-text-regular);
                }
              }
            }

            .section-card__body {
              .section-card__row {
                display: grid;
                grid-template-columns: minmax(180px, 240px) minmax(0, 1fr);
                gap: 28px;
                padding: 24px 28px;
                border-bottom: 1px solid var(--setting-border-soft);

                &:last-child {
                  border-bottom: none;
                }

                .section-card__row-label {
                  span {
                    display: block;
                    margin-bottom: 8px;
                    font-size: 15px;
                    font-weight: 600;
                    color: var(--setting-text-primary);
                  }

                  p {
                    margin: 0;
                    font-size: 13px;
                    line-height: 1.7;
                    color: var(--setting-text-muted);
                  }
                }

                .section-card__row-content {
                  .display-mode {
                    display: flex;
                    align-items: center;
                    justify-content: space-between;
                    gap: 16px;

                    &.display-mode--multiline {
                      align-items: flex-start;
                    }

                    .display-value {
                      flex: 1;
                      min-width: 0;
                      font-size: 15px;
                      line-height: 1.8;
                      color: var(--setting-text-regular);
                      word-break: break-word;
                    }

                    .inline-action {
                      padding: 0;
                      color: var(--setting-text-muted);
                      font-size: 13px;
                      font-weight: 500;
                      transition: color 0.2s ease;

                      :deep(span) {
                        display: inline-flex;
                        align-items: center;
                        gap: 6px;
                      }

                      :deep(.el-icon) {
                        font-size: 13px;
                      }

                      &:hover,
                      &:focus-visible {
                        color: var(--setting-text-primary);
                      }
                    }
                  }

                  .edit-mode {
                    display: flex;
                    flex-direction: column;
                    gap: 10px;

                    .edit-surface {
                      padding: 12px;
                      border: 1px solid var(--setting-border);
                      border-radius: 16px;
                      background: var(--setting-panel-soft);
                      transition: border-color 0.2s ease, box-shadow 0.2s ease;

                      &:focus-within {
                        border-color: var(--el-color-primary-light-5);
                        box-shadow: 0 0 0 3px var(--el-color-primary-light-9);
                      }

                      &.edit-surface--choice {
                        padding: 14px 16px;

                        :deep(.el-radio-group) {
                          display: flex;
                          flex-wrap: wrap;
                          gap: 20px;
                        }
                      }

                      &.edit-surface--textarea {
                        padding-bottom: 8px;
                      }

                      :deep(.el-input__wrapper) {
                        padding-inline: 0;
                        background: transparent;
                        box-shadow: none;
                      }

                      :deep(.el-textarea__inner) {
                        padding: 0;
                        border: none;
                        background: transparent;
                        box-shadow: none;
                      }
                    }

                    .edit-toolbar {
                      display: flex;
                      align-items: center;
                      justify-content: space-between;
                      gap: 12px;

                      .edit-toolbar__hint {
                        font-size: 12px;
                        line-height: 1.6;
                        color: var(--setting-text-muted);
                      }
                    }

                    .edit-actions {
                      display: flex;
                      align-items: center;
                      gap: 6px;

                      :deep(.el-button + .el-button) {
                        margin-left: 0;
                      }

                      :deep(.el-button--text) {
                        padding-inline: 6px;
                        color: var(--setting-text-regular);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

html.dark {
  .setting-container {
    --setting-bg: #0f1720;
    --setting-panel: #16202a;
    --setting-panel-soft: #1b2733;
    --setting-text-primary: #e8edf2;
    --setting-text-regular: #b8c2cb;
    --setting-text-muted: #8d9aa6;
    --setting-border: #293545;
    --setting-border-soft: #212c3a;
    --setting-shadow: 0 16px 36px rgba(0, 0, 0, 0.24);
    --setting-shadow-soft: 0 1px 3px rgba(0, 0, 0, 0.3);
  }
}

@media (max-width: 992px) {
  .setting-container {
    .setting-content {
      .setting-header {
        align-items: flex-start;
        flex-direction: column;

        .setting-header__meta {
          width: 100%;
          text-align: left;
        }
      }

      .setting-main {
        .setting-main__overview {
          grid-template-columns: 1fr;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .setting-container {
    .setting-content {
      padding: 20px 16px 32px;

      .setting-header {
        margin-bottom: 16px;

        .setting-header__intro {
          h2 {
            font-size: 28px;
          }
        }
      }

      .setting-skeleton {
        padding: 20px;

        .setting-skeleton__card {
          flex-direction: column;
          align-items: flex-start;
        }
      }

      .setting-main {
        .setting-main__overview {
          gap: 16px;
          margin-bottom: 16px;

          .profile-card {
            padding: 20px;

            .profile-card__main {
              flex-direction: column;
              align-items: flex-start;
            }
          }
        }

        .setting-main__sections {
          gap: 16px;

          .setting-main__section {
            &.section-card {
              .section-card__header {
                padding: 20px 20px 16px;
              }

              .section-card__body {
                .section-card__row {
                  grid-template-columns: 1fr;
                  gap: 14px;
                  padding: 20px;

                  .section-card__row-content {
                    .display-mode {
                      align-items: flex-start;
                      flex-direction: column;

                      .el-button {
                        padding-left: 0;
                      }
                    }

                    .edit-mode {
                      .edit-toolbar {
                        align-items: flex-start;
                        flex-direction: column;

                        .edit-actions {
                          width: 100%;
                          justify-content: flex-start;

                          .el-button:first-child {
                            min-width: 108px;
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

:deep(.el-dialog) {
  width: 500px;
  margin: 15vh auto !important;

  @media screen and (max-width: 767px) {
    width: 90% !important;
    margin: 20vh auto !important;
    max-height: 80vh;
    overflow-y: auto;
  }

  .check-code-panel {
    display: flex;
    width: 100%;
    align-items: center;
    gap: 10px;

    @media (max-width: 768px) {
      flex-direction: column;
      gap: 12px;

      .el-button {
        width: 100%;
        margin-left: 0 !important;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 10px;

    @media (max-width: 768px) {
      flex-direction: column;
      gap: 8px;

      .el-button {
        width: 100%;
      }
    }
  }
}

:deep(.setting-dialog) {
  .el-dialog__header {
    margin-right: 0;
    padding: 24px 24px 0;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: var(--setting-text-primary);
    }
  }

  .el-dialog__body {
    padding: 16px 24px 8px;
  }

  .el-dialog__footer {
    padding: 0 24px 24px;
  }

  .setting-dialog__panel {
    border: 1px solid var(--setting-border-soft);
    border-radius: 20px;
    background: var(--setting-panel-soft);
    overflow: hidden;
  }

  .setting-dialog__hero {
    padding: 20px 20px 18px;
    border-bottom: 1px solid var(--setting-border-soft);
    background: var(--setting-panel);

    .setting-dialog__eyebrow {
      display: block;
      margin-bottom: 8px;
      font-size: 12px;
      letter-spacing: 0.14em;
      text-transform: uppercase;
      color: var(--setting-text-muted);
    }

    h3 {
      margin: 0;
      font-size: 22px;
      font-weight: 600;
      color: var(--setting-text-primary);
    }

    p {
      margin: 10px 0 0;
      font-size: 14px;
      line-height: 1.7;
      color: var(--setting-text-regular);
    }
  }

  .setting-dialog__steps {
    padding: 18px 20px 0;
  }

  .setting-dialog__body {
    padding: 20px;
  }

  .setting-dialog__group {
    margin-bottom: 18px;

    &:last-child {
      margin-bottom: 0;
    }

    .setting-dialog__group-label {
      display: block;
      margin-bottom: 8px;
      font-size: 13px;
      font-weight: 600;
      color: var(--setting-text-primary);
    }

    .el-form-item {
      margin-bottom: 0;
    }
  }

  @media (max-width: 768px) {
    .el-dialog__header {
      padding: 20px 20px 0;
    }

    .el-dialog__body {
      padding: 14px 20px 8px;
    }

    .el-dialog__footer {
      padding: 0 20px 20px;
    }

    .setting-dialog__hero {
      padding: 18px 16px 16px;

      h3 {
        font-size: 20px;
      }
    }

    .setting-dialog__steps {
      padding: 16px 16px 0;
    }

    .setting-dialog__body {
      padding: 16px;
    }
  }
}

:deep(.el-steps) {
  @media (max-width: 768px) {
    .el-step__title {
      font-size: 12px;
    }
  }
}

:deep(.el-form) {
  @media (max-width: 768px) {
    .el-form-item {
      margin-bottom: 16px;
    }
  }
}

:deep(.dialog-footer) {
  .el-button {
    margin-left: 0 !important;
  }
}

/* 开关按钮样式 - 简洁现代设计 */
:deep(.el-switch) {
  --switch-bg-off: var(--setting-border, #e2e8f0);
  --switch-bg-on: #0ea5e9;

  .el-switch__core {
    width: 44px;
    height: 24px;
    background-color: var(--switch-bg-off);
    border-radius: 12px;
    transition: background-color 0.25s ease, border-color 0.25s ease;
    border: 1px solid transparent;
    padding: 2px;
    box-sizing: border-box;

    &::before {
      content: '';
      position: absolute;
      top: 2px;
      left: 2px;
      width: 18px;
      height: 18px;
      background-color: #ffffff;
      border-radius: 50%;
      box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
      transition: transform 0.25s ease;
    }
  }

  &.is-checked {
    .el-switch__core {
      background-color: var(--switch-bg-on);
      border-color: var(--switch-bg-on);

      &::before {
        transform: translateX(20px);
      }
    }
  }

  /* 悬停状态 */
  &:hover:not(.is-disabled) {
    .el-switch__core {
      opacity: 0.85;
    }
  }

  /* 禁用状态 */
  &.is-disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

/* 黑夜模式适配 */
html.dark {
  :deep(.el-switch) {
    --switch-bg-off: #334155;
    --switch-bg-on: #0ea5e9;

    .el-switch__core {
      &::before {
        background-color: #f1f5f9;
        box-shadow: 0 1px 3px rgba(0, 0, 0, 0.4);
      }
    }
  }
}
</style>
