package org.microboot.cache.constant;

import org.microboot.core.constant.Constant;

/**
 * @author 胡鹏
 */
public class CacheConstant {

    //topic
    public static final String DEFAULT_ACTIVEMQ_TOPIC = "microboot-topic";
    //cache
    public static final int DEFAULT_CACHE_EXPIRE = 30 * 60;
    public static final String DEFAULT_CACHE_NAME = Constant.CACHE_NAME;
    public static final String DEFAULT_CACHE_LOCAL_NAME = Constant.CACHE_LOCAL_NAME;
    public static final String DEFAULT_CACHE_CENTRAL_NAME = Constant.CACHE_CENTRAL_NAME;
    //ehcache
    public static final String DEFAULT_EHCACHE_NAME = "microboot-ehcache";
    public static final String DEFAULT_EHCACHE_XML = "ehcache/ehcache.xml";
    //caffeine
    public static final String DEFAULT_CAFFEINE_NAME = "microboot-caffeine";
    //memcached
    public static final String DEFAULT_MEMCACHED_NAME = "microboot-memcached";
    //redis
    public static final String DEFAULT_REDIS_NAME = "microboot-redis";
}