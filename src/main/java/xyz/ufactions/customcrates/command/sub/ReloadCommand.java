package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.F;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends SubCommand {

    public ReloadCommand(CustomCrates plugin) {
        super(plugin, "Reload the plugin.", "customcrates.command.reload", new String[]{
                "reload"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        sender.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.RELOADING)));
        plugin.reload();
        sender.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.RELOADED)));
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}