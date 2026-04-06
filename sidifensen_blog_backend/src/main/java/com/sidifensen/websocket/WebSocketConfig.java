package com.sidifensen.websocket;

import com.sidifensen.config.SidifensenConfig;
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

    @Resource
    private SidifensenConfig sidifensenConfig;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(privateMessageWebSocketHandler, "/ws/message")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins(sidifensenConfig.getAllowOrigins().toArray(new String[0]));
    }
}
