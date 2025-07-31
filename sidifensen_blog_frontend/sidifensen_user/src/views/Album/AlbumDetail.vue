<template>
  <div class="album-detail">
    <el-card shadow="hover">
      <template #header>
        <div class="card-header">
          <span>相册详情</span>
          <div>
            <el-button @click="goBack">返回相册</el-button>
            <el-button type="primary" @click="handleUploadPhoto">上传图片</el-button>
          </div>
        </div>
      </template>
      <el-table :data="photoList" stripe style="width: 100%" v-loading="loading">
        <el-table-column label="图片预览">
          <template #default="scope">
            <el-image :src="scope.row.url" style="width: 100px; height: 100px" :preview-src-list="[scope.row.url]" preview-teleported />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="上传时间" />
        <el-table-column label="展示状态">
          <template #default="scope">
            <el-tag :type="scope.row.showStatus === 0 ? 'success' : 'info'">{{ scope.row.showStatus === 0 ? "公开" : "私有" }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="审核状态">
          <template #default="scope">
            <el-tag v-if="scope.row.examineStatus === 0">待审核</el-tag>
            <el-tag v-else-if="scope.row.examineStatus === 1" type="success">审核通过</el-tag>
            <el-tag v-else-if="scope.row.examineStatus === 2" type="danger">审核未通过</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="scope">
            <el-button size="small" type="danger" @click="handleDeletePhoto(scope.row.id)">删除</el-button>
            <el-button size="small" type="info" @click="handleChangeShowStatus(scope.row)">
              {{ scope.row.showStatus === 0 ? "设为私有" : "设为公开" }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="photoList.length === 0 && !loading" description="暂无图片" />
    </el-card>

    <el-dialog v-model="uploadDialogVisible" title="上传图片" width="500px">
      <el-upload class="upload-demo" action="#" :auto-upload="false" v-model:file-list="fileList" :on-change="handleFileChange" multiple>
        <el-button type="primary">选择图片</el-button>
        <template #tip>
          <div class="el-upload__tip">jpg/png/jpeg 格式，单个文件不超过5MB</div>
        </template>
      </el-upload>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="uploadDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitUpload" :loading="uploadLoading">开始上传</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getAlbum } from "@/api/album";
import { uploadPhoto, deletePhoto, changeShowStatus } from "@/api/photo";
import { ElMessage, ElMessageBox } from "element-plus";

const route = useRoute();
const router = useRouter();
const loading = ref(false);
const uploadLoading = ref(false);

const photoList = ref([]);
const fileList = ref([]);
const uploadDialogVisible = ref(false);

const albumId = computed(() => route.params.albumId); //相册id从路径中获取

// 获取相册图片列表
const getPhotoList = async () => {
  loading.value = true;
  try {
    const res = await getAlbum(albumId.value);
    photoList.value = res.data.data.photos || [];
    console.log("获取相册图片成功:", photoList.value);
  } catch (error) {
    console.error("获取相册图片失败:", error);
    ElMessage.error("获取相册图片失败");
  } finally {
    loading.value = false;
  }
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
      return uploadPhoto(file.raw, albumId.value);
    });
    await Promise.all(uploadPromises);// 等待图片列表上传完成
    ElMessage.success("图片上传成功");
    uploadDialogVisible.value = false;// 关闭上传对话框
    getPhotoList();
  } catch (error) {
    ElMessage.error("上传图片失败");
  } finally {
    uploadLoading.value = false;
  }
};

// 删除图片
const handleDeletePhoto = async (photoId) => {
  ElMessageBox.confirm("确定要删除这张图片吗？此操作不可恢复", "删除图片", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await deletePhoto(photoId);
        ElMessage.success("删除图片成功");
        getPhotoList();
      } catch (error) {
        ElMessage.error("删除图片失败");
      }
    })
    .catch(() => {
      // 用户取消删除
      ElMessage.info("已取消删除");
    });
};

// 修改展示状态
const handleChangeShowStatus = async (photo) => {
  try {
    const data = {
      id: photo.id,
      showStatus: photo.showStatus === 1 ? 0 : 1,
    };
    await changeShowStatus(data);
    ElMessage.success("修改展示状态成功");
    getPhotoList();// 刷新图片列表
  } catch (error) {
    ElMessage.error("修改展示状态失败");
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

<style scoped>
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
