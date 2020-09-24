package xyz.ufactions.customcrates.gui.internal.button;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.libs.ItemBuilder;

public abstract class SelfSortingButton<T extends JavaPlugin> extends IButton<T> {

    private final ItemBuilder builder;

    public SelfSortingButton(T plugin, ItemBuilder builder) {
        super(plugin);

        this.builder = builder;
    }

    @Override
    public ItemStack getItem() {
        return builder.build();
    }

    @Override
    public final int getSlot() { // Not needed but its abstract
        return -1;
    }
}