package xyz.ufactions.customcrates.command;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.libs.F;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class Command {

    protected final CustomCrates plugin;

    public Command(CustomCrates plugin) {
        this.plugin = plugin;
    }

    protected final List<String> getMatches(String start, List<String> list) {
        return getMatches(start, list, string -> string);
    }

    protected final <Any> List<String> getMatches(String start, List<Any> list, Function<Any, String> function) { // XXX : Relocate?
        List<String> toReturn = new ArrayList<>();
        for (Any any : list) {
            String string = function.apply(any);
            if (string.equalsIgnoreCase(start)) {
                toReturn.add(string);
                break;
            } else {
                toReturn.add(string);
            }
        }
        return toReturn;
    }

    protected final Block getTargetBlock(LivingEntity entity, int range) {
        BlockIterator iterator = new BlockIterator(entity, range);
        while (iterator.hasNext()) {
            Block next = iterator.next();
            if (next.getType() != Material.AIR) {
                return next;
            }
        }
        return null;
    }

    protected final Crate getCrate(CommandSender sender, String name) {
        Crate crate = plugin.getCratesManager().getCrate(name);
        if (crate == null)
            if (sender != null)
                sender.sendMessage(F.error(plugin.getLanguage().invalidCrateInput()));
        return crate;
    }

    protected final boolean isPlayer(CommandSender sender) {
        return isPlayer(sender, true);
    }

    protected final boolean isPlayer(CommandSender sender, boolean notify) {
        if (!(sender instanceof Player)) {
            if (notify) {
                sender.sendMessage(F.color(plugin.getLanguage().noPlayer()));
            }
            return false;
        } else {
            return true;
        }
    }

    protected final boolean checkPermission(CommandSender sender, String permission) {
        return checkPermission(sender, permission, true);
    }

    protected final boolean checkPermission(CommandSender sender, String permission, boolean inform) {
        if (!sender.hasPermission(permission)) {
            if (inform) {
                sender.sendMessage(F.color(plugin.getLanguage().noPermission()));
            }
            return false;
        } else {
            return true;
        }
    }
}