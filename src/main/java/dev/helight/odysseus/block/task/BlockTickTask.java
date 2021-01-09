package dev.helight.odysseus.block.task;

import dev.helight.odysseus.block.CustomBlockDBO;
import dev.helight.odysseus.block.CustomBlockManager;
import dev.helight.odysseus.block.CustomBlockType;
import dev.helight.odysseus.task.AbstractRoutine;
import dev.helight.odysseus.task.annotation.Routine;

@Routine("Core.Block.Tick")
public class BlockTickTask extends AbstractRoutine {

    public BlockTickTask() {
        executeRepeating(1000, 1000, true);
    }

    @Override
    public void run() {
        for (CustomBlockDBO value : CustomBlockManager.getBlocks().values()) {
            CustomBlockType type = CustomBlockManager.getOrNull(value.getType());
            if (type != null && type.isTick()) type.onTick(value);
        }
    }

}
