package xyz.ufactions.customcrates.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.button.BasicButton;
import xyz.ufactions.customcrates.gui.internal.button.UpdatableButton;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.UtilMath;

public class PrizeEditorGUI extends GUI<CustomCrates> {

    public PrizeEditorGUI(CustomCrates plugin, ICrate crate, Prize prize) {
        super(plugin, prize.getDisplayItem().getItemMeta().getDisplayName(), 27, GUIFiller.RAINBOW);

        // Edit name
        addButton(new BasicButton<CustomCrates>(plugin,
                new ItemBuilder(Material.GLOWSTONE).name(ChatColor.RED + "" + ChatColor.BOLD + "GLOW")
                        .lore("* Click to toggle glowing for this item *").glow(prize.isGlowing()),
                11) {

            @Override
            public void onClick(Player player, ClickType type) {
                boolean var = !prize.isGlowing();
                plugin.getCratesManager().setGlowingPrize(crate, prize, var);
                player.sendMessage(F.format("You've toggled this prize's glowing status to: " + F.oo(var)));
            }
        });

        // Edit Chance
        addButton(new UpdatableButton<CustomCrates>(plugin, 13) {

            private final Material[] display = {Material.DIAMOND, Material.GOLD_INGOT, Material.COAL, Material.IRON_INGOT};

            @Override
            public void onClick(Player player, ClickType type) {
                // TODO
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(display[UtilMath.r(display.length)]).name(ChatColor.RED + "" + ChatColor.BOLD + "Change Chance")
                        .lore("* Click to change this prize's chance *").build();
            }
        });

        // Edit Commands
        addButton(new BasicButton<CustomCrates>(plugin,
                new ItemBuilder(Material.BOOK).name(ChatColor.RED + "" + ChatColor.BOLD + "Edit Commands")
                        .lore(ChatColor.ITALIC + "Note- Use the placeholder %player%", "* Click to change the commands linked to this prize *"),
                15) {

            @Override
            public void onClick(Player player, ClickType type) {
                // TODO
            }
        });
    }
}