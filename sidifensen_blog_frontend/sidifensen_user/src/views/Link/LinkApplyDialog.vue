<template>
  <el-dialog v-model="dialogVisible" title="申请友链" :width="dialogWidth" :before-close="handleClose" class="link-apply-dialog">
    <!-- 对话框头部说明 -->
    <div class="dialog-header">
      <el-icon class="header-icon"><Link /></el-icon>
      <div class="header-content">
        <h3 class="header-title">友链申请</h3>
        <p class="header-description">请填写您的网站信息，我们会尽快审核您的申请</p>
      </div>
    </div>

    <!-- 申请表单 -->
    <el-form ref="linkFormRef" :model="linkForm" :rules="linkFormRules" label-width="80px" class="link-apply-form">
      <el-form-item label="网站名称" prop="name">
        <el-input v-model="linkForm.name" placeholder="请输入网站名称" maxlength="20" show-word-limit clearable>
          <template #prefix>
            <el-icon><House /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="网站地址" prop="url">
        <el-input v-model="linkForm.url" placeholder="请输入完整的网站地址 (如: https://example.com)" maxlength="100" show-word-limit clearable>
          <template #prefix>
            <el-icon><Link /></el-icon>
          </template>
        </el-input>
      </el-form-item>

      <el-form-item label="网站封面" prop="coverUrl">
        <el-input v-model="linkForm.coverUrl" placeholder="请输入网站封面图片地址 (可选)" clearable>
          <template #prefix>
            <el-icon><Picture /></el-icon>
          </template>
        </el-input>
        <!-- 封面预览 -->
        <div v-if="linkForm.coverUrl" class="cover-preview">
          <el-image :src="linkForm.coverUrl" fit="cover" class="preview-image" :preview-src-list="imagePreviewList" :preview-teleported="true">
            <template #placeholder>
              <div class="image-placeholder">
                <el-icon><Loading /></el-icon>
                <span>加载中...</span>
              </div>
            </template>
            <template #error>
              <div class="image-error">
                <el-icon><Picture /></el-icon>
                <span>图片加载失败</span>
              </div>
            </template>
          </el-image>
        </div>
      </el-form-item>

      <el-form-item label="网站描述" prop="description">
        <el-input v-model="linkForm.description" type="textarea" :rows="3" placeholder="请简单描述您的网站内容和特色" maxlength="50" show-word-limit resize="none" />
      </el-form-item>

      <el-form-item label="联系邮箱" prop="email">
        <el-input v-model="linkForm.email" placeholder="请输入您的邮箱地址" clearable>
          <template #prefix>
            <el-icon><Message /></el-icon>
          </template>
        </el-input>
      </el-form-item>
    </el-form>

    <!-- 申请须知 -->
    <div class="apply-notice">
      <h4 class="notice-title">
        <el-icon><InfoFilled /></el-icon>
        申请须知
      </h4>
      <ul class="notice-list">
        <li>请确保网站内容健康，符合相关法律法规</li>
        <li>网站应正常运行，内容更新频率较高</li>
        <li>管理员收到消息后会尽快完成审核</li>
        <li>审核结果将通过邮箱通知您</li>
      </ul>
    </div>

    <!-- 对话框底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose" size="large">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading" size="large">
          <el-icon v-if="!submitLoading"><Check /></el-icon>
          {{ submitLoading ? "提交中..." : "提交申请" }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted, onUnmounted } from "vue";
import { Link, House, Picture, Message, InfoFilled, Check, Loading } from "@element-plus/icons-vue";
import { applyLink } from "@/api/link";

// 定义组件属性
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  initialData: {
    type: Object,
    default: () => ({}),
  },
});

// 定义组件事件
const emit = defineEmits(["update:modelValue", "success"]);

// 响应式数据
const dialogVisible = ref(false);
const submitLoading = ref(false);
const linkFormRef = ref(null);
const isMobile = ref(false);

// 申请表单数据
const linkForm = reactive({
  name: "",
  url: "",
  coverUrl: "",
  description: "",
  email: "",
});

// 计算属性 - 图片预览列表
const imagePreviewList = computed(() => {
  // 只有当图片URL有效且不为空时才添加到预览列表
  if (linkForm.coverUrl && linkForm.coverUrl.trim()) {
    return [linkForm.coverUrl];
  }
  return [];
});

// 计算属性 - 对话框宽度
const dialogWidth = computed(() => {
  return isMobile.value ? "95%" : "500px";
});

// 表单验证规则
const linkFormRules = {
  name: [
    { required: true, message: "请输入网站名称", trigger: "blur" },
    { min: 1, max: 20, message: "网站名称长度在1到20个字符", trigger: "blur" },
  ],
  url: [
    { required: true, message: "请输入网站地址", trigger: "blur" },
    { max: 200, message: "网站地址不能超过200个字符", trigger: "blur" },
    {
      pattern: /^https?:\/\/.+/,
      message: "请输入有效的网站地址 (以http://或https://开头)",
      trigger: "blur",
    },
  ],
  description: [
    { required: true, message: "请输入网站描述", trigger: "blur" },
    { min: 1, max: 50, message: "网站描述长度在1到50个字符", trigger: "blur" },
  ],
  email: [
    { required: true, message: "请输入邮箱地址", trigger: "blur" },
    {
      type: "email",
      message: "请输入有效的邮箱地址",
      trigger: "blur",
    },
  ],
};

// 监听父组件传入的visible值
watch(
  () => props.modelValue,
  (newVal) => {
    dialogVisible.value = newVal;
  },
  { immediate: true }
);

// 监听对话框显示状态
watch(dialogVisible, (newVal) => {
  emit("update:modelValue", newVal);
  if (!newVal) {
    resetForm();
  } else {
    // 对话框打开时，如果有初始数据，填充表单
    if (props.initialData && Object.keys(props.initialData).length > 0) {
      fillFormWithInitialData();
    }
  }
});

// 监听初始数据变化
watch(
  () => props.initialData,
  (newData) => {
    if (newData && Object.keys(newData).length > 0 && dialogVisible.value) {
      fillFormWithInitialData();
    }
  },
  { deep: true }
);

// 使用初始数据填充表单
const fillFormWithInitialData = () => {
  if (props.initialData) {
    linkForm.name = props.initialData.name || "";
    linkForm.url = props.initialData.url || "";
    linkForm.coverUrl = props.initialData.coverUrl || "";
    linkForm.description = props.initialData.description || "";
    linkForm.email = props.initialData.email || "";
  }
};

// 重置表单
const resetForm = () => {
  if (linkFormRef.value) {
    linkFormRef.value.resetFields();
  }
  Object.keys(linkForm).forEach((key) => {
    linkForm[key] = "";
  });
};

// 关闭对话框
const handleClose = () => {
  dialogVisible.value = false;
};

// 检测设备类型
const checkDevice = () => {
  isMobile.value = window.innerWidth <= 768;
};

// 提交申请
const handleSubmit = async () => {
  if (!linkFormRef.value) return;

  try {
    // 表单验证
    await linkFormRef.value.validate();

    submitLoading.value = true;

    // 提交申请
    await applyLink(linkForm);

    ElMessage.success("友链申请提交成功，我们会尽快审核！");
    emit("success");
    handleClose();
  } catch (error) {
    if (error.message) {
      ElMessage.error("申请提交失败：" + error.message);
    } else {
      console.error("友链申请失败:", error);
    }
  } finally {
    submitLoading.value = false;
  }
};

// 组件挂载和卸载
onMounted(() => {
  checkDevice();
  window.addEventListener("resize", checkDevice);
});

onUnmounted(() => {
  window.removeEventListener("resize", checkDevice);
});
</script>

<style lang="scss" scoped>
// 友链申请对话框样式
.link-apply-dialog {
  :deep(.el-dialog) {
    border-radius: 20px;
    box-shadow: 0 24px 80px rgba(0, 0, 0, 0.15);
    margin: 0 auto;
    max-height: 90vh;
    overflow-y: auto;
    background: var(--el-bg-color);
  }

  :deep(.el-dialog__header) {
    padding: 0;
    border-bottom: none;
    background: var(--el-fill-color-lighter);
    border-radius: 20px 20px 0 0;
  }

  :deep(.el-dialog__body) {
    padding: 28px;
    max-height: calc(90vh - 140px);
    overflow-y: auto;
  }

  :deep(.el-dialog__footer) {
    padding: 0 28px 28px;
    border-top: 1px solid var(--el-border-color-lighter);
    background-color: var(--el-fill-color-lighter);
    border-radius: 0 0 20px 20px;
    margin: 0 -28px -28px;
    padding: 20px 28px;
  }

  // 对话框头部
  .dialog-header {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 28px 28px 24px;

    .header-icon {
      width: 56px;
      height: 56px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 28px;
      color: white;
      background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
      border-radius: 16px;
      box-shadow: 0 8px 24px rgba(64, 158, 255, 0.35), inset 0 -2px 4px rgba(0, 0, 0, 0.1);
      flex-shrink: 0;
    }

    .header-content {
      flex: 1;
      min-width: 0;

      .header-title {
        font-size: 22px;
        font-weight: 700;
        color: var(--el-text-color-primary);
        margin: 0 0 6px 0;
        background: linear-gradient(135deg, var(--el-text-color-primary) 0%, var(--el-color-primary) 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .header-description {
        font-size: 14px;
        color: var(--el-text-color-regular);
        margin: 0;
        line-height: 1.6;
        opacity: 0.9;
      }
    }
  }

  // 申请表单
  .link-apply-form {
    margin-top: 24px;

    :deep(.el-form-item) {
      margin-bottom: 22px;

      .el-form-item__label {
        font-weight: 600;
        color: var(--el-text-color-primary);
        font-size: 14px;
        margin-bottom: 8px;
      }
    }

    :deep(.el-input) {
      .el-input__wrapper {
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        border: 1px solid var(--el-border-color-lighter);
        transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        padding: 12px 14px;

        &:hover {
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
          border-color: var(--el-color-primary-light-5);
        }

        &.is-focus {
          box-shadow: 0 4px 20px rgba(64, 158, 255, 0.25);
          border-color: var(--el-color-primary);
        }
      }
    }

    :deep(.el-textarea) {
      .el-textarea__inner {
        border-radius: 12px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
        border: 1px solid var(--el-border-color-lighter);
        transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        padding: 12px 14px;

        &:hover {
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
          border-color: var(--el-color-primary-light-5);
        }

        &:focus {
          box-shadow: 0 4px 20px rgba(64, 158, 255, 0.25);
          border-color: var(--el-color-primary);
        }
      }
    }

    // 封面预览
    .cover-preview {
      margin-top: 14px;
      display: flex;

      .preview-image {
        width: 140px;
        height: 90px;
        border-radius: 12px;
        border: 2px solid var(--el-border-color-lighter);
        overflow: hidden;
        cursor: pointer;
        transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);

        &:hover {
          border-color: var(--el-color-primary);
          transform: scale(1.03);
          box-shadow: 0 8px 24px rgba(64, 158, 255, 0.2);
        }
      }

      .image-placeholder,
      .image-error {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        height: 100%;
        color: var(--el-text-color-placeholder);
        font-size: 12px;
        background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-info-light-9) 100%);
        gap: 8px;

        .el-icon {
          font-size: 28px;
        }
      }

      .image-placeholder {
        .el-icon {
          animation: spin 1s linear infinite;
        }
      }
    }
  }

  // 申请须知
  .apply-notice {
    margin-top: 28px;
    padding: 18px;
    background: linear-gradient(135deg, rgba(64, 158, 255, 0.05) 0%, rgba(103, 194, 255, 0.03) 100%);
    border: 1px solid rgba(64, 158, 255, 0.15);
    border-radius: 14px;

    .notice-title {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 15px;
      font-weight: 600;
      color: var(--el-color-primary);
      margin: 0 0 14px 0;

      .el-icon {
        font-size: 18px;
      }
    }

    .notice-list {
      margin: 0;
      padding-left: 20px;
      color: var(--el-text-color-regular);

      li {
        font-size: 13px;
        line-height: 1.8;
        margin-bottom: 6px;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  // 对话框底部
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 8px;

    .el-button {
      border-radius: 10px;
      padding: 12px 28px;
      font-weight: 600;
      font-size: 14px;
      transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);

      &.el-button--default {
        border-color: var(--el-border-color);
        background: var(--el-bg-color);

        &:hover {
          color: var(--el-color-primary);
          border-color: var(--el-color-primary-light-5);
          background: var(--el-color-primary-light-9);
        }
      }

      &.el-button--primary {
        background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-dark-2) 100%);
        border: none;
        box-shadow: 0 6px 20px rgba(64, 158, 255, 0.35), inset 0 -2px 4px rgba(0, 0, 0, 0.1);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 10px 32px rgba(64, 158, 255, 0.45), inset 0 -2px 4px rgba(0, 0, 0, 0.1);
        }

        &:active {
          transform: translateY(0);
        }

        .el-icon {
          margin-right: 6px;
        }
      }
    }
  }
}

// 移动端响应式样式
@media (max-width: 768px) {
  .link-apply-dialog {
    :deep(.el-dialog) {
      width: 92% !important;
      margin: 4vh auto;
      max-height: 92vh;
      border-radius: 16px;
    }

    :deep(.el-dialog__body) {
      padding: 20px;
      max-height: calc(92vh - 120px);
    }

    :deep(.el-dialog__footer) {
      padding: 16px 20px;
      margin: 0 -20px -20px;
    }

    // 对话框头部移动端优化
    .dialog-header {
      padding: 20px 20px 18px;
      gap: 14px;

      .header-icon {
        width: 48px;
        height: 48px;
        font-size: 24px;
        border-radius: 14px;
      }

      .header-content {
        .header-title {
          font-size: 18px;
        }

        .header-description {
          font-size: 13px;
        }
      }
    }

    // 表单移动端优化
    .link-apply-form {
      margin-top: 20px;

      :deep(.el-form-item) {
        margin-bottom: 18px;
      }

      // 封面预览移动端优化
      .cover-preview {
        .preview-image {
          width: 120px;
          height: 80px;
        }
      }
    }

    // 申请须知移动端优化
    .apply-notice {
      margin-top: 20px;
      padding: 14px;

      .notice-title {
        font-size: 14px;
        margin-bottom: 10px;
      }

      .notice-list {
        li {
          font-size: 12px;
          line-height: 1.7;
        }
      }
    }

    // 底部按钮移动端优化
    .dialog-footer {
      padding-top: 4px;
      gap: 10px;
      flex-direction: column;

      .el-button {
        width: 100%;
        padding: 13px 20px;
        font-size: 15px;
      }
    }
  }
}

// 旋转动画
@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}
</style>
