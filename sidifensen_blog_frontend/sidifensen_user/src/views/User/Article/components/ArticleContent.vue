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
          <!-- 文章标题 -->
          <h1 class="article-title">{{ article.title }}</h1>

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
                  {{ article.readCount || 0 }} 阅读
                </span>
                <span class="like-count">
                  <svg-icon name="like" width="14px" height="14px" color="#909399" />
                  {{ article.likeCount || 0 }} 点赞
                </span>
                <span class="collect-count">
                  <el-icon>
                    <Star />
                  </el-icon>
                  {{ article.collectCount || 0 }} 收藏
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

  <!-- 文章底部操作栏 -->
  <div class="article-actions" v-if="article">
    <div class="action-item">
      <el-button :type="article.isLiked ? 'primary' : 'default'" :loading="likeLoading" @click="handleLike">
        <svg-icon name="like" width="16px" height="16px" margin-right="6px" :color="article.isLiked ? '#ffffff' : '#909399'" />
        {{ article.likeCount || 0 }}
      </el-button>
    </div>
    <div class="action-item">
      <el-button :type="article.isCollected ? 'primary' : 'default'" :icon="article.isCollected ? StarFilled : Star" @click="handleCollect">
        {{ article.collectCount || 0 }}
      </el-button>
    </div>
    <div class="action-item">
      <el-button :icon="ChatLineRound" @click="handleComment">
        {{ commentTotal || article.commentCount || 0 }}
      </el-button>
    </div>
  </div>

  <!-- 返回顶部按钮 -->
  <div class="back-to-top" @click="scrollToTop">
    <el-icon>
      <ArrowUp />
    </el-icon>
  </div>

  <!-- 评论抽屉 -->
  <CommentDrawer v-if="article?.id" v-model:visible="commentDrawerVisible" :article-id="article.id" ref="commentDrawerRef" />

  <!-- 收藏对话框 -->
  <FavoriteDialog v-if="article?.id" v-model="favoriteDialogVisible" :article-id="article.id" @success="handleFavoriteSuccess" />
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { Clock, View, Star, StarFilled, ChatLineRound, ArrowUp, Edit } from "@element-plus/icons-vue";
import { toggleLike, isLiked } from "@/api/like";
import { useUserStore } from "@/stores/userStore";
import CommentDrawer from "@/views/User/Article/components/CommentDrawer.vue";
import FavoriteDialog from "./FavoriteDialog.vue";
import MobileAuthorInfo from "./MobileAuthorInfo.vue";

// 路由和状态管理
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

// Emits 定义
const emit = defineEmits(["updateArticle"]);

// 响应式数据
const likeLoading = ref(false); // 点赞加载状态
const commentDrawerVisible = ref(false); // 评论抽屉显示状态
const commentTotal = ref(0); // 评论总数
const commentDrawerRef = ref(null); // 评论抽屉引用
const favoriteDialogVisible = ref(false); // 收藏对话框显示状态
const copySuccess = ref(false); // 复制成功状态

// 渲染富文本内容
const renderContent = computed(() => {
  if (!props.article?.content) return "";
  return props.article.content;
});

// 处理标签列表
const tagList = computed(() => {
  if (!props.article?.tag) return [];
  return props.article.tag.split(",").filter((tag) => tag.trim() !== "");
});

// 判断当前用户是否为文章作者
const isCurrentUser = computed(() => {
  if (!userStore.user?.id || !props.article?.userId) return false;
  return userStore.user.id === props.article.userId;
});

// 点赞文章
const handleLike = async () => {
  if (!props.article?.id) {
    ElMessage.warning("文章信息异常");
    return;
  }

  if (likeLoading.value) {
    return; // 防止重复点击
  }

  try {
    likeLoading.value = true;

    // 调用后端接口切换点赞状态
    await toggleLike(0, props.article.id); // 0表示文章类型

    // 更新本地文章数据
    const updatedArticle = { ...props.article };
    if (updatedArticle.isLiked) {
      // 取消点赞
      updatedArticle.isLiked = false;
      updatedArticle.likeCount = Math.max(0, (updatedArticle.likeCount || 0) - 1);
      ElMessage.success("取消点赞成功");
    } else {
      // 点赞
      updatedArticle.isLiked = true;
      updatedArticle.likeCount = (updatedArticle.likeCount || 0) + 1;
      ElMessage.success("点赞成功");
    }

    // 通知父组件更新文章数据
    emit("updateArticle", updatedArticle);
  } catch (error) {
    console.error("点赞操作失败:", error);
    ElMessage.error("点赞操作失败，请重试");
  } finally {
    likeLoading.value = false;
  }
};

// 收藏文章
const handleCollect = () => {
  if (!props.article?.id) {
    ElMessage.warning("文章信息异常");
    return;
  }
  favoriteDialogVisible.value = true;
};

// 处理收藏成功
const handleFavoriteSuccess = (result) => {
  console.log("收藏操作成功:", result);

  // 更新文章的收藏状态和收藏数
  const updatedArticle = { ...props.article };

  if (result.action === "add") {
    // 如果之前没有收藏过任何收藏夹，现在收藏了
    if (!updatedArticle.isCollected) {
      updatedArticle.isCollected = true;
      updatedArticle.collectCount = (updatedArticle.collectCount || 0) + 1;
    }
  } else if (result.action === "remove") {
    // 这里需要检查是否还有其他收藏夹收藏了这篇文章
    // 为了简化，我们暂时不更新状态，让页面刷新时重新获取
    // 如果需要精确控制，可以在后端返回当前文章的总收藏状态
  }

  // 通知父组件更新文章数据
  emit("updateArticle", updatedArticle);
};

// 评论文章
const handleComment = () => {
  commentDrawerVisible.value = true;
};

// 返回顶部
const scrollToTop = () => {
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: "smooth" });
};

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
    copySuccess.value = true;
    ElMessage.success("代码已复制到剪贴板");

    // 2秒后重置状态
    setTimeout(() => {
      copySuccess.value = false;
    }, 2000);
  } catch (error) {
    console.error("复制失败:", error);
    ElMessage.error("复制失败，请手动复制");
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
  padding: 30px 30px; // 底部留出空间给固定操作栏
  position: relative;

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
    // 文章标题
    .article-title {
      margin: 0 0 20px;
      font-size: 28px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      line-height: 1.4;
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

            .label {
              flex-shrink: 0;
              font-weight: 500;
            }

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
      margin-bottom: 100px; // 增加底部边距，避免被固定操作栏遮挡
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

          .copy-button {
            opacity: 1;
            visibility: visible;
          }
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

      // 表格样式 - 简单有效的滚动解决方案
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

        // 自定义滚动条样式，确保可见
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
        align-items: center; // 改为center实现垂直居中
        gap: 8px;
        margin-bottom: 8px;
        list-style: none;
        pointer-events: none; // 在最外层禁用所有点击事件

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
          align-items: center; // 确保内容区域也垂直居中

          p {
            margin: 0;
            line-height: 1.4; // 稍微调整行高
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

// 文章底部操作栏 - 固定在视窗底部，使用稳定的居中定位
.article-actions {
  position: fixed;
  bottom: 20px;
  left: calc(50vw - 200px); // 使用vw单位避免滚动条影响，200px是操作栏最大宽度的一半
  z-index: 999;
  display: flex;
  justify-content: center;
  gap: 20px;
  padding: 16px 24px;
  background: var(--el-bg-color);
  backdrop-filter: blur(2px);
  background-color: color-mix(in srgb, var(--el-bg-color) 50%, transparent);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 24px;
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  max-width: 400px;
  width: auto;

  .action-item {
    .el-button {
      min-width: 100px;
      border-radius: 20px;
    }
  }
}

// 返回顶部按钮样式
.back-to-top {
  position: fixed;
  right: 22px;
  bottom: 150px; // 避免与底部操作栏重叠
  display: flex;
  align-items: center;
  justify-content: center;
  width: 65px;
  height: 65px;
  font-size: 24px;
  backdrop-filter: blur(2px);
  background-color: color-mix(in srgb, var(--el-bg-color) 50%, transparent);
  border: 1px solid var(--el-border-color);
  border-radius: 50%;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;

  &:hover {
    background: var(--el-color-primary);
    color: white;
    transform: translateY(-2px);
    box-shadow: 0 4px 16px 0 rgba(0, 0, 0, 0.15);
  }

  .el-icon {
    font-size: 20px;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .article-content {
    padding: 20px 15px;

    .article-main {
      .article-title {
        font-size: 24px;
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
              justify-content: space-between; // 让内容分布在两端

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
          min-width: 100%; // 移动端不设置固定最小宽度
          font-size: 14px; // 适当缩小字体

          // 移动端滚动条样式优化
          &::-webkit-scrollbar {
            height: 6px; // 移动端滚动条更细
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
          min-width: 80px; // 移动端减小最小宽度
          padding: 6px 8px; // 减小内边距
          font-size: 14px; // 稍微缩小字体
          white-space: normal; // 允许文本换行
          word-break: break-word; // 长单词可以断开
        }

        :deep(th) {
          font-size: 14px;
          font-weight: 600;
        }
      }
    }
  }

  // 移动端操作栏调整
  .article-actions {
    left: 50%;
    transform: translateX(-50%);
    bottom: 30px;
    padding: 12px 20px;
    gap: 16px;
    max-width: 320px;

    .action-item {
      .el-button {
        min-width: 80px;
        font-size: 14px;
        padding: 8px 16px;
      }
    }
  }

  // 移动端返回顶部按钮调整
  .back-to-top {
    right: 15px;
    bottom: 120px; // 为移动端底部操作栏留出更多空间
    width: 44px;
    height: 44px;
    font-size: 20px;

    .el-icon {
      font-size: 18px;
    }
  }
}
</style>
