package org.microboot.cache.impl.ehcache;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import org.apache.commons.lang3.StringUtils;
import org.microboot.cache.utils.KeyUtils;
import org.microboot.cache.impl.AbstractLocalCache;

/**
 * @author 胡鹏
 */
public class EhcacheImpl extends AbstractLocalCache {

    //Ehcache对象
    private final Ehcache ehCache;

    public EhcacheImpl(String name, Ehcache ehCache) {
        this.name = name;
        this.ehCache = ehCache;
    }

    @Override
    public Object getNativeCache() {
        return this.ehCache;
    }

    @Override
    public void clear() {
        this.ehCache.flush();
        this.fanout();
    }

    @Override
    public void evict(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.ehCache.remove(newKey);
        this.fanout(key);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return null;
        }
        Element cacheValue = this.ehCache.get(newKey);
        return (cacheValue != null ? cacheValue.getObjectValue() : null);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.ehCache.put(new Element(newKey, value));
        this.fanout(key, value);
    }

    @Override
    public void clearByMQ() {
        this.ehCache.flush();
    }

    @Override
    public void evictByMQ(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        this.ehCache.remove(newKey);
    }
}
