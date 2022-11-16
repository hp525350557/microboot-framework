package org.microboot.cache.impl;

import org.springframework.cache.support.AbstractValueAdaptingCache;

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

    @Override
    protected Object lookup(Object key) {
        return this.getValue(key);
    }

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
