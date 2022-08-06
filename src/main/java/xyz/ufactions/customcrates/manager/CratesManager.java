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

    // Methods

    public void reload() {
        crates.clear();
        crates.addAll(plugin.getCratesFile().getCrates());
    }

    public boolean createCrate(String identifier) {
        if (crateExists(identifier)) return false;
        return plugin.getCratesFile().createCrate(identifier) != null;
    }

    public void createPrize(Crate crate, Prize prize) {
        this.plugin.getCratesFile().createPrize(crate, prize);
        crate.getSettings().getPrizes().add(prize, prize.getChance());
    }

    public boolean deleteCrate(Crate crate) {
        return this.plugin.getCratesFile().deleteCrate(crate);
    }

    public void deletePrize(Crate crate, Prize prize) {
        this.plugin.getCratesFile().deletePrize(crate, prize);
        crate.getSettings().getPrizes().remove(prize);
    }

    // Getters
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

    public boolean crateExists(String name) {
        return getCrate(name) != null;
    }

    public synchronized List<Crate> getCrates() {
        return crates;
    }
}