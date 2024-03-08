package rpc.common.utils.concurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class ThreadPoolFactoryUtil {
    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    private ThreadPoolFactoryUtil() {}

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadName) {
        ThreadPoolConfig config = new ThreadPoolConfig();
        return createCustomThreadPoolIfAbsent(threadName, config, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadName, ThreadPoolConfig config) {
        return createCustomThreadPoolIfAbsent(threadName, config, false);
    }

    public static ExecutorService createCustomThreadPoolIfAbsent(String threadName, ThreadPoolConfig config, Boolean daemon) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadName, k -> createThreadPool(threadName, config, daemon));

        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadName);
            threadPool = createThreadPool(threadName, config, daemon);
            THREAD_POOLS.put(threadName, threadPool);
        }

        return threadPool;
    }

    public static boolean shutDownAllThreadPool() {
        AtomicBoolean check = new AtomicBoolean(true);

        log.info("Calling shutDownAllThreadPool method.");
        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("Shutting down thread pool [{}] [{}]", entry.getKey(), executorService.isTerminated());

            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.error("Cannot shutdown thread pool [{}]", entry.getKey());
                executorService.shutdownNow();
                check.set(false);
            }
        });

        return check.get();
    }

    private static ExecutorService createThreadPool(String threadName, ThreadPoolConfig config, Boolean daemon) {
        ThreadFactory threadFactory = createThreadFactory(threadName, daemon);
        return new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaximumPoolSize(),
                config.getKeepAliveTime(), config.getUnit(), config.getWorkQueue(), threadFactory);
    }

    public static ThreadFactory createThreadFactory(String threadName, Boolean daemon) {
        if (Optional.ofNullable(threadName).isPresent() && !threadName.isEmpty()) {
            if (Optional.ofNullable(daemon).isPresent()) {
                return new ThreadFactoryBuilder()
                        .setNameFormat(threadName + "-%d")
                        .setDaemon(daemon)
                        .build();
            } else {
                return new ThreadFactoryBuilder().setNameFormat(threadName + "-%d").build();
            }
        }

        return Executors.defaultThreadFactory();
    }

    public static void printThreadPoolStatus(ThreadPoolExecutor threadPool) {
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("============Thread pool status============");
            log.info("Thread pool size: [{}]", threadPool.getPoolSize());
            log.info("Active threads: [{}]", threadPool.getActiveCount());
            log.info("Number of tasks: [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of tasks in queue: [{}]", threadPool.getQueue().size());
            log.info("==========================================");
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void printThreadPoolStatus(String threadName) {
        if (!THREAD_POOLS.containsKey(threadName)) {
            return;
        }
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) THREAD_POOLS.get(threadName);
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, createThreadFactory("print-thread-pool-status", false));
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            log.info("============Thread pool status============");
            log.info("Thread pool name: [{}]", threadName);
            log.info("Thread pool size: [{}]", threadPool.getPoolSize());
            log.info("Active threads: [{}]", threadPool.getActiveCount());
            log.info("Number of tasks: [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of tasks in queue: [{}]", threadPool.getQueue().size());
            log.info("==========================================");
        }, 1, 1, TimeUnit.SECONDS);
    }

    public static void printAllThreadPoolStatus() {

        log.info("============All Thread pools============");


        THREAD_POOLS.entrySet().parallelStream().forEach(entry -> {
            ThreadPoolExecutor threadPool = (ThreadPoolExecutor) entry.getValue();

            log.info("Thread pool name: [{}]", entry.getKey());
            log.info("Thread pool size: [{}]", threadPool.getPoolSize());
            log.info("Active threads: [{}]", threadPool.getActiveCount());
            log.info("Number of tasks: [{}]", threadPool.getCompletedTaskCount());
            log.info("Number of tasks in queue: [{}]", threadPool.getQueue().size());

            log.info("----------------------------------------");
        });

        log.info("========================================");



    }

    public static void main(String[] args) {
        ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("test");
        ThreadPoolFactoryUtil.createCustomThreadPoolIfAbsent("test2");
//        ThreadPoolFactoryUtil.printThreadPoolStatus("test");
        ThreadPoolFactoryUtil.printAllThreadPoolStatus();
        ThreadPoolFactoryUtil.shutDownAllThreadPool();
        ThreadPoolFactoryUtil.printAllThreadPoolStatus();
        
    }
}

