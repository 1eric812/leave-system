# ============================================================
# 请假审批流系统 - 后端镜像（多阶段构建）
# 阶段 1：用 Maven + JDK17 编译打包
# 阶段 2：只保留 JRE 和 jar，镜像更小
# ============================================================

# ---------- 阶段 1：构建 ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# 先只复制 pom.xml，利用 Docker 层缓存加速重复构建
COPY pom.xml .
RUN mvn -B dependency:go-offline

# 再复制源码并打包（跳过测试，测试依赖本地 MySQL）
COPY src ./src
RUN mvn -B clean package -DskipTests

# ---------- 阶段 2：运行 ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# 时区设为上海，避免写入数据库的时间差 8 小时
RUN apk add --no-cache tzdata \
    && cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime \
    && echo "Asia/Shanghai" > /etc/timezone

# 非 root 用户运行，降低安全风险
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# 云平台通常通过 PORT 环境变量指定端口
ENV SERVER_PORT=8080
EXPOSE 8080

# 让容器能感知到内存上限，避免云平台小内存下 OOM
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
