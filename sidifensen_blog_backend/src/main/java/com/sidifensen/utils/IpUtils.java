package com.sidifensen.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.sidifensen.domain.entity.IpDetail;
import com.sidifensen.domain.result.IpResult;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 获取IP方法
 *
 * @author sidifensen
 */
@Component
public class IpUtils {

    private static final Logger log = LoggerFactory.getLogger(IpUtils.class);

    /**
     * 获取客户端IP
     *
     * @return IP地址
     */
    public String getIp() {
        HttpServletRequest request = WebUtils.getRequest();
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    private String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (!isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    private boolean isUnknown(String checkString) {
        return StrUtil.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    private static final String GET_IP_URL = "https://ip9.com.cn/get";
    private static final String GET_IP_URL1 = "https://ip9.com.cn/get?ip={}";

    // 发送请求获取IP地址
    public String getAddress() {
        String address;
        try {
            String res = HttpUtil.get(GET_IP_URL);
            IpResult ipResult = JSONUtil.toBean(res, IpResult.class);
            IpDetail ipDetail = JSONUtil.toBean(ipResult.getData(), IpDetail.class);
            if (ipDetail.getIsp().equals("内网地址") || ipDetail.getIsp().equals("回环地址") || ipDetail.getIsp().equals("本机网络")) {
                address = "内网地址";
            } else {
                address = ipDetail.getCountry() + "-" + ipDetail.getProv() + "-" + ipDetail.getCity() + "-" + ipDetail.getArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取IP地址失败");
        }
        return address;
    }

    // 发送请求获取IP地址
    public String getAddress(String ip) {
        String address;
        try {
            String res = HttpUtil.get(StrUtil.format(GET_IP_URL1, ip));
            IpResult ipResult = JSONUtil.toBean(res, IpResult.class);
            IpDetail ipDetail = JSONUtil.toBean(ipResult.getData(), IpDetail.class);
            if (ipDetail.getIsp().equals("内网地址") || ipDetail.getIsp().equals("回环地址") || ipDetail.getIsp().equals("本机网络")) {
                address = "内网地址";
            } else {
                address = ipDetail.getCountry() + "-" + ipDetail.getProv() + "-" + ipDetail.getCity() + "-" + ipDetail.getArea();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取IP地址失败");
        }
        return address;
    }

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(8);
        executor.execute(() -> {

            long startTime = System.currentTimeMillis();
            int requestCount = 100;
            System.out.println("=== 测试开始 ===");

            IpUtils ipUtils = new IpUtils();

            for (int i = 0; i < requestCount; i++) {
                String ipDetail = ipUtils.getAddress();
                System.out.println("Request " + (i + 1) + ": " + ipDetail);
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;

            System.out.println("\n=== 测试结果 ===");
            System.out.println("请求次数: " + requestCount);
            System.out.println("总耗时: " + totalTime + " ms");
            System.out.println("平均每次请求耗时: " + (totalTime / (double) requestCount) + " ms");
        });
        //=== 测试结果 ===
        //请求次数: 60
        //总耗时: 61098 ms
        //平均每次请求耗时: 1018.3 ms
        executor.shutdown();
        executor.awaitTermination(60, TimeUnit.SECONDS);
    }


}
