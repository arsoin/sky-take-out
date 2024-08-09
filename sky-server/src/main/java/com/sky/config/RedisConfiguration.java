package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/*
* redis 的配置类
* */
@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory connectionFactory){
        log.info("开始创建redis模板对象");
        //创建一个redisTemplate
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置redis的连接工厂对象
        redisTemplate.setConnectionFactory(connectionFactory);
        //设置redis key的序列化器
        //如果不手动设置的化，会使用一个默认的序列化器，这样会导致在redis中看到的key长得不一样，但是用起来是一样的
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    };
}
