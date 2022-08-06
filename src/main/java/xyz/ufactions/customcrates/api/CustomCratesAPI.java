package xyz.ufactions.customcrates.api;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;

import java.util.Optional;

@UtilityClass
public class CustomCratesAPI {

    private CustomCrates plugin;

    /**
     * Only to be instantiated by CustomCrates
     */
    public void initialize(CustomCrates plugin) {
        if (CustomCratesAPI.plugin == null) CustomCratesAPI.plugin = plugin;
    }

    public Optional<Crate> getCrate(String crateIdentifier) {
        return Optional.ofNullable(plugin.getCratesManager().getCrate(crateIdentifier));
    }

    public Optional<Crate> getCrate(Location location) {
        return Optional.ofNullable(plugin.getCratesManager().getCrate(location));
    }
}