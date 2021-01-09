package dev.helight.odysseus.script;

import dev.helight.odysseus.Odysseus;
import dev.helight.odysseus.registry.Registrable;
import dev.helight.odysseus.registry.Registry;
import dev.helight.odysseus.scene.Runtime;
import dev.helight.odysseus.scene.SceneData;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public abstract class GameObject implements Registrable, Listener {

    public ObjectContext context;
    public Runtime runtime;

    @OverridingMethodsMustInvokeSuper
    public void create() {
        context = ObjectContext.createObjectContext(this);
        runtime.gameObjectRegistry.register(this);
    }

    @OverridingMethodsMustInvokeSuper
    public void destroy() {
        runtime.gameObjectRegistry.unregister(this);
    }

    @OverridingMethodsMustInvokeSuper
    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, Odysseus.getPlugin());
    }

    @OverridingMethodsMustInvokeSuper
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    public abstract void save();
    public abstract void load();
    public abstract void update();

    @Override
    public final String registeredId() {
        return context.objectTypeId;
    }

    @Override
    public final UUID registeredUuid() {
        return context.uuid;
    }

}
