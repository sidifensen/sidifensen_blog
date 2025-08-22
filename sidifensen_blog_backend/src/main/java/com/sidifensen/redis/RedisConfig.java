package com.sidifensen.redis;


import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;
import io.lettuce.core.ClientOptions;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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

        FastJsonRedisSerializer fastJsonRedisSerializer = new FastJsonRedisSerializer(Object.class);

        template.setConnectionFactory(factory);
        // 设置key的序列化方式为字符串类型
        template.setKeySerializer(new StringRedisSerializer());
        // 设置value的序列化方式为JSON格式
        template.setValueSerializer(fastJsonRedisSerializer);
        // 设置hash的key的序列化方式为字符串
        template.setHashKeySerializer(new StringRedisSerializer());
        // 设置hash的value的序列化方式为JSON格式
        template.setHashValueSerializer(fastJsonRedisSerializer);

        template.afterPropertiesSet(); // 初始化RedisTemplate，确保所有的属性都被正确设置。
        return template;
    }
}