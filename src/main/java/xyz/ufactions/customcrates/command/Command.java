package xyz.ufactions.customcrates.command;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.F;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Command {

    protected final CustomCrates plugin;

    public Command(CustomCrates plugin) {
        this.plugin = plugin;
    }

    protected final List<String> getMatches(String start, List<String> list) {
        List<String> matches = new ArrayList<>();
        for (String string : list)
            if (string.toLowerCase().startsWith(start.toLowerCase()))
                matches.add(string);
        return matches;
    }

    protected final <Any> List<String> getMatches(String start, List<Any> list, Function<Any, String> function) {
        return getMatches(start, list.stream().map(function).collect(Collectors.toList()));
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
                sender.sendMessage(F.color(plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_CRATE_INPUT)));
        return crate;
    }

    protected final boolean isPlayer(CommandSender sender) {
        return isPlayer(sender, true);
    }

    protected final boolean isPlayer(CommandSender sender, boolean notify) {
        if (!(sender instanceof Player)) {
            if (notify)
                sender.sendMessage(F.color(plugin.getLanguage().getString(LanguageFile.LanguagePath.NO_PLAYER)));
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
            if (inform)
                sender.sendMessage(F.color(plugin.getLanguage().getString(LanguageFile.LanguagePath.NO_PERMISSION)));
            return false;
        } else {
            return true;
        }
    }
}