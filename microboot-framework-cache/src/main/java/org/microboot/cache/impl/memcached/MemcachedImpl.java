package org.microboot.cache.impl.memcached;

import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang3.StringUtils;
import org.microboot.cache.impl.AbstractCache;
import org.microboot.cache.utils.KeyUtils;

/**
 * @author 胡鹏
 */
public class MemcachedImpl extends AbstractCache {

    //缓存时间
    private final int expire;
    //Memcache对象
    private final MemcachedClient memcachedClient;

    public MemcachedImpl(String name, int expire, MemcachedClient memcachedClient) {
        this.name = name;
        this.expire = expire;
        this.memcachedClient = memcachedClient;
    }

    @Override
    public Object getNativeCache() {
        return this.memcachedClient;
    }

    @Override
    public void clear() {
        this.memcachedClient.flush();
    }

    @Override
    public void evict(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.memcachedClient.delete(newKey);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return null;
        }
        return this.memcachedClient.get(newKey);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (StringUtils.isBlank(newKey)) {
            return;
        }
        this.memcachedClient.set(newKey, expire, value);
    }
}
