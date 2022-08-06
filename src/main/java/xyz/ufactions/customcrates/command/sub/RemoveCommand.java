package xyz.ufactions.customcrates.command.sub;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.F;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class RemoveCommand extends SubCommand {

    public RemoveCommand(CustomCrates plugin) {
        super(plugin, "Removes the crate you are looking at.", "customcrates.command.remove", new String[]{
                "remove"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return true;

        Player player = (Player) sender;
        Block block = getTargetBlock(player, 10);
        if (block == null) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.NO_TARGET_BLOCK)));
            return true;
        }
        Location location = block.getLocation();
        if (!plugin.getLocationsFile().isCrate(location)) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.TARGET_NOT_CRATE)));
            return true;
        }
        try {
            plugin.getLocationsFile().deleteLocation(location);
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.TARGET_BROKEN)));
        } catch (IOException e) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.ERROR_FILE_SAVING)));
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return Collections.emptyList();
    }
}