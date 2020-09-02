package cn.itrip.itripbiz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Administrator on 2020/5/10 0010.
 * 自动解析类，创建对象放到spring容器中
 */
@Configuration
public class ConfigManager {
    @Autowired
    private RedisPoolProperty redisPoolProperty;

    /*@Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory){
        StringRedisTemplate stringRedisTemplate=new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }
*/
    /**
     * 获取jedisPool连接池
     * @return
     */
    @Bean
    public JedisPool getJedisPool() {
        JedisPool jedisPool = null;
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxActive(redisPoolProperty.getMaxActive());
            config.setMaxIdle(redisPoolProperty.getMaxIdle());
            config.setMaxWait(redisPoolProperty.getMaxWait());
            config.setTestOnBorrow(true);
            jedisPool = new JedisPool(config, redisPoolProperty.getHost(),redisPoolProperty.getPort());
            return jedisPool;
        } catch (Exception e) {
            e.printStackTrace();
            return jedisPool;
        }
    }

}
