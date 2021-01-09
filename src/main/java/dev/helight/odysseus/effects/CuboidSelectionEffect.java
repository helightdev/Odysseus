package dev.helight.odysseus.effects;

import de.slikey.effectlib.Effect;
import de.slikey.effectlib.EffectManager;
import de.slikey.effectlib.EffectType;
import dev.helight.odysseus.Raycast;
import dev.helight.odysseus.asset.Asset;
import dev.helight.odysseus.region.MathUtils;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;

public class CuboidSelectionEffect extends Effect {

    public String actionbar = "";

    public CuboidSelectionEffect(EffectManager effectManager) {
        super(effectManager);
        type = EffectType.REPEATING;
        period=5;
        iterations=-1;
        color= Color.SILVER;
    }

    @Override
    public void onRun() {
        World world = getLocation().getWorld();

        double ax = Math.min(getLocation().getX(), getTarget().getX());
        double ay = Math.min(getLocation().getY(), getTarget().getY());
        double az = Math.min(getLocation().getZ(), getTarget().getZ());

        double bx = Math.max(getLocation().getX(), getTarget().getX());
        double by = Math.max(getLocation().getY(), getTarget().getY());
        double bz = Math.max(getLocation().getZ(), getTarget().getZ());

        for (double x = ax; x < bx; x+=0.25) {
            display(Particle.REDSTONE, new Location(world,x,ay,az), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,x,by,az), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,x,ay,bz), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,x,by,bz), color, 0, 1);
        }

        for (double y = ay; y < by; y+=0.25) {
            display(Particle.REDSTONE, new Location(world,ax,y,az), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,ax,y,bz), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,bx,y,az), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,bx,y,bz), color, 0, 1);
        }

        for (double z = az; z < bz; z+=0.25) {
            display(Particle.REDSTONE, new Location(world,ax,ay,z), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,ax,by,z), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,bx,ay,z), color, 0, 1);
            display(Particle.REDSTONE, new Location(world,bx,by,z), color, 0, 1);
        }

        Location a = new Location(world,ax,ay,az).subtract(3, 3, 3);
        Location b = new Location(world,bx,by,bz).add(3, 3, 3);
        Location center = b.clone().subtract(a.clone()).multiply(0.5).add(a.clone());

        if (MathUtils.containsBox(a,b,targetPlayer.getLocation())) {
            double dist = Raycast.maxVectorDistance(
                    targetPlayer.getEyeLocation().getDirection(),
                    Raycast.between(targetPlayer.getEyeLocation(), center)
            );

            if (dist <= 0.5) {
                targetPlayer.sendActionBar(actionbar);
            }
        }
    }
}
