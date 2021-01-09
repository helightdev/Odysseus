package dev.helight.odysseus.region;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface RegionType {

    default RIR breakBlock(Player player, Block block) { return RIR.ALLOWED; }
    default RIR placeBlock(Player player, ItemStack itemStack, Block block) { return RIR.ALLOWED; }
    default RIR interactBlock(Player player, Block block) { return RIR.ALLOWED; }
    default RIR pvp(Player player, Player target) { return RIR.ALLOWED; }
    default RIR eve(Entity attacker, Entity affected) { return RIR.ALLOWED; }

}
