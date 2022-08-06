package xyz.ufactions.customcrates.gui.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.item.Item;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.UtilMath;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class GUI implements Listener {

    protected final CustomCrates plugin;
    protected final Player player;

    private final Map<Integer, Slot> slots;

    private String inventoryTitle = "GUI";
    private Inventory inventory;
    private int inventorySize;

    private boolean valid = false;

    public GUI(CustomCrates plugin, Player player) {
        this(plugin, player, 9);
    }

    public GUI(CustomCrates plugin, Player player, int size) {
        this.inventorySize = UtilMath.roundInventorySize(size);
        this.slots = new HashMap<>();
        this.player = player;
        this.plugin = plugin;
    }

    /**
     * Apply an {@link Item}'s ItemStack and Consumer to the slot.
     *
     * @param slot The slot
     * @param item The item
     */
    public final void setItem(int slot, Item<InventoryClickEvent> item) {
        getSlot(slot).apply(item);
    }

    /**
     * Get an unmodifiable collection of all registered slots in the inventory.
     * <p>
     * Note: This collection of slots may not contain the full inventory size.
     *
     * @return An unmodifiable collection of slots.
     */
    public Collection<Slot> getSlots() {
        return slots.values();
    }

    /**
     * Get the slot at index.
     *
     * @param slot The index
     * @return The slot
     */
    public final Slot getSlot(int slot) {
        if (slot < 0 || slot >= this.inventorySize) throw new IllegalArgumentException("Invalid slot id: " + slot);
        return this.slots.computeIfAbsent(slot, i -> new Slot(this, i));
    }

    /**
     * Set the title of this GUI's inventory.
     *
     * @param title The title.
     */
    public final void setTitle(String title) {
        this.inventoryTitle = F.color(title);
    }

    /**
     * Get the inventory of this GUI. If the inventory has not yet been opened before
     * this method will bake all items into the inventory first.
     *
     * @return The inventory
     */
    public final Inventory getInventory() {
        if (this.inventory == null)
            bakeInventory();
        return this.inventory;
    }

    /**
     * Set the size of the inventory for this GUI
     *
     * @param inventorySize The size
     */
    public void setInventorySize(int inventorySize) {
        this.inventorySize = UtilMath.roundInventorySize(inventorySize);
    }

    private void bakeInventory() {
        if (this.inventory != null) return;
        this.inventory = Bukkit.createInventory(this.player, this.inventorySize, this.inventoryTitle);
        // FILLER
    }

    /**
     * Open this GUI's inventory for the player that is linked to this.
     */
    public final void open() {
        Bukkit.getScheduler().runTask(plugin, this::handleOpen);
    }

    private void handleOpen() {
        if (isValid()) return;

        this.valid = true;

        onOpen();

        this.player.openInventory(getInventory());
        Bukkit.getPluginManager().registerEvents(this, this.plugin);
    }

    /**
     * If the GUI is active and is listening for events.
     * <p>
     * Note: This does not necessarily mean the player has the GUI's inventory open.
     *
     * @return If the GUI is active
     */
    public boolean isValid() {
        return this.valid;
    }

    private void invalidate() {
        this.valid = false;

        HandlerList.unregisterAll(this);
    }

    // *** Abstract ***

    protected void onOpen() {
    }

    protected void handleClose(InventoryCloseEvent event) {
    }

    protected void handleClick(InventoryClickEvent event) {
    }

    // *** EVENTS ***

    @EventHandler
    public final void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity() != this.player) return;
        if (!isValid()) return;
        invalidate();
    }

    @EventHandler
    public final void onQuit(PlayerQuitEvent event) {
        if (event.getPlayer() != this.player) return;
        if (!isValid()) return;
        invalidate();
    }

    @EventHandler
    public final void onWorldChange(PlayerChangedWorldEvent event) {
        if (event.getPlayer() != this.player) return;
        if (!isValid()) return;
        invalidate();
    }

    @EventHandler
    public final void onTeleport(PlayerTeleportEvent event) {
        if (event.getPlayer() != this.player) return;
        if (!isValid()) return;
        invalidate();
    }

    @EventHandler
    public final void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (event.getInventory().getHolder() != this.player) return;
        if (!isValid()) return;
        invalidate();
    }

    @EventHandler
    public final void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (event.getCurrentItem() == null) return;
        if (event.getClickedInventory() == null) return;
        if (!event.getClickedInventory().equals(this.inventory)) return;
        int rs = event.getRawSlot();
        Slot slot = this.slots.get(rs);

        if (slot != null && slot.hasHandler()) {
            event.setCancelled(true);
            slot.handle(event);
        }
        handleClick(event);
    }

    @EventHandler
    public final void onInventoryOpen(InventoryOpenEvent event) {
        if (!event.getPlayer().equals(this.player)) return;
        if (!event.getInventory().equals(this.inventory)) return;
        if (!isValid()) return;
        invalidate();
    }

    @EventHandler
    public final void onInventoryClose(InventoryCloseEvent event) {
        if (!event.getPlayer().equals(this.player)) return;
        if (!event.getInventory().equals(this.inventory)) return;
        if (!isValid()) return;
        invalidate();

        handleClose(event);
    }
}