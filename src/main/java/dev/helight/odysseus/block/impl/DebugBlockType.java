package dev.helight.odysseus.block.impl;

import com.google.gson.JsonObject;
import dev.helight.odysseus.block.CustomBlockDBO;
import dev.helight.odysseus.block.CustomBlockType;
import dev.helight.odysseus.item.Item;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class DebugBlockType extends CustomBlockType {

    public DebugBlockType() {
        setItem(Item.builder(Material.CAULDRON).name("§eDebug-Kessel").delegate());
        setTick(false);
    }

    @Override
    public boolean onInteract(Player player, CustomBlockDBO dbo, PlayerInteractEvent event) {
        System.out.println("Try debug");
        return super.onInteract(player, dbo, event);
    }

    @Override
    public boolean onBreak(Player player, CustomBlockDBO dbo, BlockBreakEvent event) {
        System.out.println("Try break debug");
        return super.onBreak(player, dbo, event);
    }

    @Override
    public JsonObject onCreate(Player player, Block block, BlockPlaceEvent event) {
        System.out.println("Try create debug");
        return super.onCreate(player, block, event);
    }





}
