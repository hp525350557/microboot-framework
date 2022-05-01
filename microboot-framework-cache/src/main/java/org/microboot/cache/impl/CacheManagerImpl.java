package org.microboot.cache.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author 胡鹏
 */
public class CacheManagerImpl extends AbstractTransactionSupportingCacheManager {

    private final ConcurrentHashMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return cacheMap.values();
    }

    @Override
    public Cache getCache(String name) {
        return cacheMap.get(name);
    }

    public void setCache(String name, Cache cache) {
        cacheMap.put(name, cache);
    }
}