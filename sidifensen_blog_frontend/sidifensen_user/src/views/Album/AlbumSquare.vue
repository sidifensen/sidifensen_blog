<template>
  <div class="album-container">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <el-text size="large" class="album-title">相册广场</el-text>
          <el-button type="primary" @click="toMyAlbum" class="my-album-btn">
            <el-icon style="margin-right: 2px"><User /></el-icon>我的相册
          </el-button>
        </div>
      </template>

      <div class="album-grid" v-loading="loading">
        <div v-for="album in albums" :key="album.id" class="album-card">
          <div class="album-image-container" @click="handleViewAlbum(album.id)">
            <el-image :src="album.coverUrl" class="album-image" fit="cover" />
            <div class="album-info">
              <h3 class="album-name">{{ album.name }}</h3>
              <h4 class="album-userName">{{ album.userName }}</h4>
            </div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { listAllAlbum } from "@/api/album";
import { useRouter } from "vue-router";

const router = useRouter();
const loading = ref(false);
const albums = ref([]);

// 获取用户所有的相册列表
const getAllAlbums = async () => {
  loading.value = true;
  try {
    const response = await listAllAlbum();
    albums.value = response.data.data;
  } catch (error) {
    console.error("获取相册列表失败:", error);
  } finally {
    loading.value = false;
  }
};

// 跳转到相册详情页
const handleViewAlbum = (albumId) => {
  router.push({ name: "AlbumDetail", params: { albumId } });
};

// 跳转到我的相册
const toMyAlbum = () => {
  router.push({ name: "MyAlbum" });
};

onMounted(() => {
  getAllAlbums();
});
</script>

<style lang="scss" scoped>
.album-container {
  .main-card {
    margin-bottom: 20px;
    :deep(.el-card__header) {
      padding: 10px;
    }
    /* 相册列表头部 */
    .card-header {
      display: flex;
      align-items: center;
      @media screen and (max-width: 640px) {
        justify-content: space-between;
      }
    }

    /* 相册广场标题 */
    .album-title {
      font-size: 18px !important;
      font-weight: 600;
      background: linear-gradient(135deg, #3d92eb, #6f42c1);
      -webkit-background-clip: text; // 背景被裁剪为文本前景色 非标准属性
      -webkit-text-fill-color: transparent; // 文字颜色透明
      background-clip: text; // 背景被裁剪为文本前景色 标准属性
      margin-right: 10px;
    }

    /* 我的相册按钮 */
    .my-album-btn {
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

    /* 相册列表 */
    .album-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 24px;
      .album-card {
        border-radius: 8px;
        box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
        transition: transform 0.3s, box-shadow 0.3s;
        overflow: hidden;
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
            padding: 7px 0;
            text-align: center;
            background: linear-gradient(to top, rgba(0, 0, 0, 0.7), transparent);
            /* 相册名称 */
            .album-name {
              font-size: 20px;
              font-weight: 500;
              margin: 0;
              color: white;
              text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
              white-space: nowrap;
              // overflow: hidden;
              // text-overflow: ellipsis;
              padding: 0 10px;
            }
            .album-userName {
              font-size: 14px;
              font-weight: 500;
              margin: 0;
              color: #f9cc9d;
            }
          }
        }
      }
    }
  }
}
</style>
