package com.sidifensen.utils;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Web 工具类
 * 用于给前端返回不同状态码和信息
 */
public class WebUtil {

    /**
     * 成功
     * @param response 响应对象
     * @param msg      成功信息
     * @return null
     */
    public static void success(HttpServletResponse response, String msg) {
        try {
            response.setStatus(200);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            PrintWriter writer = response.getWriter();
            writer.println(msg);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 未授权
     * @param response 响应对象
     * @param msg      错误信息
     * @return null
     */
    public static void Unauthorized(HttpServletResponse response, String msg) {
        try {
            response.setStatus(401);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            PrintWriter writer = response.getWriter();
            writer.println(msg);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
