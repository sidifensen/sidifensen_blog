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
});

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
    let { code, msg } = response.data;
    if (code == 200) {
      if (msg) {
        ElMessage.success(msg);
      }
      return response;
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
      ElMessage.error(data.msg);
      // 清除useStore数据和localStorage中的jwt
      RemoveJwt();
      // 需要重新登陆，跳转到登录页面
      router.push("/login");
    }
    return Promise.reject(error);
  }
);

// 导出
export default request;
