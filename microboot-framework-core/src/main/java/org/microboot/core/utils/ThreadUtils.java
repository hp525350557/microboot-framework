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
    private static final int maximumPoolSize = Runtime.getRuntime().availableProcessors();
    private static final long keepAliveTime = 60;
    private static final TimeUnit unit = TimeUnit.SECONDS;

    private static volatile ThreadPoolExecutor executor;

    public static void threadFunc(int queueLength,
                                  int corePoolSize,
                                  int maximumPoolSize,
                                  long keepAliveTime,
                                  TimeUnit unit,
                                  FuncV1<ThreadPoolExecutor> func) throws Exception {
        if (queueLength == 0) {
            return;
        }
        if (executor == null) {
            synchronized (ThreadUtils.class) {
                if (executor == null) {
                    ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(queueLength);
                    executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, new ThreadFactory() {
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
                }
            }
        }

        func.func(executor);
    }

    public static void threadFunc(int queueLength, FuncV1<ThreadPoolExecutor> func) throws Exception {
        threadFunc(queueLength, corePoolSize, maximumPoolSize, keepAliveTime, unit, func);
    }

    public static void threadFunc(FuncV1<ThreadPoolExecutor> func) throws Exception {
        func.func(executor);
    }

    public static void shutdown() throws Exception {
        if (executor.isShutdown()) {
            return;
        }

        executor.shutdown();

        if (!executor.awaitTermination(1, TimeUnit.DAYS)) {
            executor.shutdownNow();
            logger.info("====================所有线程执行完毕====================");
        }
    }

    public static void shutdown(long timeout, TimeUnit unit) throws Exception {
        if (executor.isShutdown()) {
            return;
        }

        executor.shutdown();

        if (!executor.awaitTermination(timeout, unit)) {
            executor.shutdownNow();
            logger.info("====================所有线程执行完毕====================");
        }
    }
}
