package org.microboot.core.bean;

import com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.func.FuncV0;
import org.microboot.core.func.SyncFunc;
import org.microboot.core.func.Func0;
import org.microboot.core.utils.LoggerUtils;

import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author 胡鹏
 *
 * 1、DefaultSyncFuncHolder实现了SyncFunc接口，但只适用于进程内的同步
 * 2、microboot框架缓存默认使用DefaultSyncFuncHolder处理线程同步
 * 3、如果使用微服务架构，则要用到分布式锁，需自定义实现SyncFunc接口，并覆盖默认DefaultSyncFuncHolder，如下：
 * @Bean
 * public SyncFunc initSyncFunc() {
 *     return new YourSyncFunc();
 * }
 */
public class DefaultSyncFuncHolder implements SyncFunc {

    private final Logger logger = LogManager.getLogger(this.getClass());

    private final Map<String, ReentrantLock> lockMap = Maps.newConcurrentMap();

    /**
     * 自旋同步：有返回值
     *
     * @param lockKey
     * @param func
     * @param <T>
     * @return
     */
    @Override
    public <T> T spinSync(String lockKey, Func0<T> func) {
        ReentrantLock lock = spinLock(lockKey);
        try {
            return func.func();
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        } finally {
            if (lock != null) {
                if (lock.getQueueLength() == 0) {
                    lockMap.remove(lockKey, lock);
                }
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * 自旋同步：无返回值
     *
     * @param lockKey
     * @param func
     */
    @Override
    public void spinSync(String lockKey, FuncV0 func) {
        ReentrantLock lock = spinLock(lockKey);
        try {
            func.func();
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        } finally {
            if (lock != null) {
                if (lock.getQueueLength() == 0) {
                    lockMap.remove(lockKey, lock);
                }
                lock.unlock();
            }
        }
    }

    /**
     * 跳过同步：有返回值
     *
     * @param lockKey
     * @param func
     * @param <T>
     * @return
     */
    @Override
    public <T> T skipSync(String lockKey, Func0<T> func) {
        ReentrantLock lock = skipLock(lockKey);
        if (lock == null) {
            return null;
        }
        try {
            return func.func();
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        } finally {
            if (lock != null) {
                if (lock.getQueueLength() == 0) {
                    lockMap.remove(lockKey, lock);
                }
                lock.unlock();
            }
        }
        return null;
    }

    /**
     * 跳过同步：无返回值
     *
     * @param lockKey
     * @param func
     */
    @Override
    public void skipSync(String lockKey, FuncV0 func) {
        ReentrantLock lock = skipLock(lockKey);
        if (lock == null) {
            return;
        }
        try {
            func.func();
        } catch (Exception e) {
            LoggerUtils.error(logger, e);
        } finally {
            if (lock != null) {
                if (lock.getQueueLength() == 0) {
                    lockMap.remove(lockKey, lock);
                }
                lock.unlock();
            }
        }
    }

    /**
     * 自旋锁
     *
     * @param lockKey
     * @return
     */
    private ReentrantLock spinLock(String lockKey) {
        ReentrantLock oldLock = null;
        ReentrantLock newLock;
        do {
            if (oldLock != null) {
                oldLock.unlock();
            }

            oldLock = lockMap.computeIfAbsent(lockKey, k -> new ReentrantLock());

            oldLock.lock();

            /*
                如果newLock为null或者newLock != oldLock
                则表示有其他线程将lockMap中的锁删除或者替换了
                假设现在有三个线程：t1，t2，t3，如果没有 do...while (newLock == null || newLock != oldLock) 判断
                    1、t1 执行到 if (oldLock.getQueueLength() == 0)
                    2、t2 获取oldLock（与t1相同），暂未执行oldLock.lock()
                    3、t1 执行lockMap.remove(newKey, oldLock)
                    4、t3 执行oldLock = lockMap.computeIfAbsent(newKey, k -> new ReentrantLock())，此时得到新的锁
                    5、t2与t3将并发执行
                因此需要判断oldLock 与 newKey是否一致，只有一致才往下执行，否则重新获取锁
             */
            newLock = lockMap.get(lockKey);
        } while (newLock == null || newLock != oldLock);

        return oldLock;
    }

    /**
     * 跳过锁
     *
     * @param lockKey
     * @return
     */
    private ReentrantLock skipLock(String lockKey) {
        ReentrantLock oldLock = lockMap.computeIfAbsent(lockKey, k -> new ReentrantLock());

        oldLock.lock();

        /*
            如果newLock为null或者newLock != oldLock
            则表示有其他线程将lockMap中的锁删除或者替换了
            假设现在有三个线程：t1，t2，t3，如果没有 if (newLock == null || newLock != oldLock) 判断
                1、t1 执行到 if (oldLock.getQueueLength() == 0)
                2、t2 获取oldLock（与t1相同），暂未执行oldLock.lock()
                3、t1 执行lockMap.remove(newKey, oldLock)
                4、t3 执行oldLock = lockMap.computeIfAbsent(newKey, k -> new ReentrantLock())，此时得到新的锁
                5、t2与t3将并发执行
            因此需要判断oldLock 与 newKey是否一致，只有一致才往下执行，否则放弃本次执行
         */
        ReentrantLock newLock = lockMap.get(lockKey);

        if (newLock == null || newLock != oldLock) {
            oldLock.unlock();
            return null;
        }

        return oldLock;
    }
}