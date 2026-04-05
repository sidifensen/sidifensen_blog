<template>
  <div class="quick-actions">
    <DetailCard title="快速操作" :icon="Operation">
      <div class="action-grid">
        <div
          v-for="item in actions"
          :key="item.path"
          class="action-item"
          @click="navigateTo(item.path)"
        >
          <div class="action-item__icon" :class="[`action-item__icon--${item.type}`]">
            <el-icon><component :is="item.icon" /></el-icon>
          </div>
          <div class="action-item__content">
            <span class="action-item__title">{{ item.title }}</span>
            <span class="action-item__desc">{{ item.description }}</span>
          </div>
          <div class="action-item__badge" v-if="item.badge > 0">
            {{ item.badge }}
          </div>
          <div class="action-item__arrow">
            <el-icon><ArrowRight /></el-icon>
          </div>
        </div>
      </div>
    </DetailCard>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import {
  Document,
  ChatLineRound,
  User,
  Picture,
  ArrowRight,
  Operation,
} from '@element-plus/icons-vue'
import DetailCard from './cards/DetailCard.vue'

/**
 * QuickActions 快速操作组件
 * @description 提供常用操作的快捷入口
 */
defineProps({
  /**
   * 操作项数据
   */
  actions: {
    type: Array,
    default: () => [],
  },
})

const router = useRouter()

const navigateTo = (path) => {
  router.push(path)
}
</script>

<style lang="scss" scoped>
.quick-actions {
  margin-bottom: 20px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 12px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border: 1px solid var(--border);
  border-radius: 16px;
  background: var(--bg-card);
  cursor: pointer;
  transition:
    transform 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    transform: translateY(-2px);
    border-color: var(--chart-line);
    box-shadow: var(--shadow-card);

    .action-item__arrow {
      color: var(--chart-line);
      transform: translateX(4px);
    }
  }

  .action-item__icon {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 38px;
    height: 38px;
    border-radius: 12px;
    line-height: 0;

    &.action-item__icon--article {
      color: var(--action-article-text);
      background: var(--action-article-bg);
    }

    &.action-item__icon--comment {
      color: var(--action-comment-text);
      background: var(--action-comment-bg);
    }

    &.action-item__icon--user {
      color: var(--action-user-text);
      background: var(--action-user-bg);
    }

    &.action-item__icon--photo {
      color: var(--action-photo-text);
      background: var(--action-photo-bg);
    }
  }

  .action-item__content {
    flex: 1;
    display: flex;
    flex-direction: column;
    gap: 3px;

    .action-item__title {
      font-size: 14px;
      font-weight: 600;
      color: var(--text-primary);
    }

    .action-item__desc {
      font-size: 12px;
      color: var(--text-muted);
    }
  }

  .action-item__arrow {
    color: var(--text-muted);
    transition:
      color 0.2s ease,
      transform 0.2s ease;
  }

  .action-item__badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-width: 22px;
    height: 22px;
    padding: 0 6px;
    border-radius: 999px;
    background: var(--danger);
    color: #fff;
    font-size: 12px;
    font-weight: 600;
  }
}

@media (max-width: 768px) {
  .action-grid {
    grid-template-columns: 1fr;
  }
}
</style>
