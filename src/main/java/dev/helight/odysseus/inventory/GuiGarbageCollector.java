package dev.helight.odysseus.inventory;

import dev.helight.odysseus.inventory.annotation.NoGarbageCollection;
import dev.helight.odysseus.task.AbstractRoutine;
import dev.helight.odysseus.task.LightScheduler;
import dev.helight.odysseus.task.annotation.Routine;
import lombok.extern.java.Log;

@Routine(value = "Core.Gui.GarbageCollector", singleton = true, repeat = 1000)
@Log
public class GuiGarbageCollector extends AbstractRoutine {

    private static final long EVICTION_TIMEOUT = 1000000000L * 10;

    public GuiGarbageCollector() {
        executeRepeating(0, 1000, true);
    }

    public void collect() {
        for (Gui value : Gui.cache.values()) {
            if (value.getClass().isAnnotationPresent(NoGarbageCollection.class)) {
                continue;
            }
            long delay = System.nanoTime() - value.getLastAction();
            if (delay >= EVICTION_TIMEOUT) {
                log.info(String.format("GuiGarbageCollector is closing inventory with id %s for being inactive too long", value.getId().toString()));
                synchronize(() -> value.dispose(Gui.DisposeReason.GARBAGE_COLLECTOR));
            }
        }
    }

    @Override
    public void run() {
        collect();
    }
}
