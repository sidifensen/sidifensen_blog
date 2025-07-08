package com.sidifensen.redis;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig<V> {

    @Bean("redisTemplate")//这个方法返回的RedisTemplate实例会被注册到Spring容器中，并且Bean的名字是redisTemplate。
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

        template.afterPropertiesSet();//初始化RedisTemplate，确保所有的属性都被正确设置。
        return template;
    }
}
