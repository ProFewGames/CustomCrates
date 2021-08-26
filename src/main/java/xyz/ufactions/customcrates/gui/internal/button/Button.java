package xyz.ufactions.customcrates.gui.internal.button;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.libs.ItemBuilder;

public abstract class Button {

    private final int slot;

    private final long refreshTime;

    private long lastUpdated = System.currentTimeMillis();

    protected final CustomCrates plugin;
    protected final ItemStack item;

    protected GUI gui;

    public Button(CustomCrates plugin) {
        this(plugin, null);
    }

    public Button(CustomCrates plugin, ItemBuilder builder) {
        this(plugin, builder, -1);
    }

    public Button(CustomCrates plugin, ItemBuilder builder, int slot) {
        this(plugin, builder, -1, slot);
    }

    public Button(CustomCrates plugin, ItemBuilder builder, long refreshTime, int slot) {
        this.plugin = plugin;
        this.item = builder.build();
        this.refreshTime = refreshTime;
        this.slot = slot;
    }

    public abstract void onClick(Player player, ClickType clickType);

    public final void setGUI(GUI gui) {
        this.gui = gui;
    }

    public final boolean isSelfSorting() {
        return slot <= -1;
    }

    public final long getRefreshTime() {
        return refreshTime;
    }

    public final void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public final long getLastUpdated() {
        return lastUpdated;
    }

    public final int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        Validate.notNull(item, "ItemBuilder is null. Override getItem within button class or set a builder" +
                " when constructing this button class.");

        return item;
    }
}