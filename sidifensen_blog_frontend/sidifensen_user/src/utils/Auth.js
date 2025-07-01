import {ElMessage} from "element-plus";
// import useUserStore from "@/store/modules/user.js";

// 获取token 
export const GET_TOKEN = () => {
    const str = localStorage.getItem("sidifensen_blog_jwt") || sessionStorage.getItem("sidifensen_blog_jwt");
    if (!str) return null;
    // 解析json 
    const authObject = JSON.parse(str);
    // 判断token是否过期 
    if (new Date(authObject.expire) <= new Date()) {
        REMOVE_TOKEN();
        ElMessage.warning('登录状态已过期，请重新登录');
        return null;
    }
    return authObject.token;
};

// 设置token 
export const SET_TOKEN = (token, expire, remember) => {
    // const userStore = useUserStore();
    const authObject = {token, expire};
    const str = JSON.stringify(authObject);
    // 是否记住密码(记住密码token存localStorage,否则存sessionStorage) 
    remember ? localStorage.setItem("sidifensen_blog_jwt", str) : sessionStorage.setItem("sidifensen_blog_jwt", str);
    userStore.token = token;
};

// 移除token 
export const REMOVE_TOKEN = () => {
    localStorage.removeItem("sidifensen_blog_jwt");
    sessionStorage.removeItem("sidifensen_blog_jwt");
};