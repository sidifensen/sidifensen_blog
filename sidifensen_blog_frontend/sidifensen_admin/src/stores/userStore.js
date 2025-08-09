import { ref } from "vue";
import { defineStore } from "pinia";
import { getMenuList } from "@/api/menu";
import { info } from "@/api/user";
import { RemoveJwt } from "@/utils/Auth";

// 使用import.meta.glob批量导入视图组件和404组件
const modules = {};
// 导入视图组件
Object.assign(modules, import.meta.glob("../views/**/*.vue"));
// 导入404组件
Object.assign(modules, import.meta.glob("../components/404.vue"));

export const useUserStore = defineStore("user", () => {
  const user = ref(null);
  const menus = ref([]);
  const routes = ref([]);
  const menusLoaded = ref(false); // 用于跟踪菜单是否已加载
  const routesAdded = ref(false); // 用于跟踪动态路由是否已添加到路由器

  // 设置用户信息
  const setUser = (userInfo) => {
    user.value = userInfo;
  };

  // 获取用户信息
  const getUserInfo = async () => {
    // 获取用户信息
    await info().then((res) => {
      user.value = res.data.data;
    });
  };

  // 清除用户信息
  const clearUser = () => {
    RemoveJwt();
    user.value = null;
    menus.value = [];
    routes.value = [];
    menusLoaded.value = false; // 清除菜单已加载标志
    routesAdded.value = false; // 清除路由已添加标志
  };

  // 生成路由的函数
  const generateRoutes = (menuList) => {
    const routes = [];
    menuList.forEach((menu) => {
      // 确保组件路径正确
      const componentPath = menu.component ? `../views${menu.component}/index.vue` : "";
      const route = {
        path: menu.path || "/",
        name: menu.name,
        component: modules[componentPath],
        meta: {
          title: menu.name,
          icon: menu.icon,
        },
      };
      if (menu.children && menu.children.length > 0) {
        route.children = generateRoutes(menu.children);
      }
      routes.push(route);
    });
    return routes;
  };

  // 获取菜单并生成路由
  const loadMenusAndRoutes = async () => {
    // 如果菜单已加载，则直接返回已有的路由
    if (menusLoaded.value) {
      return routes.value;
    }
    try {
      const res = await getMenuList();
      menus.value = res.data.data;
      // 格式化菜单数据，添加children属性
      const formattedMenus = formatMenuData(menus.value);
      menus.value = formattedMenus;
      routes.value = generateRoutes(formattedMenus);
      menusLoaded.value = true; // 标记菜单已加载
      // 路由将在 router/index.js 中添加，这里不设置 routesAdded
      return routes.value;
    } catch (error) {
      console.error("获取菜单失败:", error);
      return [];
    }
  };

  // 格式化菜单数据，将扁平结构转换为树形结构
  const formatMenuData = (menuList) => {
    const menuMap = {};
    const rootMenus = [];

    // 创建菜单映射
    menuList.forEach((menu) => {
      menuMap[menu.id] = { ...menu, children: [] };
    });

    // 构建树形结构
    menuList.forEach((menu) => {
      if (menu.parentId === 0) {
        rootMenus.push(menuMap[menu.id]);
      } else if (menuMap[menu.parentId]) {
        menuMap[menu.parentId].children.push(menuMap[menu.id]);
      }
    });

    return rootMenus;
  };

  return {
    user,
    menus,
    routes,
    routesAdded,
    setUser,
    clearUser,
    loadMenusAndRoutes,
    getUserInfo,
  };
});
