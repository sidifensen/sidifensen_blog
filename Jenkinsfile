pipeline {
    agent any

    environment {
        DEFAULT_DEPLOY_PATH = '/opt/sidifensen_blog'
        JAVA_HOME = tool 'JDK-21'
        NODEJS_HOME = '/var/jenkins_home/tools'
        MAVEN_HOME = tool 'Maven-3'
        MAVEN_OPTS = '-Dmaven.repo.local=/var/jenkins_home/.m2/repository'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 90, unit: 'MINUTES')
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Prepare Toolchains') {
            steps {
                script {
                    // 检查 Java
                    def javaCheck = sh(script: '''
                        if [ -n "${JAVA_HOME}" ] && [ -x "${JAVA_HOME}/bin/java" ]; then
                            exit 0
                        fi
                        exit 1
                    ''', returnStatus: true)

                    if (javaCheck != 0) {
                        error 'JDK-21 不可用，请在 Jenkins 全局工具配置中正确配置 JDK-21。'
                    }

                    env.PATH = "${env.MAVEN_HOME}/bin:${env.NODEJS_HOME}/bin:${env.JAVA_HOME}/bin:${env.PATH}"

                    sh label: '检查工具链', script: '''
                        # 检查 libatomic 库
                        if ! ldconfig -p 2>/dev/null | grep -q libatomic.so.1 && ! find /usr/lib* /lib* -name "libatomic.so.1" 2>/dev/null | head -1 | grep -q .; then
                            echo "[WARN] 缺少 libatomic.so.1 库"
                            echo "[WARN] 安装: docker exec -u root jenkins apt-get install -y libatomic1"
                        fi

                        # 验证 Node.js
                        if ! node -v >/dev/null 2>&1; then
                            echo "[ERROR] Node.js 运行失败"
                            exit 1
                        fi

                        # 输出工具链版本
                        echo "--- 工具链版本 ---"
                        java -version 2>&1 | head -1
                        mvn -version 2>&1 | head -1
                        echo "Node: $(node -v)"
                        echo "npm: $(npm -v)"
                        echo "--------------------"
                    '''
                }
            }
        }

        stage('Checkout') {
            steps {
                echo '>>> 检出代码'
                script {
                    if (!env.DEPLOY_PATH) {
                        env.DEPLOY_PATH = env.DEFAULT_DEPLOY_PATH
                    }

                    def gitUrl = env.GIT_URL ?: scm.userRemoteConfigs[0].url
                    echo "仓库: ${gitUrl}"

                    def retryCount = 5
                    def checkoutSuccess = false
                    def attempt = 0

                    while (attempt < retryCount && !checkoutSuccess) {
                        attempt++
                        echo "克隆 [${attempt}/${retryCount}]..."

                        try {
                            checkout([
                                $class: 'GitSCM',
                                userRemoteConfigs: [[
                                    url: gitUrl,
                                    credentialsId: scm.userRemoteConfigs[0].credentialsId
                                ]],
                                branches: scm.branches,
                                extensions: [
                                    [$class: 'CloneOption', depth: 1, noTags: true, shallow: true]
                                ]
                            ])
                            checkoutSuccess = true
                        } catch (Exception e) {
                            if (attempt < retryCount) {
                                sleep(10)
                            } else {
                                error "Git 克隆失败"
                            }
                        }
                    }

                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.GIT_BRANCH_NAME = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                }
                echo "分支: ${env.GIT_BRANCH_NAME} | 提交: ${env.GIT_COMMIT_SHORT}"
            }
        }

        stage('Build Backend') {
            options {
                timeout(time: 45, unit: 'MINUTES')
            }
            steps {
                echo '>>> 构建后端'
                dir('sidifensen_blog_backend') {
                    sh label: 'Maven 构建', script: '''
                        echo "[1/3] Maven 打包..."
                        mvn -s ../script/prod/jenkins/maven-settings.xml -B -ntp clean package -DskipTests 2>&1 | grep -E "^(\\[INFO\\] Building|\\[ERROR\\]|\\[WARNING\\])" | head -20

                        echo "[2/3] 检查产物..."
                        JAR=$(ls target/*.jar 2>/dev/null | head -1)
                        if [ -z "$JAR" ]; then
                            echo "[ERROR] 未找到 jar 文件"
                            exit 1
                        fi

                        echo "[3/3] 产物: $(du -h $JAR | cut -f1)"
                    '''
                }
            }
            post {
                success { echo '<<< 后端构建成功' }
                failure { echo '<<< 后端构建失败' }
            }
        }

        stage('Build Frontends') {
            steps {
                script {
                    def adminBuild = {
                        dir('sidifensen_blog_frontend/sidifensen_admin') {
                            sh label: '构建管理端', script: '''
                                echo "[1/3] 安装依赖..."
                                npm ci --silent 2>/dev/null

                                echo "[2/3] 构建..."
                                npm run build 2>&1 | grep -vE "\\[[0-9;]+[a-zA-Z]" | tail -5

                                echo "[3/3] 大小: $(du -sh dist 2>/dev/null | cut -f1)"
                            '''
                        }
                    }

                    def userBuild = {
                        dir('sidifensen_blog_frontend/sidifensen_user') {
                            sh label: '构建用户端', script: '''
                                echo "[1/3] 安装依赖..."
                                npm ci --silent 2>/dev/null

                                echo "[2/3] 构建..."
                                npm run build 2>&1 | grep -vE "\\[[0-9;]+[a-zA-Z]" | tail -5

                                echo "[3/3] 大小: $(du -sh dist 2>/dev/null | cut -f1)"
                            '''
                        }
                    }

                    parallel(
                        'Admin': adminBuild,
                        'User': userBuild
                    )
                }
            }
            post {
                success { echo '<<< 前端构建成功' }
                failure { echo '<<< 前端构建失败' }
            }
        }

        stage('Deploy') {
            steps {
                echo '>>> 部署服务'
                script {
                    def deployPath = env.DEPLOY_PATH ?: env.DEFAULT_DEPLOY_PATH

                    sh label: '部署', script: """
                        export DEPLOY_PATH="${deployPath}"

                        # 检查部署目录
                        if ! mkdir -p \${DEPLOY_PATH}/sidifensen_blog_backend/target 2>/dev/null; then
                            echo "[ERROR] 无法访问部署目录"
                            exit 1
                        fi

                        # 复制后端
                        echo "[1/5] 复制后端..."
                        JAR=\$(ls sidifensen_blog_backend/target/*.jar 2>/dev/null | head -n 1)
                        if [ -n "\$JAR" ]; then
                            cp "\$JAR" \${DEPLOY_PATH}/sidifensen_blog_backend/target/
                        fi

                        # 复制前端
                        echo "[2/5] 复制前端..."
                        [ -d sidifensen_blog_frontend/sidifensen_admin/dist ] && cp -r sidifensen_blog_frontend/sidifensen_admin/dist \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_admin/
                        [ -d sidifensen_blog_frontend/sidifensen_user/dist ] && cp -r sidifensen_blog_frontend/sidifensen_user/dist \${DEPLOY_PATH}/sidifensen_blog_frontend/sidifensen_user/

                        # 复制脚本
                        echo "[3/5] 复制脚本..."
                        [ -d script/prod ] && cp -r script/prod/* \${DEPLOY_PATH}/script/prod/ 2>/dev/null || true

                        cd \${DEPLOY_PATH}

                        # 部署 - 使用 /tmp/docker-compose (已在 Jenkins 容器内)
                        echo "[4/5] 启动/更新容器..."
                        /tmp/docker-compose -f script/prod/docker-compose-ssl.yml --env-file script/prod/.env up -d --build

                        echo "[5/5] 等待启动..."
                        sleep 15

                        echo "--- 服务状态 ---"
                        /tmp/docker-compose -f script/prod/docker-compose-ssl.yml --env-file script/prod/.env ps 2>/dev/null | grep -v "^NAME" | head -10
                        echo "----------------"
                    """
                }
            }
            post {
                success { echo '<<< 部署成功' }
                failure { echo '<<< 部署失败' }
            }
        }
    }

    post {
        always {
            sh label: '清理', script: 'rm -f deploy-*.tar.gz 2>/dev/null || true'
        }
        success {
            echo '=========================================='
            echo '  构建成功 | 提交: ' + env.GIT_COMMIT_SHORT
            echo '=========================================='
        }
        failure {
            echo '=========================================='
            echo '  构建失败 | 请查看日志'
            echo '=========================================='
        }
    }
}
