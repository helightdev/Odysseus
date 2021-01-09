package dev.helight.odysseus.task;

import dev.helight.odysseus.Odysseus;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;

import java.util.concurrent.*;

public class LightScheduler {

    public static final int POOL_SIZE = 4;

    public static LightScheduler instance;

    private ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
    private ExecutorService freeing = Executors.newCachedThreadPool();

    //Unused but reserved for eventual runtime annotation processing
    @Getter(AccessLevel.PROTECTED)
    private final ExecutorService scheduler = Executors.newCachedThreadPool();

    public LightScheduler() {
        instance = this;
    }

    @SneakyThrows
    public void rebuildPool() {
        pool.shutdownNow();
        freeing.shutdownNow();
        pool = Executors.newFixedThreadPool(POOL_SIZE);
        freeing = Executors.newCachedThreadPool();
    }

    public static LightScheduler instance() {
        return instance != null ? instance : new LightScheduler();
    }

    public void synchronize(Runnable runnable) {
        Bukkit.getScheduler().runTask(Odysseus.getPlugin(), runnable);
    }
    public void synchronize(Runnable runnable, long ticks) {
        Bukkit.getScheduler().runTaskLater(Odysseus.getPlugin(), runnable, ticks);
    }


    public void execute(Runnable runnable) {
        pool.execute(runnable);
    }

    public void unblocking(Runnable runnable) {
        freeing.execute(runnable);
    }

    public void delayed(Runnable runnable, long delay) {
        execute(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runnable.run();
        });
    }

    public void repeating(Callable<Boolean> callable, long delay, long period) {
        execute(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    if (!callable.call()) break;
                    Thread.sleep(period);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void repeatingUnoccupied(Callable<Boolean> callable, long delay, long period) {
        freeing.execute(() -> {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            while (true) {
                try {
                    Future<Boolean> future = pool.submit(callable);
                    if (!future.get()) break;
                    Thread.sleep(period);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Future<LightScheduler> submit(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, pool).thenApply(r -> this);
    }
}
