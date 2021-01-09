package dev.helight.odysseus.events;

import dev.helight.odysseus.Odysseus;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredListener;

import java.util.UUID;
import java.util.function.Supplier;

public abstract class BetterListener implements Listener {

    @Getter
    private final UUID uuid = UUID.randomUUID();

    public static BetterListener find(UUID uuid) {
        for (RegisteredListener registeredListener : HandlerList.getRegisteredListeners(Odysseus.getPlugin())) {
            Listener listener = registeredListener.getListener();
            if (listener instanceof BetterListener) {
                BetterListener helightListener = (BetterListener) listener;
                if (helightListener.uuid == uuid) {
                    return helightListener;
                }
            }
        }
        return null;
    }

    public static BetterListener find(Class<? extends BetterListener> clazz) {
        for (RegisteredListener registeredListener : HandlerList.getRegisteredListeners(Odysseus.getPlugin())) {
            Listener listener = registeredListener.getListener();
            if (clazz.isAssignableFrom(listener.getClass())) {
                return (BetterListener) listener;
            }
        }
        return null;
    }

    public static <K extends BetterListener> void assureRegistered(Class<K> clazz, Supplier<K> absenceSupplier) {
        BetterListener present = find(clazz);
        if (present == null) {
            absenceSupplier.get().register();
        }
    }

    public static <K extends BetterListener> void assureRegistered(Class<K> clazz) throws IllegalAccessException, InstantiationException {
        BetterListener present = find(clazz);
        if (present == null) {
            clazz.newInstance().register();
        }
    }


    public final void register() {
        Bukkit.getPluginManager().registerEvents(this, Odysseus.getPlugin());
    }

    public final void unregister() {
        HandlerList.unregisterAll(this);
    }
}
