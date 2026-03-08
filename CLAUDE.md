# CLAUDE.md

本文件为 Claude Code (claude.ai/code) 在此仓库中工作时提供指导。

## 项目概述

Sidifensen Blog 是一个采用前后端分离架构的现代化社区系统。后端基于 Spring Boot 3.4.0 + Java 21，前端基于 Vue 3 + Vite。

**官网地址**: https://www.sidifensen.com

## 快速命令

### 后端 (Spring Boot)

```bash
cd sidifensen_blog_backend

# 构建
mvn clean package -DskipTests

# 运行
java -jar target/sidifensen_blog_backend-1.0-SNAPSHOT.jar

# 或者在 IDE 中直接运行 Main.java
```

### 前端 (Vue 3)

```bash
# 用户端
cd sidifensen_blog_frontend/sidifensen_user
npm install
npm run dev       # http://localhost:5173
npm run build

# 管理端
cd sidifensen_blog_frontend/sidifensen_admin
npm install
npm run dev       # http://localhost:5174
npm run build
```

### Docker 部署

```bash
cd script

# 复制环境配置
cp env.example .env

# 启动所有服务
docker-compose up -d

# 或使用启动脚本
./start.sh        # Linux/Mac
start.bat         # Windows
```

## 架构

### 后端技术栈

- **框架**: Spring Boot 3.4.0, Spring Security 6.4.1, Spring AI 1.0.0
- **ORM**: MyBatis-Plus 3.5.14 (无 XML mapper)
- **数据库**: MySQL 8.0, Redis 7, RabbitMQ 3, MinIO
- **AI**: 集成 DeepSeek API，用于文章摘要、标题生成、标签推荐

### 前端技术栈

- **框架**: Vue 3.5.13 (Composition API), Vite 6
- **UI**: Element Plus 2.10, Pinia 3, Vue Router 4
- **构建**: unplugin-auto-import, unplugin-vue-components

### 核心目录

```
sidifensen_blog/
├── script/                          # 部署脚本、Docker 配置
│   ├── docker-compose.yml           # 主编排文件
│   ├── env.example                  # 环境变量模板
│   └── deploy/                      # Jenkins/Gitea CI/CD
├── sidifensen_blog_backend/
│   └── src/main/java/com/sidifensen/
│       ├── controller/              # REST API 控制器
│       ├── service/impl/            # 业务逻辑
│       ├── mapper/                  # MyBatis-Plus 数据访问
│       ├── domain/                  # 实体、DTO、VO、常量
│       ├── security/                # JWT、Spring Security
│       ├── websocket/               # 实时消息
│       └── aspect/                  # AOP (限流、日志)
├── sidifensen_blog_frontend/
│   ├── sidifensen_user/             # 用户端前端
│   └── sidifensen_admin/            # 管理端后台
└── sql/                             # 数据库初始化脚本
```

## 后端规范（来自 .cursor/rules/java.mdc）

### 核心强制规则

- **依赖注入**: 使用 `@Resource` 而非 `@Autowired`
- **实体类**: 使用 `@Data`, `@EqualsAndHashCode(callSuper = false)`, `@Accessors(chain = true)`
- **MyBatis-Plus**: 使用 `LambdaQueryWrapper` 进行类型安全查询，不使用 XML
- **异常处理**: 抛出 `BlogException`，错误信息必须来自 `BlogConstants`，禁止硬编码字符串
- **Service 模式**: 继承 `ServiceImpl<Mapper, Entity>` 并实现 service 接口
- **日志**: 使用 `@Slf4j`，记录所有业务操作和异常

### 命名规范

- Controller: `XxxController`
- Service 接口：`XxxService`
- Service 实现：`XxxServiceImpl`
- Entity: 使用业务名称，如 `Article`, `User`
- DTO: `XxxDto`
- VO: `XxxVo`
- Mapper: `XxxMapper`

### Controller 层规范

- 使用 `@RestController` 和 `@RequestMapping`
- HTTP 方法：`@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`
- 参数校验：`@NotNull`, `@Valid`
- 权限控制：`@PreAuthorize`
- 统一返回 `Result` 对象

### Entity 规范

- 使用 `@TableName` 指定表名
- 主键：`@TableId(type = IdType.AUTO)`
- 自动填充：`@TableField(fill = FieldFill.INSERT/INSERT_UPDATE)`
- 逻辑删除：`@TableLogic`
- 字段校验：`@Min`, `@Max`

### 异常信息管理

```java
// ✅ 正确方式
throw new BlogException(BlogConstants.NotFoundUser);

// ❌ 错误方式 - 禁止硬编码
throw new BlogException("该用户不存在");
```

### 代码修改后检查规则

**每次修改后端代码后必须执行以下检查：**

1. **编译检查**: 运行 `mvn clean compile -DskipTests` 确保代码能通过编译
2. **导入检查**: 确认所有新增的类都已正确导入，没有缺失或冗余的 import
3. **语法检查**: 检查是否有拼写错误、注解使用是否正确

```bash
# 快速编译检查
cd sidifensen_blog_backend
mvn clean compile -DskipTests
```

## 前端规范（来自 .cursor/rules/frontend.mdc）

### 最高优先级规则

**SCSS 层次结构必须严格对应 template DOM 结构 —— 这是代码质量第一标准！**

1. SCSS 嵌套层次必须 100% 匹配 template 的 DOM 结构
2. 不允许跳过中间层级或打乱顺序
3. 每个样式模块必须有中文注释说明对应哪个部分

### Vue 组件规范

- **语法**: 统一使用 `<script setup>` 和 Composition API
- **命名**: 页面/组件使用 PascalCase，如 `Home.vue`, `ArticleDetail.vue`
- **模板**: 使用 kebab-case 属性名，组件标签使用 PascalCase
- **⚠️ 禁止导入 Element Plus 消息组件**: `ElMessage` 已全局注册，直接可用

### 代码结构规范

```vue
<script setup>
// 1. 导入依赖 - 按功能分组
import { ref, reactive, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Plus, Message } from "@element-plus/icons-vue";
import { getUserInfo } from "@/api/user";
import { useUserStore } from "@/stores/userStore";

// 2. 路由和状态管理
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 3. 响应式数据 - 按功能分组并注释
const loading = ref(false); // 加载状态
const data = ref(null); // 数据

// 4. 计算属性
const isEmpty = computed(() => !data.value);

// 5. 方法定义 - 按功能分组
const fetchData = async () => {
  try {
    loading.value = true;
    // ...
  } catch (error) {
    ElMessage.error("获取失败");
  } finally {
    loading.value = false;
  }
};

// 6. 生命周期
onMounted(() => {
  fetchData();
});
</script>
```

### 样式规范

```scss
// ✅ 正确：SCSS 层次严格对应 template
.search-container {
  .search-section {
    .container {
      // 每个模块有中文注释
    }
  }
}

// ❌ 错误：跳过层级
.search-container {
  .container {
    // 缺少 .search-section
  }
}
```

## 环境配置

必需服务（通过 Docker Compose）：

- MySQL 8.0 (端口 3306)
- Redis 7 (端口 6379)
- RabbitMQ 3 (端口 5672, 管理界面 15672)
- MinIO (端口 9000/9001)

运行前从 `script/env.example` 复制并配置 `.env` 文件。

## 核心功能

- **认证**: JWT + Spring Security, OAuth (Gitee/GitHub/QQ)
- **内容**: 文章管理、专栏、标签、评论
- **AI**: 文章摘要提取、标题生成、标签推荐 (DeepSeek API)
- **实时通信**: WebSocket 私信系统、通知中心
- **审核**: 通过阿里云内容安全服务自动审核
- **限流**: 基于 AOP 的 AI 接口限流

## CI/CD

Jenkins + Gitea 自动化：

1. 推送代码到 Gitea 仓库
2. Webhook 触发 Jenkins Pipeline
3. 自动构建后端 (Maven) 和前端 (Node.js)
4. 通过 Docker Compose 部署

详细配置请查看 `script/deploy/README.md`。

## 测试

```bash
# 后端测试
cd sidifensen_blog_backend
mvn test

# 前端测试（如已配置）
npm run test
```
