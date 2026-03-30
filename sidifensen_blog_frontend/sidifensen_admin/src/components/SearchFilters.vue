<template>
  <div class="search-filters">
    <!-- 关键词搜索 -->
    <el-input
      v-if="showKeyword"
      v-model="keywordValue"
      :placeholder="keywordPlaceholder"
      :prefix-icon="Search"
      size="small"
      class="search-input keyword-input"
      clearable
      @input="handleKeywordInput"
    />

    <!-- 下拉选择器插槽 -->
    <slot />

    <!-- 时间范围选择 -->
    <template v-if="showTimeRange">
      <el-date-picker
        v-model="startTime"
        type="datetime"
        :placeholder="startTimePlaceholder"
        size="small"
        class="search-input time-input"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
        clearable
        @change="handleChange"
      />
      <el-date-picker
        v-model="endTime"
        type="datetime"
        :placeholder="endTimePlaceholder"
        size="small"
        class="search-input time-input"
        format="YYYY-MM-DD HH:mm:ss"
        value-format="YYYY-MM-DD HH:mm:ss"
        clearable
        @change="handleChange"
      />
    </template>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from "vue";
import { Search } from "@element-plus/icons-vue";

// Props
const props = defineProps({
  // 是否显示关键词搜索
  showKeyword: {
    type: Boolean,
    default: false
  },
  keywordPlaceholder: {
    type: String,
    default: "搜索关键词"
  },
  keywordWidth: {
    type: String,
    default: "240px"
  },
  // 是否显示时间范围
  showTimeRange: {
    type: Boolean,
    default: false
  },
  startTimePlaceholder: {
    type: String,
    default: "开始时间"
  },
  endTimePlaceholder: {
    type: String,
    default: "结束时间"
  },
  // 防抖延迟
  debounceDelay: {
    type: Number,
    default: 500
  }
});

// Emits
const emit = defineEmits([
  "update:keyword",
  "update:startTime",
  "update:endTime",
  "change"
]);

// 响应式数据
const keywordValue = ref("");
const startTime = ref("");
const endTime = ref("");
let debounceTimer = null;

// 关键词输入处理（防抖）
const handleKeywordInput = () => {
  if (debounceTimer) {
    clearTimeout(debounceTimer);
  }
  debounceTimer = setTimeout(() => {
    emit("update:keyword", keywordValue.value);
    emit("change");
  }, props.debounceDelay);
};

// 变化处理
const handleChange = () => {
  emit("update:startTime", startTime.value);
  emit("update:endTime", endTime.value);
  emit("change");
};

// 重置
const reset = () => {
  keywordValue.value = "";
  startTime.value = "";
  endTime.value = "";
  emit("update:keyword", "");
  emit("update:startTime", "");
  emit("update:endTime", "");
};

// 获取值
const getValues = () => ({
  keyword: keywordValue.value,
  startTime: startTime.value,
  endTime: endTime.value
});

// 暴露方法
defineExpose({
  reset,
  getValues
});

// 初始化
onMounted(() => {
  // 可以在这里从 url 或 store 恢复状态
});
</script>

<style lang="scss" scoped>
.search-filters {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;

  .search-input {
    border-radius: 8px;

    :deep(.el-input__wrapper) {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:focus-within {
        box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
        border-color: #42b983;
      }
    }

    :deep(.el-select__wrapper) {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:focus-within {
        box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
        border-color: #42b983;
      }
    }
  }

  .keyword-input {
    width: v-bind(keywordWidth);
  }

  .time-input {
    width: 160px;
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .search-filters {
    flex-direction: column;
    width: 100%;

    .search-input {
      width: 100% !important;
    }
  }
}
</style>
