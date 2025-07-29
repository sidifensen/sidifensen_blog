import { ref } from "vue";
import { defineStore } from "pinia";

export const useUserStore = defineStore(
  "user",
  () => {
    const user = ref({});
    return { user };
  },
  {
    persist: true,
  }
);
