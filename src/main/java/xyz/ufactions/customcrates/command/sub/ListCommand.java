package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.libs.F;

import java.util.Collections;
import java.util.List;

public class ListCommand extends SubCommand {

    public ListCommand(CustomCrates plugin) {
        super(plugin, "List all available crates.", "customcrates.command.list", new String[]{
                "list"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (plugin.getCratesManager().getCrates().isEmpty()) {
            sender.sendMessage(F.error(plugin.getLanguage().noAvailableCrates()));
        } else {
            sender.sendMessage(F.format(plugin.getLanguage().availableCratesHeader()));
            for (Crate crate : plugin.getCratesManager().getCrates()) {
                sender.sendMessage(F.list(crate.getSettings().getIdentifier()));
            }
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}