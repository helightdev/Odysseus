package dev.helight.odysseus.session.impl;

import dev.helight.odysseus.events.BetterListener;
import dev.helight.odysseus.session.PlayerSession;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SessionListener extends BetterListener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerSession.sessions.add(new PlayerSession(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        PlayerSession session = PlayerSession.session(event.getPlayer());
        PlayerSession.sessions.remove(session);
    }

}
