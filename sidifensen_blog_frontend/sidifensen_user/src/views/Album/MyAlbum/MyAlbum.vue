<template>
  <div class="album-container">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <el-text class="title" size="large">我的相册</el-text>
          <div>
            <el-button type="primary" @click="toAlbumSquare" class="album-square-btn">
              <el-icon style="margin-right: 2px"><HomeFilled /></el-icon>相册广场
            </el-button>
            <el-button v-if="user" type="primary" @click="handleCreateAlbum" class="create-btn">
              <el-icon style="margin-right: 2px"><DocumentAdd /></el-icon>新建相册
            </el-button>
          </div>
        </div>
      </template>

      <LoadingAnimation v-if="loading" />
      <div class="album-grid">
        <div v-for="album in albumList" :key="album.id" class="album-card">
          <div class="album-image-container" @click="handleViewAlbum(album.id)">
            <el-image :src="album.coverUrl || ''" class="album-image" fit="cover" lazy="true" loading="lazy">
              <template #placeholder>
                <div class="loading-text">加载中...</div>
              </template>
              <template #error>
                <div class="error">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="album-info">
              <h3 class="album-name">{{ album.name }}</h3>
            </div>
          </div>
        </div>
      </div>
      <el-empty v-if="albumList.length === 0 && !loading" :image-size="200" description="暂无相册" />

      <el-dialog v-model="dialogVisible" :title="'新建相册'">
        <el-form :model="albumForm" label-width="80px">
          <el-form-item label="相册名称">
            <el-input v-model="albumForm.name" />
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
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";
import LoadingAnimation from "@/components/LoadingAnimation.vue";
const userStore = useUserStore();
const { user } = storeToRefs(userStore);

const router = useRouter();
const loading = ref(false);
const albumList = ref([]);
const dialogVisible = ref(false);
const albumForm = ref({ name: "", coverUrl: "" });

const toAlbumSquare = () => {
  router.push({ name: "AlbumSquare" });
};

// 获取相册列表
const getAlbumList = async () => {
  loading.value = true;
  try {
    const res = await listAlbum();
    albumList.value = res.data.data.sort((a, b) => a.id - b.id);
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

// 页面加载时获取相册列表
onMounted(() => {
  if (!user.value) {
    ElMessage.error("请先登录");
    router.push("/login");
  } else {
    getAlbumList();
  }
});
</script>

<style lang="scss" scoped>
.album-container {
  .main-card {
    background: transparent;
    border: none;
    box-shadow: none;
    :deep(.el-card__header) {
      padding: 10px;
      background: transparent;
      border-bottom: none;
    }
    /* 相册列表头部 */
    .card-header {
      display: flex;
      align-items: center;
      @media screen and (max-width: 640px) {
        justify-content: space-between;
      }
      .title {
        font-size: 18px !important;
        font-weight: 600;
        background: linear-gradient(135deg, #3d92eb, #10b981);
        -webkit-background-clip: text; // 背景被裁剪为文本前景色 非标准属性
        -webkit-text-fill-color: transparent; // 文字颜色透明
        background-clip: text; // 背景被裁剪为文本前景色 标准属性
        margin-right: 10px;
      }

      /* 相册广场按钮 */
      .album-square-btn {
        position: relative;
        font-size: 16px;
        font-weight: 600;
        border-radius: 30px;
        border: none;
        background: linear-gradient(135deg, #3d92eb, #6f42c1);
        color: white;
        box-shadow: 0 4px 15px rgba(61, 146, 235, 0.3);
        transition: all 0.3s ease;
        overflow: hidden;

        // 滑动光效
        &::before {
          content: "";
          position: absolute;
          top: 0;
          left: -100%; // 初始位置放在按钮左边
          width: 100%;
          height: 100%;
          // 从左到右的白色半透明光效渐变
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
          transition: 0.5s;
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(61, 146, 235, 0.4);

          &::before {
            // 光效从左到右
            left: 100%;
          }
        }

        // 按下后归位
        &:active {
          transform: translateY(0);
        }
      }

      /* 新建相册按钮 */
      .create-btn {
        background: linear-gradient(135deg, #3d92eb, #10b981);
        border: none;
        color: white;
        font-size: 16px;
        font-weight: 600;
        border-radius: 30px;
        box-shadow: 0 4px 15px rgba(61, 146, 235, 0.3);
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
        cursor: pointer;

        &::before {
          content: "";
          position: absolute;
          top: 0;
          left: -100%;
          width: 100%;
          height: 100%;
          background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
          transition: 0.5s;
        }

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(61, 146, 235, 0.4);

          &::before {
            left: 100%;
          }
        }

        &:active {
          transform: translateY(0);
        }
      }
    }

    /* 相册列表 */
    .album-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 24px;
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
            .loading-text {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              font-size: 16px;
              color: #606266;
              background-color: #f5f5f5;
            }
            // 错误占位图标样式
            .error {
              display: flex;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background-color: #f5f5f5;

              .el-icon {
                font-size: 40px;
                color: #909399;
              }
            }
            .loading-container {
              position: absolute;
              top: 0;
              left: 0;
              display: flex;
              flex-direction: column;
              justify-content: center;
              align-items: center;
              width: 100%;
              height: 100%;
              background-color: #f5f5f5;
            }
            .loading-icon {
              font-size: 30px;
              color: #4096ff;
              animation: rotate 1.5s linear infinite;
            }
            .loading-text {
              margin-top: 10px;
              font-size: 14px;
              color: #909399;
            }
            /* 加载动画 */
            @keyframes rotate {
              from {
                transform: rotate(0deg);
              }
              to {
                transform: rotate(360deg);
              }
            }
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

    /* 新建相册弹窗 */
    :deep(.el-dialog) {
      width: 30vw;
      @media screen and (max-width: 870px) {
        width: 70vw;
      }
    }
  }
}
</style>
