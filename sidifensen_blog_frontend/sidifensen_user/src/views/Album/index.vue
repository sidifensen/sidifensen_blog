<template>
  <div class="album-list">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>我的相册</span>
        </div>
      </template>
      <el-button type="primary" @click="handleCreateAlbum">新建相册</el-button>
      <el-table :data="albumList" stripe style="width: 100%" v-loading="loading">
        <el-table-column prop="name" label="相册名称" />
        <el-table-column prop="description" label="相册描述" />
        <el-table-column label="相册封面">
          <template #default="scope">
            <el-image :src="scope.row.coverUrl" style="width: 100px; height: 100px" :preview-src-list="[scope.row.coverUrl]" preview-teleported />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="展示状态">
          <template #default="scope">
            <el-tag :type="scope.row.showStatus === 0 ? 'success' : 'info'">{{ scope.row.showStatus === 0 ? "公开" : "私有" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="primary" @click="handleViewAlbum(scope.row.id)">查看</el-button>
            <el-button size="small" type="warning" @click="handleUpdateAlbum(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDeleteAlbum(scope.row.id)">删除</el-button>
            <el-button size="small" type="info" @click="handleChangeShowStatus(scope.row)">
              {{ scope.row.showStatus === 1 ? "设为公开" : "设为私有" }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-dialog v-model="dialogVisible" :title="albumForm.id ? '编辑相册' : '新建相册'" width="500px">
        <el-form :model="albumForm" label-width="80px">
          <el-form-item label="相册名称">
            <el-input v-model="albumForm.name" />
          </el-form-item>
          <el-form-item label="相册描述">
            <el-input v-model="albumForm.description" type="textarea" />
          </el-form-item>
          <el-form-item label="相册封面" v-if="albumForm.coverUrl">
            <div class="cover-container" @click="handleChangeCover(albumForm.id)">
              <el-image :src="albumForm.coverUrl" style="width: 100px; height: 100px" />
              <div class="cover-overlay">
                <span class="cover-text">点击更换封面</span>
              </div>
            </div>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitAlbumForm">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <el-dialog v-model="coverDialogVisible" title="选择封面图片" width="800px" style="padding: 20px; height: auto !important;">
        <div class="cover-selection-container">
          <el-radio-group v-model="selectedCoverUrl">
            <template v-for="(photo, index) in coverDialogPhotos" :key="index">
              <el-radio :label="photo.url" class="cover-radio">
                <div class="cover-image-container">
                  <el-image :src="photo.url" style="width: 120px; height: 120px" fit="cover"></el-image>
                  <div class="cover-radio-check" v-if="selectedCoverUrl === photo.url">✓</div>
                </div>
              </el-radio>
            </template>
          </el-radio-group>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="coverDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="confirmCoverChange">确定</el-button>
          </span>
        </template>
      </el-dialog>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { listAlbum, createAlbum, updateAlbum, deleteAlbum, changeShowStatus, changeCover, getAlbum } from "@/api/album";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

const router = useRouter();
const loading = ref(false);
const albumList = ref([]);
const dialogVisible = ref(false);
const albumForm = ref({ name: "", description: "", coverUrl: "" });
const coverDialogVisible = ref(false);


// 获取相册列表
const getAlbumList = async () => {
  loading.value = true;
  try {
    const res = await listAlbum();
    albumList.value = res.data.data;
  } catch (error) {
    ElMessage.error("获取相册列表失败");
  } finally {
    loading.value = false;
  }
};

// 新建相册
const handleCreateAlbum = () => {
  albumForm.value = { name: "", description: "" };
  dialogVisible.value = true;
};

// 提交相册表单
const submitAlbumForm = async () => {
  if (!albumForm.value.name) {
    ElMessage.warning("请输入相册名称");
    return;
  }

  try {
    if (albumForm.value.id) {
      // 编辑相册时只提交需要更新的字段
      const AlbumDto = {
        id: albumForm.value.id,
        name: albumForm.value.name,
        description: albumForm.value.description,
        coverUrl: albumForm.value.coverUrl,
      };
      await updateAlbum(AlbumDto);
      ElMessage.success("更新相册成功");
    } else {
      // 新建相册时提交所有字段
      await createAlbum(albumForm.value);
      ElMessage.success("创建相册成功");
    }
    dialogVisible.value = false;
    getAlbumList(); // 刷新相册列表
  } catch (error) {
    ElMessage.error("操作相册失败");
  }
};

// 查看相册
const handleViewAlbum = (albumId) => {
  router.push({ name: "AlbumDetail", params: { albumId } });
};

// 编辑相册
const handleUpdateAlbum = (album) => {
  albumForm.value = { ...album };
  dialogVisible.value = true;
};

// 删除相册
const handleDeleteAlbum = async (albumId) => {
  ElMessageBox.confirm("确定要删除这个相册吗？此操作不可恢复", "删除相册", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAlbum(albumId);
        ElMessage.success("删除相册成功");
        getAlbumList(); // 刷新相册列表
      } catch (error) {
        ElMessage.error("删除相册失败");
      }
    })
    .catch(() => {
      // 用户取消删除
      ElMessage.info("已取消删除相册");
    });
};

// 修改相册展示状态
const handleChangeShowStatus = async (album) => {
  try {
    const AlbumDto = {
      id: album.id,
      showStatus: album.showStatus === 1 ? 0 : 1,
    };
    await changeShowStatus(AlbumDto);
    ElMessage.success("修改展示状态成功");
    getAlbumList(); // 刷新相册列表
  } catch (error) {
    ElMessage.error("修改展示状态失败");
  }
};

const selectedCoverUrl = ref("");
const coverDialogPhotos = ref([]);
const currentCoverAlbumId = ref(0);

// 更换相册封面
const handleChangeCover = async (albumId) => {
  try {
    const res = await getAlbum(albumId);
    if (!res?.data) {
      ElMessage.error("获取相册详情失败");
      return;
    }
    const photos = res.data.data.photos;
    if (!photos || photos.length === 0) {
      ElMessage.warning("该相册暂无图片，请先上传图片");
      return;
    }
    currentCoverAlbumId.value = albumId;
    coverDialogPhotos.value = photos;
    selectedCoverUrl.value = "";
    coverDialogVisible.value = true;
  } catch (error) {
    console.error("更换封面失败:", error);
    ElMessage.error("更换封面失败");
  }
};

// 确认更换封面
const confirmCoverChange = async () => {
  if (!selectedCoverUrl.value) {
    ElMessage.warning("请选择一张图片作为封面");
    return;
  }
  try {
    const AlbumDto = {
      id: currentCoverAlbumId.value,
      coverUrl: selectedCoverUrl.value,
    };
    await changeCover(AlbumDto);
    ElMessage.success("更换封面成功");
    getAlbumList();
    
    // 更新编辑页面的封面显示
    if (dialogVisible.value && albumForm.value.id === currentCoverAlbumId.value) {
      albumForm.value.coverUrl = selectedCoverUrl.value;
    }
    
    coverDialogVisible.value = false;
  } catch (error) {
    console.error("更换封面失败:", error);
    ElMessage.error("更换封面失败");
  }
};

// 页面加载时获取相册列表
onMounted(() => {
  getAlbumList();
});
</script>

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

/* 相册封面容器样式 */
.cover-container {
  position: relative;
  width: 100px;
  height: 100px;
  cursor: pointer;
  overflow: hidden;
}

/* 遮盖层样式 */
.cover-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

/* 鼠标悬停时显示遮盖层 */
.cover-container:hover .cover-overlay {
  opacity: 1;
}

/* 提示文字样式 */
.cover-text {
  color: white;
  font-size: 12px;
  text-align: center;
  padding: 4px;
}

.cover-radio {
  /* display: inline-block; */
  /* margin: 0; */
  /* padding: 0; */
  /* border: none; */
}

/* 封面选择对话框样式 */
.cover-selection-container {
  display: flex;
  flex-wrap: wrap;
  min-height: 400px;
  overflow-y: auto;
}

/* 单选框样式 */
:deep(.el-radio-group){
  display: flex !important;
  flex-wrap: wrap !important;
  gap: 16px 16px !important;
  margin: 0 !important;
  padding: 0 !important;
}

/* 选择相册封面页面容器样式 */
.cover-image-container {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

/* 相册封面选中的对勾样式 */
.cover-radio-check {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 36px;
  height: 36px;
  background-color: #409eff;
  color: white;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  z-index: 2;
}
</style>
