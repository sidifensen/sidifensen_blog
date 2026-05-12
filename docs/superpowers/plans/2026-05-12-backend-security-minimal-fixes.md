# 后端安全最小修复 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:executing-plans` to implement this plan task-by-task. It will decide whether each batch should run in parallel or serial subagent mode and will pass only task-local context to each subagent. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复后端 6 个已确认的安全问题，并补充最小回归测试，且不扩展到额外架构改造。

**Architecture:** 通过收紧相册访问边界、补充相册写入所有权校验、移除 Redis 不安全序列化配置、调整登录成功副作用时机，以及为上传增加最小图片内容校验来完成修复。测试优先落在 service 和 utility 层，避免扩大到重型集成测试。

**Tech Stack:** Spring Boot 3、MyBatis-Plus、Spring Security、RedisTemplate、MinIO、JUnit 5、Mockito

---

### Task 1: 为相册访问边界写失败测试

**Files:**
- Create: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/AlbumServiceImplTest.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/AlbumServiceImplTest.java`

- [ ] **Step 1: 写失败测试，覆盖私有相册读取和公开搜索边界**

测试应覆盖：
- 非 owner 访问私有相册抛 `相册不存在`
- owner 访问私有相册成功
- 公开搜索查询条件包含 `showStatus = PUBLIC`

- [ ] **Step 2: 运行测试确认失败**

Run: `cd sidifensen_blog_backend && mvn -Dtest=AlbumServiceImplTest test`
Expected: FAIL，当前实现无法阻止非 owner 读取私有相册，且公开搜索未限制公开状态

- [ ] **Step 3: 提交失败测试**

```bash
git add sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/AlbumServiceImplTest.java
git commit -m "test: add album access boundary regression tests"
```

### Task 2: 实现相册访问边界并验证

**Files:**
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/AlbumServiceImpl.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/AlbumServiceImplTest.java`

- [ ] **Step 1: 实现相册详情访问控制与公开搜索过滤**

修改点：
- `getAlbum` 增加 owner/public 判断
- `searchAlbum(String keyword)` 强制 `showStatus = PUBLIC`

- [ ] **Step 2: 运行测试确认通过**

Run: `cd sidifensen_blog_backend && mvn -Dtest=AlbumServiceImplTest test`
Expected: PASS

- [ ] **Step 3: 提交实现**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/AlbumServiceImpl.java sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/AlbumServiceImplTest.java
git commit -m "fix: enforce album public access boundaries"
```

### Task 3: 为相册写入所有权校验写失败测试

**Files:**
- Create: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/PhotoServiceImplTest.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/PhotoServiceImplTest.java`

- [ ] **Step 1: 写失败测试，覆盖上传到他人相册被拒绝**

测试应断言：
- 非 owner 上传到他人相册抛 `不能操作别人的相册`
- 上传逻辑 `fileUploadUtils.upload` 不被调用

- [ ] **Step 2: 运行测试确认失败**

Run: `cd sidifensen_blog_backend && mvn -Dtest=PhotoServiceImplTest test`
Expected: FAIL，当前实现会直接调用上传

- [ ] **Step 3: 提交失败测试**

```bash
git add sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/PhotoServiceImplTest.java
git commit -m "test: add album upload ownership regression test"
```

### Task 4: 实现相册写入所有权校验并验证

**Files:**
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/PhotoServiceImpl.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/PhotoServiceImplTest.java`

- [ ] **Step 1: 在上传相册图片前校验相册存在与 owner 身份**

- [ ] **Step 2: 运行测试确认通过**

Run: `cd sidifensen_blog_backend && mvn -Dtest=PhotoServiceImplTest test`
Expected: PASS

- [ ] **Step 3: 提交实现**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/PhotoServiceImpl.java sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/PhotoServiceImplTest.java
git commit -m "fix: validate album ownership before upload"
```

### Task 5: 为登录成功副作用时机写失败测试

**Files:**
- Create: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/SysUserServiceImplTest.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/SysUserServiceImplTest.java`

- [ ] **Step 1: 写失败测试，覆盖登录成功与失败的副作用边界**

测试应覆盖：
- 认证失败时只记录失败日志，不写成功副作用
- 认证成功时写成功日志并更新最后登录信息

- [ ] **Step 2: 运行测试确认失败**

Run: `cd sidifensen_blog_backend && mvn -Dtest=SysUserServiceImplTest test`
Expected: FAIL，当前成功副作用发生在 `loadUserByUsername`

- [ ] **Step 3: 提交失败测试**

```bash
git add sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/SysUserServiceImplTest.java
git commit -m "test: add login side effect regression tests"
```

### Task 6: 实现登录成功副作用时机修正并验证

**Files:**
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/security/SysUserDetailsService.java`
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/SysUserServiceImpl.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/SysUserServiceImplTest.java`

- [ ] **Step 1: 从 `loadUserByUsername` 移除成功副作用，并在 `login` / `adminLogin` 认证成功后补回**

- [ ] **Step 2: 运行测试确认通过**

Run: `cd sidifensen_blog_backend && mvn -Dtest=SysUserServiceImplTest test`
Expected: PASS

- [ ] **Step 3: 提交实现**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/security/SysUserDetailsService.java sidifensen_blog_backend/src/main/java/com/sidifensen/service/impl/SysUserServiceImpl.java sidifensen_blog_backend/src/test/java/com/sidifensen/service/impl/SysUserServiceImplTest.java
git commit -m "fix: move login success side effects after authentication"
```

### Task 7: 为上传文件真实内容校验和 Redis 配置写失败测试

**Files:**
- Create: `sidifensen_blog_backend/src/test/java/com/sidifensen/utils/FileUploadUtilsTest.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/utils/FileUploadUtilsTest.java`
- Test: `sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisConfig.java`

- [ ] **Step 1: 写失败测试，覆盖伪装图片拦截和安全 MIME 归一化**

测试应覆盖：
- 扩展名为图片但内容不是图片时抛 `上传文件类型错误`
- 合法图片通过时写入归一化 MIME

- [ ] **Step 2: 记录 Redis 配置的结构性回归检查**

使用代码检查确认 `RedisConfig` 中不再调用 `activateDefaultTyping` / `enableDefaultTyping`

- [ ] **Step 3: 运行测试确认失败**

Run: `cd sidifensen_blog_backend && mvn -Dtest=FileUploadUtilsTest test`
Expected: FAIL，当前实现只看扩展名和客户端 MIME

- [ ] **Step 4: 提交失败测试**

```bash
git add sidifensen_blog_backend/src/test/java/com/sidifensen/utils/FileUploadUtilsTest.java
git commit -m "test: add upload content validation regression tests"
```

### Task 8: 实现上传最小真实内容校验与 Redis 配置修复并验证

**Files:**
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/utils/FileUploadUtils.java`
- Modify: `sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisConfig.java`
- Test: `sidifensen_blog_backend/src/test/java/com/sidifensen/utils/FileUploadUtilsTest.java`

- [ ] **Step 1: 实现大小写不敏感扩展名校验、魔数校验、安全 MIME 白名单，并移除 Redis 不安全多态配置**

- [ ] **Step 2: 运行测试确认通过**

Run: `cd sidifensen_blog_backend && mvn -Dtest=FileUploadUtilsTest test`
Expected: PASS

- [ ] **Step 3: 通过代码搜索验证 Redis 配置已移除危险调用**

Run: `rg -n "activateDefaultTyping|enableDefaultTyping" sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisConfig.java`
Expected: no matches

- [ ] **Step 4: 提交实现**

```bash
git add sidifensen_blog_backend/src/main/java/com/sidifensen/utils/FileUploadUtils.java sidifensen_blog_backend/src/main/java/com/sidifensen/redis/RedisConfig.java sidifensen_blog_backend/src/test/java/com/sidifensen/utils/FileUploadUtilsTest.java
git commit -m "fix: harden upload validation and redis serialization"
```

### Task 9: 整体验证

**Files:**
- Modify: `docs/superpowers/specs/2026-05-12-backend-security-minimal-fixes-design.md`
- Modify: `docs/superpowers/acceptance/2026-05-12-backend-security-minimal-fixes.md`
- Modify: `docs/superpowers/plans/2026-05-12-backend-security-minimal-fixes.md`

- [ ] **Step 1: 运行本次新增与受影响测试**

Run: `cd sidifensen_blog_backend && mvn -Dtest=AlbumServiceImplTest,PhotoServiceImplTest,SysUserServiceImplTest,FileUploadUtilsTest test`
Expected: PASS

- [ ] **Step 2: 运行编译验证**

Run: `cd sidifensen_blog_backend && mvn clean compile -DskipTests`
Expected: BUILD SUCCESS

- [ ] **Step 3: 如环境允许，运行完整测试集**

Run: `cd sidifensen_blog_backend && mvn test`
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交文档与收尾变更**

```bash
git add docs/superpowers/specs/2026-05-12-backend-security-minimal-fixes-design.md docs/superpowers/acceptance/2026-05-12-backend-security-minimal-fixes.md docs/superpowers/plans/2026-05-12-backend-security-minimal-fixes.md
git commit -m "docs: record backend security minimal fix plan"
```
