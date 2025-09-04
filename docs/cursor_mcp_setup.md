# Cursor MCP配置指南

## 概述
已成功为Cursor配置了GitHub和Gitee的MCP（Model Context Protocol）连接。这将允许Cursor AI助手直接与GitHub和Gitee仓库进行交互。

## 配置详情

### 1. GitHub MCP配置
- **服务名称**: github
- **命令**: npx -y @modelcontextprotocol/server-github
- **环境变量**: GITHUB_PERSONAL_ACCESS_TOKEN

### 2. Gitee MCP配置
- **服务名称**: gitee
- **命令**: node path/to/gitee-mcp-server.js
- **环境变量**: GITEE_ACCESS_TOKEN

## 后续配置步骤

### 获取GitHub Personal Access Token
1. 登录GitHub账户
2. 前往 Settings → Developer settings → Personal access tokens → Tokens (classic)
3. 点击 "Generate new token (classic)"
4. 选择所需权限：
   - `repo` - 完整的仓库访问权限
   - `user` - 用户信息访问权限
   - `gist` - Gist访问权限
5. 复制生成的token
6. 在Cursor设置中将 `your_github_token_here` 替换为实际的token

### 获取Gitee Access Token
1. 登录Gitee账户
2. 前往 设置 → 私人令牌
3. 点击 "生成新令牌"
4. 选择所需权限：
   - `projects` - 仓库访问权限
   - `user_info` - 用户信息权限
5. 复制生成的token
6. 在Cursor设置中将 `your_gitee_token_here` 替换为实际的token

### Gitee MCP服务器设置
由于Gitee没有官方的MCP服务器，你需要：

1. **选项1：使用通用Git MCP服务器**
   ```json
   "gitee": {
     "command": "npx",
     "args": ["-y", "@modelcontextprotocol/server-git"],
     "env": {
       "GITEE_ACCESS_TOKEN": "your_gitee_token_here"
     }
   }
   ```

2. **选项2：创建自定义Gitee MCP服务器**
   - 基于GitHub MCP服务器源码修改
   - 适配Gitee API接口
   - 部署为独立的Node.js服务

## 验证配置
1. 重启Cursor
2. 在聊天中询问关于GitHub仓库的信息
3. 测试是否能够访问和操作仓库

## 注意事项
- 请妥善保管你的访问令牌，不要提交到代码仓库
- 定期轮换访问令牌以确保安全
- 根据需要调整令牌权限范围

## 故障排除
如果MCP连接失败：
1. 检查令牌是否正确配置
2. 验证令牌权限是否足够
3. 检查网络连接
4. 查看Cursor日志获取详细错误信息
