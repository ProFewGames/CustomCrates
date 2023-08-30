package xyz.ufactions.customcrates.file.crate;

import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;

import java.io.File;
import java.nio.file.NotDirectoryException;
import java.util.List;

public interface CrateFileHandler {

    /**
     * Create the crates directory if it does not exist already.
     * If the crates path is not a directory an exception is thrown.
     * If no files are located in the crates path the default crate is loaded from resources.
     *
     * @throws NotDirectoryException Thrown if the crates path "plugins/CustomCrates/crates" is not a directory.
     */
    void reload() throws NotDirectoryException;

    /**
     * Create and/or save a prize in the crate's configuration file.
     *
     * @param crate The crate
     * @param prize The prize
     */
    void createPrize(Crate crate, Prize prize);

    /**
     * Delete a prize from the crate's configuration file.
     *
     * @param crate The crate
     * @param prize The prize
     */
    void deletePrize(Crate crate, Prize prize);

    /**
     * Returns {@code true} if a crate with the specified identifier is located, otherwise {@code false} is returned.
     *
     * @param identifier The identifier
     * @return true if exists, false otherwise.
     */
    boolean doesCrateExist(String identifier);

    /**
     * Create a new crate that is saved to file. If a crate exists with the given identifier then that crate is returned.
     *
     * @param identifier The identifier
     * @return A new or existing crate.
     */
    Crate createCrate(String identifier);

    /**
     * Delete a crate if exists. Will return {@code false} if the file does not exist or if the file failed to delete,
     * otherwise {@code true}.
     *
     * @param crate The crate
     * @return If delete.
     */
    boolean deleteCrate(Crate crate);

    /**
     * Get all crates in the crate directory of the plugin. If you're calling this method multiple times it is advised
     * to use the cached crates in {@link xyz.ufactions.customcrates.manager.CratesManager}.
     *
     * @return The crates.
     */
    List<Crate> getCrates();

    /**
     * Get a crate by the file. This will return {@code null} if the file is null OR if the config fails to parse.
     *
     * @param file The file
     * @return The crate.
     */
    Crate getCrateByFile(File file);

    /**
     * Get a crate by the identifier. This will return {@code false} if no file exists the provided identifier,
     * {@code true} otherwise.
     *
     * @param identifier The identifier
     * @return The crate.
     */
    Crate getCrateByIdentifier(String identifier);

    /**
     * Get a file by the crate. This will return {@code false} if no file exists the provided crate,
     * {@code true} otherwise.
     *
     * @return The crate.
     */
    File getFileByCrate(Crate crate);
}