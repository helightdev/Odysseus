package dev.helight.odysseus.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import dev.helight.odysseus.block.CustomBlockManager;
import dev.helight.odysseus.block.CustomBlockType;
import org.bukkit.entity.Player;

@CommandAlias("cbl")
public class CustomBlocksCommand extends BaseCommand {

    @Subcommand("give")
    public void add(Player player, String name) {
        CustomBlockType type = CustomBlockManager.getOrNull(name);
        player.getInventory().addItem(type.getItem());
    }

}
