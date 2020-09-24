package xyz.ufactions.customcrates.gui.internal.button;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.libs.UtilMath;

public abstract class IButton<T extends JavaPlugin> {

    protected GUI<?> opener; // The GUI that this button is in
    protected final T Plugin; // The plugin linked to this button

    public IButton(T plugin) {
        Plugin = plugin;
    }

    public final void setOpener(GUI<?> opener) {
        this.opener = opener;
    }

    public abstract ItemStack getItem();

    public abstract int getSlot();

    public abstract void onClick(Player player, ClickType type);

    protected final ChatColor randomColor() {
        return ChatColor.values()[UtilMath.random.nextInt(ChatColor.values().length)];
    }
}