<template>
  <div class="management-container">
    <div class="card">
      <!-- 卡片头部 -->
      <div class="card-header">
        <h2 class="card-title">标签管理</h2>
        <div class="card-actions">
          <el-input v-model="searchKeyword" placeholder="搜索分类或标签" :prefix-icon="Search" size="small" class="search-input" clearable />
          <el-button type="primary" plain round @click="handleAdd" :icon="Plus">新增标签</el-button>
          <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedTags.length === 0" :loading="batchDeleteLoading">批量删除 ({{ selectedTags.length }})</el-button>
        </div>
      </div>

      <!-- 桌面端列表视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <div v-loading="loading" class="tag-categories">
          <div v-if="filteredCategories.length === 0" class="empty-state">
            <el-empty description="暂无标签数据" />
          </div>
          <div v-else>
            <!-- 分类列表 -->
            <div v-for="(category, index) in filteredCategories" :key="index" class="category-item">
              <div class="category-header" @click="toggleCategory(category.name)">
                <div class="category-left">
                  <el-icon class="expand-icon" :class="{ expanded: expandedCategories.includes(category.name) }">
                    <ArrowRight />
                  </el-icon>
                  <span class="category-name">{{ category.name }}</span>
                  <span class="category-count">({{ category.tags.length }})</span>
                </div>
                <div class="category-actions" @click.stop>
                  <div class="sort-control">
                    <span class="sort-label">排序:</span>
                    <el-input-number v-model="category.sort" :min="0" :max="maxSortValue" size="small" @change="handleSortChange(category)" class="sort-input" />
                  </div>
                  <el-button type="primary" size="small" @click="handleAddToCategory(category.name)" :icon="Plus">新增标签</el-button>
                  <el-button type="danger" size="small" @click="handleDeleteCategory(category.name)" :icon="Delete">删除分类</el-button>
                </div>
              </div>

              <!-- 标签列表 -->
              <transition name="slide-fade">
                <div v-if="expandedCategories.includes(category.name)" class="tags-container">
                  <div v-if="category.tags.length === 0" class="empty-tags">
                    <span>该分类下暂无标签</span>
                  </div>
                  <div v-else class="tags-grid">
                    <div v-for="(tag, tagIndex) in category.tags" :key="tagIndex" class="tag-item">
                      <el-checkbox v-model="tag.checked" @change="handleTagCheck(category.name, tag)" class="tag-checkbox" />
                      <span class="tag-name">{{ tag.name }}</span>
                      <el-button type="danger" size="small" :icon="Delete" circle @click="handleDeleteTag(category.name, tag)" class="delete-icon" />
                    </div>
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </div>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div v-loading="loading" class="tag-categories-mobile">
          <div v-if="filteredCategories.length === 0" class="empty-state">
            <el-empty description="暂无标签数据" />
          </div>
          <div v-else>
            <el-card v-for="(category, index) in filteredCategories" :key="index" class="category-card">
              <div class="category-card-content">
                <div class="category-header-mobile" @click="toggleCategory(category.name)">
                  <div class="category-info">
                    <el-icon class="expand-icon" :class="{ expanded: expandedCategories.includes(category.name) }">
                      <ArrowRight />
                    </el-icon>
                    <span class="category-name">{{ category.name }}</span>
                    <span class="category-count">({{ category.tags.length }})</span>
                  </div>
                </div>

                <div class="category-actions-mobile" @click.stop>
                  <div class="sort-control-mobile">
                    <span class="sort-label">排序:</span>
                    <el-input-number v-model="category.sort" :min="0" :max="maxSortValue" size="small" @change="handleSortChange(category)" class="sort-input" />
                  </div>
                  <div class="button-group-mobile">
                    <el-button type="primary" size="small" @click="handleAddToCategory(category.name)" :icon="Plus">新增</el-button>
                    <el-button type="danger" size="small" @click="handleDeleteCategory(category.name)" :icon="Delete">删除</el-button>
                  </div>
                </div>

                <!-- 移动端标签列表 -->
                <transition name="slide-fade">
                  <div v-if="expandedCategories.includes(category.name)" class="tags-container-mobile">
                    <div v-if="category.tags.length === 0" class="empty-tags">
                      <span>该分类下暂无标签</span>
                    </div>
                    <div v-else class="tags-list-mobile">
                      <div v-for="(tag, tagIndex) in category.tags" :key="tagIndex" class="tag-item-mobile">
                        <el-checkbox v-model="tag.checked" @change="handleTagCheck(category.name, tag)" />
                        <span class="tag-name">{{ tag.name }}</span>
                        <el-button type="danger" size="small" :icon="Delete" circle @click="handleDeleteTag(category.name, tag)" />
                      </div>
                    </div>
                  </div>
                </transition>
              </div>
            </el-card>
          </div>
        </div>
      </div>
    </div>

    <!-- 新增/编辑标签对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose" width="500px" class="tag-dialog">
      <el-form ref="tagFormRef" :model="tagForm" :rules="rules" label-width="80px">
        <el-form-item prop="category" label="分类名">
          <el-select v-model="tagForm.category" placeholder="请选择或输入分类名" filterable allow-create default-first-option style="width: 100%">
            <el-option v-for="cat in categoryNames" :key="cat" :label="cat" :value="cat" />
          </el-select>
          <div class="form-tip">可以选择现有分类，也可以输入新建分类</div>
        </el-form-item>

        <el-form-item prop="name" label="标签名">
          <el-input v-model="tagForm.name" placeholder="请输入标签名" maxlength="50" show-word-limit />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="handleDialogClose">取消</el-button>
          <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from "vue";
import { Search, Plus, Delete, ArrowRight } from "@element-plus/icons-vue";
import { getTagList, addTag, deleteTags, updateCategorySort } from "@/api/tag";

// 标签数据
const tagData = ref({}); // 原始数据 Map<String, List<String>>
const categories = ref([]); // 转换后的分类数组
const loading = ref(false);
const searchKeyword = ref("");

// 展开的分类
const expandedCategories = ref([]);

// 选中的标签
const selectedTags = ref([]);

// 对话框
const dialogVisible = ref(false);
const dialogTitle = ref("新增标签");
const submitLoading = ref(false);
const tagFormRef = ref(null);

// 标签表单
const tagForm = reactive({
  category: "",
  name: "",
});

// 批量操作加载状态
const batchDeleteLoading = ref(false);

// 移动端检测
const isMobileView = ref(false);

// 表单验证规则
const rules = {
  category: [{ required: true, message: "请输入分类名", trigger: "change" }],
  name: [{ required: true, message: "请输入标签名", trigger: "blur" }],
};

// 计算所有分类名
const categoryNames = computed(() => {
  return categories.value.map((cat) => cat.name);
});

// 计算最大排序值
const maxSortValue = computed(() => {
  if (categories.value.length === 0) return 0;
  return Math.max(...categories.value.map((cat) => cat.sort || 0));
});

// 过滤后的分类列表
const filteredCategories = computed(() => {
  if (!searchKeyword.value) {
    return categories.value;
  }
  const keyword = searchKeyword.value.toLowerCase();
  return categories.value
    .map((category) => {
      // 检查分类名是否匹配
      const categoryMatch = category.name.toLowerCase().includes(keyword);
      // 过滤标签
      const filteredTags = category.tags.filter((tag) => tag.name.toLowerCase().includes(keyword));

      // 如果分类名匹配，返回该分类的所有标签
      if (categoryMatch) {
        return category;
      }
      // 如果有标签匹配，返回包含匹配标签的分类
      if (filteredTags.length > 0) {
        return {
          ...category,
          tags: filteredTags,
        };
      }
      return null;
    })
    .filter((category) => category !== null);
});

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 获取标签列表
const getTagsList = async () => {
  loading.value = true;
  try {
    const res = await getTagList();
    tagData.value = res.data.data;

    // 转换数据结构 - 后端现在返回 Map<String, List<Tag>>，已经按 sort 排序
    // 不再使用 .sort()，因为后端已经按照 sort 排序返回
    categories.value = Object.keys(tagData.value).map((categoryName) => ({
      name: categoryName,
      sort: tagData.value[categoryName][0]?.sort || 0, // 分类排序值（取第一个标签的 sort）
      tags: tagData.value[categoryName].map((tag) => ({
        id: tag.id, // 标签ID
        name: tag.name, // 标签名称
        category: tag.category, // 分类名称
        checked: false, // 选中状态
      })),
    }));

    // 默认展开所有分类
    expandedCategories.value = categoryNames.value;
  } catch (error) {
    ElMessage.error("获取标签列表失败");
    console.error("获取标签列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 切换分类展开/收起
const toggleCategory = (categoryName) => {
  const index = expandedCategories.value.indexOf(categoryName);
  if (index > -1) {
    expandedCategories.value.splice(index, 1);
  } else {
    expandedCategories.value.push(categoryName);
  }
};

// 处理标签选中
const handleTagCheck = (categoryName, tag) => {
  if (tag.checked) {
    selectedTags.value.push({
      id: tag.id,
      category: categoryName,
      name: tag.name,
    });
  } else {
    const index = selectedTags.value.findIndex((t) => t.id === tag.id);
    if (index > -1) {
      selectedTags.value.splice(index, 1);
    }
  }
};

// 新增标签
const handleAdd = () => {
  dialogTitle.value = "新增标签";
  tagForm.category = "";
  tagForm.name = "";
  dialogVisible.value = true;
};

// 新增到指定分类
const handleAddToCategory = (categoryName) => {
  dialogTitle.value = `新增标签到【${categoryName}】`;
  tagForm.category = categoryName;
  tagForm.name = "";
  dialogVisible.value = true;
};

// 提交表单
const handleSubmit = async () => {
  try {
    await tagFormRef.value.validate();
    submitLoading.value = true;

    const addData = {
      category: tagForm.category,
      name: tagForm.name,
    };

    await addTag(addData);
    ElMessage.success("添加成功");
    dialogVisible.value = false;
    await getTagsList();
  } catch (error) {
    if (error !== false) {
      ElMessage.error("添加失败");
      console.error("添加失败:", error);
    }
  } finally {
    submitLoading.value = false;
  }
};

// 删除单个标签
const handleDeleteTag = (categoryName, tag) => {
  ElMessageBox.confirm(`确定要删除标签【${tag.name}】吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteTags([tag.id]);
        ElMessage.success("删除成功");
        await getTagsList();
      } catch (error) {
        ElMessage.error("删除失败");
        console.error("删除失败:", error);
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 批量删除标签
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedTags.value.length} 个标签吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        const tagIds = selectedTags.value.map((tag) => tag.id);
        await deleteTags(tagIds);
        ElMessage.success("批量删除成功");
        selectedTags.value = [];
        await getTagsList();
      } catch (error) {
        ElMessage.error("批量删除失败");
        console.error("批量删除失败:", error);
      } finally {
        batchDeleteLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 删除整个分类
const handleDeleteCategory = (categoryName) => {
  const category = categories.value.find((cat) => cat.name === categoryName);
  if (!category) return;

  ElMessageBox.confirm(`确定要删除分类【${categoryName}】及其下的所有 ${category.tags.length} 个标签吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        const tagIds = category.tags.map((tag) => tag.id);
        await deleteTags(tagIds);
        ElMessage.success("删除分类成功");
        await getTagsList();
      } catch (error) {
        ElMessage.error("删除分类失败");
        console.error("删除分类失败:", error);
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 关闭对话框
const handleDialogClose = () => {
  tagFormRef.value?.resetFields();
  dialogVisible.value = false;
};

// 处理分类排序变化
const handleSortChange = async (category) => {
  try {
    const sortData = {
      category: category.name,
      newSort: category.sort,
    };
    await updateCategorySort(sortData);
    ElMessage.success(`分类【${category.name}】排序更新成功`);
    // 重新获取标签列表以刷新排序
    await getTagsList();
  } catch (error) {
    ElMessage.error("更新排序失败");
    console.error("更新排序失败:", error);
    // 失败时恢复数据
    await getTagsList();
  }
};

// 初始化
onMounted(() => {
  getTagsList();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});
</script>

<style lang="scss" scoped>
// 标签管理页面主容器
.management-container {
  height: 100%;
  box-sizing: border-box;
  position: relative;

  // 主卡片容器
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

    // 卡片头部
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px 10px 0 10px;

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
          background-color: #409eff;
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;
        gap: 10px;

        :deep(.el-button) {
          margin-left: 0;
        }

        .search-input {
          width: 240px;
          border-radius: 8px;

          :deep(.el-input__wrapper) {
            border-radius: 8px;
            transition: all 0.3s ease;

            &:focus-within {
              box-shadow: 0 0 0 3px rgba(64, 158, 255, 0.2);
              border-color: #409eff;
            }
          }
        }
      }
    }
  }

  // 桌面端列表视图
  .desktop-view {
    flex: 1;
    overflow-y: auto;
    padding: 10px;

    // 分类列表容器
    .tag-categories {
      // 空状态
      .empty-state {
        padding: 60px 0;
        text-align: center;
      }

      // 分类项
      .category-item {
        margin-bottom: 12px;
        border: 1px solid var(--el-border-color-light);
        border-radius: 8px;
        overflow: hidden;
        transition: all 0.3s ease;

        &:hover {
          border-color: var(--el-border-color);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
        }

        // 分类头部
        .category-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 16px 20px;
          background-color: var(--el-bg-color);
          cursor: pointer;
          user-select: none;
          transition: background-color 0.3s ease;

          &:hover {
            background-color: var(--el-fill-color-light);
          }

          .category-left {
            display: flex;
            align-items: center;
            gap: 8px;
            flex: 1;

            .expand-icon {
              transition: transform 0.3s ease;
              color: var(--el-text-color-secondary);

              &.expanded {
                transform: rotate(90deg);
              }
            }

            .category-name {
              font-size: 16px;
              font-weight: 600;
              color: var(--el-text-color-primary);
            }

            .category-count {
              font-size: 14px;
              color: var(--el-text-color-secondary);
            }
          }

          .category-actions {
            display: flex;
            align-items: center;
            gap: 12px;

            .sort-control {
              display: flex;
              align-items: center;
              gap: 6px;
              padding: 4px 12px;
              background-color: var(--el-fill-color-light);
              border-radius: 6px;
              border: 1px solid var(--el-border-color-lighter);
              transition: all 0.3s ease;

              &:hover {
                border-color: var(--el-color-primary);
                background-color: rgba(64, 158, 255, 0.05);
              }

              .sort-label {
                font-size: 13px;
                color: var(--el-text-color-secondary);
                font-weight: 500;
                white-space: nowrap;
              }

              .sort-input {
                width: 140px;

                :deep(.el-input-number__decrease),
                :deep(.el-input-number__increase) {
                  background-color: var(--el-fill-color);
                  border: none;
                  color: var(--el-text-color-regular);

                  &:hover {
                    color: var(--el-color-primary);
                  }
                }

                :deep(.el-input__wrapper) {
                  padding: 1px 8px;
                  box-shadow: none;
                  background-color: var(--el-bg-color);
                }
              }
            }

            :deep(.el-button) {
              margin-left: 0;
            }
          }
        }

        // 标签容器
        .tags-container {
          padding: 16px 20px;
          background-color: var(--el-bg-color-page);
          border-top: 1px solid var(--el-border-color-lighter);

          .empty-tags {
            text-align: center;
            padding: 20px;
            color: var(--el-text-color-secondary);
            font-size: 14px;
          }

          // 标签网格布局
          .tags-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
            gap: 12px;

            // 单个标签项
            .tag-item {
              display: flex;
              align-items: center;
              gap: 8px;
              padding: 8px 12px;
              background-color: var(--el-bg-color);
              border: 1px solid var(--el-border-color-light);
              border-radius: 6px;
              transition: all 0.3s ease;

              &:hover {
                border-color: var(--el-color-primary);
                box-shadow: 0 2px 8px rgba(64, 158, 255, 0.2);

                .delete-icon {
                  opacity: 1;
                }
              }

              .tag-checkbox {
                flex-shrink: 0;
              }

              .tag-name {
                flex: 1;
                font-size: 14px;
                color: var(--el-text-color-regular);
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
              }

              .delete-icon {
                flex-shrink: 0;
                opacity: 0;
                transition: opacity 0.3s ease;
              }
            }
          }
        }
      }
    }
  }

  // 移动端卡片视图
  .mobile-view {
    flex: 1;
    overflow-y: auto;
    padding: 10px;

    // 移动端分类列表
    .tag-categories-mobile {
      display: flex;
      flex-direction: column;
      gap: 12px;

      .empty-state {
        padding: 60px 0;
        text-align: center;
      }

      // 分类卡片
      .category-card {
        transition: all 0.3s ease;
        border-radius: 8px;

        &:hover {
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .category-card-content {
          display: flex;
          flex-direction: column;
          gap: 12px;

          // 移动端分类头部
          .category-header-mobile {
            display: flex;
            justify-content: space-between;
            align-items: center;
            cursor: pointer;
            user-select: none;

            .category-info {
              display: flex;
              align-items: center;
              gap: 8px;

              .expand-icon {
                transition: transform 0.3s ease;
                color: var(--el-text-color-secondary);

                &.expanded {
                  transform: rotate(90deg);
                }
              }

              .category-name {
                font-size: 16px;
                font-weight: 600;
                color: var(--el-text-color-primary);
              }

              .category-count {
                font-size: 14px;
                color: var(--el-text-color-secondary);
              }
            }
          }

          // 移动端操作按钮
          .category-actions-mobile {
            display: flex;
            flex-direction: column;
            gap: 10px;
            padding-top: 12px;
            border-top: 1px solid var(--el-border-color-lighter);

            .sort-control-mobile {
              display: flex;
              align-items: center;
              gap: 8px;
              padding: 8px 12px;
              background-color: var(--el-fill-color-light);
              border-radius: 6px;
              border: 1px solid var(--el-border-color-lighter);

              .sort-label {
                font-size: 13px;
                color: var(--el-text-color-secondary);
                font-weight: 500;
                white-space: nowrap;
              }

              .sort-input {
                flex: 1;

                :deep(.el-input-number__decrease),
                :deep(.el-input-number__increase) {
                  background-color: var(--el-fill-color);
                  border: none;
                  color: var(--el-text-color-regular);

                  &:hover {
                    color: var(--el-color-primary);
                  }
                }

                :deep(.el-input__wrapper) {
                  padding: 1px 8px;
                }
              }
            }

            .button-group-mobile {
              display: flex;
              gap: 8px;

              :deep(.el-button) {
                margin-left: 0;
                flex: 1;
              }
            }
          }

          // 移动端标签容器
          .tags-container-mobile {
            .empty-tags {
              text-align: center;
              padding: 20px;
              color: var(--el-text-color-secondary);
              font-size: 14px;
            }

            // 移动端标签列表
            .tags-list-mobile {
              display: flex;
              flex-direction: column;
              gap: 8px;

              .tag-item-mobile {
                display: flex;
                align-items: center;
                gap: 8px;
                padding: 8px 12px;
                background-color: var(--el-fill-color-light);
                border-radius: 6px;

                .tag-name {
                  flex: 1;
                  font-size: 14px;
                  color: var(--el-text-color-regular);
                }
              }
            }
          }
        }
      }
    }
  }
}

// 表单提示
.form-tip {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
  line-height: 1.4;
}

// 展开收起动画
.slide-fade-enter-active,
.slide-fade-leave-active {
  transition: all 0.3s ease;
}

.slide-fade-enter-from {
  opacity: 0;
  transform: translateY(-10px);
}

.slide-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

// 响应式设计
@media screen and (max-width: 1200px) {
  .management-container .card .desktop-view .tag-categories .category-item .tags-container .tags-grid {
    grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  }
}

@media screen and (max-width: 768px) {
  .management-container .card {
    padding: 10px;

    .card-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 12px;
      padding: 8px;

      .card-title {
        font-size: 18px;
      }

      .card-actions {
        width: 100%;
        flex-direction: column;

        .search-input {
          width: 100%;
        }

        .el-button {
          width: 100%;
        }
      }
    }
  }
}
</style>
