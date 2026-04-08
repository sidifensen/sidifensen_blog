# 组件分类索引

> 本文档用于快速查找组件，按功能分类，便于开发时快速定位所需组件。

---

## 目录

- [1. 布局组件 (layout)](#1-布局组件-layout)
- [2. 通用基础组件 (common)](#2-通用基础组件-common)
- [3. 业务操作组件 (actions)](#3-业务操作组件-actions)
- [4. 数据展示组件 (data)](#4-数据展示组件-data)
- [5. 卡片组件 (cards)](#5-卡片组件-cards)
- [6. 图表组件 (charts)](#6-图表组件-charts)
- [7. 搜索筛选组件 (search)](#7-搜索筛选组件-search)
- [8. 页面管理组件 (management)](#8-页面管理组件-management)

---

## 1. 布局组件 (layout)

| 组件         | 说明             | 用途                                                               |
| ------------ | ---------------- | ------------------------------------------------------------------ |
| `Layout.vue` | 后台整体布局组件 | 包含侧边栏菜单、顶栏导航（消息通知、用户信息、主题切换）、主内容区 |

---

## 2. 通用基础组件 (common)

| 组件              | 说明         | 用途                                                                    |
| ----------------- | ------------ | ----------------------------------------------------------------------- |
| `SvgIcon.vue`     | SVG 图标组件 | 通过 `use` 标签引用 SVG symbol，支持颜色、大小、名称参数                |
| `StatusBadge.vue` | 状态标签组件 | 展示审核状态（待审核/已审核/未通过）、通用状态（正常/禁用）、转载类型等 |
| `Dark.vue`        | 主题切换组件 | 白天/黑夜模式切换，带动画效果和波纹反馈                                 |
| `404.vue`         | 404 错误页面 | 页面未找到时显示                                                        |

---

## 3. 业务操作组件 (actions)

| 组件               | 说明             | 用途                                                     |
| ------------------ | ---------------- | -------------------------------------------------------- |
| `BatchActions.vue` | 批量操作按钮组件 | 批量审核、批量拒绝、批量删除，支持选中数量显示和加载状态 |
| `QuickActions.vue` | 快速操作入口组件 | 展示常用操作的快捷入口卡片（文章、评论、用户、照片管理） |

---

## 4. 数据展示组件 (data)

| 组件                 | 说明               | 用途                                                                         |
| -------------------- | ------------------ | ---------------------------------------------------------------------------- |
| `DataTable.vue`      | 通用数据表格组件   | 支持多选、封面、ID、名称、标题、用户、状态、审核状态、时间等列，支持操作按钮 |
| `TableActions.vue`   | 表格操作按钮组件   | 查看、详情、编辑、审核、拒绝、删除等操作按钮的组合展示                       |
| `Pagination.vue`     | 分页组件           | 基于 `el-pagination` 的分页控件，支持 `v-model` 绑定                         |
| `MobileCardList.vue` | 移动端卡片列表组件 | 移动端视图下的卡片列表展示，数据与桌面端表格对应                             |

---

## 5. 卡片组件 (cards)

| 组件                       | 说明         | 用途                                                           |
| -------------------------- | ------------ | -------------------------------------------------------------- |
| `StatCard.vue`             | 统计数值卡片 | 展示单个统计指标（数值、趋势、图标），支持 `type` 参数区分样式 |
| `DetailCard.vue`           | 详情卡片组件 | 带标题和内容插槽的通用卡片，可嵌入图表或其他内容               |
| `UserGrowthCard.vue`       | 用户增长卡片 | 集成 `UserGrowthChart` 图表的用户增长统计卡片                  |
| `ArticleStatusCard.vue`    | 文章状态卡片 | 集成 `ArticleDonutChart` 图表的文章状态统计卡片                |
| `InteractionTrendCard.vue` | 互动趋势卡片 | 集成 `InteractionTrendChart` 图表的互动趋势统计卡片            |
| `VipStatisticsCard.vue`    | VIP 统计卡片 | 集成 `VipStatisticsChart` 图表的 VIP 统计卡片                  |
| `UserActivityCard.vue`     | 用户活跃卡片 | 集成 `UserActivityChart` 图表的用户活跃统计卡片                |
| `VisitorTrendCard.vue`     | 访问趋势卡片 | 集成 `VisitorTrendChart` 图表的访问趋势统计卡片                |

---

## 6. 图表组件 (charts)

| 组件                        | 说明           | 用途                                                |
| --------------------------- | -------------- | --------------------------------------------------- |
| `UserActivityChart.vue`     | 用户活跃度饼图 | 展示活跃用户与非活跃用户的比例（AntV G2 饼图）      |
| `UserGrowthChart.vue`       | 用户增长趋势图 | 展示用户数量随时间的变化趋势（AntV G2 折线/柱状图） |
| `VipStatisticsChart.vue`    | VIP 统计图表   | 展示 VIP 用户相关统计数据                           |
| `VisitorTrendChart.vue`     | 访问趋势图表   | 展示访客数量随时间的变化趋势                        |
| `ArticleDonutChart.vue`     | 文章分类环形图 | 展示文章分类占比（AntV G2 环形图）                  |
| `InteractionTrendChart.vue` | 互动趋势图表   | 展示评论、点赞等互动数据随时间的变化                |

---

## 7. 搜索筛选组件 (search)

| 组件                      | 说明             | 用途                                                   |
| ------------------------- | ---------------- | ------------------------------------------------------ |
| `SearchFilters.vue`       | 搜索筛选容器组件 | 组合关键词搜索、时间范围选择的容器组件                 |
| `KeywordSearch.vue`       | 关键词搜索输入框 | 带搜索图标的关键词输入框，支持防抖搜索、`v-model` 绑定 |
| `TimeRangePicker.vue`     | 时间范围选择器   | 开始/结束时间的日期时间选择器组合                      |
| `ExamineStatusSelect.vue` | 审核状态下拉选择 | 筛选审核状态（待审核/已审核/未通过）的下拉选择器       |
| `UserSearchSelect.vue`    | 用户搜索选择组件 | 搜索并选择用户，通常配合下拉框使用                     |
| `SearchButtons.vue`       | 搜索操作按钮组   | 包含搜索、重置等操作按钮                               |

---

## 8. 页面管理组件 (management)

| 组件                 | 说明             | 用途                                                            |
| -------------------- | ---------------- | --------------------------------------------------------------- |
| `ManagementCard.vue` | 管理页面容器卡片 | 统一管理页面布局：标题、筛选区、表格/卡片视图、分页，支持响应式 |
| `PageHeader.vue`     | 页面头部组件     | 页面标题、面包屑、快捷操作按钮等头部内容                        |
| `EditDialog.vue`     | 编辑对话框组件   | 通用的编辑表单对话框，包含标题、宽度、表单验证、提交/取消按钮   |

---

## 组件命名规范

| 前缀                           | 含义         | 示例                    |
| ------------------------------ | ------------ | ----------------------- |
| `*Chart.vue`                   | 图表组件     | `UserActivityChart.vue` |
| `*Card.vue`                    | 卡片组件     | `StatCard.vue`          |
| `*Actions.vue`                 | 操作按钮组件 | `BatchActions.vue`      |
| `*Filters.vue` / `*Select.vue` | 筛选选择组件 | `SearchFilters.vue`     |
| `*Dialog.vue`                  | 对话框组件   | `EditDialog.vue`        |
| `*Table.vue`                   | 表格组件     | `DataTable.vue`         |

---

## 使用示例

### 1. 页面布局

```vue
<template>
  <Layout>
    <RouterView />
  </Layout>
</template>
```

### 2. 管理页面结构

```vue
<ManagementCard title="用户管理" :showTimeFilter="true" :total="100" @search="fetchData">
  <template #filters>
    <ExamineStatusSelect v-model="searchExamineStatus" />
    <KeywordSearch v-model="searchKeyword" />
  </template>
  <template #batch-actions>
    <BatchActions :selectedCount="selectedRows.length" @batch-delete="handleBatchDelete" />
  </template>
  <template #table-view>
    <DataTable :data="tableData" :showActions="true" @delete="handleDelete" />
  </template>
</ManagementCard>
```

### 3. 卡片 + 图表组合

```vue
<StatCard label="总用户数" :value="1000" :icon="User" trendType="positive" trendText="+12.5%" />
```

### 4. 搜索筛选

```vue
<SearchFilters :showKeyword="true" :showTimeRange="true" @change="handleSearch">
  <ExamineStatusSelect v-model="status" />
</SearchFilters>
```
