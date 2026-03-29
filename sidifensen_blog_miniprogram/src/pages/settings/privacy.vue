<script setup>
</script>

<template>
  <view class="privacy-page">
    <view class="content card">
      <view class="section-title">隐私政策</view>
      <view class="section-date">更新日期：2024年1月1日</view>

      <view class="article">
        <view class="article-title">1. 信息收集</view>
        <view class="article-content">
          我们收集您主动提供的信息，包括但不限于：注册信息（昵称、头像、邮箱）、发布的内容、评论记录等。
        </view>
      </view>

      <view class="article">
        <view class="article-title">2. 信息使用</view>
        <view class="article-content">
          我们使用收集的信息用于：提供和改进服务、内容推荐、用户交流、安全保障等目的。
        </view>
      </view>

      <view class="article">
        <view class="article-title">3. 信息共享</view>
        <view class="article-content">
          未经您的同意，我们不会与任何无关第三方共享您的个人信息，以下情况除外：法律法规要求、保护我们的权利、安全考虑。
        </view>
      </view>

      <view class="article">
        <view class="article-title">4. 信息安全</view>
        <view class="article-content">
          我们采用合理的安全措施保护您的个人信息，防止数据遭到未经授权的访问、使用或泄露。
        </view>
      </view>

      <view class="article">
        <view class="article-title">5. Cookie使用</view>
        <view class="article-content">
          我们可能使用Cookie技术来改善用户体验，Cookie是存储在您设备上的小文件，您可以通过设置禁用Cookie。
        </view>
      </view>

      <view class="article">
        <view class="article-title">6. 您的权利</view>
        <view class="article-content">
          您有权访问、修改或删除您的个人信息。如需行使这些权利，请通过联系我们进行操作。
        </view>
      </view>

      <view class="article">
        <view class="article-title">7. 未成年人保护</view>
        <view class="article-content">
          我们非常重视对未成年人信息的保护。如果您是未成年人，请在监护人的指导下使用我们的服务。
        </view>
      </view>

      <view class="article">
        <view class="article-title">8. 政策更新</view>
        <view class="article-content">
          我们可能会不时更新本隐私政策，更新后的政策将在平台上公布。继续使用服务即表示您接受更新后的政策。
        </view>
      </view>

      <view class="article">
        <view class="article-title">9. 联系我们</view>
        <view class="article-content">
          如您对本隐私政策有任何疑问，请通过以下方式联系我们：contact@sidifensen.com
        </view>
      </view>
    </view>
  </view>
</template>

<style lang="scss" scoped>
.privacy-page {
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
