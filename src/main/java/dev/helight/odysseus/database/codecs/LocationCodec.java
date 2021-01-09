package dev.helight.odysseus.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

public class LocationCodec implements Codec<Location> {

    @Override
    public Location decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        Location location = new Location(Bukkit.getWorld(UUID.fromString(reader.readString("w"))),
                reader.readDouble("x"),
                reader.readDouble("y"),
                reader.readDouble("z"));
        reader.readEndDocument();
        return location;
    }

    @Override
    public void encode(BsonWriter writer, Location value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("w", value.getWorld().getUID().toString());
        writer.writeDouble("x", value.getX());
        writer.writeDouble("y", value.getY());
        writer.writeDouble("z", value.getZ());
        writer.writeEndDocument();
    }

    @Override
    public Class<Location> getEncoderClass() {
        return Location.class;
    }

}
