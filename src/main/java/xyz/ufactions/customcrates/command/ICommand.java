package xyz.ufactions.customcrates.command;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.libs.F;

public interface ICommand {

    default ICrate getCrate(CommandSender sender, String name) {
        ICrate crate = getPlugin().getCratesManager().getCrate(name);
        if (crate == null)
            if (sender != null)
                sender.sendMessage(F.error(getPlugin().getLanguage().invalidCrateInput()));
        return crate;
    }

    default Block getTargetBlock(LivingEntity entity, int range) {
        BlockIterator iterator = new BlockIterator(entity, range);
        while (iterator.hasNext()) {
            Block next = iterator.next();
            if (next.getType() != Material.AIR) {
                return next;
            }
        }
        return null;
    }

    default boolean isPlayer(CommandSender sender) {
        return isPlayer(sender, true);
    }

    default boolean isPlayer(CommandSender sender, boolean notify) {
        if (!(sender instanceof Player)) {
            if (notify) {
                sender.sendMessage(F.color(getPlugin().getLanguage().noPlayer()));
            }
            return false;
        }
        return true;
    }

    default boolean permissionCheck(CommandSender sender, String permission, boolean inform) {
        if (!sender.hasPermission(permission)) {
            if (inform) {
                sender.sendMessage(F.color(getPlugin().getLanguage().noPermission()));
            }
            return false;
        } else {
            return true;
        }
    }

    CustomCrates getPlugin();
}