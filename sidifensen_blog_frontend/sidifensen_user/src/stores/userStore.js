import { ref, computed } from "vue";
import { defineStore } from "pinia";
import { RemoveJwt } from "@/utils/Auth";

export const useUserStore = defineStore(
  "user",
  () => {
    const user = ref({});
    const isLoggedIn = computed(() => !!user.value && !!user.value.id);
    const clearUser = () => {
      RemoveJwt();
      user.value = null;
    }
    return { user, isLoggedIn, clearUser };
  },
  {
    persist: true,
  }
);
