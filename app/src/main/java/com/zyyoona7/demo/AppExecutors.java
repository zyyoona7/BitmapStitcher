package com.zyyoona7.demo;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Global executor pools for the whole application.
 * <p>
 * Grouping tasks like this avoids the effects of task starvation (e.g. disk reads don't wait behind
 * webservice requests).
 */
public class AppExecutors {

    private static final int THREAD_COUNT = 3;

    //单线程的线程池
    private final Executor diskIO;
    //多线程的线程池
    private final Executor networkIO;
    //主线程线程池
    private final Executor mainThread;

    private static volatile AppExecutors sAppExecutors;

    private static AppExecutors getInstance() {
        if (sAppExecutors == null) {
            synchronized (AppExecutors.class) {
                if (sAppExecutors == null) {
                    sAppExecutors = new AppExecutors();
                }
            }
        }
        return sAppExecutors;
    }

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    private AppExecutors() {
        this(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(THREAD_COUNT),
                new MainThreadExecutor());
    }

    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    /**
     * 运行在单线程的线程池中
     *
     * @param runnable runnable
     */
    public static void runOnDiskIO(Runnable runnable) {
        getInstance().diskIO().execute(runnable);
    }

    /**
     * 运行在三个线程的线程池中，可并发
     *
     * @param runnable runnable
     */
    public static void runOnNetworkIO(Runnable runnable) {
        getInstance().networkIO().execute(runnable);
    }

    /**
     * 运行在主线程吃
     *
     * @param runnable runnable
     */
    public static void runOnMainThread(Runnable runnable) {
        getInstance().mainThread().execute(runnable);
    }

    /**
     * 在工作线程执行，还可以切换到主线程
     *
     * @param runnable     worker runnable
     * @param mainRunnable main runnable
     */
    public static void runOnDiskIOPostUI(@NonNull Runnable runnable, @NonNull Runnable mainRunnable) {
        runOnDiskIO(() -> {
            runnable.run();
            runOnMainThread(mainRunnable);
        });
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}