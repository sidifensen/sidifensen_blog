# CLAUDE.md

Sidifensen Blog 开发指南

## 快速命令

### 后端
```bash
cd sidifensen_blog_backend
mvn clean package -DskipTests    # 构建
java -jar target/sidifensen_blog_backend-1.0-SNAPSHOT.jar    # 运行
```

### 前端
```bash
# 用户端
cd sidifensen_blog_frontend/sidifensen_user && npm install && npm run dev
# 管理端
cd sidifensen_blog_frontend/sidifensen_admin && npm install && npm run dev
```

---

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

## Superpowers 标准流程

**日常开发提示词（放在任何任务指令后面）：**

## Superpowers 标准流程

**何时使用**：执行复杂功能开发、新增模块、重构等多步骤任务时。

**核心机制**：每个阶段显式触发对应技能，不依赖 AI 记忆长流程。

---

### 阶段 1：启动任务
```
【1. Skill 检查 → 2. brainstorming → 3. using-git-worktrees】
```
- 先查技能再行动，不要跳过
- 需求澄清，明确要做什么
- 创建隔离工作区

### 阶段 2：规划
```
【4. writing-plans → 5. executing-plans】
```
- 任务分解，输出详细实现计划
- 激活计划，准备执行

### 阶段 3：执行
```
【6. subagent-driven-development + 7. test-driven-development】
```
- 子代理开发，每个任务独立实现
- 测试驱动，先写测试再写代码

### 阶段 4：完成
```
【8. verification-before-completion → 9. requesting-code-review → 10. finishing-a-development-branch】
```
- 验证完成，运行测试证明功能正常
- 代码评审，检查代码质量
- 分支完成，合并或创建 PR

---

### 分支场景
- 遇到问题 → `systematic-debugging`（系统调试）
- 接收评审反馈 → `receiving-code-review`（接收代码评审）

### 核心原则
- Write tests first, always（始终先写测试）
- Process over guessing（流程优于猜测）
- Simplicity as primary goal（简单性为首要目标）
- Evidence before assertions - no empty claims, no skipping steps（先验证再断言 - 禁止空口断言，禁止跳过步骤）

---

### 使用方式

**方式 A：完整流程（推荐）**
```
帮我添加用户积分系统，按照 superpowers 标准流程执行
```

**方式 B：分阶段触发（更可靠）**
```
/ skill superpowers:using-superpowers   # 启动流程
/ skill superpowers:brainstorming       # 需求澄清
/ skill superpowers:using-git-worktrees # 隔离工作区
/ skill superpowers:writing-plans       # 任务分解
/ skill superpowers:executing-plans     # 激活计划
/ skill superpowers:subagent-driven-development # 执行开发
/ skill superpowers:test-driven-development     # 测试驱动
/ skill superpowers:verification-before-completion # 验证
/ skill superpowers:requesting-code-review      # 评审
/ skill superpowers:finishing-a-development-branch # 完成
```

**方式 C：简化指令（简单任务）**
```
帮我修复登录 bug【superpowers 流程：先 brainstorming，再 writing-plans，执行时 TDD，完成前 verification】
```

---

### 流程偏离纠正

如果发现 AI 跳过某个步骤，立即提醒：
- "请先执行 brainstorming，不要直接写代码"
- "还没有写 plan，请先调用 writing-plans 技能"
- "还没有验证，请运行测试命令证明功能正常"
- "请先调用 verification-before-completion 技能"
