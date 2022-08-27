package xyz.ufactions.customcrates.hologram;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.F;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    private static final NamespacedKey hologramKey = NamespacedKey.minecraft("ccholo");

    public static List<ArmorStand> getHolograms() {
        List<ArmorStand> holograms = new ArrayList<>();
        for (World world : Bukkit.getWorlds())
            for (ArmorStand entity : world.getEntitiesByClass(ArmorStand.class))
                if (entity.getPersistentDataContainer().has(hologramKey, PersistentDataType.STRING))
                    holograms.add(entity);
        return holograms;
    }

    // Meta
    private Location location;
    private List<Object> lines;

    // Storage
    private final CustomCrates plugin;
    private final List<ArmorStand> armorStands;
    private final List<Item> items;

    public Hologram(CustomCrates plugin, Location location) {
        this.location = location;
        this.plugin = plugin;
        this.lines = new ArrayList<>();
        this.armorStands = new ArrayList<>();
        this.items = new ArrayList<>();
    }

    public void centerPosition() {
        this.location = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getY(), location.getBlockZ() + 0.5, location.getYaw(), location.getPitch());
    }

    public void refresh() {
        hide();
        show();
    }

    public void show() {
        if (!isHidden()) {
            plugin.debug("Cannot show hologram as it is already visible.");
            return;
        }

        for (int delta = 0; delta < lines.size(); delta++) {
            Object line = lines.get(delta);
            Location location = this.location.clone().subtract(0, delta * 0.25, 0);
            if (line instanceof ItemStack) {
                Item item = location.getWorld().dropItem(location, (ItemStack) line);
                item.setPickupDelay(Integer.MAX_VALUE);
                // TODO
                items.add(item);
                continue;
            }
            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.getPersistentDataContainer().set(hologramKey, PersistentDataType.STRING, Boolean.TRUE.toString());
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setCustomName(F.color((String) line));
            armorStand.setRemoveWhenFarAway(false);
            armorStands.add(armorStand);
        }
    }

    public void hide() {
        if (isHidden()) {
            plugin.debug("Cannot hide hologram as there is nothing to hide");
            return;
        }

        for (Item item : items) {
            item.remove();
        }
        items.clear();

        for (ArmorStand armorStand : armorStands) {
            armorStand.remove();
        }
        armorStands.clear();
    }

    public boolean isHidden() {
        return armorStands.isEmpty();
    }

    public void addLine(String line) {
        this.lines.add(line);
    }

    public void addItem(ItemStack item) {
        this.lines.add(ItemStackBuilder.from(item).name("CustomCrates - Unobtainable").build());
    }
}