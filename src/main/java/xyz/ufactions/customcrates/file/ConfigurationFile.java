package xyz.ufactions.customcrates.file;

import com.stipess1.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.libs.FileHandler;

public class ConfigurationFile extends FileHandler {

    private Updater.UpdateType updateType;
    private LanguageFile.LanguageType language;

    public ConfigurationFile(JavaPlugin plugin) {
        super(plugin, "config.yml", plugin.getDataFolder(), "config.yml");
    }

    public LanguageFile.LanguageType getLanguage() {
        return language;
    }

    public boolean debugging() {
        return getBoolean("debug", false);
    }

    public Updater.UpdateType getUpdateType() {
        return updateType;
    }

    @Override
    public void onReload() {
        try {
            language = LanguageFile.LanguageType.valueOf(getString("language", "ENGLISH").toUpperCase());
        } catch (EnumConstantNotPresentException e) {
            language = LanguageFile.LanguageType.ENGLISH;
            plugin.getLogger().warning("Language not valid. Defaulting to 'ENGLISH'. Please review your 'config.yml' 'language:' setting");
        }
        try {
            updateType = Updater.UpdateType.valueOf(getString("updater", "CHECK_DOWNLOAD"));
        } catch (EnumConstantNotPresentException e) {
            updateType = Updater.UpdateType.CHECK_DOWNLOAD;
            plugin.getLogger().warning("Update Type not valid. Defaulting to CHECK_DOWNLOAD. Please review your `config.yml` `updater:` settings.");
        }
    }
}