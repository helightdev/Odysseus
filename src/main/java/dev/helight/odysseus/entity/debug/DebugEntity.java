package dev.helight.odysseus.entity.debug;

import dev.helight.odysseus.entity.CustomEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class DebugEntity extends CustomEntity {

    public DebugEntity() {
        super(EntityType.ZOMBIE);
        health(100);
        speed(0.07);
        knockback(1);
        getAbilities().add(new DebugAbility());
    }

    @Override
    public void setupEntity(Entity entity) {
        entity.setCustomName("Debug Entity");
        entity.setCustomNameVisible(true);
    }

    @Override
    public void loadEntity(Entity entity) {
        System.out.println("Loading debug Entity");
    }

    @Override
    public void unloadEntity(Entity entity) {
        System.out.println("Unloading debug Entity");
    }
}
