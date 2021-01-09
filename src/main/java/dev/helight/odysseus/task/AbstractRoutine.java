package dev.helight.odysseus.task;

import dev.helight.odysseus.registry.Registrable;
import dev.helight.odysseus.registry.Registry;
import dev.helight.odysseus.task.annotation.Routine;
import lombok.SneakyThrows;
import org.apache.commons.compress.utils.Lists;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class AbstractRoutine implements Callable<Boolean>, Registrable {

    public static Registry<AbstractRoutine> registry = new Registry<AbstractRoutine>();

    private final UUID uuid = UUID.randomUUID();
    private AtomicBoolean active = new AtomicBoolean(true);
    private String key;

    public AbstractRoutine() {

    }

    @SneakyThrows
    public void launch() {
        if (this.getClass().isAnnotationPresent(Routine.class)) {
            Routine routine = this.getClass().getAnnotation(Routine.class);
            key = routine.value();

            if (routine.singleton() && !registry.findById(key).isEmpty()) {
                throw new IllegalThreadStateException("Trying to start a thread marked with @SingletonThread while already maintaining a running thread under the same taskid");
            } else {
                register(routine.value());
            }

            if (routine.repeat() > 0L) {
                executeRepeating(routine.delay(), routine.repeat(), true);
            } else {
                executeDelayed(routine.delay());
            }
        } else {
            throw new IllegalArgumentException("Must be annotated with @Routine");
        }
    }

    public abstract void run();

    private void _run(boolean singleAction) {
        run();
        if (singleAction) active.set(false);
    }

    public void register(String key) {
        this.key = key;
        registry.register(this);
    }

    @Override
    public Boolean call() {
        _run(false);
        return active.get();
    }

    public void stop() {
        active.set(false);
        registry.unregister(this);
    }

    public void execute() {
        LightScheduler.instance().execute(() -> _run(true));
        stop();
    }

    public void executeUnblocking() {
        LightScheduler.instance().unblocking(() -> _run(true));
    }

    public void executeDelayed(long delay) {
        LightScheduler.instance().delayed(() -> _run(true), delay);
    }

    public void executeRepeating(long delay, long period, boolean unblock) {
        if (unblock) {
            LightScheduler.instance().repeatingUnoccupied(this, delay, period);
        } else {
            LightScheduler.instance().repeating(this, delay, period);
        }
    }

    public void executeSynchronize() {
        LightScheduler.instance().synchronize(() -> _run(true));
    }

    @SuppressWarnings("UnusedReturnValue")
    public Future<Void> synchronize(Runnable runnable) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        LightScheduler.instance().synchronize(() -> {
            runnable.run();
            future.complete(null);
        });

        return future;
    }

    public static List<AbstractRoutine> getRegisteredTask(String key) {
        List<AbstractRoutine> rList = registry.findById(key);
        return rList == null ? Lists.newArrayList() : rList;
    }

    public static AbstractRoutine getRegisteredTask(UUID id) {
        return registry.findByUuid(id);
    }

    public static List<AbstractRoutine> listRegisteredTasksStartsWith(String format) {
        return registry.stream()
                .filter((routine) -> routine.registeredId().startsWith(format))
                .collect(Collectors.toList());
    }


    @SneakyThrows
    public static void start(AbstractRoutine routine) {
        routine.launch();
    }

    @SneakyThrows
    public static void start(Class<? extends AbstractRoutine> taskClass) {
        AbstractRoutine routine = taskClass.newInstance();
        routine.launch();
    }

    @SneakyThrows
    public static void assureRunning(Class<? extends AbstractRoutine> taskClass) {
        if (taskClass.isAnnotationPresent(Routine.class) ) {
            Routine routine = taskClass.getAnnotation(Routine.class);
            List<AbstractRoutine> rList = registry.findById(routine.value());
            if (rList == null || !rList.isEmpty()) return;
        } else {
            throw new IllegalArgumentException("Routine must be annotated with @Routine");
        }
        taskClass.newInstance().launch();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractRoutine that = (AbstractRoutine) o;

        if (uuid != null ? !uuid.equals(that.uuid) : that.uuid != null) return false;
        return key != null ? key.equals(that.key) : that.key == null;
    }

    @Override
    public int hashCode() {
        int result = uuid != null ? uuid.hashCode() : 0;
        result = 31 * result + (key != null ? key.hashCode() : 0);
        return result;
    }

    @Override
    public String registeredId() {
        return key;
    }

    @Override
    public UUID registeredUuid() {
        return uuid;
    }
}
