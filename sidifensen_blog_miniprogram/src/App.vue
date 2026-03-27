<script setup>
import { onLaunch, onShow, onHide } from '@dcloudio/uni-app'
import { useAppStore } from '@/store/app'
import { useUserStore } from '@/store/user'

const appStore = useAppStore()
const userStore = useUserStore()

onLaunch(() => {
  console.log('App Launch')
  // 初始化主题
  appStore.initTheme()
  // 检查登录状态
  userStore.checkLogin()
})

onShow(() => {
  console.log('App Show')
})

onHide(() => {
  console.log('App Hide')
})
</script>

<style lang="scss">
@import '@/styles/variables.scss';
@import '@/styles/common.scss';
</style>
