package xyz.ufactions.customcrates.gui.internal.button;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;

public abstract class InverseButton extends Button {

    protected boolean inverse = false;

    public InverseButton(CustomCrates plugin) {
        super(plugin);
    }

    public InverseButton(CustomCrates plugin, int slot) {
        super(plugin, null, slot);
    }

    public InverseButton(CustomCrates plugin, long refreshTime, int slot) {
        super(plugin, null, refreshTime, slot);
    }

    public final boolean isInversed() {
        return inverse;
    }

    public final void setInversed(boolean inverse) {
        this.inverse = inverse;
    }

    public final void reverse() {
        setInversed(!isInversed());
    }

    public boolean canInverse(Player player) {
        return true;
    }

    public abstract ItemStack getInverse(boolean inversed);

    @Override
    public final ItemStack getItem() {
        return getInverse(inverse);
    }
}