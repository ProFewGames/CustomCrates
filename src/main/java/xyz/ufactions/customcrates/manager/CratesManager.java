package xyz.ufactions.customcrates.manager;

import org.bukkit.Location;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;

import java.util.ArrayList;
import java.util.List;

public class CratesManager {

    private final List<Crate> crates;
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

    public Crate getCrate(Location location) {
        return plugin.getLocationsFile().getCrate(location);
    }

    public Crate getCrate(String name) {
        for (Crate crate : getCrates()) {
            if (crate.getSettings().getIdentifier().equalsIgnoreCase(name)) {
                return crate;
            }
        }
        return null;
    }

    public void setGlowingPrize(Crate crate, Prize prize, boolean glow) {
        plugin.getCratesFile().setPrizeGlow(crate, prize, glow);
    }

    public void editCrateDisplayName(Crate crate, String displayName) {
//        crate.setDisplay(displayName); TODO FIX
        plugin.getCratesFile().changeCrateDisplayName(crate.getSettings().getIdentifier(), displayName);
    }

    public boolean deleteCrate(Crate crate) {
        crates.remove(crate);
        return plugin.getCratesFile().deleteCrate(crate.getSettings().getIdentifier());
    }

    public synchronized List<Crate> getCrates() {
        return crates;
    }
}