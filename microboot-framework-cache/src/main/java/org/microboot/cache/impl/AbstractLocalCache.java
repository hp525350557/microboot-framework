package org.microboot.cache.impl;

import org.microboot.cache.bean.CacheMQTopicProvider;
import org.microboot.core.bean.ApplicationContextHolder;

/**
 * @author 胡鹏
 */
public abstract class AbstractLocalCache extends AbstractCache {

    protected void fanout() {
        ApplicationContextHolder.getBean(CacheMQTopicProvider.class).publish(this.getClass().hashCode(), null, null);
    }

    protected void fanout(Object key) {
        ApplicationContextHolder.getBean(CacheMQTopicProvider.class).publish(this.getClass().hashCode(), key, null);
    }

    protected void fanout(Object key, Object value) {
        ApplicationContextHolder.getBean(CacheMQTopicProvider.class).publish(this.getClass().hashCode(), key, value);
    }

    public abstract void clearByMQ();

    public abstract void evictByMQ(Object key);
}
