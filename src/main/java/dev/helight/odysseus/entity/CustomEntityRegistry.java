package dev.helight.odysseus.entity;

import dev.helight.odysseus.item.Persistence;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomEntityRegistry {

    public static final Map<String, CustomEntity> registry = new ConcurrentHashMap<>();

    public static boolean is(Entity entity) {
        return Persistence.has("CEType", entity.getPersistentDataContainer());
    }

    public static <K extends CustomEntity> K get(Class<K> clazz) {
        return (K) registry.get(clazz.getName());
    }

    @Nullable
    public static CustomEntity load(Entity entity) {
        String type = Persistence.load("CEType", entity.getPersistentDataContainer());
        if (type != null) {
            return registry.get(type);
        }
        return null;
    }

}
