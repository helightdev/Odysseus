package dev.helight.odysseus.database.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerCodec implements Codec<Player> {

    @Override
    public Player decode(BsonReader reader, DecoderContext decoderContext) {
        return Bukkit.getPlayer(UUID.fromString(reader.readString()));
    }

    @Override
    public void encode(BsonWriter writer, Player value, EncoderContext encoderContext) {
        writer.writeString(value.getUniqueId().toString());
    }

    @Override
    public Class<Player> getEncoderClass() {
        return Player.class;
    }

}
