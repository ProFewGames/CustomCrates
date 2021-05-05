package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.libs.F;

import java.util.List;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(CustomCrates plugin) {
        super(plugin, "Reload the plugin.", "customcrates.command.reload", new String[]{
                "reload"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(F.format(plugin.getLanguage().reloading()));
        plugin.reload();
        sender.sendMessage(F.format(plugin.getLanguage().reloaded()));
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return null; // TODO
    }
}