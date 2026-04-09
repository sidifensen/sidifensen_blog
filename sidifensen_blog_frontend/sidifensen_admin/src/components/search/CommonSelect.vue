<template>
  <div class="common-select">
    <span v-if="label" class="filter-label">{{ label }}</span>
    <el-select
      :model-value="modelValue"
      :placeholder="placeholder"
      filterable
      :clearable="clearable"
      :disabled="disabled"
      :size="size"
      :class="['search-select', selectClass]"
      :style="{ width: width }"
      :remote="remote"
      :remote-method="remote ? loadOptions : undefined"
      :loading="loading"
      :reserve-keyword="reserveKeyword"
      @update:model-value="handleUpdate"
      @change="handleChange"
    >
      <el-option v-if="showAll" label="全部" value="" />
      <el-option v-for="opt in options" :key="getOptionValue(opt)" :label="getOptionLabel(opt)" :value="getOptionValue(opt)" :disabled="getOptionDisabled(opt)" />
    </el-select>
  </div>
</template>

<script setup>
/**
 * 通用下拉选择组件
 *
 * 功能说明：
 * - 支持本地选项列表和远程加载选项
 * - 支持 v-model 绑定
 * - 支持黑夜模式
 * - 支持自定义选项的 label/value/disabled 字段映射
 * - 标签固定显示在左侧
 *
 * 使用方式：
 * ```vue
 * <!-- 本地选项 -->
 * <CommonSelect v-model="value" :options="menuList" option-label="name" option-value="id" />
 *
 * <!-- 远程搜索选项 -->
 * <CommonSelect v-model="value" remote :load-options="fetchMenuList" />
 *
 * <!-- 带标签 -->
 * <CommonSelect v-model="value" :options="options" label="菜单" width="160px" />
 * ```
 */

// Props
const props = defineProps({
  // v-model 绑定值
  modelValue: {
    type: [String, Number, Array],
    default: '',
  },
  // 选择框宽度
  width: {
    type: String,
    default: '160px',
  },
  // 标签文本
  label: {
    type: String,
    default: '',
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择',
  },
  // 自定义类名
  selectClass: {
    type: String,
    default: '',
  },
  // 选项列表（本地模式）
  options: {
    type: Array,
    default: () => [],
  },
  // 选项标签字段名
  optionLabel: {
    type: String,
    default: 'label',
  },
  // 选项值字段名
  optionValue: {
    type: String,
    default: 'value',
  },
  // 选项禁用字段名
  optionDisabled: {
    type: String,
    default: 'disabled',
  },
  // 是否可清空
  clearable: {
    type: Boolean,
    default: true,
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false,
  },
  // 尺寸
  size: {
    type: String,
    default: 'small',
  },
  // 是否显示"全部"选项
  showAll: {
    type: Boolean,
    default: false,
  },
  // 远程加载模式
  remote: {
    type: Boolean,
    default: false,
  },
  // 远程加载方法
  loadOptions: {
    type: Function,
    default: null,
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false,
  },
  // 是否保留搜索关键词
  reserveKeyword: {
    type: Boolean,
    default: false,
  },
})

// Emits
const emit = defineEmits(['update:modelValue', 'change'])

// 获取选项标签
const getOptionLabel = (opt) => {
  if (typeof opt === 'string' || typeof opt === 'number') {
    return opt
  }
  return opt[props.optionLabel]
}

// 获取选项值
const getOptionValue = (opt) => {
  if (typeof opt === 'string' || typeof opt === 'number') {
    return opt
  }
  return opt[props.optionValue]
}

// 获取选项禁用状态
const getOptionDisabled = (opt) => {
  if (typeof opt === 'string' || typeof opt === 'number') {
    return false
  }
  return opt[props.optionDisabled] || false
}

// 更新处理
const handleUpdate = (val) => {
  emit('update:modelValue', val)
}

// 变化处理
const handleChange = (val) => {
  emit('update:modelValue', val)
  emit('change', val)
}
</script>

<style lang="scss" scoped>
.common-select {
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
    color: var(--text-placeholder);
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .common-select {
    width: 100%;

    .filter-label {
      width: 56px;
      text-align: right;
      flex-shrink: 0;
    }

    .search-select {
      width: 100% !important;
    }
  }
}
</style>
