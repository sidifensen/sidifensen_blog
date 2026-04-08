<template>
  <div class="mobile-card-list" :class="{ 'has-selection': showSelection }">
    <!-- 选择提示 -->
    <div v-if="showSelection" class="selection-hint">已选择 {{ selectedCount }} 项</div>

    <!-- 卡片列表 -->
    <div class="card-list">
      <el-card v-for="item in data" :key="item.id" class="item-card" :class="{ 'is-selected': isSelected(item) }">
        <div class="card-content">
          <!-- 选择器 -->
          <div v-if="showSelection" class="card-selector">
            <el-checkbox :model-value="isSelected(item)" @change="handleSelect(item)" />
          </div>

          <!-- 封面 -->
          <div v-if="showCover" class="card-cover">
            <el-image v-if="item.coverUrl" :src="item.coverUrl" class="cover-image" :preview-src-list="[item.coverUrl]" fit="cover" preview-teleported />
            <div v-else class="no-cover">暂无封面</div>
          </div>

          <!-- 内容区域 -->
          <div class="card-info">
            <!-- 头部（ID和状态） -->
            <div class="card-header">
              <span class="item-id">#{{ item.id }}</span>
              <StatusBadge v-if="showStatus" :value="item.status" type="status" />
              <StatusBadge v-if="showExamineStatus" :value="item.examineStatus" type="examine" />
            </div>

            <!-- 名称/标题 -->
            <div v-if="showName" class="card-name">{{ item.name }}</div>
            <div v-if="showTitle" class="card-title">{{ item.title }}</div>

            <!-- 描述 -->
            <div v-if="showDescription && item.description" class="card-description">
              {{ item.description }}
            </div>

            <!-- 用户信息 -->
            <div v-if="showUser && item.userName" class="card-user">
              <span class="label">用户:</span>
              <span class="value">{{ item.userName }}</span>
            </div>

            <!-- 元信息 -->
            <div v-if="showMeta" class="card-meta">
              <span v-if="item.createTime" class="meta-item">
                <span class="label">创建:</span>
                <span>{{ item.createTime }}</span>
              </span>
            </div>

            <!-- 操作按钮 -->
            <div v-if="showActions" class="card-actions">
              <el-button v-if="hasViewAction" type="info" size="small" @click="$emit('view', item)"> 查看 </el-button>
              <el-button v-if="hasEditAction" type="primary" size="small" @click="$emit('edit', item)"> 编辑 </el-button>
              <el-button v-if="hasAuditAction" type="primary" size="small" @click="$emit('audit', item)"> 审核 </el-button>
              <el-button v-if="hasRejectAction" type="warning" size="small" @click="$emit('reject', item)"> 拒绝 </el-button>
              <el-button v-if="hasDeleteAction" type="danger" size="small" @click="$emit('delete', item)"> 删除 </el-button>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 空状态 -->
    <div v-if="data.length === 0" class="empty-state">
      <el-empty description="暂无数据" />
    </div>
  </div>
</template>

<script setup>
/**
 * MobileCardList 移动端卡片列表组件
 *
 * 功能说明：
 * - 移动端视图下的卡片列表展示，与 DataTable 对应
 * - 支持多选、封面、名称/标题、描述、用户、状态、审核状态等展示
 * - 支持操作按钮（查看、编辑、审核、拒绝、删除）
 * - 选中状态有视觉反馈（边框高亮）
 *
 * 使用方式：
 * ```vue
 * <MobileCardList :data="tableData" :showCover="true" :showActions="true" @edit="handleEdit" />
 * ```
 */

import StatusBadge from '@/components/common/StatusBadge.vue'

// Props
const props = defineProps({
  // 数据
  data: {
    type: Array,
    default: () => [],
  },
  // 选中项（用于多选）
  selectedItems: {
    type: Array,
    default: () => [],
  },
  // 显示控制
  showSelection: {
    type: Boolean,
    default: false,
  },
  showCover: {
    type: Boolean,
    default: false,
  },
  showName: {
    type: Boolean,
    default: false,
  },
  showTitle: {
    type: Boolean,
    default: false,
  },
  showDescription: {
    type: Boolean,
    default: false,
  },
  showUser: {
    type: Boolean,
    default: false,
  },
  showStatus: {
    type: Boolean,
    default: false,
  },
  showExamineStatus: {
    type: Boolean,
    default: false,
  },
  showMeta: {
    type: Boolean,
    default: true,
  },
  showActions: {
    type: Boolean,
    default: true,
  },
  // 操作按钮
  hasViewAction: {
    type: Boolean,
    default: false,
  },
  hasEditAction: {
    type: Boolean,
    default: false,
  },
  hasDeleteAction: {
    type: Boolean,
    default: true,
  },
  hasAuditAction: {
    type: Boolean,
    default: false,
  },
  hasRejectAction: {
    type: Boolean,
    default: false,
  },
})

// Emits
const emit = defineEmits(['select', 'view', 'edit', 'delete', 'audit', 'reject'])

// 选中数量
const selectedCount = props.selectedItems.length

// 是否选中
const isSelected = (item) => {
  return props.selectedItems.some((selected) => selected.id === item.id)
}

// 选择处理
const handleSelect = (item) => {
  emit('select', item)
}
</script>

<style lang="scss" scoped>
.mobile-card-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 10px;

  .selection-hint {
    padding: 8px 12px;
    background-color: var(--el-fill-color-light);
    border-radius: 8px;
    font-size: 14px;
    color: var(--el-text-color-secondary);
    text-align: center;
  }

  .card-list {
    display: flex;
    flex-direction: column;
    gap: 12px;

    .item-card {
      border-radius: 8px;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
      }

      &.is-selected {
        border: 2px solid var(--admin-primary);
        box-shadow: 0 0 12px var(--admin-primary-light);
      }

      .card-content {
        display: flex;
        flex-direction: column;
        gap: 12px;

        .card-selector {
          align-self: flex-start;
        }

        .card-cover {
          width: 100%;
          position: relative;

          .cover-image {
            width: 100%;
            height: 120px;
            border-radius: 6px;
            cursor: pointer;
            object-fit: cover;
            transition: all 0.3s ease;

            &:hover {
              transform: scale(1.02);
              box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            }
          }

          .no-cover {
            width: 100%;
            height: 120px;
            background-color: #f5f5f5;
            border: 1px dashed #ddd;
            border-radius: 6px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 12px;
            color: #999;
          }
        }

        .card-info {
          display: flex;
          flex-direction: column;
          gap: 8px;

          .card-header {
            display: flex;
            align-items: center;
            gap: 8px;
            flex-wrap: wrap;

            .item-id {
              font-size: 12px;
              color: #666;
              background-color: #f5f5f5;
              padding: 2px 6px;
              border-radius: 4px;
            }
          }

          .card-name,
          .card-title {
            font-size: 16px;
            font-weight: 600;
            color: #333;
            line-height: 1.4;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .card-description {
            font-size: 13px;
            color: #666;
            overflow: hidden;
            text-overflow: ellipsis;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
          }

          .card-user,
          .card-meta {
            font-size: 13px;
            color: #666;
            display: flex;
            align-items: center;
            gap: 4px;

            .label {
              font-weight: 500;
              color: #888;
            }

            .value {
              color: #555;
            }
          }

          .card-actions {
            display: flex;
            gap: 6px;
            justify-content: center;
            padding-top: 8px;
            border-top: 1px solid var(--el-border-color-lighter);
            flex-wrap: wrap;

            :deep(.el-button) {
              margin-left: 0;
              flex: 1;
              min-width: 60px;
              font-size: 12px;
              padding: 6px 10px;
              height: auto;
              border-radius: 4px;
            }
          }
        }
      }
    }
  }

  .empty-state {
    padding: 60px 0;
    text-align: center;
  }
}
</style>
