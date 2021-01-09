package dev.helight.odysseus.region.tasks;

import dev.helight.odysseus.region.RegionManager;
import dev.helight.odysseus.task.AbstractRoutine;
import dev.helight.odysseus.task.annotation.Routine;

@Routine("Core.Region.DataSave")
public class DataSaveTask extends AbstractRoutine {

    private static final long PERIOD = 1000 * 30;

    public DataSaveTask() {
        executeRepeating(PERIOD, PERIOD, false);
    }

    @Override
    public void run() {
        RegionManager.save();
    }

}
