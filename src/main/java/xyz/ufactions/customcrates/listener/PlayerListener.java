package xyz.ufactions.customcrates.listener;

import com.stipess1.updater.Updater;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.exception.CustomException;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilEvent;
import xyz.ufactions.customcrates.libs.VaultManager;
import xyz.ufactions.customcrates.universal.Universal;

import java.io.IOException;

public class PlayerListener implements Listener {

    private final CustomCrates plugin;

    public PlayerListener(CustomCrates plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChangeEvent(SignChangeEvent e) {
        if (e.getLine(0).equalsIgnoreCase("[crates]") && !e.getLine(1).isEmpty() && !e.getLine(2).isEmpty() && !e.getLine(3).isEmpty()) {
            if (!VaultManager.getInstance().useEconomy()) {
                e.getPlayer().sendMessage(F.error("&cEconomy is not enabled on the server."));
                return;
            }
            if (e.getPlayer().hasPermission("customcrates.sign.purchase.create")) {
                for (ICrate crate : plugin.getCratesManager().getCrates()) {
                    if (e.getLine(2).equalsIgnoreCase(crate.getIdentifier())) {
                        double price;
                        try {
                            price = Double.parseDouble(e.getLine(3));
                        } catch (NumberFormatException ex) {
                            e.getPlayer().sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                            return;
                        }
                        int amount;
                        try {
                            amount = Integer.parseInt(e.getLine(1));
                        } catch (NumberFormatException ex) {
                            e.getPlayer().sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                            return;
                        }
                        if (price <= 0 && amount <= 0) {
                            e.getPlayer().sendMessage(F.error(plugin.getLanguage().invalidInteger()));
                            return;
                        }
                        e.setLine(0, F.color("&7[&cCrates&7]"));
                        e.setLine(1, String.valueOf(amount));
                        e.setLine(2, F.capitalizeFirstLetter(crate.getIdentifier()));
                        e.setLine(3, "$" + price);
                        e.getPlayer().sendMessage(F.format(plugin.getLanguage().signSet()));
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignInteract(PlayerInteractEvent e) {
        if (UtilEvent.isAction(e, UtilEvent.ActionType.R_BLOCK)) {
            if (Universal.getInstance().isSign(e.getClickedBlock()) || e.getClickedBlock().getState() instanceof Sign) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                if (sign.getLine(0).equalsIgnoreCase(F.color("&7[&cCrates&7]")) && !sign.getLine(1).isEmpty() && !sign.getLine(2).isEmpty() && !sign.getLine(3).isEmpty()) {
                    if (!VaultManager.getInstance().useEconomy()) {
                        e.getPlayer().sendMessage(F.error("&cEconomy is not enabled on the server."));
                        return;
                    }
                    for (ICrate crate : plugin.getCratesManager().getCrates()) {
                        if (sign.getLine(2).equalsIgnoreCase(crate.getIdentifier())) {
                            double price;
                            try {
                                price = Double.parseDouble(sign.getLine(3).substring(1));
                            } catch (NumberFormatException ex) {
                                e.getPlayer().sendMessage(F.error(plugin.getLanguage().failedToLoadSign()));
                                e.getClickedBlock().breakNaturally();
                                return;
                            }
                            int amount;
                            try {
                                amount = Integer.parseInt(sign.getLine(1));
                            } catch (NumberFormatException ex) {
                                e.getPlayer().sendMessage(F.error(plugin.getLanguage().failedToLoadSign()));
                                e.getClickedBlock().breakNaturally();
                                return;
                            }
                            if(!e.getPlayer().hasPermission("customcrates.sign.purchase.use")) return;
                            EconomyResponse response = VaultManager.getInstance().getEconomy().withdrawPlayer(e.getPlayer(), price);
                            if (response.transactionSuccess()) {
                                ItemStack key = crate.getKey();
                                key.setAmount(amount);
                                e.getPlayer().getInventory().addItem(key);
                                if (!plugin.getLanguage().purchasedKey().isEmpty()) {
                                    e.getPlayer().sendMessage(F.format(plugin.getLanguage().purchasedKey()));
                                }
                            } else {
                                e.getPlayer().sendMessage(F.error(plugin.getLanguage().notEnoughMoney()));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPouchInteract(PlayerInteractEvent e) {
        if (UtilEvent.isAction(e, UtilEvent.ActionType.R)) {
            for (ICrate crate : plugin.getCratesManager().getCrates()) {
                ItemStack pouch = crate.getPouch();
                if (pouch.getType() != Material.AIR && Universal.getInstance().getItemInHand(e.getPlayer()).isSimilar(pouch)) {
                    e.setCancelled(true);

                    ItemStack item = Universal.getInstance().getItemInHand(e.getPlayer());

                    if (item.getAmount() == 1) {
                        e.getPlayer().getInventory().remove(item);
                    } else {
                        item.setAmount(item.getAmount() - 1);
                    }

                    crate.getSpinType().getSpin(plugin).spin(e.getPlayer(), crate);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onCrateInteract(PlayerInteractEvent e) {
        if (UtilEvent.isAction(e, UtilEvent.ActionType.R_BLOCK)) {
            ICrate crate = plugin.getCratesManager().getCrate(e.getClickedBlock().getLocation());
            if (crate != null) {
                e.setCancelled(true);
                Inventory inventory = e.getPlayer().getInventory();
                ItemStack item = crate.getKey();
                item.setAmount(1); // Just in case it's not already one we're setting it to one
                if (inventory.containsAtLeast(item, 1)) {
                    inventory.removeItem(item);
                    crate.getSpinType().getSpin(plugin).spin(e.getPlayer(), crate);
                } else {
                    e.getPlayer().sendMessage(F.error(plugin.getLanguage().noKey(item.getItemMeta().getDisplayName())));
                }
            }
        } else if (UtilEvent.isAction(e, UtilEvent.ActionType.L_BLOCK)) {
            ICrate crate = plugin.getCratesManager().getCrate(e.getClickedBlock().getLocation());
            if (crate != null) {
                e.setCancelled(true);
                e.getPlayer().openInventory(crate.getPreviewInventory());
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
                        player.sendMessage(F.format(plugin.getLanguage().crateBroken()));
                        location.getWorld().playEffect(location, Effect.SMOKE, 8);
                    } catch (IOException ex) {
                        e.setCancelled(true);
                        player.sendMessage(F.error(plugin.getLanguage().errorFileSaving()));
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
        for (ICrate crate : plugin.getCratesManager().getCrates()) {
            if (e.getView().getTitle().equals(crate.getDisplay())) {
                e.setCancelled(true);
                break;
            }
        }
    }

    // Updater

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        if (!e.getPlayer().hasPermission("customcrates.notify.version")) return;
        if (plugin.getConfigurationFile().getUpdateType() == Updater.UpdateType.NONE) return;

        new BukkitRunnable() {
            @Override
            public void run() {
                Updater updater = plugin.checkUpdate(false);
                try {
                    if (updater.getResult() == Updater.Result.UPDATE_FOUND) {
                        e.getPlayer().sendMessage(F.format(plugin.getLanguage().updateAvailable()));
                    } else if (updater.getResult() == Updater.Result.SUCCESS) {
                        e.getPlayer().sendMessage(F.format(plugin.getLanguage().updateDownloaded()));
                    }
                } catch (CustomException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(plugin);
    }
}