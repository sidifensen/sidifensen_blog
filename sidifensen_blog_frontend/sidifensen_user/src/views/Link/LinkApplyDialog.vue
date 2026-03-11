<template>
  <el-dialog v-model="dialogVisible" title="申请友链" :width="dialogWidth" :before-close="handleClose" class="link-apply-dialog">
    <div class="link-apply-dialog__panel">
      <div class="link-apply-dialog__hero">
        <div class="link-apply-dialog__hero-mark">
          <el-icon><Link /></el-icon>
        </div>
        <div class="link-apply-dialog__hero-copy">
          <span class="link-apply-dialog__eyebrow">Directory Submission</span>
          <h3>提交站点资料</h3>
          <p>填写站点名称、地址和简介。通过审核后，会出现在友链目录中。</p>
        </div>
      </div>

      <div class="link-apply-dialog__content">
        <el-form ref="linkFormRef" :model="linkForm" :rules="linkFormRules" label-width="0" class="link-apply-form">
          <div class="link-apply-form__grid">
            <div class="link-apply-form__field">
              <span class="link-apply-form__label">网站名称</span>
              <el-form-item prop="name">
                <el-input v-model="linkForm.name" placeholder="请输入网站名称" maxlength="20" show-word-limit clearable>
                  <template #prefix>
                    <el-icon><House /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>

            <div class="link-apply-form__field">
              <span class="link-apply-form__label">联系邮箱</span>
              <el-form-item prop="email">
                <el-input v-model="linkForm.email" placeholder="请输入您的邮箱地址" clearable>
                  <template #prefix>
                    <el-icon><Message /></el-icon>
                  </template>
                </el-input>
              </el-form-item>
            </div>
          </div>

          <div class="link-apply-form__field">
            <span class="link-apply-form__label">网站地址</span>
            <el-form-item prop="url">
              <el-input v-model="linkForm.url" placeholder="请输入完整的网站地址，如 https://example.com" maxlength="100" show-word-limit clearable>
                <template #prefix>
                  <el-icon><Link /></el-icon>
                </template>
              </el-input>
            </el-form-item>
          </div>

          <div class="link-apply-form__field">
            <span class="link-apply-form__label">网站封面</span>
            <el-form-item prop="coverUrl">
              <el-input v-model="linkForm.coverUrl" placeholder="请输入网站封面图片地址，可选" clearable>
                <template #prefix>
                  <el-icon><Picture /></el-icon>
                </template>
              </el-input>
            </el-form-item>

            <div v-if="linkForm.coverUrl" class="cover-preview">
              <el-image :src="linkForm.coverUrl" fit="cover" class="preview-image" :preview-src-list="imagePreviewList" :preview-teleported="true">
                <template #placeholder>
                  <div class="image-placeholder">
                    <el-icon><Loading /></el-icon>
                    <span>加载中</span>
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
          </div>

          <div class="link-apply-form__field">
            <span class="link-apply-form__label">网站描述</span>
            <el-form-item prop="description">
              <el-input v-model="linkForm.description" type="textarea" :rows="4" placeholder="请简要描述网站内容、方向或更新特点" maxlength="50" show-word-limit resize="none" />
            </el-form-item>
          </div>
        </el-form>

        <div class="apply-notice">
          <div class="apply-notice__head">
            <el-icon><InfoFilled /></el-icon>
            <span>申请须知</span>
          </div>
          <ul class="apply-notice__list">
            <li>网站需可正常访问，内容长期维护。</li>
            <li>请确保站点内容健康、合规，且具备明确主题。</li>
            <li>提交后会进入审核流程，结果通过邮箱通知。</li>
          </ul>
        </div>
      </div>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <span class="dialog-footer__hint">提交即表示你同意站点信息用于友链展示。</span>
        <div class="dialog-footer__actions">
          <el-button @click="handleClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
            <el-icon v-if="!submitLoading"><Check /></el-icon>
            {{ submitLoading ? "提交中..." : "提交申请" }}
          </el-button>
        </div>
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
  return isMobile.value ? "94%" : "680px";
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
.link-apply-dialog {
  --dialog-panel: #ffffff;
  --dialog-panel-soft: #f7f9fb;
  --dialog-text-primary: #18212b;
  --dialog-text-regular: #53606d;
  --dialog-text-muted: #7f8a96;
  --dialog-border: #dce3e9;
  --dialog-border-soft: #ebf0f4;
  --dialog-shadow: 0 18px 38px rgba(15, 23, 42, 0.12);

  :deep(.el-dialog) {
    overflow: hidden;
    border-radius: 24px;
    background: var(--dialog-panel);
    box-shadow: var(--dialog-shadow);
  }

  :deep(.el-dialog__header) {
    margin-right: 0;
    padding: 24px 28px 0;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: var(--dialog-text-primary);
    }
  }

  :deep(.el-dialog__body) {
    padding: 18px 28px 12px;
  }

  :deep(.el-dialog__footer) {
    padding: 0 28px 28px;
  }

  .link-apply-dialog__panel {
    overflow: hidden;
    border: 1px solid var(--dialog-border-soft);
    border-radius: 22px;
    background: var(--dialog-panel-soft);

    .link-apply-dialog__hero {
      display: flex;
      gap: 18px;
      padding: 22px;
      border-bottom: 1px solid var(--dialog-border-soft);
      background: var(--dialog-panel);

      .link-apply-dialog__hero-mark {
        display: inline-flex;
        width: 52px;
        height: 52px;
        align-items: center;
        justify-content: center;
        border-radius: 18px;
        background: var(--dialog-panel-soft);
        color: var(--dialog-text-primary);
        font-size: 24px;
        flex-shrink: 0;
      }

      .link-apply-dialog__hero-copy {
        .link-apply-dialog__eyebrow {
          display: block;
          margin-bottom: 8px;
          font-size: 12px;
          letter-spacing: 0.14em;
          text-transform: uppercase;
          color: var(--dialog-text-muted);
        }

        h3 {
          margin: 0;
          font-size: 24px;
          font-weight: 600;
          color: var(--dialog-text-primary);
        }

        p {
          margin: 10px 0 0;
          font-size: 14px;
          line-height: 1.8;
          color: var(--dialog-text-regular);
        }
      }
    }

    .link-apply-dialog__content {
      padding: 22px;
    }
  }

  .link-apply-form {
    .link-apply-form__grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: 16px;
    }

    .link-apply-form__field {
      margin-bottom: 18px;

      &:last-child {
        margin-bottom: 0;
      }

      .link-apply-form__label {
        display: block;
        margin-bottom: 8px;
        font-size: 13px;
        font-weight: 600;
        color: var(--dialog-text-primary);
      }

      :deep(.el-form-item) {
        margin-bottom: 0;
      }

      :deep(.el-input__wrapper) {
        border-radius: 12px;
        background: var(--dialog-panel);
        border: 1px solid var(--dialog-border);
        box-shadow: none;

        &:hover {
          border-color: var(--dialog-text-muted);
        }

        &.is-focus {
          border-color: var(--el-color-primary);
          box-shadow: 0 0 0 3px var(--el-color-primary-light-9);
        }
      }

      :deep(.el-textarea__inner) {
        border-radius: 12px;
        background: var(--dialog-panel);
        border: 1px solid var(--dialog-border);
        box-shadow: none;

        &:hover {
          border-color: var(--dialog-text-muted);
        }

        &:focus {
          border-color: var(--el-color-primary);
          box-shadow: 0 0 0 3px var(--el-color-primary-light-9);
        }
      }

      .cover-preview {
        margin-top: 12px;

        .preview-image {
          width: 148px;
          height: 92px;
          overflow: hidden;
          border: 1px solid var(--dialog-border);
          border-radius: 14px;
          background: var(--dialog-panel);
        }

        .image-placeholder,
        .image-error {
          display: flex;
          width: 100%;
          height: 100%;
          flex-direction: column;
          align-items: center;
          justify-content: center;
          gap: 8px;
          color: var(--dialog-text-muted);
          font-size: 12px;
          background: var(--dialog-panel-soft);

          .el-icon {
            font-size: 22px;
          }
        }
      }
    }
  }

  .apply-notice {
    margin-top: 10px;
    padding: 18px;
    border: 1px solid var(--dialog-border);
    border-radius: 18px;
    background: var(--dialog-panel);

    .apply-notice__head {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-bottom: 10px;
      font-size: 14px;
      font-weight: 600;
      color: var(--dialog-text-primary);
    }

    .apply-notice__list {
      margin: 0;
      padding-left: 18px;
      color: var(--dialog-text-regular);

      li {
        margin-bottom: 6px;
        font-size: 13px;
        line-height: 1.8;

        &:last-child {
          margin-bottom: 0;
        }
      }
    }
  }

  .dialog-footer {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;

    .dialog-footer__hint {
      font-size: 12px;
      line-height: 1.7;
      color: var(--dialog-text-muted);
    }

    .dialog-footer__actions {
      display: flex;
      gap: 10px;

      :deep(.el-button + .el-button) {
        margin-left: 0;
      }

      .el-button {
        min-width: 112px;
        border-radius: 12px;
      }
    }
  }
}

html.dark {
  .link-apply-dialog {
    --dialog-panel: #16202a;
    --dialog-panel-soft: #1a2631;
    --dialog-text-primary: #e8edf2;
    --dialog-text-regular: #bcc6cf;
    --dialog-text-muted: #8d98a5;
    --dialog-border: #2a3544;
    --dialog-border-soft: #212c38;
    --dialog-shadow: 0 18px 42px rgba(0, 0, 0, 0.3);
  }
}

@media (max-width: 768px) {
  .link-apply-dialog {
    :deep(.el-dialog__header) {
      padding: 20px 20px 0;
    }

    :deep(.el-dialog__body) {
      padding: 16px 20px 10px;
    }

    :deep(.el-dialog__footer) {
      padding: 0 20px 20px;
    }

    .link-apply-dialog__panel {
      .link-apply-dialog__hero {
        padding: 18px;
      }

      .link-apply-dialog__content {
        padding: 18px;
      }
    }

    .link-apply-form {
      .link-apply-form__grid {
        grid-template-columns: 1fr;
      }
    }

    .dialog-footer {
      align-items: stretch;
      flex-direction: column;

      .dialog-footer__actions {
        width: 100%;
        flex-direction: column;

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
