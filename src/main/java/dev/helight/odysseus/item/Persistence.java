package dev.helight.odysseus.item;

import com.google.gson.Gson;
import dev.helight.odysseus.Odysseus;
import lombok.SneakyThrows;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class Persistence {

    @SneakyThrows
    public static <K extends PersistentDataContainer> K store(String key, String value, K k) {
        k.set(new NamespacedKey(Odysseus.getPlugin(), key.toLowerCase()), PersistentDataType.STRING, value);
        return k;
    }

    @SneakyThrows
    public static <V extends PersistentDataContainer> String load(String key, V v) {
        return v.get(new NamespacedKey(Odysseus.getPlugin(), key.toLowerCase()), PersistentDataType.STRING);
    }

    @SneakyThrows
    public static <V extends PersistentDataContainer> boolean has(String key, V v) {
        return v.has(new NamespacedKey(Odysseus.getPlugin(), key.toLowerCase()), PersistentDataType.STRING);
    }

    @SneakyThrows
    public static <V extends PersistentDataContainer> boolean has(Class<?> clazz, V v) {
        return v.has(new NamespacedKey(Odysseus.getPlugin(), clazz.getName().toLowerCase()), PersistentDataType.STRING);
    }

    @SneakyThrows
    public static <K extends PersistentDataContainer> K storeJson(Object o, K k) {
        Gson gson = new Gson();
        k.set(new NamespacedKey(Odysseus.getPlugin(), o.getClass().getName().toLowerCase()), PersistentDataType.STRING,  gson.toJson(o));
        return k;
    }

    @SneakyThrows
    public static <K extends PersistentDataContainer> K storeJson(String key, Object o, K k) {
        Gson gson = new Gson();
        k.set(new NamespacedKey(Odysseus.getPlugin(), key.toLowerCase()), PersistentDataType.STRING, gson.toJson(o));
        return k;
    }

    @SneakyThrows
    public static <K, V extends PersistentDataContainer> K loadJson(Class<K> clazz, V v) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(v.get(new NamespacedKey(Odysseus.getPlugin(), clazz.getName().toLowerCase()), PersistentDataType.STRING), clazz);
        } catch (NullPointerException ignored) {
            return null;
        }
    }


    @SneakyThrows
    public static <K, V extends PersistentDataContainer> K loadJson(String key, Class<K> clazz, V v) {
        Gson gson = new Gson();
        try {
            return gson.fromJson( v.get(new NamespacedKey(Odysseus.getPlugin(), key.toLowerCase()), PersistentDataType.STRING), clazz);
        } catch (NullPointerException ignored) {
            ignored.printStackTrace();
            return null;
        }
    }

}
