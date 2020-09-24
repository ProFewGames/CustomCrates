package xyz.ufactions.customcrates.gui.internal;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.data.type.GlassPane;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.gui.internal.button.IButton;
import xyz.ufactions.customcrates.gui.internal.button.InverseButton;
import xyz.ufactions.customcrates.gui.internal.button.SelfSortingButton;
import xyz.ufactions.customcrates.gui.internal.button.UpdatableButton;
import xyz.ufactions.customcrates.libs.UtilMath;
import xyz.ufactions.customcrates.universal.Universal;
import xyz.ufactions.customcrates.updater.UpdateType;
import xyz.ufactions.customcrates.updater.event.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class GUI<T extends JavaPlugin> implements Listener {

    public enum GUIFiller {
        NONE, RAINBOW, PANE;
    }

    private final List<IButton<?>> buttons = new ArrayList<>();
    private final GUIFiller filler;
    private ChatColor paneColor = ChatColor.WHITE;
    private Inventory inventory;
    private final String name;
    protected boolean openReturnGUI = true;
    protected GUI<?> returnGUI;
    private int size;
    protected final T Plugin;

    public GUI(T plugin, String name, GUIFiller filler) {
        this(plugin, name, -1, filler);
    }

    public GUI(T plugin, String name, int size, GUIFiller filler) {
        this.Plugin = plugin;
        this.name = name;
        this.size = size;
        this.filler = filler;
        register();
    }

    // Methods

    public final void addButton(IButton<?>... buttons) {
        for (IButton<?> button : buttons) {
            button.setOpener(this);
            this.buttons.add(button);
        }
    }

    public final void setPaneColor(ChatColor paneColor) {
        this.paneColor = paneColor;
    }

    public final ChatColor getPaneColor() {
        return paneColor;
    }

    public final GUI<T> setReturnGUI(GUI<?> returnGUI) {
        this.returnGUI = returnGUI;
        return this;
    }

    public final void updateTitle(Player player, String title) {
        // TODO
    }

    public final void openInventory(Player player) {
        if (canOpenInventory(player)) {
            preInventoryOpen(player);
            player.openInventory(getInventory());
            Plugin.getServer().getPluginManager().registerEvents(this, Plugin);
            Plugin.getServer().getPluginManager().registerEvents(new Listener() {

                @EventHandler
                public void onQuit(PlayerQuitEvent e) {
                    if (e.getPlayer() == player) {
                        HandlerList.unregisterAll(this);
                        HandlerList.unregisterAll(GUI.this);
                    }
                }
            }, Plugin);
            onInventoryOpen(player);
        }
    }

    public final Inventory getInventory() {
        if (inventory == null) prepareInventory();
        return inventory;
    }

    private void prepareInventory() {
        if (inventory == null) {
            if (size == -1) {
                size = UtilMath.round(buttons.size());
            }
            inventory = Bukkit.createInventory(null, size, name);
        }
        inventory.clear();
        List<SelfSortingButton<?>> selfSortingButtons = new ArrayList<>();
        for (IButton<?> button : buttons) {
            if (button instanceof SelfSortingButton) {
                selfSortingButtons.add((SelfSortingButton<?>) button);
            } else {
                inventory.setItem(button.getSlot(), button.getItem());
            }
        }
        for (SelfSortingButton<?> button : selfSortingButtons) {
            if (inventory.firstEmpty() == -1) {
                break; // Inventory full
            }
            inventory.setItem(inventory.firstEmpty(), button.getItem()); // Add to first empty slot
        }
        if (filler == GUIFiller.PANE) {
            for (int i = 0; i < inventory.getSize(); i++) {
                if (inventory.getItem(i) == null) {
                    inventory.setItem(i, Universal.getInstance().colorToGlassPane(paneColor).name(" ").build());
                }
            }
        }
    }

    // Events

    @EventHandler
    public final void onClose(InventoryCloseEvent e) {
        if (e.getInventory().equals(inventory)) {
            if (!canClose((Player) e.getPlayer())) {
                Bukkit.getScheduler().runTaskLater(Plugin, () -> e.getPlayer().openInventory(getInventory()), 1L); // Give Bukkit some time to catch up
                return;
            }
            HandlerList.unregisterAll(this);
            onClose((Player) e.getPlayer());
            if (returnGUI != null && openReturnGUI) {
                Bukkit.getScheduler().runTaskLater(Plugin, () -> returnGUI.openInventory((Player) e.getPlayer()), 1L); // Give Bukkit some time to catch up
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
            for (IButton<?> button : buttons) {
                if (item.equals(button.getItem())) {
                    if (button instanceof InverseButton) {
                        if (!((InverseButton<?>) button).canInverse((Player) e.getWhoClicked())) {
                            return;
                        }
                    }
                    button.onClick((Player) e.getWhoClicked(), e.getClick());
                    onClick((Player) e.getWhoClicked(), button);
                    if (button instanceof InverseButton) {
                        if (((InverseButton<?>) button).canInverse((Player) e.getWhoClicked())) {
                            ((InverseButton<?>) button).reverse();
                            inventory.setItem(button.getSlot(), button.getItem());
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void updateButtons(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST) return;

        for (IButton<?> button : buttons) {
            if (button instanceof UpdatableButton<?>) {
                inventory.setItem(button.getSlot(), button.getItem());
            }
        }
    }

    @EventHandler
    public void updatePanels(UpdateEvent e) {
        if (e.getType() != UpdateType.FAST) return;

        if (filler == GUIFiller.RAINBOW) {
            for (int i = 0; i < getInventory().getSize(); i++) {
                ItemStack item = inventory.getItem(i);
                if (item == null || item.getType().data.equals(GlassPane.class)) {
                    inventory.setItem(i, Universal.getInstance().colorToGlassPane(randomColor()).name(" ").build());
                }
            }
        }
    }

    // Abstract Methods

    public void onClick(Player player, IButton<?> button) {
    }

    public boolean canClose(Player player) {
        return true;
    }

    public void onClose(Player player) {
    }

    public boolean canOpenInventory(Player player) {
        return true;
    }

    public void preInventoryOpen(Player player) {
    }

    public void onInventoryOpen(Player player) {
    }

    public void register() { // TODO REMOVE
    }

    protected final ChatColor randomColor() {
        return ChatColor.values()[UtilMath.random.nextInt(ChatColor.values().length)];
    }
}