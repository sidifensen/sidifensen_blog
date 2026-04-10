<script>
import { onLaunch } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'
import { initThemeListener } from '@/utils/theme'

export default {
  onLaunch() {
    const userStore = useUserStore()
    userStore.checkLogin()
    initThemeListener()
  }
}
</script>

<style lang="scss">
@import '@/styles/variables.scss';
@import '@/styles/common.scss';
@import 'uview-pro/index.scss';

/* 确保 html 元素使用主题背景色 */
html {
  background: var(--u-bg-color);
}

/* 导航栏深色模式适配 - 使用最高优先级确保覆盖 uni-app 内置样式 */
html.u-theme-dark,
html[data-u-theme-mode="dark"],
:root.u-theme-dark {
  .uni-page-head {
    background-color: var(--u-bg-color) !important;
    color: var(--u-main-color) !important;
  }
}
</style>
