package dev.helight.odysseus.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import dev.helight.odysseus.chat.Chat;
import dev.helight.odysseus.region.Region;
import dev.helight.odysseus.region.RegionManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

@CommandAlias("region")
@CommandPermission("nuggets.region")
public class RegionCommand extends BaseCommand {

    @Subcommand("add")
    public void add(Player player, String rname, String type) {
        Region region = RegionManager.getRegion(rname);
        boolean created = false;
        if (region == null) {
            region = new Region(rname, type, new ArrayList<>(Arrays.asList(player.getLocation())));
            RegionManager.getRegions().add(region);
            Chat.send(player, "Region %s mit Typ %s erstellt", rname, type);
        } else {
            region.addPoint(player.getLocation());
            Chat.send(player, "Position %s zu %s mit Typ %s", player.getLocation().toString(), rname, type);
        }
        region.mark(player.getLocation().getY() + 1);
        RegionManager.save();
    }

    @Subcommand("remove")
    public void remove(Player player, Region region) {
        RegionManager.deleteRegion(region.getIdentifier());
        Chat.send(player, "Region gelöscht %s", region.getIdentifier());
    }

    @Subcommand("mark")
    public void mark(Player player) {
        RegionManager.getIntersecting(player.getLocation()).forEach(region -> region.mark(player.getLocation().getY() + 1));
        Chat.send(player, "Markiere Region");
    }

    @Subcommand("tag")
    public void tag(Player player, Region[] regions, String key, @Optional String value, @Optional String type) {
        if (value != null) {
            if (type == null) {
                regions[0].getPayload().addProperty(key,value);
            } else if (type.equalsIgnoreCase("bool")) {
                regions[0].getPayload().addProperty(key,Boolean.parseBoolean(value));
            } else if (type.equalsIgnoreCase("int")) {
                regions[0].getPayload().addProperty(key,Integer.parseInt(value));
            } else if (type.equalsIgnoreCase("double")) {
                regions[0].getPayload().addProperty(key,Double.parseDouble(value));
            }
        } else {
            regions[0].getPayload().remove(key);
        }
        Chat.send(player, "Aktion erfolgreich");
    }

}
