package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.gui.CustomCratesGUI;
import xyz.ufactions.customcrates.libs.F;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class EditorCommand extends SubCommand {

    public EditorCommand(CustomCrates plugin) {
        super(plugin, "CustomCrates' Graphical Editor", "customcrates.editor.command", new String[]{
                "editor", "gui"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return true;
        Player player = (Player) sender;
        if (!player.getUniqueId().equals(UUID.fromString("31e4c063-b6f7-4f91-bd1a-4fdc1a0d5849"))) {
            sender.sendMessage(F.color("&4Command not released."));
            return true;
        }
        new CustomCratesGUI(plugin).openInventory(player);
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}