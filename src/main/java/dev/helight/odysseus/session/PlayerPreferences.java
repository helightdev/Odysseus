package dev.helight.odysseus.session;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import dev.helight.odysseus.database.DataManager;
import dev.helight.odysseus.task.LightScheduler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

@ToString
public class PlayerPreferences {

    public PlayerPreferences(ObjectId _id, Player player, JsonObject backing) {
        this._id = _id;
        this.player = player;
        this.backing = backing;
    }

    public PlayerPreferences() {
    }

    @Getter @Setter
    private ObjectId _id;

    @Setter @Getter
    private Player player;
    @Getter
    private JsonObject backing;

    public CompletableFuture<JsonObject> loadHere() {
        CompletableFuture<JsonObject> future = new CompletableFuture<>();
        LightScheduler.instance().execute(() -> {
            backing = load(player).backing;
            future.complete(backing);
        });
        return future;
    }

    @SneakyThrows
    public void store() {
        System.out.println(1);
        MongoCollection<PlayerPreferences> collection = DataManager.database().getCollection("preferences", PlayerPreferences.class);
        System.out.println(2);
        long document = collection.countDocuments(Filters.eq("player", player));
        if (document == 0) {
            System.out.println("+");
            System.out.println(this);
            LightScheduler.instance().execute(() -> collection.insertMany(Collections.singletonList(this)));
        } else {
            System.out.println("#");
            collection.updateOne(Filters.eq("player", player), Updates.set("data", backing));
        }
        System.out.println(3);
    }

    public PlayerPreferences load(Player player) {
        System.out.println(1);
        MongoCollection<PlayerPreferences> collection = DataManager.database().getCollection("preferences", PlayerPreferences.class);
        System.out.println(2);
        PlayerPreferences preferences = collection.find(Filters.eq("player", player)).first();
        System.out.println(3);
        if (preferences == null) {
            preferences = new PlayerPreferences();
            preferences.player = player;
            preferences.backing = new JsonObject();
        }
        return preferences;
    }

}
