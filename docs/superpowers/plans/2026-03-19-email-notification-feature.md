# 邮件通知功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现评论邮件通知和系统邮件通知功能，包括单元测试验证

**Architecture:**
- 评论邮件通知：当用户评论被回复时，检查被回复者是否开启邮件通知，通过 RabbitMQ 队列异步发送邮件
- 系统邮件通知：当文章/专栏审核通过或不通过时，发送系统通知邮件，用户可通过设置控制开关
- 复用现有邮件队列和 Thymeleaf 模板引擎，通过不同路由键区分邮件类型

**Tech Stack:** Spring Boot, MyBatis-Plus, RabbitMQ, JavaMailSender, Thymeleaf, JUnit 5, Mockito

---

## 文件清单

### 新增文件
- `src/main/resources/templates/comment-notification.html` - 评论通知邮件模板
- `src/main/resources/templates/system-notification.html` - 系统通知邮件模板
- `src/test/java/com/sidifensen/service/impl/CommentServiceImplTest.java` - 评论服务单元测试
- `src/test/java/com/sidifensen/service/impl/UserSettingsServiceImplTest.java` - 用户设置服务单元测试
- `src/test/java/com/sidifensen/rabbitmq/RabbitMqListenerTest.java` - MQ 监听器单元测试

### 修改文件
- `src/main/java/com/sidifensen/domain/enums/MailEnum.java` - 新增邮件类型枚举
- `src/main/java/com/sidifensen/domain/constants/RabbitMQConstants.java` - 新增路由键常量
- `src/main/java/com/sidifensen/rabbitmq/RabbitMQConfig.java` - 新增队列绑定
- `src/main/java/com/sidifensen/rabbitmq/RabbitMqListener.java` - 新增邮件处理方法
- `src/main/java/com/sidifensen/service/UserSettingsService.java` - 新增接口方法
- `src/main/java/com/sidifensen/service/impl/UserSettingsServiceImpl.java` - 实现新增方法
- `src/main/java/com/sidifensen/service/impl/CommentServiceImpl.java` - 新增邮件发送方法
- `src/main/java/com/sidifensen/service/impl/ArticleServiceImpl.java` - 新增审核邮件通知
- `src/main/java/com/sidifensen/service/impl/ColumnServiceImpl.java` - 新增审核邮件通知

---

## 任务分解

### Task 1: 邮件枚举和常量配置

**Files:**
- Modify: `src/main/java/com/sidifensen/domain/enums/MailEnum.java`
- Modify: `src/main/java/com/sidifensen/domain/constants/RabbitMQConstants.java`

- [ ] **Step 1: 添加邮件类型枚举**

在 `MailEnum.java` 中添加：
```java
COMMENT_NOTIFICATION("commentNotification", "sidifensen_blog 评论通知", "comment-notification", "评论通知"),
SYSTEM_NOTIFICATION("systemNotification", "sidifensen_blog 系统通知", "system-notification", "系统通知");
```

- [ ] **Step 2: 添加 RabbitMQ 路由键常量**

在 `RabbitMQConstants.java` 中添加：
```java
// 评论邮件通知路由键
public static final String Comment_Email_Routing_Key = "comment_email_routing_key";

// 系统邮件通知路由键
public static final String System_Email_Routing_Key = "system_email_routing_key";
```

- [ ] **Step 3: 编译验证**

```bash
cd sidifensen_blog_backend && mvn clean compile -DskipTests
```
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add .
git commit -m "feat: 添加邮件通知枚举和常量"
```

---

### Task 2: RabbitMQ 队列配置

**Files:**
- Modify: `src/main/java/com/sidifensen/rabbitmq/RabbitMQConfig.java`

- [ ] **Step 1: 添加评论邮件队列绑定**

```java
@Bean
public Binding bindingCommentEmailQueueToExchange() {
    return BindingBuilder
            .bind(emailQueue())
            .to(emailExchange())
            .with(RabbitMQConstants.Comment_Email_Routing_Key);
}
```

- [ ] **Step 2: 添加系统邮件队列绑定**

```java
@Bean
public Binding bindingSystemEmailQueueToExchange() {
    return BindingBuilder
            .bind(emailQueue())
            .to(emailExchange())
            .with(RabbitMQConstants.System_Email_Routing_Key);
}
```

- [ ] **Step 3: 编译验证**

```bash
mvn clean compile -DskipTests
```
Expected: BUILD SUCCESS

- [ ] **Step 4: 提交**

```bash
git add .
git commit -m "feat: 配置评论和系统邮件队列绑定"
```

---

### Task 3: 用户设置服务接口

**Files:**
- Modify: `src/main/java/com/sidifensen/service/UserSettingsService.java`
- Modify: `src/main/java/com/sidifensen/service/impl/UserSettingsServiceImpl.java`
- Create: `src/test/java/com/sidifensen/service/impl/UserSettingsServiceImplTest.java`

- [ ] **Step 1: 编写测试（TDD）**

创建 `UserSettingsServiceImplTest.java`：
```java
@Test
@DisplayName("获取评论邮件设置 - 无记录时返回默认值 1")
void testGetReceiveCommentEmail_NoRecord() {
    Integer result = userSettingsService.getReceiveCommentEmail(999);
    assertEquals(1, result, "默认应该开启评论邮件通知");
}

@Test
@DisplayName("获取系统邮件设置 - 无记录时返回默认值 1")
void testGetReceiveSystemEmail_NoRecord() {
    Integer result = userSettingsService.getReceiveSystemEmail(999);
    assertEquals(1, result, "默认应该开启系统邮件通知");
}
```

- [ ] **Step 2: 运行测试验证失败**

```bash
mvn test -Dtest=UserSettingsServiceImplTest
```
Expected: COMPILATION ERROR (方法不存在)

- [ ] **Step 3: 添加接口方法**

在 `UserSettingsService.java` 中添加：
```java
Integer getReceiveCommentEmail(Integer userId);
Integer getReceiveSystemEmail(Integer userId);
void setReceiveSystemEmail(Integer userId, Integer isReceive);
```

- [ ] **Step 4: 实现方法**

在 `UserSettingsServiceImpl.java` 中实现（参考已有的 `getReceivePrivateMessageEmail` 模式）

- [ ] **Step 5: 运行测试验证通过**

```bash
mvn test -Dtest=UserSettingsServiceImplTest
```
Expected: BUILD SUCCESS

- [ ] **Step 6: 提交**

```bash
git add .
git commit -m "feat: 添加用户邮件设置接口和实现"
```

---

### Task 4: 邮件模板创建

**Files:**
- Create: `src/main/resources/templates/comment-notification.html`
- Create: `src/main/resources/templates/system-notification.html`

- [ ] **Step 1: 创建评论通知模板**

模板变量：`recipientNickname`, `commenterNickname`, `articleTitle`, `articleId`, `commentContent`, `isReply`, `parentCommentContent`

- [ ] **Step 2: 创建系统通知模板**

模板变量：`recipientNickname`, `notificationTitle`, `notificationContent`, `extraInfo`, `sendTime`, `linkUrl`, `linkText`

- [ ] **Step 3: 编译验证模板路径**

```bash
mvn clean compile -DskipTests
```
Expected: BUILD SUCCESS (资源文件被复制)

- [ ] **Step 4: 提交**

```bash
git add .
git commit -m "feat: 添加邮件通知模板"
```

---

### Task 5: RabbitMQ 监听器实现

**Files:**
- Modify: `src/main/java/com/sidifensen/rabbitmq/RabbitMqListener.java`
- Create: `src/test/java/com/sidifensen/rabbitmq/RabbitMqListenerTest.java`

- [ ] **Step 1: 编写测试（TDD）**

```java
@Test
@DisplayName("处理评论邮件 - 成功发送")
void testHandleCommentNotificationEmail() {
    // 验证 handleCommentNotificationEmail 方法调用 EmailUtils
}
```

- [ ] **Step 2: 添加评论邮件处理方法**

在 `receiveEmail()` 方法中添加：
```java
} else if (type.equals(MailEnum.COMMENT_NOTIFICATION.getType())) {
    handleCommentNotificationEmail(message);
} else if (type.equals(MailEnum.SYSTEM_NOTIFICATION.getType())) {
    handleSystemNotificationEmail(message);
```

- [ ] **Step 3: 实现处理方法**

```java
private void handleCommentNotificationEmail(Map<String, Object> message) {
    // 解析邮件参数并调用 emailUtils.sendHtmlMail
}

private void handleSystemNotificationEmail(Map<String, Object> message) {
    // 解析邮件参数并调用 emailUtils.sendHtmlMail
}
```

- [ ] **Step 4: 运行测试**

```bash
mvn test -Dtest=RabbitMqListenerTest
```

- [ ] **Step 5: 提交**

```bash
git add .
git commit -m "feat: 实现邮件队列监听处理方法"
```

---

### Task 6: 评论服务邮件通知

**Files:**
- Modify: `src/main/java/com/sidifensen/service/impl/CommentServiceImpl.java`
- Create: `src/test/java/com/sidifensen/service/impl/CommentServiceImplTest.java`

- [ ] **Step 1: 编写测试（TDD）**

```java
@Test
@DisplayName("评论审核通过 - 发送邮件通知")
void testAdminExamineComment_Pass_SendsEmail() {
    // 验证 sendCommentEmailNotification 被调用
}
```

- [ ] **Step 2: 添加 sendCommentEmailNotification 方法**

```java
private void sendCommentEmailNotification(Integer notifiedUserId, String commenterNickname,
        String articleTitle, Integer articleId, String commentContent,
        Boolean isReply, String parentCommentContent) {
    // 检查用户设置 -> 查询邮箱 -> 发送到 RabbitMQ
}
```

- [ ] **Step 3: 更新 sendCommentNotification 方法**

在发送站内通知后调用 `sendCommentEmailNotification()`

- [ ] **Step 4: 运行测试**

```bash
mvn test -Dtest=CommentServiceImplTest
```

- [ ] **Step 5: 提交**

```bash
git add .
git commit -m "feat: 实现评论邮件通知发送"
```

---

### Task 7: 文章审核邮件通知

**Files:**
- Modify: `src/main/java/com/sidifensen/service/impl/ArticleServiceImpl.java`

- [ ] **Step 1: 添加依赖注入**

```java
@Resource
private UserSettingsService userSettingsService;
```

- [ ] **Step 2: 添加 sendSystemEmailNotification 方法**

```java
private void sendSystemEmailNotification(Integer userId, String notificationTitle,
        String notificationContent, String extraInfo, String linkUrl, String linkText) {
    // 检查用户设置 -> 查询邮箱 -> 发送到 RabbitMQ
}
```

- [ ] **Step 3: 更新 adminExamineArticle 方法**

在审核完成后调用 `sendSystemEmailNotification()`

- [ ] **Step 4: 编译验证**

```bash
mvn clean compile -DskipTests
```

- [ ] **Step 5: 提交**

```bash
git add .
git commit -m "feat: 添加文章审核邮件通知"
```

---

### Task 8: 专栏审核邮件通知

**Files:**
- Modify: `src/main/java/com/sidifensen/service/impl/ColumnServiceImpl.java`

- [ ] **Step 1: 添加依赖注入**

```java
@Resource
private UserSettingsService userSettingsService;

@Resource
private RabbitTemplate rabbitTemplate;
```

- [ ] **Step 2: 添加 sendSystemEmailNotification 方法**

- [ ] **Step 3: 更新 adminExamineColumn 和 adminBatchExamineColumn 方法**

- [ ] **Step 4: 编译验证**

```bash
mvn clean compile -DskipTests
```

- [ ] **Step 5: 提交**

```bash
git add .
git commit -m "feat: 添加专栏审核邮件通知"
```

---

### Task 9: 完整验证

- [ ] **Step 1: 运行所有单元测试**

```bash
mvn test
```

- [ ] **Step 2: 编译打包**

```bash
mvn clean package -DskipTests
```

- [ ] **Step 3: 提交最终代码**

```bash
git add .
git commit -m "chore: 完成邮件通知功能验证"
```

---

## 测试检查清单

- [ ] UserSettingsService 单元测试通过
- [ ] RabbitMqListener 单元测试通过
- [ ] CommentService 单元测试通过
- [ ] 所有单元测试通过
- [ ] 编译无错误
- [ ] 代码格式检查通过
