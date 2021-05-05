package xyz.ufactions.customcrates.command.sub;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
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
                player.sendMessage(F.error(plugin.getLanguage().noTargetBlock()));
                return true;
            }
            Crate crate = getCrate(player, args[0]);
            if (crate == null) return true;
            if (block.getType() != crate.getSettings().getBlock()) {
                player.sendMessage(F.error(plugin.getLanguage().incorrectTargetBlock(F.capitalizeFirstLetter(
                        crate.getSettings().getBlock().toString().replaceAll("_", " ")))));
                return true;
            }
            Location location = block.getLocation();
            if (plugin.getLocationsFile().isCrate(location)) {
                player.sendMessage(F.error(plugin.getLanguage().locationAlreadySet()));
                return true;
            }
            try {
                plugin.getLocationsFile().saveLocation(crate, location);
                player.sendMessage(F.format(plugin.getLanguage().targetBlockSet()));
            } catch (IOException e) {
                player.sendMessage(F.error(plugin.getLanguage().errorFileSaving()));
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