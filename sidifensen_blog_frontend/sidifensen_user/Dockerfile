FROM nginx:alpine

# 设置时区为中国标准时间(UTC+8)
ENV TZ=Asia/Shanghai

# 更换为国内镜像源以加快包下载速度
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# 安装curl工具和tzdata包，用于健康检查和时区支持
RUN apk update && apk --no-cache add curl tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 创建Nginx日志目录并设置权限
RUN mkdir -p /var/log/nginx && chown -R nginx:nginx /var/log/nginx

# 删除默认配置文件
RUN rm /etc/nginx/conf.d/default.conf

# 添加自定义配置文件
ADD default.conf /etc/nginx/conf.d/

# 复制编译好的前端文件
COPY dist/ /usr/share/nginx/html/

# 设置容器启动命令
CMD ["nginx", "-g", "daemon off;"]