import { ref, onMounted, onUnmounted } from "vue";

/**
 * 移动端视图检测 composable
 * @param {number} breakpoint - 移动端断点，默认 768
 */
export function useMobileView(breakpoint = 768) {
  const isMobileView = ref(false);

  const handleResize = () => {
    isMobileView.value = window.innerWidth <= breakpoint;
  };

  onMounted(() => {
    handleResize();
    window.addEventListener("resize", handleResize);
  });

  onUnmounted(() => {
    window.removeEventListener("resize", handleResize);
  });

  return {
    isMobileView
  };
}

/**
 * 防抖搜索 composable
 * @param {Function} callback - 搜索回调
 * @param {number} delay - 防抖延迟，默认 500ms
 */
export function useDebounceSearch(callback, delay = 500) {
  let timer = null;

  const debounceSearch = () => {
    if (timer) {
      clearTimeout(timer);
    }
    timer = setTimeout(() => {
      callback();
    }, delay);
  };

  const cancelSearch = () => {
    if (timer) {
      clearTimeout(timer);
      timer = null;
    }
  };

  onUnmounted(() => {
    cancelSearch();
  });

  return {
    debounceSearch,
    cancelSearch
  };
}

/**
 * 分页 composable
 * @param {Function} fetchFn - 数据获取函数
 */
export function usePagination(fetchFn) {
  const currentPage = ref(1);
  const pageSize = ref(10);
  const total = ref(0);
  const loading = ref(false);

  const handleSizeChange = async (size) => {
    pageSize.value = size;
    currentPage.value = 1;
    await fetchFn();
  };

  const handleCurrentChange = async (current) => {
    currentPage.value = current;
    await fetchFn();
  };

  const resetPagination = () => {
    currentPage.value = 1;
  };

  const setLoading = (value) => {
    loading.value = value;
  };

  const setTotal = (value) => {
    total.value = value;
  };

  return {
    currentPage,
    pageSize,
    total,
    loading,
    handleSizeChange,
    handleCurrentChange,
    resetPagination,
    setLoading,
    setTotal
  };
}

/**
 * 批量选择 composable
 */
export function useBatchSelection() {
  const selectedItems = ref([]);

  const handleSelectionChange = (items) => {
    selectedItems.value = items;
  };

  const isSelected = (item) => {
    return selectedItems.value.some((selected) => selected.id === item.id);
  };

  const handleSelect = (item) => {
    const index = selectedItems.value.findIndex((selected) => selected.id === item.id);
    if (index > -1) {
      selectedItems.value.splice(index, 1);
    } else {
      selectedItems.value.push(item);
    }
  };

  const clearSelection = () => {
    selectedItems.value = [];
  };

  return {
    selectedItems,
    handleSelectionChange,
    isSelected,
    handleSelect,
    clearSelection
  };
}

/**
 * 确认删除 composable
 */
export function useConfirmDelete() {
  const handleDelete = (id, deleteFn, message = "确定要删除该项吗？") => {
    ElMessageBox.confirm(message, "警告", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning"
    })
      .then(async () => {
        try {
          await deleteFn(id);
          ElMessage.success("删除成功");
        } catch (error) {
          ElMessage.error("删除失败");
        }
      })
      .catch(() => {
        ElMessage.info("删除已取消");
      });
  };

  return {
    handleDelete
  };
}

/**
 * 确认操作 composable
 */
export function useConfirmAction() {
  const confirmAction = (actionFn, options = {}) => {
    const {
      title = "确认",
      message = "确定要执行此操作吗？",
      confirmText = "确定",
      cancelText = "取消",
      type = "warning",
      successMessage = "操作成功",
      errorMessage = "操作失败"
    } = options;

    ElMessageBox.confirm(message, title, {
      confirmButtonText: confirmText,
      cancelButtonText: cancelText,
      type
    })
      .then(async () => {
        try {
          await actionFn();
          ElMessage.success(successMessage);
        } catch (error) {
          ElMessage.error(errorMessage);
        }
      })
      .catch(() => {
        ElMessage.info("已取消");
      });
  };

  return {
    confirmAction
  };
}
