---
type: contract
title: public-album-access-contract
summary: 公开相册读取、公开相册搜索与相册写入接口必须遵守的可见性与所有权规则。
tags:
  - backend
  - album
  - access-control
owned_paths:
  - sidifensen_blog_backend/src/main/java/com/sidifensen/controller/AlbumController.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/AlbumServiceImpl.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/PhotoServiceImpl.java
related_docs:
  - docs/superpowers/memory/backend/security-access-module-card.md
entrypoints:
  - sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/AlbumServiceImpl.java
  - sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/PhotoServiceImpl.java
last_verified_commit: 6d44350
status: active
---

# Scope

- 公开相册详情接口 `/album/get/{albumId}`
- 公开相册搜索接口 `/album/search`
- 登录用户上传相册图片接口 `/photo/uploadAlbum`

# Producers And Consumers

- Producer:
  - `AlbumServiceImpl`
  - `PhotoServiceImpl`
- Consumers:
  - 用户端公开相册页
  - 已登录用户的相册管理页
  - 管理端相册管理能力

# States And Interface Rules

- 公开读取:
  - 匿名用户只能读取 `showStatus = PUBLIC` 的相册。
  - 登录用户如果不是相册 owner，也只能读取 `showStatus = PUBLIC` 的相册。
  - owner 允许读取自己的非公开相册。
- 公开搜索:
  - 匿名搜索结果只能包含 `showStatus = PUBLIC` 相册。
  - 公开搜索不应返回私有相册名称、封面或作者信息。
- 相册写入:
  - `/photo/uploadAlbum` 只能向当前登录用户自己的相册写入图片。
  - `albumId` 需要先校验存在，再校验 `album.userId == currentUserId`。

# Invariants

- 公开接口是否在 `Optional_Auth_Urls` 中，不等于资源本身可公开访问。
- 相册 owner 判定必须以数据库中的 `album.userId` 为准，不能信任前端传参。
- 管理端绕过公开访问限制只能通过带权限校验的 `/admin` 路由，不应复用公开接口完成越权读取。

# Compatibility Notes

- 最小修复应保持现有接口路径与响应结构不变。
- 公开读取违规时优先返回现有“找不到相册”一类业务语义，避免暴露资源是否存在。
