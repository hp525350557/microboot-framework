package org.microboot.cache.func;

import org.microboot.cache.entity.CacheMessage;

/**
 * @author 胡鹏
 */
public interface MQProviderFunc {

    void publish(CacheMessage cacheMessage);
}