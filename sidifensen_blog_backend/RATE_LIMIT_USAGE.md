# 限流功能使用指南

## 功能概述

本限流功能基于 Redis 实现分布式限流，支持自动黑名单机制，可有效防止接口被恶意刷新。

## 核心组件

### 1. `@RateLimit` 注解

限流注解，用于方法级别的访问频率限制。

**参数说明：**

- `count`: 限流次数，默认 10 次
- `period`: 限流周期（秒），默认 60 秒
- `message`: 触发限流时的提示信息，默认 "操作繁忙，请稍候再试"

### 2. `BlacklistStrategy` 黑名单策略枚举

根据限流触发次数自动实施不同等级的封禁：

| 策略等级 | 触发条件               | 封禁时长 | 说明     |
| -------- | ---------------------- | -------- | -------- |
| LOW      | 5 分钟内触发 3 次限流  | 1 小时   | 轻度违规 |
| MEDIUM   | 5 分钟内触发 5 次限流  | 6 小时   | 中度违规 |
| HIGH     | 5 分钟内触发 10 次限流 | 7 天     | 重度违规 |

### 3. `BlacklistInterceptor` 黑名单拦截器

在请求进入 Controller 前检查用户是否在黑名单中，比切面更早拦截，提高安全性和性能。

**工作流程：**

1. 获取用户标识（登录用户使用用户 ID，未登录使用 IP 地址）
2. 检查是否在黑名单中
3. 如果在黑名单中，直接拒绝请求并记录日志
4. 否则放行请求

### 4. `RateLimitAspect` 限流切面

自动拦截带有 `@RateLimit` 注解的方法，实现限流逻辑。

**工作流程：**

1. 获取用户标识（登录用户使用用户 ID，未登录使用 IP 地址）
2. 执行限流检查（基于 Redis incr 实现）
3. 如果触发限流，记录触发次数
4. 根据触发次数自动加入黑名单
5. 抛出异常或继续执行

### 5. `WebMvcConfiguration` 拦截器配置

注册黑名单拦截器，配置拦截和排除路径。

## 使用示例

### 基本用法

```java
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    /**
     * 获取文章列表
     * 限流：60秒内最多访问10次
     */
    @GetMapping("/list")
    @RateLimit(count = 10, period = 60)
    public Result getArticleList(@NotNull Integer pageNum, @NotNull Integer pageSize) {
        PageVo<List<ArticleVo>> result = articleService.getArticleList(pageNum, pageSize);
        return Result.success(result);
    }
}
```

### 自定义限流参数

```java
/**
 * 发送验证码
 * 限流：60秒内最多发送1次
 */
@PostMapping("/sendCode")
@RateLimit(count = 1, period = 60, message = "验证码发送过于频繁，请稍后再试")
public Result sendVerificationCode(@RequestParam String email) {
    // 业务逻辑
    return Result.success();
}
```

### 严格限流

```java
/**
 * 重置密码
 * 限流：300秒（5分钟）内最多尝试3次
 */
@PostMapping("/resetPassword")
@RateLimit(count = 3, period = 300, message = "密码重置次数过多，请稍后再试")
public Result resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
    // 业务逻辑
    return Result.success();
}
```

### 宽松限流

```java
/**
 * 搜索文章
 * 限流：10秒内最多搜索5次
 */
@GetMapping("/search")
@RateLimit(count = 5, period = 10)
public Result searchArticles(@RequestParam String keyword) {
    // 业务逻辑
    return Result.success();
}
```

## Redis 键说明

### 限流记录键

- **格式**: `sidifensen_blog:RateLimit:{方法名}:{用户标识}`
- **过期时间**: 根据注解的 `period` 参数动态设置
- **存储内容**: 访问次数

### 限流触发次数键

- **格式**: `sidifensen_blog:RateLimitTrigger:{用户标识}`
- **过期时间**: 5 分钟
- **存储内容**: 触发限流的次数

### 黑名单键

- **格式**: `sidifensen_blog:Blacklist:{用户标识}`
- **过期时间**: 根据违规等级设置（1 小时/6 小时/7 天）
- **存储内容**: 违规等级描述

## 用户标识规则

- **已登录用户**: `user:{用户ID}`
- **未登录用户**: `ip:{IP地址}`

这样可以确保：

- 登录用户即使更换 IP 也能正确限流
- 未登录用户通过 IP 进行限流
- 防止恶意注册大量账号绕过限流

## 异常处理

触发限流时会抛出 `BlogException`，全局异常处理器会自动捕获并返回友好提示。

**限流异常提示**: 使用注解中定义的 `message` 参数
**黑名单异常提示**: "您的访问已被限制，请稍后再试"

## 日志记录

限流切面会自动记录以下日志：

```
WARN - 限流触发 - 用户标识: user:123, 方法: getArticleList, 限制: 10/60秒
WARN - 黑名单用户尝试访问 - 用户标识: ip:192.168.1.1, 剩余封禁时间: 58分钟
WARN - 用户加入黑名单 - 用户标识: user:456, 触发次数: 5, 策略: 中度违规, 封禁时长: 3600秒
```

## 架构设计

本限流功能采用**双层防护**机制：

1. **第一层 - 拦截器（BlacklistInterceptor）**：

   - 在请求进入 Controller 之前就检查黑名单
   - 拦截被封禁用户的所有请求，无论是否有 `@RateLimit` 注解
   - 执行顺序最早，性能开销最小

2. **第二层 - 切面（RateLimitAspect）**：
   - 只对有 `@RateLimit` 注解的方法进行限流检查
   - 根据不同方法设置不同的限流策略
   - 触发限流后自动加入黑名单

**执行流程：**

```
请求 -> 拦截器（黑名单检查） -> Controller -> 切面（限流检查） -> 业务逻辑
       ↓ 在黑名单              ↓ 触发限流
       抛出异常                记录触发次数 -> 达到阈值 -> 加入黑名单
```

## 注意事项

1. **Redis 依赖**: 确保 Redis 服务正常运行
2. **异常处理**: Redis 异常不会影响业务，默认允许访问
3. **合理设置**: 根据接口特性合理设置 `count` 和 `period`
4. **日志监控**: 定期检查日志，分析限流和黑名单情况
5. **测试建议**: 在测试环境充分测试后再部署到生产环境
6. **拦截器配置**: 可根据需要在 `WebMvcConfiguration` 中调整拦截和排除路径

## 推荐配置

| 场景          | count | period | 说明         |
| ------------- | ----- | ------ | ------------ |
| 查询列表      | 10    | 60     | 允许频繁查询 |
| 发送验证码    | 1     | 60     | 严格限制     |
| 登录接口      | 5     | 300    | 防止暴力破解 |
| 创建/更新操作 | 5     | 60     | 适度限制     |
| 搜索功能      | 10    | 10     | 防止恶意搜索 |

## 常见问题

**Q: 如何手动解除黑名单？**
A: 可以通过 Redis 客户端删除对应的黑名单键：

```
DEL sidifensen_blog:Blacklist:user:123
```

**Q: 限流是否会影响性能？**
A: 影响极小，基于 Redis 的 incr 操作，性能开销可忽略不计。

**Q: 是否支持集群环境？**
A: 支持，基于 Redis 实现的分布式限流，天然支持集群部署。

**Q: 如何调整黑名单策略？**
A: 修改 `BlacklistStrategy` 枚举中的参数即可。
