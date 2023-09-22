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
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        Cache.ValueWrapper valueWrapper = this.caffeineCache.get(newKey);
        return (valueWrapper != null ? valueWrapper.get() : null);
    }

    @Override
    public void clearLocalCache() {
        this.caffeineCache.clear();
    }

    @Override
    public void evictLocalCache(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        this.caffeineCache.evict(newKey);
    }

    @Override
    public void setLocalCache(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        this.caffeineCache.put(newKey, value);
    }
}
