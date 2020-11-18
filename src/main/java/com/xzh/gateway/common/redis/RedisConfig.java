package com.xzh.gateway.common.redis;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.DefaultClientResources;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.*;

/**
 * @author 向振华
 * @date 2020/07/01 09:52
 */
@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.redis.enabled", havingValue = "true")
@Slf4j
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.type: 1}")
    private int type;

    @Value("${spring.redis.host: 192.168.3.204}")
    private String hostName;

    @Value("${spring.redis.password: 123}")
    private String password;

    @Value("${spring.redis.port: 6379}")
    private int port;

    @Value("${spring.redis.database: 1}")
    private int database;

    @Value("${spring.redis.timeout: 10000}")
    private int timeOut;

    @Value("${spring.redis.ttl: 60}")
    private int ttl;

    @Value("${spring.redis.lettuce.pool.max-idle:10}")
    private int maxIdle;

    @Value("${spring.redis.lettuce.pool.min-idle:2}")
    private int minIdle;

    @Value("${spring.redis.lettuce.pool.max-active:1000}")
    private int maxTotal;

    @Value("${spring.redis.lettuce.pool.max-wait:3000}")
    private int maxWait;

    @Value("${spring.redis.lettuce.shutdown-timeout:5000}")
    private long shutdownTimeout;

    @Value("${spring.redis.lettuce.command-timeout:5000}")
    private long commandTimeout;

    @Value("${spring.redis.cluster.max-redirects:3}")
    private int redirects;

    @Value("${spring.redis.cluster.nodes:192.168.3.204}")
    private String nodes;

    @Value("${spring.redis.sentinel.master:5000}")
    private String master;

    @Value("${spring.redis.sentinel.nodes:5000}")
    private String sentinels;

    /**
     * 自定义生成key，当cacheable key为null
     */
    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (o, method, objects) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(o.getClass().getName()).append(".");
            sb.append(method.getName()).append(".");
            for (Object obj : objects) {
                sb.append(obj.toString());
            }
            return sb.toString();
        };
    }

    /**
     * 连接池配置
     *
     * @return
     */
    @Bean(name = "genericObjectPoolConfig")
    public GenericObjectPoolConfig genericObjectPoolConfig() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMinIdle(minIdle);
        genericObjectPoolConfig.setMaxIdle(maxIdle);
        genericObjectPoolConfig.setMaxTotal(maxTotal);
        genericObjectPoolConfig.setMaxWaitMillis(maxWait);
        genericObjectPoolConfig.setTestOnBorrow(true);
        genericObjectPoolConfig.setTestOnReturn(true);
        // Idle时进行连接扫描
        genericObjectPoolConfig.setTestWhileIdle(true);
        // 表示idle object evitor两次扫描之间要sleep的毫秒数
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(30000);
        // 表示idle object evitor每次扫描的最多的对象数
        genericObjectPoolConfig.setNumTestsPerEvictionRun(10);
        // 表示一个对象至少停留在idle状态的最短时间，然后才能被idle object evitor扫描并驱逐
        // 这一项只有在timeBetweenEvictionRunsMillis大于0时才有意义
        genericObjectPoolConfig.setMinEvictableIdleTimeMillis(60000);
        return genericObjectPoolConfig;
    }

    @Bean(name = "clientResources", destroyMethod = "shutdown")
    public ClientResources clientResources() {
        return DefaultClientResources.create();
    }

    @Bean(name = "redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory(
            GenericObjectPoolConfig genericObjectPoolConfig,
            ClientResources clientResources) {
        LettuceConnectionFactory connectionFactory = null;
        LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder()
                .poolConfig(genericObjectPoolConfig).commandTimeout(Duration.ofMillis(commandTimeout))
                .shutdownTimeout(Duration.ofMillis(shutdownTimeout)).clientResources(clientResources);
        LettuceClientConfiguration clientConfiguration = builder.build();
        RedisPassword passwd = RedisPassword.of(password);
        switch (type) {
            case 3:
                Set<String> set = new HashSet<String>();
                Collections.addAll(set, sentinels.split(","));
                RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration(master, set);
                redisSentinelConfiguration.setPassword(passwd);
                connectionFactory = new LettuceConnectionFactory(redisSentinelConfiguration, clientConfiguration);
                break;
            case 2:

                List<String> list = Arrays.asList(nodes.split(","));
                RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(list);
                redisClusterConfiguration.setMaxRedirects(redirects);
                redisClusterConfiguration.setPassword(passwd);
                connectionFactory = new LettuceConnectionFactory(redisClusterConfiguration, clientConfiguration);

                break;

            case 1:
                RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
                redisStandaloneConfiguration.setPassword(passwd);
                redisStandaloneConfiguration.setHostName(hostName);
                redisStandaloneConfiguration.setPort(port);
                redisStandaloneConfiguration.setDatabase(database);
                connectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfiguration);
                break;
            default:
                RedisStandaloneConfiguration redisStandaloneConfigurationDefault = new RedisStandaloneConfiguration();
                redisStandaloneConfigurationDefault.setPassword(passwd);
                redisStandaloneConfigurationDefault.setHostName(hostName);
                redisStandaloneConfigurationDefault.setPort(port);
                redisStandaloneConfigurationDefault.setDatabase(database);
                connectionFactory = new LettuceConnectionFactory(redisStandaloneConfigurationDefault, clientConfiguration);
                break;
        }
        return connectionFactory;
    }

    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        // 序列化
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<String, Object>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericFastJsonRedisSerializer());
        redisTemplate.setValueSerializer(new GenericFastJsonRedisSerializer());
        return redisTemplate;
    }

    @Primary
    @Bean(name = "cacheManager")
    public CacheManager cacheManager(
            RedisConnectionFactory redisConnectionFactory) {
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        GenericFastJsonRedisSerializer genericFastJsonRedisSerializer = new GenericFastJsonRedisSerializer();

        // 配置序列化
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ttl))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisSerializer))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(genericFastJsonRedisSerializer));

        RedisCacheManager cacheManager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration).build();
        return cacheManager;
    }

    @Override
    @Bean
    public CacheErrorHandler errorHandler() {
        // 异常处理，当Redis发生异常时，打印日志，但是程序正常走
        log.info("初始化 -> [{}]", "Redis CacheErrorHandler");
        CacheErrorHandler cacheErrorHandler = new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheGetError：key -> [{}]", key, e);
            }

            @Override
            public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
                log.error("Redis occur handleCachePutError：key -> [{}]；value -> [{}]", key, value, e);
            }

            @Override
            public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
                log.error("Redis occur handleCacheEvictError：key -> [{}]", key, e);
            }

            @Override
            public void handleCacheClearError(RuntimeException e, Cache cache) {
                log.error("Redis occur handleCacheClearError：", e);
            }
        };
        return cacheErrorHandler;
    }

    @Bean
    public RedisService redisService(RedisTemplate<String, Object> redisTemplate) {
        return new RedisService(redisTemplate);
    }

}
