import {useUserStore} from "@/stores/userStore.js";
import { jwtDecode } from "jwt-decode";

// 获取token 
export const GET_TOKEN = () => {
    const jwt = localStorage.getItem("sidifensen_blog_jwt");
    if (jwt) {
        return jwt;
    }
    return null;
}

// 设置token 
export const SET_TOKEN = (jwt) => {
    const userStore = useUserStore();
    localStorage.setItem("sidifensen_blog_jwt", jwt);
    const user = jwtDecode(jwt);
    userStore.user = user;
};

// 移除token 
export const REMOVE_TOKEN = () => {
    localStorage.removeItem("sidifensen_blog_jwt");
};