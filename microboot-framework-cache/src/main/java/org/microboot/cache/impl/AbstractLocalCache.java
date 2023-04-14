package org.microboot.cache.impl;

import org.microboot.cache.utils.CacheUtils;
import org.microboot.core.utils.CryptoUtils;

/**
 * @author 胡鹏
 */
public abstract class AbstractLocalCache extends AbstractCache {

    private final String UNIQUE_ID = CryptoUtils.md5Hex();

    protected void fanout() {
        CacheUtils.clear(UNIQUE_ID, null, null);
    }

    protected void fanout(Object key) {
        CacheUtils.clear(UNIQUE_ID, key, null);
    }

    protected void fanout(Object key, Object value) {
        CacheUtils.clear(UNIQUE_ID, key, value);
    }

    public abstract void clearByMQ();

    public abstract void evictByMQ(Object key);

    public String getUniqueId() {
        return UNIQUE_ID;
    }
}
