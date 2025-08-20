package com.sidifensen.utils;

import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.domain.constants.BlogConstants;
import com.sidifensen.exception.BlogException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * Web 工具类
 * 用于给前端返回不同状态码和信息
 */
public class WebUtils {

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

    /**
     * 获取当前的请求地址
     */
    public static String getRequestUrl() {
        String requestURI = (((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest()).getRequestURI();
        if (ObjectUtil.isNotEmpty(requestURI)){
            return requestURI;
        }
        return "unknown";
    }

    /**
     * 获取request
     */
    public static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (ObjectUtil.isNotEmpty(requestAttributes)) {
            return requestAttributes.getRequest();
        }
        throw new BlogException(BlogConstants.CannotGetRequestInfo);
    }
}
