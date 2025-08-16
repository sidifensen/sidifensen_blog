<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">相册管理</h2>
        <div class="card-actions">
          <el-input v-model="searchAlbumName" placeholder="搜索相册名称" :prefix-icon="Search" size="small" class="search-input" clearable />
          <el-select v-model="searchStatus" placeholder="相册状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="正常" value="0" />
            <el-option label="禁用" value="1" />
          </el-select>
          <el-select v-model="searchUserId" placeholder="用户名称" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option v-for="user in userList" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </div>
      </div>

      <!-- 相册表格 -->
      <el-table v-loading="loading" :data="paginatedAlbumList" class="table" style="height: 100%">
        <el-table-column prop="id" label="相册id" width="70" />
        <el-table-column prop="coverUrl" label="相册封面">
          <template #default="{ row }">
            <div style="display: flex; align-items: center">
              <el-image preview-teleported :src="row.coverUrl" style="width: 60px; height: 60px" :preview-src-list="[row.coverUrl]" fit="cover" />
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="相册名称" />
        <el-table-column prop="userName" label="用户名" />
        <el-table-column prop="showStatus" label="状态">
          <template #default="{ row }">
            <el-switch
              v-model="row.showStatus"
              size="large"
              active-color="#42b983"
              inactive-color="#cccccc"
              active-text="正常"
              inactive-text="禁用"
              :active-value="0"
              :inactive-value="1"
              inline-prompt
              :loading="switchLoading"
              :before-change="() => handleStatusChange(row.id, row.showStatus === 0 ? 1 : 0)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" sortable width="120" />
        <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
        <el-table-column label="操作" width="230">
          <template #default="{ row }">
            <div class="table-actions">
              <el-button type="info" size="small" @click="handleAlbumDetail(row.id)" :icon="InfoFilled" class="detail-button">详情</el-button>
              <el-button type="primary" size="small" @click="handleEditAlbum(row)" :icon="Edit" class="edit-button"> 编辑 </el-button>
              <el-button type="danger" size="small" @click="handleDeleteAlbum(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout=" prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>

    <!-- 编辑相册对话框 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" :before-close="handleDialogClose">
      <el-form ref="albumFormRef" :model="albumForm" :rules="rules" class="editForm">
        <el-form-item prop="name" label="相册名称">
          <el-input v-model="albumForm.name" placeholder="请输入相册名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确认</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 相册详情 -->
    <el-dialog v-model="albumDetailDialogVisible" title="相册详情" class="album-detail" :before-close="handleDetailDialogClose" width="80%" :close-on-click-modal="false">
      <el-card v-if="albumDetail" class="album-detail-card animate-fade-in">
        <div class="album-detail-header">
          <div class="album-cover-container">
            <el-image :src="albumDetail.coverUrl" fit="cover" style="width: 180px; height: 180px; border-radius: 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1)" />
          </div>
          <div class="album-info">
            <h3 class="album-title">{{ albumDetail.name }}</h3>
            <div class="album-meta">
              <div class="meta-item"><span class="label">相册ID：</span>{{ albumDetail.id }}</div>
              <div class="meta-item"><span class="label">创建者：</span>{{ albumDetail.userName }}</div>
              <div class="meta-item"><span class="label">创建时间：</span>{{ albumDetail.createTime }}</div>
              <div class="meta-item"><span class="label">更新时间：</span>{{ albumDetail.updateTime }}</div>
              <div class="meta-item">
                <span class="label">状态：</span><span :class="albumDetail.showStatus === 0 ? 'status-normal' : 'status-disabled'">{{ albumDetail.showStatus === 0 ? "正常" : "禁用" }}</span>
              </div>
              <div class="meta-item"><span class="label">图片数量：</span>{{ albumDetail.photos?.length || 0 }}</div>
            </div>
          </div>
        </div>

        <!-- 操作按钮栏 -->
        <div class="action-bar" v-if="albumDetail.photos && albumDetail.photos.length > 0">
          <div class="action-left">
            <el-checkbox v-model="selectAll" @change="handleSelectAll">全选</el-checkbox>
            <span class="selected-count">{{ selectedPhotos.length }} 已选择</span>
          </div>
          <div class="action-right">
            <el-button type="primary" size="small" @click="handleBatchAudit" :disabled="selectedPhotos.length === 0" :loading="batchAuditLoading"> 批量审核 </el-button>
            <el-button type="warning" size="small" @click="handleBatchReject" :disabled="selectedPhotos.length === 0" :loading="batchRejectLoading"> 批量拒绝 </el-button>
            <el-button type="danger" size="small" @click="handleBatchDelete" :disabled="selectedPhotos.length === 0" :loading="batchDeleteLoading"> 批量删除 </el-button>
          </div>
        </div>

        <div class="album-photos-container" v-if="albumDetail.photos && albumDetail.photos.length > 0">
          <h4 class="photos-title">相册图片</h4>
          <div class="photos-grid">
            <div v-for="photo in albumDetail.photos" :key="photo.id" class="photo-item animate-fade-in" :class="{ 'photo-item-selected': isPhotoSelected(photo.id) }">
              <div class="photo-selector">
                <el-checkbox v-model="selectedPhotos" :value="photo.id" @change="handlePhotoSelect(photo.id, $event)" />
              </div>
              <el-image preview-teleported :src="photo.url" :preview-src-list="albumDetail.photos.map((p) => p.url)" fit="cover" class="photo-image" :class="{ 'photo-image-unaudited': photo.examineStatus === 0 }" />
              <div class="photo-info">
                <div class="photo-id">ID: {{ photo.id }}</div>
                <div class="photo-status" :class="photo.examineStatus === 0 ? 'status-unaudited' : photo.examineStatus === 1 ? 'status-audited' : 'status-rejected'">{{ photo.examineStatus === 0 ? "未审核" : photo.examineStatus === 1 ? "已审核" : "未通过" }}</div>
                <div class="photo-time">{{ photo.createTime }}</div>
                <div class="photo-actions">
                  <el-button text bg type="primary" size="small" @click="handleAuditPhoto(photo.id, 1)" v-if="photo.examineStatus === 0">审核</el-button>
                  <el-button text bg type="warning" size="small" @click="handleRejectPhoto(photo.id)" v-if="photo.examineStatus === 0">拒绝</el-button>
                  <el-button text bg type="danger" size="small" @click="handleDeletePhoto(photo.id)">删除</el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div v-else class="no-photos">该相册暂无图片</div>
      </el-card>
      <div v-else class="loading-container"><el-loading v-loading="true" />加载中...</div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from "vue";
import { Search, Plus, InfoFilled, Edit, Delete, Avatar } from "@element-plus/icons-vue";
import { adminList, adminUpdateAlbum, adminDeleteAlbum, adminSearchAlbum, adminGetAlbumDetail } from "@/api/album";
import { getUserList } from "@/api/user";
import { adminDeletePhoto, adminDeleteBatchPhoto, adminAuditPhoto, adminAuditBatchPhoto } from "@/api/photo";

// 相册列表数据
const albumList = ref([]);
// 分页后的相册列表
const paginatedAlbumList = ref([]);
// 加载状态
const loading = ref(false);
// 当前页码
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);
// 总条数
const total = ref(0);
// 对话框可见性
const dialogVisible = ref(false);
// 对话框标题
const dialogTitle = ref("编辑相册");

// 表单引用
const albumFormRef = ref(null);
// 表单数据
const albumForm = ref({
  id: null,
  name: "",
  coverUrl: "",
  showStatus: 0,
});

// 表单验证规则
const rules = {
  name: [{ required: true, message: "请输入相册名称", trigger: "blur" }],
};

// 用户列表
const userList = ref([]);
// 获取用户列表
const getUsers = async () => {
  const res = await getUserList();
  userList.value = res.data.data;
};

// 获取相册列表
const getAlbums = async () => {
  loading.value = true;
  try {
    const res = await adminList();
    albumList.value = res.data.data;
    total.value = albumList.value.length;
    updatePaginatedAlbumList();
  } catch (error) {
    ElMessage.error("获取相册列表失败");
  } finally {
    loading.value = false;
  }
};

// 初始化
onMounted(() => {
  getAlbums();
  getUsers();
});

// 更新分页数据
const updatePaginatedAlbumList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedAlbumList.value = albumList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  updatePaginatedAlbumList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedAlbumList();
};

const switchLoading = ref(false);
// 处理状态变更
const handleStatusChange = async (id, status) => {
  return new Promise((resolve, reject) => {
    switchLoading.value = true;
    adminUpdateAlbum({ id, showStatus: status })
      .then(() => {
        ElMessage.success("状态更新成功");
        // 手动更新本地数据状态
        const album = albumList.value.find((item) => item.id === id);
        if (album) {
          album.showStatus = status;
        }
        resolve();
      })
      .catch((error) => {
        ElMessage.error("状态更新失败");
        reject(error);
      })
      .finally(() => {
        switchLoading.value = false;
      });
  });
};

// 搜索相册名称
const searchAlbumName = ref("");
// 搜索用户名称
const searchUserId = ref("");
// 搜索相册状态
const searchStatus = ref("");

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    const res = await adminSearchAlbum({ name: searchAlbumName.value, userId: searchUserId.value, showStatus: searchStatus.value });
    albumList.value = res.data.data;
    total.value = albumList.value.length;
    // 更新分页数据
    updatePaginatedAlbumList();
  } catch (error) {
    ElMessage.error("搜索相册失败");
  } finally {
    loading.value = false;
  }
};

// 监听搜索输入变化
const searchTimeout = ref(null);
watch(searchAlbumName, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

watch(searchUserId, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

watch(searchStatus, (newVal) => {
  // 防抖处理
  if (searchTimeout.value) {
    clearTimeout(searchTimeout.value);
  }
  searchTimeout.value = setTimeout(() => {
    handleSearch();
  }, 500);
});

// 处理编辑相册
const handleEditAlbum = (row) => {
  dialogTitle.value = "编辑相册";
  // 深拷贝行数据
  albumForm.value = { ...row };
  dialogVisible.value = true;
};

// 处理删除相册
const handleDeleteAlbum = (id) => {
  ElMessageBox.confirm("确定要删除该相册吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      loading.value = true;
      try {
        await adminDeleteAlbum(id);
        ElMessage.success("删除成功");
        getAlbums();
      } catch (error) {
        ElMessage.error("删除失败");
      } finally {
        loading.value = false;
      }
    })
    .catch(() => {
      // 取消删除
      ElMessage.info("删除已取消");
    });
};

// 处理表单提交
const handleSubmit = () => {
  albumFormRef.value.validate(async (valid) => {
    if (!valid) {
      return;
    }
    try {
      // 编辑相册
      await adminUpdateAlbum(albumForm.value);
      ElMessage.success("编辑相册成功");
      dialogVisible.value = false;
      getAlbums();
    } catch (error) {
      ElMessage.error("编辑相册失败");
      handleDialogClose();
    }
  });
};

// 处理对话框关闭
const handleDialogClose = () => {
  albumFormRef.value.resetFields();
  dialogVisible.value = false;
};

// 详情弹窗
const albumDetailDialogVisible = ref(false);
// 相册详情
const albumDetail = ref(null);
// 选中的图片ID集合
const selectedPhotos = ref([]);
// 全选状态
const selectAll = computed({
  get() {
    return albumDetail.value?.photos?.length > 0 && selectedPhotos.value.length === albumDetail.value.photos.length;
  },
  set(value) {
    if (value) {
      selectedPhotos.value = albumDetail.value.photos.map((photo) => photo.id);
    } else {
      selectedPhotos.value = [];
    }
  },
});

// 批量删除加载状态
const batchDeleteLoading = ref(false);

// 批量审核加载状态
const batchAuditLoading = ref(false);

// 批量拒绝加载状态
const batchRejectLoading = ref(false);

// 检查图片是否被选中
const isPhotoSelected = (photoId) => {
  return selectedPhotos.value.includes(photoId);
};

// 处理单个图片选择
const handlePhotoSelect = (photoId, selected) => {
  if (selected) {
    if (!selectedPhotos.value.includes(photoId)) {
      selectedPhotos.value.push(photoId);
    }
  } else {
    selectedPhotos.value = selectedPhotos.value.filter((id) => id !== photoId);
  }
};

// 处理全选
const handleSelectAll = (value) => {
  selectAll.value = value;
};

// 处理删除单个图片
const handleDeletePhoto = (photoId) => {
  ElMessageBox.confirm("确定要删除该图片吗？", "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await adminDeletePhoto(photoId);
        ElMessage.success("删除成功");
        // 更新本地数据
        if (albumDetail.value) {
          albumDetail.value.photos = albumDetail.value.photos.filter((photo) => photo.id !== photoId);
          // 从选中列表中移除
          selectedPhotos.value = selectedPhotos.value.filter((id) => id !== photoId);
        }
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 处理批量删除
const handleBatchDelete = () => {
  ElMessageBox.confirm(`确定要删除选中的 ${selectedPhotos.value.length} 张图片吗？`, "警告", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchDeleteLoading.value = true;
      try {
        await adminDeleteBatchPhoto(selectedPhotos.value);
        ElMessage.success("批量删除成功");
        // 更新本地数据
        if (albumDetail.value) {
          albumDetail.value.photos = albumDetail.value.photos.filter((photo) => !selectedPhotos.value.includes(photo.id));
          selectedPhotos.value = [];
        }
      } catch (error) {
        ElMessage.error("批量删除失败");
      } finally {
        batchDeleteLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("删除已取消");
    });
};

// 处理单个图片拒绝
const handleRejectPhoto = (photoId) => {
  ElMessageBox.confirm("确定要拒绝该图片吗？", "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await adminAuditPhoto({ photoId: photoId, examineStatus: 2 });
        ElMessage.success("拒绝成功");
        // 更新本地数据
        if (albumDetail.value) {
          const photo = albumDetail.value.photos.find((photo) => photo.id === photoId);
          if (photo) {
            photo.examineStatus = 2;
          }
          // 从选中列表中移除
          selectedPhotos.value = selectedPhotos.value.filter((id) => id !== photoId);
        }
      } catch (error) {
        ElMessage.error("拒绝失败");
      }
    })
    .catch(() => {
      ElMessage.info("拒绝已取消");
    });
};

// 处理批量拒绝
const handleBatchReject = () => {
  ElMessageBox.confirm(`确定要拒绝选中的 ${selectedPhotos.value.length} 张图片吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      batchRejectLoading.value = true;
      try {
        // 构造符合后端接口要求的数组格式
        const data = selectedPhotos.value.map((id) => ({
          photoId: id,
          examineStatus: 2,
        }));
        await adminAuditBatchPhoto(data);
        ElMessage.success("批量拒绝成功");
        // 更新本地数据
        if (albumDetail.value) {
          albumDetail.value.photos.forEach((photo) => {
            if (selectedPhotos.value.includes(photo.id)) {
              photo.examineStatus = 2;
            }
          });
          selectedPhotos.value = [];
        }
      } catch (error) {
        ElMessage.error("批量拒绝失败");
      } finally {
        batchRejectLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("拒绝已取消");
    });
};

// 处理单个图片审核
const handleAuditPhoto = (photoId, status) => {
  ElMessageBox.confirm("确定要审核通过该图片吗？", "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      try {
        await adminAuditPhoto({ photoId: photoId, examineStatus: status });
        ElMessage.success("审核成功");
        // 更新本地数据
        if (albumDetail.value) {
          const photo = albumDetail.value.photos.find((photo) => photo.id === photoId);
          if (photo) {
            photo.examineStatus = status;
          }
        }
      } catch (error) {
        ElMessage.error("审核失败");
      }
    })
    .catch(() => {
      ElMessage.info("审核已取消");
    });
};

// 处理批量审核
const handleBatchAudit = () => {
  ElMessageBox.confirm(`确定要审核通过选中的 ${selectedPhotos.value.length} 张图片吗？`, "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      batchAuditLoading.value = true;
      try {
        const data = selectedPhotos.value.map((id) => ({
          photoId: id,
          examineStatus: 1,
        }));
        console.log(data);
        await adminAuditBatchPhoto(data);
        ElMessage.success("批量审核成功");
        // 更新本地数据
        if (albumDetail.value) {
          albumDetail.value.photos.forEach((photo) => {
            if (selectedPhotos.value.includes(photo.id)) {
              photo.examineStatus = 1;
            }
          });
          selectedPhotos.value = [];
        }
      } catch (error) {
        console.log(error);
        ElMessage.error("批量审核失败");
      } finally {
        batchAuditLoading.value = false;
      }
    })
    .catch(() => {
      ElMessage.info("审核已取消");
    });
};

// 相册详情
const handleAlbumDetail = async (id) => {
  loading.value = true;
  try {
    const res = await adminGetAlbumDetail(id);
    albumDetail.value = res.data.data;
    albumDetailDialogVisible.value = true;
    // 重置选中状态
    selectedPhotos.value = [];
  } catch (error) {
    ElMessage.error("获取相册详情失败");
  } finally {
    loading.value = false;
  }
};

// 处理详情对话框关闭
const handleDetailDialogClose = () => {
  albumDetailDialogVisible.value = false;
  albumDetail.value = null;
  selectedPhotos.value = [];
};
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
    overflow: hidden;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;

    &:hover {
      box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
    }

    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);

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
          background-color: #42b983;
          border-radius: 2px;
          margin-right: 10px;
        }
      }

      .card-actions {
        display: flex;
        align-items: center;

        .search-input {
          width: 240px;
          border-radius: 8px;
          margin-left: 10px;

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
      }
    }
  }

  //表格
  .table {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    max-height: calc(100vh - 220px);

    :deep(.el-tag__content) {
      display: flex;
      align-items: center;
    }

    :deep(.el-table__header-wrapper) {
      background-color: var(--el-bg-color);
      th {
        font-weight: 600;
        color: #475569;
      }
    }

    :deep(.el-table__body-wrapper) {
      tr {
        td {
          color: #64748b;
          padding: 12px 0;
        }
      }
    }

    :deep(.el-table__fixed-right) {
      box-shadow: -3px 0 10px rgba(0, 0, 0, 0.05);
    }

    .table-actions {
      height: 30px;
      display: flex;
      align-items: center;
      gap: 8px;
      @media screen and (max-width: 480px) {
        gap: 4px;
      }
      .detail-button {
        background-color: #f5f5f5;
        color: #606266;
        border-color: #dcdfe6;
        border-radius: 6px;
        transition: all 0.3s ease;
        &:hover {
          background-color: #e9e9e9;
          border-color: #c0c4cc;
          transform: translateY(-2px);
        }
      }
      .edit-button {
        margin-left: 0;
        background-color: #e0f2fe;
        color: #0284c7;
        border-color: #e0f2fe;
        border-radius: 6px;
        transition: all 0.3s ease;

        &:hover {
          background-color: #bae6fd;
          border-color: #bae6fd;
          transform: translateY(-2px);
          box-shadow: 0 2px 8px rgba(2, 132, 199, 0.3);
        }
      }
      .delete-button {
        margin-left: 0 !important;
        background-color: #fee2e2;
        color: #ef4444;
        border-color: #fee2e2;
        border-radius: 6px;
        transition: all 0.3s ease;

        &:hover {
          background-color: #fecaca;
          border-color: #fecaca;
          transform: translateY(-2px);
          box-shadow: 0 2px 8px rgba(239, 68, 68, 0.3);
        }
      }
    }
  }

  .pagination-container {
    display: flex;
    justify-content: flex-end;
    padding: 10px;
    background-color: var(--el-bg-color);
    border-radius: 0 0 12px 12px;
    z-index: 10;
    width: 100%;
    box-sizing: border-box;
    position: absolute;
    bottom: 0;
    left: 0;
  }
}

:deep(.el-dialog) {
  border-radius: 16px;
  width: 500px;

  @media screen and (max-width: 767px) {
    width: 90%;
  }
}

// 新增/编辑菜单对话框
.editForm {
  :deep(.el-form-item) {
    margin-bottom: 20px;
  }
  :deep(.el-input-number),
  :deep(.el-input__wrapper),
  :deep(.el-select__wrapper) {
    border-radius: 16px;
    transition: all 0.3s ease;
    &:focus-within {
      box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
      border-color: #42b983;
    }
  }
}

// 响应式设计
@media screen and (max-width: 1400px) {
  .management-container {
    .card {
      .card-header {
        .card-actions {
          .search-input {
            width: 180px;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 1220px) {
  .management-container {
    .card {
      .card-header {
        flex-direction: column;
        align-items: flex-start;
        gap: 16px;
        .card-actions {
          width: 100%;
          justify-content: space-between;
          .search-input {
            width: 100%;
          }
        }
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .management-container {
    .card {
      padding: 2px;
      .card-header {
        padding: 6px;
        .card-title {
          font-size: 16px;
        }
        .card-actions {
          .search-input {
            width: 100%;
          }
        }
      }

      .table {
        margin-top: 0;
        max-height: calc(100vh - 180px);
      }

      .pagination-container {
        padding: 4px;
        :deep(.el-pagination) {
          .el-pager {
            display: none;
          }
        }
      }
    }
  }

  .editForm {
    :deep(.el-form-item) {
      margin-bottom: 15px;
    }

    :deep(.el-input),
    :deep(.el-select),
    :deep(.el-input-number) {
      width: 100% !important;
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: center;
    gap: 10px;

    :deep(.el-button) {
      flex: 1;
      max-width: 120px;
    }
  }
}

@media screen and (max-width: 480px) {
  .card-header {
    flex-direction: column;
    align-items: flex-start;

    .card-actions {
      width: 100%;
      justify-content: space-between;

      .search-input {
        width: 100%;
      }
    }
  }
}

// 相册详情样式
:deep(.album-detail) {
  width: 1200px !important;
  @media screen and (max-width: 767px) {
    width: 90% !important;
  }
}
.album-detail-header {
  display: flex;
  border-bottom: 1px solid var(--el-bg-color);
  margin-bottom: 20px;
  .album-cover-container {
    margin-right: 20px;
  }
  .album-info {
    flex: 1;
    .album-title {
      font-size: 24px;
      margin-bottom: 16px;
      font-weight: 600;
    }
    .album-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 16px 32px;
      .meta-item {
        display: flex;
        align-items: center;
        font-size: 14px;
        color: #666;
        .label {
          color: #999;
          margin-right: 8px;
          font-weight: 500;
        }
        .status-normal {
          color: #42b983;
          font-weight: 500;
        }
        .status-disabled {
          color: #f56c6c;
          font-weight: 500;
        }
      }
    }
  }
}

// 操作按钮栏样式
.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  margin-bottom: 16px;
  border-bottom: 1px solid var(--el-bg-color);
  .action-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  .action-right {
    display: flex;
    .photo-actions {
      display: flex;
      gap: 12px;
      margin-top: 8px;
    }
  }
  .selected-count {
    color: #666;
    font-size: 14px;
  }
}

// 相册图片容器样式
.album-photos-container {
  padding: 10px 0;
  .photos-title {
    font-size: 18px;
    margin-bottom: 16px;
    font-weight: 600;
  }
  // 图片网格布局 - 桌面端一行四个
  .photos-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 20px;
  }
  // 无图片状态样式
  .no-photos {
    text-align: center;
    padding: 40px 0;
    color: #999;
    font-size: 16px;
  }
}

// 图片项样式
.photo-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.12);
  }

  &.photo-item-selected {
    box-shadow: 0 0 0 2px #42b983, 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  .photo-selector {
    position: absolute;
    top: -2px;
    left: 8px;
    z-index: 10;
    border-radius: 4px;
    padding: 2px;
  }

  .photo-image {
    width: 100%;
    height: 180px;
    object-fit: cover;
    border-radius: 8px 8px 0 0;
  }

  .photo-image-unaudited {
    position: relative;

    &::after {
      content: "未审核";
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-color: rgba(0, 0, 0, 0.5);
      color: white;
      display: flex;
      justify-content: center;
      align-items: center;
      font-size: 16px;
    }
  }

  .photo-info {
    padding: 12px;
    background-color: var(--el-bg-color);
    border-radius: 0 0 8px 8px;

    .photo-id {
      font-size: 12px;
      color: #999;
      margin-bottom: 6px;
    }

    .photo-status {
      display: inline-block;
      padding: 2px 8px;
      border-radius: 12px;
      font-size: 12px;
      margin-bottom: 6px;

      &.status-unaudited {
        background-color: #fff1f0;
        color: #f56c6c;
      }

      &.status-audited {
        background-color: #f0f9eb;
        color: #67c23a;
      }

      &.status-rejected {
        background-color: #fdf6ec;
        color: #e6a23c;
      }
    }

    .photo-time {
      font-size: 12px;
      color: #999;
      margin-bottom: 8px;
    }

    .photo-actions {
      display: flex;
      // justify-content: space-between;
    }
  }
}
// }

// 响应式布局 - 手机端一行一个
@media screen and (max-width: 768px) {
  .album-detail-header {
    flex-direction: column;

    .album-cover-container {
      margin-right: 0;
      margin-bottom: 16px;
    }

    .album-info {
      .album-meta {
        flex-direction: column;
        gap: 8px;
      }
    }
  }

  .action-bar {
    flex-direction: column;
    gap: 12px;

    .action-left,
    .action-right {
      width: 100%;
      justify-content: center;
    }
  }

  .album-photos-container {
    .photos-grid {
      grid-template-columns: 1fr;
      gap: 16px;
    }
  }
}

// 动画效果
.animate-fade-in {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>
