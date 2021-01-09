package dev.helight.odysseus.database.codecs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.helight.odysseus.session.PlayerPreferences;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.ObjectId;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerPreferenceCodec implements Codec<PlayerPreferences> {

    @Override
    public PlayerPreferences decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        ObjectId id = reader.readObjectId();
        Player player = Bukkit.getPlayer(UUID.fromString(reader.readString("player")));

        JsonObject json = new Gson().fromJson(reader.readString("data"), JsonObject.class);

        reader.readEndDocument();

        return new PlayerPreferences(id, player, json);
    }

    @Override
    public void encode(BsonWriter writer, PlayerPreferences value, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("player", value.getPlayer().getUniqueId().toString());
        writer.writeString("data", new Gson().toJson(value.getBacking()));
        writer.writeEndDocument();
    }

    @Override
    public Class<PlayerPreferences> getEncoderClass() {
        return PlayerPreferences.class;
    }

}
