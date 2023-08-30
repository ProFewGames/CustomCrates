package xyz.ufactions.customcrates.gui.item;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Consumer;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.ColorLib;

import java.util.ArrayList;
import java.util.List;

public class PlayerGUI extends GUI {

    public PlayerGUI(CustomCrates plugin, Player player, Consumer<ItemStack> consumer) {
        super(plugin, player);

        List<ItemStack> contents = new ArrayList<>();
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null) continue;
            contents.add(content);
        }

        setTitle("&3&lSelect Item to Copy");

        if (contents.isEmpty()) {
            setInventorySize(9);
            for (int index = 0; index < getInventory().getSize(); index++) {
                setItem(index, ColorLib.pane(ChatColor.RED)
                        .name("&4&lNo Items")
                        .lore("",
                                "&fYou do not have any items in your inventory")
                        .build(() -> {
                        }));
            }
            return;
        }

        setInventorySize(contents.size());

        for (int index = 0; index < contents.size(); index++) {
            ItemStack item = contents.get(index).clone();
            setItem(index, ItemStackBuilder.from(item).build(() -> consumer.accept(item)));
        }
    }
}
