package dev.helight.odysseus.region;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import dev.helight.odysseus.database.DataManager;
import dev.helight.odysseus.region.impl.BlockedRegion;
import dev.helight.odysseus.region.impl.DefaultRegionImpl;
import dev.helight.odysseus.region.tasks.DataSaveTask;
import dev.helight.odysseus.task.AbstractRoutine;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RegionManager {

    @Getter
    private static final List<Region> regions = Collections.synchronizedList(new ArrayList<>());

    @Getter
    private static final Map<String, RegionType> types = new ConcurrentHashMap<>();

    public static boolean isLocked = true;

    public static final RegionType DEFAULT_REGION = new DefaultRegionImpl();

    public static void load() {
        regions.clear();
        registerType(new BlockedRegion());
        MongoCollection<Region> poolRepo = DataManager.database().getCollection("regions", Region.class);
        for (Region region : poolRepo.find()) {
            region.setPersist(true);
            System.out.println("    " + region.toString());
            regions.add(region);
        }
        AbstractRoutine.assureRunning(DataSaveTask.class);
    }

    public static void save() {
        MongoCollection<Region> poolRepo = DataManager.database().getCollection("regions", Region.class);
        for (Region region : regions) {
            if (region.isPersist()) {
                if (poolRepo.countDocuments(Filters.eq("identifier", region.getIdentifier())) == 0) {
                    poolRepo.insertOne(region);
                } else {
                    poolRepo.replaceOne(Filters.eq("identifier", region.getIdentifier()), region);
                }
            }
        }
    }

    public static boolean canBreak(Player player, Block block) {
        List<Region> regions = getIntersecting(block.getLocation());
        List<RIR> rirs = new ArrayList<>();
        for (Region region : regions) {
            RIR rir = types.getOrDefault(region.getType(), DEFAULT_REGION).breakBlock(player, block);
            rirs.add(rir);
        }

        if (rirs.contains(RIR.FORCED)) return true;
        if (rirs.contains(RIR.DISALLOWED)) return false;
        return true;
    }

    public static boolean canInteract(Player player, Block block) {
        List<Region> regions = getIntersecting(block.getLocation());
        List<RIR> rirs = new ArrayList<>();
        for (Region region : regions) {
            RIR rir = types.getOrDefault(region.getType(), DEFAULT_REGION).interactBlock(player, block);
            rirs.add(rir);
        }

        if (rirs.contains(RIR.FORCED)) return true;
        return !rirs.contains(RIR.DISALLOWED);
    }

    public static boolean canPlace(Player player, ItemStack itemStack, Block block) {
        List<Region> regions = getIntersecting(block.getLocation());
        List<RIR> rirs = new ArrayList<>();
        for (Region region : regions) {
            RIR rir = types.getOrDefault(region.getType(), DEFAULT_REGION).placeBlock(player, itemStack, block);
            rirs.add(rir);
        }

        if (rirs.contains(RIR.FORCED)) return true;
        return !rirs.contains(RIR.DISALLOWED);
    }

    public static boolean canPvp(Player player, Player target) {
        List<Region> regions = getIntersecting(target.getLocation());
        List<RIR> rirs = new ArrayList<>();
        for (Region region : regions) {
            RIR rir = types.getOrDefault(region.getType(), DEFAULT_REGION).pvp(player, target);
            rirs.add(rir);
        }

        if (rirs.contains(RIR.FORCED)) return true;
        return !rirs.contains(RIR.DISALLOWED);
    }

    public static boolean canEve(Entity entity, Entity attacker) {
        List<Region> regions = getIntersecting(entity.getLocation());
        List<RIR> rirs = new ArrayList<>();
        for (Region region : regions) {
            RIR rir = types.getOrDefault(region.getType(), DEFAULT_REGION).eve(attacker,entity);
            rirs.add(rir);
        }

        if (rirs.contains(RIR.FORCED)) return true;
        return !rirs.contains(RIR.DISALLOWED);
    }

    public static List<Region> getIntersecting(Location location) {
        return regions
                .stream()
                .filter(r -> r.contains(location))
                .collect(Collectors.toList());
    }

    public static Region getRegion(String identifier) {
       return regions.stream()
                .filter(region -> Objects.equals(region.getIdentifier(), identifier))
                .findFirst()
                .orElse(null);
    }

    public static void deleteRegion(String identifier) {
        regions.parallelStream()
                .filter(region -> Objects.equals(region.getIdentifier(), identifier))
                .peek(region -> {
                    if (region.isPersist()) DataManager.database().getCollection("regions").deleteMany(Filters.eq("identifier", identifier));
                })
                .forEach(regions::remove);
    }

    public static void registerType(RegionType type) {
        types.put(type.getClass().getName(), type);
    }


}
