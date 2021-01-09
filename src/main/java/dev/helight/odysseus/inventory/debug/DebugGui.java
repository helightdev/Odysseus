package dev.helight.odysseus.inventory.debug;

import dev.helight.odysseus.inventory.Gui;
import dev.helight.odysseus.inventory.InteractivePoint;
import dev.helight.odysseus.inventory.routes.PagedBarRoute;
import dev.helight.odysseus.item.Item;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DebugGui extends Gui {

    @Override
    public void construct() {
        addNode("root", 0, InteractivePoint.builder().item(new ItemStack(Material.DIAMOND)).event(event -> changeView("paged")).build())
        .addNode("root", 8, InteractivePoint.nextPage(new ItemStack(Material.OAK_BUTTON)))
        .addNode("root", 1, 0, InteractivePoint.previousPage(new ItemStack(Material.OAK_BUTTON)))
        .addNode("root", 1, 8, InteractivePoint.switchView(new ItemStack(Material.ANVIL), "secondary"))
        .addNode("secondary", 0, InteractivePoint.builder().item(new ItemStack(Material.GOLD_INGOT)).build());

        PagedBarRoute route = new PagedBarRoute(getRows(), this);
        for (int i = 0; i < 255; i++) {
            ItemStack stack = Item.builder(Material.GOLD_INGOT).name(i+"").delegate();
            route.put(i, InteractivePoint.builder().item(stack).build());
        }
        getViews().put("paged", route);

        createInventory();
    }

}
