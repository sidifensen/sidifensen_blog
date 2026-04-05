<template>
  <div class="login-page">
    <!-- 左侧品牌区 -->
    <div class="login-brand-section">
      <div class="brand-content">
        <h1 class="brand-name">Sidifensen</h1>
        <p class="brand-tagline">
          专业的技术内容平台<br />
          连接开发者，分享知识价值
        </p>
        <div class="brand-stats">
          <div class="stat-item">
            <div class="stat-value">50K+</div>
            <div class="stat-label">活跃用户</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">10K+</div>
            <div class="stat-label">优质文章</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">99%</div>
            <div class="stat-label">好评率</div>
          </div>
        </div>
      </div>
      <!-- 装饰性背景线条 -->
      <div class="brand-decoration"></div>
    </div>

    <!-- 右侧表单区 -->
    <div class="login-form-section">
      <router-view v-slot="{ Component }">
        <transition name="fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script></script>

<style lang="scss" scoped>
// CSS 变量定义 - 支持黑夜模式
.login-page {
  // 浅色模式（默认）
  --brand-bg-start: #1e3a5f;
  --brand-bg-end: #2c5282;
  --brand-text: #ffffff;
  --brand-text-secondary: rgba(255, 255, 255, 0.8);
  --stat-value-color: #60a5fa;
  --stat-label-color: rgba(255, 255, 255, 0.7);
  --form-bg: #ffffff;
  --decoration-bg: rgba(255, 255, 255, 0.03);

  display: flex;
  min-height: 100vh;
  background: var(--form-bg);
}

// 黑夜模式覆盖
html.dark {
  .login-page {
    --brand-bg-start: #0f172a;
    --brand-bg-end: #1e293b;
    --brand-text: #f1f5f9;
    --brand-text-secondary: rgba(241, 245, 249, 0.7);
    --stat-value-color: #60a5fa;
    --stat-label-color: rgba(241, 245, 249, 0.5);
    --form-bg: #0f172a;
    --decoration-bg: rgba(255, 255, 255, 0.02);
  }
}

// 左侧品牌区
.login-brand-section {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px;
  background: linear-gradient(135deg, var(--brand-bg-start) 0%, var(--brand-bg-end) 100%);
  position: relative;
  overflow: hidden;
}

.brand-content {
  position: relative;
  z-index: 1;
  max-width: 480px;
}

.brand-name {
  font-size: 48px;
  font-weight: 700;
  color: var(--brand-text);
  margin-bottom: 24px;
  letter-spacing: -1px;
}

.brand-tagline {
  font-size: 18px;
  color: var(--brand-text-secondary);
  line-height: 1.7;
  margin-bottom: 60px;
}

.brand-stats {
  display: flex;
  gap: 48px;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 36px;
  font-weight: 700;
  color: var(--stat-value-color);
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: var(--stat-label-color);
}

// 装饰性背景
.brand-decoration {
  position: absolute;
  top: -50%;
  right: -20%;
  width: 80%;
  height: 200%;
  background: var(--decoration-bg);
  transform: rotate(-15deg);
  pointer-events: none;
}

// 右侧表单区
.login-form-section {
  width: 520px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
  background: var(--form-bg);
}

// 过渡动画
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

// 移动端适配
@media screen and (max-width: 900px) {
  .login-brand-section {
    display: none;
  }

  .login-form-section {
    width: 100%;
    min-height: 100vh;
    padding: 24px;
  }
}
</style>
