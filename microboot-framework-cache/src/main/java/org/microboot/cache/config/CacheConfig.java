package org.microboot.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.cache.bean.CacheMQTopicProvider;
import org.microboot.cache.constant.CacheConstant;
import org.microboot.cache.bean.CacheMQTopicListener;
import org.microboot.cache.impl.AbstractLocalCache;
import org.microboot.cache.impl.CacheImpl;
import org.microboot.cache.impl.CacheManagerImpl;
import org.microboot.cache.impl.caffeine.CaffeineImpl;
import org.microboot.cache.impl.ehcache.EhcacheImpl;
import org.microboot.cache.impl.memcached.MemcachedImpl;
import org.microboot.cache.impl.redis.RedisImpl;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.constant.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.core.io.UrlResource;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;

import java.util.List;
import java.util.Set;

/**
 * @author 胡鹏
 */
@EnableJms
@Configuration
@DependsOn(Constant.APPLICATION_CONTEXT_HOLDER)
public class CacheConfig {

    /******************************************消息队列，利用广播机制删除所有微服务local级别缓存中指定key的缓存***********************************************/
    /**
     * ActiveMQTopic初始化
     *
     * @param environment
     * @return
     */
    @ConditionalOnProperty(name = {"cache.activemq.using", "spring.jms.pub-sub-domain"}, havingValue = "true")
    @ConditionalOnMissingBean(ActiveMQTopic.class)
    @Bean(name = "activeMQTopic")
    public ActiveMQTopic initActiveMQTopic(JmsTemplate jmsTemplate, Environment environment) {
        ActiveMQTopic activeMQTopic = new ActiveMQTopic(StringUtils.isBlank(environment.getProperty("cache.activemq.topic"))
                ? CacheConstant.DEFAULT_ACTIVEMQ_TOPIC : environment.getProperty("cache.activemq.topic"));
        //查看源码发现JmsTemplate的defaultDestination需要手动赋值
        jmsTemplate.setDefaultDestination(activeMQTopic);
        return activeMQTopic;
    }

    /**
     * CacheMQTopicListener 初始化
     *
     * @param cacheImpl
     * @param localCacheImpl
     * @return
     */
    @ConditionalOnProperty(name = {"cache.activemq.using", "spring.jms.pub-sub-domain"}, havingValue = "true")
    @Bean(name = "org.microboot.cache.bean.CacheMQTopicListener")
    public CacheMQTopicListener initCacheMQTopicListener(@Autowired(required = true) @Qualifier(value = "microboot.cache") CacheImpl cacheImpl,
                                                         @Autowired(required = false) @Qualifier(value = "microboot.local.cache") CacheImpl localCacheImpl) {
        List<Cache> caches = cacheImpl.getCaches();
        Set<AbstractLocalCache> localCaches = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(caches)) {
            for (Cache cache : caches) {
                if (cache instanceof AbstractLocalCache) {
                    localCaches.add((AbstractLocalCache) cache);
                }
            }
        }
        if (localCacheImpl != null && CollectionUtils.isNotEmpty(localCacheImpl.getCaches())) {
            for (Cache cache : localCacheImpl.getCaches()) {
                localCaches.add((AbstractLocalCache) cache);
            }
        }
        return new CacheMQTopicListener(localCaches);
    }

    /**
     * CacheMQTopicProvider 初始化
     *
     * @return
     */
    @Bean(name = "org.microboot.cache.bean.CacheMQTopicProvider")
    public CacheMQTopicProvider initCacheMQTopicProvider() {
        return new CacheMQTopicProvider();
    }

    /******************************************** 缓存 **********************************************/
    /**
     * CacheImpl初始化 -> 通用缓存
     *
     * @param environment
     * @return
     * @throws ClassNotFoundException
     */
    @Bean(name = "microboot.cache")
    public CacheImpl initCacheImpl(Environment environment) throws ClassNotFoundException {
        String classNames = environment.getProperty("cache.class");
        List<Cache> cacheList = getCacheList(classNames);
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.name"))
                ? CacheConstant.DEFAULT_CACHE_NAME : environment.getProperty("cache.name");
        return getCache(cacheName, cacheList);
    }

    /**
     * CacheImpl初始化 -> 本地缓存
     *
     * @param environment
     * @return
     * @throws ClassNotFoundException
     */
    @ConditionalOnProperty(name = "cache.local.class")
    @Bean(name = "microboot.local.cache")
    public CacheImpl initLocalCacheImpl(Environment environment) throws ClassNotFoundException {
        String classNames = environment.getProperty("cache.local.class");
        List<Cache> cacheList = getCacheList(classNames, true);
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.local.name"))
                ? CacheConstant.DEFAULT_CACHE_LOCAL_NAME : environment.getProperty("cache.local.name");
        return getCache(cacheName, cacheList);
    }

    /**
     * CacheImpl初始化 -> 中央缓存
     *
     * @param environment
     * @return
     * @throws ClassNotFoundException
     */
    @ConditionalOnProperty(name = "cache.central.class")
    @Bean(name = "microboot.central.cache")
    public CacheImpl initCentralCacheImpl(Environment environment) throws ClassNotFoundException {
        String classNames = environment.getProperty("cache.central.class");
        List<Cache> cacheList = getCacheList(classNames, false);
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.central.name"))
                ? CacheConstant.DEFAULT_CACHE_CENTRAL_NAME : environment.getProperty("cache.central.name");
        return getCache(cacheName, cacheList);
    }

    /**
     * CacheManagerImpl初始化
     *
     * @param cacheImpl
     * @param localCacheImpl
     * @param centralCacheImpl
     * @param environment
     * @return
     */
    @Bean(name = "org.microboot.cache.impl.CacheManagerImpl")
    public CacheManagerImpl initCacheManagerImpl(@Autowired @Qualifier(value = "microboot.cache") CacheImpl cacheImpl,
                                                 @Autowired(required = false) @Qualifier(value = "microboot.local.cache") CacheImpl localCacheImpl,
                                                 @Autowired(required = false) @Qualifier(value = "microboot.central.cache") CacheImpl centralCacheImpl,
                                                 Environment environment) {
        //通用缓存
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.name"))
                ? CacheConstant.DEFAULT_CACHE_NAME : environment.getProperty("cache.name");
        CacheManagerImpl cacheManagerImpl = new CacheManagerImpl();
        cacheManagerImpl.setCache(cacheName, cacheImpl);
        //本地缓存
        if (localCacheImpl != null) {
            String localCacheName = StringUtils.isBlank(environment.getProperty("cache.local.name"))
                    ? CacheConstant.DEFAULT_CACHE_LOCAL_NAME : environment.getProperty("cache.local.name");
            cacheManagerImpl.setCache(localCacheName, localCacheImpl);
        }
        //中央缓存
        if (centralCacheImpl != null) {
            String centralCacheName = StringUtils.isBlank(environment.getProperty("cache.central.name"))
                    ? CacheConstant.DEFAULT_CACHE_CENTRAL_NAME : environment.getProperty("cache.central.name");
            cacheManagerImpl.setCache(centralCacheName, centralCacheImpl);
        }
        return cacheManagerImpl;
    }

    /**
     * 本来想用SpringBoot自动装配方式来构建Caffeine，将初始化工作放到配置文件中去实现
     * 于是从网上分别找到了使用yml和properties两种配置方式，但是都不成功
     * 于是去看了一下CaffeineCacheConfiguration的源码，如下：
     *
     * @Configuration(proxyBeanMethods = false)
     * @ConditionalOnClass({ Caffeine.class, CaffeineCacheManager.class })
     * @ConditionalOnMissingBean(CacheManager.class)
     * @Conditional({ CacheCondition.class })
     * class CaffeineCacheConfiguration {
     * ...
     * }
     * 可以看到，在CaffeineCacheConfiguration配置类上有一个条件@ConditionalOnMissingBean(CacheManager.class)
     * 所以，当SpringBoot检测到已经创建了CacheManager的实例时，CaffeineCacheConfiguration就失效了
     * 第一种方式：
     * 利用spring的xml配置来创建Caffeine的实例对象，类似Memcached那样
     * 但这种方式也失败了，因为Caffeine类的属性赋值方法都不是set开头的，构造函数也是私有的
     *
     * TODO 第二种方式:(最终解决方案)
     * 继续查看源码发现Caffeine提供了一个from方法创建对象，这个方法传入一个字符串类型的配置信息spec
     * 这个字符串可以通过spring.cache.caffeine.spec=...传给CacheProperties
     * 于是就有了最终解决方案，通过CacheProperties对象来为Caffeine传递配置参数
     * 查看源码可知，这个对象除了装载caffeine的配置以外，还可以装载其他的缓存的配置信息
     */
    @Bean(name = "org.springframework.boot.autoconfigure.cache.CacheProperties")
    public CacheProperties initCacheProperties() {
        return new CacheProperties();
    }

    /************************************* Caffeine相关初始化 *****************************************/
    /**
     * Caffeine初始化
     *
     * @param cacheProperties
     * @return
     */
    @SuppressWarnings("rawtypes")
    @ConditionalOnProperty(name = "cache.caffeine.using", havingValue = "true")
    @Bean(name = "com.github.benmanes.caffeine.cache.Caffeine")
    public Caffeine initCaffeine(CacheProperties cacheProperties) {
        Caffeine caffeine;
        if (cacheProperties != null) {
            caffeine = Caffeine.from(cacheProperties.getCaffeine().getSpec());
        } else {
            caffeine = Caffeine.newBuilder();
        }
        return caffeine;
    }

    /**
     * CaffeineCache初始化
     *
     * @param caffeine
     * @param environment
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @ConditionalOnProperty(name = "cache.caffeine.using", havingValue = "true")
    @Bean(name = "org.springframework.cache.caffeine.CaffeineCache")
    public CaffeineCache initCaffeineCache(Caffeine caffeine, Environment environment) {
        String caffeineName = StringUtils.isBlank(environment.getProperty("cache.caffeine.name"))
                ? CacheConstant.DEFAULT_CAFFEINE_NAME : environment.getProperty("cache.caffeine.name");
        return new CaffeineCache(caffeineName, caffeine.build());
    }

    /**
     * CaffeineImpl初始化
     *
     * @param caffeineCache
     * @param environment
     * @return
     */
    @ConditionalOnProperty(name = "cache.caffeine.using", havingValue = "true")
    @Bean(name = "org.microboot.cache.impl.caffeine.CaffeineImpl")
    public CaffeineImpl initCaffeineImpl(CaffeineCache caffeineCache, Environment environment) {
        String caffeineName = StringUtils.isBlank(environment.getProperty("cache.caffeine.name"))
                ? CacheConstant.DEFAULT_CAFFEINE_NAME : environment.getProperty("cache.caffeine.name");
        return new CaffeineImpl(caffeineName, caffeineCache);
    }

    /************************************* Ehcache相关初始化 *****************************************/
    /**
     * EhCacheManagerFactoryBean初始化
     *
     * @param environment
     * @return
     * @throws Exception
     */
    @ConditionalOnProperty(name = "cache.ehcache.using", havingValue = "true")
    @Bean(name = "org.springframework.cache.ehcache.EhCacheManagerFactoryBean")
    public EhCacheManagerFactoryBean initEhCacheManagerFactoryBean(Environment environment) throws Exception {
        String ehcacheXML = StringUtils.isBlank(environment.getProperty("cache.ehcache.xml"))
                ? CacheConstant.DEFAULT_EHCACHE_XML : environment.getProperty("cache.ehcache.xml");
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(new UrlResource(ehcacheXML));
        ehCacheManagerFactoryBean.setShared(true);
        return ehCacheManagerFactoryBean;
    }

    /**
     * EhCacheFactoryBean初始化
     *
     * @param ehCacheManagerFactoryBean
     * @param environment
     * @return
     */
    @ConditionalOnProperty(name = "cache.ehcache.using", havingValue = "true")
    @Bean(name = "org.springframework.cache.ehcache.EhCacheFactoryBean")
    public EhCacheFactoryBean initEhCacheFactoryBean(EhCacheManagerFactoryBean ehCacheManagerFactoryBean, Environment environment) {
        String ehcacheName = StringUtils.isBlank(environment.getProperty("cache.ehcache.name"))
                ? CacheConstant.DEFAULT_EHCACHE_NAME : environment.getProperty("cache.ehcache.name");
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(ehCacheManagerFactoryBean.getObject());
        ehCacheFactoryBean.setName(ehcacheName);
        return ehCacheFactoryBean;
    }

    /**
     * EhcacheImpl初始化
     *
     * @param ehCacheFactoryBean
     * @return
     */
    @ConditionalOnProperty(name = "cache.ehcache.using", havingValue = "true")
    @Bean(name = "org.microboot.cache.impl.ehcache.EhcacheImpl")
    public EhcacheImpl initEhcacheImpl(EhCacheFactoryBean ehCacheFactoryBean, Environment environment) {
        String ehcacheName = StringUtils.isBlank(environment.getProperty("cache.ehcache.name"))
                ? CacheConstant.DEFAULT_EHCACHE_NAME : environment.getProperty("cache.ehcache.name");
        return new EhcacheImpl(ehcacheName, ehCacheFactoryBean.getObject());
    }

    /*************************************Memcache相关初始化*****************************************/
    /**
     * MemcachedImpl初始化
     *
     * @param memcachedClientFactoryBean
     * @return
     * @throws Exception
     */
    @ConditionalOnProperty(name = "cache.memcached.using", havingValue = "true")
    @Bean(name = "org.microboot.cache.impl.memcached.MemcachedImpl")
    public MemcachedImpl initMemcachedImpl(MemcachedClientFactoryBean memcachedClientFactoryBean, Environment environment) throws Exception {
        int cacheExpire = StringUtils.isBlank(environment.getProperty("cache.memcached.expire"))
                ? CacheConstant.DEFAULT_CACHE_EXPIRE : Integer.parseInt(environment.getProperty("cache.memcached.expire"));
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.memcached.name"))
                ? CacheConstant.DEFAULT_MEMCACHED_NAME : environment.getProperty("cache.memcached.name");
        return new MemcachedImpl(cacheName, cacheExpire, (MemcachedClient) memcachedClientFactoryBean.getObject());
    }

    /************************************* Redis相关初始化 *****************************************/
    /**
     * RedisTemplate初始化
     *
     * @param redisConnectionFactory
     * @return
     */
    @ConditionalOnProperty(name = "cache.redis.using", havingValue = "true")
    @Bean(name = "org.springframework.data.redis.core.RedisTemplate")
    public RedisTemplate<String, Object> initRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //key序列化
        RedisSerializer<String> redisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(redisSerializer);
        //value序列化
        JdkSerializationRedisSerializer jdkSerializationRedisSerializer = new JdkSerializationRedisSerializer();
        redisTemplate.setValueSerializer(jdkSerializationRedisSerializer);
        return redisTemplate;
    }

    /**
     * RedisCacheImpl初始化
     *
     * @param redisTemplate
     * @param environment
     * @return
     */
    @ConditionalOnProperty(name = "cache.redis.using", havingValue = "true")
    @Bean(name = "org.microboot.cache.impl.redis.RedisImpl")
    public RedisImpl initRedisCacheImpl(RedisTemplate<String, Object> redisTemplate, Environment environment) {
        int cacheExpire = StringUtils.isBlank(environment.getProperty("cache.redis.name"))
                ? CacheConstant.DEFAULT_CACHE_EXPIRE : Integer.parseInt(environment.getProperty("cache.redis.expire"));
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.redis.name"))
                ? CacheConstant.DEFAULT_REDIS_NAME : environment.getProperty("cache.redis.name");
        return new RedisImpl(cacheName, cacheExpire, redisTemplate);
    }

    @SuppressWarnings("unchecked")
    private List<Cache> getCacheList(String classNames) throws ClassNotFoundException {
        if (StringUtils.isBlank(classNames)) {
            throw new ClassNotFoundException();
        }
        List<Cache> cacheList = Lists.newArrayList();
        String[] classNamesArray = StringUtils.split(classNames, ",");
        for (String className : classNamesArray) {
            Class<Cache> cacheClass = (Class<Cache>) Class.forName(className);
            Cache cache = ApplicationContextHolder.getBean(cacheClass);
            if (cacheList.contains(cache)) {
                continue;
            }
            cacheList.add(cache);
        }
        return cacheList;
    }

    @SuppressWarnings("unchecked")
    private List<Cache> getCacheList(String classNames, boolean isLocal) throws ClassNotFoundException {
        if (StringUtils.isBlank(classNames)) {
            throw new ClassNotFoundException();
        }
        List<Cache> cacheList = Lists.newArrayList();
        String[] classNamesArray = StringUtils.split(classNames, ",");
        for (String className : classNamesArray) {
            Class<Cache> cacheClass = (Class<Cache>) Class.forName(className);
            Cache cache = ApplicationContextHolder.getBean(cacheClass);
            /*
                isLocal = true时，表示筛选出local缓存，
                isLocal = false时，表示筛选出非local缓存
             */
            if (!isLocal && (cache instanceof AbstractLocalCache)) {
                continue;
            }
            if (isLocal && !(cache instanceof AbstractLocalCache)) {
                continue;
            }
            if (cacheList.contains(cache)) {
                continue;
            }
            cacheList.add(cache);
        }
        return cacheList;
    }

    private CacheImpl getCache(String cacheName, List<Cache> cacheList) {
        CacheImpl cacheImpl = new CacheImpl();
        cacheImpl.setName(cacheName);
        cacheImpl.getCaches().addAll(cacheList);
        return cacheImpl;
    }
}
