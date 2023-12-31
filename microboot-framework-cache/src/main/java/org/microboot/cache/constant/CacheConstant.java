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
    public static final boolean DEFAULT_CACHE_IS_DYNAMIC = false;
    public static final String DEFAULT_CACHE_NAME = Constant.CACHE_NAME;
    public static final String DEFAULT_CACHE_LOCAL_NAME = Constant.CACHE_LOCAL_NAME;
    public static final String DEFAULT_CACHE_CENTRAL_NAME = Constant.CACHE_CENTRAL_NAME;
    public static final boolean DEFAULT_CACHE_ALLOW_NULL_VALUES = false;
    //ehcache
    public static final String DEFAULT_EHCACHE_NAME = "microboot-ehcache";
    public static final String DEFAULT_EHCACHE_MANAGER_NAME = "microboot-manager-ehcache";
    public static final String DEFAULT_EHCACHE_XML = "ehcache/ehcache.xml";
    public static final boolean DEFAULT_EHCACHE_SHARED = false;
    public static final boolean DEFAULT_EHCACHE_ACCEPT_EXISTING = false;
    public static final boolean DEFAULT_EHCACHE_DISABLED = false;
    public static final boolean DEFAULT_EHCACHE_BLOCKING = false;
    //caffeine
    public static final String DEFAULT_CAFFEINE_NAME = "microboot-caffeine";
    public static final boolean DEFAULT_CAFFEINE_ALLOW_NULL_VALUES = false;
    //memcached
    public static final String DEFAULT_MEMCACHED_NAME = "microboot-memcached";
    //redis
    public static final String DEFAULT_REDIS_NAME = "microboot-redis";
}