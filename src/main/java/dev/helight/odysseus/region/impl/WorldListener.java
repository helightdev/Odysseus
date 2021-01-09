package dev.helight.odysseus.region.impl;

import dev.helight.odysseus.asset.AssetWorld;
import dev.helight.odysseus.asset.Assets;
import dev.helight.odysseus.events.BetterListener;
import dev.helight.odysseus.region.Region;
import dev.helight.odysseus.region.RegionManager;
import dev.helight.odysseus.scene.Scene;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class WorldListener extends BetterListener {

    @EventHandler
    public void onPhysics(BlockPhysicsEvent event) {
        if (event.getChangedType() == Material.WATER || event.getChangedType() == Material.LAVA) return;
        if (event.getChangedType().name().contains("GLASS_PANE") ||
                event.getChangedType().name().contains("FENCE") ||
                event.getChangedType().name().contains("WALL") ||
                event.getChangedType() == Material.IRON_BARS ||
                event.getChangedType() == Material.CHAIN
        ) return;

        if (event.getBlock().getWorld().equals(AssetWorld.world()) || event.getBlock().getWorld().equals(Scene.sceneWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPhysics(LeavesDecayEvent event) {
        if (event.getBlock().getWorld().equals(AssetWorld.world()) || event.getBlock().getWorld().equals(Scene.sceneWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPhysics(BlockGrowEvent event) {
        if (event.getBlock().getWorld().equals(AssetWorld.world()) || event.getBlock().getWorld().equals(Scene.sceneWorld())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPhysics(BlockFadeEvent event) {
        if (event.getBlock().getWorld().equals(AssetWorld.world()) || event.getBlock().getWorld().equals(Scene.sceneWorld())) {
            event.setCancelled(true);
        }
    }



    @EventHandler
    public void onMobSpawn(EntitySpawnEvent event) {
        if (event.getEntity().getEntitySpawnReason() != CreatureSpawnEvent.SpawnReason.NATURAL) return;
        if (event.getEntity().getWorld().equals(AssetWorld.world()) || event.getEntity().getWorld().equals(Scene.sceneWorld())) {
            event.setCancelled(true);
        }
    }

}
