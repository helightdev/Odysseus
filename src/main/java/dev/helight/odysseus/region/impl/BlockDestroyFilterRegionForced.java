package dev.helight.odysseus.region.impl;

import dev.helight.odysseus.region.RIR;
import dev.helight.odysseus.region.RegionType;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class BlockDestroyFilterRegionForced implements RegionType {

    private List<Material> materialList;

    public BlockDestroyFilterRegionForced(List<Material> materialList) {
        this.materialList = materialList;
    }

    @Override
    public RIR breakBlock(Player player, Block block) {
        return materialList.contains(block.getType()) ? RIR.FORCED : RIR.DISALLOWED;
    }

    @Override
    public RIR interactBlock(Player player, Block block) {
        return materialList.contains(block.getType()) ? RIR.FORCED : RIR.DISALLOWED;
    }
}
