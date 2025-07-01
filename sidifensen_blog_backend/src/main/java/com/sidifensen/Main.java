package com.sidifensen;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sidifensen.mapper")
public class Main {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Main.class);

        // 设置自定义 Banner
        app.setBanner((environment, sourceClass, out) -> out.println(
                "\u001B[36m" + // 设置蓝色前景色（Blue）
                        "        _       _   _    __                                     \n" +
                        "  ___  (_)   __| | (_)  / _|   ___   _ __    ___    ___   _ __  \n" +
                        " / __| | |  / _` | | | | |_   / _ \\ | '_ \\  / __|  / _ \\ | '_ \\ \n" +
                        " \\__ \\ | | | (_| | | | |  _| |  __/ | | | | \\__ \\ |  __/ | | | |\n" +
                        " |___/ |_|  \\__,_| |_| |_|    \\___| |_| |_| |___/  \\___| |_| |_|\n" +
                        "\u001B[0m"   // 重置颜色
        ));

        app.run(args);
    }
}
