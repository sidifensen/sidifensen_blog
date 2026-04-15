<template>
  <div class="hero-cosmic-ring">
    <!-- 中心发光核心 -->
    <div class="cosmic-core">
      <div class="core-inner"></div>
      <div class="core-atmosphere"></div>
    </div>

    <!-- 星环轨道 -->
    <div class="ring-orbit ring-1">
      <div class="ring-dot dot-1"></div>
    </div>
    <div class="ring-orbit ring-2">
      <div class="ring-dot dot-2"></div>
    </div>
    <div class="ring-orbit ring-3">
      <div class="ring-dot dot-3"></div>
      <div class="ring-dot dot-4"></div>
    </div>

    <!-- 散落星点 -->
    <div class="star star-1"></div>
    <div class="star star-2"></div>
    <div class="star star-3"></div>
    <div class="star star-4"></div>
    <div class="star star-5"></div>
  </div>
</template>

<script setup>
import { onMounted, onUnmounted } from 'vue'

onMounted(() => {})
onUnmounted(() => {})
</script>

<style lang="scss" scoped>
// ===== 宇宙星环组件 =====
.hero-cosmic-ring {
  position: absolute;
  right: 8%;
  top: 50%;
  transform: translateY(-50%);
  width: 480px;
  height: 480px;
  pointer-events: none;

  @media (max-width: 1200px) {
    display: none;
  }

  // ===== 中心发光核心 =====
  .cosmic-core {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    width: 80px;
    height: 80px;
    border-radius: 50%;

    .core-inner {
      position: absolute;
      inset: 0;
      border-radius: 50%;
      background: radial-gradient(
        circle at 30% 30%,
        #60a5fa 0%,
        #3b82f6 30%,
        #1d4ed8 60%,
        #1e3a8a 100%
      );
      box-shadow:
        inset -8px -8px 20px rgba(0, 0, 0, 0.4),
        inset 4px 4px 10px rgba(255, 255, 255, 0.1);
    }

    .core-atmosphere {
      position: absolute;
      inset: -8px;
      border-radius: 50%;
      background: radial-gradient(
        circle at 50% 50%,
        transparent 50%,
        rgba(59, 130, 246, 0.15) 70%,
        rgba(59, 130, 246, 0.05) 100%
      );
    }
  }

  // ===== 星环轨道 =====
  .ring-orbit {
    position: absolute;
    left: 50%;
    top: 50%;
    border-radius: 50%;
    border: 1px solid;
    transform: translate(-50%, -50%);
  }

  // 第一层轨道
  .ring-1 {
    width: 140px;
    height: 140px;
    border-color: rgba(59, 130, 246, 0.5);
    border-width: 2px;
    animation: ringRotate1 20s linear infinite;

    .ring-dot {
      position: absolute;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: #3b82f6;
      box-shadow: 0 0 10px #3b82f6;
    }

    .dot-1 {
      top: -4px;
      left: 50%;
      transform: translateX(-50%);
    }
  }

  // 第二层轨道
  .ring-2 {
    width: 250px;
    height: 250px;
    border-color: rgba(30, 58, 138, 0.4);
    border-width: 2px;
    animation: ringRotate2 30s linear infinite;

    .ring-dot {
      position: absolute;
      width: 6px;
      height: 6px;
      border-radius: 50%;
      background: #1e3a8a;
      box-shadow: 0 0 8px #1e3a8a;
    }

    .dot-2 {
      bottom: -3px;
      left: 50%;
      transform: translateX(-50%);
    }
  }

  // 第三层轨道
  .ring-3 {
    width: 370px;
    height: 370px;
    border-color: rgba(59, 130, 246, 0.25);
    border-width: 2px;
    animation: ringRotate3 40s linear infinite;

    .ring-dot {
      position: absolute;
      width: 5px;
      height: 5px;
      border-radius: 50%;
      background: #2563eb;
      box-shadow: 0 0 6px #2563eb;
    }

    .dot-3 {
      top: -2.5px;
      left: 50%;
      transform: translateX(-50%);
    }

    .dot-4 {
      bottom: -2.5px;
      left: 50%;
      transform: translateX(-50%);
    }
  }

  // ===== 散落星点 =====
  .star {
    position: absolute;
    width: 3px;
    height: 3px;
    border-radius: 50%;
    background: #fff;
    animation: starTwinkle 3s ease-in-out infinite;

    &::before {
      content: '';
      position: absolute;
      inset: -2px;
      border-radius: 50%;
      background: radial-gradient(circle, rgba(255, 255, 255, 0.8), transparent);
    }
  }

  .star-1 {
    top: 15%;
    left: 20%;
    animation-delay: 0s;
  }
  .star-2 {
    top: 25%;
    right: 25%;
    animation-delay: 0.5s;
  }
  .star-3 {
    top: 60%;
    left: 10%;
    animation-delay: 1s;
  }
  .star-4 {
    top: 70%;
    right: 15%;
    animation-delay: 1.5s;
  }
  .star-5 {
    top: 40%;
    left: 5%;
    animation-delay: 2s;
  }
}

// ===== 动画 =====
@keyframes ringRotate1 {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@keyframes ringRotate2 {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  to {
    transform: translate(-50%, -50%) rotate(-360deg);
  }
}

@keyframes ringRotate3 {
  from {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  to {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@keyframes starTwinkle {
  0%,
  100% {
    opacity: 0.3;
    transform: scale(1);
  }
  50% {
    opacity: 1;
    transform: scale(1.5);
  }
}

// ===== 黑夜模式适配 =====
html.dark {
  .hero-cosmic-ring {
    .cosmic-core {
      .core-inner {
        background: radial-gradient(
          circle at 30% 30%,
          #93c5fd 0%,
          #60a5fa 30%,
          #3b82f6 60%,
          #1d4ed8 100%
        );
      }

      .core-atmosphere {
        background: radial-gradient(
          circle at 50% 50%,
          transparent 50%,
          rgba(96, 165, 250, 0.2) 70%,
          rgba(96, 165, 250, 0.1) 100%
        );
      }
    }

    .ring-1 {
      border-color: rgba(96, 165, 250, 0.4);
      .ring-dot {
        background: #93c5fd;
        box-shadow: 0 0 12px #93c5fd;
      }
    }

    .ring-2 {
      border-color: rgba(59, 130, 246, 0.35);
      .ring-dot {
        background: #60a5fa;
        box-shadow: 0 0 10px #60a5fa;
      }
    }

    .ring-3 {
      border-color: rgba(59, 130, 246, 0.2);
      .ring-dot {
        background: #3b82f6;
        box-shadow: 0 0 8px #3b82f6;
      }
    }
  }
}
</style>
