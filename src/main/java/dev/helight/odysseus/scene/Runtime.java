package dev.helight.odysseus.scene;

import dev.helight.odysseus.database.UnspecificCubic;
import dev.helight.odysseus.database.UnspecificLocation;
import dev.helight.odysseus.registry.Registry;
import dev.helight.odysseus.script.GameObject;
import dev.helight.odysseus.script.ObjectContext;
import dev.helight.odysseus.script.OdysseusGame;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Objects;

public class Runtime {

    public static Runtime debugLink;

    public Runtime() {
        debugLink = this;
    }

    public Registry<GameObject> gameObjectRegistry = new Registry<>();

    public void spawn(GameObject gameObject) {
        gameObject.runtime = this;
        gameObject.create();
        gameObject.enable();
    }

    @SneakyThrows
    public void create() {
        SceneWorld sceneWorld = new SceneWorld(Scene.runtimeWorld());
        OdysseusGame game = OdysseusGame.find();

        //TODO REMOVE
        game.compile();
        //TODO REMOVE

        Objects.requireNonNull(game).startGameLoop(sceneWorld.readSceneData(true), this);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    public void load() {
        SceneWorld sceneWorld = new SceneWorld(Scene.runtimeWorld());
        OdysseusGame game = OdysseusGame.find();
        Objects.requireNonNull(game).continueGameLoop(sceneWorld.readSceneData(true), this);
        SceneData data = sceneWorld.readSceneData(true);
        for (ObjectContext object : data.getObjects()) {
            Class<? extends GameObject> type = game.collectTypes().stream()
                    .filter(t -> t.getName().equals(object.objectTypeId))
                    .findFirst().orElse(null);
            Constructor<? extends GameObject> constructor = null;
            for (Constructor<?> iterate : Objects.requireNonNull(type).getConstructors()) {
                if (iterate.getParameterCount() == 0) constructor = (Constructor< ? extends GameObject>) iterate;
            }

            if (constructor == null) throw new IllegalArgumentException("Can't load GameObject because the implementation is missing a no-args constructor");
            GameObject obj = constructor.newInstance();

            obj.context = object;
            gameObjectRegistry.register(obj);
            obj.load();
            obj.enable();
        }
    }

    public void store() {
        SceneWorld sceneWorld = new SceneWorld(Scene.runtimeWorld());
        SceneData data = sceneWorld.readSceneData(true);
        data.getObjects().clear();
        OdysseusGame game = OdysseusGame.find();
        Objects.requireNonNull(game).backupGameLoop(data, this);
        for (GameObject gameObject : gameObjectRegistry.all()) {
            gameObject.save();
            gameObject.disable();
            data.getObjects().add(gameObject.context);
        }
        sceneWorld.writeSceneData(data);
    }

    public static World world() {
        return Bukkit.getWorld("runtime");
    }

    public static UnspecificLocation location(String id) {
        return new SceneWorld(world()).readSceneData(true).getLocationMapping().get(id);
    }

    public static UnspecificCubic cubic(String id) {
        return new SceneWorld(world()).readSceneData(true).getCubicMapping().get(id);
    }
}
