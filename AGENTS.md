# AGENTS.md

本文件为 AI 助手在此仓库中工作时提供指导。

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
npm run dev       # http://localhost:7000
npm run build

# 管理端
cd sidifensen_blog_frontend/sidifensen_admin
npm install
npm run dev       # http://localhost:8000
npm run build
```

### Docker 部署

```bash
cd script

# 复制环境配置
cp dev/.env.example .env

# 启动所有服务
docker-compose up -d

# 或使用启动脚本
dev/start.sh        # Linux/Mac
dev/start.bat       # Windows
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
│   ├── dev/docker-compose.yml       # 主编排文件
│   ├── dev/.env.example             # 环境变量模板
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

## 后端规范

### 核心规则（必须遵守）

| 规范 | 要求 |
|------|------|
| 依赖注入 | `@Resource` (禁用 `@Autowired`) |
| 实体注解 | `@Data`, `@EqualsAndHashCode(callSuper = false)`, `@Accessors(chain = true)` |
| 查询方式 | `LambdaQueryWrapper` (禁用 XML) |
| 异常处理 | `BlogException` + `BlogConstants` (禁止硬编码) |
| Service 模式 | 继承 `ServiceImpl<Mapper, Entity>` |
| 日志 | `@Slf4j` + `log.error` (只记录错误) |
| 注释 | 新增/修改代码必须补充中文注释 |

### 代码示例

**异常处理：**
```java
// ✅ 正确
throw new BlogException(BlogConstants.NotFoundUser);

// ❌ 禁止
throw new BlogException("该用户不存在");
```

**日志规范：**
```java
try {
    userService.updateUser(user);
} catch (Exception e) {
    log.error("更新用户失败，userId={}, error={}", user.getId(), e.getMessage(), e);
    throw new BlogException(BlogConstants.UpdateUserFailed);
}
// ❌ 禁止：不要打印正常流程日志
```

### 命名规范

- `XxxController` / `XxxService` / `XxxServiceImpl`
- `XxxDto` / `XxxVo`
- Entity 用业务名：`Article`, `User`
- `XxxMapper`

### 修改后检查

```bash
cd sidifensen_blog_backend && mvn clean compile -DskipTests
```

检查清单：
- [ ] 编译通过
- [ ] 导入完整无冗余
- [ ] 无拼写错误/注解错误
- [ ] 已补充必要中文注释

> **注意**: 调试代码时不要自动重启后端服务

---

## 前端规范

### 🔴 三大铁律（违反即错误）

#### 规则 1：所有页面必须适配黑夜模式

**禁止：** 硬编码颜色值、只写浅色模式、生成完不测试

**必须：** 使用 CSS 变量、提供 `html.dark` 覆盖、切换验证

**标准模板：**
```scss
.your-component {
  // 浅色模式默认值
  --bg-page: #f8fafc;
  --bg-card: #ffffff;
  --text-primary: #1e293b;
  --text-regular: #475569;
  --border: #e2e8f0;

  .card {
    background: var(--bg-card);
    border: 1px solid var(--border);
  }
}

html.dark {
  .your-component {
    --bg-page: #0f172a;
    --bg-card: #1e293b;
    --text-primary: #f1f5f9;
    --text-regular: #cbd5e1;
    --border: #334155;
  }
}
```

#### 规则 2：SCSS 层次必须匹配 DOM 结构

1. SCSS 嵌套层次 100% 匹配 template DOM 结构
2. 不允许跳过中间层级
3. 每个样式模块必须有中文注释

#### 规则 3：禁止假数据，所有功能必须真实实现

| ❌ 禁止 | ✅ 必须 |
|--------|--------|
| 硬编码文章列表/用户数据 | API 调用获取真实数据 |
| 按钮点击 `console.log` 应付 | 实现真实业务功能 |
| 表单提交不连接后端 | 调用真实 API 持久化 |

**特殊情况（后端 API 未完成）：**
```javascript
// TODO: 后端 API 待开发，暂时使用模拟数据
```

### Vue 组件规范

- **语法**: `<script setup>` + Composition API
- **命名**: PascalCase - `Home.vue`, `ArticleDetail.vue`
- **消息组件**: `ElMessage` 已全局注册，**禁止导入**
- **表格**: `el-table-column` 使用 `min-width` / `show-overflow-tooltip`
- **SVG 图标**: `<svg-icon name="github" width="20px" height="20px" color="#999" />`
  - 常用图标：`github`, `gitee`, `qq`, `weixin`, `weibo`

### 代码结构

```vue
<script setup>
// 1. 导入依赖
import { ref, computed, onMounted } from "vue"
import { useRoute, useRouter } from "vue-router"
import { getUserInfo } from "@/api/user"
import { useUserStore } from "@/stores/userStore"

// 2. 路由和状态管理
const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 3. 响应式数据（带注释）
const loading = ref(false)
const data = ref(null)

// 4. 计算属性
const isEmpty = computed(() => !data.value)

// 5. 方法定义
const fetchData = async () => {
  try {
    loading.value = true
  } catch (error) {
    ElMessage.error("获取失败")
  } finally {
    loading.value = false
  }
}

// 6. 生命周期
onMounted(() => {
  fetchData()
})
</script>
```

### 修改后检查

```bash
cd sidifensen_blog_frontend/sidifensen_user && npm run build
cd sidifensen_blog_frontend/sidifensen_admin && npm run build
```

检查清单：
- [ ] 编译通过，无 TypeScript/语法错误
- [ ] 页面正常加载，无白屏/报错
- [ ] 功能正常（表单/按钮/跳转）
- [ ] 黑夜模式显示正常
- [ ] 响应式布局正常

---

## 设计规范（去 AI 味）

**核心原则：少即是多，克制比表达更重要**

### ❌ 避免 AI 味设计

| 问题 | 反面教材 | 正确做法 |
|------|---------|---------|
| **渐变滥用** | `linear-gradient(135deg, #0891b2, #0e7490)` | 纯色 `#0891b2` |
| **多层阴影** | 3-4 层 `box-shadow` | 单层 `0 1px 3px rgba(0,0,0,0.1)` |
| **光泽动画** | `::after` 光带扫过 | 不需要 |
| **毛玻璃滥用** | 到处 `backdrop-filter: blur()` | 仅模态框使用 |
| **夸张悬停** | 位移 + 缩放 + 变色 + 阴影 | 只变阴影或颜色 |

### ✅ 设计准则

1. 必须自定义 Element Plus 组件
2. 纯色优先，渐变仅用于品牌强调
3. 单层阴影足够
4. 悬停只变一个属性
5. 内容即装饰

### 标准色板

| 变量 | 浅色模式 | 深色模式 | 用途 |
|------|---------|---------|------|
| `--bg-page` | `#f8fafc` | `#0f172a` | 页面背景 |
| `--bg-card` | `#ffffff` | `#1e293b` | 卡片背景 |
| `--text-primary` | `#1e293b` | `#f1f5f9` | 主文字 |
| `--text-regular` | `#475569` | `#cbd5e1` | 常规文字 |
| `--text-muted` | `#64748b` | `#94a3b8` | 次要文字 |
| `--border` | `#e2e8f0` | `#334155` | 边框 |

### 设计优化指令

- "优化 xxx 页面，按照去 AI 味规范"
- "重新设计 xxx 页面，参考知乎/掘金/Vercel 风格"
- 使用 skill: `/frontend-design`

---

## 新功能/新页面开发规范

**核心原则：优先沿用项目现有设计和规范**

添加新功能或新页面时，必须先参考现有类似页面（如 `Home.vue`、`Article/index.vue`）的代码结构、样式风格、API 调用模式，保持项目一致性。

**禁止：** 不看现有代码直接生成全新结构、引入项目外 UI 库、忽略黑夜模式适配

---

## 数据库工具

项目已配置 MySQL MCP 工具，可用于：
- 新增表/字段
- 调试数据验证业务逻辑

---

## 线上部署环境配置 (本地不需要)

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

详细配置请查看 `script/jenkins/README.md`。

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
git push gitea      # Gitea (自建)
```

**远程仓库地址：**
- `origin`: https://gitee.com/sidifensen/sidifensen_blog.git
- `github`: https://github.com/sidifensen/sidifensen_blog.git
- `gitea`: http://115.190.116.72:3000/sidifensen/sidifensen_blog.git
