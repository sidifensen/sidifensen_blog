// 获取token 
export const GetJwt = () => {
    const jwt = localStorage.getItem("sidifensen_blog_jwt");
    if (jwt) {
        return jwt;
    }
    return null;
}

// 设置token 
export const SetJwt = (jwt) => {
    localStorage.setItem("sidifensen_blog_jwt", jwt);
};

// 移除token 
export const RemoveJwt = () => {
    localStorage.removeItem("sidifensen_blog_jwt");
};