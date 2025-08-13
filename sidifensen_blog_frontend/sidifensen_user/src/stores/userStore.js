import { ref } from "vue";
import { defineStore } from "pinia";
import { RemoveJwt } from "@/utils/Auth";

export const useUserStore = defineStore(
  "user",
  () => {
    const user = ref({});
    const clearUser = () => {
      RemoveJwt();
      user.value = null;
    }
    return { user,clearUser };
  },
  {
    persist: true,
  }
);
