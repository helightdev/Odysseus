package dev.helight.odysseus.entity.impl;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent;
import dev.helight.odysseus.entity.CustomEntity;
import dev.helight.odysseus.entity.CustomEntityRegistry;
import dev.helight.odysseus.events.BetterListener;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;

public class CustomMobListener extends BetterListener {

    @EventHandler
    public void onLoad(EntityAddToWorldEvent event) { ;
        Entity entity = event.getEntity();
        CustomEntity ce = CustomEntityRegistry.load(entity);
        if (ce != null) {
            ce.warmup(entity);
            ce.loadEntity(entity);
        }
    }

    @EventHandler
    public void onUnload(EntityRemoveFromWorldEvent event) {
        Entity entity = event.getEntity();
        CustomEntity ce = CustomEntityRegistry.load(entity);
        if (ce != null) {
            ce.unloadEntity(entity);
        }
    }

}
