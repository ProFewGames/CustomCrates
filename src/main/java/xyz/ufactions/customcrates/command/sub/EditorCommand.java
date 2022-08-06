package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.gui.EditorGUI;
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
        new EditorGUI(this.plugin, player).open();
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}