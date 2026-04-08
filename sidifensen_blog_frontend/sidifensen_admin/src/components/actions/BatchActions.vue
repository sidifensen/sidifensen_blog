<template>
  <div class="batch-actions">
    <!-- 批量审核 -->
    <el-button v-if="showBatchAudit" type="primary" plain round :disabled="selectedCount === 0" :loading="batchAuditLoading" :icon="Check" @click="handleBatchAudit">
      批量审核 ({{ selectedCount }})
    </el-button>

    <!-- 批量拒绝 -->
    <el-button v-if="showBatchReject" type="warning" plain round :disabled="selectedCount === 0" :loading="batchRejectLoading" :icon="Close" @click="handleBatchReject">
      批量拒绝 ({{ selectedCount }})
    </el-button>

    <!-- 批量删除 -->
    <el-button v-if="showBatchDelete" type="danger" plain round :disabled="selectedCount === 0" :loading="batchDeleteLoading" :icon="Delete" @click="handleBatchDelete">
      批量删除 ({{ selectedCount }})
    </el-button>

    <!-- 其他操作插槽 -->
    <slot />
  </div>
</template>

<script setup>
import { Check, Close, Delete } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  // 选中数量
  selectedCount: {
    type: Number,
    default: 0,
  },
  // 显示控制
  showBatchAudit: {
    type: Boolean,
    default: true,
  },
  showBatchReject: {
    type: Boolean,
    default: true,
  },
  showBatchDelete: {
    type: Boolean,
    default: true,
  },
  // 加载状态
  batchAuditLoading: {
    type: Boolean,
    default: false,
  },
  batchRejectLoading: {
    type: Boolean,
    default: false,
  },
  batchDeleteLoading: {
    type: Boolean,
    default: false,
  },
})

// Emits
const emit = defineEmits(['batch-audit', 'batch-reject', 'batch-delete'])

// 处理批量审核
const handleBatchAudit = () => {
  if (props.selectedCount === 0) return
  emit('batch-audit')
}

// 处理批量拒绝
const handleBatchReject = () => {
  if (props.selectedCount === 0) return
  emit('batch-reject')
}

// 处理批量删除
const handleBatchDelete = () => {
  if (props.selectedCount === 0) return
  emit('batch-delete')
}
</script>

<style lang="scss" scoped>
.batch-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;

  :deep(.el-button) {
    margin-left: 0;
    border-radius: 20px;
    transition: all 0.3s ease;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .batch-actions {
    width: 100%;

    :deep(.el-button) {
      flex: 1;
      min-width: 100px;
    }
  }
}
</style>
