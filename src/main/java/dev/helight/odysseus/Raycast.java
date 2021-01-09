package dev.helight.odysseus;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Raycast {

    public static Location getLookingAt(Player player, float precision, int maxDepth, boolean colluding) {
        float multiply = 1 / precision;
        float halfMultiply = multiply / 2;
        Location origin = player.getEyeLocation();
        Location current = origin.clone();
        Location last = origin.clone();
        Vector eyeDirection = player.getEyeLocation().getDirection().normalize().multiply(multiply);
        boolean iterate = true;
        int depth = 0;
        while (iterate && depth < maxDepth) {
            depth++;
            Block block = current.getBlock();
            if (block.getType().isSolid()) iterate = false;
            if (block.getWorld().getNearbyEntities(current, halfMultiply, halfMultiply, halfMultiply).stream().anyMatch(entity -> entity.getEntityId() != player.getEntityId())) iterate = false;

            if (!iterate) continue;
            last = current.clone();
            current.add(eyeDirection);
        }
        return colluding ? current : last;
    }

    public static double maxVectorDistance(Vector a, Vector b) {
        Vector an = a.normalize();
        Vector bn = b.normalize();
        double xDif = Math.abs(bn.getX() - an.getX());
        double yDif = Math.abs(bn.getY() - an.getY());
        double zDif = Math.abs(bn.getZ() - an.getZ());
        return Math.max(Math.max(xDif, yDif), zDif);
    }

    public static Vector between(Location a, Location b) {
        return b.toVector().subtract(a.toVector());
    }

}
