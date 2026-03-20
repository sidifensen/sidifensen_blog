<template>
  <router-view v-slot="{ Component }">
    <transition name="scale" mode="out-in">
      <component :is="Component" />
    </transition>
  </router-view>

  <!-- 智能客服悬浮按钮 -->
  <div
    class="customer-service-float-btn"
    @mousedown="handleMouseDown"
    @mouseup="handleMouseUp"
    @touchstart="handleTouchStart"
    @touchend="handleTouchEnd"
    @click="handleClick"
    :style="{ right: buttonPosition.right + 'px', bottom: buttonPosition.bottom + 'px' }"
    :class="{ 'is-dragging': isDragging }"
  >
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
  </div>

  <!-- 粒子特效容器（fixed 定位，全局） -->
  <div class="particles-fixed-container">
    <div
      v-for="particle in particles"
      :key="particle.id"
      class="particle"
      :style="{
        left: particle.x + 'px',
        top: particle.y + 'px',
        width: particle.size + 'px',
        height: particle.size + 'px',
        opacity: particle.opacity,
        transform: `scale(${particle.scale})`,
        background: particle.color
      }"
    />
  </div>

  <!-- 智能客服对话窗口 -->
  <CustomerServiceChat ref="customerServiceChatRef" />
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useUserStore } from "@/stores/userStore";
import CustomerServiceChat from "@/components/CustomerServiceChat.vue";

const userStore = useUserStore();
const customerServiceChatRef = ref(null);

// 拖拽相关状态
const isDragging = ref(false);
const buttonPosition = ref({ right: 100, bottom: 80 });
const btnWidth = 64; // 按钮宽度（px）
const dragOffset = ref({ x: 0, y: 0 }); // 触摸点相对于按钮左上角的偏移
const hasMoved = ref(false); // 标记是否发生了移动（区分点击和拖拽）
const particles = ref([]); // 粒子数组
let particleId = 0; // 粒子 ID 计数器
let lastParticleTime = 0; // 上次生成粒子的时间

// 粒子颜色数组（蓝色系渐变）
const particleColors = [
  'rgba(59, 130, 246, 0.8)',  // #3b82f6
  'rgba(96, 165, 250, 0.7)',  // #60a5fa
  'rgba(147, 197, 253, 0.6)', // #93c5fd
  'rgba(186, 230, 253, 0.5)', // #bae6fd
];

// 缓存窗口尺寸
let windowWidth = window.innerWidth;
let windowHeight = window.innerHeight;

// 创建粒子
const createParticle = (x, y) => {
  const id = particleId++;
  const size = Math.random() * 6 + 4; // 4-10px
  const offsetX = (Math.random() - 0.5) * 20; // 左右偏移
  const offsetY = (Math.random() - 0.5) * 20; // 上下偏移
  const color = particleColors[Math.floor(Math.random() * particleColors.length)];

  particles.value.push({
    id,
    x: x + offsetX,
    y: y + offsetY,
    size,
    opacity: 0.8,
    scale: 1,
    color,
    createdAt: Date.now()
  });

  // 限制粒子数量
  if (particles.value.length > 20) {
    particles.value.shift();
  }
};

// 更新粒子（动画）
const updateParticles = () => {
  if (!isDragging.value) {
    particles.value = [];
    return;
  }

  const now = Date.now();
  particles.value = particles.value.filter(p => {
    const age = now - p.createdAt;
    // 粒子存活时间 500ms
    if (age > 500) {
      return false;
    }
    // 逐渐淡出
    p.opacity = 0.8 * (1 - age / 500);
    p.scale = 1 + (age / 500) * 0.5; // 稍微放大
    return true;
  });

  if (isDragging.value) {
    requestAnimationFrame(updateParticles);
  }
};

// 从 localStorage 加载保存的位置
const loadSavedPosition = () => {
  try {
    const saved = localStorage.getItem("aiServiceBtnPosition");
    if (saved) {
      const pos = JSON.parse(saved);
      // 验证位置是否在合理范围内
      const maxX = window.innerWidth - btnWidth;
      const maxY = window.innerHeight - btnWidth;
      if (pos.right >= 0 && pos.right <= maxX && pos.bottom >= 0 && pos.bottom <= maxY) {
        buttonPosition.value = pos;
        return true;
      }
    }
  } catch (e) {
    console.warn("加载按钮位置失败", e);
  }
  return false;
};

// 保存位置到 localStorage
const savePosition = () => {
  try {
    localStorage.setItem("aiServiceBtnPosition", JSON.stringify(buttonPosition.value));
  } catch (e) {
    console.warn("保存按钮位置失败", e);
  }
};

// 更新按钮位置（带边界限制）- 使用 left/top 计算
const updatePositionByLeftTop = (left, top) => {
  const maxX = windowWidth - btnWidth;
  const maxY = windowHeight - btnWidth;

  // 限制在屏幕范围内
  const clampedLeft = Math.max(0, Math.min(left, maxX));
  const clampedTop = Math.max(0, Math.min(top, maxY));

  // 转换为 right/bottom 存储
  buttonPosition.value = {
    right: windowWidth - clampedLeft - btnWidth,
    bottom: windowHeight - clampedTop - btnWidth
  };
};

// 鼠标事件处理
const handleMouseDown = (e) => {
  // 只响应左键
  if (e.button !== 0) return;
  isDragging.value = true;
  hasMoved.value = false;
  // 记录鼠标位置相对于按钮左上角的偏移
  const btnRect = e.currentTarget.getBoundingClientRect();
  dragOffset.value = {
    x: e.clientX - btnRect.left,
    y: e.clientY - btnRect.top
  };
  // 添加全局事件监听（鼠标可能移出按钮）
  window.addEventListener('mousemove', handleMouseMove, { passive: false });
  window.addEventListener('mouseup', handleMouseUp);
};

const handleMouseMove = (e) => {
  if (!isDragging.value) return;
  e.preventDefault();
  // 计算新的位置（鼠标位置减去偏移量得到按钮左上角位置）
  const newLeft = e.clientX - dragOffset.value.x;
  const newTop = e.clientY - dragOffset.value.y;
  updatePositionByLeftTop(newLeft, newTop);
  // 标记发生了移动
  hasMoved.value = true;
  // 生成粒子（限制生成频率）- 使用鼠标位置作为粒子生成点
  const now = Date.now();
  if (now - lastParticleTime > 50) { // 每 50ms 生成一个粒子
    createParticle(e.clientX, e.clientY);
    lastParticleTime = now;
    updateParticles();
  }
};

const handleMouseUp = () => {
  if (isDragging.value) {
    isDragging.value = false;
    savePosition();
    // 移除全局事件监听
    window.removeEventListener('mousemove', handleMouseMove);
    window.removeEventListener('mouseup', handleMouseUp);
    // 注意：不要在这里重置 hasMoved，让 click 事件先检查它
  }
};

// 触摸事件处理
const handleTouchStart = (e) => {
  isDragging.value = true;
  hasMoved.value = false;
  const touch = e.touches[0];
  const btnRect = e.currentTarget.getBoundingClientRect();
  // 记录触摸点相对于按钮左上角的偏移
  dragOffset.value = {
    x: touch.clientX - btnRect.left,
    y: touch.clientY - btnRect.top
  };
  // 添加全局事件监听
  window.addEventListener('touchmove', handleTouchMove, { passive: false });
  window.addEventListener('touchend', handleTouchEnd);
};

const handleTouchMove = (e) => {
  if (!isDragging.value) return;
  e.preventDefault(); // 防止滚动
  const touch = e.touches[0];
  // 计算新的位置
  const newLeft = touch.clientX - dragOffset.value.x;
  const newTop = touch.clientY - dragOffset.value.y;
  updatePositionByLeftTop(newLeft, newTop);
  // 标记发生了移动
  hasMoved.value = true;
  // 生成粒子（限制生成频率）- 使用触摸位置作为粒子生成点
  const now = Date.now();
  if (now - lastParticleTime > 50) { // 每 50ms 生成一个粒子
    createParticle(touch.clientX, touch.clientY);
    lastParticleTime = now;
    updateParticles();
  }
};

const handleTouchEnd = () => {
  if (isDragging.value) {
    isDragging.value = false;
    savePosition();
    // 移除全局事件监听
    window.removeEventListener('touchmove', handleTouchMove);
    window.removeEventListener('touchend', handleTouchEnd);
    // 注意：不要在这里重置 hasMoved，让 click 事件先检查它
  }
};

// 窗口大小变化时重新检查边界
const handleWindowResize = () => {
  windowWidth = window.innerWidth;
  windowHeight = window.innerHeight;
  const maxX = windowWidth - btnWidth;
  const maxY = windowHeight - btnWidth;

  // 如果当前位置超出新边界，调整到范围内
  if (buttonPosition.value.right > maxX) {
    buttonPosition.value.right = maxX;
  }
  if (buttonPosition.value.bottom > maxY) {
    buttonPosition.value.bottom = maxY;
  }
  savePosition();
};

// 处理点击事件
const handleClick = () => {
  // 如果发生了移动，说明是拖拽而非点击，不响应
  if (hasMoved.value) return;

  // 切换弹窗状态
  customerServiceChatRef.value?.toggle();
};

// 生命周期
onMounted(() => {
  // 加载保存的位置
  loadSavedPosition();

  // 监听窗口大小变化
  window.addEventListener("resize", handleWindowResize);
});

onUnmounted(() => {
  // 清理事件监听器
  window.removeEventListener("resize", handleWindowResize);
});
</script>

<style lang="scss">
// 文章广场页面专属背景覆盖 - 黑夜模式下覆盖全局背景色
.article-page-body {
  min-height: 100vh;
}

html.dark .article-page-body {
  --el-bg-color-page: #000000 !important;
  background-color: #000000 !important;
}

// 粒子特效容器（全局 fixed）
.particles-fixed-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 10000;
  overflow: visible;

  .particle {
    position: absolute;
    border-radius: 50%;
    pointer-events: none;
  }
}

// 智能客服悬浮按钮
.customer-service-float-btn {
  position: fixed;
  // right 和 bottom 由 JavaScript 动态控制
  z-index: 9999;
  cursor: grab;
  user-select: none; // 防止拖拽时选中文字
  touch-action: none; // 防止拖拽时触发页面滚动

  // 拖拽中样式
  &.is-dragging {
    cursor: grabbing;

    .service-btn {
      opacity: 0.9;
      transform: scale(1.05);
      box-shadow: 0 8px 24px rgba(59, 130, 246, 0.5);

      .service-icon {
        transform: scale(1.02);
      }
    }
  }

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
    transition: transform 0.2s, box-shadow 0.2s, opacity 0.15s;

    // 机器人图标
    .service-icon {
      width: 32px;
      height: 32px;
      transition: transform 0.2s;
    }

    // 悬停效果（桌面端）
    @media (hover: hover) {
      &:hover {
        background: #2563eb;
        box-shadow: 0 6px 20px rgba(59, 130, 246, 0.4);

        .service-icon {
          transform: scale(1.08);
        }
      }
    }
  }

  // 响应式设计
  @media (max-width: 768px) {
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
