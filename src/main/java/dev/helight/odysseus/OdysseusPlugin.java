package dev.helight.odysseus;

import org.bukkit.plugin.java.JavaPlugin;

public class OdysseusPlugin extends JavaPlugin {

    private final Odysseus odysseus;

    public OdysseusPlugin() {
        odysseus = new Odysseus(this);
    }

    @Override
    public void onLoad() {
        odysseus.startup();
    }

    @Override
    public void onEnable() {
        odysseus.postWorld();
    }

    @Override
    public void onDisable() {
        odysseus.shutdown();
    }
}
