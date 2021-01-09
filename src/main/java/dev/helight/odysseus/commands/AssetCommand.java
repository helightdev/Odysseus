package dev.helight.odysseus.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.Streams;
import de.slikey.effectlib.EffectType;
import dev.helight.odysseus.Odysseus;
import dev.helight.odysseus.asset.Asset;
import dev.helight.odysseus.asset.AssetWorld;
import dev.helight.odysseus.asset.Assets;
import dev.helight.odysseus.chat.Chat;
import dev.helight.odysseus.database.UnspecificLocation;
import dev.helight.odysseus.effects.CuboidSelectionEffect;
import dev.helight.odysseus.task.LightScheduler;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@CommandAlias("asset")
public class AssetCommand extends BaseCommand {

    @Subcommand("world")
    public static void world(Player player) {
        Chat.send(player, "§c", "Assets", "Teleporting");
        AssetWorld.assureInitialised();
        player.teleport(AssetWorld.world().getSpawnLocation());
    }

    @Subcommand("create")
    public static void create(Player player, String id) {
        if (AssetWorld.locBuffer1 == null || AssetWorld.locBuffer2 == null) return;
        Asset asset = Asset.generate(id, AssetWorld.locBuffer1, AssetWorld.locBuffer2);
        Assets.assets.add(asset);
        Assets.storeAssets();
        Chat.send(player, "§c", "Assets", "Created asset '%s'", id);
    }


    @Subcommand("copy")
    @CommandCompletion("@assets @assetCopyArgs")
    public static void copy(Player player, String id, @Optional String[] args) {
        if (args==null) args = new String[0];
        List<String> argList = Arrays.asList(args);
        Asset asset = Assets.getById(id);
        Block block = player.getTargetBlock(119);
        if (argList.contains("--elevated")) block = block.getLocation().add(0,1,0).getBlock();
        asset.copyTo(block.getLocation(), argList.contains("--ignore-air"));
        Chat.send(player, "§c", "Assets", "Copied asset '%s' to " + UnspecificLocation.from(block.getLocation()), id);
    }

    @Subcommand("show")
    @CommandCompletion("@assets")
    public static void show(Player player, String id) {
        Asset asset = Assets.getById(id);
        player.teleport(asset.getPointA());
        CuboidSelectionEffect effect = new CuboidSelectionEffect(Odysseus.getInstance().getEffectManager());
        effect.setLocation(asset.getPointA());
        effect.setTargetLocation(asset.getPointB().clone().add(0.99,0.99,0.99));
        effect.setTargetPlayer(player);
        effect.start();
        effect.actionbar = "§cAsset§7: " + asset.getId();
        LightScheduler.instance().delayed(effect::cancel, 20000);
    }

    @Subcommand("tool")
    public static void create(Player player) {
        player.getInventory().addItem(AssetWorld.editingItem);
    }

}
