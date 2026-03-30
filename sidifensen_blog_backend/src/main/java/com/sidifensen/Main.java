package com.sidifensen;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 博客后端启动类
 *
 * @author sidifensen
 */
@SpringBootApplication(exclude = {
        org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration.class
})
@MapperScan("com.sidifensen.mapper")
@EnableAspectJAutoProxy
@EnableScheduling // 启用定时任务
@Slf4j
public class Main {
    public static void main(String[] args) {
        // 设置JVM文件编码为UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");

        // 加载 .env 文件到系统环境变量
        loadDotenv();

        SpringApplication app = new SpringApplication(Main.class);

        // 设置自定义 Banner
        app.setBanner((environment, sourceClass, out) -> out.println(
                "\u001B[36m" + // 设置蓝色前景色（Blue）
                        "        _       _   _    __                                     \n" +
                        "  ___  (_)   __| | (_)  / _|   ___   _ __    ___    ___   _ __  \n" +
                        " / __| | |  / _` | | | | |_   / _ \\ | '_ \\  / __|  / _ \\ | '_ \\ \n" +
                        " \\__ \\ | | | (_| | | | |  _| |  __/ | | | | \\__ \\ |  __/ | | | |\n" +
                        " |___/ |_|  \\__,_| |_| |_|    \\___| |_| |_| |___/  \\___| |_| |_|\n" +
                        "\u001B[0m" // 重置颜色
        ));

        app.run(args);

        // 添加项目启动成功提示
        log.info("\u001B[32m" + "项目启动成功!" + "\u001B[0m"); // 绿色字体显示
    }

    /**
     * 加载 .env 文件，将变量注入系统属性
     * Spring Boot 的 @Value 或 ${} 占位符会优先读取系统属性
     */
    private static void loadDotenv() {
        Path envFile = Path.of(".env");
        if (!Files.exists(envFile)) {
            log.warn(".env 文件不存在，跳过加载");
            return;
        }
        try {
            Files.lines(envFile)
                    .filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
                    .map(line -> line.split("=", 2))
                    .filter(parts -> parts.length == 2)
                    .forEach(parts -> {
                        String key = parts[0].trim();
                        String value = parts[1].trim();
                        // 仅当未设置时才注入，避免覆盖命令行参数
                        if (System.getProperty(key) == null) {
                            System.setProperty(key, value);
                        }
                    });
            log.info(".env 文件已加载");
        } catch (IOException e) {
            log.error("加载 .env 文件失败", e);
        }
    }
}
