# 使用 Amazon Corretto 17 作为基础镜像
FROM amazoncorretto:17-alpine

# 设置工作目录
WORKDIR /app

# 将本地的 JAR 文件复制到容器中的工作目录
COPY jinrouwerewolf-0.0.1-SNAPSHOT.jar /app/jinrouwerewolf-0.0.1-SNAPSHOT.jar

# 暴露 Spring Boot 的默认端口
EXPOSE 8080

# 启动 Spring Boot 应用
CMD ["java", "-jar", "jinrouwerewolf-0.0.1-SNAPSHOT.jar"]
