package org.microboot.cache.impl;

import org.microboot.cache.utils.CacheUtils;
import org.microboot.core.utils.CryptoUtils;

/**
 * @author 胡鹏
 */
public abstract class AbstractLocalCache extends AbstractCache {

    private final String UNIQUE_ID = CryptoUtils.md5Hex();

    @Override
    public void clear() {
        this.clearLocalCache();
        this.fanout();
    }

    @Override
    public void evict(Object key) {
        if (key == null) {
            return;
        }
        this.evictLocalCache(key);
        /*
            注意：这里不能用newKey，因为在MQListenerFunc接口中，会轮询所有本地缓存
            并执行cache.get(key)和cache.evictLocalCache(key)，这两个方法最终都会将key构建成newKey
         */
        this.fanout(key);
    }

    @Override
    protected void setValue(Object key, Object value) {
        this.setLocalCache(key, value);
        /*
            注意：这里不能用newKey，因为在MQListenerFunc接口中，会轮询所有本地缓存
            并执行cache.get(key)和cache.evictLocalCache(key)，这两个方法最终都会将key构建成newKey
         */
        this.fanout(key, value);
    }

    public String getUniqueId() {
        return UNIQUE_ID;
    }

    private void fanout() {
        CacheUtils.clear(UNIQUE_ID, null, null);
    }

    private void fanout(Object key) {
        CacheUtils.clear(UNIQUE_ID, key, null);
    }

    private void fanout(Object key, Object value) {
        CacheUtils.clear(UNIQUE_ID, key, value);
    }

    public abstract void clearLocalCache();

    public abstract void evictLocalCache(Object key);

    public abstract void setLocalCache(Object key, Object value);
}
