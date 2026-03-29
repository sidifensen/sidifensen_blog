<template>
  <div class="empty-state">
    <el-empty
      :image-size="imageSize"
      :description="displayDescription"
    >
      <slot />
    </el-empty>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { ElEmpty } from "element-plus";

// 定义组件props
const props = defineProps({
  // 空状态类型
  type: {
    type: String,
    default: "custom",
    validator: (value) =>
      ["article", "comment", "message", "search", "custom"].includes(value),
  },
  // 自定义描述文字
  description: {
    type: String,
    default: "",
  },
  // 图片大小
  imageSize: {
    type: Number,
    default: 100,
  },
});

// 类型对应的默认描述
const typeDescriptions = {
  article: "暂无文章",
  comment: "暂无评论",
  message: "暂无消息",
  search: "未找到搜索结果",
};

// 显示的描述文字
const displayDescription = computed(() => {
  if (props.description) {
    return props.description;
  }
  return typeDescriptions[props.type] || "";
});
</script>

<style lang="scss" scoped>
// 空状态容器
.empty-state {
  --empty-text-color: #606266;
  --empty-secondary-color: #909399;

  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;

  // 覆盖 ElEmpty 样式
  :deep(.el-empty__description) {
    color: var(--empty-text-color);
    font-size: 14px;
  }
}

// 黑夜模式适配
html.dark {
  .empty-state {
    --empty-text-color: #cbd5e1;
    --empty-secondary-color: #94a3b8;
  }
}
</style>
