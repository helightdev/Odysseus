package dev.helight.odysseus.inventory;

import dev.helight.odysseus.events.BetterListener;
import dev.helight.odysseus.inventory.impl.DefaultRouteImpl;
import dev.helight.odysseus.task.LightScheduler;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Gui implements InventoryHolder {

    //TODO Make value references weak
    public static final Map<UUID, Gui> cache = new ConcurrentHashMap<>();

    @Getter
    private Inventory inventory = null;

    @Getter
    private final UUID id;

    @Setter
    @Getter
    private int rows = 6;

    public abstract void construct();

    @Getter
    private long lastAction = System.nanoTime();

    @Getter
    private Map<String, Route> views = new HashMap<>();

    @Setter
    @Getter
    private String title = " ";

    private String current = "root";
    private int offset = 0;

    public void notifyAction() {
        lastAction = System.nanoTime();
    }

    public void show(HumanEntity entity) {
        entity.openInventory(inventory);
    }

    public Gui addNode(String view, int slot, InteractivePoint node) {
        if (!views.containsKey(view)) {
            views.put(view, new DefaultRouteImpl());
        }

        node.setParent(this);
        views.get(view).put(slot, node);
        return this;
    }

    public Gui addNode(String view, int page, int slot, InteractivePoint node) {
        if (!views.containsKey(view)) {
            views.put(view, new DefaultRouteImpl());
        }

        node.setParent(this);
        views.get(view).put(slot + (rows * 9 * page), node);
        return this;
    }

    protected InteractivePoint getNode(int i) {
        int index = i % (rows * 9);
        return views.get(current).get(i, index, currentPage());
    }

    public Gui() {
        this.id = UUID.randomUUID();
        cache.put(id, this);
    }

    @SneakyThrows
    public void checkInitialisation() {
        BetterListener.assureRegistered(GuiEventListener.class);
    }

    public void dispose(DisposeReason reason) {
        //Close inventory for remaining viewers
        if (inventory != null) {
            for (HumanEntity viewer : inventory.getViewers()) {
                LightScheduler.instance().synchronize(() -> viewer.closeInventory(InventoryCloseEvent.Reason.PLUGIN), 1);
            }
        }

        //Finalize
        this.inventory = null;
        cache.remove(id);
    }

    public void createInventory() {
        inventory = Bukkit.createInventory(this, rows * 9, title);
        render();
    }

    public int pageSize() {
        return rows * 9;
    }

    public void changeOffset(int difference) {
        offset += difference * (rows * 9);
        render();
    }

    public int currentPage() {
        return offset / (rows * 9);
    }

    public void changeView(String view) {
        current = view;
        offset = 0;
        render();
    }

    public int findRelativeByActual(int actual) {
        return offset + actual;
    }

    public void render() {
        Route route = views.get(current);
        route.build();
        for (int i = offset; i < offset + (rows * 9); i++) {
            int index = i % (rows * 9);
            InteractivePoint node = route.get(i, index, currentPage());
            if (node != null) node.build();
            inventory.setItem(index, node == null ? null : node.getItem());
        }
    }

    public enum DisposeReason {

        NATURAL,
        PLUGIN,
        SECURITY,
        GARBAGE_COLLECTOR

    }
}
