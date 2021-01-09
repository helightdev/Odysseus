package dev.helight.odysseus.region.impl;

import dev.helight.odysseus.events.BetterListener;
import dev.helight.odysseus.region.Region;
import dev.helight.odysseus.region.RegionManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class RegionListeners extends BetterListener {

    @EventHandler(priority = EventPriority.LOW)
    public void onBreak(BlockBreakEvent event) {
        event.setCancelled(!RegionManager.canBreak(event.getPlayer(), event.getBlock()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(!RegionManager.canPlace(event.getPlayer(), event.getItemInHand(), event.getBlock()));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null && event.getAction() == Action.LEFT_CLICK_BLOCK) {
            event.setUseInteractedBlock(RegionManager.canInteract(event.getPlayer(), block) ? Event.Result.ALLOW : Event.Result.DENY);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPvp(EntityDamageByEntityEvent event) {
        System.out.println(event.getDamage() + ":" + event.getFinalDamage());

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            event.setCancelled(!RegionManager.canPvp((Player) event.getDamager(), (Player) event.getEntity()));
        } else {
            event.setCancelled(!RegionManager.canEve(event.getDamager(), event.getEntity()));
        }
    }

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        for (Region region : RegionManager.getIntersecting(event.getBlock().getLocation())) {
            if (region.getPayload().has("physics")) {
                if (!region.getPayload().get("physics").getAsBoolean()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

}
