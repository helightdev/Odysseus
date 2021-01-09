package dev.helight.odysseus.database.codecs;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

public class JsonObjectCodec implements Codec<JsonObject> {

    private static final Gson gson = new Gson();

    @Override
    public JsonObject decode(BsonReader reader, DecoderContext decoderContext) {
        return gson.fromJson(reader.readString(), JsonObject.class);
    }

    @Override
    public void encode(BsonWriter writer, JsonObject value, EncoderContext encoderContext) {
        writer.writeString(gson.toJson(value));
    }

    @Override
    public Class<JsonObject> getEncoderClass() {
        return JsonObject.class;
    }

}
