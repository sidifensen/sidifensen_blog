# Sidifensen Blog 开发指南

## 项目结构

| 模块 | 路径 | 本地地址 | 生产地址 | 启动命令 |
|------|------|---------|----------|----------|
| 后端 API | `sidifensen_blog_backend/` | http://localhost:5000 | https://api.sidifensen.com | `mvn spring-boot:run` |
| 前端用户端 | `sidifensen_blog_frontend/sidifensen_user/` | http://localhost:7000 | https://sidifensen.com | `npm run dev` |
| 前端管理端 | `sidifensen_blog_frontend/sidifensen_admin/` | http://localhost:8000 | https://admin.sidifensen.com | `npm run dev` |
| 小程序 | `sidifensen_blog_miniprogram/` | http://localhost:9000 | 需配置合法域名 | `npm run dev:h5` |
| 文档 | `docs/` | - | - | - |

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

> 注意：调试代码时不要自动重启后端服务

## 前端规范

### 三大铁律

**规则 1：所有页面必须适配黑夜模式 + 移动端响应式**
- 必须使用 CSS 变量 + `html.dark` 覆盖
- 禁止硬编码颜色值、只写浅色模式
- ⚠️ 移动端常见问题：首页模块未固定，需用 `min-height` 或固定定位

**规则 2：SCSS 层次必须匹配 DOM 结构**
- 嵌套层次 100% 匹配 template DOM 结构
- 不允许跳过中间层级
- 每个样式模块必须有中文注释

**规则 3：禁止假数据，所有功能必须真实实现**
- 必须 API 调用获取真实数据
- 按钮点击必须实现真实业务功能
- 表单提交必须调用真实 API 持久化

### Vue 组件规范
- 语法：`<script setup>` + Composition API
- 命名：PascalCase（`Home.vue`, `ArticleDetail.vue`）
- 表格：`el-table-column` 使用 `min-width` + `show-overflow-tooltip`
- SVG 图标：`<svg-icon name="github" width="20px" height="20px" color="#999" />`
- 注释：新增/修改代码必须补充中文注释

### 修改后检查
```bash
cd sidifensen_blog_frontend/sidifensen_user && npm run build
cd sidifensen_blog_frontend/sidifensen_admin && npm run build
```
- [ ] 编译通过，无 JS/语法错误
- [ ] 页面正常加载，无白屏
- [ ] 黑夜模式正常
- [ ] 响应式布局正常（特别检查移动端）

## 设计规范（去 AI 味）

核心原则：少即是多，克制比表达更重要

| 问题 | 反面 | 正确 |
|------|------|------|
| 渐变滥用 | `linear-gradient(135deg, ...)` | 纯色 |
| 多层阴影 | 3-4 层 `box-shadow` | 单层 `0 1px 3px` |
| 毛玻璃滥用 | 到处 `backdrop-filter: blur()` | 仅模态框 |
| 夸张悬停 | 位移+缩放+变色+阴影 | 只变阴影或颜色 |

### 标准色板
```scss
--bg-page: #f8fafc;      // 浅色 / #0f172a 深色
--bg-card: #ffffff;      // 浅色 / #1e293b 深色
--text-primary: #1e293b;  // 浅色 / #f1f5f9 深色
--text-regular: #475569; // 浅色 / #cbd5e1 深色
--text-muted: #64748b;   // 浅色 / #94a3b8 深色
--border: #e2e8f0;       // 浅色 / #334155 深色
```

## 新功能开发

**核心原则：优先沿用项目现有设计和规范**

添加新功能时，必须先参考现有类似页面（如 `Home.vue`、`Article/index.vue`）的代码结构、样式风格、API 调用模式。

**禁止**：
- 不看现有代码直接生成全新结构
- 引入项目外 UI 库
- 忽略黑夜模式适配

## 工具配置

| 工具 | 用途 |
|------|------|
| MySQL MCP | 新增表/字段、调试业务逻辑 |
| chrome-devtools MCP | 页面调试、元素检查、控制台日志 |
| gstack `/browse` | 网页浏览（禁止使用 `mcp__claude-in-chrome__*`） |

> gstack 技能失效时：运行 `cd .claude/skills/gstack && ./setup`

## 文档规范

**所有项目文档必须存放在 `docs/` 目录下，禁止在其他位置创建文档文件。**
