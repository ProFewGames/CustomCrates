package xyz.ufactions.customcrates.file.migrator;

import xyz.ufactions.customcrates.CustomCrates;

public interface CrateFileMigrator {

    static CrateFileMigrator create(CustomCrates plugin) {
        return new CrateFileMigratorImpl(plugin);
    }

    /**
     * This will return true if the saved version inside of version.dat is less than the internal version for files.
     *
     * @return True if it should migrate, false otherwise.
     */
    boolean shouldPerformMigration();

    /**
     * Perform a migration. If the version saved in the version.dat file is lower than the current file version this will
     * queue a migration for all available files. Otherwise, if the force parameter is true then this will queue a migration
     * for all files regardless of version.
     *
     * @param force Force migration without relying on the saved version comparing to the current version.
     * @return Amount of operations completed.
     */
    int performMigration(boolean force);

    /**
     * Migrate any crate files still using the old file system to store items to the new base64 way of storing items.
     * <p>
     * This will perform a full scan of all files under the crates directory for any old files and convert them to new,
     * whilst also performing a full backup of said file to the backups folder located under "~/plugins/CustomCrates/backup"
     *
     * @return The amount of operations completed. If this amount is 0 then no files were changed.
     * @since 5.1
     */
//    int migrateItems();
}