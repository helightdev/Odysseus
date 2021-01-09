package dev.helight.odysseus.session;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public class PlayerSession {

    public static final List<PlayerSession> sessions = Collections.synchronizedList(new ArrayList<>());

    public PlayerSession(UUID uuid) {
        this.uuid = uuid;
    }

    private UUID uuid;
    private List<String> regions = new ArrayList<>();
    private Map<String, String> data = new HashMap<>();

    public CompletableFuture<PlayerPreferences> preferences() {
        PlayerPreferences playerPreferences = new PlayerPreferences();
        playerPreferences.setPlayer(Bukkit.getPlayer(uuid));
        return playerPreferences.loadHere().thenApply(o -> playerPreferences);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerSession that = (PlayerSession) o;

        return uuid.equals(that.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    public static PlayerSession session(Player player) {
        for (PlayerSession session : sessions) {
            if (session.uuid == player.getUniqueId()) {
                return session;
            }
        }
        return null;
    }
}
