<template>
  <div class="data-table">
    <el-table
      v-loading="loading"
      :data="data"
      class="table"
      style="width: 100%"
      v-bind="$attrs"
      :row-style="{ height: 'auto' }"
      :cell-style="{ padding: '8px 0' }"
      @selection-change="handleSelectionChange"
    >
      <!-- 多选列 -->
      <el-table-column v-if="showSelection" type="selection" width="30" />

      <!-- 封面列 -->
      <el-table-column v-if="showCover" prop="coverUrl" label="封面" :width="coverWidth">
        <template #default="{ row }">
          <div class="cover-container">
            <el-image v-if="row.coverUrl" :src="row.coverUrl" class="cover-image" :preview-src-list="[row.coverUrl]" fit="cover" preview-teleported />
            <div v-else class="no-cover">暂无封面</div>
          </div>
        </template>
      </el-table-column>

      <!-- ID列 -->
      <el-table-column v-if="showId" prop="id" label="ID" :width="idWidth" />

      <!-- 名称列 -->
      <el-table-column v-if="showName" prop="name" label="名称" :min-width="nameMinWidth">
        <template #default="{ row }">
          <el-tooltip :content="row.name" placement="top-start">
            <div class="name-text">{{ row.name }}</div>
          </el-tooltip>
        </template>
      </el-table-column>

      <!-- 标题列（用于文章等） -->
      <el-table-column v-if="showTitle" prop="title" label="标题" :min-width="titleMinWidth">
        <template #default="{ row }">
          <el-tooltip :content="row.title" placement="top-start">
            <div class="title-text">{{ row.title }}</div>
          </el-tooltip>
        </template>
      </el-table-column>

      <!-- 用户名列 -->
      <el-table-column v-if="showUser" prop="userName" label="用户" :width="userWidth" />

      <!-- 状态列 -->
      <el-table-column v-if="showStatus" prop="status" label="状态" :width="statusWidth">
        <template #default="{ row }">
          <StatusBadge :value="row.status" type="status" />
        </template>
      </el-table-column>

      <!-- 审核状态列 -->
      <el-table-column v-if="showExamineStatus" prop="examineStatus" label="审核状态" :width="examineStatusWidth">
        <template #default="{ row }">
          <StatusBadge :value="row.examineStatus" type="examine" />
        </template>
      </el-table-column>

      <!-- 创建时间列 -->
      <el-table-column v-if="showCreateTime" prop="createTime" label="创建时间" :width="createTimeWidth" sortable />

      <!-- 更新时间列 -->
      <el-table-column v-if="showUpdateTime" prop="updateTime" label="更新时间" :width="updateTimeWidth" sortable />

      <!-- 操作列 -->
      <el-table-column v-if="showActions" :label="actionsLabel" :width="actionsWidth" fixed="right">
        <template #default="{ row }">
          <TableActions
            :show-view="hasViewAction"
            :show-detail="hasDetailAction"
            :show-edit="hasEditAction"
            :show-delete="hasDeleteAction"
            :show-audit="hasAuditAction"
            :show-reject="hasRejectAction"
            :detail-text="detailText"
            @view="$emit('view', row)"
            @detail="$emit('detail', row)"
            @edit="$emit('edit', row)"
            @delete="$emit('delete', row)"
            @audit="$emit('audit', row)"
            @reject="$emit('reject', row)"
          />
        </template>
      </el-table-column>

      <!-- 默认插槽 -->
      <slot />
    </el-table>
  </div>
</template>

<script setup>
/**
 * DataTable 通用数据表格组件
 *
 * 功能说明：
 * - 通用的数据表格组件，支持多选、封面、ID、名称、标题、用户、状态、审核状态、创建/更新时间等列
 * - 支持通过 props 控制各列的显示/隐藏
 * - 支持操作按钮（查看、编辑、删除、审核、拒绝）
 * - 支持响应式布局（移动端友好）
 * - 集成 StatusBadge 和 TableActions 组件
 *
 * 使用方式：
 * ```vue
 * <DataTable :data="tableData" :showCover="true" :showActions="true" @edit="handleEdit" @delete="handleDelete" />
 * ```
 */

import StatusBadge from '@/components/common/StatusBadge.vue'
import TableActions from '@/components/data/TableActions.vue'

// Props
const props = defineProps({
  // 数据
  data: {
    type: Array,
    default: () => [],
  },
  // 加载状态
  loading: {
    type: Boolean,
    default: false,
  },
  // 列显示控制
  showSelection: {
    type: Boolean,
    default: false,
  },
  showCover: {
    type: Boolean,
    default: false,
  },
  showId: {
    type: Boolean,
    default: true,
  },
  showName: {
    type: Boolean,
    default: false,
  },
  showTitle: {
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
  showCreateTime: {
    type: Boolean,
    default: true,
  },
  showUpdateTime: {
    type: Boolean,
    default: false,
  },
  showActions: {
    type: Boolean,
    default: true,
  },
  // 列宽度
  coverWidth: {
    type: [Number, String],
    default: 120,
  },
  idWidth: {
    type: [Number, String],
    default: 60,
  },
  nameMinWidth: {
    type: [Number, String],
    default: 150,
  },
  titleMinWidth: {
    type: [Number, String],
    default: 180,
  },
  userWidth: {
    type: [Number, String],
    default: 120,
  },
  statusWidth: {
    type: [Number, String],
    default: 80,
  },
  examineStatusWidth: {
    type: [Number, String],
    default: 80,
  },
  createTimeWidth: {
    type: [Number, String],
    default: 110,
  },
  updateTimeWidth: {
    type: [Number, String],
    default: 110,
  },
  actionsWidth: {
    type: [Number, String],
    default: 200,
  },
  actionsLabel: {
    type: String,
    default: '操作',
  },
  // 操作按钮控制
  hasViewAction: {
    type: Boolean,
    default: false,
  },
  hasDetailAction: {
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
  detailText: {
    type: String,
    default: '详情',
  },
})

// Emits
const emit = defineEmits(['selection-change', 'view', 'detail', 'edit', 'delete', 'audit', 'reject'])

// 选择变化
const handleSelectionChange = (selection) => {
  emit('selection-change', selection)
}
</script>

<style lang="scss" scoped>
.data-table {
  flex: 1;
  display: flex;
  flex-direction: column;
  margin-top: 16px;

  .table {
    flex: 1;
    display: flex;
    flex-direction: column;

    :deep(.el-table__header-wrapper) {
      background-color: var(--el-bg-color);

      th {
        font-weight: 600;
        color: var(--text-regular);
      }
    }

    :deep(.el-table__body-wrapper) {
      tr {
        td {
          color: var(--text-muted);
          padding: 12px 0;
          vertical-align: middle;

          .cell {
            display: flex;
            align-items: center;
            justify-content: flex-start;
            min-height: 40px;
          }
        }
      }
    }

    :deep(.el-table__fixed-right) {
      box-shadow: -3px 0 10px rgba(0, 0, 0, 0.05);
    }

    // 名称文本
    .name-text,
    .title-text {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
      cursor: pointer;

      &:hover {
        color: var(--admin-primary);
      }
    }

    // 封面容器
    .cover-container {
      display: flex;
      justify-content: center;
      align-items: center;
      height: 100%;

      .cover-image {
        width: 100px;
        height: 60px;
        border-radius: 6px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
        }
      }

      .no-cover {
        width: 100px;
        height: 60px;
        background-color: var(--bg-page);
        border: 1px dashed var(--border);
        border-radius: 6px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 12px;
        color: var(--text-placeholder);
      }
    }
  }
}

// 响应式
@media screen and (max-width: 768px) {
  .data-table {
    .table {
      max-height: calc(100vh - 240px);
    }
  }
}
</style>
