<template>
  <div class="user-info-card">
    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item variant="circle" style="width: 80px; height: 80px" />
          <el-skeleton-item variant="h3" style="width: 50%; margin: 12px 0" />
          <el-skeleton-item variant="text" style="width: 80%" />
          <el-skeleton-item variant="text" style="width: 60%" />
        </div>
      </template>

      <!-- 实际内容 -->
      <template #default>
        <div class="user-card-content" v-if="userInfo">
          <!-- 用户基本信息 -->
          <div class="user-basic-info">
            <el-avatar :size="80" :src="userInfo.avatar" class="clickable-avatar" @click="goToUserHomepage" />
            <h3 class="nickname clickable-nickname" @click="goToUserHomepage">
              {{ userInfo.nickname }}
            </h3>
            <p class="introduction">{{ userInfo.introduction || "这个人很懒，什么都没写~" }}</p>
          </div>

          <!-- 用户统计信息 -->
          <div class="user-stats">
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.articleCount || 0 }}</span>
              <span class="stat-label">文章</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.fansCount || 0 }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-value">{{ userInfo.followCount || 0 }}</span>
              <span class="stat-label">关注</span>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="user-actions">
            <el-button type="primary" :icon="Plus" @click="handleFollow" :loading="followLoading">
              {{ isFollowed ? "已关注" : "关注" }}
            </el-button>
            <el-button :icon="Message" @click="handleMessage"> 私信 </el-button>
          </div>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import { Message, Plus } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";

// 路由
const router = useRouter();

// Props 定义
const props = defineProps({
  userInfo: {
    type: Object,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
});

// 关注状态
const isFollowed = ref(false);
const followLoading = ref(false);

// 关注用户
const handleFollow = async () => {
  try {
    followLoading.value = true;
    // TODO: 调用关注API
    isFollowed.value = !isFollowed.value;
    ElMessage.success(isFollowed.value ? "关注成功" : "取消关注成功");
  } catch (error) {
    ElMessage.error("操作失败，请重试");
  } finally {
    followLoading.value = false;
  }
};

// 发送私信
const handleMessage = () => {
  ElMessage.info("私信功能开发中...");
};

// 跳转到用户主页
const goToUserHomepage = () => {
  if (props.userInfo?.id) {
    router.push(`/user/${props.userInfo.id}`);
  }
};
</script>

<style lang="scss" scoped>
// 用户信息卡片容器
.user-info-card {
  background-color: var(--el-bg-color);
  border-radius: 8px;
  padding: 20px;
  box-shadow: var(--el-box-shadow-light);

  // 骨架屏样式
  .skeleton-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 20px 0;
  }

  // 用户卡片内容
  .user-card-content {
    // 基本信息区域
    .user-basic-info {
      display: flex;
      flex-direction: column;
      align-items: center;
      text-align: center;
      margin-bottom: 20px;

      .nickname {
        margin: 12px 0 8px;
        font-size: 18px;
        font-weight: 600;
        color: var(--el-text-color-primary);
      }

      // 可点击的头像样式
      .clickable-avatar {
        cursor: pointer;
        transition: transform 0.2s ease, box-shadow 0.2s ease;

        &:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
      }

      // 可点击的昵称样式
      .clickable-nickname {
        cursor: pointer;
        transition: color 0.2s ease;

        &:hover {
          color: var(--el-color-primary);
        }
      }

      .introduction {
        margin: 0;
        font-size: 14px;
        color: var(--el-text-color-regular);
        line-height: 1.5;
      }
    }

    // 统计信息区域
    .user-stats {
      display: flex;
      justify-content: space-around;
      padding: 15px 0;
      border-top: 1px solid var(--el-border-color-lighter);
      border-bottom: 1px solid var(--el-border-color-lighter);
      margin-bottom: 20px;

      .stat-item {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 4px;

        .stat-value {
          font-size: 18px;
          font-weight: 600;
          color: var(--el-text-color-primary);
        }

        .stat-label {
          font-size: 12px;
          color: var(--el-text-color-secondary);
        }
      }
    }

    // 操作按钮区域
    .user-actions {
      display: flex;
      gap: 12px;

      .el-button {
        flex: 1;
      }
    }
  }
}
</style>
