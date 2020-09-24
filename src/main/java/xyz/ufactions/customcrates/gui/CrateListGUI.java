package xyz.ufactions.customcrates.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.gui.internal.button.SelfSortingButton;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.libs.ItemBuilder;

public class CrateListGUI extends GUI<CustomCrates> {

    public CrateListGUI(CustomCrates plugin) {
        super(plugin, "Select a crate...", GUIFiller.NONE);

        for (ICrate crate : plugin.getCratesManager().getCrates()) {
            addButton(new SelfSortingButton<CustomCrates>(plugin, new ItemBuilder(crate.getBlock())) {

                @Override
                public void onClick(Player player, ClickType type) {
                    // TODO?
                }
            });
        }
    }
}