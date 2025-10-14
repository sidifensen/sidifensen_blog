<template>
  <div class="pagination-container">
    <el-pagination v-model:current-page="currentPageValue" v-model:page-size="pageSizeValue" :page-sizes="pageSizes" :layout="layout" :total="total" :pager-count="pagerCount" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
  </div>
</template>

<script setup>
import { computed, ref, onMounted, onUnmounted } from "vue";

// Props 定义
const props = defineProps({
  // 当前页码
  currentPage: {
    type: Number,
    required: true,
    default: 1,
  },
  // 每页显示条数
  pageSize: {
    type: Number,
    required: true,
    default: 10,
  },
  // 总条数
  total: {
    type: Number,
    required: true,
    default: 0,
  },
  // 每页显示个数选择器的选项设置
  pageSizes: {
    type: Array,
    default: () => [10, 20, 50, 100],
  },
  // 组件布局
  layout: {
    type: String,
    default: "prev, pager, next, jumper",
  },
});

// 事件定义
const emit = defineEmits(["update:currentPage", "update:pageSize", "size-change", "current-change"]);

// 双向绑定当前页码
const currentPageValue = computed({
  get: () => props.currentPage,
  set: (value) => emit("update:currentPage", value),
});

// 双向绑定每页显示条数
const pageSizeValue = computed({
  get: () => props.pageSize,
  set: (value) => emit("update:pageSize", value),
});

// 处理每页条数变化
const handleSizeChange = (size) => {
  emit("size-change", size);
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  emit("current-change", current);
};

// 移动端检测
const isMobile = ref(false);

// 根据屏幕宽度动态设置页码按钮数量
const pagerCount = computed(() => {
  return isMobile.value ? 3 : 7; // 移动端显示5个，桌面端显示7个
});

// 监听窗口大小变化
const handleResize = () => {
  isMobile.value = window.innerWidth <= 768;
};

// 组件挂载时初始化
onMounted(() => {
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 分页容器
.pagination-container {
  display: flex;
  justify-content: flex-end;
  padding: 10px;
  background-color: var(--el-bg-color);
  border-radius: 0 0 12px 12px;
  z-index: 10;
  width: 100%;
  box-sizing: border-box;
  position: absolute;
  bottom: 0;
  left: 0;
}

// 响应式设计
@media screen and (max-width: 768px) {
  .pagination-container {
    padding: 4px;
  }
}
</style>
