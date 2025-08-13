package com.sidifensen;

import com.sidifensen.utils.IpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
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
                    System.out.println("任务 " + finalI + " 运行在 " + Thread.currentThread());
                    TimeUnit.SECONDS.sleep(3);//模拟阻塞
                    // 执行其他操作
                    return "ok";
                });
            }
        }

    }

    // 一百万条数据
    @Test
    public void test2() {
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 1000000; i++) {
            int finalI = i;
            es.submit(() -> {
                System.out.println("任务 " + finalI + " 运行在 " + Thread.currentThread());
                Thread.sleep(1000);
                return 0;
            });
        }
        es.close();
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime) + "ms");
        //总耗时：20013ms
    }

    @Test
    public void test3() {
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newVirtualThreadPerTaskExecutor();
        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                Thread.sleep(1000);
                return 0;
            });
        }
        es.close();
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime) + "ms");
        //总耗时：1016ms
    }

    @Test
    public void test4() {
        long startTime = System.currentTimeMillis();
        ExecutorService es = Executors.newFixedThreadPool(16);
        for (int i = 0; i < 100; i++) {
            es.submit(() -> {
                Thread.sleep(1000);
                return 0;
            });
        }
        es.close();
        long endTime = System.currentTimeMillis();
        System.out.println("总耗时：" + (endTime - startTime) + "ms");
        // 总耗时：7040ms
    }

    @Test
    public void test5(){
        long startTime = System.currentTimeMillis();
        int requestCount = 60;
        System.out.println("=== 测试开始 ===");

        // 使用虚拟线程池并行执行请求
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < requestCount; i++) {
                final int requestId = i + 1;
                executor.submit(() -> {
                    try {
                        IpUtils ipUtils = new IpUtils();
                        String ipDetail = ipUtils.getAddress("120.231.54.196");
                        System.out.println("Request " + requestId + ": " + ipDetail);
                    } catch (Exception e) {
                        System.err.println("Request " + requestId + " failed: " + e.getMessage());
                    }
                });
            }
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("\n=== 测试结果 ===");
        System.out.println("请求次数: " + requestCount);
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println("平均每次请求耗时: " + (totalTime / (double) requestCount) + " ms");
    }

    @Test
    public void comparePerformance() throws InterruptedException {
        int requestCount = 100;

        // 虚拟线程测试
        long virtualThreadTime = executeWithVirtualThreads(requestCount);

        // 传统线程池测试
        long fixedThreadPoolTime = executeWithFixedThreadPool(requestCount);

        System.out.println("=== 性能对比 ===");
        System.out.println("虚拟线程耗时: " + virtualThreadTime + " ms");
        System.out.println("固定线程池耗时: " + fixedThreadPoolTime + " ms");
    }

    private long executeWithVirtualThreads(int requestCount) {
        long startTime = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < requestCount; i++) {
                final int requestId = i + 1;
                executor.submit(() -> {
                    try {
                        IpUtils ipUtils = new IpUtils();
                        String ipDetail = ipUtils.getAddress("120.231.54.196");
                        System.out.println("Virtual Thread Request " + requestId + ": " + ipDetail);
                    } catch (Exception e) {
                        System.err.println("Request " + requestId + " failed: " + e.getMessage());
                    }
                });
            }
        }

        return System.currentTimeMillis() - startTime;
    }

    private long executeWithFixedThreadPool(int requestCount) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(16);
        for (int i = 0; i < requestCount; i++) {
            final int requestId = i + 1;
            executor.submit(() -> {
                try {
                    IpUtils ipUtils = new IpUtils();
                    String ipDetail = ipUtils.getAddress("120.231.54.196");
                    System.out.println("Fixed Thread Request " + requestId + ": " + ipDetail);
                } catch (Exception e) {
                    System.err.println("Request " + requestId + " failed: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        return System.currentTimeMillis() - startTime;
    }

    @Test
    public void testMillionVirtualThreads() {
        long startTime = System.currentTimeMillis();
        int requestCount = 10000; // 测试更大规模

        try (ExecutorService executor = Executors.newFixedThreadPool(16)) {
            for (int i = 0; i < requestCount; i++) {
                final int requestId = i + 1;
                executor.submit(() -> {
                    // 模拟简单的计算任务而非网络请求
                    try {
                        Thread.sleep(100); // 短暂延迟
                        System.out.println("Task " + requestId + " completed on " + Thread.currentThread());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("百万虚拟线程测试完成，耗时: " + totalTime + " ms");//百万虚拟线程测试完成，耗时: 235 ms  百万固定线程测试完成，耗时: 67311 ms
    }

    @Test
    public void databaseConcurrencyTest() throws InterruptedException {
        int taskCount = 1000;
        int threadPoolSize = 20; // 模拟数据库连接池大小

        // 虚拟线程测试
        long virtualThreadTime = executeDbOperationsWithVirtualThreads(taskCount, threadPoolSize);

        // 传统线程池测试
        long fixedThreadPoolTime = executeDbOperationsWithFixedThreadPool(taskCount, threadPoolSize);

        System.out.println("=== 数据库并发性能对比 ===");
        System.out.println("任务数量: " + taskCount);
        System.out.println("模拟连接池大小: " + threadPoolSize);
        System.out.println("虚拟线程耗时: " + virtualThreadTime + " ms");
        System.out.println("固定线程池耗时: " + fixedThreadPoolTime + " ms");
        //=== 数据库并发性能对比 ===
        //任务数量: 1000
        //模拟连接池大小: 20
        //虚拟线程耗时: 3100 ms
        //固定线程池耗时: 3053 ms
    }

    private long executeDbOperationsWithVirtualThreads(int taskCount, int maxConcurrency) {
        long startTime = System.currentTimeMillis();

        // 使用Semaphore限制并发数，模拟数据库连接池限制
        Semaphore semaphore = new Semaphore(maxConcurrency);

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < taskCount; i++) {
                final int taskId = i;
                executor.submit(() -> {
                    try {
                        semaphore.acquire(); // 获取"数据库连接"
                        try {
                            // 模拟数据库操作（受连接池限制）
                            Thread.sleep(50); // 模拟数据库操作时间
                            System.out.println("Task " + taskId + " completed");
                        } finally {
                            semaphore.release(); // 释放"数据库连接"
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
            }
        }

        return System.currentTimeMillis() - startTime;
    }

    private long executeDbOperationsWithFixedThreadPool(int taskCount, int maxConcurrency) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        Semaphore semaphore = new Semaphore(maxConcurrency);
        ExecutorService executor = Executors.newFixedThreadPool(maxConcurrency);

        for (int i = 0; i < taskCount; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    semaphore.acquire();
                    try {
                        // 模拟数据库操作
                        Thread.sleep(50);
                        System.out.println("Task " + taskId + " completed");
                    } finally {
                        semaphore.release();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);

        return System.currentTimeMillis() - startTime;
    }




}
