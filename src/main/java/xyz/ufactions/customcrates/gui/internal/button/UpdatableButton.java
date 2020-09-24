package xyz.ufactions.customcrates.gui.internal.button;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class UpdatableButton<T extends JavaPlugin> extends IButton<T> {

    private final int position;

    public UpdatableButton(T plugin, int position) {
        super(plugin);

        this.position = position;
    }

    @Override
    public int getSlot() {
        return position;
    }
}