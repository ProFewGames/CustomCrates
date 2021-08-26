package xyz.ufactions.customcrates.file;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.libs.FileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// XXX - Static getter for language?
public class LocationsFile extends FileHandler {

    public LocationsFile(CustomCrates plugin) {
        super(plugin, "locations.yml");
    }

    public Crate getCrate(Location location) {
        if (!contains("saves")) return null;
        for (String key : getConfigurationSection("saves").getKeys(false)) {
            String path = "saves." + key + ".location";
            if (getString(path + ".world").equals(location.getWorld().getName())) {
                if (getInt(path + ".x") == location.getBlockX()) {
                    if (getInt(path + ".y") == location.getBlockY()) {
                        if (getInt(path + ".z") == location.getBlockZ()) {
                            return plugin.getCratesManager().getCrate(getString("saves." + key + ".crate"));
                        }
                    }
                }
            }
        }
        return null;
    }

    public boolean isCrate(Location location) {
        return getCrate(location) != null;
    }

    public HashMap<Crate, List<Location>> getLocations() {
        HashMap<Crate, List<Location>> locations = new HashMap<>();
        if (contains("saves")) {
            for (String key : getConfigurationSection("saves").getKeys(false)) {
                String path = "saves." + key;
                Crate crate = plugin.getCratesManager().getCrate(getString(path + ".crate"));
                if (crate != null) {
                    World world = Bukkit.getWorld(getString(path + ".location.world"));
                    int x = getInt(path + ".location.x");
                    int y = getInt(path + ".location.y");
                    int z = getInt(path + ".location.z");
                    if (world == null) {
                        plugin.getLogger().info("Location @ " + x + ", " + y + ", " + z + " " + getString(path + ".world") + " is invalid for crate " + crate.getSettings().getIdentifier() + ". Has this world been deleted?");
                        continue;
                    }
                    if (!locations.containsKey(crate)) locations.put(crate, new ArrayList<>());
                    locations.get(crate).add(new Location(world, x, y, z));
                }
            }
        }
        return locations;
    }

    public void saveLocation(Crate crate, Location location) throws IOException {
        int index = getInt("index", 1);

        set("saves." + index + ".crate", crate.getSettings().getIdentifier());
        set("saves." + index + ".location.world", location.getWorld().getName());
        set("saves." + index + ".location.x", location.getBlockX());
        set("saves." + index + ".location.y", location.getBlockY());
        set("saves." + index + ".location.z", location.getBlockZ());

        set("index", ++index);
        save();

        plugin.getHologramManager().reload();
    }

    public void deleteLocation(Location location) throws IOException {
        if (!contains("saves")) return;
        for (String key : getConfigurationSection("saves").getKeys(false)) {
            String path = "saves." + key + ".location";
            if (getString(path + ".world").equals(location.getWorld().getName())) {
                if (getInt(path + ".x") == location.getBlockX()) {
                    if (getInt(path + ".y") == location.getBlockY()) {
                        if (getInt(path + ".z") == location.getBlockZ()) {
                            set("saves." + key, null);
                            save();
                            plugin.getHologramManager().reload();
                            return;
                        }
                    }
                }
            }
        }
    }
}