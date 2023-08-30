package xyz.ufactions.customcrates;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.api.CustomCratesAPI;
import xyz.ufactions.customcrates.command.CratesCommand;
import xyz.ufactions.customcrates.dialog.Dialog;
import xyz.ufactions.customcrates.dialog.DialogManager;
import xyz.ufactions.customcrates.factory.SoundFactory;
import xyz.ufactions.customcrates.file.ConfigurationFile;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.file.LocationsFile;
import xyz.ufactions.customcrates.file.crate.CrateFileHandler;
import xyz.ufactions.customcrates.file.crate.CrateFileHandlerImpl;
import xyz.ufactions.customcrates.file.migrator.CrateFileMigrator;
import xyz.ufactions.customcrates.libs.VaultManager;
import xyz.ufactions.customcrates.listener.PlayerListener;
import xyz.ufactions.customcrates.listener.SignListener;
import xyz.ufactions.customcrates.manager.CrateManager;
import xyz.ufactions.customcrates.manager.CrateManagerImpl;
import xyz.ufactions.customcrates.manager.HologramManager;
import xyz.ufactions.customcrates.metrics.Metrics;

import java.nio.file.NotDirectoryException;

public class CustomCrates extends JavaPlugin {

    private static final int SPIGOTID = 29805; // SPIGOT PLUGIN ID

    @Getter
    private LanguageFile language;
    @Getter
    private CrateFileHandler crateFileHandler;
    @Getter
    private LocationsFile locationsFile;
    @Getter
    private ConfigurationFile configurationFile;

    @Getter
    private CrateManager cratesManager;
    @Getter
    private HologramManager hologramManager;
    @Getter
    private SoundFactory soundFactory;
    @Getter
    private DialogManager dialogManager;

    @Override
    public void onEnable() {
        this.configurationFile = new ConfigurationFile(this);
        this.soundFactory = new SoundFactory(this);
        this.locationsFile = new LocationsFile(this);
        this.language = new LanguageFile(this, configurationFile.getLanguage());
        this.hologramManager = new HologramManager(this);
        this.dialogManager = new DialogManager(this);

        this.crateFileHandler = new CrateFileHandlerImpl(this);
        this.cratesManager = new CrateManagerImpl(this);

        try {
            // Loading has to take place after initialization of these variables

            this.crateFileHandler.reload();
            this.cratesManager.reload();
        } catch (NotDirectoryException e) {
            e.printStackTrace();
        }

        CustomCratesAPI.initialize(this);

        CratesCommand cratesCommand = new CratesCommand(this);
        getCommand("customcrates").setExecutor(cratesCommand);
        getCommand("customcrates").setTabCompleter(cratesCommand);

        VaultManager.initialize(this);
        if (!VaultManager.getInstance().useEconomy()) {
            if (configurationFile.debugging())
                getLogger().info("Economy not enabled. Purchase signs will not be in use.");
        }

        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        new Metrics(this, 10660);

        CrateFileMigrator migrator = CrateFileMigrator.create(this);
        boolean shouldPerformMigration = migrator.shouldPerformMigration();
        if (shouldPerformMigration) {
            migrator.performMigration(true);
            reload();
        }
    }

    @Override
    public void onDisable() {
        if (this.dialogManager != null) {
            for (Player player : Bukkit.getOnlinePlayers())
                this.dialogManager.getDialog(player).ifPresent(Dialog::end);
        }

        if (this.hologramManager != null)
            this.hologramManager.unload();
    }

    public boolean debugging() {
        return configurationFile.debugging();
    }

    public void debug(Object o) {
        if (debugging()) getLogger().info("(DEBUGGING) " + o);
    }

    public void reload() {
        for (Player player : Bukkit.getOnlinePlayers())
            dialogManager.getDialog(player).ifPresent(Dialog::end);
        locationsFile.reload();
        try {
            crateFileHandler.reload();
        } catch (NotDirectoryException e) {
            getLogger().warning("Failed to reload crates file.");
            e.printStackTrace();
        }
        cratesManager.reload();
        configurationFile.reload();
        hologramManager.reload();
        language = new LanguageFile(this, configurationFile.getLanguage());
    }
}