<template>
  <div class="time-range-picker">
    <span v-if="showLabel" class="filter-label">时间</span>
    <div class="time-row">
      <el-date-picker
        v-model="localStartTime"
        type="datetime"
        placeholder="开始时间"
        size="small"
        class="time-picker-input"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
        clearable
        @change="handleChange"
      />
    </div>
    <span class="separator">至</span>
    <div class="time-row">
      <el-date-picker
        v-model="localEndTime"
        type="datetime"
        placeholder="结束时间"
        size="small"
        class="time-picker-input"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
        clearable
        @change="handleChange"
      />
    </div>
  </div>
</template>

<script setup>
/**
 * TimeRangePicker 时间范围选择器组件
 *
 * 功能说明：
 * - 开始时间和结束时间的日期时间选择器组合
 * - 支持 v-model:startTime 和 v-model:endTime 双向绑定
 * - 支持标签显示控制
 * - 响应式布局：移动端变为网格布局
 *
 * 使用方式：
 * ```vue
 * <TimeRangePicker v-model:startTime="startTime" v-model:endTime="endTime" @change="handleChange" />
 * ```
 */

import { ref, watch } from 'vue'

const props = defineProps({
  startTime: {
    type: String,
    default: '',
  },
  endTime: {
    type: String,
    default: '',
  },
  showLabel: {
    type: Boolean,
    default: true,
  },
})

const emit = defineEmits(['update:startTime', 'update:endTime', 'change'])

const localStartTime = ref(props.startTime)
const localEndTime = ref(props.endTime)

// 监听 props 变化
watch(
  () => props.startTime,
  (val) => {
    localStartTime.value = val
  },
)

watch(
  () => props.endTime,
  (val) => {
    localEndTime.value = val
  },
)

const handleChange = () => {
  emit('update:startTime', localStartTime.value)
  emit('update:endTime', localEndTime.value)
  emit('change', {
    startTime: localStartTime.value,
    endTime: localEndTime.value,
  })
}

// 暴露方法
defineExpose({
  reset: () => {
    localStartTime.value = ''
    localEndTime.value = ''
  },
})
</script>

<style lang="scss">
.time-range-picker {
  display: inline-flex;
  align-items: center;
  gap: 8px;

  .filter-label {
    font-size: 14px;
    color: var(--text-regular);
    white-space: nowrap;
  }

  .time-picker-input {
    width: 160px;
    height: 32px;

    .el-input__wrapper {
      border-radius: 8px;
      min-height: 32px;
      font-size: 14px;
      background-color: var(--bg-input);

      &:focus-within {
        box-shadow: 0 0 0 3px var(--admin-primary-light);
        border-color: var(--admin-primary);
      }
    }
  }

  .time-row {
    display: inline-block;
  }

  .separator {
    color: var(--text-muted);
    font-size: 13px;
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .time-range-picker {
    display: grid;
    grid-template-columns: 56px 1fr;
    grid-template-rows: auto auto;
    gap: 8px;
    width: 100%;

    .filter-label {
      grid-column: 1;
      grid-row: 1;
      text-align: right;
      line-height: 32px;
      font-size: 14px;
      color: var(--text-regular);
    }

    .time-row {
      grid-column: 2;
      width: 100%;

      &:first-child {
        grid-row: 1;
      }

      &:last-child {
        grid-row: 2;
      }
    }

    .time-picker-input {
      width: 100%;
    }

    .separator {
      grid-column: 1;
      grid-row: 2;
      text-align: right;
      line-height: 32px;
      color: var(--text-muted);
      font-size: 13px;
    }
  }
}
</style>
