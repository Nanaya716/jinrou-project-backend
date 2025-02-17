package com.jinrou.jinrouwerewolf.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @Author: nanaya
 * @Date: 2024/07/13/15:53
 * @Email: qiyewuyin@gmail.com\714991699@qq.com
 * @QQ: 714991699
 * @Description:
 */
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 使用 StringRedisSerializer 序列化 key
        template.setKeySerializer(new StringRedisSerializer());
        // 使用 GenericJackson2JsonRedisSerializer 序列化 value
        template.setValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
