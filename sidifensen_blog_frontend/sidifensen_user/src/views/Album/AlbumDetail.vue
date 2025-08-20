<template>
  <div class="album-detail">
    <el-card class="card" shadow="hover">
      <template #header>
        <div class="header-title">
          <el-button class="my-album-btn" @click="goBack">
            <el-icon><ArrowLeftBold /></el-icon>
          </el-button>
          <div class="album-name-container">
            <el-text class="title" type="primary" size="large">{{ albumForm.name }}</el-text>
          </div>
          <div class="user-name-container" v-if="albumForm.userName">
            <el-text class="user-name" type="secondary"> <span class="at-symbol">@</span>{{ albumForm.userName }} </el-text>
          </div>
        </div>
        <div class="header-actions" v-if="isAlbumOwner">
          <el-button class="upload-photo-btn" type="primary" @click="handleUploadPhoto">
            <el-icon style="margin-right: 2px"><UploadFilled /></el-icon>上传图片
          </el-button>
          <el-button class="edit-album-btn" type="warning" @click="handleEditAlbum">
            <el-icon style="margin-right: 2px"><Edit /></el-icon>编辑相册
          </el-button>
          <el-switch
            class="show-status-switch"
            v-model="switchShowStatus"
            @click="handleChangeAlbumShowStatus"
            active-text="公开"
            style="--el-switch-on-color: #13ce66; --el-switch-off-color: #ff4949"
            inactive-text="私密"
            size="large"
            inline-prompt />
        </div>
        <div class="header-select" v-if="isAlbumOwner">
          <el-button v-if="isSelectMode" class="select-all-btn" style="margin-right: 5px" type="success" @click="toggleAllSelection">{{ isAllSelected ? "取消全选" : "全选" }}</el-button>
          <el-button class="select-btn" style="margin-right: 5px" type="info" @click="toggleSelectMode">{{ isSelectMode ? "取消选择" : "选择图片" }}</el-button>
          <el-button class="delete-btn" style="margin-right: 5px" v-show="selectedPhotos.length > 0" type="danger" @click="handleBatchDelete">
            <el-icon><Delete /></el-icon>删除 ({{ selectedPhotos.length }})
          </el-button>
          <el-dropdown trigger="hover">
            <el-button type="default" icon="More"></el-button>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item @click="handleDeleteAlbum" type="danger">删除相册</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </template>

      <!-- 按日期分组显示图片 -->
      <div class="photo-container">
        <template v-for="(group, date) in groupedPhotos" :key="date">
          <div class="date-section">
            <div class="date-header">
              <h3>{{ date }}</h3>
              <el-checkbox v-if="isSelectMode" v-model="dateSelection[date]" @change="handleDateSelectionChange(date)" size="large" />
            </div>
            <div class="photo-grid">
              <div v-for="(photo, index) in group" :key="photo.id" class="photo-item" :class="{ selected: selectedPhotos.includes(photo.id) }" @click="handlePhotoClick(photo)">
                <el-image
                  v-if="photo.url"
                  :src="photo.url || ''"
                  class="photo"
                  fit="cover"
                  :preview-src-list="isSelectMode ? [] : getAllPhotoUrls()"
                  :preview-teleported="!isSelectMode"
                  :initial-index="getPhotoIndex(photo)"
                  show-progress
                  close-on-press-escape
                  lazy
                  loading="lazy"
                  >
                  <template #toolbar="{ actions, prev, next, activeIndex }">
                    <el-icon @click="prev"><Back /></el-icon>
                    <el-icon @click="next"><Right /></el-icon>
                    <el-icon @click="actions('zoomOut')"><ZoomOut /></el-icon>
                    <el-icon @click="actions('zoomIn', { enableTransition: false, zoomRate: 2 })">
                      <ZoomIn />
                    </el-icon>
                    <el-icon @click="download(activeIndex)"><Download /></el-icon>
                  </template>
                  <template #placeholder>
                    <div class="loading-text">加载中...</div>
                  </template>
                  <template #error>
                    <div class="error">
                      <el-icon><Picture /></el-icon>
                    </div>
                  </template>
                </el-image>
                <!-- 审核状态显示 -->
                <div class="examine-status" :class="getExamineStatusClass(photo.examineStatus)">
                  {{ getExamineStatusText(photo.examineStatus) }}
                </div>
                <div v-if="isSelectMode">
                  <el-icon v-if="selectedPhotos.includes(photo.id)" class="selected-icon"><Check /></el-icon>
                </div>
              </div>
            </div>
          </div>
        </template>
        <el-empty v-if="Object.keys(groupedPhotos).length === 0 && !loading" description="暂无图片" />
      </div>
    </el-card>

    <!-- 上传图片对话框 -->
    <el-dialog class="upload-dialog" v-model="uploadDialogVisible" title="上传图片">
      <el-upload action="#" :auto-upload="false" v-model:file-list="fileList" :on-change="handleFileChange" accept=".jpg,.gif,.png,.jpeg,.webp" multiple>
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
import { ref, onMounted, computed } from "vue";
// 显式引入FileReader和Image
const { FileReader, Image } = window;
import { useRoute, useRouter } from "vue-router";
import { getAlbum, updateAlbum, deleteAlbum, changeShowStatus, changeCover } from "@/api/album";
import { uploadPhoto, batchDeletePhoto } from "@/api/photo";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/userStore";
import { storeToRefs } from "pinia";

// 显式引入 URL 对象
const { URL } = window;

const route = useRoute();
const router = useRouter();
const loading = ref(false);

const albumId = Number(route.params.albumId);
const albumForm = ref({
  id: albumId, //相册id从路径中获取
  name: "",
  coverUrl: "",
  showStatus: 0,
  createTime: "",
  userId: "", // 相册所属用户ID
  userName: "", // 用户名称
});

const userStore = useUserStore();
const { user } = storeToRefs(userStore);

// 照片列表
const photoList = ref([]);
// 获取相册的照片
const getPhotoList = async () => {
  loading.value = true;
  try {
    const res = await getAlbum(albumForm.value.id);
    photoList.value = res.data.data.photos || [];;
    albumForm.value.showStatus = res.data.data.showStatus || 0;
    albumForm.value.name = res.data.data.name || "";
    albumForm.value.coverUrl = res.data.data.coverUrl || "";
    albumForm.value.userName = res.data.data.userName || "";
    albumForm.value.userId = res.data.data.userId || ""; // 获取相册所属用户ID
    groupPhotosByDate(); // 按日期分组
  } catch (error) {
    ElMessage.error("获取相册图片失败");
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  getPhotoList();
  // 获取相册图片列表
});

// 切换显示状态
const switchShowStatus = computed(() => albumForm.value.showStatus === 0);

// 判断是否为相册所有者
const isAlbumOwner = computed(() => {
  if (!user.value) {
    return false;
  }
  const currentUserId = user.value.id;
  const albumUserId = albumForm.value.userId;
  return currentUserId && albumUserId && currentUserId === albumUserId;
});

// 按日期分组的照片
const groupedPhotos = ref({});

// 按日期分组照片
const groupPhotosByDate = () => {
  const groups = {};
  // 先按日期排序
  const sortedPhotos = [...photoList.value].sort((a, b) => {
    return new Date(b.createTime) - new Date(a.createTime);
  });
  // 再分组
  sortedPhotos.forEach((photo) => {
    // createTime为YYYY-MM-DD HH:mm:ss格式
    const date = photo.createTime.split(" ")[0]; // 提取年月日
    if (!groups[date]) {
      groups[date] = [];
    }
    groups[date].push(photo);
  });
  groupedPhotos.value = groups;
  // console.log(groupedPhotos.value);// key:2025-08-04 value:[{...},{...}]
};

// 图片工具栏下载按钮
const download = (number) => {
  // 获取所有图片URL列表
  const allUrls = getAllPhotoUrls();
  const url = allUrls[number]; // 使用预览列表中的URL
  const suffix = url.slice(url.lastIndexOf(".")); // 文件格式
  const filename = Date.now() + suffix; // 时间戳文件名
  fetch(url)
  .then((response) => response.blob())
  .then((blob) => {
    const blobUrl = URL.createObjectURL(new Blob([blob])); // 创建一个blob对象url
    const link = document.createElement("a"); // 创建一个隐藏a标签
    link.href = blobUrl; // 给a标签的href属性赋值
    link.download = filename; // 给a标签的download属性赋值
    document.body.appendChild(link); // 下载前添加到body中
    link.click(); // 点击a标签
    URL.revokeObjectURL(blobUrl); // 释放url对象
    link.remove(); // 移除a标签
  });
};

// 是否为图片选择模式
const isSelectMode = ref(false);
// 是否全选
const isAllSelected = ref(false);
// 选中的图片列表
const selectedPhotos = ref([]);
// 按日期选择的状态
const dateSelection = ref({});

// 切换选择模式
const toggleSelectMode = () => {
  isSelectMode.value = !isSelectMode.value;
  if (!isSelectMode.value) {
    selectedPhotos.value = [];
    isAllSelected.value = false;
    dateSelection.value = {};
  } else {
    // 初始化日期选择状态
    for (const date in groupedPhotos.value) {
      dateSelection.value[date] = false;
    }
  }
};

// 处理日期选择变化
const handleDateSelectionChange = (date) => {
  const group = groupedPhotos.value[date];
  // 如果日期被选中(true)
  if (dateSelection.value[date]) {
    // 选中该日期的所有图片
    group.forEach((photo) => {
      if (!selectedPhotos.value.includes(photo.id)) {
        selectedPhotos.value.push(photo.id);
      }
    });
  } else {
    // 取消选中该日期的所有图片
    selectedPhotos.value = selectedPhotos.value.filter((id) => {
      return !group.some((photo) => photo.id === id);
    });
  }
  // 更新全选状态
  updateAllSelectedStatus();
};

// 切换全选状态
const toggleAllSelection = () => {
  isAllSelected.value = !isAllSelected.value;
  if (isAllSelected.value) {
    // 全选所有图片
    selectedPhotos.value = [];
    for (const date in groupedPhotos.value) {
      const group = groupedPhotos.value[date];
      group.forEach((photo) => {
        selectedPhotos.value.push(photo.id);
      });
      // 日期选择状态全部设为true选中
      dateSelection.value[date] = true;
    }
  } else {
    // 取消全选
    selectedPhotos.value = [];
    for (const date in groupedPhotos.value) {
      // 日期选择状态全部设为false取消选中
      dateSelection.value[date] = false;
    }
  }
};

// 更新全选状态
const updateAllSelectedStatus = () => {
  let allSelected = true;

  for (const date in groupedPhotos.value) {
    const group = groupedPhotos.value[date];
    const allInDateSelected = group.every((photo) => selectedPhotos.value.includes(photo.id));

    if (!allInDateSelected) {
      allSelected = false;
    }

    // 更新日期选择状态
    dateSelection.value[date] = allInDateSelected;
  }

  isAllSelected.value = allSelected;
};

// 点击选择按钮后,点击图片处理逻辑
const handlePhotoClick = (photo) => {
  if (!isSelectMode.value) {
    return;
  }
  // 查找选中列表中是否已存在该图片
  const index = selectedPhotos.value.indexOf(photo.id);
  if (index === -1) {
    // 未选中，添加到选中列表
    selectedPhotos.value.push(photo.id);
  } else {
    // 已选中，从选中列表移除
    selectedPhotos.value.splice(index, 1);
  }
};

// 批量删除图片
const handleBatchDelete = () => {
  if (selectedPhotos.value.length === 0) {
    ElMessage.warning("请先选择要删除的图片");
    return;
  }

  ElMessageBox.confirm(`确定要删除这 ${selectedPhotos.value.length} 张图片吗？此操作不可恢复`, "删除图片", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      try {
        await batchDeletePhoto(selectedPhotos.value);
        ElMessage.success("删除成功");
        selectedPhotos.value = [];
        getPhotoList(); // 刷新图片列表
      } catch (error) {
        ElMessage.error("删除失败");
      }
    })
    .catch(() => {
      ElMessage.info("已取消删除");
    });
};

// 上传图片对话框
const uploadDialogVisible = ref(false);
// 上传图片列表
const fileList = ref([]);

// 上传图片
const handleUploadPhoto = () => {
  fileList.value = [];
  uploadDialogVisible.value = true;
};

// 选择图片
const handleFileChange = (file, fileListData) => {
  fileList.value = fileListData;
};

// 图片压缩函数 - 优化版本：质量压缩+尺寸调整
const compressImage = (file, quality = 0.7, maxWidth = 1200, maxHeight = 1200) => {
  return new Promise((resolve) => {
    // 读取文件
    const reader = new FileReader();
    // 将文件读取为base64编码的url
    reader.readAsDataURL(file);
    reader.onload = (e) => {
      const img = new Image();
      img.src = e.target.result;
      img.onload = () => {
        // 计算调整后的尺寸，保持比例
        let width = img.width;
        let height = img.height;

        // 如果图片尺寸超过最大限制，则等比例缩小
        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height);
          width = Math.floor(width * ratio);
          height = Math.floor(height * ratio);
        }

        // 创建Canvas进行压缩
        const canvas = document.createElement("canvas");
        canvas.width = width;
        canvas.height = height;

        // 获取Canvas 2D绘图上下文
        const ctx = canvas.getContext("2d");
        // 使用drawImage将图片绘制到Canvas上
        ctx.drawImage(img, 0, 0, width, height);

        // 总是转换为WebP格式以获得更好的压缩效果
        const mimeType = "image/webp";

        // 转换为Blob
        canvas.toBlob(
          (blob) => {
            // 生成新的文件名，更改扩展名为webp
            const nameWithoutExtension = file.name.substring(0, file.name.lastIndexOf("."));
            const fileName = `${nameWithoutExtension}.webp`;
            resolve(new File([blob], fileName, { type: mimeType }));
          },
          mimeType,
          quality
        );
      };
    };
  });
};

// 上传图片加载
const uploadLoading = ref(false);
// 提交上传
const submitUpload = async () => {
  if (fileList.value.length === 0) {
    ElMessage.warning("请选择要上传的图片");
    return;
  }
  uploadLoading.value = true;
  try {
    // 压缩图片
    const compressedFiles = [];
    for (const file of fileList.value) {
      const compressedFile = await compressImage(file.raw);
      compressedFiles.push(compressedFile);
    }

    const uploadPromises = compressedFiles.map((file) => {
      return uploadPhoto(file, albumForm.value.id);
    });
    await Promise.all(uploadPromises); // 等待图片列表上传完成
    ElMessage.success("图片上传成功");
    uploadDialogVisible.value = false; // 关闭上传对话框
    setTimeout(() => {
      getPhotoList();
    }, 2000);
  } catch (error) {
    ElMessage.error("上传图片失败");
  } finally {
    uploadLoading.value = false;
  }
};

// 编辑相册对话框
const editDialogVisible = ref(false);
// 编辑相册加载
const editLoading = ref(false);
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
  console.log("switchShowStatus.value",switchShowStatus.value)
  try {
    const newStatus = albumForm.value.showStatus === 0 ? 1 : 0;
    console.log("newStatus",newStatus);
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
    console.log(photos);
    if (!photos || photos.length === 0) {
      ElMessage.warning("该相册暂无图片，请先上传图片");
      return;
    }
    // 反转图片列表顺序
    coverDialogPhotos.value = [...photos].reverse();
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
  router.go(-1);
};

// 获取所有图片的URL列表用于预览
const getAllPhotoUrls = () => {
  // 将groupedPhotos中的所有图片URL合并成一个数组
  const allUrls = [];
  for (const date in groupedPhotos.value) {
    const group = groupedPhotos.value[date];
    group.forEach((photo) => {
      allUrls.push(photo.url);
    });
  }
  return allUrls;
};

// 获取图片在预览列表中的索引
const getPhotoIndex = (photo) => {
  const allPhotos = [];
  for (const date in groupedPhotos.value) {
    const group = groupedPhotos.value[date];
    allPhotos.push(...group);
  }

  return allPhotos.findIndex((p) => p.id === photo.id);
};

// 获取审核状态文本
const getExamineStatusText = (status) => {
  switch (status) {
    case 0:
      return "待审核";
    case 1:
      // return "审核通过"; // 审核通过不需要显示
      return;
    case 2:
      return "审核未通过";
    default:
      return "未知状态";
  }
};

// 获取审核状态对应的类名
const getExamineStatusClass = (status) => {
  switch (status) {
    case 0:
      return "status-pending";
    case 1:
      return "status-approved";
    case 2:
      return "status-rejected";
    default:
      return "status-unknown";
  }
};
</script>

<style lang="scss" scoped>
.card {
  transition: all 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  :deep(.el-button) {
    margin: 0;
  }

  :deep(.el-card__header) {
    padding: 10px;
  }

  /* 头部第一行 */
  .header-title {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    width: 100%;
    /* 返回我的相册按钮 */
    .my-album-btn {
      margin-right: 15px;
    }

    .album-name-container {
      display: flex;
      align-items: center;
      flex: 1;

      .title {
        margin-right: 10px;
      }
    }

    .user-name-container {
      display: flex;
      align-items: center;
      width: 100%;
      margin-top: 5px;

      .user-name {
        display: inline-flex;
        align-items: center;
        background-color: #f0f2f5;
        padding: 2px 8px;
        border-radius: 12px;
        margin-left: 70px;
        font-size: 14px;
        font-weight: 500;
        color: #606266;
        transition: all 0.3s ease;

        &:hover {
          background-color: #e4e7ed;
          color: #4096ff;
        }

        .at-symbol {
          color: #4096ff;
          margin-right: 2px;
        }
      }
    }

    @media screen and (max-width: 550px) {
      flex-direction: column;
      align-items: flex-start;

      .album-name-container {
        width: 100%;
        margin: 10px 0 5px 0;
      }

      .user-name-container {
        width: 100%;
        justify-content: flex-start;
        .user-name {
          margin-left: 10px;
        }
      }
    }
    .my-album-btn {
      background: linear-gradient(135deg, #3d92eb, #1097b9);
      border: none;
      color: white;
      font-size: 20px;
      font-weight: 600;
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
    .title {
      font-size: 24px !important;
      font-weight: 600;
      margin-left: 10px;
      background: linear-gradient(135deg, #3d92eb, #6f42c1);
      -webkit-background-clip: text; // 背景被裁剪为文本前景色 非标准属性
      -webkit-text-fill-color: transparent; // 文字颜色透明
      background-clip: text; // 背景被裁剪为文本前景色 标准属性
    }
  }

  /* 头部第二行 */
  .header-actions {
    margin: 5px 0;
    display: flex;
    gap: 5px;
    align-items: center;
    justify-content: flex-end;
    /* 上传图片按钮样式 */
    .upload-photo-btn {
      background: linear-gradient(135deg, #409eff, #52a6f0);
      border: none;
      color: white;
      font-size: 18px;
      padding: 12px 20px;
      border-radius: 8px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);

      &:hover {
        transform: scale(1.05);
        box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
        background: linear-gradient(135deg, #52a6f0, #409eff);
      }

      &:active {
        transform: scale(0.98);
      }
    }
    /* 编辑相册按钮样式 */
    .edit-album-btn {
      background: linear-gradient(135deg, #e6a23c, #ebb563);
      border: none;
      color: white;
      font-size: 18px;
      padding: 12px 20px;
      border-radius: 8px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 12px rgba(230, 162, 60, 0.3);

      &:hover {
        transform: scale(1.05);
        box-shadow: 0 6px 16px rgba(230, 162, 60, 0.4);
        background: linear-gradient(135deg, #ebb563, #e6a23c);
      }

      &:active {
        transform: scale(0.98);
      }
    }
    /* 相册展示状态的开关 */
    :deep(.el-switch__core) {
      width: 77px;
      height: 30px;
      border-radius: 14px;
      box-shadow: 0 4px 10px rgba(0, 0, 0, 0.15);
      transition: all 0.3s ease;
    }

    :deep(.el-switch.is-checked .el-switch__core) {
      background: linear-gradient(135deg, #13ce66, #44e083);
    }

    :deep(.el-switch:not(.is-checked) .el-switch__core) {
      background: linear-gradient(135deg, #ff4949, #ff6b6b);
    }

    :deep(.el-switch__action) {
      transition: all 0.3s cubic-bezier(0.68, -0.55, 0.27, 1.55);
    }

    :deep(.is-text) {
      font-size: 16px;
      font-weight: 500;
    }
  }
  /* 头部第三行 */
  .header-select {
    display: flex;
    align-items: center;
    justify-content: flex-end;

    .select-all-btn {
      background: linear-gradient(135deg, #67c23a, #409800);
      border: none;
      color: white;
      font-size: 16px;
      padding: 10px 16px;
      border-radius: 6px;
      margin-right: 10px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 10px rgba(103, 194, 58, 0.3);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 14px rgba(103, 194, 58, 0.4);
        background: linear-gradient(135deg, #409800, #67c23a);
      }

      &:active {
        transform: translateY(0);
      }
    }

    .select-btn {
      background: linear-gradient(135deg, #909399, #a6a9ad);
      border: none;
      color: white;
      font-size: 16px;
      padding: 10px 16px;
      border-radius: 6px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 10px rgba(144, 147, 153, 0.3);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 14px rgba(144, 147, 153, 0.4);
        background: linear-gradient(135deg, #a6a9ad, #909399);
      }

      &:active {
        transform: translateY(0);
      }
    }

    .delete-btn {
      background: linear-gradient(135deg, #f56c6c, #ff4949);
      border: none;
      color: white;
      font-size: 16px;
      padding: 10px 16px;
      border-radius: 6px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 10px rgba(245, 108, 108, 0.3);

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 6px 14px rgba(245, 108, 108, 0.4);
        background: linear-gradient(135deg, #ff4949, #f56c6c);
      }

      &:active {
        transform: translateY(0);
      }
    }
  }
}

.photo-container {
  /* 日期 */
  .date-section {
    margin-bottom: 30px;
    .date-header {
      display: flex;
      align-items: center;
      // justify-content: space-between;
      margin-bottom: 15px;
      padding-bottom: 5px;
      border-bottom: 1px solid #eee;
      h3 {
        font-size: 18px;
        margin: 0;
      }
      .el-checkbox {
        height: 20px;
        width: 20px;
        margin-left: 10px;
        :deep(.el-checkbox__inner) {
          width: 20px;
          height: 20px;
        }
      }
    }
  }
  /* 照片列表 */
  .photo-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); /* 自动填充，每行2列 */
    gap: 15px;
    .photo-item {
      position: relative;
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
      }
      // 选中图片样式
      .selected {
        border: 4px solid #409eff;
        box-shadow: 0 0 12px rgba(64, 158, 255, 0.8);
        background-color: rgba(64, 158, 255, 0.1);
      }

      // 审核状态样式
      .examine-status {
        position: absolute;
        bottom: 5px;
        right: 5px;
        padding: 2px 8px;
        border-radius: 10px;
        font-size: 12px;
        font-weight: bold;
        color: white;
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
      }

      // 待审核状态
      .status-pending {
        background: linear-gradient(135deg, #909399, #606266);
      }
      // 审核通过状态
      // .status-approved {
      //   background: linear-gradient(135deg, #67c23a, #409800);
      // }
      // 审核未通过状态
      .status-rejected {
        background: linear-gradient(135deg, #f56c6c, #dd3e3e);
      }
      // 未知状态
      .status-unknown {
        background: linear-gradient(135deg, #e6a23c, #c68a1f);
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

      // 图片被选中时的小对勾
      .selected-icon {
        position: absolute;
        top: 5px;
        right: 5px;
        width: 30px;
        height: 30px;
        background: #409eff;
        color: white;
        border-radius: 50%;
        font-size: 20px;
      }
      &:hover {
        transform: translateY(-5px);
        .photo {
          transform: scale(1.1);
        }
      }
    }
  }

  /* 选择按钮选中图片时的样式 */
}

:deep(.el-dialog) {
  width: 40vw;
  @media screen and (max-width: 870px) {
    width: 85vw;
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
