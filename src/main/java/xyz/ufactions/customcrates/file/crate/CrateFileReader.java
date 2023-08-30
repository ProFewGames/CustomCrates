package xyz.ufactions.customcrates.file.crate;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;
import xyz.ufactions.customcrates.item.ItemStackBuilder;
import xyz.ufactions.customcrates.libs.RandomizableList;
import xyz.ufactions.customcrates.spin.Spin;

import java.util.*;

public interface CrateFileReader {

    /**
     * Create a {@link CrateFileReader} to read a crate's properties from file.
     *
     * @param plugin The plugin
     * @param config The config
     * @return A new instance of {@link CrateFileReader}.
     */
    static CrateFileReader create(CustomCrates plugin, FileConfiguration config) {
        return new CrateFileReaderImpl(plugin, config);
    }

    // *** Properties ***

    /**
     * Get the crate unique identifier, unique identifiers should never be the same for multiple crates.
     *
     * @return Crate unique identifier.
     */
    String readIdentifier();

    /**
     * Get the crate block {@link Material}.
     *
     * @return Crate block material.
     */
    Material readBlock();

    /**
     * Get a list of all commands to be executed before a crate opens. Users of this method should replace the variable
     * %player% with the name of the player who is opening the crate.
     *
     * @return A list of commands from file.
     */
    List<String> readCommands();

    /**
     * Get the crate's display name.
     * Note: For display purposes only, should not be used for searching for this crate; If your need is to search of this
     * crate (or related) then use readIdentifier() instead.
     *
     * @return Crate display name.
     */
    Optional<String> readDisplay();

    /**
     * Get the crate's spin time. Spin time is the amount of time the inventory will stay opened for before it
     * automatically closes.
     *
     * @return The crate spin time.
     */
    OptionalLong readSpinTime();

    /**
     * Get the crate spin type {@link xyz.ufactions.customcrates.spin.Spin.SpinType}.
     *
     * @return The crate spin type.
     */
    Optional<Spin.SpinType> readSpinType();

    // *** Sounds ***

    /**
     * Get the sound that should be played when a crate is opened.
     * If this optional returns empty that signifies that the sound string specified in the {@link FileConfiguration}
     * could not be parsed to a {@link Sound} enum or one was not specified.
     *
     * @return The crate opening sound.
     */
    Optional<Sound> readOpenSound();

    /**
     * Get the sound to be played everytime a crate spins (goes through a cycle).
     * If this optional returns empty that signifies that the sound string specified in the {@link FileConfiguration}
     * could not be parsed to a {@link Sound} enum or one was not specified.
     *
     * @return The crate spin sound.
     */
    Optional<Sound> readSpinSound();

    /**
     * Get the sound to be played when a crate reaches the "win" stage, end spin cycle.
     * If this optional returns empty that signifies that the sound string specified in the {@link FileConfiguration}
     * could not be parsed to a {@link Sound} enum or one was not specified.
     *
     * @return The win sound.
     */
    Optional<Sound> readWinSound();

    // *** Items ***

    /**
     * Get the crate's key item from config.
     *
     * @return The ItemStackBuilder (key) from config.
     */
    ItemStackBuilder readKey();

    /**
     * Get the crate's pouch item from config.
     * This will return an {@link Optional} as this section of the configuration and this feature is optional in CustomCrates.
     *
     * @return The pouch item.
     */
    Optional<ItemStackBuilder> readPouch();

    /**
     * Get a {@link RandomizableList} of {@link Prize} from config.
     *
     * @return A list of prizes from config.
     */
    RandomizableList<Prize> readPrizes();

    // *** Hologram ***

    /**
     * Get a list of lines for holograms from the config.
     *
     * @return List of lines for hologram.
     */
    List<String> readHologramLines();

    /**
     * Get the hologram offset to set. In simple words, how far above the crate the hologram should start.
     * <p>
     * This will return an optional as if not set the plugin will automatically handle hologram offsetting. This option
     * is specifically useful for crates that have custom textures where the hologram will clip into the texture otherwise.
     *
     * @return The hologram offset.
     */
    OptionalDouble readHologramOffset();

    /**
     * Get a map of string and item for holograms from the config whereas string is equivalent to the placeholder that
     * will get replaced for item.
     * <p>
     * This feature is a work-in-progress.
     *
     * @return A map of string and ItemStackBuilder.
     */
    Map<String, ItemStackBuilder> readHologramItems();

    /**
     * Read all options from the {@link FileConfiguration} provided.
     *
     * @return The crate from the provided config.
     */
    Crate readAll();
}