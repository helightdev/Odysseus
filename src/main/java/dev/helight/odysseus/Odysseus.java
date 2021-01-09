package dev.helight.odysseus;

import co.aikar.commands.PaperCommandManager;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.slikey.effectlib.EffectManager;
import dev.helight.odysseus.asset.Asset;
import dev.helight.odysseus.asset.AssetListener;
import dev.helight.odysseus.asset.AssetWorld;
import dev.helight.odysseus.asset.Assets;
import dev.helight.odysseus.block.CustomBlockListener;
import dev.helight.odysseus.block.CustomBlockManager;
import dev.helight.odysseus.block.impl.DebugBlockType;
import dev.helight.odysseus.block.task.BlockTickTask;
import dev.helight.odysseus.commands.*;
import dev.helight.odysseus.database.DataManager;
import dev.helight.odysseus.editor.PositionSelector;
import dev.helight.odysseus.entity.ArmorStandAnimation;
import dev.helight.odysseus.entity.debug.DebugEntity;
import dev.helight.odysseus.entity.impl.CustomMobListener;
import dev.helight.odysseus.events.BetterListener;
import dev.helight.odysseus.inventory.GuiGarbageCollector;
import dev.helight.odysseus.region.Region;
import dev.helight.odysseus.region.RegionManager;
import dev.helight.odysseus.region.impl.RegionListeners;
import dev.helight.odysseus.region.impl.WorldListener;
import dev.helight.odysseus.region.tasks.PlayerLocateTask;
import dev.helight.odysseus.scene.Scene;
import dev.helight.odysseus.scene.SceneData;
import dev.helight.odysseus.scene.SceneWorld;
import dev.helight.odysseus.session.impl.SessionListener;
import dev.helight.odysseus.task.AbstractRoutine;
import dev.helight.odysseus.task.LightScheduler;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("Lombok")
public class Odysseus {

    private static Odysseus singleton;

    @Getter
    private final Plugin plugin;

    @Getter
    private PaperCommandManager commandManager;

    @Getter
    private EffectManager effectManager;

    public Odysseus(Plugin plugin) {
        this.plugin = plugin;
        singleton = this;
    }

    public void startup() {
        DataManager.database();
    }

    @SneakyThrows
    public void postWorld() {
        effectManager = new EffectManager(plugin);
        this.commandManager = new PaperCommandManager(plugin);

        Assets.loadAssets();

        CustomBlockManager.init();
        CustomBlockManager.register(new DebugBlockType());

        registerFrameworkListeners();
        registerFrameworkCommands();
        runFrameworkTasks();

        File export = new File("armorstand.export");
        List<ArmorStandAnimation.ArmorStandData> dataArrayList = new ArrayList<>();
        for (String readLine : Files.readLines(export, StandardCharsets.UTF_8)) {
            System.out.println(readLine);
            dataArrayList.add(ArmorStandAnimation.readLine(readLine));
        }
        System.out.println(dataArrayList);

    }

    public void shutdown() {
        RegionManager.save();
        LightScheduler.instance().rebuildPool();
    }

    @SneakyThrows
    private void registerFrameworkListeners() {
        BetterListener.assureRegistered(CustomMobListener.class);
        BetterListener.assureRegistered(RegionListeners.class);
        BetterListener.assureRegistered(WorldListener.class);
        BetterListener.assureRegistered(SessionListener.class);
        BetterListener.assureRegistered(CustomBlockListener.class);
        BetterListener.assureRegistered(AssetListener.class);
        BetterListener.assureRegistered(PositionSelector.PositionEditingListener.class);
    }

    private void registerFrameworkCommands() {
        new DebugEntity().register();

        commandManager.getCommandContexts().registerContext(Region.class, context -> {
            String name = context.popFirstArg();
            return RegionManager.getRegion(name);
        });
        commandManager.getCommandContexts().registerIssuerAwareContext(Region[].class, context ->
                RegionManager.getIntersecting(context.getPlayer().getLocation()).toArray(new Region[0]));

        commandManager.getCommandCompletions().registerCompletion("locations", c -> {
            SceneWorld world = new SceneWorld(c.getPlayer().getWorld());
            SceneData data = world.readSceneData(true);
            return data.getLocationMapping().keySet();
        });

        commandManager.getCommandCompletions().registerCompletion("cubics", c -> {
            SceneWorld world = new SceneWorld(c.getPlayer().getWorld());
            SceneData data = world.readSceneData(true);
            return data.getCubicMapping().keySet();
        });

        commandManager.getCommandCompletions().registerCompletion("assets", c -> Assets.assets.stream().map(Asset::getId).collect(Collectors.toList()));
        commandManager.getCommandCompletions().registerCompletion("assetCopyArgs", c -> Arrays.asList("--elevated", "--ignore-air"));


        commandManager.registerCommand(new CoreCommand());
        commandManager.registerCommand(new RegionCommand());
        commandManager.registerCommand(new CustomBlocksCommand());
        commandManager.registerCommand(new AssetCommand());
        commandManager.registerCommand(new EditorCommand());
        commandManager.registerCommand(new SceneCommand());
    }

    private void runFrameworkTasks() {
        System.out.println("Starting Framework Tasks");
        AbstractRoutine.assureRunning(GuiGarbageCollector.class);
        AbstractRoutine.assureRunning(PlayerLocateTask.class);
        AbstractRoutine.assureRunning(BlockTickTask.class);

        System.out.println("Started Framework Tasks");
    }

    private static void finalizeStartup() {
        RegionManager.load();
        AssetWorld.assureInitialised();
        Scene.assureInitialised();
    }

    public static Odysseus getInstance() {
        return singleton;
    }

    public static Plugin getPlugin() {
        return singleton.plugin;
    }
}
