pipeline {
    agent any

    environment {
        DEFAULT_DEPLOY_PATH = '/opt/sidifensen_blog'
        JAVA_HOME = tool 'JDK-21'
        NODEJS_HOME = '/var/jenkins_home/tools/nodejs'
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
                        if ! ldconfig -p 2>/dev/null | grep -q libatomic.so.1 && ! find /usr/lib* /lib* -name "libatomic.so.1" 2>/dev/null | grep -q .; then
                            echo "[WARN] 缺少 libatomic.so.1 库，可能导致 JVM 崩溃"
                        fi

                        # 验证工具链
                        java -version 2>&1 | head -1
                        mvn -version 2>&1 | head -1
                        echo "Node: $(node -v)"
                        echo "npm: $(npm -v)"
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

                    // 获取提交信息（checkout 已在 SCM 阶段完成）
                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.GIT_BRANCH_NAME = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    env.WORKSPACE = sh(script: 'pwd', returnStdout: true).trim()
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
                        mvn -s ../script/prod/jenkins/maven-settings.xml -B -ntp clean package -DskipTests -q

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
                    // 串行构建，避免并发导致的 sass 原生模块问题
                    dir('sidifensen_blog_frontend/sidifensen_admin') {
                        sh label: '构建管理端', script: '''
                            echo "[0/4] 清理旧文件..."
                            rm -rf node_modules dist 2>/dev/null || true

                            echo "[1/4] 安装依赖..."
                            npm install

                            echo "[2/4] 重建原生模块..."
                            npm rebuild sass-embedded 2>/dev/null || true

                            echo "[3/4] 检查原生模块..."
                            npm install @voidzero-dev/vite-plus-linux-x64-gnu @rollup/rollup-linux-x64-gnu sass-embedded-linux-x64 lightningcss-linux-x64-gnu --save-optional 2>/dev/null || true

                            echo "[3/4] 构建..."
                            npm run build

                            echo "[4/4] 大小: $(du -sh dist 2>/dev/null | cut -f1)"
                        '''
                    }

                    dir('sidifensen_blog_frontend/sidifensen_user') {
                        sh label: '构建用户端', script: '''
                            echo "[0/4] 清理旧文件..."
                            rm -rf node_modules dist 2>/dev/null || true

                            echo "[1/4] 安装依赖..."
                            npm install

                            echo "[2/4] 重建原生模块..."
                            npm rebuild sass-embedded 2>/dev/null || true

                            echo "[3/4] 检查原生模块..."
                            npm install @voidzero-dev/vite-plus-linux-x64-gnu @rollup/rollup-linux-x64-gnu sass-embedded-linux-x64 lightningcss-linux-x64-gnu --save-optional 2>/dev/null || true

                            echo "[3/4] 构建..."
                            npm run build

                            echo "[4/4] 大小: $(du -sh dist 2>/dev/null | cut -f1)"
                        '''
                    }
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
                    def workspace = env.WORKSPACE ?: '/var/jenkins_home/workspace/sidifensen-blog-deploy'

                    sh label: '部署', script: """
                        set -e
                        cd ${workspace}

                        echo "[INFO] 部署配置：Jenkins 容器无 docker CLI"
                        echo "[INFO] 构建产物位置："
                        echo "  - 后端: \$(pwd)/sidifensen_blog_backend/target/sidifensen_blog_backend-1.0-SNAPSHOT.jar"
                        echo "  - 管理端: \$(pwd)/sidifensen_blog_frontend/sidifensen_admin/dist/"
                        echo "  - 用户端: \$(pwd)/sidifensen_blog_frontend/sidifensen_user/dist/"
                        echo ""
                        echo "[INFO] 手动部署命令（需在主机执行）："
                        echo "  docker cp \${workspace}/sidifensen_blog_backend/target/sidifensen_blog_backend-1.0-SNAPSHOT.jar sidifensen-backend:/app/sidifensen_blog_backend/target/"
                        echo "  docker cp \${workspace}/sidifensen_blog_frontend/sidifensen_admin/dist/. sidifensen-admin:/usr/share/nginx/html/admin/"
                        echo "  docker cp \${workspace}/sidifensen_blog_frontend/sidifensen_user/dist/. sidifensen-user:/usr/share/nginx/html/"
                        echo "  docker restart sidifensen-backend sidifensen-admin sidifensen-user"
                    """
                }
            }
            post {
                success { echo '<<< 构建成功（部署需手动执行）' }
                failure { echo '<<< 构建失败' }
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
