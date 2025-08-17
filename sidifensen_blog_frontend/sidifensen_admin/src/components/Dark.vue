<template>
  <div class="dark-box">
    <el-button text class="switch" :class="{ 'is-dark': !isDark, 'ripple-active': rippleActive }" @click="handleToggle">
      <div class="icon-wrapper">
        <el-icon v-if="!isDark" class="sun-icon">
          <svg viewBox="0 0 24 24">
            <path
              d="M6.05 4.14l-.39-.39a.993.993 0 0 0-1.4 0l-.01.01a.984.984 0 0 0 0 1.4l.39.39c.39.39 1.01.39 1.4 0l.01-.01a.984.984 0 0 0 0-1.4zM3.01 10.5H1.99c-.55 0-.99.44-.99.99v.01c0 .55.44.99.99.99H3c.56.01 1-.43 1-.98v-.01c0-.56-.44-1-.99-1zm9-9.95H12c-.56 0-1 .44-1 .99v.96c0 .55.44.99.99.99H12c.56.01 1-.43 1-.98v-.97c0-.55-.44-.99-.99-.99zm7.74 3.21c-.39-.39-1.02-.39-1.41-.01l-.39.39a.984.984 0 0 0 0 1.4l.01.01c.39.39 1.02.39 1.4 0l.39-.39a.984.984 0 0 0 0-1.4zm-1.81 15.1l.39.39a.996.996 0 1 0 1.41-1.41l-.39-.39a.993.993 0 0 0-1.4 0c-.4.4-.4 1.02-.01 1.41zM20 11.49v.01c0 .55.44.99.99.99H22c.55 0 .99-.44.99-.99v-.01c0-.55-.44-.99-.99-.99h-1.01c-.55 0-.99.44-.99.99zM12 5.5c-3.31 0-6 2.69-6 6s2.69 6 6 6s6-2.69 6-6s-2.69-6-6-6zm-.01 16.95H12c.55 0 .99-.44.99-.99v-.96c0-.55-.44-.99-.99-.99h-.01c-.55 0-.99.44-.99.99v.96c0 .55.44.99.99.99zm-7.74-3.21c.39.39 1.02.39 1.41 0l.39-.39a.993.993 0 0 0 0-1.4l-.01-.01a.996.996 0 0 0-1.41 0l-.39.39c-.38.4-.38 1.02.01 1.41z"
              fill="currentColor"></path>
          </svg>
        </el-icon>
        <el-icon v-else class="moon-icon">
          <svg viewBox="0 0 24 24">
            <path d="M11.01 3.05C6.51 3.54 3 7.36 3 12a9 9 0 0 0 9 9c4.63 0 8.45-3.5 8.95-8c.09-.79-.78-1.42-1.54-.95A5.403 5.403 0 0 1 11.1 7.5c0-1.06.31-2.06.84-2.89c.45-.67-.04-1.63-.93-1.56z" fill="currentColor"></path>
          </svg>
        </el-icon>
      </div>
      <div class="bg-shine"></div>
      <div class="ripple-effect"></div>
    </el-button>
  </div>
</template>
<script setup>
import { useDarkStore } from "@/stores/darkStore";
import { storeToRefs } from "pinia";
import { ref } from "vue";

const darkStore = useDarkStore();
const { isDark } = storeToRefs(darkStore);
const { toggleDark } = darkStore;
const rippleActive = ref(false);

// 处理按钮点击，添加波纹效果
const handleToggle = () => {
  rippleActive.value = true;
  toggleDark();
  setTimeout(() => {
    rippleActive.value = false;
  }, 800);
};
</script>

<style scoped lang="scss">
.dark-box {
  margin-right: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.switch {
  width: 80px;
  height: 30px;
  border: none;
  border-radius: 20px;
  box-sizing: border-box;
  cursor: pointer;
  padding: 0;
  position: relative;
  overflow: hidden;
  background: linear-gradient(145deg, #333333, #111111); /* 白天模式深黑色渐变 */
  box-shadow: 3px 3px 6px rgba(255, 255, 255, 0.8), -3px -3px 6px rgba(0, 0, 0, 0.1), 3px -3px 6px rgba(0, 0, 0, 0.1), -3px 3px 6px rgba(0, 0, 0, 0.1); /* 白色阴影移至右下角 */
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);

  // 点击效果
  &:active {
    transform: scale(0.98);
  }

  .icon-wrapper {
    position: absolute;
    top: 50%;
    left: 6px;
    width: 24px;
    height: 24px;
    transform: translateY(-50%) translateX(0);
    transition: transform 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94), background 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 2;
    background: white;
    border-radius: 50%;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);

    // 添加光晕效果
    &::after {
      content: "";
      position: absolute;
      width: 32px;
      height: 32px;
      background: radial-gradient(circle, rgba(60, 60, 60, 0.8) 0%, rgba(255, 255, 255, 0) 70%); /* 深黑色光晕 */
      border-radius: 50%;
      z-index: -1;
      opacity: 0;
      transition: opacity 0.3s ease;
    }
  }

  .sun-icon,
  .moon-icon {
    width: 18px;
    height: 18px;
    transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  }

  .sun-icon {
    color: #ff9800;
    opacity: 0;
    transform: rotate(-90deg) scale(0.8);
    // 添加阳光效果
    filter: drop-shadow(0 0 3px rgba(255, 152, 0, 0.6));
  }

  .moon-icon {
    color: #000000; 
    opacity: 1;
    transform: rotate(0deg) scale(1);
    // 添加月光效果
    filter: drop-shadow(0 0 3px rgba(0, 0, 0, 0.8)); /* 纯黑色阴影 */
  }

  // 背景光泽效果
  .bg-shine {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, rgba(255, 255, 255, 0) 0%, rgba(60, 60, 60, 0.3) 50%, rgba(255, 255, 255, 0) 100%); /* 深黑色光泽 */
    transform: translateX(-100%);
    transition: transform 0.6s ease-in-out;
    z-index: 1;
  }

  // 波纹效果
  .ripple-effect {
    position: absolute;
    width: 100px;
    height: 100px;
    background: rgba(255, 255, 255, 0.3);
    border-radius: 50%;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%) scale(0);
    opacity: 0;
    z-index: 0;
    transition: transform 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94), opacity 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  }

  // 激活波纹效果
  &.ripple-active .ripple-effect {
    transform: translate(-50%, -50%) scale(1.5);
    opacity: 1;
  }

  // 鼠标悬停时光泽效果和图标光晕
  &:hover {
    .bg-shine {
      transform: translateX(100%);
    }
    .icon-wrapper::after {
      opacity: 1;
    }
  }

  &.is-dark {
    background: linear-gradient(145deg, #ffffff, #f5f5f5); /* 夜间模式白色渐变 */
    box-shadow: 3px 3px 6px rgba(255, 255, 255, 0.8), -3px -3px 6px rgba(0, 0, 0, 0.1), 3px -3px 6px rgba(0, 0, 0, 0.1), -3px 3px 6px rgba(0, 0, 0, 0.1); /* 夜间模式白色阴影移至右下角 */

    .icon-wrapper {
      transform: translateY(-50%) translateX(46px);
      background: #ffffff; /* 夜间模式白色背景 */
    }

    .sun-icon {
      opacity: 1;
      transform: rotate(0deg) scale(1);
      color: #000; /* 夜间模式纯黑色图标 */
      filter: drop-shadow(0 0 5px rgba(0, 0, 0, 0.8)); /* 纯黑色阴影 */
    }

    .moon-icon {
      opacity: 0;
      transform: rotate(90deg) scale(0.8);
    }

    .bg-shine {
      background: linear-gradient(90deg, rgba(255, 255, 255, 0) 0%, rgba(255, 255, 255, 0.5) 50%, rgba(255, 255, 255, 0) 100%);
    }

    .ripple-effect {
      background: rgba(255, 255, 255, 0.5);
    }
  }
}
</style>
