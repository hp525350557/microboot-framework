package org.microboot.cache.impl;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.microboot.cache.utils.KeyUtils;
import org.microboot.core.bean.ApplicationContextHolder;
import org.microboot.core.func.SyncFunc;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.NullValue;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author 胡鹏
 *
 * 一个CacheImpl对象代表一条缓存线，目前microboot内置了三条缓存线，分别是：
 * 1、通用缓存：混合模式，可包含本地缓存，中央缓存
 * 2、本地缓存：单一模式，只包含本地缓存
 * 3、中央缓存：单一模式，只包含中央缓存
 *
 * 老版本：
 * 一开始是在AbstractCache中实现多线程同步，但由于缓存的真正入口是CacheImpl
 * 在CacheImpl对缓存的增删改方法中，会对缓存集合中每个缓存组件循环调用
 * 如果只在AbstractCache中做同步，无法保证缓存CacheImpl的增删改的原子性操作
 * 而且只在AbstractCache中做同步，每个缓存实例都要加一次锁，多次加锁操作（系统调用）造成极大的性能损耗
 * 现版本：
 * 把加锁的操作放到CacheImpl这个缓存总入口中来
 * 既确保了性能，又解决了线程安全性问题
 *
 * 问题1：
 * 虽然在CacheImpl中做了加锁处理，但默认情况下SyncFunc的实现类是DefaultSyncFuncHolder
 * 它只对同进程内多线程访问相同的key才会上锁（类似伴生锁的概念）
 * 对于分布式或微服务需要用户自己基于SyncFunc接口实现分布式锁并注入到Spring容器
 *
 * 问题2：
 * 在CacheImpl中加锁的目的：一是为了解决缓存击穿的问题，二是为了解决缓存内部的线程安全性问题
 * 但是缓存与业务方法之间还存在数据一致性问题
 * 说明：
 * 多个线程同时更新数据库中某一行数据并缓存相同key的数据，由于线程间执行速度不一致，造成缓存与数据库的数据不一致问题
 * 这不是在缓存里加锁能解决的，只能交给业务端去处理
 * 举例：业务方法使用@CachePut注解标注
 * 1、线程A执行业务方法，并将数据库中id = '1'这行数据的name字段改为'张三'，同时以id作为key缓存该行数据
 * 2、线程B执行业务方法，并将数据库中id = '1'这行数据的name字段改为'李四'，同时以id作为key缓存该行数据
 * 假设某个瞬间的执行顺序是这样：线程A提交事务 -> 线程B提交事务 -> 线程B缓存数据 -> 线程A缓存数据
 * 此时数据库中id = '1'这行数据的name被更新为'李四'（线程B后提交）
 * 此时缓存中id = '1'这行数据的name被更新为'张三'（线程A后缓存）
 * 原因：
 * 在Spring的Cache接口中定义了get(Object key, Callable<T> valueLoader)这样的方法
 * 所以get方法可以把缓存和业务方法包含在锁的作用范围内，确保缓存和业务方法的原子性（Callable<T> valueLoader就是我们业务方法的代理）
 * 但Spring的Cache接口中并没有类似的put方法
 * 所以put方法无法把缓存和业务方法包含在锁的作用范围内，缓存和业务方法在多线程环境下天然就不是原子性的
 * 解决方案：
 * 1、通过Spring容器获取CacheImpl的bean，然后在业务方法中调用put方法手动更新缓存，原理是通过数据库的锁来实现同步（不推荐）
 * 2、在调用业务方法的地方加锁，让业务方法和缓存成为一个整体，确保原子性（推荐）
 *
 * 问：为什么不直接用转换成字符串后的newKey作为synchronized的锁对象来实现伴生锁？
 * public class Obj2StringTest {
 *     public static void main(String[] args) {
 *         Obj key1 = new Obj();
 *         Obj key2 = new Obj();
 *
 *         String s1 = nullSafeToString(key1);
 *         String s2 = nullSafeToString(key2);
 *
 *         //两个不同的对象，转换出来的字符串不是同一个对象，因此synchronized(s1)和synchronized(s2)不会互斥
 *         System.out.println(s1 + "<==>" + s2);
 *         System.out.println(s1 == s2); //结果输出false
 *
 *         new Thread(() -> {
 *             synchronized (s1) {
 *                 try {
 *                     Thread.sleep(2000);
 *                 } catch (InterruptedException e) {
 *                     e.printStackTrace();
 *                 }
 *                 System.out.println(Thread.currentThread().getName());
 *             }
 *         },"Thread1").start();
 *
 *          new Thread(() -> {
 *              synchronized (s2) {
 *                  try {
 *                      Thread.sleep(2000);
 *                  } catch (InterruptedException e) {
 *                      e.printStackTrace();
 *                  }
 *                  System.out.println(Thread.currentThread().getName());
 *              }
 *          },"Thread2").start();
 *
 *         //但是在map中的key是通过字符串值来进行取值的，所以，即使字符串对象不是同一个，只要值一致，就可以获得到数据
 *         Map<String, Object> map1 = Maps.newConcurrentMap();
 *         //测试1
 *         map1.put(s1, 1);
 *         Object v1 = map1.get(s2);
 *         System.out.println(v1);
 *
 *         //测试2
 *         Map<String, Object> map2 = Maps.newConcurrentMap();
 *         map2.put(new String("key"), 2);
 *         Object v2 = map2.get(new String("key"));
 *         System.out.println(v2);
 *     }
 *
 *     static class Obj {
 *         private String key = "key";
 *         @Override
 *         public String toString() {
 *             return "Obj{" +
 *                         "key='" + key + '\'' +
 *                     '}';
 *         }
 *     }
 * }
 *
 * 老版本的lockMap使用的是普通Object做为锁对象，
 * 测试发现仍然会有线程安全性问题
 * 新版本使用ReentrantLock代替synchronized(Object对象)
 *
 * 老版本是直接实现Cache接口
 * 新版本参考了RedisCache后，改为继承AbstractValueAdaptingCache
 * 并对一系列列方法做了进一步的优化和调整
 *
 * CacheImpl和AbstractCache没有直接关系
 * CacheImpl是microboot的缓存入口，配置文件中的缓存组合就保存在CacheImpl中
 * AbstractCache是内置各个缓存组件的父类，它规范和统一了microboot各缓存组件的实现方法
 * CacheImpl中调用的都是AbstractCache各子类实现，即：各缓存组件
 * 所以CacheImpl和AbstractCache应该算是组合关系【PS：AbstractLocalCache也是缓存组件的父类，特指本地缓存】
 */
public class CacheImpl extends AbstractValueAdaptingCache {

    private String name;

    private final List<Cache> caches = Lists.newArrayList();

    public CacheImpl(boolean allowNullValues) {
        super(allowNullValues);
    }

    @Override
    public void clear() {
        for (Cache cache : this.caches) {
            cache.clear();
        }
    }

    @Override
    public void evict(Object key) {
        if (key == null) {
            return;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            for (Cache cache : this.caches) {
                cache.evict(newKey);
            }
        });
    }

    /**
     * 可配置开启缓存null值【默认关闭】
     *
     * lookup的返回值有三种可能，分别是：
     * 1、null：在get方法中会返回null
     * 2、NullValue.INSTANCE：在get方法中会被封装成SimpleValueWrapper(null)，这个值只会在开启缓存null的情况下出现，
     * 3、业务数据：在get方法中会被封装成SimpleValueWrapper(Object)
     *
     * @param key
     * @return
     */
    @Override
    protected Object lookup(Object key) {
        if (key == null) {
            return null;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        //记录空值Cache，并在返回数据之前，填充所有空值Cache
        List<Cache> nullValueCaches = Lists.newArrayList();
        for (Cache cache : this.caches) {
            ValueWrapper valueWrapper = cache.get(newKey);
            if (valueWrapper == null) {
                nullValueCaches.add(cache);
                continue;
            }
            Object cacheValue = valueWrapper.get();
            this.cachesPut(nullValueCaches, newKey, cacheValue);
            return cacheValue;
        }
        return null;
    }

    /**
     * 可配置开启缓存null值【默认关闭】
     *
     * 参考了RedisCache的实现
     * RedisCache采用的是方法级的synchronized来实现同步
     * 通过@Cacheable的sync参数来控制是否需要加锁
     * 其他的get或lookup方法则默认是线程不安全的
     *
     * CacheImpl采用的是类似伴生锁的概念，以缓存key作为锁对象。不同key并行，相同key互斥
     * 【内置只实现了单进程内的锁，如果是分布式系统，请自己实现SyncFunc接口，然后将实现类的bean注入spring即可】
     *
     * @param key
     * @param callable
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(Object key, Callable<T> callable) {
        if (key == null) {
            return null;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        return ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            /*
                valueWrapper有三种可能：
                    1、null：多级缓存中没有业务数据，执行业务方法
                    2、SimpleValueWrapper(null)：多级缓存中缓存了NullValue.INSTANCE，返回null
                    3、SimpleValueWrapper(Object)：多级缓存中缓存了业务数据，返回业务数据
                注意：这里get方法的参数必须用key，不能用newKey，因为get方法会调用lookup方法，在lookup方法中将key转换成了newKey
             */
            ValueWrapper valueWrapper = this.get(key);
            if (valueWrapper != null) {
                return (T) valueWrapper.get();
            }
            //执行业务方法
            T value = callable.call();
            //value != null：则可以将数据缓存下来，并返回value
            if (value != null) {
                this.cachesPut(this.caches, newKey, value);
                return value;
            }
            //value == null：则通过preProcessCacheValue方法处理一下
            Object cacheValue = this.preProcessCacheValue(null);
            /*
                1、cacheValue != null：说明开启缓存null值，此时cacheValue等于NullValue.INSTANCE
                2、cacheValue == null：说明未开启缓存null值
             */
            if (cacheValue != null) {
                this.cachesPut(this.caches, newKey, cacheValue);
            }
            /*
                执行到此处，说明以下两点：
                    1、缓存中没有业务数据
                    2、业务方法返回值是null
                那么无论是否开启缓存null值，都应该给客户端返回一个null
                开启缓存null，只是在缓存中将null进行特殊处理后存储，避免缓存穿透
             */
            return null;
        });
    }

    @Override
    public void put(Object key, Object value) {
        if (key == null) {
            return;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            Object cacheValue = this.preProcessCacheValue(value);
            this.cachesPut(this.caches, newKey, cacheValue);
        });
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (key == null) {
            return null;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        return ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            /*
                putIfAbsent方法的语义：
                    当缓存中有数据，则忽略新值，返回老值
                    当缓存中没有数据，则缓存新值，返回null
                valueWrapper有三种可能：
                    1、null：多级缓存中没有业务数据，缓存新值，返回null
                    2、SimpleValueWrapper(null)：多级缓存中缓存了NullValue.INSTANCE，缓存新值，返回null
                    3、SimpleValueWrapper(Object)：多级缓存中缓存了业务数据，忽略新值，返回老值
                注意：这里get方法的参数必须用key，不能用newKey，因为get方法会调用lookup方法，在lookup方法中将key转换成了newKey
             */
            ValueWrapper valueWrapper = this.get(key);
            if (valueWrapper == null || valueWrapper.get() == null) {
                Object cacheValue = this.preProcessCacheValue(value);
                this.cachesPut(this.caches, newKey, cacheValue);
                return null;
            }
            return valueWrapper;
        });
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cache> getCaches() {
        return caches;
    }

    private void cachesPut(List<Cache> caches, Object key, Object value) {
        if (CollectionUtils.isEmpty(caches)) {
            return;
        }
        for (Cache cache : caches) {
            cache.put(key, value);
        }
    }

    private Object preProcessCacheValue(Object value) {
        if (value != null) {
            return value;
        }
        return isAllowNullValues() ? NullValue.INSTANCE : null;
    }
}
