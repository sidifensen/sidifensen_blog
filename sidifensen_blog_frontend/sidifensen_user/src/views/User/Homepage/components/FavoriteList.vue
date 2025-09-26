<template>
  <div class="favorite-list-wrapper">
    <div class="favorite-list-section">
      <!-- 收藏夹加载状态 -->
      <div v-if="favoriteLoading" class="loading-container">
        <el-skeleton animated :count="3">
          <template #template>
            <div class="favorite-skeleton">
              <el-skeleton-item variant="image" style="width: 120px; height: 90px" />
              <div class="skeleton-content">
                <el-skeleton-item variant="h3" style="width: 60%" />
                <el-skeleton-item variant="text" style="width: 100%" />
              </div>
            </div>
          </template>
        </el-skeleton>
      </div>

      <!-- 收藏夹为空状态 -->
      <div v-else-if="favoriteList.length === 0" class="empty-state">
        <el-empty description="暂无收藏夹" />
      </div>

      <!-- 收藏夹内容 -->
      <div v-else class="favorite-content">
        <!-- 收藏夹列表 -->
        <div class="favorite-folders">
          <h4 class="section-title">
            <el-icon><Star /></el-icon>
            我的收藏夹
          </h4>
          <div class="folder-list">
            <div v-for="favorite in favoriteList" :key="favorite.id" class="folder-item">
              <!-- 收藏夹头部 -->
              <div class="folder-header" @click="handleToggleFavorite(favorite)">
                <div class="folder-main">
                  <div class="folder-icon">
                    <el-icon><Collection /></el-icon>
                  </div>
                  <div class="folder-info">
                    <h5 class="folder-name">{{ favorite.name }}</h5>
                    <div class="folder-meta">
                      <span class="article-count">{{ favorite.articleCount || 0 }} 篇文章</span>
                      <span class="visibility" :class="favorite.showStatus === 0 ? 'public' : 'private'">
                        {{ favorite.showStatus === 0 ? "公开" : "私密" }}
                      </span>
                    </div>
                  </div>
                </div>
                <div class="folder-expand">
                  <el-icon class="expand-icon" :class="{ expanded: favorite.expanded }">
                    <ArrowDown />
                  </el-icon>
                </div>
              </div>

              <!-- 收藏夹文章列表 -->
              <div v-if="favorite.expanded" class="folder-articles">
                <!-- 文章加载状态 -->
                <div v-if="favorite.loading" class="loading-container">
                  <el-skeleton animated :count="2">
                    <template #template>
                      <div class="article-skeleton">
                        <el-skeleton-item variant="image" style="width: 100px; height: 75px" />
                        <div class="skeleton-content">
                          <el-skeleton-item variant="h3" style="width: 60%" />
                          <el-skeleton-item variant="text" style="width: 100%" />
                          <el-skeleton-item variant="text" style="width: 40%" />
                        </div>
                      </div>
                    </template>
                  </el-skeleton>
                </div>

                <!-- 文章为空状态 -->
                <div v-else-if="!favorite.articles || favorite.articles.length === 0" class="empty-articles">
                  <el-empty description="收藏夹中暂无文章" :image-size="60" />
                </div>

                <!-- 文章列表 -->
                <div v-else class="article-list">
                  <div v-for="article in favorite.articles" :key="article.id" class="article-item" @click="handleArticleClick(article.id)">
                    <!-- 文章封面 -->
                    <el-image :src="article.coverUrl || ''" class="article-cover">
                      <template #placeholder>
                        <div class="loading-text">加载中...</div>
                      </template>
                      <template #error>
                        <div class="error">
                          <el-icon><Picture /></el-icon>
                        </div>
                      </template>
                    </el-image>

                    <!-- 文章内容 -->
                    <div class="article-content">
                      <h3 class="article-title">{{ article.title }}</h3>
                      <p class="article-description">{{ article.description }}</p>

                      <!-- 文章元信息 -->
                      <div class="article-meta">
                        <div class="article-meta-primary">
                          <span class="article-author">{{ article.authorName }}</span>
                          <span class="article-date">{{ article.createTime }}</span>
                        </div>
                        <div class="article-meta-stats">
                          <span class="article-readCount">{{ article.readCount }} 阅读</span>
                          <span class="article-likes">{{ article.likeCount || 0 }} 点赞</span>
                          <span class="article-comments">{{ article.commentCount || 0 }} 评论</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { Star, Collection, ArrowDown, Picture } from "@element-plus/icons-vue";

// 定义 props
const props = defineProps({
  favoriteList: {
    type: Array,
    default: () => [],
  },
  favoriteLoading: {
    type: Boolean,
    default: false,
  },
});

// 定义 emits
const emit = defineEmits(["toggle-favorite", "article-click"]);

// 处理收藏夹展开/收起事件
const handleToggleFavorite = (favorite) => {
  emit("toggle-favorite", favorite);
};

// 处理文章点击事件
const handleArticleClick = (articleId) => {
  emit("article-click", articleId);
};
</script>

<style lang="scss" scoped>
// 全局变量
$primary-color: #409eff;
$text-primary: #303133;
$text-regular: #606266;
$text-secondary: #909399;
$border-color: #dcdfe6;
$bg-color: #f5f7fa;

// 收藏列表包装器
.favorite-list-wrapper {
  position: relative; // 为返回顶部按钮提供定位参考
}

// 收藏列表区域
.favorite-list-section {
  background: var(--el-bg-color-page);
  border-radius: 8px;
  padding: 20px;
  border: 1px solid var(--el-border-color);
  box-shadow: 0 2px 12px var(--el-border-color-light);
  min-height: 580px; // 设置最小高度，与父容器一致
  height: 100%; // 占据父容器的完整高度

  // 加载容器样式
  .loading-container {
    padding: 20px 0;
  }

  // 收藏夹骨架屏样式
  .favorite-skeleton {
    display: flex;
    gap: 16px;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-light);

    &:last-child {
      border-bottom: none;
    }

    .skeleton-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
  }

  // 文章骨架屏样式
  .article-skeleton {
    display: flex;
    gap: 16px;
    padding: 20px 0;
    border-bottom: 1px solid var(--el-border-color-light);

    &:last-child {
      border-bottom: none;
    }

    .skeleton-content {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
  }

  // 收藏夹内容
  .favorite-content {
    display: flex;
    flex-direction: column;
    gap: 20px;

    // 公共标题样式
    .section-title {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 16px;
      font-weight: 600;
      color: var(--el-text-color-primary);
      margin: 0 0 16px 0;
      padding-bottom: 8px;
      border-bottom: 2px solid var(--el-color-primary);

      .el-icon {
        color: var(--el-color-primary);
      }
    }

    // 收藏夹列表区域
    .favorite-folders {
      .folder-list {
        display: flex;
        flex-direction: column;
        gap: 16px;

        .folder-item {
          border-radius: 8px;
          border: 1px solid var(--el-border-color-light);
          background-color: var(--el-bg-color);
          overflow: hidden;
          transition: all 0.3s ease;

          &:hover {
            border-color: var(--el-color-primary-light-7);
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
          }

          // 收藏夹头部
          .folder-header {
            display: flex;
            align-items: center;
            padding: 16px;
            cursor: pointer;
            transition: all 0.3s ease;

            &:hover {
              background-color: var(--el-color-primary-light-9);
            }

            .folder-main {
              display: flex;
              align-items: center;
              gap: 12px;
              flex: 1;

              .folder-icon {
                width: 40px;
                height: 40px;
                display: flex;
                align-items: center;
                justify-content: center;
                background-color: var(--el-color-primary-light-8);
                border-radius: 8px;
                color: var(--el-color-primary);

                .el-icon {
                  font-size: 20px;
                }
              }

              .folder-info {
                flex: 1;
                min-width: 0;

                .folder-name {
                  font-size: 16px;
                  font-weight: 600;
                  color: var(--el-text-color-primary);
                  margin: 0 0 6px 0;
                  overflow: hidden;
                  text-overflow: ellipsis;
                  white-space: nowrap;
                }

                .folder-meta {
                  display: flex;
                  align-items: center;
                  gap: 12px;
                  font-size: 13px;

                  .article-count {
                    color: var(--el-text-color-regular);
                  }

                  .visibility {
                    padding: 2px 8px;
                    border-radius: 12px;
                    font-size: 12px;
                    font-weight: 500;

                    &.public {
                      background-color: #f0f9ff;
                      color: var(--el-color-primary);
                      border: 1px solid var(--el-color-primary-light-7);
                    }

                    &.private {
                      background-color: #fef2f2;
                      color: #dc2626;
                      border: 1px solid #fecaca;
                    }
                  }
                }
              }
            }

            .folder-expand {
              .expand-icon {
                font-size: 16px;
                color: var(--el-text-color-regular);
                transition: transform 0.3s ease;

                &.expanded {
                  transform: rotate(180deg);
                }
              }
            }
          }

          // 收藏夹文章列表
          .folder-articles {
            border-top: 1px solid var(--el-border-color-lighter);
            background-color: var(--el-fill-color-extra-light);

            .loading-container {
              padding: 16px;
            }

            .empty-articles {
              padding: 40px 20px;
              text-align: center;
            }

            .article-list {
              .article-item {
                display: flex;
                gap: 12px;
                padding: 16px;
                border-bottom: 1px solid var(--el-border-color-lighter);
                cursor: pointer;
                transition: all 0.3s ease;

                &:last-child {
                  border-bottom: none;
                }

                &:hover {
                  background-color: var(--el-bg-color);
                  transform: translateX(4px);
                }

                // 文章封面
                .article-cover {
                  width: 100px;
                  height: 75px;
                  border-radius: 6px;
                  flex-shrink: 0;

                  .loading-text {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    width: 100%;
                    height: 100%;
                    font-size: 11px;
                    color: var(--el-text-color-regular);
                    background-color: var(--el-bg-color-page);
                  }

                  .error {
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    width: 100%;
                    height: 100%;
                    background-color: var(--el-bg-color-page);

                    .el-icon {
                      font-size: 20px;
                      color: var(--el-text-color-placeholder);
                    }
                  }
                }

                // 文章内容
                .article-content {
                  flex: 1;
                  min-width: 0;

                  .article-title {
                    font-size: 15px;
                    font-weight: 600;
                    color: var(--el-text-color-primary);
                    margin: 0 0 6px 0;
                    line-height: 1.4;
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    line-clamp: 2;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                  }

                  .article-description {
                    font-size: 13px;
                    color: var(--el-text-color-regular);
                    margin: 0 0 8px 0;
                    line-height: 1.4;
                    display: -webkit-box;
                    -webkit-line-clamp: 2;
                    line-clamp: 2;
                    -webkit-box-orient: vertical;
                    overflow: hidden;
                  }

                  // 文章元信息
                  .article-meta {
                    font-size: 12px;
                    color: var(--el-text-color-secondary);

                    .article-meta-primary {
                      display: flex;
                      align-items: center;
                      gap: 8px;
                      margin-bottom: 4px;

                      .article-author {
                        color: var(--el-color-primary);
                        font-weight: 500;
                      }
                    }

                    .article-meta-stats {
                      display: flex;
                      align-items: center;
                      gap: 8px;
                    }

                    // 桌面端单行显示
                    @media (min-width: 769px) {
                      .article-meta-primary {
                        margin-bottom: 0;
                      }

                      .article-meta-primary,
                      .article-meta-stats {
                        display: inline-flex;
                      }

                      .article-meta-stats {
                        margin-left: 8px;
                      }

                      .article-meta-stats::before {
                        content: "•";
                        margin-right: 8px;
                        color: var(--el-text-color-placeholder);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  // 空状态
  .empty-state {
    padding: 60px 0;
    text-align: center;
  }
}

// 响应式设计
@media (max-width: 768px) {
  .favorite-list-section {
    .favorite-content {
      // 收藏夹列表移动端优化
      .favorite-folders {
        .folder-list {
          .folder-item {
            .folder-header {
              padding: 12px;

              .folder-main {
                gap: 10px;

                .folder-icon {
                  width: 32px;
                  height: 32px;

                  .el-icon {
                    font-size: 16px;
                  }
                }

                .folder-info {
                  .folder-name {
                    font-size: 14px;
                  }

                  .folder-meta {
                    gap: 8px;
                    font-size: 12px;

                    .visibility {
                      font-size: 10px;
                    }
                  }
                }
              }
            }

            .folder-articles {
              .article-list {
                .article-item {
                  flex-direction: column;
                  gap: 10px;
                  padding: 12px;

                  .article-cover {
                    width: 100%;
                    height: 150px;
                  }

                  .article-content {
                    .article-title {
                      font-size: 14px;
                    }

                    .article-description {
                      font-size: 12px;
                    }

                    .article-meta {
                      font-size: 11px;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}
</style>
