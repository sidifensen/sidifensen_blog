// 封装axios，做请求处理
// 导入axios
import axios from "axios";
// 引入router
import router from "@/router/index.js";
// 获取token
import { GetJwt, RemoveJwt } from "@/utils/Auth.js";
// 引入ElMessage
import { ElMessage } from "element-plus";

// 创建axios
const request = axios.create({
  // 后端地址
  baseURL: import.meta.env.VITE_BACKEND_SERVER,
  withCredentials: false, // 用于配置请求接口跨域时是否需要凭证
  timeout: 30000, // 超时时间，单位毫秒
  headers: {
    // 配置请求头的参数类型，和编码格式
    "Content-Type": "application/json;charset=UTF-8",
  },
  // 正确编码空格为 %20（兼容 Spring @RequestParam）
  paramsSerializer: (params) => {
    return Object.entries(params)
      .filter(([_, v]) => v !== undefined && v !== null && v !== '')
      .map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`)
      .join('&');
  },
});

// 登录过期处理锁，防止多个请求同时 401 时弹出多个弹窗
let isHandlingAuthError = false;

// 配置请求的拦截器
request.interceptors.request.use(
  (config) => {
    // 在请求头添加token, 判断是否需要发送token
    if (GetJwt()) {
      config.headers["Authorization"] = GetJwt();
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 配置响应拦截器
request.interceptors.response.use(
  (response) => {
    let { code, msg,  data } = response.data;
    if (code == 200) {
      if (msg) {
        ElMessage.success(msg);
      }
      return response.data;
    } else {
      // 响应失败的处理 401 400
      ElMessage.error(msg);
    }
    return Promise.reject(response.data);
  },
  (error) => {
    let { status, data } = error.response;
    if (status === 401) {
      // 401 代表token过期，需要重新登录
      // 使用锁防止多个请求同时 401 时弹出多个弹窗
      if (!isHandlingAuthError) {
        isHandlingAuthError = true;
        ElMessage.error(data.msg || "登录已过期，请重新登录");
        // 清除useStore数据和localStorage中的jwt
        RemoveJwt();
        // 需要重新登陆，跳转到登录页面
        router.push("/login");
        // 3秒后解锁，防止极端情况下无法再次触发
        setTimeout(() => {
          isHandlingAuthError = false;
        }, 3000);
      }
    }
    return Promise.reject(error);
  }
);

// 导出
export default request;
