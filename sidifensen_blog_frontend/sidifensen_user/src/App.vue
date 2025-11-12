<template>
  <router-view v-slot="{ Component }">
    <transition name="scale" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>

  <!-- 智能客服悬浮按钮 -->
  <div class="customer-service-float-btn" @click="openCustomerService" v-if="userStore.user">
    <el-tooltip content="智能客服小林" placement="left">
      <div class="service-btn">
        <!-- 脉冲动画背景 -->
        <div class="pulse-ring"></div>
        <div class="pulse-ring pulse-ring-delay-1"></div>
        <!-- 图标容器 -->
        <div class="icon-container">
          <svg class="service-icon" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <!-- 聊天气泡背景 -->
            <path d="M12 2C6.48 2 2 6.48 2 12c0 1.54.36 2.98.97 4.29L1 23l6.71-1.97C9.02 21.64 10.46 22 12 22c5.52 0 10-4.48 10-10S17.52 2 12 2z" fill="currentColor" opacity="0.1" />
            <!-- 聊天气泡主体 -->
            <path d="M7 9.5C7 8.67 7.67 8 8.5 8h7c.83 0 1.5.67 1.5 1.5v5c0 .83-.67 1.5-1.5 1.5h-7c-.83 0-1.5-.67-1.5-1.5v-5z" fill="currentColor" />
            <!-- 消息点 -->
            <circle cx="10" cy="12" r="1" fill="white" />
            <circle cx="12" cy="12" r="1" fill="white" />
            <circle cx="14" cy="12" r="1" fill="white" />
          </svg>
        </div>
      </div>
    </el-tooltip>
  </div>

  <!-- 智能客服对话窗口 -->
  <CustomerServiceChat ref="customerServiceChatRef" />
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/userStore";
import CustomerServiceChat from "@/components/CustomerServiceChat.vue";

const userStore = useUserStore();
const customerServiceChatRef = ref(null);

// 打开智能客服对话框
const openCustomerService = () => {
  customerServiceChatRef.value?.open();
};
</script>

<style lang="scss">
// 智能客服悬浮按钮
.customer-service-float-btn {
  position: fixed;
  right: 100px;
  bottom: 80px;
  z-index: 9999;
  cursor: pointer;

  .service-btn {
    position: relative;
    width: 64px;
    height: 64px;
    border-radius: 50%;
    background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
    box-shadow: 0 8px 24px rgba(64, 158, 255, 0.4), 0 0 0 0 rgba(64, 158, 255, 0.7);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    overflow: visible;

    // 脉冲动画背景
    .pulse-ring {
      position: absolute;
      width: 100%;
      height: 100%;
      border-radius: 50%;
      background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
      opacity: 0.6;
      animation: pulse-ring 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
    }

    .pulse-ring-delay-1 {
      animation-delay: 1s;
    }

    // 图标容器
    .icon-container {
      position: relative;
      z-index: 1;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }

    // 自定义 SVG 图标
    .service-icon {
      width: 100%;
      height: 100%;
      filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.2));
      transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    }

    // 悬停效果
    &:hover {
      transform: scale(1.1) translateY(-2px);
      box-shadow: 0 12px 32px rgba(64, 158, 255, 0.5), 0 0 0 8px rgba(64, 158, 255, 0.1);

      .icon-container {
        transform: scale(1.1);
      }

      .service-icon {
        transform: rotate(5deg);
      }

      .pulse-ring {
        animation-duration: 1.5s;
      }
    }

    // 点击效果
    &:active {
      transform: scale(1.05) translateY(0);
      box-shadow: 0 6px 20px rgba(64, 158, 255, 0.4);

      .icon-container {
        transform: scale(0.95);
      }
    }
  }

  // 脉冲动画
  @keyframes pulse-ring {
    0% {
      transform: scale(1);
      opacity: 0.6;
    }
    50% {
      transform: scale(1.3);
      opacity: 0.3;
    }
    100% {
      transform: scale(1.6);
      opacity: 0;
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    right: 20px;
    bottom: 70px;

    .service-btn {
      width: 56px;
      height: 56px;

      .icon-container {
        width: 28px;
        height: 28px;
      }
    }
  }

  // 小屏幕优化
  @media (max-width: 480px) {
    right: 22px;
    bottom: 80px;

    .service-btn {
      width: 52px;
      height: 52px;

      .icon-container {
        width: 26px;
        height: 26px;
      }
    }
  }
}
</style>
