package dev.helight.odysseus.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;

import java.util.UUID;

public class CraftPlayerCodec implements Codec<CraftPlayer> {

    @Override
    public CraftPlayer decode(BsonReader reader, DecoderContext decoderContext) {
        return (CraftPlayer) Bukkit.getPlayer(UUID.fromString(reader.readString()));
    }

    @Override
    public void encode(BsonWriter writer, CraftPlayer value, EncoderContext encoderContext) {
        writer.writeString(value.getUniqueId().toString());
    }

    @Override
    public Class<CraftPlayer> getEncoderClass() {
        return CraftPlayer.class;
    }

}
