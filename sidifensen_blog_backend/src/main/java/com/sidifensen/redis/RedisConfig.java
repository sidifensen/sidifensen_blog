package com.sidifensen.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lettuce.core.ClientOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        return clientConfigurationBuilder -> {
            ClientOptions clientOptions = ClientOptions.builder()
                    .autoReconnect(true) // 启用自动重连功能，当Redis连接断开时会自动尝试重新连接
                    .pingBeforeActivateConnection(true) // 在激活连接前发送PING命令检测连接有效性
                    .build();
            clientConfigurationBuilder.clientOptions(clientOptions);
        };
    }

    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();

        // 使用普通 ObjectMapper，避免启用全局多态反序列化带来的安全风险
        ObjectMapper objectMapper = new ObjectMapper();
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        template.setConnectionFactory(factory);
        // 设置key的序列化方式为字符串类型
        template.setKeySerializer(new StringRedisSerializer());
        // 设置value的序列化方式为JSON格式
        template.setValueSerializer(serializer);
        // 设置hash的key的序列化方式为object
        template.setHashKeySerializer(serializer);
        // 设置hash的value的序列化方式为JSON格式
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet(); // 初始化RedisTemplate，确保所有的属性都被正确设置。
        return template;
    }
}
