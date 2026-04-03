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
</style>
