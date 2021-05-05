package xyz.ufactions.customcrates.crates;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.libs.UtilMath;

public class Crate {

    private final CrateSettings settings;
    private final Inventory previewInventory;

    public Crate(CrateSettings settings) {
        this.settings = settings;

        this.previewInventory = Bukkit.createInventory(null, UtilMath.round(settings.getPrizes().size()),
                settings.getDisplay());

        for (int i = 0; i < settings.getPrizes().size(); i++) {
            previewInventory.setItem(i, settings.getPrizes().get(i).getDisplayItem());
        }
    }

    public CrateSettings getSettings() {
        return settings;
    }

    public Inventory getPreviewInventory() {
        return previewInventory;
    }

    public boolean validPouch() {
        return getSettings().getPouch().build().getType() != Material.AIR;
    }

    public void giveKey(Player player, int amount) {
        ItemStack key = settings.getKey().build();
        key.setAmount(amount);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), key);
        } else {
            player.getInventory().setItem(player.getInventory().firstEmpty(), key);
        }
    }

    public void givePouch(Player player, int amount) {
        ItemStack pouch = settings.getPouch().build();
        pouch.setAmount(amount);
        if (player.getInventory().firstEmpty() == -1) {
            player.getWorld().dropItemNaturally(player.getLocation(), pouch);
        } else {
            player.getInventory().setItem(player.getInventory().firstEmpty(), pouch);
        }
    }
}