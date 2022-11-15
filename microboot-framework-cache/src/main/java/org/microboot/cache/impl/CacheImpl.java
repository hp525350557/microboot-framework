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
 * 首先：
 * 一开始是在AbstractCache中实现多线程同步，但由于缓存的真正入口是CacheImpl
 * 在CacheImpl对缓存的增删改方法中，会对缓存集合中每个缓存组件循环调用
 * 如果只在AbstractCache中做同步，无法保证缓存CacheImpl的增删改的原子性操作
 * 其次：
 * 在AbstractCache中做同步操作，当缓存集合中有多个集合时，每次进入一个缓存组件都要加锁
 * 多次加锁操作（系统调用）造成性能浪费，这里的多次加锁与锁重入是不同的
 * 因为每个AbstractCache的方法执行完之后就释放锁了，其他缓存组件虽然在同一个线程中调用，但是会重新加锁
 * 而且每个缓存组件的name属性其实都是各自命名的，所以各缓存组件之间也肯定不会获取到同一个伴生锁
 * 解决：
 * 最终解决方案就是把同步控制放到了CacheImpl这个总入口中来
 * 既提高了性能，又解决了线程安全性问题
 *
 * 这里做了多线程同步处理，但只针对同应用内多线程访问相同的key才会上锁，类似伴生锁概念
 * 目的是为了解决缓存击穿的问题，同时也是实现缓存的线程安全
 * 比如：多个线程同时缓存同一个key的值，但是线程间的数据可能不一致，这就会存在缓存安全性问题
 * 虽然在这里加了锁也不一定完全了解决，至少将这种可能性排除在缓存之外，交给业务端处理
 *
 * 下面举个例子说明，为什么不直接用转换成字符串后的newKey作为synchronized的锁对象
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
 *         new Thread(() -> {
 *             synchronized (s2) {
 *                 try {
 *                     Thread.sleep(2000);
 *                 } catch (InterruptedException e) {
 *                     e.printStackTrace();
 *                 }
 *                 System.out.println(Thread.currentThread().getName());
 *             }
 *         },"Thread2").start();
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
 *
 *         @Override
 *         public String toString() {
 *             return "Obj{" +
 *                     "key='" + key + '\'' +
 *                     '}';
 *         }
 *     }
 * }
 *
 * TODO 老版本的lockMap使用的是普通Object做为锁对象，
 *      测试发现仍然会有线程安全性问题
 *      新版本使用ReentrantLock代替synchronized(Object对象)
 *
 * TODO 老版本是直接实现Cache接口
 *      新版本参考了RedisCache后，改为继承AbstractValueAdaptingCache
 *      并对一系列列方法做了进一步的优化和调整
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
        String newKey = KeyUtils.newKey(this.name, key);
        ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            if (key == null) {
                return;
            }
            for (Cache cache : this.caches) {
                cache.evict(key);
            }
        });
    }

    /**
     * 可配置开启缓存null值【默认关闭】
     *
     * @param key
     * @return
     */
    @Override
    protected Object lookup(Object key) {
        if (key == null) {
            return null;
        }
        Object cacheValue;
        ValueWrapper valueWrapper;
        //记录空值Cache，并在返回数据之前，填充所有空值Cache
        List<Cache> nullValueCaches = Lists.newArrayList();
        for (Cache cache : this.caches) {
            valueWrapper = cache.get(key);
            if (valueWrapper == null) {
                nullValueCaches.add(cache);
                continue;
            }
            cacheValue = valueWrapper.get();
            this.nullValueCachesPut(nullValueCaches, key, cacheValue);
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
        String newKey = KeyUtils.newKey(this.name, key);
        return ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            if (key == null) {
                return null;
            }
            Object cacheValue;
            ValueWrapper valueWrapper;
            //记录空值Cache，并在返回数据之前，填充所有空值Cache
            List<Cache> nullValueCaches = Lists.newArrayList();
            for (Cache cache : this.caches) {
                valueWrapper = cache.get(key);
                if (valueWrapper == null) {
                    nullValueCaches.add(cache);
                    continue;
                }
                cacheValue = valueWrapper.get();
                //1、cacheValue == NullValue.INSTANCE：说明开启缓存null值，由于返回值类型是T，因此返回null【防止缓存穿透】
                if (cacheValue == NullValue.INSTANCE) {
                    this.nullValueCachesPut(nullValueCaches, key, cacheValue);
                    return null;
                }
                //2、cacheValue != null：说明缓存中有数据，那么直接强转返回
                if (cacheValue != null) {
                    this.nullValueCachesPut(nullValueCaches, key, cacheValue);
                    return (T) cacheValue;
                }
            }
            //3、cacheValue == null：说明缓存中没有数据，那么此时就需要执行业务方法拿到返回值
            T value = callable.call();
            //4、value != null：则可以将数据缓存下来，并直接返回了
            if (value != null) {
                this.nullValueCachesPut(nullValueCaches, key, value);
                return value;
            }
            //5、value == null：说明业务数据也是null，那么通过preProcessCacheValue方法处理一下
            cacheValue = this.preProcessCacheValue(null);
            /*
                6、cacheValue != null：说明开启缓存null值，此时cacheValue一定等于NullValue.INSTANCE
                7、cacheValue == null：说明未开启缓存null值
             */
            if (cacheValue != null) {
                this.nullValueCachesPut(nullValueCaches, key, cacheValue);
            }
            return null;
        });
    }

    @Override
    public void put(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            if (key == null) {
                return;
            }
            Object cacheValue = this.preProcessCacheValue(value);
            for (Cache cache : this.caches) {
                cache.put(key, cacheValue);
            }
        });
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        return ApplicationContextHolder.getBean(SyncFunc.class.getName(), SyncFunc.class).spinSync(newKey, () -> {
            if (key == null) {
                return null;
            }
            ValueWrapper valueWrapper;
            //记录空值Cache，并在返回数据之前，填充所有空值Cache
            List<Cache> nullValueCaches = Lists.newArrayList();
            /*
                putIfAbsent方法的语义：
                    当缓存中有对应数据，则直接返回，不做插入操作
                    当缓存中没有对应数据，则将key-value存入缓存，并返回null
                下面是实现逻辑：
                    当valueWrapper != null时：将valueWrapper的值填充所有空值Cache集合，并返回valueWrapper
                    当valueWrapper == null时：将cacheValue填充所有空值Cache集合，并返回null，当前cache忽略
             */
            for (Cache cache : this.caches) {
                valueWrapper = cache.get(key);
                if (valueWrapper == null) {
                    nullValueCaches.add(cache);
                    continue;
                }
                this.nullValueCachesPut(nullValueCaches, key, valueWrapper.get());
                return valueWrapper;
            }
            Object cacheValue = this.preProcessCacheValue(value);
            this.nullValueCachesPut(nullValueCaches, key, cacheValue);
            return null;
        });
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Cache> getCaches() {
        return caches;
    }

    private void nullValueCachesPut(List<Cache> nullValueCaches, Object key, Object value) {
        if (CollectionUtils.isEmpty(nullValueCaches)) {
            return;
        }
        for (Cache cache : nullValueCaches) {
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
