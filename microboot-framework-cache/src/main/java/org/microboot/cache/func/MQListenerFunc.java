package org.microboot.cache.func;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.cache.entity.CacheMessage;
import org.microboot.cache.impl.AbstractLocalCache;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.func.Func0;
import org.microboot.core.utils.ConvertUtils;
import org.microboot.core.utils.CryptoUtils;
import org.microboot.core.utils.LoggerUtils;
import org.springframework.cache.Cache;

import java.util.Set;

/**
 * @author 胡鹏
 */
public interface MQListenerFunc {

    Logger logger = LogManager.getLogger(MQListenerFunc.class);

    /**
     * 清除本地缓存
     *
     * @param func
     */
    default void clearLocalCaches(Func0<CacheMessage> func) {
        try {
            Set<AbstractLocalCache> localCaches = ApplicationContextHolder.getBean("localCaches", Set.class);
            if (CollectionUtils.isEmpty(localCaches)) {
                return;
            }
            CacheMessage cacheMessage = func.func();
            if (cacheMessage == null) {
                return;
            }
            String uniqueId = this.get(() -> cacheMessage.getUniqueId());
            Object key = this.get(() -> cacheMessage.getKey());
            String md5 = this.get(() -> cacheMessage.getMd5());

            for (AbstractLocalCache cache : localCaches) {
                String currUniqueId = cache.getUniqueId();
                /*
                    服务启动后AbstractLocalCache中会生成一个唯一标识，其子类实例共用这个唯一标识
                    通过这个唯一标识可判断cache跟发消息的缓存是否处于同一个服务进程
                    如果是同一个服务，则不需要往下执行

                    老版本中这里是通过class的hashcode来判断的，但是考虑到hashcode可能会出现重复的情况
                    因此改为唯一标识来判断
                 */
                if (StringUtils.equals(currUniqueId, uniqueId)) {
                    continue;
                }
                if (key == null) {
                    cache.clearLocalCache();
                } else {
                    /*
                        当开启多个服务时，本地缓存通过消息队列的广播来清除被更新的数据，以达到微服务之间本地缓存同步的效果
                        但是@Cacheable注解在获取不到缓存数据时，会调用业务方法，并调用Cache接口的put方法缓存返回值
                        这就造成了@Cacheable也会进入setValue方法，并触发缓存清除事件，这就导致了一个问题：
                        【
                            A服务被调用 -> 检查是否有缓存 -> 否 -> 调用业务方法，并缓存数据，同时触发缓存清除事件 -> B，C等服务的缓存被清除
                            B服务被调用 -> 检查是否有缓存 -> 否 -> 调用业务方法，并缓存数据，同时触发缓存清除事件 -> A，C等服务的缓存被清除
                            C服务被调用 -> 检查是否有缓存 -> 否 -> 调用业务方法，并缓存数据，同时触发缓存清除事件 -> A，B等服务的缓存被清除
                        】
                        这种情况导致@Cacheable标注的方法实际上并没有真正起作用
                        因此在调用setValue方法时需要通过缓存数据的字节数组来判断数据是否真正变化
                        只有数据真的发生变化了的服务，才需要清除
                     */
                    Cache.ValueWrapper valueWrapper = cache.get(key);
                    if (valueWrapper == null) {
                        continue;
                    }
                    Object value = valueWrapper.get();
                    if (value == null) {
                        continue;
                    }
                    String currMd5 = CryptoUtils.md5Hex(ConvertUtils.object2Bytes(value));
                    if (StringUtils.equals(md5, currMd5)) {
                        continue;
                    }
                    cache.evictLocalCache(key);
                }
            }
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        }
    }

    default <T> T get(Func0<T> func) {
        try {
            return func.func();
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return null;
    }
}