package xyz.ufactions.customcrates.gui;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.libs.ColorLib;

import java.util.function.Consumer;

public class DeleteConfirmationGUI extends GUI {

    public enum DeletionResponse {
        DECLINED,
        ACCEPTED
    }

    public DeleteConfirmationGUI(Consumer<DeletionResponse> consumer, CustomCrates plugin, Player player) {
        super(plugin, player, 27);

        setTitle("&3&lConfirm Deletion");

        setItem(11, ColorLib.wool(ChatColor.RED)
                .name("&c&lDecline")
                .lore("&7&o* Click to decline *")
                .build(event -> consumer.accept(DeletionResponse.DECLINED)));

        setItem(15, ColorLib.wool(ChatColor.GREEN)
                .name("&a&lConfirm")
                .lore("&7&o* Click to confirm *")
                .build(event -> consumer.accept(DeletionResponse.ACCEPTED)));
    }
}