package xyz.ufactions.customcrates.libs;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.GUI;
import xyz.ufactions.customcrates.gui.internal.button.BasicButton;
import xyz.ufactions.customcrates.universal.Universal;
import xyz.ufactions.customcrates.updater.UpdateType;
import xyz.ufactions.customcrates.updater.event.UpdateEvent;

import java.util.concurrent.atomic.AtomicBoolean;

public class InputRequest {

    public static void confirmationInput(final Callback<Boolean> callback, final CustomCrates plugin, final Player player) {
        confirmationInput(callback, null, plugin, player);
    }

    public static void confirmationInput(final Callback<Boolean> callback, final ItemStack display, final CustomCrates plugin, final Player player) {
        AtomicBoolean called = new AtomicBoolean(false);

        GUI<CustomCrates> gui = new GUI<CustomCrates>(plugin, ChatColor.RED + "" + ChatColor.BOLD + "Confirmation", 27, GUI.GUIFiller.PANE) {

            @Override
            public void register() {
                setPaneColor(randomColor());
                addButton(new BasicButton<CustomCrates>(plugin, Universal.getInstance().colorToGlassPane(ChatColor.GREEN)
                                  .name(ChatColor.GREEN + "" + ChatColor.BOLD + "CONFIRM"), 11) {

                              @Override
                              public void onClick(Player player, ClickType clickType) {
                                  called.set(true);
                                  player.closeInventory();
                                  callback.run(true);
                              }
                          },
                        new BasicButton<CustomCrates>(plugin, Universal.getInstance().colorToGlassPane(ChatColor.RED)
                                .name(ChatColor.RED + "" + ChatColor.BOLD + "DENY"), 15)
                {

                    @Override
                    public void onClick (Player player, ClickType clickType){
                    called.set(true);
                    player.closeInventory();
                    callback.run(false);
                }
                });
            }

            @Override
            public boolean canClose(Player player) {
                return called.get();
            }
        };
        if (display != null) {
            gui.addButton(new BasicButton<CustomCrates>(plugin, null, 13) {

                @Override
                public ItemStack getItem() {
                    return display;
                }

                @Override
                public void onClick(Player player, ClickType type) {
                }
            });
        }
        player.closeInventory();
        gui.openInventory(player);
    }

    public static void requestInput(final Callback<String> callback, final JavaPlugin plugin, final Player player) {
        final long start = System.currentTimeMillis();
        player.closeInventory(); // If theres an inventory open

        plugin.getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onInventorOpen(InventoryOpenEvent e) {
                if (e.getPlayer() == player) {
                    e.setCancelled(true);
                }
            }

            @EventHandler
            public void onUpdate(UpdateEvent e) {
                if (e.getType() != UpdateType.FAST) return;

                // Check timeout
                if (UtilTime.elapsed(start, 60000)) {
                    player.sendMessage(F.error("Did not receive any input for 60 seconds... Timing out."));
                    HandlerList.unregisterAll(this);
                    callback.run(null);
                } else {
//                    TitleAPI.sendTitle(player, C.mHead + "Type input in chat", C.mBody + "Left click to exit", 0, 20, 0); // TODO REIMPLEMENT TITLE API
                }
            }

            @EventHandler
            public void onInteract(PlayerInteractEvent e) {
                if (UtilEvent.isAction(e, UtilEvent.ActionType.L) && e.getPlayer() == player) {
                    e.setCancelled(true);
                    HandlerList.unregisterAll(this);
                    callback.run(null);
                }
            }

            @EventHandler
            public void onChat(AsyncPlayerChatEvent e) {
                if (e.getPlayer() == player) {
                    e.setCancelled(true);
                    HandlerList.unregisterAll(this);
                    callback.run(ChatColor.stripColor(e.getMessage()));
                }
            }
        }, plugin);
    }
}