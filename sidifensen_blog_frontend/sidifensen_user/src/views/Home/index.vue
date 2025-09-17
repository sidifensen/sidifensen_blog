<template>
  <div class="home-container">
    <!-- 主要内容区域 -->
    <main class="main-content">
      <div class="container">
        <!-- 英雄区域 -->
        <section class="hero">
          <h1>欢迎来到sidifensen博客</h1>
          <p>分享技术、经验和见解</p>
          <a href="/article" class="btn">浏览文章</a>
        </section>

        <!-- 最新文章 -->
        <section class="latest-articles">
          <h2>最新文章</h2>
          <div class="article-list">
            <article v-for="article in articles" :key="article.id" class="article-card">
              <div class="article-image">
                <img :src="article.image" alt="{{ article.title }}" />
              </div>
              <div class="article-content">
                <div class="article-meta">
                  <span class="category">{{ article.category }}</span>
                  <span class="date">{{ article.date }}</span>
                </div>
                <h3>
                  <router-link :to="'/article/' + article.id">{{ article.title }}</router-link>
                </h3>
                <p>{{ article.excerpt }}</p>
                <router-link :to="'/article/' + article.id" class="read-more">阅读更多</router-link>
              </div>
            </article>
          </div>
          <div class="load-more">
            <button @click="loadMoreArticles">加载更多</button>
          </div>
        </section>
      </div>
    </main>

    <!-- 页脚 -->
    <footer class="footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-logo">
            <h3>sidifensen博客</h3>
            <p>分享技术、经验和见解</p>
          </div>
          <div class="footer-links">
            <div class="footer-column">
              <h4>导航</h4>
              <ul>
                <li><router-link to="/">首页</router-link></li>
                <li><router-link to="/articles">文章</router-link></li>
                <li><router-link to="/categories">分类</router-link></li>
                <li><router-link to="/tags">标签</router-link></li>
                <li><router-link to="/about">关于</router-link></li>
              </ul>
            </div>
            <div class="footer-column">
              <h4>联系我们</h4>
              <ul>
                <li>邮箱: contact@example.com</li>
                <li>微信: example_wechat</li>
                <li>微博: example_weibo</li>
              </ul>
            </div>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; {{ currentYear }} sidifensen博客. 保留所有权利.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";

// 模拟文章数据
const articles = ref([
  {
    id: 1,
    title: "Vue 3 组合式API入门教程",
    excerpt: "本文介绍Vue 3的组合式API，包括setup函数、ref、reactive等核心概念...",
    image: "http://115.190.116.72:40000/sidifensen-blog/album/1/1/d888b05f9a4345e7b49c59d569e851d3.webp",
    category: "前端开发",
    date: "2023-06-15",
  },
  {
    id: 2,
    title: "TypeScript高级类型使用技巧",
    excerpt: "深入探讨TypeScript中的高级类型特性，如泛型、条件类型、映射类型等...",
    image: "http://115.190.116.72:40000/sidifensen-blog/album/1/2/658334308098465fbe66b5d4b3266ef9.webp",
    category: "前端开发",
    date: "2023-06-10",
  },
  {
    id: 3,
    title: "React性能优化实践指南",
    excerpt: "分享React应用性能优化的实用技巧，包括组件懒加载、memo、useCallback等...",
    image: "http://115.190.116.72:40000/sidifensen-blog/album/1/3/1d29f054b0f34070a6889dc49bf35ae3.webp",
    category: "前端开发",
    date: "2023-06-05",
  },
]);

const isMenuOpen = ref(false);
const currentYear = ref(new Date().getFullYear());
const router = useRouter();

// 切换移动端菜单
const toggleMenu = () => {
  isMenuOpen.value = !isMenuOpen.value;
};

// 关闭移动端菜单
const closeMenu = () => {
  isMenuOpen.value = false;
};

// 加载更多文章
const loadMoreArticles = () => {
  // 这里可以添加加载更多文章的逻辑
  console.log("加载更多文章");
};

// 组件挂载时执行
onMounted(() => {
  // 可以添加初始化逻辑
});
</script>

<style lang="scss" scoped>
// 全局样式变量
$primary-color: #3498db;
$secondary-color: #2ecc71;
$text-color: #333;
$light-gray: #f5f5f5;
$medium-gray: #e0e0e0;
$dark-gray: #666;
$white: #fff;
$shadow: 0 2px 10px rgba(0, 0, 0, 0.1);

// 工具类
.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.btn {
  display: inline-block;
  padding: 10px 20px;
  background-color: $primary-color;
  color: $white;
  border: none;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;

  &:hover {
    background-color: #2980b9;
    transform: translateY(-2px);
  }
}

// 导航栏样式
.navbar {
  background-color: $white;
  box-shadow: $shadow;
  position: sticky;
  top: 0;
  z-index: 100;

  .container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 70px;
  }

  .logo {
    font-size: 1.5rem;
    font-weight: 700;

    a {
      color: $primary-color;
      text-decoration: none;
    }
  }

  .nav-links {
    ul {
      display: flex;
      list-style: none;

      li {
        margin-left: 25px;

        a {
          color: $text-color;
          text-decoration: none;
          font-weight: 500;
          transition: color 0.3s ease;

          &:hover {
            color: $primary-color;
          }
        }
      }
    }
  }

  .mobile-menu-btn {
    display: none;
    background: none;
    border: none;
    font-size: 1.5rem;
    color: $text-color;
    cursor: pointer;
  }
}

// 移动端菜单
.mobile-menu {
  position: fixed;
  top: 70px;
  left: 0;
  right: 0;
  background-color: $white;
  box-shadow: $shadow;
  z-index: 99;

  ul {
    list-style: none;
    padding: 20px;

    li {
      margin-bottom: 15px;

      a {
        color: $text-color;
        text-decoration: none;
        font-size: 1.1rem;
        font-weight: 500;

        &:hover {
          color: $primary-color;
        }
      }
    }
  }
}

// 主要内容区域样式
.main-content {
  padding: 40px 0;

  .hero {
    text-align: center;
    padding: 60px 0;
    background-color: $light-gray;
    border-radius: 8px;
    margin-bottom: 40px;

    h1 {
      font-size: 2.5rem;
      margin-bottom: 15px;
      color: $text-color;
    }

    p {
      font-size: 1.2rem;
      color: $dark-gray;
      margin-bottom: 30px;
    }
  }

  .latest-articles {
    margin-bottom: 40px;

    h2 {
      font-size: 1.8rem;
      margin-bottom: 25px;
      color: $text-color;
      position: relative;
      padding-bottom: 10px;

      &::after {
        content: "";
        position: absolute;
        bottom: 0;
        left: 0;
        width: 80px;
        height: 3px;
        background-color: $primary-color;
      }
    }

    .article-list {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
      gap: 30px;
      margin-bottom: 30px;
    }

    .article-card {
      background-color: $white;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: $shadow;
      transition: transform 0.3s ease;

      &:hover {
        transform: translateY(-5px);
      }

      .article-image {
        height: 200px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s ease;

          &:hover {
            transform: scale(1.05);
          }
        }
      }

      .article-content {
        padding: 20px;

        .article-meta {
          display: flex;
          justify-content: space-between;
          margin-bottom: 10px;
          font-size: 0.9rem;
          color: $dark-gray;

          .category {
            background-color: $light-gray;
            padding: 3px 8px;
            border-radius: 4px;
          }
        }

        h3 {
          margin-bottom: 10px;
          font-size: 1.2rem;

          a {
            color: $text-color;
            text-decoration: none;
            transition: color 0.3s ease;

            &:hover {
              color: $primary-color;
            }
          }
        }

        p {
          color: $dark-gray;
          margin-bottom: 15px;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .read-more {
          display: inline-block;
          color: $primary-color;
          text-decoration: none;
          font-weight: 500;
          transition: color 0.3s ease;

          &:hover {
            color: #2980b9;
          }
        }
      }
    }

    .load-more {
      text-align: center;

      button {
        padding: 10px 20px;
        background-color: $white;
        color: $primary-color;
        border: 1px solid $primary-color;
        border-radius: 4px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background-color: $primary-color;
          color: $white;
        }
      }
    }
  }
}

// 页脚样式
.footer {
  background-color: #2c3e50;
  color: $white;
  padding: 40px 0 20px;

  .footer-content {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30px;

    .footer-logo {
      max-width: 300px;

      h3 {
        font-size: 1.5rem;
        margin-bottom: 10px;
      }

      p {
        color: #bdc3c7;
      }
    }

    .footer-links {
      display: flex;
      gap: 40px;

      .footer-column {
        h4 {
          font-size: 1.1rem;
          margin-bottom: 15px;
          color: #ecf0f1;
        }

        ul {
          list-style: none;

          li {
            margin-bottom: 10px;

            a {
              color: #bdc3c7;
              text-decoration: none;
              transition: color 0.3s ease;

              &:hover {
                color: $white;
              }
            }
          }
        }
      }
    }
  }

  .footer-bottom {
    text-align: center;
    padding-top: 20px;
    border-top: 1px solid #34495e;
    color: #bdc3c7;
    font-size: 0.9rem;
  }
}

// 响应式样式
@media screen and (max-width: 992px) {
  .navbar {
    .nav-links {
      display: none;
    }

    .mobile-menu-btn {
      display: block;
    }
  }

  .main-content {
    .latest-articles {
      .article-list {
        grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      }
    }
  }

  .footer {
    .footer-content {
      flex-direction: column;
      gap: 30px;

      .footer-links {
        flex-direction: column;
        gap: 20px;
      }
    }
  }
}

@media screen and (max-width: 768px) {
  .main-content {
    .hero {
      padding: 40px 0;

      h1 {
        font-size: 2rem;
      }

      p {
        font-size: 1rem;
      }
    }

    .latest-articles {
      .article-list {
        grid-template-columns: 1fr;
      }
    }
  }
}
</style>
