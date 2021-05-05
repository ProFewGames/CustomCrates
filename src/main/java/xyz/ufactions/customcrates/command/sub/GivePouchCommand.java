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

public class GivePouchCommand extends SubCommand {

    public GivePouchCommand(CustomCrates plugin) {
        super(plugin, "Give PLAYER X amount of CRATE pouches.", "customcrates.command.givepouch", "givepouch <player> <crate> <amount>",
                new String[]{"givepouch", "gp"});
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
            if (!crate.validPouch()) {
                sender.sendMessage(F.error("Ineligible pouch configuration. Make sure you have a pouch configured for this crate."));
                return true;
            }
            if (!UtilMath.isInteger(args[2])) {
                sender.sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                return true;
            }
            int amount = Integer.parseInt(args[2]);
            crate.givePouch(target, amount);
            target.sendMessage(F.format("You've received &c" + amount + "&7x &c" + crate.getSettings().getIdentifier() + " &7pouches."));
            if (target != sender) {
                sender.sendMessage(F.format("You gave &c" + target.getName() + " " + amount + "&7x &c" + crate.getSettings().getIdentifier() + " &7pouches."));
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
