# Sidifensen Blog 开发指南

> **强制要求：开发前必须严格按照以下 superpowers-plus 流程执行，禁止跳过任何步骤**

## superpowers-plus 标准流程

1. **brainstorming** — 需求澄清（编写代码前激活）
2. **using-git-worktrees** — 隔离工作区（设计批准后激活）
3. **writing-plans** — 任务分解（设计批准后激活）
4. **executing-plans** — 计划执行（含并行/串行子代理自动路由）
5. **test-driven-development** — 测试驱动（RED-GREEN-REFACTOR）
6. **requesting-code-review** — 代码评审（任务间隙激活）
7. **finishing-a-development-branch** — 分支完成（任务完成后激活）

### 分支场景
- 遇到问题 → **systematic-debugging**（系统调试）
- 接收评审反馈 → **receiving-code-review**（接收代码评审）
- 多任务可并行 → **dispatching-parallel-agents**（并行代理分发）
- 需要编写验收标准 → **writing-acceptance-criteria**（编写验收标准）
- 验证完成后、合并前 → **acceptance-testing**（验收测试）

### 核心原则

- **Iron Law (TDD)** — 没有失败测试就不写生产代码；遵循 RED-GREEN-REFACTOR 节奏
- **Iron Law (Debugging)** — 没有根因分析就不修 bug，禁止猜测修复
- **流程优于猜测** — 先验证再断言，禁止跳过步骤
- **提交** — 细粒度，每个 RED-GREEN-REFACTOR 循环后提交一次
- **自我评估** — 使用内联检查表自检，而非发起子代理 review 循环

## 项目结构

| 模块 | 路径 | 本地地址 | 生产地址 | 启动命令 |
|------|------|---------|----------|----------|
| 后端 API | `sidifensen_blog_backend/` | http://localhost:5000 | https://api.sidifensen.com | `mvn spring-boot:run` |
| 前端用户端 | `sidifensen_blog_frontend/sidifensen_user/` | http://localhost:7000 | https://sidifensen.com | `npm run dev` |
| 前端管理端 | `sidifensen_blog_frontend/sidifensen_admin/` | http://localhost:8000 | https://admin.sidifensen.com | `npm run dev` |
| 小程序 | `sidifensen_blog_miniprogram/` | http://localhost:9000 | 需配置合法域名 | `npm run dev:h5` |
| 文档 | `docs/` | - | - | - |

## 工具配置

| 工具 | 何时使用 | 用途 |
|------|---------|------|
| MySQL MCP | 需要新增表/字段、调试 SQL | 执行 DDL/DML、查看数据、调试业务逻辑 |
| SSH MCP | 服务器部署、查看日志、修改配置 | 远程连接生产服务器、文件传输 |
| Chrome DevTools MCP | 前端页面调试、性能分析 | 自动化浏览器操作、Network 分析 |

> **使用场景**: 小程序/前端问题 → Chrome DevTools；后端/SQL 问题 → MySQL MCP；服务器操作 → SSH MCP

## 后端规范

### 核心规则
- 依赖注入：`@Resource`（禁用 `@Autowired`）
- 实体注解：`@Data`, `@EqualsAndHashCode(callSuper = false)`, `@Accessors(chain = true)`
- 查询方式：`LambdaQueryWrapper`（禁用 XML）
- 异常处理：`BlogException` + `BlogConstants`（禁止硬编码）
- Service 模式：继承 `ServiceImpl<Mapper, Entity>`
- 日志：`@Slf4j` + `log.error`（只记录错误，禁用 `log.info`）
- 操作日志：管理端 Controller 必须添加 `@LogOperation` 注解
- 注释：新增/修改代码必须补充中文注释

### 命名规范
```
XxxController → 控制器
XxxService    → 服务接口
XxxServiceImpl → 服务实现
XxxMapper     → 数据访问
XxxDto        → 数据传输对象
XxxVo         → 视图对象
XxxEntity     → 实体类
```

### 修改后检查
```bash
cd sidifensen_blog_backend && mvn clean compile -DskipTests
```
- [ ] 编译通过
- [ ] 导入完整无冗余
- [ ] 无拼写/注解错误
- [ ] 已补充中文注释
- [ ] 管理端 Controller 已添加 `@LogOperation`

### 测试命令
```bash
cd sidifensen_blog_backend && mvn test
cd sidifensen_blog_frontend/sidifensen_user && npx vite-plugin-checker check
cd sidifensen_blog_frontend/sidifensen_admin && npx vite-plugin-checker check
```

> 注意：调试代码时不要自动重启后端服务

## 前端规范

### 代码规范
- 语法：`<script setup>` + Composition API
- 命名：PascalCase（`Home.vue`, `ArticleDetail.vue`）
- 表格：`el-table-column` 使用 `min-width` + `show-overflow-tooltip`
- SVG 图标：`<svg-icon name="github" width="20px" height="20px" color="#999" />`
- 注释：新增/修改代码必须补充中文注释

### 样式规范
1. 所有页面必须适配黑夜模式 + 移动端响应式
   - 使用 CSS 变量 + `html.dark` 覆盖
   - 禁止硬编码颜色值、只写浅色模式
   - ⚠️ 移动端常见问题：首页模块未固定，需用 `min-height` 或固定定位

2. SCSS 层次必须匹配 DOM 结构
   - 嵌套层次 100% 匹配 template DOM 结构
   - 不允许跳过中间层级
   - 每个样式模块必须有中文注释

3. 禁止假数据，所有功能必须真实实现
   - 必须 API 调用获取真实数据
   - 按钮点击必须实现真实业务功能
   - 表单提交必须调用真实 API 持久化

### 设计规范（去 AI 味）

核心原则：少即是多，克制比表达更重要

| 问题 | 反面 | 正确 |
|------|------|------|
| 渐变滥用 | `linear-gradient(135deg, ...)` | 纯色 |
| 多层阴影 | 3-4 层 `box-shadow` | 单层 `0 1px 3px` |
| 毛玻璃滥用 | 到处 `backdrop-filter: blur()` | 仅模态框 |
| 夸张悬停 | 位移+缩放+变色+阴影 | 只变阴影或颜色 |


### 颜色使用规范
- **必须先在全局定义再使用**：所有颜色必须定义在 `base.scss` 或对应模块的全局变量文件中，使用 CSS 变量引用
- **优先使用已定义颜色**：新增颜色前先检查现有变量，避免重复定义
- **禁止硬编码颜色**：组件内不允许直接写 `#xxx` 或 `rgb(x,x,x)`，必须用 `var(--xxx)` 引用全局变量

### 前端 API 调用
- 开发环境：前端通过 Vite proxy 转发到 `localhost:5000`
- 生产环境：前端请求 `https://api.sidifensen.com`
- API 路径规范：参考 `sidifensen_blog_backend/src/main/resources/config/application.yml`

### 修改后检查
```bash
cd sidifensen_blog_frontend/sidifensen_user && npm run build
cd sidifensen_blog_frontend/sidifensen_admin && npm run build
```
- [ ] 编译通过，无 JS/语法错误
- [ ] 页面正常加载，无白屏
- [ ] 黑夜模式正常
- [ ] 响应式布局正常（特别检查移动端）

## 新功能开发

**核心原则：优先沿用项目现有设计和规范**

添加新功能时，必须先参考现有类似页面（如 `Home.vue`、`Article/index.vue`）的代码结构、样式风格、API 调用模式。

**禁止**：
- 不看现有代码直接生成全新结构
- 引入项目外 UI 库
- 忽略黑夜模式适配


## 设计参考

优先沿用项目现有设计。`docs/awesome-design-md/` 目录下有 58 个真实产品的设计系统文档，查找方法：

| 需求风格 | 查找关键词 |
|---------|-----------|
| 简洁工具感 | linear, todoist, raycast |
| 文艺清新 | notion, superhuman, bear |
| 科技感强 | claude, vercel, supabase |
| 视觉冲击 | figma, framer, spotify |
| 企业级 | stripe, airbnb, apple |

常用网站直接访问：
- `linear.app/` - 简洁工具感
- `notion.so/` - 文艺清新
- `claude.ai/` - 科技感强

## 文档规范

**所有项目文档必须存放在 `docs/` 目录下，禁止在其他位置创建文档文件。**