package xyz.ufactions.customcrates.command.sub;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.F;

import java.util.Collections;
import java.util.List;

public class PreviewCommand extends SubCommand {

    public PreviewCommand(CustomCrates plugin) {
        super(plugin, "Preview a crate without physically clicking one.", "customcrates.command.preview", "preview <crate>", new String[]{
                "preview"
        });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return true;
        if (args.length == 1) {
            Player player = (Player) sender;
            Crate crate = getCrate(player, args[0]);
            if (crate == null) return true;
            player.openInventory(crate.getPreviewInventory());
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.PREVIEWING, crate.getSettings().getIdentifier())));
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