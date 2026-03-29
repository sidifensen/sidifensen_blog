<script setup>
</script>

<template>
  <view class="about-page">
    <view class="about-header">
      <image class="logo" src="/static/logo.svg" mode="aspectFit" />
      <view class="app-name">斯蒂芬森博客</view>
      <view class="version">版本 1.0.0</view>
    </view>

    <view class="about-content card">
      <view class="section-title">关于我们</view>
      <view class="section-text">
        斯蒂芬森博客是一个专注于技术分享与交流的平台，致力于为开发者提供优质的内容和良好的交流社区。
      </view>
      <view class="section-text">
        我们鼓励原创技术分享，欢迎每一位热爱技术的朋友加入我们，共同成长。
      </view>
    </view>

    <view class="about-content card">
      <view class="section-title">联系方式</view>
      <view class="contact-item">
        <text class="contact-label">邮箱：</text>
        <text class="contact-value">contact@sidifensen.com</text>
      </view>
      <view class="contact-item">
        <text class="contact-label">网站：</text>
        <text class="contact-value">https://sidifensen.com</text>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.about-page {
  min-height: 100vh;
  background: var(--u-bg-color);
  padding: var(--spacing-lg);
}

.about-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-2xl) 0;

  .logo {
    width: 80px;
    height: 80px;
    margin-bottom: var(--spacing-lg);
  }

  .app-name {
    font-size: 20px;
    font-weight: 600;
    color: var(--u-main-color);
  }

  .version {
    font-size: 14px;
    color: var(--u-tips-color);
    margin-top: var(--spacing-sm);
  }
}

.about-content {
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-lg);

  .section-title {
    font-size: 16px;
    font-weight: 600;
    color: var(--u-main-color);
    margin-bottom: var(--spacing-md);
  }

  .section-text {
    font-size: 14px;
    color: var(--u-content-color);
    line-height: 1.6;
    margin-bottom: var(--spacing-md);

    &:last-child {
      margin-bottom: 0;
    }
  }

  .contact-item {
    display: flex;
    padding: var(--spacing-sm) 0;

    .contact-label {
      font-size: 14px;
      color: var(--u-tips-color);
      width: 60px;
    }

    .contact-value {
      font-size: 14px;
      color: var(--u-main-color);
    }
  }
}
</style>
