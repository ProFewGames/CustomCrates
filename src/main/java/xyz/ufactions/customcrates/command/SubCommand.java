package xyz.ufactions.customcrates.command;

import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;

public abstract class SubCommand implements ICommand {

    protected final CustomCrates plugin;
    private final String description;
    private final String usage;
    private final String[] aliases;

    public SubCommand(CustomCrates plugin, String description, String[] aliases) {
        this(plugin, description, aliases[0], aliases);
    }

    public SubCommand(CustomCrates plugin, String description, String usage, String[] aliases) {
        this.description = description;
        this.plugin = plugin;
        this.usage = usage;
        this.aliases = aliases;
    }

    protected abstract boolean execute(CommandSender sender, String label, String[] args);

    public final String[] aliases() {
        return aliases;
    }

    public final String usage(String label) {
        return "/" + label + " " + usage;
    }

    public final String description() {
        return description;
    }

    @Override
    public CustomCrates getPlugin() {
        return plugin;
    }
}