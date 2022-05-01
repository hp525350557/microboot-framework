package org.microboot.core.utils;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.google.common.collect.Maps;

import java.util.Map;

/**
 * @author 胡鹏
 */
public class ThreadLocalUtils {

    /*
        问题1：
            ThreadLocal子线程拿不到父线程中设置的值
        解决方案：
            改用InheritableThreadLocal，InheritableThreadLocal可以在子线程初始化时将父线程中设置的值拷贝到子线程
        问题2：
            InheritableThreadLocal只会在子线程初始化时拷贝父线程的值，如果是线程池的话，则子线程无法与父线程同步
        解决方案：
            改用alibaba的com.alibaba.ttl.TransmittableThreadLocal，配合com.alibaba.ttl包下各个封装类解决
            在本工程中，由于出现这个问题的地方是在Hystrix的线程池中（FeignInterceptor）
            所以需要将Hystrix的线程池用com.alibaba.ttl包下的类封装一层
            查阅资料得知，Hystrix的线程池是在HystrixConcurrencyStrategy策略中创建的
            所以我们自定义一个RequestHystrixConcurrencyStrategy继承HystrixConcurrencyStrategy
            并重写了HystrixConcurrencyStrategy的wrapCallable方法，如下：
                @Override
                public <T> Callable<T> wrapCallable(Callable<T> callable) {
                    return super.wrapCallable(TtlCallable.get(callable));
                }
            但是SpringCloud默认使用的是HystrixConcurrencyStrategyDefault这个策略
            想让SpringCloud使用我们自定义的策略则需要在resources文件夹下放一个固定文件：hystrix-plugins.properties
            在文件中添加如下配置：
                hystrix.plugin.HystrixConcurrencyStrategy.implementation=xxx.xxx.xxx.RequestHystrixConcurrencyStrategy
     */
    private static final TransmittableThreadLocal<Map<String, Object>> threadLocal = new TransmittableThreadLocal<>();

    public static void set(Map<String, Object> map) {
        threadLocal.set(map);
    }

    public static Map<String, Object> get() {
        Map<String, Object> map = ThreadLocalUtils.threadLocal.get();
        if (map == null) {
            map = Maps.newHashMap();
            set(map);
        }
        return map;
    }

    public static void remove() {
        ThreadLocalUtils.threadLocal.remove();
    }
}
