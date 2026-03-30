<template>
  <div class="status-badge">
    <!-- 通用状态标签 -->
    <div
      v-if="type === 'custom'"
      class="badge"
      :class="customClass"
      :style="customStyle"
    >
      {{ text }}
    </div>

    <!-- 审核状态（待审核/已审核/未通过） -->
    <div
      v-else-if="type === 'examine'"
      class="badge"
      :class="examineClass"
    >
      {{ examineText }}
    </div>

    <!-- 通用状态（正常/禁用） -->
    <div
      v-else-if="type === 'status'"
      class="badge"
      :class="statusClass"
    >
      {{ statusText }}
    </div>

    <!-- 类型标签（原创/转载） -->
    <el-tag
      v-else-if="type === 'reprint'"
      :type="reprintType"
      size="small"
    >
      {{ reprintText }}
    </el-tag>

    <!-- 默认显示 -->
    <div v-else class="badge default-badge">
      {{ text }}
    </div>
  </div>
</template>

<script setup>
import { computed } from "vue";

// Props
const props = defineProps({
  // 状态值
  value: {
    type: [Number, String],
    default: 0
  },
  // 类型：custom | examine | status | reprint
  type: {
    type: String,
    default: "custom"
  },
  // 文本（用于自定义类型）
  text: {
    type: String,
    default: ""
  },
  // 自定义类名
  customClass: {
    type: String,
    default: ""
  },
  // 自定义样式
  customStyle: {
    type: Object,
    default: () => ({})
  }
});

// 审核状态类名
const examineClass = computed(() => {
  const classMap = {
    0: "status-unaudited",
    1: "status-audited",
    2: "status-rejected"
  };
  return classMap[props.value] || "status-default";
});

// 审核状态文本
const examineText = computed(() => {
  const textMap = {
    0: "待审核",
    1: "已审核",
    2: "未通过"
  };
  return textMap[props.value] || "未知";
});

// 通用状态类名
const statusClass = computed(() => {
  const classMap = {
    0: "status-normal",
    1: "status-disabled"
  };
  return classMap[props.value] || "status-default";
});

// 通用状态文本
const statusText = computed(() => {
  const textMap = {
    0: "正常",
    1: "禁用"
  };
  return textMap[props.value] || "未知";
});

// 转载类型
const reprintType = computed(() => {
  return props.value === 0 ? "success" : "warning";
});

// 转载文本
const reprintText = computed(() => {
  return props.value === 0 ? "原创" : "转载";
});
</script>

<style lang="scss" scoped>
.status-badge {
  display: inline-block;

  .badge {
    display: inline-block;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 12px;
    font-weight: 500;
    white-space: nowrap;
  }

  // 待审核
  .status-unaudited {
    background-color: #fff1f0;
    color: #f56c6c;
  }

  // 已审核/正常
  .status-audited,
  .status-normal {
    background-color: #f0f9eb;
    color: #67c23a;
  }

  // 未通过/禁用
  .status-rejected,
  .status-disabled {
    background-color: #fdf6ec;
    color: #e6a23c;
  }

  // 默认
  .status-default,
  .default-badge {
    background-color: #f4f4f5;
    color: #909399;
  }
}
</style>
