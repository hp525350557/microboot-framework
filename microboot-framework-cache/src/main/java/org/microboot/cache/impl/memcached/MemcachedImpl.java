package org.microboot.cache.impl.memcached;

import net.spy.memcached.MemcachedClient;
import org.microboot.cache.impl.AbstractCache;
import org.microboot.cache.utils.KeyUtils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author 胡鹏
 */
public class MemcachedImpl extends AbstractCache {

    //缓存时间
    private final int expire;
    //缓存时间是否动态生成（true：缓存时间则是expire作为种子乘以一个随机数，false：缓存时间则是expire，默认false）
    private final boolean isDynamic;
    //Memcache对象
    private final MemcachedClient memcachedClient;

    public MemcachedImpl(String name, int expire, boolean isDynamic, MemcachedClient memcachedClient) {
        this.name = name;
        this.expire = expire;
        this.isDynamic = isDynamic;
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
        if (key == null) {
            return;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        this.memcachedClient.delete(newKey);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        return this.memcachedClient.get(newKey);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (expire <= 0) {
            //如果expire小于等于0，则设置过期时间为0，即：永不失效
            this.memcachedClient.set(newKey, 0, value);
        } else {
            //ThreadLocalRandom.current()比new Random()获取随机数更高效（随机区间：1 ~ expire）
            this.memcachedClient.set(newKey, isDynamic ? ThreadLocalRandom.current().nextInt(1, expire) : expire, value);
        }
    }
}
