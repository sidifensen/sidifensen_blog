<template>
  <div class="login_container">
    <div class="left_panel"></div>
    <div class="welcome_title">
      <div>欢迎来到斯蒂芬森的博客</div>
      <div>在这里，你可以找到我喜欢的文章，分享自己的想法，与志同道合的朋友交流。</div>
      <motion.div
        :transition="transition"
      />
    </div>
    <div class="right_panel">
      <router-view v-slot="{ Component }">
        <transition name="scale" mode="out-in">
          <component :is="Component"/>
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script>
const transition = {
  duration: 0.8,
  delay: 0.5,
  ease: [0, 0.71, 0.2, 1.01],
}
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
  height: 100vh;
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
    background-color: #fff;
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
