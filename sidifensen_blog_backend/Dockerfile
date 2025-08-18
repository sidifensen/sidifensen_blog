# 使用官方的OpenJDK 21镜像作为基础镜像
FROM openjdk:21

# 设置工作目录
WORKDIR /app

# 复制项目的jar文件到容器中
COPY ./sidifensen_blog_backend-1.0-SNAPSHOT.jar /app/app.jar

# 暴露应用运行的端口
EXPOSE 5000

# 运行Spring Boot应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]