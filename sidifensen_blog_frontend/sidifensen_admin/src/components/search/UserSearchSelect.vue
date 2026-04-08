<template>
  <div class="user-search-select">
    <span v-if="showLabel" class="filter-label">{{ label }}</span>
    <el-select
      :model-value="modelValue"
      :placeholder="placeholder"
      filterable
      remote
      reserve-keyword
      :remote-method="searchUsers"
      :loading="userLoading"
      clearable
      size="small"
      :class="['search-select', selectClass]"
      :style="{ width: width }"
      @update:model-value="handleUpdate"
      @change="handleChange"
    >
      <el-option v-for="user in filteredUserList" :key="user.id" :label="user.nickname || user.username" :value="user.id" />
    </el-select>
  </div>
</template>

<script setup>
/**
 * 用户远程搜索下拉组件
 *
 * 功能说明：
 * - 提供用户远程搜索下拉选择
 * - 支持 v-model 绑定
 * - 支持黑夜模式
 *
 * 使用方式：
 * ```vue
 * <UserSearchSelect v-model="userId" />
 * <UserSearchSelect v-model="userId" width="160px" show-label placeholder="选择用户" />
 * ```
 */

import { computed } from 'vue'
import { useUserSearch } from '@/utils/userSearch'

// Props
const props = defineProps({
  // v-model 绑定值
  modelValue: {
    type: [String, Number],
    default: '',
  },
  // 选择框宽度
  width: {
    type: String,
    default: '180px',
  },
  // 是否显示标签
  showLabel: {
    type: Boolean,
    default: true,
  },
  // 标签文本
  label: {
    type: String,
    default: '用户',
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请搜索用户',
  },
  // 自定义类名
  selectClass: {
    type: String,
    default: '',
  },
})

// Emits
const emit = defineEmits(['update:modelValue', 'change'])

// 用户搜索
const { filteredUserList, userLoading, searchUsers } = useUserSearch()

// v-model 支持
const value = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

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
.user-search-select {
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
    background-color: var(--bg-input);

    &:focus-within {
      box-shadow: 0 0 0 3px var(--admin-primary-light) !important;
      border-color: var(--admin-primary) !important;
    }
  }

  :deep(.el-select__placeholder) {
    color: var(--text-placeholder, #94a3b8);
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .user-search-select {
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
