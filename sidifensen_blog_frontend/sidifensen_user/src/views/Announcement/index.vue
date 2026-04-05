<template>
  <div class="announcement-page">
    <div class="container">
      <div class="page-header">
        <div class="header-copy">
          <p class="header-copy__eyebrow">Announcement</p>
          <h2 class="header-copy__title">网站公告</h2>
          <p class="header-copy__description">了解平台的最新动态、功能更新和重要通知。</p>
        </div>
      </div>

      <div class="announcement-content">
        <div class="announcement-list" v-loading="loading">
          <div v-if="announcementList.length === 0 && !loading" class="empty-state">
            <el-empty description="暂无公告" />
          </div>

          <div v-else class="announcement-cards">
            <article
              v-for="item in announcementList"
              :key="item.id"
              class="announcement-card"
              :class="{ unread: !isRead(item.id) }"
              @click="handleCardClick(item)"
            >
              <div class="card-icon">
                <el-icon :size="28"><Bell /></el-icon>
              </div>
              <div class="card-body">
                <div class="card-header-row">
                  <h3 class="card-title">{{ item.title }}</h3>
                  <span class="card-time">{{ formatTime(item.sendTime || item.createTime) }}</span>
                </div>
                <div class="card-content">{{ item.content }}</div>
                <div class="card-footer">
                  <span v-if="!isRead(item.id)" class="unread-badge">未读</span>
                  <span v-else class="read-badge">已读</span>
                </div>
              </div>
            </article>
          </div>

          <div v-if="loadingMore" class="loading-more">
            <div class="loading-more__spinner"></div>
            <span>加载中...</span>
          </div>
          <div v-else-if="!hasMore && announcementList.length > 0" class="no-more">
            <span>没有更多了</span>
          </div>
        </div>

        <div v-if="total > pageSize" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="handlePageChange"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Bell } from '@element-plus/icons-vue'
import { getAnnouncementPage, readAnnouncement, getReadAnnouncementIds } from '@/api/announcement'
import { formatTimeAgo } from '@/utils/timeUtils'

const announcementList = ref([])
const readIds = ref([])
const loading = ref(false)
const loadingMore = ref(false)
const currentPage = ref(1)
const pageSize = ref(20)
const total = ref(0)
const hasMore = ref(true)

const isRead = (id) => readIds.value.includes(id)

const formatTime = (time) => {
  if (!time) return ''
  return formatTimeAgo(time)
}

// 加载已读ID列表
const fetchReadIds = async () => {
  try {
    const res = await getReadAnnouncementIds()
    readIds.value = res.data || []
  } catch (error) {
    readIds.value = []
  }
}

// 加载公告列表
const fetchAnnouncements = async (reset = false) => {
  if (reset) {
    loading.value = true
    currentPage.value = 1
    announcementList.value = []
    hasMore.value = true
  } else {
    loadingMore.value = true
  }

  try {
    const res = await getAnnouncementPage(currentPage.value, pageSize.value)
    const data = res.data || {}
    const list = data.data || []

    if (reset) {
      announcementList.value = list
    } else {
      announcementList.value = [...announcementList.value, ...list]
    }

    total.value = data.total || 0
    hasMore.value = announcementList.value.length < total.value
  } catch (error) {
    ElMessage.error('获取公告列表失败')
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

// 点击公告卡片：标记已读
const handleCardClick = async (item) => {
  if (isRead(item.id)) return

  try {
    await readAnnouncement(item.id)
    if (!readIds.value.includes(item.id)) {
      readIds.value.push(item.id)
    }
  } catch (error) {
    // 静默处理
  }
}

// 翻页
const handlePageChange = (page) => {
  currentPage.value = page
  fetchAnnouncements(true)
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

onMounted(async () => {
  await fetchAnnouncements(true)
  await fetchReadIds()
})
</script>

<style lang="scss" scoped>
.announcement-page {
  --bg-page: #f5f7fb;
  --bg-card: #ffffff;
  --bg-soft: #f8fafc;
  --bg-accent: #eef4ff;
  --text-title: #162033;
  --text-primary: #344054;
  --text-secondary: #667085;
  --text-muted: #98a2b3;
  --border: #dbe3ef;
  --border-strong: #c6d2e3;
  --accent: #2f6fec;
  --accent-strong: #1f5bd0;
  --shadow: 0 1px 3px rgba(17, 24, 39, 0.08);
  --shadow-hover: 0 10px 24px rgba(17, 24, 39, 0.08);

  min-height: 100vh;
  padding-top: 100px;
  padding-bottom: 48px;
  background:
    radial-gradient(circle at top left, var(--bg-accent), transparent 34%),
    linear-gradient(180deg, var(--bg-page) 0%, var(--bg-soft) 100%);

  .container {
    max-width: 860px;
    margin: 0 auto;
    padding: 0 20px;

    .page-header {
      display: flex;
      justify-content: space-between;
      gap: 20px;
      padding: 28px;
      background: var(--bg-card);
      border: 1px solid var(--border);
      border-radius: 24px;
      box-shadow: var(--shadow);
      margin-bottom: 18px;

      .header-copy {
        display: flex;
        flex-direction: column;
        gap: 10px;

        .header-copy__eyebrow {
          margin: 0;
          font-size: 12px;
          font-weight: 700;
          letter-spacing: 0.16em;
          text-transform: uppercase;
          color: var(--accent);
        }

        .header-copy__title {
          margin: 0;
          font-family: 'Helvetica Neue', Arial, sans-serif;
          font-size: 34px;
          color: var(--text-title);
        }

        .header-copy__description {
          margin: 0;
          max-width: 520px;
          font-size: 15px;
          line-height: 1.7;
          color: var(--text-secondary);
        }
      }
    }

    .announcement-content {
      .announcement-list {
        min-height: 400px;

        .empty-state {
          display: flex;
          align-items: center;
          justify-content: center;
          min-height: 320px;
        }

        .announcement-cards {
          display: flex;
          flex-direction: column;
          gap: 14px;

          .announcement-card {
            display: flex;
            gap: 20px;
            padding: 24px;
            background: var(--bg-card);
            border: 1px solid var(--border);
            border-radius: 18px;
            box-shadow: var(--shadow);
            cursor: pointer;
            transition:
              box-shadow 0.2s ease,
              border-color 0.2s ease;

            &:hover {
              border-color: var(--border-strong);
              box-shadow: var(--shadow-hover);
            }

            &.unread {
              background: var(--bg-accent);
              border-color: #c7d9ff;

              .card-icon {
                background: var(--accent);
                color: #fff;
              }

              .card-title {
                color: var(--accent-strong);
              }
            }

            .card-icon {
              flex-shrink: 0;
              display: flex;
              align-items: center;
              justify-content: center;
              width: 52px;
              height: 52px;
              background: var(--bg-soft);
              border-radius: 14px;
              color: var(--accent);
            }

            .card-body {
              flex: 1;
              min-width: 0;
              display: flex;
              flex-direction: column;
              gap: 10px;

              .card-header-row {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                gap: 16px;

                .card-title {
                  margin: 0;
                  font-size: 17px;
                  font-weight: 700;
                  color: var(--text-title);
                  line-height: 1.4;
                }

                .card-time {
                  flex-shrink: 0;
                  font-size: 12px;
                  color: var(--text-muted);
                  white-space: nowrap;
                }
              }

              .card-content {
                font-size: 14px;
                line-height: 1.7;
                color: var(--text-secondary);
                overflow: hidden;
                display: -webkit-box;
                text-overflow: ellipsis;
                -webkit-line-clamp: 3;
                line-clamp: 3;
                -webkit-box-orient: vertical;
              }

              .card-footer {
                .unread-badge {
                  display: inline-block;
                  padding: 2px 10px;
                  border-radius: 999px;
                  font-size: 11px;
                  font-weight: 600;
                  background: var(--accent);
                  color: #fff;
                }

                .read-badge {
                  display: inline-block;
                  padding: 2px 10px;
                  border-radius: 999px;
                  font-size: 11px;
                  font-weight: 600;
                  background: var(--bg-soft);
                  color: var(--text-muted);
                  border: 1px solid var(--border);
                }
              }
            }
          }
        }

        .loading-more {
          display: flex;
          justify-content: center;
          align-items: center;
          gap: 10px;
          padding: 24px 0 12px;
          color: var(--text-secondary);

          .loading-more__spinner {
            width: 18px;
            height: 18px;
            border: 2px solid var(--border);
            border-top-color: var(--accent);
            border-radius: 50%;
            animation: spin 1s linear infinite;
          }
        }

        .no-more {
          padding-top: 8px;
          text-align: center;
          color: var(--text-muted);
          font-size: 13px;
        }
      }

      .pagination-wrapper {
        display: flex;
        justify-content: center;
        margin-top: 24px;
        padding: 20px;
        background: var(--bg-card);
        border: 1px solid var(--border);
        border-radius: 18px;
        box-shadow: var(--shadow);

        ::v-deep(.el-pagination) {
          .el-pager li {
            border-radius: 8px;
            min-width: 36px;
            height: 36px;
            line-height: 36px;
          }

          .el-pager li.is-active {
            background: var(--accent);
            color: #fff;
          }

          .btn-prev,
          .btn-next {
            border-radius: 8px;
            min-width: 36px;
            height: 36px;
          }
        }
      }
    }
  }
}

html.dark {
  .announcement-page {
    --bg-page: #0b1220;
    --bg-card: #111a2d;
    --bg-soft: #152238;
    --bg-accent: #132a4e;
    --text-title: #f3f6fc;
    --text-primary: #d7deea;
    --text-secondary: #a7b3c7;
    --text-muted: #8292ac;
    --border: #25344d;
    --border-strong: #355070;
    --accent: #7fb0ff;
    --accent-strong: #a7c7ff;
    --shadow: 0 1px 3px rgba(2, 6, 23, 0.35);
    --shadow-hover: 0 12px 28px rgba(2, 6, 23, 0.34);
  }
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }

  100% {
    transform: rotate(360deg);
  }
}

@media (max-width: 640px) {
  .announcement-page {
    padding-top: 80px;
    padding-bottom: 36px;

    .container {
      padding: 0 14px;

      .page-header {
        padding: 20px;

        .header-copy__title {
          font-size: 28px;
        }
      }

      .announcement-content {
        .announcement-list {
          .announcement-cards {
            .announcement-card {
              padding: 16px;
              gap: 14px;

              .card-icon {
                width: 44px;
                height: 44px;
                font-size: 22px;
              }

              .card-body {
                .card-header-row {
                  flex-direction: column;
                  gap: 6px;

                  .card-title {
                    font-size: 15px;
                  }
                }

                .card-content {
                  font-size: 13px;
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
