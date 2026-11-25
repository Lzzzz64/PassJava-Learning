# PassJava — 基于 Spring Cloud 的 Java 面试刷题微服务平台

## 一、项目介绍

PassJava 是一个基于 Spring Cloud Alibaba 的 Java 面试刷题微服务平台，采用 Maven 多模块架构，将业务拆分为用户服务、题库服务、学习服务和网关服务。

项目实现了用户认证、题库管理、学习记录、收藏夹、错题本及每日学习统计等功能，并围绕微服务架构、分布式消息、数据库并发控制、系统监控和自动化部署开展了完整的工程实践。

**项目技术栈：**

Java 17、Spring Boot、Spring Cloud Alibaba、Spring Cloud Gateway、Nacos、OpenFeign、Spring Security、JWT、MyBatis-Plus、MySQL 8.0、Redis 7、RabbitMQ 4、Docker、Docker Compose、Maven、JUnit 5、Testcontainers、Prometheus、Grafana、Jaeger、Micrometer、GitHub Actions、GHCR、k6。

## 二、系统架构

项目采用四个微服务进行业务拆分：

| 服务 | 端口 | 主要职责 |
|---|---:|---|
| Gateway | 8088 | 统一请求入口、路由转发 |
| Member | 8082 | 用户注册、登录、身份认证 |
| Question | 8081 | 题库管理、题目查询 |
| Study | 8083 | 学习记录、收藏、错题、每日统计 |

**基础设施：**

| 组件 | 端口 | 主要用途 |
|---|---:|---|
| Nacos | 8848 | 服务注册与发现 |
| MySQL | 3307 | 业务数据持久化 |
| Redis | 6379 | 热点数据缓存 |
| RabbitMQ | 5672 | 异步消息处理 |
| Prometheus | 9090 | 指标采集 |
| Grafana | 3000 | 监控数据可视化 |
| Jaeger | 16686 | 分布式链路追踪 |

## 三、核心技术实现

### 1. 微服务架构设计

- 基于 Spring Cloud Alibaba Nacos 实现服务注册与发现。
- 使用 OpenFeign 实现微服务之间的远程调用。
- 通过 Spring Cloud Gateway 实现统一路由与请求转发。
- 实现 Feign 请求中的 JWT 身份信息透传。
- 使用 Docker Compose 完成多服务及中间件的容器化部署。

### 2. 安全认证与权限控制

- 基于 Spring Security + JWT 实现用户注册、登录和身份认证。
- 使用 BCrypt 对用户密码进行哈希处理。
- 基于 USER、ADMIN 角色实现接口访问权限控制。
- 从 JWT 中解析用户身份，实现用户学习数据隔离。

### 3. Redis 缓存优化

- 使用 Cache Aside 模式实现题目详情缓存。
- 通过 TTL 随机化降低缓存集中失效风险。
- 使用空值缓存处理基础缓存穿透问题。
- 通过更新后删除缓存实现数据库与缓存的一致性维护。
- 使用并发测试验证缓存行为。

### 4. RabbitMQ 与分布式消息可靠性

- 基于 RabbitMQ 实现学习事件的异步处理。
- 使用 Transactional Outbox 将业务数据与待发送事件写入同一数据库事务。
- 通过条件更新和并发领取机制实现 Outbox 事件调度。
- 使用 Publisher Confirm 处理消息发布确认。
- 实现失败重试、重试时间控制和死信队列。
- 基于 eventId 和消费记录实现消息幂等处理。
- 通过异常注入和集成测试验证消息恢复能力。

### 5. MySQL 数据库与并发优化

- 使用联合索引、覆盖索引优化业务查询。
- 通过唯一索引和原子 SQL 避免重复收藏及并发更新问题。
- 深入实践 ACID、事务隔离级别、MVCC 和 InnoDB 行锁机制。
- 通过 SELECT FOR UPDATE、SKIP LOCKED 实现并发任务领取。
- 使用 EXPLAIN ANALYZE 分析 SQL 执行计划。
- 通过并发测试复现锁等待、死锁，并实现事务级重试。

### 6. 自动化测试与质量保障

- 使用 JUnit 5、Spring Boot Test 编写单元测试和集成测试。
- 使用 Testcontainers 启动 MySQL、Redis、RabbitMQ 测试容器。
- 验证 Outbox 并发领取、消息幂等、缓存一致性及数据库事务。
- 通过多线程并发测试验证业务正确性。

### 7. 系统监控与分布式链路追踪

- 使用 Prometheus 采集微服务及中间件运行指标。
- 使用 Grafana 构建系统监控 Dashboard。
- 通过 Exporter 采集 Redis、MySQL、RabbitMQ 运行指标。
- 使用 Micrometer Tracing 实现分布式链路追踪。
- 基于 Jaeger 分析微服务调用链路。
- 增加 Outbox 自定义监控指标，观察待处理事件积压情况。

### 8. CI/CD 与容器化部署

- 使用 Dockerfile 构建四个微服务镜像。
- 使用 Docker Compose 管理微服务及基础设施。
- 基于 GitHub Actions 实现自动化构建、测试和持续集成。
- 使用 Docker Buildx 实现镜像构建与缓存优化。
- 将构建后的镜像推送至 GitHub Container Registry（GHCR）。
- 支持通过 Docker Compose 拉取镜像并部署服务。

### 9. 性能测试与优化

- 使用 k6 对 Gateway、Question、Study 等接口进行压力测试。
- 测试 Redis 缓存读取及学习记录写入等典型业务场景。
- 统计 QPS、P95、P99 响应时间和错误率。
- 结合 Grafana、Redis、MySQL、RabbitMQ 和 Jaeger 定位性能瓶颈。
- 通过优化前后对照实验验证性能改进效果。

## 四、项目测试与验证

项目围绕核心业务及分布式场景开展了多轮测试：

- JWT 身份认证及角色权限验证。
- Redis 缓存命中、失效与并发访问测试。
- RabbitMQ 重复消息消费及幂等验证。
- Outbox 异常恢复、失败重试及并发领取测试。
- MySQL 事务、索引、锁等待与死锁实验。
- Testcontainers 多中间件集成测试。
- 微服务跨容器调用及链路追踪验证。
- GitHub Actions 自动化构建与测试。
- k6 压力测试及性能优化对照实验。

## 五、项目部署

项目使用 Docker Compose 管理微服务和基础设施，支持本地容器化联调及镜像发布部署。

本地构建启动：

```bash
docker compose \
  -f docker/compose.yml \
  up -d --build
```

基于 GHCR 发布镜像启动：

```bash
docker compose \
  --env-file docker/.env \
  -f docker/compose.yml \
  -f docker/compose.release.yml \
  up -d --no-build
```

具体环境变量配置及部署说明以项目中的 Docker 配置文件为准。

## 六、项目实践总结

通过 PassJava 项目的完整开发，系统实践了 Java 微服务开发与分布式系统中的核心技术，包括：

- 微服务架构设计与服务间通信。
- JWT 身份认证与权限控制。
- Redis 缓存设计与一致性维护。
- RabbitMQ 消息可靠性与最终一致性。
- MySQL 索引优化、事务及并发控制。
- 自动化测试与容器化集成测试。
- Prometheus、Grafana 与 Jaeger 可观测性建设。
- GitHub Actions、GHCR 与 Docker CI/CD。
- 基于 k6 的性能测试与系统调优。

**项目目标：** 从基础业务开发出发，完整实践微服务系统从设计、开发、测试、部署、监控到性能优化的工程化流程。
