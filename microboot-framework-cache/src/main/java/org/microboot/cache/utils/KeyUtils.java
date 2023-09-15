package org.microboot.cache.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

/**
 * @author 胡鹏
 */
public class KeyUtils {

    public static String newKey(String cacheName, Object key) {
        return cacheName + '&' + StringUtils.replace(ObjectUtils.nullSafeToString(key), StringUtils.SPACE, "");
    }
}