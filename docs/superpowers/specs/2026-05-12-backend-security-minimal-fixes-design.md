# 后端安全最小修复设计

## 背景

本次修复范围仅覆盖已确认的 6 个问题：

1. 公开相册详情可读取私有相册
2. 公开相册搜索泄露非公开相册
3. 上传接口可向他人相册写入图片
4. Redis 序列化启用不安全多态反序列化
5. 失败登录被记录为成功登录
6. 文件上传信任用户提供的文件名和 MIME

本次目标是最小修复并补充必要回归保护，不扩展到上传重编码、统一访问控制框架重构或大规模缓存改造。

## 目标

- 收紧公开相册读取与搜索边界，保证只有公开相册可被匿名或非 owner 读取。
- 阻止用户向他人相册写入图片。
- 移除 Redis 反序列化中的危险全局多态能力。
- 确保登录成功副作用仅发生在认证成功之后。
- 为图片上传补充最小的真实内容校验。
- 为以上行为补充可重复运行的回归测试。

## 非目标

- 不修改公开接口路径、响应结构和对外业务语义。
- 不引入新的资源访问控制服务。
- 不做图片重编码、压缩或异步杀毒。
- 不新增 JWT 撤销、权限缓存或上传 CDN 策略。

## 方案

### 1. 相册公开访问边界

修改 `AlbumServiceImpl#getAlbum`：

- 先查询相册。
- 计算当前用户是否为 owner。
- 若不是 owner，则要求 `showStatus == PUBLIC`，否则返回现有“相册不存在”语义。
- 读取照片列表时，owner 保持原行为；非 owner 仍只看审核通过照片。

修改 `AlbumServiceImpl#searchAlbum(String keyword)`：

- 强制追加 `showStatus == PUBLIC` 条件。
- 继续保留现有搜索关键字与排序逻辑。

### 2. 相册写入边界

修改 `PhotoServiceImpl#uploadAlbum`：

- 上传前先查询 `albumId` 对应相册。
- 相册不存在时抛 `NotFoundAlbum`。
- 当前用户不是 owner 时抛 `CannotHandleOthersAlbum`。
- 仅在校验通过后再执行上传与异步审核逻辑。

### 3. Redis 序列化安全收口

修改 `RedisConfig`：

- 删除 `activateDefaultTyping`、`enableDefaultTyping` 和 `ObjectMixin`。
- 使用普通 `ObjectMapper` 创建 `GenericJackson2JsonRedisSerializer`。
- 保持现有 `RedisTemplate<Object, Object>` 对外类型不变，避免扩大改动。

兼容性说明：

- 当前代码中用户详情缓存接口虽存在，但未见稳定写入链路依赖，因此本次不做缓存迁移。
- 如旧缓存存在不兼容数据，按缓存失效或清理处理，不引入业务兼容层。

### 4. 登录成功副作用时机修正

修改 `SysUserDetailsService#loadUserByUsername`：

- 仅负责查用户、校验用户状态、返回 `LoginUser`。
- 删除以下成功副作用：
  - 更新 `loginTime`
  - 更新 `loginIp`
  - 更新 `loginAddress`
  - 写成功登录日志
  - 调用 `ipService.setLoginIp`

修改 `SysUserServiceImpl#login` 和 `SysUserServiceImpl#adminLogin`：

- 认证成功后，显式调用统一的登录成功后置逻辑：
  - 更新用户最后登录信息
  - 更新登录 IP 信息
  - 记录成功登录日志
- 失败登录日志逻辑保持原有行为。

### 5. 文件上传最小真实内容校验

修改 `FileUploadUtils`：

- 扩展名校验改为大小写不敏感。
- 新增按 `UploadEnum` 推导允许图片 MIME 的白名单。
- 不再直接信任客户端传入的 `Content-Type`，只在通过校验后写入归一化后的安全 MIME。
- 读取文件头并校验常见图片魔数：
  - JPEG
  - PNG
  - GIF
  - WEBP
- 文件头不匹配时拒绝上传。

### 6. 测试策略

新增或扩展单元测试，覆盖：

- `AlbumServiceImpl`
  - 非 owner 读取私有相册失败
  - owner 读取私有相册成功
  - 公开搜索不返回私有相册
- `PhotoServiceImpl`
  - 非 owner 上传到他人相册失败
- `SysUserServiceImpl` / `SysUserDetailsService`
  - 失败登录不触发成功日志与最后登录更新
  - 成功登录触发成功副作用
- `FileUploadUtils`
  - 伪造扩展名但内容不是图片时拒绝上传

## 错误处理

- 继续复用 `BlogException` 与 `BlogConstants`。
- 对私有相册非法读取保持“相册不存在”语义，避免泄露资源存在性。
- 对越权写入继续使用“不能操作别人的相册”语义。

## 验证

- 运行新增/受影响单元测试。
- 运行 `cd sidifensen_blog_backend && mvn clean compile -DskipTests`
- 如测试环境允许，运行 `cd sidifensen_blog_backend && mvn test`
