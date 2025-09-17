<template>
  <div class="management-container">
    <div class="card">
      <div class="card-header">
        <h2 class="card-title">图片审核</h2>
        <div class="card-actions">
          <el-select v-model="searchExamineStatus" placeholder="审核状态" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option label="待审核" value="0" />
            <el-option label="审核通过" value="1" />
            <el-option label="审核不通过" value="2" />
            <el-option label="全部" value="" />
          </el-select>
          <el-select v-model="searchUserId" placeholder="用户名称" filterable clearable size="small" class="search-input" @change="handleSearch">
            <el-option v-for="user in userList" :key="user.id" :label="user.username" :value="user.id" />
          </el-select>
        </div>
      </div>
      <div class="card-second">
        <el-date-picker v-model="searchCreateTimeStart" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="创建时间开始" :prefix-icon="Calendar" size="small" class="search-input" clearable @change="handleSearch" />
        <el-date-picker v-model="searchCreateTimeEnd" type="datetime" format="YYYY-MM-DD HH:mm:ss" value-format="YYYY-MM-DD HH:mm:ss" placeholder="创建时间结束" :prefix-icon="Calendar" size="small" class="search-input" clearable @change="handleSearch" />
      </div>
      <div class="card-third">
        <el-button type="primary" plain round @click="handleBatchAudit" :disabled="selectedPhotos.length === 0" :loading="batchAuditLoading"> 批量审核 </el-button>
        <el-button type="warning" plain round @click="handleBatchReject" :disabled="selectedPhotos.length === 0" :loading="batchRejectLoading"> 批量拒绝 </el-button>
        <el-button type="danger" plain round @click="handleBatchDelete" :disabled="selectedPhotos.length === 0" :loading="batchDeleteLoading"> 批量删除 </el-button>
      </div>

      <!-- 桌面端表格视图 -->
      <div v-if="!isMobileView" class="desktop-view">
        <el-table v-loading="loading" :data="paginatedPhotoList" class="table" style="height: 100%" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="30" />
          <el-table-column prop="url" label="图片" width="350">
            <template #default="{ row }">
              <div style="display: flex; align-items: center">
                <el-image preview-teleported :src="row.url" style="width: 320px; height: 200px" :preview-src-list="[row.url]" fit="cover" />
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="id" label="id" width="60" />
          <el-table-column prop="username" label="用户名" />
          <el-table-column prop="examineStatus" label="状态">
            <template #default="{ row }">
              <div class="photo-status" :class="row.examineStatus === 0 ? 'status-unaudited' : row.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                {{ row.examineStatus === 0 ? "待审核" : row.examineStatus === 1 ? "已审核" : "未通过" }}
              </div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" sortable width="120" />
          <el-table-column prop="updateTime" label="更新时间" sortable width="120" />
          <el-table-column label="操作" width="300">
            <template #default="{ row }">
              <div class="table-actions">
                <el-button type="info" @click="handleAuditPhoto(row.id)" :icon="Check" class="examine-button">审核</el-button>
                <el-button type="primary" @click="handleRejectPhoto(row.id)" :icon="Close" class="reject-button"> 拒绝 </el-button>
                <el-button type="danger" @click="handleDeletePhoto(row.id)" :icon="Delete" class="delete-button"> 删除 </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 移动端卡片视图 -->
      <div v-else class="mobile-view">
        <div class="photo-cards">
          <el-card v-for="photo in paginatedPhotoList" :key="photo.id" class="photo-card">
            <div class="photo-card-content">
              <div class="photo-header-section">
                <el-image preview-teleported :src="photo.url" class="photo-cover" :preview-src-list="[photo.url]" fit="cover" />
                <div class="photo-info">
                  <div class="photo-header">
                    <div class="photo-id">#{{ photo.id }}</div>
                    <div class="photo-username">{{ photo.username }}</div>
                    <div class="photo-status" :class="photo.examineStatus === 0 ? 'status-unaudited' : photo.examineStatus === 1 ? 'status-audited' : 'status-rejected'">
                      {{ photo.examineStatus === 0 ? "待审核" : photo.examineStatus === 1 ? "已审核" : "未通过" }}
                    </div>
                  </div>
                  <div class="photo-meta">
                    <div class="meta-item">
                      <span class="label">创建时间:</span>
                      <span>{{ photo.createTime }}</span>
                    </div>
                    <div class="meta-item">
                      <span class="label">更新时间:</span>
                      <span>{{ photo.updateTime }}</span>
                    </div>
                  </div>
                </div>
              </div>
              <div class="photo-actions">
                <el-button type="info" @click="handleAuditPhoto(photo.id)" :icon="Check" class="examine-button">审核</el-button>
                <el-button type="primary" @click="handleRejectPhoto(photo.id)" :icon="Close" class="reject-button">拒绝</el-button>
                <el-button type="danger" @click="handleDeletePhoto(photo.id)" :icon="Delete" class="delete-button">删除</el-button>
              </div>
            </div>
          </el-card>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50, 100]" layout=" prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, computed } from "vue";
import { Delete, Close, Check } from "@element-plus/icons-vue";
import { getUserList } from "@/api/user";
import { adminDeletePhoto, adminDeleteBatchPhoto, adminAuditPhoto, adminAuditBatchPhoto, adminSearchPhoto, adminGetPhotoList } from "@/api/photo";

// 图片列表数据
const photoList = ref([]);
// 分页后的图片列表
const paginatedPhotoList = ref([]);
// 加载状态
const loading = ref(false);
// 当前页码
const currentPage = ref(1);
// 每页条数
const pageSize = ref(10);
// 总条数
const total = ref(0);

// 用户列表
const userList = ref([]);
// 获取用户列表
const getUsers = async () => {
  const res = await getUserList();
  userList.value = res.data.data;
};

// 获取图片列表
const getPhotos = async () => {
  loading.value = true;
  try {
    const res = await adminGetPhotoList();
    photoList.value = res.data.data.sort((a, b) => b.id - a.id); //倒序展示
    total.value = photoList.value.length;
    updatePaginatedPhotoList();
  } catch (error) {
    ElMessage.error("获取图片列表失败");
  } finally {
    loading.value = false;
  }
};

// 移动端检测
const isMobileView = ref(false);

// 监听窗口大小变化
const handleResize = () => {
  isMobileView.value = window.innerWidth <= 768;
};

// 初始化
onMounted(() => {
  getPhotos();
  getUsers();
  handleResize();
  window.addEventListener("resize", handleResize);
});

// 组件卸载时移除监听
onUnmounted(() => {
  window.removeEventListener("resize", handleResize);
});

// 更新分页数据
const updatePaginatedPhotoList = () => {
  const startIndex = (currentPage.value - 1) * pageSize.value;
  const endIndex = startIndex + pageSize.value;
  paginatedPhotoList.value = photoList.value.slice(startIndex, endIndex);
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  updatePaginatedPhotoList();
};

// 处理当前页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
  updatePaginatedPhotoList();
};

// 搜索用户名称
const searchUserId = ref("");
// 搜索相册状态
const searchExamineStatus = ref("");
// 搜索创建时间开始
const searchCreateTimeStart = ref(null);
// 搜索创建时间结束
const searchCreateTimeEnd = ref(null);

// 处理搜索
const handleSearch = async () => {
  loading.value = true;
  try {
    const res = await adminSearchPhoto({
      userId: searchUserId.value,
      examineStatus: searchExamineStatus.value,
      createTimeStart: searchCreateTimeStart.value,
      createTimeEnd: searchCreateTimeEnd.value,
    });
    photoList.value = res.data.data;
    total.value = photoList.value.length;
    // 更新分页数据
    updatePaginatedPhotoList();
  } catch (error) {
    ElMessage.error("搜索图片失败");
  } finally {
    loading.value = false;
  }
};

// 选中的图片
const selectedPhotos = ref([]);
// 表格多选
const handleSelectionChange = async (photo) => {
  selectedPhotos.value = photo;
};
// 批量审核加载状态
const batchAuditLoading = ref(false);
// 批量拒绝加载状态
const batchRejectLoading = ref(false);
// 批量删除加载状态
const batchDeleteLoading = ref(false);

// 处理单个图片审核
const handleAuditPhoto = (photoId) => {
  ElMessageBox.confirm("确定要审核通过该图片吗？", "确认", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "info",
  })
    .then(async () => {
      try {
        await adminAuditPhoto({ photoId: photoId, examineStatus: 1 });
        ElMessage.success("审核成功");
        await getPhotos();
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
        const data = selectedPhotos.value
          .map((photo) => ({
            photoId: photo.id,
            examineStatus: 1,
          }))
          .sort((a, b) => a.photoId - b.photoId); // 按照 photoId 升序排序
        console.log(data);
        await adminAuditBatchPhoto(data);
        ElMessage.success("批量审核成功");
        await getPhotos();
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
        getPhotos();
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
        const data = selectedPhotos.value.map((photo) => ({
          photoId: photo.id,
          examineStatus: 2,
        }));
        await adminAuditBatchPhoto(data);
        ElMessage.success("批量拒绝成功");
        await getPhotos();
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
        getPhotos();
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
        const data = selectedPhotos.value.map((photo) => ({
          photoId: photo.id,
        }));
        await adminDeleteBatchPhoto(data);
        ElMessage.success("批量删除成功");
        await getPhotos();
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
    .card-second {
      display: flex;
      justify-content: flex-end;
      padding: 5px 5px 0 5px;
      gap: 10px;
      :deep(.el-input__wrapper) {
        border-radius: 8px;
        &:focus-within {
          box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
          border-color: #42b983;
        }
      }
    }
    .card-third {
      display: flex;
      justify-content: flex-end;
      padding: 10px;
      border-bottom: 1px solid var(--el-border-color);
    }
  }

  // 桌面端表格视图
  .desktop-view {
    flex: 1;
    display: flex;
    flex-direction: column;

    .table {
      flex: 1;
      display: flex;
      flex-direction: column;
      margin-top: 16px;
      max-height: calc(100vh - 270px);

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

        .examine-button {
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

        .reject-button {
          margin-left: 0;
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;
          border-radius: 6px;
          transition: all 0.3s ease;
          &:hover {
            background-color: #fde68a;
            border-color: #fde68a;
            transform: translateY(-2px);
            box-shadow: 0 2px 8px rgba(217, 119, 6, 0.3);
          }
        }

        .delete-button {
          margin-left: 0;
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
  }

  // 移动端卡片视图
  .mobile-view {
    flex: 1;
    display: flex;
    flex-direction: column;
    margin-top: 16px;
    overflow-y: auto;

    .photo-cards {
      display: flex;
      flex-direction: column;
      gap: 12px;
      padding: 5px;

      .photo-card {
        transition: all 0.3s ease;
        border-radius: 8px;
        margin-bottom: 12px;

        // 调整移动端卡片内部padding
        :deep(.el-card__body) {
          padding: 8px;
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
        }

        .photo-card-content {
          display: flex;
          flex-direction: column;
          padding: 8px;
          gap: 12px;

          .photo-header-section {
            display: flex;
            flex-direction: column;
            gap: 12px;

            .photo-cover {
              width: 100%;
              height: 180px;
              border-radius: 6px;
              flex-shrink: 0;
              object-fit: cover;
            }

            .photo-info {
              flex: 1;
              display: flex;
              flex-direction: column;
              gap: 8px;

              .photo-header {
                display: flex;
                align-items: center;
                gap: 8px;
                flex-wrap: wrap;

                .photo-id {
                  font-size: 12px;
                  color: #666;
                  background-color: #f5f5f5;
                  padding: 2px 6px;
                  border-radius: 4px;
                }

                .photo-username {
                  font-size: 14px;
                  font-weight: 500;
                  color: #333;
                }

                .photo-status {
                  display: inline-block;
                  padding: 2px 8px;
                  border-radius: 12px;
                  margin-bottom: 0;
                  font-size: 11px;
                  font-weight: 500;

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
              }

              .photo-meta {
                display: flex;
                flex-direction: column;
                gap: 4px;

                .meta-item {
                  font-size: 12px;
                  color: #666;

                  .label {
                    font-weight: 500;
                    margin-right: 4px;
                  }
                }
              }
            }
          }
        }
      }

      .photo-actions {
        display: flex;
        gap: 8px;
        justify-content: center;
        padding-top: 8px;
        border-top: 1px solid #f0f0f0;

        .el-button {
          font-size: 12px;
          padding: 6px 16px;
          height: auto;
          border-radius: 4px;
          flex: 1;
        }

        .examine-button {
          background-color: #e0f2fe;
          color: #0284c7;
          border-color: #e0f2fe;
        }

        .reject-button {
          background-color: #fef3c7;
          color: #d97706;
          border-color: #fef3c7;
        }

        .delete-button {
          background-color: #fee2e2;
          color: #ef4444;
          border-color: #fee2e2;
        }
      }
    }
  }

  // 通用的图片状态样式
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
