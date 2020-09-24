package xyz.ufactions.customcrates.manager;

import org.bukkit.Location;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.ICrate;
import xyz.ufactions.customcrates.crates.Prize;

import java.util.ArrayList;
import java.util.List;

public class CratesManager {

    private final List<ICrate> crates;
    private final CustomCrates plugin;

    public CratesManager(CustomCrates plugin) {
        this.plugin = plugin;
        this.crates = new ArrayList<>();

        reload();
    }

    public void reload() {
        crates.clear();
        crates.addAll(plugin.getCratesFile().getCrates());
    }

    public boolean crateExists(String name) {
        return getCrate(name) != null;
    }

    public ICrate getCrate(Location location) {
        return plugin.getLocationsFile().getCrate(location);
    }

    public ICrate getCrate(String name) {
        for (ICrate crate : getCrates()) {
            if (crate.getIdentifier().equalsIgnoreCase(name)) {
                return crate;
            }
        }
        return null;
    }

    public void setGlowingPrize(ICrate crate, Prize prize, boolean glow) {
        prize.setGlowing(glow);
        plugin.getCratesFile().setPrizeGlow(crate, prize, glow);
    }

    public void editCrateDisplayName(ICrate crate, String displayName) {
        crate.setDisplay(displayName);
        plugin.getCratesFile().changeCrateDisplayName(crate.getIdentifier(), displayName);
    }

    public boolean deleteCrate(ICrate crate) {
        crates.remove(crate);
        return plugin.getCratesFile().deleteCrate(crate.getIdentifier());
    }

    public synchronized List<ICrate> getCrates() {
        return crates;
    }
}