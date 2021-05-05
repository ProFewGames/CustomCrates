package xyz.ufactions.customcrates.gui.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.gui.internal.button.BasicButton;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.InputRequest;
import xyz.ufactions.customcrates.libs.ItemBuilder;

public class DeleteButton extends BasicButton<CustomCrates> {

    private final Crate crate;

    public DeleteButton(CustomCrates plugin, Crate crate) {
        super(plugin,
                new ItemBuilder(Material.BARRIER).name(ChatColor.DARK_RED + "" + ChatColor.BOLD + "Delete Crate")
                        .lore("* Click to delete this crate *"),
                15);
        this.crate = crate;
    }

    @Override
    public void onClick(Player player, ClickType type) {
        InputRequest.confirmationInput(confirmed -> {
            if (!confirmed) {
                opener.openInventory(player);
                player.sendMessage(F.error("Crate deletion cancelled."));
            } else {
                boolean deleted = Plugin.getCratesManager().deleteCrate(crate);
                if (deleted) {
                    player.sendMessage(F.format("Successfully deleted crate: " + F.element(crate.getSettings().getIdentifier()) + "."));
                } else {
                    player.sendMessage(F.error("Failed to delete crate: " + F.element(crate.getSettings().getIdentifier()) + ChatColor.RED + "."));
                }
            }
        }, crate.getSettings().getKey().build(), Plugin, player);
    }
}