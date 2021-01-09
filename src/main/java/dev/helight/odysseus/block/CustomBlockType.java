package dev.helight.odysseus.block;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class CustomBlockType {

    @Getter
    @Setter
    private ItemStack item;

    @Getter
    @Setter
    private boolean tick;

    public final String name() {
        return getClass().getName().toLowerCase();
    }

    public boolean onInteract(Player player, CustomBlockDBO dbo, PlayerInteractEvent event) {
        return true;
    }

    public boolean onBreak(Player player, CustomBlockDBO dbo, BlockBreakEvent event) {
        return true;
    }

    public JsonObject onCreate(Player player, Block block, BlockPlaceEvent event) {
        return new JsonObject();
    }

    public void onTick(CustomBlockDBO dbo) { }

    public List<ItemStack> drops() {
        return Collections.singletonList(item);
    }
}
