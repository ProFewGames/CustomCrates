package xyz.ufactions.customcrates.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.sub.*;
import xyz.ufactions.customcrates.libs.F;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CratesCommand extends xyz.ufactions.customcrates.command.Command implements CommandExecutor, TabExecutor {

    private final List<SubCommand> commands = new ArrayList<>();

    public CratesCommand(CustomCrates plugin) {
        super(plugin);

        commands.add(new InfoCommand(plugin));
        commands.add(new ListCommand(plugin));
        commands.add(new ReloadCommand(plugin));
        commands.add(new RemoveCommand(plugin));
        commands.add(new SetCommand(plugin));
        commands.add(new PreviewCommand(plugin));
        commands.add(new OpenCommand(plugin));
        commands.add(new GiveAllCommand(plugin));
        commands.add(new GivePouchCommand(plugin));
        commands.add(new GiveCommand(plugin));
        commands.add(new CreateCommand(plugin));
        commands.add(new CreatePrizeCommand(plugin));
        if (new File("editor.dat").exists())
            commands.add(new EditorCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length >= 1) {
            for (SubCommand command : commands) {
                for (String alias : command.aliases()) {
                    if (args[0].equalsIgnoreCase(alias)) {
                        if (!checkPermission(sender, command.permission())) return true;
                        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                        if (!command.execute(sender, label, newArgs)) {
                            sender.sendMessage(F.error("Incorrect Usage: " + command.usage(label)));
                        }
                        return true;
                    }
                }
            }
        }
        if (!checkPermission(sender, "customcrates.command")) return true;
        sender.sendMessage(F.format("Commands:"));
        for (SubCommand command : commands) {
            if (!checkPermission(sender, command.permission(), false)) continue;
            sender.sendMessage(F.help(command.usage(label), command.description()));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> matches = new ArrayList<>();
            if ("help".startsWith(args[0])) matches.add("help");
            for (SubCommand command : commands) {
                for (String string : command.aliases()) {
                    if (string.startsWith(args[0])) {
                        matches.add(string);
                    }
                }
            }
            return matches;
        }
        if (args.length > 1) {
            for (SubCommand command : commands) {
                for (String alias : command.aliases()) {
                    if (args[0].equalsIgnoreCase(alias)) {
                        if (checkPermission(sender, command.permission(), false)) {
                            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
                            return command.tabComplete(sender, label, newArgs);
                        }
                    }
                }
            }
        }
        return null;
    }
}