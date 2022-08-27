package xyz.ufactions.customcrates.listener;

import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.dialog.Dialog;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.*;
import xyz.ufactions.customcrates.universal.Universal;

import java.io.IOException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class PlayerListener implements Listener {

    private final CustomCrates plugin;

    public PlayerListener(CustomCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onDialogInput(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) return;
        Optional<Dialog> dialogOptional = this.plugin.getDialogManager().getDialog(event.getPlayer());
        if (!dialogOptional.isPresent()) return;
        Dialog dialog = dialogOptional.get();
        Question question = dialog.getCurrentQuestion();
        String input = event.getMessage();
        if (question.isStripColor())
            input = ChatColor.stripColor(input);

        event.setCancelled(true);
        if (!question.getInputPredicate().test(input)) {
            if (question.isRepeatIfFailed())
                dialog.promptQuestion();
            return;
        }

        Optional<Question> nextQuestion = dialog.nextQuestion();
        if (!nextQuestion.isPresent())
            dialog.end();
        else
            dialog.promptQuestion();
    }

    @EventHandler
    public void onDialogInteractExit(PlayerInteractEvent event) {
        if (!UtilEvent.isAction(event, UtilEvent.ActionType.L)) return;
        Optional<Dialog> dialogOptional = this.plugin.getDialogManager().getDialog(event.getPlayer());
        if (!dialogOptional.isPresent()) return;
        dialogOptional.get().end();
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Optional<Dialog> optionalDialog = this.plugin.getDialogManager().getDialog(event.getPlayer());
        if (!optionalDialog.isPresent()) return;
        optionalDialog.get().end();
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent event) {
        String[] lines = event.getLines();
        if (lines.length != 4) return;
        if (lines[0].isEmpty() || lines[1].isEmpty() || lines[2].isEmpty() || lines[3].isEmpty()) return;
        if (!lines[0].equalsIgnoreCase("[crates]") && !lines[0].equalsIgnoreCase("[pouches]")) return;
        Player player = event.getPlayer();
        if (!VaultManager.getInstance().useEconomy()) {
            player.sendMessage(F.format("&cEconomy is not enabled on the server."));
            return;
        }

        if (!player.hasPermission("customcrates.sign.purchase.create"))
            return;

        Crate crate = null;
        for (Crate otherCrate : plugin.getCratesManager().getCrates()) {
            if (otherCrate.getSettings().getIdentifier().equalsIgnoreCase(lines[2])) {
                crate = otherCrate;
                break;
            }
        }

        if (crate == null) {
            player.sendMessage(F.format(String.format("&cThe provided name '%s' is not a crate.", lines[2])));
            return;
        }

        OptionalDouble price = UtilMath.getDouble(lines[3]);
        if (!price.isPresent()) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_INTEGER)));
            return;
        }
        OptionalInt amount = UtilMath.getInteger(lines[1]);
        if (!amount.isPresent()) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_INTEGER)));
            return;
        }

        if (price.getAsDouble() <= 0 || amount.getAsInt() <= 0) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.INVALID_INTEGER)));
            return;
        }
        event.setLine(0, F.color("&7[&b&l" + F.capitalizeFirstLetter(
                lines[0].replace("[", "").replace("]", "")) + "&7]"));
        event.setLine(1, String.valueOf(amount.getAsInt()));
        event.setLine(2, F.capitalizeFirstLetter(crate.getSettings().getIdentifier()));
        event.setLine(3, "$" + price.getAsDouble());
        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.SIGN_PURCHASE_SET)));
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent event) {
        if (!UtilEvent.isAction(event, UtilEvent.ActionType.R_BLOCK)) return;
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (!(block.getState() instanceof Sign)) return;

        Sign sign = (Sign) block.getState();
        String[] lines = sign.getLines();
        if (lines.length != 4) return;
        String strippedFirstLine = ChatColor.stripColor(lines[0]);

        if (!strippedFirstLine.equalsIgnoreCase("[crates]") && !strippedFirstLine.equalsIgnoreCase("[pouches]")) return;

        Player player = event.getPlayer();
        if (!player.hasPermission("customcrates.sign.purchase.use")) return;

        if (!VaultManager.getInstance().useEconomy()) {
            player.sendMessage(F.format("&cEconomy is not enabled on the server."));
            return;
        }

        boolean givePouch = strippedFirstLine.equalsIgnoreCase("[pouches]");

        Crate crate = null;
        for (Crate otherCrate : this.plugin.getCratesManager().getCrates()) {
            if (lines[2].equalsIgnoreCase(otherCrate.getSettings().getIdentifier())) {
                crate = otherCrate;
                break;
            }
        }
        if (crate == null) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.FAILED_LOAD_SIGN)));
            return;
        }

        OptionalDouble price = UtilMath.getDouble(lines[3].substring(1));
        if (!price.isPresent()) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.FAILED_LOAD_SIGN)));
            return;
        }
        OptionalInt amount = UtilMath.getInteger(lines[1]);
        if (!amount.isPresent()) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.FAILED_LOAD_SIGN)));
            return;
        }

        EconomyResponse response = VaultManager.getInstance().getEconomy().withdrawPlayer(player, price.getAsDouble());
        if (!response.transactionSuccess()) {
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.NOT_ENOUGH_MONEY)));
            return;
        }
        ItemStack item = givePouch ? crate.getSettings().getPouch().build() : crate.getSettings().getKey().build();
        item.setAmount(amount.getAsInt());
        Utility.addOrDropItem(player.getInventory(), player.getLocation(), item);
        if (!plugin.getLanguage().getString(LanguageFile.LanguagePath.KEY_PURCHASED).isEmpty())
            player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.KEY_PURCHASED)));
    }

    @EventHandler
    public void onPouchInteract(PlayerInteractEvent e) {
        if (UtilEvent.isAction(e, UtilEvent.ActionType.R)) {
            for (Crate crate : plugin.getCratesManager().getCrates()) {
                ItemStack pouch = crate.getSettings().getPouch().build();
                if (pouch.getType() != Material.AIR && Universal.getInstance().getItemInHand(e.getPlayer()).isSimilar(pouch)) {
                    e.setCancelled(true);

                    ItemStack item = Universal.getInstance().getItemInHand(e.getPlayer());

                    if (item.getAmount() == 1) {
                        e.getPlayer().getInventory().remove(item);
                    } else {
                        item.setAmount(item.getAmount() - 1);
                    }

                    crate.getSettings().getSpinType().getSpin(plugin).spin(e.getPlayer(), crate);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onKeyPlace(BlockPlaceEvent event) {
        for (Crate crate : plugin.getCratesManager().getCrates()) {
            if (crate.getSettings().getKey().build().isSimilar(event.getItemInHand())) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.CANNOT_PLACE_KEY)));
            }
        }
    }

    @EventHandler
    public void onCrateInteract(PlayerInteractEvent event) {
        if (UtilEvent.isAction(event, UtilEvent.ActionType.R_BLOCK)) {
            Crate crate = plugin.getCratesManager().getCrate(event.getClickedBlock().getLocation());
            if (crate != null) {
                event.setCancelled(true);
                Inventory inventory = event.getPlayer().getInventory();
                ItemStack item = crate.getSettings().getKey().build();
                item.setAmount(1); // Just in case it's not already one we're setting it to one
                if (inventory.containsAtLeast(item, 1)) {
                    inventory.removeItem(item);
                    crate.getSettings().getSpinType().getSpin(plugin).spin(event.getPlayer(), crate);
                } else {
                    event.getPlayer().sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.NO_KEY, item.getItemMeta().getDisplayName())));
                }
            }
        } else if (UtilEvent.isAction(event, UtilEvent.ActionType.L_BLOCK) && !event.getPlayer().isSneaking()) {
            Crate crate = plugin.getCratesManager().getCrate(event.getClickedBlock().getLocation());
            if (crate != null) {
                event.setCancelled(true);
                crate.openPreviewInventory(event.getPlayer());
            }
        }
    }

    @EventHandler
    public void onCrateBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Location location = block.getLocation();
        if (plugin.getLocationsFile().isCrate(location)) {
            Player player = e.getPlayer();
            if (player.hasPermission("customcrates.break.crate")) {
                if (player.isSneaking()) {
                    try {
                        plugin.getLocationsFile().deleteLocation(location);
                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.CRATE_BROKEN)));
                        location.getWorld().playEffect(location, Effect.SMOKE, 8);
                    } catch (IOException ex) {
                        e.setCancelled(true);
                        player.sendMessage(F.format(plugin.getLanguage().getString(LanguageFile.LanguagePath.ERROR_FILE_SAVING)));
                        ex.printStackTrace();
                    }
                } else {
                    e.setCancelled(true);
                }
            } else {
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getCurrentItem() == null) return;
        for (Crate crate : plugin.getCratesManager().getCrates()) {
            if (e.getView().getTitle().equals(crate.getSettings().getDisplay())) {
                e.setCancelled(true);
                break;
            }
        }
    }
}