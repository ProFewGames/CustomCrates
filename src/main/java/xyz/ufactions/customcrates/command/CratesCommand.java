package xyz.ufactions.customcrates.command;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.UtilMath;
import xyz.ufactions.customcrates.libs.UtilTime;
import xyz.ufactions.customcrates.spin.Spin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CratesCommand implements CommandExecutor, TabExecutor {

    private final CustomCrates plugin;

    public CratesCommand(CustomCrates plugin) {
        this.plugin = plugin;
    }

    private Block getTargetBlock(LivingEntity e, int range) {
        BlockIterator bit = new BlockIterator(e, range);
        while (bit.hasNext()) {
            Block next = bit.next();
            if (next.getType() != Material.AIR) {
                return next;
            }
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            List<String> access = new ArrayList<>();
            if (permissionCheck(sender, "customcrates.command.info", false)) access.add("info");
            if (permissionCheck(sender, "customcrates.command.list", false)) access.add("list");
            if (permissionCheck(sender, "customcrates.command.open", false)) access.add("open");
            if (permissionCheck(sender, "customcrates.command.give", false)) access.add("give");
            if (permissionCheck(sender, "customcrates.command.giveall", false)) access.add("giveall");
            if (permissionCheck(sender, "customcrates.command.reload", false)) access.add("reload");
            if (permissionCheck(sender, "customcrates.command.set", false)) access.add("set");
            if (permissionCheck(sender, "customcrates.command.remove", false)) access.add("remove");
            if (permissionCheck(sender, "customcrates.command.preview", false)) access.add("preview");
            if (permissionCheck(sender, "customcrates.command.givepouch", false)) {
                access.add("givepouch");
                access.add("gp");
            }
            // TODO RE-ENABLE FOR UPDATE 4.x.x <- 4.2.2
//            access.add("editor"); // TODO Add Permissions Node
            return getMatches(args[0], access);
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("open") || args[0].equalsIgnoreCase("giveall") || args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("preview")) {
                if (permissionCheck(sender, "customcrates.command.open", false) || permissionCheck(sender, "customcrates.command.giveall", false) ||
                        permissionCheck(sender, "customcrates.command.set", false) || permissionCheck(sender, "customcrates.command.preview", false)) {
                    List<String> names = new ArrayList<>();
                    plugin.getCratesManager().getCrates().forEach(crate -> names.add(crate.getIdentifier()));
                    return getMatches(args[1], names);
                }
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("givepouch") || args[0].equalsIgnoreCase("gp")) {
                if (permissionCheck(sender, "customcrates.command.give", false) || permissionCheck(sender, "customcrates.command.givepouch", false)) {
                    List<String> names = new ArrayList<>();
                    plugin.getCratesManager().getCrates().forEach(crate -> names.add(crate.getIdentifier()));
                    return getMatches(args[2], names);
                }
            }
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("test") && sender.getName().equals("ProFewGames")) {
                ICrate crate = plugin.getCratesManager().getCrates().get(0);
                sender.sendMessage("Testing");
                final long start = System.currentTimeMillis();
                final Player player = (Player) sender;
                for (int i = 0; i <= 10; i++) {
                    sender.sendMessage("Scheduled #" + i);
                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            if (UtilTime.elapsed(start, 10000)) {
                                cancel();
                                sender.sendMessage("Done");
                            } else {
                                Spin.SpinType.NONE.getSpin(plugin).spin(player, crate);
                            }
                        }
                    }.runTaskTimer(plugin, 10, 10);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("populate") && sender.getName().equals("ProFewGames")) {
                sender.sendMessage("Populating");
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    for (ICrate crate : plugin.getCratesManager().getCrates()) {
                        for (int i = 0; i <= 50; i++) {
                            ItemBuilder builder = new ItemBuilder(Material.values()[UtilMath.random.nextInt(Material.values().length)]);
                            builder.amount(UtilMath.random.nextInt(64));
                            Prize prize = new Prize(builder, UtilMath.random.nextBoolean(), UtilMath.random.nextDouble(), "",
                                    Collections.singletonList("say Prize#" + i));
                            plugin.getCratesFile().createPrize(crate, prize);
                        }
                    }
                });
                sender.sendMessage("Done");
                return true;
            }

            // TODO RE-ENABLE FOR UPDATE 4.x.x <- 4.2.2
//            if (args[0].equalsIgnoreCase("editor")) {
//                if (isPlayer(sender, true)) {
//                     TODO Permission Check
//                    new CrateEditorGUI(plugin, plugin.getCratesManager().getCrates().get(0)).openInventory((Player) sender);
//                }
//                return true;
//            }
            if (args[0].equalsIgnoreCase("reload")) {
                if (!permissionCheck(sender, "customcrates.command.reload", true)) return true;
                sender.sendMessage(F.format(plugin.getLanguage().reloading()));
                plugin.reload();
                sender.sendMessage(F.format(plugin.getLanguage().reloaded()));
                return true;
            }
            if (args[0].equalsIgnoreCase("info")) {
                if (!permissionCheck(sender, "customcrates.command.info", true)) return true;
                PluginDescriptionFile pdf = plugin.getDescription();
                sender.sendMessage(F.format("Author: " + F.element(F.concatenate(", ", pdf.getAuthors()))));
                sender.sendMessage(F.format("Version: " + F.element(pdf.getVersion())));
                sender.sendMessage(F.format("Description: " + F.element(pdf.getDescription())));
                return true;
            }
            if (args[0].equalsIgnoreCase("list")) {
                if (!permissionCheck(sender, "customcrates.command.list", true)) return true;
                if (plugin.getCratesManager().getCrates().isEmpty()) {
                    sender.sendMessage(F.error(plugin.getLanguage().noAvailableCrates()));
                } else {
                    sender.sendMessage(F.format(plugin.getLanguage().availableCratesHeader()));
                    for (ICrate crate : plugin.getCratesManager().getCrates()) {
                        sender.sendMessage(F.list(crate.getIdentifier()));
                    }
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (!permissionCheck(sender, "customcrates.command.remove", true)) return true;
                if (isPlayer(sender, true)) {
                    Player player = (Player) sender;
                    Block block = getTargetBlock(player, 10);
                    if (block == null) {
                        player.sendMessage(F.error(plugin.getLanguage().noTargetBlock()));
                        return true;
                    }
                    Location location = block.getLocation();
                    if (!plugin.getLocationsFile().isCrate(location)) {
                        player.sendMessage(F.error(plugin.getLanguage().targetNotCrate()));
                        return true;
                    }
                    try {
                        plugin.getLocationsFile().deleteLocation(location);
                        player.sendMessage(F.format(plugin.getLanguage().targetBroken()));
                    } catch (IOException e) {
                        player.sendMessage(F.error(plugin.getLanguage().errorFileSaving()));
                        e.printStackTrace();
                    }
                    return true;
                }
                return true;
            }
        }
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("set")) {
                if (!permissionCheck(sender, "customcrates.command.set", true)) return true;
                if (isPlayer(sender, true)) {
                    Player player = (Player) sender;
                    Block block = getTargetBlock(player, 10);
                    if (block == null) {
                        player.sendMessage(F.error(plugin.getLanguage().noTargetBlock()));
                        return true;
                    }
                    ICrate crate = plugin.getCratesManager().getCrate(args[1]);
                    if (crate == null) {
                        player.sendMessage(F.error(plugin.getLanguage().invalidCrateInput()));
                        return true;
                    }
                    if (block.getType() != crate.getBlock()) {
                        player.sendMessage(F.error(plugin.getLanguage().incorrectTargetBlock(F.capitalizeFirstLetter(
                                crate.getBlock().toString().replaceAll("_", " ")))));
                        return true;
                    }
                    Location location = block.getLocation();
                    if (plugin.getLocationsFile().isCrate(location)) {
                        player.sendMessage(F.error(plugin.getLanguage().locationAlreadySet()));
                        return true;
                    }
                    try {
                        plugin.getLocationsFile().saveLocation(crate, location);
                        player.sendMessage(F.format(plugin.getLanguage().targetBlockSet()));
                    } catch (IOException e) {
                        player.sendMessage(F.error(plugin.getLanguage().errorFileSaving()));
                        e.printStackTrace();
                        return true;
                    }
                    return true;
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("preview")) {
                if (!permissionCheck(sender, "customcrates.command.preview", true)) return true;
                if (isPlayer(sender, true)) {
                    Player player = (Player) sender;
                    ICrate crate = plugin.getCratesManager().getCrate(args[1]);
                    if (crate == null) {
                        player.sendMessage(F.error(plugin.getLanguage().invalidCrateInput()));
                        return true;
                    }
                    player.openInventory(crate.getPreviewInventory());
                    player.sendMessage(F.format(plugin.getLanguage().previewing(crate.getIdentifier())));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("open")) {
                if (!permissionCheck(sender, "customcrates.command.open", true)) return true;
                if (isPlayer(sender, true)) {
                    Player player = (Player) sender;
                    ICrate crate = plugin.getCratesManager().getCrate(args[1]);
                    if (crate == null) {
                        player.sendMessage(F.error(plugin.getLanguage().invalidCrateInput()));
                        return true;
                    }
                    crate.getSpinType().getSpin(plugin).spin(player, crate);
                    player.sendMessage(F.format(plugin.getLanguage().opening(crate.getIdentifier())));
                }
                return true;
            }
        }
        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("giveall")) {
                if (!permissionCheck(sender, "customcrates.command.giveall", true)) return true;
                ICrate crate = plugin.getCratesManager().getCrate(args[1]);
                if (crate == null) {
                    sender.sendMessage(F.error(plugin.getLanguage().invalidCrateInput()));
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                    return true;
                }
                ItemStack item = crate.getKey();
                item.setAmount(amount);
                // Removed "givenkeys" variable since it will have to same outcome as just calculating it.
                for (Player pls : Bukkit.getOnlinePlayers()) {
                    pls.getInventory().addItem(item);
                    pls.sendMessage(F.format(plugin.getLanguage().keyReceived(amount, crate.getIdentifier())));
                }
                sender.sendMessage(F.format(plugin.getLanguage().keyGivenAll(amount, crate.getIdentifier(), Bukkit.getOnlinePlayers().size() * amount)));
                return true;
            }
        }
        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("givepouch") || args[0].equalsIgnoreCase("gp")) {
                if (!permissionCheck(sender, "customcrates.command.givepouch", true)) return true;
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(F.error(plugin.getLanguage().playerNotFound()));
                    return true;
                }
                ICrate crate = plugin.getCratesManager().getCrate(args[2]);
                if (crate == null) {
                    sender.sendMessage(F.error(plugin.getLanguage().invalidCrateInput()));
                    return true;
                }
                ItemStack pouch = crate.getPouch();
                if (pouch.getType() == Material.AIR) {
                    sender.sendMessage(F.error("Ineligible pouch configuration. Make sure you have a pouch configured for this crate."));
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                    return true;
                }
                pouch.setAmount(amount);
                target.getInventory().addItem(pouch);
                target.sendMessage(F.format("You've received &c" + amount + "&7x &c" + crate.getIdentifier() + " &7pouches."));
                if (target != sender) {
                    sender.sendMessage(F.format("You gave &c" + target.getName() + " " + amount + "&7x &c" + crate.getIdentifier() + " &7pouches."));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("give")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(F.error(plugin.getLanguage().playerNotFound()));
                    return true;
                }
                ICrate crate = plugin.getCratesManager().getCrate(args[2]);
                if (crate == null) {
                    sender.sendMessage(F.error(plugin.getLanguage().invalidCrateInput()));
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                    return true;
                }
                ItemStack item = crate.getKey();
                item.setAmount(amount);
                target.getInventory().addItem(item);
                target.sendMessage(F.format(plugin.getLanguage().keyReceived(amount, crate.getIdentifier())));
                if (target != sender) {
                    sender.sendMessage(F.format(plugin.getLanguage().keyGiven(target.getName(), amount, crate.getIdentifier())));
                }
                return true;
            }
        }
        if (!permissionCheck(sender, "customcrates.command", true)) return true;
        sender.sendMessage(F.format("Commands:"));
        if (permissionCheck(sender, "customcrates.command.reload", false))
            sender.sendMessage(F.help("/" + label + " reload", "Reload the plugin."));
        if (permissionCheck(sender, "customcrates.command.info", false))
            sender.sendMessage(F.help("/" + label + " info", "Retrieve information about this plugin."));
        if (permissionCheck(sender, "customcrates.command.list", false))
            sender.sendMessage(F.help("/" + label + " list", "List all available crates."));
        if (permissionCheck(sender, "customcrates.command.remove", false))
            sender.sendMessage(F.help("/" + label + " remove", "Removes the crate you are looking at."));
        if (permissionCheck(sender, "customcrates.command.set", false))
            sender.sendMessage(F.help("/" + label + " set <crate>", "Sets a crate at the block you are looking at."));
        if (permissionCheck(sender, "customcrates.command.open", false))
            sender.sendMessage(F.help("/" + label + " open <crate>", "Open a crate without a key."));
        if (permissionCheck(sender, "customcrates.command.preview", false))
            sender.sendMessage(F.help("/" + label + " preview <crate>", "Preview a crate without physically clicking one."));
        if (permissionCheck(sender, "customcrates.command.giveall", false))
            sender.sendMessage(F.help("/" + label + " giveall <crate> <amount>", "Give all players on the server x amount of <crate> keys."));
        if (permissionCheck(sender, "customcrates.command.give", false))
            sender.sendMessage(F.help("/" + label + " give <player> <crate> <amount>", "Give <player> x amount of <crate> keys."));
        if (permissionCheck(sender, "customcrates.command.givepouch", false))
            sender.sendMessage(F.help("/" + label + " givepouch|gp <player> <crate> <amount>", "Give <player> x amount of <crate> pouches."));
        // TODO RE-ENABLE FOR UPDATE 4.x.x <- 4.2.2
//        sender.sendMessage(F.help("/" + label + " editor", "Open the crate editor")); // TODO Add permission check
        return true;
    }

    private List<String> getMatches(String start, List<String> possibleMatches) {
        if (start.isEmpty()) return possibleMatches;
        List<String> matches = new ArrayList<>();
        for (String possibleMatch : possibleMatches) {
            if (possibleMatch.toLowerCase().startsWith(start.toLowerCase()))
                matches.add(possibleMatch);
        }
        return matches;
    }

    private boolean isPlayer(CommandSender sender, boolean notify) {
        if (!(sender instanceof Player)) {
            if (notify) {
                sender.sendMessage(F.color(plugin.getLanguage().noPlayer()));
            }
            return false;
        } else {
            return true;
        }
    }

    private boolean permissionCheck(CommandSender sender, String permission, boolean inform) {
        if (!sender.hasPermission(permission)) {
            if (inform) {
                sender.sendMessage(F.color(plugin.getLanguage().noPermission()));
            }
            return false;
        } else {
            return true;
        }
    }
}