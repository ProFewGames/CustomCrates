package xyz.ufactions.customcrates.gui.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.gui.internal.button.BasicButton;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.InputRequest;
import xyz.ufactions.customcrates.libs.ItemBuilder;

public class CrateDisplayChangeButton extends BasicButton<CustomCrates> {

    private final ICrate crate;

    public CrateDisplayChangeButton(CustomCrates plugin, ICrate crate) {
        super(plugin,
                new ItemBuilder(Material.PAPER).name(ChatColor.RED + "" + ChatColor.BOLD + "Change Display Name")
                        .lore("* Click to change this crates display name *")
                , 11);
        this.crate = crate;
    }

    @Override
    public void onClick(Player player, ClickType type) {
        player.sendMessage(F.format("Please enter a new display name for this crate."));
        InputRequest.requestInput(input -> {
            if (input == null) {
                player.sendMessage(F.error("Input cancelled."));
            } else {
                Plugin.getCratesManager().editCrateDisplayName(crate, input);
                player.sendMessage(F.format("Display Name changed for crate " + F.element(crate.getIdentifier()) + "."));
            }
            opener.updateTitle(player, "Editor for: " + input);
            opener.openInventory(player);
        }, Plugin, player);
    }
}