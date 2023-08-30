package xyz.ufactions.customcrates.file.crate;

import xyz.ufactions.customcrates.CustomCrates;
import xyz.ufactions.customcrates.crates.Crate;
import xyz.ufactions.customcrates.crates.Prize;

public interface CrateFileWriter {

    /**
     * Create a {@link CrateFileWriter} to write a crate's properties to file.
     * Note: You must perform {@link CrateFileWriter#save()} in order to finalize these
     * changes to file.
     *
     * @param plugin The plugin
     * @param crate  The crate
     * @return A new instance of {@link CrateFileWriter}
     */
    static CrateFileWriter create(CustomCrates plugin, Crate crate) {
        return new CrateFileWriterImpl(plugin, crate);
    }

    /**
     * WRite the crate's unique identifier.
     */
    void writeIdentifier();

    /**
     * Write the crate's block type.
     */
    void writeBlock();

    /**
     * Write the crate's display name.
     */
    void writeDisplay();

    /**
     * Write the crate's spin time.
     */
    void writeSpinTime();

    /**
     * Write the crate's open sound.
     */
    void writeOpenSound();

    /**
     * Write the crate's spin sound.
     */
    void writeSpinSound();

    /**
     * Write the crate's win sound.
     */
    void writeWinSound();

    /**
     * Write the crate's opening commands.
     */
    void writeOpenCommands();

    /**
     * Write the crate's key item.
     */
    void writeKey();

    /**
     * Write the crate's pouch item.
     */
    void writePouch();

    /**
     * Write the crate's spin type.
     */
    void writeSpinType();

    /**
     * Write a prize to the crate file.
     *
     * @param prize The prize
     */
    void writePrize(Prize prize);

    /**
     * Write all crate prizes to file.
     */
    void writePrizes();

    /**
     * Write the crate's hologram offset.
     */
    void writeHologramOffset();

    /**
     * Write a crate's hologram lines.
     */
    void writeHologramLines();

    /**
     * Write a crate's hologram items.
     */
    void writeHologramItems();

    /**
     * Write all crate properties to file.
     */
    default void writeAll() {
        writeIdentifier();
        writeBlock();
        writeDisplay();
        writeSpinTime();
        writeOpenSound();
        writeSpinSound();
        writeWinSound();
        writeOpenCommands();
        writeKey();
        writePouch();
        writeSpinType();
        writePrizes();
        writeHologramOffset();
        writeHologramLines();
        writeHologramItems();
    }

    /**
     * Save all written changes to file.
     */
    void save();
}