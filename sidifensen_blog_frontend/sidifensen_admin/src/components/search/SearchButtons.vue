<template>
  <div class="search-buttons">
    <el-button class="btn-search" :icon="Search" @click="handleSearch"> 搜索 </el-button>
    <el-button class="btn-search btn-reset" :icon="Refresh" @click="handleReset"> 重置 </el-button>
  </div>
</template>

<script setup>
/**
 * SearchButtons 搜索操作按钮组组件
 *
 * 功能说明：
 * - 包含搜索、重置两个操作按钮
 * - 搜索按钮触发 search 事件，重置按钮触发 reset 事件
 * - 按钮样式采用蓝灰色调，与整体风格统一
 *
 * 使用方式：
 * ```vue
 * <SearchButtons @search="handleSearch" @reset="handleReset" />
 * ```
 */

import { Search, Refresh } from '@element-plus/icons-vue'

const emit = defineEmits(['search', 'reset'])

const handleSearch = () => {
  emit('search')
}

const handleReset = () => {
  emit('reset')
}
</script>

<style lang="scss" scoped>
.search-buttons {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;

  :deep(.el-button) {
    height: 32px;
    padding: 0 16px;
    font-size: 14px;
    font-weight: 500;
    border-radius: 8px;
    transition: all 0.2s ease;
  }

  // 搜索按钮 - 蓝灰色调
  .btn-search {
    background-color: var(--btn-search-bg);
    border-color: var(--btn-search-border);
    color: var(--btn-search-color);

    &:hover {
      background-color: var(--btn-search-hover-bg);
      border-color: var(--btn-search-hover-border);
      color: var(--btn-search-hover-color);
    }
  }

  // 重置按钮 - 蓝绿色调
  .btn-reset {
    background-color: var(--btn-reset-bg);
    border-color: var(--btn-reset-border);
    color: var(--btn-reset-color);

    &:hover {
      background-color: var(--btn-reset-hover-bg);
      border-color: var(--btn-reset-hover-border);
      color: var(--btn-reset-hover-color);
    }
  }
}
</style>
