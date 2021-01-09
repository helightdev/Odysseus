package dev.helight.odysseus.database.codecs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.helight.odysseus.block.CustomBlockDBO;
import dev.helight.odysseus.block.CustomBlockManager;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.UUID;


public class CustomBlockCodec implements Codec<CustomBlockDBO> {

    private static final Gson gson = new Gson();

    @Override
    public CustomBlockDBO decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        reader.readObjectId();
        String type = reader.readString("t");
        JsonObject object = gson.fromJson(reader.readString("p"), JsonObject.class);
        World world = Bukkit.getWorld(UUID.fromString(reader.readString("w")));
        Location location = new Location(world,
                reader.readInt32("x"),
                reader.readInt32("y"),
                reader.readInt32("z"));
        reader.readString("c");
        reader.readString("k");
        reader.readEndDocument();
        Block block = world.getBlockAt(location);
        CustomBlockDBO dbo = new CustomBlockDBO();
        dbo.setType(type);
        dbo.setPayload(object);
        dbo.setBlock(block);
        return dbo;
    }

    @Override
    public void encode(BsonWriter writer, CustomBlockDBO value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("t", value.getType());
        writer.writeString("p", gson.toJson(value.getPayload()));
        writer.writeString("w", value.getBlock().getWorld().getUID().toString());
        writer.writeInt32("x", value.getBlock().getX());
        writer.writeInt32("y", value.getBlock().getY());
        writer.writeInt32("z", value.getBlock().getZ());
        writer.writeString("c", CustomBlockManager.chunkKey(value.getBlock().getChunk()));
        writer.writeString("k", CustomBlockManager.blockKey(value.getBlock()));
        writer.writeEndDocument();
    }

    @Override
    public Class<CustomBlockDBO> getEncoderClass() {
        return CustomBlockDBO.class;
    }

}
