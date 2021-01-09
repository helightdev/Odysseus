package dev.helight.odysseus.database;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

public class Store {

    public static Store instance;

    public Map<String,String> data;

    public void set(String key, String value) {
        data.put(key,value);
    }

    public void get(String key) {
        data.get(key);
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

    @SneakyThrows
    public void load() {
        Gson gson = new Gson();
        File file = new File("runtime/store.json");
        String content = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        Type type = new TypeToken<Map<String,String>>() {}.getType();
        data = gson.fromJson(content, type);
    }

    public static Store store() {
        if (instance != null) return instance;
        Store store = new Store();
        store.load();
        instance = store;
        return store;
    }
}
