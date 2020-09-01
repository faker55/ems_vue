package com.itnoob.cache;

import com.itnoob.utils.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.Cache;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author noob
 * @created 2020-09 01 11:29
 */

@Slf4j
public class RedisCache  implements Cache {

    private String id;

    public RedisCache(String id) {
        log.info("当前缓存的namespace:[{}]",id);
        this.id = id;
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     * 放入redis缓存
     * @param key
     * @param value
     */
    @Override
    public void putObject(Object key, Object value) {
        log.info("放入缓存key:[{}],放入缓存的value[{}]",key,value);
        getRedisTemplate().opsForHash().put(id,key.toString(),value);


    }

    /**
     * 从redis中获取缓存
     * @param key
     * @return
     */
    @Override
    public Object getObject(Object key) {

        log.info("获取缓存key:[{}]",key.toString());
        return getRedisTemplate().opsForHash().get(id,key.toString());
    }

    @Override
    public Object removeObject(Object o) {
        return null;
    }

    @Override
    public void clear() {
        log.info("清除所有缓存...");
        getRedisTemplate().delete(id);

    }

    @Override
    public int getSize() {
        return getRedisTemplate().opsForHash().size(id).intValue();
    }

    /**
     * 封装获取redisTemplate的方法
     * @return
     */
    public RedisTemplate getRedisTemplate(){
        RedisTemplate redisTemplate = (RedisTemplate) ApplicationContextUtils.getBean("redisTemplate");
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());  //把key设置为string类型的序列化
        return redisTemplate;
    }
}
