package xyz.ufactions.customcrates.gui.internal.button;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.libs.ItemBuilder;

public abstract class BasicButton<T extends JavaPlugin> extends IButton<T> {

    private final ItemBuilder builder;
    private final int slot;

    public BasicButton(T plugin, ItemBuilder builder, int slot) {
        super(plugin);

        this.builder = builder;
        this.slot = slot;
    }

    public ItemBuilder getBuilder() {
        return builder;
    }

    @Override
    public ItemStack getItem() {
        return builder.build();
    }

    @Override
    public int getSlot() {
        return slot;
    }
}