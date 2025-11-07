package com.sidifensen.websocket;

import cn.hutool.core.util.ObjectUtil;
import com.sidifensen.utils.JwtUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * WebSocket 握手拦截器
 * 用于在建立 WebSocket 连接前进行 token 验证
 */
@Component
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Resource
    private JwtUtils jwtUtils;

    /**
     * 握手前处理
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) {
        try {
            // 从请求参数中获取 token
            String query = request.getURI().getQuery();
            String token = extractToken(query);

            if (ObjectUtil.isEmpty(token)) {
                log.error("WebSocket 握手失败：token 为空");
                return false;
            }

            // 验证 token 并获取用户ID
            Integer userId = jwtUtils.parseToken(token);
            if (userId == null) {
                log.error("WebSocket 握手失败：token 无效或已过期");
                return false;
            }

            // 将 userId 存储到 WebSocket session 的 attributes 中
            attributes.put("userId", userId);
            return true;
        } catch (Exception e) {
            log.error("WebSocket 握手异常", e);
            return false;
        }
    }

    /**
     * 握手后处理
     */
    @Override
    public void afterHandshake(ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {
        // 握手后处理（可选）
    }

    /**
     * 提取 token
     */
    private String extractToken(String query) {
        if (ObjectUtil.isEmpty(query)) {
            return null;
        }
        String[] params = query.split("&");
        for (String param : params) {
            if (param.startsWith("token=")) {
                return param.substring(6);
            }
        }
        return null;
    }
}
