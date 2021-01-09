package dev.helight.odysseus.script;

import dev.helight.odysseus.Odysseus;
import dev.helight.odysseus.scene.Runtime;
import dev.helight.odysseus.scene.SceneData;
import lombok.SneakyThrows;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.compress.utils.Iterators;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.compress.utils.Sets;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.jar.JarFile;

public interface OdysseusGame {

    AtomicReference<Reflections> reflections = new AtomicReference<>();

    void startGameLoop(SceneData data, Runtime runtime);
    void backupGameLoop(SceneData data, Runtime runtime);
    void continueGameLoop(SceneData data, Runtime runtime);

    default GameReflector reflector() {
        return new GameReflector(getClass());
    }

    default Reflections reflect() {
        if (reflections.get() == null) {
            reflections.set(new Reflections(getClass().getPackage().getName()));
        }
        return reflections.get();
    }

    default List<Class<? extends GameObject>> collectTypes() {
        Reflections ref = reflect();
        return Lists.newArrayList(ref.getSubTypesOf(GameObject.class).iterator());
    }

    @SneakyThrows
    default void compile() {
        System.out.println("Test Compile");
        Reflections ref = reflect();
        JavaPlugin plugin = (JavaPlugin) find();
        Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
        getFileMethod.setAccessible(true);
        File file = (File) getFileMethod.invoke(plugin);

        System.out.println("Extracting assets from JarFile");
        JarFile jarFile = new JarFile(file);
        jarFile.stream()
                .peek(jarEntry -> System.out.println("°-° -> " + jarEntry.getName()))
                .filter(jarEntry -> !jarEntry.isDirectory())
                .filter(jarEntry -> jarEntry.getName().startsWith("assets") && !jarEntry.getName().equals("assets"))
                .forEach(jarEntry -> {
                    try {
                        InputStream stream = jarFile.getInputStream(jarEntry);
                        FileUtils.copyInputStreamToFile(stream, new File("work/" + jarEntry.getName()));
                        stream.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        System.out.println("Test Compile");
    }


    @SneakyThrows
    default URL jarFileUrl(File jar, String resource) {
        return new URL(String.format("jar:file:%s/%s"), jar.getAbsolutePath(), resource);
    }

    static OdysseusGame find() {
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin instanceof OdysseusGame) {
                return (OdysseusGame) plugin;
            }
        }
        return null;
    }

}
