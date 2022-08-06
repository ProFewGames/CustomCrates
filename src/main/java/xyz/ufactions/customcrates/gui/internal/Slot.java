package xyz.ufactions.customcrates.gui.internal;

import lombok.Getter;
import org.apache.commons.lang.Validate;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.item.Item;

import java.util.function.Consumer;

public final class Slot {

    private final GUI gui;
    @Getter
    private final int slot;
    private Consumer<InventoryClickEvent> consumers;

    public Slot(GUI gui, int slot) {
        this.gui = gui;
        this.slot = slot;
    }

    public void clear() {
        setItem(null);
        bind(null);
    }

    public void apply(Item<InventoryClickEvent> item) {
        Validate.notNull(item);

        setItem(item.getItem());
        bind(item.getConsumer());
    }

    public void handle(InventoryClickEvent event) {
        if (this.consumers == null) return;
        this.consumers.accept(event);
    }

    public boolean hasHandler() {
        return this.consumers != null;
    }

    public Slot setItem(ItemStack item) {
        this.gui.getInventory().setItem(slot, item);
        return this;
    }

    public void bind(Consumer<InventoryClickEvent> consumer) {
        this.consumers = consumer;
    }
}