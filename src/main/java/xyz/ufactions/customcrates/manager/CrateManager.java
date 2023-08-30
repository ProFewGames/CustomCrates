package xyz.ufactions.customcrates.manager;

import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;

import java.util.List;

/**
 * A class that handles crate/prize aspects. Fetching from this class will return from a cached list.
 */
public interface CrateManager {

    /**
     * Reload all cached crates with updated ones from files.
     */
    void reload();

    /**
     * Get all cached crates.
     *
     * @return All crates.
     */
    List<Crate> getCrates();

    /**
     * Get the crate at the given location, if there is no crate at the specified location null will be returned.
     *
     * @param location The location
     * @return The crate at the given location.
     */
    @Nullable
    Crate getCrate(Location location);

    /**
     * Get a crate by its unique identifier. Will return null if no crate is located with that identifier.
     *
     * @param identifier The identifier
     * @return The crate by identifier.
     */
    @Nullable
    Crate getCrate(String identifier);

    /**
     * Create a crate with the given identifier.
     * <p>
     * Will return false if a crate with the given identifier already exists, otherwise will
     * return true if the crate has been created.
     *
     * @param identifier The identifier
     * @return If created.
     */
    boolean createCrate(String identifier);

    /**
     * Delete a crate with the given identifier. Result will be based on if the crate was deleted.
     *
     * @param crate The crate
     * @return If crate was deleted.
     * @see xyz.ufactions.customcrates.file.crate.CrateFileHandler#deleteCrate(Crate)
     */
    boolean deleteCrate(Crate crate);

    /**
     * Check if a crate exists with the given identifier. Will return true if the crate does exists, otherwise false.
     *
     * @param identifier The identifier
     * @return If crate exists.
     */
    boolean crateExists(String identifier);

    /**
     * Create and add a prize to a crate, this method will also write to file.
     *
     * @param crate The crate
     * @param prize The prize
     */
    void createPrize(Crate crate, Prize prize);

    /**
     * Delete a prize from a crate, this method will also write to file.
     *
     * @param crate The crate
     * @param prize The prize
     */
    void deletePrize(Crate crate, Prize prize);
}