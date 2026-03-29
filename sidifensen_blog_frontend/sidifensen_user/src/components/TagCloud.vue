<template>
  <div class="tag-cloud">
    <span
      v-for="(tag, index) in displayTags"
      :key="index"
      class="tag-cloud-item"
      :class="{ 'clickable': clickable }"
      @click="handleTagClick(tag.name)"
    >
      {{ tag.name }}
    </span>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";

// Props 定义
const props = defineProps({
  // 标签列表
  tags: {
    type: Array,
    default: () => [],
    required: true,
  },
  // 最大显示数量，默认全部显示
  max: {
    type: Number,
    default: 0,
  },
  // 是否可点击
  clickable: {
    type: Boolean,
    default: true,
  },
});

// Emits 定义
const emit = defineEmits(["tag-click"]);

// 路由
const router = useRouter();

// 根据 max 计算实际显示的标签
const displayTags = computed(() => {
  if (props.max > 0 && props.tags.length > props.max) {
    return props.tags.slice(0, props.max);
  }
  return props.tags;
});

// 处理标签点击
const handleTagClick = (tagName) => {
  // 触发事件
  emit("tag-click", tagName);

  // 如果可点击，跳转到搜索页面
  if (props.clickable) {
    router.push({
      path: "/search",
      query: {
        keyword: tagName,
        type: "tag",
      },
    });
  }
};
</script>

<style lang="scss" scoped>
// ===== 标签云组件 - CSS 变量支持黑夜模式 =====
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-cloud-item {
  padding: 6px 14px;
  background: var(--bg-page);
  border: 1px solid var(--border);
  border-radius: 9999px;
  font-size: 0.8rem;
  color: var(--text-secondary);
  transition: all 0.2s ease;

  // 可点击样式
  &.clickable {
    cursor: pointer;

    &:hover {
      border-color: var(--accent);
      color: var(--accent);
    }
  }
}

// 黑夜模式覆盖
html.dark {
  .tag-cloud-item {
    background: var(--bg-page);
    color: var(--text-secondary);
  }
}
</style>
