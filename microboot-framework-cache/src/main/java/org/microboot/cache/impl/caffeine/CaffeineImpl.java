package org.microboot.cache.impl.caffeine;

import org.microboot.cache.impl.AbstractLocalCache;
import org.microboot.cache.utils.KeyUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;

/**
 * @author 胡鹏
 */
public class CaffeineImpl extends AbstractLocalCache {

    //CaffeineCache对象
    private final CaffeineCache caffeineCache;

    public CaffeineImpl(String name, CaffeineCache caffeineCache) {
        this.name = name;
        this.caffeineCache = caffeineCache;
    }

    @Override
    public Object getNativeCache() {
        return this.caffeineCache;
    }

    @Override
    public void clear() {
        this.caffeineCache.clear();
        this.fanout();
    }

    @Override
    public void evict(Object key) {
        if (key == null) {
            return;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        this.caffeineCache.evict(newKey);
        /*
            注意：这里不能用newKey，因为在MQListenerFunc接口中，会轮询所有本地缓存
            并执行cache.get(key)和cache.evictByMQ(key)，这两个方法最终都会将key构建成newKey
         */
        this.fanout(key);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        Cache.ValueWrapper valueWrapper = this.caffeineCache.get(newKey);
        return (valueWrapper != null ? valueWrapper.get() : null);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        this.caffeineCache.put(newKey, value);
        /*
            注意：这里不能用newKey，因为在MQListenerFunc接口中，会轮询所有本地缓存
            并执行cache.get(key)和cache.evictByMQ(key)，这两个方法最终都会将key构建成newKey
         */
        this.fanout(key, value);
    }

    @Override
    public void clearByMQ() {
        this.caffeineCache.clear();
    }

    @Override
    public void evictByMQ(Object key) {
        if (key == null) {
            return;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        this.caffeineCache.evict(newKey);
    }
}
