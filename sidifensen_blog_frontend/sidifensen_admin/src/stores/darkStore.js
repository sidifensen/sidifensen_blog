import { defineStore } from "pinia";
import { ref, watch } from "vue";

 export const useDarkStore = defineStore(
  "dark",
  () => {
    const isDark = ref(false);

    const toggleDark = () => {
      isDark.value = !isDark.value;
      updateHtmlClass();
    };

    const updateHtmlClass = () => {
      const html = document.querySelector("html");
      if (html) {
        if (!isDark.value) {
          html.classList.remove("dark");
          html.classList.add("light");
        } else {
          html.classList.remove("light");
          html.classList.add("dark");
        }
      }
    };

    // 初始化时更新 html 类名
    const initDarkMode = () => {
      updateHtmlClass();
    };

    return { isDark, toggleDark, initDarkMode };
  },
  {
    persist: {
      enabled: true,
      strategies: [
        {
          key: 'darkMode',
          storage: localStorage,
        }
      ]
    }
  }
);