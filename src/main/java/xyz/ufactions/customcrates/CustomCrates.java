package xyz.ufactions.customcrates;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.api.CustomCratesAPI;
import xyz.ufactions.customcrates.command.CratesCommand;
import xyz.ufactions.customcrates.dialog.Dialog;
import xyz.ufactions.customcrates.dialog.DialogManager;
import xyz.ufactions.customcrates.file.ConfigurationFile;
import xyz.ufactions.customcrates.file.CratesFile;
import xyz.ufactions.customcrates.file.LanguageFile;
import xyz.ufactions.customcrates.file.LocationsFile;
import xyz.ufactions.customcrates.hologram.Hologram;
import xyz.ufactions.customcrates.libs.VaultManager;
import xyz.ufactions.customcrates.listener.PlayerListener;
import xyz.ufactions.customcrates.manager.CratesManager;
import xyz.ufactions.customcrates.manager.HologramManager;
import xyz.ufactions.customcrates.manager.SoundManager;
import xyz.ufactions.customcrates.metrics.Metrics;

import java.nio.file.NotDirectoryException;

public class CustomCrates extends JavaPlugin {

    private static final int SPIGOTID = 29805; // SPIGOT PLUGIN ID

    @Getter
    private LanguageFile language;
    @Getter
    private CratesFile cratesFile;
    @Getter
    private LocationsFile locationsFile;
    @Getter
    private ConfigurationFile configurationFile;

    @Getter
    private CratesManager cratesManager;
    @Getter
    private HologramManager hologramManager;
    @Getter
    private SoundManager soundManager;
    @Getter
    private DialogManager dialogManager;

    @Override
    public void onEnable() {
        this.locationsFile = new LocationsFile(this);
        this.configurationFile = new ConfigurationFile(this);
        this.language = new LanguageFile(this, configurationFile.getLanguage());
        this.hologramManager = new HologramManager(this);
        this.soundManager = new SoundManager(this);
        this.dialogManager = new DialogManager(this);
        try {
            this.cratesFile = new CratesFile(this);
        } catch (NotDirectoryException e) {
            Bukkit.getPluginManager().disablePlugin(this);
            e.printStackTrace();
            return;
        }
        this.cratesManager = new CratesManager(this);

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

        new Metrics(this, 10660);
    }

    @Override
    public void onDisable() {
        for (Player player : Bukkit.getOnlinePlayers())
            this.dialogManager.getDialog(player).ifPresent(Dialog::end);
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
            cratesFile.reload();
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