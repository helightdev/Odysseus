package dev.helight.odysseus.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.slikey.effectlib.EffectType;
import de.slikey.effectlib.effect.LineEffect;
import dev.helight.odysseus.Odysseus;
import dev.helight.odysseus.Raycast;
import dev.helight.odysseus.scene.Scene;
import dev.helight.odysseus.chat.Chat;
import dev.helight.odysseus.editor.PositionSelector;
import dev.helight.odysseus.entity.CustomEntityRegistry;
import dev.helight.odysseus.entity.debug.DebugEntity;
import dev.helight.odysseus.inventory.debug.DebugGui;
import dev.helight.odysseus.item.SerializedItem;
import dev.helight.odysseus.session.PlayerSession;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@CommandAlias("odysseus|ody")
public class CoreCommand extends BaseCommand {

    @Subcommand("gui")
    public static void onInventoryDebug(Player player) {
        DebugGui gui = new DebugGui();
        gui.checkInitialisation();
        gui.construct();
        gui.show(player);
    }

    @Subcommand("scene")
    public static void scene(Player player) {
        Scene.assureInitialised();
        Chat.send(player, "§e", "Scene", "Teleporting");
        player.teleport(Scene.sceneWorld().getSpawnLocation());
    }

    @Subcommand("runtime")
    public static void runtime(Player player) {
        Chat.send(player, "§e", "Scene", "Copying scene...");
        Scene.copyToRuntime();
        Chat.send(player, "§e", "Scene", "Loading world...");
        World world = Scene.runtimeWorld();
        Chat.send(player, "§e", "Scene", "Teleporting");
        player.teleport(world.getSpawnLocation());
    }


    @Subcommand("center_editor")
    public static void centerEditor(Player player) {

    }

    @Subcommand("entity")
    public static void onEntity(Player player) {
        Location location = player.getLocation();
        DebugEntity entity = CustomEntityRegistry.get(DebugEntity.class);
        entity.spawn(location);
    }

    @Subcommand("testsound")
    public static void onTestSound(Player player) {
        player.playSound(player.getLocation(), Sound.MUSIC_DISC_PIGSTEP, Float.MAX_VALUE ,1);
    }

    @Subcommand("ray")
    public static void ray(Player player) {
        Location origin = player.getEyeLocation();
        Location current = origin.clone();
        Vector eyeDirection = player.getEyeLocation().getDirection().normalize();
        List<Location> points = new ArrayList<>();
        boolean iterate = true;
        int depth = 0;
        while (iterate && depth < 255) {
            Block block = current.getBlock();
            if (block.getType().isSolid()) iterate = false;
            points.add(current.clone());
            current.add(eyeDirection);
            depth++;
        }
        LineEffect lineEffect = new LineEffect(Odysseus.getInstance().getEffectManager());
        lineEffect.setLocation(origin);
        lineEffect.setTargetLocation(current);
        lineEffect.setTargetPlayer(player);
        lineEffect.duration = 10;
        lineEffect.start();
    }

    @Subcommand("eyetrace")
    public static void traceEye(Player player) {
        PositionSelector.SinglePointSelectorEffect effect = new PositionSelector.SinglePointSelectorEffect(Odysseus.getInstance().getEffectManager());

        System.out.println(Raycast.getLookingAt(player, 20, 255, false).getBlock().getType().isSolid());
        System.out.println(Raycast.getLookingAt(player, 20, 255, true).getBlock().getType().isSolid());

        effect.pointLocation = Raycast.getLookingAt(player, 20, 255, false);
        effect.period = 1;
        effect.type = EffectType.REPEATING;
        effect.iterations = Integer.MAX_VALUE;
        effect.setLocation(effect.pointLocation);
        effect.setTargetPlayer(player);
        effect.start();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Odysseus.getPlugin(), () -> {
            Location location = Raycast.getLookingAt(player, 20, 255, false);
            effect.pointLocation = location;
        }, 1, 1);
    }

    @Subcommand("editor")
    public static void editor(Player player) {
        PositionSelector.start(player);
    }

    @Subcommand("item")
    public static void onItem(Player player) {
        File file = new File("items/");
        IOFileFilter filter = FileFilterUtils.suffixFileFilter("");
        Arrays.stream(Objects.requireNonNull(file.listFiles())).filter(filter::accept)
                .map(f -> {
                    try {
                        return FileUtils.readFileToString(f, StandardCharsets.UTF_8);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .map(SerializedItem::deserialize)
                .forEach(item -> player.getInventory().addItem(item));
    }

    @Subcommand("pref-get")
    public static void player(Player player) {

        PlayerSession.session(player).preferences().thenAccept(playerPreferences -> {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            Chat.send(player, "§b", "Preferences", "\n%s", (Object)gson.toJson(playerPreferences.getBacking()));
        });

    }

    @Subcommand("pref-set")
    public static void player(Player player, String key, String value) {

        PlayerSession.session(player).preferences().thenAccept(playerPreferences -> {
            playerPreferences.getBacking().addProperty(key,value);
            playerPreferences.store();
            Chat.send(player,"§b", "Preferences", "Updated");
        });

    }

}
