<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

onLaunch(() => {
  // 检查登录状态
  userStore.checkLogin()
})

onShow(() => {
})

onHide(() => {
})
</script>

<style lang="scss">
@import '@/styles/variables.scss';
@import '@/styles/common.scss';
/* uView Pro 主题变量（必须在 index.scss 之前引入，以定义 SCSS 变量） */
@import 'uview-pro/theme.scss';
/* uView Pro 全局样式 */
@import 'uview-pro/index.scss';
</style>
