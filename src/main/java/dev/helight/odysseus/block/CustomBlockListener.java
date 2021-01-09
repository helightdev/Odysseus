package dev.helight.odysseus.block;

import com.google.gson.JsonObject;
import dev.helight.odysseus.events.BetterListener;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

public class CustomBlockListener extends BetterListener {

    @EventHandler
    public void onLoad(ChunkLoadEvent event) {
        CustomBlockManager.load(event.getChunk());
    }

    @EventHandler
    public void onUnload(ChunkUnloadEvent event) {
        CustomBlockManager.unload(event.getChunk());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;
        Pair<CustomBlockDBO,CustomBlockType> pair = CustomBlockManager.getOrNull(event.getBlock());
        if (pair == null) return;

        boolean doesBreak = pair.getRight().onBreak(event.getPlayer(), pair.getLeft(), event);

        if (!doesBreak) {
            event.setCancelled(true);
            return;
        }

        CustomBlockManager.remove(event.getBlock());
        event.setDropItems(false);

        for (ItemStack drop : pair.getRight().drops()) {
            event.getBlock().getWorld().dropItem(event.getBlock().getLocation(), drop);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;
        CustomBlockType type = CustomBlockManager.getOrNull(event.getItemInHand());
        if (type == null) return;
        JsonObject object = type.onCreate(event.getPlayer(), event.getBlock(), event);
        if (object == null) return;
        CustomBlockDBO dbo = new CustomBlockDBO();
        dbo.setBlock(event.getBlock());
        dbo.setType(type.name());
        dbo.setPayload(object);
        CustomBlockManager.add(dbo);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() == Material.AIR) return;
        if (event.useInteractedBlock() != Event.Result.DENY) return;
        Pair<CustomBlockDBO,CustomBlockType> pair = CustomBlockManager.getOrNull(event.getClickedBlock());
        if (pair == null) return;
        boolean allow = pair.getRight().onInteract(event.getPlayer(), pair.getLeft(), event);
        if (!allow) event.setUseInteractedBlock(Event.Result.DENY);
    }

}
