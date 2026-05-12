# Acceptance Criteria: 后端安全最小修复

**Spec:** `docs/superpowers/specs/2026-05-12-backend-security-minimal-fixes-design.md`
**Date:** 2026-05-12
**Status:** Approved

---

## Criteria

| ID | Description | Test Type | Preconditions | Expected Result |
|----|-------------|-----------|---------------|-----------------|
| AC-001 | 非 owner 访问私有相册详情时，服务端拒绝返回相册数据 | Logic | 构造 `showStatus` 非公开且 `album.userId != currentUserId` 的相册 | 调用 `AlbumServiceImpl#getAlbum` 抛出 `BlogException`，消息为 `相册不存在` |
| AC-002 | owner 访问自己的非公开相册时，服务端允许返回相册详情 | Logic | 构造 `showStatus` 非公开且 `album.userId == currentUserId` 的相册 | 调用 `AlbumServiceImpl#getAlbum` 返回 `AlbumVo`，且 `id` 与相册一致 |
| AC-003 | 公开相册搜索只返回公开相册 | Logic | 构造公开搜索关键字，并拦截 `AlbumServiceImpl#list` 查询条件 | 查询条件中包含 `Album::getShowStatus == PUBLIC` |
| AC-004 | 用户不能向他人相册上传图片 | Logic | 构造 `album.userId != currentUserId`，并提供可用 `MultipartFile` | 调用 `PhotoServiceImpl#uploadAlbum` 抛出 `BlogException`，消息为 `不能操作别人的相册`，且不调用上传逻辑 |
| AC-005 | Redis 配置不再启用全局不安全多态反序列化 | Logic | 加载 `RedisConfig#redisTemplate` 所需配置对象 | `RedisConfig` 中不再调用 `activateDefaultTyping` 或 `enableDefaultTyping` |
| AC-006 | 用户名密码认证失败时，不写成功登录副作用 | Logic | 模拟认证失败并提供存在用户的登录输入 | 调用登录方法后仅记录失败日志，不调用最后登录信息更新逻辑 |
| AC-007 | 用户名密码认证成功时，在生成 token 前写入成功登录副作用 | Logic | 模拟认证成功并提供可用用户实体 | 调用登录方法时会更新最后登录信息并记录一次成功日志 |
| AC-008 | 伪装成图片扩展名但文件头不是图片的数据不能上传 | Logic | 构造扩展名为 `.png` 但内容不是 PNG/JPEG/GIF/WEBP 的 `MultipartFile` | 调用 `FileUploadUtils#upload` 抛出 `FileUploadException`，消息为 `上传文件类型错误` |
| AC-009 | 合法图片文件通过最小内容校验后，上传时写入归一化后的安全 MIME | Logic | 构造文件头合法的图片 `MultipartFile` 并模拟 MinIO 可用 | `putObject` 被调用时传入的 `Content-Type` 为服务端归一化后的图片 MIME，而非任意客户端值 |
