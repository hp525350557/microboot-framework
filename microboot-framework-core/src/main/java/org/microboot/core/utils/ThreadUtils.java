package org.microboot.core.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.microboot.core.func.FuncV1;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 胡鹏
 */
public class ThreadUtils {

    private static final Logger logger = LogManager.getLogger(ThreadUtils.class);

    private static final int corePoolSize = 2;
    private static final int maximumPoolSize = 2;
    private static final long keepAliveTime = 60;
    private static final TimeUnit unit = TimeUnit.SECONDS;

    public static void threadFunc(int queueLength,
                                  int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  FuncV1<ThreadPoolExecutor> func) throws Exception {
        if (queueLength == 0) {
            return;
        }
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueLength);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, new ThreadFactory() {
            private final AtomicInteger threadNum = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "Thread-" + threadNum.getAndIncrement());
                //创建守护线程【主线程结束就结束】
                thread.setDaemon(true);
                thread.setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                    /*
                        此处捕捉的是worker的异常，不是任务的异常
                        在线程池的worker线程中执行的是被包装之后的FutureTask
                        在FutureTask中执行的才是我们的任务，而在FutureTask中将任务的异常捕捉了
                     */
                    queue.clear();
                    LoggerUtils.error(logger, e);
                });
                return thread;
            }
        }, (Runnable r, ThreadPoolExecutor poolExecutor) -> {
            logger.info(r.toString() + "：被添加到队列等待执行");
            logger.info("当前队列中存在" + poolExecutor.getQueue().size() + "个任务等待执行");
            try {
                poolExecutor.getQueue().put(r);
            } catch (InterruptedException e) {
                LoggerUtils.error(logger, e);
            }
        });
        func.func(executor);
        executor.shutdown();
        while (true) {
            if (executor.isTerminated()) {
                logger.info("====================所有线程执行完毕====================");
                break;
            }
        }
    }

    public static void threadFunc(int queueLength, FuncV1<ThreadPoolExecutor> func) throws Exception {
        if (queueLength == 0) {
            return;
        }
        ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueLength);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, new ThreadFactory() {
            private final AtomicInteger threadNum = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable, "Thread-" + threadNum.getAndIncrement());
                //创建守护线程【主线程结束就结束】
                thread.setDaemon(true);
                thread.setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                    /*
                        此处捕捉的是worker的异常，不是任务的异常
                        在线程池的worker线程中执行的是被包装之后的FutureTask
                        在FutureTask中执行的才是我们的任务，而在FutureTask中将任务的异常捕捉了
                     */
                    queue.clear();
                    LoggerUtils.error(logger, e);
                });
                return thread;
            }
        }, (Runnable r, ThreadPoolExecutor poolExecutor) -> {
            logger.info(r.toString() + "：被添加到队列等待执行");
            logger.info("当前队列中存在" + poolExecutor.getQueue().size() + "个任务等待执行");
            try {
                poolExecutor.getQueue().put(r);
            } catch (InterruptedException e) {
                LoggerUtils.error(logger, e);
            }
        });
        func.func(executor);
        executor.shutdown();
        while (true) {
            if (executor.isTerminated()) {
                logger.info("====================所有线程执行完毕====================");
                break;
            }
        }
    }
}
