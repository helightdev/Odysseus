package dev.helight.odysseus.asset;

import dev.helight.odysseus.item.Item;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;

public class AssetWorld {

    public static Location locBuffer1;
    public static Location locBuffer2;

    public static ItemStack editingItem = Item.builder(Material.REDSTONE_TORCH)
            .name("§cAsset Editing Tool")
            .lore("§eLeft Click §7Pos1","§eRight Click §7Pos2")
            .delegate();

    public static void assureInitialised() {
        World world = world();
        if (world == null) {
            world = Bukkit.createWorld(WorldCreator.name("assets").generator(new CleanroomChunkGenerator()));
        }
    }

    public static World world() {
        return Bukkit.getWorld("assets");
    }

}
