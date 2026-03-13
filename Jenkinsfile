pipeline {
    agent any
    
    environment {
        // 部署路径配置
        DEFAULT_DEPLOY_PATH = '/opt/sidifensen_blog'

        // 构建配置
        JAVA_HOME = tool 'JDK-21'
        NODEJS_HOME = tool 'NodeJS-20'
        MAVEN_HOME = tool 'Maven-3'
        // 显式指定 Maven 本地仓库，确保依赖缓存复用到 Jenkins 持久化目录
        MAVEN_OPTS = '-Dmaven.repo.local=/var/jenkins_home/.m2/repository'
    }
    
    options {
        // 保留最近 10 次构建
        buildDiscarder(logRotator(numToKeepStr: '10'))
        // 首次构建需要下载依赖，整体超时时间放宽
        timeout(time: 90, unit: 'MINUTES')
    }
    
    stages {
        stage('Prepare Toolchains') {
            steps {
                script {
                    // 确保 Java 可用
                    def javaCheckStatus = sh(
                        script: '''
                            if [ -n "${JAVA_HOME}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
                                exit 0
                            fi
                            exit 1
                        ''',
                        returnStatus: true
                    )

                    if (javaCheckStatus != 0) {
                        error 'JDK-21 不可用，请在 Jenkins 全局工具配置中正确配置 JDK-21。'
                    }

                    env.PATH = "${env.MAVEN_HOME}/bin:${env.NODEJS_HOME}/bin:${env.JAVA_HOME}/bin:${env.PATH}"

                    sh '''
                        echo "检查 Node.js 依赖库..."
                        # 检查 libatomic.so.1 是否存在
                        if ! ldconfig -p 2>/dev/null | grep -q libatomic.so.1 && ! find /usr/lib* /lib* -name "libatomic.so.1" 2>/dev/null | head -1 | grep -q .; then
                            echo "⚠️  警告: 缺少 libatomic.so.1 库，Node.js 可能无法运行"
                            echo ""
                            echo "📝 请在 Jenkins 容器中手动安装该库（只需安装一次）:"
                            echo ""
                            echo "  docker exec -u root jenkins apt-get update && docker exec -u root jenkins apt-get install -y libatomic1"
                            echo ""
                            echo "⚠️  继续执行，如果 Node.js 运行失败，请先安装上述库后重新运行构建"
                        else
                            echo "✅ libatomic.so.1 已存在"
                        fi
                        
                        echo ""
                        echo "当前工具链信息:"
                        echo "JAVA_HOME=${JAVA_HOME}"
                        echo "MAVEN_HOME=${MAVEN_HOME}"
                        echo "NODEJS_HOME=${NODEJS_HOME}"
                        java -version
                        mvn -version
                        
                        # 尝试运行 node，如果失败会给出明确错误
                        echo ""
                        echo "测试 Node.js..."
                        if node -v 2>&1; then
                            echo "✅ Node.js 运行正常"
                            npm -v
                        else
                            echo "❌ Node.js 运行失败，请执行以下命令安装依赖库:"
                            echo "  docker exec -u root jenkins apt-get update && docker exec -u root jenkins apt-get install -y libatomic1"
                            exit 1
                        fi
                    '''
                }
            }
        }

        stage('Checkout') {
            steps {
                echo '📥 检出代码...'
                script {
                    // 初始化部署路径
                    if (!env.DEPLOY_PATH) {
                        env.DEPLOY_PATH = env.DEFAULT_DEPLOY_PATH
                    }

                    // 支持从 Gitea 或 Git 仓库检出
                    def gitUrl = env.GIT_URL ?: scm.userRemoteConfigs[0].url
                    echo "仓库地址: ${gitUrl}"
                    
                    checkout scm
                    
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    env.GIT_BRANCH_NAME = sh(
                        script: 'git rev-parse --abbrev-ref HEAD',
                        returnStdout: true
                    ).trim()
                }
                echo "当前分支: ${env.GIT_BRANCH_NAME}"
                echo "提交哈希: ${env.GIT_COMMIT_SHORT}"
                echo "部署路径: ${env.DEPLOY_PATH}"
            }
        }
        
        stage('Build Backend') {
            options {
                // 后端首次下载 Maven 依赖可能较慢，单独放宽该阶段超时
                timeout(time: 45, unit: 'MINUTES')
            }
            steps {
                echo '📦 构建后端...'
                dir('sidifensen_blog_backend') {
                    sh '''
                        echo "使用 Maven 构建后端..."
                        mvn -s ../script/deploy/maven-settings.xml -B -ntp clean package -DskipTests
                        
                        # 检查构建产物
                        if [ ! -f target/*.jar ]; then
                            echo "错误: 未找到构建产物"
                            exit 1
                        fi
                        
                        echo "后端构建成功"
                        ls -lh target/*.jar
                    '''
                }
            }
            post {
                success {
                    echo '✅ 后端构建成功'
                }
                failure {
                    echo '❌ 后端构建失败'
                }
            }
        }
        
        stage('Build Frontend Admin') {
            steps {
                echo '📦 构建管理端前端...'
                dir('sidifensen_blog_frontend/sidifensen_admin') {
                    sh '''
                        echo "安装依赖..."
                        npm ci
                        
                        echo "构建前端..."
                        npm run build
                        
                        # 检查构建产物
                        if [ ! -d dist ]; then
                            echo "错误: 未找到构建产物 dist 目录"
                            exit 1
                        fi
                        
                        echo "管理端前端构建成功"
                        du -sh dist
                    '''
                }
            }
            post {
                success {
                    echo '✅ 管理端前端构建成功'
                }
                failure {
                    echo '❌ 管理端前端构建失败'
                }
            }
        }
        
        stage('Build Frontend User') {
            steps {
                echo '📦 构建用户端前端...'
                dir('sidifensen_blog_frontend/sidifensen_user') {
                    sh '''
                        echo "安装依赖..."
                        npm ci
                        
                        echo "构建前端..."
                        npm run build
                        
                        # 检查构建产物
                        if [ ! -d dist ]; then
                            echo "错误: 未找到构建产物 dist 目录"
                            exit 1
                        fi
                        
                        echo "用户端前端构建成功"
                        du -sh dist
                    '''
                }
            }
            post {
                success {
                    echo '✅ 用户端前端构建成功'
                }
                failure {
                    echo '❌ 用户端前端构建失败'
                }
            }
        }
        
        
        stage('Deploy to Server') {
            steps {
                echo '🚀 部署到服务器...'
                script {
                    def deployPath = env.DEPLOY_PATH ?: env.DEFAULT_DEPLOY_PATH
                    
                    sh """
                        set -e
                        export DEPLOY_PATH="${deployPath}"
                        
                        echo "准备部署目录..."
                        # 尝试创建子目录来验证目录是否可访问
                        # 如果目录已挂载到容器中，mkdir -p 会成功；如果未挂载，会失败并给出明确错误
                        if ! mkdir -p \${DEPLOY_PATH}/sidifensen_blog_backend/target 2>/dev/null; then
                            echo "❌ 错误: 无法访问部署目录 \${DEPLOY_PATH}"
                            echo "提示: Jenkins 容器可能未挂载该目录"
                            echo "解决方案: 确保在启动 Jenkins 容器时挂载该目录，例如:"
                            echo "  docker run -v /opt/sidifensen_blog:/opt/sidifensen_blog ..."
                            exit 1
                        fi
                        
                        # 确保其他子目录存在
                        mkdir -p \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_admin
                        mkdir -p \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_user
                        mkdir -p \${DEPLOY_PATH}/script/ssl
                        
                        echo "✅ 部署目录可访问: \${DEPLOY_PATH}"
                        
                        echo "复制后端 jar 包..."
                        # 复制后端 jar 包到部署目录
                        JAR_FILE=\$(ls sidifensen_blog_backend/target/*.jar 2>/dev/null | head -n 1)
                        if [ -n "\${JAR_FILE}" ] && [ -f "\${JAR_FILE}" ]; then
                            cp "\${JAR_FILE}" \${DEPLOY_PATH}/sidifensen_blog_backend/target/
                            echo "✅ 后端 jar 包复制成功"
                            ls -lh \${DEPLOY_PATH}/sidifensen_blog_backend/target/*.jar
                        else
                            echo "❌ 错误: 未找到后端 jar 包"
                            exit 1
                        fi
                        
                        echo "复制前端 dist 目录..."
                        # 复制管理端前端
                        if [ -d sidifensen_blog_frontend/sidifensen_admin/dist ]; then
                            rm -rf \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_admin/dist
                            cp -r sidifensen_blog_frontend/sidifensen_admin/dist \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_admin/
                            echo "✅ 管理端前端复制成功"
                        else
                            echo "❌ 错误: 未找到管理端前端 dist 目录"
                            exit 1
                        fi
                        
                        # 复制用户端前端
                        if [ -d sidifensen_blog_frontend/sidifensen_user/dist ]; then
                            rm -rf \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_user/dist
                            cp -r sidifensen_blog_frontend/sidifensen_user/dist \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_user/
                            echo "✅ 用户端前端复制成功"
                        else
                            echo "❌ 错误: 未找到用户端前端 dist 目录"
                            exit 1
                        fi
                        
                        echo "复制部署脚本和配置文件..."
                        # 复制 script/ssl 目录下的文件（如果存在）
                        if [ -d script/ssl ]; then
                            cp -r script/ssl/* \${DEPLOY_PATH}/script/ssl/ 2>/dev/null || true
                            echo "✅ 部署脚本复制成功"
                        fi
                        
                        echo "检查 .env 文件..."
                        cd \${DEPLOY_PATH}/script/ssl
                        if [ ! -f .env ]; then
                            echo "⚠️  警告: .env 文件不存在，将从 env.example 复制..."
                            if [ -f env.example ]; then
                                cp env.example .env
                                echo "⚠️  请手动编辑 .env 文件并配置正确的环境变量！"
                            else
                                echo "⚠️  警告: env.example 文件也不存在，请手动创建 .env 文件！"
                            fi
                        fi
                        
                        # 设置 PROJECT_ROOT 环境变量
                        export PROJECT_ROOT=\${DEPLOY_PATH}
                        
                        # 检测可用的 docker-compose 命令
                        if docker compose version >/dev/null 2>&1; then
                            DOCKER_COMPOSE_CMD="docker compose"
                            echo "✅ 使用 Docker Compose V2 (docker compose)"
                        elif docker-compose version >/dev/null 2>&1; then
                            DOCKER_COMPOSE_CMD="docker-compose"
                            echo "✅ 使用 Docker Compose V1 (docker-compose)"
                        else
                            echo "❌ 错误: 未找到 docker-compose 或 docker compose 命令"
                            echo ""
                            echo "解决方案: 请在 Jenkins 容器中安装 Docker Compose"
                            echo "执行以下命令（在宿主机上，以 root 权限）:"
                            echo ""
                            echo "  # 进入 Jenkins 容器"
                            echo "  docker exec -it -u root jenkins bash"
                            echo ""
                            echo "  # 安装 Docker Compose V2（推荐，如果 Docker 版本 >= 20.10）"
                            echo "  # Docker Compose V2 通常已包含在 Docker 中，检查: docker compose version"
                            echo ""
                            echo "  # 或者安装 Docker Compose V1（使用国内镜像）"
                            echo "  ARCH=\$(uname -m | sed 's/x86_64/amd64/;s/aarch64/arm64/')"
                            echo "  OS=\$(uname -s | tr '[:upper:]' '[:lower:]')"
                            echo "  # 方法1: 使用 gitee 镜像（推荐）"
                            echo "  curl -L \"https://gitee.com/mirrors/docker-compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /usr/local/bin/docker-compose"
                            echo "  # 方法2: 使用 daocloud 镜像"
                            echo "  # curl -L \"https://get.daocloud.io/docker/compose/releases/download/v2.24.5/docker-compose-\${OS}-\${ARCH}\" -o /usr/local/bin/docker-compose"
                            echo "  chmod +x /usr/local/bin/docker-compose"
                            echo ""
                            exit 1
                        fi
                        
                        echo "停止旧容器..."
                        # 使用 SSL 配置停止旧容器
                        \${DOCKER_COMPOSE_CMD} -f docker-compose-ssl.yml --env-file .env down || echo "容器未运行，跳过停止步骤"
                        
                        echo "启动新容器..."
                        # 使用 SSL 配置的 docker-compose 文件，并指定 .env 文件
                        # PROJECT_ROOT 环境变量会传递给 docker-compose
                        \${DOCKER_COMPOSE_CMD} -f docker-compose-ssl.yml --env-file .env up -d --build
                        
                        echo "等待服务启动..."
                        sleep 15
                        
                        echo "检查服务状态..."
                        \${DOCKER_COMPOSE_CMD} -f docker-compose-ssl.yml --env-file .env ps
                        
                        echo "✅ 部署完成！"
                    """
                }
            }
            post {
                success {
                    echo '✅ 部署成功'
                }
                failure {
                    echo '❌ 部署失败'
                }
            }
        }
    }
    
    post {
        always {
            echo '🧹 清理工作空间...'
            sh 'rm -f deploy-*.tar.gz || true'
        }
        success {
            echo '🎉 构建和部署成功完成！'
            // 可以在这里添加通知，如邮件、钉钉、企业微信等
        }
        failure {
            echo '💥 构建或部署失败！'
            // 可以在这里添加失败通知
        }
        unstable {
            echo '⚠️ 构建不稳定'
        }
    }
}

