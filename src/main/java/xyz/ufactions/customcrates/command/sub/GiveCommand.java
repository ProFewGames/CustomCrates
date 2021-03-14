package xyz.ufactions.customcrates.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilMath;

public class GiveCommand extends SubCommand {

    public GiveCommand(CustomCrates plugin) {
        super(plugin, "Give PLAYER X amount of CRATE keys.", "give <player> <crate> <amount>",
                new String[]{"give"});
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!permissionCheck(sender, "customcrates.command.give", true)) return true;
        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(F.error(plugin.getLanguage().playerNotFound()));
                return true;
            }
            ICrate crate = getCrate(sender, args[1]);
            if (crate == null) return true;
            if (!UtilMath.isInteger(args[2])) {
                sender.sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                return true;
            }
            int amount = Integer.parseInt(args[2]);
            crate.giveKey(target, amount);
            target.sendMessage(F.format(plugin.getLanguage().keyReceived(amount, crate.getIdentifier())));
            if (target != sender) {
                sender.sendMessage(F.format(plugin.getLanguage().keyGiven(target.getName(), amount, crate.getIdentifier())));
            }
            return true;
        }
        return false;
    }
}
