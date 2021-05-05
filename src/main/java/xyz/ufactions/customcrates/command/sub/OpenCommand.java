package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.libs.F;

import java.util.Collections;
import java.util.List;

public class OpenCommand extends SubCommand {

    public OpenCommand(CustomCrates plugin) {
        super(plugin, "Open a crate without a key.", "customcrates.command.open", "open <crate>", new String[]{
                "open"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return true;
        if (args.length == 1) {
            Player player = (Player) sender;
            Crate crate = getCrate(sender, args[0]);
            if (crate == null) return true;
            crate.getSettings().getSpinType().getSpin(plugin).spin(player, crate);
            player.sendMessage(F.format(plugin.getLanguage().opening(crate.getSettings().getIdentifier())));
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