package xyz.ufactions.customcrates.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;

import java.util.List;

public abstract class SubCommand extends xyz.ufactions.customcrates.command.Command {

    private final String description;
    private final String permission;
    private final String usage;
    private final String[] aliases;

    public SubCommand(CustomCrates plugin, String description, String permission, String[] aliases) {
        this(plugin, description, permission, aliases[0], aliases);
    }

    public SubCommand(CustomCrates plugin, String description, String permission, String usage, String[] aliases) {
        super(plugin);

        this.description = description;
        this.permission = permission;
        this.usage = usage;
        this.aliases = aliases;
    }

    protected abstract boolean execute(CommandSender sender, String label, String[] args);

    protected abstract List<String> tabComplete(CommandSender sender, String label, String[] args);

    public final String[] aliases() {
        return aliases;
    }

    public final String usage(String label) {
        return "/" + label + " " + usage;
    }

    public final String description() {
        return description;
    }

    public final String permission() {
        return permission;
    }
}