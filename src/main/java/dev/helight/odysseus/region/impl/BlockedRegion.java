package dev.helight.odysseus.region.impl;

import dev.helight.odysseus.region.RIR;
import dev.helight.odysseus.region.RegionType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BlockedRegion implements RegionType {

    @Override
    public RIR breakBlock(Player player, Block block) {
        return player.isOp() ? RIR.ALLOWED : RIR.DISALLOWED;
    }

    @Override
    public RIR placeBlock(Player player, ItemStack itemStack, Block block) {
        return player.isOp() ? RIR.ALLOWED : RIR.DISALLOWED;
    }

    @Override
    public RIR interactBlock(Player player, Block block) {
        return player.isOp() ? RIR.ALLOWED : RIR.DISALLOWED;
    }

    @Override
    public RIR pvp(Player player, Player target) {
        return player.isOp() ? RIR.ALLOWED : RIR.DISALLOWED;
    }

    @Override
    public RIR eve(Entity a, Entity b) {
        return a.isOp() ? RIR.ALLOWED : RIR.DISALLOWED;
    }
}
