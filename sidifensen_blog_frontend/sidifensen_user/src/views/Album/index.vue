<template>
  <div class="album-container">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <el-text size="large">我的相册</el-text>
        </div>
      </template>
      <el-button type="primary" @click="handleCreateAlbum" class="create-btn">新建相册</el-button>

      <div class="album-grid" v-loading="loading">
        <div v-for="album in albumList" :key="album.id" class="album-card">
          <div class="album-image-container" @click="handleViewAlbum(album.id)">
            <el-image :src="album.coverUrl" class="album-image" fit="cover" />
            <div class="album-info">
              <h3 class="album-name">{{ album.name }}</h3>
            </div>
          </div>
        </div>
      </div>

      <el-dialog v-model="dialogVisible" :title="'新建相册'" width="500px">
        <el-form :model="albumForm" label-width="80px">
          <el-form-item label="相册名称">
            <el-input v-model="albumForm.name" />
          </el-form-item>
          <el-form-item label="相册描述">
            <el-input v-model="albumForm.description" type="textarea" />
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">取消</el-button>
            <el-button type="primary" @click="submitAlbumForm">确定</el-button>
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

// 页面加载时获取相册列表
onMounted(() => {
  getAlbumList();
});
</script>

<style lang="scss" scoped>
.album-container {
  .main-card {
    margin-bottom: 20px;
    /* 相册列表头部 */
    
    .card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    /* 新建相册按钮 */
    .create-btn {
      margin-bottom: 20px;
    }

    /* 相册列表 */
    .album-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 24px;
      margin-top: 20px;
      .album-card {
        border-radius: 8px;
        overflow: hidden;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        transition: transform 0.3s, box-shadow 0.3s;
        &:hover {
          transform: translateY(-5px);
          box-shadow: 0 6px 16px 0 rgba(0, 0, 0, 0.1);
        }
        /* 相册图片容器 */
        .album-image-container {
          position: relative;
          width: 100%;
          height: 280px;
          cursor: pointer;
          overflow: hidden;
          &:hover {
            .album-image {
              transform: scale(1.05);
            }
          }
          .album-image {
            width: 100%;
            height: 100%;
            background-color: #f0f0f0;
            transition: transform 0.5s;
          }
          /* 相册信息 */
          .album-info {
            position: absolute;
            bottom: 0;
            left: 0;
            width: 100%;
            padding: 12px 0;
            background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
            text-align: center;
            /* 相册名称 */
            .album-name {
              font-size: 20px;
              margin: 0;
              font-weight: 500;
              color: white;
              text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
              white-space: nowrap;
              overflow: hidden;
              text-overflow: ellipsis;
              padding: 0 10px;
            }
          }
        }
      }
    }
  }
}
</style>
