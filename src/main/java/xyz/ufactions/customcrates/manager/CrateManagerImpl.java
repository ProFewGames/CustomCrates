package xyz.ufactions.customcrates.manager;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;

import java.util.ArrayList;
import java.util.List;

public class CrateManagerImpl implements CrateManager {

    private final CustomCrates plugin;
    private final List<Crate> crates;

    public CrateManagerImpl(CustomCrates plugin) {
        this.plugin = plugin;
        this.crates = new ArrayList<>();
    }

    @Override
    public void reload() {
        this.crates.clear();
        List<Crate> crates = this.plugin.getCrateFileHandler().getCrates();
        this.plugin.debug(crates);
        this.crates.addAll(crates);
    }

    @Override
    public synchronized List<Crate> getCrates() {
        return this.crates;
    }

    @Override
    public @Nullable Crate getCrate(Location location) {
        return this.plugin.getLocationsFile().getCrate(location);
    }

    @Override
    public @Nullable Crate getCrate(String identifier) {
        if (identifier == null || identifier.isEmpty()) return null;
        identifier = identifier.replaceAll(" ", "_");
        for (Crate crate : getCrates()) {
            if (crate.getSettings().getIdentifier().equalsIgnoreCase(identifier))
                return crate;
        }
        return null;
    }

    @Override
    public boolean createCrate(String identifier) {
        if (this.plugin.getCrateFileHandler().doesCrateExist(identifier)) return false;
        Crate crate = this.plugin.getCrateFileHandler().createCrate(identifier);
        this.crates.add(crate);
        return true;
    }

    @Override
    public boolean deleteCrate(Crate crate) {
        this.crates.remove(crate);
        return this.plugin.getCrateFileHandler().deleteCrate(crate);
    }

    @Override
    public boolean crateExists(String identifier) {
        return getCrate(identifier) != null;
    }

    @Override
    public void createPrize(Crate crate, Prize prize) {
        this.plugin.getCrateFileHandler().createPrize(crate, prize);
        crate.getSettings().getPrizes().add(prize, prize.getChance());
    }

    @Override
    public void deletePrize(Crate crate, Prize prize) {
        this.plugin.getCrateFileHandler().deletePrize(crate, prize);
        crate.getSettings().getPrizes().remove(prize);
    }
}
