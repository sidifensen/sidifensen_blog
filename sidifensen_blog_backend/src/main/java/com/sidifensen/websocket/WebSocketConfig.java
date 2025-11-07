package com.sidifensen.websocket;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 配置类
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Resource
    private PrivateMessageWebSocketHandler privateMessageWebSocketHandler;

    @Resource
    private WebSocketHandshakeInterceptor handshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(privateMessageWebSocketHandler, "/ws/message")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins("*"); // 生产环境需要配置具体域名
    }
}
