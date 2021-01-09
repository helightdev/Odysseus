package dev.helight.odysseus.chat;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.awt.*;

public class Chat {

    public static void send(Player player, String format, Object... objects) {
        String msg = String.format(format,objects);
        player.sendMessage("§8[§eSystem§8]§7 "+msg);
    }

    public static void send(Player player, String prefix, String format, Object... objects) {
        String msg = String.format(format,objects);
        player.sendMessage("§8[§e"+prefix+"8]§7 "+msg);
    }

    public static void send(Player player, String color, String prefix, String format, Object... objects) {
        String msg = String.format(format,objects);
        player.sendMessage("§8["+color+prefix+"§8]§7 "+msg);
    }

    public static void clearSend(Player player, String color, String prefix, String format, Object... objects) {
        for (int i = 0; i < 20; i++) {
            player.sendMessage("");
        }
        String msg = String.format(format,objects);
        player.sendMessage("§8["+color+prefix+"§8]§7 "+msg);
    }

    public static void clearSend(Player player, TextComponent textComponent) {
        for (int i = 0; i < 20; i++) {
            player.sendMessage("");
        }
        player.sendMessage(textComponent);
    }

}
