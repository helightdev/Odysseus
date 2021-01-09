package dev.helight.odysseus.block;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.helight.odysseus.database.DataManager;
import dev.helight.odysseus.task.LightScheduler;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class CustomBlockManager {

    private static Map<String, CustomBlockType> types = new ConcurrentHashMap<>();
    @Getter
    private static Map<Block, CustomBlockDBO> blocks = new ConcurrentHashMap<>();
    private static MongoCollection<CustomBlockDBO> mongoCollection;

    public static void init() {
        mongoCollection = DataManager.database().getCollection("blocks", CustomBlockDBO.class);
    }

    public static CustomBlockType getOrNull(String name) {
        for (Map.Entry<String, CustomBlockType> entry : types.entrySet()) {
            if (entry.getValue().name().endsWith(name.toLowerCase())) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static CustomBlockType getOrNull(ItemStack itemStack) {
        for (Map.Entry<String, CustomBlockType> entry : types.entrySet()) {
            if (entry.getValue().getItem().isSimilar(itemStack)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public static Pair<CustomBlockDBO, CustomBlockType> getOrNull(Block block) {
        if (!blocks.containsKey(block)) return null;
        CustomBlockDBO dbo = blocks.get(block);
        return Pair.of(dbo, types.get(dbo.getType()));
    }

    protected static void load(Chunk chunk) {
        LightScheduler.instance().execute(() -> mongoCollection.find(Filters.and(
                Filters.eq("c", chunkKey(chunk)),
                Filters.eq("w", chunk.getWorld().getUID().toString()))
        ).forEach((Consumer<? super CustomBlockDBO>) dbo -> {
            System.out.println("Loaded Block " + blockKey(dbo.getBlock()));
            blocks.put(dbo.getBlock(), dbo);
        }));
    }

    public static void remove(Block block) {
        blocks.remove(block);
        LightScheduler.instance().execute(() -> mongoCollection.deleteMany(
                Filters.and(Filters.eq("k", blockKey(block)), Filters.eq("w", block.getWorld().getUID().toString()))
        ));
    }

    public static void add(CustomBlockDBO dbo) {
        blocks.put(dbo.getBlock(), dbo);
        LightScheduler.instance().execute(() -> mongoCollection.insertOne(dbo));
    }

    public static void update(CustomBlockDBO dbo) {
        if (blocks.containsKey(dbo.getBlock())) blocks.put(dbo.getBlock(), dbo);
        Gson gson = new Gson();
        LightScheduler.instance().execute(() -> mongoCollection.updateMany(
                Filters.and(Filters.eq("k", blockKey(dbo.getBlock())), Filters.eq("w", dbo.getBlock().getWorld().getUID().toString())),
                Updates.set("payload", gson.toJson(dbo.getPayload()))));
    }

    public static void register(CustomBlockType block) {
        types.put(block.name(), block);
    }

    protected static void unload(Chunk chunk) {
        List<Block> blockKeys = new ArrayList<>();
        blocks.forEach((x,y) -> {
            if (chunkKey(x.getChunk()).equals(chunkKey(chunk))) {
                System.out.println("Unloading block " + chunkKey(x.getChunk()));
                blockKeys.add(x);
            }
        });
        blockKeys.forEach(blocks::remove);
    }
    
    public static String chunkKey(Chunk chunk) {
        return String.format("%s:%s:%s", chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
    }

    public static String blockKey(Block block) {
        return String.format("%s:%s:%s:%s", block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
    }
}
