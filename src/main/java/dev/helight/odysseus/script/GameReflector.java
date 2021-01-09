package dev.helight.odysseus.script;

import lombok.SneakyThrows;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections.Reflections;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;

public class GameReflector<K extends JavaPlugin, OdysseusGame> {

    private Class<K> clazz;
    private Reflections reflections;

    public GameReflector(Class<K> clazz) {
        this.clazz = clazz;
        this.reflections = new Reflections(getClass().getPackage().getName());
    }

    public List<Class<? extends GameObject>> collectGameObjectTypes() {
        return Lists.newArrayList(reflections.getSubTypesOf(GameObject.class).iterator());
    }

    @SneakyThrows
    public URL jarFileUrl(File jar, String resource) {
        return new URL(String.format("jar:file:%s/%s"), jar.getAbsolutePath(), resource);
    }


    @SneakyThrows
    public void compile() {
        System.out.println("Test Compile");
        JavaPlugin plugin = JavaPlugin.getPlugin(clazz);
        Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
        getFileMethod.setAccessible(true);
        File file = (File) getFileMethod.invoke(plugin);

        System.out.println("Extracting assets from JarFile");
        JarFile jarFile = new JarFile(file);
        jarFile.stream()
                .peek(jarEntry -> System.out.println("°-° -> " + jarEntry.getName()))
                .filter(jarEntry -> !jarEntry.isDirectory())
                .filter(jarEntry -> jarEntry.getName().startsWith("assets") && !jarEntry.getName().equals("assets"))
                .parallel()
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
}
