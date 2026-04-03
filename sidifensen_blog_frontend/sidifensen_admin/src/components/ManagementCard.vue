<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <div class="card-header-top">
          <h2 class="card-title" :style="{ '--title-color': titleColor }">{{ title }}</h2>
          <div class="card-actions">
            <slot name="header-actions" />
          </div>
        </div>

        <!-- 第二行筛选区域（可选） -->
        <div v-if="$slots['second-filters'] || showTimeFilter" class="card-second">
          <slot name="second-filters" />
          <el-date-picker
            v-if="showTimeFilter"
            v-model="startTime"
            type="datetime"
            placeholder="开始时间"
            size="small"
            class="time-input"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            clearable
            @change="handleTimeChange"
          />
          <el-date-picker
            v-if="showTimeFilter"
            v-model="endTime"
            type="datetime"
            placeholder="结束时间"
            size="small"
            class="time-input"
            format="YYYY-MM-DD HH:mm:ss"
            value-format="YYYY-MM-DD HH:mm:ss"
            clearable
            @change="handleTimeChange"
          />
        </div>

        <!-- 第三行批量操作区域（可选） -->
        <div v-if="$slots['batch-actions']" class="card-third">
          <slot name="batch-actions" />
        </div>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <slot name="table-view" />
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <slot name="card-view" />
      </div>

      <!-- 分页（可选） -->
      <Pagination
        v-if="showPagination"
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 额外插槽（对话框等） -->
    <slot />
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from "vue";
import Pagination from "./Pagination.vue";

// Props
const props = defineProps({
  title: {
    type: String,
    default: "管理页面"
  },
  titleColor: {
    type: String,
    default: "#42b983"
  },
  showTimeFilter: {
    type: Boolean,
    default: false
  },
  showPagination: {
    type: Boolean,
    default: true
  },
  // 分页相关
  modelCurrentPage: {
    type: Number,
    default: 1
  },
  modelPageSize: {
    type: Number,
    default: 10
  },
  total: {
    type: Number,
    default: 0
  }
});

// Emits
const emit = defineEmits([
  "update:modelCurrentPage",
  "update:modelPageSize",
  "search",
  "time-change",
  "resize"
]);

// 响应式
const isMobileView = ref(false);

// 分页
const currentPage = ref(props.modelCurrentPage);
const pageSize = ref(props.modelPageSize);
const startTime = ref("");
const endTime = ref("");

// 监听 props 变化
watch(() => props.modelCurrentPage, (val) => {
  currentPage.value = val;
});

watch(() => props.modelPageSize, (val) => {
  pageSize.value = val;
});

// 监听分页变化
watch(currentPage, (val) => {
  emit("update:modelCurrentPage", val);
});

watch(pageSize, (val) => {
  emit("update:modelPageSize", val);
});

// 窗口大小变化处理
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
  emit("resize", isMobileView.value);
};

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
  emit("search");
};

const handleCurrentChange = (current) => {
  currentPage.value = current;
  emit("search");
};

// 时间筛选变化
const handleTimeChange = () => {
  emit("time-change", {
    startTime: startTime.value,
    endTime: endTime.value
  });
};

// 公开方法
const resetPagination = () => {
  currentPage.value = 1;
};

// 暴露给父组件
defineExpose({
  resetPagination,
  getCurrentPage: () => currentPage.value,
  getPageSize: () => pageSize.value,
  getTimeRange: () => ({
    startTime: startTime.value,
    endTime: endTime.value
  })
});

// 初始化
onMounted(() => {
  handleResize();
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  .card {
    height: 100%;
    padding: 20px;
    overflow: hidden;
    display: flex;
    flex-direction: column;
    border-radius: 12px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    .card-header {
      padding: 10px 10px 0 10px;

      .card-header-top {
        display: flex;
        justify-content: space-between;
        align-items: center;
      }

      .card-title {
        font-size: 20px;
        font-weight: 600;
        margin: 0;
        display: flex;
        align-items: center;

        &::before {
          content: "";
          display: inline-block;
          width: 4px;
          height: 20px;
          background-color: var(--title-color, #42b983);
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;
        gap: 10px;
      }
    }

    // 第二行筛选区域：支持多元素换行显示（筛选器、时间范围选择器等）
    .card-second {
      display: flex;
      justify-content: flex-start;
      gap: 10px;
      padding: 10px;

      .time-input {
        width: 160px;
        border-radius: 8px;

        :deep(.el-input__wrapper) {
          border-radius: 8px;
          transition: all 0.3s ease;

          &:focus-within {
            box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
            border-color: #42b983;
          }
        }
      }
    }

    // 第三行批量操作区域：支持多按钮换行显示
    .card-third {
      display: flex;
      flex-wrap: wrap; // 内容超出一行时自动换行
      row-gap: 10px;
      column-gap: 10px;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
    }

    .desktop-view {
      flex: 1;
      display: flex;
      flex-direction: column;
      padding-bottom: 60px;
    }

    .mobile-view {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      padding-bottom: 60px;
      overflow-y: auto;
    }
  }
}

// 响应式设计
@media screen and (max-width: 768px) {
  .management-container {
    .card {
      padding: 2px;

      .card-header {
        padding: 6px;
        flex-direction: column;
        align-items: flex-start;
        gap: 12px;

        .card-header-top {
          flex-direction: column;
          align-items: flex-start;
          width: 100%;
          gap: 12px;
        }

        .card-title {
          font-size: 16px;
        }

        .card-actions {
          width: 100%;
          flex-direction: column;

          :deep(.el-button),
          :deep(.el-input),
          :deep(.el-select) {
            width: 100%;
          }
        }
      }

      .card-second {
        padding: 8px;
        // 移动端默认纵向排列，已通过 flex-wrap: wrap 自动处理

        .time-input {
          width: 100%;
        }
      }

      .card-third {
        padding: 8px;
        // 移动端按钮全宽，已通过 flex-wrap: wrap 自动处理

        :deep(.el-button) {
          width: 100%;
        }
      }
    }
  }
}
</style>
