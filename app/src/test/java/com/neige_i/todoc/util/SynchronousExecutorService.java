package com.neige_i.todoc.util;

import androidx.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SynchronousExecutorService implements ExecutorService {

    private boolean shutdown;

    @Override
    public void shutdown() {
        shutdown = true;
    }

    @NonNull
    @Override
    public List<Runnable> shutdownNow() {
        return Collections.emptyList();
    }

    @Override
    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public boolean isTerminated() {
        return shutdown;
    }

    @Override
    public boolean awaitTermination(long timeout, @NonNull TimeUnit unit) {
        return true;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull Callable<T> task) {
        throw new IllegalStateException("Not supported !");
    }

    @NonNull
    @Override
    public <T> Future<T> submit(@NonNull Runnable task, T result) {
        throw new IllegalStateException("Not supported !");
    }

    @NonNull
    @Override
    public Future<?> submit(@NonNull Runnable task) {
        throw new IllegalStateException("Not supported !");
    }

    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks) throws InterruptedException {
        throw new IllegalStateException("Not supported !");
    }

    @NonNull
    @Override
    public <T> List<Future<T>> invokeAll(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws InterruptedException {
        throw new IllegalStateException("Not supported !");
    }

    @NonNull
    @Override
    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
        throw new IllegalStateException("Not supported !");
    }

    @Override
    public <T> T invokeAny(@NonNull Collection<? extends Callable<T>> tasks, long timeout, @NonNull TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        throw new IllegalStateException("Not supported !");
    }
}
