<script setup>
</script>

<template>
  <view class="agreement-page">
    <view class="content card">
      <view class="section-title">用户协议</view>
      <view class="section-date">更新日期：2024年1月1日</view>

      <view class="article">
        <view class="article-title">1. 服务说明</view>
        <view class="article-content">
          斯蒂芬森博客是一个技术分享平台，为用户提供文章发布、评论、收藏等功能。使用本平台即表示您同意遵守本协议的所有条款。
        </view>
      </view>

      <view class="article">
        <view class="article-title">2. 用户注册</view>
        <view class="article-content">
          您在注册时需要提供真实的个人信息，并确保信息的准确性。您有责任妥善保管账户信息，因个人保管不当造成的损失由用户自行承担。
        </view>
      </view>

      <view class="article">
        <view class="article-title">3. 内容规范</view>
        <view class="article-content">
          用户发布的内容必须遵守法律法规，不得包含违法、暴力、色情、政治敏感等内容。我们保留对违规内容进行删除的权利。
        </view>
      </view>

      <view class="article">
        <view class="article-title">4. 知识产权</view>
        <view class="article-content">
          用户在平台上发布的原创内容，知识产权归用户所有。但用户需授权平台在合理范围内使用这些内容。
        </view>
      </view>

      <view class="article">
        <view class="article-title">5. 隐私保护</view>
        <view class="article-content">
          我们尊重并保护用户的个人隐私，具体详见《隐私政策》。如有疑问，请联系我们。
        </view>
      </view>

      <view class="article">
        <view class="article-title">6. 免责声明</view>
        <view class="article-content">
          对于因不可抗力或第三方原因导致的服务中断，我们不承担责任。用户因使用本平台造成的任何损失，由用户自行承担。
        </view>
      </view>

      <view class="article">
        <view class="article-title">7. 协议修改</view>
        <view class="article-content">
          我们有权随时修改本协议，修改后的协议将在平台上公布。请用户定期查看，以确保了解最新的协议内容。
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.agreement-page {
  min-height: 100vh;
  background: var(--u-bg-color);
  padding: var(--spacing-lg);
}

.content {
  padding: var(--spacing-lg);

  .section-title {
    font-size: 20px;
    font-weight: 600;
    color: var(--u-main-color);
    text-align: center;
    margin-bottom: var(--spacing-sm);
  }

  .section-date {
    font-size: 12px;
    color: var(--u-tips-color);
    text-align: center;
    margin-bottom: var(--spacing-xl);
  }

  .article {
    margin-bottom: var(--spacing-lg);

    .article-title {
      font-size: 15px;
      font-weight: 600;
      color: var(--u-main-color);
      margin-bottom: var(--spacing-sm);
    }

    .article-content {
      font-size: 14px;
      color: var(--u-content-color);
      line-height: 1.6;
    }
  }
}
</style>
