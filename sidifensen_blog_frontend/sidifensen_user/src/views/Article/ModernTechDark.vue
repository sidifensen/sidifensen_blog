<template>
  <div class="article-page-body">
    <div class="container">
      <!-- 页面头部 -->
      <div class="page-hero">
        <div class="hero-content">
          <div class="hero-copy">
            <div class="hero-kicker">Article Square</div>
            <h1>文章广场</h1>
            <p>按发布时间浏览社区内容，持续发现值得读的技术文章。</p>
          </div>
          <div class="hero-metrics">
            <div class="metric-card">
              <div class="metric-label">已加载</div>
              <div class="metric-value">24</div>
            </div>
            <div class="metric-card">
              <div class="metric-label">全部文章</div>
              <div class="metric-value">284</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 内容布局 -->
      <div class="content-layout">
        <!-- 主内容区 -->
        <div class="main-content">
          <div class="article-list">
            <!-- 文章 1 -->
            <div class="article-item" @click="goToArticle(1)">
              <div class="article-cover">
                <img src="https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400&h=260&fit=crop" alt="cover">
              </div>
              <div class="article-content">
                <div class="article-author">
                  <div class="author-avatar"></div>
                  <span>张三</span>
                </div>
                <h3 class="article-title">深入理解 JavaScript 异步编程：从 Callback 到 Async/Await</h3>
                <p class="article-description">异步编程是现代 JavaScript 开发的核心概念之一，本文将带你深入理解 JavaScript 异步编程的演进历程...</p>
                <div class="article-meta">
                  <span>2026/03/15</span>
                  <span>👁 2.4k</span>
                  <span>❤️ 128</span>
                  <span>⭐ 45</span>
                </div>
              </div>
            </div>

            <!-- 文章 2 -->
            <div class="article-item" @click="goToArticle(2)">
              <div class="article-cover">
                <img src="https://images.unsplash.com/photo-1518770660439-4636190af475?w=400&h=260&fit=crop" alt="cover">
              </div>
              <div class="article-content">
                <div class="article-author">
                  <div class="author-avatar"></div>
                  <span>李四</span>
                </div>
                <h3 class="article-title">Spring Boot 3.0 新特性详解：拥抱 Java 21</h3>
                <p class="article-description">Spring Boot 3.0 带来了众多令人兴奋的新特性，尤其是对 Java 21 的全面支持，让我们一起来看看...</p>
                <div class="article-meta">
                  <span>2026/03/14</span>
                  <span>👁 1.8k</span>
                  <span>❤️ 96</span>
                  <span>⭐ 32</span>
                </div>
              </div>
            </div>

            <!-- 文章 3 -->
            <div class="article-item" @click="goToArticle(3)">
              <div class="article-cover">
                <img src="https://images.unsplash.com/photo-1507721999472-8ed4421c4af2?w=400&h=260&fit=crop" alt="cover">
              </div>
              <div class="article-content">
                <div class="article-author">
                  <div class="author-avatar"></div>
                  <span>王五</span>
                </div>
                <h3 class="article-title">Vue 3 Composition API 最佳实践指南</h3>
                <p class="article-description">Composition API 是 Vue 3 最重要的特性之一，它提供了更好的代码组织方式，本文总结了实际项目中的最佳实践...</p>
                <div class="article-meta">
                  <span>2026/03/13</span>
                  <span>👁 3.2k</span>
                  <span>❤️ 215</span>
                  <span>⭐ 89</span>
                </div>
              </div>
            </div>

            <!-- 文章 4 -->
            <div class="article-item" @click="goToArticle(4)">
              <div class="article-cover">
                <img src="https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=400&h=260&fit=crop" alt="cover">
              </div>
              <div class="article-content">
                <div class="article-author">
                  <div class="author-avatar"></div>
                  <span>赵六</span>
                </div>
                <h3 class="article-title">Docker 容器化部署实战：从入门到生产</h3>
                <p class="article-description">容器化技术已经成为现代 DevOps 的核心，本文将带你从零开始掌握 Docker 容器化部署...</p>
                <div class="article-meta">
                  <span>2026/03/12</span>
                  <span>👁 4.1k</span>
                  <span>❤️ 312</span>
                  <span>⭐ 124</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 侧边栏 -->
        <aside class="sidebar">
          <!-- 热门文章 -->
          <div class="sidebar-card">
            <h4 class="card-title">
              <span class="icon">🔥</span> 热门文章
            </h4>
            <p class="card-description">按阅读热度排序</p>
            <div class="hot-articles">
              <div class="hot-article-item" @click="goToArticle(4)">
                <div class="hot-article-rank">1</div>
                <div class="hot-article-content">
                  <div class="hot-article-title">Docker 容器化部署实战：从入门到生产</div>
                  <div class="hot-article-meta">
                    <span>👁 4.1k</span>
                    <span class="hot-article-score">🔥 892</span>
                  </div>
                </div>
              </div>
              <div class="hot-article-item" @click="goToArticle(3)">
                <div class="hot-article-rank">2</div>
                <div class="hot-article-content">
                  <div class="hot-article-title">Vue 3 Composition API 最佳实践指南</div>
                  <div class="hot-article-meta">
                    <span>👁 3.2k</span>
                    <span class="hot-article-score">🔥 756</span>
                  </div>
                </div>
              </div>
              <div class="hot-article-item" @click="goToArticle(1)">
                <div class="hot-article-rank">3</div>
                <div class="hot-article-content">
                  <div class="hot-article-title">深入理解 JavaScript 异步编程</div>
                  <div class="hot-article-meta">
                    <span>👁 2.4k</span>
                    <span class="hot-article-score">🔥 623</span>
                  </div>
                </div>
              </div>
              <div class="hot-article-item" @click="goToArticle(2)">
                <div class="hot-article-rank">4</div>
                <div class="hot-article-content">
                  <div class="hot-article-title">Spring Boot 3.0 新特性详解</div>
                  <div class="hot-article-meta">
                    <span>👁 1.8k</span>
                    <span class="hot-article-score">🔥 445</span>
                  </div>
                </div>
              </div>
              <div class="hot-article-item" @click="goToArticle(5)">
                <div class="hot-article-rank">5</div>
                <div class="hot-article-content">
                  <div class="hot-article-title">Redis 高性能缓存实战</div>
                  <div class="hot-article-meta">
                    <span>👁 2.9k</span>
                    <span class="hot-article-score">🔥 412</span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <!-- 会员精选 -->
          <div class="sidebar-card">
            <h4 class="card-title">
              <span class="icon">⭐</span> 会员精选
            </h4>
            <p class="card-description">高质量付费内容</p>
            <div class="featured-articles">
              <div class="featured-article-item" @click="goToArticle(6)">
                <div class="featured-article-cover">
                  <img src="https://images.unsplash.com/photo-1542831371-29b0f74f9713?w=200&h=120&fit=crop" alt="cover">
                </div>
                <div class="featured-article-content">
                  <div class="featured-article-title">TypeScript 高级类型技巧</div>
                  <div class="featured-article-meta">
                    <span>👁 1.5k</span>
                    <span>❤️ 89</span>
                  </div>
                </div>
              </div>
              <div class="featured-article-item" @click="goToArticle(7)">
                <div class="featured-article-cover">
                  <img src="https://images.unsplash.com/photo-1555099962-4199c345e5dd?w=200&h=120&fit=crop" alt="cover">
                </div>
                <div class="featured-article-content">
                  <div class="featured-article-title">Redis 高性能缓存实战</div>
                  <div class="featured-article-meta">
                    <span>👁 2.9k</span>
                    <span>❤️ 187</span>
                  </div>
                </div>
              </div>
              <div class="featured-article-item" @click="goToArticle(4)">
                <div class="featured-article-cover">
                  <img src="https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=200&h=120&fit=crop" alt="cover">
                </div>
                <div class="featured-article-content">
                  <div class="featured-article-title">Docker 容器化部署实战</div>
                  <div class="featured-article-meta">
                    <span>👁 4.1k</span>
                    <span>❤️ 312</span>
                  </div>
                </div>
              </div>
              <div class="featured-article-item" @click="goToArticle(2)">
                <div class="featured-article-cover">
                  <img src="https://images.unsplash.com/photo-1518770660439-4636190af475?w=200&h=120&fit=crop" alt="cover">
                </div>
                <div class="featured-article-content">
                  <div class="featured-article-title">Spring Boot 3.0 新特性</div>
                  <div class="featured-article-meta">
                    <span>👁 1.8k</span>
                    <span>❤️ 96</span>
                  </div>
                </div>
              </div>
            </div>
            <div class="featured-footer">
              <button class="featured-link" @click="goToVipArticles">进入会员专区</button>
            </div>
          </div>
        </aside>
      </div>
    </div>

    <div class="style-badge">风格 D - 现代极简主义科技风</div>
  </div>
</template>

<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

// 跳转到文章详情页
const goToArticle = (articleId) => {
  console.log("跳转到文章:", articleId);
  // router.push(`/user/${userId}/article/${articleId}`);
};

// 跳转到会员专区
const goToVipArticles = () => {
  console.log("跳转到会员专区");
  // router.push("/vip/articles");
};
</script>

<style lang="scss" scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

.article-page-body {
  min-height: 100vh;
  background: #000000;
  color: #ffffff;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
  line-height: 1.6;

  .container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 24px;

    // ===== 页面头部 - 极致简约 =====
    .page-hero {
      padding: 60px 0 40px;
      border-bottom: 1px solid rgba(255, 255, 255, 0.1);

      .hero-content {
        display: flex;
        justify-content: space-between;
        align-items: flex-end;
        gap: 24px;

        .hero-copy {
          max-width: 500px;

          .hero-kicker {
            font-size: 11px;
            font-weight: 600;
            letter-spacing: 0.2em;
            text-transform: uppercase;
            color: #00d4ff;
            margin-bottom: 12px;
            display: flex;
            align-items: center;
            gap: 8px;

            &::before {
              content: '';
              width: 20px;
              height: 1px;
              background: linear-gradient(90deg, #00d4ff, transparent);
            }
          }

          h1 {
            font-size: 40px;
            font-weight: 700;
            letter-spacing: -0.04em;
            margin-bottom: 12px;
            color: #ffffff;
          }

          p {
            font-size: 14px;
            color: #888888;
            line-height: 1.7;
          }
        }

        .hero-metrics {
          display: flex;
          gap: 10px;

          .metric-card {
            min-width: 90px;
            padding: 14px 18px;
            background: rgba(255, 255, 255, 0.03);
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 8px;
            text-align: center;

            .metric-label {
              font-size: 10px;
              color: #555;
              text-transform: uppercase;
              letter-spacing: 0.1em;
              margin-bottom: 6px;
            }

            .metric-value {
              font-size: 22px;
              font-weight: 700;
              color: #ffffff;
              font-feature-settings: "tnum";
            }
          }
        }
      }
    }

    // ===== 内容布局 =====
    .content-layout {
      display: grid;
      grid-template-columns: 1fr 280px;
      gap: 32px;
      padding: 40px 0 60px;

      @media (max-width: 992px) {
        grid-template-columns: 1fr;
      }

      // ===== 主内容区 =====
      .main-content {
        // ===== 文章列表 - 极致简约 =====
        .article-list {
          display: flex;
          flex-direction: column;
          gap: 12px;

          .article-item {
            display: flex;
            gap: 20px;
            padding: 20px;
            background: transparent;
            border: 1px solid rgba(255, 255, 255, 0.08);
            border-radius: 6px;
            cursor: pointer;
            transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
            position: relative;
            overflow: hidden;

            &::before {
              content: '';
              position: absolute;
              top: 0;
              left: 0;
              right: 0;
              height: 1px;
              background: linear-gradient(90deg, rgba(0, 212, 255, 0.5), transparent);
              opacity: 0;
              transition: opacity 0.25s ease;
            }

            &:hover::before {
              opacity: 1;
            }

            &:hover {
              border-color: rgba(255, 255, 255, 0.15);
              background: rgba(255, 255, 255, 0.02);

              .article-cover img {
                transform: scale(1.03);
                filter: grayscale(0%);
              }
            }

            .article-cover {
              width: 200px;
              height: 120px;
              background: rgba(255, 255, 255, 0.05);
              border-radius: 4px;
              flex-shrink: 0;
              overflow: hidden;
              border: 1px solid rgba(255, 255, 255, 0.05);

              img {
                width: 100%;
                height: 100%;
                object-fit: cover;
                transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                filter: grayscale(20%);
              }
            }

            .article-content {
              flex: 1;
              display: flex;
              flex-direction: column;
              justify-content: space-between;
              min-width: 0;

              .article-author {
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 12px;
                font-weight: 500;
                color: #666;

                .author-avatar {
                  width: 22px;
                  height: 22px;
                  border-radius: 50%;
                  background: linear-gradient(135deg, #333 0%, #1a1a1a 100%);
                  border: 1px solid rgba(255, 255, 255, 0.1);
                }
              }

              .article-title {
                font-size: 17px;
                font-weight: 600;
                line-height: 1.45;
                color: #ffffff;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
                letter-spacing: -0.01em;
                transition: color 0.2s ease;

                &:hover {
                  color: #00d4ff;
                }
              }

              .article-description {
                font-size: 13px;
                color: #666;
                line-height: 1.6;
                display: -webkit-box;
                -webkit-line-clamp: 2;
                -webkit-box-orient: vertical;
                overflow: hidden;
              }

              .article-meta {
                display: flex;
                align-items: center;
                gap: 20px;
                font-size: 11px;
                color: #444;
                font-feature-settings: "tnum";

                span {
                  display: flex;
                  align-items: center;
                  gap: 5px;
                  transition: color 0.2s ease;
                }
              }

              &:hover .article-meta span {
                color: #666;
              }
            }
          }
        }
      }

      // ===== 侧边栏 =====
      .sidebar {
        display: flex;
        flex-direction: column;
        gap: 20px;

        .sidebar-card {
          background: rgba(255, 255, 255, 0.02);
          border: 1px solid rgba(255, 255, 255, 0.06);
          border-radius: 8px;
          padding: 20px;

          .card-title {
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 8px;
            color: #ffffff;
            display: flex;
            align-items: center;
            gap: 8px;
            letter-spacing: -0.01em;

            .icon {
              width: 16px;
              height: 16px;
              display: flex;
              align-items: center;
              justify-content: center;
              font-size: 14px;
            }
          }

          .card-description {
            font-size: 11px;
            color: #555;
            margin-bottom: 16px;
            line-height: 1.6;
          }

          // ===== 热门文章 =====
          .hot-articles {
            display: flex;
            flex-direction: column;
            gap: 8px;

            .hot-article-item {
              display: flex;
              gap: 10px;
              padding: 12px;
              background: rgba(255, 255, 255, 0.02);
              border: 1px solid rgba(255, 255, 255, 0.04);
              border-radius: 6px;
              cursor: pointer;
              transition: all 0.2s ease;

              &:hover {
                background: rgba(255, 255, 255, 0.04);
                border-color: rgba(255, 255, 255, 0.1);

                .hot-article-title {
                  color: #ffffff;
                }
              }

              .hot-article-rank {
                width: 24px;
                height: 24px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 11px;
                font-weight: 700;
                background: rgba(255, 255, 255, 0.05);
                border-radius: 4px;
                flex-shrink: 0;
                color: #444;
                font-feature-settings: "tnum";
              }

              &:nth-child(1) .hot-article-rank {
                color: #00d4ff;
                background: rgba(0, 212, 255, 0.1);
              }

              &:nth-child(2) .hot-article-rank {
                color: #7c3aed;
                background: rgba(124, 58, 237, 0.1);
              }

              &:nth-child(3) .hot-article-rank {
                color: #10b981;
                background: rgba(16, 185, 129, 0.1);
              }

              .hot-article-content {
                flex: 1;
                min-width: 0;

                .hot-article-title {
                  font-size: 13px;
                  font-weight: 500;
                  color: #aaa;
                  line-height: 1.4;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                  transition: color 0.2s ease;
                }

                .hot-article-meta {
                  display: flex;
                  gap: 12px;
                  font-size: 10px;
                  color: #444;
                  margin-top: 6px;
                  font-feature-settings: "tnum";

                  .hot-article-score {
                    color: #ff4757 !important;
                    font-weight: 600;
                  }
                }
              }
            }
          }

          // ===== 会员精选 =====
          .featured-articles {
            display: flex;
            flex-direction: column;
            gap: 8px;

            .featured-article-item {
              display: flex;
              gap: 10px;
              padding: 10px;
              background: rgba(0, 212, 255, 0.05);
              border: 1px solid rgba(0, 212, 255, 0.15);
              border-radius: 6px;
              cursor: pointer;
              transition: all 0.2s ease;

              &:hover {
                background: rgba(0, 212, 255, 0.08);
                border-color: rgba(0, 212, 255, 0.3);

                .featured-article-title {
                  color: #00d4ff;
                }
              }

              .featured-article-cover {
                width: 76px;
                height: 50px;
                background: rgba(255, 255, 255, 0.05);
                border-radius: 4px;
                flex-shrink: 0;
                overflow: hidden;
                border: 1px solid rgba(255, 255, 255, 0.05);

                img {
                  width: 100%;
                  height: 100%;
                  object-fit: cover;
                  filter: grayscale(10%);
                }
              }

              .featured-article-content {
                flex: 1;
                min-width: 0;

                .featured-article-title {
                  font-size: 12px;
                  font-weight: 500;
                  color: #ddd;
                  line-height: 1.4;
                  display: -webkit-box;
                  -webkit-line-clamp: 2;
                  -webkit-box-orient: vertical;
                  overflow: hidden;
                  transition: color 0.2s ease;
                }

                .featured-article-meta {
                  display: flex;
                  gap: 8px;
                  font-size: 10px;
                  color: #555;
                  margin-top: 4px;
                  font-feature-settings: "tnum";
                }
              }
            }
          }

          .featured-footer {
            margin-top: 16px;
            text-align: center;

            .featured-link {
              display: inline-block;
              padding: 10px 28px;
              background: transparent;
              border: 1px solid rgba(0, 212, 255, 0.3);
              border-radius: 4px;
              font-size: 11px;
              font-weight: 600;
              color: #00d4ff;
              cursor: pointer;
              transition: all 0.2s ease;
              letter-spacing: 0.05em;
              text-transform: uppercase;

              &:hover {
                background: rgba(0, 212, 255, 0.1);
                border-color: rgba(0, 212, 255, 0.5);
                color: #ffffff;
              }
            }
          }
        }
      }
    }

    // ===== 风格标识 =====
    .style-badge {
      position: fixed;
      bottom: 20px;
      right: 20px;
      padding: 8px 16px;
      background: rgba(0, 0, 0, 0.8);
      border: 1px solid rgba(0, 212, 255, 0.3);
      color: #00d4ff;
      font-size: 11px;
      font-weight: 600;
      border-radius: 4px;
      letter-spacing: 0.05em;
      backdrop-filter: blur(10px);
    }
  }
}
</style>
