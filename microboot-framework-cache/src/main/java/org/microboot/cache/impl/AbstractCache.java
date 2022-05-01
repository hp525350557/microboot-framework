package org.microboot.cache.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.utils.LoggerUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.Callable;

/**
 * @author 胡鹏
 */
public abstract class AbstractCache implements Cache {

    private final Logger logger = LogManager.getLogger(this.getClass());

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

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object value = this.getValue(key);
        if (type != null && value != null && !type.isInstance(value)) {
            throw new IllegalStateException("Cached value is not of required type [" + type.getName() + "]: " + value);
        }
        return (T) value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Callable<T> callable) {
        Object value = this.getValue(key);
        if (value == null) {
            try {
                value = callable.call();
            } catch (Exception e) {
                LoggerUtils.error(logger, e);
                return null;
            }
        }
        return (T) value;
    }

    @Override
    public void put(Object key, Object value) {
        if (value == null) {
            return;
        }
        this.setValue(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (value == null) {
            return null;
        }
        this.setValue(key, value);
        return new SimpleValueWrapper(value);
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected abstract Object getValue(Object key);

    protected abstract void setValue(Object key, Object value);
}
