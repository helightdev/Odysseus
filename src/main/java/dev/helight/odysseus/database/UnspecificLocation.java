package dev.helight.odysseus.database;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bukkit.Location;
import org.bukkit.World;

@Getter
@Setter
public class UnspecificLocation {

    private double x;
    private double y;
    private double z;

    public UnspecificLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public UnspecificLocation() { }

    public Location toLocation(World world) {
        return new Location(world,x,y,z);
    }

    @Override
    public String toString() {
        return "§c" + x + " §a" + y + " §b" + z + "§7";
    }

    public static UnspecificLocation from(Location location) {
        return new UnspecificLocation(location.getX(), location.getY(), location.getZ());
    }

}
