package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.libs.F;

public class ListCommand extends SubCommand {

    public ListCommand(CustomCrates plugin) {
        super(plugin, "List all available crates.", new String[]{
                "list"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!permissionCheck(sender, "customcrates.command.list", true)) return true;
        if (plugin.getCratesManager().getCrates().isEmpty()) {
            sender.sendMessage(F.error(plugin.getLanguage().noAvailableCrates()));
        } else {
            sender.sendMessage(F.format(plugin.getLanguage().availableCratesHeader()));
            for (ICrate crate : plugin.getCratesManager().getCrates()) {
                sender.sendMessage(F.list(crate.getIdentifier()));
            }
        }
        return true;
    }
}