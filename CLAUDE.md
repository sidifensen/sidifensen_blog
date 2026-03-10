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

### 前端设计优化指令

当需要优化页面设计时，直接说：

- "优化 xxx 页面，按照去 AI 味规范"
- "重新设计 xxx 页面，参考知乎/掘金/Vercel 风格"

或使用 skill：`/frontend-design`（已安装的官方 skill）

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

**注意**: 调试代码时不要自动重启后端服务，用户会自行重启。

## 前端规范（来自 .cursor/rules/frontend.mdc）

### 最高优先级规则（强制）

#### 规则 1：SCSS 层次结构必须严格对应 template DOM 结构

**这是代码质量第一标准！**

1. SCSS 嵌套层次必须 100% 匹配 template 的 DOM 结构
2. 不允许跳过中间层级或打乱顺序
3. 每个样式模块必须有中文注释说明对应哪个部分

#### 规则 2：所有页面必须适配黑夜模式

**这是强制性要求，没有例外！**

1. 禁止在样式中硬编码颜色值（如 `#fff`、`#1e293b`）
2. 必须使用 CSS 变量定义所有颜色相关的样式
3. 必须为 `html.dark` 提供对应的 CSS 变量覆盖
4. 新增页面或修改页面时，必须在两种模式下都测试

**每次修改前端样式后必须检查：**

```bash
# 切换黑夜模式，检查页面是否正常显示
# 如果有任何颜色显示异常，必须立即修复
```

### Vue 组件规范

- **语法**: 统一使用 `<script setup>` 和 Composition API
- **命名**: 页面/组件使用 PascalCase，如 `Home.vue`, `ArticleDetail.vue`
- **模板**: 使用 kebab-case 属性名，组件标签使用 PascalCase
- **⚠️ 禁止导入 Element Plus 消息组件**: `ElMessage` 已全局注册，直接可用
- **el-table-column 宽度**: 前端页面调试时，`el-table-column` 的宽度要自动调整，尽量避免列名占两行（可使用 `min-width` 或 `show-overflow-tooltip`）

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

## 前端设计规范（去 AI 味指南）

**核心原则：少即是多，克制比表达更重要**

### ❌ 避免 AI 味设计

| 问题 | 反面教材 | 正确做法 |
|------|---------|---------|
| **渐变滥用** | `linear-gradient(135deg, #0891b2 0%, #0e7490 100%)` | 纯色 `#0891b2` |
| **多层阴影** | 3-4 层 `box-shadow` 叠加 | 单层阴影 `0 1px 3px rgba(0,0,0,0.1)` |
| **光泽动画** | `::after` 光带扫过效果 | 不需要 |
| **毛玻璃滥用** | 到处 `backdrop-filter: blur()` | 仅模态框使用 |
| **夸张悬停** | 同时位移 + 缩放 + 变色 + 阴影 | 只变阴影或颜色 |
| **装饰性标题** | "探索文章，开启知识之旅" | 直接上核心内容 |
| **胶囊数据** | 每个数字都包底色胶囊 | 纯文本或简单图标 |

### ✅ 人味设计准则

1. **用 Element Plus 默认样式** - 不要过度覆盖按钮、输入框
2. **纯色优先** - 渐变只在品牌强调处使用
3. **单层阴影** - `0 1px 3px rgba(0,0,0,0.1)` 足够
4. **悬停只变一个属性** - 阴影或颜色，不要同时变 3 个
5. **内容即装饰** - 让内容本身上台，不要加框
6. **参考大厂产品** - Google、知乎、掘金几乎零装饰

### 参考案例

```scss
// ✅ 好的卡片样式
.article-card {
  background: #fff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;

  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); // 只变阴影
  }
}
```

## 黑夜模式适配规范（强制要求）

**⚠️ 重要：所有前端页面必须适配黑夜模式，这是强制性要求，没有例外！**

### 核心原则

1. **禁止硬编码颜色** - 任何时候都不能直接写 `#fff`、`#1e293b` 等颜色值
2. **必须使用 CSS 变量** - 所有颜色相关的样式必须通过 CSS 变量定义
3. **必须提供暗色覆盖** - 必须为 `html.dark` 提供对应的 CSS 变量覆盖
4. **必须测试两种模式** - 新增或修改页面时，必须在浅色和深色模式下都测试

### 实现方式

项目通过 `darkStore.js` 管理黑夜模式，使用 `isDark` ref 状态，存储在 localStorage 持久化。
通过切换 `html` 标签的 `dark`/`light` 类名来控制主题。

### CSS 变量使用规范（必须遵守）

```scss
// ✅ 正确方式 - 使用 CSS 变量
.home-container {
  --bg-page: #f8fafc;
  --bg-card: #ffffff;
  --text-primary: #1e293b;
  --text-regular: #475569;
  --text-muted: #64748b;
  --border: #e2e8f0;

  background: var(--bg-page);

  .card {
    background: var(--bg-card);
    color: var(--text-primary);
    border: 1px solid var(--border);
  }
}

// 黑夜模式覆盖（必须添加）
html.dark {
  .home-container {
    --bg-page: #0f172a;
    --bg-card: #1e293b;
    --text-primary: #f1f5f9;
    --text-regular: #cbd5e1;
    --text-muted: #94a3b8;
    --border: #334155;
  }
}
```

### 标准色板（推荐使用）

| 变量名 | 浅色模式 | 深色模式 | 用途 |
|--------|---------|---------|------|
| `--bg-page` | `#f8fafc` | `#0f172a` | 页面背景 |
| `--bg-card` | `#ffffff` | `#1e293b` | 卡片背景 |
| `--text-primary` | `#1e293b` | `#f1f5f9` | 主文字 |
| `--text-regular` | `#475569` | `#cbd5e1` | 常规文字 |
| `--text-muted` | `#64748b` | `#94a3b8` | 次要文字 |
| `--border` | `#e2e8f0` | `#334155` | 边框颜色 |
| `--border-light` | `#f1f5f9` | `#1e293b` | 浅色边框 |

### 注意事项

- **阴影要更轻** - 深色模式下使用更低透明度的阴影
- **对比度检查** - 确保深色模式下文字清晰可读
- **图标颜色** - 使用 `currentColor` 或 CSS 变量
- **Element Plus** - 使用 `var(--el-color-primary)` 等内置变量

### 检查清单（每次修改前端后）

- [ ] 检查是否有硬编码的颜色值
- [ ] 检查是否所有颜色都使用 CSS 变量
- [ ] 检查是否有 `html.dark` 对应的变量覆盖
- [ ] 在黑夜模式下测试页面，确保没有颜色异常

### 代码示例

```scss
// ✅ 好的按钮样式
.search-btn {
  // 用 Element Plus 默认色，不覆盖渐变
  background: var(--el-color-primary);
}

// ❌ AI 味卡片（不要这样写）
.article-card {
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(20px);
  box-shadow:
    0 4px 24px rgba(8, 145, 178, 0.12),
    0 1px 3px rgba(8, 145, 178, 0.08),
    inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(8, 145, 178, 0.15);

  &::before {
    content: "";
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.4), transparent);
  }

  &:hover {
    transform: translateX(12px) translateY(-2px) scale(1.02);
    // ... 同时变 5 个属性
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

## 数据库操作工具（本地开发）

项目已配置 MySQL MCP 工具，在本地开发时可以使用该工具进行数据库操作：

- **新增表/字段**：开发新功能时创建数据库表结构
- **调试数据**：查看表中数据验证业务逻辑
- **数据验证**：检查数据是否正确写入或更新

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

## Git 远程仓库配置

项目同步推送到三个平台：

```bash
# 查看远程仓库
git remote -v

# 推送到所有平台（分别执行，不要合并命令）
git push origin main    # Gitee
git push github main    # GitHub
git push gitea main     # Gitea (自建)
```

**远程仓库地址：**
- `origin`: https://gitee.com/sidifensen/sidifensen_blog.git
- `github`: https://github.com/sidifensen/sidifensen_blog.git
- `gitea`: http://115.190.116.72:3000/sidifensen/sidifensen_blog.git
