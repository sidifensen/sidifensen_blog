<template>
  <div class="search-buttons">
    <el-button class="btn-search" :icon="Search" @click="handleSearch"> 搜索 </el-button>
    <el-button class="btn-reset" :icon="Refresh" @click="handleReset"> 重置 </el-button>
  </div>
</template>

<script setup>
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
  display: inline-flex;
  align-items: center;
  gap: 0;

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
    background-color: #f1f5f9;
    border-color: #e2e8f0;
    color: #475569;

    &:hover {
      background-color: #e0e7ff;
      border-color: #818cf8;
      color: #4f46e5;
    }
  }

  // 重置按钮 - 轻盈透明
  .btn-reset {
    background-color: transparent;
    border-color: #f1f5f9;
    color: #94a3b8;

    &:hover {
      background-color: #f8fafc;
      border-color: #e2e8f0;
      color: #64748b;
    }
  }
}
</style>
