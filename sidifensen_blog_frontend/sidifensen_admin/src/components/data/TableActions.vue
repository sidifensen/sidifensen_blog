<template>
  <div class="table-actions">
    <!-- 查看按钮 -->
    <el-button v-if="showView" type="info" size="small" :icon="View" class="action-btn view-btn" @click="handleAction('view')">
      {{ viewText }}
    </el-button>

    <!-- 详情按钮 -->
    <el-button v-if="showDetail" type="default" size="small" :icon="InfoFilled" class="action-btn detail-btn" @click="handleAction('detail')">
      {{ detailText }}
    </el-button>

    <!-- 编辑按钮 -->
    <el-button v-if="showEdit" type="primary" size="small" :icon="Edit" class="action-btn edit-btn" @click="handleAction('edit')">
      {{ editText }}
    </el-button>

    <!-- 审核按钮 -->
    <el-button v-if="showAudit" type="primary" size="small" :icon="Check" class="action-btn audit-btn" @click="handleAction('audit')">
      {{ auditText }}
    </el-button>

    <!-- 拒绝按钮 -->
    <el-button v-if="showReject" type="warning" size="small" :icon="Close" class="action-btn reject-btn" @click="handleAction('reject')">
      {{ rejectText }}
    </el-button>

    <!-- 删除按钮 -->
    <el-button v-if="showDelete" type="danger" size="small" :icon="Delete" class="action-btn delete-btn" @click="handleAction('delete')">
      {{ deleteText }}
    </el-button>

    <!-- 其他操作插槽 -->
    <slot />
  </div>
</template>

<script setup>
/**
 * TableActions 表格操作按钮组件
 *
 * 功能说明：
 * - 展示表格行操作按钮（查看、详情、编辑、审核、拒绝、删除）
 * - 通过 props 控制各按钮的显示/隐藏
 * - 支持自定义按钮文本
 * - 支持禁用状态
 *
 * 使用方式：
 * ```vue
 * <TableActions :show-edit="true" :show-delete="true" @edit="handleEdit" @delete="handleDelete" />
 * ```
 */

import { View, Edit, Check, Close, Delete, InfoFilled } from '@element-plus/icons-vue'

// Props
const props = defineProps({
  // 按钮显示控制
  showView: {
    type: Boolean,
    default: false,
  },
  showDetail: {
    type: Boolean,
    default: false,
  },
  showEdit: {
    type: Boolean,
    default: false,
  },
  showAudit: {
    type: Boolean,
    default: false,
  },
  showReject: {
    type: Boolean,
    default: false,
  },
  showDelete: {
    type: Boolean,
    default: false,
  },
  // 按钮文本
  viewText: {
    type: String,
    default: '查看',
  },
  detailText: {
    type: String,
    default: '详情',
  },
  editText: {
    type: String,
    default: '编辑',
  },
  auditText: {
    type: String,
    default: '审核',
  },
  rejectText: {
    type: String,
    default: '拒绝',
  },
  deleteText: {
    type: String,
    default: '删除',
  },
  // 禁用状态
  disabled: {
    type: Boolean,
    default: false,
  },
})

// Emits
const emit = defineEmits(['view', 'detail', 'edit', 'audit', 'reject', 'delete', 'action'])

// 处理按钮点击
const handleAction = (action) => {
  if (props.disabled) return
  emit(action)
  emit('action', action)
}
</script>

<style lang="scss" scoped>
.table-actions {
  display: flex;
  align-items: center;
  justify-content: center;
  flex-wrap: wrap;
  gap: 8px;
  height: 100%;
  min-height: 30px;

  .action-btn {
    margin-left: 0 !important;
    border-radius: 6px;
    font-weight: 600;
    transition: all 0.3s ease;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
    }
  }

  // 查看按钮
  .view-btn {
    background-color: var(--action-view-bg);
    color: var(--action-view-color);
    border-color: var(--action-view-border);

    &:hover:not(:disabled) {
      background-color: var(--action-view-hover-bg);
      border-color: var(--action-view-hover-border);
    }
  }

  // 详情按钮
  .detail-btn {
    background-color: var(--el-fill-color-light);
    color: var(--el-text-color-regular);
    border-color: var(--el-border-color);

    &:hover:not(:disabled) {
      background-color: var(--el-fill-color);
      border-color: var(--el-color-primary);
      color: var(--el-color-primary);
    }
  }

  // 编辑按钮
  .edit-btn {
    background-color: var(--el-fill-color-light);
    color: var(--text-regular);
    border-color: var(--el-border-color);

    &:hover:not(:disabled) {
      background-color: var(--el-fill-color);
      border-color: var(--text-muted);
      color: var(--text-primary);
    }
  }

  // 审核按钮
  .audit-btn {
    background-color: var(--action-audit-bg);
    color: var(--action-audit-color);
    border-color: var(--action-audit-border);

    &:hover:not(:disabled) {
      background-color: var(--action-audit-hover-bg);
      border-color: var(--action-audit-hover-border);
      box-shadow: 0 2px 8px var(--action-audit-shadow);
    }
  }

  // 拒绝按钮
  .reject-btn {
    background-color: var(--action-reject-bg);
    color: var(--action-reject-color);
    border-color: var(--action-reject-border);

    &:hover:not(:disabled) {
      background-color: var(--action-reject-hover-bg);
      border-color: var(--action-reject-hover-border);
      box-shadow: 0 2px 8px var(--action-reject-shadow);
    }
  }

  // 删除按钮
  .delete-btn {
    background-color: var(--action-delete-bg);
    color: var(--action-delete-color);
    border-color: var(--action-delete-border);

    &:hover:not(:disabled) {
      background-color: var(--action-delete-hover-bg);
      border-color: var(--action-delete-hover-border);
      box-shadow: 0 2px 8px var(--action-delete-shadow);
    }
  }
}

// 移动端适配
@media screen and (max-width: 768px) {
  .table-actions {
    gap: 4px;

    .action-btn {
      font-size: 12px;
      padding: 6px 8px;
    }
  }
}

// 超小屏幕
@media screen and (max-width: 480px) {
  .table-actions {
    .action-btn {
      font-size: 11px;
      padding: 4px 6px;
    }
  }
}

// 深色模式适配
html.dark {
  .table-actions {
    // 查看按钮
    .view-btn {
      background-color: var(--action-view-bg-dark);
      color: var(--action-view-color-dark);
      border-color: var(--action-view-border-dark);

      &:hover:not(:disabled) {
        background-color: var(--action-view-hover-bg-dark);
        border-color: var(--action-view-hover-border-dark);
      }
    }

    // 详情按钮
    .detail-btn {
      background-color: var(--el-fill-color-dark);
      color: var(--el-text-color-regular);
      border-color: var(--el-border-color-dark);

      &:hover:not(:disabled) {
        background-color: var(--el-fill-color);
        border-color: var(--el-color-primary);
        color: var(--el-color-primary);
      }
    }

    // 编辑按钮
    .edit-btn {
      background-color: var(--el-fill-color-dark);
      color: var(--text-regular);
      border-color: var(--el-border-color-dark);

      &:hover:not(:disabled) {
        background-color: var(--el-fill-color);
        border-color: var(--text-muted);
        color: var(--text-primary);
      }
    }

    // 审核按钮
    .audit-btn {
      background-color: var(--action-audit-bg-dark);
      color: var(--action-audit-color-dark);
      border-color: var(--action-audit-border-dark);

      &:hover:not(:disabled) {
        background-color: var(--action-audit-hover-bg-dark);
        border-color: var(--action-audit-hover-border-dark);
        box-shadow: 0 2px 8px var(--action-audit-shadow-dark);
      }
    }

    // 拒绝按钮
    .reject-btn {
      background-color: var(--action-reject-bg-dark);
      color: var(--action-reject-color-dark);
      border-color: var(--action-reject-border-dark);

      &:hover:not(:disabled) {
        background-color: var(--action-reject-hover-bg-dark);
        border-color: var(--action-reject-hover-border-dark);
        box-shadow: 0 2px 8px var(--action-reject-shadow-dark);
      }
    }

    // 删除按钮
    .delete-btn {
      background-color: var(--action-delete-bg-dark);
      color: var(--action-delete-color-dark);
      border-color: var(--action-delete-border-dark);

      &:hover:not(:disabled) {
        background-color: var(--action-delete-hover-bg-dark);
        border-color: var(--action-delete-hover-border-dark);
        box-shadow: 0 2px 8px var(--action-delete-shadow-dark);
      }
    }
  }
}
</style>
