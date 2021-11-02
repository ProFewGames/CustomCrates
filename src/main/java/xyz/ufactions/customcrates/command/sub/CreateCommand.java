package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.libs.F;

import java.util.Collections;
import java.util.List;

public class CreateCommand extends SubCommand {

    public CreateCommand(CustomCrates plugin) {
        super(plugin, "Create a new crate.", "customcrates.command.create", "create <crate>", new String[]{
                "create"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            String name = args[0];
            if (plugin.getCratesFile().crateExist(name)) {
                sender.sendMessage(F.error("Crate already exists."));
                return true;
            }
            plugin.getCratesFile().createCrate(name);
            sender.sendMessage(F.format("Created crate " + name + "."));
            plugin.reload();
            sender.sendMessage(F.format("Plugin reloaded."));
            return true;
        }
        return false;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}