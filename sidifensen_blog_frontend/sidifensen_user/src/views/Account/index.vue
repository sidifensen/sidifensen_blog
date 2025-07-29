<template>
  <Header></Header>
  <div class="login_container">
    <div class="left_panel"></div>
    <div class="welcome_title">
      <div>欢迎来到斯蒂芬森的博客</div>
      <div>在这里，你可以找到我喜欢的文章，分享自己的想法，与志同道合的朋友交流。</div>
    </div>
    <div class="right_panel">
      <router-view v-slot="{ Component }">
        <transition name="slide" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script>

</script>

<style lang="scss" scoped>
:deep(.el-form) {
  //将样式穿透到子组件
  width: 420px;
}
:deep(.is-plain) {
  width: 70%;
}
.login_container {
  display: flex;
  height: calc(100vh - 40px);
  .left_panel {
    flex: 1;
    background: url("@/assets/img/bg.jpg") no-repeat center center / cover;
  }
  .welcome_title {
    position: absolute;
    left: 20px;
    bottom: 20px;
    color: white;
    text-shadow: 0 0 10px white;
    div:first-child {
      font-size: 20px;
      font-weight: bold;
      margin-top: 10px;
    }
  }
  .right_panel {
    z-index: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    flex-direction: column;
    text-align: center;
    width: 500px;
    background-color: var(--el-bg-color);
  }
  @media screen and (max-width: 600px) {
    .left_panel {
      display: none;
    }
    .right_panel {
      width: 100vw;
    }
  }
}

</style>
