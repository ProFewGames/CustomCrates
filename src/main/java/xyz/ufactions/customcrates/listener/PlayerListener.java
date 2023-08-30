package xyz.ufactions.customcrates.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
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
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.dialog.Dialog;
import xyz.ufactions.customcrates.dialog.Question;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.libs.Utility;
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
        if (event.getClickedBlock() == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
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