package dev.helight.odysseus.script;

import org.bukkit.Location;
import org.bukkit.entity.Entity;

public abstract class EntityObject extends GameObject {

    private Entity entity;

    public final boolean checkEntity(Entity entity) {
        return entity.getEntityId() == entity.getEntityId();
    }

}
