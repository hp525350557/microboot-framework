package org.microboot.cache.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.sf.ehcache.Ehcache;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.spring.MemcachedClientFactoryBean;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.microboot.cache.bean.ActiveMQListener;
import org.microboot.cache.bean.ActiveMQProvider;
import org.microboot.cache.constant.CacheConstant;
import org.microboot.cache.func.MQListenerFunc;
import org.microboot.cache.func.MQProviderFunc;
import org.microboot.cache.impl.AbstractCache;
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
     * ActiveMQTopic 初始化
     *
     * @param environment
     * @return
     */
    @Bean(name = "activeMQTopic")
    @ConditionalOnMissingBean(ActiveMQTopic.class)
    @ConditionalOnProperty(name = {"cache.activemq.using", "spring.jms.pub-sub-domain"}, havingValue = "true")
    public ActiveMQTopic initActiveMQTopic(JmsTemplate jmsTemplate, Environment environment) {
        ActiveMQTopic activeMQTopic = new ActiveMQTopic(StringUtils.isBlank(environment.getProperty("cache.activemq.topic"))
                ? CacheConstant.DEFAULT_ACTIVEMQ_TOPIC : environment.getProperty("cache.activemq.topic"));
        //查看源码发现JmsTemplate的defaultDestination需要手动赋值
        jmsTemplate.setDefaultDestination(activeMQTopic);
        return activeMQTopic;
    }

    /**
     * ActiveMQListener 初始化
     *
     * @return
     */
    @Bean(name = "org.microboot.cache.func.MQListenerFunc")
    @ConditionalOnMissingBean(MQListenerFunc.class)
    @ConditionalOnProperty(name = {"cache.activemq.using", "spring.jms.pub-sub-domain"}, havingValue = "true")
    public ActiveMQListener initActiveMQListener() {
        return new ActiveMQListener();
    }

    /**
     * ActiveMQProvider 初始化
     *
     * @return
     */
    @Bean(name = "org.microboot.cache.func.MQProviderFunc")
    @ConditionalOnMissingBean(MQProviderFunc.class)
    @ConditionalOnProperty(name = {"cache.activemq.using", "spring.jms.pub-sub-domain"}, havingValue = "true")
    public ActiveMQProvider initActiveMQProvider() {
        return new ActiveMQProvider();
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
        List<AbstractCache> cacheList = getCacheList(classNames);
        boolean allowNullValues = StringUtils.isBlank(environment.getProperty("cache.allow-null-values"))
                ? CacheConstant.DEFAULT_CACHE_ALLOW_NULL_VALUES : Boolean.parseBoolean(environment.getProperty("cache.allow-null-values"));
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.name"))
                ? CacheConstant.DEFAULT_CACHE_NAME : environment.getProperty("cache.name");
        return getCache(allowNullValues, cacheName, cacheList);
    }

    /**
     * CacheImpl初始化 -> 本地缓存
     *
     * @param environment
     * @return
     * @throws ClassNotFoundException
     */
    @Bean(name = "microboot.local.cache")
    @ConditionalOnProperty(name = "cache.local.class")
    public CacheImpl initLocalCacheImpl(Environment environment) throws ClassNotFoundException {
        String classNames = environment.getProperty("cache.local.class");
        List<AbstractCache> cacheList = getCacheList(classNames, true);
        boolean allowNullValues = StringUtils.isBlank(environment.getProperty("cache.allow-null-values"))
                ? CacheConstant.DEFAULT_CACHE_ALLOW_NULL_VALUES : Boolean.parseBoolean(environment.getProperty("cache.allow-null-values"));
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.local.name"))
                ? CacheConstant.DEFAULT_CACHE_LOCAL_NAME : environment.getProperty("cache.local.name");
        return getCache(allowNullValues, cacheName, cacheList);
    }

    /**
     * CacheImpl初始化 -> 中央缓存
     *
     * @param environment
     * @return
     * @throws ClassNotFoundException
     */
    @Bean(name = "microboot.central.cache")
    @ConditionalOnProperty(name = "cache.central.class")
    public CacheImpl initCentralCacheImpl(Environment environment) throws ClassNotFoundException {
        String classNames = environment.getProperty("cache.central.class");
        List<AbstractCache> cacheList = getCacheList(classNames, false);
        boolean allowNullValues = StringUtils.isBlank(environment.getProperty("cache.allow-null-values"))
                ? CacheConstant.DEFAULT_CACHE_ALLOW_NULL_VALUES : Boolean.parseBoolean(environment.getProperty("cache.allow-null-values"));
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.central.name"))
                ? CacheConstant.DEFAULT_CACHE_CENTRAL_NAME : environment.getProperty("cache.central.name");
        return getCache(allowNullValues, cacheName, cacheList);
    }

    /**
     * localCaches 初始化
     *
     * 将混合模式和本地模式的所有本地缓存取出，并添加到一个Set集合中
     * 当数据更新时，删除所有Set集合中本地缓存响应的数据
     *
     * @param cacheImpl
     * @param localCacheImpl
     * @return
     */
    @Bean(name = "localCaches")
    public Set<AbstractLocalCache> initLocalCaches(@Autowired(required = true) @Qualifier(value = "microboot.cache") CacheImpl cacheImpl,
                                                   @Autowired(required = false) @Qualifier(value = "microboot.local.cache") CacheImpl localCacheImpl) {
        Set<AbstractLocalCache> localCaches = Sets.newHashSet();
        if (cacheImpl != null && CollectionUtils.isNotEmpty(cacheImpl.getCaches())) {
            for (AbstractCache cache : cacheImpl.getCaches()) {
                if (cache instanceof AbstractLocalCache) {
                    localCaches.add((AbstractLocalCache) cache);
                }
            }
        }
        if (localCacheImpl != null && CollectionUtils.isNotEmpty(localCacheImpl.getCaches())) {
            for (AbstractCache cache : localCacheImpl.getCaches()) {
                localCaches.add((AbstractLocalCache) cache);
            }
        }
        return localCaches;
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
     * CaffeineCache初始化
     *
     * @param cacheProperties
     * @param environment
     * @return
     */
    @Bean(name = "org.springframework.cache.caffeine.CaffeineCache")
    @ConditionalOnProperty(name = "cache.caffeine.using", havingValue = "true")
    public CaffeineCache initCaffeineCache(CacheProperties cacheProperties, Environment environment) {
        Caffeine caffeine = cacheProperties != null
                ? Caffeine.from(cacheProperties.getCaffeine().getSpec()) : Caffeine.newBuilder();
        boolean caffeineAllowNullValues = StringUtils.isBlank(environment.getProperty("cache.caffeine.allow-null-values"))
                ? CacheConstant.DEFAULT_CAFFEINE_ALLOW_NULL_VALUES : Boolean.parseBoolean(environment.getProperty("cache.caffeine.allow-null-values"));
        String caffeineName = StringUtils.isBlank(environment.getProperty("cache.caffeine.name"))
                ? CacheConstant.DEFAULT_CAFFEINE_NAME : environment.getProperty("cache.caffeine.name");
        //这里构建CaffeineCache对象，默认让其不能缓存null值，交由CacheImpl统一管理。不然会造成CacheImpl里面缓存null值的判断失效
        return new CaffeineCache(caffeineName, caffeine.build(), caffeineAllowNullValues);
    }

    /**
     * CaffeineImpl初始化
     *
     * @param caffeineCache
     * @param environment
     * @return
     */
    @Bean(name = "org.microboot.cache.impl.caffeine.CaffeineImpl")
    @ConditionalOnProperty(name = "cache.caffeine.using", havingValue = "true")
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
    @Bean(name = "org.springframework.cache.ehcache.EhCacheManagerFactoryBean")
    @ConditionalOnProperty(name = "cache.ehcache.using", havingValue = "true")
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
    @Bean(name = "org.springframework.cache.ehcache.EhCacheFactoryBean")
    @ConditionalOnProperty(name = "cache.ehcache.using", havingValue = "true")
    public EhCacheFactoryBean initEhCacheFactoryBean(EhCacheManagerFactoryBean ehCacheManagerFactoryBean, Environment environment) {
        String ehcacheName = StringUtils.isBlank(environment.getProperty("cache.ehcache.name"))
                ? CacheConstant.DEFAULT_EHCACHE_NAME : environment.getProperty("cache.ehcache.name");
        EhCacheFactoryBean ehCacheFactoryBean = new EhCacheFactoryBean();
        ehCacheFactoryBean.setCacheManager(ehCacheManagerFactoryBean.getObject());
        ehCacheFactoryBean.setName(ehcacheName);
        return ehCacheFactoryBean;
    }

    /**
     * Ehcache初始化
     *
     * @param ehCacheFactoryBean
     * @return
     * @throws Exception
     */
    @Bean(name = "net.sf.ehcache.Ehcache")
    @ConditionalOnProperty(name = "cache.ehcache.using", havingValue = "true")
    public Ehcache initEhcache(EhCacheFactoryBean ehCacheFactoryBean) throws Exception {
        return ehCacheFactoryBean.getObject();
    }

    /**
     * EhcacheImpl初始化
     *
     * @param ehcache
     * @param environment
     * @return
     */
    @Bean(name = "org.microboot.cache.impl.ehcache.EhcacheImpl")
    @ConditionalOnProperty(name = "cache.ehcache.using", havingValue = "true")
    public EhcacheImpl initEhcacheImpl(Ehcache ehcache, Environment environment) {
        String ehcacheName = StringUtils.isBlank(environment.getProperty("cache.ehcache.name"))
                ? CacheConstant.DEFAULT_EHCACHE_NAME : environment.getProperty("cache.ehcache.name");
        return new EhcacheImpl(ehcacheName, ehcache);
    }

    /*************************************Memcache相关初始化*****************************************/
    /**
     * MemcachedClient初始化
     *
     * @param memcachedClientFactoryBean
     * @return
     * @throws Exception
     */
    @Bean(name = "net.spy.memcached.MemcachedClient")
    @ConditionalOnProperty(name = "cache.memcached.using", havingValue = "true")
    public MemcachedClient initMemcachedClient(MemcachedClientFactoryBean memcachedClientFactoryBean) throws Exception {
        return (MemcachedClient) memcachedClientFactoryBean.getObject();
    }


    /**
     * MemcachedImpl初始化
     *
     * @param memcachedClient
     * @param environment
     * @return
     * @throws Exception
     */
    @Bean(name = "org.microboot.cache.impl.memcached.MemcachedImpl")
    @ConditionalOnProperty(name = "cache.memcached.using", havingValue = "true")
    public MemcachedImpl initMemcachedImpl(MemcachedClient memcachedClient, Environment environment) throws Exception {
        int cacheExpire = StringUtils.isBlank(environment.getProperty("cache.memcached.expire"))
                ? CacheConstant.DEFAULT_CACHE_EXPIRE : Integer.parseInt(environment.getProperty("cache.memcached.expire"));
        boolean isDynamic = StringUtils.isBlank(environment.getProperty("cache.memcached.isDynamic"))
                ? CacheConstant.DEFAULT_CACHE_IS_DYNAMIC : Boolean.parseBoolean(environment.getProperty("cache.memcached.isDynamic"));
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.memcached.name"))
                ? CacheConstant.DEFAULT_MEMCACHED_NAME : environment.getProperty("cache.memcached.name");
        return new MemcachedImpl(cacheName, cacheExpire, isDynamic, memcachedClient);
    }

    /************************************* Redis相关初始化 *****************************************/
    /**
     * RedisTemplate初始化
     *
     * 这里的beanName必须用redisTemplate，使得SpringBoot官方的失效
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean(name = "redisTemplate")
    @ConditionalOnProperty(name = "cache.redis.using", havingValue = "true")
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
    @Bean(name = "org.microboot.cache.impl.redis.RedisImpl")
    @ConditionalOnProperty(name = "cache.redis.using", havingValue = "true")
    public RedisImpl initRedisCacheImpl(RedisTemplate<String, Object> redisTemplate, Environment environment) {
        int cacheExpire = StringUtils.isBlank(environment.getProperty("cache.redis.name"))
                ? CacheConstant.DEFAULT_CACHE_EXPIRE : Integer.parseInt(environment.getProperty("cache.redis.expire"));
        boolean isDynamic = StringUtils.isBlank(environment.getProperty("cache.redis.isDynamic"))
                ? CacheConstant.DEFAULT_CACHE_IS_DYNAMIC : Boolean.parseBoolean(environment.getProperty("cache.redis.isDynamic"));
        String cacheName = StringUtils.isBlank(environment.getProperty("cache.redis.name"))
                ? CacheConstant.DEFAULT_REDIS_NAME : environment.getProperty("cache.redis.name");
        return new RedisImpl(cacheName, cacheExpire, isDynamic, redisTemplate);
    }

    private List<AbstractCache> getCacheList(String classNames) throws ClassNotFoundException {
        if (StringUtils.isBlank(classNames)) {
            throw new ClassNotFoundException();
        }
        List<AbstractCache> cacheList = Lists.newArrayList();
        String[] classNamesArray = StringUtils.split(classNames, ",");
        for (String className : classNamesArray) {
            Class<AbstractCache> cacheClass = (Class<AbstractCache>) Class.forName(className);
            AbstractCache cache = ApplicationContextHolder.getBean(cacheClass);
            if (cacheList.contains(cache)) {
                continue;
            }
            cacheList.add(cache);
        }
        return cacheList;
    }

    private List<AbstractCache> getCacheList(String classNames, boolean isLocal) throws ClassNotFoundException {
        if (StringUtils.isBlank(classNames)) {
            throw new ClassNotFoundException();
        }
        List<AbstractCache> cacheList = Lists.newArrayList();
        String[] classNamesArray = StringUtils.split(classNames, ",");
        for (String className : classNamesArray) {
            Class<AbstractCache> cacheClass = (Class<AbstractCache>) Class.forName(className);
            AbstractCache cache = ApplicationContextHolder.getBean(cacheClass);
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

    private CacheImpl getCache(boolean allowNullValues, String cacheName, List<AbstractCache> cacheList) {
        CacheImpl cacheImpl = new CacheImpl(allowNullValues);
        cacheImpl.setName(cacheName);
        cacheImpl.getCaches().addAll(cacheList);
        return cacheImpl;
    }
}
