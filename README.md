### PassJava — 基于 Spring Cloud 的 Java 面试刷题微服务平台

**项目技术：** Java 17、Spring Boot、Spring Cloud Alibaba、Spring Cloud Gateway、Nacos、OpenFeign、Spring Security、JWT、MyBatis-Plus、MySQL、Redis、RabbitMQ、Docker Compose。

**项目描述：** 独立开发 Java 面试刷题后端系统，采用 Maven 多模块架构拆分用户、题库、学习和网关服务，实现用户认证、题库管理、学习记录、收藏夹、错题本及每日学习统计，支持 Docker Compose 一键部署。

**个人工作及技术实现：**

1. **微服务架构设计：** 基于 Spring Cloud Alibaba Nacos 实现服务注册与发现，使用 OpenFeign 完成学习服务与题库服务之间的远程调用，并通过 Spring Cloud Gateway 实现统一路由和接口访问控制。

2. **认证与权限控制：** 基于 Spring Security + JWT 实现用户注册、登录及身份认证，采用 BCrypt 存储密码哈希，通过角色权限控制题库管理接口，并从 JWT 中解析用户身份以实现学习数据隔离。

3. **Redis 缓存优化：** 使用 Spring Data Redis 实现题目详情的 Cache Aside 缓存模式，结合 TTL 随机化、空值缓存和更新后删除缓存机制，减少重复数据库读取，并处理基础缓存穿透问题。

4. **异步事件与消息可靠性：** 基于 RabbitMQ 实现学习事件异步统计，使用 MySQL Transactional Outbox 将业务记录与待发送事件写入同一事务，结合 Publisher Confirm、失败重试、死信队列与 eventId 幂等机制，提高消息处理的可恢复性。

5. **数据库与容器部署：** 使用 MyBatis-Plus 完成题库和学习业务持久化，通过联合唯一索引与原子 SQL 处理重复收藏和学习计数；利用 Dockerfile 与 Docker Compose 完成四个 Spring Boot 服务及 MySQL、Redis、Nacos、RabbitMQ 的容器化联调。

**项目验证：** 完成 JWT 权限验证、Redis 缓存命中与失效、RabbitMQ 重复消息幂等、Outbox 异常恢复以及微服务跨容器调用等功能测试。
