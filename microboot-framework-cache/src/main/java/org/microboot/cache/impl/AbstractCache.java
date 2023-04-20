package org.microboot.cache.impl;

import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;

import java.util.concurrent.Callable;

/**
 * @author 胡鹏
 */
public abstract class AbstractCache extends AbstractValueAdaptingCache {

    //缓存最终生成的key的前缀
    protected String name;

    protected AbstractCache() {
        this(false);
    }

    protected AbstractCache(boolean allowNullValues) {
        super(allowNullValues);
    }

    /**
     * AbstractValueAdaptingCache所有方法都会调用lookup
     *
     * @param key
     * @return
     */
    @Override
    protected Object lookup(Object key) {
        return this.getValue(key);
    }

    /**
     * 这个方法其实用不到
     * CacheImpl中已经重写了此方法，且CacheImpl是缓存入口
     * 所以这个方法是调用不到的，但是AbstractValueAdaptingCache没实现
     * 所以为了不报错只能冗余实现一下了
     *
     * @param key
     * @param callable
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(Object key, Callable<T> callable) {
        ValueWrapper valueWrapper = this.get(key);
        if (valueWrapper != null) {
            return (T) valueWrapper.get();
        }
        T value;
        try {
            value = callable.call();
        } catch (Exception e) {
            throw new ValueRetrievalException(key, callable, e);
        }
        this.put(key, value != null ? value : isAllowNullValues() ? NullValue.INSTANCE : null);
        return value;
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            return;
        }
        this.setValue(key, value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected abstract Object getValue(Object key);

    protected abstract void setValue(Object key, Object value);
}
