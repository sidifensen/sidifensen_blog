import { createRouter, createWebHistory,createWebHashHistory } from "vue-router";
import { useUserStore } from "@/stores/userStore";
import { RemoveJwt } from "@/utils/Auth";
import { GetJwt } from "@/utils/Auth";

// 创建路由实例
const router = createRouter({
  // history: createWebHashHistory(import.meta.env.BASE_URL),
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/login",
      name: "login",
      component: () => import("@/views/account/login.vue"),
    },
    {
      path: "/",
      name: "layout",
      component: () => import("@/components/Layout.vue"),
      children: [], // 动态路由将在这里添加
    },
  ],
});

// 导入404组件
const NotFound = () => import("@/components/404.vue");

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();

  // 如果没有登录
  if (!GetJwt()) {
    // 如果访问的不是登录,则跳转到登录页
    if (to.path !== "/login") {
      next({ path: "/login" });
      return;
    }
    next();
    return;
  }

  // 用户已登录但信息未加载
  if (!userStore.user) {
    try {
      // 获取用户信息
      await userStore.getUserInfo();
    } catch (error) {
      console.error("获取用户信息失败:", error);
      RemoveJwt();
      next({ path: "/login" });
      return;
    }
  }

  // 用户信息已加载，但路由未添加
  if (!userStore.routesAdded) {
    try {
      // 加载菜单和动态路由
      const dynamicRoutes = await userStore.loadMenusAndRoutes();
      // 添加动态路由到Layout的children中
      const layoutRoute = router.getRoutes().find((route) => route.path === "/");
      if (layoutRoute) {
        dynamicRoutes.forEach((route) => {
          // 检查路由是否已经添加，避免重复添加
          const existingRoute = router.getRoutes().find((r) => r.name === route.name && r.path === route.path);
          if (!existingRoute) {
            router.addRoute(layoutRoute.name, route);
          }
        });
      } else {
        console.error("未找到Layout路由");
      }
      userStore.routesAdded = true; // 标记路由已添加

      // 添加404路由，确保在所有动态路由之后添加
      router.addRoute({
        path: "/:pathMatch(.*)*",
        name: "notFound",
        component: NotFound,
      });

      // 如果是刷新页面，需要重新导航到目标路由
      if (to.path !== "/login") {
        next({ ...to, replace: true });
        return;
      }
    } catch (error) {
      console.error("加载菜单和路由失败:", error);
      RemoveJwt();
      next({ path: "/login" });
      return;
    }
  }

  // 如果用户已登录且访问登录页，则跳转到主页
  if (to.path === "/login") {
    next({ path: "/" });
    return;
  }

  next();
});

// 导出路由实例
export default router;
