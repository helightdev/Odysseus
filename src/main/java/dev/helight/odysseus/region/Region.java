package dev.helight.odysseus.region;

import com.google.gson.JsonObject;
import de.slikey.effectlib.effect.LineEffect;
import dev.helight.odysseus.Odysseus;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.List;

@Data
public class Region {

    private String identifier;
    private String type;
    private JsonObject payload = new JsonObject();
    private List<Location> points;
    private boolean persist = true;

    public Region() { }

    public Region(String identifier, String type, List<Location> points) {
        this.identifier = identifier;
        this.type = type;
        this.points = points;
    }

    public Region(String identifier, String type, JsonObject payload, List<Location> points, boolean persist) {
        this.identifier = identifier;
        this.type = type;
        this.payload = payload;
        this.points = points;
        this.persist = persist;
    }

    public boolean contains(Location location) {
        if (points.size() < 3) return false;
        if (!location.getWorld().getName().equals(points.get(0).getWorld().getName())) return false;
        return MathUtils.contains(points, location);
    }

    public boolean contains(Player player) {
        return contains(player.getLocation());
    }

    public boolean contains(Entity entity) {
        return contains(entity.getLocation());
    }

    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    public void addPoint(Location location) {
        points.add(location);
    }

    public void removePoint(Location location) {
        points.remove(location);
    }

    public void mark(double y) {
        if (points.size() == 0) return;
        if (points.size() == 1) {
            LineEffect effect = new LineEffect(Odysseus.getInstance().getEffectManager());
            effect.setLocation(points.get(0).clone());
            effect.getLocation().setY(y);
            Location target = points.get(0);
            target.setY(y + 0.5);
            effect.setTargetLocation(target);
            effect.targetPlayers = (List<Player>) Bukkit.getOnlinePlayers();
            effect.start();
        }

        for (int i = 0; i < points.size(); i++) {
            int previous = i - 1;
            if (previous == -1) previous = points.size() - 1;
            LineEffect effect = new LineEffect(Odysseus.getInstance().getEffectManager());
            effect.setLocation(points.get(i).clone());
            effect.getLocation().setY(y);
            Location target = points.get(previous);
            target.setY(y);
            effect.setTargetLocation(target);
            effect.targetPlayers = (List<Player>) Bukkit.getOnlinePlayers();
            effect.start();
        }
    }

}
