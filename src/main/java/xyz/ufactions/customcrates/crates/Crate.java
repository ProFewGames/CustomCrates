package xyz.ufactions.customcrates.crates;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.preview.PagedPreviewGUI;
import xyz.ufactions.customcrates.gui.preview.RegularPreviewGUI;
import xyz.ufactions.customcrates.libs.Utility;

// TODO: 8/3/2022 Rewrite key giving system
public class Crate {

    private final CustomCrates plugin;
    private final CrateSettings settings;

    public Crate(CustomCrates plugin, CrateSettings settings) {
        this.settings = settings;
        this.plugin = plugin;
    }

    /**
     * @return Settings from the configuration file.
     */
    public CrateSettings getSettings() {
        return settings;
    }

    /**
     * Open this crate's preview GUI for player.
     *
     * @param player The player
     */
    public void openPreviewInventory(Player player) {
        if (this.settings.getPrizes().size() > 54)
            new PagedPreviewGUI(plugin, this, player).open();
        else
            new RegularPreviewGUI(plugin, this, player).open();
    }

    /**
     * @return true if there is a pouch configuration otherwise false
     */
    public boolean validPouch() {
        return getSettings().getPouch().build().getType() != Material.AIR;
    }

    /**
     * Give <code>Player</code> the given amount of keys.
     *
     * @param player The receiver of keys
     * @param amount The amount of keys given
     */
    public void giveKey(Player player, int amount) {
        ItemStack key = settings.getKey().build();
        key.setAmount(amount);
        Utility.addOrDropItem(player.getInventory(), player.getLocation(), key);
    }

    public void givePouch(Player player, int amount) {
        ItemStack pouch = settings.getPouch().build();
        pouch.setAmount(amount);
        Utility.addOrDropItem(player.getInventory(), player.getLocation(), pouch);
    }
}