package dev.helight.odysseus.asset;

import dev.helight.odysseus.chat.Chat;
import dev.helight.odysseus.events.BetterListener;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AssetListener extends BetterListener {

    @EventHandler
    public void onBlockClick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getItem() == null) return;
        if (!event.getItem().isSimilar(AssetWorld.editingItem)) return;
        if (event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_AIR) {
            Block block = event.getPlayer().getTargetBlock(119);
            if (block == null) return;
            AssetWorld.locBuffer1 = block.getLocation();
            Chat.send(event.getPlayer(), "§c", "Assets", "Set Pos1");
            event.setCancelled(true);
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            Block block = event.getPlayer().getTargetBlock(119);
            if (block == null) return;
            AssetWorld.locBuffer2 = block.getLocation();
            Chat.send(event.getPlayer(), "§c", "Assets", "Set Pos2");
            event.setCancelled(true);
        }
    }

}
