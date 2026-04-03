<template>
  <div class="admin-search-filters">
    <!-- 审核状态下拉 -->
    <el-select
      v-if="showExamineStatus"
      v-model="examineStatus"
      placeholder="审核状态"
      filterable
      clearable
      size="small"
      class="search-select"
      @change="handleChange"
    >
      <el-option label="全部" value="" />
      <el-option label="待审核" value="0" />
      <el-option label="审核通过" value="1" />
      <el-option label="审核不通过" value="2" />
    </el-select>

    <!-- 用户搜索下拉 -->
    <el-select
      v-if="showUserSearch"
      v-model="selectedUserId"
      placeholder="输入用户名搜索"
      filterable
      remote
      reserve-keyword
      :remote-method="searchUsers"
      :loading="userLoading"
      clearable
      size="small"
      class="search-select"
      @change="handleChange"
    >
      <el-option
        v-for="user in filteredUserList"
        :key="user.id"
        :label="user.nickname || user.username"
        :value="user.id"
      />
    </el-select>

    <!-- 关键词搜索 -->
    <el-input
      v-if="showKeyword"
      v-model="keyword"
      :placeholder="keywordPlaceholder"
      :prefix-icon="Search"
      size="small"
      class="search-input"
      clearable
      @input="handleChange"
    />
  </div>
</template>

<script setup>
import { Search } from "@element-plus/icons-vue";
import { useUserSearch } from "@/utils/userSearch";

// Props
const props = defineProps({
  // 是否显示审核状态下拉
  showExamineStatus: {
    type: Boolean,
    default: true
  },
  // 是否显示用户搜索下拉
  showUserSearch: {
    type: Boolean,
    default: true
  },
  // 是否显示关键词搜索
  showKeyword: {
    type: Boolean,
    default: true
  },
  // 关键词占位符
  keywordPlaceholder: {
    type: String,
    default: "搜索关键词"
  }
});

// Emits
const emit = defineEmits([
  "update:examineStatus",
  "update:userId",
  "update:keyword",
  "change"
]);

// 用户搜索
const { filteredUserList, userLoading, searchUsers } = useUserSearch();

// 内部状态
const examineStatus = defineModel("examineStatus", { default: "" });
const selectedUserId = defineModel("userId", { default: "" });
const keyword = defineModel("keyword", { default: "" });

// 变化处理
const handleChange = () => {
  emit("update:examineStatus", examineStatus.value);
  emit("update:userId", selectedUserId.value);
  emit("update:keyword", keyword.value);
  emit("change");
};

// 重置
const reset = () => {
  examineStatus.value = "";
  selectedUserId.value = "";
  keyword.value = "";
};

// 暴露方法
defineExpose({
  reset
});
</script>

<style lang="scss" scoped>
.admin-search-filters {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;

  .search-select {
    width: 140px;
  }

  .search-input {
    width: 240px;
  }

  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    border-radius: 8px;
    transition: all 0.3s ease;

    &:focus-within {
      box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
      border-color: #42b983;
    }
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .admin-search-filters {
    flex-direction: column;
    width: 100%;

    .search-select,
    .search-input {
      width: 100% !important;
    }
  }
}
</style>
