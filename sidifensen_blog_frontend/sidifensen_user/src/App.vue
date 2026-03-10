<template>
  <router-view v-slot="{ Component }">
    <transition name="scale" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>

  <!-- 智能客服悬浮按钮 -->
  <div class="customer-service-float-btn" @click="openCustomerService">
    <el-tooltip content="智能客服小林" placement="left">
      <div class="service-btn">
        <!-- 机器人图标 -->
        <svg class="service-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <rect x="3" y="11" width="18" height="10" rx="2"/>
          <circle cx="12" cy="5" r="2"/>
          <path d="M12 7v4"/>
          <line x1="8" y1="16" x2="8" y2="16"/>
          <line x1="16" y1="16" x2="16" y2="16"/>
        </svg>
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
    background: #3b82f6;
    box-shadow: 0 4px 16px rgba(59, 130, 246, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    color: white;
    transition: all 0.2s;

    // 机器人图标
    .service-icon {
      width: 32px;
      height: 32px;
      transition: all 0.2s;
    }

    // 悬停效果
    &:hover {
      background: #2563eb;
      box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);

      .service-icon {
        transform: scale(1.05);
      }
    }

    // 点击效果
    &:active {
      transform: scale(0.95);
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
    right: 20px;
    bottom: 70px;

    .service-btn {
      width: 56px;
      height: 56px;

      .service-icon {
        width: 28px;
        height: 28px;
      }
    }
  }

  // 小屏幕优化
  @media (max-width: 480px) {
    right: 16px;
    bottom: 70px;

    .service-btn {
      width: 52px;
      height: 52px;

      .service-icon {
        width: 26px;
        height: 26px;
      }
    }
  }
}
</style>
