package xyz.ufactions.customcrates.gui.buttons;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.gui.PrizeListGUI;
import xyz.ufactions.customcrates.gui.internal.button.BasicButton;
import xyz.ufactions.customcrates.libs.ItemBuilder;

public class EditPrizesButton extends BasicButton<CustomCrates> {

    private final ICrate crate;

    public EditPrizesButton(CustomCrates plugin, ICrate crate) {
        super(plugin,
                new ItemBuilder(Material.DIAMOND).glow(true)
                        .name(ChatColor.RED + "" + ChatColor.BOLD + "Edit Prizes").lore("* Click to edit prizes *"),
                13);
        this.crate = crate;
    }

    @Override
    public void onClick(Player player, ClickType type) {
        PrizeListGUI gui = new PrizeListGUI(Plugin, crate);
//        gui.setReturnGUI(opener); TODO RE-ENABLE ONCE BUG FIXED
        gui.openInventory(player);
    }
}