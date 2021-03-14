package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.libs.F;

public class OpenCommand extends SubCommand {

    public OpenCommand(CustomCrates plugin) {
        super(plugin, "Open a crate without a key.", "open <crate>", new String[]{
                "open"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!permissionCheck(sender, "customcrates.command.open", true)) return true;
        if (!isPlayer(sender)) return true;
        if (args.length == 1) {
            Player player = (Player) sender;
            ICrate crate = getCrate(sender, args[0]);
            if (crate == null) return true;
            crate.getSpinType().getSpin(plugin).spin(player, crate);
            player.sendMessage(F.format(plugin.getLanguage().opening(crate.getIdentifier())));
            return true;
        }
        return false;
    }
}