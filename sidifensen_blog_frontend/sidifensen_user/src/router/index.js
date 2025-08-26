import { createRouter, createWebHistory, createWebHashHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  // history: createWebHashHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "index",
      component: () => import("@/views/Layout/index.vue"),
      meta: { title: "主页" },
      children: [
        {
          path: "/",
          name: "Home",
          component: () => import("@/views/Home/index.vue"),
          meta: { title: "首页" },
        },
        {
          path: "article",
          name: "Article",
          component: () => import("@/components/404.vue"),
          meta: { title: "文章" },
        },
        {
          path: "link",
          name: "Link",
          component: () => import("@/components/404.vue"),
          meta: { title: "友链" },
        },
        {
          path: "album",
          name: "Album",
          component: () => import("@/views/Album/index.vue"),
          meta: { title: "相册" },
          redirect: "/album/square",
          children: [
            {
              path: "square",
              name: "AlbumSquare",
              component: () => import("@/views/Album/AlbumSquare.vue"),
              meta: { title: "相册广场" },
            },
            {
              path: "myAlbum",
              name: "MyAlbum",
              component: () => import("@/views/Album/MyAlbum.vue"),
              meta: { title: "我的相册" },
            },
            {
              path: ":albumId",
              name: "AlbumDetail",
              component: () => import("@/views/Album/AlbumDetail.vue"),
              meta: { title: "相册详情" },
            },
          ],
        },
        {
          path: "account",
          name: "Account",
          component: () => import("@/views/Account/index.vue"),
          redirect: "/login",
          children: [
            {
              path: "/login",
              component: () => import("@/views/Account/Login/index.vue"),
              name: "login",
              meta: { title: "用户登录" },
            },
            {
              path: "/register",
              component: () => import("@/views/Account/Register/index.vue"),
              name: "register",
              meta: { title: "用户注册" },
            },
            {
              path: "/reset",
              component: () => import("@/views/Account/Reset/index.vue"),
              name: "reset",
              meta: { title: "重置密码" },
            },
          ],
        },
      ],
    },
    {
      path: "/creation",
      name: "Creation",
      component: () => import("@/views/Creation/index.vue"),
      meta: { title: "创作中心" },
      children: [
        {
          path: "",
          name: "创作中心首页",
          component: () => import("@/views/Creation/Home/index.vue"),
        },
        {
          path: "articlemanage",
          name: "创作中心文章管理",
          component: () => import("@/views/Creation/ArticleManage/index.vue"),
        },
        {
          path: "columnmanage",
          name: "创作中心专栏管理",
          component: () => import("@/views/Creation/ColumnManage/index.vue"),
        },
        {
          path: "commentmanage",
          name: "创作中心评论管理",
          component: () => import("@/views/Creation/CommentManage/index.vue"),
        },
      ],
    },
    {
      path: "/editor",
      name: "Editor",
      component: () => import("@/views/Editor/index.vue"),
      meta: { title: "发布文章" },
    },
    {
      path: "/:pathMatch(.*)*",
      name: "NotFound",
      component: () => import("@/components/404.vue"),
      meta: { title: "页面不存在" },
    },
  ],
});

export default router;
