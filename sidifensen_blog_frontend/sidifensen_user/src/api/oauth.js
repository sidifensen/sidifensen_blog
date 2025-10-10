const backendServer = import.meta.env.VITE_BACKEND_SERVER;
// 登录
export const giteeLogin = backendServer + "/oauth/gitee/login";
export const githubLogin = backendServer + "/oauth/github/login";
export const qqLogin = backendServer + "/oauth/qq/login";
