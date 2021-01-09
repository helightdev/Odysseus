package dev.helight.odysseus.entity;

import dev.helight.odysseus.task.AbstractRoutine;
import dev.helight.odysseus.task.annotation.Routine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.LivingEntity;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public abstract class EntityAbility {

    private final long cooldown;
    private final double chance;

    public EntityAbility(long cooldown, double chance) {
        this.cooldown = cooldown;
        this.chance = chance;
    }

    public abstract void execute(CustomEntity entity, LivingEntity living);

    public final void call(CustomEntity entity, LivingEntity living) {
        if (living.isValid()) {
            double dice = ThreadLocalRandom.current().nextDouble(0, 1);
            if (dice <= chance) execute(entity, living);
        }
    }

    public final void start(CustomEntity entity, LivingEntity livingEntity) {
        AbilityTask task = new AbilityTask(this, entity,livingEntity);
        task.executeRepeating(cooldown,cooldown,true);
    }

    @AllArgsConstructor
    @Routine("Core.Entity.Ability")
    public static class AbilityTask extends AbstractRoutine {

        private final EntityAbility ability;
        private final CustomEntity entity;
        private final LivingEntity livingEntity;

        @Override
        public void run() {
            if (livingEntity.isValid()) {
                ability.call(entity,livingEntity);
            } else {
                stop();
            }
        }
    }
}
