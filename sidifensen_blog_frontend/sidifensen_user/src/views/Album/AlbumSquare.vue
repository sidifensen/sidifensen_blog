<template>
  <div class="album-container">
    <el-card shadow="hover" class="main-card">
      <template #header>
        <div class="card-header">
          <el-text size="large">相册广场</el-text>
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

const handleViewAlbum = (albumId) => {
  router.push({ name: "AlbumDetail", params: { albumId } });
};

onMounted(() => {
  getAllAlbums();
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
            padding: 7px 0;
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
              // overflow: hidden;
              // text-overflow: ellipsis;
              padding: 0 10px;
            }
            .album-userName {
              font-size: 14px;
              font-weight: 500;
              margin: 0;
              color: white;
            }
          }
        }
      }
    }
  }
}
</style>
