package xyz.ufactions.customcrates.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.sub.*;
import xyz.ufactions.customcrates.libs.F;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CratesCommand implements CommandExecutor, TabExecutor, ICommand {

    private final CustomCrates plugin;
    private final List<SubCommand> subCommands = new ArrayList<>();

    public CratesCommand(CustomCrates plugin) {
        this.plugin = plugin;

        subCommands.add(new InfoCommand(plugin));
        subCommands.add(new ListCommand(plugin));
        subCommands.add(new ReloadCommand(plugin));
        subCommands.add(new RemoveCommand(plugin));
        subCommands.add(new SetCommand(plugin));
        subCommands.add(new PreviewCommand(plugin));
        subCommands.add(new OpenCommand(plugin));
        subCommands.add(new GiveAllCommand(plugin));
        subCommands.add(new GivePouchCommand(plugin));
        subCommands.add(new GiveCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            for (SubCommand command : subCommands) {
                for (String alias : command.aliases()) {
                    if (args[0].equalsIgnoreCase(alias)) {
                        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                        if (!command.execute(sender, label, newArgs)) {
                            sender.sendMessage(F.error("Incorrect Usage: " + command.usage(label)));
                        }
                        return true;
                    }
                }
            }
        }
        if (!permissionCheck(sender, "customcrates.command", true)) return true;
        sender.sendMessage(F.format("Commands:"));
        for (SubCommand command : subCommands) {
            sender.sendMessage(F.help(command.usage(label), command.description()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }

    @Override
    public CustomCrates getPlugin() {
        return plugin;
    }
}