package xyz.ufactions.customcrates.command.sub;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.F;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class SetCommand extends SubCommand {

    public SetCommand(CustomCrates plugin) {
        super(plugin, "Sets a crate at the block you are looking at.", "customcrates.command.set", "set <crate>", new String[]{
                "set"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return true;
        if (args.length == 1) {
            Player player = (Player) sender;
            Block block = getTargetBlock(player, 10);
            if (block == null) {
                player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.NO_TARGET_BLOCK)));
                return true;
            }
            Crate crate = getCrate(player, args[0]);
            if (crate == null) return true;
            if (block.getType() != crate.getSettings().getBlock()) {
                player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.INCORRECT_TARGET_BLOCK, F.capitalizeFirstLetter(
                        crate.getSettings().getBlock().toString()
                ))));
                return true;
            }
            Location location = block.getLocation();
            if (plugin.getLocationsFile().isCrate(location)) {
                player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.LOCATION_ALREADY_SET)));
                return true;
            }
            try {
                plugin.getLocationsFile().saveLocation(crate, location);
                player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.TARGET_BLOCK_SET)));
            } catch (IOException e) {
                player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.ERROR_FILE_SAVING)));
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return getMatches(args[0], plugin.getCratesManager().getCrates(), crate -> crate.getSettings().getIdentifier());
        }
        return Collections.emptyList();
    }
}