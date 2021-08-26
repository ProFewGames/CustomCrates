package xyz.ufactions.customcrates.hologram;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.libs.F;

import java.util.ArrayList;
import java.util.List;

public class Hologram {

    // Meta
    private Location location;
    private List<String> lines;

    // Storage
    private final CustomCrates plugin;
    private final List<ArmorStand> armorStands;

    public Hologram(CustomCrates plugin, Location location) {
        this.location = location;
        this.plugin = plugin;
        this.lines = new ArrayList<>();
        this.armorStands = new ArrayList<>();
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
            String line = lines.get(delta);

            Location location = this.location.clone().subtract(0, delta * 0.25, 0);
            ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
            armorStand.setGravity(false);
            armorStand.setCanPickupItems(false);
            armorStand.setCustomName(F.color(line));
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStands.add(armorStand);
        }
    }

    public void hide() {
        if (isHidden()) {
            plugin.debug("Cannot hide hologram as there is nothing to hide");
            return;
        }

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
}