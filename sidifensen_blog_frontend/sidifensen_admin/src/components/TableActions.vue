<template>
  <div class="table-actions">
    <!-- 查看按钮 -->
    <el-button
      v-if="showView"
      type="info"
      size="small"
      :icon="View"
      class="action-btn view-btn"
      @click="handleAction('view')"
    >
      {{ viewText }}
    </el-button>

    <!-- 编辑按钮 -->
    <el-button
      v-if="showEdit"
      type="primary"
      size="small"
      :icon="Edit"
      class="action-btn edit-btn"
      @click="handleAction('edit')"
    >
      {{ editText }}
    </el-button>

    <!-- 审核按钮 -->
    <el-button
      v-if="showAudit"
      type="primary"
      size="small"
      :icon="Check"
      class="action-btn audit-btn"
      @click="handleAction('audit')"
    >
      {{ auditText }}
    </el-button>

    <!-- 拒绝按钮 -->
    <el-button
      v-if="showReject"
      type="warning"
      size="small"
      :icon="Close"
      class="action-btn reject-btn"
      @click="handleAction('reject')"
    >
      {{ rejectText }}
    </el-button>

    <!-- 删除按钮 -->
    <el-button
      v-if="showDelete"
      type="danger"
      size="small"
      :icon="Delete"
      class="action-btn delete-btn"
      @click="handleAction('delete')"
    >
      {{ deleteText }}
    </el-button>

    <!-- 其他操作插槽 -->
    <slot />
  </div>
</template>

<script setup>
import { View, Edit, Check, Close, Delete } from "@element-plus/icons-vue";

// Props
const props = defineProps({
  // 按钮显示控制
  showView: {
    type: Boolean,
    default: false
  },
  showEdit: {
    type: Boolean,
    default: false
  },
  showAudit: {
    type: Boolean,
    default: false
  },
  showReject: {
    type: Boolean,
    default: false
  },
  showDelete: {
    type: Boolean,
    default: false
  },
  // 按钮文本
  viewText: {
    type: String,
    default: "查看"
  },
  editText: {
    type: String,
    default: "编辑"
  },
  auditText: {
    type: String,
    default: "审核"
  },
  rejectText: {
    type: String,
    default: "拒绝"
  },
  deleteText: {
    type: String,
    default: "删除"
  },
  // 禁用状态
  disabled: {
    type: Boolean,
    default: false
  }
});

// Emits
const emit = defineEmits(["view", "edit", "audit", "reject", "delete", "action"]);

// 处理按钮点击
const handleAction = (action) => {
  if (props.disabled) return;
  emit(action);
  emit("action", action);
};
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
    transition: all 0.3s ease;

    &:hover:not(:disabled) {
      transform: translateY(-2px);
    }
  }

  // 查看按钮
  .view-btn {
    background-color: #f0f9ff;
    color: #0369a1;
    border-color: #f0f9ff;

    &:hover:not(:disabled) {
      background-color: #dbeafe;
      border-color: #dbeafe;
      box-shadow: 0 2px 8px rgba(3, 105, 161, 0.3);
    }
  }

  // 编辑按钮
  .edit-btn {
    background-color: #e0f2fe;
    color: #0284c7;
    border-color: #e0f2fe;

    &:hover:not(:disabled) {
      background-color: #bae6fd;
      border-color: #bae6fd;
      box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
    }
  }

  // 审核按钮
  .audit-btn {
    background-color: #e0f2fe;
    color: #0284c7;
    border-color: #e0f2fe;

    &:hover:not(:disabled) {
      background-color: #bae6fd;
      border-color: #bae6fd;
      box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
    }
  }

  // 拒绝按钮
  .reject-btn {
    background-color: #fef3c7;
    color: #d97706;
    border-color: #fef3c7;

    &:hover:not(:disabled) {
      background-color: #fde68a;
      border-color: #fde68a;
      box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
    }
  }

  // 删除按钮
  .delete-btn {
    background-color: #fee2e2;
    color: #ef4444;
    border-color: #fee2e2;

    &:hover:not(:disabled) {
      background-color: #fecaca;
      border-color: #fecaca;
      box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
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
</style>
