<template>
  <el-dialog
    v-model="visible"
    :title="title"
    :width="width"
    :before-close="handleClose"
    class="edit-dialog"
  >
    <el-form
      ref="formRef"
      :model="formData"
      :rules="rules"
      :label-width="labelWidth"
      class="edit-form"
    >
      <slot />
    </el-form>

    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">
          {{ submitText }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

// Props
const props = defineProps({
  // 对话框显示状态
  modelValue: {
    type: Boolean,
    default: false,
  },
  // 标题
  title: {
    type: String,
    default: '编辑',
  },
  // 宽度
  width: {
    type: String,
    default: '500px',
  },
  // 表单标签宽度
  labelWidth: {
    type: String,
    default: '100px',
  },
  // 提交按钮文本
  submitText: {
    type: String,
    default: '确认',
  },
  // 提交加载状态
  submitLoading: {
    type: Boolean,
    default: false,
  },
  // 表单数据
  formData: {
    type: Object,
    default: () => ({}),
  },
  // 表单验证规则
  rules: {
    type: Object,
    default: () => ({}),
  },
})

// Emits
const emit = defineEmits(['update:modelValue', 'close', 'submit'])

// 响应式
const visible = ref(props.modelValue)
const formRef = ref(null)

// 监听 props 变化
watch(
  () => props.modelValue,
  (val) => {
    visible.value = val
  },
)

watch(visible, (val) => {
  emit('update:modelValue', val)
})

// 关闭处理
const handleClose = () => {
  formRef.value?.resetFields()
  visible.value = false
  emit('close')
}

// 提交处理
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    emit('submit')
  } catch (error) {
    // 验证失败
  }
}

// 暴露方法
defineExpose({
  validate: () => formRef.value?.validate(),
  resetFields: () => formRef.value?.resetFields(),
  clearValidate: () => formRef.value?.clearValidate(),
})
</script>

<style lang="scss" scoped>
.edit-dialog {
  border-radius: 16px;

  :deep(.el-dialog__header) {
    border-bottom: 1px solid var(--el-border-color-lighter);
    padding: 16px 20px;
    margin-right: 0;
  }

  :deep(.el-dialog__body) {
    padding: 20px;
  }

  :deep(.el-dialog__footer) {
    border-top: 1px solid var(--el-border-color-lighter);
    padding: 16px 20px;
  }
}

.edit-form {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper),
  :deep(.el-input-number__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:focus-within {
      box-shadow: 0 0 0 3px var(--admin-primary-light);
      border-color: var(--admin-primary);
    }
  }
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;

  :deep(.el-button) {
    min-width: 80px;
    border-radius: 8px;
    transition: all 0.3s ease;

    &:hover {
      transform: translateY(-2px);
    }
  }
}

// 移动端响应式
@media screen and (max-width: 768px) {
  .edit-dialog {
    width: 90% !important;
  }

  .edit-form {
    :deep(.el-form-item) {
      margin-bottom: 15px;
    }

    :deep(.el-form-item__label) {
      width: auto !important;
    }
  }

  .dialog-footer {
    :deep(.el-button) {
      flex: 1;
      max-width: 120px;
    }
  }
}
</style>
