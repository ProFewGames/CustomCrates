package xyz.ufactions.customcrates.manager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.hologram.Hologram;
import xyz.ufactions.customcrates.libs.F;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HologramManager {

    private final CustomCrates plugin;
    private final Map<Crate, List<Hologram>> holograms;

    public HologramManager(CustomCrates plugin) {
        this.plugin = plugin;
        this.holograms = new HashMap<>();

        reload();
    }

    public void unload() {
        for (List<Hologram> holograms : holograms.values()) {
            for (Hologram hologram : holograms) {
                hologram.hide();
            }
        }
        holograms.clear();

        List<ArmorStand> holograms = Hologram.getHolograms();
        if (holograms.isEmpty()) return;
        plugin.debug("Lingering holograms detected, removing...");
        holograms.forEach(Entity::remove);
    }

    public void reload() {
        Bukkit.getScheduler().runTask(plugin, () -> {
            unload();
            for (Map.Entry<Crate, List<Location>> entry : plugin.getLocationsFile().getLocations().entrySet()) {
                List<Location> locations = entry.getValue();
                Crate crate = entry.getKey();
                double offsetY = -1.5 + (0.25 * crate.getSettings().getHolographicLines().size());
                for (Location location : locations) {
                    location.add(0, offsetY, 0);
                    Hologram hologram = new Hologram(plugin, location);
                    hologram.centerPosition();
                    for (String line : crate.getSettings().getHolographicLines()) {
                        if (line.equalsIgnoreCase("{item_key}")) {
                            hologram.addItem(crate.getSettings().getKey().build());
                            continue;
                        }
                        line = line.replaceAll("\\{crate_display}", crate.getSettings().getDisplay());
                        line = F.color(line);
                        hologram.addLine(line);
                    }
                    hologram.refresh();
                    if (!this.holograms.containsKey(crate)) this.holograms.put(crate, new ArrayList<>());
                    this.holograms.get(crate).add(hologram);
                }
            }
        });
    }
}