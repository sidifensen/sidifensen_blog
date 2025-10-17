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
  }
});

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
    border-radius: 12px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    margin: 0 auto;
    max-height: 90vh;
    overflow-y: auto;
  }

  :deep(.el-dialog__header) {
    padding: 0;
    border-bottom: none;
  }

  :deep(.el-dialog__body) {
    padding: 24px;
    max-height: calc(90vh - 120px);
    overflow-y: auto;
  }

  :deep(.el-dialog__footer) {
    padding: 0 24px 24px;
    border-top: 1px solid var(--el-border-color-lighter);
    background-color: var(--el-bg-color-page);
    border-radius: 0 0 12px 12px;
  }

  // 对话框头部
  .dialog-header {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 24px 24px 20px;
    background: linear-gradient(135deg, var(--el-color-primary-light-9) 0%, var(--el-color-primary-light-8) 100%);
    border-radius: 12px 12px 0 0;

    .header-icon {
      font-size: 32px;
      color: var(--el-color-primary);
      background: var(--el-color-primary-light-9);
      padding: 12px;
      border-radius: 50%;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
    }

    .header-content {
      flex: 1;

      .header-title {
        font-size: 20px;
        font-weight: 600;
        color: var(--el-text-color-primary);
        margin: 0 0 4px 0;
      }

      .header-description {
        font-size: 14px;
        color: var(--el-text-color-regular);
        margin: 0;
        line-height: 1.4;
      }
    }
  }

  // 申请表单
  .link-apply-form {
    margin-top: 20px;

    :deep(.el-form-item) {
      margin-bottom: 20px;

      .el-form-item__label {
        font-weight: 600;
        color: var(--el-text-color-primary);
      }
    }

    :deep(.el-input) {
      .el-input__wrapper {
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        &.is-focus {
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
        }
      }
    }

    :deep(.el-textarea) {
      .el-textarea__inner {
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
        transition: all 0.3s ease;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        &:focus {
          box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
        }
      }
    }

    // 封面预览
    .cover-preview {
      margin-top: 12px;
      display: flex;
      justify-content: center;

      .preview-image {
        width: 120px;
        height: 80px;
        border-radius: 8px;
        border: 2px solid var(--el-border-color-lighter);
        overflow: hidden;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          border-color: var(--el-color-primary);
          transform: scale(1.02);
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
        background-color: var(--el-bg-color-page);

        .el-icon {
          font-size: 24px;
          margin-bottom: 4px;
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
    margin-top: 24px;
    padding: 16px;
    background-color: var(--el-color-info-light-9);
    border: 1px solid var(--el-color-info-light-7);
    border-radius: 8px;

    .notice-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 14px;
      font-weight: 600;
      color: var(--el-color-info);
      margin: 0 0 12px 0;

      .el-icon {
        font-size: 16px;
      }
    }

    .notice-list {
      margin: 0;
      padding-left: 20px;
      color: var(--el-text-color-regular);

      li {
        font-size: 13px;
        line-height: 1.6;
        margin-bottom: 4px;

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
    padding-top: 20px;

    .el-button {
      border-radius: 8px;
      padding: 12px 24px;
      font-weight: 500;

      &.el-button--primary {
        background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-dark-2) 100%);
        border: none;
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

        &:hover {
          background: linear-gradient(135deg, var(--el-color-primary-light-3) 0%, var(--el-color-primary) 100%);
          transform: translateY(-1px);
          box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        }

        .el-icon {
          margin-right: 4px;
        }
      }
    }
  }
}

// 移动端响应式样式
@media (max-width: 768px) {
  .link-apply-dialog {
    :deep(.el-dialog) {
      width: 95% !important;
      margin: 5vh auto;
      max-height: 90vh;
    }

    :deep(.el-dialog__body) {
      padding: 16px;
      max-height: calc(90vh - 100px);
    }

    :deep(.el-dialog__footer) {
      padding: 0 16px 16px;
    }

    // 对话框头部移动端优化
    .dialog-header {
      padding: 16px;
      gap: 12px;

      .header-icon {
        font-size: 24px;
        padding: 8px;
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
      margin-top: 16px;

      :deep(.el-form-item) {
        margin-bottom: 16px;

        .el-form-item__label {
          font-size: 14px;
          padding-bottom: 4px;
        }
      }

      :deep(.el-input) {
        .el-input__wrapper {
          padding: 8px 12px;
        }
      }

      :deep(.el-textarea) {
        .el-textarea__inner {
          padding: 8px 12px;
        }
      }

      // 封面预览移动端优化
      .cover-preview {
        .preview-image {
          width: 100px;
          height: 70px;
        }
      }
    }

    // 申请须知移动端优化
    .apply-notice {
      margin-top: 16px;
      padding: 12px;

      .notice-title {
        font-size: 13px;
        margin-bottom: 8px;
      }

      .notice-list {
        li {
          font-size: 12px;
          line-height: 1.5;
        }
      }
    }

    // 底部按钮移动端优化
    .dialog-footer {
      padding-top: 16px;
      gap: 8px;

      .el-button {
        padding: 10px 20px;
        font-size: 14px;
        flex: 1;
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
