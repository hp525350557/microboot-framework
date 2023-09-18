package org.microboot.cache.impl.caffeine;

import org.apache.commons.lang3.StringUtils;
import org.microboot.cache.utils.KeyUtils;
import org.microboot.cache.impl.AbstractLocalCache;
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
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.caffeineCache.evict(newKey);
        this.fanout(newKey);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return null;
        }
        Cache.ValueWrapper valueWrapper = this.caffeineCache.get(newKey);
        return (valueWrapper != null ? valueWrapper.get() : null);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.caffeineCache.put(newKey, value);
        this.fanout(newKey, value);
    }

    @Override
    public void clearByMQ() {
        this.caffeineCache.clear();
    }

    @Override
    public void evictByMQ(Object key) {
        this.caffeineCache.evict(key);
    }
}
