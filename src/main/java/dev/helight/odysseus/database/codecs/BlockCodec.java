package dev.helight.odysseus.database.codecs;

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

import static dev.helight.odysseus.block.CustomBlockManager.*;

public class BlockCodec implements Codec<Block> {

    @Override
    public Block decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        World world = Bukkit.getWorld(UUID.fromString(reader.readString("w")));
        Location location = new Location(world,
                reader.readInt32("x"),
                reader.readInt32("y"),
                reader.readInt32("z"));
        reader.readString("c");
        reader.readString("k");
        reader.readEndDocument();
        return world.getBlockAt(location);
    }

    @Override
    public void encode(BsonWriter writer, Block value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("w", value.getWorld().getUID().toString());
        writer.writeInt32("x", value.getX());
        writer.writeInt32("y", value.getY());
        writer.writeInt32("z", value.getZ());
        writer.writeString("c", chunkKey(value.getChunk()));
        writer.writeString("k", blockKey(value));
        writer.writeEndDocument();
    }

    @Override
    public Class<Block> getEncoderClass() {
        return Block.class;
    }

}
