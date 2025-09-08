# 使用官方的Eclipse Temurin OpenJDK 21 Alpine镜像
FROM eclipse-temurin:21-alpine

# 设置时区为中国标准时间(UTC+8)
ENV TZ=Asia/Shanghai

# 更换为国内镜像源以加快包下载速度
RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories

# 安装时区数据包并设置时区
RUN apk update && apk --no-cache add tzdata && \
  ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
  echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 复制项目的jar文件到容器中
COPY ./target/sidifensen_blog_backend-1.0-SNAPSHOT.jar /app/app.jar

# 暴露应用运行的端口
EXPOSE 5000

# 运行Spring Boot应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]