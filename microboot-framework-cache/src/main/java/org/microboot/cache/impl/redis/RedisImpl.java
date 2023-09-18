package org.microboot.cache.impl.redis;

import org.microboot.cache.impl.AbstractCache;
import org.microboot.cache.utils.KeyUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
public class RedisImpl extends AbstractCache {

    //缓存时间
    private final int expire;
    //缓存时间是否动态生成（true：缓存时间则是expire作为种子乘以一个随机数，false：缓存时间则是expire，默认false）
    private final boolean isDynamic;
    //redisTemplate
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisImpl(String name, int expire, boolean isDynamic, RedisTemplate<String, Object> redisTemplate) {
        this.name = name;
        this.expire = expire;
        this.isDynamic = isDynamic;
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
        if (key == null) {
            return;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        this.redisTemplate.delete(newKey);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        return this.redisTemplate.opsForValue().get(newKey);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (expire <= 0) {
            //如果expire小于等于0，则不设置过期时间，即：永不失效
            this.redisTemplate.opsForValue().set(newKey, value);
        } else {
            //ThreadLocalRandom.current()比new Random()获取随机数更高效（随机区间：1 ~ expire）
            this.redisTemplate.opsForValue().set(newKey, value, isDynamic ? ThreadLocalRandom.current().nextInt(1, expire) : expire, TimeUnit.SECONDS);
        }
    }
}
