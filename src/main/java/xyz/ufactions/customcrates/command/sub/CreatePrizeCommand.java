package xyz.ufactions.customcrates.command.sub;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.command.SubCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.ItemBuilder;

import java.util.Collections;
import java.util.List;

public class CreatePrizeCommand extends SubCommand {

    public CreatePrizeCommand(CustomCrates plugin) {
        super(plugin, "Create a new prize with the item in your hand as a display.", "customcrates.command.prize",
                "createprize <crate> <chance> <prize name> <reward command...>", new String[]{
                        "createprize",
                        "cp",
                        "addprize",
                        "ap"
                });
    }

    @Override
    protected boolean execute(CommandSender sender, String label, String[] args) {
        if (!isPlayer(sender)) return true;
        if (args.length < 3) return false;
        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInHand();
        if (item.getType() == Material.AIR) {
            player.sendMessage(F.error("You must be holding the item you want to display as a prize."));
            return true;
        }
        Crate crate = plugin.getCratesManager().getCrate(args[0]);
        if (crate == null) {
            player.sendMessage(F.error("Invalid Crate."));
            return false;
        }
        double chance;
        try {
            chance = Double.parseDouble(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage(F.error("Invalid chance. Chance must follow the format ###.##"));
            return false;
        }
        String name = args[2];
        name = name.replaceAll(" ", "_");
        String command = F.concatenate(" ", 3, args);
        plugin.getCratesFile().createPrize(crate, new Prize(new ItemBuilder(item), chance, name, Collections.singletonList(command)));
        return true;
    }

    @Override
    protected List<String> tabComplete(CommandSender sender, String label, String[] args) {
        if (args.length == 1) {
            return getMatches(args[0], plugin.getCratesManager().getCrates(), crate -> crate.getSettings().getIdentifier());
        }
        return Collections.emptyList();
    }
}