---
type: module_card
title: backend-security-access-control
summary: 后端请求认证、公开接口放行、相册与文件上传安全边界的当前骨架说明。
tags:
  - backend
  - security
  - auth
  - upload
owned_paths:
  - sidifensen_blog_backend/src/main/java/com/sidifensen/security/
  - sidifensen_blog_backend/src/main/java/com/sidifensen/domain/constants/SecurityConstants.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/AlbumServiceImpl.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/PhotoServiceImpl.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/utils/FileUploadUtils.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisConfig.java
related_docs:
  - docs/superpowers/memory/backend/public-album-access-contract.md
  - docs/superpowers/memory/index.md
entrypoints:
  - sidifensen_blog_backend/src/main/java/com/sidifensen/security/SecurityConfiguration.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/security/JwtAuthenticationFilter.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/controller/AlbumController.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/controller/PhotoController.java
last_verified_commit: 6d44350
status: active
---

# Responsibilities

- 维护公开接口与必须登录接口的边界。
- 在请求进入业务层前完成 JWT 认证与用户上下文装配。
- 约束公开相册访问、相册写入以及文件上传基础校验。
- 为 Redis 缓存对象提供统一序列化策略。

# Entry Points

- `SecurityConfiguration`: Spring Security 放行与过滤器装配入口。
- `JwtAuthenticationFilter`: JWT 解析、用户装配、访客记录发送。
- `SecurityConstants.Optional_Auth_Urls`: 公开或可选认证接口白名单。
- `AlbumController` / `AlbumServiceImpl`: 公开相册读取与搜索入口。
- `PhotoController` / `PhotoServiceImpl`: 图片上传与相册写入入口。
- `FileUploadUtils`: MinIO 上传与基础文件类型判断入口。

# Invariants

- 非白名单接口必须在 `JwtAuthenticationFilter` 成功认证后才可继续。
- 公开接口即使允许匿名访问，也必须在业务层自行补齐资源可见性校验。
- 任何基于用户传入 `albumId`、`userId`、`path` 的写操作都不能直接信任调用方输入。
- 文件上传至少需要同时校验大小、扩展名和真实内容类型，不应信任前端 MIME。
- Redis 序列化不能依赖全局不受限的多态反序列化。

# Extension Points

- 可在 `JwtAuthenticationFilter` 后增加认证成功后的审计或 token 撤销检查。
- 可在 `FileUploadUtils` 中继续增强文件魔数校验或增加统一图片重编码。
- 可为相册访问抽取独立访问控制服务，统一 owner / public / admin 逻辑。

# Common Pitfalls

- 只在 controller 路由层配置公开访问，而遗漏 service 层资源可见性判断。
- 在 `UserDetailsService` 中提前写入登录成功副作用，导致失败登录污染审计数据。
- 通过 Redis `DefaultTyping` 省事序列化复杂对象，带来反序列化攻击面。
- 只按文件名后缀判断图片类型，导致公开存储桶成为任意文件落点。
