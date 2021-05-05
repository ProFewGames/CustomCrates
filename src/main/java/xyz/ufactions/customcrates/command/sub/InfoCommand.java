package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.libs.F;

public class InfoCommand extends SubCommand {

    public InfoCommand(CustomCrates plugin) {
        super(plugin, "Retrieve information about this plugin.", new String[]{
                "info"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!permissionCheck(sender, "customcrates.command.info", true)) return true;
        PluginDescriptionFile pdf = plugin.getDescription();
        sender.sendMessage(F.format("Author: " + F.element(F.concatenate(", ", pdf.getAuthors()))));
        sender.sendMessage(F.format("Version: " + F.element(pdf.getVersion())));
        sender.sendMessage(F.format("Description: " + F.element(pdf.getDescription())));
        return true;
    }
}