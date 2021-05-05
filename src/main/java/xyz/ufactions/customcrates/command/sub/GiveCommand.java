package xyz.ufactions.customcrates.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilMath;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GiveCommand extends SubCommand {

    public GiveCommand(CustomCrates plugin) {
        super(plugin, "Give PLAYER X amount of CRATE keys.", "customcrates.command.give", "give <player> <crate> <amount>",
                new String[]{"give"});
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 3) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(F.error(plugin.getLanguage().playerNotFound()));
                return true;
            }
            Crate crate = getCrate(sender, args[1]);
            if (crate == null) return true;
            if (!UtilMath.isInteger(args[2])) {
                sender.sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                return true;
            }
            int amount = Integer.parseInt(args[2]);
            crate.giveKey(target, amount);
            target.sendMessage(F.format(plugin.getLanguage().keyReceived(amount, crate.getSettings().getIdentifier())));
            if (target != sender) {
                sender.sendMessage(F.format(plugin.getLanguage().keyGiven(target.getName(), amount, crate.getSettings().getIdentifier())));
            }
            return true;
        }
        return false;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return getMatches(args[0], new ArrayList<>(Bukkit.getOnlinePlayers()), HumanEntity::getName);
        }
        if (args.length == 2) {
            return getMatches(args[0], plugin.getCratesManager().getCrates(), crate -> crate.getSettings().getIdentifier());
        }
        if (args.length == 3) {
            return getMatches(args[0], Arrays.asList("1", "2", "3"));
        }
        return Collections.emptyList();
    }
}
