// 防调试和禁用开发者工具功能
export function enableAntiDebug() {
  // 禁用右键菜单
  document.addEventListener("contextmenu", (e) => {
    e.preventDefault();
    if (typeof ElMessage !== 'undefined') {
      ElMessage.warning("右键菜单已禁用");
    }
  });

  // 阻止F12、Ctrl+Shift+I等快捷键
  document.addEventListener("keydown", (e) => {
    // F12
    if (e.key === "F12") {
      e.preventDefault();
      if (typeof ElMessage !== 'undefined') {
        ElMessage.warning("开发者工具已禁用");
      }
    }
    // Ctrl+Shift+I
    if (e.ctrlKey && e.shiftKey && e.key === "I") {
      e.preventDefault();
      if (typeof ElMessage !== 'undefined') {
        ElMessage.warning("开发者工具已禁用");
      }
    }
    // Ctrl+Shift+J
    if (e.ctrlKey && e.shiftKey && e.key === "J") {
      e.preventDefault();
      if (typeof ElMessage !== 'undefined') {
        ElMessage.warning("开发者工具已禁用");
      }
    }
  });

  // 检测调试器的定时器
  setInterval(function () {
    var startTime = performance.now();
    debugger;
    var endTime = performance.now();

    if (endTime - startTime > 100) {
      window.location.href = "about:blank";
    }
  }, 100);
}

export default {
  enableAntiDebug
};