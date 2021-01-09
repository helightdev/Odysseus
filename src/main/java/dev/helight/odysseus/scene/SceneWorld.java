package dev.helight.odysseus.scene;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ForwardingObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bukkit.World;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class SceneWorld extends ForwardingObject {

    public static final Cache<String, SceneData> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(10, TimeUnit.SECONDS)
            .build();

    private final World world;

    public SceneWorld(World world) {
        this.world = world;
    }

    File sceneDataFile() {
        return new File(world.getWorldFolder(), "scene.json");
    }

    @SneakyThrows
    public SceneData readSceneData(boolean cached) {
        if (cached) {
            SceneData dat = cache.getIfPresent(world.getName());
            if (dat != null) return dat;
        }
        Gson gson = new Gson();
        String content = FileUtils.readFileToString(sceneDataFile(), StandardCharsets.UTF_8);
        SceneData sceneData = gson.fromJson(content, SceneData.class);
        cache.put(world.getName(), sceneData);
        return sceneData;
    }

    @SneakyThrows
    public void writeSceneData(SceneData sceneData) {
        Gson gson = new Gson();
        String content = gson.toJson(sceneData);
        FileUtils.write(sceneDataFile(), content, StandardCharsets.UTF_8);
        cache.invalidate(world.getName());
    }

    @Override
    public World delegate() {
        return world;
    }
}
