import { createRouter, createWebHistory } from "vue-router";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/",
      name: "index",
      component: () => import("@/views/Layout/index.vue"),
      meta: {
        title: "主页",
      },
      children: [
        {
          path: "/",
          name: "Home",
          component: () => import("@/views/Home/index.vue"),
          meta: {
            title: "首页",
          },
        },
        {
          path: "/albumSquare",
          name: "AlbumSquare",
          component: () => import("@/views/Album/AlbumSquare.vue"),
          meta: {
            title: "相册广场",
          },
        },
        {
          path: "/myAlbum",
          name: "MyAlbum",
          component: () => import("@/views/Album/MyAlbum.vue"),
          meta: {
            title: "我的相册",
          },
        },
        {
          path: "/album/:albumId",
          name: "AlbumDetail",
          component: () => import("@/views/Album/AlbumDetail.vue"),
          meta: {
            title: "相册详情",
          },
        },
      ],
    },
    {
      path: "/account",
      name: "account",
      component: () => import("@/views/Account/index.vue"),
      redirect: "/login",
      children: [
        {
          path: "/login",
          component: () => import("@/views/Account/Login/index.vue"),
          name: "login",
          meta: {
            title: "用户登录",
          },
        },
        {
          path: "/register",
          component: () => import("@/views/Account/Register/index.vue"),
          name: "register",
          meta: {
            title: "用户注册",
          },
        },
        {
          path: "/reset",
          component: () => import("@/views/Account/Reset/index.vue"),
          name: "reset",
          meta: {
            title: "重置密码",
          },
        },
      ],
    },
  ],
});

export default router;
