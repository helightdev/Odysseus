package dev.helight.odysseus.script;

import dev.helight.odysseus.database.UnspecificLocation;
import dev.helight.odysseus.scene.Runtime;
import dev.helight.odysseus.scene.Scene;
import dev.helight.odysseus.scene.SceneWorld;
import lombok.Getter;
import org.bukkit.Location;

import javax.annotation.OverridingMethodsMustInvokeSuper;

public abstract class LocationObject extends GameObject implements LocationHolder {

    private UnspecificLocation location;

    @Override
    @OverridingMethodsMustInvokeSuper
    public void enable() {
        super.enable();
        SceneLocation sceneLocation = getClass().getAnnotation(SceneLocation.class);
        if (sceneLocation == null) throw new IllegalArgumentException("Class must be annotated with @SceneLocation");
        location = new SceneWorld(Runtime.world())
                .readSceneData(true)
                .getLocationMapping()
                .get(sceneLocation.value());
    }

    public final boolean checkEqualsLocation(Location other) {
        Location loc = location.toLocation(other.getWorld());
        return loc.equals(other);
    }

    public final boolean checkEqualsBlockLocation(Location other) {
        Location loc = location.toLocation(other.getWorld());
        return loc.getBlock().getLocation().equals(other.getBlock().getLocation());
    }

    public final boolean checkDistanceLocation(Location other, double distance) {
        Location loc = location.toLocation(other.getWorld());
        return loc.distance(other) < distance;
    }

    @Override
    public UnspecificLocation getLocation() {
        return location;
    }
}
