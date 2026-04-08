<template>
  <div class="management-container">
    <div class="card">
      <!-- 标题行 -->
      <div class="card-title-row">
        <h2 class="card-title" :style="{ '--title-color': titleColor }">{{ title }}</h2>
      </div>

      <!-- 筛选区 -->
      <div class="card-filters">
        <slot name="filters" />
        <TimeRangePicker v-if="showTimeFilter" v-model:start-time="startTime" v-model:end-time="endTime" @change="handleTimeChange" />
      </div>

      <!-- 批量操作区（可选） -->
      <div v-if="$slots['batch-actions']" class="card-batch">
        <slot name="batch-actions" />
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
      <Pagination v-if="showPagination" v-model:current-page="currentPage" v-model:page-size="pageSize" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </div>

    <!-- 额外插槽（对话框等） -->
    <slot />
  </div>
</template>

<script setup>
/**
 * 管理卡片组件
 *
 * 功能说明：
 * - 统一管理页面布局：标题、筛选区、表格/卡片视图、分页
 * - 支持响应式布局（桌面端表格，移动端卡片）
 * - 可选时间范围筛选器（通过 showTimeFilter 控制，默认不显示）
 * - 可选分页（通过 showPagination 控制，默认显示）
 * - 支持批量操作区域
 *
 * 插槽说明：
 * - filters: 筛选器插槽（审核状态下拉、关键词搜索等）
 * - table-view: 桌面端表格内容
 * - card-view: 移动端卡片内容
 * - batch-actions: 批量操作按钮区
 * - table-view: 桌面端表格内容
 * - card-view: 移动端卡片内容
 * - batch-actions: 批量操作按钮区
 *
 * 使用方式：
 * ```vue
 * <ManagementCard title="用户管理" :showTimeFilter="true" :total="100" @search="fetchData">
 *   <template #filters>
 *     <ExamineStatusSelect v-model="searchExamineStatus" />
 *     <KeywordSearch v-model="searchKeyword" />
 *   </template>
 *   <template #table-view>
 *     <DataTable :data="tableData" />
 *   </template>
 * </ManagementCard>
 * ```
 */

import { ref, onMounted, onUnmounted, watch } from 'vue'
import Pagination from '@/components/data/Pagination.vue'
import TimeRangePicker from '@/components/search/TimeRangePicker.vue'

// Props
const props = defineProps({
  title: {
    type: String,
    default: '管理页面',
  },
  titleColor: {
    type: String,
    default: 'var(--admin-primary)',
  },
  showTimeFilter: {
    type: Boolean,
    default: false,
  },
  showPagination: {
    type: Boolean,
    default: true,
  },
  modelCurrentPage: {
    type: Number,
    default: 1,
  },
  modelPageSize: {
    type: Number,
    default: 10,
  },
  total: {
    type: Number,
    default: 0,
  },
})

// Emits
const emit = defineEmits(['update:modelCurrentPage', 'update:modelPageSize', 'search', 'time-change', 'resize'])

// 响应式
const isMobileView = ref(false)

// 分页
const currentPage = ref(props.modelCurrentPage)
const pageSize = ref(props.modelPageSize)

// 时间筛选
const startTime = ref('')
const endTime = ref('')

// 监听 props 并同步到内部状态
watch(
  () => [props.modelCurrentPage, props.modelPageSize],
  ([newPage, newSize]) => {
    currentPage.value = newPage
    pageSize.value = newSize
  },
)

// 监听内部状态变化并同步到 props
watch([currentPage, pageSize], ([newPage, newSize]) => {
  emit('update:modelCurrentPage', newPage)
  emit('update:modelPageSize', newSize)
})

// 窗口大小变化处理
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768
  emit('resize', isMobileView.value)
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  emit('search')
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  emit('search')
}

// 时间筛选变化
const handleTimeChange = () => {
  emit('time-change', { startTime: startTime.value, endTime: endTime.value })
}

// 公开方法
const resetPagination = () => {
  currentPage.value = 1
}

// 暴露给父组件
defineExpose({
  resetPagination,
  getCurrentPage: () => currentPage.value,
  getPageSize: () => pageSize.value,
  getTimeRange: () => ({ startTime: startTime.value, endTime: endTime.value }),
})

// 初始化
onMounted(() => {
  handleResize()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
})
</script>

<style lang="scss" scoped>
.management-container {
  height: 100%;
  position: relative;
}

.card {
  height: 100%;
  padding: 20px;
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  transition: box-shadow 0.3s ease;
  overflow: hidden;

  &:hover {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  }
}

.card-title-row {
  margin-bottom: 12px;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  display: flex;
  align-items: center;

  &::before {
    content: '';
    display: inline-block;
    width: 4px;
    height: 20px;
    background-color: var(--title-color, var(--admin-primary));
    border-radius: 2px;
    margin-right: 10px;
  }
}

.card-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  align-items: center;
  margin-bottom: 12px;
}

.card-batch {
  display: flex;
  flex-wrap: wrap;
  gap: 14px;
  padding: 12px 0;
  border-bottom: 1px solid var(--el-border-color);
  margin-bottom: 12px;
}

.desktop-view {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.mobile-view {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
  overflow-y: auto;
}

// 响应式设计
@media screen and (max-width: 768px) {
  .card {
    padding: 12px;
  }

  .card-title {
    font-size: 16px;
  }

  .card-filters {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .card-batch {
    flex-direction: column;
    gap: 10px;

    :deep(.el-button) {
      width: 100%;
    }
  }

  .mobile-view {
    margin-top: 12px;
  }
}
</style>
