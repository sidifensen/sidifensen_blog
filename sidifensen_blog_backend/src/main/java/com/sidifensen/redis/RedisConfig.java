package com.sidifensen.redis;


import io.lettuce.core.ClientOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig<V> {
    
    private static final Logger logger = LoggerFactory.getLogger(RedisConfig.class);

    @Bean
    public LettuceClientConfigurationBuilderCustomizer lettuceClientConfigurationBuilderCustomizer() {
        logger.info("配置Lettuce客户端选项: 启用自动重连和连接前PING检测");

        return clientConfigurationBuilder -> {
            ClientOptions clientOptions = ClientOptions.builder()
                    .autoReconnect(true) // 启用自动重连功能，当Redis连接断开时会自动尝试重新连接
                    .pingBeforeActivateConnection(true) // 在激活连接前发送PING命令检测连接有效性
                    .build();
            clientConfigurationBuilder.clientOptions(clientOptions);

            logger.info("Lettuce客户端配置已应用: autoReconnect={}, pingBeforeActivateConnection={}",
                    clientOptions.isAutoReconnect(),
                    clientOptions.isPingBeforeActivateConnection());
        };
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, V> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, V> template = new RedisTemplate<>();

        template.setConnectionFactory(factory);
        // 设置key的序列化方式为字符串类型
        template.setKeySerializer(RedisSerializer.string());
        // 设置value的序列化方式为JSON格式
        template.setValueSerializer(RedisSerializer.json());
        // 设置hash的key的序列化方式为字符串
        template.setHashKeySerializer(RedisSerializer.string());
        // 设置hash的value的序列化方式为JSON格式
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet(); // 初始化RedisTemplate，确保所有的属性都被正确设置。
        return template;
    }
}