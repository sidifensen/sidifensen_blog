<template>
  <div class="article-content">
    <!-- 加载状态 -->
    <el-skeleton :loading="loading" animated>
      <template #template>
        <div class="skeleton-content">
          <el-skeleton-item variant="h1" style="width: 50%; margin-bottom: 20px" />
          <div class="article-meta-skeleton">
            <el-skeleton-item variant="text" style="width: 100px" />
            <el-skeleton-item variant="text" style="width: 100px" />
            <el-skeleton-item variant="text" style="width: 100px" />
          </div>
          <el-skeleton-item variant="text" style="width: 100%; height: 300px; margin: 20px 0" />
          <el-skeleton-item variant="text" style="width: 90%" />
          <el-skeleton-item variant="text" style="width: 95%" />
          <el-skeleton-item variant="text" style="width: 85%" />
        </div>
      </template>

      <!-- 实际内容 -->
      <template #default>
        <div class="article-main" v-if="article">
          <!-- 文章标题区 -->
          <div class="article-header">
            <div class="title-row">
              <h1 class="article-title">{{ article.title }}</h1>
              <span v-if="isVipArticle" class="vip-article-badge">VIP 文章</span>
            </div>
            <p v-if="isVipArticle" class="title-tip">当前文章属于 VIP 可见内容，只有有效会员和作者本人可以查看。</p>
          </div>

          <!-- 移动端作者信息 -->
          <MobileAuthorInfo v-if="userInfo" :user-info="userInfo" :loading="userLoading" />

          <!-- 文章元信息 -->
          <div class="article-meta">
            <!-- 第一行：基础信息 -->
            <div class="meta-row first-row">
              <div class="basic-info">
                <div class="basic-info-content">
                  <span class="reprint-type">
                    <el-tag :type="article.reprintType === 0 ? 'success' : 'warning'" size="small" effect="light">
                      {{ article.reprintType === 0 ? "原创" : "转载" }}
                    </el-tag>
                  </span>
                  <span class="publish-time">
                    <el-icon>
                      <Clock />
                    </el-icon>
                    {{ article.createTime }}
                  </span>
                </div>
                <!-- 移动端编辑按钮 - 与发布时间在同一行 -->
                <div class="edit-actions mobile-edit" v-if="isCurrentUser">
                  <el-button link type="info" size="small" :icon="Edit" @click="handleEditArticle"> 编辑文章 </el-button>
                </div>
              </div>
              <div class="stats-info">
                <span class="read-count">
                  <el-icon>
                    <View />
                  </el-icon>
                  {{ formatCompactNumber(article.readCount || 0) }} 阅读
                </span>
                <span class="like-count">
                  <svg-icon name="like" width="14px" height="14px" color="#909399" />
                  {{ formatCompactNumber(article.likeCount || 0) }} 点赞
                </span>
                <span class="collect-count">
                  <el-icon>
                    <Star />
                  </el-icon>
                  {{ formatCompactNumber(article.collectCount || 0) }} 收藏
                </span>
              </div>
              <!-- 编辑按钮 - 只有当前用户是文章作者时才显示（桌面端） -->
              <div class="edit-actions desktop-edit" v-if="isCurrentUser">
                <el-button link type="info" size="small" :icon="Edit" @click="handleEditArticle"> 编辑文章 </el-button>
              </div>
            </div>

            <!-- 第二行：标签信息 -->
            <div class="meta-row second-row tags-row">
              <div class="article-tags">
                <span>文章标签：</span>
                <div class="tags-container">
                  <el-tag v-for="tag in tagList" :key="tag" size="small" effect="light" class="tag-clickable" @click="handleTagClick(tag)">
                    {{ tag }}
                  </el-tag>
                </div>
              </div>
            </div>

            <!-- 第三行：专栏信息 -->
            <div class="meta-row third-row columns-row">
              <div class="article-columns">
                <span>文章专栏：</span>
                <div class="columns-container">
                  <el-tag v-for="column in article.columns || []" :key="column.id" type="success" size="small" effect="light" class="column-clickable" @click="handleColumnClick(column)">
                    {{ column.name }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>

          <!-- 文章描述 -->
          <div class="article-desc" v-if="article.description">
            <el-alert :title="article.description" type="info" :closable="false" />
          </div>

          <!-- 文章内容 -->
          <div class="article-body" v-html="renderContent" @click="handleCodeBlockClick"></div>
        </div>
      </template>
    </el-skeleton>
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Clock, View, Star, Edit } from "@element-plus/icons-vue";
import { useUserStore } from "@/stores/userStore";
import { formatCompactNumber } from "@/utils/formatNumber";
import MobileAuthorInfo from "./MobileAuthorInfo.vue";

// 路由
const router = useRouter();
const userStore = useUserStore();

// Props 定义
const props = defineProps({
  article: {
    type: Object,
    default: () => null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
  userInfo: {
    type: Object,
    default: () => null,
  },
  userLoading: {
    type: Boolean,
    default: false,
  },
});

// 渲染富文本内容
const renderContent = computed(() => {
  if (!props.article?.content) {
    return "";
  }
  return props.article.content;
});

// 处理标签列表
const tagList = computed(() => {
  if (!props.article?.tag) return [];
  return props.article.tag.split(",").filter((tag) => tag.trim() !== "");
});

// 标记当前文章是否为 VIP 可见文章
const isVipArticle = computed(() => props.article?.visibleRange === 3);

// 判断当前用户是否为文章作者
const isCurrentUser = computed(() => {
  if (!userStore.user?.id || !props.article?.userId) return false;
  return userStore.user.id === props.article.userId;
});

// 点击标签跳转到搜索页面
const handleTagClick = (tag) => {
  if (!tag || !tag.trim()) return;

  // 跳转到搜索页面，并传递标签参数
  router.push({
    path: "/search",
    query: {
      keyword: tag.trim(),
      type: "tag",
    },
  });
};

// 点击专栏跳转到专栏详情页
const handleColumnClick = (column) => {
  if (!column?.id || !column?.userId) {
    ElMessage.warning("专栏信息异常");
    return;
  }

  // 跳转到专栏详情页
  router.push({
    path: `/user/${column.userId}/column/${column.id}`,
  });
};

// 复制代码块内容
const copyCodeBlock = async (codeElement) => {
  try {
    // 获取代码文本内容
    const codeText = codeElement.textContent || codeElement.innerText;

    // 使用 Clipboard API 复制文本
    await navigator.clipboard.writeText(codeText);

    // 显示成功提示
    ElMessage.success("代码已复制到剪贴板");
  } catch (error) {
    // 静默处理
  }
};

// 处理代码块点击事件
const handleCodeBlockClick = (event) => {
  const target = event.target;

  // 如果点击的是代码块本身，复制内容
  if (target.tagName === "PRE" || target.closest("pre")) {
    const codeElement = target.tagName === "PRE" ? target : target.closest("pre");
    copyCodeBlock(codeElement);
  }
};

// 编辑文章
const handleEditArticle = () => {
  if (!props.article?.id) {
    ElMessage.warning("文章信息异常");
    return;
  }

  // 使用完整的页面跳转，确保页面完全重新加载
  const editorUrl = `/editor?articleId=${props.article.id}`;
  window.location.href = editorUrl;
};
</script>

<style lang="scss" scoped>
// 文章内容容器
.article-content {
  padding: 30px 30px;

  // 骨架屏样式
  .skeleton-content {
    .article-meta-skeleton {
      display: flex;
      gap: 20px;
      margin: 16px 0;
    }
  }

  // 文章主体
  .article-main {
    // 文章标题区
    .article-header {
      margin-bottom: 20px;

      .title-row {
        display: flex;
        align-items: center;
        gap: 12px;
        flex-wrap: wrap;

        .article-title {
          margin: 0;
          font-size: 28px;
          font-weight: 600;
          color: var(--el-text-color-primary);
          line-height: 1.4;
        }

        .vip-article-badge {
          display: inline-flex;
          align-items: center;
          height: 30px;
          padding: 0 12px;
          border-radius: 999px;
          background: color-mix(in srgb, var(--el-color-warning) 14%, transparent);
          border: 1px solid color-mix(in srgb, var(--el-color-warning) 28%, transparent);
          color: var(--el-color-warning-dark-2);
          font-size: 13px;
          font-weight: 700;
          flex-shrink: 0;
        }
      }

      .title-tip {
        margin: 10px 0 0;
        font-size: 14px;
        line-height: 1.7;
        color: var(--el-text-color-secondary);
      }
    }

    // 文章元信息
    .article-meta {
      margin-bottom: 16px;

      // 元信息行样式
      .meta-row {
        display: flex;
        align-items: center;
        color: var(--el-text-color-secondary);
        font-size: 14px;

        // 第一行：基础信息
        &.first-row {
          gap: 20px;
          margin-bottom: 8px;

          .basic-info {
            display: flex;
            gap: 20px;
            align-items: center;

            .basic-info-content {
              display: flex;
              gap: 20px;
              align-items: center;

              .reprint-type {
                align-items: center;
                display: flex;
              }

              .publish-time {
                display: flex;
                align-items: center;
                gap: 4px;
                line-height: 1;
              }
            }

            // 移动端编辑按钮样式
            .edit-actions.mobile-edit {
              display: none; // 桌面端隐藏
            }
          }

          .stats-info {
            display: flex;
            gap: 20px;
            align-items: center;

            .read-count,
            .like-count,
            .collect-count {
              display: flex;
              align-items: center;
              gap: 4px;
              line-height: 1;
            }
          }

          // 编辑按钮区域
          .edit-actions {
            display: flex;
            align-items: center;
            flex-shrink: 0; // 防止按钮被压缩

            .el-button {
              border-radius: 6px;
              font-size: 13px;
              padding: 6px 12px;
              height: auto;
            }

            // 桌面端编辑按钮
            &.desktop-edit {
              display: flex;
            }
          }
        }

        // 第二行：标签信息
        &.second-row.tags-row {
          margin-bottom: 8px;

          .article-tags {
            display: flex;
            align-items: center;
            gap: 8px;

            .tags-container {
              display: flex;
              gap: 6px;
              flex-wrap: wrap;

              // 可点击的标签样式
              .tag-clickable {
                cursor: pointer;
                transition: all 0.3s ease;

                &:hover {
                  transform: translateY(-2px);
                  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
                }
              }
            }
          }
        }

        // 第三行：专栏信息
        &.third-row.columns-row {
          .article-columns {
            display: flex;
            align-items: center;
            gap: 8px;

            .columns-container {
              display: flex;
              gap: 6px;
              flex-wrap: wrap;

              // 可点击的专栏样式
              .column-clickable {
                cursor: pointer;
                transition: all 0.3s ease;

                &:hover {
                  transform: translateY(-2px);
                  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
                }
              }
            }
          }
        }
      }
    }

    // 文章描述
    .article-desc {
      margin-bottom: 30px;
    }

    // 文章内容
    .article-body {
      margin-bottom: 50px;
      line-height: 1.8;
      font-size: 16px;
      color: var(--el-text-color-primary);

      :deep(h1, h2, h3, h4, h5, h6) {
        margin-top: 24px;
        margin-bottom: 16px;
        font-weight: 600;
        line-height: 1.25;
      }

      :deep(p) {
        margin-bottom: 16px;
      }

      :deep(img) {
        width: 100%;
        border-radius: 4px;
      }

      :deep(pre) {
        position: relative;
        padding: 16px;
        overflow: auto;
        font-size: 14px;
        line-height: 1.45;
        background-color: var(--el-fill-color-light) !important;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background-color: var(--el-fill-color) !important;
        }

        // 复制按钮样式
        &::before {
          content: "复制";
          position: absolute;
          top: 8px;
          right: 8px;
          padding: 4px 8px;
          font-size: 12px;
          color: var(--el-text-color-regular);
          background-color: var(--el-bg-color);
          border: 1px solid var(--el-border-color);
          border-radius: 4px;
          cursor: pointer;
          opacity: 0;
          visibility: hidden;
          transition: all 0.3s ease;
          z-index: 10;
          user-select: none;
          pointer-events: none;
        }

        &:hover::before {
          opacity: 1;
          visibility: visible;
        }
      }

      :deep(code) {
        padding: 0.2em 0.4em;
        font-size: 85%;
        background-color: var(--el-fill-color-light) !important;
        border-radius: 3px;
      }

      // 链接样式
      :deep(a) {
        color: #409eff;
        text-decoration: none;

        &:hover {
          text-decoration: underline;
        }
      }

      // 引用
      :deep(blockquote) {
        margin: 0;
        padding: 1px 1em;
        background-color: var(--el-fill-color-light) !important;
        color: var(--el-text-color-secondary);
        border-left: 0.25em solid var(--el-border-color);
        p {
          margin: 5px;
        }
      }

      // 表格样式
      :deep(table) {
        min-width: 100% !important;
        max-width: 100% !important;
        border-collapse: collapse;
        margin: 16px 0;
        display: block;
        overflow-x: auto;
        white-space: nowrap;
        p {
          text-align: center;
        }

        &::-webkit-scrollbar {
          height: 10px;
        }
      }

      :deep(th) {
        padding: 5px;
        border: 1px solid var(--el-border-color);
        background: var(--el-fill-color-light) !important;
      }

      :deep(td) {
        padding: 5px;
        border: 1px solid var(--el-border-color);
      }

      // 任务列表样式
      :deep(li[data-type="taskItem"]) {
        display: flex;
        align-items: center;
        gap: 8px;
        margin-bottom: 8px;
        list-style: none;
        pointer-events: none;

        label {
          display: flex;
          align-items: center;

          input[type="checkbox"] {
            margin: 0;
            width: 16px;
            height: 16px;
          }
        }

        div {
          flex: 1;
          margin: 0;
          display: flex;
          align-items: center;

          p {
            margin: 0;
            line-height: 1.4;
          }
        }
      }

      // 任务列表容器样式
      :deep(ul:has(li[data-type="taskItem"])) {
        padding-left: 0;
        margin: 16px 0;
      }

      // 无序列表和有序列表
      :deep(ul),
      :deep(ol) {
        padding-left: 15px;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-content {
    padding: 20px 15px;

    .article-main {
      .article-header {
        .title-row {
          align-items: flex-start;

          .article-title {
            font-size: 24px;
          }

          .vip-article-badge {
            height: 28px;
            padding: 0 10px;
            font-size: 12px;
          }
        }

        .title-tip {
          font-size: 13px;
        }
      }

      .article-meta {
        .meta-row {
          // 第一行在手机端分两行显示
          &.first-row {
            flex-direction: column;
            align-items: flex-start;
            gap: 12px;

            .basic-info {
              gap: 15px;
              flex-wrap: wrap;
              width: 100%;
              justify-content: space-between;

              .basic-info-content {
                display: flex;
                gap: 15px;
                flex-wrap: wrap;
                align-items: center;
              }

              // 移动端编辑按钮显示
              .edit-actions.mobile-edit {
                display: flex;
                margin-left: auto;
                flex-shrink: 0;

                .el-button {
                  font-size: 12px;
                  padding: 4px 8px;
                  height: auto;
                }
              }
            }

            .stats-info {
              gap: 15px;
              width: 100%;
            }

            // 桌面端编辑按钮在移动端隐藏
            .edit-actions.desktop-edit {
              display: none;
            }
          }

          // 第二行：标签信息独占一行
          &.second-row.tags-row {
            .article-tags {
              flex-direction: column;
              align-items: flex-start;
              gap: 8px;

              .tags-container {
                gap: 8px;
              }
            }
          }

          // 第三行：专栏信息独占一行
          &.third-row.columns-row {
            .article-columns {
              flex-direction: column;
              align-items: flex-start;
              gap: 8px;

              .columns-container {
                gap: 8px;
              }
            }
          }
        }
      }

      .article-body {
        font-size: 15px;

        // 移动端表格样式优化
        :deep(table) {
          min-width: 100%;
          font-size: 14px;

          &::-webkit-scrollbar {
            height: 6px;
          }

          &::-webkit-scrollbar-track {
            background: var(--el-fill-color-light);
            border-radius: 3px;
          }

          &::-webkit-scrollbar-thumb {
            background: var(--el-color-primary-light-5);
            border-radius: 3px;

            &:hover {
              background: var(--el-color-primary);
            }
          }
        }

        :deep(td, th) {
          min-width: 80px;
          padding: 6px 8px;
          font-size: 14px;
          white-space: normal;
          word-break: break-word;
        }

        :deep(th) {
          font-size: 14px;
          font-weight: 600;
        }
      }
    }
  }
}
</style>
