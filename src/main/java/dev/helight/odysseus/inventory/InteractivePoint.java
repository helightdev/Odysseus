package dev.helight.odysseus.inventory;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

@Setter
@Getter
@Builder
public class InteractivePoint {

    private ItemStack item;

    private Gui parent;

    @Builder.Default
    private Consumer<InventoryClickEvent> event = (ignored) -> {};

    protected void callEvent(InventoryClickEvent event) {
        parent.notifyAction();
        this.event.accept(event);
    }

    public void build() { }

    public void scheduleRebuild() {
        parent.render();
    }

    public static InteractivePoint nextPage(ItemStack itemStack) {
        InteractivePoint itemNode = InteractivePoint.builder().item(itemStack).build();
        itemNode.setEvent(ignored -> itemNode.parent.changeOffset(1));
        return itemNode;
    }

    public static InteractivePoint previousPage(ItemStack itemStack) {
        InteractivePoint itemNode = InteractivePoint.builder().item(itemStack).build();
        itemNode.setEvent(ignored -> itemNode.parent.changeOffset(-1));
        return itemNode;
    }

    public static InteractivePoint switchView(ItemStack itemStack, String view) {
        InteractivePoint itemNode = InteractivePoint.builder().item(itemStack).build();
        itemNode.setEvent(ignored -> itemNode.parent.changeView(view));
        return itemNode;
    }

}
