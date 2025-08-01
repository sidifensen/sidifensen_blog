<template>
  <div class="album-detail">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <el-text type="primary" size="large">{{ albumForm.name }}</el-text>
          <el-text size="large">
            相册介绍:
            {{ albumForm.description }}
          </el-text>
          <el-text size="large">创建时间: {{ albumForm.createTime }}</el-text>
          <div class="header-actions">
            <el-button @click="goBack">返回相册列表</el-button>
            <el-button type="primary" @click="handleUploadPhoto">上传图片</el-button>
            <el-button type="success" @click="handleEditAlbum">编辑相册</el-button>
            <el-dropdown trigger="hover">
              <el-button type="default" icon="More"></el-button>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="handleDeleteAlbum" type="danger">删除相册</el-dropdown-item>
                  <el-dropdown-item @click="handleChangeAlbumShowStatus">
                    {{ albumForm.showStatus === 0 ? "设为私密" : "设为公开" }}
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </template>

      <!-- 按日期分组显示图片 -->
      <div v-loading="loading" class="photo-container">
        <template v-for="(group, date) in groupedPhotos" :key="date">
          <div class="date-section">
            <h3>{{ date }}</h3>
            <div class="photo-grid">
              <div v-for="photo in group" :key="photo.id" class="photo-item">
                <el-image :src="photo.url" class="photo" fit="cover" :preview-src-list="[photo.url]" preview-teleported lazy>
                  <template #error>
                    <div class="error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
              </div>
            </div>
          </div>
        </template>
        <el-empty v-if="Object.keys(groupedPhotos).length === 0 && !loading" description="暂无图片" />
      </div>
    </el-card>

    <!-- 上传图片对话框 -->
    <el-dialog v-model="uploadDialogVisible" title="上传图片" width="500px">
      <el-upload class="upload-demo" action="#" :auto-upload="false" v-model:file-list="fileList" :on-change="handleFileChange" accept=".jpg,.gif,.png,.jpeg,.webp" multiple>
        <el-button type="primary">选择图片</el-button>
        <template #tip>
          <div class="el-upload__tip">jpg/png/gif/jpeg/webp 格式，单个文件不超过5MB</div>
        </template>
      </el-upload>
      <div class="preview-container">
        <div v-for="(file, index) in fileList" :key="index" class="preview-item">
          <el-image :src="file.url || (file.raw ? URL.createObjectURL(file.raw) : '')" class="preview-image" fit="cover" :preview-src-list="[file.url || (file.raw ? URL.createObjectURL(file.raw) : '')]">
            <template #error>
              <div class="error">
                <el-icon><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="uploadLoading">开始上传</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑相册对话框 -->
    <el-dialog v-model="editDialogVisible" title="编辑相册" width="500px">
      <el-form :model="albumForm" label-width="80px">
        <el-form-item label="相册名称">
          <el-input v-model="albumForm.name" />
        </el-form-item>
        <el-form-item label="相册描述">
          <el-input v-model="albumForm.description" type="textarea" />
        </el-form-item>
        <el-form-item label="相册封面" v-if="albumForm.coverUrl">
          <div class="cover-container" @click="handleChangeCover(albumForm.id)">
            <el-image :src="albumForm.coverUrl" style="width: 100px; height: 100px; border-radius: 8px" fit="cover"></el-image>
            <div class="cover-overlay">
              <span class="cover-text">点击更换封面</span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEditAlbum">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 选择封面图片对话框 -->
    <el-dialog v-model="coverDialogVisible" title="选择封面图片" width="690px" style="padding: 20px">
      <div class="cover-selection-container">
        <template v-for="(photo, index) in coverDialogPhotos" :key="index">
          <div class="cover-image-container" @click="selectedCoverUrl = photo.url">
            <el-image :src="photo.url" style="width: 100%; height: 100%" fit="cover" :class="{ selected: selectedCoverUrl === photo.url }"></el-image>
          </div>
        </template>
      </div>
      <template #footer>
        <el-button @click="coverDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmCoverChange">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getAlbum, updateAlbum, deleteAlbum, changeShowStatus, changeCover } from "@/api/album";
import { uploadPhoto, deletePhoto } from "@/api/photo";
import { ElMessage, ElMessageBox } from "element-plus";

// 显式引入 URL 对象
const { URL } = window;

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const uploadLoading = ref(false);
const editLoading = ref(false);

const photoList = ref([]);
const groupedPhotos = ref({}); // 按日期分组的照片
const fileList = ref([]);

const uploadDialogVisible = ref(false);
const editDialogVisible = ref(false);

const albumId = Number(route.params.albumId);

const albumForm = ref({
  id: albumId, //相册id从路径中获取
  name: "",
  description: "",
  coverUrl: "",
  showStatus: 0,
  createTime: "",
});

// 获取相册图片列表
const getPhotoList = async () => {
  loading.value = true;
  try {
    const res = await getAlbum(albumForm.value.id);
    photoList.value = res.data.data.photos || [];
    albumForm.value.showStatus = res.data.data.showStatus || 0;
    albumForm.value.name = res.data.data.name || "";
    albumForm.value.description = res.data.data.description || "";
    albumForm.value.coverUrl = res.data.data.coverUrl || "";
    albumForm.value.createTime = res.data.data.createTime || "";
    groupPhotosByDate(); // 按日期分组
  } catch (error) {
    ElMessage.error("获取相册图片失败");
  } finally {
    loading.value = false;
  }
};

// 按日期分组照片
const groupPhotosByDate = () => {
  const groups = {};
  // 先按日期排序
  const sortedPhotos = [...photoList.value].sort((a, b) => {
    return new Date(b.createTime) - new Date(a.createTime);
  });
  // 再分组
  sortedPhotos.forEach((photo) => {
    // 假设createTime是YYYY-MM-DD HH:mm:ss格式
    const date = photo.createTime.split(" ")[0];
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(photo);
  });
  groupedPhotos.value = groups;
};

// 格式化时间，只显示时分秒
const formatTime = (time) => {
  return time.split(" ")[1];
};

// 上传图片
const handleUploadPhoto = () => {
  fileList.value = [];
  uploadDialogVisible.value = true;
};

// 选择图片
const handleFileChange = (file, fileListData) => {
  fileList.value = fileListData;
};

// 提交上传
const submitUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning("请选择要上传的图片");
    return;
  }
  uploadLoading.value = true;
  try {
    const uploadPromises = fileList.value.map((file) => {
      return uploadPhoto(file.raw, albumForm.value.id);
    });
    await Promise.all(uploadPromises); // 等待图片列表上传完成
    ElMessage.success("图片上传成功");
    uploadDialogVisible.value = false; // 关闭上传对话框
    getPhotoList();
  } catch (error) {
    ElMessage.error("上传图片失败");
  } finally {
    uploadLoading.value = false;
  }
};

// 编辑相册
const handleEditAlbum = () => {
  editDialogVisible.value = true;
};

// 提交编辑相册
const submitEditAlbum = async () => {
  if (!albumForm.value.name) {
    ElMessage.warning("请输入相册名称");
    return;
  }
  editLoading.value = true;
  try {
    await updateAlbum(albumForm.value);
    ElMessage.success("编辑相册成功");
    editDialogVisible.value = false; // 关闭编辑对话框
    getAlbum(albumForm.value.id); // 刷新相册详情
  } catch (error) {
    ElMessage.error("编辑相册失败");
  } finally {
    editLoading.value = false;
  }
};

// 删除相册
const handleDeleteAlbum = () => {
  ElMessageBox.confirm("确定要删除这个相册吗？此操作不可恢复", "删除相册", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deleteAlbum(albumForm.value.id);
        ElMessage.success("删除相册成功");
        router.push("/album");
      } catch (error) {
        ElMessage.error("删除相册失败");
      }
    })
    .catch(() => {
      // 用户取消删除
      ElMessage.info("已取消删除");
    });
};

// 修改相册展示状态
const handleChangeAlbumShowStatus = async () => {
  try {
    const newStatus = albumForm.value.showStatus === 0 ? 1 : 0;
    const AlbumDto = {
      id: albumForm.value.id,
      showStatus: newStatus,
    };
    await changeShowStatus(AlbumDto);
    albumForm.value.showStatus = newStatus;
    ElMessage.success("修改相册状态成功");
  } catch (error) {
    console.log(albumForm.value);
    console.error("修改相册状态失败:", error);
    ElMessage.error("修改相册状态失败");
  }
};

const coverDialogVisible = ref(false); // 选择封面对话框是否可见
const selectedCoverUrl = ref(""); // 选择的封面图片url
const coverDialogPhotos = ref([]); // 选择封面图片列表

// 更换相册封面
const handleChangeCover = async () => {
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
    coverDialogPhotos.value = photos; // 赋值相册图片列表
    selectedCoverUrl.value = ""; // 重置选择的封面
    coverDialogVisible.value = true; // 打开选择封面对话框
  } catch (error) {
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
      id: albumId,
      coverUrl: selectedCoverUrl.value,
    };
    await changeCover(AlbumDto);
    ElMessage.success("更换封面成功");
    getAlbum(albumId);
    // 更新编辑页面的封面显示
    albumForm.value.coverUrl = selectedCoverUrl.value;
    coverDialogVisible.value = false; // 关闭选择封面对话框
  } catch (error) {
    console.log(error);
    ElMessage.error("更换封面失败");
  }
};

// 返回相册列表
const goBack = () => {
  router.push("/album");
};

// 页面加载完成后获取相册图片列表
onMounted(() => {
  getPhotoList();
});
</script>

<style lang="scss" scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  .header-actions {
    display: flex;
    gap: 5px;
    align-items: center;
  }
}

.photo-container {
  /* 日期 */
  .date-section {
    margin-bottom: 30px;
    h3 {
      font-size: 16px;
      margin-bottom: 15px;
      padding-bottom: 5px;
      border-bottom: 1px solid #eee;
    }
  }
  /* 照片列表 */
  .photo-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* 自动填充，每行2列 */
    gap: 15px;
    .photo-item {
      display: flex;
      flex-direction: column;
      border-radius: 4px;
      overflow: hidden;
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
      transition: transform 0.3s ease;
      .photo {
        width: 100%;
        height: 160px;
        object-fit: cover;
        transition: transform 0.3s ease;
        .error {
          display: flex;
          justify-content: center;
          align-items: center;
          width: 100%;
          height: 100%;
          font-size: 30px;
        }
      }
      &:hover {
        transform: translateY(-5px);
        .photo {
          transform: scale(1.1);
        }
      }
    }
  }
}

/* 上传图片对话框样式--图片预览 */
.preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 10px;
  .preview-item {
    width: 100px;
    height: 100px;
    .preview-image {
      width: 100%;
      height: 100%;
    }
  }
}

/* 相册封面容器样式 */
.cover-container {
  position: relative;
  width: 100px;
  height: 100px;
  cursor: pointer;
  overflow: hidden;
  .cover-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    border-radius: 8px;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    opacity: 0;
    transition: opacity 0.3s;
    .cover-text {
      color: white;
      font-size: 14px;
      text-align: center;
      padding: 4px;
    }
  }
  &:hover {
    .cover-overlay {
      opacity: 1;
    }
  }
}

/* 封面选择对话框样式 */
.cover-selection-container {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  max-height: 500px;
  overflow-y: auto;
  /* 选择相册封面页面容器样式 */
  .cover-image-container {
    position: relative;
    width: 100px;
    height: 100px;
    border-radius: 8px;
    overflow: hidden;
    cursor: pointer;
    transition: transform 0.2s, box-shadow 0.2s;
  }
  /* 相册被选中 */
  .selected {
    border-radius: 8px;
    overflow: hidden;
    border: 4px solid #409eff;
    box-shadow: 0 0 12px rgba(64, 158, 255, 0.8);
    background-color: rgba(64, 158, 255, 0.1);
  }
}
</style>

<style scoped></style>
