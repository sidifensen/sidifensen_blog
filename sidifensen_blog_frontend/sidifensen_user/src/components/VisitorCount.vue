<template>
  <div class="visitor-count">
    <div class="count-container">
      <el-icon class="count-icon"><View /></el-icon>
      <div class="count-info">
        <span class="count-label">今日访问量</span>
        <span class="count-number">{{ visitorCount }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { View } from "@element-plus/icons-vue";
import { getTodayVisitorCount } from "@/api/visitor";

// 响应式数据
const visitorCount = ref(0);

// 获取今日访问量
const fetchVisitorCount = async () => {
  try {
    const res = await getTodayVisitorCount();
    visitorCount.value = res.data.data || 0;
  } catch (error) {
    console.error("获取今日访问量失败:", error);
    // 失败时显示 0
    visitorCount.value = 0;
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchVisitorCount();
});
</script>

<style lang="scss" scoped>
// 访客统计容器
.visitor-count {
  padding: 12px 16px;
  background: var(--el-bg-color-page);
  border-radius: 8px;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 8px var(--el-border-color-light);

  // 统计内容容器
  .count-container {
    display: flex;
    align-items: center;
    gap: 12px;

    // 图标样式
    .count-icon {
      font-size: 32px;
      color: var(--el-color-primary);
    }

    // 统计信息
    .count-info {
      display: flex;
      flex-direction: column;
      gap: 4px;

      // 标签样式
      .count-label {
        font-size: 14px;
        color: var(--el-text-color-regular);
      }

      // 数字样式
      .count-number {
        font-size: 24px;
        font-weight: 700;
        color: var(--el-text-color-primary);
      }
    }
  }
}
</style>
