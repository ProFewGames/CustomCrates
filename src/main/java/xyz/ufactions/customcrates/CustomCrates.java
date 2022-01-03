package xyz.ufactions.customcrates;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.api.CustomCratesAPI;
import xyz.ufactions.customcrates.command.CratesCommand;
import xyz.ufactions.customcrates.file.ConfigurationFile;
import xyz.ufactions.customcrates.file.CratesFile;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.file.LocationsFile;
import xyz.ufactions.customcrates.libs.VaultManager;
import xyz.ufactions.customcrates.listener.PlayerListener;
import xyz.ufactions.customcrates.manager.CratesManager;
import xyz.ufactions.customcrates.manager.HologramManager;
import xyz.ufactions.customcrates.manager.SoundManager;
import xyz.ufactions.customcrates.metrics.Metrics;

import java.nio.file.NotDirectoryException;

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
    private HologramManager hologramManager;
    private SoundManager soundManager;

    @Override
    public void onEnable() {
        this.locationsFile = new LocationsFile(this);
        this.configurationFile = new ConfigurationFile(this);
        this.language = new LanguageFile(this, configurationFile.getLanguage().getResource());
        this.hologramManager = new HologramManager(this);
        this.soundManager = new SoundManager(this);
        try {
            this.cratesFile = new CratesFile(this);
        } catch (NotDirectoryException e) {
            Bukkit.getPluginManager().disablePlugin(this);
            e.printStackTrace();
            return;
        }
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

        new xyz.ufactions.customcrates.updater.Updater(this);
        new Metrics(this, 10660);
    }

    @Override
    public void onDisable() {
        this.hologramManager.unload();
    }

    public boolean debugging() {
        return configurationFile.debugging();
    }

    public void debug(Object o) {
        if (debugging()) getLogger().info("(DEBUGGING) " + o);
    }

    public void reload() {
        locationsFile.reload();
        try {
            cratesFile.reload();
        } catch (NotDirectoryException e) {
            getLogger().warning("Failed to reload crates file.");
            e.printStackTrace();
        }
        manager.reload();
        configurationFile.reload();
        hologramManager.reload();
        language = new LanguageFile(this, configurationFile.getLanguage().getResource());
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

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public ConfigurationFile getConfigurationFile() {
        return configurationFile;
    }

    public LanguageFile getLanguage() {
        return language;
    }
}