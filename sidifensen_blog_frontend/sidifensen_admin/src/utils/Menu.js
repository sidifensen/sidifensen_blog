export const formatMenu = (menuList) => {
  // 先按sort排序所有菜单（父菜单排序）
  menuList.sort((a, b) => a.sort - b.sort);

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

  menuList = formatMenuData(menuList);
  return menuList;
};
