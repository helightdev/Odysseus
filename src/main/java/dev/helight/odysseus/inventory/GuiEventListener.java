package dev.helight.odysseus.inventory;

import dev.helight.odysseus.events.BetterListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;

public class GuiEventListener extends BetterListener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        event.setCancelled(true);
        Gui gui = (Gui)event.getInventory().getHolder();
        gui.notifyAction();
        if (event.getClickedInventory() == null) return;
        if (!(event.getClickedInventory().getHolder() instanceof Gui)) return;
        int slot = gui.findRelativeByActual(event.getSlot());
        InteractivePoint node = gui.getNode(slot);
        if (node != null) node.getEvent().accept(event);
    }

    @EventHandler
    public void onInventoryMove(InventoryMoveItemEvent event) {
        if (event.getSource().getHolder() == null) if (!(event.getSource().getHolder() instanceof Gui)) {
            Gui gui = (Gui)event.getSource().getHolder();
            gui.notifyAction();
            event.setCancelled(true);
        }

        if (event.getDestination().getHolder() == null) if (!(event.getDestination().getHolder() instanceof Gui)) {
            Gui gui = (Gui)event.getDestination().getHolder();
            gui.notifyAction();
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof Gui)) return;
        Gui gui = (Gui)event.getInventory().getHolder();
        gui.dispose(Gui.DisposeReason.NATURAL);
    }


}
