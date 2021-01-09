package dev.helight.odysseus.inventory.routes;

import dev.helight.odysseus.inventory.Gui;
import dev.helight.odysseus.inventory.InteractivePoint;
import dev.helight.odysseus.item.Item;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

//TODO Implement
public class PagedBarRoute extends BarRoute {

    private final Gui gui;

    private final InteractivePoint previous;
    private final InteractivePoint next;
    private final InteractivePoint pages;

    private final ItemStack right = Item.builder(Material.LIME_STAINED_GLASS_PANE).name("§a->").delegate();
    private final ItemStack left = Item.builder(Material.RED_STAINED_GLASS_PANE).name("§c<-").delegate();

    public PagedBarRoute(int rows, Gui gui) {
        super(rows);
        this.gui = gui;

        this.pages = InteractivePoint.builder()
                .item(
                        Item.builder(new ItemStack(Material.BOOK))
                                .name(" ")
                                .amount(gui.currentPage() + 1)
                                .delegate()
                )
                .event(event -> {})
                .parent(gui)
                .build();

        this.previous = InteractivePoint.builder()
                .parent(gui)
                .item(null)
                .event(inventoryClickEvent -> {
                    if (gui.currentPage() == 0) return;
                    gui.changeOffset(-1);
                })
                .build();

        this.next = InteractivePoint.builder()
                .parent(gui)
                .item(null)
                .event(inventoryClickEvent -> {
                    if (gui.currentPage() == pages() - 1) return;
                    gui.changeOffset(1);
                })
                .build();

        addBar(4, previous);
        addBar(5, pages);
        addBar(6, next);
    }

    @Override
    public void build() {
        pages.setItem(
                Item.builder(pages.getItem())
                        .amount(gui.currentPage() + 1)
                .delegate()
        );

        previous.setItem(
                gui.currentPage() == 0 ? null : left
        );

        next.setItem(
                gui.currentPage() >= pages() - 1 ? null : right
        );
    }

    @Override
    public InteractivePoint get(int absolute, int relative, int page) {
        return super.get(absolute, relative, page);
    }
}
