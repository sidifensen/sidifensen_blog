<template>
  <div class="time-range-picker">
    <el-date-picker
      v-model="localStartTime"
      type="datetime"
      placeholder="开始时间"
      size="small"
      class="time-input"
      format="YYYY-MM-DD HH:mm:ss"
      value-format="YYYY-MM-DD HH:mm:ss"
      clearable
      @change="handleChange"
    />
    <span class="separator">至</span>
    <el-date-picker
      v-model="localEndTime"
      type="datetime"
      placeholder="结束时间"
      size="small"
      class="time-input"
      format="YYYY-MM-DD HH:mm:ss"
      value-format="YYYY-MM-DD HH:mm:ss"
      clearable
      @change="handleChange"
    />
  </div>
</template>

<script setup>
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

<style lang="scss" scoped>
.time-range-picker {
  display: inline-flex;
  align-items: center;
  gap: 6px;

  .time-input {
    width: 160px;
    border-radius: 6px;
  }

  .separator {
    color: var(--text-muted);
    font-size: 13px;
  }
}
</style>
