# 使用官方的Eclipse Temurin OpenJDK 21 Alpine镜像
FROM eclipse-temurin:21-alpine

# 设置时区为中国标准时间(UTC+8)
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && \
  ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
  echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 复制已经打包好的jar文件
COPY target/sidifensen_blog_backend-1.0-SNAPSHOT.jar /app/app.jar

# 暴露应用运行的端口
EXPOSE 5000

# 运行Spring Boot应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]