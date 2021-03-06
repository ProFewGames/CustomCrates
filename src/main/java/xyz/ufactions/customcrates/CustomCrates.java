package xyz.ufactions.customcrates;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.api.CustomCratesAPI;
import xyz.ufactions.customcrates.command.CratesCommand;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.file.ConfigurationFile;
import xyz.ufactions.customcrates.file.CratesFile;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.file.LocationsFile;
import xyz.ufactions.customcrates.libs.F;
import xyz.ufactions.customcrates.libs.VaultManager;
import xyz.ufactions.customcrates.listener.PlayerListener;
import xyz.ufactions.customcrates.manager.CratesManager;
import xyz.ufactions.customcrates.metrics.Metrics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomCrates extends JavaPlugin {

    private final int SPIGOTID = 29805; // SPIGOT PLUGIN ID

    // Files
    private CratesFile cratesFile;
    private LocationsFile locationsFile;
    private ConfigurationFile configurationFile;

    // Handlers
    private LanguageFile language;

    // Managers
    private CratesManager manager;

    @Override
    public void onEnable() {
        this.cratesFile = new CratesFile(this);
        this.locationsFile = new LocationsFile(this);
        this.configurationFile = new ConfigurationFile(this);
        this.language = new LanguageFile(this, configurationFile.getLanguage().getResource());
        this.manager = new CratesManager(this);

        CustomCratesAPI.initialize(this);

        CratesCommand cratesCommand = new CratesCommand(this);
        getCommand("customcrates").setExecutor(cratesCommand);
        getCommand("customcrates").setTabCompleter(cratesCommand);

        VaultManager.initialize(this);
        if (!VaultManager.getInstance().useEconomy()) {
            if (configurationFile.debugging())
                getLogger().info("Economy not enabled. Purchase signs will not be in use.");
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        reseatHolograms();

        new xyz.ufactions.customcrates.updater.Updater(this);
        new Metrics(this, 10660);
    }

    public boolean debugging() {
        return configurationFile.debugging();
    }

    public void debug(Object o) {
        if (debugging()) getLogger().info("(DEBUGGING) " + o);
    }

    public void reload() {
        locationsFile.reload();
        cratesFile.reload();
        manager.reload();
        configurationFile.reload();
        language = new LanguageFile(this, configurationFile.getLanguage().getResource());
        reseatHolograms();
    }

    public void reseatHolograms() {
        HashMap<Crate, List<Location>> crates = locationsFile.getLocations();

        Bukkit.getScheduler().runTask(this, () -> { // Make sure this task is running sync'd
            if (Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
                HologramsAPI.getHolograms(this).forEach(Hologram::delete); // Delete all previous holograms
                for (Map.Entry<Crate, List<Location>> entry : crates.entrySet()) {
                    Crate crate = entry.getKey();
                    if (crate.getSettings().getHolographicLines().isEmpty()) continue;
                    List<Location> locations = entry.getValue();

                    for (Location location : locations) {
                        location.add(0.5, 1.0 + (0.25 * crate.getSettings().getHolographicLines().size()), 0.5);

                        Hologram hologram = HologramsAPI.createHologram(this, location);
                        for (String line : crate.getSettings().getHolographicLines()) {
                            line = line.replaceAll("\\{crate_display}", crate.getSettings().getDisplay());
                            boolean found = false;
                            for (String identifier : crate.getSettings().getHolographicItemMap().keySet()) {
                                if (line.equalsIgnoreCase("{" + identifier + "}")) {
                                    hologram.teleport(location.add(0, 0.5, 0));
                                    hologram.appendItemLine(crate.getSettings().getHolographicItemMap().get(identifier).build());
                                    found = true;
                                    break;
                                }
                            }
                            if (found) continue;
                            hologram.appendTextLine(F.color(line));
                        }
                    }
                }
            }
        });
    }

    // Managers

    public CratesManager getCratesManager() {
        return manager;
    }

    // Files Start

    public CratesFile getCratesFile() {
        return cratesFile;
    }

    public LocationsFile getLocationsFile() {
        return locationsFile;
    }

    public ConfigurationFile getConfigurationFile() {
        return configurationFile;
    }

    public LanguageFile getLanguage() {
        return language;
    }
}