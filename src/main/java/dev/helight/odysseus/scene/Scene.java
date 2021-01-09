package dev.helight.odysseus.scene;

import com.google.common.io.Files;
import dev.helight.odysseus.asset.CleanroomChunkGenerator;
import dev.helight.odysseus.database.UnspecificCubic;
import dev.helight.odysseus.database.UnspecificLocation;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileFilter;

public class Scene {

    public static void assureInitialised() {
        World world = sceneWorld();
        if (world == null) {
            world = Bukkit.createWorld(WorldCreator.name("scene")
                    .generator(new CleanroomChunkGenerator())
                    .generateStructures(false));

            SceneWorld sceneWorld = new SceneWorld(world);
            if (!sceneWorld.sceneDataFile().exists()) sceneWorld.writeSceneData(SceneData.empty());
        }
    }

    @SneakyThrows
    public static void copyToRuntime() {
        File rf = new File("runtime");
        File sf = new File("scene");

        World sw = sceneWorld();
        unloadWorld(sw);

        if (runtimeWorld() != null) {
            unloadWorld(runtimeWorld());
            rf.delete();
        }

        FileUtils.copyDirectory(sf, rf, pathname -> !(pathname.getName().equals("session.lock") || pathname.getName().equals("uid.dat")));

        WorldCreator worldCreator = new WorldCreator("runtime")
                .generator(new CleanroomChunkGenerator())
                .generateStructures(false);
        Bukkit.createWorld(worldCreator);
    }

    public static void unloadWorld(World world) {
        for (Player player : world.getPlayers()) {
            player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
        Bukkit.unloadWorld(world, true);
    }

    public static World sceneWorld() {
        return Bukkit.getWorld("scene");
    }
    public static World runtimeWorld() {
        return Bukkit.getWorld("runtime");
    }

}
