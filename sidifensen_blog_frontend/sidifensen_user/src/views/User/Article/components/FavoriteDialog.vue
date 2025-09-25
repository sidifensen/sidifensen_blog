<template>
  <el-dialog v-model="visible" :title="isAddingFavorite ? '新增收藏夹' : '收藏到'" :width="dialogWidth" :before-close="handleClose" @closed="handleDialogClosed" class="favorite-dialog" top="7vh" :close-on-click-modal="false">
    <!-- 新增收藏夹表单 -->
    <div v-if="isAddingFavorite" class="add-favorite-form">
      <el-form :model="favoriteForm" :rules="favoriteRules" ref="favoriteFormRef" label-width="80px">
        <el-form-item label="收藏夹名" prop="name">
          <el-input v-model="favoriteForm.name" placeholder="请输入收藏夹名称" maxlength="20" show-word-limit />
        </el-form-item>
        <el-form-item label="公开设置" prop="showStatus">
          <el-radio-group v-model="favoriteForm.showStatus">
            <el-radio :value="0">公开</el-radio>
            <el-radio :value="1">私密</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
    </div>

    <!-- 收藏夹列表 -->
    <div v-else class="favorite-list-container">
      <!-- 新增收藏夹按钮 -->
      <div class="add-favorite-btn">
        <el-button type="primary" :icon="Plus" @click="showAddForm" plain> 新增收藏夹 </el-button>
      </div>

      <!-- 收藏夹列表 -->
      <div class="favorite-list" v-loading="loading">
        <div v-if="favoriteList.length === 0 && !loading" class="empty-state">
          <el-empty description="暂无收藏夹" />
        </div>
        <div v-else class="favorite-items">
          <div v-for="favorite in favoriteList" :key="favorite.id" class="favorite-item" :class="{ collected: favorite.isCollected }">
            <!-- 收藏夹信息 -->
            <div class="favorite-info">
              <div class="favorite-header">
                <h4 class="favorite-name">{{ favorite.name }}</h4>
                <el-tag :type="favorite.showStatus === 0 ? 'success' : 'info'" size="small" effect="light">
                  {{ favorite.showStatus === 0 ? "公开" : "私密" }}
                </el-tag>
              </div>
              <div class="favorite-meta">
                <span class="article-count">{{ favorite.articleCount || 0 }} 篇文章</span>
                <span class="create-time">{{ formatDateCN(favorite.createTime) }}</span>
              </div>
            </div>

            <!-- 收藏操作按钮 -->
            <div class="favorite-action">
              <el-button :type="favorite.isCollected ? 'danger' : 'primary'" :loading="operatingIds.includes(favorite.id)" @click="handleToggleCollect(favorite)" size="small">
                {{ favorite.isCollected ? "取消收藏" : "收藏" }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 对话框底部按钮 -->
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button v-if="isAddingFavorite" type="primary" @click="handleCreateFavorite" :loading="creating"> 确定 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed, watch, nextTick, onMounted, onUnmounted } from "vue";
import { Plus } from "@element-plus/icons-vue";
import { getFavoriteListByArticle, addFavorite, addArticleToFavorite, removeArticleFromFavorite } from "@/api/favorite";
import { formatDateCN } from "@/utils/formatTime";

// Props 定义
const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  articleId: {
    type: Number,
    required: true,
  },
});

// Emits 定义
const emit = defineEmits(["update:modelValue", "success"]);

// 响应式数据
const loading = ref(false); // 列表加载状态
const creating = ref(false); // 创建收藏夹加载状态
const isAddingFavorite = ref(false); // 是否正在新增收藏夹
const favoriteList = ref([]); // 收藏夹列表
const operatingIds = ref([]); // 正在操作的收藏夹ID列表
const favoriteFormRef = ref(null); // 表单引用
const windowWidth = ref(window.innerWidth); // 窗口宽度

// 新增收藏夹表单数据
const favoriteForm = reactive({
  name: "",
  showStatus: 0, // 默认公开
});

// 表单验证规则
const favoriteRules = {
  name: [
    { required: true, message: "请输入收藏夹名称", trigger: "blur" },
    { min: 1, max: 20, message: "收藏夹名称长度为1-20个字符", trigger: "blur" },
  ],
  showStatus: [{ required: true, message: "请选择公开设置", trigger: "change" }],
};

// 计算属性：对话框显示状态
const visible = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:modelValue", value),
});

// 计算属性：对话框宽度
const dialogWidth = computed(() => {
  if (windowWidth.value <= 480) {
    return "95vw";
  } else if (windowWidth.value <= 768) {
    return "90vw";
  } else {
    return "500px";
  }
});

// 获取收藏夹列表
const fetchFavoriteList = async () => {
  if (!props.articleId) return;

  try {
    loading.value = true;
    const res = await getFavoriteListByArticle(props.articleId);
    favoriteList.value = res.data.data || [];
  } catch (error) {
    console.error("获取收藏夹列表失败:", error);
    ElMessage.error("获取收藏夹列表失败");
  } finally {
    loading.value = false;
  }
};

// 显示新增表单
const showAddForm = () => {
  isAddingFavorite.value = true;
  // 重置表单
  favoriteForm.name = "";
  favoriteForm.showStatus = 0;
  // 清除表单验证
  nextTick(() => {
    if (favoriteFormRef.value) {
      favoriteFormRef.value.clearValidate();
    }
  });
};

// 创建收藏夹
const handleCreateFavorite = async () => {
  if (!favoriteFormRef.value) return;

  try {
    // 表单验证
    await favoriteFormRef.value.validate();

    creating.value = true;

    // 调用创建接口
    await addFavorite({
      name: favoriteForm.name.trim(),
      showStatus: favoriteForm.showStatus,
    });

    ElMessage.success("收藏夹创建成功");

    // 返回列表并刷新
    isAddingFavorite.value = false;
    await fetchFavoriteList();
  } catch (error) {
    if (error !== false) {
      // 不是表单验证错误
      console.error("创建收藏夹失败:", error);
      ElMessage.error("创建收藏夹失败");
    }
  } finally {
    creating.value = false;
  }
};

// 切换收藏状态
const handleToggleCollect = async (favorite) => {
  if (operatingIds.value.includes(favorite.id)) {
    return; // 防止重复操作
  }

  try {
    operatingIds.value.push(favorite.id);

    if (favorite.isCollected) {
      // 取消收藏
      await removeArticleFromFavorite(props.articleId, favorite.id);
      favorite.isCollected = false;
      favorite.articleCount = Math.max(0, (favorite.articleCount || 0) - 1);
      ElMessage.success("取消收藏成功");
    } else {
      // 添加收藏
      await addArticleToFavorite(props.articleId, favorite.id);
      favorite.isCollected = true;
      favorite.articleCount = (favorite.articleCount || 0) + 1;
      ElMessage.success("收藏成功");
    }

    // 通知父组件更新收藏状态
    emit("success", {
      action: favorite.isCollected ? "add" : "remove",
      favoriteId: favorite.id,
      favoriteName: favorite.name,
    });
  } catch (error) {
    console.error("收藏操作失败:", error);
    ElMessage.error("收藏操作失败");
  } finally {
    // 移除操作ID
    const index = operatingIds.value.indexOf(favorite.id);
    if (index > -1) {
      operatingIds.value.splice(index, 1);
    }
  }
};

// 关闭对话框
const handleClose = () => {
  visible.value = false;
};

// 对话框关闭后的处理
const handleDialogClosed = () => {
  // 重置状态
  isAddingFavorite.value = false;
  favoriteList.value = [];
  operatingIds.value = [];

  // 重置表单
  favoriteForm.name = "";
  favoriteForm.showStatus = 0;

  // 清除表单验证
  if (favoriteFormRef.value) {
    favoriteFormRef.value.clearValidate();
  }
};

// 监听对话框显示状态
watch(
  () => props.modelValue,
  (newValue) => {
    if (newValue && props.articleId) {
      // 对话框打开时获取收藏夹列表
      fetchFavoriteList();
    }
  },
  { immediate: true }
);

// 监听文章ID变化
watch(
  () => props.articleId,
  (newArticleId) => {
    if (newArticleId && props.modelValue) {
      fetchFavoriteList();
    }
  }
);

// 窗口大小变化处理
const handleResize = () => {
  windowWidth.value = window.innerWidth;
};

// 生命周期钩子
onMounted(() => {
  window.addEventListener("resize", handleResize);
});

onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 对话框样式优化
:deep(.favorite-dialog) {
  .el-dialog {
    max-height: 90vh;
    margin: 0 auto;
    display: flex;
    flex-direction: column;
  }

  .el-dialog__body {
    flex: 1;
    overflow: hidden;
    padding: 20px 20px 10px 20px;
  }

  .el-dialog__footer {
    flex-shrink: 0;
    padding: 10px 20px 20px 20px;
  }
}

// 新增收藏夹表单样式
.add-favorite-form {
  padding: 0;
}

// 收藏夹列表容器
.favorite-list-container {
  // 新增收藏夹按钮
  .add-favorite-btn {
    margin-bottom: 20px;
    text-align: center;
    padding: 16px;
    border: 1px dashed var(--el-border-color);
    border-radius: 8px;
    background-color: var(--el-fill-color-lighter);

    .el-button {
      font-weight: 500;
    }
  }

  // 收藏夹列表
  .favorite-list {
    min-height: 200px;
    max-height: 60vh;
    overflow-y: auto;

    // 空状态
    .empty-state {
      padding: 40px 0;
      text-align: center;
    }

    // 收藏夹项目容器
    .favorite-items {
      display: flex;
      flex-direction: column;
      gap: 12px;

      // 收藏夹项目
      .favorite-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 16px;
        border: 1px solid var(--el-border-color-light);
        border-radius: 8px;
        background-color: var(--el-bg-color-page);
        transition: all 0.3s ease;

        &:hover {
          border-color: var(--el-color-primary-light-7);
          box-shadow: 0 2px 12px var(--el-color-primary-light-9);
        }

        // 已收藏状态
        &.collected {
          border-color: var(--el-color-primary-light-5);
          background-color: var(--el-color-primary-light-9);
        }

        // 收藏夹信息
        .favorite-info {
          flex: 1;
          min-width: 0;

          .favorite-header {
            display: flex;
            align-items: center;
            gap: 8px;
            margin-bottom: 8px;

            .favorite-name {
              margin: 0;
              font-size: 16px;
              font-weight: 500;
              color: var(--el-text-color-primary);
              overflow: hidden;
              text-overflow: ellipsis;
              white-space: nowrap;
            }
          }

          .favorite-meta {
            display: flex;
            align-items: center;
            gap: 16px;
            font-size: 13px;
            color: var(--el-text-color-secondary);

            .article-count {
              display: flex;
              align-items: center;
              gap: 4px;
            }

            .create-time {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }

        // 收藏操作
        .favorite-action {
          flex-shrink: 0;
          margin-left: 16px;

          .el-button {
            min-width: 80px;
          }
        }
      }
    }

    // 自定义滚动条样式
    &::-webkit-scrollbar {
      width: 6px;
    }

    &::-webkit-scrollbar-track {
      background: var(--el-fill-color-light);
      border-radius: 3px;
    }

    &::-webkit-scrollbar-thumb {
      background: var(--el-border-color);
      border-radius: 3px;

      &:hover {
        background: var(--el-border-color-dark);
      }
    }
  }
}

// 对话框底部按钮
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

// 响应式设计
@media (max-width: 768px) {
  // 移动端对话框优化
  :deep(.favorite-dialog) {
    .el-dialog {
      max-height: 85vh;
    }
  }

  // 移动端表单优化
  .add-favorite-form {
    :deep(.el-form) {
      .el-form-item {
        margin-bottom: 20px;

        .el-form-item__label {
          width: 80px !important;
          font-size: 14px;
        }

        .el-form-item__content {
          margin-left: 10px !important;
        }
      }
    }
  }

  .favorite-list-container {
    .favorite-list {
      max-height: 50vh; // 移动端降低最大高度

      .favorite-items {
        .favorite-item {
          flex-direction: column;
          align-items: stretch;
          gap: 12px;

          .favorite-info {
            .favorite-meta {
              flex-direction: column;
              align-items: flex-start;
              gap: 8px;
            }
          }

          .favorite-action {
            margin-left: 0;
            align-self: stretch;

            .el-button {
              width: 100%;
            }
          }
        }
      }
    }
  }
}
</style>
