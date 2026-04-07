<template>
  <div class="keyword-search">
    <span v-if="showLabel" class="filter-label">{{ label }}</span>
    <el-input
      v-model="value"
      :placeholder="placeholder"
      :prefix-icon="Search"
      size="small"
      :clearable="clearable"
      :class="['search-input', inputClass]"
      :style="inputStyle"
      @input="handleInput"
      @keyup.enter="handleEnter"
    >
      <template v-if="$slots.prefix" #prefix>
        <slot name="prefix" />
      </template>
    </el-input>
  </div>
</template>

<script setup>
/**
 * 关键词搜索组件
 *
 * 功能说明：
 * - 提供带搜索图标的关键词输入框
 * - 支持防抖搜索（默认 300ms）
 * - 支持 v-model 绑定
 * - 支持黑夜模式
 *
 * 使用方式：
 * ```vue
 * <KeywordSearch v-model="keyword" />
 * <KeywordSearch v-model="kw" width="240px" :debounce="500" @search="onSearch" />
 * ```
 */

import { computed } from 'vue'
import { Search } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  // v-model 绑定值
  modelValue: {
    type: String,
    default: '',
  },
  // 输入框宽度
  width: {
    type: String,
    default: '200px',
  },
  // 是否显示标签
  showLabel: {
    type: Boolean,
    default: false,
  },
  // 标签文本
  label: {
    type: String,
    default: '关键词',
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请输入关键词',
  },
  // 是否自动宽度（根据 placeholder 长度撑开）
  autoWidth: {
    type: Boolean,
    default: false,
  },
  // 是否可清除
  clearable: {
    type: Boolean,
    default: true,
  },
  // 防抖延迟（毫秒），0 表示不防抖
  debounce: {
    type: Number,
    default: 300,
  },
  // 自定义类名
  inputClass: {
    type: String,
    default: '',
  },
})

// Emits
const emit = defineEmits(['update:modelValue', 'search', 'change'])

// v-model 支持
const value = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

// 计算输入框样式
const inputStyle = computed(() => {
  if (props.autoWidth) {
    // 自动宽度：根据 placeholder 长度计算最小宽度
    const minWidth = Math.max(props.placeholder.length * 14 + 48, 140)
    return { width: `${minWidth}px`, 'max-width': '320px' }
  }
  return { width: props.width }
})

// 防抖定时器
let debounceTimer = null

// 输入处理（带防抖）
const handleInput = (val) => {
  emit('update:modelValue', val)

  if (props.debounce > 0) {
    if (debounceTimer) {
      clearTimeout(debounceTimer)
    }
    debounceTimer = setTimeout(() => {
      emit('search', val)
      emit('change', val)
    }, props.debounce)
  } else {
    emit('search', val)
    emit('change', val)
  }
}

// 回车搜索
const handleEnter = (val) => {
  if (debounceTimer) {
    clearTimeout(debounceTimer)
  }
  emit('search', val)
  emit('change', val)
}
</script>

<style lang="scss" scoped>
.keyword-search {
  display: flex;
  align-items: center;
  gap: 8px;

  .filter-label {
    font-size: 14px;
    color: var(--text-regular);
    white-space: nowrap;
  }

  .search-input {
    width: v-bind(width);
  }

  :deep(.el-input__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;
    min-height: 32px;
    font-size: 14px;

    &:focus-within {
      box-shadow: 0 0 0 3px var(--admin-primary-light) !important;
      border-color: var(--admin-primary) !important;
    }
  }

  :deep(.el-input__inner) {
    &::placeholder {
      color: var(--text-muted);
    }
  }

  :deep(.el-input__prefix) {
    color: var(--text-muted);
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .keyword-search {
    width: 100%;

    .search-input {
      width: 100% !important;
    }
  }
}
</style>
