# Job Scheduler Backend

分布式定时任务调度平台后端服务

## 技术栈

- Java 21
- Spring Boot 3.2.x
- MyBatis-Plus 3.5.x
- MySQL 8.0
- XXL-JOB 2.4.x

## 快速启动

### 环境要求

- JDK 21+
- Maven 3.8+

### 构建运行

```bash
mvn clean install
mvn spring-boot:run
```

### 配置

修改 `src/main/resources/application.yml` 中的数据库连接配置。

## API文档

详见 `../docs/05-API接口设计.md`
