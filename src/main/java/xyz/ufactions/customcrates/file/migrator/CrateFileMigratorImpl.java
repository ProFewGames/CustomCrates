package xyz.ufactions.customcrates.file.migrator;

import org.bukkit.configuration.file.FileConfiguration;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.file.crate.CrateFileHandlerImpl;
import xyz.ufactions.customcrates.file.crate.CrateFileWriter;
import xyz.ufactions.customcrates.file.depricated.DeprecatedFileReader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.logging.Level;

class CrateFileMigratorImpl implements CrateFileMigrator {

    private static final int currentConfigVersion = 1;

    private final CustomCrates plugin;

    private final File versionFile;

    public CrateFileMigratorImpl(CustomCrates plugin) {
        this.plugin = plugin;

        this.versionFile = new File(plugin.getDataFolder(), "config_version.dat");
    }

    private void log(Level level, String msg) {
        log(level, msg, null);
    }

    private void log(Level level, String msg, Exception exception) {
        if (exception == null)
            this.plugin.getLogger().log(level, String.format("[File Migrator] %s", msg));
        else
            this.plugin.getLogger().log(level, String.format("[File Migrator] %s", msg), exception);
    }

    private boolean saveBackup(File file) {
        log(Level.INFO, String.format("Attempting to make backup for '%s'", file.getName()));
        File directory = new File(this.plugin.getDataFolder(), "backup");
        if (directory.mkdirs())
            log(Level.INFO, "Backup directory created.");

        try {
            Files.copy(file.toPath(), new File(directory, file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
            log(Level.INFO, "Backup complete.");
            return true;
        } catch (IOException exception) {
            log(Level.SEVERE, "Backup failed.", exception);
            return false;
        }
    }

    @Override
    public boolean shouldPerformMigration() {
        if (!this.versionFile.exists()) return true;

        try (FileReader fileReader = new FileReader(this.versionFile); BufferedReader reader = new BufferedReader(fileReader)) {
            String line = reader.readLine();

            double configVersion;
            try {
                configVersion = Double.parseDouble(line);
            } catch (NumberFormatException e) {
                log(Level.WARNING, String.format("A string was located in the config version file but it was not an expected double. [%s]", line));
                return true;
            }

            return currentConfigVersion > configVersion;
        } catch (IOException e) {
            log(Level.WARNING, "Failed to read config version file.", e);
            return true;
        }
    }

    @Override
    public int performMigration(boolean force) {
        if (!shouldPerformMigration() && !force) return 0;

        if (force)
            log(Level.INFO, "Migration was forced");

        int changes = 0;
        changes += migrateItems();

        try (FileWriter fileWriter = new FileWriter(this.versionFile); BufferedWriter writer = new BufferedWriter(fileWriter)) {
            writer.write(String.valueOf(currentConfigVersion));
        } catch (IOException e) {
            log(Level.WARNING, "Failed to write to config version file.", e);
        }
        return changes;
    }

    private int migrateItems() {
        log(Level.INFO, "Commencing migration for items, X -> 5.1.0");
        CrateFileHandlerImpl crateFileHandler = (CrateFileHandlerImpl) this.plugin.getCrateFileHandler();
        Map<File, FileConfiguration> configurations = crateFileHandler.getConfigurations();

        int totalChanges = 0;

        for (Map.Entry<File, FileConfiguration> entry : configurations.entrySet()) {
            File file = entry.getKey();
            FileConfiguration config = entry.getValue();
            log(Level.INFO, String.format("Migrating '%s'", file.getName()));

            if (!saveBackup(file)) {
                log(Level.SEVERE, String.format("Migration for '%s' cannot be completed, please create a support ticket on Discord.", file.getName()));
                continue;
            }

            //noinspection deprecation
            DeprecatedFileReader reader = new DeprecatedFileReader(plugin, config);
            Crate crate = reader.read();

            if (crate == null) {
                log(Level.INFO, "File seems to be up-to-date, skipping...");
                continue;
            }

            CrateFileWriter writer = CrateFileWriter.create(plugin, crate);
            writer.writeAll();
            writer.save();

            totalChanges += 1;

            log(Level.INFO, "File migrated.");
        }

        log(Level.INFO, String.format("Migration complete - total amount of changes '%s'.", totalChanges));
        return totalChanges;
    }
}