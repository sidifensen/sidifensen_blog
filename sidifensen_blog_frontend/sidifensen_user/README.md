# sidifensen_blog_frontend

基于 Vue 3 + Vite 构建的个人博客前端项目，使用 [Vite+](https://vite.plus) 作为统一开发工具链。

## 常用命令

### npm 脚本命令

| 命令               | 说明                                            |
| ------------------ | ----------------------------------------------- |
| `npm run dev`      | 开发环境启动（等同于 `vp dev`）                 |
| `npm run build`    | 编译生产构建（等同于 `vp build`）               |
| `npm run check`    | 格式检查 + lint + 类型检查（等同于 `vp check`） |
| `npm run test`     | 运行测试（监视模式，等同于 `vp test`）          |
| `npm run test:run` | 单次运行测试                                    |
| `npm run preview`  | 预览生产构建（等同于 `vp preview`）             |

### 安装依赖

```sh
npm install
```

## Vite+ 工具命令

Vite+ 是项目的统一工具链，整合了 Vite、Vitest、Oxlint、Oxfmt、Rolldown 等工具。

### 开发命令

| 命令          | 说明                             |
| ------------- | -------------------------------- |
| `vp dev`      | 启动 Vite 开发服务器，支持热更新 |
| `vp build`    | 使用 Rolldown 构建生产版本       |
| `vp preview`  | 预览生产构建结果                 |
| `vp check`    | 运行格式检查、lint 和类型检查    |
| `vp test`     | 通过 Vitest 运行测试（监视模式） |
| `vp test run` | 单次运行测试                     |
| `vp fmt`      | 格式化代码                       |
| `vp lint`     | 检查代码规范                     |

### 包管理命令

| 命令              | 说明                         |
| ----------------- | ---------------------------- |
| `vp install`      | 安装依赖（自动检测包管理器） |
| `vp add <pkg>`    | 添加依赖包                   |
| `vp remove <pkg>` | 移除依赖包                   |
| `vp update`       | 更新依赖到最新版本           |
| `vp list`         | 列出已安装的包               |

### 其他命令

| 命令            | 说明                 |
| --------------- | -------------------- |
| `vp create`     | 创建新项目           |
| `vp migrate`    | 迁移现有项目到 Vite+ |
| `vp run <task>` | 执行 monorepo 任务   |
| `vp exec <cmd>` | 执行本地 bin 命令    |
| `vp upgrade`    | 更新 Vite+ 自身      |
| `vp help`       | 查看所有可用命令     |

## 推荐 IDE 配置

[VSCode](https://code.visualstudio.com/) + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar) (禁用 Vetur)。

## 项目配置

Vite+ 配置位于 `vite.config.ts`，详情见 [Vite 配置参考](https://vite.dev/config/)。
