<template>
  <div class="sidebar-card">
    <div class="sidebar-card-header">
      <h4 class="sidebar-card-title">
        <span v-if="icon" class="sidebar-card-icon">{{ icon }}</span>
        {{ title }}
      </h4>
      <p v-if="description" class="sidebar-card-description">{{ description }}</p>
    </div>
    <div class="sidebar-card-content">
      <slot></slot>
    </div>
    <div v-if="$slots.footer" class="sidebar-card-footer">
      <slot name="footer"></slot>
    </div>
  </div>
</template>

<script setup>
/**
 * 侧边栏卡片组件
 * @property {string} title - 卡片标题
 * @property {string} icon - emoji 图标，如 "🔥"
 * @property {string} description - 描述文字（可选）
 */
defineProps({
  title: {
    type: String,
    required: true,
  },
  icon: {
    type: String,
    default: "",
  },
  description: {
    type: String,
    default: "",
  },
});
</script>

<style lang="scss" scoped>
.sidebar-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 20px;
}

// 标题区域
.sidebar-card-header {
  margin-bottom: 16px;
}

.sidebar-card-title {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  display: flex;
  align-items: center;
  gap: 6px;
}

.sidebar-card-icon {
  font-size: 14px;
}

.sidebar-card-description {
  font-size: 12px;
  color: var(--text-muted);
  margin: 6px 0 0 0;
}

// 内容区域
.sidebar-card-content {
  // 内容插槽样式
}

// 底部区域
.sidebar-card-footer {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

// 黑夜模式
html.dark {
  .sidebar-card {
    // 深色模式使用 var(--bg-card) 和 var(--border) 自动适配
  }
}
</style>
