package dev.helight.odysseus.asset;

import dev.helight.odysseus.database.codecs.LocationCodec;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_16_R2.block.CraftBlockState;

@Getter
public class Asset {

    public Asset(String id, int ax, int ay, int az, int bx, int by, int bz) {
        this.id = id;
        this.ax = ax;
        this.ay = ay;
        this.az = az;
        this.bx = bx;
        this.by = by;
        this.bz = bz;
    }

    public Asset() {}

    private String id;
    private boolean skipAir;

    //Lowest
    private int ax;
    private int ay;
    private int az;

    //Highest
    private int bx;
    private int by;
    private int bz;

    public Location getPointA() {
        return new Location(AssetWorld.world(),ax,ay,az);
    }

    public Location getPointB() {
        return new Location(AssetWorld.world(),bx,by,bz);
    }

    public int xDif() {
        return bx-ax;
    }

    public int yDif() {
        return by-ay;
    }

    public int zDif() {
        return bz-az;
    }

    public void copyTo(Location location, boolean skipAirCopy) {
        World assetWorld = AssetWorld.world();
        World targetWorld = location.getWorld();
        int tx = location.getBlockX();
        int ty = location.getBlockY();
        int tz = location.getBlockZ();
        for (int xOffset = 0; xOffset <= xDif(); xOffset++) {
            for (int yOffset = 0; yOffset <= yDif(); yOffset++) {
                for (int zOffset = 0; zOffset <= zDif(); zOffset++) {
                    Block from = assetWorld.getBlockAt(ax+xOffset,ay+yOffset, az+zOffset);
                    Block to = targetWorld.getBlockAt(tx+xOffset,ty+yOffset, tz+zOffset);
                    if (skipAirCopy && from.getType() == Material.AIR) continue;
                    to.setType(from.getType());
                    to.setBlockData(from.getBlockData());
                }
            }
        }
    }

    public static Asset generate(String id, Location first, Location second) {
        int ax = Math.min(first.getBlockX(), second.getBlockX());
        int bx = Math.max(first.getBlockX(), second.getBlockX());
        int ay = Math.min(first.getBlockY(), second.getBlockY());
        int by = Math.max(first.getBlockY(), second.getBlockY());
        int az = Math.min(first.getBlockZ(), second.getBlockZ());
        int bz = Math.max(first.getBlockZ(), second.getBlockZ());
        Asset asset = new Asset(id,ax,ay,az,bx,by,bz);
        return asset;
    }

}
