package dev.helight.odysseus.script;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dev.helight.odysseus.database.Store;
import dev.helight.odysseus.database.UnspecificLocation;
import dev.helight.odysseus.registry.Registrable;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.checkerframework.checker.units.qual.C;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ObjectContext implements Registrable {

    public UUID uuid;
    public String objectTypeId;
    public Map<String,String> data;

    public void set(String key, String value) {
        data.put(key,value);
    }

    public String get(String key) {
        return data.get(key);
    }

    public void set(Object object) {
        Gson gson = new Gson();
        data.put(object.getClass().getName(), Base64.getEncoder().encodeToString(gson.toJson(object).getBytes(StandardCharsets.UTF_8)));
    }

    public <T> T get(Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(new String(Base64.getDecoder().decode(data.get(clazz.getName())), StandardCharsets.UTF_8), clazz);
    }

    @SneakyThrows
    public void save() {
        Gson gson = new Gson();
        File file = new File("runtime/store.json");
        String content = gson.toJson(data);
        Files.write(content, file, StandardCharsets.UTF_8);
    }

    @Override
    public String registeredId() {
        return objectTypeId;
    }

    @Override
    public UUID registeredUuid() {
        return uuid;
    }

    public static ObjectContext createObjectContext(GameObject object) {
        ObjectContext context = new ObjectContext();
        context.uuid = UUID.randomUUID();
        context.objectTypeId = object.getClass().getName();
        context.data = new HashMap<>();
        return context;
    }


}
