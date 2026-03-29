# Sidifensen Blog 开发指南

---

## 项目结构

| 模块 | 路径 | 本地地址                  | 生产地址 |
|------|------|-----------------------|----------|
| 后端 API | `sidifensen_blog_backend/` | http://localhost:5000 | https://api.sidifensen.com |
| 前端用户端 | `sidifensen_blog_frontend/` | http://localhost:7000 | https://sidifensen.com |
| 前端管理端 | `sidifensen_blog_frontend/` | http://localhost:8000 | https://admin.sidifensen.com |
| 小程序 | `sidifensen_blog_miniprogram/` | http://localhost:9000 | 需配置合法域名 |

---

## 复杂任务流程

> 遇到复杂任务时，必须遵循以下流程，确保质量和可维护性。

```
brainstorming → using-git-worktrees → writing-plans → executing-plans
→ subagent-driven-development → test-driven-development → verification-before-completion
```

**核心原则**：先测试后实现、流程优于猜测、验证先于断言

详细流程见 `skill: superpowers:using-superpowers`

---

## 后端规范

### 核心规则

| 规范 | 要求 |
|------|------|
| 依赖注入 | `@Resource` (禁用 `@Autowired`) |
| 实体注解 | `@Data`, `@EqualsAndHashCode(callSuper = false)`, `@Accessors(chain = true)` |
| 查询方式 | `LambdaQueryWrapper` (禁用 XML) |
| 异常处理 | `BlogException` + `BlogConstants` (禁止硬编码) |
| Service 模式 | 继承 `ServiceImpl<Mapper, Entity>` |
| 日志 | `@Slf4j` + `log.error` (只记录错误，禁用 `log.info`) |
| 操作日志 | 管理端 Controller 必须添加 `@LogOperation` 注解 |
| 注释 | 新增/修改代码必须补充中文注释 |

### 命名规范

```
XxxController    # 控制器
XxxService       # 服务接口
XxxServiceImpl   # 服务实现
XxxMapper        # 数据访问
XxxDto           # 数据传输对象
XxxVo            # 视图对象
XxxEntity        # 实体类 (业务命名，如 Article, User)
```

### 修改后检查

```bash
cd sidifensen_blog_backend && mvn clean compile -DskipTests
```

检查清单：
- [ ] 编译通过
- [ ] 导入完整无冗余
- [ ] 无拼写错误/注解错误
- [ ] 已补充必要中文注释
- [ ] 管理端 Controller 已添加 `@LogOperation` 注解

> **注意**: 调试代码时不要自动重启后端服务

---

## 前端规范

### 三大铁律

#### 规则 1：所有页面必须适配黑夜模式 + 移动端响应式

**禁止**：硬编码颜色值、只写浅色模式、生成完不测试、移动端布局错乱

**必须**：使用 CSS 变量、提供 `html.dark` 覆盖、切换验证、**移动端布局正常**

> ⚠️ **移动端常见问题**：首页模块（如"欢迎来到斯蒂芬森"）未固定，随屏幕大小改变被挤到屏幕外不可见。必须使用 `min-height` 或固定定位确保内容始终可见。

**标准模板：**

```scss
.your-component {
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

| 禁止 | 必须 |
|------|------|
| 硬编码文章列表/用户数据 | API 调用获取真实数据 |
| 按钮点击 `console.log` 应付 | 实现真实业务功能 |
| 表单提交不连接后端 | 调用真实 API 持久化 |

### Vue 组件规范

- **语法**: `<script setup>` + Composition API
- **命名**: PascalCase (`Home.vue`, `ArticleDetail.vue`)
- **消息组件**: `ElMessage` 已全局注册，**禁止导入**
- **表格**: `el-table-column` 使用 `min-width` / `show-overflow-tooltip`
- **SVG 图标**: `<svg-icon name="github" width="20px" height="20px" color="#999" />`
  - 常用图标：`github`, `gitee`, `qq`, `weixin`, `weibo`

### 修改后检查

```bash
cd sidifensen_blog_frontend/sidifensen_user && npm run build
cd sidifensen_blog_frontend/sidifensen_admin && npm run build
```

检查清单：
- [ ] 编译通过，无 JavaScript/语法错误
- [ ] 页面正常加载，无白屏/报错
- [ ] 功能正常（表单/按钮/跳转）
- [ ] 黑夜模式显示正常
- [ ] 响应式布局正常（**特别检查移动端**）
- [ ] 浏览器打开验证（非 Playwright）

---

## 设计规范（去 AI 味）

**核心原则**：少即是多，克制比表达更重要

### 避免 AI 味设计

| 问题 | 反面教材 | 正确做法 |
|------|---------|---------|
| 渐变滥用 | `linear-gradient(135deg, #0891b2, #0e7490)` | 纯色 `#0891b2` |
| 多层阴影 | 3-4 层 `box-shadow` | 单层 `0 1px 3px rgba(0,0,0,0.1)` |
| 光泽动画 | `::after` 光带扫过 | 不需要 |
| 毛玻璃滥用 | 到处 `backdrop-filter: blur()` | 仅模态框使用 |
| 夸张悬停 | 位移 + 缩放 + 变色 + 阴影 | 只变阴影或颜色 |

### 设计准则

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

## 新功能开发规范

**核心原则**：优先沿用项目现有设计和规范

添加新功能或新页面时，必须先参考现有类似页面（如 `Home.vue`、`Article/index.vue`）的代码结构、样式风格、API 调用模式，保持项目一致性。

**禁止**：
- 不看现有代码直接生成全新结构
- 引入项目外 UI 库
- 忽略黑夜模式适配

---


## 数据库工具

项目已配置 MySQL MCP 工具，可用于：
- 新增表/字段
- 调试数据验证业务逻辑

---

## 常用工具

| 工具 | 命令 | 说明 |
|------|------|------|
| Claude Code | `/commit` | 提交当前更改 |
| Claude Code | `/frontend-design` | 前端设计优化 |
| Claude Code | `skill: superpowers:*` | 复杂任务流程 |

### 前端页面调试

项目已配置 `chrome-devtools` MCP 工具，可用于打开浏览器查看和调试页面，包括元素检查、交互操作、控制台日志查看等功能。

---

## gstack

所有网页浏览必须使用 gstack 的 `/browse` 技能，**禁止**使用 `mcp__claude-in-chrome__*` 工具。

如果 gstack 技能不起作用，请运行 `cd .claude/skills/gstack && ./setup` 来构建二进制文件并注册技能。

### 可用技能

| 技能 | 说明 |
|------|------|
| `/office-hours` | 办公时间咨询 |
| `/plan-ceo-review` | CEO 计划审查 |
| `/plan-eng-review` | 工程计划审查 |
| `/plan-design-review` | 设计计划审查 |
| `/design-consultation` | 设计咨询 |
| `/design-shotgun` | 快速设计评审 |
| `/review` | 代码审查 |
| `/ship` | 发布功能 |
| `/land-and-deploy` | 部署上线 |
| `/canary` | 金丝雀发布 |
| `/benchmark` | 性能基准测试 |
| `/browse` | 网页浏览 |
| `/connect-chrome` | 连接 Chrome |
| `/qa` | QA 测试 |
| `/qa-only` | 仅 QA 测试 |
| `/design-review` | 设计审查 |
| `/setup-browser-cookies` | 设置浏览器 Cookie |
| `/setup-deploy` | 设置部署 |
| `/retro` | 回顾 |
| `/investigate` | 调查 |
| `/document-release` | 文档发布 |
| `/codex` | Codex 助手 |
| `/cso` | CSO 助手 |
| `/autoplan` | 自动规划 |
| `/careful` | 谨慎模式 |
| `/freeze` | 冻结 |
| `/guard` | 守护模式 |
| `/unfreeze` | 解冻 |
| `/gstack-upgrade` | 升级 gstack |
