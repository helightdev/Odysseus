package dev.helight.odysseus.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import dev.helight.odysseus.asset.Asset;
import dev.helight.odysseus.asset.AssetWorld;
import dev.helight.odysseus.asset.Assets;
import dev.helight.odysseus.chat.Chat;
import dev.helight.odysseus.editor.PositionSelector;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@CommandAlias("editor")
public class EditorCommand extends BaseCommand {

    @Subcommand("start")
    public static void world(Player player) {
        PositionSelector.start(player);
    }

    @Subcommand("center")
    public static void create(Player player) {
        PositionSelector.PositionEditingSession session = PositionSelector.sessionMap.get(player.getUniqueId());
        if (session == null) return;
        session.current.setX(Math.floor(session.current.getX()) + 0.5);
        session.current.setY(Math.floor(session.current.getY()) + 0.5);
        session.current.setZ(Math.floor(session.current.getZ()) + 0.5);
        session.update();
    }

    @Subcommand("align")
    public static void align(Player player) {
        PositionSelector.PositionEditingSession session = PositionSelector.sessionMap.get(player.getUniqueId());
        if (session == null) return;
        double x = session.current.getX();
        double xFloor = session.current.getBlockX();
        double y = session.current.getY();
        double yFloor = session.current.getBlockY();
        double z = session.current.getZ();
        double zFloor = session.current.getBlockZ();

        double step = 0.05;

        double xBuf = xFloor;
        for (double d = xFloor; d < x; d+=step) {
            xBuf = d;
        }

        double yBuf = yFloor;
        for (double d = yFloor; d < y; d+=step) {
            yBuf = d;
        }

        double zBuf = zFloor;
        for (double d = zFloor; d < z; d+=step) {
            zBuf = d;
        }

        session.current.setX(xBuf);
        session.current.setY(yBuf);
        session.current.setZ(zBuf);
        session.update();

    }

}
