package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.libs.F;

public class PreviewCommand extends SubCommand {

    public PreviewCommand(CustomCrates plugin) {
        super(plugin, "Preview a crate without physically clicking one.", "preview <crate>", new String[]{
                "preview"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!permissionCheck(sender, "customcrates.command.preview", true)) return true;
        if (!isPlayer(sender)) return true;
        if (args.length == 1) {
            Player player = (Player) sender;
            ICrate crate = getCrate(player, args[0]);
            if (crate == null) return true;
            player.openInventory(crate.getPreviewInventory());
            player.sendMessage(F.format(plugin.getLanguage().previewing(crate.getIdentifier())));
            return true;
        }
        return false;
    }
}