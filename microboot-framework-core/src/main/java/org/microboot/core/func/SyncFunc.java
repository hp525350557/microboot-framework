package org.microboot.core.func;

/**
 * @author 胡鹏
 *
 * 这个SyncFunc接口提供了两种同步方式，四个同步方法：
 * 1、自旋同步：锁异常的线程会通过自旋重新获取锁对象，确保每个线程都能执行到业务逻辑
 * ① 有返回值
 * ② 无返回值
 *
 * 2、跳过同步：锁异常的线程会直接退出，业务逻辑将被跳过
 * ① 有返回值
 * ② 无返回值
 */
public interface SyncFunc {

    /**
     * 自旋同步：有返回值
     *
     * @param lockKey
     * @param func
     * @param <T>
     * @return
     */
    <T> T spinSync(String lockKey, Func0<T> func);

    /**
     * 自旋同步：无返回值
     *
     * @param lockKey
     * @param func
     */
    void spinSync(String lockKey, FuncV0 func);

    /**
     * 跳过同步：有返回值
     *
     * @param lockKey
     * @param func
     * @param <T>
     * @return
     */
    <T> T skipSync(String lockKey, Func0<T> func);

    /**
     * 跳过同步：无返回值
     *
     * @param lockKey
     * @param func
     */
    void skipSync(String lockKey, FuncV0 func);
}
