package xyz.ufactions.customcrates.gui;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.ButtonBuilder;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.button.Button;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.ResponseLib;

public class CustomCratesGUI extends GUI {

    public CustomCratesGUI(CustomCrates plugin) {
        super(plugin, F.color("&3&lCustomCrates"), 27, GUIFiller.NONE);

        setPaneColor(ChatColor.AQUA);

        Button CREATE_CRATE = ButtonBuilder.instance(plugin)
                .slot(13)
                .item(new ItemBuilder(Material.CHEST)
                        .name(F.color("&b&lCreate crate"))
                        .lore("* Click to create a new crate *"))
                .onClick((player, type) -> {
                    player.closeInventory();
                    ResponseLib.getString(plugin, player, 60).whenComplete((name, throwable) -> {
                        player.sendMessage("You want to create crate " + name);
                    });
                })
                .build();

        addButton(CREATE_CRATE);
    }
}