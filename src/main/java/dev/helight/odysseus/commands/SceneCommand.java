package dev.helight.odysseus.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.base.Joiner;
import dev.helight.odysseus.Odysseus;
import dev.helight.odysseus.chat.Chat;
import dev.helight.odysseus.database.UnspecificCubic;
import dev.helight.odysseus.database.UnspecificLocation;
import dev.helight.odysseus.editor.PositionSelector;
import dev.helight.odysseus.effects.CuboidSelectionEffect;
import dev.helight.odysseus.effects.SingleSelectionEffect;
import dev.helight.odysseus.scene.Runtime;
import dev.helight.odysseus.scene.Scene;
import dev.helight.odysseus.scene.SceneData;
import dev.helight.odysseus.scene.SceneWorld;
import dev.helight.odysseus.script.LocationHolder;
import dev.helight.odysseus.script.LocationObject;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("scene")
public class SceneCommand extends BaseCommand {

    @Subcommand("world")
    public static void world(Player player) {
        Scene.assureInitialised();
        Chat.send(player, "§e", "Scene", "Teleporting");
        player.teleport(Scene.sceneWorld().getSpawnLocation());
    }

    @Subcommand("debug")
    public static void debug(Player player) {
        SceneWorld world = new SceneWorld(player.getWorld());
        SceneData data = world.readSceneData(true);
        data.getLocationMapping().forEach((x,y) -> {
            SingleSelectionEffect effect = new SingleSelectionEffect(Odysseus.getInstance().getEffectManager());
            effect.setLocation(y.toLocation(world.delegate()));
            effect.setTargetPlayer(player);
            effect.actionbar = "§aLocation§7: " + x;
            effect.color = Color.RED;
            effect.start();
            Bukkit.getScheduler().runTaskLater(Odysseus.getPlugin(), (Runnable) effect::cancel, 20*20);
        });

        data.getCubicMapping().forEach((x,y) -> {
            CuboidSelectionEffect effect = new CuboidSelectionEffect(Odysseus.getInstance().getEffectManager());
            effect.setLocation(y.getA().toLocation(world.delegate()));
            effect.setTargetLocation(y.getB().toLocation(world.delegate()));
            effect.setTargetPlayer(player);
            effect.actionbar = "§aCubic§7: " + x;
            effect.color = Color.AQUA;
            effect.start();
            Bukkit.getScheduler().runTaskLater(Odysseus.getPlugin(), (Runnable) effect::cancel, 20*20);
        });

        if (Runtime.debugLink != null) {
            Runtime.debugLink.gameObjectRegistry.stream()
                    .filter(object -> object instanceof LocationHolder)
                    .forEach(x -> {
                        SingleSelectionEffect effect = new SingleSelectionEffect(Odysseus.getInstance().getEffectManager());
                        effect.setLocation(((LocationHolder) x).getLocation().toLocation(world.delegate()));
                        effect.setTargetPlayer(player);
                        effect.actionbar = "§dGameObject§7: " + x.context.objectTypeId;
                        effect.color = Color.GREEN;
                        effect.start();
                        Bukkit.getScheduler().runTaskLater(Odysseus.getPlugin(), (Runnable) effect::cancel, 20*20);
            });
        }
    }

    @Subcommand("runtime start")
    public static void run(Player player) {
        Chat.send(player, "§e", "Scene", "Copying scene...");
        Scene.copyToRuntime();
        Chat.send(player, "§e", "Scene", "Loading world...");
        World world = Scene.runtimeWorld();
        Chat.send(player, "§e", "Scene", "Starting Runtime...");
        Runtime runtime = new Runtime();
        runtime.create();
        Chat.send(player, "§e", "Scene", "Teleporting");
        player.teleport(world.getSpawnLocation());

    }

    @Subcommand("runtime backupStop")
    public static void backup(Player player) {
        Chat.send(player, "§e", "Scene", "Storing and stopping runtime state...");
        Runtime.debugLink.store();
        Chat.send(player, "§e", "Scene", "Done");
    }

    @Subcommand("runtime continue")
    public static void continueRuntime(Player player) {
        Chat.send(player, "§e", "Scene", "Restoring runtime state...");
        Runtime.debugLink.load();
        Chat.send(player, "§e", "Scene", "Done");
    }


    @Subcommand("stop")
    public static void stop(Player player) {
        Chat.send(player, "§e", "Scene", "Stopping Runtime...");
        Scene.unloadWorld(Scene.runtimeWorld());
    }

    @Subcommand("cubic add")
    public static void addCubic(Player player, String cubic) {
        PositionSelector.start(player).thenAccept(position1 -> {
            PositionSelector.start(player).thenAccept(position2 -> {
                SceneWorld world = new SceneWorld(player.getWorld());
                SceneData data = world.readSceneData(true);
                UnspecificCubic unspecificCubic = UnspecificCubic.from(
                        UnspecificLocation.from(position1),
                        UnspecificLocation.from(position2)
                );
                data.getCubicMapping().put(cubic, unspecificCubic);
                world.writeSceneData(data);
                Chat.send(player, "§e", "Scene", "Created location mapping for '%s'", cubic);
            });
        });
    }

    @Subcommand("cubic remove")
    @CommandCompletion("@cubics")
    public static void removeCubic(Player player, String cubic) {
        SceneWorld world = new SceneWorld(player.getWorld());
        SceneData data = world.readSceneData(true);
        data.getCubicMapping().remove(cubic);
        world.writeSceneData(data);
        Chat.send(player, "§e", "Scene", "Removed location mapping for '%s'", cubic);
    }


    @Subcommand("cubic list")
    public static void cubics(Player player) {
        SceneWorld world = new SceneWorld(player.getWorld());
        SceneData data = world.readSceneData(true);
        List<String> locationList = data.getCubicMapping().entrySet().stream()
                .map(e -> "§e" + e.getKey() + " §7-> " + e.getValue().toString())
                .collect(Collectors.toList());
        world.writeSceneData(data);
        Chat.send(player, "§e", "Scene", "§e§7§lCubicMapping:§r\n%s", Joiner.on("\n").join(locationList));
    }

    @Subcommand("location add")
    public static void addLocation(Player player, String location) {
        PositionSelector.start(player).thenAccept(position -> {
            SceneWorld world = new SceneWorld(player.getWorld());
            SceneData data = world.readSceneData(true);
            data.getLocationMapping().put(location, UnspecificLocation.from(position));
            world.writeSceneData(data);
            Chat.send(player, "§e", "Scene", "Created location mapping for '%s'", location);
        });
    }

    @Subcommand("location remove")
    @CommandCompletion("@locations")
    public static void removeLocation(Player player, String location) {
        SceneWorld world = new SceneWorld(player.getWorld());
        SceneData data = world.readSceneData(true);
        data.getLocationMapping().remove(location);
        world.writeSceneData(data);
        Chat.send(player, "§e", "Scene", "Removed location mapping for '%s'", location);
    }

    @Subcommand("location list")
    public static void locations(Player player) {
        SceneWorld world = new SceneWorld(player.getWorld());
        SceneData data = world.readSceneData(true);
        List<String> locationList = data.getLocationMapping().entrySet().stream()
                .map(e -> "§e" + e.getKey() + " §7-> " + e.getValue().toString())
                .collect(Collectors.toList());
        world.writeSceneData(data);
        Chat.send(player, "§e", "Scene", "§e§7§lLocationMapping:§r\n%s", Joiner.on("\n").join(locationList));
    }

}
