package xyz.ufactions.customcrates.file;

import org.bukkit.plugin.java.JavaPlugin;
import xyz.ufactions.customcrates.libs.FileHandler;

public class ConfigurationFile extends FileHandler {

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

    @Override
    public void onReload() {
        try {
            language = LanguageFile.LanguageType.valueOf(getString("language", "ENGLISH").toUpperCase());
        } catch (EnumConstantNotPresentException e) {
            language = LanguageFile.LanguageType.ENGLISH;
            plugin.getLogger().warning("Language not valid. Defaulting to 'ENGLISH'. Please review your 'config.yml' 'language:' setting");
        }
    }
}