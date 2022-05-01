package org.microboot.cache.impl.redis;

import org.apache.commons.lang3.StringUtils;
import org.microboot.cache.utils.KeyUtils;
import org.microboot.cache.impl.AbstractCache;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
public class RedisImpl extends AbstractCache {

    //缓存时间
    private final int expire;
    //redisTemplate
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisImpl(String name, int expire, RedisTemplate<String, Object> redisTemplate) {
        this.name = name;
        this.expire = expire;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public void clear() {
        Set<String> keys = redisTemplate.keys("*");
        this.redisTemplate.delete(keys);
    }

    @Override
    public void evict(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.redisTemplate.delete(newKey);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return null;
        }
        return this.redisTemplate.opsForValue().get(newKey);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.redisTemplate.opsForValue().set(newKey, value, expire, TimeUnit.SECONDS);
    }
}
