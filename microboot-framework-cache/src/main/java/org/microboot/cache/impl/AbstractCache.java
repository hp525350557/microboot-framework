package org.microboot.cache.impl;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * @author 胡鹏
 */
public abstract class AbstractCache implements Cache {

    //缓存最终生成的key的前缀
    protected String name;

    @Override
    public ValueWrapper get(Object key) {
        ValueWrapper wrapper = null;
        Object value = this.getValue(key);
        if (value != null) {
            wrapper = new SimpleValueWrapper(value);
        }
        return wrapper;
    }

    /**
     * 无需实现该方法，CacheImpl作为多级缓存的入口并没有调用该方法
     *
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        return null;
    }

    /**
     * 无需实现该方法，CacheImpl作为多级缓存的入口并没有调用该方法
     *
     * @param key
     * @param callable
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(Object key, Callable<T> callable) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            return;
        }
        this.setValue(key, value);
    }

    /**
     * 无需实现该方法，CacheImpl作为多级缓存的入口并没有调用该方法
     *
     * @param key
     * @param value
     * @return
     */
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected abstract Object getValue(Object key);

    protected abstract void setValue(Object key, Object value);
}
