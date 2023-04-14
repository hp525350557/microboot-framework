package org.microboot.cache.utils;

import org.microboot.cache.entity.CacheMessage;
import org.microboot.cache.func.MQProviderFunc;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.CryptoUtils;

/**
 * @author 胡鹏
 */
public class CacheUtils {

    public static void clear(String uniqueId, Object key, Object value) {
        boolean notMissing = ApplicationContextHolder.getApplicationContext().containsLocalBean(MQProviderFunc.class.getName());
        if (!notMissing) {
            return;
        }
        CacheMessage cacheMessage = new CacheMessage();
        cacheMessage.setUniqueId(uniqueId);
        if (key != null) {
            cacheMessage.setKey(key);
        }
        if (value != null) {
            cacheMessage.setMd5(CryptoUtils.md5Hex(ConvertUtils.object2Bytes(value)));
        }
        ApplicationContextHolder.getBean(MQProviderFunc.class.getName(), MQProviderFunc.class).publish(cacheMessage);
    }
}