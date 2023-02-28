package org.microboot.cache.impl;

import org.microboot.cache.bean.CacheMQTopicProvider;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.utils.CryptoUtils;

/**
 * @author 胡鹏
 */
public abstract class AbstractLocalCache extends AbstractCache {

    private final String UNIQUE_ID = CryptoUtils.md5Hex();

    protected void fanout() {
        boolean notMissing = ApplicationContextHolder.getApplicationContext().containsLocalBean(CacheMQTopicProvider.class.getName());
        if (!notMissing) {
            return;
        }
        ApplicationContextHolder.getBean(CacheMQTopicProvider.class).publish(UNIQUE_ID, null, null);
    }

    protected void fanout(Object key) {
        boolean notMissing = ApplicationContextHolder.getApplicationContext().containsLocalBean(CacheMQTopicProvider.class.getName());
        if (!notMissing) {
            return;
        }
        ApplicationContextHolder.getBean(CacheMQTopicProvider.class).publish(UNIQUE_ID, key, null);
    }

    protected void fanout(Object key, Object value) {
        boolean notMissing = ApplicationContextHolder.getApplicationContext().containsLocalBean(CacheMQTopicProvider.class.getName());
        if (!notMissing) {
            return;
        }
        ApplicationContextHolder.getBean(CacheMQTopicProvider.class).publish(UNIQUE_ID, key, value);
    }

    public abstract void clearByMQ();

    public abstract void evictByMQ(Object key);

    public String getUniqueId() {
        return UNIQUE_ID;
    }
}
