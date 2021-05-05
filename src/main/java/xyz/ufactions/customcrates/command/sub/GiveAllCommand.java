package xyz.ufactions.customcrates.command.sub;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilMath;

import java.util.List;

public class GiveAllCommand extends SubCommand {

    public GiveAllCommand(CustomCrates plugin) {
        super(plugin, "Give all players on the server X amount of crate keys.", "customcrates.command.giveall", "giveall <crate> <amount>",
                new String[]{
                        "giveall"
                });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (args.length == 2) {
            Crate crate = getCrate(sender, args[0]);
            if (crate == null) return true;
            if (!UtilMath.isInteger(args[1])) {
                sender.sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                return true;
            }
            final int amount = Integer.parseInt(args[1]);
            Bukkit.getOnlinePlayers().forEach(player -> {
                crate.giveKey(player, amount);
                player.sendMessage(F.format(plugin.getLanguage().keyReceived(amount, crate.getSettings().getIdentifier())));
            });
            sender.sendMessage(F.format(plugin.getLanguage().keyGivenAll(amount, crate.getSettings().getIdentifier(),
                    Bukkit.getOnlinePlayers().size() * amount)));
            return true;
        }
        return false;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        return null; // TODO
    }
}