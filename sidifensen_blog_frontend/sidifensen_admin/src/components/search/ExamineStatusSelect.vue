<template>
  <div class="examine-status-select">
    <span v-if="showLabel" class="filter-label">审核状态</span>
    <el-select
      v-model="value"
      :placeholder="placeholder"
      filterable
      clearable
      size="small"
      :class="['search-select', selectClass]"
      :style="{ width: width }"
      @change="handleChange"
    >
      <el-option label="全部" value="" />
      <el-option label="待审核" value="0" />
      <el-option label="审核通过" value="1" />
      <el-option label="审核不通过" value="2" />
    </el-select>
  </div>
</template>

<script setup>
/**
 * 审核状态选择器组件
 *
 * 功能说明：
 * - 提供审核状态的快速选择：全部、待审核、审核通过、审核不通过
 * - 支持 v-model 绑定
 * - 支持黑夜模式
 *
 * 使用方式：
 * ```vue
 * <ExamineStatusSelect v-model="examineStatus" />
 * <ExamineStatusSelect v-model="status" width="160px" show-label placeholder="选择状态" />
 * ```
 */

import { computed } from 'vue'

// Props
const props = defineProps({
  // v-model 绑定值
  modelValue: {
    type: String,
    default: '',
  },
  // 选择框宽度
  width: {
    type: String,
    default: '140px',
  },
  // 是否显示标签
  showLabel: {
    type: Boolean,
    default: true,
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择审核状态',
  },
  // 自定义类名
  selectClass: {
    type: String,
    default: '',
  },
})

// Emits
const emit = defineEmits(['update:modelValue', 'change'])

// v-model 支持
const value = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

// 变化处理
const handleChange = (val) => {
  emit('update:modelValue', val)
  emit('change', val)
}
</script>

<style lang="scss" scoped>
.examine-status-select {
  display: flex;
  align-items: center;
  gap: 8px;

  .filter-label {
    font-size: 14px;
    color: var(--text-regular);
    white-space: nowrap;
  }

  .search-select {
    width: v-bind(width);
  }

  :deep(.el-select__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;
    min-height: 32px;
    font-size: 14px;

    &:focus-within {
      box-shadow: 0 0 0 3px var(--admin-primary-light) !important;
      border-color: var(--admin-primary) !important;
    }
  }

  :deep(.el-select__placeholder) {
    color: var(--text-muted);
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .examine-status-select {
    width: 100%;

    .search-select {
      width: 100% !important;
    }
  }
}
</style>
