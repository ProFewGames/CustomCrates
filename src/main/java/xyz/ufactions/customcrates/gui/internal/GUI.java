package xyz.ufactions.customcrates.gui.internal;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.gui.internal.button.Button;
import xyz.ufactions.customcrates.gui.internal.button.InverseButton;
import xyz.ufactions.customcrates.libs.ColorLib;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.ItemBuilder;
import xyz.ufactions.customcrates.libs.UtilMath;

import java.util.ArrayList;
import java.util.List;

// XXX Optimize
public abstract class GUI implements Listener {

    public enum GUIFiller {
        NONE, RAINBOW, PANE;
    }

    public enum GUIAction {
        REGISTER,
        CLICK,
        CLOSE,
        POST_OPEN,
        PRE_OPEN,
        BAKE
    }

    private final List<Button> buttons = new ArrayList<>();
    private final GUIFiller filler;
    private ChatColor paneColor = ChatColor.WHITE;
    private Inventory inventory;
    private final String name;
    protected GUI returnGUI;
    protected final CustomCrates plugin;

    private int size;
    private int index = 0; // Used for paging

    public GUI(CustomCrates plugin, String name, GUIFiller filler) {
        this(plugin, name, -1, filler);
    }

    public GUI(CustomCrates plugin, String name, int size, GUIFiller filler) {
        this.plugin = plugin;
        this.name = name;
        this.size = size;
        this.filler = filler;
        onActionPerformed(GUIAction.REGISTER, null);
    }

    // Methods

    public final void addButton(Button... buttons) {
        for (Button button : buttons) {
            button.setGUI(this);
            this.buttons.add(button);
        }
    }

    public final void setPaneColor(ChatColor paneColor) {
        this.paneColor = paneColor;
    }

    public final ChatColor getPaneColor() {
        return paneColor;
    }

    public final void setReturnGUI(GUI returnGUI) {
        Validate.notNull(returnGUI);

        this.returnGUI = returnGUI;
    }

    public final void updateTitle(Player player, String title) {
        // TODO
    }

    public final void openInventory(Player player, GUI from) {
        if (from != null) setReturnGUI(from);
        openInventory(player);
    }

    public final synchronized void openInventory(Player player) {
        if (canOpenInventory(player)) {
            onActionPerformed(GUIAction.PRE_OPEN, player);
            player.openInventory(getInventory());
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            plugin.getServer().getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onQuit(PlayerQuitEvent e) {
                    if (e.getPlayer() == player) {
                        HandlerList.unregisterAll(this);
                        HandlerList.unregisterAll(GUI.this);
                    }
                }
            }, plugin);
            onActionPerformed(GUIAction.POST_OPEN, player);
        }
    }

    public final Inventory getInventory() {
        if (inventory == null) prepareInventory();
        return inventory;
    }

    // Prepare inventory

    private void prepareInventory() {
        bakeInventory();
        seatButtons();
    }

    private void bakeInventory() {
        if (inventory != null) return;
        if (size != -1) {
            this.inventory = Bukkit.createInventory(null, Math.min(UtilMath.round(size), 54), name);
        } else {
            int max = 0;
            int selfSortingButtons = 0;
            for (Button button : buttons) {
                if (button.isSelfSorting()) {
                    selfSortingButtons++;
                } else if (button.getSlot() > max) {
                    max = button.getSlot();
                }
            }
            max = Math.min(UtilMath.round(max + selfSortingButtons), 54);
            this.size = UtilMath.round(max + selfSortingButtons);

            this.inventory = Bukkit.createInventory(null, max, name);
        }
        onActionPerformed(GUIAction.BAKE, null);
    }

    private void seatButtons() {
        inventory.clear();

        List<Button> buttons = this.buttons.subList(index * 45,
                Math.min(45 + (index * 45), this.buttons.size()));
        List<Button> selfSortingButtons = new ArrayList<>(); // Add these to the inventory last

        for (Button button : buttons) {
            if (button.isSelfSorting()) {
                selfSortingButtons.add(button);
                continue;
            }
            inventory.setItem(button.getSlot(), button.getItem());
        }

        for (Button button : selfSortingButtons) {
            if (inventory.firstEmpty() == -1) {
                plugin.debug("Insufficient space for a self-sorting button.");
                break;
            }
            inventory.setItem(inventory.firstEmpty(), button.getItem());
        }

        if (size > 54) {
            ItemStack previous = new ItemBuilder(Material.BOOK).name(F.color("&3&lPrevious Page")).lore("* Click to get to the previous page. *").glow(true).build();
            ItemStack next = new ItemBuilder(Material.BOOK).name(F.color("&3&lNext Page")).lore("* Click to get to the next page. *").glow(true).build();
            inventory.setItem(48, previous);
            inventory.setItem(50, next);
        }

        if (filler == GUIFiller.PANE) {
            for (int i = 0; i < inventory.getSize(); i++)
                if (inventory.getItem(i) == null)
                    inventory.setItem(i, ColorLib.cp(paneColor).name(" ").build());
        }
    }

    // Events

    @EventHandler
    public final void onClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            if (!canClose((Player) e.getPlayer())) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> e.getPlayer().openInventory(getInventory()), 1L);
                return;
            }
            HandlerList.unregisterAll(this);
            onActionPerformed(GUIAction.CLOSE, (Player) e.getPlayer());
            if (returnGUI != null) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> returnGUI.openInventory((Player) e.getPlayer()), 1L);
            }
        }
    }

    @EventHandler
    public final void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getCurrentItem() == null) return;
        if (e.getClickedInventory().equals(inventory)) {
            ItemStack item = e.getCurrentItem();
            e.setCancelled(true);
            if (item.getType() == Material.BOOK) {
                if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                    if (item.getItemMeta().getDisplayName().equalsIgnoreCase(F.color("&3&lNext Page"))) {
                        if (index * 54 > buttons.size()) {
                            e.getWhoClicked().sendMessage(F.error("There are no more pages."));
                            return;
                        }
                        index++;
                        seatButtons();
                        return;
                    } else if (item.getItemMeta().getDisplayName().equalsIgnoreCase(F.color("&3&lPrevious Page"))) {
                        if (index == 0) {
                            e.getWhoClicked().sendMessage(F.error("There is no previous page"));
                            return;
                        }
                        index--;
                        seatButtons();
                        return;
                    }
                }
            }
            for (Button button : buttons) {
                if (item.equals(button.getItem())) {
                    if (button instanceof InverseButton) {
                        if (!((InverseButton) button).canInverse((Player) e.getWhoClicked())) {
                            return;
                        }
                    }
                    button.onClick((Player) e.getWhoClicked(), e.getClick());
                    onActionPerformed(GUIAction.CLICK, (Player) e.getWhoClicked());
                    if (button instanceof InverseButton) {
                        if (((InverseButton) button).canInverse((Player) e.getWhoClicked())) {
                            ((InverseButton) button).reverse();
                            inventory.setItem(button.getSlot(), button.getItem());
                        }
                    }
                }
            }
        }
    }

    // TODO REENABLE THESE METHODS

//    @EventHandler
//    public final void updateButtons(UpdateEvent e) {
//        if (e.getType() != UpdateType.TICK) return;
//
//        for (Button<?> button : buttons) {
//            if (button.getRefreshTime() <= -1) continue;
//            if (!UtilTime.elapsed(button.getLastUpdated(), button.getRefreshTime())) continue;
//            inventory.setItem(button.getSlot(), button.getItem());
//            button.setLastUpdated(System.currentTimeMillis());
//        }
//    }

//    @EventHandler
//    public final void updatePanels(UpdateEvent e) {
//        if (e.getType() != UpdateType.FAST) return;
//
//        if (filler == GUIFiller.RAINBOW) {
//            for (int i = 0; i < getInventory().getSize(); i++) {
//                ItemStack item = inventory.getItem(i);
//                if (VersionUtils.getVersion().greaterOrEquals(VersionUtils.Version.V1_9)) {
//                    if (item != null && !item.getType().data.equals(GlassPane.class)) continue;
//                } else {
//                    if (item != null && item.getType() != Material.getMaterial("STAINED_GLASS_PANE")) continue;
//                }
//                inventory.setItem(i, ColorLib.cp(ColorLib.randomColor()).name(" ").build());
//            }
//        }
//    }

    // Abstract Methods

    public boolean canClose(Player player) {
        return true;
    }

    public boolean canOpenInventory(Player player) {
        return true;
    }

    public void onActionPerformed(GUIAction action, Player player) {
    }
}