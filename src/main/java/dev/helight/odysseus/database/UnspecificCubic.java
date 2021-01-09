package dev.helight.odysseus.database;

import dev.helight.odysseus.region.MathUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
public class UnspecificCubic {
    
    private UnspecificLocation a;
    private UnspecificLocation b;

    public boolean contains(Location location) {
        return MathUtils.containsBox(a.toLocation(location.getWorld()), b.toLocation(location.getWorld()), location);
    }

    public static UnspecificCubic from(UnspecificLocation aLoc, UnspecificLocation bLoc) {
        double ax = Math.min(aLoc.getX(), bLoc.getX());
        double ay = Math.min(aLoc.getY(), bLoc.getY());
        double az = Math.min(aLoc.getZ(), bLoc.getZ());

        double bx = Math.max(aLoc.getX(), bLoc.getX());
        double by = Math.max(aLoc.getY(), bLoc.getY());
        double bz = Math.max(aLoc.getZ(), bLoc.getZ());

        UnspecificLocation a = new UnspecificLocation();
        a.setX(ax);
        a.setY(ay);
        a.setZ(az);

        UnspecificLocation b = new UnspecificLocation();
        b.setX(bx);
        b.setY(by);
        b.setZ(bz);

        UnspecificCubic cubic = new UnspecificCubic();
        cubic.setA(a);
        cubic.setB(b);

        return cubic;
    }

    @Override
    public String toString() {
        return "§f§lA:§r " + a.toString() + " §f§lB:§r " + b + "§7";
    }
}
