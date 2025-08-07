package com.sidifensen;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author sidifensen
 * @since 2025-08-05
 */
@SpringBootTest
public class ExecutorsTest {


    @Test
    public void test() {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1000000; i++) {
                int finalI = i;
                var future = executor.submit(() -> {
                    System.out.println("任务 " + finalI +" 运行在 " + Thread.currentThread());
                    TimeUnit.SECONDS.sleep(3);//模拟阻塞
                    // 执行其他操作
                    return "ok";
                });
            }
        }

    }

}
