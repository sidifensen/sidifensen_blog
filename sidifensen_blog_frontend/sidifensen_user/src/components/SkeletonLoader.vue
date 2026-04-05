<template>
  <div class="skeleton-loader">
    <div v-for="i in displayCount" :key="i" class="skeleton-item" :class="`skeleton-${type}`">
      <!-- article 类型：图片 + 标题 + 描述 + 日期 -->
      <template v-if="type === 'article'">
        <el-skeleton-item variant="image" class="skeleton-image" />
        <el-skeleton-item variant="h3" class="skeleton-title" />
        <el-skeleton-item variant="text" class="skeleton-desc" />
        <el-skeleton-item variant="text" class="skeleton-date" />
      </template>

      <!-- card 类型：卡片样式 -->
      <template v-else-if="type === 'card'">
        <el-skeleton-item variant="image" class="skeleton-card-image" />
        <div class="skeleton-card-content">
          <el-skeleton-item variant="h3" class="skeleton-card-title" />
          <el-skeleton-item variant="text" class="skeleton-card-desc" />
        </div>
      </template>

      <!-- list 类型：列表项样式 -->
      <template v-else-if="type === 'list'">
        <el-skeleton-item variant="circle" class="skeleton-list-avatar" />
        <div class="skeleton-list-content">
          <el-skeleton-item variant="text" class="skeleton-list-title" />
          <el-skeleton-item variant="text" class="skeleton-list-subtitle" />
        </div>
      </template>

      <!-- profile 类型：圆形头像 + 名称 + 描述 -->
      <template v-else-if="type === 'profile'">
        <el-skeleton-item variant="circle" class="skeleton-profile-avatar" />
        <el-skeleton-item variant="text" class="skeleton-profile-name" />
        <el-skeleton-item variant="text" class="skeleton-profile-desc" />
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { ElSkeleton, ElSkeletonItem } from 'element-plus'

// 定义组件props
const props = defineProps({
  // 骨架屏类型：article-文章、card-卡片、list-列表项、profile-用户资料
  type: {
    type: String,
    default: 'card',
    validator: (value) => ['article', 'card', 'list', 'profile'].includes(value),
  },
  // 骨架屏数量，默认为1
  count: {
    type: Number,
    default: 1,
  },
})

// 计算显示数量，确保至少为1
const displayCount = computed(() => Math.max(1, props.count))
</script>

<style lang="scss" scoped>
// 骨架屏容器
.skeleton-loader {
  width: 100%;

  // 骨架屏项目通用样式
  .skeleton-item {
    margin-bottom: 20px;

    &:last-child {
      margin-bottom: 0;
    }
  }
}

/* article 类型样式：图片 + 标题 + 描述 + 日期 */
.skeleton-article {
  display: flex;
  flex-direction: column;
  padding: 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;

  .skeleton-image {
    width: 100%;
    height: 200px;
    margin-bottom: 16px;
  }

  .skeleton-title {
    width: 70%;
    height: 24px;
    margin-bottom: 12px;
  }

  .skeleton-desc {
    width: 100%;
    height: 16px;
    margin-bottom: 8px;
  }

  .skeleton-date {
    width: 40%;
    height: 14px;
  }
}

/* card 类型样式：卡片样式 */
.skeleton-card {
  display: flex;
  flex-direction: column;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  overflow: hidden;

  .skeleton-card-image {
    width: 100%;
    height: 160px;
  }

  .skeleton-card-content {
    padding: 16px;
  }

  .skeleton-card-title {
    width: 60%;
    height: 20px;
    margin-bottom: 12px;
  }

  .skeleton-card-desc {
    width: 100%;
    height: 14px;
  }
}

/* list 类型样式：列表项样式 */
.skeleton-list {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;

  .skeleton-list-avatar {
    width: 48px;
    height: 48px;
    margin-right: 12px;
    flex-shrink: 0;
  }

  .skeleton-list-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }

  .skeleton-list-title {
    width: 50%;
    height: 16px;
    margin-bottom: 8px;
  }

  .skeleton-list-subtitle {
    width: 80%;
    height: 14px;
  }
}

/* profile 类型样式：圆形头像 + 名称 + 描述 */
.skeleton-profile {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 24px;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;

  .skeleton-profile-avatar {
    width: 80px;
    height: 80px;
    margin-bottom: 16px;
  }

  .skeleton-profile-name {
    width: 120px;
    height: 20px;
    margin-bottom: 12px;
  }

  .skeleton-profile-desc {
    width: 180px;
    height: 14px;
  }
}

/* 浅色模式 */
html:not(.dark) {
  .skeleton-article,
  .skeleton-card,
  .skeleton-list,
  .skeleton-profile {
    background: #ffffff;
    border-color: #e2e8f0;
  }
}

/* 深色模式 */
html.dark {
  .skeleton-article,
  .skeleton-card,
  .skeleton-list,
  .skeleton-profile {
    background: #1e293b;
    border-color: #334155;
  }
}
</style>
